package thundr.redstonerepository.items;

import cofh.api.item.IInventoryContainerItem;
import cofh.core.init.CoreEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import thundr.redstonerepository.RedstoneRepository;
import thundr.redstonerepository.api.IHungerStorageItem;
import thundr.redstonerepository.gui.GuiHandler;
import thundr.redstonerepository.util.HungerHelper;

import static thundr.redstonerepository.RedstoneRepository.NAME;


//@TODO rename this to ItemEndoscopicGastrostomizer?
public class ItemFeeder extends ItemCoreRF implements IInventoryContainerItem, IHungerStorageItem {

    public int hungerPointsMax;

    public ItemFeeder() {
        super(NAME);
        setMaxStackSize(1);
        setCreativeTab(RedstoneRepository.tabCommon);
    }

    public ItemFeeder(int hungerPointsMax, int feederCapacity) {
        super(NAME);
        setMaxStackSize(1);
        setCreativeTab(RedstoneRepository.tabCommon);
        this.hungerPointsMax = hungerPointsMax;
        this.maxEnergy = feederCapacity;
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


	public int receiveHungerPoints(ItemStack container, int maxReceive){
		HungerHelper.setDefaultHungerTag(container);
		int stored = Math.min(container.getTagCompound().getInteger("Hunger"), getMaxHungerPoints(container));
		int receive = Math.min(maxReceive, getMaxEnergyStored(container) - stored);

		if (!isCreative) {
			stored += receive;
			container.getTagCompound().setInteger("Hunger", stored);
		}
		return receive;
	}

	public int useHungerPoints(ItemStack container, int maxExtract, EntityPlayer player){
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

	public int getMaxHungerPoints(ItemStack container){
		int enchant = EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.holding, container);
		return hungerPointsMax + hungerPointsMax * enchant / 2;
	}
}
