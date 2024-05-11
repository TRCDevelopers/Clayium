package com.github.trcdevelopers.clayium.common.config;

import com.github.trcdevelopers.clayium.common.Clayium;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RangeInt;

@LangKey("clayium.config.tier")
@Config(modid = Clayium.MOD_ID, name = Clayium.MOD_ID + "/tier_balance")
public class ConfigTierBalance {
    private ConfigTierBalance() {}
    @RangeInt(min = 1)
    @Comment({
            "Auto IO interval for machines other than buffers.",
            "These values correspond to the tiers 0 to 13 respectively, from top to bottom."
    })
    public static int[] machineInterval = new int[] {20, 20, 20, 20, 20, 2, 1, 1, 1, 1, 1, 1, 1, 1};

    @RangeInt(min = 0)
    @Comment({
            "The machine will transfer X items per transfer.",
            "These values correspond to the tiers 0 to 13 respectively, from top to bottom."
    })
    public static int[] machineAmount = new int[] {8, 8, 8, 8, 8, 16, 64, 64, 64, 64, 64, 64, 64, 64};

    @RangeInt(min = 1)
    @Comment({
            "Auto IO interval for buffers.",
            "These values correspond to the tiers 0 to 13 respectively, from top to bottom."
    })
    public static int[] bufferInterval = new int[] {8, 8, 8, 8, 8, 4, 2, 1, 1, 1, 1, 1, 1, 1};

    @RangeInt(min = 0)
    @Comment({
            "The buffer will transfer X items per transfer.",
            "These values correspond to the tiers 0 to 13 respectively, from top to bottom."
    })
    public static int[] bufferAmount = new int[] {1, 1, 1, 1, 1, 4, 16, 64, 128, 192, 256, 512, 1024, 6400};

    public static SubCategoryMachines crafting = new SubCategoryMachines();

    public static class SubCategoryMachines {
        private SubCategoryMachines() {}
        @Comment({
                "craft time multiplier for clay smelters.",
                "These values correspond to the tiers 4 to 9 respectively, from top to bottom."
        })
        public double[] smelterCraftTimeMultiplier = new double[] { 2.0, 0.5, 0.125, 0.03, 0.01, 0.0025 };

        @Comment({
                "energy consumption multiplier for clay smelters.",
                "These values correspond to the tiers 4 to 9 respectively, from top to bottom."
        })
        public double[] smelterConsumingEnergyMultiplier = new double[] { 1.0, 14.0, 200.0, 2800.0, 40000.0, 560000.0 };
    }
}
