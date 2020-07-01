package com.dcall.core.configuration.generic.parser.expression.operator.solver.impl;

import com.dcall.core.configuration.generic.parser.expression.Expression;
import com.dcall.core.configuration.generic.parser.expression.operand.Operand;
import com.dcall.core.configuration.generic.parser.expression.operator.Operator;
import com.dcall.core.configuration.generic.parser.expression.operator.solver.OperatorSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ArithmeticOperatorSolver implements OperatorSolver {
    private static final Logger LOG = LoggerFactory.getLogger(ArithmeticOperatorSolver.class);

    @Override
    public <T> byte[] execute(Operand<T> operand) {
        return (byte[]) operand.getValue();
    }

    @Override
    public Expression solve(Operator operator) {
        if (operator.getLeft() instanceof Operand && operator.getRight() instanceof Operand) {

            final Operand left = (Operand) operator.getLeft();
            final Operand right = (Operand) operator.getRight();
            final Operand result = new Operand<>();

            switch (operator.getValue().toString()) {
                case "+" : result.setValue((Long)left.getValue() + (Long)right.getValue()); break;
                case "-" : result.setValue((Long)left.getValue() - (Long)right.getValue()); break;
                case "*" : result.setValue((Long)left.getValue() * (Long)right.getValue()); break;
                case "/" : result.setValue((Long)left.getValue() / (Long)right.getValue()); break;
                case "%" : result.setValue((Long)left.getValue() % (Long)right.getValue()); break;
                default : break;
            }
            return result;
        }
        else
            LOG.debug("Cannot run operator with a left operator as left operand or an operator as right operand");

        return operator;
    }
}
