package thundr.redstonerepository.items.tools.gelidenderium;

import cofh.core.init.CoreProps;
import cofh.core.util.helpers.StringHelper;
import cofh.redstonearsenal.item.tool.ItemAxeFlux;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

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
    //TODO: enable lumberaxe-like functionality

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
			if (isEmpowered(stack) && (block.isWood(world, pos) || canHarvestBlock(state, stack))) {
				for (int i = x - 1; i <= x + 1; i++) {
					for (int k = z - 1; k <= z + 1; k++) {
						for (int j = y - 4; j <= y + 4; j++) {
							BlockPos pos2 = new BlockPos(i, j, k);
							block = world.getBlockState(pos2).getBlock();
							if (block.isWood(world, pos2) || canHarvestBlock(state, stack)) {
								harvestBlock(world, pos2, player);
							}
						}
					}
				}
			}
			if (!player.capabilities.isCreativeMode) {
				useEnergy(stack, false);
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

}
