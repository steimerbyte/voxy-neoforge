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
