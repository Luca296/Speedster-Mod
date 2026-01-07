# Speedster-Mod Development Roadmap

## Phase 1: Project Setup
- [ ] Initialize Fabric mod project structure
- [ ] Configure build.gradle with dependencies (Fabric API, Cloth Config, Architectury)
- [ ] Create fabric.mod.json with mod metadata
- [ ] Set up Mixin configuration
- [ ] Create main mod initializer classes (client/server)

## Phase 2: Core Speed System
- [ ] Create SpeedsterData class to track player state (momentum, charge, heat)
- [ ] Implement IEntityDataSaver interface for persistent data
- [ ] Create SpeedsterDataComponent for data attachment
- [ ] Add Mixin to PlayerEntity.tickMovement() for momentum mechanics
- [ ] Implement dynamic speed scaling (acceleration over time while sprinting)
- [ ] Add momentum-based physics (velocity vectors, drift mechanics)
- [ ] Create speed tier system (thresholds for abilities)

## Phase 3: Movement Abilities
- [ ] Implement hydroplaning (water/lava running at 75%+ speed)
- [ ] Add wall running mechanics (horizontal to vertical momentum)
- [ ] Create inertia drift system (sliding on sharp turns)
- [ ] Add spark particle effects for drifting
- [ ] Implement low friction at high speeds
- [ ] Add collision detection overrides for movement abilities

## Phase 4: Combat Mechanics
- [ ] Implement velocity-based damage scaling
- [ ] Add knockback multiplier based on speed (5+ blocks at max)
- [ ] Create Static Discharge charge system
- [ ] Implement chain lightning melee attack (3 enemy chain)
- [ ] Add AoE stun ability (R key)
- [ ] Create Vortex Vacuum mechanic (circular running pulls entities)

## Phase 5: Defense & Utility Abilities
- [ ] Implement Phase Shift ability (V key) - pass through blocks
- [ ] Add phase shift cooldown and duration management
- [ ] Create Time Dilation ability (G key) - client-side slow-motion
- [ ] Implement Speed-Sense (entity detection within 25 blocks)
- [ ] Add spectral glow effect on nearby entities

## Phase 6: Balancing & Weaknesses
- [ ] Implement armor requirement (damage without armor at high speed)
- [ ] Create friction overheat system (heat meter)
- [ ] Add smoke particles at heat Stage 1
- [ ] Implement auto-ignite at heat Stage 2
- [ ] Add water cooling mechanic

## Phase 7: Visual Effects
- [ ] Create speed trail afterimages (0.3s fade)
- [ ] Implement yellow vignette effect at high speed
- [ ] Add optional motion blur shader
- [ ] Create cloud/flash particles while running
- [ ] Add electric spark particles for abilities
- [ ] Implement end-rod outline effect for phasing
- [ ] Add Speed Vision glow rendering

## Phase 8: Networking & Multiplayer
- [ ] Create custom packets for ability sync
- [ ] Implement server-authoritative movement validation
- [ ] Sync visual effects to other players
- [ ] Handle combat interactions in multiplayer
- [ ] Add proper phase shift PvP handling

## Phase 9: Configuration & Polish
- [ ] Integrate Cloth Config API
- [ ] Create config screen with all settings
- [ ] Add keybind configuration
- [ ] Implement enable/disable toggles for effects
- [ ] Add numeric value adjustments (speed caps, cooldowns)

## Phase 10: Speedster Suit (Optional)
- [ ] Design Speedster Suit armor set
- [ ] Create armor textures
- [ ] Implement crafting recipes
- [ ] Add suit-specific bonuses
- [ ] Create suit upgrade system

## Phase 11: Sound Effects (Optional)
- [ ] Add speed running sounds (wind whoosh)
- [ ] Create ability activation sounds
- [ ] Add electrical crackling for discharge
- [ ] Implement phase shift audio
- [ ] Add impact sounds for velocity damage

## Phase 12: Future - Skill Tree System
- [ ] Design skill tree UI
- [ ] Create Combat branch abilities
- [ ] Create Mobility branch abilities
- [ ] Create Electric Speedster branch abilities
- [ ] Implement skill point progression

---

## Current Progress
**Phase:** 1 - Project Setup  
**Status:** In Progress  
**Last Updated:** 2026-01-07
