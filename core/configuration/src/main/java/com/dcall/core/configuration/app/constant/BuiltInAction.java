package com.dcall.core.configuration.app.constant;

import com.dcall.core.configuration.app.service.builtin.env.BuiltInEnvServiceImpl;
import com.dcall.core.configuration.app.service.builtin.help.BuiltInHelpServiceImpl;
import com.dcall.core.configuration.app.service.builtin.identity.BuiltInIdentityServiceImpl;
import com.dcall.core.configuration.app.service.builtin.publish.BuiltInPublishServiceImpl;
import com.dcall.core.configuration.app.service.builtin.snapshot.BuiltInSnapshotServiceImpl;
import com.dcall.core.configuration.generic.service.command.GenericCommandService;

import java.util.function.Supplier;

public enum BuiltInAction {
    help(() -> new BuiltInHelpServiceImpl()),
    env(() -> new BuiltInEnvServiceImpl()),
    identity(() -> new BuiltInIdentityServiceImpl()),
    snapshot(() -> new BuiltInSnapshotServiceImpl()),
    publish(() -> new BuiltInPublishServiceImpl());

    private Supplier<GenericCommandService> service;

    BuiltInAction(final Supplier<GenericCommandService> service) {
        this.service = service;
    }
    public GenericCommandService getService() { return this.service.get(); }
}
