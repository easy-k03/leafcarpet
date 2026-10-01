# leafcarpet

[![Build](https://github.com/easy-k03/leafcarpet/actions/workflows/build.yml/badge.svg)](https://github.com/easy-k03/leafcarpet/actions/workflows/build.yml)

Carpet Mod for [Leaf](https://github.com/Winds-Studio/Leaf) — a Paper-compatible Minecraft server.

This plugin ports [gnembon's fabric-carpet](https://github.com/gnembon/fabric-carpet) onto Leaf as a Bukkit plugin. It keeps Carpet's command style, rule system, fake players, and many creative/technical tools, talking to server internals through Leaf/Paper NMS instead of Fabric mixins.

| | |
|---|---|
| **Version** | 1.0.0 |
| **Server** | Leaf 1.21.11 |
| **Java** | 21 |
| **Plugin name** | `CarpetPlugin` |
| **Build** | GitHub Actions (`Build` + manual `Release`) |

> This is an in-progress port. Commands, fake players, and the `/carpet` rule UI work. Many Fabric Carpet *rules* are listed for compatibility but have no gameplay effect yet, because they still need NMS hooks that Fabric implements with mixins. See [Status](#status).

## Install

1. Run a [Leaf](https://www.leafmc.one/) 1.21.11 server (Java 21).
2. Download `leafcarpet-1.0.0.jar` from the latest [GitHub Release](https://github.com/easy-k03/leafcarpet/releases). If no release exists yet, use the `leafcarpet` artifact from the latest [GitHub Actions](https://github.com/easy-k03/leafcarpet/actions/workflows/build.yml) `Build` run.
3. Drop the jar into `plugins/`.
4. Start the server. The plugin loads at `STARTUP`.

Works alongside ProtocolLib / PacketEvents. Fake-player shadow and disconnect handling is written to avoid those plugins cancelling or kicking bot connections.

## Building

**Canonical build is GitHub Actions.** Do not compile this plugin on a local machine unless you are iterating on source. The CI runners compile with Paperweight on Ubuntu. You do not need a mapped Leaf/Paper server jar in this repo.

### Build (push / PR)

Workflow: [`.github/workflows/build.yml`](.github/workflows/build.yml)

It:

1. Checks out the repo
2. Validates the Gradle wrapper
3. Sets up Temurin JDK 21
4. Caches Paperweight / Minecraft mappings
5. Runs `./gradlew build`
6. Uploads `build/libs/leafcarpet-*.jar` as the `leafcarpet` artifact

Trigger it with a push to `main`/`master`, a pull request, or **Actions → Build → Run workflow**.

### Release (manual only)

Workflow: [`.github/workflows/release.yml`](.github/workflows/release.yml)

This workflow **does not run on git push**. It only starts when you click **Actions → Release → Run workflow**.

It reuses the same JDK 21 / Gradle / Paperweight build, then:

1. Builds `leafcarpet-<version>.jar`
2. Creates a Git tag (`v1.0.0` by default)
3. Publishes a GitHub Release and attaches the jar

When running the workflow you can set:

| Input | Default | Meaning |
|---|---|---|
| `version` | Gradle version (`1.0.0`) | Jar version and `plugin.yml` version |
| `tag` | `v<version>` | Git tag / GitHub Release tag |
| `prerelease` | `false` | Mark the GitHub Release as a prerelease |
| `notes` | empty | Extra text prepended to generated release notes |

Leave `version` empty to use `1.0.0` from `build.gradle.kts`.

The first CI run downloads the Paper 1.21.11 dev bundle and can take several minutes. Later runs reuse the Paperweight cache.

The plugin is compiled against `paperDevBundle("1.21.11-R0.1-SNAPSHOT")` with Mojang mappings (`paperweight-userdev` 2.0.0-beta.24). That plugin requires Gradle 9.7.1 or newer; this repo ships the Gradle **9.8.0** wrapper. Leaf/Paper 1.21.11 is Mojang-mapped at runtime, so the plugin jar is **not** reobfuscated. Do not commit `libs/`, `.gradle/`, or `build/`.

### Local compile (contributors only)

A local `./gradlew build` is optional and **not required**. Paperweight will download Minecraft/Paper mappings on first use (JDK 21).

```text
./gradlew build
```

Output jar: `build/libs/leafcarpet-1.0.0.jar`

## Commands

Most commands follow Fabric Carpet. Permission for each one is a Carpet rule (`true`, `false`, `ops`, or a vanilla op level). Defaults are noted below.

| Command | Default access | What it does |
|---|---|---|
| `/carpet` | ops | List, change, and persist Carpet rules |
| `/player` | ops | Spawn and control fake players |
| `/log` | everyone | Subscribe to event loggers / HUD overlays |
| `/spawn` | ops | Mobcap, spawn tracking, and spawn rates |
| `/profile` | everyone | Tick health / entity performance snapshot |
| `/info` | everyone | Inspect a block |
| `/distance` | everyone | Measure distance between two points |
| `/perimeterinfo` | ops | Perimeter spawn diagnostics |
| `/draw` | ops | Draw spheres, cylinders, cuboids, … |
| `/counter` | requires `hopperCounters` | Hopper-counter readout |
| `/track` | ops | Track mob AI |

`/script` (Scarpet) and `/tick` are **not** registered in this port.

### `/carpet`

```
/carpet
/carpet list [defaults|<category>]
/carpet <rule>
/carpet <rule> <value>
/carpet setDefault <rule> <value>
/carpet removeDefault <rule>
```

Click the rule name in chat to cycle values. `setDefault` writes to `plugins/CarpetPlugin/carpet.conf` so the value survives restarts.

To freeze settings, put `locked` on its own line in `carpet.conf`.

### `/player`

Fake players are real `ServerPlayer`s on a fake network connection. They keep-alive correctly, can ride minecarts/boats without deleting the vehicle on logout, and can shadow an existing player.

```
/player <name> spawn [at <pos> facing <rot> in <dimension>] [in <gamemode>]
/player <name> kill
/player <name> shadow
/player <name> stop
/player <name> use|jump|attack|drop|dropStack|swapHands [once|continuous|interval <ticks>]
/player <name> drop [all|mainhand|offhand|<slot>]
/player <name> hotbar <1-9>
/player <name> sneak|unsneak|sprint|unsprint
/player <name> look north|south|east|west|up|down|at <pos>|<rotation>
/player <name> turn left|right|back|<rotation>
/player <name> move forward|backward|left|right
/player <name> mount [anything]
/player <name> dismount
```

Related rules:

- `commandPlayer` — who can run `/player`
- `allowSpawningOfflinePlayers` — spawn a bot in online-mode when that name has no real account (default `true`)
- `allowListingFakePlayers` — show bots on the multiplayer player list (default `false`)

### `/log`

```
/log
/log clear
/log <logger> [option]
```

Built-in loggers: `tps`, `mobcaps`, `packets`, `counter`, `tnt`, `explosions`, `projectiles`, `fallingBlocks`, `pathfinding`.

HUD loggers (`tps`, `mobcaps`, `packets`, `counter`) render in the player tab list footer. `defaultLoggers` can auto-subscribe new players (`none`, `tps`, or `mobcaps,tps`).

## Rules

Rules live in `/carpet` and, if saved, in `plugins/CarpetPlugin/carpet.conf`:

```
# plugins/CarpetPlugin/carpet.conf
language zh_cn
commandPlayer ops
antiCheatDisabled true
```

Language options: `en_us`, `zh_cn`, `zh_tw`, `fr_fr`, `es_ar`, `pt_br`.

Categories: `bugfix`, `survival`, `creative`, `experimental`, `optimization`, `feature`, `command`, `tnt`, `dispenser`, `scarpet`, `client`.

Rules that currently have a gameplay hook (Bukkit events or fake-player code):

| Rule | Default | Effect |
|---|---|---|
| `language` | `en_us` | Carpet UI language |
| `carpetCommandPermissionLevel` | `ops` | Who can run `/carpet` (set in `.conf`) |
| `commandPlayer` / `commandLog` / … | see above | Enable Carpet commands |
| `allowSpawningOfflinePlayers` | `true` | Offline-name fake players |
| `allowListingFakePlayers` | `false` | List bots in the server player sample |
| `explosionNoBlockDamage` | `false` | Explosions do not break blocks |
| `tntPrimerMomentumRemoved` | `false` | Primed TNT has no random velocity |
| `tntDoNotUpdate` | `false` | Placing TNT next to power does not auto-prime |
| `antiCheatDisabled` | `false` | Cancel "moved too quickly / flying" kicks |
| `flippinCactus` | `false` | Right-click with cactus to rotate blocks |
| `desertShrubs` | `false` | Saplings in hot biomes without water become shrubs (growth cancelled) |
| `liquidDamageDisabled` | `false` | Flowing water/lava does not break blocks |
| `persistentParrots` | `false` | Shoulder parrots ignore tiny damage |
| `stackableShulkerBoxes` | `false` | Cap dropped shulker stack size when enabled |
| `hopperCounters` | `false` | Hoppers pointing into wool count items (Paper hopper search event) |
| `carpets` | `false` | Placing coloured carpet on wool interacts with hopper counters |
| `lightningKillsDropsFix` | `false` | Lightning does not destroy freshly dropped items |

Everything else in `/carpet list` is carried over from Fabric Carpet (push limit, optimized TNT, renewable coral, creative no-clip, …) so extensions and configs can still *name* those rules. **Toggling them does nothing until the corresponding NMS hook is ported.**

## Status

Implemented:

- Plugin lifecycle on Leaf (`CarpetPlugin`, command registration, tick/HUD loop)
- Settings manager + `/carpet` + `carpet.conf`
- Fake players (`/player`, shadow, keep-alive, ProtocolLib/PacketEvents kick bypass, dismount-before-quit so carts/boats survive)
- Technical commands: `/log`, `/spawn`, `/profile`, `/info`, `/distance`, `/perimeterinfo`, `/draw`, `/track`
- A handful of rules via Bukkit/Paper events (table above)
- Translations for `en_us`, `zh_cn`, `zh_tw`, `fr_fr`, `es_ar`, `pt_br`
- CI build with Paperweight (no local server jars)

Not ported yet (code may exist, but it is unused, skipped, or has no NMS injection):

- Scarpet (`/script` is not registered)
- `/tick`
- Mixin-level rules: optimized TNT, fast redstone, quasi-connectivity, movable TE, spawn-rule changes, structure-block limits, …
- Carpet client extras (`fogOff`, structure-block outline distance, …)

Contributions that wire a listed rule to Leaf NMS or a Paper event are welcome.

## Credits

- [fabric-carpet](https://github.com/gnembon/fabric-carpet) by gnembon and contributors — original mod, commands, and rule design
- [Leaf](https://github.com/Winds-Studio/Leaf) — Paper fork this plugin targets
- [Paperweight](https://docs.papermc.io/paper/dev/userdev/) — CI compile against Paper NMS
