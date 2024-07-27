package com.github.trc.clayium.common.config;

import com.github.trc.clayium.common.Clayium;
import net.minecraftforge.common.config.Config;

@Config.LangKey("clayium.config.core")
@Config(modid = Clayium.MOD_ID, name = Clayium.MOD_ID + "/core")
public class ConfigCore {

    public static SubCategoryRendering rendering = new SubCategoryRendering();
    public static SubCategoryMisc misc = new SubCategoryMisc();
    public static SubCategoryWorldGen worldGen = new SubCategoryWorldGen();

    public static class SubCategoryRendering {
        private SubCategoryRendering() {}

        @Config.Comment({
                "Visual quality of clay laser",
                "2 is recommended for for low-end computers."
        })
        @Config.RangeInt(min = 1, max = 32)
        public int laserQuality = 8;
    }

    public static class SubCategoryMisc {
        private SubCategoryMisc() {}

        @Config.Comment({
                "Inverts the redstone condition for the clay laser.",
                "false: laser irradiates when NOT powered",
                "true : laser irradiates when powered"
        })
        public boolean invertClayLaserRsCondition = false;
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
}
