package com.dcall.core.configuration.app.service.builtin.help;

import com.dcall.core.configuration.generic.service.command.AbstractCommand;
import com.dcall.core.configuration.utils.HelpUtils;

public class BuiltInHelpServiceImpl extends AbstractCommand implements BuiltInHelpService {
    @Override
    public byte[] execute(final String... params) {
        return init(getRuntimeContext(), HelpUtils.getBuiltInHelp(params[0])).usage();
    }

    @Override
    public byte[] execute() {
        return init(getRuntimeContext(), HelpUtils.getHelpPath(HelpUtils.HELP)).usage();
    }
}
