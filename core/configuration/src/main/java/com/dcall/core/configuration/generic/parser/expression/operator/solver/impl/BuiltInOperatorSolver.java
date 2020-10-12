package com.dcall.core.configuration.generic.parser.expression.operator.solver.impl;

import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.generic.parser.ASCII;
import com.dcall.core.configuration.generic.parser.expression.Expression;
import com.dcall.core.configuration.generic.parser.expression.operand.Operand;
import com.dcall.core.configuration.generic.parser.expression.operator.Operator;
import com.dcall.core.configuration.generic.parser.expression.operator.solver.OperatorSolver;
import com.dcall.core.configuration.generic.service.command.GenericCommandService;

public final class BuiltInOperatorSolver implements OperatorSolver {
    private final RuntimeContext runtimeContext;

    private BuiltInOperatorSolver() { this.runtimeContext = null; }
    public BuiltInOperatorSolver(final RuntimeContext runtimeContext) { this.runtimeContext = runtimeContext; }

    @Override
    public byte[] execute(final Operand operand) {
        return ((GenericCommandService)operand.getValue()).setContext(runtimeContext).run();
    }

    @Override
    public Expression solve(final Operator operator) {
        final Operand left = (Operand) operator.getLeft();
        final Operand right = (Operand) operator.getRight();

        if (left.getValue() instanceof GenericCommandService && right.getValue() instanceof GenericCommandService) {
            final Operand result = new Operand<byte[]>();

            switch (operator.getValue().toString()) {
                case "&&" : result.setValue(solveLogicalAnd(left, right)); break;
                case "&" : result.setValue(solveLogicalAnd(left, right)); break;
                case "||" : result.setValue(solveLogicalOr(left, right)); break;
                case "|" : result.setValue(solveLogicalOr(left, right)); break;
                case "+" : result.setValue(solveLogicalAnd(left, right)); break;
                default : break;
            }
            return result;
        }

        return new ArithmeticOperatorSolver().solve(operator);
    }

    // '&&' logical <AND>
    public byte[] solveLogicalAnd(final Operand<GenericCommandService> left, final Operand<GenericCommandService> right) {
        final byte[] lres = execute(left);

        if (lres != null) {
            final byte[] rres = execute(right);
            if (rres != null) {
                return (new String(lres) + (char) ASCII.new_line + new String(rres)).getBytes();
            }
        }

        return null;
    }

    // '||' logical <OR>
    public byte[] solveLogicalOr(final Operand<GenericCommandService> left, final Operand<GenericCommandService> right) {
        final byte[] lres = execute(left);

        if (lres != null)
            return lres;
        else
            return execute(right);
    }
}
