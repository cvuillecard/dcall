package com.dcall.core.configuration.context;

import com.dcall.core.configuration.context.cluster.ClusterContext;
import com.dcall.core.configuration.context.data.DataContext;

public class RuntimeContext {
    private final DataContext dataContext = new DataContext();
    private final ClusterContext clusterContext = new ClusterContext();

    public ClusterContext clusterContext() { return clusterContext; }
    public DataContext dataContext() { return dataContext; }
}
