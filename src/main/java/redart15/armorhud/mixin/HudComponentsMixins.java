package redart15.armorhud.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.component.HudComponents;
import org.spongepowered.asm.mixin.Mixin;
import redart15.armorhud.ArmorHudClient;

@Environment(EnvType.CLIENT)
@Mixin(HudComponents.class)
public abstract class HudComponentsMixins {


	static {
		ArmorHudClient.hudComponentInit();
	}
}
