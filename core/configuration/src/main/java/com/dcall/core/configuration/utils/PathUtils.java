package com.dcall.core.configuration.utils;

import com.dcall.core.configuration.generic.system.platform.Platform;

public final class PathUtils extends Platform {

    private PathUtils() {}

    private static class Holder {
        static final PathUtils INSTANCE = new PathUtils();
    }

    public static PathUtils getInstance() {
        return Holder.INSTANCE;
    }

    public String getPlatformPath(final String path) {
        if (isWin())
            return path.replace("/", "\\");
        return path.replace("\\", "/");
    }
}
