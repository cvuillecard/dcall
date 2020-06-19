package com.dcall.core.configuration.app.service.builtin.identity;

import com.dcall.core.configuration.generic.entity.identity.Identity;
import com.dcall.core.configuration.generic.service.command.GenericCommandService;

public interface BuiltInIdentityService extends GenericCommandService {
    Identity getUserIdentity();
    byte[] getUserIdentity(final String... keys);
    byte[] setUserIdentity(final String... args);
    byte[] identity(final String... params);
    String entryToString(final String k, final String v);
    String usageSet();
    String usageDel();
}
