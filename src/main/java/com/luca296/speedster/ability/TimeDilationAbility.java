package com.luca296.speedster.ability;

import com.luca296.speedster.Speedster;
import com.luca296.speedster.data.SpeedsterData;
import com.luca296.speedster.registry.SpeedsterSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;

/**
 * Time Dilation ability - slows down perception of nearby entities (client-side effect).
 */
public class TimeDilationAbility {

    // How much to slow down entity rendering (0.5 = half speed)
    public static final float DILATION_FACTOR = 0.3f;

    public static void activate(PlayerEntity player, SpeedsterData data) {
        if (!data.canUseTimeDilation()) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                int remaining = data.getTimeDilationCooldown() / 20;
                serverPlayer.sendMessage(Text.literal("§cTime Dilation on cooldown! " + remaining + "s remaining"), true);
            }
            return;
        }

        // Check if player has enough momentum (60% threshold)
        if (data.getMomentumPercent() < 0.6f) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.sendMessage(Text.literal("§cNot enough momentum for Time Dilation!"), true);
            }
            return;
        }

        // Activate the ability
        data.activateTimeDilation();
        
        // Consume some momentum
        data.addMomentum(-30);

        // Play sound
        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
            SpeedsterSounds.TIME_DILATE, SoundCategory.PLAYERS, 1.0f, 0.5f);

        if (player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.sendMessage(Text.literal("§eTime Dilation activated!"), true);
        }

        Speedster.LOGGER.debug("Time Dilation activated for player: {}", player.getName().getString());
    }

    public static void deactivate(PlayerEntity player, SpeedsterData data) {
        data.deactivateTimeDilation();

        if (player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.sendMessage(Text.literal("§7Time Dilation ended"), true);
        }
    }

    /**
     * Get the current dilation factor for rendering.
     * Returns 1.0 if not active, DILATION_FACTOR if active.
     */
    public static float getDilationFactor(SpeedsterData data) {
        return data.isTimeDilationActive() ? DILATION_FACTOR : 1.0f;
    }

    /**
     * Check if time dilation should affect a given entity distance.
     * Only affects entities within a certain range.
     */
    public static boolean shouldAffectEntity(PlayerEntity player, double distance) {
        return distance <= 30.0; // 30 block radius
    }
}
