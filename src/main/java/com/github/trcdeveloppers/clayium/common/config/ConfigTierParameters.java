package com.github.trcdeveloppers.clayium.common.config;

import com.github.trcdeveloppers.clayium.Clayium;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RangeInt;

@LangKey("clayium.config.tier")
@Config(modid = Clayium.MOD_ID, name = Clayium.MOD_ID + "/tier_parameters")
public class ConfigTierParameters {
    @RangeInt(min = 0)
    public static int[] bufferTransferIntervals = { 8, 8, 8, 8, 8, 4, 2, 1, 1, 1, 1, 1, 1, 1 };

    @RangeInt(min = 0, max = 6400)
    public static int[] bufferTransferAmount = { 1, 1, 1, 1, 1, 1, 4, 16, 64, 128, 192, 256, 512, 1024, 6400 };
}
