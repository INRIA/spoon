package me.ccrama.redditslide.Notifications;
import me.ccrama.redditslide.Reddit;
/**
 * Created by carlo_000 on 10/13/2015.
 */
public class NotificationJobScheduler {
    private final android.app.PendingIntent pendingIntent;

    public NotificationJobScheduler(android.content.Context context) {
        android.content.Intent alarmIntent = new android.content.Intent(context, me.ccrama.redditslide.Notifications.CheckForMail.class);
        pendingIntent = android.app.PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        start(context);
    }

    public void start(android.content.Context c) {
        android.app.AlarmManager manager = ((android.app.AlarmManager) (c.getSystemService(android.content.Context.ALARM_SERVICE)));
        int interval = (1000 * 60) * me.ccrama.redditslide.Reddit.notificationTime;
        long currentTime = java.lang.System.currentTimeMillis();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            manager.setAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, currentTime + interval, pendingIntent);
        } else {
            manager.set(android.app.AlarmManager.RTC_WAKEUP, currentTime + interval, pendingIntent);
        }
    }

    public void cancel(android.content.Context c) {
        android.app.AlarmManager manager = ((android.app.AlarmManager) (c.getSystemService(android.content.Context.ALARM_SERVICE)));
        manager.cancel(pendingIntent);
    }
}