package thundr.redstonerepository.gui;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.ElementButton;
import cofh.core.gui.element.ElementButtonManaged;
import cofh.core.gui.element.tab.TabInfo;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.input.Keyboard;
import thundr.redstonerepository.gui.element.ElementEnergyItem;
import thundr.redstonerepository.gui.element.ElementHungerPoints;
import thundr.redstonerepository.init.RedstoneRepositoryProps;

public class GuiFeeder extends GuiContainerCore {

	ElementButton addFood;
	ElementEnergyItem energy;
	ElementHungerPoints hungerPoints;

	String PATH_BUTTON = RedstoneRepositoryProps.PATH_GUI + "storage_1.png";

	public GuiFeeder(InventoryPlayer inventoryPlayer, ContainerFeeder containerFeeder){
		super(containerFeeder);

		name = containerFeeder.getInventoryName();

		xSize = 14 + 18 * 9;
		ySize = 112 + 18 * 2;
		texture = RedstoneRepositoryProps.FEEDER_GUI_STORAGE;

		generateInfo("tab.redstonerepository.feeder");
	}

	@Override
	public void initGui() {
		super.initGui();
		if (!myInfo.isEmpty()) {
			addTab(new TabInfo(this, myInfo));
		}

		addFood = new ElementButton(this, 109, 42, "AddFood", 177, 64,
				177, 84, 177, 104, 38, 20, PATH_BUTTON);
		energy = new ElementEnergyItem(this, 151, 6, ((ContainerFeeder)inventorySlots).getContainerStack());
		hungerPoints = new ElementHungerPoints(this, 160, 6,  ((ContainerFeeder)inventorySlots).getContainerStack());

		addElement(addFood);
		addElement(energy);
		addElement(hungerPoints);

		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void handleElementButtonClick(String button, int mouseButton){
		if(mouseButton == 0){
			//eat one food item from slot
		}
		else if(mouseButton == 1){
			//eat all food in slot
		}

		playClickSound(0.7F);
	}

}
