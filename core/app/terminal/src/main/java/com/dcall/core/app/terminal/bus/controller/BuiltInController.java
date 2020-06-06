package com.dcall.core.app.terminal.bus.controller;

import com.dcall.core.app.terminal.bus.constant.BuiltInAction;
import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.utils.HelpUtils;

import java.util.Arrays;

public final class BuiltInController {
    public final String INPUT_SEPARATOR = " ";
    private RuntimeContext runtimeContext;
    private String[] input;

    public byte[] execute(final RuntimeContext runtimeContext, final String input) {
        this.runtimeContext = runtimeContext;
        this.input = input.trim().toLowerCase().split(INPUT_SEPARATOR);

        if (input.length() <= 0)
            return ("Built-in " + input + " doesn't exists.").getBytes();

        final String cmdName = this.input[0];
        final BuiltInAction cmd = BuiltInAction.valueOf(cmdName);
        final String[] params = Arrays.copyOfRange(this.input, 1, input.length());

        if (cmd != null) {
            return cmd.getAction()
                    .init(runtimeContext, HelpUtils.getBuiltInHelp(cmdName, params))
                    .run(params);
        }
        return BuiltInAction.help.getAction()
                .init(runtimeContext, HelpUtils.getHelpPath(HelpUtils.HELP)).usage();
    }
}
