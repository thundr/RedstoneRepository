package thundr.redstonerepository.gui;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.ElementButton;
import cofh.core.gui.element.ElementEnergyStored;
import cofh.core.gui.element.ElementFluidTank;
import cofh.core.gui.element.tab.TabInfo;
import net.minecraft.entity.player.InventoryPlayer;
import thundr.redstonerepository.gui.element.ElementEnergyItem;
import thundr.redstonerepository.gui.element.ElementHungerPoints;
import thundr.redstonerepository.init.RedstoneRepositoryProps;

public class GuiFeeder extends GuiContainerCore {

	public GuiFeeder(InventoryPlayer inventoryPlayer, ContainerFeeder containerFeeder){
		super(containerFeeder);

		name = containerFeeder.getInventoryName();

		xSize = 14 + 18 * 9;
		ySize = 112 + 18 * 2;
		texture = RedstoneRepositoryProps.FEEDER_GUI_STORAGE;

		ElementButton addFood;
		ElementEnergyItem energy;
		ElementHungerPoints hungerPoints;

		generateInfo("tab.redstonerepository.feeder");
	}

	@Override
	public void initGui() {
		super.initGui();
		if (!myInfo.isEmpty()) {
			addTab(new TabInfo(this, myInfo));
		}
	}
}
