# Voxy — NeoForge 1.21.1 (completed build)

Voxy is a far-distance Level-of-Detail rendering mod for Minecraft Java Edition.
This repository is a **completed build of the NeoForge 1.21.1 port** — the
upstream patch left the build pipeline half-finished (no access transformers,
no `neoforge.mods.toml`, no main `@Mod` class). This fork fills in the missing
pieces so the mod compiles, the jar builds, and the mod loads in a real
NeoForge 1.21.1 server.

## Credits

This is a fork of a fork of a fork. The lineage:

| Layer | Repo | Role |
|---|---|---|
| Original | [MCRcortex/voxy](https://github.com/MCRcortex/voxy) | Original Voxy — Fabric mod by Cortex, LoD rendering for MC |
| Backport | [m3t4f1v3/voxy](https://github.com/m3t4f1v3/voxy) | First 1.21.1 fork (`backport to 1.21.1` commit `9dbb8174`); this is what we built on top of |
| NeoForge patch | [1luik/voxy_1_21_1_neoforge](https://github.com/1luik/voxy_1_21_1_neoforge) | The patch repo — ships only `voxy_1_21_1_neoforge.patch`, no source |
| **This fork** | steimerbyte/voxy-neoforge | Applied the patch on top of `9dbb8174`, then completed the missing build artifacts |

**All credit for the mod itself goes to the original authors.** I (steimerbyte)
just finished wiring up the NeoForge build pipeline so the patch actually
produces a working mod.

Voxy is `All-Rights-Reserved` per its `neoforge.mods.toml`. This fork adds
build-pipeline glue only, no gameplay/rendering code changes.

## What this fork adds on top of the patch

The `1luik` patch ports the Java sources from Fabric to NeoForge but **deletes
the original access-widener and never replaces it**, **references a
`META-INF/neoforge.mods.toml` it never creates**, and **references a
`me.cortex.voxy.NeoVoxyMod` class that doesn't exist**. Without those pieces
the project won't compile and NeoForge won't recognise the jar as a mod.

Concretely, this fork adds:

| File | Why |
|---|---|
| `src/main/resources/META-INF/accesstransformer.cfg` | Forge-AT translations of every entry from the deleted `voxy.accesswidener` (classes, fields, methods). Without this, mixins can't reach the MC internals Voxy needs. |
| `src/main/resources/META-INF/neoforge.mods.toml` | Required mod metadata. Built from `gradle.properties` via `processResources` (placeholder values; jar shows resolved values). |
| `src/main/java/me/cortex/voxy/NeoVoxyMod.java` | Stub `@Mod("voxy")` entry-point class. The patch imports it but never ships it. Wires up the existing `VoxyClient.initVoxyClient()` and `VoxyCommands.register()`. **No server-side logic added** — the original Voxy is client-only. |
| `src/main/java/me/cortex/voxy/client/mixin/minecraft/AccessorEmptyTextureStateShard.java` | `@Invoker` mixin interface to call `cutoutTexture()` on `RenderStateShard.EmptyTextureStateShard`. Restored because making the method public via AT would break the `protected` overrides in `MultiTextureStateShard` and `TextureStateShard`. |
| `build.gradle` (patched) | `compileOnly("maven.modrinth:nvidium-neoforge:1vMc0Kcf")` is commented out — that Modrinth version hash is gone from the registry. |
| `.gitignore` | Standard Gradle/IDE + `runs/` (server world data, do not commit) |

### Changelog vs `1luik/voxy_1_21_1_neoforge` patch (as-is)

```
+  src/main/resources/META-INF/accesstransformer.cfg        (new, 30 lines)
+  src/main/resources/META-INF/neoforge.mods.toml            (new, 21 lines)
+  src/main/java/me/cortex/voxy/NeoVoxyMod.java              (new, 32 lines)
+  src/main/java/me/cortex/voxy/client/mixin/minecraft/
+      AccessorEmptyTextureStateShard.java                   (new, 17 lines)
+  .gitignore                                                (new)
M  build.gradle                                              (1 line commented out)
M  src/main/resources/client.voxy.mixins.json                (1 line added back)
M  src/main/java/.../BakedBlockEntityModel.java              (1 import, 1 call site)
```

The patch itself (32 modified source files for the Fabric→NeoForge mixin
rewrites) is applied as-is — those changes come from `1luik`.

## Build

Requires **JDK 21** and **8 GB+ RAM** (NeoGradle's `neoFormDecompile` step
loads the whole decompiled Minecraft jar in memory). On Debian/Ubuntu the
toolchain auto-resolves via `foojay-resolver-convention`.

```bash
git clone https://github.com/steimerbyte/voxy-neoforge
cd voxy-neoforge
./gradlew build -x test
```

Artifacts:

- `build/libs/voxy-0.2.7-alpha.jar` — mod-only (768 KB)
- `build/libs/voxy-0.2.7-alpha-all.jar` — shaded jar with `jarjar`-merged deps (80 MB)

Prebuilt jars are available on the [Releases](../../releases) page.

## Install

Drop `voxy-0.2.7-alpha.jar` (or the `-all` fat-jar if you don't want to chase
down the optional deps) into `<minecraft>/mods/`. Requires:

- Minecraft `1.21.1`
- NeoForge `21.1.173` (or any compatible 1.21.1 NeoForge build)
- Sodium (for the rendering path Voxy hooks into)
- Iris (optional — Voxy has Iris-shader-compat mixins)
- Lithium (optional — Voxy reads some Lithium config flags)
- Fabric API base (auto-loaded by NeoForge for some compat shims) — bundled in the `-all` jar via `jarJar`

Voxy is a **client-side** mod — you only need it installed on the client. It
works fine on a vanilla 1.21.1 NeoForge server without it (the world will just
not be pre-voxelised).

## Verified

Headless mod-load test on a 1.21.1 NeoForge server (this fork, no manual
hacking):

```
[10:32:49] Found mod file "neoforge-21.1.173.jar"
...        Found mod file "voxy-0.2.7-alpha.jar"
[10:36:01] Done (11.305s)! For help, type "help"
[10:36:01] Listening on *:25565
[10:36:01] Done loading Lithium Cached BlockState Flags are disabled!
```

The Lithium + Sodium integrations wire up cleanly; `@EventBusSubscriber`
classes are auto-registered; the world prepares; the server stays up.

## Known issues / not yet addressed

- **Vivecraft mixin fails** with `Class version 65 required is higher than the
  class version supported by the current version of Mixin (JAVA_17 supports
  class version 61)`. This is the Vivecraft dependency, not Voxy. To fix:
  either bump Mixin to a Java 21–capable version, or remove Vivecraft from
  `build.gradle` for a server-only run.
- **NeoVoxyMod is a stub.** It only registers the client-side lifecycle events
  present in the original Voxy (`FMLClientSetupEvent` → `VoxyClient.init`,
  `RegisterClientCommandsEvent` → `VoxyCommands.register`). Any common-side
  init the original Voxy did has to be re-added here.
- **The patch's `jarJar` config** is opinionated — pulls in `jedis`,
  `rocksdbjni`, `commons-pool2`, `xz`. The `-all` jar embeds all of them. If
  you don't need them, use the regular jar.

## License

Voxy itself is **All-Rights-Reserved** by its authors. The build-pipeline glue
in this fork (`NeoVoxyMod`, the AT file, the toml, the accessor interface,
this README) is released under the same terms — no rights granted beyond
personal use of the resulting mod.

If you want to redistribute, talk to the original Voxy authors first.
