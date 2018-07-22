//package thundr.redstonerepository.init;
//
//import net.minecraft.init.PotionTypes;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.crafting.Ingredient;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.potion.PotionType;
//import net.minecraft.util.ResourceLocation;
//import net.minecraftforge.fluids.Fluid;
//import net.minecraftforge.fluids.FluidRegistry;
//import net.minecraftforge.fluids.FluidStack;
//import net.minecraftforge.fluids.FluidUtil;
//import net.minecraftforge.fml.common.registry.ForgeRegistries;
//import net.minecraftforge.fml.common.registry.GameRegistry;
//import net.minecraftforge.common.crafting.IngredientNBT;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//public class PotionFourBucketHelper {
//    public static ArrayList<ItemStack> buckets = new ArrayList<ItemStack>();
//    public static Fluid potionBase;
//
//
//    public static void initialize() {
//        potionBase = FluidRegistry.getFluid("potion");
//        Iterator iter = ForgeRegistries.POTION_TYPES.getValuesCollection().iterator();
//        while(iter.hasNext()){
//            PotionType pot = (PotionType)iter.next();
//            String name = pot.getRegistryName().toString();
//            System.out.println( name + "F1002");
//            if (name.endsWith("4")){
//                System.out.println("Added " + name + "F1002");
//                buckets.add(FluidUtil.getFilledBucket(addPotionToFluidStack(new FluidStack(potionBase, 1000), pot)));
//            }
//        }
//        // Make fluids from potions
//
//
//    }
//
//    //From TFFluids.java in Thermal Foundation (https://github.com/CoFH/ThermalFoundation)
//    public static FluidStack addPotionToFluidStack(FluidStack stack, PotionType type) {
//
//        ResourceLocation resourcelocation = PotionType.REGISTRY.getNameForObject(type);
//
//        /* NOTE: This can actually happen. */
//        if (resourcelocation == null) {
//            return null;
//        }
//        if (type == PotionTypes.EMPTY) {
//            if (stack.tag != null) {
//                stack.tag.removeTag("Potion");
//                if (stack.tag.hasNoTags()) {
//                    stack.tag = null;
//                }
//            }
//        } else {
//            if (stack.tag == null) {
//                stack.tag = new NBTTagCompound();
//            }
//            stack.tag.setString("Potion", resourcelocation.toString());
//        }
//        return stack;
//    }
//
//    public static Ingredient getIngredient(){
//        ItemStack[] stacks = new ItemStack[buckets.size()];
//        return IngredientNBT.fromStacks(buckets.toArray(stacks));
//    }
//}
