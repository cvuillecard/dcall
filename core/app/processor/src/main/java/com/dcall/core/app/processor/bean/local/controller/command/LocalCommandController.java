package com.dcall.core.app.processor.bean.local.controller.command;

import com.dcall.core.app.processor.bean.local.action.command.LocalCommandAction;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

@Component
@Scope(SCOPE_SINGLETON)
public class LocalCommandController {
    public final String INPUT_SEPARATOR = " ";
    private String[] input;

    public byte[] execute(final String input) {
        this.input = input.trim().toLowerCase().split(INPUT_SEPARATOR);

        if (input.length() <= 0)
            return ("Command "  + input + " doesn't exists.").getBytes();

        final LocalCommandAction cmd = LocalCommandAction.valueOf(this.input[0]);
        final String[] params = Arrays.copyOfRange(this.input, 1, input.length());

        if (cmd != null)
            return cmd.getAction().execute(params);
        return LocalCommandAction.help.getAction().execute(params);
    }
}
