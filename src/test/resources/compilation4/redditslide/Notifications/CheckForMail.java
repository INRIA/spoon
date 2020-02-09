/**
 * Created by carlo_000 on 10/13/2015.
 */
package me.ccrama.redditslide.Notifications;
import java.util.Locale;
import java.util.HashMap;
import me.ccrama.redditslide.Visuals.Palette;
import java.util.ArrayList;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Activities.ModQueue;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.Adapters.MarkAsReadService;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.HasSeen;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Activities.OpenContent;
import me.ccrama.redditslide.Authentication;
import java.util.List;
import me.ccrama.redditslide.Activities.CancelSubNotifs;
import java.util.Collections;
import me.ccrama.redditslide.Activities.Inbox;
public class CheckForMail extends android.content.BroadcastReceiver {
    public static final java.lang.String MESSAGE_EXTRA = "MESSAGE_FULLNAMES";

    public static final java.lang.String SUBS_TO_GET = "SUBREDDIT_NOTIFS";

    private android.content.Context c;

    @java.lang.Override
    public void onReceive(android.content.Context context, android.content.Intent intent) {
        c = context;
        if ((me.ccrama.redditslide.Authentication.reddit == null) || (!me.ccrama.redditslide.Authentication.reddit.isAuthenticated())) {
            me.ccrama.redditslide.Reddit.authentication = new me.ccrama.redditslide.Authentication(context);
        }
        if (!((me.ccrama.redditslide.Reddit) (c.getApplicationContext())).isNotificationAccessEnabled()) {
            new me.ccrama.redditslide.Notifications.CheckForMail.AsyncGetMail().executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if (me.ccrama.redditslide.Authentication.mod) {
            new me.ccrama.redditslide.Notifications.CheckForMail.AsyncGetModmail().executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if (!me.ccrama.redditslide.Reddit.appRestart.getString(me.ccrama.redditslide.Notifications.CheckForMail.SUBS_TO_GET, "").isEmpty()) {
            new me.ccrama.redditslide.Notifications.CheckForMail.AsyncGetSubs(c).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if (me.ccrama.redditslide.Reddit.notificationTime != (-1))
            new me.ccrama.redditslide.Notifications.NotificationJobScheduler(context).start(context);

    }

    private class AsyncGetMail extends android.os.AsyncTask<java.lang.Void, java.lang.Void, java.util.List<net.dean.jraw.models.Message>> {
        @java.lang.Override
        public void onPostExecute(java.util.List<net.dean.jraw.models.Message> messages) {
            android.content.res.Resources res = c.getResources();
            if ((messages != null) && (!messages.isEmpty())) {
                java.util.Collections.reverse(messages);
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
                // create arraylist of the messages fullName for markasread action
                java.lang.String[] messageNames = new java.lang.String[messages.size()];
                int counter = 0;
                for (net.dean.jraw.models.Message x : messages) {
                    messageNames[counter] = x.getFullName();
                    counter++;
                }
                android.support.v4.app.NotificationManagerCompat notificationManager = android.support.v4.app.NotificationManagerCompat.from(c);
                android.content.Intent notificationIntent = new android.content.Intent(c, me.ccrama.redditslide.Activities.Inbox.class);
                notificationIntent.putExtra(me.ccrama.redditslide.Activities.Inbox.EXTRA_UNREAD, true);
                android.app.PendingIntent intent = android.app.PendingIntent.getActivity(c, 0, notificationIntent, 0);
                // Intent for mark as read notification action
                android.app.PendingIntent readPI = me.ccrama.redditslide.Adapters.MarkAsReadService.getMarkAsReadIntent(2, c, messageNames);
                {
                    int amount = messages.size();
                    android.support.v4.app.NotificationCompat.InboxStyle notiStyle = new android.support.v4.app.NotificationCompat.InboxStyle();
                    notiStyle.setBigContentTitle(res.getQuantityString(me.ccrama.redditslide.R.plurals.mail_notification_title, amount, amount));
                    notiStyle.setSummaryText("");
                    for (net.dean.jraw.models.Message m : messages) {
                        if (m.getAuthor() != null) {
                            notiStyle.addLine(c.getString(me.ccrama.redditslide.R.string.mail_notification_msg_from, m.getAuthor()));
                        } else {
                            notiStyle.addLine(c.getString(me.ccrama.redditslide.R.string.mail_notification_msg_via, m.getSubreddit()));
                        }
                    }
                    android.support.v4.app.NotificationCompat.Builder builder = new android.support.v4.app.NotificationCompat.Builder(c, me.ccrama.redditslide.Reddit.CHANNEL_MAIL).setContentIntent(intent).setSmallIcon(me.ccrama.redditslide.R.drawable.notif).setTicker(res.getQuantityString(me.ccrama.redditslide.R.plurals.mail_notification_title, amount, amount)).setWhen(java.lang.System.currentTimeMillis()).setAutoCancel(true).setContentTitle(res.getQuantityString(me.ccrama.redditslide.R.plurals.mail_notification_title, amount, amount)).setStyle(notiStyle).setGroup("MESSAGES").setGroupSummary(true).setGroupAlertBehavior(android.support.v4.app.NotificationCompat.GROUP_ALERT_SUMMARY).addAction(me.ccrama.redditslide.R.drawable.ic_check_all_black, c.getString(me.ccrama.redditslide.R.string.mail_mark_read), readPI);
                    if (!me.ccrama.redditslide.SettingValues.notifSound) {
                        builder.setSound(null);
                    }
                    android.app.Notification notification = builder.build();
                    notificationManager.notify(0, notification);
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    for (net.dean.jraw.models.Message m : messages) {
                        android.support.v4.app.NotificationCompat.BigTextStyle notiStyle = new android.support.v4.app.NotificationCompat.BigTextStyle();
                        java.lang.String contentTitle;
                        if (m.getAuthor() != null) {
                            notiStyle.setBigContentTitle(c.getString(me.ccrama.redditslide.R.string.mail_notification_msg_from, m.getAuthor()));
                            contentTitle = c.getString(me.ccrama.redditslide.R.string.mail_notification_author, m.getSubject(), m.getAuthor());
                        } else {
                            notiStyle.setBigContentTitle(c.getString(me.ccrama.redditslide.R.string.mail_notification_msg_via, m.getSubreddit()));
                            contentTitle = c.getString(me.ccrama.redditslide.R.string.mail_notification_subreddit, m.getSubject(), m.getSubreddit());
                        }
                        android.content.Intent openPIBase;
                        if (m.isComment()) {
                            openPIBase = new android.content.Intent(c, me.ccrama.redditslide.Activities.OpenContent.class);
                            java.lang.String context = m.getDataNode().get("context").asText();
                            openPIBase.putExtra(me.ccrama.redditslide.Activities.OpenContent.EXTRA_URL, "https://reddit.com" + context.substring(0, context.lastIndexOf("/")));
                            openPIBase.setAction(m.getSubject());
                        } else {
                            openPIBase = new android.content.Intent(c, me.ccrama.redditslide.Activities.Inbox.class);
                            openPIBase.putExtra(me.ccrama.redditslide.Activities.Inbox.EXTRA_UNREAD, true);
                        }
                        // openPIBase.setFlags(
                        // Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        android.app.PendingIntent openPi = android.app.PendingIntent.getActivity(c, 3 + ((int) (m.getCreated().getTime())), openPIBase, 0);
                        notiStyle.bigText(android.text.Html.fromHtml(org.apache.commons.text.StringEscapeUtils.unescapeHtml4(m.getDataNode().get("body_html").asText())));
                        android.app.PendingIntent readPISingle = me.ccrama.redditslide.Adapters.MarkAsReadService.getMarkAsReadIntent(2 + ((int) (m.getCreated().getTime())), c, new java.lang.String[]{ m.getFullName() });
                        android.support.v4.app.NotificationCompat.Builder builder = new android.support.v4.app.NotificationCompat.Builder(c, me.ccrama.redditslide.Reddit.CHANNEL_MAIL).setContentIntent(openPi).setSmallIcon(me.ccrama.redditslide.R.drawable.notif).setTicker(res.getQuantityString(me.ccrama.redditslide.R.plurals.mail_notification_title, 1, 1)).setWhen(java.lang.System.currentTimeMillis()).setAutoCancel(true).setContentTitle(contentTitle).setContentText(android.text.Html.fromHtml(org.apache.commons.text.StringEscapeUtils.unescapeHtml4(m.getDataNode().get("body_html").asText()))).setStyle(notiStyle).setGroup("MESSAGES").setGroupAlertBehavior(android.support.v4.app.NotificationCompat.GROUP_ALERT_SUMMARY).addAction(me.ccrama.redditslide.R.drawable.ic_check_all_black, c.getString(me.ccrama.redditslide.R.string.mail_mark_read), readPISingle);
                        if (!me.ccrama.redditslide.SettingValues.notifSound) {
                            builder.setSound(null);
                        }
                        android.app.Notification notification = builder.build();
                        notificationManager.notify(((int) (m.getCreated().getTime())), notification);
                    }
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

    private class AsyncGetModmail extends android.os.AsyncTask<java.lang.Void, java.lang.Void, java.util.List<net.dean.jraw.models.Message>> {
        @java.lang.Override
        public void onPostExecute(java.util.List<net.dean.jraw.models.Message> messages) {
            android.content.res.Resources res = c.getResources();
            if ((messages != null) && (!messages.isEmpty())) {
                java.util.Collections.reverse(messages);
                android.app.NotificationManager notificationManager = ((android.app.NotificationManager) (c.getSystemService(android.content.Context.NOTIFICATION_SERVICE)));
                android.content.Intent notificationIntent = new android.content.Intent(c, me.ccrama.redditslide.Activities.ModQueue.class);
                // notificationIntent.setFlags(
                // Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                android.app.PendingIntent intent = android.app.PendingIntent.getActivity(c, 0, notificationIntent, 0);
                {
                    int amount = messages.size();
                    android.support.v4.app.NotificationCompat.InboxStyle notiStyle = new android.support.v4.app.NotificationCompat.InboxStyle();
                    notiStyle.setBigContentTitle(res.getQuantityString(me.ccrama.redditslide.R.plurals.mod_mail_notification_title, amount, amount));
                    notiStyle.setSummaryText("");
                    for (net.dean.jraw.models.Message m : messages) {
                        notiStyle.addLine(c.getString(me.ccrama.redditslide.R.string.mod_mail_notification_msg, m.getAuthor()));
                    }
                    android.support.v4.app.NotificationCompat.Builder builder = new android.support.v4.app.NotificationCompat.Builder(c, me.ccrama.redditslide.Reddit.CHANNEL_MODMAIL).setContentIntent(intent).setSmallIcon(me.ccrama.redditslide.R.drawable.mod_png).setTicker(res.getQuantityString(me.ccrama.redditslide.R.plurals.mod_mail_notification_title, amount, amount)).setWhen(java.lang.System.currentTimeMillis()).setAutoCancel(true).setGroupSummary(true).setGroup("MODMAIL").setGroupAlertBehavior(android.support.v4.app.NotificationCompat.GROUP_ALERT_SUMMARY).setContentTitle(res.getQuantityString(me.ccrama.redditslide.R.plurals.mod_mail_notification_title, amount, amount)).setStyle(notiStyle);
                    if (!me.ccrama.redditslide.SettingValues.notifSound) {
                        builder.setSound(null);
                    }
                    android.app.Notification notification = builder.build();
                    notificationManager.notify(1, notification);
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    for (net.dean.jraw.models.Message m : messages) {
                        android.support.v4.app.NotificationCompat.BigTextStyle notiStyle = new android.support.v4.app.NotificationCompat.BigTextStyle();
                        notiStyle.setBigContentTitle(c.getString(me.ccrama.redditslide.R.string.mod_mail_notification_msg, m.getAuthor()));
                        notiStyle.bigText(android.text.Html.fromHtml(org.apache.commons.text.StringEscapeUtils.unescapeHtml4(m.getDataNode().get("body_html").asText())));
                        android.support.v4.app.NotificationCompat.Builder builder = new android.support.v4.app.NotificationCompat.Builder(c, me.ccrama.redditslide.Reddit.CHANNEL_MODMAIL).setContentIntent(intent).setSmallIcon(me.ccrama.redditslide.R.drawable.mod_png).setTicker(res.getQuantityString(me.ccrama.redditslide.R.plurals.mod_mail_notification_title, 1, 1)).setWhen(java.lang.System.currentTimeMillis()).setAutoCancel(true).setGroup("MODMAIL").setGroupAlertBehavior(android.support.v4.app.NotificationCompat.GROUP_ALERT_SUMMARY).setContentTitle(c.getString(me.ccrama.redditslide.R.string.mail_notification_author, m.getSubject(), m.getAuthor())).setContentText(android.text.Html.fromHtml(m.getBody())).setStyle(notiStyle);
                        if (!me.ccrama.redditslide.SettingValues.notifSound) {
                            builder.setSound(null);
                        }
                        android.app.Notification notification = builder.build();
                        notificationManager.notify(((int) (m.getCreated().getTime())), notification);
                    }
                }
            }
        }

        @java.lang.Override
        protected java.util.List<net.dean.jraw.models.Message> doInBackground(java.lang.Void... params) {
            try {
                if (me.ccrama.redditslide.Authentication.isLoggedIn && me.ccrama.redditslide.Authentication.didOnline) {
                    net.dean.jraw.paginators.InboxPaginator unread = new net.dean.jraw.paginators.InboxPaginator(me.ccrama.redditslide.Authentication.reddit, "moderator/unread");
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

    public static class AsyncGetSubs extends android.os.AsyncTask<java.lang.Void, java.lang.Void, java.util.List<net.dean.jraw.models.Submission>> {
        public android.content.Context c;

        public AsyncGetSubs(android.content.Context context) {
            this.c = context;
        }

        @java.lang.Override
        public void onPostExecute(java.util.List<net.dean.jraw.models.Submission> messages) {
            if (messages != null) {
                if (!messages.isEmpty()) {
                    android.app.NotificationManager notificationManager = ((android.app.NotificationManager) (c.getSystemService(android.content.Context.NOTIFICATION_SERVICE)));
                    for (net.dean.jraw.models.Submission s : messages) {
                        android.content.Intent readIntent = new android.content.Intent(c, me.ccrama.redditslide.Activities.OpenContent.class);
                        readIntent.putExtra(me.ccrama.redditslide.Activities.OpenContent.EXTRA_URL, "https://reddit.com" + s.getPermalink());
                        readIntent.setAction(s.getTitle());
                        android.app.PendingIntent readPI = android.app.PendingIntent.getActivity(c, ((int) (s.getCreated().getTime() / 1000)), readIntent, 0);
                        android.content.Intent cancelIntent = new android.content.Intent(c, me.ccrama.redditslide.Activities.CancelSubNotifs.class);
                        cancelIntent.putExtra(me.ccrama.redditslide.Activities.CancelSubNotifs.EXTRA_SUB, s.getSubredditName());
                        android.app.PendingIntent cancelPi = android.app.PendingIntent.getActivity(c, ((int) (s.getCreated().getTime())) / 1000, cancelIntent, 0);
                        android.support.v4.app.NotificationCompat.BigTextStyle notiStyle = new android.support.v4.app.NotificationCompat.BigTextStyle();
                        notiStyle.setBigContentTitle("/r/" + s.getSubredditName());
                        notiStyle.bigText((android.text.Html.fromHtml((s.getTitle() + " ") + c.getString(me.ccrama.redditslide.R.string.submission_properties_seperator_comments)) + " ") + s.getAuthor());
                        android.app.Notification notification = new android.support.v4.app.NotificationCompat.Builder(c).setContentIntent(readPI).setSmallIcon(me.ccrama.redditslide.R.drawable.notif).setTicker(c.getString(me.ccrama.redditslide.R.string.sub_post_notifs_notification_title, s.getSubredditName())).setWhen(java.lang.System.currentTimeMillis()).setAutoCancel(true).setChannelId(me.ccrama.redditslide.Reddit.CHANNEL_SUBCHECKING).setContentTitle((((("/r/" + s.getSubredditName()) + " ") + c.getString(me.ccrama.redditslide.R.string.submission_properties_seperator_comments)) + " ") + android.text.Html.fromHtml(s.getTitle())).setContentText((((android.text.Html.fromHtml(s.getTitle()) + " ") + c.getString(me.ccrama.redditslide.R.string.submission_properties_seperator_comments)) + " ") + s.getAuthor()).setColor(me.ccrama.redditslide.Visuals.Palette.getColor(s.getSubredditName())).setStyle(notiStyle).addAction(me.ccrama.redditslide.R.drawable.close, c.getString(me.ccrama.redditslide.R.string.sub_post_notifs_notification_btn, s.getSubredditName()), cancelPi).build();
                        notificationManager.notify(((int) (s.getCreated().getTime() / 1000)), notification);
                    }
                }
            }
            if (me.ccrama.redditslide.Reddit.notificationTime != (-1))
                new me.ccrama.redditslide.Notifications.NotificationJobScheduler(c).start(c);

        }

        java.util.HashMap<java.lang.String, java.lang.Integer> subThresholds;

        @java.lang.Override
        protected java.util.List<net.dean.jraw.models.Submission> doInBackground(java.lang.Void... params) {
            try {
                long lastTime = java.lang.System.currentTimeMillis() - (60000 * me.ccrama.redditslide.Reddit.notificationTime);
                java.util.ArrayList<net.dean.jraw.models.Submission> toReturn = new java.util.ArrayList<>();
                java.util.ArrayList<java.lang.String> rawSubs = me.ccrama.redditslide.Reddit.stringToArray(me.ccrama.redditslide.Reddit.appRestart.getString(me.ccrama.redditslide.Notifications.CheckForMail.SUBS_TO_GET, ""));
                subThresholds = new java.util.HashMap<>();
                for (java.lang.String s : rawSubs) {
                    try {
                        java.lang.String[] split = s.split(":");
                        subThresholds.put(split[0].toLowerCase(java.util.Locale.ENGLISH), java.lang.Integer.valueOf(split[1]));
                    } catch (java.lang.Exception ignored) {
                    }
                }
                if (subThresholds.isEmpty()) {
                    return null;
                }
                java.lang.String first = "";
                boolean skipFirst = false;
                java.util.ArrayList<java.lang.String> finalSubs = new java.util.ArrayList<>();
                for (java.lang.String s : subThresholds.keySet()) {
                    if ((!s.isEmpty()) && (!skipFirst)) {
                        finalSubs.add(s);
                    } else {
                        skipFirst = true;
                        first = s;
                    }
                }
                net.dean.jraw.paginators.SubredditPaginator unread = new net.dean.jraw.paginators.SubredditPaginator(me.ccrama.redditslide.Authentication.reddit, first, finalSubs.toArray(new java.lang.String[finalSubs.size()]));
                unread.setSorting(net.dean.jraw.paginators.Sorting.NEW);
                unread.setLimit(30);
                if (unread.hasNext()) {
                    for (net.dean.jraw.models.Submission subm : unread.next()) {
                        if (((subm.getCreated().getTime() > lastTime) && (subm.getScore() >= subThresholds.get(subm.getSubredditName().toLowerCase()))) && (!me.ccrama.redditslide.HasSeen.getSeen(subm))) {
                            toReturn.add(subm);
                        }
                    }
                }
                return toReturn;
            } catch (java.lang.Exception ignored) {
                ignored.printStackTrace();
            }
            return null;
        }
    }
}