package com.minenash.independent_gizmo.mixin;

import com.minenash.independent_gizmo.IndependentGizmo;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = DebugHud.class, priority = 1200)
public abstract class DebugHudMixin {

	@ModifyVariable(method = "renderDebugCrosshair", at = @At("STORE"),ordinal = 0)
	private float injected(float f) {
		return f*IndependentGizmo.debugCrosshairScale;
	}

}
