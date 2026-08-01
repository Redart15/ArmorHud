package redart15.armorhud;

import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.hud.HudIngame;
import net.minecraft.client.gui.hud.component.HudComponentMovable;
import net.minecraft.client.gui.hud.component.layout.Layout;
import net.minecraft.client.gui.hud.component.layout.LayoutSnap;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.State;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.IArmorItem;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.LightIndexHelper;
import net.minecraft.core.util.helper.MathHelper;
import org.jetbrains.annotations.NotNull;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static redart15.armorhud.ArmorHudClient.MOD_ID;

@Environment(EnvType.CLIENT)
public class HudComponentArmorHud extends HudComponentMovable {
	private final @NotNull Random random = new Random();
	private static final IconCoordinate DEFAULT_ICON = TextureRegistry.getTexture(MOD_ID + ":gui/armorhud/default");
	private static final IconCoordinate UNKNOWN = TextureRegistry.getTexture(MOD_ID + ":gui/armorhud/unknown");
	private static final List<IconCoordinate> ICONS = new ArrayList<>();

	static {
		ICONS.add(TextureRegistry.getTexture(MOD_ID + ":gui/armorhud/helm"));
		ICONS.add(TextureRegistry.getTexture(MOD_ID + ":gui/armorhud/body"));
		ICONS.add(TextureRegistry.getTexture(MOD_ID + ":gui/armorhud/legs"));
		ICONS.add(TextureRegistry.getTexture(MOD_ID + ":gui/armorhud/boots"));
	}


	public HudComponentArmorHud(String key, LayoutSnap layout) {
		this(key, 50, 14, layout);
	}

	public HudComponentArmorHud(String key, int xSize, int ySize, Layout layout) {
		super(key, xSize, ySize, layout);
	}

	@Override
	public boolean isVisible() {
		return GameSettings.IMMERSIVE_MODE.drawHotbar();
	}

	@Override
	public void render(HudIngame hud, int xSizeScreen, int ySizeScreen, float partialTick) {
		int x = this.getLayout().getComponentX(this, xSizeScreen);
		int y = this.getLayout().getComponentY(this, ySizeScreen);
		ItemStack[] armorInventory = mc.thePlayer.inventory.armorInventory;
		this.renderItemList(hud, armorInventory, x, y);
	}

