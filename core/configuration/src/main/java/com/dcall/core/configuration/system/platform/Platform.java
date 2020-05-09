package com.dcall.core.configuration.system.platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.dcall.core.configuration.constant.PlatformConstant.*;

public final class Platform {
    private static final Logger LOG = LoggerFactory.getLogger(Platform.class);

    public final static String name;

    static {
        name = System.getProperty(OS_NAME).toLowerCase();
    }

    public static boolean isLinux() {
        return isPlatform(LINUX);
    }

    public static boolean isWin() {
        return isPlatform(WIN);
    }

    public static boolean isMac() {
        return isPlatform(MAC);
    }

    public static boolean isSun() {
        return isPlatform(SUN);
    }

    private static boolean isPlatform(final String platformName) {
        return name.indexOf(platformName) >= 0;
    }

    public static void execute(final Runnable winFunc, final Runnable linuxFunc, final Runnable macFunc) {
        switch (name) {
            case WIN : winFunc.run(); break;
            case LINUX : linuxFunc.run(); break;
            case MAC : macFunc.run(); break;
            default : linuxFunc.run(); break;
        }
    }

    public static void runCmd(final String winCmd, final String linuxCmd, final String macCmd) {
        try {
            String cmd;

            switch (name) {
                case WIN: cmd = winCmd; break;
                case LINUX: cmd = linuxCmd; break;
                case MAC: cmd = macCmd; break;
                default: cmd = linuxCmd; break;
            }

            Runtime.getRuntime().exec(cmd).waitFor();

            LOG.debug(Platform.class.getSimpleName() + " > runtime cmd executed : " + cmd);
        }
        catch (IOException | InterruptedException e) {
            LOG.error(e.getMessage());
        }
    }
}
