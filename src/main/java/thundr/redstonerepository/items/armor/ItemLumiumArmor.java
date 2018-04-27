//package thundr.redstonerepository.items.armor;
//
//import cofh.core.item.ItemArmorAdv;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.init.Blocks;
//import net.minecraft.item.EnumRarity;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.EnumChatFormatting;
//import net.minecraft.world.World;
//import thundr.redstonerepository.RedstoneRepository;
//import thundr.redstonerepository.RedstoneRepository;
//import thundr.redstonerepository.blocks.BlockRegistry;
//import thundr.redstonerepository.items.ItemRegistry;
//import thundr.redstonerepository.util.Utils;
//TODO: see if this is worth adding back
//import java.util.List;
//
//public class ItemLumiumArmor extends ItemArmorAdv {
//
//    public ItemLumiumArmor(int type) {
//
//        super(ItemRegistry.ARMOR_MATERIAL_LUMIUM, type);
//        setRepairIngot("ingotLumium");
//        setCreativeTab(RedstoneRepository.tabRArm);
//
//        switch (type) {
//            case 0: {
//                setTextureName(RedstoneRepository.ID + ":armor/lumiumHelm");
//                setUnlocalizedName(RedstoneRepository.ID + ".armor.lumium.helm");
//                break;
//            }
//            case 1: {
//                setTextureName(RedstoneRepository.ID + ":armor/lumiumChestplate");
//                setUnlocalizedName(RedstoneRepository.ID + ".armor.lumium.chestplate");
//                break;
//            }
//            case 2: {
//                setTextureName(RedstoneRepository.ID + ":armor/lumiumLeggings");
//                setUnlocalizedName(RedstoneRepository.ID + ".armor.lumium.leggings");
//                break;
//            }
//            case 3: {
//                setTextureName(RedstoneRepository.ID + ":armor/lumiumBoots");
//                setUnlocalizedName(RedstoneRepository.ID + ".armor.lumium.boots");
//                break;
//            }
//        }
//    }
//
//    @Override
//    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
//        int x = (int) Math.floor(player.posX);
//        int y = (int) player.posY + 1;
//        int z = (int) Math.floor(player.posZ);
//
//        if (!world.isRemote && world.getBlock(x, y, z) == Blocks.air)
//            world.setBlock(x, y, z, BlockRegistry.invisiLight);
//    }
//
//    @Override
//    @SideOnly(Side.CLIENT)
//    public String getArmorTexture(ItemStack Stack, Entity entity, int slot, String type) {
//        if (slot == 2)
//            return RedstoneRepository.ID + ":textures/models/armor/lumiumArmor_2.png";
//        else
//            return RedstoneRepository.ID + ":textures/models/armor/lumiumArmor_1.png";
//
//    }
//
//    @Override
//    public EnumRarity getRarity(ItemStack stack) {
//        return EnumRarity.uncommon;
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {
//        list.add("");
//        list.add(EnumChatFormatting.GRAY + Utils.localize("info.RArm.tooltip.armor.lumium.shine"));
//    }
//}
