package com.dcall.core.app.terminal.bus.service.builtin.help;

import com.dcall.core.configuration.generic.service.command.AbstractCommand;

public class BuiltInHelpServiceImpl extends AbstractCommand implements BuiltInHelpService {
    @Override
    public byte[] execute(String... params) {
        return usage();
    }

    @Override
    public byte[] execute() {
        return usage();
    }
}
