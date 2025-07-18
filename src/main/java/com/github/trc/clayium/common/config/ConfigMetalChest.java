package com.github.trc.clayium.common.config;

import net.minecraftforge.common.config.Config;

import static com.github.trc.clayium.api.CValues.MOD_ID;

@Config.LangKey("clayium.config.metal_chest")
@Config(modid = MOD_ID, name = MOD_ID + "/" + "metal_chest")
public class ConfigMetalChest {
    @Config.Comment({
            "Syntax:",
            "<material_id>;<inv_width>,<inv_height>,<inv_page>",
            "Example 'clayium:aluminum;9,6,1'"
    })
    public static String[] metalChestConfig = {
            "clayium:actinium;13,5,1",
            "clayium:aluminum;9,6,1",
            "clayium:americium;13,6,5",
            "clayium:antimony;9,6,1",
            "clayium:az91d_alloy;13,8,1",
            "clayium:barium;10,8,1",
            "clayium:beryllium;10,8,1",
            "clayium:bismuth;9,6,1",
            "clayium:brass;9,6,1",
            "clayium:caesium;13,3,1",
            "clayium:calcium;9,6,1",
            "clayium:cerium;13,2,4",
            "clayium:chrome;13,8,1",
            "clayium:clay_steel;9,8,1",
            "clayium:clayium;13,8,1",
            "clayium:cobalt;11,6,1",
            "clayium:curium;13,6,6",
            "clayium:electrum;13,8,1",
            "clayium:europium;13,8,8",
            "clayium:francium;13,4,1",
            "clayium:hafnium;9,8,1",
            "clayium:invar;9,8,1",
            "clayium:iridium;13,8,3",
            "clayium:lanthanum;13,2,2",
            "clayium:lead;9,6,1",
            "clayium:lithium;9,7,1",
            "clayium:magnesium;9,6,1",
            "clayium:manganese;9,6,1",
            "clayium:molybdenum;13,8,2",
            "clayium:neodymium;13,2,8",
            "clayium:neptunium;13,6,3",
            "clayium:nickel;9,6,1",
            "clayium:osmium;13,8,4",
            "clayium:palladium;11,8,1",
            "clayium:platinum;13,8,2",
            "clayium:plutonium;13,6,4",
            "clayium:potassium;9,6,1",
            "clayium:praseodymium;13,2,6",
            "clayium:promethium;13,4,8",
            "clayium:protactinium;13,6,1",
            "clayium:radium;13,4,1",
            "clayium:rhenium;13,8,5",
            "clayium:rubidium;13,3,1",
            "clayium:samarium;13,6,8",
            "clayium:silicon;9,5,1",
            "clayium:sodium;9,6,1",
            "clayium:strontium;10,8,1",
            "clayium:tantalum;10,8,1",
            "clayium:thorium;13,5,1",
            "clayium:titanium;13,8,1",
            "clayium:tungsten;13,8,1",
            "clayium:ultimate_alloy;13,8,3",
            "clayium:uranium;13,6,2",
            "clayium:vanadium;4,8,1",
            "clayium:zinc;9,6,1",
            "clayium:zirconium;9,7,1",
            "clayium:zk60a_alloy;13,8,1",
    };
}
