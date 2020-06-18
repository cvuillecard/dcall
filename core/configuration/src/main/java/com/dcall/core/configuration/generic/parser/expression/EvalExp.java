package com.dcall.core.configuration.generic.parser.expression;

import com.dcall.core.configuration.generic.parser.expression.operator.Operator;
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
                if (((Operator)ptr.getData()).getRight().getType().equals(ExpressionType.OPERATOR))
                    return eval(ptr.getRight().firstLeft(), ptr);
                updateParent(parent, ptr, (exp = ((Operator) ptr.getData()).solve()));
            }
            ptr = ptr.getParent();
        }

        return exp == null ? tree.getData() : exp;
    }

    private static void updateParent(final BTree<Expression> parent, final BTree<Expression> ptr, final Expression exp) {
        if (ptr.getParent() != null) {
            final Operator operator = (Operator) ptr.getParent().getData();
            if (ptr.getParent() != parent) {
                if (operator.getLeft().getType().equals(ExpressionType.OPERATOR))
                    operator.setLeft(exp);
                else
                    operator.setRight(exp);
            }
            else
                operator.setRight(exp);
        }
    }

}
