package thundr.redstonerepository.items.tools.gelidenderium;


import cofh.core.init.CoreProps;
import cofh.redstonearsenal.item.tool.ItemSickleFlux;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

//TODO: should this even be a thing???? answer yes for that sick automation options
public class ItemSickleGelidEnderium extends ItemSickleFlux {
    public ItemSickleGelidEnderium(ToolMaterial toolMaterial) {
        super(toolMaterial);
	    maxEnergy = GelidEnderiumEnergy.maxEnergy;
	    energyPerUse = GelidEnderiumEnergy.energyPerUse;
	    energyPerUseCharged =  GelidEnderiumEnergy.energyPerUseCharged;
	    maxTransfer = GelidEnderiumEnergy.maxTransfer;
        radius = 5;
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
