package com.dcall.core.configuration.app.constant;

public final class EnvironConstant {
    // Extern
    public static final String RUNTIME_CONF_PATH = "extern.runtime.conf.path";
    public static final String RUNTIME_CONF_NAME = "extern.runtime.conf.name";

    // User
    public static final String USER_HOME = "user_home";
    public static final String USER_CONF = "user_conf";
    public static String USER_WORKSPACE = "user_workspace";
    public static final String USER_PROP = "user_prop";
    public static final String USER_CERT = "user_cert";
    public static final String USER_IDENTITY_PROP = "user_identity_prop";
    public static final String COMMIT_MODE = "auto_commit";
    public static final String INTERPRET_MODE = "local_interpret";
    public static final String PUBLIC_ID = "public_id";

    public static final String USER_PROP_FILENAME = "env.properties";
    public static String USER_IDENTITY_FILENAME = "identity.properties";
    public static String USER_KEYSTORE_FILENAME = "user_keystore.p12";

    public static String SYSTEM_MAIL_DOMAIN = "dcall.system.user";
    public static String SYSTEM_LOGIN = "system";

    // allow properties
    public static String ALLOW_HOST_FILES = "allow_host_files";

    // paths
    public static String HOST_FILES_DIR = "host_files_dir";
    public static String HOST_FILES_DIR_NAME = "host";
}
