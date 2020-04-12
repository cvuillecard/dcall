package com.dcall.core.configuration.utils;

public final class URIUtils {
    public static String getUri(final String baseUri, final String domainUri) {
        return baseUri + '/' + domainUri;
    }
}
