package com.luca296.speedster.network;

import com.luca296.speedster.Speedster;
import com.luca296.speedster.component.SpeedsterComponents;
import com.luca296.speedster.data.SpeedsterData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * Client-side network packet handling for Speedster mod.
 */
public class SpeedsterClientNetworking {

    // === Client-side Registration ===
    
    public static void registerClientPackets() {
        Speedster.LOGGER.info("Registering client packets...");
        
        // Handle sync data from server
        ClientPlayNetworking.registerGlobalReceiver(SpeedsterNetworking.SyncDataPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                if (context.player() != null) {
                    SpeedsterData data = SpeedsterComponents.getData(context.player());
                    data.setMomentum(payload.momentum());
                    data.setCharge(payload.charge());
                    data.setHeat(payload.heat());
                }
            });
        });
    }

    // === Send Methods ===
    
    public static void sendAbilityUse(String abilityId) {
        ClientPlayNetworking.send(new SpeedsterNetworking.AbilityUsePayload(abilityId));
    }

    public static void sendToggleSpeedster(boolean enabled) {
        ClientPlayNetworking.send(new SpeedsterNetworking.ToggleSpeedsterPayload(enabled));
    }
}
