package com.dcall.core.configuration.app.constant;

import com.dcall.core.configuration.generic.entity.user.User;
import com.dcall.core.configuration.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class GitMessage {

    public static String getLocalSnapshotCommitMsg(final User user) {
        return '[' + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "] > " + user.getLogin() + " - snapshot (local)";
    }

    public static String getLocalSnapshotUserCreatedMsg(final User user) {
        final Locale locale = new Locale("en", "US");
        final Date today = new Date();

        return "LOCAL - " + DateUtils.getDateFormated("yyyy/MM/dd", today) + " at " + DateUtils.getTime(locale, today)  + " > New user : " + user.getLogin();
//        return "[LOCAL] " + date + " at " + time  + " - New user : " + user.getLogin();
//        return '[' + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "] - New user : " + user.getLogin();
    }

}
