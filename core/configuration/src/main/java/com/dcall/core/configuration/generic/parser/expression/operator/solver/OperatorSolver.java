package com.dcall.core.configuration.generic.parser.expression.operator.solver;

import com.dcall.core.configuration.generic.parser.expression.Expression;
import com.dcall.core.configuration.generic.parser.expression.operand.Operand;
import com.dcall.core.configuration.generic.parser.expression.operator.Operator;

public interface OperatorSolver {
    <T> byte[] execute(final Operand<T> operand);
    Expression solve(final Operator operator);
}
