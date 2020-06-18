package com.dcall.core.configuration.app.service.builtin.env;

import com.dcall.core.configuration.generic.service.command.GenericCommandService;

public interface BuiltInEnvService extends GenericCommandService {
    byte[] getUserEnv(String... keys);

    byte[] setUserEnv(String... args);

    byte[] env(String... params);

    String entryToString(String k, String v);

    String usageSet();
}
