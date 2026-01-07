package com.luca296.speedster.mixin;

import com.luca296.speedster.ability.ChainLightningAbility;
import com.luca296.speedster.component.SpeedsterComponents;
import com.luca296.speedster.config.SpeedsterConfig;
import com.luca296.speedster.data.SpeedsterData;
import com.luca296.speedster.network.SpeedsterNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to handle player-specific speedster mechanics.
 */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void speedster$onTick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        
        if (player.getWorld().isClient()) return;
        
        SpeedsterData data = SpeedsterComponents.getData(player);
        
        if (!data.isSpeedsterEnabled()) {
            // Reset states when disabled
            data.reset();
            return;
        }

        SpeedsterConfig config = SpeedsterConfig.get();

        // Tick cooldowns and abilities
        data.tickCooldowns();
        data.tickAbilities();

        // Handle momentum
        if (player.isSprinting() && player.isOnGround()) {
            // Build momentum while sprinting on ground
            data.addMomentum(config.momentumGainRate);
            
            // Build charge while moving fast
            if (data.getMomentumPercent() > 0.3f) {
                data.addCharge(config.chargeGainRate);
            }
        } else if (!player.isSprinting()) {
            // Decay momentum when not sprinting
            data.addMomentum(-config.momentumDecayRate);
        }

        // Handle heat generation at max speed
        if (data.shouldGenerateHeat()) {
            data.addHeat(config.heatGainRate);
            
            // Spawn smoke particles
            if (data.isInDangerZone() && player.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.SMOKE, 
                    player.getX(), player.getY() + 0.5, player.getZ(), 
                    2, 0.2, 0.2, 0.2, 0.01);
            }
            
            // Ignite player at max heat
            if (data.isOverheated() && !player.isOnFire()) {
                player.setOnFireFor(3);
            }
        } else {
            // Natural heat decay
            data.coolDown(config.heatDecayRate);
        }

        // Water cooling
        if (player.isTouchingWater()) {
            data.coolDown(config.waterCoolingRate);
        }

        // Armor damage penalty at high speed
        if (data.getMomentumPercent() > 0.8f && player.getArmor() == 0) {
            if (player.age % 40 == 0) { // Every 2 seconds
                player.damage(player.getDamageSources().generic(), config.armorlessDamageRate);
            }
        }

        // Sync data to client periodically
        if (player instanceof ServerPlayerEntity serverPlayer && player.age % 5 == 0) {
            SpeedsterNetworking.syncToClient(serverPlayer, data);
        }

        // Apply speed modifier
        applySpeedModifier(player, data);
    }

    private void applySpeedModifier(PlayerEntity player, SpeedsterData data) {
        // Get base movement speed attribute
        var speedAttribute = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (speedAttribute == null) return;

        // Calculate speed multiplier based on momentum
        float multiplier = data.getSpeedMultiplier();
        
        // Apply as velocity modification instead of attribute (smoother)
        if (player.isSprinting() && data.getMomentumPercent() > 0.1f) {
            Vec3d velocity = player.getVelocity();
            double boost = 0.02 * (multiplier - 1.0);
            
            // Apply boost in movement direction
            float yaw = player.getYaw();
            double motionX = -Math.sin(Math.toRadians(yaw)) * boost;
            double motionZ = Math.cos(Math.toRadians(yaw)) * boost;
            
            player.setVelocity(velocity.add(motionX, 0, motionZ));
        }
    }

    @Inject(method = "attack", at = @At("HEAD"))
    private void speedster$onAttack(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        
        if (player.getWorld().isClient()) return;
        if (!(target instanceof LivingEntity livingTarget)) return;
        
        SpeedsterData data = SpeedsterComponents.getData(player);
        
        if (!data.isSpeedsterEnabled()) return;

        SpeedsterConfig config = SpeedsterConfig.get();

        // Velocity-based damage bonus
        float momentumPercent = data.getMomentumPercent();
        if (momentumPercent > 0.2f) {
            // Calculate bonus damage
            float bonusDamage = momentumPercent * config.velocityDamageMultiplier * data.getMomentum();
            
            // Apply damage directly (separate from main attack)
            if (bonusDamage > 0.5f) {
                livingTarget.damage(player.getDamageSources().playerAttack(player), bonusDamage);
            }

            // Enhanced knockback
            float knockback = Math.min(momentumPercent * config.velocityKnockbackMultiplier * data.getMomentum(), 
                                       config.maxBonusKnockback);
            if (knockback > 0.5f) {
                Vec3d knockbackDir = livingTarget.getPos().subtract(player.getPos()).normalize();
                livingTarget.addVelocity(knockbackDir.x * knockback, 0.2, knockbackDir.z * knockback);
                livingTarget.velocityModified = true;
            }
        }

        // Try to trigger chain lightning
        ChainLightningAbility.onMeleeHit(player, data, livingTarget);
    }
}
