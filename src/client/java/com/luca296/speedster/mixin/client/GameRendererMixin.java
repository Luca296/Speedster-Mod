package com.luca296.speedster.mixin.client;

import com.luca296.speedster.component.SpeedsterComponents;
import com.luca296.speedster.config.SpeedsterConfig;
import com.luca296.speedster.data.SpeedsterData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Client-side mixin for rendering effects (vignette, motion blur simulation).
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow
    @Final
    MinecraftClient client;

    @Inject(method = "render", at = @At("HEAD"))
    private void speedster$onRender(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        if (client.player == null) return;
        
        SpeedsterData data = SpeedsterComponents.getData(client.player);
        SpeedsterConfig config = SpeedsterConfig.get();
        
        if (!data.isSpeedsterEnabled()) return;

        // Apply FOV modification based on speed (creates speed effect)
        // This is handled separately via the FOV modifier below
    }

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void speedster$modifyFov(Camera camera, float tickDelta, 
                                      boolean changingFov, CallbackInfoReturnable<Float> cir) {
        if (client.player == null) return;
        
        SpeedsterData data = SpeedsterComponents.getData(client.player);
        
        if (!data.isSpeedsterEnabled()) return;

        // Increase FOV based on momentum for speed effect
        float momentumPercent = data.getMomentumPercent();
        if (momentumPercent > 0.3f) {
            float baseFov = cir.getReturnValue();
            float fovBoost = (momentumPercent - 0.3f) * 30.0f; // Up to +21 FOV at max speed
            cir.setReturnValue(baseFov + fovBoost);
        }
    }
}
