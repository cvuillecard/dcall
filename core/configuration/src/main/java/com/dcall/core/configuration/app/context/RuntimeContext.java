package com.dcall.core.configuration.app.context;

import com.dcall.core.configuration.app.context.cluster.ClusterContext;
import com.dcall.core.configuration.app.context.data.DataContext;
import com.dcall.core.configuration.app.context.service.ServiceContext;

public class RuntimeContext {
    private final ClusterContext clusterContext = new ClusterContext();
    private final DataContext dataContext = new DataContext();
    private final ServiceContext serviceContext = new ServiceContext();

    public ClusterContext clusterContext() { return clusterContext; }
    public DataContext dataContext() { return dataContext; }
    public ServiceContext serviceContext() { return serviceContext; }
}
