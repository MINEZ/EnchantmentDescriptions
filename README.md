<!-- name-start -->
# EnchantmentDescriptions

[![MINEZ version](docs/badges/version.svg)](https://github.com/MINEZ/EnchantmentDescriptions/releases)
<!-- name-end -->

<!-- description-start -->
This is the MINEZ fork of [Enchantment Descriptions](https://github.com/Darkhax-Minecraft/Enchantment-Descriptions) for Minecraft 26.3. It fixes crashes when an activation hint is added to an immutable tooltip list and preserves hidden tooltips. The original mod ID, `enchdesc`, is retained.

Download this fork from [GitHub Releases](https://github.com/MINEZ/EnchantmentDescriptions/releases) and report fork-specific issues [here](https://github.com/MINEZ/EnchantmentDescriptions/issues). See [简体中文说明](README_zh-CN.md) for build and installation instructions.
<!-- description-end -->

<!-- maven-start -->
## Build this fork

Use JDK 25 and the `26.3` branch. Open PowerShell in the repository root and run:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\build-fabric.ps1
```

The build script reads `fork-version.txt` and supplies the build number expected by the upstream conventions plugin. The Fabric artifact is `fabric/build/libs/EnchantmentDescriptions-fabric-MC26.3-26.3.0.2.1.jar`.

Replace the existing EnchantmentDescriptions jar with this artifact and keep the required Prickle dependency installed. The MINEZ fork is distributed through GitHub Releases; the upstream CurseForge, Modrinth and Maven pages do not distribute this fork.
<!-- maven-end -->

<!-- sponsor-start -->
## Sponsors
[![](https://assets.blamejared.com/nodecraft/darkhax.jpg)](https://nodecraft.com/r/darkhax)    
EnchantmentDescriptions is proudly sponsored by Nodecraft! Play your favorite games with your friends using their high
performance game servers! Use code **[DARKHAX](https://nodecraft.com/r/darkhax)** for 30% off your first
month of service!

<!-- sponsor-end -->
