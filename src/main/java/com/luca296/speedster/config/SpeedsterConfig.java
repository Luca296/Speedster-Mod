package com.luca296.speedster.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.luca296.speedster.Speedster;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Configuration for Speedster Mod.
 * Loaded from config/speedster.json
 */
public class SpeedsterConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("speedster.json");
    
    private static SpeedsterConfig INSTANCE;

    // === Speed Settings ===
    public float maxMomentum = 100.0f;
    public float momentumGainRate = 0.5f; // Per tick while sprinting
    public float momentumDecayRate = 1.0f; // Per tick when not sprinting
    public float maxSpeedMultiplier = 5.0f; // Max speed at full momentum

    // === Threshold Settings (as percentages) ===
    public float hydroplaneThreshold = 0.75f;
    public float wallRunThreshold = 0.50f;
    public float driftThreshold = 0.60f;
    public float heatGenerationThreshold = 0.90f;

    // === Charge Settings ===
    public float maxCharge = 100.0f;
    public float chargeGainRate = 0.1f; // Per tick while moving fast
    public float chainLightningChargeCost = 30.0f;
    public float aoeStunChargeCost = 50.0f;
    public int chainLightningTargets = 3;
    public float chainLightningRange = 5.0f;

    // === Heat Settings ===
    public float maxHeat = 100.0f;
    public float heatGainRate = 0.2f; // Per tick at max speed
    public float heatDecayRate = 0.1f; // Per tick natural cooling
    public float waterCoolingRate = 2.0f; // Per tick in water
    public float heatDamageThreshold = 0.75f; // Start smoke
    public float heatIgniteThreshold = 1.0f; // Catch fire
    public float armorlessDamageRate = 0.5f; // Damage per second without armor at high speed

    // === Cooldown Settings (in ticks) ===
    public int phaseShiftCooldown = 200; // 10 seconds
    public int timeDilationCooldown = 600; // 30 seconds
    public int aoeStunCooldown = 400; // 20 seconds

    // === Duration Settings (in ticks) ===
    public int phaseShiftDuration = 60; // 3 seconds
    public int timeDilationDuration = 100; // 5 seconds

    // === Combat Settings ===
    public float velocityDamageMultiplier = 0.5f; // Bonus damage per momentum point
    public float velocityKnockbackMultiplier = 0.1f; // Bonus knockback per momentum point
    public float maxBonusKnockback = 5.0f; // Max extra knockback blocks

    // === Visual Settings ===
    public boolean enableSpeedTrails = true;
    public boolean enableMotionBlur = true;
    public boolean enableVignette = true;
    public boolean enableSparkParticles = true;
    public float trailFadeTime = 0.3f; // Seconds
    public float speedVisionRange = 25.0f; // Blocks

    // === Keybind Defaults (handled by Fabric keybinding system) ===
    public String phaseShiftKey = "key.keyboard.v";
    public String timeDilationKey = "key.keyboard.g";
    public String aoeStunKey = "key.keyboard.r";
    public String toggleSpeedsterKey = "key.keyboard.h";

    public static SpeedsterConfig get() {
        if (INSTANCE == null) {
            load();
        }
        return INSTANCE;
    }

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                INSTANCE = GSON.fromJson(json, SpeedsterConfig.class);
                Speedster.LOGGER.info("Loaded Speedster config from file");
            } catch (IOException e) {
                Speedster.LOGGER.error("Failed to load config, using defaults", e);
                INSTANCE = new SpeedsterConfig();
                save();
            }
        } else {
            INSTANCE = new SpeedsterConfig();
            save();
            Speedster.LOGGER.info("Created default Speedster config");
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(INSTANCE));
        } catch (IOException e) {
            Speedster.LOGGER.error("Failed to save config", e);
        }
    }
}
