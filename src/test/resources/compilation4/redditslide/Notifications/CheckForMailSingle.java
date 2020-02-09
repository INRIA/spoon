/**
 * Created by carlo_000 on 10/13/2015.
 */
package me.ccrama.redditslide.Notifications;
import me.ccrama.redditslide.Adapters.MarkAsReadService;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Activities.OpenContent;
import java.util.ArrayList;
import me.ccrama.redditslide.Authentication;
import java.util.List;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Activities.Inbox;
public class CheckForMailSingle extends android.content.BroadcastReceiver {
    public static final java.lang.String SUBS_TO_GET = "SUBREDDIT_NOTIFS";

    private android.content.Context c;

    @java.lang.Override
    public void onReceive(android.content.Context context, android.content.Intent intent) {
        c = context;
        if ((me.ccrama.redditslide.Authentication.reddit == null) || (!me.ccrama.redditslide.Authentication.reddit.isAuthenticated())) {
            me.ccrama.redditslide.Reddit.authentication = new me.ccrama.redditslide.Authentication(context);
        }
        new me.ccrama.redditslide.Notifications.CheckForMailSingle.AsyncGetMailSingle().executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class AsyncGetMailSingle extends android.os.AsyncTask<java.lang.Void, java.lang.Void, java.util.List<net.dean.jraw.models.Message>> {
        @java.lang.Override
        public void onPostExecute(java.util.List<net.dean.jraw.models.Message> messages) {
            android.content.res.Resources res = c.getResources();
            if ((messages != null) && (!messages.isEmpty())) {
                if (me.ccrama.redditslide.Reddit.isPackageInstalled("com.teslacoilsw.notifier")) {
                    try {
                        android.content.ContentValues cv = new android.content.ContentValues();
                        cv.put("tag", "me.ccrama.redditslide/me.ccrama.redditslide.MainActivity");
                        cv.put("count", messages.size());
                        c.getContentResolver().insert(android.net.Uri.parse("content://com.teslacoilsw.notifier/unread_count"), cv);
                    } catch (java.lang.Exception ex) {
                        ex.printStackTrace();
                    }
                }
                net.dean.jraw.models.Message message = messages.get(0);
                android.support.v4.app.NotificationManagerCompat notificationManager = android.support.v4.app.NotificationManagerCompat.from(c);
                android.content.Intent notificationIntent = new android.content.Intent(c, me.ccrama.redditslide.Activities.Inbox.class);
                notificationIntent.putExtra(me.ccrama.redditslide.Activities.Inbox.EXTRA_UNREAD, true);
                notificationIntent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
                android.app.PendingIntent intent = android.app.PendingIntent.getActivity(c, 0, notificationIntent, 0);
                // Intent for mark as read notification action
                android.app.PendingIntent readPI = me.ccrama.redditslide.Adapters.MarkAsReadService.getMarkAsReadIntent(2, c, new java.lang.String[]{ message.getFullName() });
                {
                    android.support.v4.app.NotificationCompat.InboxStyle notiStyle = new android.support.v4.app.NotificationCompat.InboxStyle();
                    notiStyle.setBigContentTitle(res.getQuantityString(me.ccrama.redditslide.R.plurals.mail_notification_title, 1, 1));
                    notiStyle.setSummaryText("");
                    if (message.getAuthor() != null) {
                        notiStyle.addLine(c.getString(me.ccrama.redditslide.R.string.mail_notification_msg_from, message.getAuthor()));
                    } else {
                        notiStyle.addLine(c.getString(me.ccrama.redditslide.R.string.mail_notification_msg_via, message.getSubreddit()));
                    }
                    android.support.v4.app.NotificationCompat.Builder builder = new android.support.v4.app.NotificationCompat.Builder(c, me.ccrama.redditslide.Reddit.CHANNEL_MAIL).setContentIntent(intent).setSmallIcon(me.ccrama.redditslide.R.drawable.notif).setTicker(res.getQuantityString(me.ccrama.redditslide.R.plurals.mail_notification_title, 1, 1)).setWhen(java.lang.System.currentTimeMillis()).setAutoCancel(true).setContentTitle(res.getQuantityString(me.ccrama.redditslide.R.plurals.mail_notification_title, 1, 1)).setStyle(notiStyle).setGroup("MESSAGES").setGroupSummary(true).setGroupAlertBehavior(android.support.v4.app.NotificationCompat.GROUP_ALERT_SUMMARY).addAction(me.ccrama.redditslide.R.drawable.ic_check_all_black, c.getString(me.ccrama.redditslide.R.string.mail_mark_read), readPI);
                    if (!me.ccrama.redditslide.SettingValues.notifSound) {
                        builder.setSound(null);
                    }
                    android.app.Notification notification = builder.build();
                    notificationManager.notify(0, notification);
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    android.support.v4.app.NotificationCompat.BigTextStyle notiStyle = new android.support.v4.app.NotificationCompat.BigTextStyle();
                    java.lang.String contentTitle;
                    if (message.getAuthor() != null) {
                        notiStyle.setBigContentTitle(c.getString(me.ccrama.redditslide.R.string.mail_notification_msg_from, message.getAuthor()));
                        contentTitle = c.getString(me.ccrama.redditslide.R.string.mail_notification_author, message.getSubject(), message.getAuthor());
                    } else {
                        notiStyle.setBigContentTitle(c.getString(me.ccrama.redditslide.R.string.mail_notification_msg_via, message.getSubreddit()));
                        contentTitle = c.getString(me.ccrama.redditslide.R.string.mail_notification_subreddit, message.getSubject(), message.getSubreddit());
                    }
                    android.content.Intent openPIBase;
                    if (message.isComment()) {
                        openPIBase = new android.content.Intent(c, me.ccrama.redditslide.Activities.OpenContent.class);
                        java.lang.String context = message.getDataNode().get("context").asText();
                        openPIBase.putExtra(me.ccrama.redditslide.Activities.OpenContent.EXTRA_URL, "https://reddit.com" + context.substring(0, context.lastIndexOf("/")));
                        openPIBase.setAction(message.getSubject());
                    } else {
                        openPIBase = new android.content.Intent(c, me.ccrama.redditslide.Activities.Inbox.class);
                        openPIBase.putExtra(me.ccrama.redditslide.Activities.Inbox.EXTRA_UNREAD, true);
                    }
                    openPIBase.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    android.app.PendingIntent openPi = android.app.PendingIntent.getActivity(c, 3 + ((int) (message.getCreated().getTime())), openPIBase, 0);
                    notiStyle.bigText(android.text.Html.fromHtml(org.apache.commons.text.StringEscapeUtils.unescapeHtml4(message.getDataNode().get("body_html").asText())));
                    android.app.PendingIntent readPISingle = me.ccrama.redditslide.Adapters.MarkAsReadService.getMarkAsReadIntent(2 + ((int) (message.getCreated().getTime())), c, new java.lang.String[]{ message.getFullName() });
                    android.support.v4.app.NotificationCompat.Builder builder = new android.support.v4.app.NotificationCompat.Builder(c, me.ccrama.redditslide.Reddit.CHANNEL_MAIL).setContentIntent(openPi).setSmallIcon(me.ccrama.redditslide.R.drawable.notif).setTicker(res.getQuantityString(me.ccrama.redditslide.R.plurals.mail_notification_title, 1, 1)).setWhen(java.lang.System.currentTimeMillis()).setAutoCancel(true).setContentTitle(contentTitle).setContentText(android.text.Html.fromHtml(org.apache.commons.text.StringEscapeUtils.unescapeHtml4(message.getDataNode().get("body_html").asText()))).setStyle(notiStyle).setGroup("MESSAGES").setGroupAlertBehavior(android.support.v4.app.NotificationCompat.GROUP_ALERT_SUMMARY).addAction(me.ccrama.redditslide.R.drawable.ic_check_all_black, c.getString(me.ccrama.redditslide.R.string.mail_mark_read), readPISingle);
                    if (!me.ccrama.redditslide.SettingValues.notifSound) {
                        builder.setSound(null);
                    }
                    android.app.Notification notification = builder.build();
                    notificationManager.notify(((int) (message.getCreated().getTime())), notification);
                }
                if (message != null) {
                    message.recycle();
                }
            }
        }

        @java.lang.Override
        protected java.util.List<net.dean.jraw.models.Message> doInBackground(java.lang.Void... params) {
            try {
                if (me.ccrama.redditslide.Authentication.isLoggedIn && me.ccrama.redditslide.Authentication.didOnline) {
                    net.dean.jraw.paginators.InboxPaginator unread = new net.dean.jraw.paginators.InboxPaginator(me.ccrama.redditslide.Authentication.reddit, "unread");
                    java.util.List<net.dean.jraw.models.Message> messages = new java.util.ArrayList<>();
                    if (unread.hasNext()) {
                        messages.addAll(unread.next());
                    }
                    return messages;
                }
            } catch (java.lang.Exception ignored) {
                ignored.printStackTrace();
            }
            return null;
        }
    }
}