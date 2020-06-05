package com.dcall.core.app.processor.bean.local.controller.command;

import com.dcall.core.app.processor.bean.local.action.command.LocalCommandAction;
import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.utils.HelpUtils;

import java.util.Arrays;

public final class LocalCommandController {
    public final String INPUT_SEPARATOR = " ";
    private RuntimeContext runtimeContext;
    private String[] input;

    public byte[] execute(final RuntimeContext runtimeContext, final String input) {
        this.runtimeContext = runtimeContext;
        this.input = input.trim().toLowerCase().split(INPUT_SEPARATOR);

        if (input.length() <= 0)
            return ("Command "  + input + " doesn't exists.").getBytes();

        final String cmdName = this.input[0];
        final LocalCommandAction cmd = LocalCommandAction.valueOf(cmdName);
        final String[] params = Arrays.copyOfRange(this.input, 1, input.length());

        if (cmd != null) {
            return cmd.getAction()
                    .init(runtimeContext, HelpUtils.getHelpPath(cmdName))
                    .execute(params);
        }
        return LocalCommandAction.help.getAction().usage();
    }
}
