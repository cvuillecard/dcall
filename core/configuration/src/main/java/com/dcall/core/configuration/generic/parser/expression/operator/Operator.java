package com.dcall.core.configuration.generic.parser.expression.operator;

import com.dcall.core.configuration.generic.parser.expression.Expression;
import com.dcall.core.configuration.generic.parser.expression.ExpressionType;
import com.dcall.core.configuration.generic.parser.expression.operand.Operand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class Operator extends Expression {
    private static final Logger LOG = LoggerFactory.getLogger(Operator.class);
    private CharSequence value;
    private OperatorType operatorType;
    private Expression left;
    private Expression right;
    private Function<Operator, Expression> solver;

    public Operator() { super(ExpressionType.OPERATOR); }
    public Operator(final CharSequence value) { super(ExpressionType.OPERATOR); this.value = value; }
    public Operator(final CharSequence value, final OperatorType operatorType) { super(ExpressionType.OPERATOR); this.value = value; this.operatorType = operatorType; }

    public Operator(final CharSequence value, final OperatorType operatorType, final Function<Operator, Expression> solver) {
        super(ExpressionType.OPERATOR);
        this.value = value;
        this.operatorType = operatorType;
        this.solver = solver;
    }

    public Expression solve() {
        if (left != null && right != null && solver != null)
            return solver.apply(this);

        return this;
    }

    private void debugOperand() {
        if (left != null)
            LOG.debug(((Operand)left).getValue().toString());
        else
            LOG.debug("Operator -> left NULL");
        if (right != null)
            LOG.debug(((Operand)right).getValue().toString());
        else
            LOG.debug("Operator -> right NULL");
    }

    // getter
    public CharSequence getValue() { return value; }
    public OperatorType getOperatorType() { return operatorType; }
    public Expression getLeft() { return left; }
    public Expression getRight() { return right; }
    public Function<Operator, Expression> getSolver() { return solver; }

    // setter
    public Operator setValue(final CharSequence value) { this.value = value; return this; }
    public Operator setOperatorType(final OperatorType operatorType) { this.operatorType = operatorType; return this; }
    public Operator setLeft(final Expression left) { this.left = left; return this; }
    public Operator setRight(final Expression right) { this.right = right; return this;}
    public Operator setSolver(final Function<Operator, Expression> solver) { this.solver = solver; return this; }
}
