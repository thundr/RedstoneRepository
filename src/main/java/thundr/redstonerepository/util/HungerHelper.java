package thundr.redstonerepository.util;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.FoodStats;
import net.minecraft.entity.player.EntityPlayer;


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
	public static int findHungerValues(ItemStack food) {
		if (!(food.getItem() instanceof ItemFood)) {
			return 0;
		}
		ItemFood itemFood = (ItemFood) food.getItem();
		return (int) (food.getCount() * ((itemFood.getHealAmount(food) * itemFood.getSaturationModifier(food) * 2.0F) + itemFood.getHealAmount(food)));
	}

	public static void addHunger(EntityPlayer player, int amount) {
		//Only add hunger points, not saturation. This automatically caps at 20 due to addStats logic.
		player.getFoodStats().addStats(amount, 0.0F);
	}

	public static void addSaturation(EntityPlayer player, int amount) {
		FoodStats foodStats = player.getFoodStats();
		//Cap saturation at 20 points.
		foodStats.setFoodSaturationLevel(Math.min(foodStats.getSaturationLevel() + amount, 20.0F));
	}
}
