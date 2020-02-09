package me.ccrama.redditslide.Autocache;
import me.ccrama.redditslide.Reddit;
import java.util.Calendar;
/**
 * Created by carlo_000 on 10/13/2015.
 */
public class AutoCacheScheduler {
    private final android.app.PendingIntent pendingIntent;

    public AutoCacheScheduler(android.content.Context context) {
        android.content.Intent alarmIntent = new android.content.Intent(context, me.ccrama.redditslide.Autocache.CacheAll.class);
        pendingIntent = android.app.PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        start(context);
    }

    public void start(android.content.Context c) {
        android.app.AlarmManager manager = ((android.app.AlarmManager) (c.getSystemService(android.content.Context.ALARM_SERVICE)));
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, me.ccrama.redditslide.Reddit.cachedData.getInt("hour", 0));
        cal.set(java.util.Calendar.MINUTE, me.ccrama.redditslide.Reddit.cachedData.getInt("minute", 0));
        if (cal.getTimeInMillis() < java.lang.System.currentTimeMillis()) {
            cal.set(java.util.Calendar.DAY_OF_YEAR, cal.get(java.util.Calendar.DAY_OF_YEAR) + 1);
        }
        manager.setRepeating(android.app.AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), android.app.AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void cancel(android.content.Context c) {
        android.app.AlarmManager manager = ((android.app.AlarmManager) (c.getSystemService(android.content.Context.ALARM_SERVICE)));
        manager.cancel(pendingIntent);
    }
}