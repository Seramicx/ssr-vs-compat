# Shoulder Surfing Reloaded x Valkyrien Skies Compat

Makes [Shoulder Surfing Reloaded](https://www.curseforge.com/minecraft/mc-mods/shoulder-surfing-reloaded) and [Valkyrien Skies 2](https://www.curseforge.com/minecraft/mc-mods/valkyrien-skies) actually work together when you're riding a mount on a VS / Eureka ship. Forge 1.20.1.

SSR officially marks VS as incompatible. The two fight over camera control: SSR's decoupled camera and VS's ship-mounted camera each try to take over, and the result is funky angles, the camera not following your mouse, and the player rendering centered instead of off the shoulder. This patches over all of that.

## Features

- Adds SSR's shoulder offset on top of VS's ship-mounted camera. The camera stays off-shoulder when riding a mount on a ship, even when the ship rolls or pitches.
- Camera follows your mouse on a ship. Without this fix, you'd be stuck looking wherever your body was last facing because VS reads `player.yRot` for the camera basis.
- Falls back to the base SSR config when looking down. SSR's "center camera when looking down" feature is intended for off-ship pillar climbing, but it constantly trips on a ship because you naturally pitch the camera down to see ahead along the hull. This keeps your shoulder shift visible even when looking down on deck.
- Suppresses SSR's "incompatible with valkyrienskies" loading warning, since this mod is the reason both can coexist. SSR's other warnings (nimble, customcameraview, betterthirdperson) still fire normally.
- Off-ship behavior is untouched.

## Config

None. SSR's own settings still apply, and VS doesn't need configuring for this to work.

## Requires

- Minecraft 1.20.1
- Forge 47+
- Shoulder Surfing Reloaded 4.22.0+
- Valkyrien Skies 2 2.3.0+

Works with [Eureka](https://www.curseforge.com/minecraft/mc-mods/eureka-ships-for-valkyrien-skies) and any other VS-based ship mod.

## Manual install

1. Install Forge 47+ for Minecraft 1.20.1.
2. Install Shoulder Surfing Reloaded and Valkyrien Skies 2.
3. Download the jar from the [latest release](https://github.com/Seramicx/ssr-vs-compat/releases/latest).
4. Drop it into your `.minecraft/mods/` folder.

## License

MIT
