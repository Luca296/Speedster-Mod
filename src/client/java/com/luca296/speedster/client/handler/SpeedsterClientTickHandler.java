package com.luca296.speedster.client.handler;

import com.luca296.speedster.client.SpeedsterKeybindings;
import com.luca296.speedster.component.SpeedsterComponents;
import com.luca296.speedster.data.SpeedsterData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * Client-side tick handler for Speedster mod.
 * Handles momentum updates, visual effects, and keybind processing.
 */
public class SpeedsterClientTickHandler {

    public static void onClientTick(MinecraftClient client) {
        if (client.player == null || client.world == null) return;
        
        ClientPlayerEntity player = client.player;
        
        // Handle keybind presses
        SpeedsterKeybindings.handleKeyPresses();
        
        // Get speedster data
        SpeedsterData data = SpeedsterComponents.getData(player);
        
        if (!data.isSpeedsterEnabled()) return;
        
        // Client-side prediction for smooth visuals
        updateClientPrediction(player, data);
    }

    private static void updateClientPrediction(ClientPlayerEntity player, SpeedsterData data) {
        // Update movement states based on current player state
        boolean isSprinting = player.isSprinting();
        boolean isOnGround = player.isOnGround();
        
        // Check for wall collision (simplified client-side check)
        boolean nearWall = isNearWall(player);
        
        // Check for water/lava
        boolean onLiquid = player.isTouchingWater() || player.isInLava();
        
        // Update states for rendering
        if (data.canWallRun() && nearWall && !isOnGround) {
            data.setWallRunning(true);
        } else {
            data.setWallRunning(false);
        }
        
        if (data.canHydroplane() && onLiquid && isSprinting) {
            data.setHydroplaning(true);
        } else {
            data.setHydroplaning(false);
        }
        
        // Check for drift (sharp turn while fast)
        if (data.getMomentumPercent() >= SpeedsterData.DRIFT_THRESHOLD && isOnGround && isSprinting) {
            // Calculate turn angle based on velocity change
            double velocityAngle = Math.atan2(player.getVelocity().z, player.getVelocity().x);
            double lookAngle = Math.toRadians(player.getYaw());
            double angleDiff = Math.abs(normalizeAngle(velocityAngle - lookAngle));
            
            data.setDrifting(angleDiff > Math.PI / 4); // 45+ degree turn
        } else {
            data.setDrifting(false);
        }
    }

    private static boolean isNearWall(ClientPlayerEntity player) {
        // Check for blocks in cardinal directions
        double checkDist = 0.6;
        var world = player.getWorld();
        var pos = player.getPos();
        
        return !world.getBlockState(player.getBlockPos().north()).isAir() ||
               !world.getBlockState(player.getBlockPos().south()).isAir() ||
               !world.getBlockState(player.getBlockPos().east()).isAir() ||
               !world.getBlockState(player.getBlockPos().west()).isAir();
    }

    private static double normalizeAngle(double angle) {
        while (angle > Math.PI) angle -= 2 * Math.PI;
        while (angle < -Math.PI) angle += 2 * Math.PI;
        return angle;
    }
}
