package com.dcall.core.configuration.utils.parser;

import com.dcall.core.configuration.utils.tree.BTree;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParserOldVersionTest {
    private static final Logger LOG = LoggerFactory.getLogger(ParserOldVersionTest.class);

    private static final ParserOldVersion parser = new ParserOldVersion();

    @Test
    public void should_parse_string_expressions_as_btree_parse() {
        final String empty = "";
        final String exp1 = "env set auto_commit=true";
        final String exp2 = "(identity get email)";
        final String exp3 = "(identity get email) & (identity get surname)";
        final String exp4 = "((identity get email & identity get surname) & identity get age)";
        final String exp5 = "((identity get email & identity get surname) & (identity get age))";

        BTree<CharSequence> NUL = parser.reset().parse(empty, 0, empty.length());
        BTree<CharSequence> tree1 = parser.reset().parse(exp1, 0, exp1.length());
        BTree<CharSequence> tree2 = parser.reset().parse(exp2, 0, exp2.length());
        BTree<CharSequence> tree3 = parser.reset().parse(exp3, 0, exp3.length());
        BTree<CharSequence> tree4 = parser.reset().parse(exp4, 0, exp4.length());
        BTree<CharSequence> tree5 = parser.reset().parse(exp5, 0, exp5.length());

        // empty : when empty or null string
        Assert.assertTrue(NUL == null);

        // exp1 : "env set auto_commit=true" > when single expression
        Assert.assertEquals(exp1, tree1.getData());

        // exp2 : "(identity get email)" > when single expression in parenthesis
        Assert.assertEquals("identity get email", tree2.getData().toString());
        Assert.assertTrue(tree2.getParent() == null && tree2.getLeft() == null && tree2.getRight() == null);

        // exp3 : "(identity get email) & (identity get surname)" > when 2 expressions in parenthesis separated with one unary operator
        Assert.assertEquals("&", tree3.getData());
        Assert.assertEquals("identity get email", tree3.getLeft().getData());
        Assert.assertEquals("identity get surname", tree3.getRight().getData());
        Assert.assertEquals(tree3, tree3.getLeft().getParent());
        Assert.assertEquals(tree3, tree3.getRight().getParent());

        // exp4 : "((identity get email & identity get surname) & identity get age)" > when one expression in parenthesis with an inner/sub expression as left operand
        // > top
        Assert.assertEquals("&", tree4.getData());
        Assert.assertEquals(tree4, tree4.getLeft().getParent());
        Assert.assertEquals(tree4, tree4.getRight().getParent());

        // > left child expression
        Assert.assertEquals("&", tree4.getLeft().getData());
        Assert.assertEquals("identity get email", tree4.getLeft().getLeft().getData().toString().trim());
        Assert.assertEquals("identity get surname", tree4.getLeft().getRight().getData());
        Assert.assertEquals(tree4.getLeft(), tree4.getLeft().getLeft().getParent());
        Assert.assertEquals(tree4.getLeft(), tree4.getLeft().getRight().getParent());
        // > right child expression
        Assert.assertEquals("identity get age", tree4.getRight().getData());

        // exp5 : "((identity get email & identity get surname) & (identity get age))" > when one expression in parenthesis with an inner/sub expression as left operand and a right operand as expression in parenthesis
        // > top
        Assert.assertEquals("&", tree5.getData());
        Assert.assertEquals(tree5, tree5.getLeft().getParent());
        Assert.assertEquals(tree5, tree5.getRight().getParent());

        // > left child expression
        Assert.assertEquals("&", tree5.getLeft().getData());
        Assert.assertEquals("identity get email", tree5.getLeft().getLeft().getData().toString().trim());
        Assert.assertEquals("identity get surname", tree5.getLeft().getRight().getData());
        Assert.assertEquals(tree5.getLeft(), tree5.getLeft().getLeft().getParent());
        Assert.assertEquals(tree5.getLeft(), tree5.getLeft().getRight().getParent());
        // > right child expression
        Assert.assertEquals("identity get age", tree5.getRight().getData());
    }

    @Test
    public void should_parse_binary_operators_expressions_as_btree_parse() {
        final String exp1 = "(identity get email) && (identity get surname)";
        final String exp2 = "((identity get email && identity get surname) || identity get age)";
        final String exp3 = "((identity get email || identity get surname) && (identity get age))";

        BTree<CharSequence> tree1 = parser.reset().parse(exp1, 0, exp1.length());
        BTree<CharSequence> tree2 = parser.reset().parse(exp2, 0, exp2.length());
        BTree<CharSequence> tree3 = parser.reset().parse(exp3, 0, exp3.length());

        // exp1 : "(identity get email) & (identity get surname)" > when 2 expressions in parenthesis separated with one unary operator
        Assert.assertEquals("&&", tree1.getData());
        Assert.assertEquals("identity get email", tree1.getLeft().getData());
        Assert.assertEquals("identity get surname", tree1.getRight().getData());
        Assert.assertEquals(tree1, tree1.getLeft().getParent());
        Assert.assertEquals(tree1, tree1.getRight().getParent());

        // exp2 : "((identity get email & identity get surname) & identity get age)" > when one expression in parenthesis with an inner/sub expression as left operand
        // > top
        Assert.assertEquals("||", tree2.getData());
        Assert.assertEquals(tree2, tree2.getLeft().getParent());
        Assert.assertEquals(tree2, tree2.getRight().getParent());

        // > left child expression
        Assert.assertEquals("&&", tree2.getLeft().getData());
        Assert.assertEquals("identity get email", tree2.getLeft().getLeft().getData().toString().trim());
        Assert.assertEquals("identity get surname", tree2.getLeft().getRight().getData());
        Assert.assertEquals(tree2.getLeft(), tree2.getLeft().getLeft().getParent());
        Assert.assertEquals(tree2.getLeft(), tree2.getLeft().getRight().getParent());
        // > right child expression
        Assert.assertEquals("identity get age", tree2.getRight().getData());

        // exp3 : "((identity get email & identity get surname) & (identity get age))" > when one expression in parenthesis with an inner/sub expression as left operand and a right operand as expression in parenthesis
        // > top
        Assert.assertEquals("&&", tree3.getData());
        Assert.assertEquals(tree3, tree3.getLeft().getParent());
        Assert.assertEquals(tree3, tree3.getRight().getParent());

        // > left child expression
        Assert.assertEquals("||", tree3.getLeft().getData());
        Assert.assertEquals("identity get email", tree3.getLeft().getLeft().getData().toString().trim());
        Assert.assertEquals("identity get surname", tree3.getLeft().getRight().getData());
        Assert.assertEquals(tree3.getLeft(), tree3.getLeft().getLeft().getParent());
        Assert.assertEquals(tree3.getLeft(), tree3.getLeft().getRight().getParent());
        // > right child expression
        Assert.assertEquals("identity get age", tree3.getRight().getData());
    }

    @Test
    public void should_parse_unary_operators_expressions_as_btree_parse() {
        final String exp1 = "(identity get email) & (identity get surname)";
        final String exp2 = "((identity get email & identity get surname) | identity get age)";
        final String exp3 = "((identity get email | identity get surname) && (identity get age))";

        BTree<CharSequence> tree1 = parser.reset().parse(exp1, 0, exp1.length());
        BTree<CharSequence> tree2 = parser.reset().parse(exp2, 0, exp2.length());
        BTree<CharSequence> tree3 = parser.reset().parse(exp3, 0, exp3.length());

        // exp1 : "(identity get email) & (identity get surname)" > when 2 expressions in parenthesis separated with one unary operator
        Assert.assertEquals("&", tree1.getData());
        Assert.assertEquals("identity get email", tree1.getLeft().getData());
        Assert.assertEquals("identity get surname", tree1.getRight().getData());
        Assert.assertEquals(tree1, tree1.getLeft().getParent());
        Assert.assertEquals(tree1, tree1.getRight().getParent());

        // exp2 : "((identity get email & identity get surname) & identity get age)" > when one expression in parenthesis with an inner/sub expression as left operand
        // > top
        Assert.assertEquals("|", tree2.getData());
        Assert.assertEquals(tree2, tree2.getLeft().getParent());
        Assert.assertEquals(tree2, tree2.getRight().getParent());

        // > left child expression
        Assert.assertEquals("&", tree2.getLeft().getData());
        Assert.assertEquals("identity get email", tree2.getLeft().getLeft().getData().toString().trim());
        Assert.assertEquals("identity get surname", tree2.getLeft().getRight().getData());
        Assert.assertEquals(tree2.getLeft(), tree2.getLeft().getLeft().getParent());
        Assert.assertEquals(tree2.getLeft(), tree2.getLeft().getRight().getParent());
        // > right child expression
        Assert.assertEquals("identity get age", tree2.getRight().getData());

        // exp3 : "((identity get email & identity get surname) & (identity get age))" > when one expression in parenthesis with an inner/sub expression as left operand and a right operand as expression in parenthesis
        // > top
        Assert.assertEquals("&&", tree3.getData());
        Assert.assertEquals(tree3, tree3.getLeft().getParent());
        Assert.assertEquals(tree3, tree3.getRight().getParent());

        // > left child expression
        Assert.assertEquals("|", tree3.getLeft().getData());
        Assert.assertEquals("identity get email", tree3.getLeft().getLeft().getData().toString().trim());
        Assert.assertEquals("identity get surname", tree3.getLeft().getRight().getData());
        Assert.assertEquals(tree3.getLeft(), tree3.getLeft().getLeft().getParent());
        Assert.assertEquals(tree3.getLeft(), tree3.getLeft().getRight().getParent());
        // > right child expression
        Assert.assertEquals("identity get age", tree3.getRight().getData());
    }

    @Test
    public void should_parse_arithmetic_expressions_as_btree_parse() {
        final String exp6 = " 1 + ( 2 * ( 3 + 4 ))  ";
        final String exp7 = " 1 + ((2 + 3) * ( 4 + 5 ))  ";
        final String exp8 = " 1 + (((2 + 3) * ( 4 + 5 )) / 3)  ";
        final String exp9 = "  1   +   3  /  (5) ";

        final BTree<CharSequence> tree6 = parser.reset().parse(exp6, 0, exp6.length());
        final BTree<CharSequence> tree7 = parser.reset().parse(exp7, 0, exp7.length());
        final BTree<CharSequence> tree8 = parser.reset().parse(exp8, 0, exp8.length());
        final BTree<CharSequence> tree9 = parser.reset().parse(exp9, 0, exp9.length());

        // exp6 : " 1 + ( 2 * ( 3 + 4 ))" > one left operand with one right expression as tree expressions in parenthesis
        // > top
        final BTree topSum = tree6;
        Assert.assertEquals("+", topSum.getData());
        // > left child
        Assert.assertEquals("1", topSum.getLeft().getData().toString().trim());
        Assert.assertEquals(topSum, topSum.getLeft().getParent());
        Assert.assertTrue(topSum.getLeft().getLeft() == null && topSum.getLeft().getRight() == null);
        // > right child
        final BTree lmul6 = topSum.getRight();
        Assert.assertEquals("*", lmul6.getData());
        // > right child > left child
        Assert.assertEquals("2", lmul6.getLeft().getData().toString().trim());
        Assert.assertEquals(tree6.getRight(), lmul6.getLeft().getParent());
        Assert.assertEquals(tree6.getRight(), lmul6.getRight().getParent());
        // > right child > right child
        final BTree rsum6 = tree6.getRight().getRight();
        Assert.assertEquals("+", rsum6.getData());
        Assert.assertEquals("3", rsum6.getLeft().getData().toString().trim());
        Assert.assertEquals("4", rsum6.getRight().getData().toString().trim());
        Assert.assertEquals(rsum6, rsum6.getLeft().getParent());
        Assert.assertEquals(rsum6, rsum6.getRight().getParent());

        // exp7 : "1 + ((2 + 3) * ( 4 + 5 ))" > tow expressions : one operand left + one expression (sum) multiplied by another expression (sum)
        // top
        Assert.assertEquals("+", tree7.getData());
        Assert.assertEquals(tree7, tree7.getLeft().getParent());
        Assert.assertEquals(tree7, tree7.getRight().getParent());
        // top > left child
        final BTree topLeftChild = tree7.getLeft();
        Assert.assertEquals("1", topLeftChild.getData().toString().trim());
        Assert.assertTrue(topLeftChild.getLeft() == null && topLeftChild.getRight() == null);
        // top > right child
        final BTree topRightChild = tree7.getRight();
        Assert.assertEquals("*", topRightChild.getData());
        Assert.assertEquals("+", topRightChild.getLeft().getData());
        Assert.assertEquals("+", topRightChild.getRight().getData());
        // top > right child > left child
        final BTree leftSumChild = topRightChild.getLeft();
        Assert.assertEquals("2", leftSumChild.getLeft().getData().toString().trim());
        Assert.assertEquals("3", leftSumChild.getRight().getData());
        Assert.assertEquals(leftSumChild, leftSumChild.getLeft().getParent());
        Assert.assertEquals(leftSumChild, leftSumChild.getRight().getParent());
        Assert.assertTrue(leftSumChild.getLeft().getLeft() == null && leftSumChild.getLeft().getRight() == null);
        Assert.assertTrue(leftSumChild.getRight().getLeft() == null && leftSumChild.getRight().getRight() == null);

        final BTree rightSumChild = topRightChild.getRight();
        Assert.assertEquals("4", rightSumChild.getLeft().getData().toString().trim());
        Assert.assertEquals("5", rightSumChild.getRight().getData().toString().trim());
        Assert.assertEquals(rightSumChild, rightSumChild.getLeft().getParent());
        Assert.assertEquals(rightSumChild, rightSumChild.getRight().getParent());
        Assert.assertTrue(rightSumChild.getLeft().getLeft() == null && rightSumChild.getLeft().getRight() == null);
        Assert.assertTrue(rightSumChild.getRight().getLeft() == null && rightSumChild.getRight().getRight() == null);

        // exp8 : "1 + (((2 + 3) * ( 4 + 5 )) / 3)" > [ leftExp 'SUM' ( ( (subExp_left) 'MUL' (subExp_right) ) 'DIV' subExp_right ) ]
        Assert.assertEquals("+", tree8.getData());
        Assert.assertEquals(tree8, tree8.getLeft().getParent());
        Assert.assertEquals(tree8, tree8.getRight().getParent());
        // top > left child
        final BTree topLeftChild8 = tree8.getLeft();
        Assert.assertEquals("1", topLeftChild8.getData().toString().trim());
        Assert.assertTrue(topLeftChild8.getLeft() == null && topLeftChild8.getRight() == null);

        // top > right child
        final BTree div = tree8.getRight();
        Assert.assertEquals("/", div.getData());
        // top > right child > right child
        Assert.assertEquals("3", div.getRight().getData());
        Assert.assertTrue(div.getRight().getLeft() == null && div.getRight().getRight() == null);
        Assert.assertEquals(div, div.getRight().getParent());
        // top > right child > left child
        Assert.assertEquals("*", div.getLeft().getData());
        Assert.assertEquals(div, div.getLeft().getParent());
        Assert.assertEquals(div, div.getRight().getParent());
        // top > right child > left child > left child
        final BTree sumLeft = div.getLeft().getLeft();
        Assert.assertEquals("+", sumLeft.getData());
        Assert.assertEquals("2", sumLeft.getLeft().getData().toString().trim());
        Assert.assertEquals("3", sumLeft.getRight().getData().toString().trim());
        Assert.assertEquals(sumLeft, sumLeft.getLeft().getParent());
        Assert.assertEquals(sumLeft, sumLeft.getRight().getParent());
        Assert.assertTrue(sumLeft.getLeft().getLeft() == null && sumLeft.getLeft().getRight() == null);
        Assert.assertTrue(sumLeft.getRight().getLeft() == null && sumLeft.getRight().getRight() == null);
        // top > right child > left child > right child
        final BTree sumRight = div.getLeft().getRight();
        Assert.assertEquals("+", sumRight.getData());
        Assert.assertEquals("4", sumRight.getLeft().getData().toString().trim());
        Assert.assertEquals("5", sumRight.getRight().getData().toString().trim());
        Assert.assertEquals(sumRight, sumRight.getLeft().getParent());
        Assert.assertEquals(sumRight, sumRight.getRight().getParent());
        Assert.assertTrue(sumRight.getLeft().getLeft() == null && sumRight.getLeft().getRight() == null);
        Assert.assertTrue(sumRight.getRight().getLeft() == null && sumRight.getRight().getRight() == null);

        // exp9 :  "1 + 3 / (5)" > SUM(left, right) DIV right
        final BTree topDiv = tree9;
        Assert.assertEquals("/", topDiv.getData());
        // top > right child
        Assert.assertEquals("5", topDiv.getRight().getData());
        Assert.assertEquals(topDiv, topDiv.getRight().getParent());
        Assert.assertTrue(topDiv.getRight().getLeft() == null && topDiv.getRight().getRight() == null);
        // top > left child
        final BTree leftSum = topDiv.getLeft();
        Assert.assertEquals("+", leftSum.getData());
        Assert.assertEquals("1", leftSum.getLeft().getData().toString().trim());
        Assert.assertEquals("3", leftSum.getRight().getData().toString().trim());
        Assert.assertEquals(leftSum, leftSum.getLeft().getParent());
        Assert.assertEquals(leftSum, leftSum.getRight().getParent());
        Assert.assertTrue(leftSum.getLeft().getLeft() == null && leftSum.getLeft().getRight() == null);
        Assert.assertTrue(leftSum.getRight().getLeft() == null && leftSum.getRight().getRight() == null);

        Assert.assertEquals("1", parser.getFirst().getData().toString().trim());
        Assert.assertEquals("+", parser.getFirst().getParent().getData());
        Assert.assertEquals("3", parser.getFirst().getParent().getRight().getData().toString().trim());
        Assert.assertEquals("/", parser.getFirst().getParent().getParent().getData());
        Assert.assertEquals("5", parser.getFirst().getParent().getParent().getRight().getData());
    }

//    public CharSequence evalTree(final BTree<CharSequence> tree) {
//        BTree<CharSequence> ptr = tree;
//        BTree<CharSequence> last = null;
//        BTree<CharSequence> visited = null;
//        CharSequence res = "";
//
//        while (ptr != null && ptr != visited) {
//            if (ptr.getRight() != null && ptr.getRight() != last) {
//                res = res.toString() + evalTree(ptr.getRight().firstLeft());
//                visited = ptr.getParent();
//            }
//            else {
//                last = ptr;
//                res = last.getData().toString() + res.toString();
//            }
//            ptr = ptr.getParent();
//        }
//        LOG.debug(res.toString());
//
//        return res;
//    }

}
