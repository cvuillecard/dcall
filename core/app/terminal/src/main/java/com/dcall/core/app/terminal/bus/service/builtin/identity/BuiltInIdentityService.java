package com.dcall.core.app.terminal.bus.service.builtin.identity;

import com.dcall.core.configuration.generic.entity.identity.Identity;
import com.dcall.core.configuration.generic.service.command.GenericCommandService;

public interface BuiltInIdentityService extends GenericCommandService {
    Identity getUserIdentity();
    byte[] getUserProperties(final String... keys);
    byte[] setUserProperties(final String... args);
    byte[] identity(final String... params);
    String entryToString(final String k, final String v);
    String usageSet();
}