package com.luca296.speedster.component;

import com.luca296.speedster.Speedster;
import com.luca296.speedster.data.SpeedsterData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

/**
 * Component registration for Cardinal Components API.
 * This allows persistent data attachment to players.
 */
public class SpeedsterComponents implements EntityComponentInitializer {
    
    public static final ComponentKey<SpeedsterComponent> SPEEDSTER_DATA = 
        ComponentRegistry.getOrCreate(Identifier.of(Speedster.MOD_ID, "speedster_data"), SpeedsterComponent.class);

    public static void register() {
        Speedster.LOGGER.info("Registering Speedster Components...");
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(SPEEDSTER_DATA, SpeedsterComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
    }

    /**
     * Gets the SpeedsterData for a player.
     */
    public static SpeedsterData getData(PlayerEntity player) {
        return SPEEDSTER_DATA.get(player).getData();
    }

    /**
     * Sets the SpeedsterData for a player.
     */
    public static void setData(PlayerEntity player, SpeedsterData data) {
        SPEEDSTER_DATA.get(player).setData(data);
    }
}
