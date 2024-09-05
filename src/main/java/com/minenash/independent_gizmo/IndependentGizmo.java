package com.minenash.independent_gizmo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
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
	public static long lastPressed = 0;
	public static boolean lastSavedState = false;

	public static final KeyBinding TOGGLE_GIZMO_CROSSHAIR = KeyBindingHelper.registerKeyBinding(
			new KeyBinding("key.independent.gizmo.gizmo_crosshair_toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_GRAVE_ACCENT, "key.categories.misc"));

	@Override
	public void onInitializeClient() {
		read();
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			long time = System.currentTimeMillis();
			while (TOGGLE_GIZMO_CROSSHAIR.wasPressed()) {
				debugCrosshairEnable = !debugCrosshairEnable;
				lastPressed = time;
			}
			if (time - lastPressed > 1000 && debugCrosshairEnable != lastSavedState)
				save();
		});
	}

	public static void save() {
		try {
			LOGGER.error("[IndependentGizmo] Saving");
			if (!Files.exists(CONFIG))
				Files.createFile(CONFIG);
			JsonObject o = new JsonObject();
			o.addProperty("debugCrosshairEnable", debugCrosshairEnable);
			Files.write(CONFIG, GSON.toJson(o).getBytes());
			lastSavedState = debugCrosshairEnable;
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
				lastSavedState = debugCrosshairEnable;
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
