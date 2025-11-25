package io.github.trcdevelopers.clayium.common.config;

import net.minecraftforge.common.config.Config;

import static io.github.trcdevelopers.clayium.api.CValues.MOD_ID;

@Config(modid = MOD_ID, name = MOD_ID + "/" + "recipe")
@Config.LangKey("clayium.config.recipe")
public class ConfigRecipe {

    public static SubCategoryIntegrationRecipes integration = new SubCategoryIntegrationRecipes();

    @SuppressWarnings("ClassWithTooManyFields")
    public static class SubCategoryIntegrationRecipes {

        private SubCategoryIntegrationRecipes() {}

        @Config.Name("!Enable Integration Recipes")
        @Config.Comment(
                "If set to false, ALL integration recipes will be DISABLED regardless of individual mod settings. "
                        + "If set to true, integration recipes will be loaded based on their individual per-mod configurations."
        )
        @Config.LangKey("clayium.config.mod_integration.all_toggle")
        @Config.RequiresMcRestart
        public boolean enableIntegrationRecipes = true;

        @Config.Name("AE2")
        @Config.RequiresMcRestart
        public boolean ae2 = true;

        @Config.Name("EnderIO")
        @Config.RequiresMcRestart
        public boolean enderio = true;

        @Config.Name("ExtremeReactors")
        @Config.RequiresMcRestart
        public boolean extremeReactors = true;

        @Config.Name("Galacticraft")
        @Config.RequiresMcRestart
        public boolean galacticraft = true;

        @Config.Name("Gregtech")
        @Config.RequiresMcRestart
        public boolean gregtech = true;

        @Config.Name("Mekanism")
        @Config.RequiresMcRestart
        public boolean mekanism = true;

        @Config.Name("Metallurgy-4-Reforged")
        @Config.RequiresMcRestart
        public boolean metallurgy = true;

        @Config.Name("ProjectRedExpansion")
        @Config.RequiresMcRestart
        public boolean projectRedExpansion = true;

        @Config.Name("Sakura")
        @Config.RequiresMcRestart
        public boolean sakura = true;

        @Config.Name("ThermalFoundation")
        @Config.RequiresMcRestart
        public boolean thermalFoundation = true;

        @Config.Name("TinkersConstruct")
        @Config.RequiresMcRestart
        public boolean tconstruct = true;

        @Config.Name("TofuCraftReload")
        @Config.RequiresMcRestart
        public boolean tofuCraft = true;
    }
}
