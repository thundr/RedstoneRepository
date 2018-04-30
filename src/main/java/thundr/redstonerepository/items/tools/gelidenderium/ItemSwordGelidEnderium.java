package thundr.redstonerepository.items.tools.gelidenderium;

import cofh.core.init.CoreProps;
import cofh.core.util.helpers.DamageHelper;
import cofh.core.util.helpers.EnergyHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.redstonearsenal.init.RAProps;
import cofh.redstonearsenal.item.tool.ItemSwordFlux;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ItemSwordGelidEnderium extends ItemSwordFlux{

	//eventually set a config for this
    public int radius = 8;

    public ItemSwordGelidEnderium(ToolMaterial toolMaterial) {
        super(toolMaterial);
	    maxEnergy = GelidEnderiumEnergy.maxEnergy;
	    energyPerUse = GelidEnderiumEnergy.energyPerUse;
	    energyPerUseCharged =  GelidEnderiumEnergy.energyPerUseCharged;
	    maxTransfer = GelidEnderiumEnergy.maxTransfer;
        damage = 15;
        damageCharged = 8;
    }

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase entity, EntityLivingBase player) {

		if (stack.getItemDamage() > 0) {
			stack.setItemDamage(0);
		}
		EntityPlayer thePlayer = (EntityPlayer) player;

		if (thePlayer.capabilities.isCreativeMode || getEnergyStored(stack) >= energyPerUse) {
			int fluxDamage = isEmpowered(stack) ? damageCharged : 1;
			float potionDamage = 1.0F;

			if (player.isPotionActive(MobEffects.STRENGTH)) {
				potionDamage += player.getActivePotionEffect(MobEffects.STRENGTH).getAmplifier() * 1.3F;
			}
			entity.attackEntityFrom(DamageHelper.causePlayerFluxDamage(thePlayer), fluxDamage * potionDamage);

			AxisAlignedBB bb = new AxisAlignedBB(entity.posX - radius, entity.posY - radius, entity.posZ - radius, entity.posX + radius, entity.posY + radius, entity.posZ + radius);
			ArrayList<EntityMob> entities = new ArrayList<>(thePlayer.world.getEntitiesWithinAABB(EntityMob.class, bb));

            if(entities.size() > 0 && getEnergyStored(stack) >= energyPerUseCharged * entities.size()){
                for(EntityMob i : entities){
                    i.attackEntityFrom(DamageHelper.causePlayerFluxDamage(thePlayer), fluxDamage * potionDamage);
				}
				extractEnergy(stack, energyPerUseCharged * entities.size(), thePlayer.capabilities.isCreativeMode);
			}
		}
		return true;
	}


	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(StringHelper.BRIGHT_GREEN + "Smashes mobs in a large radius when empowered.");
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {
		return CoreProps.RGB_DURABILITY_ENDER;
	}
}
