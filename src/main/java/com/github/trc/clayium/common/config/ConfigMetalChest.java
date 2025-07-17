package com.github.trc.clayium.common.config;

import net.minecraftforge.common.config.Config;

import static com.github.trc.clayium.api.CValues.MOD_ID;

@Config.LangKey("clayium.config.metal_chest")
@Config(modid = MOD_ID, name = MOD_ID + "/" + "metal_chest")
public class ConfigMetalChest {
    @Config.Comment({
            "Syntax:",
            "<material_id>;<inv_row>,<inv_column>,<inv_page>",
            "Example 'clayium:aluminum;9,6,1'"
    })
    public static String[] metalChestConfig = {
            "clayium:silicon;9,5,1",
            "clayium:aluminum;9,6,1",
    };
}
