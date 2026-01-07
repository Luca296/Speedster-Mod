# Speedster-Mod Debug / Test Guide

This file is a **hands-on testing checklist** for the systems currently implemented in this repo.
It’s written to be used while you’re in-game so you can verify behavior quickly and consistently.

## 0) Recommended test setup (fast + repeatable)

### World / player setup
- Use a **Creative** test world (so you can set up environments quickly).
- Turn on **cheats** so you can spawn mobs and control time/weather.
- Optional but helpful:
  - `/gamerule doDaylightCycle false`
  - `/gamerule doWeatherCycle false`
  - `/time set noon`

### Make a simple “test track” area
Build these close together so you can move between tests quickly:
- **Sprint lane**: a 100+ block flat runway (stone or grass).
- **Wall-run strip**: a long 2–3 block tall wall alongside a runway.
- **Hydroplane pool**: a shallow 1-block-deep **water** pool with a runway into it.
- **Lava hydroplane pool** (optional): a contained lava lane (be careful; have fire resistance).
- **Combat pen**: a fenced area to spawn several mobs grouped close together.

### Important: speedster HUD must be visible
The mod’s feedback is mostly visual via HUD.
- Don’t hide the HUD (avoid pressing **F1**).
- When Speedster is enabled you should see:
  - **Bottom-left bars**: `Speed`, `Charge`, `Heat`
  - **Center text** when abilities are active: `PHASING`, `TIME DILATION`
  - **Center text** for movement states: `WALL RUNNING`, `HYDROPLANING`, `DRIFTING`
  - **Right-side cooldown text** after using abilities

If you see none of that, jump to **Troubleshooting** at the bottom.

## 1) Controls (default keybinds)

From the current client keybind registration:
- **Phase Shift**: `V`
- **Time Dilation**: `G`
- **AoE Stun**: `R`
- **Toggle Speedster**: `H`

Note: the toggle keybind currently sends an “enable=true” packet unconditionally, so it effectively behaves like a **force-enable** key rather than a true toggle.

## 2) How to interpret the HUD meters

### Speed (Momentum)
- “Speed” is the mod’s **momentum meter**.
- Momentum is a percentage of an internal max (currently 100).
- Many systems key off **thresholds** of this bar.

### Charge (Static Discharge)
- “Charge” builds while moving fast and is consumed by electric abilities.

### Heat (Friction / Overheat)
- Heat rises only at very high momentum.
- Heat has a danger zone starting at **75%**.

## 3) Core Speed System

### 3.1 Momentum build + decay
**What it is**
- Momentum increases while you are **sprinting on the ground**.
- Momentum decays when you **stop sprinting**.

**How to test**
1. Stand at the start of your sprint lane.
2. Hold sprint and run forward.
3. Watch the `Speed` bar increase.
4. Stop sprinting (keep walking) and watch the bar decay.

**Expected results**
- `Speed` rises smoothly while sprinting.
- `Speed` falls when you stop sprinting.
- If you stop sprinting completely, you should return toward 0% over time.

### 3.2 Speed scaling (movement boost)
**What it is**
- As momentum rises, the mod applies a **forward velocity boost** while sprinting.

**How to test**
1. On a flat runway, sprint until `Speed` is at least ~50%.
2. Compare how quickly you cover distance at low vs high momentum.

**Expected results**
- At higher momentum, you accelerate and maintain higher speed.
- The effect is strongest when sprinting and already moving forward.

### 3.3 Speed “tiers” (threshold-based behavior)
**What it is**
Several systems unlock/activate at these approximate thresholds:
- **30%**: charge begins building
- **40%**: chain lightning can trigger (if you have enough charge)
- **50%**: wall-run is allowed; Phase Shift is allowed
- **60%**: drift indicator and Time Dilation are allowed; vignette begins
- **75%**: hydroplaning is allowed
- **80%**: armorless damage penalty begins (if wearing **0 armor**)
- **90%**: heat generation begins
- **100%**: ignition from overheating (eventually)

**How to test**
- Use this guide’s sections below and intentionally target specific `Speed` percentages.

## 4) Charge System (Static Discharge)

### 4.1 Charge generation
**What it is**
- Charge increases while sprinting on ground **once momentum is above ~30%**.

**How to test**
1. Sprint until `Speed` is above ~30%.
2. Keep sprinting steadily.
3. Watch `Charge` increase.

**Expected results**
- `Charge` should begin increasing only after you’re moving fast enough.
- If you slow down and lose momentum, charge gain should stop.

## 5) Heat / Overheat System

### 5.1 Heat generation at extreme speed
**What it is**
- Heat increases when momentum is very high (at/above ~90%).

**How to test**
1. Sprint until `Speed` is near max.
2. Hold that speed as long as possible.
3. Watch the `Heat` bar.

