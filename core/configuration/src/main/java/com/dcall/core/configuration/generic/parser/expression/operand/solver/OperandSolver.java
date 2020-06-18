package com.dcall.core.configuration.generic.parser.expression.operand.solver;

public interface OperandSolver {
    <T> T solve(CharSequence seq);
}
