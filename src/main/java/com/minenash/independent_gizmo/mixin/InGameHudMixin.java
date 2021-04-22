package com.minenash.independent_gizmo.mixin;

import com.minenash.independent_gizmo.IndependentGizmo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.options.AttackIndicator;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.util.math.MatrixStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

	@Shadow protected abstract void renderCrosshair(MatrixStack matrices);

	boolean renderAttackIndicator = false;

	@Inject(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;pushMatrix()V"))
	private void renderAttackIndicatorForDebugScreen(MatrixStack stack, CallbackInfo _info) {
		if (MinecraftClient.getInstance().options.attackIndicator == AttackIndicator.CROSSHAIR) {
			renderAttackIndicator = true;
			renderCrosshair(stack);
			renderAttackIndicator = false;
		}
	}

	@Redirect(method = "renderCrosshair", at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;debugEnabled:Z", opcode = Opcodes.GETFIELD))
	private boolean getDebugCrosshairEnable(GameOptions options) {
		return !renderAttackIndicator && IndependentGizmo.debugCrosshairEnable;
	}

	@Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", ordinal = 0,target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"))
	private void skipNormalCrosshairRendering(InGameHud hud, MatrixStack stack, int x, int y, int u, int v, int width, int height) {
		if (!renderAttackIndicator)
			hud.drawTexture(stack, x, y, u, v, width, height);
	}

}
