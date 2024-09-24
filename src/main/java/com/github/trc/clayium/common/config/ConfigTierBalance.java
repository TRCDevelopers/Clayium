package com.github.trc.clayium.common.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RangeInt;

import static com.github.trc.clayium.api.CValues.MOD_ID;

@LangKey("clayium.config.tier")
@Config(modid = MOD_ID, name = MOD_ID + "/tier_balance")
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

        @Comment({
                "used in Grinder, Condenser, Centrifuge",
                "Tier: default, 5, 6, 10"
        })
        public double[] craftTimeMultiplier = new double[] { 1.0, 0.25, 0.0625, 0.01 };

        @Comment({
                "used in Grinder, Condenser, Centrifuge",
                "Tier: default, 5, 6, 10"
        })
        public double[] consumingEnergyMultiplier = new double[] { 1.0, 5.0, 25.0, 250.0 };

        public double getCraftTimeMultiplier(int tier) {
            return getMachineTierValues(craftTimeMultiplier, tier);
        }

        public double getConsumingEnergyMultiplier(int tier) {
            return getMachineTierValues(consumingEnergyMultiplier, tier);
        }

        private double getMachineTierValues(double[] arr, int tier) {
            switch (tier) {
                case 5:
                    return arr[1];
                case 6:
                    return arr[2];
                case 10:
                    return arr[3];
                default:
                    return arr[0];
            }
        }
    }
}
