//package thundr.redstonerepository.items.armor;
//
//import cofh.core.item.ItemArmorAdv;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.EnumChatFormatting;
//import net.minecraft.world.World;
//import thundr.redstonerepository.RedstoneRepository;
//import thundr.redstonerepository.RedstoneRepository;
//import thundr.redstonerepository.items.ItemRegistry;
//import thundr.redstonerepository.util.KeyboardHelper;
//import thundr.redstonerepository.util.Utils;
//
//import java.util.List;
//
//public class ItemTuberousArmor extends ItemArmorAdv {
//
//    public ItemTuberousArmor(int type) {
//
//        super(ItemRegistry.ARMOR_MATERIAL_TUBEROUS, type);
//        setCreativeTab(RedstoneRepository.tabRArm);
//        setRepairIngot("cropPotato");
//
//        switch (type) {
//            case 0: {
//                setTextureName(RedstoneRepository.ID + ":armor/tuberousHelm");
//                setUnlocalizedName(RedstoneRepository.ID + ".armor.tuberous.helm");
//                setMaxDamage(32);
//                break;
//            }
//            case 1: {
//                setTextureName(RedstoneRepository.ID + ":armor/tuberousChestplate");
//                setUnlocalizedName(RedstoneRepository.ID + ".armor.tuberous.chestplate");
//                setMaxDamage(58);
//                break;
//            }
//            case 2: {
//                setTextureName(RedstoneRepository.ID + ":armor/tuberousLeggings");
//                setUnlocalizedName(RedstoneRepository.ID + ".armor.tuberous.leggings");
//                setMaxDamage(41);
//                break;
//            }
//            case 3: {
//                setTextureName(RedstoneRepository.ID + ":armor/tuberousBoots");
//                setUnlocalizedName(RedstoneRepository.ID + ".armor.tuberous.boots");
//                setMaxDamage(25);
//                break;
//            }
//        }
//    }
//
//    @Override
//    @SideOnly(Side.CLIENT)
//    public String getArmorTexture(ItemStack Stack, Entity entity, int slot, String type) {
//        if (slot == 2)
//            return RedstoneRepository.ID + ":textures/models/armor/tuberousArmor_2.png";
//        else
//            return RedstoneRepository.ID + ":textures/models/armor/tuberousArmor_1.png";
//
//    }
//
//    @Override
//    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
//        if (player.getFoodStats().needFood()) {
//            player.inventory.damageArmor(1F);
//            player.getFoodStats().addStats(1, 4.0f);
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {
//
//        if (KeyboardHelper.isShiftDown()) {
//            list.add("");
//            list.add(EnumChatFormatting.GRAY + Utils.localize("info.RArm.tooltip.armor.tuberous.clever.1"));
//            list.add(EnumChatFormatting.GRAY + Utils.localize("info.RArm.tooltip.armor.tuberous.clever.2"));
//        }
//    }
//}
