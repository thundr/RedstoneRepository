package thundr.redstonerepository.items.baubles;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.common.Baubles;
import cofh.core.util.CoreUtils;
import cofh.core.util.helpers.BaublesHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.PotionColorCalculationEvent;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.lwjgl.Sys;
import thundr.redstonerepository.RedstoneRepository;
import thundr.redstonerepository.items.ItemCoreRF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class ItemRingEffect extends ItemCoreRF implements IBauble {

	public HashMap<UUID, ArrayList<PotionEffect>> globalMap;

	public ItemRingEffect() {
		super(RedstoneRepository.NAME);
		globalMap = new HashMap<>();

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
		}
	}

	@Optional.Method(modid = "baubles")
	public void onWornTick(ItemStack ring, EntityLivingBase player) {

		if (player.getEntityWorld().getTotalWorldTime() % 4 == 0) {
			if (!(player instanceof EntityPlayer) || player.world.isRemote || CoreUtils.isFakePlayer(player)) {
				return;
			}

			EntityPlayer entityPlayer = (EntityPlayer) player;
			if (globalMap.get(entityPlayer.getUniqueID()) == null && ring.hasTagCompound()){
				globalMap.put(entityPlayer.getUniqueID(), readPotionEffectsFromNBT(ring.getTagCompound()));
			}

			if (isActive(ring) && (getEnergyStored(ring) >= getEnergyPerUse(ring))) {
				ArrayList<PotionEffect> effects = new ArrayList<>(entityPlayer.getActivePotionEffects());
				if (!globalMap.get(entityPlayer.getUniqueID()).equals(effects)) {
//					entityPlayer.clearActivePotions();
					for (PotionEffect p : globalMap.get(entityPlayer.getUniqueID())) {
						PotionEffect pot = new PotionEffect(p.getPotion(), 100);
						entityPlayer.addPotionEffect(pot);
					}
				}
			}
		}
	}

	public ArrayList<PotionEffect> readPotionEffectsFromNBT(NBTTagCompound tagCompound){
		if(tagCompound == null || tagCompound.getTag("curEffects") == null || tagCompound.getTag("efx") == null){
			return null;
		}

		NBTTagList nbtEffects = (NBTTagList) tagCompound.getTag("curEffects");
		NBTTagList nbtAmp = (NBTTagList) tagCompound.getTag("efx");
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
			tagCompound.setTag("curEffects", tagListIds);
			tagCompound.setTag("efx", tagListEffects);
			ring.setTagCompound(tagCompound);
		} else {
			ring.getTagCompound().setTag("curEffects", tagListIds);
			ring.getTagCompound().setTag("efx", tagListEffects);
		}
	}

//	@SubscribeEvent
//	public void potionListener(PotionColorCalculationEvent event) {
//		if (event.getEntity() instanceof EntityPlayer) {
//			EntityPlayer player = (EntityPlayer) event.getEntity();
//			for (ItemStack item : BaublesHelper.getBaubles(player)) {
//				if (item.getItem() instanceof ItemRingEffect) {
//					RedstoneRepository.LOG.info("RingEffect: Caught potion event on player wearing ring");
//					ArrayList<PotionEffect> playerEffects = new ArrayList<>(event.getEffects());
//					for(PotionEffect p : playerEffects){
//					}
//				}
//			}
//		}
//
//	}
}