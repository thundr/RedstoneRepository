package thundr.redstonerepository.items.tools.gelidenderium;

import cofh.core.init.CoreProps;
import cofh.core.util.helpers.StringHelper;
import cofh.redstonearsenal.item.tool.ItemAxeFlux;
import com.sun.istack.internal.NotNull;
import jline.internal.Log;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nullable;
import java.util.*;

import static cofh.redstonearsenal.RedstoneArsenal.MOD_NAME;

public class ItemAxeGelidEnderium extends ItemAxeFlux{

	public static final int LIGHTNING_ENERGY = 6400;
	public static final int EMPOWERED_LIGHTNING_ENERGY = 48000;

    public ItemAxeGelidEnderium(Item.ToolMaterial toolMaterial) {
        super(toolMaterial);
	    maxEnergy = GelidEnderiumEnergy.maxEnergy;
	    energyPerUse = GelidEnderiumEnergy.energyPerUse;
	    energyPerUseCharged =  GelidEnderiumEnergy.energyPerUseCharged;
	    maxTransfer = GelidEnderiumEnergy.maxTransfer;
	    damage = 10;
    }
    //TODO: enable lumberaxe-like functionality WIP!!!!!

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
//        if (ConfigHandler.enableAxeWeatherClear) {
	    Random rand = new Random();
	    ItemStack held = player.getHeldItem(hand);
            if (!world.isRemote && hand == EnumHand.MAIN_HAND) {
                if (isEmpowered(held) && (world.isRaining() || world.isThundering())) {

                    WorldInfo worldinfo = world.getWorldInfo();
                    int i = 300 + rand.nextInt(600) * 20;
                    worldinfo.setRaining(false);
                    worldinfo.setThundering(false);
                    worldinfo.setRainTime(i);
	                world.spawnEntity(new EntityLightningBolt(world, player.posX, player.posY, player.posZ, true));
                    if (!player.capabilities.isCreativeMode)
                        useEnergy(held, false);
                }
            }
		player.swingArm(EnumHand.MAIN_HAND);
//        }
        return new ActionResult<ItemStack>(EnumActionResult.PASS, player.getHeldItem(hand));
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
//        if (ConfigHandler.enableAxeLightning) {
	    ItemStack held = player.getHeldItem(hand);
	    int x = pos.getX();
	    int y = pos.getY();
	    int z = pos.getZ();
            if(getEnergyStored(held) > LIGHTNING_ENERGY){
                if(!isEmpowered(held)){
	                world.spawnEntity(new EntityLightningBolt(world, x, y, z, false));
	                if(!player.capabilities.isCreativeMode){
		                extractEnergy(held, LIGHTNING_ENERGY, false);
	                }
                }
                else if(isEmpowered(held) && getEnergyStored(held) >= EMPOWERED_LIGHTNING_ENERGY){
                    for(int i = 0; i <= 10; i++){
                        world.spawnEntity(new EntityLightningBolt(world, x, y, z, false));
                    }
	                if(!player.capabilities.isCreativeMode){
		                extractEnergy(held, EMPOWERED_LIGHTNING_ENERGY, false);
	                }
                }
            }
//        }
		return EnumActionResult.SUCCESS;
    }

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {

		World world = player.world;
		IBlockState state = world.getBlockState(pos);

		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		Block block = state.getBlock();

		float refStrength = state.getPlayerRelativeBlockHardness(player, world, pos);
		if (refStrength != 0.0F) {
			if (isEmpowered(stack) && block.isWood(world, pos) && canHarvestBlock(state, stack)) {
				if (!world.isRemote)
				{
					// Call the task to cut tree down asyc
					MinecraftForge.EVENT_BUS.register(new CutTreeTask(stack, pos, player));
				}
				if (!player.capabilities.isCreativeMode) {
					useEnergy(stack, false);
				}
				return true;
			}
		}
		return false;
	}


	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(StringHelper.BRIGHT_GREEN + "Right click a block to call down the power of the sky.");
		tooltip.add(StringHelper.BRIGHT_GREEN + "Right click the air to clear the skies when empowered.");
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

	@Override
	protected boolean harvestBlock(World world, BlockPos pos, EntityPlayer player) {
		if (world.isAirBlock(pos)) {
			return false;
		}
		EntityPlayerMP playerMP = null;
		if (player instanceof EntityPlayerMP) {
			playerMP = (EntityPlayerMP) player;
		}
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		// only effective materials
		if (!(toolClasses.contains(state.getBlock().getHarvestTool(state)) || canHarvestBlock(state, player.getHeldItemMainhand()))) {
			return false;
		}
		if (!ForgeHooks.canHarvestBlock(block, player, world, pos)) {
			return false;
		}

		if (!world.isRemote) {
			// send the blockbreak event
			int xpToDrop = 0;
			if (playerMP != null) {
				xpToDrop = ForgeHooks.onBlockBreakEvent(world, playerMP.interactionManager.getGameType(), playerMP, pos);
				if (xpToDrop == -1) {
					return false;
				}
			}

			if (block.removedByPlayer(state, world, pos, player, !player.capabilities.isCreativeMode)) {
				block.onBlockDestroyedByPlayer(world, pos, state);

				if (!player.capabilities.isCreativeMode) {
					block.harvestBlock(world, player, pos, state, world.getTileEntity(pos), player.getHeldItemMainhand());
					if (xpToDrop > 0) {
						block.dropXpOnBlockBreak(world, pos, xpToDrop);
					}
				}
			}
			// always send block update to client
			playerMP.connection.sendPacket(new SPacketBlockChange(world, pos));
		} else {
			if (block.removedByPlayer(state, world, pos, player, !player.capabilities.isCreativeMode)) {
				block.onBlockDestroyedByPlayer(world, pos, state);
			}
			Minecraft.getMinecraft().getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, Minecraft.getMinecraft().objectMouseOver.sideHit));
		}
		return true;
	}




	public static class CutTreeTask
	{
		public World world;
		public ItemStack tool;
		public ItemAxeGelidEnderium axe;
		public BlockPos pos;
		public EntityPlayer player = null;
		public boolean blockDeleted = false;

		public Queue<BlockPos> candidates = new LinkedList<BlockPos>();
		public HashSet<BlockPos> visited = new HashSet<BlockPos>();

		public CutTreeTask(@NotNull ItemStack stack,@NotNull BlockPos pos,@NotNull EntityPlayer player) {
			if (stack.getItem() instanceof ItemAxeGelidEnderium) {
				this.tool = stack;
				// Cast to correct item
				this.axe = (ItemAxeGelidEnderium)stack.getItem();
			}
			else {
				// We're not a Gelid Enderium Axe!! GAAHHHH
				unregister();
				return;
			}
			this.pos = pos;
			this.player = player;
			this.world = player.getEntityWorld();

			candidates.add(pos); // add starting block
		}

		@SubscribeEvent
		public void cutTree(TickEvent.WorldTickEvent event) {
			if (event.side.isClient()){
				//Only run on server.
				unregister();
				return;
			}

			// Ignore ticks from other dimensions.
			if(event.world.provider.getDimension() != world.provider.getDimension()) {
				return;
			}

			if (axe == null){
				unregister();
				return;
			}

			// Check to see if we have enough energy to cut the log.
			if (axe.getEnergyStored(tool) < axe.getEnergyPerUse(tool)){
				unregister();
				return;
			}

			// Loop through the blocks in the candidates until we break one.
			while(!blockDeleted) {
				// check if any blocks in queue. If there are none, stop.
				if (candidates.isEmpty()){
					unregister();
					return;
				}

				// Get current block position
				BlockPos curPos = candidates.remove();
				// Check to see if we've visited it before.
				// If we have, continue to the next block.
				// Else look at current block.
				if (!visited.add(curPos)) {
					continue;
				}

				IBlockState state = world.getBlockState(curPos);
				if(!state.getBlock().isWood(world, curPos)) {
					// Not a log, try the next one.
					continue;
				}

				//TODO: Verify that this hardness check works
				float refStrength = state.getPlayerRelativeBlockHardness(player, world, curPos);
				if (refStrength != 0.0F) {

					// Need to iterate through all the sides on the same y level and the one above
					BlockPos newPos;
					for (int x = -1; x < 2; x++){
						for (int y = 0; y < 2; y++){
							for (int z = -1; z < 2; z++) {
								newPos = curPos.add(x, y, z);
								if (!visited.contains(newPos)){
									// If not visited yet, then add to the candidate list!
									candidates.add(newPos);

								}
							}
						}
					}

					//If we get here, we should actually break the block.
					axe.harvestBlock(world, curPos, player);
					//TODO: remove logging once done debugging
					//sLog.info(MOD_NAME + ": Breaking block at " + curPos.toString());
					// Stop looping. We're done working for this tick.
					blockDeleted = true;
				}
			}
			// Reset for next run
			blockDeleted = false;
		}

		private void unregister() {
			// goodbye cruel world
			MinecraftForge.EVENT_BUS.unregister(this);
		}
	}
}

