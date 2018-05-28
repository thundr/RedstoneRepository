package thundr.redstonerepository.api;

import cofh.redstoneflux.api.IEnergyContainerItem;
import net.minecraft.item.ItemStack;

public interface IArmorEnderium extends IEnergyContainerItem {

    /*
    Allows armor to be calculated as part of a set of Gelid Enderium Armor
    Requires implementation of IEnergyContainerItem for energy manipulation
    @return whether or not the piece of armor is a part of a Gelid set.
    */
    boolean isEnderiumArmor(ItemStack stack);

}
