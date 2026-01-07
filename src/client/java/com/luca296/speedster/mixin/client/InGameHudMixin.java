package com.luca296.speedster.mixin.client;

import com.luca296.speedster.component.SpeedsterComponents;
import com.luca296.speedster.config.SpeedsterConfig;
import com.luca296.speedster.data.SpeedsterData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Client-side mixin for HUD rendering (vignette effect at high speed).
 */
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "render", at = @At("TAIL"))
    private void speedster$renderSpeedVignette(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (client.player == null) return;
        
        SpeedsterData data = SpeedsterComponents.getData(client.player);
        SpeedsterConfig config = SpeedsterConfig.get();
        
        if (!data.isSpeedsterEnabled() || !config.enableVignette) return;

        float momentumPercent = data.getMomentumPercent();
        
        // Only show vignette at higher speeds
        if (momentumPercent < 0.6f) return;

        // Calculate vignette intensity
        float intensity = (momentumPercent - 0.6f) / 0.4f; // 0 to 1
        int alpha = (int) (intensity * 100); // Max 100 alpha
        
        // Yellow-orange tint color
        int color = (alpha << 24) | 0xFFAA00; // ARGB

        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        // Draw vignette gradient from edges
        int vignetteSize = 50;
        
        // Top edge
        context.fillGradient(0, 0, width, vignetteSize, color, 0x00000000);
        // Bottom edge  
        context.fillGradient(0, height - vignetteSize, width, height, 0x00000000, color);
        // Left edge
        context.fillGradient(0, 0, vignetteSize, height, color, 0x00000000);
        // Right edge
        context.fillGradient(width - vignetteSize, 0, width, height, 0x00000000, color);

        // Phase shift overlay (cyan tint)
        if (data.isPhaseShiftActive()) {
            int phaseAlpha = 40;
            int phaseColor = (phaseAlpha << 24) | 0x00FFFF;
            context.fill(0, 0, width, height, phaseColor);
        }

        // Time dilation overlay (yellow/slow-mo effect)
        if (data.isTimeDilationActive()) {
            int timeAlpha = 30;
            int timeColor = (timeAlpha << 24) | 0xFFFF88;
            context.fill(0, 0, width, height, timeColor);
        }

        // Heat warning overlay (red when overheating)
        if (data.isInDangerZone()) {
            float heatIntensity = (data.getHeatPercent() - 0.75f) / 0.25f;
            int heatAlpha = (int) (heatIntensity * 60);
            int heatColor = (heatAlpha << 24) | 0xFF0000;
            
            // Pulsing effect
            if ((client.player.age / 10) % 2 == 0) {
                context.fill(0, 0, width, height, heatColor);
            }
        }
    }
}
