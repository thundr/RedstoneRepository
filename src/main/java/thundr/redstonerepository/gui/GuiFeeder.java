package thundr.redstonerepository.gui;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.ElementButton;
import cofh.core.gui.element.ElementEnergyStored;
import cofh.core.gui.element.ElementFluidTank;
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

		energy = new ElementEnergyItem(this, 150, 42, ((ContainerFeeder)inventorySlots).getContainerStack());
		hungerPoints = new ElementHungerPoints(this, 159, 43,  ((ContainerFeeder)inventorySlots).getContainerStack());

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
