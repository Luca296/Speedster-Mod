# Speedster-Mod Development Roadmap

## Phase 1: Project Setup
- [x] Initialize Fabric mod project structure
- [x] Configure build.gradle with dependencies (Fabric API, Cloth Config, Architectury)
- [x] Create fabric.mod.json with mod metadata
- [x] Set up Mixin configuration
- [x] Create main mod initializer classes (client/server)

## Phase 2: Core Speed System
- [x] Create SpeedsterData class to track player state (momentum, charge, heat)
- [x] Implement IEntityDataSaver interface for persistent data
- [x] Create SpeedsterDataComponent for data attachment
- [x] Add Mixin to PlayerEntity.tickMovement() for momentum mechanics
- [x] Implement dynamic speed scaling (acceleration over time while sprinting)
- [x] Add momentum-based physics (velocity vectors, drift mechanics)
- [x] Create speed tier system (thresholds for abilities)

## Phase 3: Movement Abilities
- [x] Implement hydroplaning (water/lava running at 75%+ speed)
- [x] Add wall running mechanics (horizontal to vertical momentum)
- [x] Create inertia drift system (sliding on sharp turns)
- [x] Add spark particle effects for drifting
- [x] Implement low friction at high speeds
- [ ] Add collision detection overrides for movement abilities

## Phase 4: Combat Mechanics
- [x] Implement velocity-based damage scaling
- [x] Add knockback multiplier based on speed (5+ blocks at max)
- [x] Create Static Discharge charge system
- [x] Implement chain lightning melee attack (3 enemy chain)
- [x] Add AoE stun ability (R key)
- [ ] Create Vortex Vacuum mechanic (circular running pulls entities)

## Phase 5: Defense & Utility Abilities
- [x] Implement Phase Shift ability (V key) - pass through blocks
- [x] Add phase shift cooldown and duration management
- [x] Create Time Dilation ability (G key) - client-side slow-motion
- [ ] Implement Speed-Sense (entity detection within 25 blocks)
- [ ] Add spectral glow effect on nearby entities

## Phase 6: Balancing & Weaknesses
- [x] Implement armor requirement (damage without armor at high speed)
- [x] Create friction overheat system (heat meter)
- [x] Add smoke particles at heat Stage 1
- [x] Implement auto-ignite at heat Stage 2
- [x] Add water cooling mechanic

## Phase 7: Visual Effects
- [x] Create speed trail afterimages (0.3s fade)
- [x] Implement yellow vignette effect at high speed
- [ ] Add optional motion blur shader
- [x] Create cloud/flash particles while running
- [x] Add electric spark particles for abilities
- [ ] Implement end-rod outline effect for phasing
- [ ] Add Speed Vision glow rendering

## Phase 8: Networking & Multiplayer (Low Priority)
- [x] Create custom packets for ability sync
- [ ] Implement server-authoritative movement validation
- [ ] Sync visual effects to other players
- [ ] Handle combat interactions in multiplayer
- [ ] Add proper phase shift PvP handling

## Phase 9: Configuration & Polish
- [x] Integrate Cloth Config API
- [x] Create config screen with all settings
- [x] Add keybind configuration
- [x] Implement enable/disable toggles for effects
- [x] Add numeric value adjustments (speed caps, cooldowns)

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
**Phase:** 3-7 - Core Systems Complete  
**Status:** Ready for Testing  
**Last Updated:** 2026-01-07
