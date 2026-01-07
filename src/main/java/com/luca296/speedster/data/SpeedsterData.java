package com.luca296.speedster.data;

import net.minecraft.nbt.NbtCompound;

/**
 * Stores all speedster-related data for a player.
 * This includes momentum, charge, heat, cooldowns, and ability states.
 */
public class SpeedsterData {
    // Speed & Momentum
    private float momentum = 0.0f;
    private float maxMomentum = 100.0f;
    private float speedMultiplier = 1.0f;
    
    // Static Discharge (Electrical charge)
    private float charge = 0.0f;
    private float maxCharge = 100.0f;
    
    // Heat system (friction overheat)
    private float heat = 0.0f;
    private float maxHeat = 100.0f;
    private boolean overheated = false;
    
    // Ability cooldowns (in ticks)
    private int phaseShiftCooldown = 0;
    private int timeDilationCooldown = 0;
    private int aoeStunCooldown = 0;
    
    // Ability states
    private boolean phaseShiftActive = false;
    private int phaseShiftDuration = 0;
    private boolean timeDilationActive = false;
    private int timeDilationDuration = 0;
    private boolean speedsterEnabled = true;
    
    // Movement states
    private boolean wallRunning = false;
    private boolean hydroplaning = false;
    private boolean drifting = false;
    
    // Speed thresholds (percentage of max momentum)
    public static final float HYDROPLANE_THRESHOLD = 0.75f;
    public static final float WALL_RUN_THRESHOLD = 0.50f;
    public static final float DRIFT_THRESHOLD = 0.60f;
    public static final float HEAT_GENERATION_THRESHOLD = 0.90f;
    
    // Cooldown constants (in ticks - 20 ticks = 1 second)
    public static final int PHASE_SHIFT_COOLDOWN = 200; // 10 seconds
    public static final int TIME_DILATION_COOLDOWN = 600; // 30 seconds
    public static final int AOE_STUN_COOLDOWN = 400; // 20 seconds
    
    // Duration constants (in ticks)
    public static final int PHASE_SHIFT_DURATION = 60; // 3 seconds
    public static final int TIME_DILATION_DURATION = 100; // 5 seconds

    public SpeedsterData() {
    }

    // === Momentum Methods ===
    
    public float getMomentum() {
        return momentum;
    }

    public void setMomentum(float momentum) {
        this.momentum = Math.max(0, Math.min(momentum, maxMomentum));
    }

    public void addMomentum(float amount) {
        setMomentum(this.momentum + amount);
    }

    public float getMomentumPercent() {
        return momentum / maxMomentum;
    }

    public float getMaxMomentum() {
        return maxMomentum;
    }

    public float getSpeedMultiplier() {
        // Base multiplier + bonus from momentum (up to 5x speed at max momentum)
        return speedMultiplier + (getMomentumPercent() * 4.0f);
    }

    // === Charge Methods ===
    
    public float getCharge() {
        return charge;
    }

    public void setCharge(float charge) {
        this.charge = Math.max(0, Math.min(charge, maxCharge));
    }

    public void addCharge(float amount) {
        setCharge(this.charge + amount);
    }

    public float getChargePercent() {
        return charge / maxCharge;
    }

    public float getMaxCharge() {
        return maxCharge;
    }

    public boolean hasEnoughCharge(float required) {
        return charge >= required;
    }

    public boolean consumeCharge(float amount) {
        if (hasEnoughCharge(amount)) {
            setCharge(charge - amount);
            return true;
        }
        return false;
    }

    // === Heat Methods ===
    
    public float getHeat() {
        return heat;
    }

    public void setHeat(float heat) {
        this.heat = Math.max(0, Math.min(heat, maxHeat));
        this.overheated = this.heat >= maxHeat;
    }

    public void addHeat(float amount) {
        setHeat(this.heat + amount);
    }

    public void coolDown(float amount) {
        setHeat(this.heat - amount);
    }

    public float getHeatPercent() {
        return heat / maxHeat;
    }

    public float getMaxHeat() {
        return maxHeat;
    }

    public boolean isOverheated() {
        return overheated;
    }

    public boolean isInDangerZone() {
        return getHeatPercent() >= 0.75f;
    }

    // === Cooldown Methods ===
    
    public void tickCooldowns() {
        if (phaseShiftCooldown > 0) phaseShiftCooldown--;
        if (timeDilationCooldown > 0) timeDilationCooldown--;
        if (aoeStunCooldown > 0) aoeStunCooldown--;
    }

    public boolean canUsePhaseShift() {
        return phaseShiftCooldown <= 0 && !phaseShiftActive;
    }

    public boolean canUseTimeDilation() {
        return timeDilationCooldown <= 0 && !timeDilationActive;
    }

    public boolean canUseAoeStun() {
        return aoeStunCooldown <= 0;
    }

    public void startPhaseShiftCooldown() {
        this.phaseShiftCooldown = PHASE_SHIFT_COOLDOWN;
    }

