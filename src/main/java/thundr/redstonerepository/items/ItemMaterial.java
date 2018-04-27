package thundr.redstonerepository.items;

import cofh.api.util.ThermalExpansionHelper;
import cofh.core.item.ItemMulti;
import cofh.core.util.core.IInitializer;
import cofh.core.util.helpers.ItemHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import thundr.redstonerepository.RedstoneRepository;

import static cofh.core.util.helpers.RecipeHelper.*;

public class ItemMaterial extends ItemMulti implements IInitializer {

    public static ItemStack dustGelidEnderium;
    public static ItemStack ingotGelidEnderium;
    public static ItemStack nuggetGelidEnderium;
    public static ItemStack gearGelidEnderium;
    public static ItemStack plateGelidEnderium;
    public static ItemStack gemGelid;
    public static ItemStack rodGelid;
    public static ItemStack plateArmorGelidEnderium;
//    public static ItemStack blockGelidEnderium;
//    public static ItemStack blockGelidGem;
    public static ItemStack stringFluxed;

    public ItemMaterial() {

        super("redstonerepository");

        setUnlocalizedName("material");
        setCreativeTab(RedstoneRepository.tabCommon);
    }

    /* IInitializer */
    @Override
    public boolean initialize() {
        //TODO make sure ItemMap ids are not overlapping (?)

        dustGelidEnderium = addOreDictItem(0, "dustGelidEnderium", EnumRarity.RARE);
        ingotGelidEnderium = addOreDictItem(16, "ingotGelidEnderium", EnumRarity.RARE);
        nuggetGelidEnderium = addOreDictItem(32, "nuggetGelidEnderium", EnumRarity.RARE);
        gearGelidEnderium = addOreDictItem(48, "gearGelidEnderium", EnumRarity.RARE);

        plateGelidEnderium = addOreDictItem(64, "plateGelidEnderium", EnumRarity.RARE);

        gemGelid = addOreDictItem(80, "gemGelid", EnumRarity.RARE);
        rodGelid = addItem(96, "rodGelid", EnumRarity.RARE);
        plateArmorGelidEnderium = addItem(112, "plateArmorGelidEnderium", EnumRarity.RARE);
	    stringFluxed = addOreDictItem(128, "stringFluxed", EnumRarity.UNCOMMON);


        RedstoneRepository.proxy.addIModelRegister(this);

        return true;
    }

    @Override
    public boolean register() {
        addTwoWayStorageRecipe(ingotGelidEnderium, "ingotGelidEnderium", nuggetGelidEnderium, "nuggetGelidEnderium");

        addReverseStorageRecipe(ingotGelidEnderium, "blockGelidEnderium");
        addReverseStorageRecipe(gemGelid, "blockGelidGem");

        addGearRecipe(gearGelidEnderium, "ingotGelidEnderium");
        
        addShapedRecipe(rodGelid, "  O", " B ", "O  ", 'B', cofh.redstonearsenal.item.ItemMaterial.rodObsidian, 'O', "gemGelid");
        addShapedRecipe(plateArmorGelidEnderium, " I ", "IGI", " I ", 'G', "gemGelid", 'I', "plateGelidEnderium");

        ItemStack dustEnderium = ItemHelper.cloneStack(OreDictionary.getOres("dustEnderium", false).get(0), 1);
        FluidStack fluidCryotheum = new FluidStack(FluidRegistry.getFluid("cryotheum"), 1000);
		FluidStack fluidRedstone = new FluidStack(FluidRegistry.getFluid("redstone"), 200);

        ThermalExpansionHelper.addSmelterRecipe(4000, dustGelidEnderium, new ItemStack(Blocks.SAND), ingotGelidEnderium);

        ThermalExpansionHelper.addCompactorPressRecipe(4000, ingotGelidEnderium, plateGelidEnderium);
        ThermalExpansionHelper.addCompactorStorageRecipe(400, ItemHelper.cloneStack(nuggetGelidEnderium, 9), ingotGelidEnderium);

        ThermalExpansionHelper.addTransposerFill(4000, dustEnderium, dustGelidEnderium, fluidCryotheum, false);
        ThermalExpansionHelper.addTransposerFill(4000, new ItemStack(Items.EMERALD), gemGelid, fluidCryotheum, false);
        ThermalExpansionHelper.addTransposerFill(2000, new ItemStack(Items.STRING), stringFluxed, fluidRedstone, false);

        return true;
    }


}
