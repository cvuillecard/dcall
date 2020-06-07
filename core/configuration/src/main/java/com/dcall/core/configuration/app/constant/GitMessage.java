package com.dcall.core.configuration.app.constant;

import com.dcall.core.configuration.generic.entity.user.User;
import com.dcall.core.configuration.utils.DateUtils;

import java.util.Date;
import java.util.Locale;

public final class GitMessage {

    public static String getLocalSnapshotUserMsg(final User user, final String msg) {
        return getFormatedMessage(user, "LOCAL", msg);
    }

    public static String getMountPointMsg(final User user) {
        return getFormatedMessage(user, "INIT", "Mount point");
    }

    public static String getFormatedMessage(final User user, final String prefix, final String suffix) {
        final Locale locale = new Locale("en", "US");
        final Date today = new Date();

        return prefix + " < " + DateUtils.getDateFormated("yyyy/MM/dd", today) + " at " + DateUtils.getTime(locale, today)  + " > " + suffix + " : " + user.getLogin();
    }
}
