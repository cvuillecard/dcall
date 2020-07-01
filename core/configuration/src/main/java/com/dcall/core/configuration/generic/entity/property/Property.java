package com.dcall.core.configuration.generic.entity.property;

import java.io.Serializable;
import java.util.Properties;

public interface Property extends Serializable {
    Properties getProperties();
    Property setProperties(final Properties properties);
}
