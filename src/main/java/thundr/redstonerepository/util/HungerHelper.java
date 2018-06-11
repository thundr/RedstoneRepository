package thundr.redstonerepository.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class HungerHelper {
	public static ItemStack setDefaultHungerTag(ItemStack container) {
		if (!container.hasTagCompound()) {
			container.setTagCompound(new NBTTagCompound());
			container.getTagCompound().setInteger("Hunger", 0);
		}
		return container;
	}
}
