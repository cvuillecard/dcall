package com.dcall.core.configuration.generic.parser.expression.operand.solver;

/**
 * Definition of what is an operand
 *
 * @see com.dcall.core.configuration.generic.parser.Parser
 */
public interface OperandSolver {
    /**
     * Defines and interpret a char sequence as an object of type T
     *
     * @param seq
     * @param <T>
     * @return an object of type T as the operand's value
     */
    <T> T solve(final CharSequence seq);
}
