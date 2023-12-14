package com.minenash.independent_gizmo.mixin;

import com.minenash.independent_gizmo.IndependentGizmo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InGameHud.class, priority = 1200)
public abstract class InGameHudMixin {

	@Shadow protected abstract void renderCrosshair(DrawContext context);

	boolean renderAttackIndicator = false;

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderCrosshair(Lnet/minecraft/client/gui/DrawContext;)V", shift = At.Shift.AFTER))
	private void renderAttackIndicatorForDebugScreen2(DrawContext context, float _tickDelta, CallbackInfo _info) {
		if (MinecraftClient.getInstance().options.getAttackIndicator().getValue() == AttackIndicator.CROSSHAIR) {
			renderAttackIndicator = true;
			renderCrosshair(context);
			renderAttackIndicator = false;
		}
	}

	@Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;shouldShowDebugHud()Z"))
	private boolean getDebugCrosshairEnable(DebugHud instance) {
		return !renderAttackIndicator && IndependentGizmo.debugCrosshairEnable;
	}

	@Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"))
	private void skipNormalCrosshairRendering(DrawContext context, Identifier texture, int x, int y, int width, int height) {
		if (!renderAttackIndicator)
			context.drawGuiTexture(texture, x, y, width, height);
	}

}
