package com.dcall.core.configuration.generic.parser.expression.operand.solver.impl;

import com.dcall.core.configuration.generic.parser.ASCII;
import com.dcall.core.configuration.generic.parser.IterStringUtils;
import com.dcall.core.configuration.generic.parser.expression.operand.solver.OperandSolver;

public final class StringOperandSolver implements OperandSolver {
    @Override
    public <T> T solve(final CharSequence seq) {
        if (seq != null && seq.length() > 0) {
            final int numIdx = IterStringUtils.iterFront(seq, 0, seq.length(), c -> ASCII.isNum(c));

            if (numIdx < seq.length())
                return (T) seq.toString();
            else
                return (T) Long.valueOf(seq.toString());
        }
        return null;
    }
}
