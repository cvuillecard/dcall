package com.dcall.core.configuration.generic.service.command;

import com.dcall.core.configuration.app.context.RuntimeContext;
import org.eclipse.jgit.revwalk.RevCommit;

public interface GenericCommandService {
    GenericCommandService init(final RuntimeContext context, final String helpFile);
    byte[] usage();
    String commitMessage(final String msg);
    RevCommit commit(final String msg);

    byte[] run(final String... params);
    byte[] run();

    byte[] handleException(Exception e);

    // getter
    RuntimeContext getContext();
    String getHelp();

    String[] getParams();

    byte[] getDatas();

    // setter
    GenericCommandService setContext(final RuntimeContext context);
    GenericCommandService setHelp(final String helpFile);
    GenericCommandService setParams(String[] params);
    GenericCommandService setDatas(byte[] datas);

}
