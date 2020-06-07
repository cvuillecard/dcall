package com.dcall.core.configuration.generic.service.command;

import com.dcall.core.configuration.app.context.RuntimeContext;
import org.eclipse.jgit.revwalk.RevCommit;

public interface GenericCommandService {
    GenericCommandService init(final RuntimeContext context, final String helpFile);
    byte[] usage();
    String commitMessage(final String msg);
    RevCommit commit(final String msg);

    byte[] run(final String... params);
    byte[] handleException(Exception e);

    RuntimeContext getContext();
    String getHelp();
    byte[] getDatas();

}
