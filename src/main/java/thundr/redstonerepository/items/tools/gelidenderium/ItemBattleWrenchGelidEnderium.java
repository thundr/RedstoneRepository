package thundr.redstonerepository.items.tools.gelidenderium;

import cofh.core.init.CoreProps;
import cofh.redstonearsenal.item.tool.ItemBattleWrenchFlux;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

import java.util.Random;

public class ItemBattleWrenchGelidEnderium extends ItemBattleWrenchFlux{
	//TODO: what the fuck is even the point of this tool tbh
    public ItemBattleWrenchGelidEnderium(ToolMaterial toolMaterial) {
        super(toolMaterial);
        damage = 7;
        damageCharged = 2;
	    maxEnergy = GelidEnderiumEnergy.maxEnergy;
	    energyPerUse = GelidEnderiumEnergy.energyPerUse;
	    energyPerUseCharged =  GelidEnderiumEnergy.energyPerUseCharged;
	    maxTransfer = GelidEnderiumEnergy.maxTransfer;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {
		return CoreProps.RGB_DURABILITY_ENDER;
	}
}
