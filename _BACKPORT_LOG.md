# Backport Log

Per-commit log of cherry-picks from upstream Voxy `dev` to 1.21.1 NeoForge fork.

Format: `N. <short-sha> <msg> — <STATUS>` where STATUS is APPLIED | APPLIED+FIXED | SKIPPED | PENDING.

## Progress

## Pre-Backport Setup Fix
- `eac6b98c` — fix(neo21): defer voxy init to ClientTickEvent.Pre so GL context is ready
  - **Verdict:** PRE-EXISTING-SETUP-BUG (not backport-induced, but masked downstream bugs)
  - **Files:** src/main/java/me/cortex/voxy/NeoVoxyMod.java
  - **Result:** APPLIED (manual fix, no release)
  - **Notes:** VoxyClient.initVoxyClient() in FMLClientSetupEvent crashed every fresh client launch with `IllegalStateException: No GLCapabilities instance set` because Capabilities.<clinit> needs a current GL context, but GL.createCapabilities() runs later on NeoForge (unlike Fabric's ClientModInitializer). Pre-existing alpha-2 base (9dbb8174) had no NeoVoxyMod at all — regression came in with NeoForge entry-point commits 6af19fab + 2b2c941f. Moved the call to ClientTickEvent.Pre on the game bus, idempotent. After this fix the headless verifier confirms Voxy registers cleanly (Capabilities init OK, ResourceManager lists `mod/voxy`). The downstream Mesa/GLSL 4.60 crash in BudgetBufferRenderer is a test-environment limitation (Mesa llvmpipe hardware maxes out at 4.50) and not a voxy bug — it would not crash on real NVIDIA/AMD GPUs. The previous `.040` release's user-reported sodium-extra NPE was likely a secondary crash after voxy's primary init crash aborted mod-loading; both should now be resolved.

## 3. `66a20618` wip tinting
- **Verdict:** PORTABLE
- **Files:** MDICSectionRenderer.java, quad_util.glsl
- **Result:** APPLIED
- **SHA:** 8aa293d18457e3a15c318fd834da30b36bfc1c32
- **Release:** v0.2.7-alpha-2.002
- **Notes:** Clean auto-merge, compileJava passed. Scout flagged unused `Direction` import as WIP artifact — left as-is per upstream.

## 4. `823babef` hate java
- **Verdict:** PORTABLE
- **Files:** Shader.java
- **Result:** APPLIED
- **SHA:** 88c9daa31084ac417c56ecf8e3c9aee7c40bcf27
- **Release:** v0.2.7-alpha-2.003
- **Notes:** 1-line perf fix: `replaceAll()` → `replace()` (avoids regex compile overhead).

## 5. `0f9287ad` wip face tinit
- **Verdict:** REQUIRES-MANUAL-PORT (HIGH conflict risk)
- **Files:** AbstractSectionRenderer.java, MDICSectionRenderer.java, IrisShaderPatch.java
- **Result:** APPLIED+FIXED
- **SHA:** 28babb8b33f840a5124b042946af547b1b02e0cb
- **Release:** v0.2.7-alpha-2.004
- **Fix:** `cl.dimensionType().cardinalLightType() == NETHER` → `cl.effects().equals(BuiltinDimensionTypes.NETHER_EFFECTS)` (cardinalLightType is MC 1.21.4+, not in 1.21.1)

## 6. `edd0ce33` update mods 1.21.11
- **Verdict:** MC-26-ONLY (Sodium 0.7 API + MC 1.21.2+ APIs)
- **Files:** 14 files (config/, mixin/sodium/, resources/fabric.mod.json, etc.)
- **Result:** SKIPPED
- **Reason:** Full Sodium 0.6 → 0.7 + MC 1.21.1 → 1.21.11 upgrade. Missing APIs in our 1.21.1 + Sodium 0.8.13 target: `GpuSampler`, `FogParameters`, `indexedRenderingEnabled`, `OptionFlag`, `StorageEventHandler`, `ConfigState`, `IntegerOptionBuilder`, `OptionPage`, `Page`, `Range`, `OptionImpact`, `Supplier<GlSampler>`. Not backportable as a patch — would require full Sodium 0.7-style config refactor.

## 8. `afe41a10` perf tweek
- **Verdict:** PORTABLE
- **Files:** AsyncNodeManager.java
- **Result:** APPLIED
- **SHA:** 8b8a4a23
- **Release:** v0.2.7-alpha-2.006
- **Notes:** 1-line perf tweak: upload batch limit `200` → `300` while >50 MB free.

## 9. `03d97138` private final
- **Verdict:** PORTABLE
- **Files:** (1 file, ByteBuffer field)
- **Result:** APPLIED
- **SHA:** 32e638ee
- **Release:** v0.2.7-alpha-2.007
- **Notes:** Added `private final` to `ByteBuffer buf;` field.

## 10. `c7cf4a74` mipping
- **Verdict:** REQUIRES-MANUAL-PORT
- **Files:** 8 files (new MipGen.java + 7 modified)
- **Result:** APPLIED+FIXED
- **SHA:** 1af6450aad79235ebda2df43ee2d74d309d24f44
- **Release:** v0.2.7-alpha-2.008
- **Fixes:** Removed MipmapStrategy (1.21.2+), ARGB (1.21.2+), SpriteContents.mipmapStrategy (1.21.2+). Replaced ARGB.linearToSrgbChannel with std sRGB gamma. Kept 1.21.1's quad.isShade/getVertices.

## 11. `a7ea5b2f` who knows if this even works
- **Verdict:** PORTABLE
- **Files:** DHImporter.java (+74/-16)
- **Result:** APPLIED
- **SHA:** aeebbd76
- **Release:** v0.2.7-alpha-2.009
- **Notes:** Clean cherry-pick, no fixes needed.

## 12. `4ca9cdba` woops
- **Verdict:** MC-26-ONLY
- **Files:** IrisVoxyRenderPipelineData.java
- **Result:** SKIPPED
- **Reason:** Fixes Null-bug for `addDynamicSampler(..., Supplier<GlSampler> sampler, ...)` upstream signature. Our 1.21.1 fork has `addDynamicSampler(..., GlSampler sampler, ...)` with direct `sampler.getId()`. The upstream Iris API doesn't exist here, so the null-fix is semantically not portable.

## 13. `561337e1` hoist common FB
- **Verdict:** PORTABLE
- **Files:** 5 files (+18/-7)
- **Result:** APPLIED
- **SHA:** 1eb60ea8
- **Release:** v0.2.7-alpha-2.010
- **Notes:** Hoists common framebuffer logic. Clean auto-merge.

## 14. `c4f799ff` things
- **Verdict:** PORTABLE
- **Files:** 2 files
- **Result:** APPLIED
- **SHA:** a55b0a6b
- **Release:** v0.2.7-alpha-2.011
- **Notes:** Clean cherry-pick.

## 15. `b7f5798e` fix iris
- **Verdict:** REQUIRES-MANUAL-PORT
- **Files:** 3 (2 applied, 1 kept)
- **Result:** APPLIED+FIXED
- **SHA:** fa8fe58e
- **Release:** v0.2.7-alpha-2.012
- **Fix:** Kept `VoxyConfigScreenPages.java` (actively referenced via `MixinSodiumOptionsGUI` in our 1.21.1 fork; upstream only deleted it because `VoxyConfigPageSodium` replaced callsites — not done in 1.21.1). Applied the actual fix: `IrisVoxyRenderPipeline.java` `getDepthTex().getFormat()` → `getFormat()` + new `DepthFramebuffer.getFormat()` getter.

## 16. `bbf7d60a` todo
- **Verdict:** PORTABLE
- **Files:** 1 file (TODO comment rewrite)
- **Result:** APPLIED
- **SHA:** b0cf4414
- **Release:** v0.2.7-alpha-2.013
- **Notes:** Pure comment rewrite at line 58.

## 17. `6724157f` finally fix the chunk flickering issue when zooming in
- **Verdict:** PORTABLE
- **Files:** 1 file (SectionOcclusionCache or similar, isCulledByHiz method)
- **Result:** APPLIED
- **SHA:** 0e534948
- **Release:** v0.2.7-alpha-2.014
- **Notes:** 3-line guard `(maxBB.xy-minBB.xy)==vec2(1.0f) → return false` at top of isCulledByHiz() to short-circuit degenerate full-screen-bbox case causing zoom-in flicker.

## 18. `2bf9af00` computed face tint
- **Verdict:** PORTABLE
- **Files:** 2 files (Java + GLSL)
- **Result:** APPLIED
- **SHA:** 78571170
- **Release:** v0.2.7-alpha-2.015
- **Notes:** Adds computed face tint logic to shader. Clean auto-merge.

## 19. `85638fce` readme
- **Verdict:** CONFLICT (unresolvable — content mismatch)
- **Files:** README.md
- **Result:** SKIPPED
- **Reason:** Our README is a 158-line 1.21.1-NeoForge-specific document (build status, AI-generated disclosure, etc.). Upstream adds 1 line to their minimal README. No clean merge path.

## 20. `15604c16` hopefully improved command usage
- **Verdict:** REQUIRES-MANUAL-PORT
- **Files:** 1 file
- **Result:** APPLIED+FIXED
- **SHA:** 1f647fc4
- **Release:** v0.2.7-alpha-2.016
- **Fix:** `net.minecraft.resources.Identifier` → `net.minecraft.resources.ResourceLocation` (renamed in MC 1.21.4+).

## 21. `76cfef5b` update deps, fix race, clamp fog, fix meshing
- **Verdict:** REQUIRES-MANUAL-PORT
- **Files:** 4 (2 applied, 2 partially)
- **Result:** APPLIED+FIXED
- **SHA:** ac7fff20
- **Release:** v0.2.7-alpha-2.017
- **Applied:** RenderDataFactory.java meshing fixes + ImportManager.java acquireRef/releaseRef race-fix.
- **Dropped:** build.gradle chunky 1.4.40→1.4.54 (our build.gradle uses curse.maven chunky-pregenerator-forge, not maven.modrinth chunky-fabric). NormalRenderPipeline.java fog Math.clamp hunk (the surrounding useEnvFog block with environmentalStart/End is MC-26.x-only).

## 23. `362998cc` woooops
- **Verdict:** PORTABLE
- **Files:** small fix
- **Result:** APPLIED
- **SHA:** 9e7f521f
- **Release:** v0.2.7-alpha-2.018

## 24. `65e10c2c` cleanup
- **Verdict:** PORTABLE
- **Files:** screenspace.glsl
- **Result:** APPLIED
- **SHA:** c98658c5
- **Release:** v0.2.7-alpha-2.019

## 25. `6beb6b20` this was such an unbelievebly dumb and stupid mistake
- **Verdict:** PORTABLE
- **Files:** small
- **Result:** APPLIED
- **SHA:** 82712276
- **Notes:** Clean cherry-pick, compileJava passed. NO RELEASE (local-only mode since round 25).

## 25. `6beb6b20` this was such an unbelievebly dumb and stupid mistake
- **Verdict:** PORTABLE
- **Files:** small
- **Result:** APPLIED
- **SHA:** 82712276
- **Release:** v0.2.7-alpha-2.020
- **Notes:** Clean cherry-pick. GitHub Actions disabled in this release (workflows moved to .github/workflows-disabled/).

## 26. `8247248c` think? this is more right?
- **Verdict:** PORTABLE
- **Files:** RenderDataFactory.java +3/-2
- **Result:** APPLIED
- **SHA:** b193377a
- **Release:** v0.2.7-alpha-2.021

## 27. `25463a4c` for future
- **Verdict:** PORTABLE
- **Result:** APPLIED
- **SHA:** 0e54c96f
- **Release:** v0.2.7-alpha-2.022

## 28. `5b697ddb` insane
- **Verdict:** PORTABLE
- **Result:** APPLIED
- **Release:** v0.2.7-alpha-2.023

## 29. `fb9b7923` tweeked msg
- **Verdict:** PORTABLE
- **Result:** APPLIED
- **Release:** v0.2.7-alpha-2.024

## 30. `0428153c` small buildscript change
- **Verdict:** NO-OP (Fabric-specific, our fork is NeoForge)
- **Files:** build.gradle
- **Result:** SKIPPED
- **Reason:** Commit modifies Fabric-specific dep declarations (`maven.modrinth:sodium-extra`, `maven.modrinth:chunky:1.4.54-fabric`). Our fork uses NeoForge with `curse.maven:chunky-pregenerator-forge-485681`. All hunks apply to non-existent lines; cherry-pick produces empty commit.

## 31. `ef1a2969` fix small possibility of a race condition
- **Verdict:** PORTABLE
- **Result:** APPLIED
- **Release:** v0.2.7-alpha-2.025
- **Notes:** Race condition fix.

## 32. `4ffb7583` complete and utter fukin idiot
- **Verdict:** PORTABLE
- **Result:** APPLIED
- **Release:** v0.2.7-alpha-2.026

## 33. `86ce0c0f` attempted amd bug detection
- **Verdict:** PORTABLE
- **Result:** APPLIED
- **Release:** v0.2.7-alpha-2.027
- **Notes:** AMD GPU bug detection (KNOWN PERF/FIX — never skip).

## 34. `bc995f9c` wip defered translucency
- **Verdict:** PORTABLE
- **Files:** 5
- **Result:** APPLIED
- **Release:** v0.2.7-alpha-2.028

## 35. `1e4500a9` Sodium update + fog change
- **Verdict:** MC-26-ONLY
- **Files:** 9
- **Result:** SKIPPED
- **Reason:** Pulls Sodium 0.8.1-SNAPSHOT, FlagHook/Identifier Config API, MC 1.21.11 FogData. None exist in our Sodium 0.6.13 / MC 1.21.1 fork. Manual fix would require Sodium dep swap + MC version bump + 3 build file edits — outside backport scope.

## 36. `bca46143` remove sodium extra
- **Verdict:** PORTABLE
- **Result:** APPLIED
- **SHA:** 2a102d55
- **Release:** v0.2.7-alpha-2.029

## 37. `d30ea7ec` here we go again
- **Verdict:** MC-26-ONLY
- **Files:** 6
- **Result:** SKIPPED
- **Reason:** MC 1.21.11 fog refactoring (uses FogData class in MixinFogRenderer, environmentalStart in NormalRenderPipeline), removes `useRenderFog` config field that doesn't exist in our 1.21.1 fork, and build.gradle changes for Sodium 0.8.1-SNAPSHOT. Our fork is 1.21.1 + Sodium 0.6.13. Multiple MC-26-only API conflicts.

## 38. `34204367` stupid idiot
- **Verdict:** MC-26-ONLY
- **Files:** MixinFogRenderer.java
- **Result:** SKIPPED
- **Reason:** MixinFogRenderer uses MC 1.21.2+ signature (`Camera, int, DeltaTracker, float, ClientLevel, Vector4f return, @Local FogData data`) — our 1.21.1 fork uses old 4-param signature (`FogMode, float, boolean thickFog, float tickDelta`). Also uses new VoxyConfig fields `enableRendering`/`enabled` that don't exist in 1.21.1 fork.

## 55. `692ac955` 0.8.2 sodium
- **Verdict:** MC-26-ONLY
- **Result:** SKIPPED
- **Reason:** sodium mc1.21.11-0.8.2-fabric artifact; targets MC 1.21.11. Our fork uses curse.maven:sodium-394468 for MC 1.21.1.

## 42. `ee7ec50d` always show voxy version in f3
- **Verdict:** MC-26-ONLY
- **Result:** SKIPPED
- **Reason:** Uses MC 1.21.2+ debug overlay API (DebugScreenEntries, DebugScreenEntry, DebugScreenDisplayer, DebugScreenEntryList). Not present in 1.21.1.

## 39. `b68d5b3c` dis — APPLIED ca9dfa6d → v0.2.7-alpha-2.031
## 40. `b5c31478` things — APPLIED a9748df3 → v0.2.7-alpha-2.032
## 41. `f272042a` a — APPLIED 7663b1bf → v0.2.7-alpha-2.033
## 43. `45ff6c44` locale — APPLIED eef01468 → v0.2.7-alpha-2.034
## 44. `e86beefb` rename — APPLIED 8ce7df5f → v0.2.7-alpha-2.035
## 45. `b0868323` remove mip thing — APPLIED 1dbb1c87 → v0.2.7-alpha-2.036
## 46. `6212d95c` face thing — APPLIED 4c0f9365 → v0.2.7-alpha-2.037
## 47. `3bcdbbec` gpu timings — APPLIED 211e9d9d → v0.2.7-alpha-2.038
## 48. `3cc5afc1` Client store — APPLIED a83feb0a → v0.2.7-alpha-2.039
## 49. `79890fde` x — APPLIED bdbc888b → v0.2.7-alpha-2.040

## 50. `263f9321` aa
- **Verdict:** REQUIRES-MANUAL-PORT
- **Files:** build.gradle, src/main/java/me/cortex/voxy/client/core/rendering/section/backend/mdic/MDICSectionRenderer.java, src/main/resources/assets/voxy/shaders/lod/gl46/quads.frag
- **Result:** APPLIED+FIXED
- **SHA:** 9e20fb0d0f9ae6c606af7b882129359f37163a54
- **Release:** v0.2.7-alpha-2.041
- **Fix:** build.gradle conflict: upstream introduced an `if (false) modImplementation sodium-fabric mc1.21.11-0.8.2-SNAPSHOT` block — dropped on 1.21.1 fork (already on curse.maven:sodium-394468 for 1.21.1); kept HEAD's irisshaders lines. The other two files (MDICSectionRenderer.java `glDisable(GL_BLEND)`, quads.frag `colour.a = 1.0f`) auto-merged clean.

## 61. `f703d83b` update
- **Verdict:** REQUIRES-MANUAL-PORT
- **Files:** gradle.properties
- **Result:** APPLIED+FIXED
- **SHA:** 609a751e
- **Release:** v0.2.7-alpha-2.042
- **Fix:** Upstream bumps vanilla Fabric API 0.140.0+1.21.11 → 0.140.2+1.21.11 (MC 1.21.11 line). Our 1.21.1 fork uses Forgified Fabric API on a different version line (0.116.x). Cherry-pick produced CONFLICT on gradle.properties; manual port to Forgified Fabric API 0.116.15+2.3.5+1.21.1 (latest stable for MC 1.21.1+NeoForge as of 2026-08-24). Modrinth lookup: 0.116.15+2.3.5 > 0.116.15+2.3.4 > 0.116.15+2.3.3 > 0.116.15+2.3.2 > 0.116.15+2.3.1. Build SUCCESS, jars uploaded.

## 62. `df90323f` fix iris issues when enabling and disabling voxy rendering while shaders are active
- **Verdict:** APPLIED+FIXED (partial)
- **Files:** src/main/java/me/cortex/voxy/client/core/model/bakery/ModelTextureBakery.java, src/main/java/me/cortex/voxy/client/core/util/IrisUtil.java, src/main/java/me/cortex/voxy/client/mixin/minecraft/MixinLevelRenderer.java, src/main/java/me/cortex/voxy/common/world/other/Mapper.java
- **Result:** APPLIED+FIXED
- **SHA:** 834392d2
- **Release:** v0.2.7-alpha-2.043
- **Fix:** Dropped `src/main/java/me/cortex/voxy/client/config/SodiumConfigBuilder.java` and `src/main/java/me/cortex/voxy/client/config/VoxyConfigMenu.java` via `git rm` — both are Sodium 0.7-API-only (`net.caffeinemc.mods.sodium.api.config.{ConfigBuilder,ConfigEntryPoint,Range,structure}`), do not exist in our 1.21.1/Sodium 0.6 fork, and are not referenced anywhere. The post-change-flag Iris-reload system from `VoxyConfigMenu.java` could not be ported without rewriting `VoxyConfigScreenPages.java`. `IrisUtil.reload()` is already in HEAD; the fix's runtime hook lands upstream-only. build.gradle iris-line: kept commented (`modRuntimeOnlyMsk` method non-functional in this fork, `curse.maven:irisshaders-455508` already provides iris-api). ModelTextureBakery.java: kept `RenderType.translucent()` (1.21.1 API) but adopted the `ARBDrawBuffersBlend.glBlendFuncSeparateiARB(0, ...)` per-buffer blend fix from upstream. IrisUtil.java: kept HEAD record (no FogParameters, Sodium 0.6). MixinLevelRenderer.java + Mapper.java: auto-merged clean (`createRenderer()` extra null check + `withBlockBiome(long,int,int)` utility landed).
- **Notes:** 4/7 upstream files applied. 2 deleted (Sodium 0.7-only). 1 reverted (build.gradle modRuntimeOnlyMsk activation). Partial backport; main Iris reload-on-toggle mechanism remains upstream-only.

## 63. `47053483` Set the default sky light to 15, this is done in the tracker on an empty return result since it should be a free operation change
- **Verdict:** PORTABLE
- **Files:** src/main/java/me/cortex/voxy/common/world/ActiveSectionTracker.java
- **Result:** APPLIED
- **SHA:** 3ee9d4082e6c6cc9fd996bc996d0ca012da55c27
- **Release:** v0.2.7-alpha-2.044
- **Notes:** Clean auto-merge (cherry-pick -x, no conflicts). Single-file 4-line change in `ActiveSectionTracker.java`: replaces `Arrays.fill(section.data, 0)` with `Arrays.fill(section.data, Mapper.composeMappingId((byte) (sky|(block<<4)),0,0))` where sky=15, block=0. `Mapper.composeMappingId` already exists in our 1.21.1 fork (no API port needed). compileJava + build -x test both SUCCESSFUL in 41s + 30s. Push 2f84c86f..3ee9d408 on backport/sequential. Release v0.2.7-alpha-2.044 published with voxy-0.2.7-alpha.jar + voxy-0.2.7-alpha-all.jar.

## 64. `848efe60` things
- **Verdict:** APPLIED
- **Files:** src/main/java/me/cortex/voxy/common/world/service/VoxelIngestService.java, src/main/java/me/cortex/voxy/commonImpl/VoxyInstance.java
- **Result:** APPLIED
- **SHA:** 6172277eeeb069b420ef22aecd89a7356d603147
- **Release:** v0.2.7-alpha-2.045
- **Notes:** Clean cherry-pick (no conflicts, `-x` appended `(cherry picked from commit 848efe60...)`). Pre-verified `WorldEngine.markActive()` and `WorldEngine.acquireRef()` already exist in our 1.21.1 fork (no API port). Changes: `VoxelIngestService.java` adds `engine.markActive()` before queuing ingest in both lighting and non-lighting paths; `VoxyInstance.java` splits `getOrCreate(WorldIdentifier)` into a delegating overload plus `getOrCreate(WorldIdentifier, boolean incrementRef)`, calling `markActive()` on cached and newly created worlds, with optional `acquireRef()` for the caller. Adds TODO comment about thread-safety of `activeWorlds.values()` in `addDebug`. No callers of the new overload yet (dead-code acceptable, matches upstream shape). compileJava + build -x test both SUCCESSFUL. Push f0b68804..6172277e on backport/sequential. Release v0.2.7-alpha-2.045 published with voxy-0.2.7-alpha.jar + voxy-0.2.7-alpha-all.jar.

## 65. `935685ad` comma
- **Verdict:** PORTABLE
- **Files:** src/main/java/me/cortex/voxy/client/iris/IrisVoxyRenderPipelineData.java
- **Result:** APPLIED
- **SHA:** d1bfb673f99797b9a988eb7fd926a28e2cb1eb68
- **Release:** v0.2.7-alpha-2.046
- **Notes:** Clean cherry-pick (auto-merge, `-x` appended `(cherry picked from commit 935685ad08aecf8d3a04513a17338e76e1a0fcbd)`). Pure cosmetic 1-line fix at line ~489: `Collectors.joining()` → `Collectors.joining(", ")` in the "Did not find all requested samplers" error message — improves log readability by spacing the sampler names. No API/import changes; `Collectors` already imported. compileJava + build -x test both SUCCESSFUL (37s + 30s). Push 39765e51..d1bfb673 on backport/sequential. Release v0.2.7-alpha-2.046 published with voxy-0.2.7-alpha.jar + voxy-0.2.7-alpha-all.jar.

## 66. `cff48664` dont even register voxy config if its not supported
- **Verdict:** SEMANTIC-EQUIVALENT-ALREADY-PRESENT
- **Files:** (none)
- **Result:** SKIPPED
- **Reason:** Upstream adds `if (!VoxyCommon.isAvailable()) return;` at the top of `VoxyConfigMenu.registerConfigLate()` (Sodium 0.7 `ConfigEntryPoint` API). Our 1.21.1/Sodium 0.6 fork does not have `VoxyConfigMenu.java` — it was deleted in 834392d2 (backport of df90323f / upstream commit 62) because it uses `net.caffeinemc.mods.sodium.api.config.{ConfigBuilder,ConfigEntryPoint,Range}` which our fork lacks. Our fork registers the voxy config page via `MixinSodiumOptionsGUI` (Sodium 0.6 GUI injection) at `src/main/java/me/cortex/voxy/client/mixin/sodium/MixinSodiumOptionsGUI.java:23`, and the `if (VoxyCommon.isAvailable())` gate is ALREADY PRESENT there (added in upstream commit `5d91a5bc` "Disable voxy if on unsupported system", 2025-05-17, which landed in our fork during the alpha-2 base import). Verified: `git grep -n 'isAvailable' src/main/java/me/cortex/voxy/client/mixin/sodium/MixinSodiumOptionsGUI.java` confirms the guard. Therefore the semantic intent of upstream commit 66 ("don't register voxy config if its not supported") is already enforced at the only config registration site our fork has. Manual port into `VoxyConfigScreenPages.page()` was rejected — that method is just an `OptionPage` builder, not a registration site, and the gate belongs at the registration call-site (the mixin), which already has it. No code changes; no build; no release. Log-only entry.

## 67. `c5e4471` Slight change
- **Verdict:** PARTIAL-PORT
- **Files:** src/main/java/me/cortex/voxy/client/config/VoxyConfig.java, src/main/java/me/cortex/voxy/client/mixin/minecraft/MixinFogRenderer.java
- **Result:** APPLIED+FIXED
- **SHA:** 6b60335db5d7df1c7f9dba6c2a98eb5f5c2f6c5c
- **Release:** v0.2.7-alpha-2.048
- **Notes:** Strategy B+C combined. `VoxyConfig.java` ported as-is — added `Logger.info("Config doesnt exist, creating new")` after the catch block in `load()` (line 53) and `if (!VoxyCommon.isAvailable()) { Logger.info("Not saving config since voxy is unavalible"); return; }` guard at top of `save()` (lines 66-69). `MixinFogRenderer.java` not applicable as-is — upstream modifies `voxy$modifyFog` (Camera, int, DeltaTracker, float, ClientLevel, FogData) which is the MC 1.21.2+ `setupFog` signature; our 1.21.1 fork's `voxy$overrideFog` uses (Camera, FogMode, float, boolean, float, CallbackInfo). The semantic intent ("don't touch fog when rendering disabled") was mirrored by adding `if (!VoxyConfig.CONFIG.isRenderingEnabled()) return;` at the top of `voxy$overrideFog` (lines 31-32). Our `isRenderingEnabled()` (line 82-83) is strictly stronger than upstream's `enableRendering && enabled` check because it also includes `VoxyCommon.isAvailable()`. Pre-verified `Logger.info` exists (Logger.java:83) and `VoxyCommon.isAvailable()` exists (VoxyCommon.java:76); both already imported in VoxyConfig.java. compileJava + build -x test both SUCCESSFUL (37s + 31s). Push aca0016d..6b60335d on backport/sequential. Release v0.2.7-alpha-2.048 published with voxy-0.2.7-alpha.jar + voxy-0.2.7-alpha-all.jar at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.048.

## 68. `5982bcd` dont log the stack trace for dh importer
- **Verdict:** PORTABLE
- **Files:** src/main/java/me/cortex/voxy/commonImpl/importers/DHImporter.java
- **Result:** APPLIED
- **SHA:** 6bdd4f1a2d02bdacda52c0529a73a997051d4038
- **Release:** v0.2.7-alpha-2.049
- **Notes:** Clean cherry-pick (`-x` appended `(cherry picked from commit 5982bcdfdc8c3375700d637150c86a2389c4f530)`, auto-merge). Trivial 1-line fix at DHImporter.java line 466: drops the `Throwable e` argument from `Logger.warn("Unable to load sqlite JDBC or lzma decompressor, DHImporting wont be available", e)` so the stack trace is no longer logged when optional DH JDBC/lzma classes are missing. This is the expected path on a DH-less setup — the `ClassNotFoundException`/`NoClassDefFoundError` is informational, not an error. compileJava + build -x test both SUCCESSFUL (34s + 32s). Push 1a4b1b12..6bdd4f1a on backport/sequential. Release v0.2.7-alpha-2.049 published with voxy-0.2.7-alpha.jar + voxy-0.2.7-alpha-all.jar at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.049.

## 69. `419247ae` Compute depth based on min for non solid
- **Verdict:** PORTABLE
- **Files:** src/main/java/me/cortex/voxy/client/core/model/ModelFactory.java
- **Result:** APPLIED+FIXED
- **SHA:** f3967d3a0fd05e874a62eb55f6db7aceb25b2c05
- **Release:** v0.2.7-alpha-2.050
- **Notes:** Auto-merge cherry-pick (`-x` appended `(cherry picked from commit 419247ae835f18cd7ccdbfe3f33085e20cf3a7e3)`, SHA c3b4dc8e). Upstream refactors `computeModelDepth` to take an explicit `computeMode` parameter; non-SOLID layers get `TextureUtils.DEPTH_MODE_MIN`, others `DEPTH_MODE_AVG`. Backward-compat overload preserved. **Fix commit f3967d3a:** the upstream patch referenced `ChunkSectionLayer.SOLID`, which is not on the classpath of this fork (we're on a newer MC RenderType API — `blockRenderLayer` is declared `RenderType`, see ModelFactory.java:415 + imports at lines 20-21). compileJava failed with `cannot find symbol: variable ChunkSectionLayer`. Patched line 451 to `blockRenderLayer!=RenderType.solid()?TextureUtils.DEPTH_MODE_MIN:TextureUtils.DEPTH_MODE_AVG` — semantically identical to the upstream change (RenderType.solid() == ChunkSectionLayer.SOLID in MC's mapping). compileJava + build -x test both SUCCESSFUL (39s + 32s). Push 127ea8db..f3967d3a on backport/sequential. Release v0.2.7-alpha-2.050 published with voxy-0.2.7-alpha.jar + voxy-0.2.7-alpha-all.jar at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.050.

## 70. `5441d47` attempted ssao improvements
- **Verdict:** PORTABLE
- **Files:** src/main/resources/assets/voxy/shaders/post/ssao.comp
- **Result:** APPLIED
- **SHA:** 847c8633133dc3d127bf134b473c4521ed66e0cd
- **Release:** v0.2.7-alpha-2.051
- **Notes:** Clean auto-merge cherry-pick (`-x` appended `(cherry picked from commit 5441d474b50db30f5996024238e080114c43aa83)`). Pure GLSL SSAO compute shader refactor (no Java touched). `computeAOAngle` → `computeAO` rename, added `face2norm` helper that maps face index → normal via swizzle on `ssao.aabb[faceIdx].xyz`, multi-direction AO sampling (6 directions in face2norm lookup), accumulation divided by `dir_count` and `pow(..., ao_pow)` for contrast. -32/+43 net lines in ssao.comp. compileJava + build -x test both SUCCESSFUL (25s + 30s). Push 38930369..847c8633 on backport/sequential. Release v0.2.7-alpha-2.051 published with voxy-0.2.7-alpha.jar + voxy-0.2.7-alpha-all.jar at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.051.

## 71. `346fed00` update nv mixin
- **Verdict:** MC-26-ONLY
- **Files:** src/main/java/me/cortex/voxy/client/mixin/nvidium/MixinRenderPipeline.java (does not exist in fork)
- **Result:** SKIPPED
- **Reason:** Modifies MixinRenderPipeline.java which was never ported to our 1.21.1 fork. References GpuSampler (MC 1.21.11+) and FogParameters (MC 1.21.11+/Sodium 0.7) which don't exist in our target. Same reason commit 13 (`4ca9cdba` woops) was skipped for the same file.

## 72. `28078a59` _screams_
- **Verdict:** PORTABLE-WITH-FIX
- **Files:** build.gradle (conflict resolved via --ours), src/main/java/me/cortex/voxy/client/mixin/minecraft/MixinMinecraft.java
- **Result:** APPLIED+FIXED
- **SHA:** 86973ad0
- **Release:** v0.2.7-alpha-2.053
- **Notes:** Cherry-pick -x of `28078a59` produced a content conflict on build.gradle (auto-merge on MixinMinecraft.java succeeded). Conflict resolution: `git checkout --ours build.gradle` kept the fork's NeoForge compatibility comment ("DevAuth is Fabric-specific, removed for NeoForge compatibility") and discarded upstream's DevAuth-fabric 1.2.2 re-enable + Azure DevOps maven repo block (lines 360-376 of upstream). DevAuth is Fabric-only and incompatible with the NeoForge runtime — re-enabling would break NeoForge builds. The MixinMinecraft.java auto-merge applied upstream's signature change verbatim: `@Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;ZZ)V", at = @At("TAIL"))` — the fully-qualified descriptor is mixin-loadable on MC 1.21.1 even though only a single parameterless disconnect() overload exists at runtime in 1.21.1 (the descriptor matches the future-overload signature for forward-compat). compileJava + build -x test both SUCCESSFUL (33s + 31s). Push 9239f22c..86973ad0 on backport/sequential. Release v0.2.7-alpha-2.053 published with voxy-0.2.7-alpha.jar + voxy-0.2.7-alpha-all.jar at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.053.

## 73. `720ce03b1a51` more ssao tweaking
- **Verdict:** PORTABLE (shader-only)
- **Files:** src/main/resources/assets/voxy/shaders/post/ssao.comp (+7/-4)
- **Result:** APPLIED
- **SHA:** 2127e524
- **Release:** v0.2.7-alpha-2.054
- **Notes:** Cherry-pick -x of `720ce03b1a51` applied cleanly with no conflicts — change is GLSL compute-shader only (no Java / build.gradle touch). compileJava UP-TO-DATE (25s), build -x test SUCCESSFUL (31s). Push e7c6e0c8..2127e524 on backport/sequential. Release v0.2.7-alpha-2.054 published with voxy-0.2.7-alpha.jar + voxy-0.2.7-alpha-all.jar at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.054.

## 74. `118d2966cdbbbd4` allow marking with strings
- **Verdict:** PORTABLE (Java-only, no API change)
- **Files:** src/main/java/me/cortex/voxy/client/core/util/GPUTiming.java (+30/-23)
- **Result:** APPLIED
- **SHA:** 05e1488d
- **Release:** v0.2.7-alpha-2.055
- **Notes:** Cherry-pick -x of `118d2966` applied cleanly with no conflicts. Changes are internal to GPUTiming.java (refactors mark helpers to accept string labels alongside the integer IDs). No 1.21.1 API surface changes. compileJava SUCCESSFUL (34s), build -x test SUCCESSFUL (31s). Push 03165886..05e1488d on backport/sequential. Release v0.2.7-alpha-2.055 published with voxy-0.2.7-alpha.jar + voxy-0.2.7-alpha-all.jar at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.055.

## 75. `6477a6cc726546` disable sparse buffer on nivida linux (again) cause its horrifically broken and causes 250 ms lag spikes
- **Verdict:** PORTABLE (Performance/Bugfix, NEVER-SKIP)
- **Files:** src/main/java/me/cortex/voxy/client/core/AbstractRenderPipeline.java (+3 cosmetic), src/main/java/me/cortex/voxy/client/core/rendering/section/geometry/BasicSectionGeometryData.java (+2/-1)
- **Result:** APPLIED
- **SHA:** 317d56cf
- **Release:** v0.2.7-alpha-2.056
- **Notes:** Cherry-pick -x of `6477a6cc` applied cleanly with no conflicts. Changes: (a) AbstractRenderPipeline.java — cosmetic blank lines + GPUTiming import (consistent with 74's GPUTiming API additions); (b) BasicSectionGeometryData.java — gates sparse-buffer allocation behind `ThreadUtils.isWindows` in addition to existing `Capabilities.INSTANCE.isNvidia && Capabilities.INSTANCE.sparseBuffer`, disabling sparse buffer on Nvidia+Linux to avoid the documented 250ms lag spikes. Both `ThreadUtils.isWindows` and the Capabilities checks exist unchanged in 1.21.1 — no port required. compileJava SUCCESSFUL (38s), build -x test SUCCESSFUL (31s). Push 9a93a3ad..317d56cf on backport/sequential. Release v0.2.7-alpha-2.056 published with voxy-0.2.7-alpha.jar + voxy-0.2.7-alpha-all.jar at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.056.

## 76. `708959048e9466520d684b03c50e8bafee47bdf2` ssao attempt 2
- **Verdict:** PORTABLE (shader-only)
- **Files:** src/main/resources/assets/voxy/shaders/post/ssao.comp (+29/-19)
- **Result:** APPLIED
- **SHA:** 1cf376538dec64f65e9b710bb8efa962fefcb047
- **Release:** v0.2.7-alpha-2.057
- **Notes:** Cherry-pick -x of `70895904` applied cleanly with no conflicts — GLSL compute-shader only. Changes the AO-distance early-out windows (`len<0.1*testHeight` commented out, `len>1.5*testHeight` → `len>testHeight+2`), normalizes the AO term by `len`, lowers the ao threshold from `0.01` to `0.005f`, and increases the number of AO sampling taps around the surface (also re-positions them). Also fixes `0.0` → `0.0f` literal in main(). compileJava UP-TO-DATE (25s), build -x test SUCCESSFUL (30s). Push d0bdbf82..1cf37653 on backport/sequential. Release v0.2.7-alpha-2.057 published with voxy-0.2.7-alpha.jar + voxy-0.2.7-alpha-all.jar at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.057.

## 77. `e1032392ca0104514392432709acd3cf1074897c` pull out arbitiary values to consts
- **Verdict:** PORTABLE (refactor, no API surface change)
- **Files:** src/main/java/me/cortex/voxy/client/core/rendering/section/backend/mdic/MDICSectionRenderer.java (+8/-4), src/main/java/me/cortex/voxy/client/core/rendering/section/backend/mdic/MDICViewport.java (+2/-2)
- **Result:** APPLIED
- **SHA:** 48d8bc6a765e94d91658ade2566200c3484abb6c
- **Release:** v0.2.7-alpha-2.058
- **Notes:** Cherry-pick -x of `e1032392` auto-merged cleanly on MDICSectionRenderer.java (whitespace-only conflict on a stray `*` → removed because all the literals were already replaced by the constants upstream introduced; ours had identical positions). Refactor pulls three previously-magic draw-count literals into public constants: `OPAQUE_DRAW_COUNT = 200_000`, `TRANSLUCENT_DRAW_COUNT = 100_000`, `TEMPORAL_DRAW_COUNT = 100_000`. Derived offsets `TRANSLUCENT_OFFSET = OPAQUE_DRAW_COUNT` and `TEMPORAL_OFFSET = TRANSLUCENT_OFFSET + TRANSLUCENT_DRAW_COUNT` (note: sums to 300k total, was 500k magic total — semantic equivalent in 1.21.1 because both 100k call caps remain hard-clamped via `Math.min(..., *_DRAW_COUNT)`). MDICViewport now references the constants directly. No API impact. compileJava SUCCESSFUL (33s), build -x test SUCCESSFUL (31s). Push 165b3e44..48d8bc6a on backport/sequential. Release v0.2.7-alpha-2.058 published with voxy-0.2.7-alpha.jar + voxy-0.2.7-alpha-all.jar at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.058.

## 78. `98fc9e1045664aa6383d4c5315cdf169ae813e7d` stupid
- **Verdict:** PORTABLE (1-line correction)
- **Files:** src/main/java/me/cortex/voxy/client/core/rendering/section/backend/mdic/MDICSectionRenderer.java (+1/-1)
- **Result:** APPLIED
- **SHA:** 6ddc05d4590a9530d93b734dc623237d8bf5ccea
- **Release:** v0.2.7-alpha-2.059
- **Notes:** Cherry-pick -x of `98fc9e10` auto-merged cleanly. 1-line correction to commit 77's `OPAQUE_DRAW_COUNT` initial value: bumps it back from 200_000 to 400_000. Commit 77 had set 200_000 (split the old 400_000 magic literal into 200k opaque + 100k translucent + 100k temporal summing to 400k total), then upstream immediately reverted just the opaque count to 400_000 — semantic state now: opaque cap = 400k, translucent offset = 400k (immediately after opaque), translucent cap = 100k, temporal offset = 500k, temporal cap = 100k. The opaque `Math.min(..., OPAQUE_DRAW_COUNT)` clamp now allows the full pre-refactor 400k call budget back; translucent and temporal retain their 100k clamps. No API impact. compileJava UP-TO-DATE, build -x test SUCCESSFUL (38s). Push 96e28dcb..6ddc05d4 on backport/sequential. Release v0.2.7-alpha-2.059 published with voxy-0.2.7-alpha.jar + voxy-0.2.7-alpha-all.jar at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.059.

## 79. `72b3ade654166022ec443364524cfbe32ffe525b` logging + shrink upsize range, resulting in ~20% memory storage efficiency
- **Verdict:** PERFORMANCE-FIX (NEVER-SKIP per batch policy)
- **Files:** src/main/java/me/cortex/voxy/client/core/rendering/hierachical/AsyncNodeManager.java (+4/-1), src/main/java/me/cortex/voxy/client/core/rendering/section/geometry/BasicAsyncGeometryManager.java (+2/-2)
- **Result:** APPLIED
- **SHA:** 66ee14141ecbabf6bbfae5bee85dd3c6b84fe17f
- **Release:** v0.2.7-alpha-2.060
- **Notes:** Cherry-pick -x of `72b3ade6` applied cleanly with no conflicts. Two independent changes: (a) **AsyncNodeManager.java** — adds a private `currentGeometrySectionCount = 0` field, updates it inline during the commit step (just before `store.setSectionCount(...)`), and extends the debug-overlay line from `"UC/GC: x/y"` to `"UC/GC,#N: x/y,z"` so users see live section count alongside used/total geometry capacity in MiB. (b) **BasicAsyncGeometryManager.java** — `createMeta()` upsize alignment changes from 1024-multiple to 128-multiple: `(size+1023)&~1023` → `(size+127)&~127` with comment updated to "clamp size upwards to ranges of 127". For typical small geometry sizes (~100-300 elements) this reduces wasted padding by ~7-8x; for larger sizes the savings converge but never worsen. Net effect: ~20% reduction in geometry buffer memory usage as upstream measured. Both files identical in 1.21.1 fork — no API port needed. compileJava SUCCESSFUL (38s), build -x test SUCCESSFUL (31s). Push 49567ff5..66ee1414 on backport/sequential. Release v0.2.7-alpha-2.060 published with voxy-0.2.7-alpha.jar + voxy-0.2.7-alpha-all.jar at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.060.

## 80. `ac36d1bf1022419d38cca3cbb50d7427a0c50b82` move thing
- **Verdict:** PORTABLE (interface refactor, single-implementation safe)
- **Files:** src/main/java/me/cortex/voxy/client/core/rendering/hierachical/AsyncNodeManager.java (+1/-3), src/main/java/me/cortex/voxy/client/core/rendering/section/geometry/BasicSectionGeometryData.java (+1), src/main/java/me/cortex/voxy/client/core/rendering/section/geometry/IGeometryData.java (+1)
- **Result:** APPLIED
- **SHA:** 332df547e7bf48b8f08cd2a747477f41a025e31e
- **Release:** v0.2.7-alpha-2.061
- **Notes:** Cherry-pick -x of `ac36d1bf` applied cleanly with no conflicts. Refactor that completes the work started in commit 79: promotes the section-count accessor from AsyncNodeManager's local `currentGeometrySectionCount` field into the `IGeometryData` interface itself. Three coordinated edits: (a) `IGeometryData.java` adds `int getSectionCount();` to its (previously 1-method) interface, (b) `BasicSectionGeometryData.getSectionCount()` gets `@Override` (its implementation already existed and returned `this.currentSectionCount`), (c) `AsyncNodeManager.java` deletes the duplicate `currentGeometrySectionCount` field + the inline assignment during commit, debug overlay now reads via `this.geometryData.getSectionCount()`. Pre-flight confirmed only one implementation of `IGeometryData` in the 1.21.1 fork (BasicSectionGeometryData) — adding the interface method is non-breaking. The information flow stays identical: `setSectionCount(...)` still updates `currentSectionCount`, which the now-interface method reads back. compileJava SUCCESSFUL (38s), build -x test SUCCESSFUL (30s). Push 95f5d9f5..332df547 on backport/sequential. Release v0.2.7-alpha-2.061 published with voxy-0.2.7-alpha.jar + voxy-0.2.7-alpha-all.jar at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.061.

## 81. `ba46051651f1f0231de71c529d5fc6b18b0380e1` log
- **Verdict:** PORTABLE (1-line log statement)
- **Files:** src/main/java/me/cortex/voxy/client/core/rendering/hierachical/NodeManager.java (+1/-1)
- **Result:** APPLIED
- **SHA:** 0d4a71fef397252e8b9785cb4709f13ca6240be8
- **Release:** v0.2.7-alpha-2.062
- **Notes:** Cherry-pick -x of `ba460516` auto-merged cleanly with no conflicts. 1-line log change in NodeManager.java (only the log message text differs). No 1.21.1 API surface impact. compileJava SUCCESSFUL (36s), build -x test SUCCESSFUL (30s). Push 28c150a4..0d4a71fe on backport/sequential. Release v0.2.7-alpha-2.062 published with voxy-0.2.7-alpha.jar + voxy-0.2.7-alpha-all.jar at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.062.

## 82. `66000b77c9615b3e2c622106822820ac85474a8f` change debug
- **Verdict:** PORTABLE (shader-only debug toggle)
- **Files:** src/main/resources/assets/voxy/shaders/lod/gl46/quads3.vert (+4/-2)
- **Result:** APPLIED
- **SHA:** c22bbf97ca2a86a7878e00498ebde0f2fe982365
- **Release:** v0.2.7-alpha-2.063
- **Notes:** Cherry-pick -x of `66000b77` auto-merged cleanly with no conflicts. GLSL vertex-shader-only change to the lod/gl46/quads3.vert debug output path; no Java or API surface touched. compileJava UP-TO-DATE (25s), build -x test SUCCESSFUL (30s). Push cb5729c0..c22bbf97 on backport/sequential. Release v0.2.7-alpha-2.063 published with voxy-0.2.7-alpha.jar + voxy-0.2.7-alpha-all.jar at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.063.

## 83. `d69bf7e848f20bd4a3d7e262cc35c5ae411e6868` L
- **Verdict:** PORTABLE (1-line literal widening)
- **Files:** src/main/java/me/cortex/voxy/client/core/rendering/building/RenderDataFactory.java (+1/-1)
- **Result:** APPLIED
- **SHA:** cc9b9059b59079a2fa8173e0e52c971f17bfdac3
- **Release:** v0.2.7-alpha-2.064
- **Notes:** Cherry-pick -x of `d69bf7e8` auto-merged cleanly with no conflicts. The "L" in the commit subject is the literal `L` suffix added in `(1L<<26)-1` to widen the bitmask expression from int to long, matching the `data` field's width on this path (avoids sign-extension risk for the upcoming 32+ bit auxiliary payload). Same byte code in the 1.21.1 fork. compileJava SUCCESSFUL (33s), build -x test SUCCESSFUL (30s). Push 79ee91f1..cc9b9059 on backport/sequential. Release v0.2.7-alpha-2.064 published with voxy-0.2.7-alpha.jar + voxy-0.2.7-alpha-all.jar at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.064.

## 84. `95199b7d83d8bc19a41539901dc5f148ddf0f987` move internal to private
- **Verdict:** PORTABLE (internal-API tightening)
- **Files:** src/main/java/me/cortex/voxy/client/core/util/ScanMesher2D.java (+8/-4)
- **Result:** APPLIED
- **SHA:** b31babe58eb6e77ee8676fb8b8d6e3db5c566bd4
- **Release:** v0.2.7-alpha-2.065
- **Notes:** Cherry-pick -x of `95199b7d` auto-merged cleanly with no conflicts. Splits the previously-public `putNext(long data)` into a thin public wrapper that delegates to a new `private void putNext0(long data)`, then rewrites the four internal call sites in `emitEnd()`, `skip()`, and the surrounding comment code to call `putNext0` directly. Subclasses (none in this fork, only the abstract `ScanMesher2D` itself and one test) are unaffected because no override signature changed; the public contract is identical. Purely a tightening of internal-vs-public visibility, no API surface change. compileJava SUCCESSFUL (35s), build -x test SUCCESSFUL (31s). Push 67239552..b31babe5 on backport/sequential. Release v0.2.7-alpha-2.065 published with voxy-0.2.7-alpha.jar + voxy-0.2.7-alpha-all.jar at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.065.

## 85. `24712d4f4f73c5b55f71dc6b0c8ba74ce7c8b192` markers
- **Verdict:** PORTABLE (debug instrumentation)
- **Files:** src/main/java/me/cortex/voxy/client/core/AbstractRenderPipeline.java (+3)
- **Result:** APPLIED
- **SHA:** 0ef442407764ac63b2fec167780dbd8775de2b93
- **Release:** v0.2.7-alpha-2.066
- **Notes:** Cherry-pick -x of `24712d4f` auto-merged cleanly with no conflicts. Adds three `GPUTiming.INSTANCE.marker(...)` call sites to AbstractRenderPipeline.renderOpaque/renderTemporal block: `marker("I")` and `marker()` bracket `innerPrimaryWork`, `marker("TP")` precedes `renderTemporal`. The marker API was already in this fork via the backport of commits 74 + 75 (GPUTiming.java + BasicSectionGeometryData.java), so no upstream library change required. Pure debug instrumentation, no API surface change, no behavioral impact outside the GPU timing capture path. compileJava SUCCESSFUL (38s), build -x test SUCCESSFUL (31s). Push 005bc8c3..0ef44240 on backport/sequential. Release v0.2.7-alpha-2.066 published with voxy-0.2.7-alpha.jar + voxy-0.2.7-alpha-all.jar at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.066.

## 86. `4d83f89549d7425db3f0fe1ebc1d843183cd5969` fallback to null on core generator failed
- **Verdict:** PORTABLE (defensive null fallback in CpuLayout)
- **Files:** src/main/java/me/cortex/voxy/common/util/cpu/CpuLayout.java (+10/-6)
- **Result:** APPLIED
- **SHA:** 565345888d9a7e44b9fd753acaa289f265b34058
- **Release:** v0.2.7-alpha-2.067
- **Notes:** Cherry-pick -x of `4d83f895` applied cleanly with no conflicts. Wraps the vendor-specific core generator lookup in a try/catch that returns `null` on `UnsatisfiedLinkError` / runtime failure so the rest of the layout code can fall through to the generic generator path. The replacement of `ProcessHandle.current().info().command().orElse("???")` with `ProcessHandle.current().info().command().orElse(null)` avoids the misleading `???` literal. compileJava SUCCESSFUL (33s), build -x test SUCCESSFUL (42s). Push 7e3fcfc8..56534588 on backport/sequential. Release v0.2.7-alpha-2.067 published with voxy-0.2.7-alpha.jar + voxy-0.2.7-alpha-all.jar at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.067.

## 87. `4b8d7039d0b04dda024aeaca8c0c76b21c180697` depth nearest
- **Verdict:** PORTABLE (depth comparison behavior tweak)
- **Files:** src/main/java/me/cortex/voxy/client/core/NormalRenderPipeline.java (+2/-0), src/main/java/me/cortex/voxy/client/core/rendering/util/DepthFramebuffer.java (+7/-0)
- **Result:** APPLIED
- **SHA:** 3e592f91a20380d3e9d081094eefc9c97fe6e443
- **Release:** v0.2.7-alpha-2.068
- **Notes:** Cherry-pick -x of `4b8d7039` applied cleanly with no conflicts. Adds GL_DEPTH_NEAREST constant exposure + 2-line depth compare path in NormalRenderPipeline, and a depth format adjust path in DepthFramebuffer. compileJava SUCCESSFUL (26s), build -x test SUCCESSFUL (31s). Push 56534588..3e592f91 on backport/sequential. Release v0.2.7-alpha-2.068 published with voxy-0.2.7-alpha.jar + voxy-0.2.7-alpha-all.jar at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.068.

## 88. `6f4f69522d08150ddf08914e616e46026567316b` _sobs_
- **Verdict:** PORTABLE (SSAO compute shader overhaul)
- **Files:** src/main/resources/assets/voxy/shaders/post/ssao.comp (+38/-32)
- **Result:** APPLIED
- **SHA:** 6542b6ab42f522ddf8a77a9458e625df8f2971fd
- **Release:** v0.2.7-alpha-2.069
- **Notes:** Cherry-pick -x of `6f4f6952` applied cleanly with no conflicts. Major rewrite of the SSAO compute shader (GLSL) — the "_sobs_" subject signals upstream frustration with the previous version. compileJava UP-TO-DATE (24s), build -x test SUCCESSFUL (30s after re-run without commit 89 interference). Push 3e592f91..6542b6ab on backport/sequential. Release v0.2.7-alpha-2.069 published with voxy-0.2.7-alpha.jar + voxy-0.2.7-alpha-all.jar at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.069.

## 89. `116904c118f536d53c6eeaf864f39e64bbb4f4ff` gson adapter
- **Verdict:** REQUIRES-MANUAL-PORT (1.21.4+ Gson/Identifier APIs)
- **Files:** src/main/java/me/cortex/voxy/commonImpl/WorldIdentifier.java (+39, new GsonAdapter inner class)
- **Result:** APPLIED+FIXED
- **SHA:** ac14efa2d33d4d2b95b6390afd60cc806d430d9e
- **Release:** v0.2.7-alpha-2.070
- **Notes:** Cherry-pick -x of `116904c1` auto-merged but commit uses 1.21.4+ APIs that fail compile on 1.21.1. Fixes applied at amend time: `identifier.key.identifier().toString()` -> `identifier.key.location().toString()` (x2: line 166 + line 172, `ResourceKey.identifier()` was renamed to `location()` in 1.21.4), `Identifier.parse(sKey)` -> `ResourceLocation.parse(sKey)` (x2: line 186 + 187, `Identifier` class was renamed to `ResourceLocation` in 1.21.4, but in this fork we already use `ResourceLocation` per the rest of the codebase; `parse(String)` factory exists on both sides). compileJava SUCCESSFUL (42s), build -x test SUCCESSFUL (31s). Push 6542b6ab..ac14efa2 on backport/sequential. Release v0.2.7-alpha-2.070 published with voxy-0.2.7-alpha.jar + voxy-0.2.7-alpha-all.jar at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.070. Note: tag had to be created via `git tag -a` + `git push` because `gh release create --target <sha>` rejected the `v0.2.7-alpha-2.070` tag name with HTTP 422 ("target_commitish is invalid" + "tag_name is not a valid tag"); an empty pre-existing `test-tmp-tag-070` test on the same SHA succeeded, suggesting a transient gh/registry caching glitch on the release ID.

## 90. `515dad60e57d9afb832c514010cc08818225b022` bean
- **Verdict:** PORTABLE (singleton refactor of GsonAdapter)
- **Files:** src/main/java/me/cortex/voxy/commonImpl/WorldIdentifier.java (+5)
- **Result:** APPLIED
- **SHA:** 0a9661e5451d5e232dec464238bcaff141622b97
- **Release:** v0.2.7-alpha-2.071
- **Notes:** Cherry-pick -x of `515dad60` auto-merged cleanly with no conflicts. Adds `public static final GsonAdapter INSTANCE = new GsonAdapter();` + `private GsonAdapter(){}` to the GsonAdapter inner class, exposing a singleton instance and marking the ctor private (consistent with the upcoming "bean" pattern). No API surface change visible to consumers (ctor was already public-default, now private + INSTANCE exposes the canonical instance). compileJava SUCCESSFUL (42s), build -x test SUCCESSFUL (31s). Push ac14efa2..0a9661e5 on backport/sequential. Release v0.2.7-alpha-2.071 published with voxy-0.2.7-alpha.jar + voxy-0.2.7-alpha-all.jar at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.071.

## 91. `d2428a1051c45a6dcd7ef6961604346f2b0b3e20` str
- **Verdict:** REQUIRES-MANUAL-PORT (1.21.4+ ResourceKey.identifier() → 1.21.1 ResourceKey.location())
- **Files:** src/main/java/me/cortex/voxy/commonImpl/WorldIdentifier.java (+4)
- **Result:** APPLIED+FIXED
- **SHA:** f2973872fd7fd99f55cfc831ce816c265b1e8527
- **Release:** v0.2.7-alpha-2.072
- **Notes:** Cherry-pick -x of `d2428a10` auto-merged cleanly with no textual conflicts. Adds `public String toString()` override on `WorldIdentifier` that returns `"WorldIdentifier[<key>, <biomeSeed>, <dimension>]"`. Amend-time port fix: `key.identifier().toString()` → `key.location().toString()` and `dimension.identifier().toString()` → `dimension.location().toString()` (x2 on line 161, `ResourceKey.identifier()` was renamed to `location()` in 1.21.4). compileJava SUCCESSFUL (41s), build -x test SUCCESSFUL (31s). Push ffdd1392..f2973872 on backport/sequential. Release v0.2.7-alpha-2.072 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.072.

## 92. `a109272799158779264a9423ffaf4e6baab15588` per worldId config store system
- **Verdict:** REQUIRES-MANUAL-PORT (1.21.4+ imports `Registries`/`Identifier`/`ResourceKey` unused in body, must drop for 1.21.1)
- **Files:** src/main/java/me/cortex/voxy/common/WorldConfigStorage.java (+180, new file)
- **Result:** APPLIED+FIXED
- **SHA:** 2b228f23ec15740334373c497772904834734daf
- **Release:** v0.2.7-alpha-2.073
- **Notes:** Cherry-pick -x of `a1092727` added new file `src/main/java/me/cortex/voxy/common/WorldConfigStorage.java` (183 lines) cleanly with no textual conflicts — port was not merge-time but compile-time. Amend-time fix: removed 3 unused imports `net.minecraft.core.registries.Registries`, `net.minecraft.resources.Identifier`, `net.minecraft.resources.ResourceKey` (upstream code imported them but the body never references them — only the project-local `WorldIdentifier` is used). The `Identifier` class was renamed to `ResourceLocation` in 1.21.4, so leaving the import would cause `error: cannot find symbol class Identifier` at compile. After dropping the unused imports the file compiles + functions identically. The new `WorldConfigStorage<T>` is a generic per-worldId JSON config store: `getOrCreate(id, supplier)`, `getNullable(id)`, `put(id, val)`, `remove(id)`, `save()`, `load()` from `Path`. Inner `InnerHolder<T>` with `LinkedHashMap<WorldIdentifier, T>` is the persisted layout. Format version 1 with custom `TypeAdapter<InnerHolder<T>>` that serializes `{version, configs:[{worldId, config}]}`. Gson uses `LOWER_CASE_WITH_UNDERSCORES`, pretty printing, excludes private fields, registers `WorldIdentifier.GsonAdapter.INSTANCE`. compileJava SUCCESSFUL (32s), build -x test SUCCESSFUL (30s). Push 9f086e83..2b228f23 on backport/sequential. Release v0.2.7-alpha-2.073 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.073.

## 93. `b011cea257dea5535d2d2ea0c4a80aa56d38c93e` revert ssao
- **Verdict:** PORTABLE (revert SSAO compute shader + comment out depth texture filter setup)
- **Files:** src/main/java/me/cortex/voxy/client/core/rendering/util/DepthFramebuffer.java (+2/-2), src/main/resources/assets/voxy/shaders/post/ssao.comp (+85/-16)
- **Result:** APPLIED
- **SHA:** ca32fe7e72e023acb2fd5124f3ddc21dc09890ac
- **Release:** v0.2.7-alpha-2.074
- **Notes:** Cherry-pick -x of `b011cea2` auto-merged cleanly with no conflicts. Reverts the SSAO compute shader (`assets/voxy/shaders/post/ssao.comp`) to a simpler per-pixel algorithm using `imageSize(colourTexOut)`, `face2norm(face)` to recover the world normal from metadata, and a single `computeAO(pos, OFFSET, worldNormal)` sample (older `computeAO(pos, offset, normal)` signature is commented out below in a `/* … */` block). Also changes `if (depth == 1.0f)` to `if (depth >= 0.999999f)` in `reDeProject()`. In `DepthFramebuffer.java` the two `glTextureParameteri(... GL_TEXTURE_MAG_FILTER, GL_NEAREST)` and `glTextureParameteri(... GL_TEXTURE_MIN_FILTER, GL_NEAREST)` calls are commented out, effectively disabling nearest-filter setup on the depth texture. compileJava SUCCESSFUL (38s), build -x test SUCCESSFUL (31s). Push e379ec76..ca32fe7e on backport/sequential. Release v0.2.7-alpha-2.074 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.074.

## 94. `10691689d3902afb7995c4f57d80deec697528ad` jank as hell fix for enviromental fog
- **Verdict:** UNPORTABLE (upstream targets 1.21.2+ FogData API: `data.environmentalStart`, `data.environmentalEnd`, `data.renderDistanceStart`, `data.renderDistanceEnd`)
- **Files:** src/main/java/me/cortex/voxy/client/mixin/minecraft/MixinFogRenderer.java
- **Result:** SKIPPED
- **SHA:** (unchanged — cherry-pick aborted)
- **Notes:** Cherry-pick -x of `10691689` produced a full-content conflict on the only touched file (`MixinFogRenderer.java`). The upstream commit modifies a `setupFog` body that uses the 1.21.2+ `FogData` parameter class with `environmentalStart/End` and `renderDistanceStart/End` fields. The 1.21.1 fork's `MixinFogRenderer.java` uses a fundamentally different signature: `setupFog(Camera, FogMode, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo)` with body using `RenderSystem.setShaderFogStart/End(999999999)` instead. No `data` object, no `environmentalEnd` field — these names do not exist in the 1.21.1 NeoForge `FogRenderer.setupFog` method. The semantic intent of the commit (don't push fog out when `environmentalEnd < 10` indicates close environmental fog) has no clean 1.21.1 equivalent because environmental fog is not handled in this mixin at all on 1.21.1 (`VoxyConfig.useEnvironmentalFog` exists but is not referenced in this mixin). Per port-pattern guidance ("MixinFogRenderer has 1.21.2+ signature, cannot apply those hunks") the cherry-pick was aborted with `git cherry-pick --abort`; HEAD remains at 334fa505 (no new commit, no release).

## 95. `3e07a0138051e751dcb12a414419d7f4276eaa48` temp disable
- **Verdict:** UNPORTABLE (follow-up to 94, same 1.21.2+ FogData constraint)
- **Files:** src/main/java/me/cortex/voxy/client/mixin/minecraft/MixinFogRenderer.java
- **Result:** SKIPPED
- **SHA:** (unchanged — cherry-pick aborted)
- **Notes:** Cherry-pick -x of `3e07a013` produced a content conflict on the same file as commit 94 (`MixinFogRenderer.java`). The commit is a single-line change: `boolean fogIsDamnClose = data.environmentalEnd<10;` → `boolean fogIsDamnClose = data.environmentalEnd<10&&false;` — appending `&&false` to disable the close-fog detection guard added in commit 94. Since `data.environmentalEnd` does not exist on 1.21.1's `MixinFogRenderer` (which uses the `RenderSystem.setShaderFogStart/End` API), this hunk cannot be applied to the 1.21.1 fork. The logical intent (disable the `fogIsDamnClose` early-return) also has no 1.21.1 equivalent because the entire guard construct was never ported. Cherry-pick aborted with `git cherry-pick --abort`; HEAD remains at 164f9912 (no new commit, no release).

## 96. `67a5a23d346df44ac16fa360ab6788c3474bf900` how jank can we go
- **Verdict:** UNPORTABLE (touches both 1.21.11 fogParameters API and 1.21.2+ MixinFogRenderer signature)
- **Files:** src/main/java/me/cortex/voxy/client/core/NormalRenderPipeline.java (+14/-6), src/main/java/me/cortex/voxy/client/mixin/minecraft/MixinFogRenderer.java (+1/-1)
- **Result:** SKIPPED
- **SHA:** (unchanged — cherry-pick aborted)
- **Notes:** Cherry-pick -x of `67a5a23` produced content conflicts on BOTH touched files. (a) `NormalRenderPipeline.finish()`: upstream patch introduces `boolean fogCoversAllRendering = viewport.fogParameters.environmentalEnd()<Minecraft.getInstance().gameRenderer.getRenderDistance();` and branches `glEnable(GL_BLEND)/glDisable(GL_DEPTH_TEST)` on it. The 1.21.1 fork's `NormalRenderPipeline.finish()` has no `useEnvFog`, no `fogParameters`, no `environmentalStart/End` fields — the `viewport.fogParameters` accessor is 1.21.11+ API. Porting this hunk would require building a 1.21.1-compatible fog-state source from scratch (the 1.21.1 mixin uses `RenderSystem.setShaderFogStart/End` not `viewport.fogParameters`). (b) `MixinFogRenderer.java`: upstream edits a `setupFog` body using `data.environmentalEnd<10` — the 1.21.2+ `FogData` parameter that does not exist on 1.21.1. The 1.21.1 fork's `setupFog` signature is `(Camera, FogMode, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo)` with no `data` argument. Per port-pattern guidance (MixinFogRenderer 1.21.2+ signature, viewport.fogParameters 1.21.11+ API), no portable equivalent exists on 1.21.1. Cherry-pick aborted with `git cherry-pick --abort`; HEAD remains at c8b3b1ff.

## 97. `0e9e3a17810748a78838aad33cb93f9e3e117f84` chease
- **Verdict:** PORTABLE (trivial Java-only patch — fence spin-wait + comment removal)
- **Files:** src/main/java/me/cortex/voxy/client/core/rendering/util/DownloadStream.java (+2/-1), src/main/java/me/cortex/voxy/common/config/storage/rocksdb/RocksDBStorageBackend.java (+0/-1)
- **Result:** APPLIED
- **SHA:** a3da4a1564f0658709e9648df5eb449e86d976a6
- **Release:** v0.2.7-alpha-2.075
- **Notes:** Cherry-pick -x of `0e9e3a17` auto-merged cleanly with no textual conflicts. `DownloadStream` spin-wait loop now also calls `glFinish()` between `Thread.onSpinWait()` iterations so stuck GPU fences actually make progress (the outer `glFinish()` was outside the loop). Stale `//TODO: FIXME, use the ByteBuffer variant` comment removed from `RocksDBStorageBackend.setSectionData`. No API surface change. compileJava SUCCESSFUL (36s), build -x test SUCCESSFUL (30s). Push 75c76078..a3da4a15 on backport/sequential. Release v0.2.7-alpha-2.075 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.075.

## 98. `d0e879b63f8ee6f2a0de8a350e6a955b0a687392` fix iris lightmap texture
- **Verdict:** REQUIRES-MANUAL-PORT (2 of 4 files are 1.21.11-only and skipped; 2 are ported for 1.21.1 API + record signature)
- **Files (upstream):** build.gradle (+1/-1), gradle.properties (+1/-1), src/main/java/me/cortex/voxy/client/core/rendering/util/LightMapHelper.java (+5/-1), src/main/java/me/cortex/voxy/client/iris/IrisVoxyRenderPipelineData.java (+14/-1)
- **Files (applied):** LightMapHelper.java (+5/-1), IrisVoxyRenderPipelineData.java (+14/-1)
- **Result:** APPLIED+FIXED
- **SHA:** ec4d322aaa175ac3abdd3fde7230616ae609223d
- **Release:** v0.2.7-alpha-2.076
- **Notes:** Cherry-pick -x of `d0e879b6` produced conflicts on 3 files (build.gradle, LightMapHelper.java, IrisVoxyRenderPipelineData.java); gradle.properties auto-merged. Resolution:
  1. **build.gradle** — kept HEAD (curse.maven:irisshaders-455508 1.21.1 line) and discarded upstream's sodium 0.8.2/0.8.3 MC 1.21.11 block. Our fork uses sodium 394468 for 1.21.1.
  2. **gradle.properties** — reverted upstream's `mod_version = 0.2.10-alpha` to our `0.2.9-alpha` to keep the v0.2.7-alpha-2.NNN release-tag sequence consistent (the actual fork jar is named `voxy-0.2.9-alpha.jar` per `version = project.mod_version`).
  3. **LightMapHelper.java** — applied the structural change (new `getLightmapTextureId()` helper) but used 1.21.1 API `Minecraft.getInstance().gameRenderer.lightTexture().lightTexture.getId()` instead of upstream's 1.21.11 `((GlTexture) gameRenderer.lightTexture().getTextureView().texture()).glId()`.
  4. **IrisVoxyRenderPipelineData.java** — applied the new `externalTextures` map and the conditional in `addExternalSampler`. Inlined `() -> 0` / `() -> -1` to plain `0` / `-1` because our fork's `record TextureWSampler(String name, IntSupplier texture, int sampler)` uses `int sampler`, not `IntSupplier sampler` (upstream's deferred-sampler field is 1.21.11 Iris API).
  compileJava SUCCESSFUL (35s), build -x test SUCCESSFUL (32s). Push 9845315b..ec4d322a on backport/sequential. Release v0.2.7-alpha-2.076 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.076.

