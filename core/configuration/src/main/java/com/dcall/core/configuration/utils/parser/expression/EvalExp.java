package com.dcall.core.configuration.utils.parser.expression;

import com.dcall.core.configuration.utils.parser.expression.operator.Operator;
import com.dcall.core.configuration.utils.tree.BTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EvalExp {
    private static final Logger LOG = LoggerFactory.getLogger(EvalExp.class);

    public static Expression eval(final BTree<Expression> tree, final BTree<Expression> parent) {
        BTree<Expression> ptr = tree;
        Expression exp = null;

        while (ptr != null) {
            if (ptr.getData().getType().equals(ExpressionType.OPERATOR)) {
                if (((Operator)ptr.getData()).getRight().getType().equals(ExpressionType.OPERATOR)) {
                    return eval(ptr.getRight().firstLeft(), ptr);
                }
                exp = ((Operator) ptr.getData()).run();
                if (ptr.getParent() != null) {
                    if (ptr.getParent() != parent)
                        ((Operator) ptr.getParent().getData()).setLeft(exp);
                    else
                        ((Operator) ptr.getParent().getData()).setRight(exp);
                }
            }
            ptr = ptr.getParent();
        }

        return exp;
    }

}
