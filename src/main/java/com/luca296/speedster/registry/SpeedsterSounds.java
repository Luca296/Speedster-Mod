package com.luca296.speedster.registry;

import com.luca296.speedster.Speedster;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

/**
 * Custom sound events for Speedster mod.
 */
public class SpeedsterSounds {

    public static final SoundEvent SPEED_WHOOSH = register("speed_whoosh");
    public static final SoundEvent LIGHTNING_CRACK = register("lightning_crack");
    public static final SoundEvent PHASE_SHIFT = register("phase_shift");
    public static final SoundEvent TIME_DILATE = register("time_dilate");
    public static final SoundEvent CHARGE_BUILD = register("charge_build");
    public static final SoundEvent OVERHEAT = register("overheat");
    public static final SoundEvent WALL_RUN = register("wall_run");
    public static final SoundEvent HYDROPLANE = register("hydroplane");
    public static final SoundEvent DRIFT = register("drift");

    private static SoundEvent register(String name) {
        Identifier id = Identifier.of(Speedster.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void register() {
        Speedster.LOGGER.info("Registering Speedster Sounds...");
    }
}
