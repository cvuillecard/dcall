package com.dcall.core.configuration.generic.service.command;

import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.context.WithRuntimeContext;
import org.eclipse.jgit.revwalk.RevCommit;

public interface GenericCommandService extends WithRuntimeContext {
    GenericCommandService init(final RuntimeContext runtimeContext, final String helpFile);
    byte[] usage();
    String commitMessage(final String msg);
    RevCommit commit(final String msg);

    byte[] run(final String... params);
    byte[] run();

    byte[] handleException(Exception e);

    // getter
    RuntimeContext getRuntimeContext();
    String getHelp();

    String[] getParams();

    byte[] getDatas();

    // setter
    GenericCommandService setRuntimeContext(final RuntimeContext context);
    GenericCommandService setHelp(final String helpFile);
    GenericCommandService setParams(String[] params);
    GenericCommandService setDatas(byte[] datas);

}
