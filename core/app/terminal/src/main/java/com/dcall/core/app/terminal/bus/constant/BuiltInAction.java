package com.dcall.core.app.terminal.bus.constant;

import com.dcall.core.app.terminal.bus.service.builtin.env.BuiltInEnvServiceImpl;
import com.dcall.core.app.terminal.bus.service.builtin.help.BuiltInHelpServiceImpl;
import com.dcall.core.app.terminal.bus.service.builtin.identity.BuiltInIdentityServiceImpl;
import com.dcall.core.app.terminal.bus.service.builtin.snapshot.BuiltInSnapshotServiceImpl;
import com.dcall.core.configuration.generic.service.command.GenericCommandService;

public enum BuiltInAction {
    help(new BuiltInHelpServiceImpl()),
    env(new BuiltInEnvServiceImpl()),
    identity(new BuiltInIdentityServiceImpl()),
    snapshot(new BuiltInSnapshotServiceImpl());

    private GenericCommandService action;

    BuiltInAction(final GenericCommandService action) {
        this.action = action;
    }
    public GenericCommandService getAction() { return this.action; }
}
