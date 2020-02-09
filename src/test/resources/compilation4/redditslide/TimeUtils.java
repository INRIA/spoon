package me.ccrama.redditslide;
/**
 * Created by ccrama on 3/1/2015.
 */
public class TimeUtils {
    private static final long SECOND_MILLIS = 1000;

    private static final long MINUTE_MILLIS = 60 * me.ccrama.redditslide.TimeUtils.SECOND_MILLIS;

    private static final long HOUR_MILLIS = 60 * me.ccrama.redditslide.TimeUtils.MINUTE_MILLIS;

    private static final long DAY_MILLIS = 24 * me.ccrama.redditslide.TimeUtils.HOUR_MILLIS;

    private static final long YEAR_MILLIS = 365 * me.ccrama.redditslide.TimeUtils.DAY_MILLIS;

    private static final long MONTH_MILLIS = 30 * me.ccrama.redditslide.TimeUtils.DAY_MILLIS;

    private TimeUtils() {
    }

    public static java.lang.String getTimeAgo(long time, android.content.Context c) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }
        long now = java.lang.System.currentTimeMillis();
        if ((time > now) || (time <= 0)) {
            return null;
        }
        final long diff = now - time;
        if (diff < me.ccrama.redditslide.TimeUtils.MINUTE_MILLIS) {
            return c.getString(me.ccrama.redditslide.R.string.time_just_now);
        } else if (diff < me.ccrama.redditslide.TimeUtils.HOUR_MILLIS) {
            int minutes = me.ccrama.redditslide.TimeUtils.longToInt(diff / me.ccrama.redditslide.TimeUtils.MINUTE_MILLIS);
            return c.getString(me.ccrama.redditslide.R.string.time_minutes_short, minutes);
        } else if (diff < me.ccrama.redditslide.TimeUtils.DAY_MILLIS) {
            int hours = me.ccrama.redditslide.TimeUtils.longToInt(diff / me.ccrama.redditslide.TimeUtils.HOUR_MILLIS);
            return c.getString(me.ccrama.redditslide.R.string.time_hours_short, hours);
        } else if (diff < me.ccrama.redditslide.TimeUtils.YEAR_MILLIS) {
            int days = me.ccrama.redditslide.TimeUtils.longToInt(diff / me.ccrama.redditslide.TimeUtils.DAY_MILLIS);
            return c.getString(me.ccrama.redditslide.R.string.time_days_short, days);
        } else {
            int years = me.ccrama.redditslide.TimeUtils.longToInt(diff / me.ccrama.redditslide.TimeUtils.YEAR_MILLIS);
            return c.getString(me.ccrama.redditslide.R.string.time_years_short, years);
        }
    }

    public static java.lang.String getTimeSince(long time, android.content.Context c) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }
        long now = java.lang.System.currentTimeMillis();
        if ((time > now) || (time <= 0)) {
            return null;
        }
        android.content.res.Resources res = c.getResources();
        final long diff = now - time;
        if (diff < me.ccrama.redditslide.TimeUtils.SECOND_MILLIS) {
            return res.getQuantityString(me.ccrama.redditslide.R.plurals.time_seconds, 0, 0);
        } else if (diff < me.ccrama.redditslide.TimeUtils.MINUTE_MILLIS) {
            int seconds = me.ccrama.redditslide.TimeUtils.longToInt(diff / me.ccrama.redditslide.TimeUtils.MINUTE_MILLIS);
            return res.getQuantityString(me.ccrama.redditslide.R.plurals.time_seconds, seconds, seconds);
        } else if (diff < me.ccrama.redditslide.TimeUtils.HOUR_MILLIS) {
            int minutes = me.ccrama.redditslide.TimeUtils.longToInt(diff / me.ccrama.redditslide.TimeUtils.MINUTE_MILLIS);
            return res.getQuantityString(me.ccrama.redditslide.R.plurals.time_minutes, minutes, minutes);
        } else if (diff < me.ccrama.redditslide.TimeUtils.DAY_MILLIS) {
            int hours = me.ccrama.redditslide.TimeUtils.longToInt(diff / me.ccrama.redditslide.TimeUtils.HOUR_MILLIS);
            return res.getQuantityString(me.ccrama.redditslide.R.plurals.time_hours, hours, hours);
        } else if (diff < me.ccrama.redditslide.TimeUtils.MONTH_MILLIS) {
            int days = me.ccrama.redditslide.TimeUtils.longToInt(diff / me.ccrama.redditslide.TimeUtils.DAY_MILLIS);
            return res.getQuantityString(me.ccrama.redditslide.R.plurals.time_days, days, days);
        } else if (diff < me.ccrama.redditslide.TimeUtils.YEAR_MILLIS) {
            int months = me.ccrama.redditslide.TimeUtils.longToInt(diff / me.ccrama.redditslide.TimeUtils.MONTH_MILLIS);
            return res.getQuantityString(me.ccrama.redditslide.R.plurals.time_months, months, months);
        } else {
            int years = me.ccrama.redditslide.TimeUtils.longToInt(diff / me.ccrama.redditslide.TimeUtils.YEAR_MILLIS);
            return res.getQuantityString(me.ccrama.redditslide.R.plurals.time_years, years, years);
        }
    }

    private static java.lang.Integer longToInt(java.lang.Long temp) {
        return temp.intValue();
    }

    public static java.lang.String getTimeInHoursAndMins(int mins, android.content.Context c) {
        int hours = mins / 60;
        int minutes = mins - (hours * 60);
        android.content.res.Resources res = c.getResources();
        java.lang.String hour = "";
        java.lang.String minute = "";
        if (hours > 0)
            hour = res.getQuantityString(me.ccrama.redditslide.R.plurals.time_hours, hours, hours);

        if (minutes > 0)
            minute = res.getQuantityString(me.ccrama.redditslide.R.plurals.time_minutes, minutes, minutes);

        return hour.isEmpty() ? minute : (hour + " ") + minute;
    }
}