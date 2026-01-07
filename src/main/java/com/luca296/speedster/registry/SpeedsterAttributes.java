package com.luca296.speedster.registry;

import com.luca296.speedster.Speedster;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

/**
 * Custom attributes for Speedster mod.
 */
public class SpeedsterAttributes {

    public static final RegistryEntry<EntityAttribute> SPEED_MOMENTUM = register("speed_momentum",
        new ClampedEntityAttribute("attribute.speedster.speed_momentum", 0.0, 0.0, 500.0).setTracked(true));

    public static final RegistryEntry<EntityAttribute> STATIC_CHARGE = register("static_charge",
        new ClampedEntityAttribute("attribute.speedster.static_charge", 0.0, 0.0, 500.0).setTracked(true));

    public static final RegistryEntry<EntityAttribute> FRICTION_HEAT = register("friction_heat",
        new ClampedEntityAttribute("attribute.speedster.friction_heat", 0.0, 0.0, 500.0).setTracked(true));

    private static RegistryEntry<EntityAttribute> register(String name, EntityAttribute attribute) {
        return Registry.registerReference(Registries.ATTRIBUTE, Identifier.of(Speedster.MOD_ID, name), attribute);
    }

    public static void register() {
        Speedster.LOGGER.info("Registering Speedster Attributes...");
    }
}
