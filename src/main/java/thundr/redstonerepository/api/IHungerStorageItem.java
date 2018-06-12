package thundr.redstonerepository.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IHungerStorageItem {

	int receiveHungerPoints(ItemStack container, int maxReceive, boolean simulate);

	int useHungerPoints(ItemStack container, int maxExtract, EntityPlayer player);

	int getHungerPoints(ItemStack container);

	int getMaxHungerPoints(ItemStack container);
}
