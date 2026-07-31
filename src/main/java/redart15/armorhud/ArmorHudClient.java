package redart15.armorhud;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.hud.component.ComponentAnchor;
import net.minecraft.client.gui.hud.component.HudComponent;
import net.minecraft.client.gui.hud.component.HudComponents;
import net.minecraft.client.gui.hud.component.layout.LayoutSnap;
import net.minecraft.client.gui.options.components.BooleanOptionComponent;
import net.minecraft.client.gui.options.components.ToggleableOptionComponent;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.option.OptionBoolean;
import net.minecraft.client.option.OptionEnum;
import net.minecraft.core.util.helper.ITranslatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.HalpLibe;

import static net.minecraft.client.gui.hud.component.HudComponents.HOTBAR;

public class ArmorHudClient implements ClientModInitializer {
	public static final String MOD_ID = HalpLibe.registerMod("armorhud", true);
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static HudComponent ArmorHud;
	protected static OptionEnum<ArmorHudStyle> STYLE_ARMORHUD = GameSettings.register(new OptionEnum<>("armorHudStyle", ArmorHudStyle.class, ArmorHudStyle.TINT).setIsSlider(true));
	protected static OptionBoolean VERTICAL_ARMORHUD = GameSettings.register(new OptionBoolean("verticalArmorHud", false));

	@Override
	public void onInitializeClient() {

	}

	public static void hudComponentInit(){
		ArmorHud = HudComponents.register(
			new HudComponentArmorHud("armorhud",
				new LayoutSnap(HOTBAR, ComponentAnchor.TOP_RIGHT, ComponentAnchor.BOTTOM_RIGHT)
			)
				.addAttachedOption(ArmorHudClient.VERTICAL_ARMORHUD, () -> new BooleanOptionComponent(ArmorHudClient.VERTICAL_ARMORHUD))
				.addAttachedOption(ArmorHudClient.STYLE_ARMORHUD, () -> new ToggleableOptionComponent<>(ArmorHudClient.STYLE_ARMORHUD))
		);
	}

	public enum ArmorHudStyle implements ITranslatable {
		TINT, BAR;

		@Override
		public String getTranslationKey() {
			return this.name().toLowerCase();
		}
	}


}
