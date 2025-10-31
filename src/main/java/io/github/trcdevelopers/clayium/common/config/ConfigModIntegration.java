package io.github.trcdevelopers.clayium.common.config;

import net.minecraftforge.common.config.Config;

import static io.github.trcdevelopers.clayium.api.CValues.MOD_ID;

@Config.LangKey("clayium.config.mod_integration")
@Config(modid = MOD_ID, name = MOD_ID + "/" + "mod_integration")
public class ConfigModIntegration {
    @Config.Name("EnderIO")
    public static boolean enderio = true;

    @Config.Name("Gregtech")
    public static boolean gregtech = true;

    @Config.Name("Extreme Reactors")
    public static boolean extremeReactors = true;

    @Config.Name("TofuCraft Reload")
    public static boolean tofuCraft = true;

    @Config.Name("Sakura")
    public static boolean sakura = true;
}
