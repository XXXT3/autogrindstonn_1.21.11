---
name: Fabric Loom Maven bridge
description: Build constraints for the Fabric 1.21.11 mod project.
---

Fabric Loom remains the actual Minecraft build/remapping tool, while Maven is the user-facing entrypoint through an exec plugin. Loom 1.14.10 requires Gradle 9.2, and its generated local Minecraft/remapped repositories must remain visible; avoid settings-level repository preference rules that hide Loom's local repositories. Mixin configs must use a dedicated package containing only mixin classes, not the main mod package.

**Why:** Minecraft's obfuscation remapping and Fabric dependency transformation are provided by Loom, not by a native Maven compiler, and the 1.21.11 dependency graph includes generated local artifacts. Mixin treats every class under its configured package as a mixin and rejects ordinary entrypoint classes there.

**How to apply:** Preserve the Maven `package` bridge, the Gradle wrapper version aligned with Loom, and the standard plugin/build repository layout when updating the mod.