package thundr.redstonerepository.items.baubles;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cofh.core.util.CoreUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.Optional;
import thundr.redstonerepository.RedstoneRepository;
import thundr.redstonerepository.items.ItemCoreRF;

import java.util.ArrayList;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class ItemEffectRing extends ItemCoreRF implements IBauble {

    public ItemEffectRing() {
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
}
