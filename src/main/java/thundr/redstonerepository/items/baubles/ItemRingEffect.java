package thundr.redstonerepository.items.baubles;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cofh.core.util.CoreUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.Optional;
import thundr.redstonerepository.RedstoneRepository;
import thundr.redstonerepository.items.ItemCoreRF;

import java.util.ArrayList;
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
    public BaubleType getBaubleType(ItemStack itemstack){
        return BaubleType.RING;
    }

	@Optional.Method(modid = "baubles")
	public void onEquipped(ItemStack ring, EntityLivingBase player) {
		if(!(player instanceof EntityPlayer) || player.world.isRemote || CoreUtils.isFakePlayer(player)){
			return;
		}

		EntityPlayer entityPlayer = (EntityPlayer)player;

		if(isActive(ring) && (getEnergyStored(ring) >= getEnergyPerUse(ring))){
			ArrayList<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());
			NBTTagList tagListEffects = new NBTTagList();
			NBTTagList tagListIds = new NBTTagList();
			NBTTagCompound tagCompound = new NBTTagCompound();

			for(PotionEffect e : effects){
				tagListEffects.appendTag(new NBTTagInt(e.getAmplifier()));
				tagListIds.appendTag(new NBTTagInt(Potion.getIdFromPotion(e.getPotion())));
				globalMap.put(entityPlayer.getUniqueID(), effects);
				System.out.println(e.getPotion().getRegistryName());
			}
			if (!ring.hasTagCompound()) {
				tagCompound.setTag("curEffects", tagListIds);
				tagCompound.setTag("efx", tagListEffects);
				ring.setTagCompound(tagCompound);
			}
			else {
				ring.getTagCompound().setTag("curEffects", tagListIds);
				ring.getTagCompound().setTag("efx", tagListEffects);
			}
		}
	}

	@Optional.Method(modid = "baubles")
	public void onWornTick(ItemStack ring, EntityLivingBase player){
    	if(player.getEntityWorld().getTotalWorldTime() % 4 == 0) {
		    if (!(player instanceof EntityPlayer) || player.world.isRemote || CoreUtils.isFakePlayer(player)) {
			    return;
		    }

		    EntityPlayer entityPlayer = (EntityPlayer) player;

		    if (isActive(ring) && (getEnergyStored(ring) >= getEnergyPerUse(ring))) {
			    ArrayList<PotionEffect> effects = new ArrayList<>(entityPlayer.getActivePotionEffects());

			    if (!globalMap.get(entityPlayer.getUniqueID()).equals(effects)) {
					entityPlayer.clearActivePotions();
					for(PotionEffect p :  globalMap.get(entityPlayer.getUniqueID())){
						PotionEffect pot = new PotionEffect(p.getPotion(), 1000);
						entityPlayer.addPotionEffect(pot);
					}
			    }
//			    if (!ring.hasTagCompound()) {
//				    return;
//			    }
//
//			    ring.getTagCompound().getTag("curEffects");
		    }

	    }
	}


}
