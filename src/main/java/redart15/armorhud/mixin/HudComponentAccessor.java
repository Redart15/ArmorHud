package redart15.armorhud.mixin;

import net.minecraft.client.gui.hud.component.HudComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HudComponent.class)
public interface HudComponentAccessor {
	@Accessor
	String getKey();
}
