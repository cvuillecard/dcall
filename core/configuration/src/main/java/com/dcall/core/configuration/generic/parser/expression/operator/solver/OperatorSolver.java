package com.dcall.core.configuration.generic.parser.expression.operator.solver;

import com.dcall.core.configuration.generic.parser.expression.Expression;
import com.dcall.core.configuration.generic.parser.expression.operand.Operand;
import com.dcall.core.configuration.generic.parser.expression.operator.Operator;

/**
 * Definition of what is an operator
 *
 * @see com.dcall.core.configuration.generic.parser.expression.Expression
 * @see com.dcall.core.configuration.generic.parser.expression.operator.Operator
 * @see com.dcall.core.configuration.generic.parser.expression.operand.Operand
 * @see com.dcall.core.configuration.generic.parser.Parser
 */
public interface OperatorSolver {
    /**
     * resolve the operand with the behaviour defined in the method's implementation
     *
     * @param operand
     * @param <T>
     * @return the result produced by the method as byte array
     */
    <T> byte[] execute(final Operand<T> operand);

    /**
     * solve the operator given, and produces an expression which generally is considered as an Operand
     *
     * @param operator
     * @return an expression containing the value produced by the behaviour implemented in the method
     */
    Expression solve(final Operator operator);
}
