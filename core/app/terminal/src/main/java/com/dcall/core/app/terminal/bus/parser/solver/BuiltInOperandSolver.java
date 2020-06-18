package com.dcall.core.app.terminal.bus.parser.solver;

import com.dcall.core.configuration.app.constant.BuiltInAction;
import com.dcall.core.configuration.generic.parser.ASCII;
import com.dcall.core.configuration.generic.parser.IterStringUtils;
import com.dcall.core.configuration.generic.parser.expression.operand.solver.OperandSolver;
import com.dcall.core.configuration.generic.parser.expression.operand.solver.StringOperandSolver;
import com.dcall.core.configuration.utils.HelpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BuiltInOperandSolver implements OperandSolver {
    private static final Logger LOG = LoggerFactory.getLogger(BuiltInOperandSolver.class);

    @Override
    public <T> T solve(final CharSequence seq) {
        final int endIdx = IterStringUtils.iterFront(seq, 0, c -> !ASCII.isBlank(c));
        final String cmdName = seq.subSequence(0, endIdx).toString();
        try {
            final BuiltInAction cmd = BuiltInAction.valueOf(cmdName);

            if (cmd != null) {
                final int nextArgIdx = IterStringUtils.iterFront(seq, endIdx, c -> ASCII.isBlank(c));
                final String[] args = nextArgIdx > endIdx ? seq.subSequence(nextArgIdx, seq.length()).toString().split(" ") : null;
                return (T) cmd.getService().setHelp(HelpUtils.getBuiltInHelp(cmdName)).setParams(args);
            }
        }
        catch (Exception e) {
            LOG.debug(e.getMessage());
        }

        return new StringOperandSolver().solve(seq);
    }
}
