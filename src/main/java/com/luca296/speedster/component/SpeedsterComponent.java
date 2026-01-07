package com.luca296.speedster.component;

import com.luca296.speedster.data.SpeedsterData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.ComponentV3;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

/**
 * Cardinal Components API component for storing SpeedsterData on players.
 */
public class SpeedsterComponent implements ComponentV3, AutoSyncedComponent {
    
    private final PlayerEntity player;
    private final SpeedsterData data;

    public SpeedsterComponent(PlayerEntity player) {
        this.player = player;
        this.data = new SpeedsterData();
    }

    public SpeedsterData getData() {
        return data;
    }

    public void setData(SpeedsterData newData) {
        this.data.copyFrom(newData);
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public void sync() {
        SpeedsterComponents.SPEEDSTER_DATA.sync(player);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        data.readFromNbt(tag);
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        data.writeToNbt(tag);
    }
}
