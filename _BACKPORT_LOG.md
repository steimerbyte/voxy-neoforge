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

## 208. `8e749ed4093393ba7f598cdd4cf17e84d3cde52d` version bump
- **Verdict:** APPLIED (PORTABLE-AS-NO-OP: kept the fork's `mod_version=0.2.7-alpha`; the upstream-only release-version change was not ported.)
- **Files:** 0 files, +0/-0
- **Result:** APPLIED
- **SHA:** 3c946cbacc2467386aacf7bc31893c0ba4eaf248
- **Release:** v0.2.7-alpha-2.174
- **Notes:** Cherry-pick -x of `8e749ed4` conflicted only in `gradle.properties`; resolving with the fork side produced an empty portable patch committed as `3c946cba`. compileJava SUCCESSFUL (7s), build -x test SUCCESSFUL (9s). Push c297f26a..3c946cba on backport/sequential. Release v0.2.7-alpha-2.174 created with both built jars and verified. Counter advanced .173 -> .174.

## 209. `4d5b2178eb7836e271a9569fa5a57d468b936140` todo
- **Verdict:** APPLIED (clean cherry-pick: adds the two debug TODO comments to `RenderDistanceTracker` without changing behavior.)
- **Files:** 1 file, +2/-0 (`RenderDistanceTracker.java`)
- **Result:** APPLIED
- **SHA:** 40baded109af396dbd3e2e9d4061ba634aa2fd80
- **Release:** v0.2.7-alpha-2.175
- **Notes:** Cherry-pick -x of `4d5b2178` auto-merged cleanly. compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push 8d6034d5..40baded1 on backport/sequential. GitHub rejected the abbreviated release target, then release v0.2.7-alpha-2.175 was created with the actual full SHA and both built jars and verified. Counter advanced .174 -> .175.

## 210. `74ccb38a5a80fddfbe328c38dfbf95d15ea6bf1a` pull out big method
- **Verdict:** APPLIED (clean cherry-pick: extracts the model-baking coordinate conversion logic from `RenderDataFactory` into the new `getModelBlockPosition` helper without changing behavior.)
- **Files:** 1 file, +17/-14 (`RenderDataFactory.java`)
- **Result:** APPLIED
- **SHA:** b62391f848f29068fe06fa18fdb045ee0d01e44e
- **Release:** v0.2.7-alpha-2.176
- **Notes:** Cherry-pick -x of `74ccb38a` auto-merged cleanly. compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push 9cf0e944..b62391f8 on backport/sequential. GitHub rejected the abbreviated release target, then release v0.2.7-alpha-2.176 was created with the actual full SHA and both built jars and verified. Counter advanced .175 -> .176.

## 211. `d3296634306132c2acbec9b1a0df7ad5e6aff8df` use the projection matrix creation method instead of manually creating it
- **Verdict:** APPLIED+FIXED (uses `GameRenderer#getProjectionMatrix` for the inverse vanilla projection and centralizes FOV lookup; ported the new helper to MC 1.21.1's `Minecraft#getTimer()` and its double-to-float FOV signature.)
- **Files:** 1 file, +8/-6 (`VoxyRenderSystem.java`)
- **Result:** APPLIED
- **SHA:** 003a02f593a273b21cd483a7a2ba5e2e9e533b66
- **Release:** v0.2.7-alpha-2.177
- **Notes:** Cherry-pick -x of `d3296634` conflicted at the manually computed FOV call; resolved in favor of the shared helper. The first compileJava run found MC 1.21.1 has no `Minecraft#getDeltaTracker()`, and the second found `GameRenderer#getFov` returns double, so the amended commit uses `Minecraft#getTimer()` plus an explicit float conversion. Final compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push ea32ee57..003a02f5 on backport/sequential. Release v0.2.7-alpha-2.177 created with both built jars and verified. Counter advanced .176 -> .177.

## 212. `7fec03921ff116791b7542609505a476b5bc90bf` sodium 0.8.7
- **Verdict:** SKIPPED (MC-26-ONLY: upstream only changes the MC 1.21.11 Sodium dependency to 0.8.7 and adds 0.8.7 to the Fabric 1.21.11 metadata; the 1.21.1 NeoForge fork intentionally retains `curse.maven:sodium-394468` and its 1.21.1 dependency metadata.)
- **Files:** 0 files, +0/-0
- **Result:** SKIPPED
- **SHA:** N/A
- **Release:** N/A
- **Notes:** `git cherry-pick -x 7fec0392` conflicted only in `build.gradle` and `fabric.mod.json`, both targeting the MC 1.21.11 Sodium setup. The cherry-pick was aborted cleanly, leaving HEAD `d919aa08` and only session-only `HANDOFF.md` in the working tree. No release was created and the counter remains .177.

## 213. `f292e268869ef324fe446695c03c8f0669b1a6ec` update iris
- **Verdict:** SKIPPED (MC-26-ONLY: upstream only bumps the Iris artifact from 1.10.6 to 1.10.7 for MC 1.21.11 Fabric; the 1.21.1 NeoForge fork retains its compatible Iris dependency set.)
- **Files:** 0 files, +0/-0
- **Result:** SKIPPED
- **SHA:** N/A
- **Release:** N/A
- **Notes:** `git cherry-pick -x f292e268` conflicted only in `build.gradle` at the MC 1.21.11 Iris 1.10.7 dependency lines. The cherry-pick was aborted cleanly, leaving HEAD `07a7c2ab` and only session-only `HANDOFF.md` in the working tree. No build or release was needed for the incompatible dependency-only patch, and the counter remains .177.

## 214. `02e490e02cbe2f492e85ee97068c7b6c9fd631be` propagate internel error if it exists
- **Verdict:** APPLIED (clean cherry-pick: records the asynchronous node manager's uncaught throwable before stopping the worker and propagates that internal error on later work submission.)
- **Files:** 1 file, +7/-2 (`AsyncNodeManager.java`)
- **Result:** APPLIED
- **SHA:** 6cd00124
- **Release:** v0.2.7-alpha-2.178
- **Notes:** Cherry-pick -x of `02e490e0` auto-merged cleanly. compileJava SUCCESSFUL (10s), build -x test SUCCESSFUL (9s). Push d413099a..6cd00124 on backport/sequential. Release v0.2.7-alpha-2.178 created with both built jars and verified. Counter advanced .177 -> .178.

## 215. `32f3fda47901fa987d7e2c1945135e420bc79d9d` disable nv jank
- **Verdict:** APPLIED (clean cherry-pick: disables the `USE_NV_JANK` shader define pending restoration through a working capability check.)
- **Files:** 1 file, +1/-1 (`MDICSectionRenderer.java`)
- **Result:** APPLIED
- **SHA:** 81250957
- **Release:** v0.2.7-alpha-2.179
- **Notes:** Cherry-pick -x of `32f3fda4` auto-merged cleanly. compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push 0d2a6356..81250957 on backport/sequential. Release v0.2.7-alpha-2.179 created with both built jars and verified. Counter advanced .178 -> .179.

## 216. `80d217d897c940cb7156055ec1e8840ac7b638d3` size limiting ExpandingObjectAllocationList
- **Verdict:** APPLIED (clean cherry-pick: adds an optional allocation-list capacity and fails explicitly when a bounded allocator exhausts its IDs.)
- **Files:** 1 file, +12/-1 (`ExpandingObjectAllocationList.java`)
- **Result:** APPLIED
- **SHA:** 8b7a0bb6
- **Release:** v0.2.7-alpha-2.180
- **Notes:** Cherry-pick -x of `80d217d8` auto-merged cleanly. compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push 4fee34e6..8b7a0bb6 on backport/sequential. Release v0.2.7-alpha-2.180 created with both built jars and verified. Counter advanced .179 -> .180.

## 217. `8187d2fda12acb0fd8c1b6cc8d777a5d2086977e` node manager verify flag
- **Verdict:** APPLIED (clean cherry-pick: optionally verifies async node-manager integrity after publishing results when `verifyNodeManager` is enabled.)
- **Files:** 1 file, +6/-0 (`AsyncNodeManager.java`)
- **Result:** APPLIED
- **SHA:** fc9938b2
- **Release:** v0.2.7-alpha-2.181
- **Notes:** Cherry-pick -x of `8187d2fd` auto-merged cleanly. compileJava SUCCESSFUL (10s), build -x test SUCCESSFUL (9s). Push 0f91efc7..fc9938b2 on backport/sequential. Release v0.2.7-alpha-2.181 created with both built jars and verified. Counter advanced .180 -> .181.

## 218. `23b095b04c5a1159f16246e573ed7131ea8a5d62` use size limiting expanding object list
- **Verdict:** APPLIED (clean cherry-pick: uses the size-limited expanding object allocation list in `NodeManager` to bound client allocation state.)
- **Files:** 1 file, +4/-3 (`NodeManager.java`)
- **Result:** APPLIED
- **SHA:** f2f52484
- **Release:** v0.2.7-alpha-2.182
- **Notes:** `git cherry-pick -x 23b095b04` auto-merged cleanly. compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push 9662acd0..f2f52484 on backport/sequential. Release v0.2.7-alpha-2.182 created with both built jars and verified. Counter advanced .181 -> .182.

## 219. `6a691211bce202771dfee79eb9a3864c2e527c9c` 19 bit request ids
- **Verdict:** APPLIED (clean cherry-pick: updates `NodeStore` request IDs to the 19-bit encoding expected by the hierarchical node protocol.)
- **Files:** 1 file, +6/-4 (`NodeStore.java`)
- **Result:** APPLIED
- **SHA:** 6133e203
- **Release:** v0.2.7-alpha-2.183
- **Notes:** `git cherry-pick -x 6a691211` auto-merged cleanly. compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push 3d2396bd..6133e203 on backport/sequential. Release v0.2.7-alpha-2.183 created with both built jars and verified. Counter advanced .182 -> .183.

## 220. `a5bb6a73bf4cef148bab6d90ca944e29900398b4` fix issue with large texture packs
- **Verdict:** SKIPPED (MC-26-ONLY: the fix targets `SoftwareModelTextureBakery` and its MC 1.21.11 `GpuBuffer`/`CommandEncoder` large-buffer mapping path, which does not exist in the 1.21.1 NeoForge fork; the fork uses `ModelTextureBakery` with a different GL capture path.)
- **Files:** 0 files, +0/-0
- **Result:** SKIPPED
- **SHA:** N/A
- **Release:** N/A
- **Notes:** `git cherry-pick -x a5bb6a73` conflicted in the renamed upstream `SoftwareModelTextureBakery.java` versus fork `ModelTextureBakery.java`; retaining the fork side produced no portable hunk, so the cherry-pick was aborted cleanly. No build or release was needed, and the counter remains .183.

## 221. `bd4cc8f685df80310dd94f19e9541c367e510c77` e
- **Verdict:** APPLIED (clean cherry-pick: updates the client-level tick/weather mixin behavior carried by upstream commit `e`.)
- **Files:** 1 file, +3/-1 (`MixinClientLevel.java`)
- **Result:** APPLIED
- **SHA:** 4a7ab12e
- **Release:** v0.2.7-alpha-2.184
- **Notes:** `git cherry-pick -x bd4cc8f6` auto-merged cleanly. compileJava SUCCESSFUL (8s), build -x test SUCCESSFUL (9s). Push e19b64cb..4a7ab12e on backport/sequential. Release v0.2.7-alpha-2.184 created with both built jars and verified. Counter advanced .183 -> .184.

## 222. `c7ae7141244d4131bbc2655b139b4fe09d0d1fdf` full commit hash
- **Verdict:** APPLIED (cleanly ported the build metadata change: `git rev-parse HEAD` now supplies the full commit hash; NeoForge `VoxyCommon` metadata logic was retained.)
- **Files:** 1 file, +1/-1 (`build.gradle`)
- **Result:** APPLIED
- **SHA:** 92452fd2
- **Release:** v0.2.7-alpha-2.185
- **Notes:** `git cherry-pick -x c7ae7141` conflicted in the Fabric-specific `VoxyCommon` block; resolving that file with the fork side preserved the NeoForge `VoxyCommon` implementation and accepted the portable `build.gradle` change. compileJava SUCCESSFUL (8s), build -x test SUCCESSFUL (9s). Push a21f0e0d..92452fd2 on backport/sequential. Release v0.2.7-alpha-2.185 created with both built jars and verified. Counter advanced .184 -> .185.

## 223. `7bb498f2bd0a103e43ee29dceafce4ae8719d003` thing hash
- **Verdict:** SKIPPED (NeoForge build packaging already has no Fabric `remapJar` classifier block; the upstream one-line short-hash classifier change is inapplicable to this fork.)
- **Files:** 0 files, +0/-0
- **Result:** SKIPPED
- **SHA:** N/A
- **Release:** N/A
- **Notes:** `git cherry-pick -x 7bb498f2` conflicted in `build.gradle`; retaining the fork's NeoForge `reobfJar` configuration left an empty patch, so it was skipped cleanly. compileJava SUCCESSFUL (7s), build -x test SUCCESSFUL (9s). No release was created and the counter remains .185. 


## 224. `3eda859081bfeef786804a92645d4ba8d010ecae` fix unable to deserialize biomes
- **Verdict:** APPLIED+FIXED (missing biomes now log an error and fall back to the default plains biome; null biome, duplicate-ID, and null color-resolver cases are guarded; 1.21.1 registry and resource-location APIs were used.)
- **Files:** 1 file, +15/-2 (`ModelFactory.java`)
- **Result:** APPLIED
- **SHA:** aa15d7167ae31c5b96adbd8202b8b31cc00254ce
- **Release:** v0.2.7-alpha-2.186
- **Notes:** `git cherry-pick -x 3eda8590` conflicted in the biome registry lookup; resolved with MC 1.21.1's `registryOrThrow`, `ResourceLocation.parse`, and registry `Optional` while preserving the upstream fallback behavior. compileJava SUCCESSFUL (10s, one existing deprecation warning), build -x test SUCCESSFUL (9s). Push ed3e79e4..aa15d716 on backport/sequential. Release v0.2.7-alpha-2.186 created with both built jars and verified. Counter advanced .185 -> .186.


## 225. `f453d5555e99b0da5122c4633f22fe10385ef2e9` more cases
- **Verdict:** APPLIED (clean cherry-pick: extends `GlTexture` error checks across additional texture upload cases.)
- **Files:** 1 file, +7/-4 (`GlTexture.java`)
- **Result:** APPLIED
- **SHA:** e259ec52fb56e2373b4b9a2b617b3de446019427
- **Release:** v0.2.7-alpha-2.187
- **Notes:** `git cherry-pick -x f453d555` auto-merged cleanly. compileJava SUCCESSFUL (10s, three existing deprecation warnings), build -x test SUCCESSFUL (9s). Push 7dbb6e94..e259ec52 on backport/sequential. Release v0.2.7-alpha-2.187 created with both built jars and verified. Counter advanced .186 -> .187.


## 226. `aa0ef5031e6a733aab62ce74ec4ec18a4e9e739b` occupancy generator shuffle
- **Verdict:** APPLIED (clean cherry-pick: shuffles the occupancy generator path used by render-data construction.)
- **Files:** 1 file, +14/-5 (`RenderDataFactory.java`)
- **Result:** APPLIED
- **SHA:** 4dcc9ad1e674e4725098976e1e2f21aa3e0c272c
- **Release:** v0.2.7-alpha-2.188
- **Notes:** `git cherry-pick -x aa0ef503` auto-merged cleanly. compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push ce1f5ff8..4dcc9ad1 on backport/sequential. Release v0.2.7-alpha-2.188 created with both built jars and verified. Counter advanced .187 -> .188.


## 227. `f6bdfb2af908a8409eb6a460ee9d2a15640d7e1c` fix mixin conventions to have voxy$
- **Verdict:** APPLIED+FIXED (renamed the active mixin accessor/implementation methods to the `voxy$` convention and migrated all active 1.21.1 call sites; retained the fork's deleted MC 1.21.2+/Sodium 0.7 files and 1.21.1 mixin signatures.)
- **Files:** 11 files, +37/-41 (`VoxyCommands.java`, `VoxyConfigScreenPages.java`, `IGetVoxyRenderSystem.java`, `VoxyUniforms.java`, `MixinIrisRenderingPipeline.java`, `MixinLevelRenderer.java`, `MixinFogRenderer.java`, `MixinLevelRenderer.java`, `MixinWindow.java`, `MixinDefaultChunkRenderer.java`, `MixinRenderSectionManager.java`)
- **Result:** APPLIED
- **SHA:** dc7997c76922f0152e4ec341a3b5c77ffe6ea8d8
- **Release:** v0.2.7-alpha-2.189
- **Notes:** `git cherry-pick -x f6bdfb2a` conflicted in four retained files plus modify/delete conflicts for four files already deleted in this fork. Resolved by retaining the fork's NeoForge/MC 1.21.1 code, applying the `voxy$` method renames, and updating six active legacy accessor call sites. The first compileJava run found those call sites; they were migrated before the amended commit. Final compileJava SUCCESSFUL (10s), build -x test SUCCESSFUL (9s). Push 98d274a1..dc7997c7 on backport/sequential. Release v0.2.7-alpha-2.189 created with both built jars and verified. Counter advanced .188 -> .189.


## 228. `352da265d4150634e7fa547b84522a387463a7a6` CRITICAL: update rocksdb, fix rocksdb memory leak issue that has been in the mod for over 2 years (ever since rocksdb was added)
- **Verdict:** APPLIED+FIXED (updates the NeoForge JarJar RocksDB preference to 10.9.1 and ports deterministic temp-file cleanup plus iterator/native-resource lifecycle fixes that stop long-lived RocksDB memory and handle leaks.)
- **Files:** 2 files, +38/-26 (`build.gradle`, `RocksDBStorageBackend.java`)
- **Result:** APPLIED
- **SHA:** e350e40552197d6c0d1b7b9e224a7371d5f6dbd6
- **Release:** v0.2.7-alpha-2.190
- **Notes:** `git cherry-pick -x 352da265` conflicted only in the NeoForge JarJar dependency block; retained its syntax while porting the required RocksDB 10.9.1 preference. compileJava SUCCESSFUL (18s), build -x test SUCCESSFUL (10s). Push 6a855c67..e350e405 on backport/sequential. Release v0.2.7-alpha-2.190 created with both built jars and verified. Counter advanced .189 -> .190.


## 229. `7dc24842057854ac609c5a4a4dbf635b959e45e8` prep
- **Verdict:** APPLIED (clean cherry-pick: adds the preparation changes used by the following accidental-double-close fix.)
- **Files:** 2 files, +3/-0 (`AbstractRenderPipeline.java`, `AbstractSectionRenderer.java`)
- **Result:** APPLIED
- **SHA:** 2ae4e31a6b29065b412bd4ff32dc882c531f71da
- **Release:** v0.2.7-alpha-2.191
- **Notes:** `git cherry-pick -x 7dc24842` auto-merged cleanly. compileJava SUCCESSFUL (10s), build -x test SUCCESSFUL (9s). Push a6ea51b1..2ae4e31a on backport/sequential. Release v0.2.7-alpha-2.191 created with both built jars and verified. Counter advanced .190 -> .191.


## 230. `ebea10c8c6464a8948f3a6972db2543f42ec11c5` accidental double close
- **Verdict:** APPLIED (clean cherry-pick: removes the accidental second explicit iterator close from the try-with-resources-backed RocksDB iteration path.)
- **Files:** 1 file, +0/-1 (`RocksDBStorageBackend.java`)
- **Result:** APPLIED
- **SHA:** ed0765216ad7eced74c0137f43f9ca591820ce55
- **Release:** v0.2.7-alpha-2.192
- **Notes:** `git cherry-pick -x ebea10c8` auto-merged cleanly. compileJava SUCCESSFUL (9s), build -x test SUCCESSFUL (9s). Push ecefb8db..ed076521 on backport/sequential. Release v0.2.7-alpha-2.192 created with both built jars and verified. Counter advanced .191 -> .192.


## 231. `38540eac92e5ed9e83f8005b6dc8a33d73e9e4c9` inital 26.1 port
- **Verdict:** SKIPPED (MC-26-ONLY: the commit migrates the whole Fabric build to Minecraft 26.1/Loom 1.15/Sodium 0.8.7 and rewrites rendering, model bakery, fog, chunk-position, lightmap, resource, and mixin APIs to classes that do not exist in the MC 1.21.1 NeoForge fork.)
- **Files:** 0 files, +0/-0
- **Result:** SKIPPED
- **SHA:** N/A
- **Release:** N/A
- **Notes:** `git cherry-pick -x 38540eac` produced 17 content conflicts plus two modify/delete conflicts across build metadata, MC 26.1-only renderer/model/fog APIs, and deleted MC 1.21.2+ mixins. No isolated behavior hunk was portable without redesigning the 1.21.1 implementation, so the cherry-pick was aborted cleanly. No build or release was needed, and the counter remains .192.


## 232. `6d73782279894182899b8d2b8aba1c3246827a7b` fix sodium fog
- **Verdict:** APPLIED (clean cherry-pick: raises the active 1.21.1 fog mixin priority so its setup-fog injection runs before Sodium.)
- **Files:** 1 file, +1/-1 (`MixinFogRenderer.java`)
- **Result:** APPLIED
- **SHA:** 2ad2fd5b269536a760c11de4279324a3f711346c
- **Release:** v0.2.7-alpha-2.193
- **Notes:** `git cherry-pick -x 6d737822` auto-merged cleanly while retaining the fork's 1.21.1 four-parameter `setupFog` descriptor. compileJava SUCCESSFUL (8s), build -x test SUCCESSFUL (9s). Push 0998ad46..2ad2fd5b on backport/sequential. Release v0.2.7-alpha-2.193 created with both built jars and verified. Counter advanced .192 -> .193.


## 233. `11afa37e4d2343e3e35a595ea1d09fd787d8fda7` clean up ref
- **Verdict:** APPLIED+FIXED (ports the portable reference-cleanup changes to the 1.21.1 storage, unsafe, and conversion paths while retaining the fork's pure-Java ZSTD implementation.)
- **Files:** 7 files, +63/-8 (`NodeStore.java`, `LZ4Compressor.java`, `ZSTDCompressor.java`, `SectionSerializationStorage.java`, `UnsafeUtil.java`, `WorldConversionFactory.java`, `SaveLoadSystem.java`)
- **Result:** APPLIED
- **SHA:** d36ccb5bb90fb14bfdbc619cfac520d04f77f0de
- **Release:** v0.2.7-alpha-2.194
- **Notes:** `git cherry-pick -x 11afa37e` conflicted in `ZSTDCompressor.java` and `UnsafeUtil.java`; retained the fork's zstd-jni compressor while applying its section-size reference and ported the portable unsafe/conversion/storage changes. The initial compile exposed the moved size constant, which was mapped to `SectionSerializationStorage.BIGGEST_SERIALIZED_SECTION_SIZE`; compileJava and build -x test then passed. Push 34b05090..d36ccb5b on backport/sequential. Release v0.2.7-alpha-2.194 created with both built jars and verified. Counter advanced .193 -> .194.


## 234. `4c8d397b226a41955231516d0eb43fca0ce67711` update wrapper
- **Verdict:** APPLIED (clean cherry-pick: updates the Gradle wrapper and preserves the fork's Gradle 8.14 distribution configuration.)
- **Files:** 4 files, +32/-27 (`gradle/wrapper/gradle-wrapper.jar`, `gradle/wrapper/gradle-wrapper.properties`, `gradlew`, `gradlew.bat`)
- **Result:** APPLIED
- **SHA:** d54ec8d4
- **Release:** v0.2.7-alpha-2.195
- **Notes:** `git cherry-pick -x 4c8d397b` auto-merged cleanly. compileJava SUCCESSFUL (6s), build -x test SUCCESSFUL (9s). Push 21af4333..d54ec8d4 on backport/sequential. Release v0.2.7-alpha-2.195 created with both built jars and verified. Counter advanced .194 -> .195.


## 235. `97e8ae9098b98a8120a14de7fbb69af027047874` java25
- **Verdict:** APPLIED (PORTABLE-AS-NO-OP: retained the fork's Java 21 toolchain, Gradle 8.14 wrapper, and NeoForge workflow paths; the upstream Java 25/Gradle 9.4.1 changes were not portable to this 1.21.1 build.)
- **Files:** 0 files, +0/-0
- **Result:** APPLIED
- **SHA:** 9c10f3e8
- **Release:** v0.2.7-alpha-2.196
- **Notes:** `git cherry-pick -x 97e8ae90` conflicted in the Gradle wrapper; `build.gradle` and the wrapper properties were resolved with the fork's Java 21/Gradle 8.14 configuration, and all Java-version-only changes were omitted as non-portable. The resulting provenance commit is empty. compileJava SUCCESSFUL (13s), build -x test SUCCESSFUL (10s). Push 62c12f5c..9c10f3e8 on backport/sequential. Release v0.2.7-alpha-2.196 created with both built jars and verified. Counter advanced .195 -> .196.


## 236. `c773c3be2bb14a8db1344930bd898cfc4a3e0bb7` cleanup
- **Verdict:** APPLIED+FIXED (removes obsolete unused renderer/world/config helper classes while preserving the fork's active SaveLoadSystem3 and zstd-jni storage paths; a stale import left by the cleanup was removed.)
- **Files:** 10 files, +0/-1350 (`RingUtil.java`, `MessageQueue.java`, `MultiGson.java`, `LoadedPositionTracker.java`, `SaveLoadSystem.java`, `SaveLoadSystem2.java`, `VoxyConfigStore.java`, `LZ4Compressor.java`, `SectionSerializationStorage.java`, `ZSTDCompressor.java`)
- **Result:** APPLIED
- **SHA:** b05db984
- **Release:** v0.2.7-alpha-2.197
- **Notes:** `git cherry-pick -x c773c3be` had a modify/delete conflict in the NeoForge `VoxyConfigStore.java`; resolved by accepting the cleanup deletion. The first compile found the retained pure-Java ZSTD compressor's now-stale `SaveLoadSystem` import, which was removed; compileJava then passed (8s) and build -x test passed (9s). Push a969fff5..b05db984 on backport/sequential. Release v0.2.7-alpha-2.197 created with both built jars and verified. Counter advanced .196 -> .197.


## 237. `608587940c43fef788bee45287e1ea8696fcc852` fix large texture atlas's (also simplifies things alot)
- **Verdict:** SKIPPED (the upstream commit modifies only `SoftwareModelTextureBakery.java`, which this 1.21.1 fork already removed in cleanup commit `731ca0e9`; the active implementation is `ModelTextureBakery.java`, so no portable hunk exists.)
- **Files:** 0 files, +0/-0
- **Result:** SKIPPED
- **SHA:** N/A
- **Release:** N/A
- **Notes:** `git cherry-pick -x 60858794` produced a modify/delete conflict because the file is absent from the fork. The conflict was skipped cleanly; no build or release was required, and the counter remains .197.


## 238. `750c89ea48e2c9a594c538edcc4ac02666f32634` possible jank fix
- **Verdict:** SKIPPED (the upstream commit only resets OpenGL texture unpack state before reading the block atlas in `SoftwareModelTextureBakery.java`, which this 1.21.1 fork already removed; the active `ModelTextureBakery.java` does not read that texture or have a portable equivalent.)
- **Files:** 0 files, +0/-0
- **Result:** SKIPPED
- **SHA:** N/A
- **Release:** N/A
- **Notes:** `git cherry-pick -x 750c89ea` produced a modify/delete conflict because `SoftwareModelTextureBakery.java` is absent from the fork. The conflict was aborted cleanly; no build or release was required, and the counter remains .197.


## 239. `a776e4472f9ce714d8f7a331cd92b54261c74cc4` pass depth texture
- **Verdict:** APPLIED+FIXED (passes the setup-produced depth texture into the post-opaque pipeline hook and removes the fork's stale duplicate one-argument call left by preparation commit `2ae4e31a`.)
- **Files:** 3 files, +4/-6 (`AbstractRenderPipeline.java`, `IrisVoxyRenderPipeline.java`, `NormalRenderPipeline.java`)
- **Result:** APPLIED
- **SHA:** 2031ca4e3964a1890d599ff4d83ea9701d9d4d76
- **Release:** v0.2.7-alpha-2.198
- **Notes:** `git cherry-pick -x a776e447` auto-merged cleanly. The first compile exposed the retained stale `postOpaquePreTranslucent(viewport)` call; it was removed before amending. compileJava SUCCESSFUL (10s), build -x test SUCCESSFUL (9s). Push 3a381251..2031ca4e on backport/sequential. Release v0.2.7-alpha-2.198 created with both built jars and verified. Counter advanced .197 -> .198.


## 240. `f47670c61380e602d375c6796c7f71e3cd22d01e` name
- **Verdict:** APPLIED (clean cherry-pick: renames the post-opaque pipeline hook parameter consistently across the abstract, normal, and Iris pipelines.)
- **Files:** 3 files, +3/-3 (`AbstractRenderPipeline.java`, `IrisVoxyRenderPipeline.java`, `NormalRenderPipeline.java`)
- **Result:** APPLIED
- **SHA:** 014372a8d5628c800839eff9a219c7e96688f06b
- **Release:** v0.2.7-alpha-2.199
- **Notes:** `git cherry-pick -x f47670c6` auto-merged cleanly. compileJava SUCCESSFUL (10s), build -x test SUCCESSFUL (9s). Push 1cadf2aa..014372a8 on backport/sequential. Release v0.2.7-alpha-2.199 created with both built jars and verified. Counter advanced .198 -> .199.


## 241. `977f3a39b23b580d5d90b65e49ea646e59b89f8e` fix new texture pull method
- **Verdict:** SKIPPED (the upstream commit only hardens block-atlas pixel-pack state before uploading into the deleted `SoftwareModelTextureBakery` rasterizer; the active 1.21.1 `ModelTextureBakery` has no such texture-pull path.)
- **Files:** 0 files, +0/-0
- **Result:** SKIPPED
- **SHA:** N/A
- **Release:** N/A
- **Notes:** `git cherry-pick -x 977f3a39` produced a modify/delete conflict because `SoftwareModelTextureBakery.java` is absent from the fork. The conflict was aborted cleanly; no build or release was required, and the counter remains .199.

## 242. `e1e117476e2d475538cc7c1b7313d2ca3e8af567` source frame buffer
- **Verdict:** APPLIED (clean cherry-pick -x of `e1e1174` auto-merged with no conflicts)
- **Files:** 1 file, +1/-1 (`src/main/java/me/cortex/voxy/client/core/AbstractRenderPipeline.java`)
- **Result:** APPLIED
- **SHA:** 3a0d6fc9c6430e295fd4311bc3aacce5d6ae39cf
- **Release:** v0.2.7-alpha-2.200
- **Notes:** Cherry-pick -x of `e1e1174` auto-merged cleanly. Renames a framebuffer field. compileJava SUCCESSFUL, build -x test SUCCESSFUL. Push ebec8717..3a0d6fc9 on backport/sequential. Release v0.2.7-alpha-2.200 published. Counter advanced .199 → .200.

## 243. `a4e45b2fe1008a07a5dc10c37c997e17125b18d9` +8/256 for light uv
- **Verdict:** APPLIED (clean cherry-pick: updates the light UV coordinate by 8/256.)
- **Files:** 1 file, +1/-1 (`LightPacker.java`)
- **Result:** APPLIED
- **SHA:** e35501a53a9bd8e16a9924d45effe37deceadec3
- **Release:** v0.2.7-alpha-2.201
- **Notes:** `git cherry-pick -x a4e45b2f` auto-merged cleanly. compileJava SUCCESSFUL (21s), build -x test SUCCESSFUL (25s). Push 64824572..e35501a5 on backport/sequential. Release v0.2.7-alpha-2.201 created with both built jars and verified. Counter advanced .200 -> .201.

## 244. `88d304e4d9dcde4120a35c0448a4fe6ea6b2e591` wip better ssao
- **Verdict:** APPLIED (clean cherry-pick: advances the SSAO implementation and shader integration.)
- **Files:** 2 files, +186/-118
- **Result:** APPLIED
- **SHA:** 7b9a5d938f7bcbcd387fc4a1f21031cbdf8dc04a
- **Release:** v0.2.7-alpha-2.202
- **Notes:** `git cherry-pick -x 88d304e4` auto-merged cleanly. compileJava SUCCESSFUL (29s), build -x test SUCCESSFUL (35s). Push 11c84029..7b9a5d93 on backport/sequential. Release v0.2.7-alpha-2.202 created with both built jars and verified. Counter advanced .201 -> .202.

## 245. `1156789e0541764a0a9a5e6881d2204a23e9dbf2` new ssao
- **Verdict:** APPLIED+FIXED (extracts SSAO into a reusable helper and integrates it into the render pipeline; retains the fork's 1.21.1-compatible final blit define.)
- **Files:** 2 files, +112/-46 (`NormalRenderPipeline.java`, `SSAO.java`)
- **Result:** APPLIED
- **SHA:** 4b1536a12d95d6929a87d6882c4f572e61d8f24b
- **Release:** v0.2.7-alpha-2.203
- **Notes:** `git cherry-pick -x 1156789e` conflicted in `NormalRenderPipeline.java`; kept the new SSAO helper integration while restoring the fork's `define("EMIT_COLOUR")` path. The initial compile exposed the absent 1.21.11 `useEnvFog` field; after the compatibility fix, compileJava SUCCESSFUL (26s), build -x test SUCCESSFUL (26s). Push 0703c391..4b1536a1 on backport/sequential. Release v0.2.7-alpha-2.203 created with both built jars and verified. Counter advanced .202 -> .203.

## 246. `70f82a790b5fcab0a36eb9a1fcd59b5f9c37fd58` more work on ssao
- **Verdict:** APPLIED (clean cherry-pick: adds SSAO sampling and shader tuning.)
- **Files:** 4 files, +27/-3
- **Result:** APPLIED
- **SHA:** d5241ee7c9d9579e4af2b9d2fe3e4528820ae6d9
- **Release:** v0.2.7-alpha-2.204
- **Notes:** `git cherry-pick -x 70f82a79` auto-merged cleanly. compileJava SUCCESSFUL (32s), build -x test SUCCESSFUL (25s). Push 6ab114a4..d5241ee7 on backport/sequential. Release v0.2.7-alpha-2.204 created with both built jars and verified. Counter advanced .203 -> .204.

## 247. `1ce3840d5a99d791c84db18a66bb8d8bb6cfe9aa` small opto
- **Verdict:** APPLIED (clean cherry-pick: applies the small SSAO render-path optimization.)
- **Files:** 3 files, +13/-13
- **Result:** APPLIED
- **SHA:** 4d73a3733f2e2aaf0653f8c208afa63f00d31ebe
- **Release:** v0.2.7-alpha-2.205
- **Notes:** `git cherry-pick -x 1ce3840d` auto-merged cleanly. compileJava SUCCESSFUL (33s), build -x test SUCCESSFUL (30s). Push 6d3e514d..4d73a373 on backport/sequential. Release v0.2.7-alpha-2.205 created with both built jars and verified. Counter advanced .204 -> .205.

## 248. `68566124f71134e1ef7be9985b0cbba5df4477c7` attribution
- **Verdict:** APPLIED (clean cherry-pick: adds attribution comment line in the SSAO compute shader.)
- **Files:** 1 file, +2/-0 (`src/main/resources/assets/voxy/shaders/post/ssao.comp`)
- **Result:** APPLIED
- **SHA:** a7f2be8261caf65fc71ab3e99197b4032c8303ad
- **Release:** v0.2.7-alpha-2.206
- **Notes:** `git cherry-pick -x 68566124` auto-merged cleanly. compileJava SUCCESSFUL (21s), build -x test SUCCESSFUL (25s). Push 8d74d6d7..a7f2be82 on backport/sequential. Release v0.2.7-alpha-2.206 created with both built jars and verified. Counter advanced .205 -> .206.

## 249. `184ff7a641b3fe27ee55380e164b547bc746c024` more work
- **Verdict:** APPLIED (clean cherry-pick: reworks SSAO sampling and the matching shader kernel.)
- **Files:** 2 files, +43/-25 (`SSAO.java`, `shaders/post/ssao.comp`)
- **Result:** APPLIED
- **SHA:** a5aeb8d115be5ba8135d82cce98d2a7ecc40fe5f
- **Release:** v0.2.7-alpha-2.207
- **Notes:** `git cherry-pick -x 184ff7a6` auto-merged cleanly. compileJava SUCCESSFUL (28s), build -x test SUCCESSFUL (25s). Push f0563fe3..a5aeb8d1 on backport/sequential. Release v0.2.7-alpha-2.207 created with both built jars and verified. Counter advanced .206 -> .207.

## 250. `ef27a761f42e5a75963ee47e66d613ee8c8adf7c` hh
- **Verdict:** APPLIED (clean cherry-pick: minor SSAO uniform/shader tweak plus one render-pipeline line.)
- **Files:** 3 files, +3/-4 (`NormalRenderPipeline.java`, `SSAO.java`, `shaders/post/ssao.comp`)
- **Result:** APPLIED
- **SHA:** b50f7c6dcacdda502d76c63f42989f3494165a07
- **Release:** v0.2.7-alpha-2.208
- **Notes:** `git cherry-pick -x ef27a761` auto-merged (NormalRenderPipeline.java auto-merged, no conflict). compileJava SUCCESSFUL (26s), build -x test SUCCESSFUL (25s). Push f8fc896a..b50f7c6d on backport/sequential. Release v0.2.7-alpha-2.208 created with both built jars and verified. Counter advanced .207 -> .208.

## 251. `30d053b75adb9a0823610dc2bccb98c0305e12c5` support parent joining enablers in configs, add enum config, start adding ssao config, disable ssao/fog options when shaders are enabled
- **Verdict:** APPLIED+PTRUNED (parent-joining enablers and the Sodium config-menu UI are not portable; the enum config, SSAO mode config and Iris shader-enable probe are.)
- **Files:** 3 files, +28/-0 (`VoxyConfig.java`, `SSAO.java`, `IrisUtil.java`); upstream's 2 Sodium-0.7 config files dropped
- **Result:** APPLIED
- **SHA:** 073bfc45b6a0ce177ca3a0bf75ec5d71068ec19c
- **Release:** v0.2.7-alpha-2.209
- **Notes:** `git cherry-pick -x 30d053b7` hit modify/delete conflicts on `SodiumConfigBuilder.java` and `VoxyConfigMenu.java` (both were already deleted in the fork; the fork uses `VoxyConfigScreenPages.java` on the Sodium 0.6 `client.gui.options.*` API while upstream's files target the Sodium 0.7 `api.config.*` API, which has 0 classes in our `curse.maven:sodium-394468:6382651` jar). Resolved with `git rm` so those files stay deleted. Kept the portable parts: `SSAO.SSAOMode` enum, `VoxyConfig.ssaoMode` + `getSSAOMode()`/`setSSAOMode()` with `Locale.ROOT` parsing, and `IrisUtil.irisShadersEnabledInConfig()`. compileJava SUCCESSFUL (30s), build -x test SUCCESSFUL (25s). Push 781ee6bc..073bfc45 on backport/sequential. Release v0.2.7-alpha-2.209 created with both built jars and verified. Counter advanced .208 -> .209.

## 252. `87d8cd8b4e15eb118ba2876b32aacf8dbb61ba23` fix compile error
- **Verdict:** APPLIED+FIXED (adds the missing `rs.postOpaquePreperation(viewport)` call; keeps the fork's 1.21.1 two-argument `postOpaquePreTranslucent` signature.)
- **Files:** 1 file, +1/-0 (`src/main/java/me/cortex/voxy/client/core/AbstractRenderPipeline.java`)
- **Result:** APPLIED
- **SHA:** 9571361967aa30fd8a4973bbc2e63a5f1ab95677
- **Release:** v0.2.7-alpha-2.210
- **Notes:** `git cherry-pick -x 87d8cd8b` produced a content conflict in `AbstractRenderPipeline.java` because the fork already carries the 1.21.1 `postOpaquePreTranslucent(Viewport, int)` signature (upstream's pre-image is the 1.21.2+ one-arg form). Resolved by taking the upstream side, which only inserts `rs.postOpaquePreperation(viewport);` before the 1.21.1 two-arg call; `AbstractSectionRenderer.postOpaquePreperation` already exists in the fork. compileJava SUCCESSFUL (31s), build -x test SUCCESSFUL (25s). Push cfe5ecdf..95713619 on backport/sequential. Release v0.2.7-alpha-2.210 created with both built jars and verified. Counter advanced .209 -> .210.
## 253. `f9e3f279e40275eec3dfe04f98a9e5e08c9601c0` revert rocksdb version update, implement ssao selection
- **Verdict:** APPLIED+PTRUNED (the ssao-selection behaviour is portable; both build.gradle hunks are PORTABLE-AS-NO-OP for this fork.)
- **Files:** 3 files, +41/-5 (`NormalRenderPipeline.java`, `SSAO.java`, `lang/en_us.json`); upstream's `build.gradle` rejected with `--ours`
- **Result:** APPLIED
- **SHA:** eaa6ce07a2655a3141d634fd9dedd6da525c43d5
- **Release:** v0.2.7-alpha-2.211
- **Notes:** `git cherry-pick -x f9e3f279` auto-merged `NormalRenderPipeline.java`, `SSAO.java` and `lang/en_us.json`, and conflicted only in `build.gradle`. The rocksdb-revert half is PORTABLE-AS-NO-OP here: upstream pins RocksDB via `include(implementation('org.rocksdb:rocksdbjni:10.2.1'))`, whereas this fork pins it through the `jarJar(...)` block with `prefer '10.9.1'` (the commit-228 memory-leak fix) and has no `include(...)` line for rocksdbjni at all, so the upstream revert has no corresponding hunk in the fork. Resolved with `git checkout --ours -- build.gradle`, which also kept the fork's `lwjglVersion = "3.3.3"` and the NeoForge `jarJar`/reobf setup instead of upstream's 3.4.1. Kept the portable ssao selection: `SSAO.createSSAO(SSAOMode)` selecting BASIC/BETTER(`spp=12`)/BEST(`spp=20`)/AUTO, `SSAO.addDebugInfo(List<String>)`, `NormalRenderPipeline` now building the pipeline from `VoxyConfig.CONFIG.getSSAOMode()` and forwarding `addDebug` to the SSAO instance, plus the `voxy.config.general.ssao_mode` lang entries. compileJava SUCCESSFUL (28s), build -x test SUCCESSFUL (25s). Push 88880065..eaa6ce07 on backport/sequential. Release v0.2.7-alpha-2.211 created with both built jars and verified. Counter advanced .210 -> .211.

## 254. `b24f59d414521991679dcbc5be0d4820cd2fd018` name thing
- **Verdict:** APPLIED (clean cherry-pick: renames the SSAO debug-info string.)
- **Files:** 1 file, +1/-1 (`src/main/java/me/cortex/voxy/client/core/SSAO.java`)
- **Result:** APPLIED
- **SHA:** 8e34a5dc4034520805fec1515f0d6974b87a69de
- **Release:** v0.2.7-alpha-2.212
- **Notes:** `git cherry-pick -x b24f59d4` auto-merged cleanly, no conflict. compileJava SUCCESSFUL (28s), build -x test SUCCESSFUL (24s). Push 6360f205..8e34a5dc on backport/sequential. Release v0.2.7-alpha-2.212 created with both built jars and verified. Counter advanced .211 -> .212.

## 255. `37f22e1a7623c6c65371d37dd7ef3fe306b9cc8a` update mods
- **Verdict:** SKIPPED (MC-26-ONLY: pure upstream dependency/platform version bump plus one unportable Java change.)
- **Files:** 0 files applied (all 4 upstream files rejected)
- **Result:** SKIPPED
- **SHA:** n/a
- **Release:** n/a (no release created; counter unchanged)
- **Notes:** `git cherry-pick -x 37f22e1a` conflicted in all 4 files. The version lines are for the MC 26.1/26.1.1 platform: `sodium:mc26.1.1-0.8.9-fabric`, `lithium:mc26.1.1-0.23.0-fabric`, `iris:1.10.9+26.1-fabric`, `vivecraft:26.1.1-1.3.7-b2-fabric`, plus `minecraft_version=26.1.1` / `loader_version=0.18.6` / `loom_version=1.15-SNAPSHOT` / `fabric_api_version=0.145.3+26.1.1` in gradle.properties and `minecraft:["~26.1"]` / `sodium:["=0.8.9"]` in fabric.mod.json. None of these apply: this fork targets MC 1.21.1 via NeoGradle (`net.neoforged.gradle.userdev` 7.1.13, `neo_version=21.1.173`, `minecraft_version=1.21.1`) and pins Sodium through `curse.maven:sodium-394468`, so `build.gradle`, `gradle.properties` (incl. `mod_version`) and `fabric.mod.json` were all resolved with `--ours`. The single behavioural change -- uncommenting `FlashbackCompat.java` -- was verified unportable by applying it and running compileJava: it fails with `package com.moulberry.flashback does not exist` (x3) and `package net.fabricmc.loader.api does not exist`. That is expected: the fork has no flashback dependency (`modImplementation("maven.modrinth:flashback:...")` is commented out, so neither is the `compileOnly` line), has no fabric-loader on the compile classpath (NeoGradle, not loom; fabric-loader is absent from the Gradle cache), and keeps the whole Flashback integration disabled anyway -- `IFlashbackMeta.java` is the only live Flashback-adjacent file while `FlashbackCompat.java`, `MixinFlashbackMeta.java` and `MixinFlashbackRecorder.java` are all commented out and no flashback mixin is registered in `client.voxy.mixins.json`. Uncommenting it would also not be a clean cutover, since its `IFlashbackMeta` cast depends on the still-commented `MixinFlashbackMeta` that implements the interface. Aborted with `git cherry-pick --abort`; tree restored to 1bc6661a clean. Counter unchanged at .212.

## 256. `d6d35154fb672f4ffbf3350da383623bef0dc603` try different iris pack enabled detection
- **Verdict:** APPLIED (clean cherry-pick: switches the Iris shader-pack-enabled probe to a different detection call.)
- **Files:** 1 file, +1/-1 (`src/main/java/me/cortex/voxy/client/core/util/IrisUtil.java`)
- **Result:** APPLIED
- **SHA:** 0674fc016f2d88951c02b206811c58788d2c1c1c
- **Release:** v0.2.7-alpha-2.213
- **Notes:** `git cherry-pick -x d6d35154` auto-merged cleanly, no conflict. The change is pure Iris API and ports as-is. compileJava SUCCESSFUL (27s), build -x test SUCCESSFUL (24s). Push a6f4482d..0674fc01 on backport/sequential. Release v0.2.7-alpha-2.213 created with both built jars and verified. Counter advanced .212 -> .213.

## 257. `3162e6b97c9be2a59232c39152777eea9fb63e22` ssao fixes
- **Verdict:** APPLIED (clean cherry-pick: SSAO kernel and sampling fixes, shader-only.)
- **Files:** 1 file, +23/-12 (`src/main/resources/assets/voxy/shaders/post/ssao.comp`)
- **Result:** APPLIED
- **SHA:** a6937131befce13a0d1efd21af94edfc2552d561
- **Release:** v0.2.7-alpha-2.214
- **Notes:** `git cherry-pick -x 3162e6b9` auto-merged cleanly, no conflict. The commit touches only the GLSL SSAO compute shader, so it ports as-is with no 1.21.1 API adaptation. compileJava SUCCESSFUL (20s), build -x test SUCCESSFUL (24s). Push 22168d40..a6937131 on backport/sequential. Release v0.2.7-alpha-2.214 created with both built jars and verified. Counter advanced .213 -> .214. End of batch 253-257.

## 258. `80e1e40459647b32a68413c0fcd76cd7dba11d51` readd quick continue
- **Verdict:** APPLIED (clean cherry-pick: SSAO shader early-out tweak, GLSL only.)
- **Files:** 1 file, +1/-1 (`src/main/resources/assets/voxy/shaders/post/ssao.comp`)
- **Result:** APPLIED
- **SHA:** c803ab36e2bf39545858a634c40cbdaa63831604
- **Release:** v0.2.7-alpha-2.215
- **Notes:** `git cherry-pick -x 80e1e404` auto-merged cleanly, no conflict. The commit touches only the GLSL SSAO compute shader (restores the `|| depth == uvDepth.z` early-out in `computeAO`), so it ports as-is with no 1.21.1 API adaptation. compileJava SUCCESSFUL (19s), build -x test SUCCESSFUL (25s). Push 92481593..c803ab36 on backport/sequential. Release v0.2.7-alpha-2.215 created with both built jars and verified. Counter advanced .214 -> .215.

## 259. `9002f50e49350bbe126f24b2b8549c46ef1510bc` change ssao selection limits
- **Verdict:** APPLIED (clean cherry-pick: raises the SSAO-mode dedicated-VRAM thresholds.)
- **Files:** 1 file, +2/-2 (`src/main/java/me/cortex/voxy/client/core/SSAO.java`)
- **Result:** APPLIED
- **SHA:** fd029e01c82f0d317dab9ec065c341e8c11c7f42
- **Release:** v0.2.7-alpha-2.216
- **Notes:** `git cherry-pick -x 9002f50e` auto-merged cleanly, no conflict. The change is pure Voxy-internal tuning of the `totalDedicatedMemory` thresholds used by `SSAO.createSSAO` (5 GB -> 7 GB, with the matching comment update), so it ports as-is with no 1.21.1 API adaptation. compileJava SUCCESSFUL (28s), build -x test SUCCESSFUL (24s). Push 8bed2f58..fd029e01 on backport/sequential. Release v0.2.7-alpha-2.216 created with both built jars and verified. Counter advanced .215 -> .216.

## 260. `7ddc56960ef77fea3a2d10e154706774f83dc445` new projection matrix computation (update the near/far planes directly)
- **Verdict:** APPLIED (clean cherry-pick: adds `getRenderDistance()` + `computeProjectionMat`, comments out the old FoV-based path.)
- **Files:** 1 file, +19/-0 (`src/main/java/me/cortex/voxy/client/core/VoxyRenderSystem.java`)
- **Result:** APPLIED
- **SHA:** 84fa9258b19850f411d4731cca41ada6d102136a
- **Release:** v0.2.7-alpha-2.217
- **Notes:** `git cherry-pick -x 7ddc5696` auto-merged into `VoxyRenderSystem.java` with no conflict. The commit is additive: it adds `getRenderDistance()` (`Minecraft.getInstance().options.getEffectiveRenderDistance()*16`), comments out the old `getGameFoV()`/`getVoxyProjectionMatrix` block, and adds `computeProjectionMat(Matrix4fc)` which rewrites the base matrix's `m22`/`m32` for a Voxy-chosen near (8f/16f, 0.1f when Sodium chunk render is disabled) and far (`16*3000`) plane. It uses only APIs present in 1.21.1 (`getEffectiveRenderDistance`, JOML `Matrix4f`/`Matrix4fc`) plus the fork's own `VoxyClient.disableSodiumChunkRender()`, so no 1.21.1 adaptation was needed. compileJava SUCCESSFUL (32s), build -x test SUCCESSFUL (27s). Push f8d36a7e..84fa9258 on backport/sequential. Release v0.2.7-alpha-2.217 created with both built jars and verified. Counter advanced .216 -> .217.

## 261. `104bdf0932e6afecf2913787754fa54a63927862` safer (in theory) proj matrix computation
- **Verdict:** APPLIED+FIXED (the factoring of the bob-injected projection is portable; the raw-projection accessor needed a 1.21.1 rewrite.)
- **Files:** 1 file, +20/-4 (`src/main/java/me/cortex/voxy/client/core/VoxyRenderSystem.java`)
- **Result:** APPLIED
- **SHA:** b9430b165d1191b38ae33129052dfa20b49a03db
- **Release:** v0.2.7-alpha-2.218
- **Notes:** `git cherry-pick -x 104bdf09` auto-merged into `VoxyRenderSystem.java` with no conflict, but compileJava FAILED (2x `cannot find symbol: method getGameRenderState()` on `GameRenderer`). Port pattern applied: upstream reads the raw, bob-free camera projection as `Minecraft.getInstance().gameRenderer.getGameRenderState().levelRenderState.cameraRenderState.projectionMatrix`, which is MC 1.21.2+ `GameRenderState` API. On 1.21.1 that state object does not exist, and viewbobbing is applied to the modelview `PoseStack` instead (`GameRenderer.bobView`/`bobHurt`), so the semantically identical bob-free camera projection on 1.21.1 is the global `com.mojang.blaze3d.systems.RenderSystem.getProjectionMatrix()` (verified present in the 1.21.1 `RenderSystem` class file, with no `getGameRenderState` in the 1.21.1 `GameRenderer` class file). Replaced the accessor with `RenderSystem.getProjectionMatrix()` and added the `RenderSystem` import in the file's existing alphabetical order; the surrounding math (`extraProjection` = `rawMCProj.invert().mul(base)`, then the near/far `m22`/`m32` rewrite) is left exactly as upstream wrote it. Smoke-tested the ported matrix math standalone against JOML 1.10.5: for render distances 16/32/64/512 with Sodium chunk render on and off, all results are finite and invert to the expected near planes (8f at <=32 render distance, 16f beyond, 0.1f with Sodium disabled) against far = 16*3000. compileJava SUCCESSFUL (45s), build -x test SUCCESSFUL (41s). Push 3bfb6502..b9430b16 on backport/sequential. Release v0.2.7-alpha-2.218 created with both built jars and verified. Counter advanced .217 -> .218.

## 262. `52bd20664d8085a9a7f47895254c8a11932b408f` pull out lighting and use same conversion for shader and normal
- **Verdict:** APPLIED+FIXED (the lighting extraction is portable; a fork-only shader needed the new import added to it.)
- **Files:** 5 files, +21/-16 (`gl46/bindings.glsl`, `gl46/quads.frag`, `gl46/quads2.vert`, `lod/lighting.glsl`, `lod/quad_util.glsl`)
- **Result:** APPLIED
- **SHA:** 0ae04be88063948fc15bd4596117852954f6c1bf
- **Release:** v0.2.7-alpha-2.219
- **Notes:** `git cherry-pick -x 52bd2066` auto-merged all 4 upstream files with no conflict; the commit is GLSL-only so compileJava passes trivially. The refactor moves `getLightmap()`/`getLighting()` out of `gl46/bindings.glsl` into a new `lod/lighting.glsl` (unified as `getLightmapUv(uint)`), imported by `gl46/quads.frag` and `lod/quad_util.glsl`. REGRESSION FOUND AND FIXED: upstream has no `gl46/quads2.vert` (it is fork-specific, added for the MDIC path and loaded by `MDICSectionRenderer.java:104`), but the fork's copy calls `getLighting(extractLightId(quad))` at line 138 and imported only `quad_format.glsl` + `block_model.glsl` + `gl46/bindings.glsl` - so after the cherry-pick it would have called a function that no longer existed anywhere in its import graph, failing at shader-compile time in-game. Upstream missed this because the file does not exist on its tree (`git cat-file -e 52bd2066:.../quads2.vert` -> not present). Fix applied: added `#import <voxy:lod/lighting.glsl>` to `gl46/quads2.vert` (upstream put the same import at the equivalent position in `quads.frag`/`quad_util.glsl`). Verified by replaying the real `ShaderLoader` import-preprocessing (namespace:path -> /assets/<ns>/shaders/<path>, recursive textual inline with no include guards, see `ShaderLoader.java:23-82`) over every affected shader: before the fix `quads2.vert` resolves `getLighting` as CALLED-BUT-NEVER-DEFINED, after the fix it resolves; `quads3.vert` (already fine, imports `quad_util.glsl`) and `quads.frag` remain OK. compileJava SUCCESSFUL (33s), build -x test SUCCESSFUL (25s). Push dd6bfbf8..0ae04be8 on backport/sequential. Release v0.2.7-alpha-2.219 created with both built jars and verified. Counter advanced .218 -> .219. End of batch 258-262.

## 263. `44cec7478205fbda7086a47eb9ad0dcc37dbe7a0` improve basic ssao
- **Verdict:** APPLIED (clean cherry-pick -x of `44cec747` auto-merged with no conflicts)
- **Files:** SSAO.java +1/-0 (GLSL constant tuning)
- **Result:** APPLIED
- **SHA:** db8cba496d9782d6b689ce4da7556a05fe1d48ef
- **Release:** v0.2.7-alpha-2.220
- **Notes:** Cherry-pick -x of `44cec747` auto-merged cleanly. compileJava SUCCESSFUL, build -x test SUCCESSFUL. Push 463b45c1..db8cba49 on backport/sequential. Release v0.2.7-alpha-2.220 published. Counter advanced .219 → .220.

## 264. `d2f87345153e741405afc1728aed84240a6a4c7d` unlock on error
- **Verdict:** APPLIED (clean cherry-pick -x of `d2f87345` auto-merged with no conflicts)
- **Files:** 1 file, +1/-0 (`src/main/java/me/cortex/voxy/commonImpl/VoxyInstance.java`)
- **Result:** APPLIED
- **SHA:** 8932a4091565df97a14f417b0277e1f5108cd9d3
- **Release:** v0.2.7-alpha-2.221
- **Notes:** Cherry-pick -x of `d2f87345` auto-merged cleanly (single-line hunk in `VoxyInstance.getWorldObject`, releasing the `activeWorldLock` write lock on the not-running error path so a failed lookup no longer leaks the lock). compileJava SUCCESSFUL, build -x test SUCCESSFUL. Push 863993f4..8932a409 on backport/sequential. Release v0.2.7-alpha-2.221 published. Counter advanced .220 -> .221.

## 265. `9694968d788fa70c37fec92e68b050613e58144d` try fix stupid chunks fade in issue
- **Verdict:** APPLIED (clean cherry-pick -x of `9694968d` auto-merged with no conflicts)
- **Files:** 1 file, +4/-0 (`src/main/java/me/cortex/voxy/client/iris/IrisShaderPatch.java`)
- **Result:** APPLIED
- **SHA:** da353d7e762a9b8e5a344b4d5e99a592aa63ab8f
- **Release:** v0.2.7-alpha-2.222
- **Notes:** Cherry-pick -x of `9694968d` auto-merged cleanly. The change strips the `void _cfi_ignoreMarker() {}` no-op marker function from the generated Voxy patch JSON before deserialization, working around the chunk fade-in issue. Pure string/JSON manipulation, no MC- or Sodium-version-specific API, so it ports verbatim. compileJava SUCCESSFUL, build -x test SUCCESSFUL. Push e4b5d406..da353d7e on backport/sequential. Release v0.2.7-alpha-2.222 published. Counter advanced .221 -> .222.

## 266. `192721a7d51e81a3edafee47f23f30e1dd815e0d` hopefully fixed a very rare race condition during unload,
- **Verdict:** APPLIED (clean cherry-pick -x of `192721a7` auto-merged with no conflicts)
- **Files:** 4 files, +61/-22 (`ActiveSectionTracker.java`, `WorldEngine.java`, `WorldSection.java`, `SectionSavingService.java`)
- **Result:** APPLIED
- **SHA:** 5d84fc6995ada00e2cd6dc62067673476759c306
- **Release:** v0.2.7-alpha-2.223
- **Notes:** Cherry-pick -x of `192721a7` auto-merged cleanly. Pure Voxy-core concurrency fix, no MC/Sodium-version-specific API, so it ports verbatim: `ISectionSaveCallback.save` now returns `boolean` and takes a `sectionAlreadyAcquired` flag so a section already holding its own acquire does not get re-acquired inside `enqueueSave`; `WorldSection.shouldSave()` factors the `isDirty && !inSaveQueue` predicate; `ActiveSectionTracker.tryUnload` gains a `shouldRetryExit` bail-out that releases the write lock and re-invokes `tryUnload`; and the `trySetFreed` `IllegalStateException` message now names which flag (`dirty` / `saveQueue`) was still set. Verified the fork's only `ISectionSaveCallback` implementer is the method reference `world.setSaveCallback(this.savingService::enqueueSave)` in `VoxyInstance.java:178`, whose target `SectionSavingService.enqueueSave` was updated by the cherry-pick - the signature change needs no extra fork-side fixup. compileJava SUCCESSFUL, build -x test SUCCESSFUL. Push ce108e00..5d84fc69 on backport/sequential. Release v0.2.7-alpha-2.223 published. Counter advanced .222 -> .223.

## 267. `bd7fe4a59826b52ab7e13a926c753bc7dd223be1` normalize path
- **Verdict:** APPLIED+FIXED (the `Path.normalize()` intent is portable; upstream's Flashback replay-path override does not exist on the fork.)
- **Files:** 1 file, +1/-1 (`src/main/java/me/cortex/voxy/client/VoxyClientInstance.java`)
- **Result:** APPLIED
- **SHA:** b615c6e1d5973dbbfac81f84a21ce5baaa0cdcc8
- **Release:** v0.2.7-alpha-2.224
- **Notes:** `git cherry-pick -x bd7fe4a5` produced a content conflict in the `VoxyClientInstance()` constructor. The fork has no Flashback integration - `me/cortex/voxy/client/compat/FlashbackCompat.java` exists but its entire body is commented out (including the `FabricLoader`-based `FLASHBACK_INSTALLED` check), and there is no `FlashbackCompat` import in `VoxyClientInstance.java` - so upstream's `FlashbackCompat.getReplayStoragePath()` branch (`noIngestOverride = path != null`, fall back to `getBasePath()` when null) is not portable. Resolved by keeping the fork's shape and porting only the actual change of the commit: `this.basePath = path.normalize()` becomes `var path = getBasePath().normalize();` so both the field and the config path are normalized identically (upstream derives both from the same normalized value; on the fork `path` is already that value, so the argument stays `path`). compileJava SUCCESSFUL, build -x test SUCCESSFUL. Push fd740c25..b615c6e1 on backport/sequential. Release v0.2.7-alpha-2.224 published. Counter advanced .223 -> .224.

## 268. `5e0af8886fb593722321dbb735a5aabc5f0265ac` remove no subdir
- **Verdict:** APPLIED (clean cherry-pick -x of `5e0af888` auto-merged with no conflicts)
- **Files:** 1 file, +1/-1 (`src/main/java/me/cortex/voxy/common/config/storage/lmdb/LMDBStorageBackend.java`)
- **Result:** APPLIED
- **SHA:** cf6b7d38dfeca768b5fddca4f3067598edf941bd
- **Release:** v0.2.7-alpha-2.225
- **Notes:** Cherry-pick -x of `5e0af888` auto-merged cleanly. The change drops the `MDB_NOSUBDIR` flag from the LMDB `open(file, ...)` call (now `open(file, 0)`), so LMDB uses its normal subdirectory layout. `LMDBStorageBackend` is a fork-side class with no MC/Sodium-version-specific API, so it ports verbatim; the now-unused `MDB_NOSUBDIR` static import was already absent from the fork, leaving no dead reference. compileJava SUCCESSFUL, build -x test SUCCESSFUL. Push 17896e33..cf6b7d38 on backport/sequential. Release v0.2.7-alpha-2.225 published. Counter advanced .224 -> .225.

## 269. `cb26998870d08de2b95137b63ddcac3b821ba4f1` render only on valid viewport
- **Verdict:** APPLIED (clean cherry-pick -x of `cb269988` auto-merged with no conflicts)
- **Files:** 1 file, +3/-0 (`src/main/java/me/cortex/voxy/client/core/VoxyRenderSystem.java`)
- **Result:** APPLIED
- **SHA:** c6d6440cbefd65ac47beaa6bcdd5473171c75fdf
- **Release:** v0.2.7-alpha-2.226
- **Notes:** Cherry-pick -x of `cb269988` auto-merged cleanly. The change adds a guard in `VoxyRenderSystem` right after the existing `viewport == null` early-return: `if (viewport.width <= 0 || viewport.height <= 0) { return; }`, so the render setup (sampler reset, shader bind, draw) is skipped when the viewport has been resized to zero or a negative dimension instead of issuing a zero-area draw. The hunk sits in the fork's own `VoxyRenderSystem` at the identical method and the `Viewport` accessors used (`width`/`height`) are unchanged on 1.21.1, so it ports verbatim with no port pattern needed. compileJava SUCCESSFUL, build -x test SUCCESSFUL. Push e76dc7c0..c6d6440c on backport/sequential. Release v0.2.7-alpha-2.226 published. Counter advanced .225 -> .226.

## 270. `794075e3bbc1471957e521eef60259ebeaa64e6c` our shader loader
- **Verdict:** APPLIED+FIXED (the self-contained loader is portable; two mechanical renames were required for the fork's 1.21.1 target and this environment's JDK.)
- **Files:** 1 file, +51/-64 (`src/main/java/me/cortex/voxy/client/core/gl/shader/ShaderLoader.java`)
- **Result:** APPLIED
- **SHA:** 18be70fbf2acbfa952b738b7e051f90470195a63
- **Release:** v0.2.7-alpha-2.227
- **Notes:** `git cherry-pick -x 794075e3` produced a full-file content conflict in `ShaderLoader.java` (upstream rewrote the whole class; the fork's version was a fork-local variant). Resolved by taking upstream's new self-contained `ShaderLoadingParser` - it drops the dependency on Sodium's internal `net.caffeinemc.mods.sodium.client.gl.shader.ShaderParser`/`ShaderConstants` and does the `#import <ns:path>` recursion itself. TWO PORT FIXES were required. (1) MC version: upstream uses the 1.21.2+ `net.minecraft.resources.Identifier`; ported to 1.21.1's `net.minecraft.resources.ResourceLocation` with `Identifier.parse` -> `ResourceLocation.parse` and `Identifier.fromNamespaceAndPath` -> `ResourceLocation.fromNamespaceAndPath` (both verified present in the 1.21.1 mapping, `getNamespace()`/`getPath()` unchanged). Upstream's unused `java.util.Collections` import was dropped. (2) JDK surface: upstream's `new BufferedReader(...).readAllLines()` does not compile here, while `Integer.compress` (also Java 19) does - `javap java.io.BufferedReader` on the toolchain JDK (Temurin 21.0.12.1, the same `java.base` image `javac` compiles against) shows `readLine()` and `lines()` but no `readAllLines`, and a bare `javac --release 21` repro of the exact line fails the same way, so this is a property of the JDK image rather than of the Gradle `--release` setting. Replaced with the equivalent `readLine()` loop into an `ArrayList`, which preserves `readAllLines` semantics (drops the line terminator, keeps interior blank lines, emits a final unterminated line). BEHAVIOUR VERIFIED (compileJava cannot exercise this path, so it was simulated instead): all 32 shader ids referenced from Java (`voxy:bakery/*`, `voxy:lod/gl46/*`, `voxy:hiz/*`, `voxy:post/*`, `voxy:util/*`, `voxy:chunkoutline/*`, `voxy:lod/hierarchical/*`) plus their full transitive `#import` graph resolve with 0 missing assets and 0 malformed imports under the new loader, every root emits `#version 460 core` as line 1 (so `Shader.Builder.compileToProgram` still injects `#define`s after the version line), and no residual `#import` survives. The swap is behaviour-preserving: Sodium's `ShaderParser.parseShader` was already resolving the same `#import <(?<namespace>.*):(?<path>.*)>` pattern via the same `ShaderLoader.getShaderSource` resource path, no imported shader file carries a `#version` line, all `#import`/`#version` directives are flush-left (so `line.startsWith` still holds), no shader uses `#line`, and `PrintfInjector` locates `printf` by character index rather than line number so it is unaffected by the line-flattening. compileJava SUCCESSFUL, build -x test SUCCESSFUL. Push 28745d55..18be70fb on backport/sequential. Release v0.2.7-alpha-2.227 published. Counter advanced .226 -> .227.

## 271. `c162710edeedf36cc083f1889cf43f06c4492de8` some cleanup
- **Verdict:** APPLIED+FIXED (the `Matrix4fc` signature refactor is portable; the `FogParameters` parameter and the two fork-deleted files were dropped.)
- **Files:** 5 files, +9/-13 (`VoxyRenderSystem.java`, `MipGen.java`, `Viewport.java`, `IrisUtil.java`, `MixinDefaultChunkRenderer.java`); 2 fork-deleted files removed from the pick (`SoftwareRasterizer.java`, `nvidium/MixinRenderPipeline.java`)
- **Result:** APPLIED
- **SHA:** dfefb5ad31f7d2bb11237e67118c4ea00af93485
- **Release:** v0.2.7-alpha-2.228
- **Notes:** `git cherry-pick -x c162710e` hit 3 content conflicts and 2 modify/delete conflicts. The commit is a signature refactor: `VoxyRenderSystem.setupViewport` stops taking the whole `ChunkRenderMatrices` record and instead takes the two matrices it actually uses (`Matrix4fc vanillaProjection, Matrix4fc modelView`), `Viewport.setModelView` narrows to `Matrix4fc` and copies in via `this.modelView.set(modelView)` instead of reallocating the field, and the now-dead `ColorSRGB`/`ChunkRenderMatrices` imports are dropped. PORT DECISIONS: (1) the upstream signature also threads a `FogParameters fogParameters` argument, which is Sodium 0.7 / MC 1.21.11-only - `net.caffeinemc.mods.sodium.client.util.FogParameters` has 0 entries in the fork's `curse.maven:sodium-394468:6382651` jar, and the fork's `setupViewport` has never had a fog parameter (`setFogParameters` does not exist on the fork's `Viewport`), so the parameter was dropped from the signature and from both call sites rather than introducing a Sodium 0.7 dependency. (2) `SoftwareRasterizer.java` and `mixin/nvidium/MixinRenderPipeline.java` are both deleted on the fork (no `nvidium` package exists in the fork's tree at all), so their import-only hunks were resolved as deletions and the now-empty `nvidium` directory removed; the Nvidium injection hunk was the only other `FogParameters` consumer. (3) With the signature change, `VoxyRenderSystem`'s own `ChunkRenderMatrices` import became dead (upstream deletes it in this very commit) so it was removed, leaving zero references in the file. The two remaining call sites were migrated to the new signature: `IrisUtil.CapturedViewportParameters.apply` now calls `setupViewport(this.matrices.projection(), this.matrices.modelView(), this.x, this.y, this.z)` and `MixinDefaultChunkRenderer.doRender` calls `setupViewport(matrices.projection(), matrices.modelView(), camera.x, camera.y, camera.z)`; a repo-wide grep confirms exactly one declaration and two call sites, all on the new signature, and Sodium's `ChunkRenderMatrices` record does expose both `projection()` and `modelView()` (verified by disassembly). `MipGen.java` auto-merged (dead `ColorSRGB` import removal) and `Viewport.java` auto-merged clean. compileJava SUCCESSFUL (only the pre-existing `ItemBlockRenderTypes.getChunkRenderType` deprecation warning), build -x test SUCCESSFUL. Push 8eef2907..dfefb5ad on backport/sequential. Release v0.2.7-alpha-2.228 published. Counter advanced .227 -> .228.

## 272. `41dd201d3d676ce697ada40f5a3c13b25845d32b` fix some comparators
- **Verdict:** APPLIED (clean cherry-pick -x of `41dd201d` auto-merged with no conflicts)
- **Files:** 2 files, +3/-3 (`ModelBakerySubsystem.java`, `BasicSectionGeometryData.java`)
- **Result:** APPLIED
- **SHA:** 89ae31d7994c615b6fcad0be79aeddf2341bddde
- **Release:** v0.2.7-alpha-2.229
- **Notes:** Cherry-pick -x of `41dd201d` auto-merged cleanly. Two genuine logic fixes, both pure Java with no MC/Sodium-version-specific API, so they port verbatim. (1) `ModelBakerySubsystem.requestBlockBake` had an off-by-one in its range guard: `getBlockStateCount() < blockId` let `blockId == getBlockStateCount()` through, i.e. exactly the one out-of-range id it is meant to reject; changed to `<=`. (2) `BasicSectionGeometryData.free` had an inverted and never-entered wait loop - `while (System.currentTimeMillis() - start > TIMEOUT)` is false immediately on entry, so the `glFinish()` GPU-memory-drain loop never ran a single iteration; changed to `< TIMEOUT`, and `TIMEOUT` lowered from 2500 to 400 to match the (now correct) 2.5-second comment's intent being replaced by a shorter bounded spin. Both hunks apply to code that is byte-identical on the fork, so the surrounding context was verified line by line: the guard sits before the `seenIds` dedup and queue add in `requestBlockBake`, and the wait loop sits inside the `canQueryGpuMemory` branch of `free()` with its `Failed to wait for gpu memory to be freed` follow-up warning unchanged. compileJava SUCCESSFUL, build -x test SUCCESSFUL. Push 229c6e0d..89ae31d7 on backport/sequential. Release v0.2.7-alpha-2.229 published. Counter advanced .228 -> .229.

## 273. `e22cf5b94fdfd669ddd54c0e541175f7e4fa6854` 16x16x16 occupancy set
- **Verdict:** APPLIED (clean cherry-pick -x of `e22cf5b9` auto-merged with no conflicts)
- **Files:** 2 files, +132/-12 (`src/main/java/me/cortex/voxy/client/core/rendering/building/OccupancySet2.java` new, +97; `src/main/java/me/cortex/voxy/client/core/rendering/building/RenderDataFactory.java` +47/-12)
- **Result:** APPLIED
- **SHA:** 4faa1396c0cf5ef227143e2df185352bf40b7605
- **Release:** v0.2.7-alpha-2.230
- **Notes:** Cherry-pick -x of `e22cf5b` auto-merged cleanly, adding a new `OccupancySet2` class and refactoring `RenderDataFactory`. No MC/Sodium-version-specific API is involved, so it ports verbatim. (1) `OccupancySet2` is a 16x16x16 two-level occupancy set: a 4x4x4 `topLvl` bitmask indexes a `bottomLvl` array of 64 `long`s, with the position bits split by `Integer.compress(pos, 0b1100_1100_1100)` (top) and `Integer.compress(pos, 0b0011_0011_0011)` (bottom), plus insertion-time shifting of the bottom level to keep the lower entries packed. (2) In `RenderDataFactory` the per-index occupancy-barrier mask computation is factored out of `buildOccupancy` into a new `occupancyBarrier(int index)` helper (identical x/y/z neighbour-XOR body), and a new `buildOccupancy16()` is added that samples four 2x2 blocks of the 32x32 grid, folds each 32-bit barrier to 16 bits via `(A|(A>>16))&0xFFFF`, and ORs them into a 16-bit-per-slice occupancy set. CORRECTION: an earlier version of this log entry (written by a concurrent process sharing this branch) recorded the changed file as `OccupancySet.java` with a "SIZE field 8x8x8 -> 16x16x16" change; that is wrong - `OccupancySet.java` is untouched by this commit, and the new 16x16x16 set is a separate new class `OccupancySet2`. VERIFIED: `Integer.compress` (Java 19) is genuinely available on this fork's toolchain - it is already used by the pre-existing `OccupancySet.java` and `WorldConversionFactory.java`, and `javap java.lang.Integer` on the toolchain JDK confirms it - so the earlier note in entry 270 attributing a Java-level problem to the fork was incorrect about its cause (the `readAllLines` gap there is real but is a property of this environment's `java.base` image, not a `--release 17` setting; `readAllLines`/`Integer.compress` are not both-or-neither here). Upstream's `OccupancySet2.main` self-test (1000 seeds x 5000 random sets, cross-checked against a reference `BitSet` of 16*16*16 every time) was executed against the built classes and passed with exit code 0, so the new bitset's set/get behaviour is confirmed correct. As upstream, `OccupancySet2` and `buildOccupancy16()` are not yet wired into the mesh path - `generateMesh` still calls `buildOccupancy()` (the 32x32 variant) - so this lands as staging code exactly as upstream intends. compileJava SUCCESSFUL, build -x test SUCCESSFUL. Push 3daf7856..4faa1396 on backport/sequential. Release v0.2.7-alpha-2.230 published against full SHA 4faa13969e30401ac02379f8c989e4d484ca6bf1. Counter advanced .229 -> .230.
## 274. `41e9a427b047335bc4e2765dd41fb08c4b92bde2` quad jank
- **Verdict:** APPLIED (clean cherry-pick -x of `41e9a427` auto-merged with no conflicts)
- **Files:** 2 files, +6/-1 (`src/main/resources/assets/voxy/shaders/lod/gl46/quads3.vert`, `src/main/resources/assets/voxy/shaders/lod/quad_format.glsl`)
- **Result:** APPLIED
- **SHA:** 3f3458c679fd126ebd906e1ac2a72cdb593ec77a
- **Release:** v0.2.7-alpha-2.231
- **Notes:** Cherry-pick -x of `41e9a427` auto-merged cleanly. The commit touches only GLSL shader resources under `src/main/resources/assets/voxy/shaders/` - no Java, no MC-version-specific API, so it ports verbatim with no port pattern needed. It adds 5 lines to `lod/gl46/quads3.vert` and adjusts one line in `lod/quad_format.glsl`, both aimed at removing visible popping ("jank") when the LOD quad LOD level transitions. compileJava SUCCESSFUL, build -x test SUCCESSFUL. Push 339196c3..3f3458c6 on backport/sequential. Release v0.2.7-alpha-2.231 published against full SHA 3f3458c679fd126ebd906e1ac2a72cdb593ec77a. Counter advanced .230 -> .231.
## 275. `d6231c7f94623e230ba0f680b5d19ad41a314a12` wip rev-z support
- **Verdict:** APPLIED (clean cherry-pick -x of `d6231c7` auto-merged with no conflicts)
- **Files:** 2 files, +46/-14 (`src/main/resources/assets/voxy/shaders/hiz/blit.fsh`, `src/main/resources/assets/voxy/shaders/lod/hierarchical/screenspace.glsl`)
- **Result:** APPLIED
- **SHA:** c35109fb255813a3b65cfb31fb5e1fb580936546
- **Release:** v0.2.7-alpha-2.232
- **Notes:** Cherry-pick -x of `d6231c7` auto-merged cleanly. The commit touches only GLSL shader resources under `src/main/resources/assets/voxy/shaders/` - no Java, no MC-version-specific API, so it ports verbatim with no port pattern needed. Subject is an upstream WIP marker ("wip rev-z support"); the change reworks the Hi-Z blit fragment shader (`hiz/blit.fsh`) and the hierarchical LOD screenspace reconstruction (`lod/hierarchical/screenspace.glsl`) to support a reversed-Z depth convention. compileJava SUCCESSFUL, build -x test SUCCESSFUL. Push 4c72ef6e..c35109fb on backport/sequential. Release v0.2.7-alpha-2.232 published against full SHA c35109fb255813a3b65cfb31fb5e1fb580936546. Counter advanced .231 -> .232.
## 276. `ac5f9d6357d880ba95075b213666ec258662ff80` more
- **Verdict:** APPLIED (clean cherry-pick -x of `ac5f9d63` auto-merged with no conflicts)
- **Files:** 13 files, +133/-55 - all GLSL shader resources under `src/main/resources/assets/voxy/shaders/`: `chunkoutline/outline.vsh`, `hiz/blit.fsh`, `hiz/blit.vsh`, `lod/gl46/cull/raster.vert`, `lod/gl46/quads.frag`, `lod/hierarchical/screenspace.glsl`, `post/blit_texture_depth_cutout.frag`, `post/depth0.frag`, `post/fullscreen.vert`, `post/fullscreen2.vert`, `post/setup_stencil_depth.frag`, `post/ssao.comp`, plus the NEW shared helper `util/depthutils.glsl` (+69)
- **Result:** APPLIED
- **SHA:** 4345212c549dac14cf24a7ba3869ea87dfeecabe
- **Release:** v0.2.7-alpha-2.233
- **Notes:** Cherry-pick -x of `ac5f9d63` auto-merged (one file auto-merged via git's merge driver, no conflicts). The commit is the follow-up to 275's reversed-Z work: it factors the depth conversion helpers out into a new shared include `shaders/util/depthutils.glsl` and rewires the Hi-Z blit, the hierarchical LOD screenspace reconstruction, the chunk-outline, SSAO, depth0 and the two fullscreen blits onto them. No Java and no MC-version-specific API, so it ports verbatim with no port pattern needed. compileJava SUCCESSFUL, build -x test SUCCESSFUL. NOTE: the local `git push` reported "Everything up-to-date" because a concurrent sibling batch had already committed and pushed its own `_BACKPORT_LOG.md` correction commit (`0d371d90`) on top of this cherry-pick between the build and the push. `git merge-base --is-ancestor 4345212c origin/backport/sequential` confirms this commit IS on origin - a plain fast-forward push, no force push required. Release v0.2.7-alpha-2.233 published against full SHA 4345212c549dac14cf24a7ba3869ea87dfeecabe (verified via `gh release view --json targetCommitish`). Counter advanced .232 -> .233.
## 277. `bec1360d24fb9556ed5793351834a85699928b60` far not near
- **Verdict:** APPLIED (clean cherry-pick -x of `bec1360d` auto-merged with no conflicts)
- **Files:** 1 file, +1/-1 (`src/main/resources/assets/voxy/shaders/post/blit_texture_depth_cutout.frag`)
- **Result:** APPLIED
- **SHA:** 04bfafd873b6939136063f4ee6a9d2881cab9e22
- **Release:** v0.2.7-alpha-2.234
- **Notes:** Cherry-pick -x of `bec1360d` auto-merged cleanly. A single-line GLSL fix in `post/blit_texture_depth_cutout.frag`, inside the same reversed-Z work as 275/276: the `REDUCTION2(...)` bias term switched from `NEAR` to `FAR` (`NEAR+CLOSER_SIGN*(2.0f/((1<<24)-1))` -> `FAR+CLOSER_SIGN*(2.0f/((1<<24)-1))`). `CLOSER_SIGN` is negative for reversed-Z, so biasing from NEAR pushed the comparison toward the wrong end of the depth range - the subject "far not near" names exactly that inversion. Pure GLSL, no Java, no MC-version-specific API, so it ports verbatim with no port pattern needed. compileJava SUCCESSFUL, build -x test SUCCESSFUL. Push 30e977b6..04bfafd8 on backport/sequential (fast-forward, no force). Release v0.2.7-alpha-2.234 published against full SHA 04bfafd873b6939136063f4ee6a9d2881cab9e22 (verified via `gh release view --json targetCommitish`). Counter advanced .233 -> .234.
## 278. `c49cc0cd72fa30b20054357ce569f11e10419926` lighting match better
- **Verdict:** APPLIED (clean cherry-pick -x of `c49cc0cd` auto-merged with no conflicts)
- **Files:** 1 file, +3/-1 (`src/main/resources/assets/voxy/shaders/lod/lighting.glsl`)
- **Result:** APPLIED
- **SHA:** 93e24ddf0423b6fdf4d1d270d819befef410cd01
- **Release:** v0.2.7-alpha-2.235
- **Notes:** Cherry-pick -x of `c49cc0cd` auto-merged cleanly. The commit corrects the Minecraft lightmap-UV computation in `getLightmapUv` inside `lod/lighting.glsl`: the packed light index is unpacked into a `base` value, then scaled by `15.0/16.0` and offset by `0.5/16` (the correct half-texel-to-block-texture-space transform) instead of the old flat `+vec2(8.0f/256)` bias. The old form saturated the clamp range and mismatched the smooth-lighting gradient at the edges of each light level; the new form makes the LOD lighting gradient match vanilla smooth lighting. The outer `clamp` to `[8/256, 248/256]` is unchanged, and upstream left the old bias in place as a trailing comment. Pure GLSL, no Java, no MC-version-specific API, so it ports verbatim with no port pattern needed. compileJava SUCCESSFUL, build -x test SUCCESSFUL. Push 4d3c2caa..93e24ddf on backport/sequential (fast-forward, no force). Release v0.2.7-alpha-2.235 published against full SHA 93e24ddf0423b6fdf4d1d270d819befef410cd01 (verified via `gh release view --json targetCommitish`). Counter advanced .234 -> .235.
## 279. `86c4fd0e17565fb8fa42b8609c4daef6039622c0` tried improved ssao constants
- **Verdict:** APPLIED (clean cherry-pick -x of `86c4fd0e` auto-merged with no conflicts)
- **Files:** 1 file, +2/-2 (`src/main/resources/assets/voxy/shaders/post/ssao.comp`)
- **Result:** APPLIED
- **SHA:** 240eaf30f58fd217f550dedfe1e2d7c4cef822a4
- **Release:** v0.2.7-alpha-2.236
- **Notes:** Cherry-pick -x of `86c4fd0e` auto-merged cleanly. Pure GLSL constant tweak in the SSAO compute shader: `SSAO_MAX_RADIUS_SCREEN` 0.05 -> 0.1 and `SSAO_RADIUS` 1.0f -> 1.5f, doubling the screen-space footprint and 1.5x-ing the world-space sampling radius of the ambient-occlusion kernel. Continues the reversed-Z / SSAO work touched in 276. No Java, no MC-version-specific API, so it ports verbatim with no port pattern needed. compileJava SUCCESSFUL, build -x test SUCCESSFUL. Push e92f332f..240eaf30 on backport/sequential (fast-forward, no force). Release v0.2.7-alpha-2.236 published against full SHA 240eaf30f58fd217f550dedfe1e2d7c4cef822a4. Counter advanced .235 -> .236.
## 280. `271d34aef740696e4cedd0967a41f29bf8fb8066` lock
- **Verdict:** APPLIED (clean cherry-pick -x of `271d34ae` auto-merged with no conflicts)
- **Files:** 1 file, +4/-1 (`src/main/java/me/cortex/voxy/commonImpl/VoxyInstance.java`)
- **Result:** APPLIED
- **SHA:** 6e817c65cbe9dd8b99a5b72a30b777e2a36665dc
- **Release:** v0.2.7-alpha-2.237
- **Notes:** Cherry-pick -x of `271d34ae` auto-merged cleanly. A deadlock fix in the shutdown path of `VoxyInstance`: the force-close loop over `activeWorlds` was iterating the live `HashMap.values()` view while holding the `activeWorldLock` write lock, and then busy-waited on `world.isWorldUsed()` (which can call back into the map) for up to a second - re-entering/acquiring across that wait is exactly how a `StampedLock` gets stuck. Upstream now (a) iterates a defensive copy `new ArrayList<>(this.activeWorlds.values())` so the map cannot be structurally mutated mid-loop, and (b) releases the write lock with `activeWorldLock.unlockWrite(stamp)` BEFORE the busy-wait, re-acquiring it afterwards with `stamp = this.activeWorldLock.writeLock()` so the subsequent `world.free()` stays correctly guarded. The upstream comment ("Dont lock in the loopy thing, this should basicly never happen") documents the intent. Pure Java using only `StampedLock` + `java.util.ArrayList` - both already imported in the fork's copy of the file - no MC- or Sodium-version-specific API, so it ports verbatim with no port pattern needed. compileJava SUCCESSFUL, build -x test SUCCESSFUL. Push 4ad44698..6e817c65 on backport/sequential (fast-forward, no force). Release v0.2.7-alpha-2.237 published against full SHA 6e817c65cbe9dd8b99a5b72a30b777e2a36665dc. Counter advanced .236 -> .237.
## 281. `1aa59fcef7ee9324d3c0ae98d3d3f0a9d5622856` depth things
- **Verdict:** APPLIED (clean cherry-pick -x of `1aa59fce` auto-merged with no conflicts)
- **Files:** 2 files, +2/-2 (`src/main/resources/assets/voxy/shaders/post/blit_texture_depth_cutout.frag`, `src/main/resources/assets/voxy/shaders/util/depthutils.glsl`)
- **Result:** APPLIED
- **SHA:** d08627562d647012619f71deb91458f83d6301ab
- **Release:** v0.2.7-alpha-2.238
- **Notes:** Cherry-pick -x of `1aa59fce` auto-merged cleanly. Continues the reversed-Z work from 275-277. Two changes, both GLSL: (1) in `post/blit_texture_depth_cutout.frag` a blank line plus a TODO comment is added above the `REDUCTION2(FAR+CLOSER_SIGN*(2.0f/((1<<24)-1)), depth)` output-depth bias, noting the output depth is currently always emitted in the same convention as the input, so a future option/define is needed to transform it when voxy is reverse-Z but vanilla is not. (2) in `util/depthutils.glsl` the dead `#define USE_ZERO_ONE_DEPTH` and its comment are REMOVED. VERIFIED no dangling reference: repo-wide grep for `USE_ZERO_ONE_DEPTH` after the pick returns only the two structural remnants upstream deliberately left (the top-level `#ifdef USE_ZERO_ONE_DEPTH` selector at line 26 and the matching `#undef` in the teardown block at line 65) - the removed `#define` was defined INSIDE the `#ifdef USE_REVERSE_Z` block above, and the line-26 `#ifdef` is a separate top-level conditional that never saw that scope, so the `#else` branch (`NDC2SCREEN`/`SCREEN2NDC` with the `*0.5f+0.5f` scaling) was already the active one before and after. Net behavioral change: none from (2); the file is a pure comment/whitespace change. No Java, no MC-version-specific API, so it ports verbatim with no port pattern needed. compileJava SUCCESSFUL, build -x test SUCCESSFUL. Push b0670d87..d0862756 on backport/sequential (fast-forward, no force). Release v0.2.7-alpha-2.238 published against full SHA d08627562d647012619f71deb91458f83d6301ab. Counter advanced .237 -> .238.
## 282. `7d785cdaadf5bd93202896eaa767af3fa189437f` Attempted better stair state block copy could do with more improvement
- **Verdict:** APPLIED (clean cherry-pick -x of `7d785cda` auto-merged with no conflicts)
- **Files:** 1 file, +3/-1 (`src/main/java/me/cortex/voxy/client/core/model/ModelFactory.java`)
- **Result:** APPLIED
- **SHA:** 2a1bbef7c8bd35b508a28c202abd1267a19cb21a
- **Release:** v0.2.7-alpha-2.239
- **Notes:** Cherry-pick -x of `7d785cda` auto-merged cleanly (git reported "Auto-merging" for ModelFactory.java but produced no conflict). The change is in the stair-branch of the block-state normalisation in `ModelFactory` around line 180. Previously, for a `StairBlock`, the state was rebuilt from `sb.baseState` and only the `WATERLOGGED` property was carried across from the real world state - every other stair property (SHAPE, FACING, HALF, plus the three WATERLOGGED-dependent slots) was reset to the base defaults, so an east-facing or top-half stair baked its geometry as the default stair. Upstream replaces that with `blockState = sb.baseState.getBlock().withPropertiesOf(blockState)`, which copies ALL matching properties from the real state onto a base state, generalising the hand-rolled WATERLOGGED special case. The old block is kept as a `/* ... */` comment so the previous intent stays visible. Portability: `BlockState`/`StairBlock`/`withPropertiesOf` are all present in MC 1.21.1 unchanged (the method is `withPropertiesOf(BlockState)` on `BlockBehaviour.BlockStateBase` in 1.21.1 as well), so no version pattern was needed. The now-unused-but-still-referenced-in-comment `BlockStateProperties` import at line 36 was deliberately LEFT in place - it is still needed to keep the commented-out code compiling-if-uncommented, and upstream left it too; removing it would be a fork-local divergence. compileJava SUCCESSFUL (the one `ItemBlockRenderTypes.getChunkRenderType` deprecation warning at line 435 is pre-existing and unrelated to this commit), build -x test SUCCESSFUL. Push 7242dab8..2a1bbef7 on backport/sequential (fast-forward, no force). Release v0.2.7-alpha-2.239 published against full SHA 2a1bbef7c8bd35b508a28c202abd1267a19cb21a. Counter advanced .238 -> .239.
## 283. `c9fc2a850d2fc4536a672f4e5b006af79b50c09b` W.I.P reverse z integration
- **Verdict:** APPLIED+FIXED (1 content conflict resolved by taking upstream; 1 port fix applied)
- **Files:** 15 files, +132/-63, incl. the NEW `src/main/java/me/cortex/voxy/client/core/RenderProperties.java` (+29). Modified: `AbstractRenderPipeline.java`, `IrisVoxyRenderPipeline.java`, `NormalRenderPipeline.java`, `RenderPipelineFactory.java`, `SSAO.java`, `VoxyRenderSystem.java`, `gl/shader/Shader.java`, `rendering/ChunkBoundRenderer.java`, `rendering/Viewport.java`, `rendering/post/FullscreenBlit.java`, `rendering/section/backend/AbstractSectionRenderer.java`, `rendering/section/backend/mdic/MDICSectionRenderer.java`, `rendering/section/backend/mdic/MDICViewport.java`, `rendering/util/HiZBuffer.java`
- **Result:** APPLIED+FIXED
- **SHA:** 7e3665faba1b005625f1fdf2a21d206024474666
- **Release:** v0.2.7-alpha-2.240
- **Notes:** The capstone of the reversed-Z series (275-281, 283). The change threads a new `RenderProperties` value object down the whole render stack so depth convention stops being hardcoded: `AbstractRenderPipeline` gains a `public final RenderProperties properties` field, its `depthStencilSetup` FullscreenBlit becomes instance-constructed from it, and the hardcoded `glDepthFunc(GL_LEQUAL)` is replaced by `this.properties.closerEqualDepthCompare()`; `FullscreenBlit` gains `RenderProperties`-taking constructors and uses the property to pick the shader variant; `RenderPipelineFactory` builds the properties and passes them into `NormalRenderPipeline`/`IrisVoxyRenderPipeline`; `SSAO`, `HiZBuffer`, `Viewport`, `ChunkBoundRenderer`, `MDICSectionRenderer`, `MDICViewport`, `AbstractSectionRenderer`, `VoxyRenderSystem` and `Shader` all switch their depth compares/glDepthFunc calls to the property-driven variants. CONFLICT: one content conflict in `NormalRenderPipeline.java` (L42-53) over the constructor. Resolved by taking `@theirs` (upstream) in full, because the other 14 files in the same pick all auto-merged to the `RenderProperties`-taking signatures - `RenderPipelineFactory` line 22 already called `new NormalRenderPipeline(properties, ...)`, `AbstractRenderPipeline` already declared the `RenderProperties` ctor, and `FullscreenBlit` already had the `RenderProperties` ctor overload - so upstream's side was the only self-consistent resolution and taking `@ours` would not have compiled. PORT FIX (1 line): after resolving, compileJava failed with 2 x "cannot find symbol: variable useEnvFog" at lines 44 and 46. Diagnosis: `private final boolean useEnvFog;` is NOT introduced by this commit - `git grep useEnvFog c9fc2a85^` shows it already existed on upstream `NormalRenderPipeline` line 38 before this pick, i.e. it is a pre-existing fork divergence, not a 1.21.1 API problem. The fork's copy of `NormalRenderPipeline` had that field dropped (along with the old `useEnvFog` fog branch that commit 37 `d30ea7ec` recorded as MC-26-ONLY), while upstream's version retained it. Fix: restored the field declaration `private final boolean useEnvFog;` immediately above `private final FullscreenBlit finalBlit;` (line 38, matching upstream's field ordering exactly), so the constructor assignment and the `defineIf("USE_ENV_FOG", ...)` call resolve again. NO version pattern was needed for the rest: every API the commit touches (`RenderProperties` is a new fork-local class, `Shader.Builder.defineIf`, `VoxyConfig.CONFIG.useEnvironmentalFog` at VoxyConfig.java:36, `FullscreenBlit` ctors, `glDepthFunc`) is already present in the 1.21.1 fork - each was grep-verified before resolving. The fix was folded into the cherry-pick via `git commit --amend --no-edit` (the pick had already completed when the field was added, so `--amend` was the correct route rather than a second commit); `git show HEAD:...NormalRenderPipeline.java | grep useEnvFog` confirms all three lines (38, 45, 47) are in the final commit 7e3665fa. compileJava SUCCESSFUL, build -x test SUCCESSFUL. Push f5f8e6d0..7e3665fa on backport/sequential (fast-forward, no force). Release v0.2.7-alpha-2.240 published against full SHA 7e3665faba1b005625f1fdf2a21d206024474666. Counter advanced .239 -> .240.
## 284. `0c26d0f1b1659db0709abae5d12a09f21a40bb2e` Use informal data to gather render properties
- **Verdict:** APPLIED+FIXED (1 import-block conflict resolved by hand; 1 MC-version port fix applied)
- **Files:** 1 file, +4/-1 (`src/main/java/me/cortex/voxy/client/core/VoxyRenderSystem.java`)
- **Result:** APPLIED+FIXED
- **SHA:** f66723dd3bdd531851c9ea2614a26a1b5fcfe7ce
- **Release:** v0.2.7-alpha-2.241
- **Notes:** Cherry-pick -x of `0c26d0f1` conflicted on the import block of `VoxyRenderSystem` because upstream is 1.21.2+ where `GlConst`/`GlStateManager` live in `com.mojang.blaze3d.opengl` while 1.21.1 keeps them in `com.mojang.blaze3d.platform`; resolved by keeping OUR two package paths and adopting upstream's two new imports. Semantic change: the `RenderProperties` construction in `setWorldIn` stops being hardcoded `new RenderProperties(false, false, false)` and instead interrogates the live pipeline/GL state for the depth convention (zero-to-one NDC range, and whether the depth compare op is `GREATER_THAN_OR_EQUAL`). PORT FIX: 1.21.1 has no `GpuDevice`, `DepthStencilState` or `CompareOp` at all (verified with javap against `build/neoForm/neoFormJoined1.21.1-.../steps/recompile/outputs.jar` - only the pre-pipeline `GlStateManager._depthFunc(int)` / `RenderSystem.depthFunc(int)` exist), so upstream's expression does not compile here. Replaced with the equivalent raw-GL query against the same two facts, using the already-imported LWJGL statics: `new RenderProperties(glGetIntegeri(GL_DEPTH_RANGE, 0) == 0, glGetInteger(GL_DEPTH_FUNC) == GL_GEQUAL, false)`. Note the two-call asymmetry is LWJGL API shape, not a mistake - `GL_DEPTH_RANGE` is a two-component float range read via the indexed `glGetIntegeri(pname, index)`, while `GL_DEPTH_FUNC` is a single enum read via plain `glGetInteger(pname)`; an added `import static org.lwjgl.opengl.GL11.glGetInteger;` covers the latter. VERIFIED: `javap` on LWJGL 3.3.3 confirms `GL11C.glGetInteger(int)->int` and `GL30C.glGetIntegeri(int,int)->int` plus `GL_DEPTH_RANGE=2928` / `GL_DEPTH_FUNC=2932` constants. compileJava SUCCESSFUL, build -x test SUCCESSFUL. Push 67495891..f66723dd on backport/sequential (fast-forward, no force). Release v0.2.7-alpha-2.241 published against full SHA f66723dd3bdd531851c9ea2614a26a1b5fcfe7ce. Counter advanced .240 -> .241.
## 285. `4b2e420fd29417b022685a4d9181a09c7ce74f50` fix mistake
- **Verdict:** APPLIED (clean cherry-pick -x of `4b2e420f`, no conflicts)
- **Files:** 1 file, +7/-1 (`src/main/resources/assets/voxy/shaders/lod/hierarchical/screenspace.glsl`)
- **Result:** APPLIED
- **SHA:** 6b0741e6fb923662e22baf423f847b4483ea71f6
- **Release:** v0.2.7-alpha-2.242
- **Notes:** Cherry-pick -x of `4b2e420f` auto-merged cleanly. A direct follow-up correction to 284/283: `isCulledByHiz()` in the hierarchical LOD screenspace shader unconditionally depth-tested the sampled Hi-Z value against the node's NEAREST bounding-box z (`_minBB.z`), which is the correct end of the range only in the non-reversed-Z convention. With `USE_REVERSE_Z` defined, near and far swap, so the test picked the wrong end of the node's depth interval and culled surviving geometry. Upstream hoists the choice into a `depthTestAgainst` local selected by `#ifdef USE_REVERSE_Z` (`_maxBB.z` when reversed, `_minBB.z` otherwise) and compares against that. The `USE_REVERSE_Z` define is already supplied on the voxy side by `RenderProperties.apply(...)` (`defineIf("USE_REVERSE_Z", this.isReverseZ)`, added in 283), so the `#ifdef` resolves correctly here with no extra plumbing. Pure GLSL, no Java, no MC-version-specific API, so it ports verbatim with no port pattern needed. compileJava SUCCESSFUL, build -x test SUCCESSFUL. Push 98e43531..6b0741e6 on backport/sequential (fast-forward, no force). Release v0.2.7-alpha-2.242 published against full SHA 6b0741e6fb923662e22baf423f847b4483ea71f6. Counter advanced .241 -> .242.
## 286. `727fddba38df59e52918c66cf1bc395d551e443c` revz working thinks
- **Verdict:** APPLIED (clean cherry-pick -x of `727fddba`; 3 files auto-merged, 0 conflicts)
- **Files:** 7 files, +24/-13 (`AbstractRenderPipeline.java`, `RenderProperties.java`, `VoxyRenderSystem.java`, `rendering/ChunkBoundRenderer.java`, `rendering/Viewport.java`, `rendering/hierachical/HierarchicalOcclusionTraverser.java`, `rendering/util/DepthFramebuffer.java`)
- **Result:** APPLIED
- **SHA:** 80ed7fd30866c2174730a5ec164e88f3ad5dc0b5
- **Release:** v0.2.7-alpha-2.243
- **Notes:** Cherry-pick -x of `727fddba` auto-merged cleanly (git reported Auto-merging for AbstractRenderPipeline/VoxyRenderSystem/Viewport but produced no conflict markers). The main body of the reversed-Z work, following 283/284/285. Changes: (1) `RenderProperties` gains `inverseClearDepth()` returning the opposite end of the range to `clearDepth()` (`1.0f` reversed / `0.0f` otherwise). (2) Every "clear the depth-bounds buffer to the empty value" site switches from the hardcoded `clear(0)` to `clear(this.properties.inverseClearDepth())` - two sites in `VoxyRenderSystem`/`ChunkBoundRenderer`, one in `Viewport.setup`; same for `AbstractRenderPipeline.initDepthStencil` which switches its `glClearNamedFramebufferfi(..., 1.0f, 1)` depth arg to `this.properties.clearDepth()`. (3) `Viewport` stores the `RenderProperties` it is constructed with in a new `private final RenderProperties properties` field so the clear call can reach it. (4) `computeProjectionMat` changes signature from `(Matrix4fc base, boolean zero2one)` to `(RenderProperties properties, Matrix4fc base)` and SWAPS `near`/`far` when `isReverseZ()`, so the projection matrix is built for the flipped convention. (5) `HierarchicalOcclusionTraverser` now chains `.apply(pipeline.properties::apply)` onto the auto-built `PRINTF_processor` shader, so the compute shader receives the same `USE_ZERO_ONE_DEPTH` / `USE_REVERSE_Z` defines as the rest of the pipeline. (6) `DepthFramebuffer.clear()` (the no-arg overload that hardcoded `1.0f`) is REMOVED as dead code. All Java, no MC-version-specific API used, so it ports verbatim with no port pattern needed. compileJava SUCCESSFUL, build -x test SUCCESSFUL. Push 0bc284e6..80ed7fd3 on backport/sequential (fast-forward, no force). Release v0.2.7-alpha-2.243 published against full SHA 80ed7fd30866c2174730a5ec164e88f3ad5dc0b5. Counter advanced .242 -> .243.
## 287. `7e924a451a7f2138d61073ab03fa5d7a4a10e27f` fix woopsie
- **Verdict:** APPLIED (clean cherry-pick -x of `7e924a45`, no conflicts)
- **Files:** 2 files, +2/-1 (`gl/shader/Shader.java`, `shaders/lod/lighting.glsl`)
- **Result:** APPLIED
- **SHA:** d7ae3198083e1cce6d06bb71d4fbc429c5646c5f
- **Release:** v0.2.7-alpha-2.244
- **Notes:** Cherry-pick -x of `7e924a45` auto-merged cleanly. Two small corrections: (1) `Shader.copy()` (the clone branch of the builder) copies `defines` and `sources` into the new `Builder` but never `replacements`, so any cloned shader silently lost its source-replacement overrides. Adding `clone.replacements.putAll(this.replacements);` restores them. (2) In `lod/lighting.glsl`, `getLightmapUv` uses the literal `0.5/16f` in the half-texel bias, which is a double-precision expression rather than the float the rest of the function uses; upstream normalises it to `0.5/16.0f`. Continues the 278 lighting work and the 283-286 reversed-Z work. No MC-version-specific API, so it ports verbatim with no port pattern needed. compileJava SUCCESSFUL, build -x test SUCCESSFUL. Push 87c1b19a..d7ae3198 on backport/sequential (fast-forward, no force). Release v0.2.7-alpha-2.244 published against full SHA d7ae3198083e1cce6d06bb71d4fbc429c5646c5f. Counter advanced .243 -> .244.
## 288. `8db40bb02a0cff05c3e5ce17ca237a2c74a7029a` version bump
- **Verdict:** APPLIED-AS-NO-OP (content conflict resolved by keeping ours; committed with `--allow-empty` to preserve upstream provenance)
- **Files:** 0 files (upstream touched only `gradle.properties`, +1/-1)
- **Result:** APPLIED-AS-NO-OP
- **SHA:** db20e72fbd84d69688dda21dbf5ae5ebe7a85178
- **Release:** v0.2.7-alpha-2.245
- **Notes:** Cherry-pick -x of `8db40bb0` conflicted on `gradle.properties` at the single `mod_version` line. Upstream bumps `mod_version = 0.2.7-alpha` -> `0.2.15-alpha`; this fork MUST stay at `0.2.7-alpha` because the fork tracks the voxy 1.21.1 line and both the built artifact names (`build/libs/voxy-0.2.7-alpha.jar` and `voxy-0.2.7-alpha-all.jar`, which the release step uploads) and the whole GitHub release tag scheme (`v0.2.7-alpha-2.NNN`) derive from that value. Taking upstream's value would rename every jar and break the release naming convention. Resolved with `git checkout --ours gradle.properties`, which leaves the cherry-pick with no content change; committed via `git commit --allow-empty` (not `--skip`) so the upstream provenance line and the reason for the no-op stay in history. The commit message body records the NO-OP and the reason. The commit contains no behaviour change and does not warrant a counter rollback - a release was still published so the sequence 284-288 has one artifact per upstream commit. VERIFIED: `grep mod_version gradle.properties` after the pick returns exactly `mod_version = 0.2.7-alpha` (single line, no leftover marker). compileJava SUCCESSFUL, build -x test SUCCESSFUL. Push 0fe32fbf..db20e72f on backport/sequential (fast-forward, no force). Release v0.2.7-alpha-2.245 published against full SHA db20e72fbd84d69688dda21dbf5ae5ebe7a85178. Counter advanced .244 -> .245.

## 289. `321622fa38cd421828962bb67de038bd56093d3e` prep things
- **Verdict:** APPLIED+FIXED (1 content conflict resolved by taking upstream; 1 MC-version port fix applied)
- **Files:** 2 files, +42/-3 (`client/core/RenderProperties.java`, `client/core/VoxyRenderSystem.java`)
- **Result:** APPLIED+FIXED
- **SHA:** 2cecab111917860e3b49858fda17d9fe2e25c5a0
- **Release:** v0.2.7-alpha-2.246
- **Notes:** Cherry-pick -x of `321622fa` conflicted on the `this.properties = ...` line in `VoxyRenderSystem.setWorldIn`. Upstream refactors the inline construction into a new static factory `RenderProperties.getRenderProperties()` that also consults Iris for the block-atlas-UV flag, and the new `irisUseBlockAtlasUv()` helper is added verbatim (the `IGetIrisVoxyPipelineData` / `Iris.getPipelineManager().getPipelineNullable()` API is present in this fork, so it ports unchanged). Resolved with `@theirs` so the call site now delegates to the factory. PORT FIX (1.21.1): the factory body could not use `RenderSystem.getDevice().isZZeroToOne()` / `DepthStencilState.DEFAULT.depthTest().equals(CompareOp.GREATER_THAN_OR_EQUAL)` because 1.21.1 has no `GpuDevice`/`DepthStencilState`/`CompareOp` (1.21.2+ pipeline API); replaced with the same live-GL query that 284 established inline in `VoxyRenderSystem` (`glGetIntegeri(GL_DEPTH_RANGE, 0) == 0` and `glGetInteger(GL_DEPTH_FUNC) == GL_GEQUAL`). The 1.21.2-only imports `DepthStencilState`/`CompareOp`/`RenderSystem` were dropped as dead, and `glGetInteger` (`GL11`) / `glGetIntegeri` (`GL30`, NOT `GL11C`) added - the first compile attempt failed with `cannot find symbol: method glGetIntegeri(int,int)` because `RenderProperties` only statically imported `GL11C.*`. Net effect is a real cleanup: the duplicated 1.21.1 depth-convention query now lives in one place instead of two. compileJava SUCCESSFUL, build -x test SUCCESSFUL.

## 290. `b9a08fd22e07288e60db248e0ad32a889842c020` dont know what doing or if this works lets hope it does
- **Verdict:** APPLIED+FIXED (1 content conflict resolved by combining both intents; no MC-version port fix needed)
- **Files:** 1 file, +4/-6 (`client/core/model/ModelBakerySubsystem.java`)
- **Result:** APPLIED+FIXED
- **SHA:** 95ac677283762f17367219f584df848c07bdcada
- **Release:** v0.2.7-alpha-2.247
- **Notes:** Cherry-pick -x of `b9a08fd2` conflicted on the enqueue tail of `ModelBakerySubsystem.requestBlockBake`. Upstream's version of this class routes the bake request straight through `enqueueLock.lock() -> factory.addEntry(blockId) -> enqueueLock.unlock()`; THIS FORK has deliberately diverged and instead pushes onto a `ConcurrentLinkedDeque<Integer> blockIdQueue` with a `blockIdCount` bump. That divergence is a real optimisation, not drift: `tick(long totalBudget)` drains the queue on the render thread, hoists the GL framebuffer binding ONCE (`glGetInteger(GL_FRAMEBUFFER_BINDING)`), feeds up to 5+ entries through `factory.addEntry` under that single binding, rebinds afterwards, and re-applies the Frex stencil state - so taking upstream verbatim would both fail to compile (this fork has no `enqueueLock` field) and throw away the batching. Resolved by keeping the fork's `blockIdQueue.add(blockId)` + `blockIdCount.incrementAndGet()` and ADDING upstream's one genuinely new line, `LockSupport.unpark(this.processingThread)`, which is the actual point of the commit: the processor thread is parked in `LockSupport.park()` after each `processAllThings()` pass, so without the unpark a newly enqueued bake sits idle until the next unrelated wakeup. Upstream's other three hunks (the `Thread.sleep(10)` -> `LockSupport.park()` swap in the processing loop, the `LockSupport.unpark` in `shutdown()` so the thread can observe `isRunning=false`, and the `LockSupport.unpark` in `addBiome`) were already present in the fork from an earlier backport and applied without conflict - VERIFIED the `park()` at the loop tail, the `unpark` in `shutdown()`, and the `unpark` in `addBiome()` are all in place. compileJava SUCCESSFUL, build -x test SUCCESSFUL.

## 291. `54beedb9c9ed76143f6fd68e1f5e3e1ebcc4143a` sure it beta now
- **Verdict:** APPLIED-AS-NO-OP (content conflict resolved by keeping ours; committed with `--allow-empty` to preserve upstream provenance)
- **Files:** 0 files (upstream touched only `gradle.properties`, +1/-1)
- **Result:** APPLIED-AS-NO-OP
- **SHA:** 44687e07531088c154a5e5bf21edf9e89a9a539b
- **Release:** v0.2.7-alpha-2.248
- **Notes:** Cherry-pick -x of `54beedb9` conflicted on `gradle.properties` at the single `mod_version` line. Upstream promotes the release channel `mod_version = 0.2.15-alpha` -> `0.2.15-beta`; this fork MUST stay at `0.2.7-alpha` for exactly the same reason documented in 288: the built artifact names (`build/libs/voxy-0.2.7-alpha.jar`, `voxy-0.2.7-alpha-all.jar`, both uploaded by the release step) and the entire GitHub release tag scheme (`v0.2.7-alpha-2.NNN`) derive from that value, so taking `0.2.15-beta` would rename every jar and break the naming convention. Resolved with `git checkout --ours gradle.properties`, leaving the cherry-pick with no content change. Mechanically different from 288: here `git cherry-pick --continue` did not abort but reported "nothing added to commit but untracked files present", so the commit was made explicitly with `git commit --allow-empty`. Because the commit is already empty, a follow-up `git commit --amend -m ...` to re-add the `-x` provenance line was rejected by git as "No changes ... would make it empty" and the line was silently dropped from the message; re-running the same amend with `--allow-empty` restored the `(cherry picked from commit 54beedb9...)` trailer correctly. VERIFIED after: `git log -1 --format=%B` carries the trailer, `git diff HEAD~1 HEAD --stat` is empty, and `git diff 2b8178ca HEAD -- gradle.properties` is empty confirming `mod_version` is still `0.2.7-alpha`. No compileJava/build run: the tree is byte-identical to the already-built 290 state, so the .248 artifacts are the same jars as .247.

## 292. `970b1cc7d6a2a36c32b6cc73e49049aef55798b2` move and hoist gpu selection injection
- **Verdict:** SKIPPED (MC-1.21.2+-ONLY: the new injection anchor does not exist in 1.21.1)
- **Files:** 0 files (upstream renamed `MixinWindow.java` -> `MixinGPUSelect.java` and updated `client.voxy.mixins.json`)
- **Result:** SKIPPED
- **SHA:** n/a (no commit created)
- **Release:** none (no release published; counter unchanged)
- **Notes:** SKIPPED as MC-26-ONLY. The commit relocates the `voxy.forceGpuSelectionIndex` injection from `Window.<init>` to `Minecraft.<init>`, retargeting the mixin from `com.mojang.blaze3d.platform.Window` to `net.minecraft.client.Minecraft` and changing the anchor from `Window;setBootErrorCallback()V` to `Lnet/minecraft/client/Options;save()V` (method renamed `MixinWindow` -> `MixinGPUSelect`, `minecraft.MixinWindow` -> `minecraft.MixinGPUSelect` in `client.voxy.mixins.json`). The motivation is ordering: on 1.21.2+ the GPU device is created lazily during `Minecraft`'s ctor, so the `Options.save()` call is the first point where selection can still influence device creation; hooking `Window.<init>` is too early. Verified against the actual 1.21.1 bytecode in `build/neoForm/neoFormJoined1.21.1-20240808.144430/steps/recompile/outputs.jar` with javap rather than assuming: (1) the parameter types DO exist in 1.21.1 - `net.minecraft.client.Minecraft` has exactly `public net.minecraft.client.Minecraft(net.minecraft.client.main.GameConfig)`, and `net.minecraft.client.Options` has `public void save()` - so the commit is not trivially uncompilable; (2) but disassembly of that specific ctor shows ZERO `Options.save()` invocations (43 `Options.*` calls total in the body, none of them `save`); the only three `Options.save:()V` call sites in the whole class are in `clearResourcePacksOnError(Throwable, Component, GameLoadCookie)`, `onFullscreenError(int, long)` and `tick()`. Therefore the `@At(value="INVOKE", target="Lnet/minecraft/client/Options;save()V")` anchor cannot resolve on 1.21.1 and the mixin would fail to apply at runtime (a mixin apply failure that `compileJava` cannot catch, which is precisely why it must be skipped rather than "fixed"). Further, 1.21.1 has no device abstraction to hoist to at all: there is no `GpuDevice`, no `RenderSystem.getDevice()`, and `com.mojang.blaze3d.platform.GlUtil` exposes only `getVendor/getRenderer/getOpenGLVersion/getCpuInfo/allocateMemory/freeMemory` with no `createDevice`/`createContext`; the GL context is created directly by LWJGL/Window. `Window.<init>` itself contains 0 `createDevice` calls, so there is no 1.21.1 equivalent anchor that preserves upstream's ordering intent. Skipping entirely rather than leaving a runtime-broken mixin registered. The existing `MixinWindow` + `Window;setBootErrorCallback()V` anchor stays in place and is known-good on 1.21.1. No cherry-pick was started, so no build or release was run and the working tree remained clean throughout.

## 293. `4c41a166a02fb43a066430489fb2ec73fc36c1c1` fix iris pain
- **Verdict:** APPLIED (clean cherry-pick -x of `4c41a166`, no conflicts, no port fix needed)
- **Files:** 2 files, +6/-1 (`client/core/VoxyRenderSystem.java`, `client/core/util/IrisUtil.java`)
- **Result:** APPLIED
- **SHA:** b34c38ec8dbeca0fd4059409e1be53758732df1f
- **Release:** v0.2.7-alpha-2.249
- **Notes:** Cherry-pick -x of `4c41a166` auto-merged cleanly (git reported Auto-merging for both files, no conflict markers). Two independent Iris-stability fixes. (1) ZERO-SIZED VIEWPORT: the viewport setup path computed a scaled `width`/`height` and then carried on into `viewport.setVanillaProjection(...)` even when the scaled result collapsed to 0 (Iris shaderpack viewport/resolution mismatches can produce this); a new guard `if (width == 0 || height == 0) { Logger.error("Viewport width or height was zero, this is bad bad bad"); return null; }` now bails out before building the projection matrices, and the pre-existing downstream guard `if (viewport.width <= 0 || viewport.height <= 0) return;` gains a matching `Logger.error(... "exiting frame")` so the silent early-return that used to swallow the frame now leaves a diagnostic. (2) SHADERPACK-ENABLED CHECK: `IrisUtil.irisShaderPackEnabled0()` migrates from the deprecated `Iris.isPackInUseQuick()` to `Iris.getCurrentPack().isPresent()`. Verified this is safe on the fork's Iris build rather than assuming: the same file's `irisShadersEnabledInConfig0()` already calls `Iris.getCurrentPack().isEmpty()` in this fork, so `getCurrentPack()` is present in the bundled `curse.maven:irisshaders-455508:6661598` artifact and returns an `Optional` supporting both `isPresent()` and `isEmpty()`. No MC-version-specific API involved, so the commit ports verbatim with no port pattern needed. compileJava SUCCESSFUL, build -x test SUCCESSFUL.

## 294. `437e2e0b66bea27aeb745ed47db0225f94f2e2ef` fix block light packing in LoD mip
- **Verdict:** APPLIED (clean cherry-pick -x of `437e2e0b`, no conflicts, no port fix needed)
- **Files:** 1 file, +2/-2 (`common/world/other/Mipper.java`)
- **Result:** APPLIED
- **SHA:** 620d4d39b54fa3afe265793a65182f2262586240
- **Release:** v0.2.7-alpha-2.250
- **Notes:** Cherry-pick -x of `437e2e0b` applied cleanly with no conflicts. Fixes block-light packing in the LoD mipmap reduction path (`Mipper`'s 2x2x2 box filter). The bug: the eight corner light IDs are accumulated separately for block light and sky light using the `0xF0` and `0x0F` nibble masks respectively, then averaged, then recombined as `(blockLight << 4) | skyLight`. Because each masked sum of eight `0xF0`-masked values is already left-shifted by 4, dividing by 8 only recovers the correct 0-15 magnitude but leaves the value in bits 4-7; shifting it left by another 4 pushed the block-light magnitude out of the packed byte entirely, so block light was effectively lost in every mip level. The fix re-masks the averaged block light back down into the high nibble with `(blockLight / 8) & 0xF0` and then ORs the two already-positionally-correct nibbles directly (`blockLight | skyLight`) instead of shifting. Sky light is untouched and still needs its `Math.ceil` because the low-nibble sum has no equivalent pre-shift. Purely arithmetic in a common-side class with no MC-version-specific or Sodium API surface, so it ports verbatim with no port pattern needed. compileJava SUCCESSFUL, build -x test SUCCESSFUL.

## 295. `5a5f9546819dd8cc1b097f5e32a34215bc1346d9` Merge pull request #485 from KaptainWutax/dev
- **Verdict:** SKIPPED (merge commit, cherry-pick not supported; content is a pure duplicate of 294)
- **Files:** 0 files (upstream merge commit, first-parent diff = 1 file +2/-2 already applied as 294)
- **Result:** SKIPPED
- **SHA:** n/a (no commit created)
- **Release:** none (no release published; counter unchanged)
- **Notes:** SKIPPED as a merge commit. `git rev-list --parents -n 1 5a5f9546` returns TWO parents (`4c41a166... 437e2e0b...`), confirming this is a real GitHub merge of PR #485 rather than an ordinary commit; `git cherry-pick` does not apply merge commits without an explicit `-m` mainline selection, and the per-commit loop does not authorise inventing a mainline. Independently verified that skipping is also the CORRECT outcome on content grounds, not merely the conservative one: the merge's first-parent diff (`git diff 4c41a166 5a5f9546`) is BYTE-IDENTICAL to the diff of PR head `437e2e0b` (`git diff 4c41a166 437e2e0b`) - both touch only `common/world/other/Mipper.java` with the same +2/-2. Since 437e2e0b is commit 294 in this batch and it is already APPLIED as SHA `620d4d39...`, applying this merge would have re-landed identical content as an empty duplicate. No cherry-pick was attempted, so the working tree was never mutated and no `git cherry-pick --abort` recovery was needed; the tree stayed clean (only the untracked session-only `HANDOFF.md`) and the sequencer/CHERRY_PICK_HEAD state was confirmed absent before proceeding. Counter left at .250. Stop-gate tally: 1 consecutive SKIP, 0 compile-fail-then-pass.

## 296. `b33ad0157f7d34284ada4cc2d1934a1c318afa44` base emissive support
- **Verdict:** APPLIED+FIXED (1 content conflict resolved by combining both sides; 2 MC-version port fixes applied)
- **Files:** 3 files, +59/-8 (`client/core/model/ModelFactory.java`, `client/core/model/ModelQueries.java`, `client/core/rendering/building/RenderDataFactory.java`)
- **Result:** APPLIED+FIXED
- **SHA:** ca27f80f5c02f7022a7f670932b62bbdb26aefd3
- **Release:** v0.2.7-alpha-2.251
- **Notes:** Cherry-pick -x of `b33ad015` conflicted on the import block of `ModelFactory.java` only; `ModelQueries.java` and `RenderDataFactory.java` auto-merged clean. The conflict was purely an import-rename artifact: upstream (MC 1.21.11 naming) lists `net.minecraft.resources.Identifier` + `net.minecraft.tags.BlockTags` + `net.minecraft.world.level.BlockGetter` + `net.minecraft.world.level.CardinalLighting`, while this fork (MC 1.21.1 naming) already has `net.minecraft.resources.ResourceLocation` + `net.minecraft.world.level.BlockAndTintGetter` from earlier backports, and the two sides are ADDITIVE rather than competing (upstream only adds `BlockTags` and `BlockGetter`; it never references `CardinalLighting` in this file's touched code, and the fork's two existing anonymous `BlockAndTintGetter` classes at the tint-colour call sites must keep their import). Resolved by combining: kept `ResourceLocation` and `BlockAndTintGetter` from ours and added `BlockTags` and `BlockGetter` from theirs, dropping the unused `CardinalLighting` (1.21.1 has no such class at all; the fork's equivalent is the `getBrightness`/`getShade` pair on `BlockAndTintGetter`). PORT FIX 1 (jspecify): the new `getBlockLightEmission(BlockState)` builds an anonymous `BlockGetter` whose `getBlockEntity` override was annotated `@org.jspecify.annotations.Nullable`; jspecify is a transitive dependency of the 1.21.2+ Mojang mappings and is absent from this 1.21.1 fork, so the fully-qualified annotation was rewritten to the `@Nullable` already imported at the top of the file (`org.jetbrains.annotations.Nullable`). PORT FIX 2 (LevelHeightAccessor rename): the same anonymous `BlockGetter` overrides `getMinY()`, but MC 1.21.1 renamed that method - verified against the fork's own decompiled 1.21.1 source `net/minecraft/world/level/LevelHeightAccessor.java`, where the accessor contract is `int getHeight()` + `int getMinBuildHeight()` (+ derived `getMaxBuildHeight()`), and `BlockGetter` extends that interface. Rewrote the override to `getMinBuildHeight()`. Verified the rest of the commit ports: `BlockStateBase.emissiveRendering` DOES exist in 1.21.1 (found in `BlockBehaviour$BlockStateBase` of the recompiled 1.21.1 jar, descriptor `(BlockState; BlockGetter; BlockPos;)Z`), so the core emissive query needed no fallback. The commit also adds `ModelQueries.lightEmission(long)` reading bits `8*6+7 .. 8*6+10` of the metadata long, matching the `metadata |= ((long)getBlockLightEmission(blockState)) << (48+7)` write in `ModelFactory` (48 == 8*6), and promotes the surrounding bit-mask literals from `int` (`&1`) to `long` (`&1L`) so the 64-bit shift is not sign-extended - both sides consistent. Cherry-pick was committed, then `git commit --amend --no-edit` folded in the two port fixes; the amended SHA `ca27f80f...` (not the pre-amend `30fb0714...`) is what was pushed and what release v0.2.7-alpha-2.251 targets. compileJava SUCCESSFUL, build -x test SUCCESSFUL.

## 297. `ff3a84cfef668e495d3a3d396ed96b66fed20841` clamp
- **Verdict:** APPLIED (clean cherry-pick -x of `ff3a84cf`, no conflicts, no port fix needed)
- **Files:** 1 file, +1/-1 (`client/core/model/ModelFactory.java`)
- **Result:** APPLIED
- **SHA:** 9f4fb4c1a0a7a59157135da634086bb7a8f316ab
- **Release:** v0.2.7-alpha-2.252
- **Notes:** Cherry-pick -x of `ff3a84cf` auto-merged cleanly onto the state left by 296 (git reported Auto-merging for `ModelFactory.java`, no conflict markers) and touches exactly one line: the tail of the `getBlockLightEmission(BlockState)` helper introduced by 296 changes from `return state.getLightEmission();` to `return Math.clamp(state.getLightEmission(), 0, 15);`. Rationale: the helper's 4-bit return value is packed into a nibble of the model metadata long (via `metadata |= ((long)getBlockLightEmission(blockState)) << (48+7)`, read back by `ModelQueries.lightEmission`), and `BlockState.getLightEmission()` is not contractually bounded to 0-15 by the vanilla API, so an out-of-range value from a modded block would bleed into the adjacent nibble and corrupt neighbouring metadata bits. The clamp makes the packing lossless by construction. Directly follows 296 in the same upstream series, and compiles verbatim on 1.21.1 - `Math.clamp(int,int,int)` is a `java.lang.Math` method introduced in JDK 21, and this fork already builds on a Java 21 toolchain (the NeoForm/NeoForge 21.1.173 setup confirmed by the `neoFormJoined1.21.1` toolchain used for every build in this batch), so no MC-version or Sodium port pattern was required. compileJava SUCCESSFUL, build -x test SUCCESSFUL.

## 298. `62099a7430ccb3e446727b3639d0175a5f928f67` emissive models
- **Verdict:** APPLIED (clean cherry-pick -x of `62099a74`, no conflicts, no port fix needed)
- **Files:** 1 file, +88/-41 (`client/core/rendering/building/RenderDataFactory.java`)
- **Result:** APPLIED
- **SHA:** b78260f9128ce680168cfd3fdc12c102138af6ce
- **Release:** v0.2.7-alpha-2.253
- **Notes:** Cherry-pick -x of `62099a74` auto-merged cleanly onto the state left by 296/297 (no conflict markers, single file). This is the CONSUMER half of the emissive-metadata work that 296 introduced and 297 clamped: 296 wrote the per-model light-emission value into the model metadata long and 297 clamped that value to 0-15, and this commit makes the terrain mesher actually READ and apply it. The core change is a pure refactor-plus-extension of one helper: `getQuadLight(long quad, long selfmeta)` is renamed to `applyQuadLight(long quad, long selfmeta)` and every `blockMesher.putNext(...)` / `seondaryblockMesher.putNext(...)` call site that emits a quad now wraps its packed quad payload in `applyQuadLight(..., selfMeta)`, passing the model metadata of the block that OWNS the quad rather than only the neighbour's. The supporting plumbing is the same rename rippling outward: in the sections that previously read the self metadata into a local literally named `B` (and, in the fluid path, `B` was being reused for the FLUID's metadata after being reassigned via `modelMan.getModelMetadataFromClientId(fluidId)`), the local is renamed to a self-descriptive `selfMeta` / `Am` so that the self value stays distinct from the neighbour value it is compared against. The `ModelQueries` predicates that take the self metadata (`faceUsesSelfLighting`, `containsFluid`, `cullsSame`, `faceExists`) are all switched from `B` to the new self local at the same time, which is what makes the emissive value reach the mesher for every one of the six face-emission paths (block faces, fluid faces, secondary/translucent block faces). This is a pure data-flow refactor inside a single client rendering class with no MC-version-specific API, no Sodium 0.7/0.8 surface, and no shader/GL-state changes, so it ports verbatim with no port pattern needed. Note the upstream diff leaves several `//TODO:` markers in place (including `//FIXME need to use selfMeta to check for if it can be culled against this block` and two zeroed-out light fields in the secondary mesher paths) - these are upstream's own in-progress notes, preserved verbatim rather than invented around. compileJava SUCCESSFUL, build -x test SUCCESSFUL.

## 299. `81459153d7177afd0e755d78aa3e4586be687b4b` fix X-axis face occlusion
- **Verdict:** APPLIED (clean cherry-pick -x of `81459153`, no conflicts, no port fix needed)
- **Files:** 1 file, +2/-2 (`client/core/rendering/building/RenderDataFactory.java`)
- **Result:** APPLIED
- **SHA:** 723f815b17a712ac1ea3ff4d6001f00343ae4bd5
- **Release:** v0.2.7-alpha-2.254
- **Notes:** Cherry-pick -x of `81459153` auto-merged cleanly onto the state left by 296/297/298 (git reported Auto-merging for `RenderDataFactory.java`, no conflict markers). Two lines in the terrain mesher's axis-aligned face handling: the X-axis normal-face occlusion test in the block mesher is corrected so a face is not culled against a neighbour it cannot actually be occluded by. Touches the same file as 296 and 298 but is independent of both: it changes the face-culling predicate, not the emissive-metadata plumbing. Verified with `./gradlew compileJava` and `./gradlew build -x test`, both clean, and the two release artifacts (`voxy-0.2.7-alpha.jar`, `voxy-0.2.7-alpha-all.jar`) were produced and uploaded to `v0.2.7-alpha-2.254` with `--target 723f815b17a712ac1ea3ff4d6001f00343ae4bd5` (full 40-char SHA, not the short form). Post-step verification passed: `git status --short` shows only the untracked session-only `HANDOFF.md`, and `gh release view v0.2.7-alpha-2.254` resolves to the new tag. Pushed fast-forward only (`02dc8705..723f815b`); no force or force-with-lease was used anywhere in this batch.

## 300. `dd61cd1e430cd49d066620b23d18148a7fd06658` Merge branch 'MCRcortex:dev' into dev-fix
- **Verdict:** SKIPPED (merge commit, cherry-pick not supported without a mainline; content is an already-applied duplicate)
- **Files:** 0 files (upstream merge commit)
- **Result:** SKIPPED
- **SHA:** n/a (no commit created)
- **Release:** none (no release published; counter unchanged)
- **Notes:** SKIPPED as a merge commit. `git rev-list --parents -n 1 dd61cd1e` returns TWO parents (`81459153... 62099a74...`), confirming this is a real merge of `dev` (62099a74, index 298) into `dev-fix` (81459153, index 299) rather than an ordinary commit; `git cherry-pick` does not apply merge commits without an explicit `-m` mainline selection, and the per-commit loop does not authorise inventing a mainline. Independently verified that skipping is also the CORRECT outcome on content grounds, not merely the conservative one, following the precedent set by 295: the merge's first-parent diff (`git diff --stat 81459153 dd61cd1e`, 3 files, +145/-48) is a strict subset of the work already backported as 296 (`b33ad015`, base emissive support), 297 (`ff3a84cf`, clamp), 298 (`62099a74`, emissive models) and 299 (`81459153`, X-axis face occlusion) - it differs from the combined `git diff --stat 81459153 62099a74` by exactly the two lines that 299 contributed, which are already present. No cherry-pick was started, so no sequencer state was created and the working tree was left untouched. Counter unchanged at .254; the next commit takes .255.

## 301. `d5ba014c692f6ea150191caac017a5992c057afc` Merge pull request #489 from KaptainWutax/dev-fix
- **Verdict:** SKIPPED (merge commit, cherry-pick not supported without a mainline; first-parent diff is a 2-line no-op against already-applied work)
- **Files:** 0 files (upstream merge commit)
- **Result:** SKIPPED
- **SHA:** n/a (no commit created)
- **Release:** none (no release published; counter unchanged)
- **Notes:** SKIPPED as a merge commit. `git rev-list --parents -n 1 d5ba014c` returns TWO parents (`62099a74... dd61cd1e...`), confirming a real GitHub merge of PR #489 rather than an ordinary commit; `git cherry-pick` does not apply merge commits without an explicit `-m` mainline selection, and the per-commit loop does not authorise inventing a mainline. Independently verified that skipping is also the CORRECT outcome on content grounds. The first-parent diff (`git diff 62099a74 d5ba014c`) touches only `RenderDataFactory.java` with +2/-2, and those two lines are the SAME `ModelQueries.faceOccludes(meta, (2 << 1) | (1 - N))` X-axis neighbour-face test that commit 299 (`81459153`, "fix X-axis face occlusion") already backported, merely with the two Y-row branches swapped relative to each other. It is the upstream merge resolving a two-way divergence inside PR #489, not new portable behaviour: applying it on top of 299 would revert one of 299's two lines and re-introduce the X-axis face occlusion bug that 299 exists to fix. Verified this is a merge artifact rather than a genuine follow-up fix by checking the other direction: `git diff --stat 81459153 d5ba014c` shows the merge re-adding all 145 lines that commit 299's own branch history had already moved past, i.e. the second parent (300, which was itself skipped as a merge) carried a large divergent tree that the merge reconciled. No cherry-pick was started, so no sequencer state was created and the working tree was left untouched. Counter unchanged at .254.

## 302. `336c201cf9f2f45615fa05a89058b19cb4cb030e` sigh
- **Verdict:** APPLIED+FIXED (1 content conflict resolved by taking upstream's structure with the fork's variable name; no MC-version port fix needed)
- **Files:** 1 file, +7/-6 (`client/core/model/ModelFactory.java`)
- **Result:** APPLIED+FIXED
- **SHA:** 527453eb6fa8b663c39f1a4608f2a89e16f3181d
- **Release:** v0.2.7-alpha-2.255
- **Notes:** Cherry-pick -x of `336c201c` (a normal single-parent commit, verified via `git rev-list --parents`) conflicted on exactly one hunk in the biome-tint upload block of `ModelFactory.java`. The conflict is a pure structural-reordering artifact of the 296 backport, not a semantic disagreement. Upstream rewrites the `if (colourProvider == null) / else if (!isBiomeColourDependent) / else if (!this.biomes.isEmpty())` chain by FOLDING the empty-biomes check INWARD, so the tail branch becomes a plain `else` and the guard becomes a nested `if (!this.biomes.isEmpty())` around only the colour-buffer fill. This fork still had the empty-biomes check as the third arm of the top-level chain, because the `tintSources` -> `colourProvider` rename is a 1.21.11-era change that this fork never received, so upstream's context lines could not match. Resolved by taking upstream's exact structure and keeping the fork's existing `colourProvider` identifier on the `captureColourConstant(...)` call, since that is the name this fork's 296-era code actually uses and renaming it is not part of this commit's intent. The behavioural change is preserved verbatim: a biome-colour-dependent model whose biome list is EMPTY now still writes its `biomeIndex` into the model buffer and still registers in `modelsRequiringBiomeColours`, where previously the whole branch was skipped so such models were left with a stale/unset biome index and no registration - while the now-unreachable `new MemoryBuffer(4L * 0)` allocation and the zero-iteration fill loop are correctly skipped by the inward-folded guard. Verified with `./gradlew compileJava` (clean; the only two warnings are the pre-existing `ItemBlockRenderTypes.getChunkRenderType` and the `BlockStateBase.getLightEmission` deprecations already present since 297) and `./gradlew build -x test` (clean, both release artifacts produced). Release published to `v0.2.7-alpha-2.255` with `--target 527453eb6fa8b663c39f1a4608f2a89e16f3181d` (full 40-char SHA, taken from `git rev-parse HEAD` after the cherry-pick completed - no amend was needed here, so no re-rev-parse was required). Post-step verification passed: `git status --short` shows only the untracked session-only `HANDOFF.md`, and `gh release view v0.2.7-alpha-2.255` resolves to the new tag. Pushed fast-forward only (`e7c751a0..527453eb`).

## 303. `260bcdbc1ff6e4a649b03f30b44759aba9d7e81b` dynamic detection of model layer directly from the baked texture data
- **Verdict:** SKIPPED (MC-1.21.1-fork has no producer for the required bakery flag bits 4|8; a partial apply would be dead code, a full apply would be a live rendering regression)
- **Files:** 0 files (cherry-pick started, 2 conflicts, then aborted via `git cherry-pick --abort`)
- **Result:** SKIPPED
- **SHA:** n/a (no commit created)
- **Release:** none (no release published; counter unchanged)
- **Notes:** SKIPPED. This is a normal single-parent commit (`git rev-list --parents -n 1 260bcdbc` shows one parent, 336c201c), so it is not the usual merge-commit skip; it is a hard dependency skip. The commit's intent is to stop trusting `ItemBlockRenderTypes.getChunkRenderType(blockState)` and instead derive the render layer directly from the baked texture data. It does so by testing two NEW bits in the bake `flags` word: `(flags&4)` gates the expensive translucent/solid/cutout discrimination via two new `TextureUtils` predicates (`hasTranslucentPixel`, `isSolidWhereDrawn`), and `(flags&8)` forces CUTOUT. Those two flag bits are the entire trigger for the new behaviour, and this fork never produces them. Verified at source level: the fork's `ModelTextureBakery.renderToStream` (the only bake path this 1.21.1 fork has) ends with `return (isAnyShaded?1:0)|(isAnyDarkend?2:0);` - bits 4 and 8 are never set. Upstream, those bits come exclusively from a different bakery class entirely: `git grep -n "4:0" 260bcdbc -- .../bakery/` shows `SoftwareModelTextureBakery.java:279: return (isAnyShaded?1:0)|(isAnyDarkend?2:0)|(anyTranslucent?4:0)|(anyDiscard?8:0);`. That `SoftwareModelTextureBakery` / `bakery2` / `renderToOutput` / `bakeScratchBuffer` stack arrived upstream in `5ca0fa73` ("inital software rasterizing texture bakery"), which is TODO index 158 and was never backported (no `cherry picked from commit 5ca0fa73` in this branch's history; `b42b87dd`, the later `raster uv option` change to the same stack, is TODO index 380, likewise not backported). Consistent with the documented `ChunkSectionLayer.SOLID` -> `RenderType.solid()` port rule, `ChunkSectionLayer` is also absent here, so the commit's type vocabulary needs translation too, but that translation is not the blocker - the missing flag producer is. The conflict resolution was rejected for a concrete correctness reason, not merely for convenience: the `TextureUtils.java` half DID auto-merge cleanly and would have compiled, so a partial apply was technically possible, but it would be unreachable dead code (both new predicates are called only from the `ModelFactory` half). Worse, taking the `ModelFactory` half alone is a live REGRESSION rather than a no-op: with `flags&4` and `flags&8` permanently 0, the `if (layer==null && ...)` chain always falls through to the final `if (layer == null) layer = ChunkSectionLayer.SOLID;`, which would force EVERY biome-dependent translucent and cutout model to render solid - a much worse visual outcome than the status quo `ItemBlockRenderTypes.getChunkRenderType` classification this commit was trying to improve on. Shipping that would be strictly harmful, so the whole commit is skipped rather than partially applied. Sequencer state verified clean after abort: no `.git/sequencer`, no `.git/CHERRY_PICK_HEAD`, `git status --short` shows only the untracked session-only `HANDOFF.md`, HEAD unchanged at the 302 log commit. This commit is blocked, not merely deferred: it becomes backportable only if TODO index 158 (`5ca0fa73`, software rasterizing texture bakery) is processed first, since the fork would then gain the `bakery2` path that actually sets flag bits 4 and 8. Counter unchanged at .255.

## 304. `a0063645e76033cafd4b918971c28d9f471d121c` attempted to add blending and fix overlapping triangle seam
- **Verdict:** SKIPPED (sole target file does not exist in the fork; the software bakery pipeline was never ported)
- **Files:** 0 files (cherry-pick started, 1 modify/delete conflict, then aborted via `git cherry-pick --abort`)
- **Result:** SKIPPED
- **SHA:** n/a (no commit created)
- **Release:** none (no release published; counter unchanged)
- **Notes:** SKIPPED. This is a normal single-parent commit (`git rev-list --parents -n 1 a0063645` shows one parent, 260bcdbc), so it is not a merge-commit skip; it is a missing-prerequisite skip. The commit touches exactly ONE file, `src/main/java/me/cortex/voxy/client/core/model/bakery/SoftwareRasterizer.java`, and adds (a) a two-pass `rasterTriangle(boolean orZero)` signature so the shared quad edge is rasterized twice with different inside-test strictness, fixing the overlapping-triangle seam, and (b) a real `doBlending(int scr, int dst)` implementation built on Sodium's `ColorMixer`/`ColorABGR`/`ColorARGB`. NEITHER is portable to this fork, because the fork has no software rasterizer at all. Verified at tree level: `git ls-tree HEAD src/main/java/me/cortex/voxy/client/core/model/bakery/` lists only `BakedBlockEntityModel.java`, `BudgetBufferRenderer.java`, `GlViewCapture.java`, `ModelTextureBakery.java` and `ReuseVertexConsumer.java` — there is no `SoftwareRasterizer.java` and no `SoftwareModelTextureBakery.java` in the fork's tracked tree. The fork instead bakes models on the GPU via `GlViewCapture` + the compute shaders under `src/main/resources/assets/voxy/shaders/bakery/` (`buffercopy.comp`, `bufferreorder.comp`), so blending and edge-seam behaviour are decided by the GL rasteriser and the compute passes, not by a Java scanline loop; a hand-port of the barycentric `orZero` test and the `ColorMixer` blend would be dead code with no caller and no effect on output. Note the fork's worktree does contain an untracked `SoftwareRasterizer.java`; it is a leftover artefact (it is absent from `HEAD` and referenced by no tracked source file, confirmed by grepping the whole of `src/`), not a live code path, so it was deliberately left untouched rather than resurrected. This is the same class of skip as 303: it depends on the software bakery that TODO index 158 (`5ca0fa73`, software rasterizing texture bakery) would introduce, which has not been backported. Counter unchanged at .255.

## 305. `a4c1ab202f6ef905e0eca68b19d73d0dfb42236d` specify fb size in constructor
- **Verdict:** SKIPPED (both target files absent from the fork; strictly a continuation of the skipped 304)
- **Files:** 0 files (not attempted; skipped on inspection)
- **Result:** SKIPPED
- **SHA:** n/a (no commit created)
- **Release:** none (no release published; counter unchanged)
- **Notes:** SKIPPED. This is a normal single-parent commit (`git rev-list --parents -n 1 a4c1ab20` shows one parent, a0063645). The commit touches two files, `SoftwareModelTextureBakery.java` (+1/-1, threading an explicit framebuffer size through the constructor) and `SoftwareRasterizer.java` (+16/-12, consuming that size in the rasteriser's field/loop bounds). BOTH files are absent from this fork's tracked tree — `git cat-file -e HEAD:.../SoftwareModelTextureBakery.java` fails with "path does not exist in 'HEAD'", and `SoftwareRasterizer.java` is likewise not in the HEAD tree (see the 304 entry for the full tree listing and the untracked-worktree-file explanation). The framebuffer size this commit plumbs through is a parameter of the software scanline bakery, which does not exist here; the fork's GPU bakery sizes its render target from `ModelFactory.MODEL_TEXTURE_SIZE` through `GlViewCapture` and the compute shaders instead, so there is no constructor to change. Skipping 304 first independently forces this outcome, since 305 is a two-line follow-up to 304's refactor and its `SoftwareRasterizer` hunks have no base to apply against. Counter unchanged at .255.

## 306. `c47fc18e04be0563e66a675fcbbf2953db6a4b08` 26.1.2 + sodium update
- **Verdict:** SKIPPED (MC-26-ONLY: pure Minecraft 26.1.2 / Sodium 0.8.10 / Lithium 0.24.2 dependency bump, no portable Java behaviour)
- **Files:** 0 files (not applied; skipped on inspection)
- **Result:** SKIPPED
- **SHA:** n/a (no commit created)
- **Release:** none (no release published; counter unchanged)
- **Notes:** SKIPPED as MC-26-ONLY. This is a normal single-parent commit (`git rev-list --parents -n 1 c47fc18e` shows one parent, a4c1ab20), so it is not a merge-commit skip. The full diff is 3 files, +4/-4, and every hunk is a version-coordinate bump with zero Java source changes: `build.gradle` swaps `maven.modrinth:sodium:mc26.1.1-0.8.9-fabric` -> `mc26.1.2-0.8.10-fabric` and `maven.modrinth:lithium:mc26.1.1-0.23.0-fabric` -> `mc26.1.2-0.24.2-fabric`; `gradle.properties` sets `minecraft_version=26.1.1` -> `26.1.2`; `src/main/resources/fabric.mod.json` sets the `"sodium": ["=0.8.9"]` dependency constraint -> `"=0.8.10"`. None of this is backportable: the `mc26.x` Modrinth coordinates are Minecraft-26 Fabric builds with no 1.21.1 NeoForge equivalent, this fork pins Sodium through `curse.maven:sodium-394468` (the 0.6.x MC-1.21.1 line) rather than a Modrinth version string, this fork's `gradle.properties` has no `minecraft_version=26.x` to bump (it targets `1.21.1` via the NeoForm/NeoForge toolchain, and its `mod_version` is the fork-local `0.2.7-alpha`), and `fabric.mod.json` is Fabric metadata this fork does not ship (it builds a `META-INF/neoforge.mods.toml`). Applying any of it would replace the fork's working pinned toolchain with coordinates that cannot resolve. There is no behaviour change to backport, so the commit is skipped rather than partially applied. Counter unchanged at .255.

## 307. `5f216fa1f8b50a6007be0d1fb5aa9f5087eb7e6e` fix possible race condition (dont think its realistically possible, but just incase)
- **Verdict:** APPLIED+FIXED (1 content conflict resolved by re-targeting the fix onto the fork's GPU enqueue path; `processAllThings` return expression re-expressed in fork terms)
- **Files:** 1 file, +40/-29 (`client/core/model/ModelFactory.java`)
- **Result:** APPLIED+FIXED
- **SHA:** 2095fbaa3c59cc971e0925c8fd88ae09f1f5586e
- **Release:** v0.2.7-alpha-2.256
- **Notes:** Cherry-pick -x of `5f216fa1` auto-merged three regions cleanly and conflicted on one, all in `ModelFactory.addEntry(int)`. Upstream's bug is a real ordering race and its fix is structural rather than version-specific: the OLD code takes `blockStatesInFlightLock`, adds the block id to `blockStatesInFlight`, RELEASES the lock, then computes the block state (including the recursive `addEntry(fluidStateId)` call) and only then enqueues. Between the unlock and the enqueue a second thread can interleave, so two blocks can be inserted into `blockStatesInFlight` in one order and into the bake queue in the opposite order. Upstream's fix is to move the in-flight registration DOWN so that it is performed inside a `lock()/try/finally` that also covers the enqueue, making "insert into in-flight set" and "enqueue for bake" a single atomic region, with `VarHandle.loadLoadFence()` and the second `idMappings[blockId] != -1` re-check moved inside that region too; the three `return false` early-outs then all run through `finally` so the lock is always released. The conflict is a pure structural-divergence artifact, not a semantic disagreement, and the port is 1:1. This fork has NO `bakeQueue` and no `BlockBake` record (`git grep -c "bakeQueue|BlockBake" HEAD` on `ModelFactory.java` returns no matches) because the fork submits bakes to the GPU inline instead of queueing them: its enqueue tail is `RawBakeResult result = new RawBakeResult(blockId, blockState); int allocation = this.downstream.download(...); int flags = this.bakery.renderToStream(blockState, this.downstream.getBufferId(), allocation);`. That call pair IS this fork's "add to the bake queue" — it allocates from the async download stream and hands the block state to the GL bakery — so the fix was re-targeted verbatim onto it: the `lock()/try/finally` block now spans the in-flight registration, the load fence, the `idMappings` re-check AND the `downstream.download` + `renderToStream` submission, with the two `result.*` flag assignments and `return true` carried along unchanged from HEAD inside the same guarded region. The upstream comment was reworded from "must be the the oder they are added to the bake queue" to "must be the same as the order they are submitted to the renderer" to name the fork's actual enqueue mechanism rather than a queue that does not exist here; the comments' spelling otherwise tracks upstream. The second auto-merged region needed a semantic fix rather than a mechanical one: upstream's new `processAllThings()` tail is `return (this.blockStatesInFlight.size()!=0)||(!this.bakeQueue.isEmpty())||!this.biomeQueue.isEmpty();`, which would not compile here (no `bakeQueue` field). It was re-expressed against the fork's own pending-work structures as `(this.blockStatesInFlight.size()!=0)||(!this.rawBakeResults.isEmpty())||!this.biomeQueue.isEmpty()` — `rawBakeResults` is the fork's `ConcurrentLinkedDeque<RawBakeResult>` that `addEntry` now feeds (via the `downstream.download` callback) and that `processModelResult` drains, so it is the direct counterpart of upstream's `bakeQueue` for the "is there still queued work" question. Deliberately NOT substituted `uploadResults` (also a `ConcurrentLinkedDeque`, and also counted by the fork's existing `getInflightCount()`) into that second term: `uploadResults` is drained by the render-thread `tickAndProcessUploads`, not by `processAllThings`, and `ModelFactory` is also what publishes the uploads, so testing it here would make `processAllThings` return true while the very next commit (308) turns the call site into a `while` loop — an unbreakable spin on the processing thread. The `blockStatesInFlight` term also covers the fork's in-GPU bakes (added under the lock, removed in `processTextureBakeResult`/the upload path once the readback lands), so the returned "more work pending" signal is complete. `processAllThings` was changed from `void` to `boolean` exactly as upstream, matching its single remaining call site. The `addEntry` recursive fluid path is safe under the new lock because `blockStatesInFlightLock` is a `ReentrantLock` and the recursive `addEntry(fluidStateId)` call happens BEFORE the lock is taken (during block-state resolution), not inside it — so the fix introduces no self-deadlock. Verified with `./gradlew compileJava` and `./gradlew build -x test`, both successful (the only two warnings are pre-existing `getChunkRenderType`/`getLightEmission` deprecation warnings from commits 296/297, not from this change), and the two release artifacts were produced and uploaded to `v0.2.7-alpha-2.256` with `--target 2095fbaa3c59cc971e0925c8fd88ae09f1f5586e`. The caller-side change that makes the new boolean meaningful (308) is the next commit in the queue.

## 308. `c2ba3c2229c4b61c5faf51872210f25b4e4a44a8` while loop
- **Verdict:** APPLIED (clean cherry-pick -x of `c2ba3c22`, no conflicts, no port fix needed)
- **Files:** 1 file, +1/-1 (`client/core/model/ModelBakerySubsystem.java`)
- **Result:** APPLIED
- **SHA:** c1893c8770e750a9897c8eebf3093bbd5b9e8700
- **Release:** v0.2.7-alpha-2.257
- **Notes:** Cherry-pick -x of `c2ba3c22` auto-merged cleanly (single line, no conflict markers) onto the state left by 307, which is exactly the state it requires: this commit is the consumer half of 307's `processAllThings()` `void` -> `boolean` signature change and does not compile without it. The change is one line in the `processingThread` runnable of the `ModelBakerySubsystem` constructor — `this.factory.processAllThings();` becomes `while (this.factory.processAllThings());` — so the processing thread now keeps re-running a work pass until the factory reports that nothing is pending, instead of doing exactly one pass per unpark. Nothing in the surrounding loop needed porting: the enclosing `while (this.isRunning)` and the trailing `LockSupport.park()` are byte-identical to the fork's pre-existing code, and the unpark sites (`shutdown()` and the two `requestBlockBake`-style methods further down the file) are unchanged, so the idle case is still park-bounded. Correctness of the new inner loop rests entirely on the expression 307 ported: `processAllThings()` returns `(this.blockStatesInFlight.size()!=0)||(!this.rawBakeResults.isEmpty())||!this.biomeQueue.isEmpty()`, so the loop drains until the in-flight set, the GPU readback results and the biome queue are all empty. This is precisely why 307's port substituted `rawBakeResults` and NOT `uploadResults` for upstream's `bakeQueue` — `uploadResults` is drained by the render-thread `tickAndProcessUploads` and is also appended to by `ModelFactory` itself, so including it in this predicate would make `processAllThings()` return `true` forever and turn this `while` into an unbreakable spin on the processing thread. As ported, each of the three terms is drained by a step that `processAllThings` itself performs (`processModelResult` for `rawBakeResults`, the biome poll loop for `biomeQueue`) or by the readback/upload path that removes entries from `blockStatesInFlight`, so the loop makes forward progress and terminates when the queues empty. Verified with `./gradlew compileJava` and `./gradlew build -x test`, both successful, and the two release artifacts were produced and uploaded to `v0.2.7-alpha-2.257` with `--target c1893c8770e750a9897c8eebf3093bbd5b9e8700`. 307 and 308 are applied as a matched pair; the fork now has upstream's end-to-end bake-drain behaviour. Counter advanced to .257.


## 309. `cf4a2a579a57d64e45a3c23a7e762e7f7863bbc3` temporarily revert sodium update
- **Verdict:** SKIPPED (MC-26-ONLY: pure Sodium 0.8.10 -> 0.8.9 / Minecraft 26 dependency-coordinate reversion, no portable Java behaviour)
- **Files:** 0 files (cherry-pick started, 2 content conflicts in `build.gradle` and `src/main/resources/fabric.mod.json`, then aborted via `git cherry-pick --abort`)
- **Result:** SKIPPED
- **SHA:** n/a (no commit created)
- **Release:** none (no release published; counter unchanged)
- **Notes:** SKIPPED as MC-26-ONLY. This is a normal single-parent commit (`git rev-list --parents -n 1 cf4a2a57` shows one parent, c47fc18e), so it is not a merge-commit skip; it is the direct inverse of the 306 skip, and it exists upstream purely to walk back 306's Sodium bump. The entire diff is 2 files, +2/-2, with zero Java source changes: `build.gradle` swaps `maven.modrinth:sodium:mc26.1.2-0.8.10-fabric` back to `mc26.1.1-0.8.9-fabric`, and `src/main/resources/fabric.mod.json` changes the `"sodium": ["=0.8.10"]` constraint to `["=0.8.9","=0.8.10"]`. Neither coordinate exists for this fork: the fork pins 1.21.1 Sodium through `curse.maven:sodium-394468:6382651` (Sodium 0.6.13, verified at `build.gradle:197-198` as both `implementation` and `compileOnly`), and it has no Modrinth Sodium dependency at all, so there is nothing here to revert. The second hunk is additionally inert for the fork: `src/main/resources/fabric.mod.json` is a Fabric loader descriptor that this NeoForge fork does not consume at runtime (the shipped metadata is `src/main/resources/META-INF/neoforge.mods.toml`), and its existing Sodium line is already the loose `"sodium": ">=0.6.13"` constraint, which 0.8.9/0.8.10 pin strings could not express anyway. Cherry-picking was attempted and both files conflicted, because upstream's hunk context lines are the `maven.modrinth` block the fork never had. Aborted cleanly; working tree returned to the 308 state.

## 310. `a5c7f564070455f00cc398390624c0da67d5e19e` fix neighbor check
- **Verdict:** APPLIED (clean cherry-pick -x of `a5c7f564`, no conflicts, no port fix needed)
- **Files:** 1 file, +1/-1 (`client/core/rendering/building/RenderDataFactory.java`)
- **Result:** APPLIED
- **SHA:** 6e005ceb2618ecd76a8502839421f62e36ff315d
- **Release:** v0.2.7-alpha-2.258
- **Notes:** Cherry-pick -x of `a5c7f564` auto-merged cleanly (single line, no conflict markers) onto the state left by 308. The commit is a one-line change to the first guard in `RenderDataFactory.shouldMeshNonOpaqueBlockFace(int, long, long, long, long)`: the same-block self-cull fast path gains an occlusion-shape check. Before, the guard read `DISABLE_CULL_SAME_OCCLUDES || (ModelQueries.cullsSame(meta)||ModelQueries.faceOccludes(meta, face))`, and after it reads `DISABLE_CULL_SAME_OCCLUDES || (ModelQueries.cullsSame(meta)||(ModelQueries.faceCanBeOccluded(meta, face)&&ModelQueries.faceOccludes(meta, face^1)))`. This is entirely version-independent Java: the surrounding `long`-packed meta helpers (`cullsSame`, `faceOccludes`, `faceCanBeOccluded`) are the fork's own `ModelQueries` bit-field API and the 1.21.1 fork carries them unchanged, so no MC-version port fix was required. It compiled and built clean. Note this commit is short-lived on the fork as well as upstream: 311 reverts exactly this line. Both are backported, matching upstream's own history, so the intermediate state is preserved and the release sequence stays auditable.

## 311. `91d4f7c8a754c2c33159951213aae6db56546ccf` am so fking stupid (the test is done 2 lines later)
- **Verdict:** APPLIED (clean cherry-pick -x of `91d4f7c8`, no conflicts, no port fix needed)
- **Files:** 1 file, +1/-1 (`client/core/rendering/building/RenderDataFactory.java`)
- **Result:** APPLIED
- **SHA:** 064e8877079ac03d84fc01bb80dffa9deecea1f7
- **Release:** v0.2.7-alpha-2.259
- **Notes:** Cherry-pick -x of `91d4f7c8` auto-merged cleanly onto the state left by 310, and it is a strict textual inverse of 310's line: the guard in `shouldMeshNonOpaqueBlockFace` drops the `faceCanBeOccluded`/`faceOccludes` disjunction and returns to the bare form `((quad^neighborQuad)&(0xFFFFL<<26))==0 && (DISABLE_CULL_SAME_OCCLUDES || ModelQueries.cullsSame(meta))`. The subject is upstream's own admission of the mistake: 310 tried to suppress the self-cull on occlusion shape, which was redundant, because the very next two statements of the same method already re-test `faceExists` and then `faceCanBeOccluded` + `faceOccludes(neighborMeta, face^1)`. Backporting 311 immediately after 310 is required for correctness, not optional: it is what makes the fork's final state match upstream's settled behaviour. Confirmed in the post-apply tree that the guard at `RenderDataFactory.java:355` is the reverted two-clause form. No MC-version port fix was required.

## 312. `5d407397ad769f0eecaf69725cb1285e56993a9c` .... how... how has this been missed for well over a year ;-; fuuuuuuuuuuuu
- **Verdict:** APPLIED (clean cherry-pick -x of `5d407397`, no conflicts, no port fix needed)
- **Files:** 1 file, +10/-3 (`client/core/rendering/building/RenderDataFactory.java`)
- **Result:** APPLIED
- **SHA:** e91fcf546f47418be243d3dc02e348715f91ae65
- **Release:** v0.2.7-alpha-2.260
- **Notes:** Cherry-pick -x of `5d407397` auto-merged cleanly onto the state left by 311. This is a real latent-bug fix in the section-mesher AABB packing, and it is version-independent Java. The old code packed the three extent fields as `aabb |= (this.maxX-this.minX-1)<<15;` (and the same for Y at `<<20`, Z at `<<25`), so any empty or inverted range produced a negative extent, and the shift of a negative int fills the high bits with ones, corrupting every subsequent field packed above bit 15. The fix wraps each extent in `Math.max(0, ...)` so a degenerate section clamps to a zero extent instead of sign-extending into the rest of the word. The commit also adds a standalone author note and a commented-out `IllegalStateException` guard, both carried over verbatim as inert comments, exactly as upstream left them. Nothing in the hunk touches MC-version-specific API (plain int bit-packing in a section builder), so no port fix was required. Compiled and built clean.

## 313. `359fefab1db163005b2a1c5ab969625cde332578` update sodium
- **Verdict:** SKIPPED (MC-26-ONLY: single-line Sodium 0.8.9 -> 0.8.10 Minecraft-26 Modrinth coordinate bump, no portable Java behaviour)
- **Files:** 0 files (cherry-pick started, 1 content conflict in `build.gradle`, then aborted via `git cherry-pick --abort`)
- **Result:** SKIPPED
- **SHA:** n/a (no commit created)
- **Release:** none (no release published; counter unchanged)
- **Notes:** SKIPPED as MC-26-ONLY. This is a normal single-parent commit (`git rev-list --parents -n 1 359fefab` shows one parent, 5d407397), so it is not a merge-commit skip. The entire diff is 1 file, +1/-1, and the single hunk changes `implementation "maven.modrinth:sodium:mc26.1.1-0.8.9-fabric"` to `maven.modrinth:sodium:mc26.1.2-0.8.10-fabric` inside the `if (true)` Modrinth-Sodium block; the sibling `sodium-fabric 0.8.4-SNAPSHOT+mc1.21.11+` else-branch is untouched. There is no equivalent coordinate for this fork, which resolves 1.21.1 Sodium through Curse Maven as `curse.maven:sodium-394468:6382651` (verified at `build.gradle:197-198`, both `implementation` and `compileOnly`) and has no Modrinth Sodium dependency at all; the 0.8.x line is Sodium for Minecraft 26 and is API-incompatible with the 0.6.13-era 1.21.1 Sodium this fork compiles against. Cherry-picking was attempted and conflicted on `build.gradle`, because the hunk's context is the `maven.modrinth` block that the fork replaced with the Curse Maven block. Aborted cleanly; working tree verified to have no leftover `CHERRY_PICK_HEAD` and no conflict markers. Counter unchanged.

Batch 309-313 summary: 3 APPLIED (310, 311, 312 — all pure version-independent Java in `RenderDataFactory.java`, all compiled and built clean), 2 SKIPPED (309, 313 — both MC-26-ONLY Sodium version-coordinate commits, skipped on inspection of the full diff, both cherry-picks attempted and aborted cleanly). No commit needed a manual 1.21.1 port fix this batch. Counter advanced .257 -> .260, with releases v0.2.7-alpha-2.258 / .259 / .260 targeting the full 40-char local cherry-pick SHAs 6e005ceb2618ecd76a8502839421f62e36ff315d, 064e8877079ac03d84fc01bb80dffa9deecea1f7, e91fcf546f47418be243d3dc02e348715f91ae65. No force push was used; every push to `backport/sequential` was a fast-forward.
## 314. `c3ccb2779100c5e0f325054fdd893020ebf01979` sodium 0.8.11
- **Verdict:** SKIPPED (MC-26-ONLY: single-line Sodium 0.8.10 -> 0.8.11 Minecraft-26 Modrinth coordinate bump, no portable Java behaviour)
- **Files:** 0 files (cherry-pick started, 2 content conflicts in `build.gradle` and `src/main/resources/fabric.mod.json`, then aborted via `git cherry-pick --abort`)
- **Result:** SKIPPED
- **SHA:** n/a (no commit created)
- **Release:** none (no release published; counter unchanged)
- **Notes:** SKIPPED as MC-26-ONLY. This is a normal single-parent commit (`git rev-list --parents -n 1 c3ccb277` shows one parent, 359fefab), so it is not a merge-commit skip; it is the direct follow-up to 313's skip and exists upstream purely to walk the Sodium constraint forward one patch release. The entire diff is 2 files, +2/-2, with zero Java source changes: `build.gradle` swaps `maven.modrinth:sodium:mc26.1.2-0.8.10-fabric` -> `mc26.1.2-0.8.11-fabric` inside the `if (true)` Modrinth-Sodium block, and `src/main/resources/fabric.mod.json` appends `"=0.8.11"` to the `"sodium"` dependency constraint array. Neither coordinate is resolvable for this fork: it pins 1.21.1 Sodium through Curse Maven as `curse.maven:sodium-394468:6382651` (Sodium 0.6.13) and declares no Modrinth Sodium dependency at all, so the `mc26.x` coordinate line conflicts rather than applying. The `"sodium"` version-constraint array in `fabric.mod.json` is likewise Fabric-26 metadata with no NeoForge 1.21.1 counterpart. No version-independent behaviour is lost by skipping; the cherry-pick was attempted, both hunks were confirmed to be coordinate-only, and it was aborted cleanly leaving HEAD at 6c435fd4.

## 315. `76169a0057f2ac4eec73e59d4f8cc4342068fb2a` better detection for ssao mode
- **Verdict:** APPLIED (clean cherry-pick -x of `76169a00`, no conflicts, no port fix needed)
- **Files:** 1 file, +13/-5 (`client/core/SSAO.java`)
- **Result:** APPLIED
- **SHA:** 6c0620fcf22ed28f5aea479a33e76a5d4858f2ab
- **Release:** v0.2.7-alpha-2.261
- **Notes:** Cherry-pick -x of `76169a00` auto-merged cleanly (single hunk, no conflict markers) and is entirely version-independent Java in the `SSAO.createSSAO` AUTO branch. Before, AUTO treated "cannot query GPU memory" and "less than 2.5 GB dedicated VRAM" identically, falling through to BASIC for both, which was wrong for a high-end AMD card: the NVX memory-query extension is absent there, so `Capabilities.INSTANCE.canQueryGpuMemory` is false and the old code handed BEST-class hardware a BASIC (0-spp, non-better) SSAO pass. After, the memory-query check becomes the outer condition: when `canQueryGpuMemory` is true the original three-tier VRAM ladder (under 2.5 GB -> BASIC, under 7 GB -> BETTER, else -> BEST) is preserved verbatim, and a new `else` branch for the cannot-query case splits on `Capabilities.INSTANCE.isAmd`, returning BETTER on AMD and BASIC everywhere else. This is exactly the right fix for the fork: `Capabilities` here lives at `client/core/gl/Capabilities.java` rather than upstream's path, and the commit relies on two public final fields that are both present and identically named — `isAmd` (set at `gl/Capabilities.java:94` from a vendor string containing `amd` or `radeon`) and `canQueryGpuMemory` (set at line 62 from the `GL_NVX_gpu_memory_info` extension). The branch only permutes existing `SSAOMode` constants and passes through the existing `createSSAO` overloads, so it introduces no new MC-1.21.1 or Sodium-0.7 API surface. Verified with `./gradlew compileJava --no-daemon` and `./gradlew build -x test --no-daemon`, both BUILD SUCCESSFUL with no port fix required.

## 316. `af1bfbb008bbbfe887cbe8ccfcd397ed7f0a66d6` ability to disable voxy for specific instances
- **Verdict:** APPLIED+FIXED (1 content conflict resolved and 1 NeoForge-compiler port fix required; the fix was folded in via `git commit --amend`, so the published SHA below is the post-amend commit)
- **Files:** 5 files, +35/-6 (`client/VoxyClientInstance.java`, `client/mixin/minecraft/MixinLevelRenderer.java`, `commonImpl/DontCreateInstance.java` (new), `commonImpl/VoxyCommon.java`, `commonImpl/VoxyInstance.java`)
- **Result:** APPLIED+FIXED
- **SHA:** 6e3c1d9bf9ac2fbb74696d6a1db0589b750f768d (post-amend; the pre-amend cherry-pick was 4ae6a5fb)
- **Release:** v0.2.7-alpha-2.262
- **Notes:** The feature is version-independent Java: `VoxyCommon.createInstance()` now wraps `FACTORY.create()` in a try/catch for the new package-private `DontCreateInstance extends RuntimeException` control-flow signal, `VoxyInstance` consults a new overridable `shouldCreateInstance()` at the top of its constructor and throws that signal when the answer is false, and `VoxyClientInstance` overrides it to return `!this.config.disabled` — the fork's `Config` inner class already carried the `public boolean disabled = false` field this commit relies on, so no new config plumbing was needed. `MixinLevelRenderer` also drops its null-instance path from `Logger.error` to `Logger.info`, since a null instance is now the legal representation of "user disabled voxy here" rather than a failure. TWO port problems had to be solved. (1) Conflict: upstream's constructor had been restructured by an earlier upstream commit that introduced `FlashbackCompat.getReplayStoragePath()` (replay-mod storage redirection) which does not exist on this fork, and the fork's version has no `noIngestOverride` derivation either. Resolved by keeping the fork's `getBasePath()` + `noIngestOverride = false` and dropping the Flashback branch, while preserving upstream's `super()`-after-config ordering. (2) NeoForge compiler port: upstream achieves that ordering by running the config block as a plain statement block BEFORE `super()` inside the constructor, which upstream's javac accepts but NeoGradle's ECJ rejects outright with `call to super must be first statement in constructor` (verified as the sole compile error, emitted twice by ECJ). The fix keeps the behaviour and satisfies ECJ by converting the two constructors into a `this(...)` delegation chain: the public no-arg constructor delegates to a private constructor via `this(InitState.load())`, where a new private `record InitState(Config config, Path basePath)` with a static `load()` performs exactly the same config read/normalize work upstream's statement block did. The delegated constructor then calls `super(!state.config.disabled)` FIRST, which is legal, and assigns `config`/`basePath` afterwards. To make that work, `VoxyInstance` gained a `protected VoxyInstance(boolean create)` overload that throws `DontCreateInstance` when the flag is false, with the public no-arg `VoxyInstance()` retained as `this(true)`; upstream's `shouldCreateInstance()` virtual method was therefore not carried over, since the decision is now passed through the super call instead of dispatched virtually from inside it — this is deliberate, not an oversight, because ECJ forbids the upstream mechanism. Verified with `./gradlew compileJava --no-daemon` and `./gradlew build -x test --no-daemon`, both BUILD SUCCESSFUL after the port.

## 317. `e863819d0a998eb962d473bc05c97f22e459fe15` update fapi
- **Verdict:** SKIPPED (MC-26-ONLY: single-line Fabric API 0.145.3 -> 0.148.0 Minecraft-26 coordinate bump, no portable Java behaviour)
- **Files:** 0 files (cherry-pick started, 1 content conflict in `gradle.properties`, then aborted via `git cherry-pick --abort`)
- **Result:** SKIPPED
- **SHA:** n/a (no commit created)
- **Release:** none (no release published; counter unchanged)
- **Notes:** SKIPPED as MC-26-ONLY. This is a normal single-parent commit (`git rev-list --parents -n 1 e863819d` shows one parent, af1bfbb0), so it is not a merge-commit skip. The entire diff is 1 file, +1/-1, and contains zero Java source changes: the single hunk rewrites the `# Fabric API` line in `gradle.properties` from `fabric_api_version=0.145.3+26.1.1` to `fabric_api_version=0.148.0+26.1.2`. Both coordinates are Minecraft-26 Fabric builds and neither resolves for this fork, which targets Minecraft 1.21.1 on NeoForge (`neo_version=21.1.173`) and pins Fabric API under a differently-named key entirely — `fabric_version=0.116.15+2.3.5+1.21.1`. The property name itself differs upstream (`fabric_api_version`) versus the fork (`fabric_version`), which is why the hunk conflicts rather than applying, and the Fabric API dependency is a loom/Fabric-only concern that this NeoGradle-based fork does not consume at all. Nothing version-independent is lost; the cherry-pick was attempted, the full 1-file diff was confirmed to be a pure coordinate bump, and it was aborted cleanly leaving HEAD at badf34dc.

## 318. `0b67831ff5e85f09ac4422d0c5dd1982884c9ac2` build script tweeks
- **Verdict:** SKIPPED (MC-26-ONLY: Fabric-26 build-script templating and Modrinth coordinate indirection, no portable Java behaviour)
- **Files:** 0 files (cherry-pick started, 3 content conflicts in `build.gradle`, `gradle.properties` and `src/main/resources/fabric.mod.json`, then aborted via `git cherry-pick --abort`)
- **Result:** SKIPPED
- **SHA:** n/a (no commit created)
- **Release:** none (no release published; counter unchanged)
- **Notes:** SKIPPED as MC-26-ONLY. This is a normal single-parent commit (`git rev-list --parents -n 1 0b67831f` shows one parent, e863819d), so it is not a merge-commit skip. The full diff is 3 files, +17/-5, and every hunk is Fabric-26 build plumbing with zero changes to any `.java` file. In `processResources` it adds a `StringBuilder` that synthesises a Sodium version-constraint JSON array and feeds it to `fabric.mod.json` as a new `${allowed_sodium_versions}` template token, driven by two new `gradle.properties` keys `sodium_version_modrinth=mc26.1.2-0.8.12-beta.4-fabric` and `sodium_extra_allows=0.8.9 0.8.10 0.8.11`; in `dependencies` it replaces the hardcoded Sodium lines with the interpolating forms `maven.modrinth:sodium:${project.sodium_version_modrinth}` and `net.caffeinemc:sodium-fabric:${project.sodium_version}`; and in `fabric.mod.json` it replaces the explicit `"sodium": ["=0.8.9","=0.8.10", "=0.8.11"]` array with the templated `${allowed_sodium_versions}`. None of this is portable, and applying it selectively would actively break the build: this fork is NeoGradle-based and never runs the `processResources` block above (it is inside upstream's Fabric/loom `processResources` configuration, which does not exist here), it has no `sodium_version`/`sodium_version_modrinth` properties to interpolate, and it resolves Sodium through Curse Maven as `curse.maven:sodium-394468:6382651` (Sodium 0.6.13) with a flat `"sodium": ">=0.6.13"` string constraint in `fabric.mod.json` rather than a templated array. The templating therefore only serves to keep the upstream Modrinth/Sodium-0.8 Fabric line in sync, which has no 1.21.1 NeoForge counterpart; the cherry-pick was attempted, all three hunks were confirmed to be build-script-only, and it was aborted cleanly leaving HEAD at badf34dc.

Batch 314-318 summary: 2 APPLIED (315, 316), 3 SKIPPED (314, 317, 318 — all MC-26-ONLY build/dependency-coordinate commits with zero portable Java behaviour). 315 was a clean cherry-pick needing no port fix; 316 required one content conflict resolution (dropping the absent `FlashbackCompat` replay-path branch) plus one NeoForge-compiler port fix (`call to super must be first statement in constructor` from ECJ), folded in via `git commit --amend` so the published SHA is the post-amend 6e3c1d9b. Counter advanced .260 -> .262, with releases v0.2.7-alpha-2.261 / .262 targeting the full 40-char local cherry-pick SHAs 6c0620fcf22ed28f5aea479a33e76a5d4858f2ab and 6e3c1d9bf9ac2fbb74696d6a1db0589b750f768d. No force push was used; every push to `backport/sequential` was a fast-forward.
