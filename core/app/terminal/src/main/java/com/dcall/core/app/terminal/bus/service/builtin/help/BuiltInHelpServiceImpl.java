package com.dcall.core.app.terminal.bus.service.builtin.help;

import com.dcall.core.configuration.generic.service.command.AbstractCommand;
import com.dcall.core.configuration.utils.HelpUtils;

public class BuiltInHelpServiceImpl extends AbstractCommand implements BuiltInHelpService {
    @Override
    public byte[] execute(final String... params) {
        return init(getContext(), HelpUtils.getBuiltInHelp(params[0])).usage();
    }

    @Override
    public byte[] execute() {
        return init(getContext(), HelpUtils.getHelpPath(HelpUtils.HELP)).usage();
    }
}
