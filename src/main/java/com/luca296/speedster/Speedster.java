package com.luca296.speedster;

import com.luca296.speedster.component.SpeedsterComponents;
import com.luca296.speedster.config.SpeedsterConfig;
import com.luca296.speedster.network.SpeedsterNetworking;
import com.luca296.speedster.registry.SpeedsterAttributes;
import com.luca296.speedster.registry.SpeedsterSounds;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Speedster implements ModInitializer {
    public static final String MOD_ID = "speedster";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Speedster Mod - Time to go fast!");

        // Load configuration
        SpeedsterConfig.load();

        // Register components for player data
        SpeedsterComponents.register();

        // Register custom attributes
        SpeedsterAttributes.register();

        // Register sounds
        SpeedsterSounds.register();

        // Register networking packets
        SpeedsterNetworking.registerServerPackets();

        LOGGER.info("Speedster Mod initialized successfully!");
    }
}
