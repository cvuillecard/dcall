package com.dcall.core.configuration.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateUtils {

    public static String getDate(final Locale locale, final Date date) {
        return DateFormat.getDateInstance(DateFormat.DEFAULT, locale).format(date);
    }

    public static String getTime(final Locale locale, final Date date) {
        return DateFormat.getTimeInstance(DateFormat.DEFAULT, locale).format(date);
    }

    public static String getDateFormated(final String pattern, final Date date) {
        return new SimpleDateFormat(pattern).format(date);
    }
}
