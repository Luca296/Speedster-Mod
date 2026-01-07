package com.luca296.speedster.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * Mod Menu integration for Speedster config screen.
 */
public class SpeedsterModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::createConfigScreen;
    }

    private Screen createConfigScreen(Screen parent) {
        SpeedsterConfig config = SpeedsterConfig.get();
        
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Text.translatable("config.speedster.title"))
            .setSavingRunnable(SpeedsterConfig::save);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Speed Settings Category
        ConfigCategory speedCategory = builder.getOrCreateCategory(Text.translatable("config.speedster.category.speed"));
        
        speedCategory.addEntry(entryBuilder.startFloatField(Text.translatable("config.speedster.maxMomentum"), config.maxMomentum)
            .setDefaultValue(100.0f)
            .setMin(10.0f)
            .setMax(500.0f)
            .setTooltip(Text.translatable("config.speedster.maxMomentum.tooltip"))
            .setSaveConsumer(val -> config.maxMomentum = val)
            .build());

        speedCategory.addEntry(entryBuilder.startFloatField(Text.translatable("config.speedster.momentumGainRate"), config.momentumGainRate)
            .setDefaultValue(0.5f)
            .setMin(0.1f)
            .setMax(5.0f)
            .setSaveConsumer(val -> config.momentumGainRate = val)
            .build());

        speedCategory.addEntry(entryBuilder.startFloatField(Text.translatable("config.speedster.maxSpeedMultiplier"), config.maxSpeedMultiplier)
            .setDefaultValue(5.0f)
            .setMin(1.0f)
            .setMax(20.0f)
            .setSaveConsumer(val -> config.maxSpeedMultiplier = val)
            .build());

        // Threshold Settings Category
        ConfigCategory thresholdCategory = builder.getOrCreateCategory(Text.translatable("config.speedster.category.thresholds"));

        thresholdCategory.addEntry(entryBuilder.startFloatField(Text.translatable("config.speedster.hydroplaneThreshold"), config.hydroplaneThreshold)
            .setDefaultValue(0.75f)
            .setMin(0.1f)
            .setMax(1.0f)
            .setSaveConsumer(val -> config.hydroplaneThreshold = val)
            .build());

        thresholdCategory.addEntry(entryBuilder.startFloatField(Text.translatable("config.speedster.wallRunThreshold"), config.wallRunThreshold)
            .setDefaultValue(0.50f)
            .setMin(0.1f)
            .setMax(1.0f)
            .setSaveConsumer(val -> config.wallRunThreshold = val)
            .build());

        // Charge Settings Category
        ConfigCategory chargeCategory = builder.getOrCreateCategory(Text.translatable("config.speedster.category.charge"));

        chargeCategory.addEntry(entryBuilder.startFloatField(Text.translatable("config.speedster.maxCharge"), config.maxCharge)
            .setDefaultValue(100.0f)
            .setMin(10.0f)
            .setMax(500.0f)
            .setSaveConsumer(val -> config.maxCharge = val)
            .build());

        chargeCategory.addEntry(entryBuilder.startIntField(Text.translatable("config.speedster.chainLightningTargets"), config.chainLightningTargets)
            .setDefaultValue(3)
            .setMin(1)
            .setMax(10)
            .setSaveConsumer(val -> config.chainLightningTargets = val)
            .build());

        // Heat Settings Category
        ConfigCategory heatCategory = builder.getOrCreateCategory(Text.translatable("config.speedster.category.heat"));

        heatCategory.addEntry(entryBuilder.startFloatField(Text.translatable("config.speedster.maxHeat"), config.maxHeat)
            .setDefaultValue(100.0f)
            .setMin(10.0f)
            .setMax(500.0f)
            .setSaveConsumer(val -> config.maxHeat = val)
            .build());

        heatCategory.addEntry(entryBuilder.startFloatField(Text.translatable("config.speedster.waterCoolingRate"), config.waterCoolingRate)
            .setDefaultValue(2.0f)
            .setMin(0.1f)
            .setMax(10.0f)
            .setSaveConsumer(val -> config.waterCoolingRate = val)
            .build());

        // Cooldown Settings Category
        ConfigCategory cooldownCategory = builder.getOrCreateCategory(Text.translatable("config.speedster.category.cooldowns"));

        cooldownCategory.addEntry(entryBuilder.startIntField(Text.translatable("config.speedster.phaseShiftCooldown"), config.phaseShiftCooldown)
            .setDefaultValue(200)
            .setMin(20)
            .setMax(1200)
            .setTooltip(Text.translatable("config.speedster.cooldown.tooltip"))
            .setSaveConsumer(val -> config.phaseShiftCooldown = val)
            .build());

        cooldownCategory.addEntry(entryBuilder.startIntField(Text.translatable("config.speedster.timeDilationCooldown"), config.timeDilationCooldown)
            .setDefaultValue(600)
            .setMin(20)
            .setMax(2400)
            .setSaveConsumer(val -> config.timeDilationCooldown = val)
            .build());

        cooldownCategory.addEntry(entryBuilder.startIntField(Text.translatable("config.speedster.aoeStunCooldown"), config.aoeStunCooldown)
            .setDefaultValue(400)
            .setMin(20)
            .setMax(1200)
            .setSaveConsumer(val -> config.aoeStunCooldown = val)
            .build());

        // Visual Settings Category
        ConfigCategory visualCategory = builder.getOrCreateCategory(Text.translatable("config.speedster.category.visual"));

        visualCategory.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.speedster.enableSpeedTrails"), config.enableSpeedTrails)
            .setDefaultValue(true)
            .setSaveConsumer(val -> config.enableSpeedTrails = val)
            .build());

        visualCategory.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.speedster.enableMotionBlur"), config.enableMotionBlur)
            .setDefaultValue(true)
            .setSaveConsumer(val -> config.enableMotionBlur = val)
            .build());

        visualCategory.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.speedster.enableVignette"), config.enableVignette)
            .setDefaultValue(true)
            .setSaveConsumer(val -> config.enableVignette = val)
            .build());

        visualCategory.addEntry(entryBuilder.startFloatField(Text.translatable("config.speedster.speedVisionRange"), config.speedVisionRange)
            .setDefaultValue(25.0f)
            .setMin(5.0f)
            .setMax(100.0f)
            .setSaveConsumer(val -> config.speedVisionRange = val)
            .build());

        return builder.build();
    }
}
