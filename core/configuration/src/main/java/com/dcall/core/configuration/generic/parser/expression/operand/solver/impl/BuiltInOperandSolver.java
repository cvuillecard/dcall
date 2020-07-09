package com.dcall.core.configuration.generic.parser.expression.operand.solver.impl;

import com.dcall.core.configuration.app.constant.BuiltInAction;
import com.dcall.core.configuration.generic.parser.ASCII;
import com.dcall.core.configuration.generic.parser.IterStringUtils;
import com.dcall.core.configuration.generic.parser.expression.operand.solver.OperandSolver;
import com.dcall.core.configuration.utils.HelpUtils;
import com.dcall.core.configuration.utils.StringParserUtils;
import com.dcall.core.configuration.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BuiltInOperandSolver implements OperandSolver {
    private static final Logger LOG = LoggerFactory.getLogger(BuiltInOperandSolver.class);

    @Override
    public <T> T solve(final CharSequence seq) {
        final int lastIdx = IterStringUtils.iterFront(seq, 0, c -> !ASCII.isBlank(c));
        final String cmdName = seq.subSequence(0, lastIdx).toString();
        try {
            final BuiltInAction cmd = BuiltInAction.valueOf(cmdName);

            if (cmd != null) {
                final int nextIdx = IterStringUtils.iterFront(seq, lastIdx, c -> ASCII.isBlank(c));
                final String[] args = nextIdx > lastIdx ? getCmdArgs(seq.subSequence(nextIdx, seq.length())) : null;
                return (T) cmd.getService().setHelp(HelpUtils.getBuiltInHelp(cmdName)).setParams(args);
            }
        }
        catch (Exception e) {
            LOG.debug(e.getMessage());
        }

        return new StringOperandSolver().solve(seq);
    }

    public String[] getCmdArgs(final CharSequence seq) {
        final char equalsToken = '=';
        final Predicate<Character> isDelimiter = d -> d == '\'' || d == '"';
        final int endOptionIdx = IterStringUtils.iterFront(seq, 0, c -> !ASCII.isSpace(c));
        final String option = seq.subSequence(0, endOptionIdx).toString();
        final CharSequence optionArgs = seq.subSequence(endOptionIdx, seq.length());
        List<String> args = StringParserUtils.parseKeyValueToList(optionArgs, 0, optionArgs.length(), equalsToken, isDelimiter);

        if(!args.isEmpty())
            args.add(0, option);
        else
            args = StringParserUtils.parseWordToList(seq, 0, seq.length(), isDelimiter);

        return args.toArray(new String[args.size()]);
    }
}
