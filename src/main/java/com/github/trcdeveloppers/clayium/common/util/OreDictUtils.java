package com.github.trcdeveloppers.clayium.common.util;

import javax.annotation.Nonnull;
import java.util.Locale;

public class OreDictUtils {
    /**
     * shapeMaterialName => material_name
     */
    public static String extractMaterialName(@Nonnull String oreDictName) {
        if (oreDictName.isEmpty()) return "";
        return oreDictName.replaceAll("^[a-z]+", "")
            .replaceAll("([a-z])([A-Z])", "$1_$2")
            .toLowerCase(Locale.ROOT);
    }
}
