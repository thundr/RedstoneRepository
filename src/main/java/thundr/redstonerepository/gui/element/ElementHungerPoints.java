package thundr.redstonerepository.gui.element;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.ElementBase;
import cofh.core.util.helpers.MathHelper;
import cofh.core.util.helpers.RenderHelper;
import cofh.core.util.helpers.StringHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thundr.redstonerepository.api.IHungerStorageItem;

import java.util.List;

public class ElementHungerPoints extends ElementBase {
	public static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation("redstonerepository:textures/gui/hunger.png");
	public static final int DEFAULT_SCALE = 56;

	protected IHungerStorageItem storage;
	protected ItemStack stack;

	public ElementHungerPoints(GuiContainerCore gui, int posX, int posY, ItemStack stack) {

		super(gui, posX, posY);
		this.storage = (IHungerStorageItem) stack.getItem();
		this.stack = stack;

		this.texture = DEFAULT_TEXTURE;
		this.sizeX = 9;
		this.sizeY = DEFAULT_SCALE;

		this.texW = 32;
		this.texH = 64;
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {
		int amount = getScaled();
		RenderHelper.bindTexture(texture);
		drawTexturedModalRect(posX, posY, 0, 0, sizeX, sizeY);
		drawTexturedModalRect(posX, posY + DEFAULT_SCALE - amount, 17, DEFAULT_SCALE - amount, sizeX, amount);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {

	}

	@Override
	public void addTooltip(List<String> list) {
		list.add(StringHelper.formatNumber(storage.getHungerPoints(stack)) + " / " +
				StringHelper.formatNumber(storage.getMaxHungerPoints(stack)) + " Hunger Points");
	}

	protected int getScaled() {

		if (storage.getMaxHungerPoints(stack) <= 0) {
			return sizeY;
		}
		long fraction = (long) storage.getHungerPoints(stack) * sizeY / storage.getMaxHungerPoints(stack);

		return storage.getHungerPoints(stack) > 0 ? Math.max(1, MathHelper.round(fraction)) : MathHelper.round(fraction);
	}
}
