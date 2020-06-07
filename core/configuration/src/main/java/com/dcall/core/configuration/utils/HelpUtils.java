package com.dcall.core.configuration.utils;

public final class HelpUtils {
    public static final String HELP = "help";
    private static final String HELP_DIR = HELP + "/";
    private static final String BUILTIN_DIR = "builtin/";
    private static final String FILE_EXT = "." + HELP;

    public static String getHelpPath(final String name) {
        return HELP_DIR + name + FILE_EXT;
    }

    public static String getBuiltInHelp(final String name) {
        return HELP_DIR + BUILTIN_DIR + name + FILE_EXT;
    }
}
