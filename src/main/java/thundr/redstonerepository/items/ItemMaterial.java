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
import net.minecraftforge.fml.common.registry.ForgeRegistries;
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
    public static ItemStack stringFluxed;

    public ItemMaterial() {
        super("redstonerepository");
        setUnlocalizedName("material");
        setCreativeTab(RedstoneRepository.tabCommon);
    }

    /* IInitializer */
    @Override
    public boolean preInit() {
	    ForgeRegistries.ITEMS.register(setRegistryName("material"));
	    RedstoneRepository.proxy.addIModelRegister(this);
        dustGelidEnderium = addOreDictItem(0, "dustGelidEnderium", EnumRarity.RARE);
        ingotGelidEnderium = addOreDictItem(1, "ingotGelidEnderium", EnumRarity.RARE);
        nuggetGelidEnderium = addOreDictItem(2, "nuggetGelidEnderium", EnumRarity.RARE);
        gearGelidEnderium = addOreDictItem(3, "gearGelidEnderium", EnumRarity.RARE);
        plateGelidEnderium = addOreDictItem(4, "plateGelidEnderium", EnumRarity.RARE);
        gemGelid = addOreDictItem(5, "gemGelid", EnumRarity.RARE);
        rodGelid = addItem(6, "rodGelid", EnumRarity.RARE);
        plateArmorGelidEnderium = addItem(7, "plateArmorGelidEnderium", EnumRarity.RARE);
	    stringFluxed = addOreDictItem(8, "stringFluxed", EnumRarity.UNCOMMON);

        return true;
    }

    @Override
    public boolean initialize() {
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


        ThermalExpansionHelper.addTransposerFill(4000, dustEnderium, dustGelidEnderium, fluidCryotheum, false);
        ThermalExpansionHelper.addTransposerFill(4000, new ItemStack(Items.EMERALD), gemGelid, fluidCryotheum, false);
        ThermalExpansionHelper.addTransposerFill(2000, new ItemStack(Items.STRING), stringFluxed, fluidRedstone, false);

        return true;
    }


}
