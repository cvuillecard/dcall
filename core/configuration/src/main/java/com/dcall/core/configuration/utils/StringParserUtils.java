package com.dcall.core.configuration.utils;

import com.dcall.core.configuration.generic.parser.ASCII;
import com.dcall.core.configuration.generic.parser.IterStringUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class StringParserUtils {

    public static int nextDelimiterIdx(final CharSequence seq, int idx, final int endIdx, final Predicate<Character> isNotDelimiterCond, final Predicate<Character> isDelimiter, final Consumer<CharSequence> accu) {
        if (idx < endIdx) {
            final char delimiter = seq.charAt(idx);
            Predicate<Character> cond = isNotDelimiterCond;
            if (isDelimiter.test(delimiter)) {
                idx += 1;
                cond = c -> c != delimiter;
            }
            final int nextIdx = IterStringUtils.iterFront(seq, idx, endIdx, cond);
            accu.accept(seq.subSequence(idx, nextIdx));
            return nextIdx;
        }
        return endIdx;
    }

    // parsing Key-Value methods
    public static void consumeKeyValues(final CharSequence seq, int idx, final int endIdx, final char equalsToken, final Predicate<Character> isValueDelimiter, final Consumer<Map.Entry<String, String>> entryConsumer) {
        final StringBuilder sb = new StringBuilder();

        while ((idx = IterStringUtils.iterFront(seq, idx, endIdx, c -> ASCII.isBlank(c))) < endIdx) {
            idx = nextDelimiterIdx(seq, idx, endIdx, c -> c != equalsToken, isValueDelimiter, s -> sb.append(s.toString()));
            final String key = sb.toString();
            sb.setLength(0);
            idx = IterStringUtils.iterFront(seq, idx, endIdx, c -> c != equalsToken) + 1;
            idx = IterStringUtils.iterFront(seq, idx, endIdx, c -> ASCII.isBlank(c));
            idx = nextDelimiterIdx(seq, idx, endIdx, c -> !ASCII.isBlank(c), isValueDelimiter, s -> entryConsumer.accept(new AbstractMap.SimpleEntry<>(key, s.toString()))) + 1;
        }
    }

    public static List<String> parseKeyValueToList(final CharSequence seq, int idx, final int endIdx, final char equalsToken, final Predicate<Character> isValueDelimiter) {
        final List<String> entries = new ArrayList<>();

        consumeKeyValues(seq, idx, endIdx, equalsToken, isValueDelimiter, entry -> entries.add(entry.getKey() + equalsToken +  entry.getValue()));

        return entries;
    }

    public static Map<String, String> parseKeyValueToMap(final CharSequence seq, int idx, final int endIdx, final char equalsToken, final Predicate<Character> isValueDelimiter) {
        final Map<String, String> entries = new HashMap<>();

        consumeKeyValues(seq, idx, endIdx, equalsToken, isValueDelimiter, entry -> entries.put(entry.getKey(), entry.getValue()));

        return entries;
    }

    // parsing Word methods
    public static void consumeWords(final CharSequence seq, int idx, final int endIdx, final Predicate<Character> isNotDelimiterCond, final Predicate<Character> isValueDelimiter, final Consumer<CharSequence> wordConsumer) {
        while ((idx = IterStringUtils.iterFront(seq, idx, endIdx, c -> ASCII.isBlank(c))) < endIdx)
            idx = nextDelimiterIdx(seq, idx, endIdx, isNotDelimiterCond, isValueDelimiter, s -> wordConsumer.accept(s)) + 1;
    }

    public static List<String> parseWordToList(final CharSequence seq, int idx, final int endIdx, final Predicate<Character> isValueDelimiter) {
        final List<String> entries = new ArrayList<>();

        consumeWords(seq, idx, endIdx, c -> !ASCII.isBlank(c), isValueDelimiter, s -> entries.add(s.toString()));

        return entries;
    }

    public static List<String> parseWordToList(final CharSequence seq, int idx, final int endIdx, final Predicate<Character> isNotDelimiterCond, final Predicate<Character> isValueDelimiter) {
        final List<String> entries = new ArrayList<>();

        consumeWords(seq, idx, endIdx, isNotDelimiterCond, isValueDelimiter, s -> entries.add(s.toString()));

        return entries;
    }
}
