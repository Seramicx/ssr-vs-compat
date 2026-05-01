# Shoulder Surfing Reloaded x Valkyrien Skies Compat

Makes [Shoulder Surfing Reloaded](https://www.curseforge.com/minecraft/mc-mods/shoulder-surfing-reloaded) and [Valkyrien Skies 2](https://www.curseforge.com/minecraft/mc-mods/valkyrien-skies) work together when you're riding a mount on a VS / Eureka ship. Forge 1.20.1.

## Background

SSR officially marks VS as incompatible. The two fight over camera control: SSR uses a decoupled camera since v3.0, VS moves entities into a separate "shipyard" dimension and rewrites the camera through `MixinGameRenderer.setupCameraWithMountedShip`, and the result is funky camera angles, camera not following the mouse, and the player + ship rendering centered instead of off to the shoulder. This fixes that.

## What it does

- Applies SSR's shoulder shift on top of VS's ship-mounted camera. VS sets the camera to a ship-mounted player-eye position, pulled back by a ship-bbox-derived distance, with ship-rotated basis vectors. SSR's standard shoulder shift gets layered on top of that, in the ship's frame, so the camera stays off-shoulder even when the ship rolls.
- Camera tracks the mouse on a ship. In SSR's decoupled mode, mouse input updates SSR's camera yaw but not `player.yRot`. VS reads `player.yRot` to compute the camera basis, so on a ship you'd be stuck looking wherever your body last rotated. This forces `player.yRot` to track SSR's mouse-driven camera yaw before VS reads it.
- Falls back to base SSR config when looking down. SSR's "center camera when looking down" feature (intended for pillar-up off-ship) trips constantly on a ship because you naturally pitch down to see ahead along the hull. When SSR collapses the offset, this falls back to the base config X/Y so the lateral shoulder shift doesn't disappear.
- Suppresses SSR's "incompatible with valkyrienskies" loading warning. This compat mod is the reason both can coexist, so the warning is misleading. Filtered out at SSR's `incompatibleMods` lookup. SSR's other warnings (nimble, customcameraview, betterthirdperson) still fire normally.

## Requirements

- Minecraft 1.20.1 + Forge 47+
- [Shoulder Surfing Reloaded](https://www.curseforge.com/minecraft/mc-mods/shoulder-surfing-reloaded) 4.22.0+
- [Valkyrien Skies 2](https://www.curseforge.com/minecraft/mc-mods/valkyrien-skies) 2.3.0+

Works on any VS-based ship, including [Eureka](https://www.curseforge.com/minecraft/mc-mods/eureka-ships-for-valkyrien-skies).

## Config

None. Off-ship behavior is untouched, so you tune SSR via its own config.

## License

MIT