**Expected results**
- `Heat` increases while you maintain very high momentum.
- If you slow down below the heat threshold, heat should begin to decay.

### 5.2 Danger zone overlays + smoke
**What it is**
- At **75% heat**, the mod considers you in the danger zone:
  - A **red pulsing overlay** may appear.
  - **Smoke particles** spawn around you (server-side).

**How to test**
1. Build heat above 75%.
2. Keep running and observe visuals.

**Expected results**
- You see intermittent/pulsing red tint.
- Smoke particles appear around your player while you stay hot.

### 5.3 Ignition at maximum heat
**What it is**
- When heat reaches max, the player will be set on fire briefly.

**How to test**
1. In a safe area, keep heat climbing to 100%.
2. Watch your player state.

**Expected results**
- Your player ignites for a short duration.

### 5.4 Water cooling
**What it is**
- Touching water quickly cools you down.

**How to test**
1. Build heat to a noticeable level (ideally >50%).
2. Jump into water.
3. Watch the `Heat` bar.

**Expected results**
- Heat drops much faster in water than in air.

## 6) Movement Abilities

### 6.1 Hydroplaning (water/lava running)
**What it is**
- At high momentum (≥ ~75%), sprinting on water/lava prevents you from sinking and helps you stay on the surface.

**How to test (water)**
1. Build `Speed` to ≥ 75% on land.
2. Sprint into the water lane without stopping.
3. Keep holding sprint.

**Expected results**
- You do **not** sink like normal swimming.
- The HUD should show `HYDROPLANING` while conditions are met.

**How to test (lava)**
- Same as water, but do this only with fire resistance or in creative.

**Expected results**
- Similar surface-running behavior on lava.

### 6.2 Wall running
**What it is**
- At momentum ≥ ~50%, when you are airborne and adjacent to a wall, the mod converts horizontal speed into **upward lift** and reduces falling.

**How to test**
1. Build `Speed` to ≥ 50%.
2. Sprint parallel to a long wall.
3. Jump while staying close to the wall.
4. Keep moving forward along the wall.

**Expected results**
- You “stick” to the wall run longer than a normal jump.
- You may gain slight upward lift and fall more slowly.
- HUD should show `WALL RUNNING` while active.

### 6.3 Drift indicator + spark feedback
**What it is**
- Drift is currently a **client-side state** used for visuals/feedback.
- It triggers when:
  - momentum ≥ ~60%
  - sprinting on ground
  - your **movement direction** differs from your **look direction** by ~45°+

**How to test**
1. Build `Speed` to ≥ 60%.
2. While sprinting, make a sharp turn (try turning your camera hard while still moving forward).
3. Keep sprint held.

**Expected results**
- HUD shows `DRIFTING`.
- If enabled, **electric spark particles** appear around your feet.

## 7) Combat Mechanics

### 7.1 Velocity-based bonus damage
**What it is**
- When you melee attack while momentum is above ~20%, extra damage is applied.

**How to test**
1. Spawn a zombie on flat ground.
2. Hit it once at low momentum (near 0%).
3. Build momentum to high values (≥ 60%).
4. Hit it again and compare the result.

**Expected results**
- At higher momentum, attacks should deal noticeably more total damage.

**Notes**
- The bonus can become very large at max momentum, so test with care.

### 7.2 Speed-based knockback
**What it is**
- At higher momentum, melee attacks apply extra knockback (capped).

**How to test**
1. On a flat area, hit a mob at low momentum and observe knockback distance.
2. Repeat at high momentum.

**Expected results**
- High momentum hits launch mobs significantly farther.

### 7.3 Chain lightning on melee hit
**What it is**
- A chain lightning effect can trigger on melee hits if:
  - `Charge` ≥ 30%
  - `Speed` ≥ 40%
- It jumps to additional nearby non-player living entities (default max 3 targets).

**How to test**
1. Build `Speed` to ≥ 40%.
2. Build `Charge` to ≥ 30%.
3. Spawn 3–6 mobs within ~5 blocks of each other.
4. Melee hit one mob.

**Expected results**
- Electric spark particles form a path between chained targets.
- Nearby mobs take additional damage in a chain.
- Charge decreases after the trigger.

### 7.4 AoE stun (Static Discharge)
**What it is**
- Pressing `R` consumes charge to stun nearby mobs (radius ~8 blocks).
- Requires `Charge` ≥ 50%.
- Applies strong slowness + weakness + glowing briefly, and deals a small amount of damage.

**How to test**
1. Build `Charge` to ≥ 50%.
2. Stand near several mobs.
3. Press `R`.

**Expected results**
- Mobs become **glowing**.
- Mobs are heavily slowed and weakened for a few seconds.
- Many electric spark particles appear in the area.
- You receive an actionbar message confirming the stun and count.