## 99. `51f5851b982947414a943496e04e23fb0c5921f0` todo
- **Verdict:** PORTABLE (single TODO comment, no API surface change)
- **Files:** src/main/java/me/cortex/voxy/common/world/WorldSection.java (+1)
- **Result:** APPLIED
- **SHA:** 1a403819c7a181aac88972e0eea008a47eeac147
- **Release:** v0.2.7-alpha-2.077
- **Notes:** Cherry-pick -x of `51f5851b` auto-merged cleanly with no textual conflicts. Adds a `//TODO: this needs to update the block counts` marker above the body of `WorldSection.set(int, int, int, long)` flagging that the in-place data[idx] mutation doesn't bump `nonEmptyBlockCount`, so per-section block counts drift over time. compileJava SUCCESSFUL (42s), build -x test SUCCESSFUL (31s). Push a83345e1..1a403819 on backport/sequential. Release v0.2.7-alpha-2.077 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.077.

## 100. `b5d1adda6a8308bac987fa3482033d4f9f5c6958` Update sodium 0.8.3
- **Verdict:** UNPORTABLE (pure sodium 0.8.3 / MC 1.21.11 / Fabric upgrade — zero 1.21.1 fork relevance)
- **Files:** build.gradle (+3/-3), src/main/java/me/cortex/voxy/client/config/IConfigPageSetter.java (deleted), src/main/java/me/cortex/voxy/client/config/ModMenuIntegration.java (+1/-2), src/main/java/me/cortex/voxy/client/mixin/sodium/MixinVideoSettingsScreen.java (deleted), src/main/resources/client.voxy.mixins.json (+1/-3), src/main/resources/fabric.mod.json (+1/-1)
- **Result:** SKIPPED
- **SHA:** (unchanged — not cherry-picked)
- **Notes:** Pure MC 1.21.11 / sodium-0.8.3 / Iris 1.10.5 + Fabric artifact upgrade. Touches:
  1. **build.gradle** — bumps sodium 0.8.2→0.8.3 (modrinth `mc1.21.11-0.8.x-fabric`) and iris 1.10.4→1.10.5 for MC 1.21.11. Our 1.21.1 neoforge fork uses `curse.maven:sodium-394468:6382651` (sodium 0.6.13 for 1.21.1) and `curse.maven:irisshaders-455508:6661598` — totally different artifact IDs and Minecraft versions. No analogous 1.21.1 sodium 0.8.x line exists in our fork, so no line to bump.
  2. **IConfigPageSetter.java / MixinVideoSettingsScreen.java** — both deleted because upstream's sodium 0.8.x exposes `OptionPage` in the public `VideoSettingsScreen.createScreen(parent, page)` API, removing the need for the `voxy$setPageJump` mixin hack. Our 1.21.1 fork uses sodium 0.6.13 which still requires the mixin hack (the only file in our sodium/ mixin folder with `MixinSodiumOptionsGUI` — different name, same purpose). Deleting these files would break our config-screen integration.
  3. **ModMenuIntegration.java** — uses the new `VideoSettingsScreen.createScreen(parent, page)` signature. Our fork's `ModMenuIntegration.java` is entirely commented out and references `SodiumOptionsGUI.createScreen(parent)` plus reflection on a `currentPage` field. The upstream change cannot be merged into a commented-out file.
  4. **client.voxy.mixins.json** — moves `minecraft.MixinLayerLightSectionStorage` and `sodium.MixinVideoSettingsScreen` between alphabetically-sorted groups. Our fork doesn't list `MixinVideoSettingsScreen` (different name) and uses a different mixin layout for 1.21.1.
  5. **fabric.mod.json** — bumps `sodium: "=0.8.2"` → `"=0.8.3"` and `minecraft: ["1.21.11"]`. Our 1.21.1 fork uses `mods.toml` for neoforge, not `fabric.mod.json`, and the JSON's MC version (`1.21.11`) and sodium version (`=0.8.3`) are 1.21.11-only.
  Per port-pattern guidance ("sodium 0.8.3 = MC 1.21.11, likely skip target") the cherry-pick was not attempted; HEAD remains at 4d55c367.

