# FlimsyXMods Auto Grindstone

Client-side Fabric mod for Minecraft **1.21.11** with Fabric Loader **0.19.3**.

## What it does

- Adds an `AUTO` / `STOP` button to the grindstone screen.
- Press `G` while a grindstone is open to start or stop automation.
- Scans the complete player inventory for enchanted items.
- Uses Minecraft's enchantment component APIs, so registered modded/custom enchantments are included instead of filtering only vanilla enchantment IDs.
- Moves each matching stack into the grindstone, collects the output, and continues until no matching inventory items remain.
- Waits for the server to acknowledge each grindstone result when TPS sync is enabled.

The mod intentionally does not bypass server-side inventory rules: it uses the same screen click packets as a player, so it is compatible with normal multiplayer server validation.

## Configuration

On first launch, the mod creates:

```text
config/auto_grindstone.json
```

```json
{
  "actionsPerSecond": 4,
  "syncToServerTps": true,
  "includeEnchantedBooks": true
}
```

- `actionsPerSecond`: requested automation pace from 1 to 20.
- `syncToServerTps`: waits for server output before sending the next grindstone action. Keep this enabled for multiplayer.
- `includeEnchantedBooks`: also process enchanted books.

## Build

Maven is the user-facing build command:

```bash
cd minecraft-mod
mvn package
```

The Maven build invokes the Fabric Loom build and produces the remapped mod jar in `build/libs/`.

## Identity

Mod author and project identity: **FlimsyXMods**.