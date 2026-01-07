package com.luca296.speedster.client;

import com.luca296.speedster.Speedster;
import com.luca296.speedster.network.SpeedsterClientNetworking;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Keybinding registration and handling for Speedster mod.
 */
public class SpeedsterKeybindings {

    public static final String CATEGORY = "category.speedster.abilities";

    public static KeyBinding PHASE_SHIFT;
    public static KeyBinding TIME_DILATION;
    public static KeyBinding AOE_STUN;
    public static KeyBinding TOGGLE_SPEEDSTER;

    public static void register() {
        Speedster.LOGGER.info("Registering Speedster keybindings...");

        PHASE_SHIFT = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.speedster.phase_shift",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            CATEGORY
        ));

        TIME_DILATION = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.speedster.time_dilation",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            CATEGORY
        ));

        AOE_STUN = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.speedster.aoe_stun",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            CATEGORY
        ));

        TOGGLE_SPEEDSTER = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.speedster.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            CATEGORY
        ));
    }

    public static void handleKeyPresses() {
        while (PHASE_SHIFT.wasPressed()) {
            SpeedsterClientNetworking.sendAbilityUse("phase_shift");
        }

        while (TIME_DILATION.wasPressed()) {
            SpeedsterClientNetworking.sendAbilityUse("time_dilation");
        }

        while (AOE_STUN.wasPressed()) {
            SpeedsterClientNetworking.sendAbilityUse("aoe_stun");
        }

        while (TOGGLE_SPEEDSTER.wasPressed()) {
            // Toggle is handled client-side and synced
            SpeedsterClientNetworking.sendToggleSpeedster(true); // Will be implemented properly
        }
    }
}
