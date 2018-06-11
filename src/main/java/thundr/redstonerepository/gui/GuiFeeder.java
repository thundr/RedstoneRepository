package thundr.redstonerepository.gui;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.ElementButton;
import cofh.core.gui.element.ElementButtonManaged;
import cofh.core.gui.element.tab.TabInfo;
import cofh.core.util.helpers.StringHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.lwjgl.input.Keyboard;
import thundr.redstonerepository.RedstoneRepository;
import thundr.redstonerepository.gui.element.ElementEnergyItem;
import thundr.redstonerepository.gui.element.ElementHungerPoints;
import thundr.redstonerepository.init.RedstoneRepositoryProps;
import thundr.redstonerepository.items.ItemFeeder;
import thundr.redstonerepository.util.HungerHelper;

public class GuiFeeder extends GuiContainerCore {

	ElementButton addFood;
	ElementEnergyItem energy;
	ElementHungerPoints hungerPoints;

	String PATH_BUTTON = RedstoneRepositoryProps.PATH_GUI + "storage_1.png";

	ItemStack feederStack;
	ItemFeeder baseFeeder;
	ContainerFeeder containerFeeder;

	public GuiFeeder(InventoryPlayer inventoryPlayer, ContainerFeeder containerFeeder){
		super(containerFeeder);

		name = containerFeeder.getInventoryName();

		xSize = 14 + 18 * 9;
		ySize = 112 + 18 * 2;
		texture = RedstoneRepositoryProps.FEEDER_GUI_STORAGE;
		this.feederStack = containerFeeder.getContainerStack();
		this.baseFeeder = (ItemFeeder) containerFeeder.getContainerStack().getItem();
		this.containerFeeder = containerFeeder;

		generateInfo("tab.redstonerepository.feeder");
	}

	@Override
	public void initGui() {
		super.initGui();
		if (!myInfo.isEmpty()) {
			addTab(new TabInfo(this, myInfo));
		}

		addFood = new ElementButton(this, 101, 26, "AddFood", 177, 64,
				177, 80, 177, 96, 16, 16, PATH_BUTTON);
		energy = new ElementEnergyItem(this, 151, 6, ((ContainerFeeder)inventorySlots).getContainerStack());
		hungerPoints = new ElementHungerPoints(this, 160, 6,  ((ContainerFeeder)inventorySlots).getContainerStack());

		addElement(addFood);
		addElement(energy);
		addElement(hungerPoints);

		Keyboard.enableRepeatEvents(true);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {

		if (drawTitle & name != null) {
			fontRenderer.drawString(StringHelper.localize(name), 6, 6, 0x404040);
		}
		if (drawInventory) {
			fontRenderer.drawString(StringHelper.localize("container.inventory"), 8, ySize - 96 + 3, 0x404040);
		}
		drawElements(0, true);
		drawTabs(0, true);
	}

	@Override
	public void handleElementButtonClick(String button, int mouseButton){
		ItemStack tmpStack = containerFeeder.inventorySlots.get(containerFeeder.inventorySlots.size() - 1).getStack().copy();

		if(mouseButton == 0){
			tmpStack.setCount(1);
			baseFeeder.receiveHungerPoints(feederStack, HungerHelper.findHungerValues(tmpStack));
		}
		else if(mouseButton == 1){
			baseFeeder.receiveHungerPoints(feederStack, HungerHelper.findHungerValues(tmpStack));
		}
		playClickSound(0.7F);
	}
}
