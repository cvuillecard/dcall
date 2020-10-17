package com.dcall.core.configuration.app.service.builtin.send;

import com.dcall.core.configuration.generic.service.command.AbstractCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

// TODO : skeleton only : not implemented
public final class BuiltInSendServiceImpl extends AbstractCommand implements BuiltInSendService {
    final static Logger LOG = LoggerFactory.getLogger(BuiltInSendServiceImpl.class);
    final String SRC_OPT = "-src";
    final String DEST_OPT = "-dest";
    final List<String> srcPaths = new ArrayList<>();
    String destPath = ".";

    @Override
    public byte[] execute(final String... params) {
        return send(params);
    }

    @Override
    public byte[] execute() {
        return send(null);
    }

    byte[] send(final String... params) {
        if (params != null && params.length > 3) {
            if (!getRuntimeContext().serviceContext().serviceProvider().environService().getInterpretMode(getRuntimeContext()))
                return usage();
            initParams(params);
            return send();
        }
        else
            return usage();

    }

    private void initParams(final String[] params) {
        for (int i = 0; i < params.length; i++)
            switch (params[i]) {
                case SRC_OPT : {
                    while (params[i].charAt(0) != '-' && i < params.length) srcPaths.add(params[i++]);
                    break;
                }
                case DEST_OPT : {
                    if (i + 1 < params.length)
                        destPath = params[++i];
                    break;
                }
                default: break;
            }
    }

    // TODO : not implemented
    private byte[] send() {
        if (!srcPaths.isEmpty() && destPath != null) {

        }
        return usage();
    }
}
