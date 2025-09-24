package com.minenash.independent_gizmo.mixin;

import com.minenash.independent_gizmo.IndependentGizmo;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = InGameHud.class, priority = 1200)
public abstract class InGameHudMixin {

	@Shadow protected abstract void renderCrosshair(DrawContext context, RenderTickCounter tickCounter);

	@Unique boolean renderAttackIndicator = false;

	@Inject(method = "renderCrosshair", at = @At(value = "HEAD"))
	private void renderAttackIndicatorForDebugScreen2(DrawContext context, RenderTickCounter tickCounter, CallbackInfo _info) {
		if (MinecraftClient.getInstance().options.getAttackIndicator().getValue() == AttackIndicator.CROSSHAIR && !renderAttackIndicator) {
			renderAttackIndicator = true;
			renderCrosshair(context, tickCounter);
			renderAttackIndicator = false;
		}
	}

	@Inject(method = "shouldRenderCrosshair", at = @At(value ="RETURN"),cancellable = true)
	public void getDebugCrosshairEnable(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(!renderAttackIndicator && IndependentGizmo.debugCrosshairEnable);
	}


	@Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V"))
	private void skipNormalCrosshairRendering(DrawContext instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height) {
		if (!renderAttackIndicator)
			instance.drawGuiTexture(pipeline,sprite, x, y, width, height);
	}

}
