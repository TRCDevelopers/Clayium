package com.github.trc.clayium.common.config;

import net.minecraftforge.common.config.Config;

import static com.github.trc.clayium.api.CValues.MOD_ID;

@Config.LangKey("clayium.config.core")
@Config(modid = MOD_ID, name = MOD_ID + "/" + MOD_ID)
public class ConfigCore {

    @Config.Name("Rendering Options")
    public static SubCategoryRendering rendering = new SubCategoryRendering();

    @Config.Name("World Generation Options")
    public static SubCategoryWorldGen worldGen = new SubCategoryWorldGen();

    @Config.Name("Miscellaneous Options")
    public static SubCategoryMisc misc = new SubCategoryMisc();

    @Config.Name("CE-FE Converter Options")
    public static FeGen feGen = new FeGen();

    public static class SubCategoryRendering {
        private SubCategoryRendering() {}

        @Config.Comment({
                "Visual quality of clay laser",
                "2 is recommended for for low-end computers.",
                "Default: 8"
        })
        @Config.RangeInt(min = 1, max = 32)
        public int laserQuality = 8;
    }

    public static class SubCategoryWorldGen {
        private SubCategoryWorldGen() {}

        @Config.Comment("Default: 88")
        public int clayOreVeinMaxY = 88;
        @Config.Comment("Default: 24")
        public int clayOreVeinMinY = 24;
        @Config.Comment("Default: 8")
        public int clayOreVeinNumber = 8;
        @Config.Comment("Default: 24")
        public int clayOreVeinSize = 24;

        @Config.Comment("Default: 10")
        public int denseClayOreVeinSize = 10;
        @Config.Comment("Default: 2")
        public int largeDenseClayOreVeinNumber = 2;
        @Config.Comment("Default: 6")
        public int largeDenseClayOreVeinSize = 6;
        @Config.Comment("Default: 10")
        public int largeDenseClayOreVeinMinY = 10;
        @Config.Comment("Default: 16")
        public int largeDenseClayOreVeinMaxY = 16;

        @Config.Comment("Default: true")
        public boolean generateDenseClayOreVein = true;
    }

    public static class SubCategoryMisc {
        private SubCategoryMisc() {}

        @Config.Comment({
                "Inverts the redstone condition for the clay laser.",
                "false: laser irradiates when NOT powered",
                "true : laser irradiates when powered",
                "Default: false"
        })
        public boolean invertClayLaserRsCondition = false;

        @Config.Comment({
                "Lower is better.",
                "The lower the value, more chance to generate energy when ticked.",
                "Default: 40"
        })
        @Config.RangeInt(min = 1, max = 100)
        public int waterwheelEfficiency = 40;

        @Config.RangeInt(min = 1, max = 30)
        @Config.Comment({
               "The maximum tier that the waterwheel can provide energy to.",
               "It cannot provide energy to machines of a higher tier.",
                "Default: 3"
        })
        public int waterwheelMaxTier = 3;

        @Config.Comment({
                "Max search distance for PAN",
                "Default: 500"
        })
        @Config.RangeInt(min = 1, max = 1000)
        public int panMaxSearchDistance = 500;

        @Config.Comment("Default: 64")
        @Config.RangeInt(min = 1, max = 512)
        public int clayMarkerMaxRange = 64;

        @Config.Comment("Default: 10")
        @Config.RangeInt(min = 1, max = 64)
        public int rangedMinerMaxBlocksPerTick = 10;

        @Config.Comment("Default: 32")
        @Config.RangeInt(min = 1, max = 64)
        public int maxClayLaserLength = 32;

        @Config.RequiresMcRestart
        @Config.Comment({
                "The block used for specifying the range of the clay steel tools.",
                "Format: 'modid:name;meta?'",
                "meta is default to 0. fallback to minecraft:clay.",
                "Default: minecraft:clay"
        })
        public String claySteelToolBlock = "minecraft:clay";
    }

    public static class FeGen {
        private FeGen() {}

        @Config.Comment("Default: false")
        @Config.RequiresMcRestart
        public boolean enableFeGenerators = false;

        @Config.Comment({
                "CE consumption /t of the Energy Converters in CE unit (1 = 1 CE, not 10u CE)",
                "Default: 0.001 * 10^i, i = 0, 1, 2, ..., 9"
        })
        @Config.Name("CE/t")
        public double[] cePerTick = new double[] { 0.001, 0.01, 0.1, 1, 10, 100, 1_000, 10_000, 100_000, 1000_000 };

        @Config.Comment({
                "FE production /t of the Energy Converters.",
                "Default: 10 * 3^i, i = 0, 1, 2, ..., 9"
        })
        @Config.Name("FE/t")
        public int[] fePerTick = new int[] { 10, 30, 90, 270, 810, 2430, 7290, 21870, 65610, 196830 };

        @Config.Comment({
                "FE storage size of the Energy Converters.",
                "Default: FE/t * 1000"
        })
        @Config.Name("FE Storage Size")
        public int[] feStorageSize = new int[] { 10_000, 30_000, 90_000, 270_000, 810_000, 2430_000, 7290_000, 21870_000, 65610_000, 196830_000 };
    }
}
