package thundr.redstonerepository.util;

import cofh.core.util.helpers.BaublesHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.living.PotionColorCalculationEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import thundr.redstonerepository.RedstoneRepository;
import thundr.redstonerepository.items.baubles.ItemRingEffect;
import thundr.redstonerepository.items.tools.gelidenderium.ItemPickaxeGelidEnderium;


public class ToolEventHandler {

	public static int pickaxeDistanceFactor;
	public static int pickaxeDimensionFactor;

	public static void preInit(){
		pickaxeDistanceFactor =  RedstoneRepository.CONFIG.get("Equipment.Tools", "PickaxeDistanceDrainFactor", 5,
				"Set the factor that scales the power drained from the Gelid Enderium Pickaxe when teleporting items over a distance. (distance*factor*itemDrops=power)");
		pickaxeDimensionFactor =  RedstoneRepository.CONFIG.get("Equipment.Tools", "PickaxeDimensionDrainFactor", 7500,
				"Set the factor that scales the power drained from the Gelid Enderium Pickaxe when teleporting items between dimensions. This is a flat value per item.");
	}

    @SubscribeEvent
    public void onHarvestDrops(BlockEvent.HarvestDropsEvent event) {
		World world = event.getWorld();
        if (!world.isRemote) {

            if (event.getHarvester() != null && !event.getHarvester().getHeldItem(EnumHand.MAIN_HAND).isEmpty() &&
		            event.getHarvester().getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemPickaxeGelidEnderium) {

                ItemStack stack = event.getHarvester().getHeldItem(EnumHand.MAIN_HAND);
	            ItemPickaxeGelidEnderium pickaxe = (ItemPickaxeGelidEnderium) event.getHarvester().getHeldItem(EnumHand.MAIN_HAND).getItem();

                if (isEmpowered(stack)) {

                    if (stack.getTagCompound() == null) {
	                    stack.setTagCompound(new NBTTagCompound());
                    }

                    NBTTagCompound tag = stack.getTagCompound();
                    int coordX = tag.getInteger("CoordX");
                    int coordY = tag.getInteger("CoordY");
                    int coordZ = tag.getInteger("CoordZ");
                    int dimID = tag.getInteger("DimID");
                    int side = tag.getInteger("Side");
                    boolean isBound = tag.getBoolean("Bound");

	                if (isBound) {
		                World boundWorld = DimensionManager.getWorld(dimID);
		                if (event.getWorld().getBlockState(event.getPos()) != boundWorld.getBlockState(new BlockPos(coordX, coordY, coordZ))) {

			                TileEntity bound = boundWorld.getTileEntity(new BlockPos(coordX, coordY, coordZ));
			                IItemHandler inventory;
			                EnumFacing dir = EnumFacing.getFront(side);

			                if (bound.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir)) {
				                inventory = bound.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir);
			                } else {
				                return;
			                }

			                for (int drop = 0; drop < event.getDrops().size(); drop++) {
				                ItemStack returned = ItemHandlerHelper.insertItemStacked(inventory, event.getDrops().get(drop), false);
				                //drain energy depending on how far away you are from the inventory
				                int temp = drainEnergyByDistance(event.getPos(), new BlockPos(coordX, coordY, coordZ),
						                !(dimID == event.getHarvester().dimension));
				                pickaxe.extractEnergy(stack, temp, false);
				                if (returned.isEmpty()) {
					                event.setDropChance(0);
				                } else {
					                return;
				                }
			                }
		                }
	                }
                }
            }
        }
    }

    //calculates the proper amount of energy to drain by how far the harvested blocks are being teleported
	//returns energy to drain
    private int drainEnergyByDistance(BlockPos from, BlockPos to, boolean interdim){
    	if(!interdim){
		    return (int) ((Math.sqrt(Math.pow(from.getX() - to.getX(), 2) + Math.pow(from.getY() - to.getY(), 2) + Math.pow(from.getX() - to.getX(), 2)))*pickaxeDistanceFactor);
	    }
	    else{
    		return pickaxeDimensionFactor;
	    }
    }

    //cause cofh methods are protected :))))))))))))))))))))))))))
    public boolean isEmpowered(ItemStack stack) {
    	//this casts to a pick every time, but that doesn't matter. check here if bug
    	ItemPickaxeGelidEnderium pick = (ItemPickaxeGelidEnderium)stack.getItem();
	    return pick.getMode(stack) == 1 && pick.getEnergyStored(stack) >= pick.getEnergyPerUseCharged();
    }

	@SubscribeEvent
	public void potionListener(PotionColorCalculationEvent event) {
		if (event.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntity();
			for (ItemStack item : BaublesHelper.getBaubles(player)) {
				if (item.getItem() instanceof ItemRingEffect) {
					RedstoneRepository.LOG.info("RingEffect: Caught potion event on player wearing ring");
				}
			}
		}
	}
}
