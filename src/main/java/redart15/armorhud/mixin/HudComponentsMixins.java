package redart15.armorhud.mixin;

import net.minecraft.client.gui.hud.component.HudComponents;
import org.spongepowered.asm.mixin.Mixin;
import redart15.armorhud.ArmorHudClient;

@Mixin(HudComponents.class)
public abstract class HudComponentsMixins {


	static {
		ArmorHudClient.hudComponentInit();
	}
}
