package com.dcall.core.configuration.utils.parser;

import com.dcall.core.configuration.utils.TimerUtils;
import com.dcall.core.configuration.utils.tree.BTree;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ParserTest {
    private static final TimerUtils timer = new TimerUtils();
    private static final Parser parser = new Parser();

    @Test
    public void should_create_list_with_alphanum_string_accumulate() {
        final String str = " Toto a 5 ans depuis le lendemain du dernier jour de ses 4 ans";

        timer.startTimer();
        List<CharSequence> alphaNum = IterStringUtils.accumulateList(str, 0, c -> ASCII.isAlphaNum(c));
        timer.stopTimer();

        timer.logTime("should_create_list_with_alphanum_string_accumulate");

        Assert.assertTrue(alphaNum.contains("Toto"));
        Assert.assertTrue(alphaNum.contains("a"));
        Assert.assertTrue(alphaNum.contains("5"));
        Assert.assertTrue(alphaNum.contains("lendemain"));
        Assert.assertTrue(alphaNum.contains("4"));
        Assert.assertTrue(alphaNum.contains("ans"));
    }

    @Test
    public void should_parse_string_as_btree_parse() {
        final String empty = "";
        final String exp1 = "env set auto_commit=true";
        final String exp2 = "(identity get email)";
        final String exp3 = "(identity get email) & (identity get surname)";
        final String exp4 = "((identity get email & identity get surname) & identity get age)";
        final String exp5 = "((identity get email & identity get surname) & (identity get age))";
        final String exp6 = " 1 + ( 2 * ( 3 + 4 ))";
        final String exp7 = " 1 + ((2 + 3) * ( 4 + 5 ))";
        final String exp8 = " 1 + (((2 + 3) * ( 4 + 5 )) / 3)";

        BTree<CharSequence> NUL = parser.parse(empty);
        BTree<CharSequence> tree1 = parser.parse(exp1);
        BTree<CharSequence> tree2 = parser.parse(exp2);
        BTree<CharSequence> tree3 = parser.parse(exp3);
        BTree<CharSequence> tree4 = parser.parse(exp4);
        BTree<CharSequence> tree5 = parser.parse(exp5);
        BTree<CharSequence> tree6 = parser.parse(exp6);
        BTree<CharSequence> tree7 = parser.parse(exp7);
        BTree<CharSequence> tree8 = parser.parse(exp8);

        Assert.assertTrue(NUL == null);
        Assert.assertEquals(exp1, tree1.getData());
        Assert.assertEquals(exp2.substring(1, exp2.length() - 1), tree2.getData().toString());
    }
}
