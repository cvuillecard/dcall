package com.dcall.core.configuration.utils.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class IterStringUtils implements java.io.Serializable {

    public static int iter(final CharSequence seq, int idx, final Predicate<Character> cond) {
        while (idx < seq.length() && cond.test(seq.charAt(idx))) idx++;

        return idx;
    }

    public static CharSequence accumulate(final CharSequence seq, int idx, final Predicate<Character> cond) {
        final int start = idx;
        while (idx < seq.length() && cond.test(seq.charAt(idx))) idx++;

        return idx > start ? seq.subSequence(start, idx) : null;
    }

    public static List<CharSequence> accumulateList(final CharSequence seq, int idx, final Predicate<Character> cond) {
        final List<CharSequence> accu = new ArrayList<>();
        int end = 0;

        while ((idx = iter(seq, idx, c -> ASCII.isBlank(c))) < seq.length()) {
            end = iter(seq, idx, cond);
            if (end > idx) {
                accu.add(seq.subSequence(idx, end));
                idx = end;
            }
        }

        return accu;
    }

//    public static List<CharSequence> accumulateList(final CharSequence seq, int idx, final Predicate<Character> cond) {
//        final List<CharSequence> accu = new ArrayList<>();
//
//        while ((idx = iter(seq, idx, c -> ASCII.isBlank(c))) < seq.length()) {
//            final CharSequence sub = accumulate(seq, idx, cond);
//            if (sub != null) {
//                accu.add(sub);
//                idx += sub.length();
//            }
//            else
//                idx++;
//        }
//
//        return accu.isEmpty() ? null : accu;
//    }

    //
    public static int iterSpace(final CharSequence seq, int idx, final Predicate<Character> cond) {
        return iter(seq, idx, c -> ASCII.isSpace(c) && predicateNotNull(cond).test(c));
    }

    public static int iterBlank(final CharSequence seq, int idx, final Predicate<Character> cond) {
        return iter(seq, idx, c -> ASCII.isBlank(c) && predicateNotNull(cond).test(c));
    }

    public static int iterAlpha(final CharSequence seq, int idx, final Predicate<Character> cond) {
        return iter(seq, idx, c -> ASCII.isAlpha(c) && predicateNotNull(cond).test(c));
    }

    public static int iterNum(final CharSequence seq, int idx, final Predicate<Character> cond) {
        return iter(seq, idx, c -> ASCII.isNum(c) && predicateNotNull(cond).test(c));
    }

    public static int iterAlphaNum(final CharSequence seq, int idx, final Predicate<Character> cond) {
        return iter(seq, idx, c -> ASCII.isAlphaNum(c) && predicateNotNull(cond).test(c));
    }

    public static int iterNonAlphaNum(final CharSequence seq, int idx, final Predicate<Character> cond) {
        return iter(seq, idx, c -> !ASCII.isAlphaNum(c) && predicateNotNull(cond).test(c));
    }

    public static int iterBackslash(final CharSequence seq, int idx, final Predicate<Character> cond) {
        return iter(seq, idx, c -> ASCII.isBackslash(c) && predicateNotNull(cond).test(c));
    }

    // util
    public static final <T> Predicate<T> predicateNotNull(final Predicate<T> predicate) { return predicate == null ? (c -> true) : predicate; }

}
