package com.dcall.core.configuration.utils;

import com.dcall.core.configuration.security.hash.HashProvider;

public final class URIUtils {
    private static String context;

    public static String getUri(final String baseUri, final String domainUri) {
        return baseUri + '/' + domainUri;
    }

    public static String getContextUri(final String baseUri, final String domainUri) {
        if (context != null)
            return HashProvider.sign(context, getUri(baseUri, domainUri));
        
        return getUri(baseUri, domainUri);
    }

    public static void setContext(final String seed) {
        if (seed != null && seed.length() > 0)
            context = HashProvider.seed(seed.getBytes());
    }
}
