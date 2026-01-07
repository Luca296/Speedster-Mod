# Speedster-Mod – Minecraft Mod (1.21.1)

A high‑speed movement and combat overhaul that turns the player into a physics‑breaking speedster.  
Designed for **Minecraft Java Edition 1.21.1** using **Fabric**.

This mod focuses on *momentum‑based movement*, *kinetic combat*, *time dilation*, and *stylized visual effects* to deliver a superhero‑level gameplay experience.

---

## Features Overview

### **Core Mechanics**
- **Dynamic Speed Scaling**  
  Speed increases the longer you sprint, building momentum instead of instantly reaching max velocity.

- **Momentum‑Based Physics**  
  Custom velocity vectors, drift mechanics, and low friction at high speeds.

- **Hydroplaning**  
  Run across water or lava when above 75% max speed.

- **Wall Running**  
  Convert horizontal momentum into vertical movement when sprinting into walls.

- **Inertia Drift**  
  Slide when turning too sharply at high speeds, with spark effects and reduced friction.

---

## Combat Mechanics

### **Velocity‑Based Damage**
Damage and knockback scale with your current speed.  
At max velocity, enemies can be launched 5+ blocks.

### **Static Discharge**
- **Passive:** Running builds electrical charge.  
- **Charged Hit:** Next melee attack chains lightning to 3 enemies.  
- **Active (R):** Release charge in a small AoE stun.

### **Vortex Vacuum**
Running in tight circles creates a suction vortex that pulls mobs and items inward.

---

## Defense & Utility

### **Phase Shift (V)**
Temporarily phase through solid blocks by vibrating your molecules.  
Ends safely by teleporting upward if stuck.

### **Speed‑Sense (Time Dilation) (G)**
Slows nearby mobs and projectiles (client‑side illusion).  
Simulates heightened reflexes without altering tick rate.

---

## Visual Effects

### **Speed Trails**
- Afterimage “ghosts” appear behind the player at high speeds.
- Fade out over 0.3 seconds.
- Client‑side only for performance.

### **Speed Vision**
- Entities within 25 blocks gain the **Spectral Arrow glowing effect**.
- Effect is removed instantly when leaving range.
- Helps track threats while moving fast.

### **Vignette & Motion Blur**
- Yellow‑tinted vignette at high speeds.
- Optional shader‑based motion blur.

### **Particles**
- Running: cloud/flash particles  
- Drifting: electric sparks  
- Phasing: end‑rod ghost outline  

---

## Controls & Keybinds

| Action                | Default Key            | Type        | Description |
|-----------------------|------------------------|-------------|-------------|
| Toggle Speedster Mode | Left Shift (Hold 3s)   | Toggle      | Enables/disables all abilities. |
| Phase Shift           | V                      | Instant     | Pass through blocks. |
| Time Dilation         | G                      | Toggle/Hold | Slow‑motion perception. |
| Static Discharge      | R                      | Instant     | Release stored electrical charge. |

All keybinds are configurable.

---

## Balancing & Weaknesses

### **Armor Requirement**
Running at high speeds without armor deals **1 damage every 2 seconds**.  
Encourages wearing a Speedster Suit or at least 2 armor pieces.

### **Friction Overheat**
Maintaining max speed for too long builds heat:
- Stage 1: Smoke trail  
- Stage 2: Player ignites  
Cool down by stopping or touching water.

---

## Technical Implementation

### Tech Stack
- **Java 21**  
- **Fabric Loader**
- **Fabric API / Architectury**  
- **Fabric events** for client tick visuals/particles  
- **Mixins** for movement, rendering, and collision logic  
- **Cloth Config API** for in‑game settings  
- **Custom networking packets** for syncing visuals and combat logic  

### Key Systems Modified
- `PlayerEntity.tickMovement()`  
- Collision handling  
- Client render loop  
- Attribute modifiers  
- Custom capability for charge/heat meters  

---

## Future Plans

### **Skill Tree System**
A full RPG‑style progression system with three branches:

#### Combat Speedster
- Stronger kinetic punches  
- Longer lightning chains  
- Dash attacks  

#### Mobility Speedster
- Faster acceleration  
- Longer wall runs  
- Enhanced hydroplaning  

#### Electric Speedster
- Lightning aura  
- Electric dashes  
- EMP‑style mob stuns  

This feature requires custom UI, persistent data storage, and unlock logic — planned for a later update.

---

## Contributing
Pull requests and feature suggestions are welcome.

---

## Contact
Created by **Luca296**.  
Feedback, ideas, and collaboration are encouraged.

**Note:** I'm still a relatively young student, so development might slow down sometimes when school gets busy.