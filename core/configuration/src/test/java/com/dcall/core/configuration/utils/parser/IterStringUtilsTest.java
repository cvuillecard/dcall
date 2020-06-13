package com.dcall.core.configuration.utils.parser;

import com.dcall.core.configuration.utils.TimerUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class IterStringUtilsTest {
    private static final Logger LOG = LoggerFactory.getLogger(IterStringUtilsTest.class);
    private static final TimerUtils timer = new TimerUtils();

    private static final String randomSymbols = "\\[;";
    private static final String spacesStr = "   ";

    private static final String alphaStr = "abcdefZYXWzA";
    private static final String notAlphaStr = randomSymbols + alphaStr;
    private static final String numStr = "0123456789";
    private static final String alphaNumStr = alphaStr + numStr;
    private static final String notAlphaNumStr = spacesStr + randomSymbols;
    private static final String backSlashStr = "\b\t\n\f\r\0";
    private static final String notBackslashStr = backSlashStr + notAlphaNumStr + alphaNumStr;
    private static final String alphaNumWithSpaces = spacesStr + alphaNumStr;

    private static final String totoStr = " Toto a 5 ans depuis le lendemain du dernier jour de ses 4 ans";

    @Test
    public void should_iterate_spaces_in_str_iterSpace() {
        final int nbSpaces = spacesStr.length();
        final CharSequence str = alphaNumWithSpaces;
        int idx = 0;
        idx = IterStringUtils.iter(alphaNumWithSpaces, idx, c -> ASCII.isSpace(c));

        Assert.assertEquals(nbSpaces, idx);
        Assert.assertTrue(ASCII.isAlphaNum(str.charAt(idx)));
    }

    @Test
    public void should_iterate_alpha_in_str_iterAlpha() {
        final int nbAlpha = alphaStr.length();
        final CharSequence str = alphaNumStr;
        int idx = 0;
        idx = IterStringUtils.iter(str, idx, c -> ASCII.isAlpha(c));

        Assert.assertEquals(nbAlpha, idx);
    }

    @Test
    public void should_iterate_num_in_str_iterAlpha() {
        timer.startTimer();
        Assert.assertEquals(numStr.length(), IterStringUtils.iter(numStr, 0, c -> ASCII.isNum(c)));
        timer.stopTimer().logTime("should_iterate_num_in_str_iterAlpha -> with IterStringUtils.iter(..)");

        timer.startTimer();
        int i = 0;
        CharSequence seq = numStr;
        while (i < numStr.length() && ASCII.isNum(seq.charAt(i))) i++;
        Assert.assertEquals(numStr.length(), i);
        timer.stopTimer().logTime("should_iterate_num_in_str_iterAlpha -> with 'while' in test method ");
    }

    @Test
    public void should_iterate_alpha_num_in_str_iterAlpha() {
        final int nbAlphaNum = alphaNumStr.length();
        final CharSequence str = alphaNumStr;
        int idx = 0;
        idx = IterStringUtils.iter(str, idx, c -> ASCII.isAlphaNum(c));

        Assert.assertEquals(nbAlphaNum, idx);
    }

    @Test
    public void should_iterate_not_alpha_num_str_iterNonAlphaNum() {
        final int nbSymbols = notAlphaNumStr.length();
        final CharSequence str = notAlphaNumStr;
        int idx = 0;
        idx = IterStringUtils.iter(str, idx, c -> !ASCII.isAlphaNum(c));

        Assert.assertEquals(nbSymbols, idx);
    }

    @Test
    public void should_iterate_backslash_str_iterBackslash() {
        final int nbBackslash = backSlashStr.length();
        final CharSequence str = notBackslashStr;
        int idx = 0;

        // when backslash continue iteration
        idx = IterStringUtils.iter(str, idx, c -> ASCII.isBackslash(c));

        Assert.assertEquals(nbBackslash, idx);
        Assert.assertFalse(ASCII.isBackslash(str.charAt(idx)));
        Assert.assertTrue(ASCII.isSpace(str.charAt(idx)));

        // when space continue iteration
        idx = IterStringUtils.iter(str, idx, c -> ASCII.isBlank(c));

        Assert.assertFalse(ASCII.isSpace(str.charAt(idx)));
        Assert.assertFalse(ASCII.isAlphaNum(str.charAt(idx)));

        // when not alpha num continue iteration
        idx = IterStringUtils.iter(str, idx, c -> !ASCII.isAlphaNum(c));

        Assert.assertTrue(ASCII.isAlphaNum(str.charAt(idx)));

        // when alpha num continue iteration
        idx = IterStringUtils.iter(str, idx, c -> ASCII.isAlphaNum(c));
        Assert.assertEquals(str.length(), idx);
    }

    @Test
    public void should_accumulate_alpha_num_in_str_accumulate() {
//        timer.startTimer();
        final int nbAlpha = alphaStr.length();
        final int nbNum = numStr.length();
        final CharSequence str = notBackslashStr;
        int idx = IterStringUtils.iter(str, 0, c -> !ASCII.isAlphaNum(c));

        CharSequence alpha = IterStringUtils.accumulate(str, idx, c -> ASCII.isAlpha(c));
        CharSequence num = IterStringUtils.accumulate(str, idx + alpha.length(), c -> ASCII.isNum(c));
//        timer.stopTimer().logTime("should_accumulate_alpha_num_in_str_accumulate");
        Assert.assertEquals(nbAlpha, alpha.length());
        Assert.assertEquals(alphaStr, alpha.toString());

        Assert.assertEquals(nbNum, num.length());
        Assert.assertEquals(numStr, num.toString());
    }

    @Test
    public void should_accumulate_alpha_num_using_Regex() {
        timer.startTimer();

        final List<String> list = new ArrayList<>();
        final String seq = "[a-zA-Z0-9]*";
        final Pattern pattern = Pattern.compile(seq);
        final Matcher matcher = pattern.matcher(totoStr);

        while (matcher.find())
            if (matcher.group().length() > 0)
                list.add(matcher.group());

        timer.stopTimer();

        timer.logTime("should_accumulate_alpha_num_using_Regex");

        Assert.assertEquals(list.size(), list.size());

        Assert.assertTrue(list.contains("Toto"));
        Assert.assertTrue(list.contains("a"));
        Assert.assertTrue(list.contains("5"));
        Assert.assertTrue(list.contains("lendemain"));
        Assert.assertTrue(list.contains("4"));
        Assert.assertTrue(list.contains("ans"));
    }

    @Test
    public void should_accumulate_alpha_num_using_accumulateList() {
        timer.startTimer();
        List<CharSequence> list = IterStringUtils.accumulateList(totoStr, 0, c -> ASCII.isAlphaNum(c));
        timer.stopTimer();

        timer.logTime("should_accumulate_alpha_num_accumulateList");

        Assert.assertEquals(list.size(), list.size());

        Assert.assertTrue(list.contains("Toto"));
        Assert.assertTrue(list.contains("a"));
        Assert.assertTrue(list.contains("5"));
        Assert.assertTrue(list.contains("lendemain"));
        Assert.assertTrue(list.contains("4"));
        Assert.assertTrue(list.contains("ans"));
    }

    @Test
    public void should_accumulate_alpha_num_using_inner_while_with_iter_class() {
        timer.startTimer();

        final List<CharSequence> list = new ArrayList<>();
        int idx = 0;
        int end = 0;

        while ((idx = IterStringUtils.iter(totoStr, idx, c -> ASCII.isBlank(c))) < totoStr.length()) {
            end = IterStringUtils.iter(totoStr, idx, c -> ASCII.isAlphaNum(c));
            if (end > idx) {
                list.add(totoStr.subSequence(idx, end));
                idx = end;
            }
        }

        timer.stopTimer();

        timer.logTime("should_accumulate_alpha_num_using_inner_while_with_iter_class");

        Assert.assertEquals(list.size(), list.size());

        Assert.assertTrue(list.contains("Toto"));
        Assert.assertTrue(list.contains("a"));
        Assert.assertTrue(list.contains("5"));
        Assert.assertTrue(list.contains("lendemain"));
        Assert.assertTrue(list.contains("4"));
        Assert.assertTrue(list.contains("ans"));
    }

    @Test
    public void should_accumulate_alpha_num_using_inner_while() {
        timer.startTimer();
        final List<String> list = new ArrayList<>();
        final CharSequence strArray = totoStr;

        int i = 0;
        while (i < strArray.length()) {
            final StringBuilder sb = new StringBuilder();
            while (i < strArray.length() && ASCII.isSpace(strArray.charAt(i))) i++;
            while (i < strArray.length() && ASCII.isAlphaNum(strArray.charAt(i)))
                sb.append(strArray.charAt(i++));
            final String s = sb.toString();
            if (s.length() > 0)
                list.add(s);
        }
        timer.stopTimer();

        timer.logTime("should_accumulate_alpha_num_using_inner_while");

        Assert.assertEquals(list.size(), list.size());

        Assert.assertTrue(list.contains("Toto"));
        Assert.assertTrue(list.contains("a"));
        Assert.assertTrue(list.contains("5"));
        Assert.assertTrue(list.contains("lendemain"));
        Assert.assertTrue(list.contains("4"));
        Assert.assertTrue(list.contains("ans"));
    }
}
