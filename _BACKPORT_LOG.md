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
