package com.dcall.core.configuration.generic.service.command;

import com.dcall.core.configuration.app.context.RuntimeContext;

public interface GenericCommandService {
    RuntimeContext getContext();
    String getHelp();
    byte[] getDatas();

    byte[] usage();
    byte[] execute(final String... params);
    GenericCommandService init(final RuntimeContext context, final String helpFile);
}
