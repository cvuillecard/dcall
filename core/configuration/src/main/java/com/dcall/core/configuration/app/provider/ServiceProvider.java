package com.dcall.core.configuration.app.provider;

import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.provider.hash.HashServiceProvider;
import com.dcall.core.configuration.app.provider.message.MessageServiceProvider;
import com.dcall.core.configuration.app.provider.task.TaskServiceProvider;
import com.dcall.core.configuration.app.provider.user.UserServiceProvider;
import com.dcall.core.configuration.app.provider.version.VersionServiceProvider;
import com.dcall.core.configuration.app.service.environ.EnvironService;
import com.dcall.core.configuration.app.service.environ.EnvironServiceImpl;

import java.io.Serializable;

public final class ServiceProvider implements Serializable {
    private final RuntimeContext runtimeContext;
    private final HashServiceProvider hashServiceProvider;
    private final EnvironService environService;
    private final VersionServiceProvider versionServiceProvider;
    private final UserServiceProvider userServiceProvider;
    private final MessageServiceProvider messageServiceProvider;
    private final TaskServiceProvider taskServiceProvider;

    public ServiceProvider(final RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
        this.hashServiceProvider = new HashServiceProvider();
        this.environService = new EnvironServiceImpl(hashServiceProvider);
        this.versionServiceProvider = new VersionServiceProvider(environService);
        this.userServiceProvider = new UserServiceProvider(versionServiceProvider);
        this.messageServiceProvider = new MessageServiceProvider(userServiceProvider);
        this.taskServiceProvider = new TaskServiceProvider(this.runtimeContext);
    }

    public HashServiceProvider hashServiceProvider() { return hashServiceProvider; }
    public EnvironService environService() { return environService; }
    public VersionServiceProvider versionServiceProvider() { return versionServiceProvider; }
    public UserServiceProvider userServiceProvider() { return userServiceProvider; }
    public MessageServiceProvider messageServiceProvider() { return messageServiceProvider; }
    public TaskServiceProvider taskServiceProvider() { return taskServiceProvider; }
}
