package thundr.redstonerepository.items.baubles;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import cofh.api.item.INBTCopyIngredient;
import cofh.core.init.CoreEnchantments;
import cofh.core.item.IEnchantableItem;
import cofh.core.key.KeyBindingItemMultiMode;
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
public class ItemCapacitorAmulet extends ItemCoreRF implements IBauble, INBTCopyIngredient {

	public ItemCapacitorAmulet(int capacity, int transfer) {
		super("redstonerepository");
		this.maxEnergy = capacity;
		this.maxTransfer = transfer;
		setHasSubtypes(true);
		setMaxStackSize(1);
		setNoRepair();

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
			tooltip.add(StringHelper.localizeFormat("info.redstonerepository.tooltip.active", StringHelper.BRIGHT_GREEN, StringHelper.END, StringHelper.getKeyName(KeyBindingItemMultiMode.INSTANCE.getKey())));
		} else {
			tooltip.add(StringHelper.localizeFormat("info.redstonerepository.tooltip.disabled", StringHelper.LIGHT_RED, StringHelper.END, StringHelper.getKeyName(KeyBindingItemMultiMode.INSTANCE.getKey())));
		}

		if(!RedstoneRepositoryEquipment.EquipmentInit.enable[0]){
			tooltip.add(StringHelper.RED + "Baubles not loaded: Recipe disabled.");
		}
		tooltip.add(StringHelper.localize("info.cofh.charge") + ": " + StringHelper.getScaledNumber(getEnergyStored(stack)) + " / " + StringHelper.getScaledNumber(getMaxEnergyStored(stack)) + " RF");
		tooltip.add(StringHelper.localize("info.cofh.send") + "/" + StringHelper.localize("info.cofh.receive") + ": " + StringHelper.formatNumber(maxTransfer) + "/" + StringHelper.formatNumber(maxTransfer) + " RF/t");
	}

	@Optional.Method(modid = "baubles")
	public void onWornTick(ItemStack cap, EntityLivingBase player){
		if (!isActive(cap) || player.world.isRemote || CoreUtils.isFakePlayer(player) || !(player instanceof EntityPlayer)) {
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
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}
}
