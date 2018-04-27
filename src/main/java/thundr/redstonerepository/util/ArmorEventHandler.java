package thundr.redstonerepository.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thundr.redstonerepository.RedstoneRepository;
import thundr.redstonerepository.init.RedstoneRepositoryEquipment;
import thundr.redstonerepository.items.armor.ItemArmorEnderium;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ArmorEventHandler {

	public static int fallDrainFactor;
	public static int fireDrainFactor;
	public static int fluxDrainFactor;

	public static void preInit(){
		fallDrainFactor =  RedstoneRepository.CONFIG.get("Equipment.Armor", "FallDamageDrainFactor", 2400,
				"Set the factor that calculates how much energy a fall drains. Scales with height.");
		fireDrainFactor = RedstoneRepository.CONFIG.get("Equipment.Armor", "FireDamageDrainFactor", 1200,
				"Set the factor that calculates how much energy fire-type damage drains.");
		fluxDrainFactor = RedstoneRepository.CONFIG.get("Equipment.Armor", "FluxDamageDrainFactor", 4800,
				"Set the factor that calculates how much energy Flux damage drains.");
	}

	@SubscribeEvent
	public void onFall(LivingFallEvent event) {
		if(event.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();

			if(!player.world.isRemote){
				ArmorSummary summary = new ArmorSummary().getSummary(player);

				if(summary != null){
					if (summary.enderiumPieces.containsKey("Boots")) {
						int toDrain = (int) (event.getDistance() * fallDrainFactor);

						if(summary.energyStored.get("Boots") >= toDrain){
							ItemStack boots = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
							event.setDamageMultiplier(0);
							//TODO: I should really make a nice util method for extractEnergy
							summary.enderiumPieces.get("Boots").extractEnergy(boots, toDrain, false);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerAttacked(LivingAttackEvent event) {
		if(event.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();

			if(!player.world.isRemote){
				ArmorSummary summary = new ArmorSummary().getSummary(player);

				if(summary.isFullSet){
					//the enderium armor is made out of cryotheum, after all...
					if(event.getSource().isFireDamage()){
						if(doFullArmorDrain((int) event.getAmount()* fireDrainFactor, summary, player)){
							event.setCanceled(true);
						}
					}
					//this is to deal with flux quiver weirdness, bypasses armor calc
					else if(event.getSource().getDamageType().contains("flux")){
						if(doFullArmorDrain((int) event.getAmount()* fluxDrainFactor, summary, player)){
							event.setCanceled(true);
						}
					}
				}
			}
		}
	}

	private boolean doFullArmorDrain(int toDrain, ArmorSummary summary, EntityPlayer player){
		if(summary.totalEnergyStored >= toDrain){
			Iterator armor = player.getArmorInventoryList().iterator();
			summary.enderiumPieces.forEach((key, value) -> value.extractEnergy((ItemStack)armor.next(), toDrain/4, false));
			return true;
		}
		else {
			return false;
		}
	}




	// Idea adapted from Draconic Evolution by brandon3055
	// https://github.com/brandon3055/Draconic-Evolution
	public static class ArmorSummary{

		// energy stored in armor from top piece down (helm, chest, legs, boots)
		// 0 if no armor or depleted
		public Map<String, Integer> energyStored = new HashMap<String, Integer>();

		public int totalEnergyStored = 0;

		//armor on player
		ArrayList<ItemStack> armorStacks = new ArrayList<>();

		//our armor on player
		public Map<String, ItemArmorEnderium> enderiumPieces = new HashMap<String, ItemArmorEnderium>();

		public boolean isFullSet = false;

		public ArmorSummary getSummary(EntityPlayer player){
			energyStored.put("Helm", 0);
			energyStored.put("Chest", 0);
			energyStored.put("Legs", 0);
			energyStored.put("Boots", 0);

			armorStacks.add(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
			armorStacks.add(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
			armorStacks.add(player.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
			armorStacks.add(player.getItemStackFromSlot(EntityEquipmentSlot.FEET));

			for(ItemStack i : armorStacks){
				if (i.isEmpty() || !(i.getItem() instanceof ItemArmorEnderium)) continue;
				ItemArmorEnderium armor = (ItemArmorEnderium) i.getItem();
				int energy = armor.getEnergyStored(i);

				if (armor == RedstoneRepositoryEquipment.ArmorSet.GELID.itemHelmet) {
					energyStored.replace("Helm", 0, energy);
					totalEnergyStored += energy;
					enderiumPieces.put("Helm", armor);
				}
				else if(armor == RedstoneRepositoryEquipment.ArmorSet.GELID.itemPlate){
					energyStored.replace("Chest", 0, energy);
					totalEnergyStored += energy;
					enderiumPieces.put("Chest", armor);
				}
				else if(armor == RedstoneRepositoryEquipment.ArmorSet.GELID.itemLegs){
					energyStored.replace("Legs", 0, energy);
					totalEnergyStored += energy;
					enderiumPieces.put("Legs", armor);
				}
				else if(armor == RedstoneRepositoryEquipment.ArmorSet.GELID.itemBoots){
					energyStored.replace("Boots", 0, energy);
					totalEnergyStored += energy;
					enderiumPieces.put("Boots", armor);
				}

			}
			if(enderiumPieces == null){
				return null;
			}
			else{
				isFullSet = enderiumPieces.size() == 4;
				return this;
			}
		}
	}
}
