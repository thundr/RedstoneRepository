package thundr.redstonerepository.items.baubles;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.common.Baubles;
import cofh.core.util.CoreUtils;
import cofh.core.util.helpers.BaublesHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.PotionColorCalculationEvent;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import thundr.redstonerepository.RedstoneRepository;
import thundr.redstonerepository.items.ItemCoreRF;

import java.util.ArrayList;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class ItemRingEffect extends ItemCoreRF implements IBauble {

    public ItemRingEffect() {
        super(RedstoneRepository.NAME);
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
//			for(effects.)
		}
	}

	@Optional.Method(modid = "baubles")
	public void onWornTick(ItemStack ring, EntityLivingBase player){
    	if(!(player instanceof EntityPlayer) || player.world.isRemote || CoreUtils.isFakePlayer(player)){
    		return;
	    }



	}

	@SubscribeEvent
	public void potionListener(PotionColorCalculationEvent event){
    	if (event.getEntity() instanceof EntityPlayer){
    		EntityPlayer player = (EntityPlayer)event.getEntity();
    		for (ItemStack item : BaublesHelper.getBaubles(player)){
    			if (item.getItem() instanceof ItemRingEffect){
					RedstoneRepository.LOG.info("RingEffect: Caught potion event on player wearing ring");
				}
			}
		}
	}
}
