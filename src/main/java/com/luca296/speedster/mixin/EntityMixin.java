package com.luca296.speedster.mixin;

import com.luca296.speedster.ability.PhaseShiftAbility;
import com.luca296.speedster.component.SpeedsterComponents;
import com.luca296.speedster.data.SpeedsterData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin for Entity to handle phase shift collision bypass.
 */
@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    public abstract Box getBoundingBox();

    /**
     * Allow players to pass through blocks while Phase Shift is active.
     */
    /**
     * Modify collision shape checking for phasing players.
     */
    @Inject(method = "collidesWithStateAtPos", at = @At("HEAD"), cancellable = true)
    private void speedster$onStateCollision(net.minecraft.util.math.BlockPos pos,
                                             net.minecraft.block.BlockState state,
                                             CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        
        if (!(entity instanceof PlayerEntity player)) return;
        
        SpeedsterData data = SpeedsterComponents.getData(player);
        
        if (PhaseShiftAbility.canPhaseThrough(player, data)) {
            if (!state.isOf(net.minecraft.block.Blocks.BEDROCK) && 
                !state.isOf(net.minecraft.block.Blocks.BARRIER)) {
                cir.setReturnValue(false);
            }
        }
    }
}
