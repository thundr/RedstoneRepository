package thundr.redstonerepository.init;

import cofh.core.util.core.IInitializer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thundr.redstonerepository.blocks.RABlockStorage;

import java.util.ArrayList;

public class RedstoneRepositoryBlocks{
    public static final RedstoneRepositoryBlocks INSTANCE = new RedstoneRepositoryBlocks();

    private RedstoneRepositoryBlocks() {

    }

    public static void preInit() {

        RABlockStorage = new RABlockStorage();

        initList.add(RABlockStorage);

        for (IInitializer init : initList) {
            init.initialize();
        }
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    /* EVENT HANDLING */
    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> event) {

        for (IInitializer init : initList) {
            init.register();
        }
    }

    static ArrayList<IInitializer> initList = new ArrayList<>();

    /* REFERENCES */
    public static RABlockStorage RABlockStorage;

}