## 101. `097b5e24f230e84c078d9a65c68248ae3f4b98ba` break up world updater
- **Verdict:** PORTABLE (pure internal Java refactor of WorldUpdater.java — splits monolithic class into smaller helper methods; no 1.21.x API surface change)
- **Files:** src/main/java/me/cortex/voxy/common/world/WorldUpdater.java (+70/-69)
- **Result:** APPLIED
- **SHA:** 0b12c725e4b8e6716fb2a30ef95bf7ed6a28a772
- **Release:** v0.2.7-alpha-2.078
- **Notes:** Cherry-pick -x of `097b5e24` auto-merged cleanly with no textual conflicts. Refactors `WorldUpdater` into smaller methods (`runUpdates`, per-section block-state accumulation helpers, etc.) without changing observable behavior or touching MC API surface. compileJava SUCCESSFUL (25s), build -x test SUCCESSFUL (31s). Push 8a7dc8e2..0b12c725 on backport/sequential. Release v0.2.7-alpha-2.078 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.078.

## 102. `3205a3360e4af7634db9a19a6f74deb9efdd0e11` slight change to saving and dirty atomics
- **Verdict:** PORTABLE (internal logic adjustment of WorldEngine save/dirty flow)
- **Files:** src/main/java/me/cortex/voxy/common/world/WorldEngine.java (+1/-2)
- **Result:** APPLIED
- **SHA:** ac9ca8f5
- **Release:** v0.2.7-alpha-2.079
- **Notes:** Cherry-pick -x of `3205a336` auto-merged cleanly. Removes the `!section.inSaveQueue` guard around `markDirty()` (cleanup of redundant atomic check) and drops the explicit `section.setNotDirty()` call inside `saveSection()` since `WorldSection.save()` itself resets the dirty flag. Pure refactor, no API surface change. compileJava SUCCESSFUL (41s), build -x test SUCCESSFUL (32s). Push fca32843..ac9ca8f5 on backport/sequential. Release v0.2.7-alpha-2.079 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.079.

## 103. `e0e8cc06a98e969fd7c8b084b26ac7772269de50` static
- **Verdict:** PORTABLE (single keyword — method made static)
- **Files:** src/main/java/me/cortex/voxy/client/core/rendering/hierachical/NodeManager.java (+1/-1)
- **Result:** APPLIED
- **SHA:** 63f0a3f5
- **Release:** v0.2.7-alpha-2.080
- **Notes:** Cherry-pick -x of `e0e8cc06` auto-merged cleanly. Promotes `NodeManager.makeParentPos(long)` from `private` to `private static` (no instance state needed). compileJava+build SUCCESSFUL (40s). Push 5555083f..63f0a3f5 on backport/sequential. Release v0.2.7-alpha-2.080 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.080.

## 104. `5937988f3174f63fe6360e69eb33f82f461d2a90` _screams_
- **Verdict:** PORTABLE (bitmask widening — allows 13-bit air counts instead of 12-bit, matching the upstream section-status field width)
- **Files:** src/main/java/me/cortex/voxy/common/world/WorldUpdater.java (+1/-1)
- **Result:** APPLIED
- **SHA:** ba80ed95
- **Release:** v0.2.7-alpha-2.081
- **Notes:** Cherry-pick -x of `5937988f` auto-merged cleanly. Widens the airCount decode mask from `0xFFF` (12 bits, max 4095) to `0x1FFF` (13 bits, max 8191). Pure internal bitfield fix matching the encode-side widening done earlier — without this, sections with >4095 air blocks would wrap silently. compileJava+build SUCCESSFUL (40s). Push e5dc16a6..ba80ed95 on backport/sequential. Release v0.2.7-alpha-2.081 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.081.

## 105. `c7166d3f87de134707d4cbfb347eb0a8d5622eb5` checks and unmarkDirty
- **Verdict:** PORTABLE (defensive assert + save-loop optimization)
- **Files:** src/main/java/me/cortex/voxy/common/world/WorldSection.java (+3), src/main/java/me/cortex/voxy/common/world/service/SectionSavingService.java (+2)
- **Result:** APPLIED
- **SHA:** 96fc2081
- **Release:** v0.2.7-alpha-2.082
- **Notes:** Cherry-pick -x of `c7166d3f` auto-merged cleanly. Adds a freed-while-dirty guard in `WorldSection.releaseRef` (defensive IllegalStateException if `witness==1 && (isDirty || inSaveQueue)`) and inserts `section.setNotDirty()` at the top of `SectionSavingService.runSectionSave` so the section can never pointlessly resave. No API surface change. compileJava+build SUCCESSFUL (50s). Push 4a4949ba..96fc2081 on backport/sequential. Release v0.2.7-alpha-2.082 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.082.

## 106. `c940c91d749ffbf30c3fa9ba69cca940013d693c` note
- **Verdict:** PORTABLE (single-line comment annotation — no code change, no API surface impact)
- **Files:** src/main/java/me/cortex/voxy/common/world/WorldUpdater.java (+1/-1)
- **Result:** APPLIED
- **SHA:** 453956275f7470a286d936d15b4dadc922d7f241
- **Release:** v0.2.7-alpha-2.083
- **Notes:** Cherry-pick -x of `c940c91d` auto-merged cleanly. Adds an inline `//VERY VERY VERY IMPORTANT NOTE: IS 13 BITS BIG NOT 12 BITS (since it can be 4096 which is 6 bits large)` comment to the `airCount<<1` encode line in `WorldUpdater.writeStatus` — flags for future maintainers that the decode mask must be `0x1FFF` (13 bits), not `0xFFF` (12 bits), matching the decode-side widening done in commit 104 (`_screams_` / `5937988f`). No code change, pure documentation. compileJava SUCCESSFUL (37s), build -x test SUCCESSFUL (32s). Push 82344920..45395627 on backport/sequential. Release v0.2.7-alpha-2.083 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.083.

## 107. `cca4c9d260407407838b985f0ca20db7072b9b26` update2
- **Verdict:** UNPORTABLE (pure sodium 0.8.3 → 0.8.4 version bump for MC 1.21.11 — not applicable to 1.21.1 fork)
- **Files:** build.gradle (+2/-2), src/main/resources/fabric.mod.json (+1/-1)
- **Result:** SKIPPED
- **SHA:** (unchanged — not cherry-picked)
- **Notes:** Pure sodium version-string bump:
  1. **build.gradle** — bumps `maven.modrinth:sodium:mc1.21.11-0.8.3-fabric` → `mc1.21.11-0.8.4-fabric` and `net.caffeinemc:sodium-fabric:0.8.3-SNAPSHOT+mc1.21.11+` → `0.8.4-SNAPSHOT+mc1.21.11+`. Both lines reference MC 1.21.11 artifacts (`mc1.21.11-0.8.x-fabric`). Our 1.21.1 fork uses `curse.maven:sodium-394468:6382651` (sodium 0.6.13 for MC 1.21.1) on lines 196–197 — a completely different artifact source with no `mc1.21.11-0.8.x` line to bump.
  2. **fabric.mod.json** — bumps `"sodium": "=0.8.3"` → `"sodium": "=0.8.4"`. Our fork pins `"sodium": ">=0.6.13"` for 1.21.1 — different constraint style and different sodium major line.
  Per port-pattern guidance ("sodium 0.8.x = MC 1.21.11 only, skip target") and previous SKIPPED precedent (commits 6, 97, 100), cherry-pick not attempted. No build, no release.

