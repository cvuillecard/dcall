package com.dcall.core.configuration.app.context;

import com.dcall.core.configuration.app.context.cluster.ClusterContext;
import com.dcall.core.configuration.app.context.data.DataContext;
import com.dcall.core.configuration.app.context.service.ServiceContext;
import com.dcall.core.configuration.app.context.system.SystemContext;
import com.dcall.core.configuration.app.context.user.UserContext;

public class RuntimeContext {
    private final ClusterContext clusterContext = new ClusterContext();
    private final DataContext dataContext = new DataContext();
    private final UserContext userContext = new UserContext();
    private final SystemContext systemContext = new SystemContext();
    private final ServiceContext serviceContext = new ServiceContext();

    public ClusterContext clusterContext() { return clusterContext; }
    public DataContext dataContext() { return dataContext; }
    public UserContext userContext() { return userContext; }
    public ServiceContext serviceContext() { return serviceContext; }
}
