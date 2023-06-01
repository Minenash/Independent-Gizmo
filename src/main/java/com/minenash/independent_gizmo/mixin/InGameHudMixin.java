package com.minenash.independent_gizmo.mixin;

import com.minenash.independent_gizmo.IndependentGizmo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
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

	@Redirect(method = "renderCrosshair", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;debugEnabled:Z", opcode = Opcodes.GETFIELD))
	private boolean getDebugCrosshairEnable(GameOptions options) {
		return !renderAttackIndicator && IndependentGizmo.debugCrosshairEnable;
	}

	@Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V"))
	private void skipNormalCrosshairRendering(DrawContext context, Identifier texture, int x, int y, int u, int v, int width, int height) {
		if (!renderAttackIndicator)
			context.drawTexture(texture, x, y, u, v, width, height);
	}

}
