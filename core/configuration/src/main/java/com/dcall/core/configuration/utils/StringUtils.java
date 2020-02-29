package com.dcall.core.configuration.utils;

import java.util.List;

public final class StringUtils {

    public static <T> String listToString(final List<T> list) {
        if (!list.isEmpty()) {
            final String str = list.toString().replaceAll(", ", " ");
            return str.substring(1, str.length() - 1);
        }

        return null;
    }

    public static String epur(final String str) {
        if (str != null && str.length() > 0)
            return str.replaceAll("^ +| +$|( )+", "$1");

        return str;
    }
}
