package com.dcall.core.configuration.utils;

public final class HelpUtils {
    private static final String HELP_DIR = "help/";
    private static final String FILE_EXT = ".help";

    public static String getHelpPath(final String name) {
        return HELP_DIR + name + FILE_EXT;
    }
}
