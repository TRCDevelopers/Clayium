package com.github.trcdeveloppers.clayium.util;

import java.util.Locale;

public class StringUtils {
    public static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase(Locale.ROOT) + string.substring(1);
    }
}
