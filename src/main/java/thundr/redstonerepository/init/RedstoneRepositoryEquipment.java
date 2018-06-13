package thundr.redstonerepository.init;


import cofh.core.render.IModelRegister;
import cofh.core.util.core.IInitializer;
import cofh.core.util.helpers.EnergyHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.redstonearsenal.init.RAEquipment;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thundr.redstonerepository.RedstoneRepository;
import thundr.redstonerepository.items.ItemFeeder;
import thundr.redstonerepository.items.ItemMaterial;
import thundr.redstonerepository.items.armor.ItemArmorEnderium;
import thundr.redstonerepository.items.baubles.ItemCapacitorAmulet;
import thundr.redstonerepository.items.baubles.ItemRingBase;
import thundr.redstonerepository.items.baubles.ItemRingEffect;
import thundr.redstonerepository.items.tools.gelidenderium.*;

import java.util.Locale;

import static cofh.core.util.helpers.RecipeHelper.addShapedRecipe;


//thanks to cofh for the structure of this class

public class RedstoneRepositoryEquipment{
	public static final RedstoneRepositoryEquipment INSTANCE = new RedstoneRepositoryEquipment();
	public static EquipmentInit equipInit;
	private RedstoneRepositoryEquipment() {
	}

	public static void preInit() {
		for (ArmorSet e : ArmorSet.values()) {
			e.initialize();
			RedstoneRepository.proxy.addIModelRegister(e);
		}
		for (ToolSet e : ToolSet.values()) {
			e.initialize();
			RedstoneRepository.proxy.addIModelRegister(e);
		}
		equipInit = new EquipmentInit();
		equipInit.preInit();
		MinecraftForge.EVENT_BUS.register(INSTANCE);
	}

	/* EVENT HANDLING */
	@SubscribeEvent
	public void registerRecipes(RegistryEvent.Register<IRecipe> event) {

		for (ArmorSet e : ArmorSet.values()) {
			e.register();
		}
		for (ToolSet e : ToolSet.values()) {
			e.register();
		}
		equipInit.initialize();
	}

//	/* MATERIALS */
	public static final ItemArmor.ArmorMaterial ARMOR_MATERIAL_GELID = EnumHelper.addArmorMaterial("GELID", "gelidenderium", 100, new int[] { 3, 6, 8, 3 }, 25, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 3F);
	public static final Item.ToolMaterial TOOL_MATERIAL_GELID = EnumHelper.addToolMaterial("GELID", 4, 100, 10.0F, 0, 25);

	/* ARMOR */
	public enum ArmorSet implements IModelRegister {

		GELID("gelid", ARMOR_MATERIAL_GELID);

		private final String name;
		private final ArmorMaterial ARMOR_MATERIAL;

		public ItemArmorEnderium itemHelmet;
		public ItemArmorEnderium itemPlate;
		public ItemArmorEnderium itemLegs;
		public ItemArmorEnderium itemBoots;

		public ItemStack armorHelmet;
		public ItemStack armorPlate;
		public ItemStack armorLegs;
		public ItemStack armorBoots;

		public boolean[] enable = new boolean[4];

		ArmorSet(String name, ArmorMaterial material) {

			this.name = name.toLowerCase(Locale.US);
			ARMOR_MATERIAL = material;
		}

		protected void create() {

			itemHelmet = new ItemArmorEnderium(ARMOR_MATERIAL, EntityEquipmentSlot.HEAD);
			itemPlate = new ItemArmorEnderium(ARMOR_MATERIAL, EntityEquipmentSlot.CHEST);
			itemLegs = new ItemArmorEnderium(ARMOR_MATERIAL, EntityEquipmentSlot.LEGS);
			itemBoots = new ItemArmorEnderium(ARMOR_MATERIAL, EntityEquipmentSlot.FEET);
		}

