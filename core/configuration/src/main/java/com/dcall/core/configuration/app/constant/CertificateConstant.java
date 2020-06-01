package com.dcall.core.configuration.app.constant;

public class CertificateConstant {
    // validity
    public static final Long INFINITE_VALIDITY = Long.MAX_VALUE / 1000000;
    public static final Long ONE_YEAR = 365L;
    public static final Long ONE_CENTURY = 36500L;
    public static final Long ONE_MILLENIUM = 365000L;

    // domain
    public static final String CERT_PUB_DOMAIN = "public";
    public static final String CERT_PRIV_DOMAIN = "private";

    // keystore
    public static final String DEFAULT_KEY_PASS = "storePwd";
    public static final String DEFAULT_STORE_PASS = "storePwd";
}
