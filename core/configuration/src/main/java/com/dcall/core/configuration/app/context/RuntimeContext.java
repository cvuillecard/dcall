package com.dcall.core.configuration.app.context;

import com.dcall.core.configuration.app.context.cluster.ClusterContext;
import com.dcall.core.configuration.app.context.data.DataContext;
import com.dcall.core.configuration.app.context.service.ServiceContext;
import com.dcall.core.configuration.app.context.system.SystemContext;
import com.dcall.core.configuration.app.context.user.UserContext;

import java.io.Serializable;

public class RuntimeContext implements Serializable {
    private final UserContext userContext = new UserContext();
    private final SystemContext systemContext = new SystemContext();
    private final DataContext dataContext = new DataContext();
    private final ClusterContext clusterContext = new ClusterContext(userContext);
    private final ServiceContext serviceContext = new ServiceContext();

    public UserContext userContext() { return userContext; }
    public SystemContext systemContext() { return systemContext; }
    public DataContext dataContext() { return dataContext; }
    public ClusterContext clusterContext() { return clusterContext; }
    public ServiceContext serviceContext() { return serviceContext; }
}
