package me.ccrama.redditslide.Notifications;
/**
 * Created by carlo_000 on 10/13/2015.
 */
public class StartOnBoot extends android.content.BroadcastReceiver {
    @java.lang.Override
    public void onReceive(android.content.Context context, android.content.Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            /* Setting the alarm here */
            android.content.Intent alarmIntent = new android.content.Intent(context, me.ccrama.redditslide.Notifications.NotificationJobScheduler.class);
            android.app.PendingIntent pendingIntent = android.app.PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
            android.app.AlarmManager manager = ((android.app.AlarmManager) (context.getSystemService(android.content.Context.ALARM_SERVICE)));
            int interval = 8000;
            manager.setInexactRepeating(android.app.AlarmManager.RTC_WAKEUP, java.lang.System.currentTimeMillis(), interval, pendingIntent);
        }
    }
}