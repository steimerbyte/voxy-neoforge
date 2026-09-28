# Backport Log

Per-commit log of cherry-picks from upstream Voxy `dev` to 1.21.1 NeoForge fork.

Format: `N. <short-sha> <msg> — <STATUS>` where STATUS is APPLIED | APPLIED+FIXED | SKIPPED | PENDING.

## Progress


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
