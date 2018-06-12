package thundr.redstonerepository.items;

import cofh.core.init.CoreEnchantments;
import cofh.core.key.KeyBindingItemMultiMode;
import cofh.core.util.helpers.EnergyHelper;
import cofh.core.util.helpers.StringHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import thundr.redstonerepository.RedstoneRepository;
import baubles.api.BaubleType;
import baubles.api.IBauble;
import thundr.redstonerepository.init.RedstoneRepositoryEquipment;

import javax.annotation.Nullable;
import java.util.List;

import cofh.api.item.IInventoryContainerItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import thundr.redstonerepository.api.IHungerStorageItem;
import thundr.redstonerepository.gui.GuiHandler;
import thundr.redstonerepository.util.HungerHelper;

import static thundr.redstonerepository.RedstoneRepository.NAME;


//@TODO Change name ItemEndoscopicGastrostomizer?
@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class ItemFeeder extends ItemCoreRF implements IBauble, IInventoryContainerItem, IHungerStorageItem {

    public int hungerPointsMax;

    public enum MODE {
        DISABLED(0),
        ENABLED(1);
        private final int value;

        MODE(final int newValue) {
            value = newValue;
        }

        public int getValue() {
            return value;
        }
    }

    public ItemFeeder() {
        super(NAME);
        setMaxStackSize(1);
        setCreativeTab(RedstoneRepository.tabCommon);
    }

    public ItemFeeder(int hungerPointsMax, int maxEnergy) {
        super(NAME);
        setMaxStackSize(1);
        setCreativeTab(RedstoneRepository.tabCommon);
        setNoRepair();
        this.hungerPointsMax = hungerPointsMax;
        this.maxEnergy = maxEnergy;
        this.maxTransfer = 80000; //TODO: move this to config. If possible pull from hardened capacitor automatically
	    this.energyPerUse = 30000;

        addPropertyOverride(new ResourceLocation("active"), (stack, world, entity) -> this.getMode(stack) == MODE.ENABLED.getValue() ? 1F : 0F);
    }

    @Optional.Method(modid = "baubles")
    public void onWornTick(ItemStack feeder, EntityLivingBase player) {
        //enjoy your soylent...
        if (player.isServerWorld()) {
            if (getMode(feeder) == MODE.ENABLED.getValue()) {
                if (getHungerPoints(feeder) > 0 && (getEnergyStored(feeder) >= getEnergyPerUse(feeder))) {
                    if (player instanceof EntityPlayer) {
                        EntityPlayer ePlayer = (EntityPlayer) player;
                        if (ePlayer.getFoodStats().needFood()) {
                            HungerHelper.addHunger(ePlayer, 1);
                            useHungerPoints(feeder, 1, ePlayer);
	                        RedstoneRepository.LOG.info(useEnergy(feeder, 1, false));
                            //TODO: do we need to send an update to the player here?
                        } else if (ePlayer.getFoodStats().getSaturationLevel() < 5) {
                            HungerHelper.addSaturation(ePlayer, 1);
                            useHungerPoints(feeder, 1, ePlayer);
                            RedstoneRepository.LOG.info(useEnergy(feeder, 1, false));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
            tooltip.add(StringHelper.shiftForDetails());
        }
        if (!StringHelper.isShiftKeyDown()) {
            tooltip.add(StringHelper.getInfoText("info.redstonerepository.feeder.short"));
            return;
        }
        tooltip.add(StringHelper.getInfoText("info.redstonerepository.feeder.title"));

        //Display active/disabled text
        if (getMode(stack) == MODE.ENABLED.getValue()) {
            tooltip.add(StringHelper.localizeFormat("info.redstonerepository.feeder.active", StringHelper.BRIGHT_GREEN, StringHelper.END, StringHelper.getKeyName(KeyBindingItemMultiMode.INSTANCE.getKey())));
        } else {
            tooltip.add(StringHelper.localizeFormat("info.redstonerepository.feeder.disabled", StringHelper.LIGHT_RED, StringHelper.END, StringHelper.getKeyName(KeyBindingItemMultiMode.INSTANCE.getKey())));
        }

        if (!RedstoneRepositoryEquipment.EquipmentInit.enable[1]) {
            tooltip.add(StringHelper.RED + "Baubles not loaded: Recipe disabled.");
        }
        tooltip.add(StringHelper.localize("info.redstonerepository.hungerPoints") + ": " + StringHelper.getScaledNumber(getHungerPoints(stack)) + " / " + StringHelper.getScaledNumber(getMaxHungerPoints(stack)));
        tooltip.add(StringHelper.localize("info.cofh.charge") + ": " + StringHelper.getScaledNumber(getEnergyStored(stack)) + " / " + StringHelper.getScaledNumber(getCapacity(stack)) + " RF");
    }

    protected int getCapacity(ItemStack stack) {
        int enchant = EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.holding, stack);
        return maxEnergy + maxEnergy * enchant / 2;
    }

    @Override
    public int getMaxEnergyStored(ItemStack stack) {
        return getCapacity(stack);
    }


    @Override
    public void onModeChange(EntityPlayer player, ItemStack stack) {
        if (getMode(stack) == MODE.ENABLED.getValue()) {
            player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.2F, 0.8F);
        } else {
            player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.2F, 0.5F);
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            items.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(this, 1, 0), 0));
            items.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(this, 1, 0), maxEnergy));
        }
    }

    @Optional.Method(modid = "baubles")
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.BELT;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        player.openGui(RedstoneRepository.instance, GuiHandler.FEEDER_ID, world, 0, 0, 0);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public int getSizeInventory(ItemStack container) {
        return 1;
    }

    @Override
    public int getHungerPoints(ItemStack container) {
        HungerHelper.setDefaultHungerTag(container);
        return Math.min(container.getTagCompound().getInteger("Hunger"), getMaxHungerPoints(container));
    }


    public int receiveHungerPoints(ItemStack container, int maxReceive, boolean simulate) {
        HungerHelper.setDefaultHungerTag(container);
        int stored = Math.min(container.getTagCompound().getInteger("Hunger"), getMaxHungerPoints(container));
        int receive = Math.min(maxReceive, getMaxHungerPoints(container) - stored);

        if (!isCreative && !simulate) {
            stored += receive;
            container.getTagCompound().setInteger("Hunger", stored);
        }
        return receive;
    }

    public int useHungerPoints(ItemStack container, int maxExtract, EntityPlayer player) {
        if (isCreative) {
            return maxExtract;
        }
        HungerHelper.setDefaultHungerTag(container);
        int stored = Math.min(container.getTagCompound().getInteger("Hunger"), getMaxHungerPoints(container));
        int extract = Math.min(maxExtract, stored);

        stored -= extract;
        container.getTagCompound().setInteger("Hunger", stored);

        return extract;
    }

    public int getMaxHungerPoints(ItemStack container) {
        int enchant = EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.holding, container);
        return hungerPointsMax + hungerPointsMax * enchant / 2;
    }
}
