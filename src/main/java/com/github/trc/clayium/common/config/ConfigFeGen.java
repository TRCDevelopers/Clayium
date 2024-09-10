package com.github.trc.clayium.common.config;

import net.minecraftforge.common.config.Config;

import static com.github.trc.clayium.api.CValues.MOD_ID;

@Config(modid = MOD_ID, name = MOD_ID + "/fe_generator")
@Config.LangKey("config." + MOD_ID + ".fe_gen.ce_per_tick")
public class ConfigFeGen {

    public static boolean enableFeGenerators = false;

    @Config.Comment({
            "CE consumption /t of the FE Generators in CE unit (1 = 1 CE, not 10u CE)",
    })
    public static double[] cePerTick = new double[] { 0.001, 0.01, 0.1, 1, 10, 100, 1_000, 10_000, 100_000, 1000_000 };

    @Config.Comment({
            "FE production /t of the FE Generators.",
    })
    public static int[] fePerTick = new int[] { 10, 30, 90, 270, 810, 2430, 7290, 21870, 65610, 196830 };

    public static int[] feStorageSize = new int[] { 10_000, 30_000, 90_000, 270_000, 810_000, 2430_000, 7290_000, 21870_000, 65610_000, 196830_000 };
}
