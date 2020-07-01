package com.dcall.core.app.terminal.bus.parser.solver;

import com.dcall.core.configuration.app.constant.BuiltInAction;
import com.dcall.core.configuration.generic.parser.ASCII;
import com.dcall.core.configuration.generic.parser.IterStringUtils;
import com.dcall.core.configuration.generic.parser.expression.operand.solver.OperandSolver;
import com.dcall.core.configuration.generic.parser.expression.operand.solver.impl.StringOperandSolver;
import com.dcall.core.configuration.utils.HelpUtils;
import com.dcall.core.configuration.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                final String[] args = nextArgIdx > endIdx ? getCmdArgs(seq.subSequence(nextArgIdx, seq.length())) : null;
                return (T) cmd.getService().setHelp(HelpUtils.getBuiltInHelp(cmdName)).setParams(args);
            }
        }
        catch (Exception e) {
            LOG.debug(e.getMessage());
        }

        return new StringOperandSolver().solve(seq);
    }

    public String[] getCmdArgs(final CharSequence seq) {
        final List<String> cmdArgs = new ArrayList<>();
        final int endOptionIdx = IterStringUtils.iterFront(seq, 0, c -> !ASCII.isSpace(c));
        final String option = seq.subSequence(0, endOptionIdx).toString();
        final Pattern pattern = Pattern.compile("([\\w-]+)=((.*?)([ \\w-]+)(.*?)\\3)");
        final Matcher matcher = pattern.matcher(seq.toString());

        while (matcher.find()) {
            final String keyValue = matcher.group(1) + '=' + matcher.group(2);
            cmdArgs.add(keyValue);
            LOG.debug(keyValue);
        }
        if(!cmdArgs.isEmpty())
            cmdArgs.add(0, option);

        return cmdArgs.isEmpty() ? StringUtils.trim(seq).toString().split(" ") : cmdArgs.toArray(new String[cmdArgs.size()]);
    }
}