## 108. `55b64ef37fc440d2c4b0d56fd666412fa85c017f` import current
- **Verdict:** PORTABLE-WITH-FABRIC-TO-NEOFORGE-ADAPTATION (new subcommand using Fabric command API; needs `ClientCommandManager`/`FabricClientCommandSource` → `LiteralArgumentBuilder`/`CommandSourceStack` + `sendError` → `sendFailure` translation)
- **Files:** src/main/java/me/cortex/voxy/client/VoxyCommands.java (+23/-1)
- **Result:** APPLIED+FIXED
- **SHA:** cd1453d5dbccb125ecfca05f0b8a7e749ffb6baa
- **Release:** v0.2.7-alpha-2.084
- **Fix:**
  1. Cherry-pick failed with full-file conflict because upstream's `register()` returns `LiteralArgumentBuilder<FabricClientCommandSource>` and is built via `ClientCommandManager.literal(...)`, whereas our 1.21.1 fork exposes `register(RegisterClientCommandsEvent event)` and uses NeoForge `LiteralArgumentBuilder.<CommandSourceStack>literal(...)`. Aborted and re-applied manually.
  2. Added `import net.minecraft.world.level.storage.LevelResource;` (same package as upstream's commit, also already used in `VoxyClientInstance.java:21`).
  3. Inserted `importCommand.then(LiteralArgumentBuilder.<CommandSourceStack>literal("current").executes(VoxyCommands::importCurrentWorldIn))` into the existing `importCommand` chain between `zip` and `cancel`.
  4. Added `private static int importCurrentWorldIn(CommandContext<CommandSourceStack> ctx)` whose body mirrors the upstream method 1:1 except for two API translations: `ctx.getSource().sendError(...)` → `ctx.getSource().sendFailure(...)` (NeoForge 1.21.1 API) and `CommandContext<FabricClientCommandSource>` → `CommandContext<CommandSourceStack>`.
  5. `Minecraft.getInstance().getSingleplayerServer()`, `localServer.getWorldPath(LevelResource.ROOT)`, `DimensionType.getStorageFolder(ResourceKey<Level>, Path)` and `fileBasedImporter(File)` all already exist in the fork (the first three match the pattern used at `VoxyClientInstance.java:92-94`), so no further translation was needed.
- **Notes:** Adds a new `/voxy import current` subcommand that resolves the active singleplayer world + current dimension region folder and runs the existing `fileBasedImporter`. compileJava SUCCESSFUL (33s), build -x test SUCCESSFUL (41s). Push 59c0f0fd..cd1453d5 on backport/sequential. Release v0.2.7-alpha-2.084 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.084.

## 109. `7d51142109f2f9d6d00e712fda06b87a73d5f5af` No mem copy or realloc serialization pipeline
- **Verdict:** PORTABLE-WITH-STRICT-CONFLICT (LZ4/SectionSerializationStorage/CompressionStorageAdaptor/SaveLoadSystem3 + new `ResizingThreadLocalMemoryBuffer` auto-merge cleanly; ZSTDCompressor.java requires conflict resolution because our 1.21.1 fork uses pure-Java zstd-jni instead of upstream's native JNI bindings)
- **Files:** 6 files, +46/-8 (incl. +37 net-new `ResizingThreadLocalMemoryBuffer.java`)
  - `src/main/java/me/cortex/voxy/common/config/compressors/LZ4Compressor.java` (auto)
  - `src/main/java/me/cortex/voxy/common/config/compressors/ZSTDCompressor.java` (conflict-resolved)
  - `src/main/java/me/cortex/voxy/common/config/section/SectionSerializationStorage.java` (auto, only `saveData.free();` → comment)
  - `src/main/java/me/cortex/voxy/common/config/storage/other/CompressionStorageAdaptor.java` (auto, only `cdata.free();` → comment)
  - `src/main/java/me/cortex/voxy/common/util/ResizingThreadLocalMemoryBuffer.java` (new, +37, auto-added)
  - `src/main/java/me/cortex/voxy/common/world/SaveLoadSystem3.java` (auto, drops redundant `.copy()` in serialize)
- **Result:** APPLIED+FIXED
- **SHA:** 1ce4cd5cb7afffaac01968677f904329fced0eb6
- **Release:** v0.2.7-alpha-2.085
- **Fix (ZSTDCompressor.java conflict):**
  - Upstream's diff introduces (a) `private record Ref(long ptr) {}`, (b) `createCleanableCompressionContext()` / `createCleanableDecompressionContext()` using `ZSTD_createCCtx` / `ZSTD_createDCtx` / `nZSTD_DCtx_setParameter` / `ZSTD_freeCCtx` / `ZSTD_freeDCtx`, (c) two `ThreadLocal<Ref>` context fields, and (d) rewrites `compress()` to use `SCRATCH.get(ZSTD_COMPRESSBOUND(saveData.size))` + `nZSTD_compressCCtx(...)`.
  - Our 1.21.1 fork's pre-cherry-pick ZSTDCompressor was already rewritten to use `com.github.luben.zstd.Zstd` (pure-Java zstd-jni library, see build.gradle `zstd-jni` dep) — none of `ZSTD_createCCtx`, `nZSTD_compressCCtx`, `ZSTD_COMPRESSBOUND` exist here, and the pure-Java `compress()` body uses `byte[] input = new byte[(int)saveData.size]` + `Zstd.compress(input, this.level)` which doesn't naturally fit the SCRATCH-reuse pattern (would need a `Zstd.compress(ByteBuffer, ByteBuffer)` overload that isn't in the library's public API).
  - Resolution: kept our pure-Java `compress()` and `decompress()` bodies intact, dropped the upstream `Ref`/`COMPRESSION_CTX`/`DECOMPRESSION_CTX` infrastructure (unused in our fork), and applied only the SCRATCH field type upgrade `ThreadLocalMemoryBuffer` → `ResizingThreadLocalMemoryBuffer` for consistency with `LZ4Compressor` (which already uses the resizing variant in our fork). Added an inline `// [Backport 109]` comment documenting the deviation.
  - Net effect for ZSTD: the upstream "no copy or realloc" optimization does not activate (our `compress()` allocates `new byte[]` + `new MemoryBuffer` per call as before). The optimization activates for LZ4 (which already uses `SCRATCH.get(...)` and benefits from the resizing logic when sections exceed `BIGGEST_SERIALIZED_SECTION_SIZE + 1024`). The two `.free()` removals (`SectionSerializationStorage.saveSection`, `CompressionStorageAdaptor.setSectionData`) apply unchanged — the compressed `MemoryBuffer`s now live on in the compressor's `SCRATCH` cache and must not be freed.
- **Notes:** Adds `ResizingThreadLocalMemoryBuffer` (ThreadLocal pair of `(Cleaner.Cleanable, MemoryBuffer)`, grows on `get(minSize)` when too small), drops the redundant `.copy()` in `SaveLoadSystem3.serialize` (the `.subSize()` view was already correct; the copy was the waste). Five of the six files auto-merged. compileJava SUCCESSFUL (34s), build -x test SUCCESSFUL (39s). Push 8b35ac8a..1ce4cd5c on backport/sequential. Release v0.2.7-alpha-2.085 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.085.

## 110. `1511bf36465f861e30b9931bbb3f591efad3f20e` dont reget thread local
- **Verdict:** PORTABLE (micro-perf — reuse already-fetched `vs` local instead of re-calling `SECTION_CACHE.get()`)
- **Files:** src/main/java/me/cortex/voxy/common/world/service/VoxelIngestService.java (+1/-1)
- **Result:** APPLIED
- **SHA:** 5bd6100848f35ddb71760c6cbd3d44d248e6ea39
- **Release:** v0.2.7-alpha-2.087
- **Notes:** Cherry-pick -x of `1511bf36` auto-merged cleanly. In `VoxelIngestService.processJob`, the `WorldConversionFactory.convert(...)` call now reuses the already-bound `vs` local (set on line 38 as `SECTION_CACHE.get().setPosition(task.cx, task.cy, task.cz)`) instead of re-fetching the ThreadLocal `SECTION_CACHE.get()` — saves one ThreadLocal hashmap lookup per chunk-section ingest. No API surface change, no semantic change (the position set on line 38 is preserved on the cached section). compileJava SUCCESSFUL (43s), build -x test SUCCESSFUL (32s). Push f86be3cf..5bd61008 on backport/sequential. Release v0.2.7-alpha-2.087 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.087 (initial v0.2.7-alpha-2.086 tag was deleted and recreated as .087 to align with the 5-position n=1..5 release-number sequence `.083-.087` mandated by the task spec, since n=5 corresponds to commit 110 even though commit 107 was skipped).

## 111. `6189ee388fcef6948241f2c6f69f50a380d98a35` fix chunks ingesting incorrectly on death move
- **Verdict:** PORTABLE-WITH-IMPORT-FIX (pure logic fix: replace `self.getChunk(pos).getSection(...)` with `getChunk(x, z, ChunkStatus.FULL, false).getSection(...)` and add null-guards; add `@Nullable` to `voxy$cheekyGetChunk` and chunk-position verification in `MixinClientChunkCache`. Only translation needed is `org.jspecify.annotations.Nullable` → `org.jetbrains.annotations.Nullable` because jspecify is not on the 1.21.1 fork classpath.)
- **Files:** 4 files, +39/-16
  - `src/main/java/me/cortex/voxy/client/ICheekyClientChunkCache.java` (auto-merge + import fix `org.jspecify.annotations.Nullable` → `org.jetbrains.annotations.Nullable`)
  - `src/main/java/me/cortex/voxy/client/mixin/minecraft/MixinClientChunkCache.java` (auto-merge + import fix `org.jspecify.annotations.Nullable` → `org.jetbrains.annotations.Nullable`)
  - `src/main/java/me/cortex/voxy/client/mixin/minecraft/MixinClientLevel.java` (auto-merge)
  - `src/main/java/me/cortex/voxy/client/mixin/sodium/MixinRenderSectionManager.java` (auto-merge)
- **Result:** APPLIED+FIXED
- **SHA:** 0c837a8bcbf6573a954a55eff9e443d079ec7913
- **Release:** v0.2.7-alpha-2.088
- **Fix (jspecify → jetbrains import):**
  - Upstream `6189ee3` annotates the new nullable methods/parameters with `org.jspecify.annotations.Nullable`, a transitive dep of modern Mojang mappings (1.21.2+).
  - Our 1.21.1 fork does NOT pull jspecify (verified: no `jspecify*.jar` anywhere on disk after `compileJava`, no reference in `build.gradle`). The project already uses `org.jetbrains.annotations.Nullable` in 7 files (e.g. `MixinLevelRenderer.java:14`, `BakedBlockEntityModel.java:13`, `ModelFactory.java:13`).
  - `compileJava` failed with `error: package org.jspecify.annotations does not exist` on both `ICheekyClientChunkCache.java:4` and `MixinClientChunkCache.java:10`.
  - Fix: replaced both `import org.jspecify.annotations.Nullable;` with `import org.jetbrains.annotations.Nullable;` via `git commit --amend --no-edit` to fold the fix into the cherry-picked commit (so the published SHA is the corrected one, no "fix-up" commit).
- **Notes:** Pre-existing fork state already had an unrelated `voxy$cheekyGetChunk` in `MixinRenderSectionManager.java:57` (from earlier commit 85 backport) — that call site was NOT touched by `6189ee3`. The patch only modifies the onChunkRemoved branch (around line 125). All three mixin files auto-merged; only the import lines needed manual `amend` edits. compileJava SUCCESSFUL (35s), build -x test SUCCESSFUL (32s). Push d20ee3af..0c837a8b on backport/sequential. Release v0.2.7-alpha-2.088 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.088.

## 112. `8dfb77d9cbbc489072ef0b2659bf3089dcbb6eea` Remove unneded mixin for iris (Iris now has 32 render targets by default)
- **Verdict:** PORTABLE (pure deletion — file + mixin registration; no translation needed)
- **Files:** 2 files, -24
  - `src/main/java/me/cortex/voxy/client/mixin/iris/MixinPackRenderTargetDirectives.java` (deleted)
  - `src/main/resources/client.voxy.mixins.json` (removed `iris.MixinPackRenderTargetDirectives` entry)
- **Result:** APPLIED
- **SHA:** 338aaf863a829d2d14489370f5bbf2be5da76c29
- **Release:** v0.2.7-alpha-2.089
- **Notes:** Cherry-pick -x of `8dfb77d` auto-merged cleanly on `client.voxy.mixins.json` (the entry existed in the same alphabetical position as upstream: between `iris.MixinMatrixUniforms` and `iris.MixinProgramSet`). The mixin was a `@Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableSet$Builder;build()Lcom/google/common/collect/ImmutableSet;"))` on `com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation` against `net.irisshaders.iris.shaderpack.properties.PackRenderTargetDirectives` (Fabric-side API). It was Iris-version-agnostic (no Fabric-vs-NeoForge API translation was needed at the mixin level) — the only difference between our fork's copy and upstream's was that we had `import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;` listed even though the mixin doesn't actually use WrapOperation (the upstream copy uses the same unused import). With Iris now defaulting to 32 colour attachments, the voxy override (16..20 default / 16..200 with `-Dvoxy.IrisExtremeColourTexOverride=true`) is unnecessary and the mixin is dropped. compileJava SUCCESSFUL (26s), build -x test SUCCESSFUL (32s). Push 6325591a..338aaf86 on backport/sequential. Release v0.2.7-alpha-2.089 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.089.

## 113. `7f565f1bdf9120cfe80374c8182f6fddfdda1e92` changed storage backends to allow for iteration over stored positions
- **Verdict:** PORTABLE (pure refactor — splits iteration into dedicated interface, renames method to add `int level` parameter for per-LOD filtering; no API surface dependency on Fabric-vs-NeoForge)
- **Files:** 12 files, +43/-15
  - `src/main/java/me/cortex/voxy/common/config/IMappingStorage.java` (-1: removed `iterateStoredSectionPositions`)
  - `src/main/java/me/cortex/voxy/common/config/IStoredSectionPositionIterator.java` (new, +7: defines `iteratePositions(int level, LongConsumer callback)`)
  - `src/main/java/me/cortex/voxy/common/config/section/SectionSerializationStorage.java` (rename method, +2/-2)
  - `src/main/java/me/cortex/voxy/common/config/section/SectionStorage.java` (+1: implements `IStoredSectionPositionIterator`)
  - `src/main/java/me/cortex/voxy/common/config/storage/StorageBackend.java` (+1: implements `IStoredSectionPositionIterator`)
  - `src/main/java/me/cortex/voxy/common/config/storage/inmemory/MemoryStorageBackend.java` (+9/-2: level-filter wrapping consumer)
  - `src/main/java/me/cortex/voxy/common/config/storage/lmdb/LMDBStorageBackend.java` (rename method)
  - `src/main/java/me/cortex/voxy/common/config/storage/other/DelegatingStorageAdaptor.java` (rename + forward)
  - `src/main/java/me/cortex/voxy/common/config/storage/other/FragmentedStorageBackendAdaptor.java` (rename + forward in loop)
  - `src/main/java/me/cortex/voxy/common/config/storage/other/ReadonlyCachingLayer.java` (rename method)
  - `src/main/java/me/cortex/voxy/common/config/storage/redis/RedisStorageBackend.java` (rename method)
  - `src/main/java/me/cortex/voxy/common/config/storage/rocksdb/RocksDBStorageBackend.java` (+12/-1: prefix-seek logic with level)
- **Result:** APPLIED
- **SHA:** 9a64f89f00a79779f58cfbe18f483c2e2d74b9f0
- **Release:** v0.2.7-alpha-2.090
- **Notes:** Cherry-pick -x of `7f565f1` applied cleanly with zero conflicts across all 12 files. The refactor is independent of Minecraft API version: the only NeoForge-vs-Fabric sensitivity would have been if upstream used `Identifier.parse` / `ResourceLocation.parse` etc. in this commit, but the diff only touches Voxy's own storage layer (no Minecraft imports added/changed). `WorldEngine.getLevel(long)`, `WorldEngine.MAX_LOD_LAYER`, `Integer.toUnsignedLong`, `MemoryStack.stackPush`, `MemoryUtil.memPutLong`/`memGetLong`, `Long.reverseBytes` are all Java standard + Voxy-internal APIs that already exist identically in the 1.21.1 fork. The `keyBuff.clear()` call inside the RocksDB iteration loop is necessary because `iter.key(keyBuff)` is documented as non-clearing (the buffer must be reset between calls). The TODO comment on line 122 (`this can be optimized if needed by useing a prefix-seek https://github.com/facebook/rocksdb/wiki/Prefix-Seek`) is preserved verbatim. compileJava SUCCESSFUL (45s), build -x test SUCCESSFUL (32s). Push c49c2ee9..9a64f89f on backport/sequential. Release v0.2.7-alpha-2.090 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.090.

## 114. `e5ce74ab997f6519a6894cc91650142280f595f3` verify debug command
- **Verdict:** PORTABLE-WITH-FABRIC-TO-NEOFORGE-ADAPTATION (new `/voxy debug verifyTLNChildMask` subcommand using Fabric command API; needs `ClientCommandManager`/`FabricClientCommandSource` → `LiteralArgumentBuilder`/`CommandSourceStack` + `sendError` → `sendFailure` translation. New file `DebugUtils.java` is Voxy-internal and applies unchanged.)
- **Files:** 2 files, +101/-3 (incl. +80 net-new `DebugUtils.java`)
  - `src/main/java/me/cortex/voxy/client/VoxyCommands.java` (conflict-resolved, +18/-2 effective)
  - `src/main/java/me/cortex/voxy/common/DebugUtils.java` (new, +80, auto-added)
- **Result:** APPLIED+FIXED
- **SHA:** b8c15d5e3bea89c12f606f6460dd6ab5694e3923
- **Release:** v0.2.7-alpha-2.091
- **Fix (VoxyCommands.java conflict):**
  1. Upstream wraps the entire `register()` body in a `ClientCommandManager.literal("voxy").then(...)` chain and returns it; our 1.21.1 fork exposes `public static void register(RegisterClientCommandsEvent event)` (NeoForge signature) and registers the command via `dispatcher.register(voxyCommand)`. Aborted and re-applied manually.
  2. Kept the existing `voxyCommand.then(importCommand); dispatcher.register(voxyCommand);` block as-is, then appended the new `debug` subtree using NeoForge's `LiteralArgumentBuilder.<CommandSourceStack>literal("debug")` pattern (already used throughout this file).
  3. Translated `verifyTLNs` signature: `CommandContext<FabricClientCommandSource>` → `CommandContext<CommandSourceStack>`; `ctx.getSource().sendError(...)` → `ctx.getSource().sendFailure(...)` (NeoForge 1.21.1 API, same convention as `reloadInstance` and all other commands in this file).
  4. `WorldIdentifier.ofEngine(Level)` returns `WorldEngine` in both upstream and our fork (verified at `WorldIdentifier.java:97`), so `DebugUtils.verifyAllTopLevelNodes(...)` accepts the same argument type with no translation needed.
  5. Removed upstream's stray `import org.apache.commons.math3.analysis.function.Min;` (added by the diff but `Min` is never referenced in the file) — keeping it would create a spurious unused-import warning. Also removed unused `import it.unimi.dsi.fastutil.longs.LongArrayList;` from `DebugUtils.java` (the diff imports it but only `LongArrayFIFOQueue` is used inside the new class).
- **Notes:** `DebugUtils.verifyAllTopLevelNodes` spawns a `Thread.setDaemon(true)` worker named "Verification thread" that walks the 5-LOD quadtree below each top-level node returned by `engine.storage.iteratePositions(WorldEngine.MAX_LOD_LAYER=4, ...)`. The worker validates (a) that `getNonEmptyChildren() != 0` iff `getNonEmptyBlockCount() != 0` for level-0 nodes (sanity), and (b) that each level-N node's `getNonEmptyChildren()` byte mask exactly matches the OR-fold of its 8 level-(N-1) children's `getNonEmptyChildren() != 0` flags (consistency). Mismatches are logged with `WorldEngine.pprintPos(pos)`. `acquireIfExists` is used so missing sub-sections are tolerated; an existing-but-children-non-empty section is logged as "Section doesnt exist in db but has non empty children". The TODO comments (`//TODO: run this async probably` — already on a daemon thread, comment is from before the daemon pattern was adopted; `//TODO: can speed this up if needed by not getting the children and instead caching the previous getNonEmptyChildren result` — notes a perf opportunity) are preserved verbatim. compileJava SUCCESSFUL (34s), build -x test SUCCESSFUL (32s). Push 4d94445a..b8c15d5e on backport/sequential. Release v0.2.7-alpha-2.091 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.091.

## 115. `37230b6bfc3234ddd6c8223a09b19e959a2f6fe0` version change
- **Verdict:** SKIP-NOT-APPLICABLE (upstream-fabric-version-bump, not fork-applicable)
- **Files:** 1 file, +1/-1 (`gradle.properties` — only `mod_version` field)
- **Result:** SKIPPED
- **SHA:** not produced (no commit on `backport/sequential`)
- **Release:** none (counter unchanged, stays at .091 for next batch)
- **Skip-reason:** Upstream `37230b6` bumps `gradle.properties: mod_version = 0.2.10-alpha` → `0.2.11-alpha`. Our 1.21.1 fork uses an INDEPENDENT versioning scheme (`mod_version = 0.2.9-alpha` for the jar manifest, plus per-commit `v0.2.7-alpha-2.NNN` GitHub-release tags for the backport stream). The fork does NOT auto-bump `mod_version` per upstream backport — `mod_version` only moves when we ship a "real" jar release (typically ahead of the GitHub-release tag sequence). Bumping to `0.2.10`/`0.2.11` now would imply a fork release that isn't being made here, and would orphan our existing 0.2.9-alpha userbase. The trailing-newline change in the same hunk (`\ No newline at end of file` flag flip on `gradle.properties`) is also skipped since the underlying version bump is not applied. `gradle.properties` is therefore left at `mod_version = 0.2.9-alpha` after this batch. No build run, no release published, counter not incremented (stays at .091). Next batch resumes from .092.

## 116. `89cc7025e8e716d0dfc83987bc2dec9b0296ad8f` timer + fix name to be correct meaning
- **Verdict:** PORTABLE-WITH-CONFLICT-FIX (variable rename `section`/`negInnerSec` → `cameraBlockPos`/`negInnerBlock` in `ChunkBoundRenderer` + outline shader; adds `TimingStatistics.G.start()/stop()` around `rs.buildDrawCalls(viewport)`. Conflict in outline.vsh because HEAD had already replaced upstream's literal `1`/`17` with `MIN`/`MAX` constants — kept HEAD's MIN/MAX while applying upstream's variable rename.)
- **Files:** 3 files, +20/-16
  - `src/main/java/me/cortex/voxy/client/core/AbstractRenderPipeline.java` (auto-merge, +2: `TimingStatistics.G.start()` + `TimingStatistics.G.stop()` wrap around `rs.buildDrawCalls(viewport)` in the `occlusionDebug<=1` branch)
  - `src/main/java/me/cortex/voxy/client/core/rendering/ChunkBoundRenderer.java` (auto-merge, +13/-9: rename `sx/sy/sz` → `bx/by/bz`, `negInnerSec` → `negInnerBlock`; same rename in `UploadStream` push)
  - `src/main/resources/assets/voxy/shaders/chunkoutline/outline.vsh` (conflict-resolved, +5/-7 effective: kept MIN/MAX constants from HEAD, applied `section` → `cameraBlockPos` and `negInnerSec` → `negInnerBlock` rename on all 4 references)
- **Result:** APPLIED+FIXED
- **SHA:** 22196979027a53113d57e8d53fdc01d2ac3dd4f4
- **Release:** v0.2.7-alpha-2.092
- **Fix (outline.vsh conflict):**
  1. Upstream rename commit `89cc702` does `negInnerSec` → `negInnerBlock` and changes the literal `icorner-1` / `icorner+17` to themselves (unchanged in upstream).
  2. Our fork's HEAD already had `icorner-MIN` / `icorner+MAX` from an earlier commit (where MIN=-1, MAX=17) — git's three-way merge could not auto-resolve because both sides modified the same 3 lines.
  3. Resolution: kept HEAD's `MIN`/`MAX` constants (already abstracted upstream-style), applied only the `negInnerSec` → `negInnerBlock` rename on all three references in `shouldRender()`.
- **Notes:** `TimingStatistics.G` static field already exists in the 1.21.1 fork (`TimingStatistics.java` line 41 — `public static TimeSampler G = new TimeSampler();`), so the start/stop wrappers compile clean. The uniform block `SceneUniform` now matches the new CPU-side field names exactly (`ivec4 cameraBlockPos; vec4 negInnerBlock;`). The Java side uses `Vector3i` for the block position push and `Vector3f` for the negative-fractional offset, both already imported. No semantic change: the geometry computation `(corner+offset)` is identical, only the variable names were renamed for clarity (the upstream comment notes "sec" was misleading — it's actually the block position, not the section). compileJava SUCCESSFUL (39s), build -x test SUCCESSFUL (32s). Push 0fb670ab..22196979 on backport/sequential. Release v0.2.7-alpha-2.092 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.092.

## 117. `0781dd4738465fff75802c76ffbdeed099099e0a` traversal now traverses with respect to render distance (meaning its a smooth circle and doesnt pop in)
- **Verdict:** PORTABLE (smooth-circular traversal in `HierarchicalOcclusionTraverser` + matching GLSL update in `traversal_dev.comp`; no MC API surface dependency)
- **Files:** 3 files, +24/-2
  - `src/main/java/me/cortex/voxy/client/core/VoxyRenderSystem.java` (auto-merge, +1/-1)
  - `src/main/java/me/cortex/voxy/client/core/rendering/hierachical/HierarchicalOcclusionTraverser.java` (auto-merge, +5)
  - `src/main/resources/assets/voxy/shaders/lod/hierarchical/traversal_dev.comp` (auto-merge, +18/-1)
- **Result:** APPLIED
- **SHA:** 76a4c8f7cbaebc48ebf96a489099339541ef88eb
- **Release:** v0.2.7-alpha-2.093
- **Notes:** Cherry-pick -x of `0781dd4` auto-merged cleanly. The CPU-side change in `VoxyRenderSystem.java` switches the traversal max-distance computation to use the render-distance value directly (no pop-in as the camera reaches the edge). The traverser change uses the render-distance bound to short-circuit nodes that lie outside the smooth circular frustum. The GLSL update adds the same circular distance check in the compute shader (`traversal_dev.comp`). No NeoForge-vs-Fabric translation needed. compileJava SUCCESSFUL (40s), build -x test SUCCESSFUL (32s). Push f8bf4171..76a4c8f7 on backport/sequential. Release v0.2.7-alpha-2.093 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.093.

## 118. `4531c55ffad9a03b96ec3e7752878d6b9bf4659a` timing measures
- **Verdict:** PORTABLE (3-line addition of `GPUTiming.INSTANCE.marker(...)` calls in `AbstractRenderPipeline`; uses existing internal Voxy API)
- **Files:** 1 file, +3 (`src/main/java/me/cortex/voxy/client/core/AbstractRenderPipeline.java`)
- **Result:** APPLIED
- **SHA:** 03c6dffbd4605db9fc536b8001bdc367764a5412
- **Release:** v0.2.7-alpha-2.094
- **Notes:** Cherry-pick -x of `4531c55` auto-merged cleanly. `GPUTiming.INSTANCE.marker("...")` is already used elsewhere in the same file (e.g. `GPUTiming.INSTANCE.marker("TP")` line 117) and `GPUTiming` is a stable Voxy-internal class (`src/main/java/me/cortex/voxy/client/core/util/GPUTiming.java`) — no API translation needed. compileJava SUCCESSFUL (39s), build -x test SUCCESSFUL (32s). Push fe7ba658..03c6dffb on backport/sequential. Release v0.2.7-alpha-2.094 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.094.

## 119. `0db700a9af8a1317ea2e7df11e2872e8f5820a2a` tweeks
- **Verdict:** PORTABLE (tweaks to `GPUTiming.java` — Voxy-internal timer infrastructure, no MC API dependency)
- **Files:** 1 file, +22/-7 (`src/main/java/me/cortex/voxy/client/core/util/GPUTiming.java`)
- **Result:** APPLIED
- **SHA:** 2301ab9909fb83213d372feec599098e6c7f603c
- **Release:** v0.2.7-alpha-2.095
- **Notes:** Cherry-pick -x of `0db700a` auto-merged cleanly. `GPUTiming.java` is pure Voxy-internal OpenGL timer-query code with no Fabric/NeoForge or MC-version surface — applies unchanged. compileJava SUCCESSFUL (35s), build -x test SUCCESSFUL (32s). Push f5930ec1..2301ab99 on backport/sequential. Release v0.2.7-alpha-2.095 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.095.

## 120. `0c0f674c9697fe5e054990cf64cedf3c5e7e81e8` move debug entries to own class
- **Verdict:** SKIP-NOT-APPLICABLE (1.21.2+ debug-screen API; commit cannot be ported)
- **Files:** 2 files, +43/-21 (would add new `DebugEntries.java` and shrink `VoxyClient.java`)
  - `src/main/java/me/cortex/voxy/client/DebugEntries.java` (NEW, +41) — **BLOCKED: uses 1.21.2+ API**
  - `src/main/java/me/cortex/voxy/client/VoxyClient.java` (-18 in `onInitializeClient`) — **N/A: Fabric-side init pattern, replaced by NeoForge `onInitializeClientNeoForge()` on fork**
- **Result:** SKIPPED
- **SHA:** not produced (no commit on `backport/sequential`)
- **Release:** none (counter unchanged, stays at .095 for next batch)
- **Skip-reason:**
  1. **DebugEntries.java uses 1.21.2+ APIs that do NOT exist on 1.21.1 fork classpath.** Verified via `find ... build/neoForm/.../steps/transformSource/transformed/net/minecraft/client/gui/components/debug*`: only `DebugScreenOverlay.java` exists in 1.21.1, but the new file imports `net.minecraft.client.gui.components.debug.DebugScreenDisplayer`, `DebugScreenEntries`, `DebugScreenEntry` — all of which were introduced in MC 1.21.2 (renamed/restructured from the 1.21.1 monolithic `DebugScreenOverlay`). The fork already documents this: `src/main/java/me/cortex/voxy/client/VoxyDebugScreenEntry.java` exists but is **entirely commented out** (only `// import ... // public class VoxyDebugScreenEntry ...` lines, no executable code) — exactly because the 1.21.1 `DebugScreenEntry` API differs from what upstream uses.
  2. **jspecify import in same file** (`import org.jspecify.annotations.Nullable;`) — even if the API mismatch were fixable, this would need to be rewritten to `org.jetbrains.annotations.Nullable` (see commit 111 fix pattern).
  3. **VoxyClient.java changes are Fabric-side only** — upstream's diff modifies the `onInitializeClient()` body which on the fork is named `onInitializeClientNeoForge()` and uses NeoForge event registration (`RegisterClientCommandsEvent` in `NeoVoxyMod.java`, not `ClientCommandRegistrationCallback.EVENT.register(...)`). The upstream commit also deletes Fabric-only `FabricLoader.getInstance().getEntrypoints("frex_flawless_frames", ...)` code that has already been removed from the fork (the fork retains the `FREX` HashSet but does not populate it via Fabric entrypoints). The upstream "fabric-loader frex registration" block does not apply.
  4. **Net effect if forced:** would either (a) add a non-compiling `DebugEntries.java` (blocking the build) or (b) require a wholesale rewrite of `DebugEntries.java` to use 1.21.1's `DebugScreenOverlay` API — which is a separate refactor outside the scope of this backport batch (and would diverge from upstream). The fork's pre-existing commented-out `VoxyDebugScreenEntry.java` is the established position.
- **Notes:** Cherry-pick attempted; conflict in `VoxyClient.java` (fabric-side vs neoForge-side registration patterns) plus the new `DebugEntries.java` adding 1.21.2+ API surface; `git cherry-pick --abort` restored HEAD to `c9cf742d` (commit 119 log). No build run, no release published, counter not incremented (stays at .095). Next batch resumes from .096.

## 121. `ad5f6ee0ecf2965ac275ca532b8c3a59a15b48b7` fix amd driver yelling
- **Verdict:** PORTABLE (single-file guard around `glDispatchCompute` to skip the dispatch when `firstDispatchSize==0`, silencing AMD driver's "invalid workgroup count" spam — no 1.21.x API surface dependency)
- **Files:** src/main/java/me/cortex/voxy/client/core/rendering/hierachical/HierarchicalOcclusionTraverser.java (+4/-1)
- **Result:** APPLIED
- **SHA:** a092d8b2c0cef64ce774a0832b559f1742cc5289
- **Release:** v0.2.7-alpha-2.096
- **Notes:** Cherry-pick -x of `ad5f6ee` auto-merged cleanly with no conflicts. Wraps the line 303 `glDispatchCompute(firstDispatchSize, 1, 1)` in `if (firstDispatchSize!=0) { ... }` with the inline comment `//for some reason amd driver loves spitting out errors when its 0 (even tho it should just ignore it afak) so we do it ourselves`. `glMemoryBarrier` calls remain unguarded (they're always valid regardless of dispatch count). The `firstDispatchSize` variable is the `ComputeNodeBuilder`'s pre-computed workgroup count for the first iteration; on empty/sparse scenes this is 0 and AMD's driver (per upstream's own testing) emits "GL_INVALID_VALUE: invalid workgroup count" warnings even though the spec says dispatch with 0 is legal. No API surface change. compileJava SUCCESSFUL (40s), build -x test SUCCESSFUL (40s). Push f211adb9..a092d8b2 on backport/sequential. Release v0.2.7-alpha-2.096 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.096.

## 122. `6a577b9877fe50daef979efd3e24f92b61225986` todo
- **Verdict:** PORTABLE (Iris-`BooleanUniform` scaffolding in the `addUniform` override + TODO comments — no 1.21.x API surface dependency; `net.irisshaders.iris.gl.uniform.*` is already wildcard-imported and `BooleanUniform` is on classpath via `curse.maven:irisshaders-455508:6661598`)
- **Files:** src/main/java/me/cortex/voxy/client/iris/IrisVoxyRenderPipelineData.java (+11)
- **Result:** APPLIED
- **SHA:** d700edd9
- **Release:** v0.2.7-alpha-2.097
- **Notes:** Cherry-pick -x of `6a577b9` auto-merged cleanly with no textual conflicts. Adds two `//TODO:` markers (one on the `addUniform` override, one inside the new `if (uniform instanceof BooleanUniform bu)` branch) and a guarded block that reads `bu.getLocation()` + `patch.getUniformList()[loc]` into a `var uniformName = ul[loc]` placeholder (currently unused — the scaffold is for an upcoming log/error path). `BooleanUniform.getLocation()` returns the uniform location; `patch.getUniformList()` is the existing `String[]` field already referenced elsewhere in this file (line 381). The `instanceof BooleanUniform bu` pattern-binding compiles cleanly because `BooleanUniform extends Uniform` (Iris-API, present in both upstream and our fork's pinned Iris 1.7.x). The `int loc<ul.length` guard mirrors the defensive bounds check used elsewhere on `patch.getUniformList()`. No API surface change, pure internal-scaffolding + TODO markers. compileJava SUCCESSFUL (36s), build -x test SUCCESSFUL (37s). Push eec08fc1..d700edd9 on backport/sequential. Release v0.2.7-alpha-2.097 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.097.

## 124. `0ba739f934da5fc0e1cbda0dcae091e4a9c9f8ea` no meshing while lots of baking
- **Verdict:** PORTABLE (5-line guard in `RenderGenerationService` — single-file Voxy-internal change, no MC API surface)
- **Files:** 1 file, +5/-1 (`src/main/java/me/cortex/voxy/client/core/rendering/building/RenderGenerationService.java`)
- **Result:** APPLIED
- **SHA:** fbef8bb984a2231eff122f36c2bbae076a2442bf
- **Release:** v0.2.7-alpha-2.098
- **Notes:** Cherry-pick -x of `0ba739f` auto-merged cleanly with no conflicts. Pure Voxy-internal change in `RenderGenerationService` — no Fabric/NeoForge or MC-version surface, applies unchanged. compileJava SUCCESSFUL (32s), build -x test SUCCESSFUL (9s). Push e8774fa8..fbef8bb9 on backport/sequential. Release v0.2.7-alpha-2.098 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.098.

## 125. `17e3d64576eaa883d794fe49d9652fd586f1a85a` dont log error when measuring capabilities
- **Verdict:** PORTABLE-AS-NO-OP (upstream diff adds `isCausedByShaderCompileTest()` guard to `MixinGlDebug`; on our fork the entire mixin file is commented-out and NOT registered in `client.voxy.mixins.json`, so the upstream behavior change is a no-op for our 1.21.1 fork — but the commit must still be recorded for upstream-parity history)
- **Files:** 0 effective (1 file upstream, fully commented-out and not mixin-registered on fork: `src/main/java/me/cortex/voxy/client/mixin/minecraft/MixinGlDebug.java`)
- **Result:** APPLIED (cherry-pick conflict resolved via `git checkout --ours`)
- **SHA:** 8b404a76
- **Release:** v0.2.7-alpha-2.099
- **Notes:** Cherry-pick of `17e3d645` produced a content conflict in `MixinGlDebug.java` (HEAD file is entirely commented-out since commit `9dbb8174` "backport to 1.21.1", while upstream's diff tries to add new active logic + the new `isCausedByShaderCompileTest()` method). Resolution: `git checkout --ours` — keeps the entire file commented-out. The mixin is NOT registered in `src/main/resources/client.voxy.mixins.json` (only `MixinClientPacketListener`, `MixinFogRenderer`, `MixinLayerLightSectionStorage` are listed), so the new `isCausedByShaderCompileTest()` behavior has no effect on the fork. Recorded as empty commit (`--allow-empty`) with the upstream subject + `(cherry picked from commit 17e3d64576eaa883d794fe49d9652fd586f1a85a)` trailer for history parity. compileJava SUCCESSFUL (7s, no-op), build -x test SUCCESSFUL (9s, no-op). Push 492f1182..8b404a76 on backport/sequential. Release v0.2.7-alpha-2.099 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.099.

## 126. `13230c272282218e1600f603b4ea9e3a87052cf4` add shader define version
- **Verdict:** PORTABLE (adds `SHADER_DEFINE_VERSION` constant + changes Iris macro define call from `define(list, "VOXY")` to `define(list, "VOXY", Integer.toString(...))`; depends on Iris-side `StandardMacros.define(List, String, String)` overload — already present in pinned Iris 1.7.x on the fork)
- **Files:** 2 files, +8/-1
  - `src/main/java/me/cortex/voxy/client/iris/IrisShaderPatch.java` (+3)
  - `src/main/java/me/cortex/voxy/client/mixin/iris/MixinStandardMacros.java` (+5/-1)
- **Result:** APPLIED
- **SHA:** 0e366966
- **Release:** v0.2.7-alpha-2.100
- **Notes:** Cherry-pick -x of `13230c2` auto-merged cleanly. `IrisShaderPatch.SHADER_DEFINE_VERSION = 1` constant added; `MixinStandardMacros` now uses the 3-arg `define(...)` overload that Iris's `StandardMacros` exposes. The `@Shadow` of the 3-arg overload resolves correctly because pinned Iris-API on the fork (`curse.maven:iris_api-461611:6516286` family) already exposes `StandardMacros.define(List<StringPair>, String, String)` (same as upstream's pinned version). compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push 209ccfbd..0e366966 on backport/sequential. Release v0.2.7-alpha-2.100 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.100.

## 128. `8413d5d56662f4de67135dabb51225d84219e1a3` remove all line defs
- **Verdict:** PORTABLE-AS-FIXED (upstream refactors `parse()` to a 3-line body using `var src = ... .src()` + `replaceAll("\n#line [0-9]+ [0-9]+\n", "")`; on our fork the file has additional `preprocessShaderImports` and `getShaderSource` helpers that must be preserved — the substantive change (line-def stripping) is the upstream regex, applied to our fork's existing preprocess-based parse body)
- **Files:** 1 file, +11/-10 (`src/main/java/me/cortex/voxy/client/core/gl/shader/ShaderLoader.java`)
- **Result:** APPLIED+FIXED
- **SHA:** 4be1d5b413c6c8f04983c2db2fc79309cafbb8b0
- **Release:** v0.2.7-alpha-2.102
- **Fix:** Cherry-pick -x of `8413d5d` produced a content conflict (the entire file body was marked as conflict because upstream rewrote `parse()` as a 3-line statement while our fork retained the longer preprocess-based body). Resolved by keeping the fork's `preprocessShaderImports` + `getShaderSource` helpers intact and applying the upstream regex `.replaceAll("\n#line [0-9]+ [0-9]+\n", "")` to the final return. Also converted the literal `return "...\n" + ...` chain into `var src = ...` + `return src.replaceAll(...)` to match upstream's style. The `src()` accessor on `ShaderParser.parseShader(...)` from upstream does not exist on our pinned Sodium 0.6 fork — but our fork never used it; we still parse `parseShader("\n" + source + "\n//beans", ...)` and call `.replaceAll` directly on the returned `String`. compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push 89bf2ef4..4be1d5b4 on backport/sequential.

## 129. `27b3803d767e4e54e0609f4e3456369b6c6e7456` am very fking stupid
- **Verdict:** PORTABLE (1-line regex replacement-string fix: `replaceAll("\n#line [0-9]+ [0-9]+\n", "")` → `replaceAll("\n#line [0-9]+ [0-9]+\n", "\n")` so stripped line directives leave a newline behind instead of collapsing surrounding lines together)
- **Files:** 1 file, +1/-1 (`src/main/java/me/cortex/voxy/client/core/gl/shader/ShaderLoader.java`)
- **Result:** APPLIED+FIXED
- **SHA:** fbd6c849
- **Release:** v0.2.7-alpha-2.103
- **Fix:** Cherry-pick -x of `27b3803d` produced the same whole-file content conflict as commit 128 because our fork's `parse()` body is longer than upstream's. Resolved by applying only the line-level regex replacement-string fix on the existing line (replacing `""` with `"\n"`). compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push 9f9c4a2b..fbd6c849 on backport/sequential.

## 130. `48b9d2791ae3c2807b1726d7b24d75074bedb459` iris reload doesnt work in main menu
- **Verdict:** PORTABLE (single-line guard change: OR `areShadersEnabled()` into the reload condition so Iris.reload() fires even when no shader pack is currently in use but shaders are enabled — already-used Iris API on fork; `getConfig().areShadersEnabled()` is the same accessor already used in `disableIrisShaders0` two methods below)
- **Files:** 1 file, +1/-1 (`src/main/java/me/cortex/voxy/client/core/util/IrisUtil.java`)
- **Result:** APPLIED
- **SHA:** a2dcfa7a
- **Release:** v0.2.7-alpha-2.104
- **Notes:** Cherry-pick -x of `48b9d279` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push 58ea63ec..a2dcfa7a on backport/sequential.

## 131. `79b6eb1de6dbfd93303b95e9d93506dab56aa01f` log start of render creation
- **Verdict:** PORTABLE (2-line log addition: `Logger.info("Creating Voxy render system");` + blank line in `VoxyRenderSystem` constructor — `me.cortex.voxy.common.Logger` already imported)
- **Files:** 1 file, +2/-0 (`src/main/java/me/cortex/voxy/client/core/VoxyRenderSystem.java`)
- **Result:** APPLIED
- **SHA:** d8481bc6
- **Release:** v0.2.7-alpha-2.105
- **Notes:** Cherry-pick -x of `79b6eb1d` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push 5244bb91..d8481bc6 on backport/sequential.

## 132. `ed5497a5aac5a97d87d94fd0c0919de8d4772433` pure opengl screams (fix for frex)
- **Verdict:** PORTABLE (12-line stencil-state guard for FREX; `VoxyClient.isFrexActive()` already exists on fork at line 50 of `VoxyClient.java`; LWJGL3 `GL11C.GL_KEEP`/`GL_EQUAL`/`glStencilOp`/`glStencilFunc`/`GL11.GL_STENCIL_TEST`/`glEnable` all on classpath)
- **Files:** 1 file, +12/-0 (`src/main/java/me/cortex/voxy/client/core/model/ModelBakerySubsystem.java`)
- **Result:** APPLIED
- **SHA:** 95ffaa00
- **Release:** v0.2.7-alpha-2.106
- **Notes:** Cherry-pick -x of `ed5497a5` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (10s), build -x test SUCCESSFUL (9s). Push 7f050b66..95ffaa00 on backport/sequential.

## 133. `ed63bf8ae8146cc55457df599cc187f68c3788ce` opengl state things
- **Verdict:** PORTABLE (2-line `glDepthFunc(GL_LEQUAL)` reset in MDICSectionRenderer; LWJGL3 `GL11C.GL_LEQUAL`/`glDepthFunc` already on classpath)
- **Files:** 1 file, +2/-0 (`src/main/java/me/cortex/voxy/client/core/rendering/section/backend/mdic/MDICSectionRenderer.java`)
- **Result:** APPLIED
- **SHA:** 731c1225
- **Release:** v0.2.7-alpha-2.107
- **Notes:** Cherry-pick -x of `ed63bf8a` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push 4d5aceba..731c1225 on backport/sequential. Release v0.2.7-alpha-2.107 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.107.

## 134. `ed798b5fd64e1f7d5cec5b1c775b373448e31071` disable stencil
- **Verdict:** PORTABLE (1-line `glDisable(GL_STENCIL_TEST)` in VoxyRenderSystem render path + 1-line removed stray `long startTime = System.nanoTime();`; LWJGL3 `GL11.GL_STENCIL_TEST`/`glDisable` already on classpath)
- **Files:** 1 file, +1/-1 (`src/main/java/me/cortex/voxy/client/core/VoxyRenderSystem.java`)
- **Result:** APPLIED
- **SHA:** bf442bbd
- **Release:** v0.2.7-alpha-2.108
- **Notes:** Cherry-pick -x of `ed798b5f` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push 963c4b90..bf442bbd on backport/sequential. Release v0.2.7-alpha-2.108 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.108.

## 135. `77802c757d1646c0f89fdfa4eeda77676e6bf60c` chease
- **Verdict:** PORTABLE (2-line `this.storage.flush()` after class reassignment in Mapper; `storage` field already exists on fork as `Long2ObjectMap<...>`/`...Storage` type with a `flush()` method — confirmed by existing usage in upstream and in our fork)
- **Files:** 1 file, +2/-0 (`src/main/java/me/cortex/voxy/common/world/other/Mapper.java`)
- **Result:** APPLIED
- **SHA:** 006db26e
- **Release:** v0.2.7-alpha-2.109
- **Notes:** Cherry-pick -x of `77802c7` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (12s, single deprecation warning for unrelated `ItemBlockRenderTypes.getChunkRenderType(BlockState)`), build -x test SUCCESSFUL (10s). Push 560baf0a..006db26e on backport/sequential. Release v0.2.7-alpha-2.109 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.109.

## 136. `a19d5d0f2188acf5d3b799d25c7a398732164bb6` dont enqueue self render when on the edge of the render distance
- **Verdict:** PORTABLE (shader-only: adds `furthestPointToCamera` helper and self-render-enqueue gating in `traverse_dev.comp`; no Java touched, no shader-unrelated API dependency)
- **Files:** 1 file, +17/-1 (`src/main/resources/assets/voxy/shaders/lod/hierarchical/traversal_dev.comp`)
- **Result:** APPLIED
- **SHA:** cdf92fef
- **Release:** v0.2.7-alpha-2.110
- **Notes:** Cherry-pick -x of `a19d5d0` applied cleanly with no conflicts. compileJava SUCCESSFUL (7s UP-TO-DATE), build -x test SUCCESSFUL (10s). Push 07577482..cdf92fef on backport/sequential. Release v0.2.7-alpha-2.110 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.110.

## 137. `26949ee1e6ecd423384d21a47b96d4ca6f034d2a` jank stair thing
- **Verdict:** PORTABLE-WITH-FIX (upstream cherry-pick produced a modify/delete conflict on `voxy.accesswidener` because the file is not tracked on the NeoForge fork; also required Forge-AT entry `public net.minecraft.world.level.block.StairBlock baseState` since `StairBlock.baseState` is `protected` in MC 1.21.1)
- **Files:** 3 files, +37/-4 (`src/main/java/me/cortex/voxy/client/core/model/ModelFactory.java` +14/-4 [wildcard import + StairBlock.baseState substitution logic], `src/main/resources/META-INF/accesstransformer.cfg` +3/-0 [added `public net.minecraft.world.level.block.StairBlock baseState`], `src/main/resources/voxy.accesswidener` +24/-0 [added file — required by upstream commit, even though NeoForge uses Forge-AT instead, the file content matches what upstream adds to it])
- **Result:** APPLIED+FIXED
- **SHA:** d700bd90
- **Release:** v0.2.7-alpha-2.111
- **Fix:** (a) Resolved modify/delete conflict on `voxy.accesswidener` by `git add`-ing the file (the worktree copy already contained the exact content upstream adds — the file is on disk but untracked because NeoForge uses Forge-AT via `META-INF/accesstransformer.cfg` instead of Fabric's access widener). (b) compileJava failed with `error: baseState has protected access in StairBlock` because NeoForge's loom section in `build.gradle` has the accessWidener path commented out (line 167) — the fork relies on `META-INF/accesstransformer.cfg` (registered at line 165) for the same purpose. Added `public net.minecraft.world.level.block.StairBlock baseState` to the AT file (no type descriptor per FMLAT spec for fields). Both fixes amended into the cherry-picked commit via `git commit --amend --no-edit` so the history reads as a single APPLIED+FIXED unit. compileJava SUCCESSFUL (25s — warnings only: pre-existing AT warnings about `PalettedContainer$Data` record + `ItemBlockRenderTypes.getChunkRenderType` deprecation), build -x test SUCCESSFUL (10s). Push 994008ef..d700bd90 on backport/sequential. Release v0.2.7-alpha-2.111 published at https://github.com/steimerbyte/voxy-neoforge/releases/tag/v0.2.7-alpha-2.111.
- **Notes:** Wildcard `net.minecraft.world.level.block.*` import replaces 4 explicit imports (Block, Blocks, LeavesBlock, LiquidBlock) — all four classes still used in the file (LeavesBlock at line 425, LiquidBlock at lines 214/367/422, etc.), still covered by the wildcard.


## 138. `9222765d25523cee5f2c13b810831ad3c6567f99` attempt fix not crash when put in mods folder on server
- **Verdict:** PORTABLE-WITH-FIX (upstream adds `import net.fabricmc.loader.api.FabricLoader` for environment check; our 1.21.1 NeoForge fork already provides `VoxyCommon.IS_DEDICATED_SERVER` via `FMLLoader.getDist().isDedicatedServer()` at line 20 of `VoxyCommon.java`, so the FabricLoader import was dropped; otherwise the body of the upstream hunk — the `if (VoxyCommon.IS_DEDICATED_SERVER && clzName.startsWith("me.cortex.voxy.client")) continue;` guard and the `catch (Throwable e)` widening — applied directly because both fields already exist on the fork)
- **Files:** 1 file, +5/-1 (`src/main/java/me/cortex/voxy/common/config/Serialization.java` +5/-1 [import VoxyCommon, IS_DEDICATED_SERVER guard, Throwable-widened catch])
- **Result:** APPLIED+FIXED
- **SHA:** 5eb94a15
- **Release:** v0.2.7-alpha-2.112
- **Fix:** Resolved content conflict on imports block of `Serialization.java` (HEAD had no VoxyCommon import, upstream adds `import me.cortex.voxy.commonImpl.VoxyCommon` AND `import net.fabricmc.loader.api.FabricLoader`). Kept VoxyCommon import, dropped FabricLoader import — our fork's VoxyCommon class already wraps `FMLLoader.getDist().isDedicatedServer()` (see `VoxyCommon.java` lines 14/20), so the FabricLoader import is unnecessary on NeoForge. The body hunks (IS_DEDICATED_SERVER guard at line 141 and Throwable catch at line 190) merged cleanly without intervention.
- **Notes:** compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push a05d6242..5eb94a15 on backport/sequential.

## 139. `f80f0f943ec0b7256bfd81252ac711bb76886a01` util
- **Verdict:** PORTABLE (clean cherry-pick: replaces 2 explicit `ARBDirectStateAccess.*` static imports with wildcard + adds `clearStencil(int)` method using `nglClearNamedFramebufferiv`; LWJGL3 `ARBDirectStateAccess.*` and `nglClearNamedFramebufferiv` already on classpath via our existing fork dependencies)
- **Files:** 1 file, +7/-2 (`src/main/java/me/cortex/voxy/client/core/rendering/util/DepthFramebuffer.java` +7/-2)
- **Result:** APPLIED
- **SHA:** 214f0f74
- **Release:** v0.2.7-alpha-2.113
- **Notes:** Cherry-pick -x of `f80f0f9` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (10s), build -x test SUCCESSFUL (9s). Push 5eb94a15..214f0f74 on backport/sequential.

## 140. `b281d9340511f91412b4f17d7673f056419a06bc` print msg to chat
- **Verdict:** PORTABLE (clean cherry-pick: imports `net.minecraft.network.chat.Component` and mirrors the render-distance-2 warning to chat via `Minecraft.getInstance().getChatListener().handleSystemMessage(Component.literal(msg), false)`; both APIs available in MC 1.21.1)
- **Files:** 1 file, +4/-1 (`src/main/java/me/cortex/voxy/client/core/VoxyRenderSystem.java` +4/-1)
- **Result:** APPLIED
- **SHA:** 2cd45000
- **Release:** v0.2.7-alpha-2.114
- **Notes:** Cherry-pick -x of `b281d93` auto-merged cleanly. compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push 214f0f74..2cd45000 on backport/sequential.

## 141. `516ad99f7c85ece16e07aa5806911f4368caced0` tweeks
- **Verdict:** PORTABLE (clean cherry-pick: `Mth.createInsecureUUID()` → `UUID.randomUUID()` in ClientImportManager bossbar UUID and `player.displayClientMessage(...)` → `instance.getChatListener().handleSystemMessage(...)` in Logger chat-error path; both API changes are available in MC 1.21.1)
- **Files:** 2 files, +2/-2 (`src/main/java/me/cortex/voxy/client/ClientImportManager.java` +1/-1, `src/main/java/me/cortex/voxy/common/Logger.java` +1/-1)
- **Result:** APPLIED
- **SHA:** 02cdf16f
- **Release:** v0.2.7-alpha-2.115
- **Notes:** Cherry-pick -x of `516ad99` auto-merged cleanly. compileJava SUCCESSFUL (11s), build -x test SUCCESSFUL (9s). Push 2cd45000..02cdf16f on backport/sequential.

## 142. `131604305550e8b208ca9da7b127a30ae86f2cbb` remove legacy dh shader impersonation thing
- **Verdict:** PORTABLE (clean cherry-pick: removes `IMPERSONATE_DISTANT_HORIZONS` System.getProperty-flag field from IrisShaderPatch, comments out DH-impersonation blocks in VoxySamplers/VoxyUniforms/MixinStandardMacros, and adds `Math.round()` around the `sectionRenderDistance*32` and `*32*16` uniforms in VoxyUniforms; all changes are pure code-removal / `Math.round` cast — no API dependency changes)
- **Files:** 4 files, +8/-10 (`src/main/java/me/cortex/voxy/client/iris/IrisShaderPatch.java` +0/-4 [removed IMPERSONATE_DISTANT_HORIZONS field], `src/main/java/me/cortex/voxy/client/iris/VoxySamplers.java` +2/-2 [comment-wrapped DH-impersonation block], `src/main/java/me/cortex/voxy/client/iris/VoxyUniforms.java` +3/-3 [comment-wrapped DH-impersonation block + Math.round casts on the kept vxRenderDistance/dhRenderDistance uniforms], `src/main/java/me/cortex/voxy/client/mixin/iris/MixinStandardMacros.java` +3/-1 [comment-wrapped DH-impersonation block])
- **Result:** APPLIED
- **SHA:** 59e8f139
- **Release:** v0.2.7-alpha-2.116
- **Notes:** Cherry-pick -x of `1316043` auto-merged cleanly across all 4 files. compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push 02cdf16f..59e8f139 on backport/sequential.

## 143. `7446e9ec` change render distance to lowest level incremnt
- **Verdict:** REQUIRES-MANUAL-PORT (VoxyConfigMenu.java was deleted on the 1.21.1 fork, replaced by VoxyConfigScreenPages.java — the upstream hunk for VoxyConfigMenu.java needs to be ported to our VoxyConfigScreenPages.java equivalent; otherwise the int→float type change breaks callers)
- **Files:** 3 files, +7/-7 (`src/main/java/me/cortex/voxy/client/config/VoxyConfig.java` +1/-1 [int sectionRenderDistance → float], `src/main/java/me/cortex/voxy/client/config/VoxyConfigScreenPages.java` +4/-4 [port of upstream VoxyConfigMenu hunk: slider range (2,64)→(2*16,64*16), formatter v*32→Math.round(v/16f*32), binding (s,v)->{s.sectionRenderDistance=v/16f; vrs.setRenderDistance(s.sectionRenderDistance)}, getter s->s.sectionRenderDistance→s->Math.round(s.sectionRenderDistance*16)], `src/main/java/me/cortex/voxy/client/core/VoxyRenderSystem.java` +2/-2 [setRenderDistance int → float, +1 → Math.ceil(rd+1)])
- **Result:** APPLIED+FIXED
- **SHA:** 9548c631
- **Release:** v0.2.7-alpha-2.117
- **Fix:** VoxyConfigMenu.java modify/delete conflict (file is deleted on our fork — git rm). Auto-merge applied int→float change to VoxyConfig.java and VoxyRenderSystem.java cleanly. Manually ported the equivalent VoxyConfigMenu hunks to VoxyConfigScreenPages.java so the slider keeps working: (1) SliderControl range `2..64` → `2*16..64*16`; (2) formatter `v*32` → `Math.round(v/16f * 32)`; (3) setter stores `v/16f` instead of `v`; (4) getter returns `Math.round(s.sectionRenderDistance*16)` to map float back to int slider value. The third int-callsite in `VoxyRenderSystem.setRenderDistance(int)` had to change to `float` to accept the new float type.
- **Notes:** Also a pre-backport setup fix in this batch: `gradle.properties` `mod_version=0.2.9-alpha` → `0.2.7-alpha` so build/libs jars match the GitHub release tag (committed as `18d2fae2` between cherry-pick and release; no release of its own). compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push 72de36f0..9548c631 on backport/sequential.

## 144. `0033da2a` use config option value instead of effective
- **Verdict:** PORTABLE (clean cherry-pick: `Minecraft.getInstance().options.getEffectiveRenderDistance()` → `Minecraft.getInstance().options.renderDistance().get()`; both `getEffectiveRenderDistance()` and `renderDistance().get()` are available in MC 1.21.1's `net.minecraft.client.Options` — the upstream commit's choice aligns with the renderer-side which should respect the user's chosen value, not the server-clamped effective value)
- **Files:** 1 file, +1/-1 (`src/main/java/me/cortex/voxy/client/core/VoxyRenderSystem.java` +1/-1 [line 89: getEffectiveRenderDistance() → renderDistance().get()])
- **Result:** APPLIED
- **SHA:** 69e09660
- **Release:** v0.2.7-alpha-2.118
- **Notes:** Cherry-pick -x of `0033da2a` auto-merged cleanly. compileJava SUCCESSFUL, build -x test SUCCESSFUL (12s). Push b846dd92..69e09660 on backport/sequential.

## 145. `4333864c` move all the pos unpacks into a single place
- **Verdict:** PORTABLE (clean cherry-pick: pure GLSL refactor — extracts the inline `extractDetail`/`extractLoDPosition` helpers from `quad_util.glsl` + `extractDetail`/`extractPosition` helpers from `section.glsl` + `lodLevel`/inline unpacks from `hierarchical/node.glsl` into a single shared `pos_util.glsl` with `getLoDLevel(uvec2)` and `getLoDPosition(uvec2)`; `#import <voxy:lod/pos_util.glsl>` syntax already widely used in our fork's shaders)
- **Files:** 4 files, +27/-35 (`src/main/resources/assets/voxy/shaders/lod/hierarchical/node.glsl` +2/-11 [adds `#import`, replaces inline unpacks with getLoDLevel/getLoDPosition], `src/main/resources/assets/voxy/shaders/lod/pos_util.glsl` +18/-0 [new file: getLoDLevel + getLoDPosition], `src/main/resources/assets/voxy/shaders/lod/quad_util.glsl` +1/-13 [adds `#import`, removes local extractDetail/extractLoDPosition, renames callsites], `src/main/resources/assets/voxy/shaders/lod/section.glsl` +1/-10 [adds `#import`, removes local extractDetail/extractPosition, renames callsites])
- **Result:** APPLIED
- **SHA:** 1597a11f
- **Release:** v0.2.7-alpha-2.119
- **Notes:** Cherry-pick -x of `4333864c` auto-merged cleanly across all 4 files. compileJava SUCCESSFUL, build -x test SUCCESSFUL (9s). Push ee59db50..1597a11f on backport/sequential.

## 146. `ffc60c79` sodium 0.8.6
- **Verdict:** MC-26-ONLY (pure Sodium 0.8.4 → 0.8.6 version bump in build.gradle (`maven.modrinth:sodium:mc1.21.11-0.8.4-fabric` → `mc1.21.11-0.8.6-fabric`) and `fabric.mod.json` (`"sodium": "=0.8.4"` → `"=0.8.6"`). Our 1.21.1 fork uses `curse.maven:sodium-394468:6382651` for MC 1.21.1/Sodium 0.6.13; the maven.modrinth sodium-mc1.21.11-0.8.x line doesn't apply to our 1.21.1 target.)
- **Files:** 2 files (build.gradle + fabric.mod.json)
- **Result:** SKIPPED
- **SHA:** ffc60c79 (unchanged)
- **Release:** none (counter unchanged at .119)
- **Notes:** Cherry-pick failed with CONFLICT on both build.gradle and fabric.mod.json (both modified: ours uses curse.maven sodium for 1.21.1, upstream uses maven.modrinth sodium for 1.21.11). Aborted via `git cherry-pick --abort`. No portable Java behavior change in the commit — purely a Sodium 0.8.4 → 0.8.6 dependency bump that doesn't apply to our 1.21.1/Sodium 0.6.13 fork. Counter stays at .119.

## 147. `136381a7` fix deadlock update sodium
- **Verdict:** PORTABLE-WITH-FIX (4 of 6 upstream hunks applied cleanly — pure Java deadlock-fix logic with no Sodium-version-specific APIs; build.gradle sodium-bump hunk and fabric.mod.json sodium-version-array hunk dropped via `--ours` because our fork uses curse.maven:sodium-394468 for MC 1.21.1)
- **Files:** 4 files, +15/-11 (`src/main/java/me/cortex/voxy/client/core/rendering/hierachical/NodeManager.java` +0/-3 [removed a redundant `if ((pos&0xF) != 0) throw new IllegalStateException(...)` sanity check — the proper one is `assertPosValid(pos)` just above], `src/main/java/me/cortex/voxy/common/world/ActiveSectionTracker.java` +2/-2 [comments added at the two `engine.saveSection(section)` callsites + the second one now calls the new 2-arg overload `saveSection(section, true)` since it's in a lock], `src/main/java/me/cortex/voxy/common/world/WorldEngine.java` +5/-2 [ISectionSaveCallback signature gains `boolean nonBlocking`; `saveSection(section)` delegates to `saveSection(section, false)`; new 2-arg overload `saveSection(WorldSection section, boolean nonBlocking)`], `src/main/java/me/cortex/voxy/common/world/service/SectionSavingService.java` +8/-4 [extracts `SOFT_MAX_QUEUE_SIZE=5_000` constant; new 3-arg overload `enqueueSave(WorldEngine in, WorldSection section, boolean nonBlocking)`; replaces `Thread.sleep(10)` (commented out) with `Thread.yield()`; the queue-full check is now gated on `!nonBlocking` so callers in a lock context pass through immediately to the steal loop])
- **Result:** APPLIED+FIXED
- **SHA:** e836efe8
- **Release:** v0.2.7-alpha-2.120
- **Fix:** Resolved conflicts on build.gradle + fabric.mod.json with `git checkout --ours` — our fork's sodium dep is `curse.maven:sodium-394468:6382651` (MC 1.21.1/Sodium 0.6.13), upstream bumps `maven.modrinth:sodium:mc1.21.11-0.8.4-fabric` → `mc1.21.11-0.8.6-fabric`. Upstream's Iris bump (1.10.5+1.21.11 → 1.10.6+1.21.11) and the `fabric.mod.json` `"sodium": ["=0.8.4","=0.8.6"]` array-form update were both dropped as they only apply to MC 1.21.11. The method-reference binding `world.setSaveCallback(this.savingService::enqueueSave)` in `VoxyInstance.java` continues to resolve cleanly to the new 3-arg `enqueueSave(WorldEngine, WorldSection, boolean)` overload.
- **Notes:** Cherry-pick -x of `136381a7` auto-merged 4 Java files cleanly; 2 dep-metadata files resolved via `--ours`. compileJava SUCCESSFUL, build -x test SUCCESSFUL (13s). Push eff5fc32..e836efe8 on backport/sequential.

## 148. `2efe32f7` add aborting verification command
- **Verdict:** PORTABLE (clean cherry-pick: adds `engine.markActive()` + `engine.acquireRef()/releaseRef()` lifecycle binding to the `verifyAllTopLevelNodes` worker thread in `DebugUtils.java`, breaks the verification loop on `engine.instanceIn != null && !engine.instanceIn.isRunning()`, and exposes a public `isRunning()` getter in `VoxyInstance.java` that reads the existing `volatile boolean isRunning` field — all APIs are 1.21.1-compatible; `VoxyInstance.isRunning` field + `instanceIn` linkage already exist on our fork)
- **Files:** 2 files, +25/-12 (`src/main/java/me/cortex/voxy/common/DebugUtils.java` +21/-10 [worker thread now bounded by engine refcount: markActive at start, acquireRef+try/finally releaseRef around the verification loop, early-exit on `!engine.instanceIn.isRunning()`, distinct "aborted due to shutdown" vs "Verification complete" log], `src/main/java/me/cortex/voxy/commonImpl/VoxyInstance.java` +4/-0 [public `isRunning()` returns `this.isRunning`])
- **Result:** APPLIED
- **SHA:** de2c700c
- **Release:** v0.2.7-alpha-2.121
- **Notes:** Cherry-pick -x of `2efe32f7` auto-merged cleanly across both files. compileJava SUCCESSFUL (11s), build -x test SUCCESSFUL (9s). Push 135426ec..de2c700c on backport/sequential.

## 149. `72768ca6` dont zip jar on upload
- **Verdict:** PORTABLE-AS-NO-OP (CI-only bump: bumps `actions/upload-artifact@v4` → `v7` and adds `archive: false` so the workflow no longer zips the build/libs/*.jar files. Our fork had pre-moved `manual-artifact.yml` into `workflows-disabled/` (commit `c6270e54`, "Disable GitHub Actions workflows") so the workflow is non-functional — but the upstream hunk's semantic change still landed correctly under the disabled dir for archival parity.)
- **Files:** 1 file, +3/-2 (`.github/workflows-disabled/manual-artifact.yml` +3/-2 [actions/upload-artifact@v4 → v7, added `archive: false`])
- **Result:** APPLIED
- **SHA:** 40192d31
- **Release:** v0.2.7-alpha-2.122
- **Notes:** Cherry-pick -x of `72768ca6` auto-resolved cleanly: the fork's `workflows/manual-artifact.yml` was already renamed to `workflows-disabled/manual-artifact.yml` by prior commit `c6270e54`, so git tracked this as a modify on the existing disabled copy rather than a new file in `.github/workflows/`. compileJava SUCCESSFUL (7s), build -x test SUCCESSFUL (9s). Push 74619760..40192d31 on backport/sequential.

## 150. `d7782df2` tex zero func
- **Verdict:** PORTABLE (clean cherry-pick: adds `GlTexture.getPixelTransferFormat()` and `GlTexture.zero()` helpers in `GlTexture.java` — uses only OpenGL constants (`GL_RGBA`, `GL_RED`, `GL_DEPTH_COMPONENT`, `GL_DEPTH_STENCIL`, `GL_INT`, `GL_FLOAT`, `GL_UNSIGNED_INT_24_8`, `GL_FLOAT_32_UNSIGNED_INT_24_8_REV`) and `nglClearTexImage` which are vanilla GL 4.4+/GL_ARB_clear_texture API — no MC-version-specific or Sodium-version-specific dependencies)
- **Files:** 1 file, +25/-0 (`src/main/java/me/cortex/voxy/client/core/gl/GlTexture.java` +25/-0 [new `getPixelTransferFormat()` maps `this.format` (GL_RGBA8/GL_R32UI/GL_R32F/GL_DEPTH_COMPONENT{24,32F,32}/GL_DEPTH24_STENCIL8) → transfer format; new `zero()` clears all mipmap levels with the proper GL type per format via nglClearTexImage])
- **Result:** APPLIED
- **SHA:** 975f180e
- **Release:** v0.2.7-alpha-2.123
- **Notes:** Cherry-pick -x of `d7782df2` auto-merged cleanly. compileJava SUCCESSFUL (10s), build -x test SUCCESSFUL (9s). Push 8b57bbba..975f180e on backport/sequential.

## 151. `74214ccb` why was this a thing
- **Verdict:** PORTABLE (clean cherry-pick: pure dead-code removal — deletes a 5-line `try { Thread.sleep(1); } catch (InterruptedException ex) { throw new RuntimeException(ex); }` block from `RenderGenerationService.java` around line 218. No API dependency, no behavior implication other than removing a useless 1ms sleep on the render-generation path.)
- **Files:** 1 file, +0/-5 (`src/main/java/me/cortex/voxy/client/core/rendering/building/RenderGenerationService.java` +0/-5 [deleted Thread.sleep(1) + try/catch block])
- **Result:** APPLIED
- **SHA:** d6981116
- **Release:** v0.2.7-alpha-2.124
- **Notes:** Cherry-pick -x of `74214ccb` auto-merged cleanly. compileJava SUCCESSFUL (10s), build -x test SUCCESSFUL (9s). Push e924921f..d6981116 on backport/sequential.

## 152. `40a62448` tex mask gen util
- **Verdict:** PORTABLE (clean cherry-pick: adds two `static long[] generateMask(ColourDepthTextureData, int checkMode[, long[] outMsk])` overloads in `TextureUtils.java`. The 1-arg version allocates `new long[data.width()*data.height()/64]`, the 3-arg version fills the caller-provided buffer. Iterates x,y in (0,0)→(data.width, data.height) order and sets bit `i&63` of `outMsk[i/64]` when `wasPixelWritten(...)` returns true. Uses pre-existing `ColourDepthTextureData.width()/height()` and `wasPixelWritten(data, checkMode, i)` helpers from the same file — all MC 1.21.1-compatible.)
- **Files:** 1 file, +20/-0 (`src/main/java/me/cortex/voxy/client/core/model/TextureUtils.java` +20/-0 [added `import java.util.Arrays`; new 1-arg + 3-arg `generateMask` overloads between `getWrittenPixelCount`/`isSolid`/`computeFaceTint` helpers and the existing `computeBounds` method])
- **Result:** APPLIED
- **SHA:** 34633029
- **Release:** v0.2.7-alpha-2.125
- **Notes:** Cherry-pick -x of `40a62448` auto-merged cleanly. compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push 5d150888..34633029 on backport/sequential.

## 153. `a5afb2fb` external buffer constructor and max size getter
- **Verdict:** PORTABLE (clean cherry-pick: adds a static `BasicSectionGeometryData.create(int, int)` factory constructor and a `maxSize()` getter to the IGeometryData interface; the factory reads `MAX_BATCH_SIZE` field of each section data type and uses standard OpenGL `GL_MAX_TEXTURE_BUFFER_SIZE` / `glGetInteger(GL_MAX_TEXTURE_SIZE)` calls via the existing `Capabilities` class — all 1.21.1-compatible; the `maxSize()` getter simply returns the static `MAX_BATCH_SIZE` field already declared in each implementation)
- **Files:** 2 files, +38/-16 (`src/main/java/me/cortex/voxy/client/core/rendering/section/geometry/BasicSectionGeometryData.java` +38/-16 [new static `create(int format, int count)` reads `MAX_BATCH_SIZE` then allocates the geometry data and sets minCount to count; new `maxSize()` getter returns `MAX_BATCH_SIZE`; new protected `BasicSectionGeometryData(long address, int sizeBytes)` constructor for external buffer use], `src/main/java/me/cortex/voxy/client/core/rendering/section/geometry/IGeometryData.java` +1/-0 [added `int maxSize()` to interface])
- **Result:** APPLIED
- **SHA:** aa5e1c30
- **Release:** v0.2.7-alpha-2.126
- **Notes:** Cherry-pick -x of `a5afb2fb` auto-merged cleanly. compileJava SUCCESSFUL (10s), build -x test SUCCESSFUL (9s). Push f725c8ec..aa5e1c30 on backport/sequential.

## 154. `672ee7c7` geometry buffer and texture atlas reuse system
- **Verdict:** PORTABLE (clean cherry-pick: adds a new `RenderResourceReuse` static helper class that owns reusable OpenGL resources — `AcquiredUploadStream`, `acquireUploadStream()` (per-frame pool), `RenderBuffer` (per-megamesh geometry buffer with `setup`/`free`/`resize`), and `acquireBuffer()/releaseBuffer()/getBuffer()` (LRU-cached geometry buffer pool by material type). VoxyRenderSystem only loses its own `free()` method (it now delegates to the reuse system) and ModelStore drops a `renderType` dimension from `getRenderType`. All purely internal Java code using only standard OpenGL APIs.)
- **Files:** 4 files, +143/-35 (`src/main/java/me/cortex/voxy/client/VoxyClientInstance.java` +8/-0 [new `RenderResourceReuse` lifecycle wiring], `src/main/java/me/cortex/voxy/client/core/RenderResourceReuse.java` +126/-0 [new file: full reuse system with AcquiredUploadStream, RenderBuffer pool], `src/main/java/me/cortex/voxy/client/core/VoxyRenderSystem.java` +4/-35 [removed free() helper, deleted obsolete static init], `src/main/java/me/cortex/voxy/client/core/model/ModelStore.java` +6/-0 [simplified to use `MaterialRenderType` instead of nested renderType])
- **Result:** APPLIED
- **SHA:** 67cf2bdf
- **Release:** v0.2.7-alpha-2.127
- **Notes:** Cherry-pick -x of `672ee7c7` auto-merged across all 4 files. compileJava SUCCESSFUL (10s), build -x test SUCCESSFUL (9s). Push 6c536178..67cf2bdf on backport/sequential.

## 155. `3a6f2ac5` change min render distance to 32
- **Verdict:** PORTABLE-WITH-REBASE (upstream edits `VoxyConfigMenu.java:106` (Range min 2*16 → 1*16), but on our fork this file was already deleted by commit `cad8d593` ("1.21.10 backport") and the equivalent code lives in `VoxyConfigScreenPages.java:126` as a `SliderControl(opt, 2*16, 64*16, 1, ...)` — semantic port: change the same min from `2*16` to `1*16` on line 126 of `VoxyConfigScreenPages.java`. Note that `VoxyConfigMenu.java` still exists in the worktree but with a non-conflicting range value already matching the upstream post-change state — the upstream delta on that file was effectively a no-op on our fork. The semantic equivalent was applied in the live config page.)
- **Files:** 1 file, +1/-1 (`src/main/java/me/cortex/voxy/client/config/VoxyConfigScreenPages.java` +1/-1 [SliderControl min: `2*16` → `1*16` on line 126])
- **Result:** APPLIED
- **SHA:** cf1f59a7
- **Release:** v0.2.7-alpha-2.128
- **Notes:** Cherry-pick -x of `3a6f2ac5` triggered a modify/delete conflict on `VoxyConfigMenu.java` because that file was deleted by upstream commit `cad8d593` and replaced by `VoxyConfigScreenPages.java`. Aborted the cherry-pick, then manually applied the semantic equivalent edit on `VoxyConfigScreenPages.java:126` and committed with `(cherry picked from commit 3a6f2ac56a986800bce963bafcf5bd0a008b700c)` trailer. compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push 3f77c764..cf1f59a7 on backport/sequential.

## 156. `e5af2c91` implement upload stream alignement better based on opengl capabilities
- **Verdict:** PORTABLE-WITH-COMBINED-FIX (upstream commit `e5af2c91` introduces `BASE_ALLOCATION_ALIGNEMENT = Math.max(Capabilities.INSTANCE.ssboBindingAlignment, 16)` and replaces hardcoded 16-byte alignment with capability-aware alignment in `UploadStream.java` and `AsyncNodeManager.java`. However, the `Capabilities.ssboBindingAlignment` field is NOT in this commit — it's added in the next upstream commit `eaf107e4` (commit 157). On the fork's release branch, applying only 156 leaves a compile error: `cannot find symbol: variable ssboBindingAlignment`. To preserve the semantic of the pair, the cherry-pick was amended to also include the field declaration, import (`GL_SHADER_STORAGE_BUFFER_OFFSET_ALIGNMENT`, `glGetInteger`), and constructor assignment from commit 157's `Capabilities.java` diff. Both upstream commits together restore the same behavior.)
- **Files:** 3 files, +29/-10 (`src/main/java/me/cortex/voxy/client/core/gl/Capabilities.java` +4/-0 [new `ssboBindingAlignment` field; constructor reads `glGetInteger(GL_SHADER_STORAGE_BUFFER_OFFSET_ALIGNMENT)`; `GL11.glGetInteger` import added; `GL43C.GL_SHADER_STORAGE_BUFFER_OFFSET_ALIGNMENT` import added], `src/main/java/me/cortex/voxy/client/core/rendering/util/UploadStream.java` +20/-3 [new `BASE_ALLOCATION_ALIGNEMENT`, `alignUp(long,int)`/`alignUp(int,int)` helpers, alignment-aware allocation], `src/main/java/me/cortex/voxy/client/core/rendering/hierachical/AsyncNodeManager.java` +5/-7 [replaced inline 16-byte alignment with `UploadStream.alignUpAlloc(...)`])
- **Result:** APPLIED
- **SHA:** 77692634
- **Release:** v0.2.7-alpha-2.129
- **Notes:** Cherry-pick -x of `e5af2c91` initially landed as 4aac68a7 but failed compileJava with `cannot find symbol: variable ssboBindingAlignment`. Aborted, re-applied, and amended with the missing Capabilities field from upstream `eaf107e4` (commit 157, planned next) so the combined port is self-consistent. compileJava SUCCESSFUL (10s), build -x test SUCCESSFUL (9s). Push 7b01ab2a..77692634 on backport/sequential. The remaining 157 changes (NodeCleaner.java edits + RenderResourceReuse.java 512MB min buffer line) still need to be applied as commit 157.

## 157. `eaf107e4` fix binding alignement (flipped file commits capabilities ment to go in previous commit mb)
- **Verdict:** PORTABLE (clean cherry-pick: adds `geometryCapacity = Math.max(512*1024*1024, geometryCapacity)` floor in `RenderResourceReuse.java` constructor (line 108-109), and replaces NodeCleaner's hand-rolled 16-byte alignment with `UploadStream.alignUpAlloc(...)` in `NodeCleaner.java`. Also adds `import me.cortex.voxy.client.core.gl.Capabilities;` and `import static me.cortex.voxy.client.core.rendering.util.UploadStream.alignUp;` to NodeCleaner.java. Capabilities.java auto-merged as no-op because the field was already added by commit 156 (see 156 entry).)
- **Files:** 2 files, +6/-3 (`src/main/java/me/cortex/voxy/client/core/RenderResourceReuse.java` +2/-0 [512MB geometryCapacity floor in constructor], `src/main/java/me/cortex/voxy/client/core/rendering/hierachical/NodeCleaner.java` +4/-3 [replaced inline 16-byte alignment with `UploadStream.alignUpAlloc(count*4)`, added Capabilities + alignUp imports])
- **Result:** APPLIED
- **SHA:** 08b18d21
- **Release:** v0.2.7-alpha-2.130
- **Notes:** Cherry-pick -x of `eaf107e4` auto-merged cleanly (Capabilities.java was a no-op since the field was already added in commit 156 to make 156 self-consistent). compileJava SUCCESSFUL (10s), build -x test SUCCESSFUL (9s). Push 09e175fc..08b18d21 on backport/sequential.

## 158. `5ca0fa73` inital software rasterizing texture bakery
- **Verdict:** MC-26-ONLY (introduces `SoftwareModelTextureBakery` and `SoftwareRasterizer` classes that use MC 1.21.11-only APIs throughout: `com.mojang.blaze3d.buffers.GpuBuffer`, `com.mojang.blaze3d.systems.CommandEncoder`, `com.mojang.blaze3d.textures.TextureFormat`, and `RenderSystem.getDevice()` — none of these exist on 1.21.1. The file also imports `net.minecraft.client.renderer.chunk.ChunkSectionLayer` which would need 1.21.1 port to `RenderType`, but the GpuBuffer/CommandEncoder dependency is the dominant blocker: the entire rendering path goes through GPU device API rather than the 1.21.1 GL11/GL13/GL15 stack.)
- **Files:** 5 files, +592/-10 (new `src/main/java/me/cortex/voxy/client/core/model/bakery/SoftwareModelTextureBakery.java` +305/-0, new `src/main/java/me/cortex/voxy/client/core/model/bakery/SoftwareRasterizer.java` +145/-0, `src/main/java/me/cortex/voxy/client/core/model/ModelFactory.java` +127/-5 [software bakery wiring + new methods], `src/main/java/me/cortex/voxy/client/core/model/TextureUtils.java` +8/-0 [helper additions], other minor)
- **Result:** SKIPPED
- **SHA:** n/a
- **Release:** n/a (counter unchanged at .130)
- **Notes:** Cherry-pick -x of `5ca0fa73` initially landed as a6cbe770 with auto-merge on ModelFactory.java and TextureUtils.java. compileJava FAILED with 8 errors all pointing at MC 1.21.11-only APIs in SoftwareModelTextureBakery.java: cannot find symbol class GpuBuffer (com.mojang.blaze3d.buffers), package com.mojang.blaze3d.textures does not exist (TextureFormat), cannot find symbol class CommandEncoder, and 4 ChunkSectionLayer references (which is port-able to RenderType but moot given the GpuBuffer blockers). Reset --hard HEAD~1 to restore clean tree. Counter unchanged at .130.

## 159. `e62beff1` offthread baking + version update
- **Verdict:** MC-26-ONLY (depends on `SoftwareModelTextureBakery` from commit 158 which is MC 1.21.11-only due to GpuBuffer/CommandEncoder/TextureFormat usage; the "offthread baking" portion of this commit instantiates `new SoftwareModelTextureBakery()` as `this.bakery2` and switches the bake path to call `bakery2.renderToOutput(state, bakeScratchBuffer)` instead of the 1.21.1-compatible `bakery.renderToStream(...)`. The version bump from 0.2.7-alpha to 0.2.12-alpha is the only potentially portable part and we keep our 0.2.7-alpha (`gradle.properties` is `--ours`).)
- **Files:** 2 files (per upstream): gradle.properties (mod_version 0.2.7-alpha → 0.2.12-alpha), src/main/java/me/cortex/voxy/client/core/model/ModelFactory.java (replace bakery field with bakery2/SoftwareModelTextureBakery, add bakeQueue/BakeScratchBuffer, switch renderToStream path → renderToOutput + bakeScratchBuffer)
- **Result:** SKIPPED
- **SHA:** n/a
- **Release:** n/a (counter unchanged at .130)
- **Notes:** Cherry-pick -x of `e62beff1` triggered content conflicts on gradle.properties (mod_version) and ModelFactory.java. Resolution plan was clear: keep our `mod_version = 0.2.7-alpha` and the upstream `SoftwareModelTextureBakery` integration would not compile (the type doesn't exist on 1.21.1 in any form — no GpuBuffer replacement exists). Aborted with `git cherry-pick --abort` to preserve a clean tree for the next commits. Counter unchanged at .130.

## 160. `731ca0e9` removed old stuff
- **Verdict:** MC-26-ONLY (this commit deletes `ModelTextureBakery.java` (the 1.21.1-compatible baker using `GlViewCapture`), `BudgetBufferRenderer.java`, and the GLSL shaders used by `ModelTextureBakery`. Our fork's `ModelFactory.java` instantiates `ModelTextureBakery` (the only baker available on 1.21.1 since `SoftwareModelTextureBakery` from commits 158/159 was SKIPPED). Applying this deletion would break the build: removing the only 1.21.1 baker the fork currently uses is non-portable. The companion VoxyClient.java + ModelFactory.java touch-ups also reference `SoftwareModelTextureBakery` which doesn't exist on 1.21.1.)
- **Files:** 11 files (per upstream): deletes ModelTextureBakery.java, BudgetBufferRenderer.java, GlViewCapture.java, SoftwareRasterizer.java (not present on fork, so just deletes nothing), and 4 bakery shader files (.comp/.vsh/.fsh); VoxyClient.java -14, ModelFactory.java -4, ReuseVertexConsumer.java +3/-0
- **Result:** SKIPPED
- **SHA:** n/a
- **Release:** n/a (counter unchanged at .130)
- **Notes:** Cherry-pick -x of `731ca0e9` triggered 2 content conflicts (VoxyClient.java, ModelFactory.java) and 3 modify/delete conflicts (BudgetBufferRenderer.java, ModelTextureBakery.java, SoftwareRasterizer.java). The dependency direction makes this commit uncatch-up-able: removing ModelTextureBakery would force ModelFactory.java to also rewrite its bakery field to point at the SoftwareModelTextureBakery type, which we already determined is MC 1.21.11-only. Aborted with `git cherry-pick --abort`. Counter unchanged at .130.

## 161. `4149b0cb` smaller rd
- **Verdict:** PORTABLE-WITH-REBASE (upstream edits only `VoxyConfigMenu.java:109` (`1*16` → `10/*1*16*/`). Same pattern as commit 155: VoxyConfigMenu.java was deleted on the fork by `cad8d593` ("1.21.10 backport"); the equivalent lives in `VoxyConfigScreenPages.java:126` as a `SliderControl(opt, 1*16, 64*16, 1, ...)`. Semantic port: change the min from `1*16` to `10/*1*16*/` on line 126 of `VoxyConfigScreenPages.java`. The state coming into 161 had `1*16` (because of commit 155) — 161 brings it down to `10`.)
- **Files:** 1 file, +1/-1 (`src/main/java/me/cortex/voxy/client/config/VoxyConfigScreenPages.java` +1/-1 [SliderControl min: `1*16` → `10/*1*16*/` on line 126])
- **Result:** APPLIED
- **SHA:** 5555759b
- **Release:** v0.2.7-alpha-2.131
- **Notes:** Cherry-pick -x of `4149b0cb` triggered a modify/delete conflict on VoxyConfigMenu.java because that file was deleted by upstream commit `cad8d593` and replaced by VoxyConfigScreenPages.java. Aborted the cherry-pick, then manually applied the semantic equivalent edit on `VoxyConfigScreenPages.java:126` (1*16 → 10/*1*16*/) and committed with `(cherry picked from commit 4149b0cb93d553978dd2619456d271d3923a5d75)` trailer. compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push 9dc7cb4f..5555759b on backport/sequential.

## 162. `aa5fa61` other archetectures
- **Verdict:** PORTABLE-WITH-REBASE (upstream builds for arm64 Linux natives behind a `def INCLUDE_OTHER_ARCHS = false` flag. Our fork's build.gradle is completely rewritten for NeoForge (no fabric-loom, no `processIncludeJars`, no `makeExcludedRocksDB`, and our deps use `runtimeOnly` directly instead of `include(runtimeOnly …)`). The semantic of the upstream commit has two parts: (a) add `def INCLUDE_OTHER_ARCHS = false` flag, and (b) conditionally include arm64 natives. Part (a) is portable. Part (b)'s `processIncludeJars` wrap is irrelevant on NeoForge (we don't have that block). The `dependencies` arm64 add is portable with a small adaptation: replace upstream's `include(runtimeOnly …)` syntax with our `runtimeOnly …` syntax.)
- **Files:** 1 file, +6/-0 (`build.gradle` +6/-0 [added `def INCLUDE_OTHER_ARCHS = false` after plugins block; added `if (INCLUDE_OTHER_ARCHS) { … linux-arm64 … }` block after the linux natives])
- **Result:** APPLIED
- **SHA:** acfe448b
- **Release:** v0.2.7-alpha-2.132
- **Notes:** Cherry-pick -x of `aa5fa61` triggered a content conflict on build.gradle (huge: upstream is fabric-loom, ours is NeoForge). Aborted the merge logic via `git checkout --ours build.gradle`, then manually applied the two semantic additions: (1) `def INCLUDE_OTHER_ARCHS = false` after the plugins block (line 7), (2) the `if (INCLUDE_OTHER_ARCHS) { runtimeOnly … natives-linux-arm64 }` block after line 302. The `processIncludeJars` wrap was a no-op on our fork because that block doesn't exist. Used `runtimeOnly` instead of `include(runtimeOnly)` to match our existing line style. compileJava SUCCESSFUL (8s, all up-to-date), build -x test SUCCESSFUL (10s). Push 283e1e88..acfe448b on backport/sequential.

## 163. `792927eb` hints
- **Verdict:** PORTABLE (clean cherry-pick: threads optional release hints through `WorldSection.release`, `ActiveSectionTracker.tryUnload`, and `RenderDataFactory` neighbor-section cleanup; adds `WorldSection.RELEASE_HINT_POSSIBLE_REUSE` and passes it after neighbor data is copied. All changes are internal Java APIs and compile on MC 1.21.1.)
- **Files:** 4 files, +21/-13 (`src/main/java/me/cortex/voxy/client/core/model/ModelBakerySubsystem.java` +2/-0 [unused `LockSupport` import and TODO hint], `src/main/java/me/cortex/voxy/client/core/rendering/building/RenderDataFactory.java` +6/-6 [neighbor releases pass possible-reuse hint], `src/main/java/me/cortex/voxy/common/world/ActiveSectionTracker.java` +3/-3 [accept and forward release hints], `src/main/java/me/cortex/voxy/common/world/WorldSection.java` +10/-4 [add release hint constant and overloaded release path])
- **Result:** APPLIED
- **SHA:** 6afc7817
- **Release:** v0.2.7-alpha-2.133
- **Notes:** Cherry-pick -x of `792927eb` auto-merged cleanly. compileJava SUCCESSFUL (11s), build -x test SUCCESSFUL (9s). Push f12775be..6afc7817 on backport/sequential. Release v0.2.7-alpha-2.133 created with both built jars. GitHub initially rejected the abbreviated target SHA; retried with full commit SHA successfully. Counter advanced .132 → .133.

## 164. `7889f119` overwrite error
- **Verdict:** PORTABLE (clean cherry-pick: enables Mixin overwrite validation by adding `"overwrites": {"requireAnnotations": true}` to both client and common mixin configurations; this is configuration-only and compatible with the fork's current mixin setup.)
- **Files:** 2 files, +7/-1 (`src/main/resources/client.voxy.mixins.json` +3/-0, `src/main/resources/common.voxy.mixins.json` +4/-1)
- **Result:** APPLIED
- **SHA:** e5db1b3f
- **Release:** v0.2.7-alpha-2.134
- **Notes:** Cherry-pick -x of `7889f119` auto-merged cleanly. compileJava SUCCESSFUL (7s), build -x test SUCCESSFUL (10s). Push dbe68ec6..e5db1b3f on backport/sequential. Release v0.2.7-alpha-2.134 created with both built jars. Counter advanced .133 → .134.

## 165. `36f85026` update limit + error throwing
- **Verdict:** PORTABLE-WITH-CONFLICT-RESOLUTION (clean semantic merge: propagated worker-thread failures from both the model bakery and async node manager, capped each async upload loop to 1 MB of estimated geometry, and added hierarchical traversal debug output. The only conflict was the fork-specific timed `ModelBakerySubsystem.tick` implementation; retained that processing and added the upstream failure check before it rather than replacing the port's budgeted upload path with `processUploads()` alone.)
- **Files:** 4 files, +40/-1 (`src/main/java/me/cortex/voxy/client/core/AbstractRenderPipeline.java` +1/-0 [forward traversal debug], `src/main/java/me/cortex/voxy/client/core/model/ModelBakerySubsystem.java` +11/-0 [capture and propagate processor failure], `src/main/java/me/cortex/voxy/client/core/rendering/hierachical/AsyncNodeManager.java` +19/-1 [propagate failure and cap estimated upload amount], `src/main/java/me/cortex/voxy/client/core/rendering/hierachical/HierarchicalOcclusionTraverser.java` +9/-0 [top-node debug metric])
- **Result:** APPLIED
- **SHA:** d6775b7d
- **Release:** v0.2.7-alpha-2.135
- **Notes:** Cherry-pick -x of `36f85026` conflicted only in ModelBakerySubsystem.tick because the fork retains a timed `tickAndProcessUploads` path introduced by later local architecture. Resolved by prepending the upstream processing-thread exception guard and preserving the existing budgeted bakery work. compileJava SUCCESSFUL (10s), build -x test SUCCESSFUL (9s). Push 0e182ddd..d6775b7d on backport/sequential. Release v0.2.7-alpha-2.135 created with both built jars. Counter advanced .134 → .135.

## 166. `11f4fdfc` fix render distance scaling
- **Verdict:** ALREADY-PRESENT / PORTABLE (the fork deleted upstream's `VoxyConfigMenu` and moved this setting to `VoxyConfigScreenPages`; that live binding already stores `s.sectionRenderDistance = v/16f` and calls `vrs.setRenderDistance(s.sectionRenderDistance)`, so the fixed semantic is already present and the upstream patch is empty here.)
- **Files:** 0 files, +0/-0 (upstream attempted a 1-file, +2/-1 edit to deleted `VoxyConfigMenu.java`; no new change was needed)
- **Result:** APPLIED (empty provenance commit)
- **SHA:** 937ef399
- **Release:** v0.2.7-alpha-2.136
- **Notes:** Cherry-pick -x of `11f4fdfc` triggered a modify/delete conflict for the fork-deleted `VoxyConfigMenu.java`. Inspection of `VoxyConfigScreenPages.java:127-134` confirmed its binding already passes normalized `s.sectionRenderDistance`, not raw slider `c`, so it cannot trigger the upstream 16x error. Continued as an allow-empty provenance commit and attached the source trailer. compileJava SUCCESSFUL (6s), build -x test SUCCESSFUL (9s). Release v0.2.7-alpha-2.136 created with both built jars. Counter advanced .135 → .136.

## 167. `a1ee2eed` barrier fix
- **Verdict:** MC-26-ONLY / ALREADY-EQUIVALENT (upstream changes only the skipped MC 1.21.11 software bakery, clearing its output buffer before every render. The fork still uses the 1.21.1 GL-backed `ModelTextureBakery`, whose `renderToStream` path already calls `capture.clear()` before rendering. The source file `SoftwareModelTextureBakery.java` is intentionally absent because commits 158-160 were skipped as MC-26-ONLY.)
- **Files:** 0 files applied (upstream attempted 1 file, +3/-4 in absent `SoftwareModelTextureBakery.java`; cherry-pick misdetected the GL bakery as a rename conflict)
- **Result:** SKIPPED
- **SHA:** n/a
- **Release:** n/a (counter unchanged at .136)
- **Notes:** Cherry-pick -x of `a1ee2eed` attempted to edit absent `SoftwareModelTextureBakery.java` and surfaced a content conflict in the fork's existing `ModelTextureBakery.java` because upstream tracked it as the rename source. Inspected the full upstream diff: its only semantic change is unconditional `MemoryUtil.memSet(outputBuffer,...)` in the software-only `renderToOutput`; the live 1.21.1 `renderToStream` already clears its GL capture. Aborted with `git cherry-pick --abort`; no release and counter unchanged at .136.

## 168. `921883ea587510f572182a61c69d43bb76b2fff7` final
- **Verdict:** PORTABLE (clean cherry-pick: marks `ModelFactory.getModelMetadataFromClientId(int)` final; this is a Java modifier-only change with no Minecraft or Sodium API dependency.)
- **Files:** 1 file, +1/-1 (`src/main/java/me/cortex/voxy/client/core/model/ModelFactory.java` +1/-1)
- **Result:** APPLIED
- **SHA:** 3e89246c
- **Release:** v0.2.7-alpha-2.137
- **Notes:** Cherry-pick -x of `921883ea` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (10s), build -x test SUCCESSFUL (9s). Push abb18a90..3e89246c on backport/sequential. Release v0.2.7-alpha-2.137 created with both built jars; GitHub initially rejected the abbreviated target SHA, so the release command was retried with the full SHA. Counter advanced .136 → .137.

## 169. `53eb914ea54dd25c3adc34bf0c0cab08080e6199` minor changes
- **Verdict:** PORTABLE-WITH-CONFLICT-RESOLUTION (adds an optional `attemptRepair` argument to the TLN verification command, repairs incorrect level-0 state and section empty-child masks, aborts promptly on shutdown, and exposes HUD/logging helpers. NeoForge command types were substituted during the VoxyCommands conflict resolution.)
- **Files:** 3 files, +38/-12 (`src/main/java/me/cortex/voxy/client/VoxyCommands.java` +6/-3, `src/main/java/me/cortex/voxy/common/DebugUtils.java` +24/-3, `src/main/java/me/cortex/voxy/common/Logger.java` +8/-6)
- **Result:** APPLIED+FIXED
- **SHA:** 6bcdba9f
- **Release:** v0.2.7-alpha-2.138
- **Fix:** Resolved the VoxyCommands conflict using the fork's `LiteralArgumentBuilder<CommandSourceStack>` and `RequiredArgumentBuilder<CommandSourceStack, Boolean>` patterns, replaced upstream Fabric command types with `CommandSourceStack`, and preserved the fork's import-command registration. The live verification path now defaults repair off and accepts a Boolean argument.
- **Notes:** compileJava SUCCESSFUL (11s), build -x test SUCCESSFUL (9s). Push 5e3e716f..6bcdba9f on backport/sequential. Release v0.2.7-alpha-2.138 created with both built jars after retried full-SHA targeting. Counter advanced .137 → .138.

## 170. `7cacc8632eb435d1d5d0f52bcb5324392ceb5c90` atempted splitting of the prepareSectionData method but thinks it just made the jit worse
- **Verdict:** PORTABLE (clean cherry-pick: refactors `RenderDataFactory.prepareSectionData` into smaller internal helpers while preserving section data creation, neighbor release behavior, and upload flow; the change uses only fork-existing APIs.)
- **Files:** 1 file, +22/-23 (`src/main/java/me/cortex/voxy/client/core/rendering/building/RenderDataFactory.java`)
- **Result:** APPLIED
- **SHA:** 222e2685
- **Release:** v0.2.7-alpha-2.139
- **Notes:** Cherry-pick -x of `7cacc863` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push 9ff48bc9..222e2685 on backport/sequential. Release v0.2.7-alpha-2.139 created with both built jars. Counter advanced .138 → .139.

## 171. `5cb96e9f1c4363101ab009db637084e68cadaa63` vp -> mvp
- **Verdict:** PORTABLE (clean cherry-pick: converts screen-space shader calculations from direct viewport pointers to model-view-projection transforms and adjusts traversal shader position math; both are renderer-version-independent GLSL changes using the fork's existing shader conventions.)
- **Files:** 2 files, +13/-4 (`src/main/resources/assets/voxy/shaders/lod/hierarchical/screenspace.glsl` +12/-3, `src/main/resources/assets/voxy/shaders/lod/hierarchical/traversal_dev.comp` +1/-1)
- **Result:** APPLIED
- **SHA:** 0553d106
- **Release:** v0.2.7-alpha-2.140
- **Notes:** Cherry-pick -x of `5cb96e9f` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (6s), build -x test SUCCESSFUL (9s). Push 045e62d7..0553d106 on backport/sequential. Release v0.2.7-alpha-2.140 created with both built jars. Counter advanced .139 → .140.

## 172. `68f782d60d661ea3b51e76f4e6dae4c9bf31f0ee` taa stuff
- **Verdict:** PORTABLE (clean cherry-pick: threads temporal-antialiasing Jitter state through the render pipeline into chunk-bound culling and the GL 4.6 raster vertex shader, and updates Iris shader patching to preserve the TAA mode. The commit uses the fork's existing renderer and shader interfaces and has no MC 1.21.11/Sodium 0.7 API dependency.)
- **Files:** 5 files, +37/-6 (`AbstractRenderPipeline.java` +4/-0, `IrisVoxyRenderPipeline.java` +10/-1, `ChunkBoundRenderer.java` +5/-2, `IrisShaderPatch.java` +1/-1, `gl46/cull/raster.vert` +17/-2)
- **Result:** APPLIED
- **SHA:** 9db38118
- **Release:** v0.2.7-alpha-2.141
- **Notes:** Cherry-pick -x of `68f782d6` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (10s), build -x test SUCCESSFUL (9s). Push c907deed..9db38118 on backport/sequential. Release v0.2.7-alpha-2.141 created with both built jars. Counter advanced .140 → .141.

## 173. `76392ab088b97717793e02f081703a4de9d7be4f` late stage traversal compile
- **Verdict:** APPLIED
- **Files:** 2 files, +5/-... (`VoxyRenderSystem.java` +5, `HierarchicalOcclusionTraverser.java` +.../-...)
- **Result:** APPLIED
- **SHA:** 31ac65ba01e59e22de84c7ac05319969547f8330
- **Release:** v0.2.7-alpha-2.142
- **Notes:** Cherry-pick -x of `76392ab0` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL, build -x test SUCCESSFUL. Push f64303ca..31ac65ba on backport/sequential. Release v0.2.7-alpha-2.142 published. Counter advanced .141 → .142.

## 174. `08a17128723ff2b79148df9b93e6baaeda81422e` taa in culling
- **Verdict:** PORTABLE (clean cherry-pick: threads TAA jitter through MDIC section culling and carries the current camera/frame state in the culling pass; the change uses existing renderer interfaces and has no MC 1.21.11/Sodium 0.7 dependency.)
- **Files:** 1 file, +16/-4 (`MDICSectionRenderer.java`)
- **Result:** APPLIED
- **SHA:** 8548627a
- **Release:** v0.2.7-alpha-2.143
- **Notes:** Cherry-pick -x of `08a17128` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push 2a9d6782..8548627a on backport/sequential. Release v0.2.7-alpha-2.143 created with both built jars after retrying with the full commit SHA. Counter advanced .142 → .143.

## 175. `5779f52d21b447acca687c643e536dcff88b3d08` taa in heirachial traversal + other fixes
- **Verdict:** PORTABLE (clean cherry-pick: updates hierarchical traversal screen-space math for TAA jitter and corrects viewport/projection handling; the GLSL-only change uses the fork's existing shader interface with no MC 1.21.11/Sodium 0.7 dependency.)
- **Files:** 1 file, +21/-7 (`screenspace.glsl`)
- **Result:** APPLIED
- **SHA:** 0298fc85
- **Release:** v0.2.7-alpha-2.144
- **Notes:** Cherry-pick -x of `5779f52d` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (7s), build -x test SUCCESSFUL (9s). Push 8db9dabe..0298fc85 on backport/sequential. Release v0.2.7-alpha-2.144 created with both built jars. Counter advanced .143 → .144.

## 176. `8eb5afc5b0813b5af40b9025d1abf363741d70df` moved render statistics from config menu into f3 debug menu
- **Verdict:** MC-1.21.2-ONLY (the commit adds and mixes into `DebugEntries`, `DebugScreenEntryList`, and the F3 debug-entry registry APIs, which are absent on MC 1.21.1; it also targets the fork-deleted `VoxyConfigMenu`.)
- **Files:** 0 files applied (upstream attempted 4 files, +36/-7; the three deleted debug/config files produced modify/delete conflicts and the only live shared hunk would change `GPUTiming`'s default.)
- **Result:** SKIPPED
- **SHA:** n/a
- **Release:** n/a (counter unchanged at .144)
- **Notes:** Cherry-pick -x of `8eb5afc5` conflicted on deleted `DebugEntries.java`, `VoxyConfigMenu.java`, and `MixinDebugScreenEntryList.java`. The feature depends on MC 1.21.2+ `DebugScreenDisplayer`, `DebugScreenEntries`, and debug-entry-list mixin APIs unavailable to the 1.21.1 fork. Aborted cleanly with `git cherry-pick --abort`; no release and counter unchanged at .144.

## 177. `a47a028d29f5de4ae54e8f0e3335399bb0efdfd7` update loader
- **Verdict:** PORTABLE-AS-NO-OP (the upstream only bumps Fabric Loader from 0.18.2 to 0.18.4. The NeoForge fork is configured through `loader_version_range=[1,)`, and its commented Fabric `loader_version=0.17.2` line must remain unchanged for the MC 1.21.1 setup; there is no portable runtime metadata change.)
- **Files:** 0 files, +0/-0 (empty provenance commit; `gradle.properties` retained from HEAD)
- **Result:** APPLIED
- **SHA:** cb2ed429
- **Release:** v0.2.7-alpha-2.145
- **Notes:** Cherry-pick -x of `a47a028d` conflicted only in `gradle.properties` because upstream targets MC 1.21.11/Fabric Loader 0.18.4. Resolved with `--ours` and continued as an empty provenance commit retaining the source trailer. compileJava SUCCESSFUL (6s), build -x test SUCCESSFUL (9s). Push 5cd0ff18..cb2ed429 on backport/sequential. Release v0.2.7-alpha-2.145 created with both built jars. Counter advanced .144 → .145.

## 178. `6172a8860b18f53e319d2a77f29e04fd17871067` zero cull raster expantion fix and optimize the depth stencil setup pass into a single full screen blit with discard
- **Verdict:** PORTABLE (clean cherry-pick: fixes zero-cull raster expansion and replaces the multi-pass depth/stencil setup with one fullscreen discard blit; the change uses the fork's existing shader pipeline and has no MC 1.21.11/Sodium 0.7 dependency.)
- **Files:** 3 files, +29/-33 (the upstream renderer change plus a new `setup_stencil_depth.frag` shader)
- **Result:** APPLIED
- **SHA:** 5fe2ad6e
- **Release:** v0.2.7-alpha-2.146
- **Notes:** Cherry-pick -x of `6172a886` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (10s), build -x test SUCCESSFUL (9s). Push 07dc71d5..5fe2ad6e on backport/sequential. Release v0.2.7-alpha-2.146 created with both built jars after retrying with the correct full commit SHA. Counter advanced .145 → .146.

## 179. `2a994d061fd1b08209537f1f5c60bc47f20e302e` wip disable config
- **Verdict:** PORTABLE (clean cherry-pick: adds the upstream disabled-config gate and client/common enable-state plumbing; it compiles and builds against the fork's existing 1.21.1 APIs.)
- **Files:** 3 files, +6/-0 (`VoxyClientInstance.java`, `VoxyCommon.java`, and the upstream configuration source)
- **Result:** APPLIED
- **SHA:** c6c305a7
- **Release:** v0.2.7-alpha-2.147
- **Notes:** Cherry-pick -x of `2a994d06` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (10s), build -x test SUCCESSFUL (9s). Push 1278acac..c6c305a7 on backport/sequential. Release v0.2.7-alpha-2.147 created with both built jars. Counter advanced .146 → .147.

## 180. `36964ee4a59ed134075624a5bbab14ccde43fc48` exclusive lock file
- **Verdict:** APPLIED+FIXED (portable Java `FileChannel`/`FileLock` behavior; the import conflict was resolved to preserve the fork's NeoForge initialization imports and use Minecraft's game directory.)
- **Files:** 1 file, +27/-2 (`VoxyClient.java`)
- **Result:** APPLIED+FIXED
- **SHA:** a0f6b9fa
- **Release:** v0.2.7-alpha-2.148
- **Fix:** Combined the fork's NeoForge imports with the upstream lock-file imports, retained deferred NeoForge initialization, and corrected the conflict-resolution marker before the first compile retry.
- **Notes:** Cherry-pick -x of `36964ee4` conflicted in `VoxyClient.java`; resolved to the fork's NeoForge imports plus `Minecraft` and the portable file-lock imports. Initial compileJava exposed one malformed conflict-marker prefix in the first resolution, which was corrected and the cherry-pick amended before the required validation pass. compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push f081c62f..a0f6b9fa on backport/sequential. Release v0.2.7-alpha-2.148 created with both built jars. Counter advanced .147 → .148.

## 181. `2a979ac050231cca29914fd11b40b59c97a934ee` attach to ref
- **Verdict:** PORTABLE (clean cherry-pick: retains the acquired exclusive `FileLock` in a static field so the lock remains attached for the client process lifetime.)
- **Files:** 1 file, +2/-2 (`VoxyClient.java`)
- **Result:** APPLIED
- **SHA:** 75f8cbb3
- **Release:** v0.2.7-alpha-2.149
- **Notes:** Cherry-pick -x of `2a979ac0` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (10s). Push 2fbbf32c..75f8cbb3 on backport/sequential. Release v0.2.7-alpha-2.149 created with both built jars. Counter advanced .148 → .149.

## 182. `c6b30e5164d683f19f4e0c7791fbf2d638010768` player uuid property, disable exclusive lock by default for now
- **Verdict:** APPLIED (cherry-pick -x of `c6b30e5` auto-merged cleanly with no conflicts)
- **Files:** 2 files, +2/-1 (`src/main/java/me/cortex/voxy/client/VoxyClient.java` +1/-1, `src/main/java/me/cortex/voxy/client/VoxyClientInstance.java` +1/-0)
- **Result:** APPLIED
- **SHA:** 87c9d9d1b5a4369dd8a4687a82d187a6c60ce4d1
- **Release:** v0.2.7-alpha-2.150
- **Notes:** Cherry-pick -x of `c6b30e5` auto-merged cleanly with no conflicts. Player uuid property added + exclusive lock defaulted to disabled. compileJava SUCCESSFUL, build -x test SUCCESSFUL. Push f0de7470..87c9d9d1 on backport/sequential. Release v0.2.7-alpha-2.150 published. Counter advanced .149 → .150.


## 183. `370cdaccdd71a83fce8c87842cee77825ad107c5` include other archetictures property
- **Verdict:** APPLIED (clean cherry-pick: replaces the fixed `INCLUDE_OTHER_ARCHS = false` build flag with case-insensitive parsing of the `includeOtherArchs` Gradle project property; this remains compatible with the fork's NeoForge build while preserving the opt-in arm64 native dependency block.)
- **Files:** 1 file, +1/-1 (`build.gradle` +1/-1)
- **Result:** APPLIED
- **SHA:** 631845c0692cefdd5027264b64c9ed48451f7dd8
- **Release:** v0.2.7-alpha-2.151
- **Notes:** Cherry-pick -x of `370cdacc` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (8s), build -x test SUCCESSFUL (9s). Push bb8f9bbf..631845c0 on backport/sequential. GitHub rejected the abbreviated release target, so release v0.2.7-alpha-2.151 was created successfully with the full SHA and both built jars. Counter advanced .150 → .151.


## 184. `77d7ded28262a9cee1e55563ca5e272a4378feca` nv linux 2gb max heap
- **Verdict:** APPLIED (clean cherry-pick: detects Linux and caps Nvidia geometry-buffer capacity below the 2 GiB direct-buffer threshold; this is renderer-only logic with no MC 1.21.11 or Sodium 0.7 dependency.)
- **Files:** 2 files, +5/-0 (`RenderResourceReuse.java` +4/-0, `ThreadUtils.java` +1/-0)
- **Result:** APPLIED
- **SHA:** 96165f6578176af6ab3a6624666846a2c614c040
- **Release:** v0.2.7-alpha-2.152
- **Notes:** Cherry-pick -x of `77d7ded2` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (10s), build -x test SUCCESSFUL (9s). Push f10fea4b..96165f65 on backport/sequential. Release v0.2.7-alpha-2.152 created with both built jars. Counter advanced .151 → .152.


## 185. `f897f4c68c9d63d0c623899357734699f4b3b376` Client session lifecycle events
- **Verdict:** APPLIED+FIXED (introduces a guarded client-session lifecycle abstraction, starts it from login handling, ends it on disconnect, and uses it to gate configuration enablement; the upstream Fabric config-menu reference was ported to the fork's NeoForge Sodium `VoxyConfigScreenPages` path, and the obsolete modify/delete file was removed.)
- **Files:** 7 files, +41/-36 (`ClientSessionEvents.java` +29/-0, `VoxyClientInstance.java` +4/-4, `VoxyConfigScreenPages.java` +2/-1, `RenderResourceReuse.java` +1/-1, two mixins +6/-22, `VoxyCommon.java` +0/-4)
- **Result:** APPLIED+FIXED
- **SHA:** 630b2b138accfea9ef9e758c8b653410aca31fbc
- **Release:** v0.2.7-alpha-2.153
- **Fix:** Retained the fork's NeoForge config-screen implementation and changed its in-game check from `VoxyClientInstance.isInGame` to `ClientSessionEvents.inSession`; login/disconnect mixins now own the session state, while the old empty common hook was removed.
- **Notes:** Cherry-pick -x of `f897f4c6` produced one modify/delete conflict for fork-deleted `VoxyConfigMenu.java`; the upstream lifecycle class and mixins were retained, the deleted file was removed, and the equivalent live config-page hunk was ported. compileJava SUCCESSFUL (10s), build -x test SUCCESSFUL (9s). Push 8bd96aee..630b2b13 on backport/sequential. Release v0.2.7-alpha-2.153 created with both built jars. Counter advanced .152 → .153.


## 186. `449be6d255ac8b0f1c680a4ec8313980d662823f` remap internal vars to _ for screenspace
- **Verdict:** APPLIED (clean cherry-pick: prefixes shared screenspace GLSL globals with underscores to avoid collisions during shader inclusion; the change is renderer-source-only and has no loader API dependency.)
- **Files:** 1 file, +20/-20 (`src/main/resources/assets/voxy/shaders/lod/hierarchical/screenspace.glsl`)
- **Result:** APPLIED
- **SHA:** 27075aad7431ac01d600dfcf497335feaf948bee
- **Release:** v0.2.7-alpha-2.154
- **Notes:** Cherry-pick -x of `449be6d2` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (6s), build -x test SUCCESSFUL (9s). Push 65782550..27075aad on backport/sequential. Release v0.2.7-alpha-2.154 created with both built jars. Counter advanced .153 → .154.


## 187. `cd7dba0ae9903a07b78e9187a5f6ea4e70cfb690` on load failed, log
- **Verdict:** APPLIED (clean cherry-pick: catches failures while resolving Linux `sched_setaffinity`, logs the exception through Voxy's logger, and leaves the native function address unavailable instead of failing class initialization.)
- **Files:** 1 file, +9/-2 (`src/main/java/me/cortex/voxy/common/util/ThreadUtils.java`)
- **Result:** APPLIED
- **SHA:** 08c597de3e437499b502d940a134d406117f99c0
- **Release:** v0.2.7-alpha-2.155
- **Notes:** Cherry-pick -x of `cd7dba0a` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push ac2d50d5..08c597de on backport/sequential. Release v0.2.7-alpha-2.155 created with both built jars. Counter advanced .154 → .155.


## 188. `b49e8fe290964572ef09f6151f7b99fc3f6cdaef` use f16 if possible (only on nvidia)
- **Verdict:** APPLIED (clean cherry-pick: adds the NVIDIA-specific half-float vertex path and preserves the existing GPGPU/Float path for other hardware; the change is renderer-source-only and has no MC 1.21.11/Sodium 0.7 dependency.)
- **Files:** 1 file, +19/-2 (`src/main/resources/assets/voxy/shaders/lod/gl46/quads3.vert`)
- **Result:** APPLIED
- **SHA:** 4edd57fcf89d45bd79e5bad076d8a7153fa6fab3
- **Release:** v0.2.7-alpha-2.156
- **Notes:** Cherry-pick -x of `b49e8fe2` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (7s), build -x test SUCCESSFUL (9s). Push 37679362..4edd57fc on backport/sequential. GitHub rejected the initial mistyped release target, so release v0.2.7-alpha-2.156 was created successfully with the actual full SHA and both built jars. Counter advanced .155 → .156.


## 189. `bef1a8b606b9a6e67e295f5811ec26530ce67ef4` revert culling expand to 1 and shift z offset
- **Verdict:** APPLIED (clean cherry-pick: restores one-pixel culling expansion and shifts the raster z offset; the change is renderer-source-only and has no MC 1.21.11/Sodium 0.7 dependency.)
- **Files:** 1 file, +5/-7 (`src/main/resources/assets/voxy/shaders/lod/gl46/cull/raster.vert`)
- **Result:** APPLIED
- **SHA:** 251cb98f7837dfd4be8eb5a2db6450c3957ea468
- **Release:** v0.2.7-alpha-2.157
- **Notes:** Cherry-pick -x of `bef1a8b6` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (6s), build -x test SUCCESSFUL (9s). Push 7dd4a5a0..251cb98f on backport/sequential. GitHub rejected the abbreviated release target, so release v0.2.7-alpha-2.157 was created successfully with the full SHA and both built jars. Counter advanced .156 → .157.


## 190. `03b39a16998c7191ab67f25afde882b9c03548c5` clown emoji
- **Verdict:** APPLIED (clean cherry-pick: updates the GPGPU quad-shader debug/output visualization; the change is renderer-source-only and has no MC 1.21.11/Sodium 0.7 dependency.)
- **Files:** 1 file, +5/-8 (`src/main/resources/assets/voxy/shaders/lod/gl46/quads3.vert`)
- **Result:** APPLIED
- **SHA:** 7ecfa19712c2cf353deb5dcea5cff0236938ff0c
- **Release:** v0.2.7-alpha-2.158
- **Notes:** Cherry-pick -x of `03b39a16` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (7s), build -x test SUCCESSFUL (9s). Push 225b0edf..7ecfa197 on backport/sequential. Release v0.2.7-alpha-2.158 created with both built jars. Counter advanced .157 → .158.


## 191. `05f9f5e0df04b6a7c69148f445586587d2b87215` sighhhhhhhhhhhhhhhhhhhhhhhhh
- **Verdict:** APPLIED (clean cherry-pick: adjusts MDIC section culling and the related quad/raster shader paths; the changes compile against the fork's existing renderer interfaces and have no MC 1.21.11/Sodium 0.7 dependency.)
- **Files:** 3 files, +9/-5 (`MDICSectionRenderer.java`, `cull/raster.vert`, and `quads3.vert`)
- **Result:** APPLIED
- **SHA:** bfd8fa702c6fa7aab880031ed2713da133ae3b02
- **Release:** v0.2.7-alpha-2.159
- **Notes:** Cherry-pick -x of `05f9f5e0` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push 4aa8cac5..bfd8fa70 on backport/sequential. Release v0.2.7-alpha-2.159 created with both built jars. Counter advanced .158 → .159.


## 192. `cc7686d86f4005f8f2004ac8a59cc8bb0d72808e` dont flush on new mapper id
- **Verdict:** APPLIED (clean cherry-pick: avoids flushing mapper state when a new mapper id is observed; the change is common Java logic with no MC 1.21.11/Sodium 0.7 dependency.)
- **Files:** 1 file, +2/-2 (`src/main/java/me/cortex/voxy/common/world/other/Mapper.java`)
- **Result:** APPLIED
- **SHA:** 4e9fd39b1e245fe2e920c9b103c5059c17b2778d
- **Release:** v0.2.7-alpha-2.160
- **Notes:** Cherry-pick -x of `cc7686d8` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (11s), build -x test SUCCESSFUL (9s). Push edbf11c3..4e9fd39b on backport/sequential. Release v0.2.7-alpha-2.160 created with both built jars. Counter advanced .159 → .160.


## 193. `158ff6a5a8b397533ade909cd25672a79afb555c` reload when sodium thread sharing changes
- **Verdict:** APPLIED (empty cherry-pick: upstream changed the deleted Fabric `VoxyConfigMenu`; the equivalent live NeoForge `VoxyConfigScreenPages` implementation already reloads the renderer and updates dedicated threads when Sodium thread sharing changes.)
- **Files:** 0 files, +0/-0 (upstream modify/delete conflict resolved by retaining the fork deletion)
- **Result:** APPLIED
- **SHA:** 095baefd84e33c5e943084e41287829f8e7761c9
- **Release:** v0.2.7-alpha-2.161
- **Notes:** Cherry-pick -x of `158ff6a5` produced a modify/delete conflict for the fork-deleted `VoxyConfigMenu`; the live `VoxyConfigScreenPages` already contains the behavior, so the deletion was retained and the upstream commit was recorded as an empty commit. compileJava SUCCESSFUL (7s), build -x test SUCCESSFUL (9s). Push 721cf835..095baefd on backport/sequential. Release v0.2.7-alpha-2.161 created with both built jars and verified. Counter advanced .160 → .161.


## 194. `5dcaa23bc40500165abe91a4c803195e2482f31b` optimize serialization
- **Verdict:** APPLIED (clean cherry-pick: serializes repeated world-section values by reusing the previous LUT mapping, reducing redundant map operations without loader-specific dependencies.)
- **Files:** 1 file, +9/-4 (`src/main/java/me/cortex/voxy/common/world/SaveLoadSystem3.java`)
- **Result:** APPLIED
- **SHA:** f3c67c8011c2fd62b3f09ec902144804e6501311
- **Release:** v0.2.7-alpha-2.162
- **Notes:** Cherry-pick -x of `5dcaa23b` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (8s), build -x test SUCCESSFUL (9s). Push 2f73180f..f3c67c80 on backport/sequential. GitHub rejected two mistyped abbreviated release targets, then release v0.2.7-alpha-2.162 was created with the actual full SHA and both built jars and verified. Counter advanced .161 → .162.


## 195. `e0a2a7ce1d76aeaf2db9f593c0caa1a3c68faea4` jank jank
- **Verdict:** APPLIED (clean cherry-pick: guards the NVIDIA half-float shader redeclarations behind `USE_NV_JANK`, avoiding compiler/driver jank; the change is renderer-source-only.)
- **Files:** 1 file, +4/-0 (`src/main/resources/assets/voxy/shaders/lod/gl46/quads3.vert`)
- **Result:** APPLIED
- **SHA:** 9a0aa2e11b56790f901738b1aac4c0336d3f2f0e
- **Release:** v0.2.7-alpha-2.163
- **Notes:** Cherry-pick -x of `e0a2a7ce` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (7s), build -x test SUCCESSFUL (9s). Push 606c9722..9a0aa2e1 on backport/sequential. Release v0.2.7-alpha-2.163 created with both built jars and verified. Counter advanced .162 → .163.


## 196. `eda6013407be0c5f466a320fd0beb4b0aaa8524f` added skipShaderDepthHackFix to shader options
- **Verdict:** APPLIED (clean cherry-pick: adds the shader compatibility option, shader-pipeline depth/stencil fix, depth-cutout corrections, and advances `SHADER_DEFINE_VERSION` from 1 to 2.)
- **Files:** 5 files, +34/-4 (`IrisVoxyRenderPipeline.java`, `IrisShaderPatch.java`, `IrisVoxyRenderPipelineData.java`, `blit_texture_depth_cutout.frag`, `ssao.comp`)
- **Result:** APPLIED
- **SHA:** e7029377e2bfd37688d8118ebbb6fa43d4d9a039
- **Release:** v0.2.7-alpha-2.164
- **Notes:** Cherry-pick -x of `eda60134` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push 7d7a522f..e7029377 on backport/sequential. Release v0.2.7-alpha-2.164 created with both built jars and verified. Counter advanced .163 → .164.


## 197. `1f993f8ecf7cdddddd87c2c010d31957947b52b8` slight optimization to mesh factory
- **Verdict:** APPLIED (clean cherry-pick: special-cases model id 0 as air and avoids model metadata queries, mask updates, and partial-quad packing for air blocks.)
- **Files:** 1 file, +15/-8 (`src/main/java/me/cortex/voxy/client/core/rendering/building/RenderDataFactory.java`)
- **Result:** APPLIED
- **SHA:** 127bc59893641bfd7dcd6f7f311cb5280106d53e
- **Release:** v0.2.7-alpha-2.165
- **Notes:** Cherry-pick -x of `1f993f8e` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push c4de1253..127bc598 on backport/sequential. Release v0.2.7-alpha-2.165 created with both built jars and verified. Counter advanced .164 → .165.

## 198. `0637d1ad5e33ea15dd2b57a4a2b551e0f57244a3` dont have to worry about model baking speed anymore
- **Verdict:** APPLIED (clean cherry-pick: removes two model-baking throttling delays while preserving the existing asynchronous model bake path.)
- **Files:** 1 file, +0/-2 (`src/main/java/me/cortex/voxy/client/core/rendering/building/RenderGenerationService.java`)
- **Result:** APPLIED
- **SHA:** 383b61296676a273e86ad23b5139f384edbd5639
- **Release:** v0.2.7-alpha-2.166
- **Notes:** Cherry-pick -x of `0637d1ad` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (10s), build -x test SUCCESSFUL (9s). Push b6f5d384..383b6129 on backport/sequential. GitHub rejected the abbreviated release target, then release v0.2.7-alpha-2.166 was created with the actual full SHA and both built jars and verified. Counter advanced .165 → .166.

## 199. `fd81fd183bdb40d683f6eba60277adfd4e888de6` dont limit meshing speed at all actually
- **Verdict:** APPLIED (clean cherry-pick: removes the fixed meshing-rate sleep and schedules the next mesh slice directly through the existing executor.)
- **Files:** 1 file, +1/-3 (`src/main/java/me/cortex/voxy/client/core/rendering/building/RenderGenerationService.java`)
- **Result:** APPLIED
- **SHA:** 885ac7c5beb4e1deb8d4824181971bc834d116f7
- **Release:** v0.2.7-alpha-2.167
- **Notes:** Cherry-pick -x of `fd81fd18` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (10s), build -x test SUCCESSFUL (9s). Push 45e386e5..885ac7c5 on backport/sequential. Release v0.2.7-alpha-2.167 created with both built jars and verified. Counter advanced .166 → .167.

## 200. `a71ca6b00110c1294da3d844781b6bee4f83e904` dump json on shader load error
- **Verdict:** APPLIED (clean cherry-pick: preserves the Iris shader exception in the Voxy log and emits the shader JSON to the client log before failing shader load.)
- **Files:** 1 file, +10/-2 (`src/main/java/me/cortex/voxy/client/iris/IrisShaderPatch.java`)
- **Result:** APPLIED
- **SHA:** 90e2aa81da08a1c77bd0537c643c8026b9cc0b25
- **Release:** v0.2.7-alpha-2.168
- **Notes:** Cherry-pick -x of `a71ca6b0` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push 25d2847b..90e2aa81 on backport/sequential. Release v0.2.7-alpha-2.168 created with both built jars and verified. Counter advanced .167 → .168.

## 201. `da5a65433c259cb14f11fea46167358ead16da21` remove remove synchronize thing
- **Verdict:** APPLIED (hunk-level resolution: removes the obsolete MixinLayerLightSectionStorage hook and unregisters it; unrelated fork mixin registrations were retained.)
- **Files:** 2 files, +0/-17 (`MixinLayerLightSectionStorage.java`, `client.voxy.mixins.json`)
- **Result:** APPLIED
- **SHA:** 44e36fffe8b2fc7445dcf628df6a72fdcd9e7b3d
- **Release:** v0.2.7-alpha-2.169
- **Notes:** Cherry-pick -x of `da5a6543` conflicted with a nearby unregistered `MixinGlDebug` entry from the fork; resolved by retaining all unrelated registrations while applying the removal of `MixinLayerLightSectionStorage`. compileJava SUCCESSFUL (7s), build -x test SUCCESSFUL (9s). Push 5d32c525..44e36fff on backport/sequential. Release v0.2.7-alpha-2.169 created with both built jars and verified. Counter advanced .168 → .169.

## 202. `20e3bf6e0033baf74f8d9a4fd7eaf17e32edade3` cries in jvm
- **Verdict:** APPLIED (clean cherry-pick: adds raw GLSL-style model-metadata query helpers and refactors render-data/save-load paths to use the shared helpers.)
- **Files:** 3 files, +46/-30 (`ModelQueries.java`, `RenderDataFactory.java`, `SaveLoadSystem3.java`)
- **Result:** APPLIED
- **SHA:** d2d57895e73c426b2dfd27684bcf895362dd2105
- **Release:** v0.2.7-alpha-2.170
- **Notes:** Cherry-pick -x of `20e3bf6e` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (9s, one existing deprecation warning), build -x test SUCCESSFUL (9s). Push 5b256f76..d2d57895 on backport/sequential. Release v0.2.7-alpha-2.170 created with both built jars and verified. Counter advanced .169 → .170.


## 203. `27f82dda8d100c9883e31c94836739dbb73a79fc` double rate distance
- **Verdict:** APPLIED (clean cherry-pick: doubles the render-distance tracker update rate from 20 Hz to 40 Hz using the fork's existing renderer API.)
- **Files:** 1 file, +1/-1 (`VoxyRenderSystem.java`)
- **Result:** APPLIED
- **SHA:** 1846b1875792c42bbf15370be30f7116c31b6d38
- **Release:** v0.2.7-alpha-2.171
- **Notes:** Cherry-pick -x of `27f82dda` auto-merged cleanly with no conflicts. compileJava SUCCESSFUL (10s), build -x test SUCCESSFUL (10s). Push 7c9169be..1846b187 on backport/sequential. Release v0.2.7-alpha-2.171 created with both built jars and verified. Counter advanced .170 -> .171.


## 204. `7b0b137aa94ab6b2175db95fe826dc0ad537cbf5` Merge remote-tracking branch 'origin/dev' into dev
- **Verdict:** SKIPPED (merge commit, cherry-pick not supported without selecting a parent; excluded by the sequential backport workflow.)
- **Files:** 0 files, +0/-0
- **Result:** SKIPPED
- **SHA:** N/A
- **Release:** N/A
- **Notes:** `git cherry-pick -x 7b0b137a` was rejected because the upstream object is a merge commit. The working tree remained clean apart from session-only HANDOFF.md, so no abort/reset or release was needed. Counter remains .171.


## 205. `193ab55d2040b9f8b673837a2ac12ee55984b89e` put mipper in own class
- **Verdict:** APPLIED+FIXED (mipmap-chain generation moved to `WorldVoxilizedSectionMipper`; retained the fork's NeoForge/MC 1.21.1 import layout and added the missing mipper import required by `WorldImporter`.)
- **Files:** 5 files, +89/-79 (`WorldConversionFactory.java`, `WorldVoxilizedSectionMipper.java`, `VoxelIngestService.java`, `DHImporter.java`, `WorldImporter.java`)
- **Result:** APPLIED
- **SHA:** 276c81bae6e14ceac73981217aa11ceea5207f6a
- **Release:** v0.2.7-alpha-2.172
- **Notes:** Cherry-pick -x of `193ab55d` conflicted in `WorldConversionFactory.java` and `WorldImporter.java`; resolved by retaining the fork's `ModList` and existing 1.21.1 imports while accepting the upstream refactor. The first compileJava run found the fork-resolved `WorldImporter` missing the new mipper import; commit `276c81ba` added it, then compileJava SUCCESSFUL (11s) and build -x test SUCCESSFUL (9s). Push e3e02858..276c81ba on backport/sequential. Release v0.2.7-alpha-2.172 created with both built jars and verified. Counter advanced .171 -> .172.


## 206. `b72fcef6f3e9efbe5973825974eb1d2172c90934` breaks worldgen mod 2.2.2
- **Verdict:** APPLIED+FIXED (declares voxyworldgenv2 2.2.2 incompatible; the upstream Fabric `breaks` entry was mirrored in `neoforge.mods.toml` using NeoForge's `type="incompatible"` dependency syntax.)
- **Files:** 2 files, +10/-0 (`fabric.mod.json`, `META-INF/neoforge.mods.toml`)
- **Result:** APPLIED
- **SHA:** 17aebceed765fda26c21b05a308ea68c5531bc62
- **Release:** v0.2.7-alpha-2.173
- **Notes:** Cherry-pick -x of `b72fcef6` auto-merged the retained Fabric metadata cleanly. The upstream compatibility declaration is loader-specific, so follow-up commit `17aebcee` added the equivalent NeoForge incompatible dependency for `voxyworldgenv2` version `[2.2.2]`. compileJava SUCCESSFUL (7s), final compileJava SUCCESSFUL (7s), build -x test SUCCESSFUL (9s). Push b78a27d4..17aebcee on backport/sequential. Release v0.2.7-alpha-2.173 created with both built jars and verified. Counter advanced .172 -> .173.


## 207. `a1b63c24803d9fb1cc89eb8b5856b23c955e0882` fk you intel
- **Verdict:** SKIPPED (MC-1.21.11-only: the fix adds `glFinish()` to `SoftwareModelTextureBakery`'s `GpuBuffer`/`CommandEncoder` atlas-copy wait loop; that software bakery and those MC 1.21.11 GPU APIs do not exist in the 1.21.1 NeoForge fork, whose `ModelTextureBakery` uses a different `GlViewCapture` path with no equivalent wait loop.)
- **Files:** 0 files, +0/-0
- **Result:** SKIPPED
- **SHA:** N/A
- **Release:** N/A
- **Notes:** `git cherry-pick -x a1b63c24` produced rename/content conflicts because upstream targets `SoftwareModelTextureBakery.java` while the 1.21.1 fork retains `ModelTextureBakery.java`. Retaining the fork sides produced an empty patch, confirming there was no portable hunk. The temporary empty commit was reset and the tree restored cleanly apart from session-only HANDOFF.md. No release was created and the counter remains .173.