	@SuppressWarnings("UnusedReturnValue")
	private IntIntPair renderItemList(Gui hud, ItemStack[] armorInventory, int x, int y) {
		boolean isVertical = this.isVertical();
		int rows = isVertical ? 4 : (int) Math.ceil(armorInventory.length / 4.0f);
		int columns = isVertical ? (int) Math.ceil(armorInventory.length / 4.0f) : 4;
		GLRenderer.modelM4f().scaleAround(0.75f, x, y, hud.zLevel);
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				int i = isVertical ? row + column * 4 : row * 4 + column;
				ItemStack stack = armorInventory[i];
				if (stack == null) {
					continue;
				}
				int cx = x + 2 + 16 * column;
				int cy = y + 2 + 16 * row;
				this.renderItem(hud, stack, i, cx, cy);
			}
		}
		return new IntIntImmutablePair(x + 16 * columns, y + 18 * rows);
	}

	private static double getProgress(ItemStack stack) {
		if (stack.getMaxDamage() > 0.0F) {
			return MathHelper.lerp(0.0F, 255.0F, ((double) stack.getMaxDamage() - stack.getItemDamageForDisplay()) / stack.getMaxDamage());
		}
		return 255.0F;
	}

	private void renderItem(Gui hud, ItemStack stack, int i, int cx, int cy) {
		int color = Color.HSBtoRGB((float) getProgress(stack) / 255.0F / 3.0F, 1.0F, 1.0F);
		ArmorHudClient.ArmorHudStyle style = ArmorHudClient.STYLE_ARMORHUD.value;
		cy += this.adjustY(hud, stack, i);
		if (!style.equals(ArmorHudClient.ArmorHudStyle.TINT)) {
			GLRenderer.pushFrame();
			GLRenderer.enableState(State.DEPTH_TEST);
			GLRenderer.enableState(State.BLEND);
			Lighting.enableInventoryLight();
			GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			ItemModel itemModel = ItemModelDispatcher.getInstance().getDispatch(stack);
			itemModel.renderGui(GLRenderer.getTessellator(), null, stack, cx, cy, LightIndexHelper.lightIndex2i(15, 15), 1.0F);
			itemModel.renderItemOverlayIntoGUI(GLRenderer.getTessellator(), mc.font, mc.textureManager, stack, cx, cy + 2, null, 1.0F);
			GLRenderer.disableState(State.DEPTH_TEST);
			GLRenderer.disableState(State.BLEND);
			Lighting.disable();
			GLRenderer.popFrame();
			return;
		}
		GLRenderer.pushFrame();
		IconCoordinate icon = this.getNextIcon(i, stack);
		GLRenderer.setColor1i(color);
		hud.drawGuiIcon(cx, cy, 16, 16, icon);
		GLRenderer.popFrame();

	}

	private @NotNull IconCoordinate getNextIcon(int i, ItemStack stack) {
		if (stack.getItem() instanceof IArmorItem<?> iArmorItem) {
			if (iArmorItem.getArmorMaterial() == null) {
				return DEFAULT_ICON; // useful to know durability but not the icon
			}
			return ICONS.get(i % ICONS.size());
		}
		return UNKNOWN; // something went wrong
	}

	private int adjustY(Gui gui, ItemStack stack, int i) {
		if (gui instanceof HudIngame hud && stack.isItemStackDamageable()) {
			boolean inAcidWarning = mc.thePlayer != null && mc.thePlayer.shouldShowAcidVisualEffects() && mc.thePlayer.getGamemode().hasToolDurability();
			double maxDamage = stack.getMaxDamage();
			double currentDurability = (double) stack.getMaxDamage() - stack.getMetadata();
			if (maxDamage > 0.0F && (currentDurability / maxDamage <= 0.15 || inAcidWarning)) {
				this.random.setSeed(hud.updateCounter * 312871L + i * 2654435769L);
				return this.random.nextInt(2);
			}
		}
		return 0;
	}

	@Override
	public void renderPreview(Gui gui, Layout layout, int xSizeScreen, int ySizeScreen) {
		int x = layout.getComponentX(this, xSizeScreen);
		int y = layout.getComponentY(this, ySizeScreen);
		ItemStack[] armorInventory = {
			Blocks.GRASS.getDefaultStack(),
			new ItemStack(Items.ARMOR_CHESTPLATE_DIAMOND, 1, (int) Math.ceil(Items.ARMOR_CHESTPLATE_DIAMOND.getMaxDamage() * 0.95F)),
			new ItemStack(Items.ARMOR_LEGGINGS_GOLD, 1, (int) Math.ceil(Items.ARMOR_LEGGINGS_GOLD.getMaxDamage() * 0.5F)),
			new ItemStack(Items.ARMOR_BOOTS_CHAINMAIL, 1, (int) Math.ceil(Items.ARMOR_BOOTS_CHAINMAIL.getMaxDamage() * 0.75F)),
		};
		this.renderItemList(gui, armorInventory, x, y);
	}


	private boolean isVertical() {
		return ArmorHudClient.VERTICAL_ARMORHUD.value;
	}

	@Override
	public int getDisplayedXSize() {
		return this.isVertical() ? super.getDisplayedYSize() : super.getDisplayedXSize();
	}

	@Override
	public int getDisplayedYSize() {
		return this.isVertical() ? super.getDisplayedXSize() : super.getDisplayedYSize();
	}


	@Override
	public int getTrueXSize() {
		return this.isVertical() ? super.getTrueYSize() : super.getTrueXSize();
	}

	@Override
	public int getTrueYSize() {
		return this.isVertical() ? super.getTrueXSize() : super.getTrueYSize();
	}

}

