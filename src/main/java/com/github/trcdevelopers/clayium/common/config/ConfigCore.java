package com.github.trcdevelopers.clayium.common.config;

import net.minecraftforge.common.config.Config;

public class ConfigCore {

    public static SubCategoryRendering rendering = new SubCategoryRendering();

    public static class SubCategoryRendering {
        private SubCategoryRendering() {}

        @Config.Comment({
                "Visual quality of clay laser",
                "2 is recommended for for low-end computers."
        })
        @Config.RangeInt(min = 1, max = 32)
        public int laserQuality = 8;
    }
}
