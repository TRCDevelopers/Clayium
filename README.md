# Clayium Unofficial
An **unofficial** 1.12.2 port of Clayium.
The original version was made by deb_rk,
and published on [CurseForge](https://www.curseforge.com/minecraft/mc-mods/clayium) under [CC-BY 4.0](https://creativecommons.org/licenses/by/4.0/).

This mod is currently in alpha stage and may contain critical bugs including game crashes.
Check [here](https://github.com/TRCDevelopers/Clayium/releases) for the latest release.

## Required Mods
- [ModularUI](https://github.com/CleanroomMC/ModularUI) (currently incompatible with `v2.5.0-rc1`, please use `v2.4.3` instead)
- [CodeChickenLib](https://github.com/TheCBProject/CodeChickenLib)
- [Forgelin Continuous](https://github.com/ChAoSUnItY/Forgelin-Continuous)

## Ported Features
Features have been ported up to tier 13 Clay Fabricator Mk3, 
including PAN, except for those listed below.

## WIP Features
- ~~Alloy Smelter~~ (Done)
- Auto Crafter
- Auto Trader
- CE-RF Converter
- Clay Crafting Board
- Clay Gadgets
- Clay Guns
- ClaySteel tools
- Decoration Blocks
- Distributor
- Energetic Clay Decomposer
- Filters other than black/white list
- Fluid related features
- Metal Chests
- Void Container
- World Interactive Machines
  - Activators
  - Area Replacer
  - ~~Block Breaker~~ (Done)
  - ~~Ranged Miner~~ (Done)
  - Advanced Area Miner
  - Chunk Loader
  - Item Collectors
- Configs (ProgressionRate, HardcoreXXX, etc.)
- Other Mod Integrations

## License
### Code
The code is licensed under the [LGPL-3.0](https://www.gnu.org/licenses/lgpl-3.0.html.en#license-text).

### Assets
Assets are created by deb_rk, and are licensed under [CC-BY 4.0](https://creativecommons.org/licenses/by/4.0/).

## Credits
Many parts of the code such as [`MetaTileEntity`](src/main/kotlin/com/github/trc/clayium/api/metatileentity/MetaTileEntity.kt),
[`Unification API`](src/main/kotlin/com/github/trc/clayium/api/unification),
are copied and modified from [GTCEu](https://github.com/GregTechCEu/GregTech), 
which is licensed under the [LGPL-3.0](https://www.gnu.org/licenses/lgpl-3.0.html.en) license.

Buildscripts we used can be found [here](https://github.com/GregTechCEu/Buildscripts).
It is [MIT licensed](https://github.com/GregTechCEu/Buildscripts/blob/master/LICENSE).