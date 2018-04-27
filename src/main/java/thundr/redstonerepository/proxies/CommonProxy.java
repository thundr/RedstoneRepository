package thundr.redstonerepository.proxies;

import cofh.core.render.IModelRegister;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import thundr.redstonerepository.util.ArmorEventHandler;
import thundr.redstonerepository.util.ToolEventHandler;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
    	registerEventHandlers();
    }

    public void initialize(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {

    }
    /* HELPERS */
    public boolean addIModelRegister(IModelRegister modelRegister) {
        return false;
    }

    private void registerEventHandlers(){
	    MinecraftForge.EVENT_BUS.register(new ArmorEventHandler());
	    MinecraftForge.EVENT_BUS.register(new ToolEventHandler());
    }
}