		protected void initialize() {
			final String ENDERIUM_LOCALE = "redstonerepository.armor.enderium." + name + ".";
			final String PATH_ARMOR = "redstonerepository:textures/models/armor/";
			final String[] TEXTURE = { PATH_ARMOR + name + "_1.png", PATH_ARMOR + name + "_2.png" };



			String category = "Equipment.Armor." + StringHelper.titleCase(name);

			enable[0] = RedstoneRepository.CONFIG.getConfiguration().get(category, "Helmet", true).getBoolean(true);
			enable[1] = RedstoneRepository.CONFIG.getConfiguration().get(category, "Chestplate", true).getBoolean(true);
			enable[2] = RedstoneRepository.CONFIG.getConfiguration().get(category, "Leggings", true).getBoolean(true);
			enable[3] = RedstoneRepository.CONFIG.getConfiguration().get(category, "Boots", true).getBoolean(true);

			create();

			/* HELMET */
			itemHelmet.setArmorTextures(TEXTURE).setUnlocalizedName(ENDERIUM_LOCALE + "helm").setCreativeTab(RedstoneRepository.tabCommon);
			itemHelmet.setShowInCreative(enable[0]);
			itemHelmet.setRegistryName("armor.helmet_" + name);
			ForgeRegistries.ITEMS.register(itemHelmet);

			/* PLATE */
			itemPlate.setArmorTextures(TEXTURE).setUnlocalizedName(ENDERIUM_LOCALE + "chestplate").setCreativeTab(RedstoneRepository.tabCommon);
			itemPlate.setShowInCreative(enable[1]);
			itemPlate.setRegistryName("armor.plate_" + name);
			ForgeRegistries.ITEMS.register(itemPlate);

			/* LEGS */
			itemLegs.setArmorTextures(TEXTURE).setUnlocalizedName(ENDERIUM_LOCALE + "leggings").setCreativeTab(RedstoneRepository.tabCommon);
			itemLegs.setShowInCreative(enable[2]);
			itemLegs.setRegistryName("armor.legs_" + name);
			ForgeRegistries.ITEMS.register(itemLegs);

			/* BOOTS */
			itemBoots.setArmorTextures(TEXTURE).setUnlocalizedName(ENDERIUM_LOCALE + "boots").setCreativeTab(RedstoneRepository.tabCommon);
			itemBoots.setShowInCreative(enable[3]);
			itemBoots.setRegistryName("armor.boots_" + name);
			ForgeRegistries.ITEMS.register(itemBoots);

			/* REFERENCES */
			armorHelmet = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemHelmet), 0);
			armorPlate = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemPlate), 0);
			armorLegs = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemLegs), 0);
			armorBoots = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemBoots), 0);
		}

		protected void register() {
			//TODO: Fix these materials
			if (enable[0]) {
				addShapedRecipe(armorHelmet,
						"III", "IAI",
						'I', ItemMaterial.plateArmorGelidEnderium,
						'A', RAEquipment.ArmorSet.FLUX.itemHelmet);
			}
			if (enable[1]) {
				addShapedRecipe(armorPlate,
						"IAI", "III", "III",
						'I', ItemMaterial.plateArmorGelidEnderium,
						'A', RAEquipment.ArmorSet.FLUX.itemPlate);
			}
			if (enable[2]) {
				addShapedRecipe(armorLegs,
						"III", "IAI", "I I",
						'I', ItemMaterial.plateArmorGelidEnderium,
						'A', RAEquipment.ArmorSet.FLUX.itemLegs);
			}
			if (enable[3]) {
				addShapedRecipe(armorBoots,
						"IAI", "I I",
						'I', ItemMaterial.plateArmorGelidEnderium,
						'A', RAEquipment.ArmorSet.FLUX.itemBoots);
			}
		}

		/* HELPERS */
		@SideOnly (Side.CLIENT)
		public void registerModel(Item item, String stackName) {
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(RedstoneRepository.ID + ":armor", "type=" + stackName));
		}

		/* IModelRegister */
		@Override
		@SideOnly (Side.CLIENT)
		public void registerModels() {
			registerModel(itemHelmet, "helmet_" + name);
			registerModel(itemPlate, "chestplate_" + name);
			registerModel(itemLegs, "leggings_" + name);
			registerModel(itemBoots, "boots_" + name);
		}
	}

	/* TOOLS */
	public enum ToolSet implements IModelRegister {

		GELID("gelid", TOOL_MATERIAL_GELID);

		private final String name;
		private final ToolMaterial TOOL_MATERIAL;

		public ItemBattleWrenchGelidEnderium itemBattleWrench;
		public ItemSwordGelidEnderium itemSword;
		public ItemShovelGelidEnderium itemShovel;
		public ItemPickaxeGelidEnderium itemPickaxe;
		public ItemAxeGelidEnderium itemAxe;
		public ItemSickleGelidEnderium itemSickle;

		public ItemStack toolBattleWrench;
		public ItemStack toolSword;
		public ItemStack toolShovel;
		public ItemStack toolPickaxe;
		public ItemStack toolAxe;
		public ItemStack toolSickle;

		//config start
		public int axeBlocksCutPerTick;

		public boolean[] enable = new boolean[11];

		ToolSet(String name, ToolMaterial material) {
			this.name = name.toLowerCase(Locale.US);
			TOOL_MATERIAL = material;
		}

		protected void create() {
			itemBattleWrench = new ItemBattleWrenchGelidEnderium(TOOL_MATERIAL);
			itemSword = new ItemSwordGelidEnderium(TOOL_MATERIAL);
			itemShovel = new ItemShovelGelidEnderium(TOOL_MATERIAL);
			itemPickaxe = new ItemPickaxeGelidEnderium(TOOL_MATERIAL);
			itemAxe = new ItemAxeGelidEnderium(TOOL_MATERIAL, axeBlocksCutPerTick);
			itemSickle = new ItemSickleGelidEnderium(TOOL_MATERIAL);
		}

		protected void config() {
			//axe
			axeBlocksCutPerTick = RedstoneRepository.CONFIG.getConfiguration().get("Equipment.Tools.Axe", "BlocksPerTick", 3,
					"Sets the number of blocks per tick the axe attempts to cut in empowered mode. Higher values cause more lag. ").setMinValue(0).setMaxValue(10).getInt();
		}

		protected void initialize() {

			final String ENDERIUM_LOCALE = "redstonerepository.tool.enderium." + name + ".";

			String category = "Equipment.Tools." + StringHelper.titleCase(name);

			enable[1] = RedstoneRepository.CONFIG.getConfiguration().get(category, "BattleWrench", true).getBoolean(true);
			enable[2] = RedstoneRepository.CONFIG.getConfiguration().get(category, "Sword", true).getBoolean(true);
			enable[3] = RedstoneRepository.CONFIG.getConfiguration().get(category, "Shovel", true).getBoolean(true);
			enable[4] = RedstoneRepository.CONFIG.getConfiguration().get(category, "Pickaxe", true).getBoolean(true);
			enable[5] = RedstoneRepository.CONFIG.getConfiguration().get(category, "Axe", true).getBoolean(true);
			enable[8] = RedstoneRepository.CONFIG.getConfiguration().get(category, "Sickle", true).getBoolean(true);

			config();

			create();

			/* BATTLEWRENCH */
			itemBattleWrench.setUnlocalizedName(ENDERIUM_LOCALE + "battlewrench");
			itemBattleWrench.setCreativeTab(RedstoneRepository.tabCommon);
			itemBattleWrench.setShowInCreative(enable[1]);
			itemBattleWrench.setRegistryName("tool.battlewrench_" + name);
			ForgeRegistries.ITEMS.register(itemBattleWrench);

			/* SWORD */
			itemSword.setUnlocalizedName(ENDERIUM_LOCALE + "sword").setCreativeTab(RedstoneRepository.tabCommon);
			itemSword.setShowInCreative(enable[2]);
			itemSword.setRegistryName("tool.sword_" + name);
			ForgeRegistries.ITEMS.register(itemSword);

			/* SHOVEL */
			itemShovel.setUnlocalizedName(ENDERIUM_LOCALE + "shovel").setCreativeTab(RedstoneRepository.tabCommon);
			itemShovel.setShowInCreative(enable[3]);
			itemShovel.setRegistryName("tool.shovel_" + name);
			ForgeRegistries.ITEMS.register(itemShovel);

			/* PICKAXE */
			itemPickaxe.setUnlocalizedName(ENDERIUM_LOCALE + "pickaxe").setCreativeTab(RedstoneRepository.tabCommon);
			itemPickaxe.setShowInCreative(enable[4]);
			itemPickaxe.setRegistryName("tool.pickaxe_" + name);
			ForgeRegistries.ITEMS.register(itemPickaxe);

			/* AXE */
			itemAxe.setUnlocalizedName(ENDERIUM_LOCALE + "axe").setCreativeTab(RedstoneRepository.tabCommon);
			itemAxe.setShowInCreative(enable[5]);
			itemAxe.setRegistryName("tool.axe_" + name);
			ForgeRegistries.ITEMS.register(itemAxe);

			/* SICKLE */
			itemSickle.setUnlocalizedName(ENDERIUM_LOCALE + "sickle").setCreativeTab(RedstoneRepository.tabCommon);
			itemSickle.setShowInCreative(enable[8]);
			itemSickle.setRegistryName("tool.sickle_" + name);
			ForgeRegistries.ITEMS.register(itemSickle);

			toolBattleWrench = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemBattleWrench), 0);
			toolSword = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemSword), 0);
			toolShovel = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemShovel), 0);
			toolPickaxe = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemPickaxe), 0);
			toolAxe = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemAxe), 0);
			toolSickle = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemSickle), 0);
		}

		protected void register() {
			if (enable[1]) {
				addShapedRecipe(toolBattleWrench,
						"IWI", " G ", " R ", 'I', "ingotGelidEnderium",
						'G', "gearGelidEnderium",
						'R', ItemMaterial.rodGelid,
						'W', RAEquipment.ToolSet.FLUX.itemBattleWrench);
			}
			if (enable[2]) {
				addShapedRecipe(toolSword,
						" I ", " S ", " R ",
						'I', "ingotGelidEnderium",
						'R', ItemMaterial.rodGelid,
						'S', RAEquipment.ToolSet.FLUX.itemSword);
			}
			if (enable[3]) {
				addShapedRecipe(toolShovel, " I ", " S ", " R ",
						'I', "ingotGelidEnderium",
						'R', ItemMaterial.rodGelid,
						'S', RAEquipment.ToolSet.FLUX.itemShovel);
			}
			if (enable[4]) {
				addShapedRecipe(toolPickaxe, "III", " P ", " R ",
						'I', "ingotGelidEnderium",
						'R', ItemMaterial.rodGelid,
						'P', RAEquipment.ToolSet.FLUX.itemPickaxe);
			}
			if (enable[5]) {
				addShapedRecipe(toolAxe, "II ", "IA ", " R ",
						'I', "ingotGelidEnderium",
						'R', ItemMaterial.rodGelid,
						'A', RAEquipment.ToolSet.FLUX.itemAxe);
			}
			if (enable[8]) {
				addShapedRecipe(toolSickle, " I ", " SI", "RI ",
						'I', "ingotGelidEnderium",
						'R', ItemMaterial.rodGelid,
						'S', RAEquipment.ToolSet.FLUX.itemSickle);
			}
		}

		/* HELPERS */
		@SideOnly (Side.CLIENT)
		public void registerModel(Item item, String stackName) {
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(RedstoneRepository.ID + ":tools/" + stackName, "inventory"));
		}

		/* IModelRegister */
		@Override
		@SideOnly (Side.CLIENT)
		public void registerModels() {
			registerModel(itemBattleWrench, "battle_wrench_" + name);
			registerModel(itemSword, "sword_" + name);
			registerModel(itemShovel, "shovel_" + name);
			registerModel(itemPickaxe, "pickaxe_" + name);
			registerModel(itemAxe, "axe_" + name);
			registerModel(itemSickle, "sickle_" + name);
		}
	}

	public static class EquipmentInit implements IInitializer, IModelRegister{

		public static ItemFeeder itemFeeder;
		public static ItemCapacitorAmulet itemCapacitorAmulet;
		public static ItemRingEffect itemEffectRing;
		public static ItemRingBase itemBaseRing;

		@GameRegistry.ItemStackHolder(value = "thermalexpansion:capacitor", meta = 4)
		public static final ItemStack resonantCapacitor = null;

		@GameRegistry.ItemStackHolder(value = "thermalexpansion:capacitor", meta = 1)
		public static final ItemStack hardenedCapacitor = null;

		public static ItemStack capacitorAmuletGelid;

		public static ItemStack feederStack;
		public static ItemStack effectRingStack;
		public static ItemStack baseRingStack;
		public static ItemStack mushroomStewBucket;


		public static boolean[] enable = new boolean[4];
		public static int capacity;
		public static int transfer;
		public static int hungerPointsMax;
		public static int feederCapacity;
		public static int feederMaxTransfer;
		public static int feederEnergyPerUse;
		public static int feederMaxSat;

		public boolean preInit() {
			config();

			itemCapacitorAmulet = new ItemCapacitorAmulet(capacity, transfer);
			itemCapacitorAmulet.setUnlocalizedName("redstonerepository.bauble.capacitor.gelid").setCreativeTab(RedstoneRepository.tabCommon);
			itemCapacitorAmulet.setRegistryName("capacitor_gelid");
			ForgeRegistries.ITEMS.register(itemCapacitorAmulet);
			capacitorAmuletGelid = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemCapacitorAmulet), 0);

			itemFeeder = new ItemFeeder(hungerPointsMax, feederCapacity, feederMaxTransfer, feederEnergyPerUse, feederMaxSat);
			itemFeeder.setUnlocalizedName("redstonerepository.bauble.feeder").setCreativeTab(RedstoneRepository.tabCommon);
			itemFeeder.setRegistryName("feeder");
			ForgeRegistries.ITEMS.register(itemFeeder);
			feederStack = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemFeeder), 0);

			itemEffectRing = new ItemRingEffect();
			itemEffectRing.setUnlocalizedName("redstonerepository.bauble.ring.effect").setCreativeTab(RedstoneRepository.tabCommon);
			itemEffectRing.setRegistryName("ring_effect");
			ForgeRegistries.ITEMS.register(itemEffectRing);
			effectRingStack = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemEffectRing), 0);

			itemBaseRing = new ItemRingBase();
			itemBaseRing.setUnlocalizedName("redstonerepository.bauble.ring.base").setCreativeTab(RedstoneRepository.tabCommon);
			itemBaseRing.setRegistryName("ring_base");
			ForgeRegistries.ITEMS.register(itemBaseRing);
			baseRingStack = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemEffectRing), 0);

			RedstoneRepository.proxy.addIModelRegister(this);
			return true;
		}

		public void config() {
			boolean enableConfig = RedstoneRepository.CONFIG.get("Item.Capacitor", "Enable", true, "Enable the Gelid Capacitor Amulet");
			boolean enableLoaded = Loader.isModLoaded("baubles");
			enable[0] = enableConfig && enableLoaded;

			transfer = RedstoneRepository.CONFIG.get("Item.Capacitor", "BaseTransfer", 100000, "Set the base transfer rate of the Gelid Capacitor Amulet in RF/t (Default 100,000) ");
			capacity = RedstoneRepository.CONFIG.get("Item.Capacitor", "BaseCapacity", 100000000, "Set the base capacity of the Gelid Capacitor Amulet in RF/t (Default 100,000,000) ");

			//Feeder config
			boolean enableFeederConfig = RedstoneRepository.CONFIG.get("Item.Feeder", "Enable", true, "Enable the Endoscopic Gastrostomizer (Automatic Feeder)");
			enable[1] = enableFeederConfig && enableLoaded;

			hungerPointsMax = RedstoneRepository.CONFIG.get("Item.Feeder", "MaxHungerPoints", 500, "Set the maximum hunger point storage of the Endoscopic Gastrostomizer (EG) (Default 500)");
			feederCapacity = RedstoneRepository.CONFIG.get("Item.Feeder", "BaseCapacity", 4000000, "Set the base capacity of the E.G. in RF (Default 4,000,000) ");
			feederMaxTransfer = RedstoneRepository.CONFIG.get("Item.Feeder", "MaxTransfer", 8000, "Set the maximum transfer rate into the item in RF/t (Default 8000)");
			feederEnergyPerUse = RedstoneRepository.CONFIG.get("Item.Feeder", "EnergyPerUse", 30000, "Set amount of energy used per food point in RF (Default 3000)");
			feederMaxSat = RedstoneRepository.CONFIG.get("Item.Feeder", "SaturationFillLevel", 5, "Maximum amount of hunger saturation to automatically fill to. Higher numbers consume hunger points more quickly. (Default 5, Max 20)");

			enable[2] = enableLoaded && RedstoneRepository.CONFIG.get("Item.Ring.Effect", "Enable", true, "Enable the Cryotheum Stasis Ring");


		}

		public boolean initialize() {
			mushroomStewBucket = FluidUtil.getFilledBucket(FluidRegistry.getFluidStack("mushroom_stew", 1000));
			if (enable[0]) {
				addShapedRecipe(capacitorAmuletGelid,
						" S ",
						"ACA",
						"AGA",
						'S', ItemMaterial.stringFluxed,
						'A', ItemMaterial.plateArmorGelidEnderium,
						'G', ItemMaterial.ingotGelidEnderium,
						'C', resonantCapacitor
				);
			}
			if (enable[1]) {
				addShapedRecipe(feederStack,
						"SCS",
						"PMP",
						" G ",
						'S', ItemMaterial.stringFluxed,
						'C', hardenedCapacitor,
						'M', mushroomStewBucket,
						'P', ItemMaterial.plateGelidEnderium,
						'G', ItemMaterial.gearGelidEnderium);
			}
			if (enable[2]){
				ItemStack gelidCryotheumBucket = FluidUtil.getFilledBucket(FluidRegistry.getFluidStack("cryotheum", 1000));
				addShapedRecipe(effectRingStack,
						"   ",
						"NBN",
						"GCG",
						'B', baseRingStack,
						'N', Items.NETHER_STAR,
						'C', hardenedCapacitor,
						'G', gelidCryotheumBucket);
			}
			addShapedRecipe(baseRingStack,
					" G ",
					"I I",
					" I ",
					'G', ItemMaterial.gemGelid,
					'I', ItemMaterial.ingotGelidEnderium);
			return true;
		}

		@SideOnly (Side.CLIENT)
		public void registerModels() {
			ModelLoader.setCustomModelResourceLocation(itemCapacitorAmulet, 0, new ModelResourceLocation(RedstoneRepository.ID + ":" + "capacitor_gelid", "inventory"));
			ModelLoader.setCustomModelResourceLocation(itemFeeder, 0, new ModelResourceLocation(RedstoneRepository.ID + ":" + "feeder", "inventory"));
			ModelLoader.setCustomModelResourceLocation(itemEffectRing, 0, new ModelResourceLocation(RedstoneRepository.ID + ":" + "ring_effect", "inventory"));
			ModelLoader.setCustomModelResourceLocation(itemBaseRing, 0, new ModelResourceLocation(RedstoneRepository.ID + ":" + "ring_base", "inventory"));
		}
	}
}


