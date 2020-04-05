package com.dcall.core.app.processor.bean.local.action.command;

import com.dcall.core.app.processor.bean.local.service.command.LocalHelpCommandServiceImpl;
import com.dcall.core.configuration.service.GenericCommandService;

public enum LocalCommandAction {
    help(new LocalHelpCommandServiceImpl());

    private GenericCommandService action;

    LocalCommandAction(final GenericCommandService action) {
        this.action = action;
    }

    public GenericCommandService getAction() { return this.action; }
}
