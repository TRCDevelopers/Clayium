package io.github.trcdevelopers.clayium.api.util

import net.minecraftforge.fml.common.Loader

enum class Mods(
    val modId: String,
) {

    AE2(Names.AE2),
    Baubles(Names.BAUBLES),
    EnderIO(Names.ENDER_IO),
    ExtremeReactors(Names.EXTREME_REACTORS),
    GalacticraftCore(Names.GALACTICRAFT_CORE),
    GregTech(Names.GREGTECH),
    GroovyScript(Names.GROOVY_SCRIPT),
    JustEnoughItems(Names.JUST_ENOUGH_ITEMS),
    Mekanism(Names.MEKANISM),
    Metallurgy(Names.METALLURGY),
    ModularUI(Names.MODULAR_UI),
    ProjectRedExpansion(Names.PROJECT_RED_EXPANSION),
    Sakura(Names.SAKURA),
    TConstruct(Names.TCONSTRUCT),
    ThermalFoundation(Names.THERMAL_FOUNDATION),
    TheOneProbe(Names.THE_ONE_PROBE),
    TofuCraft(Names.TOFU_CRAFT),
    ;

    val isModLoaded by lazy { Loader.isModLoaded(this.modId) }

    object Names {
        const val AE2 = "appliedenergistics2"
        const val BAUBLES = "baubles"
        const val ENDER_IO = "enderio"
        const val EXTREME_REACTORS = "bigreactors"
        const val GALACTICRAFT_CORE = "galacticraftcore"
        const val GREGTECH = "gregtech"
        const val GROOVY_SCRIPT = "groovyscript"
        const val MEKANISM = "mekanism"
        const val METALLURGY = "metallurgy"
        const val JUST_ENOUGH_ITEMS = "jei"
        const val MODULAR_UI = "modularui"
        const val PROJECT_RED_EXPANSION = "projectred-expansion"
        const val SAKURA = "sakura"
        const val THE_ONE_PROBE = "theoneprobe"
        const val TCONSTRUCT = "tconstruct"
        const val THERMAL_FOUNDATION = "thermalfoundation"
        const val TOFU_CRAFT = "tofucraft"
    }
}