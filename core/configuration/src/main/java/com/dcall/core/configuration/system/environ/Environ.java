package com.dcall.core.configuration.system.environ;

import com.dcall.core.configuration.system.platform.Platform;
import com.dcall.core.configuration.utils.FileUtils;

import java.util.HashMap;
import java.util.Map;

import static com.dcall.core.configuration.constant.EnvironConstant.DCALL_HOME;
import static com.dcall.core.configuration.constant.EnvironConstant.LINUX_HOME_DCALL;
import static com.dcall.core.configuration.constant.EnvironConstant.WINDOWS_HOME_DCALL;

public final class Environ {
    private static final Map<String, String> env = new HashMap<>();

    public static final void init() {
        initHome();
    }

    private static void initHome() {
        String home = getHome();

        if (home == null)
            Platform.execute(
                    () -> setHome(WINDOWS_HOME_DCALL),
                    () -> setHome(LINUX_HOME_DCALL),
                    () -> setHome(LINUX_HOME_DCALL)
            );
        else
            env.put(DCALL_HOME, home);

        home = env.get(DCALL_HOME);

        FileUtils.createDirectory(home);
        FileUtils.lockDelete(home);
    }

    public static final String getHome() {
        return sysEnv().get(DCALL_HOME);
    }

    public static final void setHome(final String path) {
        sysEnv().put(DCALL_HOME, path);
        env.put(DCALL_HOME, path);
    }

    private static Map<String, String> sysEnv() {
        return System.getenv();
    }
}
