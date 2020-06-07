package com.dcall.core.configuration.generic.service.command;

import com.dcall.core.configuration.app.context.RuntimeContext;

public interface GenericCommandService {
    RuntimeContext getContext();
    String getHelp();
    byte[] getDatas();

    GenericCommandService init(final RuntimeContext context, final String helpFile);
    byte[] usage();
    byte[] run(final String... params);
    byte[] handleException(Exception e);
}
