package com.luca296.speedster.mixin.client;

import com.luca296.speedster.component.SpeedsterComponents;
import com.luca296.speedster.config.SpeedsterConfig;
import com.luca296.speedster.data.SpeedsterData;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Client-side mixin for player movement feedback and particle spawning.
 */
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void speedster$onClientTick(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        SpeedsterData data = SpeedsterComponents.getData(player);
        
        if (!data.isSpeedsterEnabled()) return;

        SpeedsterConfig config = SpeedsterConfig.get();

        // Spawn speed particles when moving fast
        if (config.enableSpeedTrails && data.getMomentumPercent() > 0.5f && player.isSprinting()) {
            spawnSpeedParticles(player, data);
        }

        // Spawn drift particles
        if (config.enableSparkParticles && data.isDrifting()) {
            spawnDriftParticles(player);
        }
    }

    private void spawnSpeedParticles(ClientPlayerEntity player, SpeedsterData data) {
        // Cloud particles behind the player
        Vec3d velocity = player.getVelocity();
        double speed = velocity.horizontalLength();
        
        if (speed > 0.3) {
            // Spawn cloud particles
            for (int i = 0; i < (int)(data.getMomentumPercent() * 3); i++) {
                double offsetX = (Math.random() - 0.5) * 0.5;
                double offsetY = Math.random() * 0.5;
                double offsetZ = (Math.random() - 0.5) * 0.5;
                
                player.getWorld().addParticle(
                    net.minecraft.particle.ParticleTypes.CLOUD,
                    player.getX() - velocity.x * 0.5 + offsetX,
                    player.getY() + offsetY,
                    player.getZ() - velocity.z * 0.5 + offsetZ,
                    -velocity.x * 0.1,
                    0.02,
                    -velocity.z * 0.1
                );
            }
        }
    }

    private void spawnDriftParticles(ClientPlayerEntity player) {
        // Spark particles at feet when drifting
        for (int i = 0; i < 3; i++) {
            double offsetX = (Math.random() - 0.5) * 0.3;
            double offsetZ = (Math.random() - 0.5) * 0.3;
            
            player.getWorld().addParticle(
                net.minecraft.particle.ParticleTypes.ELECTRIC_SPARK,
                player.getX() + offsetX,
                player.getY() + 0.1,
                player.getZ() + offsetZ,
                (Math.random() - 0.5) * 0.2,
                0.05,
                (Math.random() - 0.5) * 0.2
            );
        }
    }
}
