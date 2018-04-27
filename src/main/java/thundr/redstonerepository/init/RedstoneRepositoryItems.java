package thundr.redstonerepository.init;

import cofh.core.util.core.IInitializer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thundr.redstonerepository.items.ItemMaterial;

import java.util.ArrayList;

public class RedstoneRepositoryItems {

    public static final RedstoneRepositoryItems INSTANCE = new RedstoneRepositoryItems();

    private RedstoneRepositoryItems() {

    }

    public static void preInit() {
        itemMaterial = new ItemMaterial();

        initList.add(itemMaterial);


        for (IInitializer init : initList) {
            init.preInit();
        }
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    /* EVENT HANDLING */
    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> event) {

        for (IInitializer init : initList) {
            init.initialize();
        }
    }

    static ArrayList<IInitializer> initList = new ArrayList<>();

    /* REFERENCES */
    public static ItemMaterial itemMaterial;


}
