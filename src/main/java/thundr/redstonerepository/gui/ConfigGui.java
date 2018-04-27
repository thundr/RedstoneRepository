package thundr.redstonerepository.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import thundr.redstonerepository.RedstoneRepository;

import java.util.ArrayList;
import java.util.List;

public class ConfigGui extends GuiConfig {

    public ConfigGui(GuiScreen parentScreen) {
    	super(parentScreen, getConfigElements(parentScreen), RedstoneRepository.ID, false, false, GuiConfig.getAbridgedConfigPath(RedstoneRepository.CONFIG.getConfiguration().toString()));
    }

	private static List<IConfigElement> getConfigElements(GuiScreen parent) {

		List<IConfigElement> list = new ArrayList<IConfigElement>();

		for(String section : RedstoneRepository.CONFIG.getCategoryNames()){
			final ConfigCategory category = RedstoneRepository.CONFIG.getCategory(section);
			if(!category.isChild()){
				list.add(new ConfigElement(category));
			}

		}

		return list;
	}
}