package com.dcall.core.configuration.app.constant;

import com.dcall.core.configuration.generic.entity.user.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class GitMessage {

    public static String getLocalSnapshotCommitMsg(final User user) {
        return '[' + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "] > " + user.getLogin() + " - snapshot (local)";
    }

    public static String getLocalSnapshotUserCreatedMsg(final User user) {
        final Locale locale = new Locale("en", "US");
        final DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
        final DateFormat tf = DateFormat.getTimeInstance(DateFormat.DEFAULT, locale);
        final Date today = new Date();
        final String date = df.format(today);
        final String time = tf.format(today);

        return "[ LOCAL - " + new SimpleDateFormat("yyyy/MM/dd").format(today) + " at " + time  + " ] New user : " + user.getLogin();
//        return "[LOCAL] " + date + " at " + time  + " - New user : " + user.getLogin();
//        return '[' + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "] - New user : " + user.getLogin();
    }
}
