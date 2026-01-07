package com.luca296.speedster.mixin;

import com.luca296.speedster.ability.PhaseShiftAbility;
import com.luca296.speedster.component.SpeedsterComponents;
import com.luca296.speedster.data.SpeedsterData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin for LivingEntity to handle speedster movement mechanics.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "travel", at = @At("HEAD"))
    private void speedster$onTravel(Vec3d movementInput, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        
        if (!(entity instanceof PlayerEntity player)) return;
        if (entity.getWorld().isClient()) return;
        
        SpeedsterData data = SpeedsterComponents.getData(player);
        
        if (!data.isSpeedsterEnabled()) return;

        // Handle hydroplaning
        if (data.canHydroplane() && (player.isTouchingWater() || player.isInLava()) && player.isSprinting()) {
            // Keep player on surface of liquid
            Vec3d velocity = player.getVelocity();
            if (velocity.y < 0) {
                player.setVelocity(velocity.x, 0.0, velocity.z);
            }
            
            // Reduce liquid slowdown
            data.setHydroplaning(true);
        } else {
            data.setHydroplaning(false);
        }

        // Handle wall running
        if (data.canWallRun() && !player.isOnGround() && isNearWall(player)) {
            Vec3d velocity = player.getVelocity();
            
            // Convert horizontal momentum to vertical lift
            double horizontalSpeed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
            if (horizontalSpeed > 0.2) {
                // Apply wall run - reduce fall speed and allow upward movement
                double lift = Math.min(0.15, horizontalSpeed * 0.3);
                if (velocity.y < lift) {
                    player.setVelocity(velocity.x * 0.95, Math.max(velocity.y, -0.1) + lift * 0.5, velocity.z * 0.95);
                }
                data.setWallRunning(true);
            }
        } else {
            data.setWallRunning(false);
        }
    }

    private boolean isNearWall(PlayerEntity player) {
        // Check blocks in each cardinal direction
        var world = player.getWorld();
        var pos = player.getBlockPos();
        double checkDistance = 0.5;
        
        // Check if there's a solid block adjacent at player height
        return !world.getBlockState(pos.north()).isAir() ||
               !world.getBlockState(pos.south()).isAir() ||
               !world.getBlockState(pos.east()).isAir() ||
               !world.getBlockState(pos.west()).isAir();
    }

    @Inject(method = "isClimbing", at = @At("HEAD"), cancellable = true)
    private void speedster$onIsClimbing(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        
        if (!(entity instanceof PlayerEntity player)) return;
        
        SpeedsterData data = SpeedsterComponents.getData(player);
        
        // Wall running counts as climbing for fall damage purposes
        if (data.isSpeedsterEnabled() && data.isWallRunning()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
    private void speedster$onFallDamage(float fallDistance, float damageMultiplier, 
                                         net.minecraft.entity.damage.DamageSource source, 
                                         CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        
        if (!(entity instanceof PlayerEntity player)) return;
        
        SpeedsterData data = SpeedsterComponents.getData(player);
        
        // Reduce fall damage based on momentum (speedsters are used to high-speed impacts)
        if (data.isSpeedsterEnabled() && data.getMomentumPercent() > 0.5f) {
            float reduction = data.getMomentumPercent() * 0.5f; // Up to 50% reduction
            float newDistance = fallDistance * (1.0f - reduction);
            
            if (newDistance < 3.0f) {
                cir.setReturnValue(false); // No damage
            }
        }
    }
}
