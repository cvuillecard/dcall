package com.dcall.core.configuration.utils.parser.expression.operator;

import com.dcall.core.configuration.utils.parser.expression.Expression;
import com.dcall.core.configuration.utils.parser.expression.ExpressionType;
import com.dcall.core.configuration.utils.parser.expression.operand.Operand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class Operator extends Expression {
    private static final Logger LOG = LoggerFactory.getLogger(Operator.class);
    private CharSequence value;
    private OperatorType operatorType;
    private Expression left;
    private Expression right;
    private Function<Operator, Expression> resolver;

    public Operator() { super(ExpressionType.OPERATOR); }
    public Operator(final CharSequence value) { super(ExpressionType.OPERATOR); this.value = value; }
    public Operator(final CharSequence value, final OperatorType operatorType) { super(ExpressionType.OPERATOR); this.value = value; this.operatorType = operatorType; }

    public Operator(final CharSequence value, final OperatorType operatorType, final Function<Operator, Expression> resolver) {
        super(ExpressionType.OPERATOR);
        this.value = value;
        this.operatorType = operatorType;
        this.resolver = resolver;
    }

    public Expression run() {
        if (this.left instanceof Operand && this.right instanceof Operand) {
            Operand left = (Operand)this.getLeft();
            Operand right = (Operand) this.getRight();
            Operand result = new Operand<>();
            switch (this.getValue().toString()) {
                case "+" : result.setValue((Long)left.getValue() + (Long)right.getValue()); break;
                case "-" : result.setValue((Long)left.getValue() - (Long)right.getValue()); break;
                case "*" : result.setValue((Long)left.getValue() * (Long)right.getValue()); break;
                case "/" : result.setValue((Long)left.getValue() / (Long)right.getValue()); break;
                default : break;
            }
            return result;
        }
        else
            LOG.debug("Cannot run operator with a left operator as left operand or an operator as right operand");
//        if (left != null && right != null && resolver != null) {
//            return resolver.apply(this);
//        }
//
//        debugOperand();


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
    public Function<Operator, Expression> getResolver() { return resolver; }

    // setter
    public Operator setValue(final CharSequence value) { this.value = value; return this; }
    public Operator setOperatorType(final OperatorType operatorType) { this.operatorType = operatorType; return this; }
    public Operator setLeft(final Expression left) { this.left = left; return this; }
    public Operator setRight(final Expression right) { this.right = right; return this;}
    public Operator setResolver(final Function<Operator, Expression> resolver) { this.resolver = resolver; return this; }
}
