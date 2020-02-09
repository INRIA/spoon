package me.ccrama.redditslide.Notifications;
import me.ccrama.redditslide.util.LogUtil;
/**
 * Created by Carlos on 9/27/2017.
 */
@android.support.annotation.RequiresApi(api = android.os.Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationPiggyback extends android.service.notification.NotificationListenerService {
    @java.lang.Override
    public void onNotificationPosted(android.service.notification.StatusBarNotification sbn) {
        final java.lang.String packageName = sbn.getPackageName();
        if ((!android.text.TextUtils.isEmpty(packageName)) && packageName.equals("com.reddit.frontpage")) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                cancelNotification(sbn.getKey());
            } else {
                cancelNotification(packageName, sbn.getTag(), sbn.getId());
            }
            android.content.Intent alarmIntent = new android.content.Intent(getApplicationContext(), me.ccrama.redditslide.Notifications.CheckForMailSingle.class);
            android.app.PendingIntent pendingIntent = android.app.PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, 0);
            android.app.AlarmManager manager = ((android.app.AlarmManager) (getApplication().getSystemService(android.content.Context.ALARM_SERVICE)));
            manager.set(android.app.AlarmManager.RTC_WAKEUP, java.lang.System.currentTimeMillis() + 100, pendingIntent);
        }
    }

    @java.lang.Override
    public void onNotificationRemoved(android.service.notification.StatusBarNotification sbn) {
        // Nothing to do
    }
}