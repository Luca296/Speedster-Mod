package com.luca296.speedster.client.render;

import com.luca296.speedster.component.SpeedsterComponents;
import com.luca296.speedster.config.SpeedsterConfig;
import com.luca296.speedster.data.SpeedsterData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

/**
 * HUD renderer for Speedster meters (momentum, charge, heat).
 */
public class SpeedsterHudRenderer {

    private static final int BAR_WIDTH = 100;
    private static final int BAR_HEIGHT = 8;
    private static final int PADDING = 5;
    private static final int MARGIN = 10;

    // Colors
    private static final int MOMENTUM_COLOR = 0xFF00AAFF; // Blue
    private static final int CHARGE_COLOR = 0xFFFFFF00; // Yellow
    private static final int HEAT_COLOR_SAFE = 0xFFFF8800; // Orange
    private static final int HEAT_COLOR_DANGER = 0xFFFF0000; // Red
    private static final int BACKGROUND_COLOR = 0x80000000; // Semi-transparent black
    private static final int BORDER_COLOR = 0xFF444444; // Dark gray

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        SpeedsterData data = SpeedsterComponents.getData(client.player);
        
        if (!data.isSpeedsterEnabled()) return;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        // Position bars in bottom-left corner, above hotbar
        int x = MARGIN;
        int y = screenHeight - 60 - (BAR_HEIGHT + PADDING) * 3;

        // Draw momentum bar
        drawBar(context, x, y, "Speed", data.getMomentumPercent(), MOMENTUM_COLOR);
        y += BAR_HEIGHT + PADDING;

        // Draw charge bar
        drawBar(context, x, y, "Charge", data.getChargePercent(), CHARGE_COLOR);
        y += BAR_HEIGHT + PADDING;

        // Draw heat bar
        int heatColor = data.isInDangerZone() ? HEAT_COLOR_DANGER : HEAT_COLOR_SAFE;
        drawBar(context, x, y, "Heat", data.getHeatPercent(), heatColor);

        // Draw ability cooldowns
        drawCooldowns(context, screenWidth, screenHeight, data);

        // Draw active ability indicators
        drawActiveAbilities(context, screenWidth, screenHeight, data);
    }

    private static void drawBar(DrawContext context, int x, int y, String label, float percent, int color) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        
        // Draw label
        context.drawText(textRenderer, label, x, y - 10, 0xFFFFFFFF, true);

        // Draw background
        context.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, BACKGROUND_COLOR);

        // Draw filled portion
        int filledWidth = (int) (BAR_WIDTH * Math.min(1.0f, percent));
        context.fill(x, y, x + filledWidth, y + BAR_HEIGHT, color);

        // Draw border
        context.drawBorder(x, y, BAR_WIDTH, BAR_HEIGHT, BORDER_COLOR);

        // Draw percentage text
        String percentText = String.format("%.0f%%", percent * 100);
        int textX = x + BAR_WIDTH + 5;
        context.drawText(textRenderer, percentText, textX, y, 0xFFFFFFFF, true);
    }

    private static void drawCooldowns(DrawContext context, int screenWidth, int screenHeight, SpeedsterData data) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        
        int x = screenWidth - 120;
        int y = screenHeight - 80;

        // Phase Shift cooldown
        if (data.getPhaseShiftCooldown() > 0) {
            String text = "Phase: " + formatTicks(data.getPhaseShiftCooldown());
            context.drawText(textRenderer, text, x, y, 0xFFAAAAAA, true);
            y += 12;
        }

        // Time Dilation cooldown
        if (data.getTimeDilationCooldown() > 0) {
            String text = "Time: " + formatTicks(data.getTimeDilationCooldown());
            context.drawText(textRenderer, text, x, y, 0xFFAAAAAA, true);
            y += 12;
        }

        // AoE Stun cooldown
        if (data.getAoeStunCooldown() > 0) {
            String text = "Stun: " + formatTicks(data.getAoeStunCooldown());
            context.drawText(textRenderer, text, x, y, 0xFFAAAAAA, true);
        }
    }

    private static void drawActiveAbilities(DrawContext context, int screenWidth, int screenHeight, SpeedsterData data) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        
        int x = screenWidth / 2 - 50;
        int y = screenHeight / 2 + 30;

        if (data.isPhaseShiftActive()) {
            String text = "PHASING - " + formatTicks(data.getPhaseShiftDuration());
            context.drawText(textRenderer, text, x, y, 0xFF00FFFF, true);
            y += 12;
        }

        if (data.isTimeDilationActive()) {
            String text = "TIME DILATION - " + formatTicks(data.getTimeDilationDuration());
            context.drawText(textRenderer, text, x, y, 0xFFFFFF00, true);
        }

        // Movement state indicators
        y = screenHeight / 2 + 60;
        if (data.isWallRunning()) {
            context.drawText(textRenderer, "WALL RUNNING", x, y, 0xFF00FF00, true);
            y += 12;
        }
        if (data.isHydroplaning()) {
            context.drawText(textRenderer, "HYDROPLANING", x, y, 0xFF0088FF, true);
            y += 12;
        }
        if (data.isDrifting()) {
            context.drawText(textRenderer, "DRIFTING", x, y, 0xFFFF8800, true);
        }
    }

    private static String formatTicks(int ticks) {
        float seconds = ticks / 20.0f;
        return String.format("%.1fs", seconds);
    }
}
