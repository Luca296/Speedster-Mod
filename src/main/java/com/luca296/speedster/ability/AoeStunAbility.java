package com.luca296.speedster.ability;

import com.luca296.speedster.Speedster;
import com.luca296.speedster.config.SpeedsterConfig;
import com.luca296.speedster.data.SpeedsterData;
import com.luca296.speedster.registry.SpeedsterSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;

import java.util.List;

/**
 * AoE Stun ability - releases electrical charge to stun nearby enemies.
 */
public class AoeStunAbility {

    private static final double STUN_RADIUS = 8.0;
    private static final int STUN_DURATION = 60; // 3 seconds in ticks
    private static final float CHARGE_COST = 50.0f;

    public static void activate(PlayerEntity player, SpeedsterData data) {
        if (!data.canUseAoeStun()) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                int remaining = data.getAoeStunCooldown() / 20;
                serverPlayer.sendMessage(Text.literal("§cAoE Stun on cooldown! " + remaining + "s remaining"), true);
            }
            return;
        }

        // Check if player has enough charge
        if (!data.hasEnoughCharge(CHARGE_COST)) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.sendMessage(Text.literal("§cNot enough charge! Need " + (int)CHARGE_COST + "%"), true);
            }
            return;
        }

        // Consume charge
        data.consumeCharge(CHARGE_COST);
        
        // Start cooldown
        data.startAoeStunCooldown();

        // Get nearby entities
        Box area = player.getBoundingBox().expand(STUN_RADIUS);
        List<Entity> nearbyEntities = player.getWorld().getOtherEntities(player, area, 
            e -> e instanceof LivingEntity && e != player);

        int stunCount = 0;
        for (Entity entity : nearbyEntities) {
            if (entity instanceof LivingEntity living) {
                // Apply slowness and weakness (stun effect)
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, STUN_DURATION, 4));
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, STUN_DURATION, 2));
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, STUN_DURATION, 0));
                
                // Apply small damage
                living.damage(player.getDamageSources().lightningBolt(), 2.0f);
                
                stunCount++;
            }
        }

        // Play sound and particles
        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
            SpeedsterSounds.LIGHTNING_CRACK, SoundCategory.PLAYERS, 1.5f, 1.0f);

        // Spawn electric particles
        if (player.getWorld() instanceof ServerWorld serverWorld) {
            for (int i = 0; i < 50; i++) {
                double angle = Math.random() * Math.PI * 2;
                double distance = Math.random() * STUN_RADIUS;
                double px = player.getX() + Math.cos(angle) * distance;
                double py = player.getY() + Math.random() * 2;
                double pz = player.getZ() + Math.sin(angle) * distance;
                
                serverWorld.spawnParticles(ParticleTypes.ELECTRIC_SPARK, px, py, pz, 3, 0.1, 0.1, 0.1, 0.1);
            }
        }

        if (player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.sendMessage(Text.literal("§eStatic Discharge! Stunned " + stunCount + " enemies"), true);
        }

        Speedster.LOGGER.debug("AoE Stun activated by {}, stunned {} entities", 
            player.getName().getString(), stunCount);
    }
}
