package com.luca296.speedster.ability;

import com.luca296.speedster.Speedster;
import com.luca296.speedster.config.SpeedsterConfig;
import com.luca296.speedster.data.SpeedsterData;
import com.luca296.speedster.registry.SpeedsterSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Chain Lightning ability - triggers on melee hit, chains to nearby enemies.
 */
public class ChainLightningAbility {

    private static final float CHARGE_COST = 30.0f;
    private static final float BASE_DAMAGE = 4.0f;
    private static final float CHAIN_DAMAGE_FALLOFF = 0.7f; // Each chain does 70% of previous

    /**
     * Attempts to trigger chain lightning on a melee hit.
     * Returns true if it was triggered.
     */
    public static boolean onMeleeHit(PlayerEntity player, SpeedsterData data, LivingEntity target) {
        if (!data.isSpeedsterEnabled()) return false;
        
        // Need at least 30% charge to trigger
        if (!data.hasEnoughCharge(CHARGE_COST)) return false;
        
        // Need at least 40% momentum
        if (data.getMomentumPercent() < 0.4f) return false;

        // Consume charge
        data.consumeCharge(CHARGE_COST);

        // Calculate chain targets
        SpeedsterConfig config = SpeedsterConfig.get();
        int maxTargets = config.chainLightningTargets;
        float range = config.chainLightningRange;

        List<LivingEntity> chainTargets = findChainTargets(player, target, maxTargets, range);

        // Apply damage to chain targets
        float currentDamage = BASE_DAMAGE;
        LivingEntity previousTarget = target;

        for (LivingEntity chainTarget : chainTargets) {
            // Apply damage
            chainTarget.damage(player.getDamageSources().indirectMagic(player, player), currentDamage);
            
            // Spawn lightning particles between targets
            spawnLightningParticles(player.getWorld(), previousTarget.getPos(), chainTarget.getPos());
            
            // Reduce damage for next chain
            currentDamage *= CHAIN_DAMAGE_FALLOFF;
            previousTarget = chainTarget;
        }

        // Play sound
        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
            SpeedsterSounds.LIGHTNING_CRACK, SoundCategory.PLAYERS, 1.0f, 1.2f);

        Speedster.LOGGER.debug("Chain lightning triggered, hit {} additional targets", chainTargets.size());
        
        return true;
    }

    private static List<LivingEntity> findChainTargets(PlayerEntity player, LivingEntity initialTarget, 
                                                        int maxTargets, float range) {
        List<LivingEntity> targets = new ArrayList<>();
        List<LivingEntity> processed = new ArrayList<>();
        processed.add(initialTarget);
        processed.add(player);

        LivingEntity currentTarget = initialTarget;

        while (targets.size() < maxTargets) {
            // Find nearest unprocessed enemy to current target
            Box searchArea = currentTarget.getBoundingBox().expand(range);
            LivingEntity finalCurrentTarget = currentTarget;
            
            List<LivingEntity> nearby = player.getWorld()
                .getEntitiesByClass(LivingEntity.class, searchArea, 
                    e -> !processed.contains(e) && e.isAlive() && !(e instanceof PlayerEntity))
                .stream()
                .sorted(Comparator.comparingDouble(e -> e.squaredDistanceTo(finalCurrentTarget)))
                .toList();

            if (nearby.isEmpty()) break;

            LivingEntity nextTarget = nearby.get(0);
            targets.add(nextTarget);
            processed.add(nextTarget);
            currentTarget = nextTarget;
        }

        return targets;
    }

    private static void spawnLightningParticles(net.minecraft.world.World world, Vec3d from, Vec3d to) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        Vec3d direction = to.subtract(from);
        double distance = direction.length();
        Vec3d normalized = direction.normalize();

        // Spawn particles along the path
        int particleCount = (int) (distance * 3);
        for (int i = 0; i < particleCount; i++) {
            double t = (double) i / particleCount;
            double x = from.x + direction.x * t + (Math.random() - 0.5) * 0.3;
            double y = from.y + 1 + direction.y * t + (Math.random() - 0.5) * 0.3;
            double z = from.z + direction.z * t + (Math.random() - 0.5) * 0.3;
            
            serverWorld.spawnParticles(ParticleTypes.ELECTRIC_SPARK, x, y, z, 1, 0, 0, 0, 0);
        }
    }
}
