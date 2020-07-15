package com.dcall.core.configuration.app.service.builtin.publish;

import com.dcall.core.configuration.generic.service.command.GenericCommandService;

public interface BuiltInPublishService extends GenericCommandService {
    byte[] publish(final String... params);
}
