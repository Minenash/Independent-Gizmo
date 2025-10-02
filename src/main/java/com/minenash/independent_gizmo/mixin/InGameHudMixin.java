package com.minenash.independent_gizmo.mixin;

import com.minenash.independent_gizmo.IndependentGizmo;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = InGameHud.class, priority = 1200)
public abstract class InGameHudMixin {

	@Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V"))
	private void skipNormalCrosshairRendering(DrawContext context, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height) {
		if (!IndependentGizmo.debugCrosshairEnable)
			context.drawGuiTexture(pipeline, sprite, x, y, width, height);
	}

	@Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;shouldRenderCrosshair()Z"))
	boolean alwaysRenderAttackIndicator(InGameHud instance) {
		return false;
	}

	@Redirect(method = "shouldRenderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;shouldShowDebugHud()Z"))
	private boolean showDebugCrossHairWhenToggled(DebugHud instance) {
		return IndependentGizmo.debugCrosshairEnable;
	}

}
