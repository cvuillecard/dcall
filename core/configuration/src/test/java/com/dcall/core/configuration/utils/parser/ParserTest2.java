package com.dcall.core.configuration.utils.parser;

import com.dcall.core.configuration.utils.parser.expression.EvalExp;
import com.dcall.core.configuration.utils.parser.expression.Expression;
import com.dcall.core.configuration.utils.parser.expression.operand.Operand;
import com.dcall.core.configuration.utils.tree.BTree;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParserTest2 {
    private static final Logger LOG = LoggerFactory.getLogger(ParserTest2.class);

    private static final Parser2 parser = new Parser2();

    @Test
    public void should_parse_string_as_expression_btree_parse() {
        final String exp1 = "5 + 2 / 7"; // 1
        final String exp2 = "(5 + 2) / 7"; // 1
        final String exp3 = "((1 + 5 - (3 + 3)) + 6) / 2"; // (0 + 6) / 2 = 3
        final String exp4 = "(5 + 2) * (1 + 2)"; // 21

        BTree<Expression> tree1 = parser.reset().parse(exp1, 0, exp1.length()).firstLeft();
        BTree<Expression> tree2 = parser.reset().parse(exp2, 0, exp2.length()).firstLeft();
        BTree<Expression> tree3 = parser.reset().parse(exp3, 0, exp3.length()).firstLeft();
        BTree<Expression> tree4 = parser.reset().parse(exp4, 0, exp4.length()).firstLeft();

        Operand res1 = (Operand) EvalExp.eval(tree1, tree1.getParent());
        Operand res2 = (Operand) EvalExp.eval(tree2, tree2.getParent());
        Operand res3 = (Operand) EvalExp.eval(tree3, tree3.getParent());
        Operand res4 = (Operand) EvalExp.eval(tree4, tree4.getParent());

        Assert.assertEquals(1L, res1.getValue());
        Assert.assertEquals(1L, res2.getValue());
        Assert.assertEquals(3L, res3.getValue());
        Assert.assertEquals(21L, res4.getValue());
    }

}
