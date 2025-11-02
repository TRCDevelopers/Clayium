package io.github.trcdevelopers.clayium.common.config;

import net.minecraftforge.common.config.Config;

import static io.github.trcdevelopers.clayium.api.CValues.MOD_ID;

@Config.LangKey("clayium.config.mod_integration")
@Config(modid = MOD_ID, name = MOD_ID + "/" + "mod_integration")
public class ConfigModIntegration {
    @Config.Name("AE2")
    public static boolean ae2 = true;

    @Config.Name("EnderIO")
    public static boolean enderio = true;

    @Config.Name("ExtremeReactors")
    public static boolean extremeReactors = true;

    @Config.Name("Gregtech")
    public static boolean gregtech = true;

    @Config.Name("Mekanism")
    public static boolean mekanism = true;

    @Config.Name("Sakura")
    public static boolean sakura = true;

    @Config.Name("TinkersConstruct")
    public static boolean tconstruct = true;

    @Config.Name("TofuCraftReload")
    public static boolean tofuCraft = true;
}
