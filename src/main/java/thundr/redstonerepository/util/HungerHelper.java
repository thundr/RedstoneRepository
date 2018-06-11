package thundr.redstonerepository.util;

import net.minecraft.item.ItemFood;
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

	//returns the value of an ItemStack of food in Hunger Points
	//each point of food and saturation is a hunger point
	public static int findHungerValues(ItemStack food){
		if(!(food.getItem() instanceof ItemFood)){
			return 0;
		}
		ItemFood itemFood = (ItemFood) food.getItem();
		return (int) (food.getCount() * ((itemFood.getHealAmount(food) * itemFood.getSaturationModifier(food) * 2.0F) + itemFood.getHealAmount(food)));
	}
}
