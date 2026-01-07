package com.luca296.speedster;

import com.luca296.speedster.client.SpeedsterKeybindings;
import com.luca296.speedster.client.handler.SpeedsterClientTickHandler;
import com.luca296.speedster.client.render.SpeedsterHudRenderer;
import com.luca296.speedster.network.SpeedsterNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

@Environment(EnvType.CLIENT)
public class SpeedsterClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Speedster.LOGGER.info("Initializing Speedster Client...");

        // Register keybindings
        SpeedsterKeybindings.register();

        // Register client tick handler
        ClientTickEvents.END_CLIENT_TICK.register(SpeedsterClientTickHandler::onClientTick);

        // Register HUD renderer for speed/charge/heat meters
        HudRenderCallback.EVENT.register(SpeedsterHudRenderer::render);

        // Register client-side networking
        SpeedsterNetworking.registerClientPackets();

        Speedster.LOGGER.info("Speedster Client initialized!");
    }
}
