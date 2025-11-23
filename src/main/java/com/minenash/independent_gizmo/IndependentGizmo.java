package com.minenash.independent_gizmo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.debug.DebugHudEntries;
import net.minecraft.client.gui.hud.debug.DebugHudEntryVisibility;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.nio.file.Files;
import java.nio.file.Path;

public class IndependentGizmo implements ClientModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("IndependentGizmo");
	public static final Path CONFIG = FabricLoader.getInstance().getConfigDir().resolve("independent_gizmo.json");
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static boolean debugCrosshairEnable = false;
	public static float debugCrosshairScale = 1f;

	public static boolean stateDirty = false;
	public static long lastSaved = 0;

	public static final KeyBinding.Category keyCategory = new KeyBinding.Category(Identifier.of("key.categories.independent_gizmo"));

	public static final KeyBinding TOGGLE_GIZMO_CROSSHAIR = KeyBindingHelper.registerKeyBinding(
			new KeyBinding("key.independent.gizmo.gizmo_crosshair_toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_GRAVE_ACCENT, keyCategory));
	public static final KeyBinding GIZMO_SIZE_UP = KeyBindingHelper.registerKeyBinding(
			new KeyBinding("key.independent.gizmo.gizmo_size_up", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_ADD, keyCategory));
	public static final KeyBinding GIZMO_SIZE_DOWN = KeyBindingHelper.registerKeyBinding(
			new KeyBinding("key.independent.gizmo.gizmo_size_down", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_SUBTRACT, keyCategory));

	@Override
	public void onInitializeClient() {
		read();
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if(TOGGLE_GIZMO_CROSSHAIR.wasPressed()) {
				debugCrosshairEnable= MinecraftClient.getInstance().debugHudEntryList.toggleVisibility(DebugHudEntries.THREE_DIMENSIONAL_CROSSHAIR);
			}

			if(GIZMO_SIZE_UP.isPressed()){
				debugCrosshairScale *=1.015625f;
				stateDirty=true;
			}

			if(GIZMO_SIZE_DOWN.isPressed()){
				debugCrosshairScale *= 0.984375f;
				stateDirty=true;
			}

			saveIfDirty();
		});
	}

	public static void saveIfDirty(){
		if(!stateDirty)
			return;

		long time = System.currentTimeMillis();

		if(time-lastSaved < 1000)
			return;

		lastSaved=time;
		save();
	}

	public static void save() {
		try {
			LOGGER.error("[IndependentGizmo] Saving");
			if (!Files.exists(CONFIG))
				Files.createFile(CONFIG);
			JsonObject o = new JsonObject();
			o.addProperty("debugCrosshairEnable", debugCrosshairEnable);
			o.addProperty("debugCrosshairScale", debugCrosshairScale);
			Files.write(CONFIG, GSON.toJson(o).getBytes());
			stateDirty = false;
		}
		catch (Exception e) {
			LOGGER.error("[IndependentGizmo] Failed to save config");
			LOGGER.catching(e);
		}

	}
	public static void read() {
		try {
			if (!Files.exists(CONFIG)) {
				save();
				return;
			}
			try {
				JsonObject o = GSON.fromJson(Files.newBufferedReader(CONFIG), JsonObject.class);
				debugCrosshairEnable = o.get("debugCrosshairEnable").getAsBoolean();
				debugCrosshairScale = o.get("debugCrosshairScale").getAsFloat();

				DebugHudEntryVisibility visibility = debugCrosshairEnable? DebugHudEntryVisibility.ALWAYS_ON : DebugHudEntryVisibility.NEVER;
				MinecraftClient.getInstance().debugHudEntryList.setEntryVisibility(DebugHudEntries.THREE_DIMENSIONAL_CROSSHAIR, visibility);

				stateDirty = false;
			}
			catch (JsonSyntaxException | NullPointerException e) {
				LOGGER.error("[CustomHud] Couldn't read the config, recreating");
				save();
			}
		}
		catch (Exception e) {
			LOGGER.error("[IndependentGizmo] Failed to load config");
			LOGGER.catching(e);
		}
	}
}
