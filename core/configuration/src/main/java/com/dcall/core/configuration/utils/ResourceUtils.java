package com.dcall.core.configuration.utils;

import com.dcall.core.configuration.constant.ConstantResource;
import com.dcall.core.configuration.exception.TechnicalException;

import java.io.IOException;
import java.util.Properties;

public final class ResourceUtils {
    private static Properties properties = null;

    public static Properties localProperties() {

        if (properties == null) {
            try {
                properties = new Properties();
                properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(ConstantResource.LOCAL_PROPERTIES));
            } catch (IOException e) {
                new TechnicalException(e).log();
            }
        }

        return properties;
    }

    public static String getString(final String key) {
        return localProperties().get(key).toString();
    }

    public static Integer getInt(final String key) {
        return Integer.valueOf(getString(key));
    }

    public static Boolean getBool(final String key) {
        return Boolean.valueOf(getString(key));
    }

    public static Double getDouble(final String key) {
        return Double.parseDouble(getString(key));
    }

    public static void reset() {
        properties = null;
    }
}
