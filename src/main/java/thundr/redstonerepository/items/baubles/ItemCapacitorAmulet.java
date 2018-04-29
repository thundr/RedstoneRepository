package thundr.redstonerepository.items.baubles;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import cofh.api.item.INBTCopyIngredient;
import cofh.core.init.CoreEnchantments;
import cofh.core.item.IEnchantableItem;
import cofh.core.util.CoreUtils;
import cofh.core.util.helpers.EnergyHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.redstoneflux.api.IEnergyContainerItem;
import com.google.common.collect.Iterables;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;
import thundr.redstonerepository.init.RedstoneRepositoryEquipment;
import thundr.redstonerepository.items.ItemCoreRF;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class ItemCapacitorAmulet extends ItemCoreRF implements IBauble, IEnergyContainerItem, IEnchantableItem, INBTCopyIngredient {

	public ItemCapacitorAmulet(int capacity, int transfer) {
		super("redstonerepository");
		this.maxEnergy = capacity;
		this.maxTransfer = transfer;
		setHasSubtypes(true);
		setMaxStackSize(1);
		setNoRepair();

		addPropertyOverride(new ResourceLocation("active"), (stack, world, entity) -> ItemCapacitorAmulet.this.isActive(stack) ? 1F : 0F);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			tooltip.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		tooltip.add(StringHelper.getInfoText("info.redstonerepository.capacitor.title"));

		if (isActive(stack)) {
			tooltip.add(StringHelper.getDeactivationText("info.redstonerepository.capacitor.deactivate"));
		} else {
			tooltip.add(StringHelper.getActivationText("info.redstonerepository.capacitor.activate"));
		}
		if(!RedstoneRepositoryEquipment.EquipmentInit.enable[0]){
			tooltip.add(StringHelper.RED + "Baubles not loaded: Recipe disabled.");
		}
		tooltip.add(StringHelper.localize("info.cofh.charge") + ": " + StringHelper.getScaledNumber(getEnergyStored(stack)) + " / " + StringHelper.getScaledNumber(getCapacity(stack)) + " RF");
		tooltip.add(StringHelper.localize("info.cofh.send") + "/" + StringHelper.localize("info.cofh.receive") + ": " + StringHelper.formatNumber(maxTransfer) + "/" + StringHelper.formatNumber(maxTransfer) + " RF/t");
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (isInCreativeTab(tab)) {
			items.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(this, 1, 0), 0));
			items.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(this, 1, 0), maxEnergy));
		}
	}

	@Optional.Method(modid = "baubles")
	public void onWornTick(ItemStack cap, EntityLivingBase player){
		if (player.world.isRemote || CoreUtils.isFakePlayer(player) || !(player instanceof EntityPlayer)) {
			return;
		}
		EntityPlayer entityPlayer = (EntityPlayer) player;
		Iterable<ItemStack> playerItems;
		playerItems = Iterables.concat(entityPlayer.inventory.armorInventory, entityPlayer.inventory.mainInventory, entityPlayer.inventory.offHandInventory, getBaubles(entityPlayer));
		for(ItemStack playerItem : playerItems){
			//dont play yourself (or charge yourself)
			if (playerItem.isEmpty() || playerItem.equals(cap) || playerItem.getItem() instanceof ItemCapacitorAmulet) {
				continue;
			}

			if (EnergyHelper.isEnergyContainerItem(playerItem)) {
				extractEnergy(cap, ((IEnergyContainerItem) playerItem.getItem()).receiveEnergy(playerItem, Math.min(getEnergyStored(cap), maxTransfer), false), false);
			} else if (EnergyHelper.isEnergyHandler(playerItem)) {
				IEnergyStorage handler = EnergyHelper.getEnergyHandler(playerItem);

				if (handler != null) {
					extractEnergy(cap, handler.receiveEnergy(Math.min(getEnergyStored(cap), maxTransfer), false), false);
				}
			}
		}
	}


	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		ItemStack stack = player.getHeldItem(hand);
		if (CoreUtils.isFakePlayer(player)) {
			return new ActionResult<>(EnumActionResult.FAIL, stack);
		}
		if (player.isSneaking()) {
			if (setActiveState(stack, !isActive(stack))) {
				if (isActive(stack)) {
					player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.2F, 0.8F);
				} else {
					player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.2F, 0.5F);
				}
			}
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return EnumActionResult.FAIL;
	}

	public boolean isActive(ItemStack stack) {
		return stack.getTagCompound() != null && stack.getTagCompound().getBoolean("Active");
	}

	public boolean setActiveState(ItemStack stack, boolean state) {

		if (getEnergyStored(stack) > 0) {
			stack.getTagCompound().setBoolean("Active", state);
			return true;
		}
		stack.getTagCompound().setBoolean("Active", false);
		return false;
	}

	protected int getCapacity(ItemStack stack) {
		int enchant = EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.holding, stack);
		return maxEnergy + maxEnergy * enchant / 2;
	}

	@Override
	public int getMaxEnergyStored(ItemStack stack){
		return getCapacity(stack);
	}

	/* stolt from TE
	* bauble charging ablility */
	@CapabilityInject(IBaublesItemHandler.class)
	private static Capability<IBaublesItemHandler> CAPABILITY_BAUBLES = null;

	private static Iterable<ItemStack> getBaubles(Entity entity) {

		if (CAPABILITY_BAUBLES == null) {
			return Collections.emptyList();
		}
		IBaublesItemHandler handler = entity.getCapability(CAPABILITY_BAUBLES, null);

		if (handler == null) {
			return Collections.emptyList();
		}
		return IntStream.range(0, handler.getSlots()).mapToObj(handler::getStackInSlot).filter(stack -> !stack.isEmpty()).collect(Collectors.toList());
	}

	@Optional.Method(modid = "baubles")
	public BaubleType getBaubleType(ItemStack itemstack){
		return BaubleType.AMULET;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			EnergyHelper.setDefaultEnergyTag(stack, 0);
		}
		return 1D - (double) stack.getTagCompound().getInteger("Energy") / (double) getCapacity(stack);
	}

	@Override
	public boolean setMode(ItemStack stack, int mode) {
		return false;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}
}
