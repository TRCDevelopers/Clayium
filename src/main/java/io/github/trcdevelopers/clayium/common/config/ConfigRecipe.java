package io.github.trcdevelopers.clayium.common.config;

import net.minecraftforge.common.config.Config;

import static io.github.trcdevelopers.clayium.api.CValues.MOD_ID;

@Config(modid = MOD_ID, name = MOD_ID + "/" + "recipe")
@Config.LangKey("clayium.config.recipe")
public class ConfigRecipe {

    @Config.RequiresMcRestart
    @Config.Name("RecipeStaticity")
    @Config.Comment({
            "Some recipes change dynamically based on whether a specific item exists.",
            "(e.g. Matter Transformer: If Graphite Dust is present: carbon -> graphite -> charcoal If absent: carbon -> charcoal.)",
            "This behavior can cause issues if Graphite Dust is added later, breaking recipes already registered in systems like AE2.",
            "Setting this to true will make these recipes \"static\" (not dependent on item presence) to avoid this issue.",
            " See the wiki for details."
    })
    public static boolean recipeStaticity = false;

}
