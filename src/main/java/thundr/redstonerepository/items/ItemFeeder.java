package thundr.redstonerepository.items;

import cofh.core.init.CoreEnchantments;
import cofh.core.key.KeyBindingItemMultiMode;
import cofh.core.util.CoreUtils;
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
import baubles.api.cap.IBaublesItemHandler;
import thundr.redstonerepository.init.RedstoneRepositoryEquipment;

import javax.annotation.Nullable;
import java.util.List;

import static thundr.redstonerepository.RedstoneRepository.NAME;


//@TODO Change name ItemEndoscopicGastrostomizer?
@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class ItemFeeder extends ItemCoreRF implements IBauble {
    public int hungerPointsMax;
    public enum MODE{
        DISABLED(0),
        ENABLED(1);
        private final int value;
        MODE(final int newValue) {
            value = newValue;
        }
        public int getValue() { return value; }
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

        addPropertyOverride(new ResourceLocation("active"), (stack, world, entity) -> this.getMode(stack) == MODE.ENABLED.getValue() ? 1F : 0F);
    }

    @Optional.Method(modid = "baubles")
    public void onWornTick(ItemStack cap, EntityLivingBase player) {

    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

        if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
            tooltip.add(StringHelper.shiftForDetails());
        }
        if (!StringHelper.isShiftKeyDown()) {
            return;
        }
        tooltip.add(StringHelper.getInfoText("info.redstonerepository.feeder.title"));

        //Display active/disabled text
        if (getMode(stack) == MODE.ENABLED.getValue()) {
            tooltip.add(StringHelper.localizeFormat("info.redstonerepository.feeder.active", StringHelper.BRIGHT_GREEN, StringHelper.END, StringHelper.getKeyName(KeyBindingItemMultiMode.INSTANCE.getKey())));
        }
        else {
            tooltip.add(StringHelper.localizeFormat("info.redstonerepository.feeder.disabled", StringHelper.LIGHT_RED, StringHelper.END, StringHelper.getKeyName(KeyBindingItemMultiMode.INSTANCE.getKey())));
        }

        if(!RedstoneRepositoryEquipment.EquipmentInit.enable[1]){
            tooltip.add(StringHelper.RED + "Baubles not loaded: Recipe disabled.");
        }
        tooltip.add(StringHelper.localize("info.cofh.charge") + ": " + StringHelper.getScaledNumber(getEnergyStored(stack)) + " / " + StringHelper.getScaledNumber(getCapacity(stack)) + " RF");
    }

    protected int getCapacity(ItemStack stack) {
        int enchant = EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.holding, stack);
        return maxEnergy + maxEnergy * enchant / 2;
    }

    @Override
    public int getMaxEnergyStored(ItemStack stack){
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
    public BaubleType getBaubleType(ItemStack itemstack){
        return BaubleType.BELT;
    }
}
