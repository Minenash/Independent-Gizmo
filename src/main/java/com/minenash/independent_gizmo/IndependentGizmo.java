package com.minenash.independent_gizmo;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class IndependentGizmo implements ClientModInitializer {
	public static boolean debugCrosshairEnable = false;

	private static final KeyBinding TOGGLE_GIZMO_CROSSHAIR = KeyBindingHelper.registerKeyBinding(
			new KeyBinding("key.independent.gizmo.gizmo_crosshair_toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_GRAVE_ACCENT, "key.categories.misc"));

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (TOGGLE_GIZMO_CROSSHAIR.wasPressed())
				debugCrosshairEnable = !debugCrosshairEnable;
		});
	}
}
