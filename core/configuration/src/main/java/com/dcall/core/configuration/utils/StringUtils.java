package com.dcall.core.configuration.utils;

import com.dcall.core.configuration.utils.parser.ASCII;
import com.dcall.core.configuration.utils.parser.IterStringUtils;

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

    public static CharSequence trim(final CharSequence seq) {
        final int startIdx = IterStringUtils.iterFront(seq, 0, seq.length(), c -> ASCII.isBlank(c));
        final int endIdx = IterStringUtils.iterBack(seq, seq.length() - 1, 0, c -> ASCII.isBlank(c));

        return seq.subSequence(startIdx, endIdx + 1);
    }

    public static boolean isEmpty(final String s) {
        return s == null || s.isEmpty();
    }
}
