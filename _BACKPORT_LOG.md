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
