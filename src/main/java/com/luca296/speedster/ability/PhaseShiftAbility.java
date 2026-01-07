package com.luca296.speedster.ability;

import com.luca296.speedster.Speedster;
import com.luca296.speedster.config.SpeedsterConfig;
import com.luca296.speedster.data.SpeedsterData;
import com.luca296.speedster.registry.SpeedsterSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;

/**
 * Phase Shift ability - allows passing through solid blocks temporarily.
 */
public class PhaseShiftAbility {

    public static void activate(PlayerEntity player, SpeedsterData data) {
        if (!data.canUsePhaseShift()) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                int remaining = data.getPhaseShiftCooldown() / 20;
                serverPlayer.sendMessage(Text.literal("§cPhase Shift on cooldown! " + remaining + "s remaining"), true);
            }
            return;
        }

        // Check if player has enough momentum (50% threshold)
        if (data.getMomentumPercent() < 0.5f) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.sendMessage(Text.literal("§cNot enough momentum for Phase Shift!"), true);
            }
            return;
        }

        // Activate the ability
        data.activatePhaseShift();
        
        // Consume some momentum
        data.addMomentum(-20);

        // Play sound
        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
            SpeedsterSounds.PHASE_SHIFT, SoundCategory.PLAYERS, 1.0f, 1.0f);

        if (player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.sendMessage(Text.literal("§bPhase Shift activated!"), true);
        }

        Speedster.LOGGER.debug("Phase Shift activated for player: {}", player.getName().getString());
    }

    public static void deactivate(PlayerEntity player, SpeedsterData data) {
        data.deactivatePhaseShift();
        
        // Safety check - push player out of blocks if they end up inside one
        if (player.isInsideWall()) {
            // Try to find safe position
            pushPlayerToSafety(player);
        }

        if (player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.sendMessage(Text.literal("§7Phase Shift ended"), true);
        }
    }

    private static void pushPlayerToSafety(PlayerEntity player) {
        // Try moving up first
        for (int y = 0; y <= 3; y++) {
            var testPos = player.getBlockPos().up(y);
            if (player.getWorld().getBlockState(testPos).isAir() && 
                player.getWorld().getBlockState(testPos.up()).isAir()) {
                player.setPosition(player.getX(), player.getY() + y, player.getZ());
                return;
            }
        }
        
        // If no safe spot above, try horizontally
        double[][] offsets = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (double[] offset : offsets) {
            var testPos = player.getBlockPos().add((int) offset[0], 0, (int) offset[1]);
            if (player.getWorld().getBlockState(testPos).isAir() && 
                player.getWorld().getBlockState(testPos.up()).isAir()) {
                player.setPosition(player.getX() + offset[0], player.getY(), player.getZ() + offset[1]);
                return;
            }
        }
    }

    /**
     * Check if a player can pass through a block while phasing.
     */
    public static boolean canPhaseThrough(PlayerEntity player, SpeedsterData data) {
        return data.isPhaseShiftActive() && data.isSpeedsterEnabled();
    }
}
