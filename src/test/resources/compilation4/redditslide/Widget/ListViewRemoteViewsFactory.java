package me.ccrama.redditslide.Widget;
import java.util.Set;
import me.ccrama.redditslide.Autocache.AutoCacheScheduler;
import me.ccrama.redditslide.Visuals.Palette;
import java.util.ArrayList;
import me.ccrama.redditslide.Notifications.NotificationJobScheduler;
import me.ccrama.redditslide.PostMatch;
import me.ccrama.redditslide.util.NetworkUtil;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.TimeUtils;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Activities.OpenContent;
import me.ccrama.redditslide.Authentication;
import java.util.HashSet;
class ListViewRemoteViewsFactory implements android.widget.RemoteViewsService.RemoteViewsFactory {
    private android.content.Context mContext;

    private java.util.ArrayList<net.dean.jraw.models.Submission> records;

    java.lang.String subreddit;

    int id;

    public ListViewRemoteViewsFactory(android.content.Context context, android.content.Intent intent, java.lang.String subreddit, int id) {
        mContext = context;
        this.subreddit = subreddit;
        this.id = id;
    }

    // Initialize the data set.
    public void onCreate() {
        // In onCreate() you set up any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
        records = new java.util.ArrayList<>();
        if (me.ccrama.redditslide.util.NetworkUtil.isConnected(mContext)) {
            new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
                @java.lang.Override
                protected java.lang.Void doInBackground(java.lang.Void... params) {
                    if (me.ccrama.redditslide.Authentication.reddit == null) {
                        new me.ccrama.redditslide.Authentication(mContext.getApplicationContext());
                        me.ccrama.redditslide.Authentication.me = me.ccrama.redditslide.Authentication.reddit.me();
                        me.ccrama.redditslide.Authentication.mod = me.ccrama.redditslide.Authentication.me.isMod();
                        me.ccrama.redditslide.Authentication.authentication.edit().putBoolean(me.ccrama.redditslide.Reddit.SHARED_PREF_IS_MOD, me.ccrama.redditslide.Authentication.mod).apply();
                        if (me.ccrama.redditslide.Reddit.notificationTime != (-1)) {
                            me.ccrama.redditslide.Reddit.notifications = new me.ccrama.redditslide.Notifications.NotificationJobScheduler(mContext);
                            me.ccrama.redditslide.Reddit.notifications.start(mContext.getApplicationContext());
                        }
                        if (me.ccrama.redditslide.Reddit.cachedData.contains("toCache")) {
                            me.ccrama.redditslide.Reddit.autoCache = new me.ccrama.redditslide.Autocache.AutoCacheScheduler(mContext);
                            me.ccrama.redditslide.Reddit.autoCache.start(mContext.getApplicationContext());
                        }
                        final java.lang.String name = me.ccrama.redditslide.Authentication.me.getFullName();
                        me.ccrama.redditslide.Authentication.name = name;
                        me.ccrama.redditslide.util.LogUtil.v("AUTHENTICATED");
                        if (me.ccrama.redditslide.Authentication.reddit.isAuthenticated()) {
                            final java.util.Set<java.lang.String> accounts = me.ccrama.redditslide.Authentication.authentication.getStringSet("accounts", new java.util.HashSet<java.lang.String>());
                            if (accounts.contains(name)) {
                                // convert to new system
                                accounts.remove(name);
                                accounts.add((name + ":") + me.ccrama.redditslide.Authentication.refresh);
                                me.ccrama.redditslide.Authentication.authentication.edit().putStringSet("accounts", accounts).apply();// force commit

                            }
                            me.ccrama.redditslide.Authentication.isLoggedIn = true;
                            me.ccrama.redditslide.Reddit.notFirst = true;
                        }
                    }
                    java.lang.String sub = me.ccrama.redditslide.Widget.SubredditWidgetProvider.getSubFromId(id, mContext);
                    net.dean.jraw.paginators.Paginator p;
                    if (sub.equals("frontpage")) {
                        p = new net.dean.jraw.paginators.SubredditPaginator(me.ccrama.redditslide.Authentication.reddit);
                    } else if (!sub.contains(".")) {
                        p = new net.dean.jraw.paginators.SubredditPaginator(me.ccrama.redditslide.Authentication.reddit, sub);
                    } else {
                        p = new net.dean.jraw.paginators.DomainPaginator(me.ccrama.redditslide.Authentication.reddit, sub);
                    }
                    p.setLimit(50);
                    switch (me.ccrama.redditslide.Widget.SubredditWidgetProvider.getSorting(id, mContext)) {
                        case 0 :
                            p.setSorting(net.dean.jraw.paginators.Sorting.HOT);
                            break;
                        case 1 :
                            p.setSorting(net.dean.jraw.paginators.Sorting.NEW);
                            break;
                        case 2 :
                            p.setSorting(net.dean.jraw.paginators.Sorting.RISING);
                            break;
                        case 3 :
                            p.setSorting(net.dean.jraw.paginators.Sorting.TOP);
                            break;
                        case 4 :
                            p.setSorting(net.dean.jraw.paginators.Sorting.CONTROVERSIAL);
                            break;
                        case 5 :
                            p.setSorting(net.dean.jraw.paginators.Sorting.BEST);
                            break;
                    }
                    switch (me.ccrama.redditslide.Widget.SubredditWidgetProvider.getSortingTime(id, mContext)) {
                        case 0 :
                            p.setTimePeriod(net.dean.jraw.paginators.TimePeriod.HOUR);
                            break;
                        case 1 :
                            p.setTimePeriod(net.dean.jraw.paginators.TimePeriod.DAY);
                            break;
                        case 2 :
                            p.setTimePeriod(net.dean.jraw.paginators.TimePeriod.WEEK);
                            break;
                        case 3 :
                            p.setTimePeriod(net.dean.jraw.paginators.TimePeriod.MONTH);
                            break;
                        case 4 :
                            p.setTimePeriod(net.dean.jraw.paginators.TimePeriod.YEAR);
                            break;
                        case 5 :
                            p.setTimePeriod(net.dean.jraw.paginators.TimePeriod.ALL);
                            break;
                    }
                    try {
                        java.util.ArrayList<net.dean.jraw.models.Submission> s = new java.util.ArrayList(p.next());
                        records = new java.util.ArrayList<>();
                        for (net.dean.jraw.models.Submission subm : s) {
                            if ((!me.ccrama.redditslide.PostMatch.doesMatch(subm)) && (!subm.isStickied())) {
                                records.add(subm);
                            }
                        }
                    } catch (java.lang.Exception e) {
                    }
                    return null;
                }

                @java.lang.Override
                protected void onPostExecute(java.lang.Void aVoid) {
                    android.content.Intent widgetUpdateIntent = new android.content.Intent(mContext, me.ccrama.redditslide.Widget.SubredditWidgetProvider.class);
                    widgetUpdateIntent.setAction(me.ccrama.redditslide.Widget.SubredditWidgetProvider.UPDATE_MEETING_ACTION);
                    widgetUpdateIntent.putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID, id);
                    mContext.sendBroadcast(widgetUpdateIntent);
                }
            }.execute();
        } else {
            android.content.Intent widgetUpdateIntent = new android.content.Intent(mContext, me.ccrama.redditslide.Widget.SubredditWidgetProvider.class);
            widgetUpdateIntent.setAction(me.ccrama.redditslide.Widget.SubredditWidgetProvider.UPDATE_MEETING_ACTION);
            widgetUpdateIntent.putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID, id);
            mContext.sendBroadcast(widgetUpdateIntent);
        }
    }

    // Given the position (index) of a WidgetItem in the array, use the item's text value in
    // combination with the app widget item XML file to construct a RemoteViews object.
    public android.widget.RemoteViews getViewAt(int position) {
        // position will always range from 0 to getCount() - 1.
        // Construct a RemoteViews item based on the app widget item XML file, and set the
        // text based on the position.
        int view = me.ccrama.redditslide.R.layout.submission_widget_light;
        switch (me.ccrama.redditslide.Widget.SubredditWidgetProvider.getViewType(id, mContext)) {
            case 1 :
            case 0 :
                if (me.ccrama.redditslide.Widget.SubredditWidgetProvider.getThemeFromId(id, mContext) == 2) {
                    view = me.ccrama.redditslide.R.layout.submission_widget_light;
                } else {
                    view = me.ccrama.redditslide.R.layout.submission_widget;
                }
                break;
            case 2 :
                if (me.ccrama.redditslide.Widget.SubredditWidgetProvider.getThemeFromId(id, mContext) == 2) {
                    view = me.ccrama.redditslide.R.layout.submission_widget_compact_light;
                } else {
                    view = me.ccrama.redditslide.R.layout.submission_widget_compact;
                }
                break;
        }
        final android.widget.RemoteViews rv = new android.widget.RemoteViews(mContext.getPackageName(), view);
        try {
            // feed row
            net.dean.jraw.models.Submission data = records.get(position);
            rv.setTextViewText(me.ccrama.redditslide.R.id.title, android.text.Html.fromHtml(data.getTitle()));
            rv.setTextViewText(me.ccrama.redditslide.R.id.score, data.getScore() + "");
            rv.setTextViewText(me.ccrama.redditslide.R.id.comments, data.getCommentCount() + "");
            rv.setTextViewText(me.ccrama.redditslide.R.id.information, (data.getAuthor() + " ") + me.ccrama.redditslide.TimeUtils.getTimeAgo(data.getCreated().getTime(), mContext));
            rv.setTextViewText(me.ccrama.redditslide.R.id.subreddit, data.getSubredditName());
            rv.setTextColor(me.ccrama.redditslide.R.id.subreddit, me.ccrama.redditslide.Visuals.Palette.getColor(data.getSubredditName()));
            if (me.ccrama.redditslide.Widget.SubredditWidgetProvider.getViewType(id, mContext) == 1) {
                net.dean.jraw.models.Thumbnails s = data.getThumbnails();
                rv.setViewVisibility(me.ccrama.redditslide.R.id.thumbimage2, android.view.View.GONE);
                if (((s != null) && (s.getVariations() != null)) && (s.getSource() != null)) {
                    rv.setImageViewBitmap(me.ccrama.redditslide.R.id.bigpic, ((me.ccrama.redditslide.Reddit) (mContext.getApplicationContext())).getImageLoader().loadImageSync(android.text.Html.fromHtml(data.getThumbnails().getSource().getUrl()).toString()));
                    rv.setViewVisibility(me.ccrama.redditslide.R.id.bigpic, android.view.View.VISIBLE);
                } else {
                    rv.setViewVisibility(me.ccrama.redditslide.R.id.bigpic, android.view.View.GONE);
                }
            } else {
                if (me.ccrama.redditslide.Widget.SubredditWidgetProvider.getViewType(id, mContext) != 2) {
                    rv.setViewVisibility(me.ccrama.redditslide.R.id.bigpic, android.view.View.GONE);
                }
                if (data.getThumbnailType() == net.dean.jraw.models.Submission.ThumbnailType.URL) {
                    rv.setImageViewBitmap(me.ccrama.redditslide.R.id.thumbimage2, ((me.ccrama.redditslide.Reddit) (mContext.getApplicationContext())).getImageLoader().loadImageSync(data.getThumbnail()));
                    rv.setViewVisibility(me.ccrama.redditslide.R.id.thumbimage2, android.view.View.VISIBLE);
                } else {
                    rv.setViewVisibility(me.ccrama.redditslide.R.id.thumbimage2, android.view.View.GONE);
                }
            }
            switch (me.ccrama.redditslide.Widget.SubredditWidgetProvider.getViewType(id, mContext)) {
                case 1 :
                case 0 :
                    if (me.ccrama.redditslide.Widget.SubredditWidgetProvider.getThemeFromId(id, mContext) == 2) {
                    } else {
                        rv.setTextColor(me.ccrama.redditslide.R.id.title, android.graphics.Color.WHITE);
                        rv.setTextColor(me.ccrama.redditslide.R.id.score, android.graphics.Color.WHITE);
                        rv.setTextColor(me.ccrama.redditslide.R.id.comments, android.graphics.Color.WHITE);
                        rv.setTextColor(me.ccrama.redditslide.R.id.information, android.graphics.Color.WHITE);
                    }
                    break;
                case 2 :
                    if (me.ccrama.redditslide.Widget.SubredditWidgetProvider.getThemeFromId(id, mContext) == 2) {
                    } else {
                        rv.setTextColor(me.ccrama.redditslide.R.id.title, android.graphics.Color.WHITE);
                        rv.setTextColor(me.ccrama.redditslide.R.id.score, android.graphics.Color.WHITE);
                        rv.setTextColor(me.ccrama.redditslide.R.id.comments, android.graphics.Color.WHITE);
                        rv.setTextColor(me.ccrama.redditslide.R.id.information, android.graphics.Color.WHITE);
                    }
                    break;
            }
            android.os.Bundle infos = new android.os.Bundle();
            infos.putString(me.ccrama.redditslide.Activities.OpenContent.EXTRA_URL, data.getPermalink());
            infos.putBoolean("popup", true);
            final android.content.Intent activityIntent = new android.content.Intent();
            activityIntent.putExtras(infos);
            activityIntent.setAction(data.getTitle());
            rv.setOnClickFillInIntent(me.ccrama.redditslide.R.id.card, activityIntent);
        } catch (java.lang.Exception e) {
        }
        return rv;
    }

    public int getCount() {
        return records.size();
    }

    public void onDataSetChanged() {
        // Fetching JSON data from server and add them to records arraylist
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public void onDestroy() {
        records.clear();
    }

    public boolean hasStableIds() {
        return true;
    }

    public android.widget.RemoteViews getLoadingView() {
        return null;
    }
}