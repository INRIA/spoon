package me.ccrama.redditslide;
import me.ccrama.redditslide.util.LogUtil;
import java.util.ArrayList;
import java.util.List;
import me.ccrama.redditslide.Adapters.SubredditPosts;
class StackRemoteViewsFactory implements android.widget.RemoteViewsService.RemoteViewsFactory {
    private final android.content.Context mContext;

    private java.util.List<net.dean.jraw.models.Submission> submissions = new java.util.ArrayList<>();

    private me.ccrama.redditslide.Adapters.SubredditPosts posts;

    public StackRemoteViewsFactory(android.content.Context context, android.content.Intent intent) {
        mContext = context;
        int mAppWidgetId = intent.getIntExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID, android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public void onCreate() {
    }

    public void onDestroy() {
        submissions.clear();
    }

    public int getCount() {
        return submissions.size();
    }

    public android.widget.RemoteViews getViewAt(int position) {
        final android.widget.RemoteViews rv = new android.widget.RemoteViews(mContext.getPackageName(), me.ccrama.redditslide.R.layout.submission_widget);
        if (position <= getCount()) {
            final net.dean.jraw.models.Submission submission = submissions.get(position);
            java.lang.String url = "";
            me.ccrama.redditslide.ContentType.Type type = me.ccrama.redditslide.ContentType.getContentType(submission);
            if (type == me.ccrama.redditslide.ContentType.Type.IMAGE) {
                url = submission.getUrl();
            } else if ((submission.getDataNode().has("preview") && submission.getDataNode().get("preview").get("images").get(0).get("source").has("height")) && (submission.getDataNode().get("preview").get("images").get(0).get("source").get("height").asInt() > 200)) {
                url = submission.getDataNode().get("preview").get("images").get(0).get("source").get("url").asText();
            } else if ((submission.getThumbnail() != null) && ((submission.getThumbnailType() == net.dean.jraw.models.Submission.ThumbnailType.URL) || (submission.getThumbnailType() == net.dean.jraw.models.Submission.ThumbnailType.NSFW))) {
                url = submission.getThumbnail();
            }
            try {
                // todo rv.setImageViewBitmap(R.id.thumbnail, Glide.with(mContext).load(url).asBitmap().);
                rv.setTextViewText(me.ccrama.redditslide.R.id.title, android.text.Html.fromHtml(submission.getTitle()));
            } catch (java.lang.Exception e) {
                android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), e.toString());
            }
            rv.setTextViewText(me.ccrama.redditslide.R.id.title, android.text.Html.fromHtml(submission.getTitle()));
            rv.setTextViewText(me.ccrama.redditslide.R.id.subreddit, submission.getSubredditName());
            rv.setTextViewText(me.ccrama.redditslide.R.id.info, (submission.getAuthor() + " ") + me.ccrama.redditslide.TimeUtils.getTimeAgo(submission.getCreated().getTime(), mContext));
            android.os.Bundle extras = new android.os.Bundle();
            extras.putString("url", submission.getUrl());
            android.content.Intent fillInIntent = new android.content.Intent();
            fillInIntent.putExtras(extras);
            rv.setOnClickFillInIntent(me.ccrama.redditslide.R.id.card, fillInIntent);
        }
        return rv;
    }

    public android.widget.RemoteViews getLoadingView() {
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        // if (posts == null) {
        // posts = new SubredditPosts("all", StackWidgetService.this);
        android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "MAKING POSTS");
        // }
        // posts.loadMore(mContext, null, true);
        // TODO
        submissions = posts.posts;
        android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "POSTS IS SIZE " + submissions.size());
    }
}