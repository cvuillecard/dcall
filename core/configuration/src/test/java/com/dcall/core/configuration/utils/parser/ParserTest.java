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
    public void should_create_list_expressions_in_parentheses_parseExpression() {
        timer.startTimer();
        final String exp0 = " expression 1 + expression 2";
        final String exp1 = " \n my expression +46745 - 013     \t  ";
        final String exp2 = "    \t  (    expression 1    )    (  expression 2 )  ";
        final String exp3 = "    \t  (    expression 1    )  +  (  expression 2 ) &  (  expression 3 ) ";
        final String exp4 = "    \t  (    expression 1  + \t  expression 2  )  +  (  expression 3  +   expression 4  )  ";
        final String exp5 = "    \t  expression 1  *  (  expression 2  +   expression 3  )  ";

        List<Node<CharSequence>> list0 = parser.parseExpression(exp0);
        BTree<CharSequence> btree0 = parser.parse(exp0);
        List<Node<CharSequence>> list1 = parser.parseExpression(exp1);
        BTree<CharSequence> btree1 = parser.parse(exp1);
        List<Node<CharSequence>> list2 = parser.parseExpression(exp2);
        BTree<CharSequence> btree2 = parser.parse(exp2);
        List<Node<CharSequence>> list3 = parser.parseExpression(exp3);
        BTree<CharSequence> btree3 = parser.parse(exp3);
        List<Node<CharSequence>> list4 = parser.parseExpression(exp4);
        BTree<CharSequence> btree4 = parser.parse(exp4);
        List<Node<CharSequence>> list5 = parser.parseExpression(exp5);
        BTree<CharSequence> btree5 = parser.parse(exp5);

        timer.stopTimer().logTime("should_create_list_expressions_in_parentheses_parseExpression");

        Assert.assertEquals(3, list0.size()); // 2 operand + 1 operator = 3 expressions
        Assert.assertEquals(5, list1.size()); // 3 operand + 2 operators = 5 expressions
        Assert.assertEquals(2, list2.size()); // no operators + 2 operand = 2 expressions
        Assert.assertEquals(5, list3.size()); // 2 operators + 3 operand = 5 expressions
        Assert.assertEquals(7, list4.size()); // 3 operators + 4 operand  = 7 expressions
        Assert.assertEquals(5, list5.size()); // 2 operators + 3 expressions = 5 expressions
    }

    @Test
    public void should_parse_string_as_btree_parse() {
        final String empty = "";
        final String exp1 = "env set auto_commit=true";
        final String exp2 = "(identity get email)";
        final String exp3 = "(identity get email) & (identity get surname)";
        final String exp4 = "((identity get email & identity get surname) & identity get age)";
        final String exp5 = "((identity get email & identity get surname) & (identity get age))";

        BTree<CharSequence> NUL = parser.parse("");
        BTree<CharSequence> tree1 = parser.parse(exp1);
        BTree<CharSequence> tree2 = parser.parse(exp2);
        BTree<CharSequence> tree3 = parser.parse(exp3);
        BTree<CharSequence> tree4 = parser.parse(exp4);

        Assert.assertTrue(NUL == null);
        Assert.assertEquals(exp1, tree1.getData());
        Assert.assertEquals(exp2.substring(1, exp2.length() - 1), tree2.getData().toString());

        Assert.assertEquals("identity get email", tree3.getData());
        Assert.assertEquals("identity get email", tree3.getParent().getLeft().getData());
        Assert.assertEquals(tree3, tree3.getParent().getLeft());

        Assert.assertEquals("&", tree3.getParent().getData());
        Assert.assertTrue(ASCII.isOperator(tree3.getParent().getData().charAt(0)));

        Assert.assertEquals("identity get surname", tree3.getParent().getRight().getData());

        //todo not finished
    }
}
