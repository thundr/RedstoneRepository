package thundr.redstonerepository.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thundr.redstonerepository.RedstoneRepository;

import java.util.List;

//import net.minecraft.client.renderer.texture.IIconRegister;
//import net.minecraft.util.IIcon;

public class ItemArmorPlating extends Item {

    private static final String[] names = {"enderium", "power.empty", "power.full"};
//    public IIcon[] icon = new IIcon[16];

    public ItemArmorPlating() {
        setUnlocalizedName(RedstoneRepository.ID + ".plating");
        setCreativeTab(RedstoneRepository.tabCommon);
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return getUnlocalizedName() + "." + names[stack.getItemDamage() % names.length];
    }
//
//    @SideOnly(Side.CLIENT)
//    public IIcon getIconFromDamage(int meta) {
//        return this.icon[meta];
//    }
//
//    @SideOnly(Side.CLIENT)
//    public void registerIcons(IIconRegister iconRegister) {
//        this.icon[0] = iconRegister.registerIcon(RedstoneRepository.ID + ":materials/plateEnderium");
//        this.icon[1] = iconRegister.registerIcon(RedstoneRepository.ID + ":materials/plateCrafting_empty");
//        this.icon[2] = iconRegister.registerIcon(RedstoneRepository.ID + ":materials/plateCrafting_full");
//    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list) {
//        for (int i = 0; i < names.length; i++)
            list.add(new ItemStack(this, 1, 0));
    }

    public EnumRarity getRarity(ItemStack stack) {
        if (stack.getItemDamage() == 0)
            return EnumRarity.RARE;
        else if (stack.getItemDamage() == 1)
            return EnumRarity.UNCOMMON;
        else if (stack.getItemDamage() == 2)
            return EnumRarity.EPIC;

        return EnumRarity.COMMON;
    }
}
