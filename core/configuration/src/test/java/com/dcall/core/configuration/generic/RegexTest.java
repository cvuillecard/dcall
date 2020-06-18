package com.dcall.core.configuration.generic;

import com.dcall.core.configuration.utils.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public final class RegexTest {
    private static final Logger LOG = LoggerFactory.getLogger(RegexTest.class);

//    final String singleParenthesesSet = "(([^()]*))";
    final String singleParenthesesSet = "([^()]*)";
    final String identitfiers = "[A-Za-z_][0-9A-Za-z_]*";
    final String characters_with_quote_only = "'[^']'";
    final String characters_without_quote = "[^']";
    final String escape_spaces = "[^\\s]";
    final String string_chars = "[^\"]*";
    final String single_parentheses_text = "(expression -1-)";
    final String multiple_parentheses_text = "'a'  ( expression -1- ) + (expression -2-) + ((a) + (b * 3))";

    @Test
    public void should_match_string_in_string() {
//        final String pattern = "[^\"\\s$][^\"]*";
        final String pattern = "[^\"\\s\\t\\n\\r$][^\"]*";
        final String simple_string = "   \n\t\r  key1-   =    \"value with spaces\"    key2   = \"   value2   with   spaces\" ";
        final Pattern p = Pattern.compile(pattern);
        final Matcher matcher = p.matcher(simple_string);

        while (matcher.find()) {
            LOG.info(matcher.group() + " : start=" + matcher.start() + " end=" + matcher.end());
//            res.add(matcher.group());
        }

//        Assert.assertEquals(2, res.size());
//        Assert.assertTrue(res.contains("expression -1-"));
//        Assert.assertTrue(res.contains("expression -2-"));
    }

    @Test
    public void should_get_single_parentheses_expression() {
        final String parentheses = "([()])";
        final String str = "(env get auto_commit) && (identity set status=test) && (identity set (identity get status))";
        final Pattern p = Pattern.compile(parentheses);
        final Matcher matcher = p.matcher(multiple_parentheses_text);
        final List<String> operand = Arrays.asList(p.split(str));
        final List<String> expressions = new ArrayList<>();

        while (matcher.find()) {
            LOG.info(matcher.group() + " : start=" + matcher.start() + " end=" + matcher.end());
            if (matcher.group().length() > 0)
                expressions.add(matcher.group());

        }

//        Assert.assertEquals(2, res.size());
        Assert.assertTrue(expressions.contains("expression -1-"));
        Assert.assertTrue(expressions.contains("expression -2-"));
    }

    @Test
    public void should_match_arithmetic_operators_expression() {
        final String arithmeticOperators = "[+*/\\-$]";
        final String str = " 1 + 2 * 3 / 4 - 5";
        final Pattern p = Pattern.compile(arithmeticOperators);
        final Matcher matcher = p.matcher(str);
        final List<String> expressions = new ArrayList<>();
        final List<String> operand = Arrays.asList(p.split(str));

        while (matcher.find()) {
            LOG.info(matcher.group() + " : start=" + matcher.start() + " end=" + matcher.end());
            if (matcher.group().length() > 0)
                expressions.add(matcher.group());

        }

        IntStream.range(0, operand.size()).forEach(i -> Assert.assertEquals(String.valueOf(i + 1), StringUtils.epur(operand.get(i))));
    }

    @Test
    public void should_match_binary_operators_expression() {
        final String binaryOperators = "[&&||]";
        final String str = " 1 && 2 && 3 || 4 && 5 || 6 + 7";
        final Pattern p = Pattern.compile(binaryOperators);
        final Matcher matcher = p.matcher(str);
        final List<String> expressions = new ArrayList<>();
        final List<String> operand = Arrays.asList(p.split(str));

        while (matcher.find()) {
            LOG.info(matcher.group() + " : start=" + matcher.start() + " end=" + matcher.end());
            if (matcher.group().length() > 0)
                expressions.add(matcher.group());

        }

        Assert.assertTrue(!operand.isEmpty());
    }
}
