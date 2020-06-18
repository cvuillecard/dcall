package com.dcall.core.configuration.app.service.builtin.snapshot;

import com.dcall.core.configuration.generic.service.command.GenericCommandService;

public interface BuiltInSnapshotService extends GenericCommandService {
    byte[] snapshot(final String msg);
}
