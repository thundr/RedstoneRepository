package thundr.redstonerepository.items.baubles;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import codechicken.lib.util.ItemNBTUtils;
import cofh.core.key.KeyBindingItemMultiMode;
import cofh.core.util.CoreUtils;
import cofh.core.util.helpers.StringHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import thundr.redstonerepository.RedstoneRepository;
import thundr.redstonerepository.init.RedstoneRepositoryEquipment;
import thundr.redstonerepository.items.ItemCoreRF;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//why this dont work?
//import static codechicken.lib.util.ItemNBTUtils;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class ItemRingEffect extends ItemCoreRF implements IBauble {

	public final static int POTION_DURATION_TICKS = 290;
	public static final int REMOVAL_TIMER = 100;
	public static final int COOLDOWN_TIMER = 1200;

	public static final String POWER_TICK = "pwrTick";
	public static final String UNTIL_SAFE_TO_REMOVE = "cd";
	public static final String EFFECTS = "efx";
	public static final String AMPLIFIER = "amp";
	public static final String ON_COOLDOWN = "cd2";

	public ConcurrentHashMap<UUID, ArrayList<PotionEffect>> globalMap;

	public ItemRingEffect() {
		super(RedstoneRepository.NAME);
		//CME generator start
		globalMap = new ConcurrentHashMap<>();
		maxEnergy = 4000000;
		maxTransfer = 500000;
		energyPerUse = 2000;
		setMaxStackSize(1);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			tooltip.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		tooltip.add(StringHelper.getInfoText("info.redstonerepository.ring.effect.title"));

		if (isActive(stack)) {
			tooltip.add(StringHelper.localizeFormat("info.redstonerepository.tooltip.active", StringHelper.BRIGHT_GREEN, StringHelper.END, StringHelper.getKeyName(KeyBindingItemMultiMode.INSTANCE.getKey())));
		} else {
			tooltip.add(StringHelper.localizeFormat("info.redstonerepository.tooltip.disabled", StringHelper.LIGHT_RED, StringHelper.END, StringHelper.getKeyName(KeyBindingItemMultiMode.INSTANCE.getKey())));
		}

		if(ItemNBTUtils.getInteger(stack, ON_COOLDOWN) > 0){
			tooltip.add(StringHelper.RED + StringHelper.localizeFormat("info.redstonerepository.ring.effect.disabled", StringHelper.formatNumber(ItemNBTUtils.getInteger(stack, ON_COOLDOWN))));
		}

		if(!RedstoneRepositoryEquipment.EquipmentInit.enable[0]){
			tooltip.add(StringHelper.RED + "Baubles not loaded: Recipe disabled.");
		}
		tooltip.add(StringHelper.localize("info.cofh.charge") + ": " + StringHelper.getScaledNumber(getEnergyStored(stack)) + " / " + StringHelper.getScaledNumber(getMaxEnergyStored(stack)) + " RF");
		tooltip.add(StringHelper.localize("info.cofh.send") + "/" + StringHelper.localize("info.cofh.receive") + ": " + StringHelper.formatNumber(maxTransfer) + "/" + StringHelper.formatNumber(maxTransfer) + " RF/t");
	}

	@Optional.Method(modid = "baubles")
	public BaubleType getBaubleType(ItemStack itemstack) {
		return BaubleType.RING;
	}

	@Optional.Method(modid = "baubles")
	public void onEquipped(ItemStack ring, EntityLivingBase player) {
		if (!(player instanceof EntityPlayer) || player.world.isRemote || CoreUtils.isFakePlayer(player)) {
			return;
		}
		EntityPlayer entityPlayer = (EntityPlayer) player;

		if (isActive(ring) && (getEnergyStored(ring) >= getEnergyPerUse(ring))) {
			ArrayList<PotionEffect> effects = new ArrayList<>(10);
			int powerUsage = getEnergyPerUse(ring); //use basic energy level when ring is active
			for (PotionEffect p: player.getActivePotionEffects()){
				p.duration = POTION_DURATION_TICKS;
				effects.add(p);
				//add to power usage per tick per potion and level (int)Math.pow(2, diff + 6.0)
				powerUsage += (int)Math.pow(getEnergyPerUse(ring), p.getAmplifier() );
				RedstoneRepository.LOG.info(powerUsage + "");
			}
			//Write potion list to NBT
			writePotionEffectsToNBT(effects, ring);
			//Update global potion map and power usage
			globalMap.put(entityPlayer.getUniqueID(), effects);
			ItemNBTUtils.setInteger(ring, POWER_TICK, powerUsage);
			ItemNBTUtils.setInteger(ring, UNTIL_SAFE_TO_REMOVE, REMOVAL_TIMER);

			// Kill the old potions.
			entityPlayer.clearActivePotions();
		}
	}

	@Optional.Method(modid = "baubles")
	public void onUnequipped(ItemStack ring, EntityLivingBase player) {
		if (!(player instanceof EntityPlayer) || player.world.isRemote || CoreUtils.isFakePlayer(player)) {
			return;
		}
		EntityPlayer entityPlayer = (EntityPlayer) player;

		if (isActive(ring) && (getEnergyStored(ring) >= getEnergyPerUse(ring))) {
			entityPlayer.clearActivePotions();
			if(ItemNBTUtils.getInteger(ring, UNTIL_SAFE_TO_REMOVE) > 0){
				RedstoneRepository.LOG.info("test1");

				ItemNBTUtils.setInteger(ring, ON_COOLDOWN, COOLDOWN_TIMER);
			}
			ItemNBTUtils.setInteger(ring, UNTIL_SAFE_TO_REMOVE, 0);

		}
		globalMap.remove(player.getUniqueID());
	}

	@Optional.Method(modid = "baubles")
	public void onWornTick(ItemStack ring, EntityLivingBase player) {

		if (!(player instanceof EntityPlayer) || player.world.isRemote || CoreUtils.isFakePlayer(player)) {
			return;
		}
		if(!ItemNBTUtils.hasTag(ring)){
			//somehow has been equipped without calling onEquipped
			RedstoneRepository.LOG.error("Stasis Ring has Invalid NBT! This is a bug! Report to author.");
			return;
		}

		if(ItemNBTUtils.getInteger(ring, ON_COOLDOWN) > 0){
			ItemNBTUtils.setInteger(ring, ON_COOLDOWN, ItemNBTUtils.getInteger(ring, ON_COOLDOWN) - 1);
		}

		EntityPlayer entityPlayer = (EntityPlayer) player;
		//Read potion list from cache

		ArrayList<PotionEffect> cacheEffects =  globalMap.get(entityPlayer.getUniqueID());
		if (cacheEffects == null && ItemNBTUtils.hasKey(ring, EFFECTS)){
			//Try to load potion list from NBT
			cacheEffects = readPotionEffectsFromNBT(ItemNBTUtils.getTag(ring));
			globalMap.put(entityPlayer.getUniqueID(), cacheEffects);
		}

		if (isActive(ring) && (getEnergyStored(ring) >= ItemNBTUtils.getInteger(ring, POWER_TICK))) {
			for (PotionEffect p : globalMap.get(entityPlayer.getUniqueID())) {
				player.addPotionEffect(p);
			}
			//Use energy to sustain potions
			useEnergyExact(ring, ItemNBTUtils.getInteger(ring, POWER_TICK), false);
			ItemNBTUtils.setInteger(ring, UNTIL_SAFE_TO_REMOVE, ItemNBTUtils.getInteger(ring, UNTIL_SAFE_TO_REMOVE) - 1);
		}
		else{
			entityPlayer.clearActivePotions();
			globalMap.remove(player.getUniqueID());
		}
	}

	public void onUpdate(ItemStack stack, World worldIn, Entity player, int itemSlot, boolean isSelected){
		if (!(player instanceof EntityPlayer) || player.world.isRemote || CoreUtils.isFakePlayer(player)) {
			return;
		}

		if(ItemNBTUtils.getInteger(stack, ON_COOLDOWN) > 0){
			ItemNBTUtils.setInteger(stack, ON_COOLDOWN, ItemNBTUtils.getInteger(stack, ON_COOLDOWN) - 1);
			RedstoneRepository.LOG.info("test1");
		}
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}

	public ArrayList<PotionEffect> readPotionEffectsFromNBT(NBTTagCompound tagCompound){
		if(tagCompound == null || tagCompound.getTag(EFFECTS) == null || tagCompound.getTag(AMPLIFIER) == null){
			return new ArrayList<>();
		}

		NBTTagList nbtEffects = (NBTTagList) tagCompound.getTag(EFFECTS);
		NBTTagList nbtAmp = (NBTTagList) tagCompound.getTag(AMPLIFIER);
		ArrayList<PotionEffect> toLoadEffects = new ArrayList<>();

		for(int i = 0; i < nbtEffects.tagCount(); i++){
			PotionEffect p = new PotionEffect(Potion.getPotionById(nbtEffects.getIntAt(i)), POTION_DURATION_TICKS, nbtAmp.getIntAt(i));
			toLoadEffects.add(p);
		}
		return toLoadEffects;
	}

	public void writePotionEffectsToNBT(ArrayList<PotionEffect> effects, ItemStack ring){
		NBTTagList tagListEffects = new NBTTagList();
		NBTTagList tagListIds = new NBTTagList();
		NBTTagCompound tagCompound = new NBTTagCompound();

		for (PotionEffect e : effects) {
			tagListEffects.appendTag(new NBTTagInt(e.getAmplifier()));
			tagListIds.appendTag(new NBTTagInt(Potion.getIdFromPotion(e.getPotion())));
		}
		if (!ring.hasTagCompound()) {
			tagCompound.setTag(EFFECTS, tagListIds);
			tagCompound.setTag(AMPLIFIER, tagListEffects);
			ring.setTagCompound(tagCompound);
		} else {
			ring.getTagCompound().setTag(EFFECTS, tagListIds);
			ring.getTagCompound().setTag(AMPLIFIER, tagListEffects);
		}
	}

}