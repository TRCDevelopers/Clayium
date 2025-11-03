package io.github.trcdevelopers.clayium.common.config;

import net.minecraftforge.common.config.Config;

import static io.github.trcdevelopers.clayium.api.CValues.MOD_ID;

@Config(modid = MOD_ID, name = MOD_ID + "/" + "mod_integration")
@Config.LangKey("clayium.config.mod_integration")
public class ConfigModIntegration {
    @Config.Name("!Enable Integration Recipes")
    @Config.Comment(
        "If set to false, ALL integration recipes will be DISABLED regardless of individual mod settings. "
        + "If set to true, integration recipes will be loaded based on their individual per-mod configurations."
    )
    @Config.LangKey("clayium.config.mod_integration.all_toggle")
    @Config.RequiresMcRestart
    public static boolean enableIntegrationRecipes = true;

    @Config.Name("AE2")
    @Config.RequiresMcRestart
    public static boolean ae2 = true;

    @Config.Name("EnderIO")
    @Config.RequiresMcRestart
    public static boolean enderio = true;

    @Config.Name("ExtremeReactors")
    @Config.RequiresMcRestart
    public static boolean extremeReactors = true;

    @Config.Name("Galacticraft")
    @Config.RequiresMcRestart
    public static boolean galacticraft = true;

    @Config.Name("Gregtech")
    @Config.RequiresMcRestart
    public static boolean gregtech = true;

    @Config.Name("Mekanism")
    @Config.RequiresMcRestart
    public static boolean mekanism = true;

    @Config.Name("Metallurgy-4-Reforged")
    @Config.RequiresMcRestart
    public static boolean metallurgy = true;

    @Config.Name("ProjectRedExpansion")
    @Config.RequiresMcRestart
    public static boolean projectRedExpansion = true;

    @Config.Name("Sakura")
    @Config.RequiresMcRestart
    public static boolean sakura = true;

    @Config.Name("ThermalFoundation")
    @Config.RequiresMcRestart
    public static boolean thermalFoundation = true;

    @Config.Name("TinkersConstruct")
    @Config.RequiresMcRestart
    public static boolean tconstruct = true;

    @Config.Name("TofuCraftReload")
    @Config.RequiresMcRestart
    public static boolean tofuCraft = true;
}
