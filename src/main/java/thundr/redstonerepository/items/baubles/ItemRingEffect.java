package thundr.redstonerepository.items.baubles;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.common.Baubles;
import cofh.core.key.KeyBindingItemMultiMode;
import cofh.core.util.CoreUtils;
import cofh.core.util.helpers.BaublesHelper;
import cofh.core.util.helpers.StringHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.PotionColorCalculationEvent;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.lwjgl.Sys;
import thundr.redstonerepository.RedstoneRepository;
import thundr.redstonerepository.init.RedstoneRepositoryEquipment;
import thundr.redstonerepository.items.ItemCoreRF;

import javax.annotation.Nullable;
import java.util.*;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class ItemRingEffect extends ItemCoreRF implements IBauble {

	public HashMap<UUID, ArrayList<PotionEffect>> globalMap;

	public ItemRingEffect() {
		super(RedstoneRepository.NAME);
		globalMap = new HashMap<>();

		maxEnergy = 4000000;
		maxTransfer = 100000;
		energyPerUse = 1000;
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
			tooltip.add(StringHelper.localizeFormat("info.redstonerepository.ring.effect.active", StringHelper.BRIGHT_GREEN, StringHelper.END, StringHelper.getKeyName(KeyBindingItemMultiMode.INSTANCE.getKey())));
		} else {
			tooltip.add(StringHelper.localizeFormat("info.redstonerepository.ring.effect.disabled", StringHelper.LIGHT_RED, StringHelper.END, StringHelper.getKeyName(KeyBindingItemMultiMode.INSTANCE.getKey())));
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
			ArrayList<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());
			writePotionEffectsToNBT(effects, ring);
			globalMap.put(entityPlayer.getUniqueID(), effects);
			entityPlayer.clearActivePotions();
			int sum = getEnergyPerUse(ring);
			for(PotionEffect p : effects){
				sum += p.getAmplifier() * getEnergyPerUse(ring);
			}
			writePowerToNBT(ring, sum);
		}
	}

	public void onUnequipped(ItemStack ring, EntityLivingBase player) {
		if (!(player instanceof EntityPlayer) || player.world.isRemote || CoreUtils.isFakePlayer(player)) {
			return;
		}
		EntityPlayer entityPlayer = (EntityPlayer) player;

		if (isActive(ring) && (getEnergyStored(ring) >= getEnergyPerUse(ring))) {
			entityPlayer.clearActivePotions();
		}
	}

	@Optional.Method(modid = "baubles")
	public void onWornTick(ItemStack ring, EntityLivingBase player) {

//		if (player.getEntityWorld().getTotalWorldTime() % 4 == 0) {
			if (!(player instanceof EntityPlayer) || player.world.isRemote || CoreUtils.isFakePlayer(player)) {
				return;
			}
			if(!ring.hasTagCompound()){
				//somehow has been equipped without calling onEquipped
				RedstoneRepository.LOG.error("Stasis Ring has Invalid NBT! This is a bug! Report to author.");
				return;
			}

			EntityPlayer entityPlayer = (EntityPlayer) player;

			ArrayList<PotionEffect> cacheEffects =  globalMap.get(entityPlayer.getUniqueID());
			if (cacheEffects == null && ring.hasTagCompound()){
				cacheEffects = readPotionEffectsFromNBT(ring.getTagCompound());
				globalMap.put(entityPlayer.getUniqueID(), cacheEffects);
			}

			if (isActive(ring) && (getEnergyStored(ring) >= getEnergyPerUse(ring))) {
//				if(entityPlayer.getActivePotionEffects().size() > cacheEffects.size()){
//					entityPlayer.clearActivePotions();
//					useEnergy(ring,  entityPlayer.getActivePotionEffects().size() - cacheEffects.size(), false);
//				}

				for (PotionEffect p : globalMap.get(entityPlayer.getUniqueID())) {
					PotionEffect pot = new PotionEffect(p.getPotion(), 290);
					entityPlayer.addPotionEffect(pot);
				}
				useEnergyExact(ring, ring.getTagCompound().getInteger("pwrTick"), false);
			}
//		}
	}

	public ArrayList<PotionEffect> readPotionEffectsFromNBT(NBTTagCompound tagCompound){
		if(tagCompound == null || tagCompound.getTag("efx") == null || tagCompound.getTag("amp") == null){
			return null;
		}

		NBTTagList nbtEffects = (NBTTagList) tagCompound.getTag("efx");
		NBTTagList nbtAmp = (NBTTagList) tagCompound.getTag("amp");
		ArrayList<PotionEffect> toLoadEffects = new ArrayList<>();

		for(int i = 0; i < nbtEffects.tagCount(); i++){
			PotionEffect p = new PotionEffect(Potion.getPotionById(nbtEffects.getIntAt(i)), 100, nbtAmp.getIntAt(i));
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
			tagCompound.setTag("efx", tagListIds);
			tagCompound.setTag("amp", tagListEffects);
			ring.setTagCompound(tagCompound);
		} else {
			ring.getTagCompound().setTag("efx", tagListIds);
			ring.getTagCompound().setTag("amp", tagListEffects);
		}
	}

	public void writePowerToNBT(ItemStack ring, int totalPowerToUse){
		NBTTagCompound tagCompound = new NBTTagCompound();
		NBTTagInt perTick = new NBTTagInt(totalPowerToUse);
		if (!ring.hasTagCompound()) {
			tagCompound.setTag("pwrTick", perTick);
			ring.setTagCompound(tagCompound);
		} else {
			ring.getTagCompound().setTag("pwrTick", perTick);
		}
	}
}