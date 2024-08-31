package com.github.trc.clayium.common.config;

import com.github.trc.clayium.api.CValues;
import net.minecraftforge.common.config.Config;

@Config.LangKey("clayium.config.core")
@Config(modid = CValues.MOD_ID, name = CValues.MOD_ID + "/core")
public class ConfigCore {

    public static SubCategoryRendering rendering = new SubCategoryRendering(); //todo move to separate file
    public static SubCategoryWorldGen worldGen = new SubCategoryWorldGen();
    public static SubCategoryMisc misc = new SubCategoryMisc();

    public static class SubCategoryRendering {
        private SubCategoryRendering() {}

        @Config.Comment({
                "Visual quality of clay laser",
                "2 is recommended for for low-end computers."
        })
        @Config.RangeInt(min = 1, max = 32)
        public int laserQuality = 8;
    }

    public static class SubCategoryWorldGen {
        private SubCategoryWorldGen() {}

        public int clayOreVeinMaxY = 88;
        public int clayOreVeinMinY = 24;
        public int clayOreVeinNumber = 8;
        public int clayOreVeinSize = 24;

        public int denseClayOreVeinSize = 10;
        public int largeDenseClayOreVeinNumber = 2;
        public int largeDenseClayOreVeinSize = 6;
        public int largeDenseClayOreVeinMinY = 10;
        public int largeDenseClayOreVeinMaxY = 16;

        public boolean generateDenseClayOreVein = true;
    }

    public static class SubCategoryMisc {
        private SubCategoryMisc() {}

        @Config.Comment({
                "Inverts the redstone condition for the clay laser.",
                "false: laser irradiates when NOT powered",
                "true : laser irradiates when powered"
        })
        public boolean invertClayLaserRsCondition = false;

        @Config.Comment({
                "Lower is better.",
                "The lower the value, more chance to generate energy when ticked."
        })
        @Config.RangeInt(min = 1, max = 100)
        public int waterwheelEfficiency = 40;

        @Config.RangeInt(min = 1, max = 30)
        @Config.Comment({
               "The maximum tier that the waterwheel can provide energy to.",
               "It cannot provide energy to machines of a higher tier."
        })
        public int waterwheelMaxTier = 3;

        @Config.Comment({
                "Max search distance for PAN"
        })
        @Config.RangeInt(min = 1, max = 1000)
        public int panMaxSearchDistance = 500;

        @Config.RangeInt(min = 1, max = 512)
        public int clayMarkerMaxRange = 64;

        @Config.RangeInt(min = 1, max = 64)
        public int rangedMinerMaxBlocksPerTick = 10;

        @Config.RangeInt(min = 1, max = 64)
        public int maxClayLaserLength = 32;
    }
}