    public void startTimeDilationCooldown() {
        this.timeDilationCooldown = TIME_DILATION_COOLDOWN;
    }

    public void startAoeStunCooldown() {
        this.aoeStunCooldown = AOE_STUN_COOLDOWN;
    }

    public int getPhaseShiftCooldown() {
        return phaseShiftCooldown;
    }

    public int getTimeDilationCooldown() {
        return timeDilationCooldown;
    }

    public int getAoeStunCooldown() {
        return aoeStunCooldown;
    }

    // === Ability State Methods ===
    
    public boolean isPhaseShiftActive() {
        return phaseShiftActive;
    }

    public void activatePhaseShift() {
        this.phaseShiftActive = true;
        this.phaseShiftDuration = PHASE_SHIFT_DURATION;
    }

    public void deactivatePhaseShift() {
        this.phaseShiftActive = false;
        this.phaseShiftDuration = 0;
        startPhaseShiftCooldown();
    }

    public boolean isTimeDilationActive() {
        return timeDilationActive;
    }

    public void activateTimeDilation() {
        this.timeDilationActive = true;
        this.timeDilationDuration = TIME_DILATION_DURATION;
    }

    public void deactivateTimeDilation() {
        this.timeDilationActive = false;
        this.timeDilationDuration = 0;
        startTimeDilationCooldown();
    }

    public void tickAbilities() {
        if (phaseShiftActive) {
            phaseShiftDuration--;
            if (phaseShiftDuration <= 0) {
                deactivatePhaseShift();
            }
        }
        if (timeDilationActive) {
            timeDilationDuration--;
            if (timeDilationDuration <= 0) {
                deactivateTimeDilation();
            }
        }
    }

    public int getPhaseShiftDuration() {
        return phaseShiftDuration;
    }

    public int getTimeDilationDuration() {
        return timeDilationDuration;
    }

    // === Movement State Methods ===
    
    public boolean isWallRunning() {
        return wallRunning;
    }

    public void setWallRunning(boolean wallRunning) {
        this.wallRunning = wallRunning;
    }

    public boolean isHydroplaning() {
        return hydroplaning;
    }

    public void setHydroplaning(boolean hydroplaning) {
        this.hydroplaning = hydroplaning;
    }

    public boolean isDrifting() {
        return drifting;
    }

    public void setDrifting(boolean drifting) {
        this.drifting = drifting;
    }

    public boolean canHydroplane() {
        return getMomentumPercent() >= HYDROPLANE_THRESHOLD;
    }

    public boolean canWallRun() {
        return getMomentumPercent() >= WALL_RUN_THRESHOLD;
    }

    public boolean shouldGenerateHeat() {
        return getMomentumPercent() >= HEAT_GENERATION_THRESHOLD;
    }

    // === Toggle Methods ===
    
    public boolean isSpeedsterEnabled() {
        return speedsterEnabled;
    }

    public void setSpeedsterEnabled(boolean enabled) {
        this.speedsterEnabled = enabled;
    }

    public void toggleSpeedster() {
        this.speedsterEnabled = !this.speedsterEnabled;
    }

    // === NBT Serialization ===
    
    public void writeToNbt(NbtCompound nbt) {
        nbt.putFloat("momentum", momentum);
        nbt.putFloat("charge", charge);
        nbt.putFloat("heat", heat);
        nbt.putInt("phaseShiftCooldown", phaseShiftCooldown);
        nbt.putInt("timeDilationCooldown", timeDilationCooldown);
        nbt.putInt("aoeStunCooldown", aoeStunCooldown);
        nbt.putBoolean("speedsterEnabled", speedsterEnabled);
    }

    public void readFromNbt(NbtCompound nbt) {
        momentum = nbt.getFloat("momentum");
        charge = nbt.getFloat("charge");
        heat = nbt.getFloat("heat");
        phaseShiftCooldown = nbt.getInt("phaseShiftCooldown");
        timeDilationCooldown = nbt.getInt("timeDilationCooldown");
        aoeStunCooldown = nbt.getInt("aoeStunCooldown");
        speedsterEnabled = nbt.getBoolean("speedsterEnabled");
    }

    public void copyFrom(SpeedsterData other) {
        this.momentum = other.momentum;
        this.charge = other.charge;
        this.heat = other.heat;
        this.phaseShiftCooldown = other.phaseShiftCooldown;
        this.timeDilationCooldown = other.timeDilationCooldown;
        this.aoeStunCooldown = other.aoeStunCooldown;
        this.speedsterEnabled = other.speedsterEnabled;
    }

    public void reset() {
        this.momentum = 0;
        this.charge = 0;
        this.heat = 0;
        this.overheated = false;
        this.phaseShiftActive = false;
        this.timeDilationActive = false;
        this.wallRunning = false;
        this.hydroplaning = false;
        this.drifting = false;
    }
}
