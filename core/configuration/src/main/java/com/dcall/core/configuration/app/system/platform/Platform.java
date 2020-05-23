package com.dcall.core.configuration.app.system.platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.dcall.core.configuration.app.constant.PlatformConstant.*;

public abstract class Platform {
    private static final Logger LOG = LoggerFactory.getLogger(Platform.class);

    private final String name = System.getProperty(OS_NAME).toLowerCase();

    protected boolean isLinux() {
        return isPlatform(LINUX);
    }

    protected boolean isWin() {
        return isPlatform(WIN);
    }

    protected boolean isMac() {
        return isPlatform(MAC);
    }

    protected boolean isSun() {
        return isPlatform(SUN);
    }

    protected boolean isPlatform(final String platformName) {
        return name.indexOf(platformName) >= 0;
    }

    protected void execute(final Runnable winFunc, final Runnable linuxFunc, final Runnable macFunc) {
        switch (name) {
            case WIN : winFunc.run(); break;
            case LINUX : linuxFunc.run(); break;
            case MAC : macFunc.run(); break;
            default : linuxFunc.run(); break;
        }
    }

    protected void runCmd(final String winCmd, final String linuxCmd, final String macCmd) {
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
