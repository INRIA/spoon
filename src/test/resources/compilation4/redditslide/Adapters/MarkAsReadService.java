package me.ccrama.redditslide.Adapters;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.Notifications.CheckForMail;
import me.ccrama.redditslide.util.NetworkUtil;
/**
 * Created by brent on 1/27/16.
 */
public class MarkAsReadService extends android.app.IntentService {
    public static final java.lang.String NOTIFICATION_ID = "NOTIFICATION_ID";

    public MarkAsReadService() {
        super("MarkReadService");
    }

    public static android.app.PendingIntent getMarkAsReadIntent(int notificationId, android.content.Context context, java.lang.String[] messageNames) {
        android.content.Intent intent = new android.content.Intent(context, me.ccrama.redditslide.Adapters.MarkAsReadService.class);
        intent.putExtra(me.ccrama.redditslide.Adapters.MarkAsReadService.NOTIFICATION_ID, notificationId - 2);
        intent.putExtra(me.ccrama.redditslide.Notifications.CheckForMail.MESSAGE_EXTRA, messageNames);
        return android.app.PendingIntent.getService(context, notificationId, intent, android.app.PendingIntent.FLAG_CANCEL_CURRENT);
    }

    @java.lang.Override
    protected void onHandleIntent(android.content.Intent intent) {
        android.app.NotificationManager manager = ((android.app.NotificationManager) (getSystemService(android.content.Context.NOTIFICATION_SERVICE)));
        manager.cancel(intent.getIntExtra(me.ccrama.redditslide.Adapters.MarkAsReadService.NOTIFICATION_ID, -1));
        java.lang.String[] messages = null;
        android.os.Bundle extras = intent.getExtras();
        if (extras != null)
            messages = extras.getStringArray(me.ccrama.redditslide.Notifications.CheckForMail.MESSAGE_EXTRA);

        net.dean.jraw.managers.InboxManager inboxManager = new net.dean.jraw.managers.InboxManager(me.ccrama.redditslide.Authentication.reddit);
        if ((messages != null) && me.ccrama.redditslide.util.NetworkUtil.isConnected(getBaseContext())) {
            for (java.lang.String message : messages) {
                try {
                    inboxManager.setRead(message, true);
                } catch (net.dean.jraw.http.NetworkException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
}