package com.dcall.core.configuration.utils.parser.expression;

import com.dcall.core.configuration.utils.parser.ASCII;
import com.dcall.core.configuration.utils.parser.IterStringUtils;

public final class Mutator {
    public static <T> T mutate(final CharSequence seq) {
        if (seq != null && seq.length() > 0) {
            final int numIdx = IterStringUtils.iterFront(seq, 0, seq.length(), c -> ASCII.isNum(c));

            if (numIdx < seq.length())
                return (T) seq.toString();
            else
                return (T) Long.valueOf(seq.toString());
        }
        return null;
    }
}
