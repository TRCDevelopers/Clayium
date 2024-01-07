package com.github.trcdeveloppers.clayium.common.config;

import com.github.trcdeveloppers.clayium.common.Clayium;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.Comment;

@LangKey("clayium.config.tier")
@Config(modid = Clayium.MOD_ID, name = Clayium.MOD_ID + "/tier_balance")
public class ConfigTierBalance {
    @RangeInt(min = 1)
    @Comment({
        "The buffer perform a transfer every X ticks. 1 = no delay = every tick.",
        "For example, if the X is 2, the buffer will transfer every 2 ticks i.e. 10 times per second.",
        "These values correspond to the tiers 1 to 13 respectively, from top to bottom.",
    })
    public static int[] bufferTransferIntervals = { 8, 8, 8, 8, 4, 2, 1, 1, 1, 1, 1, 1, 1 };

    @RangeInt(min = 0)
    @Comment({
        "The buffer will transfer X items per transfer.",
        "These values correspond to the tiers 1 to 13 respectively, from top to bottom.",
    })
    public static int[] bufferTransferAmount = {  1, 1, 1, 1, 4, 16, 64, 128, 192, 256, 512, 1024, 6400 };
}
