package com.luca296.speedster.network;

import com.luca296.speedster.Speedster;
import com.luca296.speedster.component.SpeedsterComponents;
import com.luca296.speedster.data.SpeedsterData;
import com.luca296.speedster.ability.PhaseShiftAbility;
import com.luca296.speedster.ability.TimeDilationAbility;
import com.luca296.speedster.ability.AoeStunAbility;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * Network packet handling for Speedster mod.
 */
public class SpeedsterNetworking {

    // === Packet IDs ===
    public static final Identifier ABILITY_USE_ID = Identifier.of(Speedster.MOD_ID, "ability_use");
    public static final Identifier SYNC_DATA_ID = Identifier.of(Speedster.MOD_ID, "sync_data");
    public static final Identifier TOGGLE_SPEEDSTER_ID = Identifier.of(Speedster.MOD_ID, "toggle_speedster");

    // === Packet Records ===
    
    public record AbilityUsePayload(String abilityId) implements CustomPayload {
        public static final CustomPayload.Id<AbilityUsePayload> ID = new CustomPayload.Id<>(ABILITY_USE_ID);
        public static final PacketCodec<RegistryByteBuf, AbilityUsePayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, AbilityUsePayload::abilityId,
            AbilityUsePayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public record SyncDataPayload(float momentum, float charge, float heat, 
                                   boolean phaseActive, boolean timeActive) implements CustomPayload {
        public static final CustomPayload.Id<SyncDataPayload> ID = new CustomPayload.Id<>(SYNC_DATA_ID);
        public static final PacketCodec<RegistryByteBuf, SyncDataPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.FLOAT, SyncDataPayload::momentum,
            PacketCodecs.FLOAT, SyncDataPayload::charge,
            PacketCodecs.FLOAT, SyncDataPayload::heat,
            PacketCodecs.BOOL, SyncDataPayload::phaseActive,
            PacketCodecs.BOOL, SyncDataPayload::timeActive,
            SyncDataPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public record ToggleSpeedsterPayload(boolean enabled) implements CustomPayload {
        public static final CustomPayload.Id<ToggleSpeedsterPayload> ID = new CustomPayload.Id<>(TOGGLE_SPEEDSTER_ID);
        public static final PacketCodec<RegistryByteBuf, ToggleSpeedsterPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, ToggleSpeedsterPayload::enabled,
            ToggleSpeedsterPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    // === Server-side Registration ===
    
    public static void registerServerPackets() {
        Speedster.LOGGER.info("Registering server packets...");
        
        // Register payload types
        PayloadTypeRegistry.playC2S().register(AbilityUsePayload.ID, AbilityUsePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ToggleSpeedsterPayload.ID, ToggleSpeedsterPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncDataPayload.ID, SyncDataPayload.CODEC);

        // Handle ability use from client
        ServerPlayNetworking.registerGlobalReceiver(AbilityUsePayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            String abilityId = payload.abilityId();
            
            context.server().execute(() -> {
                SpeedsterData data = SpeedsterComponents.getData(player);
                
                if (!data.isSpeedsterEnabled()) return;
                
                switch (abilityId) {
                    case "phase_shift" -> PhaseShiftAbility.activate(player, data);
                    case "time_dilation" -> TimeDilationAbility.activate(player, data);
                    case "aoe_stun" -> AoeStunAbility.activate(player, data);
                }
            });
        });

        // Handle toggle speedster
        ServerPlayNetworking.registerGlobalReceiver(ToggleSpeedsterPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            
            context.server().execute(() -> {
                SpeedsterData data = SpeedsterComponents.getData(player);
                data.setSpeedsterEnabled(payload.enabled());
            });
        });
    }

    // === Client-side Registration ===
    
    public static void registerClientPackets() {
        Speedster.LOGGER.info("Registering client packets...");
        
        // Handle sync data from server
        ClientPlayNetworking.registerGlobalReceiver(SyncDataPayload.ID, (payload, context) -> {
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
        ClientPlayNetworking.send(new AbilityUsePayload(abilityId));
    }

    public static void sendToggleSpeedster(boolean enabled) {
        ClientPlayNetworking.send(new ToggleSpeedsterPayload(enabled));
    }

    public static void syncToClient(ServerPlayerEntity player, SpeedsterData data) {
        ServerPlayNetworking.send(player, new SyncDataPayload(
            data.getMomentum(),
            data.getCharge(),
            data.getHeat(),
            data.isPhaseShiftActive(),
            data.isTimeDilationActive()
        ));
    }
}
