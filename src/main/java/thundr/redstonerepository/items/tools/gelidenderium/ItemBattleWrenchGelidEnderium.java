package thundr.redstonerepository.items.tools.gelidenderium;

import cofh.core.init.CoreProps;
import cofh.redstonearsenal.item.tool.ItemBattleWrenchFlux;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


import java.util.ArrayList;
import java.util.Iterator;


public class ItemBattleWrenchGelidEnderium extends ItemBattleWrenchFlux{

    public int radius = 10;
    public ItemBattleWrenchGelidEnderium(ToolMaterial toolMaterial) {
        super(toolMaterial);
        damage = 7;
        damageCharged = 2;
	    maxEnergy = GelidEnderiumEnergy.maxEnergy;
	    energyPerUse = GelidEnderiumEnergy.energyPerUse;
	    energyPerUseCharged =  GelidEnderiumEnergy.energyPerUseCharged;
	    maxTransfer = GelidEnderiumEnergy.maxTransfer;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack held = player.getHeldItem(hand);
        BlockPos pos = player.getPosition();

        if (!world.isRemote && hand == EnumHand.MAIN_HAND && isEmpowered(held)) {
            ArrayList<EntityItem> items = new ArrayList<>(world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius,
                    pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius)));

            if(items.size() > 0 && getEnergyStored(held) >= energyPerUseCharged * items.size()){

                for(EntityItem i : items){
                    i.setPosition(pos.getX(), pos.getY(), pos.getZ());
                }

                extractEnergy(held, energyPerUseCharged * items.size(), player.capabilities.isCreativeMode);
            }

            player.swingArm(EnumHand.MAIN_HAND);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
        }

        return new ActionResult<ItemStack>(EnumActionResult.FAIL, player.getHeldItem(hand));
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
