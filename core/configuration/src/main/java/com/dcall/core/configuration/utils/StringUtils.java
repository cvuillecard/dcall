package com.dcall.core.configuration.utils;

import java.util.List;

public final class StringUtils {

    public static <T> String listToString(final List<T> list) {
        final String str = list.toString().replaceAll(", ", " ");
        return str.substring(1, str.length() - 1);
    }

    public static String epur(final String str) {
        return str.replaceAll("^ +| +$|( )+", "$1");
    }
}
