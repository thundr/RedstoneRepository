package thundr.redstonerepository.gui;

import cofh.core.gui.container.ContainerInventoryItem;
import cofh.core.gui.slot.ISlotValidator;
import cofh.core.gui.slot.SlotLocked;
import cofh.core.gui.slot.SlotValidated;
import cofh.core.util.helpers.StringHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;


public class ContainerFeeder extends ContainerInventoryItem implements ISlotValidator {

	static final String NAME = "item.redstonerepository.bauble.feeder.name";

	public ContainerFeeder(ItemStack stack, InventoryPlayer inventoryPlayer){
		super(stack, inventoryPlayer);
		bindPlayerInventory(inventoryPlayer);

		addSlotToContainer(new SlotValidated(this, containerWrapper, 0, 80, 26));

	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return stack.getItem() instanceof ItemFood;
	}


	@Override
	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {

		int xOffset = getPlayerInventoryHorizontalOffset();
		int yOffset = getPlayerInventoryVerticalOffset();

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, xOffset + j * 18, yOffset + i * 18));
			}
		}
		for (int i = 0; i < 9; i++) {
			if (i == inventoryPlayer.currentItem) {
				addSlotToContainer(new SlotLocked(inventoryPlayer, i, xOffset + i * 18, yOffset + 58));
			} else {
				addSlotToContainer(new Slot(inventoryPlayer, i, xOffset + i * 18, yOffset + 58));
			}
		}
	}

	@Override
	public String getInventoryName() {
		return containerWrapper.hasCustomName() ? containerWrapper.getName() : StringHelper.localize(NAME);
	}

	@Override
	protected int getPlayerInventoryVerticalOffset() {

		return 30 + 18 * 2;
	}

	@Override
	protected int getPlayerInventoryHorizontalOffset() {

		return 8;
	}

}
