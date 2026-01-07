package com.luca296.speedster.client.handler;

import com.luca296.speedster.client.SpeedsterKeybindings;
import com.luca296.speedster.component.SpeedsterComponents;
import com.luca296.speedster.config.SpeedsterConfig;
import com.luca296.speedster.data.SpeedsterData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

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

        SpeedsterConfig config = SpeedsterConfig.get();
        
        // Client-side prediction for smooth visuals
        updateClientPrediction(player, data);

        // Client-side particles / feedback
        if (config.enableSpeedTrails && data.getMomentumPercent() > 0.5f && player.isSprinting()) {
            spawnSpeedParticles(player, data);
        }

        if (config.enableSparkParticles && data.isDrifting()) {
            spawnDriftParticles(player);
        }
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

    private static void spawnSpeedParticles(ClientPlayerEntity player, SpeedsterData data) {
        Vec3d velocity = player.getVelocity();
        double speed = velocity.horizontalLength();

        if (speed <= 0.3) return;

        for (int i = 0; i < (int) (data.getMomentumPercent() * 3); i++) {
            double offsetX = (Math.random() - 0.5) * 0.5;
            double offsetY = Math.random() * 0.5;
            double offsetZ = (Math.random() - 0.5) * 0.5;

            player.getWorld().addParticle(
                ParticleTypes.CLOUD,
                player.getX() - velocity.x * 0.5 + offsetX,
                player.getY() + offsetY,
                player.getZ() - velocity.z * 0.5 + offsetZ,
                -velocity.x * 0.1,
                0.02,
                -velocity.z * 0.1
            );
        }
    }

    private static void spawnDriftParticles(ClientPlayerEntity player) {
        for (int i = 0; i < 3; i++) {
            double offsetX = (Math.random() - 0.5) * 0.3;
            double offsetZ = (Math.random() - 0.5) * 0.3;

            player.getWorld().addParticle(
                ParticleTypes.ELECTRIC_SPARK,
                player.getX() + offsetX,
                player.getY() + 0.1,
                player.getZ() + offsetZ,
                (Math.random() - 0.5) * 0.2,
                0.05,
                (Math.random() - 0.5) * 0.2
            );
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
