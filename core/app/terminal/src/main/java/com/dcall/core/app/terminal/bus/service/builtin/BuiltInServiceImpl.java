package com.dcall.core.app.terminal.bus.service.builtin;

import com.dcall.core.app.terminal.bus.constant.BuiltInAction;
import com.dcall.core.configuration.generic.service.command.AbstractCommand;
import com.dcall.core.configuration.utils.HelpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public final class BuiltInServiceImpl extends AbstractCommand implements BuiltInService {
    private static final Logger LOG = LoggerFactory.getLogger(BuiltInServiceImpl.class);

    @Override
    public byte[] execute(final String... params) {
        try {
            final String cmdName = params[0];
            final BuiltInAction cmd = BuiltInAction.valueOf(cmdName);
            final String[] args = Arrays.copyOfRange(params, 1, params.length);

            if (cmd != null) {
                return cmd.getAction()
                        .init(getContext(), HelpUtils.getBuiltInHelp(cmdName))
                        .run(args);
            }
        }
        catch (Exception e) {
            LOG.debug(e.getMessage());
            return execute();
        }
        return execute();
    }

    @Override
    public byte[] execute() {
        return null;
    }
}