## 8) Defense / Utility Abilities

### 8.1 Phase Shift (block phasing)
**What it is**
- Press `V` to phase through most blocks for ~3 seconds.
- Requirements:
  - `Speed` ≥ 50%
  - cooldown ready
- It consumes some momentum on use.
- You cannot phase through **bedrock** or **barrier** blocks.

**How to test**
1. Build `Speed` to ≥ 50%.
2. Sprint toward a thick wall (3+ blocks thick is a good test).
3. Press `V` as you reach the wall.

**Expected results**
- You pass through most solid blocks while active.
- Screen gets a subtle **cyan overlay**.
- HUD shows `PHASING - Xs` while active.
- When it ends, if you’re inside blocks, the mod tries to push you to a safe nearby air space.

### 8.2 Time Dilation (visual slow-mo indicator)
**What it is**
- Press `G` to activate Time Dilation for ~5 seconds.
- Requirements:
  - `Speed` ≥ 60%
  - cooldown ready
- It consumes momentum on use.

**How to test**
1. Build `Speed` to ≥ 60%.
2. Press `G`.

**Expected results**
- You get a yellow-tinted overlay and a HUD indicator: `TIME DILATION - Xs`.
- You receive actionbar messages for activation/ending.

**Important note (current implementation)**
- At the moment, the code primarily provides **state + overlays/messages**; it does not appear to fully slow game simulation or entity AI.

## 9) Visual Effects

### 9.1 Speed vignette (high-speed edge tint)
**What it is**
- At momentum ≥ ~60%, a yellow/orange vignette appears around the screen edges.

**How to test**
1. Build `Speed` to ≥ 60%.
2. Keep sprinting.

**Expected results**
- A vignette fades in stronger as you approach max momentum.

### 9.2 FOV expansion (speed feeling)
**What it is**
- At momentum ≥ ~30%, your FOV increases with speed.

**How to test**
1. Build `Speed` above 30%.
2. Observe the camera FOV compared to standing still.

**Expected results**
- Your field of view widens noticeably as momentum rises.

### 9.3 Speed trails (particle wake)
**What it is**
- While sprinting with momentum ≥ ~50%, the mod spawns cloud particles behind you.

**How to test**
1. Sprint above 50% momentum.
2. Look behind you in third person.

**Expected results**
- A cloud-like particle wake appears behind your movement direction.

### 9.4 Drift sparks
**What it is**
- When `DRIFTING` is active, electric spark particles spawn.

**How to test**
- See the drift test in section 6.3.

## 10) Balancing / Weaknesses

### 10.1 Armorless damage penalty
**What it is**
- If you have **0 armor** and momentum is above ~80%, you take periodic damage.

**How to test**
1. Remove all armor.
2. Build `Speed` to above 80%.
3. Keep sprinting.

**Expected results**
- You take small damage periodically (about every 2 seconds).

## 11) Multiplayer / Networking sanity checks

### 11.1 Client/server sync of meters
**What it is**
- Momentum/Charge/Heat are calculated server-side and synced to the client frequently.

**How to test**
1. Build momentum and watch the HUD.
2. Use abilities and watch cooldown text.

**Expected results**
- HUD updates smoothly (minor delay is normal).
- Ability activation messages appear.

### 11.2 Ability activation packets
**How to test**
- Press `V`, `G`, `R` and confirm the server-side actionbar messages appear.

**Expected results**
- If you are missing requirements (momentum/charge/cooldown), you get a clear message.

## 12) Persistence tests (save/load)

### 12.1 Data persists across respawn / relog
**What it is**
- The component saves momentum/charge/heat and cooldowns to player NBT.

**How to test**
1. Build some non-zero `Speed`, `Charge`, and `Heat`.
2. Leave the world and rejoin.
3. Observe whether the bars return to the same values.

**Expected results**
- Momentum/charge/heat and cooldown numbers should generally persist.

**Note**
- Active ability states (like currently phasing) should not be expected to persist mid-ability.

## Troubleshooting (when something doesn’t show up)

### HUD missing entirely
- Make sure HUD isn’t hidden (don’t use F1).
- Confirm you’re actually in the client with the mod loaded.
- If you pressed `H` expecting a toggle: currently it only forces enable; it won’t “turn off” the HUD.

### Movement states never appear (WALL RUNNING / HYDROPLANING / DRIFTING)
- Check the `Speed` threshold first:
  - Wall run needs ~50% and you must be airborne near a wall.
  - Hydroplane needs ~75% and you must be sprinting on water/lava.
  - Drift needs ~60% and a sharp turn while sprinting on ground.

### Time Dilation feels like “just a tint”
- That’s currently expected: the implementation is mostly overlays/messages/state.

---