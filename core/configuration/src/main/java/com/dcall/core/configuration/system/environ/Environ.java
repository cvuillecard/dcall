package com.dcall.core.configuration.system.environ;

import com.dcall.core.configuration.system.platform.Platform;
import com.dcall.core.configuration.utils.FileUtils;

import java.util.HashMap;
import java.util.Map;

import static com.dcall.core.configuration.constant.EnvironConstant.DCALL_HOME;
import static com.dcall.core.configuration.constant.EnvironConstant.LINUX_HOME_DCALL;
import static com.dcall.core.configuration.constant.EnvironConstant.WINDOWS_HOME_DCALL;

public final class Environ extends Platform {
    private final Map<String, String> env = new HashMap<>();

    private Environ() { init(); }

    private static class Holder {
        static final Environ INSTANCE = new Environ();
    }

    public static Environ getInstance() {
        return Environ.Holder.INSTANCE;
    }

    public final void init() {
        initHome();
    }

    private final void initHome() {
        String home = sysEnv().get(DCALL_HOME);

        if (home == null)
            execute(
                    () -> setHome(WINDOWS_HOME_DCALL),
                    () -> setHome(LINUX_HOME_DCALL),
                    () -> setHome(LINUX_HOME_DCALL)
            );
        else
            env.put(DCALL_HOME, home);

        home = getHome();

        FileUtils.getInstance().createDirectory(home);
        FileUtils.getInstance().lockDelete(home);
    }

    public final String getHome() {
        return env.get(DCALL_HOME);
    }

    public final void setHome(final String path) {
        env.put(DCALL_HOME, path);
    }

    private final Map<String, String> sysEnv() {
        return System.getenv();
    }
}
