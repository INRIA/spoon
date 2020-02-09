package me.ccrama.redditslide.SubmissionViews;
import me.ccrama.redditslide.Views.DoEditorActions;
import me.ccrama.redditslide.ForceTouch.PeekViewActivity;
import me.ccrama.redditslide.Activities.*;
import me.ccrama.redditslide.Adapters.SubmissionViewHolder;
import me.ccrama.redditslide.Views.AnimateHelper;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.Toolbox.ToolboxUI;
import me.ccrama.redditslide.*;
import me.ccrama.redditslide.Visuals.FontPreferences;
import me.ccrama.redditslide.util.*;
import me.ccrama.redditslide.Views.CreateCardView;
import me.ccrama.redditslide.Fragments.SubmissionsView;
import me.ccrama.redditslide.Adapters.CommentAdapter;
/**
 * Created by ccrama on 9/19/2015.
 */
public class PopulateSubmissionViewHolder {
    public PopulateSubmissionViewHolder() {
    }

    public static int getStyleAttribColorValue(final android.content.Context context, final int attribResId, final int defaultValue) {
        final android.util.TypedValue tv = new android.util.TypedValue();
        final boolean found = context.getTheme().resolveAttribute(attribResId, tv, true);
        return found ? tv.data : defaultValue;
    }

    private static void addClickFunctions(final android.view.View base, final me.ccrama.redditslide.ContentType.Type type, final android.app.Activity contextActivity, final net.dean.jraw.models.Submission submission, final me.ccrama.redditslide.Adapters.SubmissionViewHolder holder, final boolean full) {
        base.setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
            @java.lang.Override
            public void onSingleClick(android.view.View v) {
                if (me.ccrama.redditslide.util.NetworkUtil.isConnected(contextActivity) || ((!me.ccrama.redditslide.util.NetworkUtil.isConnected(contextActivity)) && me.ccrama.redditslide.ContentType.fullImage(type))) {
                    if (me.ccrama.redditslide.SettingValues.storeHistory && (!full)) {
                        if ((!submission.isNsfw()) || me.ccrama.redditslide.SettingValues.storeNSFWHistory) {
                            me.ccrama.redditslide.HasSeen.addSeen(submission.getFullName());
                            if (((((contextActivity instanceof me.ccrama.redditslide.Activities.MainActivity) || (contextActivity instanceof me.ccrama.redditslide.Activities.MultiredditOverview)) || (contextActivity instanceof me.ccrama.redditslide.Activities.SubredditView)) || (contextActivity instanceof me.ccrama.redditslide.Activities.Search)) || (contextActivity instanceof me.ccrama.redditslide.Activities.Profile)) {
                                holder.title.setAlpha(0.54F);
                                holder.body.setAlpha(0.54F);
                            }
                        }
                    }
                    if (((!(contextActivity instanceof me.ccrama.redditslide.ForceTouch.PeekViewActivity)) || (!((me.ccrama.redditslide.ForceTouch.PeekViewActivity) (contextActivity)).isPeeking())) || ((base instanceof me.ccrama.redditslide.SubmissionViews.HeaderImageLinkView) && ((me.ccrama.redditslide.SubmissionViews.HeaderImageLinkView) (base)).popped)) {
                        if ((!me.ccrama.redditslide.PostMatch.openExternal(submission.getUrl())) || (type == me.ccrama.redditslide.ContentType.Type.VIDEO)) {
                            switch (type) {
                                case VID_ME :
                                case STREAMABLE :
                                    if (me.ccrama.redditslide.SettingValues.video) {
                                        android.content.Intent myIntent = new android.content.Intent(contextActivity, me.ccrama.redditslide.Activities.MediaView.class);
                                        myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.SUBREDDIT, submission.getSubredditName());
                                        myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_URL, submission.getUrl());
                                        me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.addAdaptorPosition(myIntent, submission, holder.getAdapterPosition());
                                        contextActivity.startActivity(myIntent);
                                    } else {
                                        me.ccrama.redditslide.util.LinkUtil.openExternally(submission.getUrl());
                                    }
                                    break;
                                case IMGUR :
                                    me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.openImage(type, contextActivity, submission, holder.leadImage, holder.getAdapterPosition());
                                    break;
                                case EMBEDDED :
                                    if (me.ccrama.redditslide.SettingValues.video) {
                                        java.lang.String data = android.text.Html.fromHtml(submission.getDataNode().get("media_embed").get("content").asText()).toString();
                                        {
                                            android.content.Intent i = new android.content.Intent(contextActivity, me.ccrama.redditslide.Activities.FullscreenVideo.class);
                                            i.putExtra(me.ccrama.redditslide.Activities.FullscreenVideo.EXTRA_HTML, data);
                                            contextActivity.startActivity(i);
                                        }
                                    } else {
                                        me.ccrama.redditslide.util.LinkUtil.openExternally(submission.getUrl());
                                    }
                                    break;
                                case REDDIT :
                                    me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.openRedditContent(submission.getUrl(), contextActivity);
                                    break;
                                case LINK :
                                    me.ccrama.redditslide.util.LinkUtil.openUrl(submission.getUrl(), me.ccrama.redditslide.Visuals.Palette.getColor(submission.getSubredditName()), contextActivity, holder.getAdapterPosition(), submission);
                                    break;
                                case SELF :
                                    if (holder != null) {
                                        me.ccrama.redditslide.util.OnSingleClickListener.override = true;
                                        holder.itemView.performClick();
                                    }
                                    break;
                                case ALBUM :
                                    if (me.ccrama.redditslide.SettingValues.album) {
                                        android.content.Intent i;
                                        if (me.ccrama.redditslide.SettingValues.albumSwipe) {
                                            i = new android.content.Intent(contextActivity, me.ccrama.redditslide.Activities.AlbumPager.class);
                                            i.putExtra(me.ccrama.redditslide.Activities.AlbumPager.SUBREDDIT, submission.getSubredditName());
                                            i.putExtra(me.ccrama.redditslide.Activities.Album.EXTRA_URL, submission.getUrl());
                                        } else {
                                            i = new android.content.Intent(contextActivity, me.ccrama.redditslide.Activities.Album.class);
                                            i.putExtra(me.ccrama.redditslide.Activities.Album.SUBREDDIT, submission.getSubredditName());
                                            i.putExtra(me.ccrama.redditslide.Activities.Album.EXTRA_URL, submission.getUrl());
                                        }
                                        me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.addAdaptorPosition(i, submission, holder.getAdapterPosition());
                                        contextActivity.startActivity(i);
                                        contextActivity.overridePendingTransition(me.ccrama.redditslide.R.anim.slideright, me.ccrama.redditslide.R.anim.fade_out);
                                    } else {
                                        me.ccrama.redditslide.util.LinkUtil.openExternally(submission.getUrl());
                                    }
                                    break;
                                case TUMBLR :
                                    if (me.ccrama.redditslide.SettingValues.album) {
                                        android.content.Intent i;
                                        if (me.ccrama.redditslide.SettingValues.albumSwipe) {
                                            i = new android.content.Intent(contextActivity, me.ccrama.redditslide.Activities.TumblrPager.class);
                                            i.putExtra(me.ccrama.redditslide.Activities.TumblrPager.SUBREDDIT, submission.getSubredditName());
                                            i.putExtra(me.ccrama.redditslide.Activities.Album.EXTRA_URL, submission.getUrl());
                                        } else {
                                            i = new android.content.Intent(contextActivity, me.ccrama.redditslide.Activities.Tumblr.class);
                                            i.putExtra(me.ccrama.redditslide.Activities.Tumblr.SUBREDDIT, submission.getSubredditName());
                                            i.putExtra(me.ccrama.redditslide.Activities.Album.EXTRA_URL, submission.getUrl());
                                        }
                                        me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.addAdaptorPosition(i, submission, holder.getAdapterPosition());
                                        contextActivity.startActivity(i);
                                        contextActivity.overridePendingTransition(me.ccrama.redditslide.R.anim.slideright, me.ccrama.redditslide.R.anim.fade_out);
                                    } else {
                                        me.ccrama.redditslide.util.LinkUtil.openExternally(submission.getUrl());
                                    }
                                    break;
                                case DEVIANTART :
                                case XKCD :
                                case IMAGE :
                                    me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.openImage(type, contextActivity, submission, holder.leadImage, holder.getAdapterPosition());
                                    break;
                                case VREDDIT_REDIRECT :
                                case GIF :
                                case VREDDIT_DIRECT :
                                    me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.openGif(contextActivity, submission, holder.getAdapterPosition());
                                    break;
                                case NONE :
                                    if (holder != null) {
                                        holder.itemView.performClick();
                                    }
                                    break;
                                case VIDEO :
                                    if (!me.ccrama.redditslide.util.LinkUtil.tryOpenWithVideoPlugin(submission.getUrl())) {
                                        me.ccrama.redditslide.util.LinkUtil.openUrl(submission.getUrl(), me.ccrama.redditslide.Visuals.Palette.getStatusBarColor(), contextActivity);
                                    }
                                    break;
                            }
                        } else {
                            me.ccrama.redditslide.util.LinkUtil.openExternally(submission.getUrl());
                        }
                    }
                } else if ((!(contextActivity instanceof me.ccrama.redditslide.ForceTouch.PeekViewActivity)) || (!((me.ccrama.redditslide.ForceTouch.PeekViewActivity) (contextActivity)).isPeeking())) {
                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.go_online_view_content, android.support.design.widget.Snackbar.LENGTH_SHORT);
                    android.view.View view = s.getView();
                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(android.graphics.Color.WHITE);
                    s.show();
                }
            }
        });
    }

    public static void openRedditContent(java.lang.String url, android.content.Context c) {
        new me.ccrama.redditslide.OpenRedditLink(c, url);
    }

    public static void openImage(me.ccrama.redditslide.ContentType.Type type, android.app.Activity contextActivity, net.dean.jraw.models.Submission submission, me.ccrama.redditslide.SubmissionViews.HeaderImageLinkView baseView, int adapterPosition) {
        if (me.ccrama.redditslide.SettingValues.image) {
            android.content.Intent myIntent = new android.content.Intent(contextActivity, me.ccrama.redditslide.Activities.MediaView.class);
            myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.SUBREDDIT, submission.getSubredditName());
            java.lang.String url;
            java.lang.String previewUrl;
            url = submission.getUrl();
            if ((((baseView != null) && baseView.lq) && me.ccrama.redditslide.SettingValues.loadImageLq) && (type != me.ccrama.redditslide.ContentType.Type.XKCD)) {
                myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_LQ, true);
                myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_DISPLAY_URL, baseView.loadedUrl);
            } else if ((submission.getDataNode().has("preview") && submission.getDataNode().get("preview").get("images").get(0).get("source").has("height")) && (type != me.ccrama.redditslide.ContentType.Type.XKCD)) {
                // Load the preview image which has probably already been cached in memory instead of the direct link
                previewUrl = submission.getDataNode().get("preview").get("images").get(0).get("source").get("url").asText();
                if ((baseView == null) || ((!me.ccrama.redditslide.SettingValues.loadImageLq) && baseView.lq)) {
                    myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_DISPLAY_URL, previewUrl);
                } else {
                    myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_DISPLAY_URL, baseView.loadedUrl);
                }
            }
            myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_URL, url);
            me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.addAdaptorPosition(myIntent, submission, adapterPosition);
            myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_SHARE_URL, submission.getUrl());
            contextActivity.startActivity(myIntent);
        } else {
            me.ccrama.redditslide.util.LinkUtil.openExternally(submission.getUrl());
        }
    }

    public static void addAdaptorPosition(android.content.Intent myIntent, net.dean.jraw.models.Submission submission, int adapterPosition) {
        if ((submission.getComments() == null) && (adapterPosition != (-1))) {
            myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.ADAPTER_POSITION, adapterPosition);
            myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.SUBMISSION_URL, submission.getPermalink());
        }
        me.ccrama.redditslide.Fragments.SubmissionsView.currentPosition(adapterPosition);
        me.ccrama.redditslide.Fragments.SubmissionsView.currentSubmission(submission);
    }

    public static void openGif(android.app.Activity contextActivity, net.dean.jraw.models.Submission submission, int adapterPosition) {
        if (me.ccrama.redditslide.SettingValues.gif) {
            me.ccrama.redditslide.DataShare.sharedSubmission = submission;
            android.content.Intent myIntent = new android.content.Intent(contextActivity, me.ccrama.redditslide.Activities.MediaView.class);
            myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.SUBREDDIT, submission.getSubredditName());
            me.ccrama.redditslide.util.GifUtils.AsyncLoadGif.VideoType t = me.ccrama.redditslide.util.GifUtils.AsyncLoadGif.getVideoType(submission.getUrl());
            if (t == me.ccrama.redditslide.util.GifUtils.AsyncLoadGif.VideoType.VREDDIT) {
                if (submission.getDataNode().has("media") && submission.getDataNode().get("media").has("reddit_video")) {
                    myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_URL, org.apache.commons.text.StringEscapeUtils.unescapeJson(submission.getDataNode().get("media").get("reddit_video").get("fallback_url").asText()).replace("&amp;", "&"));
                } else if (submission.getDataNode().has("crosspost_parent_list")) {
                    myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_URL, org.apache.commons.text.StringEscapeUtils.unescapeJson(submission.getDataNode().get("crosspost_parent_list").get(0).get("media").get("reddit_video").get("fallback_url").asText()).replace("&amp;", "&"));
                } else {
                    new me.ccrama.redditslide.SubmissionViews.OpenVRedditTask(contextActivity, submission.getSubredditName()).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR, submission.getUrl());
                    return;
                }
            } else if (((t.shouldLoadPreview() && submission.getDataNode().has("preview")) && submission.getDataNode().get("preview").get("images").get(0).has("variants")) && submission.getDataNode().get("preview").get("images").get(0).get("variants").has("mp4")) {
                myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_URL, org.apache.commons.text.StringEscapeUtils.unescapeJson(submission.getDataNode().get("preview").get("images").get(0).get("variants").get("mp4").get("source").get("url").asText()).replace("&amp;", "&"));
            } else if ((((t == me.ccrama.redditslide.util.GifUtils.AsyncLoadGif.VideoType.DIRECT) && submission.getDataNode().has("media")) && submission.getDataNode().get("media").has("reddit_video")) && submission.getDataNode().get("media").get("reddit_video").has("fallback_url")) {
                myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_URL, org.apache.commons.text.StringEscapeUtils.unescapeJson(submission.getDataNode().get("media").get("reddit_video").get("fallback_url").asText()).replace("&amp;", "&"));
            } else if (t != me.ccrama.redditslide.util.GifUtils.AsyncLoadGif.VideoType.OTHER) {
                myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_URL, submission.getUrl());
            } else {
                me.ccrama.redditslide.util.LinkUtil.openUrl(submission.getUrl(), me.ccrama.redditslide.Visuals.Palette.getColor(submission.getSubredditName()), contextActivity, adapterPosition, submission);
                return;
            }
            if (submission.getDataNode().has("preview") && submission.getDataNode().get("preview").get("images").get(0).get("source").has("height")) {
                // Load the preview image which has probably already been cached in memory instead of the direct link
                java.lang.String previewUrl = submission.getDataNode().get("preview").get("images").get(0).get("source").get("url").asText();
                myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_DISPLAY_URL, previewUrl);
            }
            me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.addAdaptorPosition(myIntent, submission, adapterPosition);
            contextActivity.startActivity(myIntent);
        } else {
            me.ccrama.redditslide.util.LinkUtil.openExternally(submission.getUrl());
        }
    }

    public static int getCurrentTintColor(android.content.Context v) {
        return me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.getStyleAttribColorValue(v, me.ccrama.redditslide.R.attr.tintColor, android.graphics.Color.WHITE);
    }

    public java.lang.String reason;

    boolean[] chosen = new boolean[]{ false, false, false };

    boolean[] oldChosen = new boolean[]{ false, false, false };

    public static int getWhiteTintColor() {
        return me.ccrama.redditslide.Visuals.Palette.ThemeEnum.DARK.getTint();
    }

    public <T extends net.dean.jraw.models.Contribution> void showBottomSheet(final android.app.Activity mContext, final net.dean.jraw.models.Submission submission, final me.ccrama.redditslide.Adapters.SubmissionViewHolder holder, final java.util.List<T> posts, final java.lang.String baseSub, final android.support.v7.widget.RecyclerView recyclerview, final boolean full) {
        int[] attrs = new int[]{ me.ccrama.redditslide.R.attr.tintColor };
        android.content.res.TypedArray ta = mContext.obtainStyledAttributes(attrs);
        int color = ta.getColor(0, android.graphics.Color.WHITE);
        android.graphics.drawable.Drawable profile = android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.profile, null);
        final android.graphics.drawable.Drawable sub = android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.sub, null);
        android.graphics.drawable.Drawable saved = android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.iconstarfilled, null);
        android.graphics.drawable.Drawable hide = android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.hide, null);
        final android.graphics.drawable.Drawable report = android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.report, null);
        android.graphics.drawable.Drawable copy = android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.ic_content_copy, null);
        final android.graphics.drawable.Drawable readLater = android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.save, null);
        android.graphics.drawable.Drawable open = android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.openexternal, null);
        android.graphics.drawable.Drawable link = android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.link, null);
        android.graphics.drawable.Drawable reddit = android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.commentchange, null);
        android.graphics.drawable.Drawable filter = android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.filter, null);
        android.graphics.drawable.Drawable crosspost = android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.forward, null);
        profile.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        sub.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        saved.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        hide.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        report.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        copy.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        open.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        link.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        reddit.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        readLater.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        filter.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        crosspost.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        ta.recycle();
        final com.cocosw.bottomsheet.BottomSheet.Builder b = new com.cocosw.bottomsheet.BottomSheet.Builder(mContext).title(android.text.Html.fromHtml(submission.getTitle()));
        final boolean isReadLater = mContext instanceof me.ccrama.redditslide.Activities.PostReadLater;
        final boolean isAddedToReadLaterList = me.ccrama.redditslide.ReadLater.isToBeReadLater(submission);
        if (me.ccrama.redditslide.Authentication.didOnline) {
            b.sheet(1, profile, "/u/" + submission.getAuthor()).sheet(2, sub, "/r/" + submission.getSubredditName());
            java.lang.String save = mContext.getString(me.ccrama.redditslide.R.string.btn_save);
            if (me.ccrama.redditslide.ActionStates.isSaved(submission)) {
                save = mContext.getString(me.ccrama.redditslide.R.string.comment_unsave);
            }
            if (me.ccrama.redditslide.Authentication.isLoggedIn) {
                b.sheet(3, saved, save);
            }
        }
        if (isAddedToReadLaterList) {
            b.sheet(28, readLater, "Mark As Read");
        } else {
            b.sheet(28, readLater, "Read later");
        }
        if (me.ccrama.redditslide.Authentication.didOnline) {
            if (me.ccrama.redditslide.Authentication.isLoggedIn) {
                b.sheet(12, report, mContext.getString(me.ccrama.redditslide.R.string.btn_report));
                b.sheet(13, crosspost, mContext.getString(me.ccrama.redditslide.R.string.btn_crosspost));
            }
        }
        if (((submission.getSelftext() != null) && (!submission.getSelftext().isEmpty())) && full) {
            b.sheet(25, copy, mContext.getString(me.ccrama.redditslide.R.string.submission_copy_text));
        }
        boolean hidden = submission.isHidden();
        if ((!full) && me.ccrama.redditslide.Authentication.didOnline) {
            if (!hidden) {
                b.sheet(5, hide, mContext.getString(me.ccrama.redditslide.R.string.submission_hide));
            } else {
                b.sheet(5, hide, mContext.getString(me.ccrama.redditslide.R.string.submission_unhide));
            }
        }
        b.sheet(7, open, mContext.getString(me.ccrama.redditslide.R.string.submission_link_extern));
        b.sheet(4, link, mContext.getString(me.ccrama.redditslide.R.string.submission_share_permalink)).sheet(8, reddit, mContext.getString(me.ccrama.redditslide.R.string.submission_share_reddit_url));
        if ((mContext instanceof me.ccrama.redditslide.Activities.MainActivity) || (mContext instanceof me.ccrama.redditslide.Activities.SubredditView)) {
            b.sheet(10, filter, mContext.getString(me.ccrama.redditslide.R.string.filter_content));
        }
        b.listener(new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                switch (which) {
                    case 1 :
                        {
                            android.content.Intent i = new android.content.Intent(mContext, me.ccrama.redditslide.Activities.Profile.class);
                            i.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_PROFILE, submission.getAuthor());
                            mContext.startActivity(i);
                        }
                        break;
                    case 2 :
                        {
                            android.content.Intent i = new android.content.Intent(mContext, me.ccrama.redditslide.Activities.SubredditView.class);
                            i.putExtra(me.ccrama.redditslide.Activities.SubredditView.EXTRA_SUBREDDIT, submission.getSubredditName());
                            mContext.startActivityForResult(i, 14);
                        }
                        break;
                    case 10 :
                        java.lang.String[] choices;
                        final java.lang.String flair = (submission.getSubmissionFlair().getText() != null) ? submission.getSubmissionFlair().getText() : "";
                        if (flair.isEmpty()) {
                            choices = new java.lang.String[]{ mContext.getString(me.ccrama.redditslide.R.string.filter_posts_sub, submission.getSubredditName()), mContext.getString(me.ccrama.redditslide.R.string.filter_posts_user, submission.getAuthor()), mContext.getString(me.ccrama.redditslide.R.string.filter_posts_urls, submission.getDomain()), mContext.getString(me.ccrama.redditslide.R.string.filter_open_externally, submission.getDomain()) };
                            chosen = new boolean[]{ me.ccrama.redditslide.SettingValues.subredditFilters.contains(submission.getSubredditName().toLowerCase(java.util.Locale.ENGLISH)), me.ccrama.redditslide.SettingValues.userFilters.contains(submission.getAuthor().toLowerCase(java.util.Locale.ENGLISH)), me.ccrama.redditslide.SettingValues.domainFilters.contains(submission.getDomain().toLowerCase(java.util.Locale.ENGLISH)), me.ccrama.redditslide.SettingValues.alwaysExternal.contains(submission.getDomain().toLowerCase(java.util.Locale.ENGLISH)) };
                            oldChosen = chosen.clone();
                        } else {
                            choices = new java.lang.String[]{ mContext.getString(me.ccrama.redditslide.R.string.filter_posts_sub, submission.getSubredditName()), mContext.getString(me.ccrama.redditslide.R.string.filter_posts_user, submission.getAuthor()), mContext.getString(me.ccrama.redditslide.R.string.filter_posts_urls, submission.getDomain()), mContext.getString(me.ccrama.redditslide.R.string.filter_open_externally, submission.getDomain()), mContext.getString(me.ccrama.redditslide.R.string.filter_posts_flair, flair, baseSub) };
                        }
                        chosen = new boolean[]{ me.ccrama.redditslide.SettingValues.subredditFilters.contains(submission.getSubredditName().toLowerCase(java.util.Locale.ENGLISH)), me.ccrama.redditslide.SettingValues.userFilters.contains(submission.getAuthor().toLowerCase(java.util.Locale.ENGLISH)), me.ccrama.redditslide.SettingValues.domainFilters.contains(submission.getDomain().toLowerCase(java.util.Locale.ENGLISH)), me.ccrama.redditslide.SettingValues.alwaysExternal.contains(submission.getDomain().toLowerCase(java.util.Locale.ENGLISH)), me.ccrama.redditslide.SettingValues.flairFilters.contains((baseSub + ":") + flair.toLowerCase(java.util.Locale.ENGLISH).trim()) };
                        oldChosen = chosen.clone();
                        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.filter_title).alwaysCallMultiChoiceCallback().setMultiChoiceItems(choices, chosen, new android.content.DialogInterface.OnMultiChoiceClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialog, int which, boolean isChecked) {
                                chosen[which] = isChecked;
                            }
                        }).setPositiveButton(me.ccrama.redditslide.R.string.filter_btn, new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                boolean filtered = false;
                                android.content.SharedPreferences.Editor e = me.ccrama.redditslide.SettingValues.prefs.edit();
                                if (chosen[0] && (chosen[0] != oldChosen[0])) {
                                    me.ccrama.redditslide.SettingValues.subredditFilters.add(submission.getSubredditName().toLowerCase(java.util.Locale.ENGLISH).trim());
                                    filtered = true;
                                    e.putStringSet(me.ccrama.redditslide.SettingValues.PREF_SUBREDDIT_FILTERS, me.ccrama.redditslide.SettingValues.subredditFilters);
                                } else if ((!chosen[0]) && (chosen[0] != oldChosen[0])) {
                                    me.ccrama.redditslide.SettingValues.subredditFilters.remove(submission.getSubredditName().toLowerCase(java.util.Locale.ENGLISH).trim());
                                    filtered = false;
                                    e.putStringSet(me.ccrama.redditslide.SettingValues.PREF_SUBREDDIT_FILTERS, me.ccrama.redditslide.SettingValues.subredditFilters);
                                    e.apply();
                                }
                                if (chosen[1] && (chosen[1] != oldChosen[1])) {
                                    me.ccrama.redditslide.SettingValues.userFilters.add(submission.getAuthor().toLowerCase(java.util.Locale.ENGLISH).trim());
                                    filtered = true;
                                    e.putStringSet(me.ccrama.redditslide.SettingValues.PREF_USER_FILTERS, me.ccrama.redditslide.SettingValues.userFilters);
                                } else if ((!chosen[1]) && (chosen[1] != oldChosen[1])) {
                                    me.ccrama.redditslide.SettingValues.userFilters.remove(submission.getAuthor().toLowerCase(java.util.Locale.ENGLISH).trim());
                                    filtered = false;
                                    e.putStringSet(me.ccrama.redditslide.SettingValues.PREF_USER_FILTERS, me.ccrama.redditslide.SettingValues.userFilters);
                                    e.apply();
                                }
                                if (chosen[2] && (chosen[2] != oldChosen[2])) {
                                    me.ccrama.redditslide.SettingValues.domainFilters.add(submission.getDomain().toLowerCase(java.util.Locale.ENGLISH).trim());
                                    filtered = true;
                                    e.putStringSet(me.ccrama.redditslide.SettingValues.PREF_DOMAIN_FILTERS, me.ccrama.redditslide.SettingValues.domainFilters);
                                } else if ((!chosen[2]) && (chosen[2] != oldChosen[2])) {
                                    me.ccrama.redditslide.SettingValues.domainFilters.remove(submission.getDomain().toLowerCase(java.util.Locale.ENGLISH).trim());
                                    filtered = false;
                                    e.putStringSet(me.ccrama.redditslide.SettingValues.PREF_DOMAIN_FILTERS, me.ccrama.redditslide.SettingValues.domainFilters);
                                    e.apply();
                                }
                                if (chosen[3] && (chosen[3] != oldChosen[3])) {
                                    me.ccrama.redditslide.SettingValues.alwaysExternal.add(submission.getDomain().toLowerCase(java.util.Locale.ENGLISH).trim());
                                    e.putStringSet(me.ccrama.redditslide.SettingValues.PREF_ALWAYS_EXTERNAL, me.ccrama.redditslide.SettingValues.alwaysExternal);
                                    e.apply();
                                } else if ((!chosen[3]) && (chosen[3] != oldChosen[3])) {
                                    me.ccrama.redditslide.SettingValues.alwaysExternal.remove(submission.getDomain().toLowerCase(java.util.Locale.ENGLISH).trim());
                                    e.putStringSet(me.ccrama.redditslide.SettingValues.PREF_ALWAYS_EXTERNAL, me.ccrama.redditslide.SettingValues.alwaysExternal);
                                    e.apply();
                                }
                                if (chosen.length > 4) {
                                    if (chosen[4] && (chosen[4] != oldChosen[4])) {
                                        me.ccrama.redditslide.SettingValues.flairFilters.add(((baseSub + ":") + flair).toLowerCase(java.util.Locale.ENGLISH).trim());
                                        e.putStringSet(me.ccrama.redditslide.SettingValues.PREF_FLAIR_FILTERS, me.ccrama.redditslide.SettingValues.flairFilters);
                                        e.apply();
                                        filtered = true;
                                    } else if ((!chosen[4]) && (chosen[4] != oldChosen[4])) {
                                        me.ccrama.redditslide.SettingValues.flairFilters.remove(((baseSub + ":") + flair).toLowerCase(java.util.Locale.ENGLISH).trim());
                                        e.putStringSet(me.ccrama.redditslide.SettingValues.PREF_FLAIR_FILTERS, me.ccrama.redditslide.SettingValues.flairFilters);
                                        e.apply();
                                    }
                                }
                                if (filtered) {
                                    e.apply();
                                    java.util.ArrayList<net.dean.jraw.models.Contribution> toRemove = new java.util.ArrayList<>();
                                    for (net.dean.jraw.models.Contribution s : posts) {
                                        if ((s instanceof net.dean.jraw.models.Submission) && me.ccrama.redditslide.PostMatch.doesMatch(((net.dean.jraw.models.Submission) (s)))) {
                                            toRemove.add(s);
                                        }
                                    }
                                    me.ccrama.redditslide.OfflineSubreddit s = me.ccrama.redditslide.OfflineSubreddit.getSubreddit(baseSub, false, mContext);
                                    for (net.dean.jraw.models.Contribution remove : toRemove) {
                                        final int pos = posts.indexOf(remove);
                                        posts.remove(pos);
                                        if (baseSub != null) {
                                            s.hideMulti(pos);
                                        }
                                    }
                                    s.writeToMemoryNoStorage();
                                    recyclerview.getAdapter().notifyDataSetChanged();
                                }
                            }
                        }).setNegativeButton(me.ccrama.redditslide.R.string.btn_cancel, null).show();
                        break;
                    case 3 :
                        saveSubmission(submission, mContext, holder, full);
                        break;
                    case 5 :
                        {
                            hideSubmission(submission, posts, baseSub, recyclerview, mContext);
                        }
                        break;
                    case 7 :
                        me.ccrama.redditslide.util.LinkUtil.openExternally(submission.getUrl());
                        if (submission.isNsfw() && (!me.ccrama.redditslide.SettingValues.storeNSFWHistory)) {
                            // Do nothing if the post is NSFW and storeNSFWHistory is not enabled
                        } else if (me.ccrama.redditslide.SettingValues.storeHistory) {
                            me.ccrama.redditslide.HasSeen.addSeen(submission.getFullName());
                        }
                        break;
                    case 13 :
                        me.ccrama.redditslide.util.LinkUtil.crosspost(submission, mContext);
                        break;
                    case 28 :
                        if (!isAddedToReadLaterList) {
                            me.ccrama.redditslide.ReadLater.setReadLater(submission, true);
                            android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(holder.itemView, "Added to read later!", android.support.design.widget.Snackbar.LENGTH_SHORT);
                            android.view.View view = s.getView();
                            android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                            tv.setTextColor(android.graphics.Color.WHITE);
                            s.setAction(me.ccrama.redditslide.R.string.btn_undo, new android.view.View.OnClickListener() {
                                @java.lang.Override
                                public void onClick(android.view.View view) {
                                    me.ccrama.redditslide.ReadLater.setReadLater(submission, false);
                                    android.support.design.widget.Snackbar s2 = android.support.design.widget.Snackbar.make(holder.itemView, "Removed from read later", android.support.design.widget.Snackbar.LENGTH_SHORT);
                                    android.view.View view2 = s2.getView();
                                    android.widget.TextView tv2 = view2.findViewById(android.support.design.R.id.snackbar_text);
                                    tv2.setTextColor(android.graphics.Color.WHITE);
                                    s2.show();
                                }
                            });
                            if (me.ccrama.redditslide.util.NetworkUtil.isConnected(mContext)) {
                                new me.ccrama.redditslide.CommentCacheAsync(java.util.Arrays.asList(submission), mContext, me.ccrama.redditslide.CommentCacheAsync.SAVED_SUBMISSIONS, new boolean[]{ true, true }).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                            s.show();
                        } else {
                            me.ccrama.redditslide.ReadLater.setReadLater(submission, false);
                            if (isReadLater || (!me.ccrama.redditslide.Authentication.didOnline)) {
                                final int pos = posts.indexOf(submission);
                                posts.remove(submission);
                                recyclerview.getAdapter().notifyItemRemoved(holder.getAdapterPosition());
                                android.support.design.widget.Snackbar s2 = android.support.design.widget.Snackbar.make(holder.itemView, "Removed from read later", android.support.design.widget.Snackbar.LENGTH_SHORT);
                                android.view.View view2 = s2.getView();
                                android.widget.TextView tv2 = view2.findViewById(android.support.design.R.id.snackbar_text);
                                tv2.setTextColor(android.graphics.Color.WHITE);
                                s2.setAction(me.ccrama.redditslide.R.string.btn_undo, new android.view.View.OnClickListener() {
                                    @java.lang.Override
                                    public void onClick(android.view.View view) {
                                        posts.add(pos, ((T) (submission)));
                                        recyclerview.getAdapter().notifyDataSetChanged();
                                    }
                                });
                            } else {
                                android.support.design.widget.Snackbar s2 = android.support.design.widget.Snackbar.make(holder.itemView, "Removed from read later", android.support.design.widget.Snackbar.LENGTH_SHORT);
                                android.view.View view2 = s2.getView();
                                android.widget.TextView tv2 = view2.findViewById(android.support.design.R.id.snackbar_text);
                                s2.show();
                            }
                            me.ccrama.redditslide.OfflineSubreddit.newSubreddit(me.ccrama.redditslide.CommentCacheAsync.SAVED_SUBMISSIONS).deleteFromMemory(submission.getFullName());
                        }
                        break;
                    case 4 :
                        me.ccrama.redditslide.Reddit.defaultShareText(android.text.Html.fromHtml(submission.getTitle()).toString(), org.apache.commons.text.StringEscapeUtils.escapeHtml4(submission.getUrl()), mContext);
                        break;
                    case 12 :
                        final com.afollestad.materialdialogs.MaterialDialog reportDialog = new com.afollestad.materialdialogs.MaterialDialog.Builder(mContext).customView(me.ccrama.redditslide.R.layout.report_dialog, true).title(me.ccrama.redditslide.R.string.report_post).positiveText(me.ccrama.redditslide.R.string.btn_report).negativeText(me.ccrama.redditslide.R.string.btn_cancel).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                            @java.lang.Override
                            public void onClick(com.afollestad.materialdialogs.MaterialDialog dialog, com.afollestad.materialdialogs.DialogAction which) {
                                android.widget.RadioGroup reasonGroup = dialog.getCustomView().findViewById(me.ccrama.redditslide.R.id.report_reasons);
                                java.lang.String reportReason;
                                if (reasonGroup.getCheckedRadioButtonId() == me.ccrama.redditslide.R.id.report_other) {
                                    reportReason = ((android.widget.EditText) (dialog.getCustomView().findViewById(me.ccrama.redditslide.R.id.input_report_reason))).getText().toString();
                                } else {
                                    reportReason = ((android.widget.RadioButton) (reasonGroup.findViewById(reasonGroup.getCheckedRadioButtonId()))).getText().toString();
                                }
                                new me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.AsyncReportTask(submission, holder.itemView).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR, reportReason);
                            }
                        }).build();
                        final android.widget.RadioGroup reasonGroup = reportDialog.getCustomView().findViewById(me.ccrama.redditslide.R.id.report_reasons);
                        reasonGroup.setOnCheckedChangeListener(new android.widget.RadioGroup.OnCheckedChangeListener() {
                            @java.lang.Override
                            public void onCheckedChanged(android.widget.RadioGroup group, int checkedId) {
                                if (checkedId == me.ccrama.redditslide.R.id.report_other)
                                    reportDialog.getCustomView().findViewById(me.ccrama.redditslide.R.id.input_report_reason).setVisibility(android.view.View.VISIBLE);
                                else
                                    reportDialog.getCustomView().findViewById(me.ccrama.redditslide.R.id.input_report_reason).setVisibility(android.view.View.GONE);

                            }
                        });
                        // Load sub's report reasons and show the appropriate ones
                        new android.os.AsyncTask<java.lang.Void, java.lang.Void, net.dean.jraw.models.Ruleset>() {
                            @java.lang.Override
                            protected net.dean.jraw.models.Ruleset doInBackground(java.lang.Void... voids) {
                                net.dean.jraw.models.Ruleset rules = me.ccrama.redditslide.Authentication.reddit.getRules(submission.getSubredditName());
                                return rules;
                            }

                            @java.lang.Override
                            protected void onPostExecute(net.dean.jraw.models.Ruleset rules) {
                                reportDialog.getCustomView().findViewById(me.ccrama.redditslide.R.id.report_loading).setVisibility(android.view.View.GONE);
                                if (rules.getSubredditRules().size() > 0) {
                                    android.widget.TextView subHeader = new android.widget.TextView(mContext);
                                    subHeader.setText(mContext.getString(me.ccrama.redditslide.R.string.report_sub_rules, submission.getSubredditName()));
                                    reasonGroup.addView(subHeader, reasonGroup.getChildCount() - 2);
                                }
                                for (net.dean.jraw.models.SubredditRule rule : rules.getSubredditRules()) {
                                    if ((rule.getKind() == net.dean.jraw.models.SubredditRule.RuleKind.LINK) || (rule.getKind() == net.dean.jraw.models.SubredditRule.RuleKind.ALL)) {
                                        android.widget.RadioButton btn = new android.widget.RadioButton(mContext);
                                        btn.setText(rule.getViolationReason());
                                        reasonGroup.addView(btn, reasonGroup.getChildCount() - 2);
                                        btn.getLayoutParams().width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
                                    }
                                }
                                if (rules.getSiteRules().size() > 0) {
                                    android.widget.TextView siteHeader = new android.widget.TextView(mContext);
                                    siteHeader.setText(me.ccrama.redditslide.R.string.report_site_rules);
                                    reasonGroup.addView(siteHeader, reasonGroup.getChildCount() - 2);
                                }
                                for (java.lang.String rule : rules.getSiteRules()) {
                                    android.widget.RadioButton btn = new android.widget.RadioButton(mContext);
                                    btn.setText(rule);
                                    reasonGroup.addView(btn, reasonGroup.getChildCount() - 2);
                                    btn.getLayoutParams().width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
                                }
                            }
                        }.execute();
                        reportDialog.show();
                        break;
                    case 8 :
                        if (me.ccrama.redditslide.SettingValues.shareLongLink) {
                            me.ccrama.redditslide.Reddit.defaultShareText(submission.getTitle(), "https://reddit.com" + submission.getPermalink(), mContext);
                        } else {
                            me.ccrama.redditslide.Reddit.defaultShareText(submission.getTitle(), "https://redd.it/" + submission.getId(), mContext);
                        }
                        break;
                    case 6 :
                        {
                            android.content.ClipboardManager clipboard = ((android.content.ClipboardManager) (mContext.getSystemService(android.content.Context.CLIPBOARD_SERVICE)));
                            android.content.ClipData clip = android.content.ClipData.newPlainText("Link", submission.getUrl());
                            clipboard.setPrimaryClip(clip);
                            android.widget.Toast.makeText(mContext, me.ccrama.redditslide.R.string.submission_link_copied, android.widget.Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 25 :
                        final android.widget.TextView showText = new android.widget.TextView(mContext);
                        showText.setText(org.apache.commons.text.StringEscapeUtils.unescapeHtml4((submission.getTitle() + "\n\n") + submission.getSelftext()));
                        showText.setTextIsSelectable(true);
                        int sixteen = me.ccrama.redditslide.Reddit.dpToPxVertical(24);
                        showText.setPadding(sixteen, 0, sixteen, 0);
                        com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext);
                        builder.setView(showText).setTitle("Select text to copy").setCancelable(true).setPositiveButton("COPY SELECTED", new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                java.lang.String selected = showText.getText().toString().substring(showText.getSelectionStart(), showText.getSelectionEnd());
                                if (!selected.isEmpty()) {
                                    android.content.ClipboardManager clipboard = ((android.content.ClipboardManager) (mContext.getSystemService(android.content.Context.CLIPBOARD_SERVICE)));
                                    android.content.ClipData clip = android.content.ClipData.newPlainText("Selftext", selected);
                                    clipboard.setPrimaryClip(clip);
                                    android.widget.Toast.makeText(mContext, me.ccrama.redditslide.R.string.submission_comment_copied, android.widget.Toast.LENGTH_SHORT).show();
                                } else {
                                    android.content.ClipboardManager clipboard = ((android.content.ClipboardManager) (mContext.getSystemService(android.content.Context.CLIPBOARD_SERVICE)));
                                    android.content.ClipData clip = android.content.ClipData.newPlainText("Selftext", android.text.Html.fromHtml((submission.getTitle() + "\n\n") + submission.getSelftext()));
                                    clipboard.setPrimaryClip(clip);
                                    android.widget.Toast.makeText(mContext, me.ccrama.redditslide.R.string.submission_comment_copied, android.widget.Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).setNegativeButton(me.ccrama.redditslide.R.string.btn_cancel, null).setNeutralButton("COPY ALL", new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                android.content.ClipboardManager clipboard = ((android.content.ClipboardManager) (mContext.getSystemService(android.content.Context.CLIPBOARD_SERVICE)));
                                android.content.ClipData clip = android.content.ClipData.newPlainText("Selftext", android.text.Html.fromHtml((submission.getTitle() + "\n\n") + submission.getSelftext()));
                                clipboard.setPrimaryClip(clip);
                                android.widget.Toast.makeText(mContext, me.ccrama.redditslide.R.string.submission_comment_copied, android.widget.Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                        break;
                }
            }
        });
        b.show();
    }

    private void saveSubmission(final net.dean.jraw.models.Submission submission, final android.app.Activity mContext, final me.ccrama.redditslide.Adapters.SubmissionViewHolder holder, final boolean full) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
            @java.lang.Override
            protected java.lang.Void doInBackground(java.lang.Void... params) {
                try {
                    if (me.ccrama.redditslide.ActionStates.isSaved(submission)) {
                        new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).unsave(submission);
                        me.ccrama.redditslide.ActionStates.setSaved(submission, false);
                    } else {
                        new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).save(submission);
                        me.ccrama.redditslide.ActionStates.setSaved(submission, true);
                    }
                } catch (java.lang.Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @java.lang.Override
            protected void onPostExecute(java.lang.Void aVoid) {
                android.support.design.widget.Snackbar s;
                try {
                    if (me.ccrama.redditslide.ActionStates.isSaved(submission)) {
                        ((android.widget.ImageView) (holder.save)).setColorFilter(android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_amber_500), android.graphics.PorterDuff.Mode.SRC_ATOP);
                        ((android.widget.ImageView) (holder.save)).setContentDescription(mContext.getString(me.ccrama.redditslide.R.string.btn_unsave));
                        s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.submission_info_saved, android.support.design.widget.Snackbar.LENGTH_LONG);
                        if (me.ccrama.redditslide.Authentication.me.hasGold()) {
                            s.setAction(me.ccrama.redditslide.R.string.category_categorize, new android.view.View.OnClickListener() {
                                @java.lang.Override
                                public void onClick(android.view.View v) {
                                    categorizeSaved(submission, holder.itemView, mContext);
                                }
                            });
                        }
                        me.ccrama.redditslide.Views.AnimateHelper.setFlashAnimation(holder.itemView, holder.save, android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_amber_500));
                    } else {
                        s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.submission_info_unsaved, android.support.design.widget.Snackbar.LENGTH_SHORT);
                        ((android.widget.ImageView) (holder.save)).setColorFilter(((holder.itemView.getTag(holder.itemView.getId()) != null) && holder.itemView.getTag(holder.itemView.getId()).equals("none")) || full ? me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.getCurrentTintColor(mContext) : me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.getWhiteTintColor(), android.graphics.PorterDuff.Mode.SRC_ATOP);
                        ((android.widget.ImageView) (holder.save)).setContentDescription(mContext.getString(me.ccrama.redditslide.R.string.btn_save));
                    }
                    android.view.View view = s.getView();
                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(android.graphics.Color.WHITE);
                    s.show();
                } catch (java.lang.Exception ignored) {
                }
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void categorizeSaved(final net.dean.jraw.models.Submission submission, android.view.View itemView, final android.content.Context mContext) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.util.List<java.lang.String>>() {
            android.app.Dialog d;

            @java.lang.Override
            public void onPreExecute() {
                d = new com.afollestad.materialdialogs.MaterialDialog.Builder(mContext).progress(true, 100).title(me.ccrama.redditslide.R.string.profile_category_loading).content(me.ccrama.redditslide.R.string.misc_please_wait).show();
            }

            @java.lang.Override
            protected java.util.List<java.lang.String> doInBackground(java.lang.Void... params) {
                try {
                    java.util.List<java.lang.String> categories = new java.util.ArrayList<java.lang.String>(new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).getSavedCategories());
                    categories.add("New category");
                    return categories;
                } catch (java.lang.Exception e) {
                    e.printStackTrace();
                    return new java.util.ArrayList<java.lang.String>() {
                        {
                            add("New category");
                        }
                    };
                    // sub probably has no flairs?
                }
            }

            @java.lang.Override
            public void onPostExecute(final java.util.List<java.lang.String> data) {
                try {
                    new com.afollestad.materialdialogs.MaterialDialog.Builder(mContext).items(data).title(me.ccrama.redditslide.R.string.sidebar_select_flair).itemsCallback(new com.afollestad.materialdialogs.MaterialDialog.ListCallback() {
                        @java.lang.Override
                        public void onSelection(com.afollestad.materialdialogs.MaterialDialog dialog, final android.view.View itemView, int which, java.lang.CharSequence text) {
                            final java.lang.String t = data.get(which);
                            if (which == (data.size() - 1)) {
                                new com.afollestad.materialdialogs.MaterialDialog.Builder(mContext).title(me.ccrama.redditslide.R.string.category_set_name).input(mContext.getString(me.ccrama.redditslide.R.string.category_set_name_hint), null, false, new com.afollestad.materialdialogs.MaterialDialog.InputCallback() {
                                    @java.lang.Override
                                    public void onInput(com.afollestad.materialdialogs.MaterialDialog dialog, java.lang.CharSequence input) {
                                    }
                                }).positiveText(me.ccrama.redditslide.R.string.btn_set).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                                    @java.lang.Override
                                    public void onClick(com.afollestad.materialdialogs.MaterialDialog dialog, com.afollestad.materialdialogs.DialogAction which) {
                                        final java.lang.String flair = dialog.getInputEditText().getText().toString();
                                        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
                                            @java.lang.Override
                                            protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                                                try {
                                                    new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).save(submission, flair);
                                                    return true;
                                                } catch (net.dean.jraw.ApiException e) {
                                                    e.printStackTrace();
                                                    return false;
                                                }
                                            }

                                            @java.lang.Override
                                            protected void onPostExecute(java.lang.Boolean done) {
                                                android.support.design.widget.Snackbar s;
                                                if (done) {
                                                    if (itemView != null) {
                                                        s = android.support.design.widget.Snackbar.make(itemView, me.ccrama.redditslide.R.string.submission_info_saved, android.support.design.widget.Snackbar.LENGTH_SHORT);
                                                        android.view.View view = s.getView();
                                                        android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                                                        tv.setTextColor(android.graphics.Color.WHITE);
                                                        s.show();
                                                    }
                                                } else if (itemView != null) {
                                                    s = android.support.design.widget.Snackbar.make(itemView, me.ccrama.redditslide.R.string.category_set_error, android.support.design.widget.Snackbar.LENGTH_SHORT);
                                                    android.view.View view = s.getView();
                                                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                                                    tv.setTextColor(android.graphics.Color.WHITE);
                                                    s.show();
                                                }
                                            }
                                        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                                    }
                                }).negativeText(me.ccrama.redditslide.R.string.btn_cancel).show();
                            } else {
                                new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
                                    @java.lang.Override
                                    protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                                        try {
                                            new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).save(submission, t);
                                            return true;
                                        } catch (net.dean.jraw.ApiException e) {
                                            e.printStackTrace();
                                            return false;
                                        }
                                    }

                                    @java.lang.Override
                                    protected void onPostExecute(java.lang.Boolean done) {
                                        android.support.design.widget.Snackbar s;
                                        if (done) {
                                            if (itemView != null) {
                                                s = android.support.design.widget.Snackbar.make(itemView, me.ccrama.redditslide.R.string.submission_info_saved, android.support.design.widget.Snackbar.LENGTH_SHORT);
                                                android.view.View view = s.getView();
                                                android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                                                tv.setTextColor(android.graphics.Color.WHITE);
                                                s.show();
                                            }
                                        } else if (itemView != null) {
                                            s = android.support.design.widget.Snackbar.make(itemView, me.ccrama.redditslide.R.string.category_set_error, android.support.design.widget.Snackbar.LENGTH_SHORT);
                                            android.view.View view = s.getView();
                                            android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                                            tv.setTextColor(android.graphics.Color.WHITE);
                                            s.show();
                                        }
                                    }
                                }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        }
                    }).show();
                    if (d != null) {
                        d.dismiss();
                    }
                } catch (java.lang.Exception ignored) {
                }
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public <T extends net.dean.jraw.models.Contribution> void hideSubmission(final net.dean.jraw.models.Submission submission, final java.util.List<T> posts, final java.lang.String baseSub, final android.support.v7.widget.RecyclerView recyclerview, android.content.Context c) {
        final int pos = posts.indexOf(submission);
        if (pos != (-1)) {
            if (submission.isHidden()) {
                posts.remove(pos);
                me.ccrama.redditslide.Hidden.undoHidden(submission);
                recyclerview.getAdapter().notifyItemRemoved(pos + 1);
                android.support.design.widget.Snackbar snack = android.support.design.widget.Snackbar.make(recyclerview, me.ccrama.redditslide.R.string.submission_info_unhidden, android.support.design.widget.Snackbar.LENGTH_LONG);
                android.view.View view = snack.getView();
                android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(android.graphics.Color.WHITE);
                snack.show();
            } else {
                final T t = posts.get(pos);
                posts.remove(pos);
                me.ccrama.redditslide.Hidden.setHidden(t);
                final me.ccrama.redditslide.OfflineSubreddit s;
                boolean success = false;
                if (baseSub != null) {
                    s = me.ccrama.redditslide.OfflineSubreddit.getSubreddit(baseSub, false, c);
                    try {
                        s.hide(pos);
                        success = true;
                    } catch (java.lang.Exception e) {
                    }
                } else {
                    success = false;
                    s = null;
                }
                recyclerview.getAdapter().notifyItemRemoved(pos + 1);
                final boolean finalSuccess = success;
                android.support.design.widget.Snackbar snack = android.support.design.widget.Snackbar.make(recyclerview, me.ccrama.redditslide.R.string.submission_info_hidden, android.support.design.widget.Snackbar.LENGTH_LONG).setAction(me.ccrama.redditslide.R.string.btn_undo, new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        if (((baseSub != null) && (s != null)) && finalSuccess) {
                            s.unhideLast();
                        }
                        posts.add(pos, t);
                        recyclerview.getAdapter().notifyItemInserted(pos + 1);
                        me.ccrama.redditslide.Hidden.undoHidden(t);
                    }
                });
                android.view.View view = snack.getView();
                android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(android.graphics.Color.WHITE);
                snack.show();
            }
        }
    }

    public <T extends net.dean.jraw.models.Contribution> void showModBottomSheet(final android.app.Activity mContext, final net.dean.jraw.models.Submission submission, final java.util.List<T> posts, final me.ccrama.redditslide.Adapters.SubmissionViewHolder holder, final android.support.v7.widget.RecyclerView recyclerview, final java.util.Map<java.lang.String, java.lang.Integer> reports, final java.util.Map<java.lang.String, java.lang.String> reports2) {
        final android.content.res.Resources res = mContext.getResources();
        int[] attrs = new int[]{ me.ccrama.redditslide.R.attr.tintColor };
        android.content.res.TypedArray ta = mContext.obtainStyledAttributes(attrs);
        int color = ta.getColor(0, android.graphics.Color.WHITE);
        android.graphics.drawable.Drawable profile = android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.profile, null);
        final android.graphics.drawable.Drawable report = android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.report, null);
        final android.graphics.drawable.Drawable approve = android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.support, null);
        final android.graphics.drawable.Drawable nsfw = android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.hide, null);
        final android.graphics.drawable.Drawable spoiler = android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.spoil, null);
        final android.graphics.drawable.Drawable pin = android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.sub, null);
        final android.graphics.drawable.Drawable lock = android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.lock, null);
        final android.graphics.drawable.Drawable flair = android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.ic_format_quote_white_48dp, null);
        final android.graphics.drawable.Drawable remove = android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.close, null);
        final android.graphics.drawable.Drawable remove_reason = android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.reportreason, null);
        final android.graphics.drawable.Drawable ban = android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.ban, null);
        final android.graphics.drawable.Drawable spam = android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.spam, null);
        final android.graphics.drawable.Drawable distinguish = android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.iconstarfilled, null);
        final android.graphics.drawable.Drawable note = android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.note, null);
        profile.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        report.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        approve.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        spam.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        nsfw.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        pin.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        flair.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        remove.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        spoiler.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        remove_reason.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        ban.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        spam.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        distinguish.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        lock.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        note.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        ta.recycle();
        com.cocosw.bottomsheet.BottomSheet.Builder b = new com.cocosw.bottomsheet.BottomSheet.Builder(mContext).title(android.text.Html.fromHtml(submission.getTitle()));
        int reportCount = reports.size() + reports2.size();
        b.sheet(0, report, res.getQuantityString(me.ccrama.redditslide.R.plurals.mod_btn_reports, reportCount, reportCount));
        if (me.ccrama.redditslide.SettingValues.toolboxEnabled) {
            b.sheet(24, note, res.getString(me.ccrama.redditslide.R.string.mod_usernotes_view));
        }
        boolean approved = false;
        java.lang.String whoApproved = "";
        b.sheet(1, approve, res.getString(me.ccrama.redditslide.R.string.mod_btn_approve));
        b.sheet(6, remove, mContext.getString(me.ccrama.redditslide.R.string.mod_btn_remove)).sheet(7, remove_reason, res.getString(me.ccrama.redditslide.R.string.mod_btn_remove_reason)).sheet(30, spam, res.getString(me.ccrama.redditslide.R.string.mod_btn_spam));
        // b.sheet(2, spam, mContext.getString(R.string.mod_btn_spam)) todo this
        b.sheet(20, flair, res.getString(me.ccrama.redditslide.R.string.mod_btn_submission_flair));
        final boolean isNsfw = submission.isNsfw();
        if (isNsfw) {
            b.sheet(3, nsfw, res.getString(me.ccrama.redditslide.R.string.mod_btn_unmark_nsfw));
        } else {
            b.sheet(3, nsfw, res.getString(me.ccrama.redditslide.R.string.mod_btn_mark_nsfw));
        }
        final boolean isSpoiler = submission.getDataNode().get("spoiler").asBoolean();
        if (isSpoiler) {
            b.sheet(12, nsfw, res.getString(me.ccrama.redditslide.R.string.mod_btn_unmark_spoiler));
        } else {
            b.sheet(12, nsfw, res.getString(me.ccrama.redditslide.R.string.mod_btn_mark_spoiler));
        }
        final boolean locked = submission.isLocked();
        if (locked) {
            b.sheet(9, lock, "Unlock thread");
        } else {
            b.sheet(9, lock, "Lock thread");
        }
        final boolean stickied = submission.isStickied();
        if (!me.ccrama.redditslide.SubmissionCache.removed.contains(submission.getFullName())) {
            if (stickied) {
                b.sheet(4, pin, res.getString(me.ccrama.redditslide.R.string.mod_btn_unpin));
            } else {
                b.sheet(4, pin, res.getString(me.ccrama.redditslide.R.string.mod_btn_pin));
            }
        }
        final boolean distinguished = (submission.getDistinguishedStatus() == net.dean.jraw.models.DistinguishedStatus.MODERATOR) || (submission.getDistinguishedStatus() == net.dean.jraw.models.DistinguishedStatus.ADMIN);
        if (submission.getAuthor().equalsIgnoreCase(me.ccrama.redditslide.Authentication.name)) {
            if (distinguished) {
                b.sheet(5, distinguish, "Undistingiush");
            } else {
                b.sheet(5, distinguish, "Distinguish");
            }
        }
        final java.lang.String finalWhoApproved = whoApproved;
        final boolean finalApproved = approved;
        b.sheet(8, profile, res.getString(me.ccrama.redditslide.R.string.mod_btn_author));
        b.sheet(23, ban, mContext.getString(me.ccrama.redditslide.R.string.mod_ban_user));
        b.listener(new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                switch (which) {
                    case 0 :
                        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.util.ArrayList<java.lang.String>>() {
                            @java.lang.Override
                            protected java.util.ArrayList<java.lang.String> doInBackground(java.lang.Void... params) {
                                java.util.ArrayList<java.lang.String> finalReports = new java.util.ArrayList<>();
                                for (java.util.Map.Entry<java.lang.String, java.lang.Integer> entry : reports.entrySet()) {
                                    finalReports.add((entry.getValue() + "× ") + entry.getKey());
                                }
                                for (java.util.Map.Entry<java.lang.String, java.lang.String> entry : reports2.entrySet()) {
                                    finalReports.add((entry.getKey() + ": ") + entry.getValue());
                                }
                                if (finalReports.isEmpty()) {
                                    finalReports.add(mContext.getString(me.ccrama.redditslide.R.string.mod_no_reports));
                                }
                                return finalReports;
                            }

                            @java.lang.Override
                            public void onPostExecute(java.util.ArrayList<java.lang.String> data) {
                                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.mod_reports).setItems(data.toArray(new java.lang.CharSequence[data.size()]), new android.content.DialogInterface.OnClickListener() {
                                    @java.lang.Override
                                    public void onClick(android.content.DialogInterface dialog, int which) {
                                    }
                                }).show();
                            }
                        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                        break;
                    case 1 :
                        if (finalApproved) {
                            android.content.Intent i = new android.content.Intent(mContext, me.ccrama.redditslide.Activities.Profile.class);
                            i.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_PROFILE, finalWhoApproved);
                            mContext.startActivity(i);
                        } else {
                            approveSubmission(mContext, posts, submission, recyclerview, holder);
                        }
                        break;
                    case 2 :
                        // todo this
                        break;
                    case 3 :
                        if (isNsfw) {
                            unNsfwSubmission(mContext, submission, holder);
                        } else {
                            setPostNsfw(mContext, submission, holder);
                        }
                        break;
                    case 12 :
                        if (isSpoiler) {
                            unSpoiler(mContext, submission, holder);
                        } else {
                            setSpoiler(mContext, submission, holder);
                        }
                        break;
                    case 9 :
                        if (locked) {
                            unLockSubmission(mContext, submission, holder);
                        } else {
                            lockSubmission(mContext, submission, holder);
                        }
                        break;
                    case 4 :
                        if (stickied) {
                            unStickySubmission(mContext, submission, holder);
                        } else {
                            stickySubmission(mContext, submission, holder);
                        }
                        break;
                    case 5 :
                        if (distinguished) {
                            unDistinguishSubmission(mContext, submission, holder);
                        } else {
                            distinguishSubmission(mContext, submission, holder);
                        }
                        break;
                    case 6 :
                        removeSubmission(mContext, submission, posts, recyclerview, holder, false);
                        break;
                    case 7 :
                        if ((me.ccrama.redditslide.SettingValues.removalReasonType == me.ccrama.redditslide.SettingValues.RemovalReasonType.TOOLBOX.ordinal()) && me.ccrama.redditslide.Toolbox.ToolboxUI.canShowRemoval(submission.getSubredditName())) {
                            me.ccrama.redditslide.Toolbox.ToolboxUI.showRemoval(mContext, submission, new me.ccrama.redditslide.Toolbox.ToolboxUI.CompletedRemovalCallback() {
                                @java.lang.Override
                                public void onComplete(boolean success) {
                                    if (success) {
                                        me.ccrama.redditslide.SubmissionCache.removed.add(submission.getFullName());
                                        me.ccrama.redditslide.SubmissionCache.approved.remove(submission.getFullName());
                                        me.ccrama.redditslide.SubmissionCache.updateInfoSpannable(submission, mContext, submission.getSubredditName());
                                        if (mContext instanceof me.ccrama.redditslide.Activities.ModQueue) {
                                            final int pos = posts.indexOf(submission);
                                            posts.remove(submission);
                                            if (pos == 0) {
                                                recyclerview.getAdapter().notifyDataSetChanged();
                                            } else {
                                                recyclerview.getAdapter().notifyItemRemoved(pos + 1);
                                            }
                                        } else {
                                            recyclerview.getAdapter().notifyItemChanged(holder.getAdapterPosition());
                                        }
                                        android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.submission_removed, android.support.design.widget.Snackbar.LENGTH_LONG);
                                        android.view.View view = s.getView();
                                        android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                                        tv.setTextColor(android.graphics.Color.WHITE);
                                        s.show();
                                    } else {
                                        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.err_general).setMessage(me.ccrama.redditslide.R.string.err_retry_later).show();
                                    }
                                }
                            });
                        } else {
                            // Show a Slide reason dialog if we can't show a toolbox or reddit one
                            doRemoveSubmissionReason(mContext, submission, posts, recyclerview, holder);
                        }
                        break;
                    case 30 :
                        removeSubmission(mContext, submission, posts, recyclerview, holder, true);
                        break;
                    case 8 :
                        android.content.Intent i = new android.content.Intent(mContext, me.ccrama.redditslide.Activities.Profile.class);
                        i.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_PROFILE, submission.getAuthor());
                        mContext.startActivity(i);
                        break;
                    case 20 :
                        doSetFlair(mContext, submission, holder);
                        break;
                    case 23 :
                        // ban a user
                        showBan(mContext, holder.itemView, submission, "", "", "", "");
                        break;
                    case 24 :
                        me.ccrama.redditslide.Toolbox.ToolboxUI.showUsernotes(mContext, submission.getAuthor(), submission.getSubredditName(), "l," + submission.getId());
                        break;
                }
            }
        });
        b.show();
    }

    private <T extends net.dean.jraw.models.Contribution> void doRemoveSubmissionReason(final android.app.Activity mContext, final net.dean.jraw.models.Submission submission, final java.util.List<T> posts, final android.support.v7.widget.RecyclerView recyclerview, final me.ccrama.redditslide.Adapters.SubmissionViewHolder holder) {
        reason = "";
        new com.afollestad.materialdialogs.MaterialDialog.Builder(mContext).title(me.ccrama.redditslide.R.string.mod_remove_title).positiveText(me.ccrama.redditslide.R.string.btn_remove).alwaysCallInputCallback().input(mContext.getString(me.ccrama.redditslide.R.string.mod_remove_hint), mContext.getString(me.ccrama.redditslide.R.string.mod_remove_template), false, new com.afollestad.materialdialogs.MaterialDialog.InputCallback() {
            @java.lang.Override
            public void onInput(com.afollestad.materialdialogs.MaterialDialog dialog, java.lang.CharSequence input) {
                reason = input.toString();
            }
        }).inputType(android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES).neutralText(me.ccrama.redditslide.R.string.mod_remove_insert_draft).onNeutral(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
            @java.lang.Override
            public void onClick(@android.support.annotation.NonNull
            com.afollestad.materialdialogs.MaterialDialog dialog, @android.support.annotation.NonNull
            com.afollestad.materialdialogs.DialogAction which) {
            }
        }).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
            @java.lang.Override
            public void onClick(final com.afollestad.materialdialogs.MaterialDialog dialog, com.afollestad.materialdialogs.DialogAction which) {
                removeSubmissionReason(submission, mContext, posts, reason, holder, recyclerview);
            }
        }).negativeText(me.ccrama.redditslide.R.string.btn_cancel).onNegative(null).show();
    }

    private <T extends net.dean.jraw.models.Contribution> void removeSubmissionReason(final net.dean.jraw.models.Submission submission, final android.app.Activity mContext, final java.util.List<T> posts, final java.lang.String reason, final me.ccrama.redditslide.Adapters.SubmissionViewHolder holder, final android.support.v7.widget.RecyclerView recyclerview) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
            @java.lang.Override
            public void onPostExecute(java.lang.Boolean b) {
                if (b) {
                    me.ccrama.redditslide.SubmissionCache.removed.add(submission.getFullName());
                    me.ccrama.redditslide.SubmissionCache.approved.remove(submission.getFullName());
                    me.ccrama.redditslide.SubmissionCache.updateInfoSpannable(submission, mContext, submission.getSubredditName());
                    if (mContext instanceof me.ccrama.redditslide.Activities.ModQueue) {
                        final int pos = posts.indexOf(submission);
                        posts.remove(submission);
                        if (pos == 0) {
                            recyclerview.getAdapter().notifyDataSetChanged();
                        } else {
                            recyclerview.getAdapter().notifyItemRemoved(pos + 1);
                        }
                    } else {
                        recyclerview.getAdapter().notifyItemChanged(holder.getAdapterPosition());
                    }
                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.submission_removed, android.support.design.widget.Snackbar.LENGTH_LONG);
                    android.view.View view = s.getView();
                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(android.graphics.Color.WHITE);
                    s.show();
                } else {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.err_general).setMessage(me.ccrama.redditslide.R.string.err_retry_later).show();
                }
            }

            @java.lang.Override
            protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                try {
                    java.lang.String toDistinguish = new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).reply(submission, reason);
                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).remove(submission, false);
                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).setDistinguishedStatus(me.ccrama.redditslide.Authentication.reddit.get("t1_" + toDistinguish).get(0), net.dean.jraw.models.DistinguishedStatus.MODERATOR);
                } catch (net.dean.jraw.ApiException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private <T extends net.dean.jraw.models.Contribution> void removeSubmission(final android.app.Activity mContext, final net.dean.jraw.models.Submission submission, final java.util.List<T> posts, final android.support.v7.widget.RecyclerView recyclerview, final me.ccrama.redditslide.Adapters.SubmissionViewHolder holder, final boolean spam) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
            @java.lang.Override
            public void onPostExecute(java.lang.Boolean b) {
                me.ccrama.redditslide.SubmissionCache.removed.add(submission.getFullName());
                me.ccrama.redditslide.SubmissionCache.approved.remove(submission.getFullName());
                me.ccrama.redditslide.SubmissionCache.updateInfoSpannable(submission, mContext, submission.getSubredditName());
                if (b) {
                    if (mContext instanceof me.ccrama.redditslide.Activities.ModQueue) {
                        final int pos = posts.indexOf(submission);
                        posts.remove(submission);
                        if (pos == 0) {
                            recyclerview.getAdapter().notifyDataSetChanged();
                        } else {
                            recyclerview.getAdapter().notifyItemRemoved(pos + 1);
                        }
                    } else {
                        recyclerview.getAdapter().notifyItemChanged(holder.getAdapterPosition());
                    }
                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.submission_removed, android.support.design.widget.Snackbar.LENGTH_LONG);
                    android.view.View view = s.getView();
                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(android.graphics.Color.WHITE);
                    s.show();
                } else {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.err_general).setMessage(me.ccrama.redditslide.R.string.err_retry_later).show();
                }
            }

            @java.lang.Override
            protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                try {
                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).remove(submission, spam);
                } catch (net.dean.jraw.ApiException | net.dean.jraw.http.NetworkException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void doSetFlair(final android.app.Activity mContext, final net.dean.jraw.models.Submission submission, final me.ccrama.redditslide.Adapters.SubmissionViewHolder holder) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.util.ArrayList<java.lang.String>>() {
            java.util.ArrayList<net.dean.jraw.models.FlairTemplate> flair;

            @java.lang.Override
            protected java.util.ArrayList<java.lang.String> doInBackground(java.lang.Void... params) {
                net.dean.jraw.fluent.FlairReference allFlairs = new net.dean.jraw.fluent.FluentRedditClient(me.ccrama.redditslide.Authentication.reddit).subreddit(submission.getSubredditName()).flair();
                try {
                    flair = new java.util.ArrayList<>(allFlairs.options(submission));
                    final java.util.ArrayList<java.lang.String> finalFlairs = new java.util.ArrayList<>();
                    for (net.dean.jraw.models.FlairTemplate temp : flair) {
                        finalFlairs.add(temp.getText());
                    }
                    return finalFlairs;
                } catch (java.lang.Exception e) {
                    e.printStackTrace();
                    // sub probably has no flairs?
                }
                return null;
            }

            @java.lang.Override
            public void onPostExecute(final java.util.ArrayList<java.lang.String> data) {
                try {
                    if (data.isEmpty()) {
                        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.mod_flair_none_found).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, null).show();
                    } else {
                        showFlairSelectionDialog(mContext, submission, data, flair, holder);
                    }
                } catch (java.lang.Exception ignored) {
                }
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void showFlairSelectionDialog(final android.app.Activity mContext, final net.dean.jraw.models.Submission submission, java.util.ArrayList<java.lang.String> data, final java.util.ArrayList<net.dean.jraw.models.FlairTemplate> flair, final me.ccrama.redditslide.Adapters.SubmissionViewHolder holder) {
        new com.afollestad.materialdialogs.MaterialDialog.Builder(mContext).items(data).title(me.ccrama.redditslide.R.string.sidebar_select_flair).itemsCallback(new com.afollestad.materialdialogs.MaterialDialog.ListCallback() {
            @java.lang.Override
            public void onSelection(com.afollestad.materialdialogs.MaterialDialog dialog, android.view.View itemView, int which, java.lang.CharSequence text) {
                final net.dean.jraw.models.FlairTemplate t = flair.get(which);
                if (t.isTextEditable()) {
                    showFlairEditDialog(mContext, submission, t, holder);
                } else {
                    setFlair(mContext, null, submission, t, holder);
                }
            }
        }).show();
    }

    private void showFlairEditDialog(final android.app.Activity mContext, final net.dean.jraw.models.Submission submission, final net.dean.jraw.models.FlairTemplate t, final me.ccrama.redditslide.Adapters.SubmissionViewHolder holder) {
        new com.afollestad.materialdialogs.MaterialDialog.Builder(mContext).title(me.ccrama.redditslide.R.string.sidebar_select_flair_text).input(mContext.getString(me.ccrama.redditslide.R.string.mod_flair_hint), t.getText(), true, new com.afollestad.materialdialogs.MaterialDialog.InputCallback() {
            @java.lang.Override
            public void onInput(com.afollestad.materialdialogs.MaterialDialog dialog, java.lang.CharSequence input) {
            }
        }).positiveText(me.ccrama.redditslide.R.string.btn_set).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
            @java.lang.Override
            public void onClick(com.afollestad.materialdialogs.MaterialDialog dialog, com.afollestad.materialdialogs.DialogAction which) {
                final java.lang.String flair = dialog.getInputEditText().getText().toString();
                setFlair(mContext, flair, submission, t, holder);
            }
        }).negativeText(me.ccrama.redditslide.R.string.btn_cancel).show();
    }

    private void setFlair(final android.content.Context mContext, final java.lang.String flair, final net.dean.jraw.models.Submission submission, final net.dean.jraw.models.FlairTemplate t, final me.ccrama.redditslide.Adapters.SubmissionViewHolder holder) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
            @java.lang.Override
            protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                try {
                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).setFlair(submission.getSubredditName(), t, flair, submission);
                    return true;
                } catch (net.dean.jraw.ApiException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @java.lang.Override
            protected void onPostExecute(java.lang.Boolean done) {
                android.support.design.widget.Snackbar s = null;
                if (done) {
                    if (holder.itemView != null) {
                        s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.snackbar_flair_success, android.support.design.widget.Snackbar.LENGTH_SHORT);
                    }
                    if (holder.itemView != null) {
                        me.ccrama.redditslide.SubmissionCache.updateTitleFlair(submission, flair, mContext);
                        doText(holder, submission, mContext, submission.getSubredditName(), false);
                    }
                } else if (holder.itemView != null) {
                    s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.snackbar_flair_error, android.support.design.widget.Snackbar.LENGTH_SHORT);
                }
                if (s != null) {
                    android.view.View view = s.getView();
                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(android.graphics.Color.WHITE);
                    s.show();
                }
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void doText(me.ccrama.redditslide.Adapters.SubmissionViewHolder holder, net.dean.jraw.models.Submission submission, android.content.Context mContext, java.lang.String baseSub, boolean full) {
        android.text.SpannableStringBuilder t = me.ccrama.redditslide.SubmissionCache.getTitleLine(submission, mContext);
        android.text.SpannableStringBuilder l = me.ccrama.redditslide.SubmissionCache.getInfoLine(submission, mContext, baseSub);
        android.text.SpannableStringBuilder c = me.ccrama.redditslide.SubmissionCache.getCrosspostLine(submission, mContext);
        int[] textSizeAttr = new int[]{ me.ccrama.redditslide.R.attr.font_cardtitle, me.ccrama.redditslide.R.attr.font_cardinfo };
        android.content.res.TypedArray a = mContext.obtainStyledAttributes(textSizeAttr);
        int textSizeT = a.getDimensionPixelSize(0, 18);
        int textSizeI = a.getDimensionPixelSize(1, 14);
        t.setSpan(new android.text.style.AbsoluteSizeSpan(textSizeT), 0, t.length(), 0);
        l.setSpan(new android.text.style.AbsoluteSizeSpan(textSizeI), 0, l.length(), 0);
        android.text.SpannableStringBuilder s = new android.text.SpannableStringBuilder();
        if (me.ccrama.redditslide.SettingValues.titleTop) {
            s.append(t);
            s.append("\n");
            s.append(l);
        } else {
            s.append(l);
            s.append("\n");
            s.append(t);
        }
        if ((!full) && (c != null)) {
            c.setSpan(new android.text.style.AbsoluteSizeSpan(textSizeI), 0, c.length(), 0);
            s.append("\n");
            s.append(c);
        }
        a.recycle();
        holder.title.setText(s);
    }

    private void stickySubmission(final android.app.Activity mContext, final net.dean.jraw.models.Submission submission, final me.ccrama.redditslide.Adapters.SubmissionViewHolder holder) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
            @java.lang.Override
            public void onPostExecute(java.lang.Boolean b) {
                if (b) {
                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.really_pin_submission_message, android.support.design.widget.Snackbar.LENGTH_LONG);
                    android.view.View view = s.getView();
                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(android.graphics.Color.WHITE);
                    s.show();
                } else {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.err_general).setMessage(me.ccrama.redditslide.R.string.err_retry_later).show();
                }
            }

            @java.lang.Override
            protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                try {
                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).setSticky(submission, true);
                } catch (net.dean.jraw.ApiException | net.dean.jraw.http.NetworkException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void unStickySubmission(final android.app.Activity mContext, final net.dean.jraw.models.Submission submission, final me.ccrama.redditslide.Adapters.SubmissionViewHolder holder) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
            @java.lang.Override
            public void onPostExecute(java.lang.Boolean b) {
                if (b) {
                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.really_unpin_submission_message, android.support.design.widget.Snackbar.LENGTH_LONG);
                    android.view.View view = s.getView();
                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(android.graphics.Color.WHITE);
                    s.show();
                } else {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.err_general).setMessage(me.ccrama.redditslide.R.string.err_retry_later).show();
                }
            }

            @java.lang.Override
            protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                try {
                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).setSticky(submission, false);
                } catch (net.dean.jraw.ApiException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void lockSubmission(final android.app.Activity mContext, final net.dean.jraw.models.Submission submission, final me.ccrama.redditslide.Adapters.SubmissionViewHolder holder) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
            @java.lang.Override
            public void onPostExecute(java.lang.Boolean b) {
                if (b) {
                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(holder.itemView, "Thread locked", android.support.design.widget.Snackbar.LENGTH_LONG);
                    android.view.View view = s.getView();
                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(android.graphics.Color.WHITE);
                    s.show();
                } else {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.err_general).setMessage(me.ccrama.redditslide.R.string.err_retry_later).show();
                }
            }

            @java.lang.Override
            protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                try {
                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).setLocked(submission);
                } catch (net.dean.jraw.ApiException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void unLockSubmission(final android.app.Activity mContext, final net.dean.jraw.models.Submission submission, final me.ccrama.redditslide.Adapters.SubmissionViewHolder holder) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
            @java.lang.Override
            public void onPostExecute(java.lang.Boolean b) {
                if (b) {
                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(holder.itemView, "Thread unlocked", android.support.design.widget.Snackbar.LENGTH_LONG);
                    android.view.View view = s.getView();
                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(android.graphics.Color.WHITE);
                    s.show();
                } else {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.err_general).setMessage(me.ccrama.redditslide.R.string.err_retry_later).show();
                }
            }

            @java.lang.Override
            protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                try {
                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).setUnlocked(submission);
                } catch (net.dean.jraw.ApiException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void distinguishSubmission(final android.app.Activity mContext, final net.dean.jraw.models.Submission submission, final me.ccrama.redditslide.Adapters.SubmissionViewHolder holder) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
            @java.lang.Override
            public void onPostExecute(java.lang.Boolean b) {
                if (b) {
                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(holder.itemView, "Submission distinguished", android.support.design.widget.Snackbar.LENGTH_LONG);
                    android.view.View view = s.getView();
                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(android.graphics.Color.WHITE);
                    s.show();
                } else {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.err_general).setMessage(me.ccrama.redditslide.R.string.err_retry_later).show();
                }
            }

            @java.lang.Override
            protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                try {
                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).setDistinguishedStatus(submission, net.dean.jraw.models.DistinguishedStatus.MODERATOR);
                } catch (net.dean.jraw.ApiException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void unDistinguishSubmission(final android.app.Activity mContext, final net.dean.jraw.models.Submission submission, final me.ccrama.redditslide.Adapters.SubmissionViewHolder holder) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
            @java.lang.Override
            public void onPostExecute(java.lang.Boolean b) {
                if (b) {
                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(holder.itemView, "Submission distinguish removed", android.support.design.widget.Snackbar.LENGTH_LONG);
                    android.view.View view = s.getView();
                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(android.graphics.Color.WHITE);
                    s.show();
                } else {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.err_general).setMessage(me.ccrama.redditslide.R.string.err_retry_later).show();
                }
            }

            @java.lang.Override
            protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                try {
                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).setDistinguishedStatus(submission, net.dean.jraw.models.DistinguishedStatus.MODERATOR);
                } catch (net.dean.jraw.ApiException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setPostNsfw(final android.app.Activity mContext, final net.dean.jraw.models.Submission submission, final me.ccrama.redditslide.Adapters.SubmissionViewHolder holder) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
            @java.lang.Override
            public void onPostExecute(java.lang.Boolean b) {
                if (b) {
                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(holder.itemView, "NSFW status set", android.support.design.widget.Snackbar.LENGTH_LONG);
                    android.view.View view = s.getView();
                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(android.graphics.Color.WHITE);
                    s.show();
                } else {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.err_general).setMessage(me.ccrama.redditslide.R.string.err_retry_later).show();
                }
            }

            @java.lang.Override
            protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                try {
                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).setNsfw(submission, true);
                } catch (net.dean.jraw.ApiException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void unNsfwSubmission(final android.content.Context mContext, final net.dean.jraw.models.Submission submission, final me.ccrama.redditslide.Adapters.SubmissionViewHolder holder) {
        // todo update view with NSFW tag
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
            @java.lang.Override
            public void onPostExecute(java.lang.Boolean b) {
                if (b) {
                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(holder.itemView, "NSFW status removed", android.support.design.widget.Snackbar.LENGTH_LONG);
                    android.view.View view = s.getView();
                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(android.graphics.Color.WHITE);
                    s.show();
                } else {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.err_general).setMessage(me.ccrama.redditslide.R.string.err_retry_later).show();
                }
            }

            @java.lang.Override
            protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                try {
                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).setNsfw(submission, false);
                } catch (net.dean.jraw.ApiException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setSpoiler(final android.app.Activity mContext, final net.dean.jraw.models.Submission submission, final me.ccrama.redditslide.Adapters.SubmissionViewHolder holder) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
            @java.lang.Override
            public void onPostExecute(java.lang.Boolean b) {
                if (b) {
                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(holder.itemView, "Spoiler status set", android.support.design.widget.Snackbar.LENGTH_LONG);
                    android.view.View view = s.getView();
                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(android.graphics.Color.WHITE);
                    s.show();
                } else {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.err_general).setMessage(me.ccrama.redditslide.R.string.err_retry_later).show();
                }
            }

            @java.lang.Override
            protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                try {
                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).setSpoiler(submission, true);
                } catch (net.dean.jraw.ApiException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void unSpoiler(final android.content.Context mContext, final net.dean.jraw.models.Submission submission, final me.ccrama.redditslide.Adapters.SubmissionViewHolder holder) {
        // todo update view with NSFW tag
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
            @java.lang.Override
            public void onPostExecute(java.lang.Boolean b) {
                if (b) {
                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(holder.itemView, "Spoiler status removed", android.support.design.widget.Snackbar.LENGTH_LONG);
                    android.view.View view = s.getView();
                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(android.graphics.Color.WHITE);
                    s.show();
                } else {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.err_general).setMessage(me.ccrama.redditslide.R.string.err_retry_later).show();
                }
            }

            @java.lang.Override
            protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                try {
                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).setSpoiler(submission, false);
                } catch (net.dean.jraw.ApiException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private <T extends net.dean.jraw.models.Thing> void approveSubmission(final android.content.Context mContext, final java.util.List<T> posts, final net.dean.jraw.models.Submission submission, final android.support.v7.widget.RecyclerView recyclerview, final me.ccrama.redditslide.Adapters.SubmissionViewHolder holder) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
            @java.lang.Override
            public void onPostExecute(java.lang.Boolean b) {
                if (b) {
                    me.ccrama.redditslide.SubmissionCache.approved.add(submission.getFullName());
                    me.ccrama.redditslide.SubmissionCache.removed.remove(submission.getFullName());
                    me.ccrama.redditslide.SubmissionCache.updateInfoSpannable(submission, mContext, submission.getSubredditName());
                    if (mContext instanceof me.ccrama.redditslide.Activities.ModQueue) {
                        final int pos = posts.indexOf(submission);
                        posts.remove(submission);
                        if (pos == 0) {
                            recyclerview.getAdapter().notifyDataSetChanged();
                        } else {
                            recyclerview.getAdapter().notifyItemRemoved(pos + 1);
                        }
                    } else {
                        recyclerview.getAdapter().notifyItemChanged(holder.getAdapterPosition());
                    }
                    try {
                        android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.mod_approved, android.support.design.widget.Snackbar.LENGTH_LONG);
                        android.view.View view = s.getView();
                        android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(android.graphics.Color.WHITE);
                        s.show();
                    } catch (java.lang.Exception ignored) {
                    }
                } else {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.err_general).setMessage(me.ccrama.redditslide.R.string.err_retry_later).show();
                }
            }

            @java.lang.Override
            protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                try {
                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).approve(submission);
                } catch (net.dean.jraw.ApiException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void showBan(final android.content.Context mContext, final android.view.View mToolbar, final net.dean.jraw.models.Submission submission, java.lang.String rs, java.lang.String nt, java.lang.String msg, java.lang.String t) {
        android.widget.LinearLayout l = new android.widget.LinearLayout(mContext);
        l.setOrientation(android.widget.LinearLayout.VERTICAL);
        int sixteen = me.ccrama.redditslide.Reddit.dpToPxVertical(16);
        l.setPadding(sixteen, 0, sixteen, 0);
        final android.widget.EditText reason = new android.widget.EditText(mContext);
        reason.setHint(me.ccrama.redditslide.R.string.mod_ban_reason);
        reason.setText(rs);
        reason.setInputType(android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        l.addView(reason);
        final android.widget.EditText note = new android.widget.EditText(mContext);
        note.setHint(me.ccrama.redditslide.R.string.mod_ban_note_mod);
        note.setText(nt);
        note.setInputType(android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        l.addView(note);
        final android.widget.EditText message = new android.widget.EditText(mContext);
        message.setHint(me.ccrama.redditslide.R.string.mod_ban_note_user);
        message.setText(msg);
        message.setInputType(android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        l.addView(message);
        final android.widget.EditText time = new android.widget.EditText(mContext);
        time.setHint(me.ccrama.redditslide.R.string.mod_ban_time);
        time.setText(t);
        time.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        l.addView(time);
        com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext);
        builder.setView(l).setTitle(mContext.getString(me.ccrama.redditslide.R.string.mod_ban_title, submission.getAuthor())).setCancelable(true).setPositiveButton(me.ccrama.redditslide.R.string.mod_btn_ban, new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                // to ban
                if (reason.getText().toString().isEmpty()) {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.mod_ban_reason_required).setMessage(me.ccrama.redditslide.R.string.misc_please_try_again).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            showBan(mContext, mToolbar, submission, reason.getText().toString(), note.getText().toString(), message.getText().toString(), time.getText().toString());
                        }
                    }).setCancelable(false).show();
                } else {
                    new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
                        @java.lang.Override
                        protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                            try {
                                java.lang.String n = note.getText().toString();
                                java.lang.String m = message.getText().toString();
                                if (n.isEmpty()) {
                                    n = null;
                                }
                                if (m.isEmpty()) {
                                    m = null;
                                }
                                if (time.getText().toString().isEmpty()) {
                                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).banUserPermanently(submission.getSubredditName(), submission.getAuthor(), reason.getText().toString(), n, m);
                                } else {
                                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).banUser(submission.getSubredditName(), submission.getAuthor(), reason.getText().toString(), n, m, java.lang.Integer.valueOf(time.getText().toString()));
                                }
                                return true;
                            } catch (java.lang.Exception e) {
                                if (e instanceof net.dean.jraw.http.oauth.InvalidScopeException) {
                                    scope = true;
                                }
                                e.printStackTrace();
                                return false;
                            }
                        }

                        boolean scope;

                        @java.lang.Override
                        protected void onPostExecute(java.lang.Boolean done) {
                            android.support.design.widget.Snackbar s;
                            if (done) {
                                s = android.support.design.widget.Snackbar.make(mToolbar, me.ccrama.redditslide.R.string.mod_ban_success, android.support.design.widget.Snackbar.LENGTH_SHORT);
                            } else {
                                if (scope) {
                                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.mod_ban_reauth).setMessage(me.ccrama.redditslide.R.string.mod_ban_reauth_question).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                                        @java.lang.Override
                                        public void onClick(android.content.DialogInterface dialog, int which) {
                                            android.content.Intent i = new android.content.Intent(mContext, me.ccrama.redditslide.Activities.Reauthenticate.class);
                                            mContext.startActivity(i);
                                        }
                                    }).setNegativeButton(me.ccrama.redditslide.R.string.misc_maybe_later, null).setCancelable(false).show();
                                }
                                s = android.support.design.widget.Snackbar.make(mToolbar, me.ccrama.redditslide.R.string.mod_ban_fail, android.support.design.widget.Snackbar.LENGTH_INDEFINITE).setAction(me.ccrama.redditslide.R.string.misc_try_again, new android.view.View.OnClickListener() {
                                    @java.lang.Override
                                    public void onClick(android.view.View v) {
                                        showBan(mContext, mToolbar, submission, reason.getText().toString(), note.getText().toString(), message.getText().toString(), time.getText().toString());
                                    }
                                });
                            }
                            if (s != null) {
                                android.view.View view = s.getView();
                                android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                                tv.setTextColor(android.graphics.Color.WHITE);
                                s.show();
                            }
                        }
                    }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        }).setNegativeButton(me.ccrama.redditslide.R.string.btn_cancel, null).show();
    }

    public <T extends net.dean.jraw.models.Contribution> void populateSubmissionViewHolder(final me.ccrama.redditslide.Adapters.SubmissionViewHolder holder, final net.dean.jraw.models.Submission submission, final android.app.Activity mContext, boolean fullscreen, final boolean full, final java.util.List<T> posts, final android.support.v7.widget.RecyclerView recyclerview, final boolean same, final boolean offline, final java.lang.String baseSub, @android.support.annotation.Nullable
    final me.ccrama.redditslide.Adapters.CommentAdapter adapter) {
        holder.itemView.findViewById(me.ccrama.redditslide.R.id.vote).setVisibility(android.view.View.GONE);
        if ((((!offline) && (me.ccrama.redditslide.UserSubscriptions.modOf != null)) && (submission.getSubredditName() != null)) && me.ccrama.redditslide.UserSubscriptions.modOf.contains(submission.getSubredditName().toLowerCase(java.util.Locale.ENGLISH))) {
            holder.mod.setVisibility(android.view.View.VISIBLE);
            final java.util.Map<java.lang.String, java.lang.Integer> reports = submission.getUserReports();
            final java.util.Map<java.lang.String, java.lang.String> reports2 = submission.getModeratorReports();
            if ((reports.size() + reports2.size()) > 0) {
                ((android.widget.ImageView) (holder.mod)).setColorFilter(android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_red_300), android.graphics.PorterDuff.Mode.SRC_ATOP);
            } else {
                ((android.widget.ImageView) (holder.mod)).setColorFilter(((holder.itemView.getTag(holder.itemView.getId()) != null) && holder.itemView.getTag(holder.itemView.getId()).equals("none")) || full ? me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.getCurrentTintColor(mContext) : me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.getWhiteTintColor(), android.graphics.PorterDuff.Mode.SRC_ATOP);
            }
            holder.mod.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    showModBottomSheet(mContext, submission, posts, holder, recyclerview, reports, reports2);
                }
            });
        } else {
            holder.mod.setVisibility(android.view.View.GONE);
        }
        holder.menu.setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View view) {
                showBottomSheet(mContext, submission, holder, posts, baseSub, recyclerview, full);
            }
        });
        // Use this to offset the submission score
        int submissionScore = submission.getScore();
        final int commentCount = submission.getCommentCount();
        final int more = me.ccrama.redditslide.LastComments.commentsSince(submission);
        holder.comments.setText(java.lang.String.format(java.util.Locale.getDefault(), "%d %s", commentCount, (more > 0) && me.ccrama.redditslide.SettingValues.commentLastVisit ? ("(+" + more) + ")" : ""));
        java.lang.String scoreRatio = ((me.ccrama.redditslide.SettingValues.upvotePercentage && full) && (submission.getUpvoteRatio() != null)) ? ("(" + ((int) (submission.getUpvoteRatio() * 100))) + "%)" : "";
        if (!scoreRatio.isEmpty()) {
            android.widget.TextView percent = holder.itemView.findViewById(me.ccrama.redditslide.R.id.percent);
            percent.setVisibility(android.view.View.VISIBLE);
            percent.setText(scoreRatio);
            final double numb = submission.getUpvoteRatio();
            if (numb <= 0.5) {
                if (numb <= 0.1) {
                    percent.setTextColor(android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_blue_500));
                } else if (numb <= 0.3) {
                    percent.setTextColor(android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_blue_400));
                } else {
                    percent.setTextColor(android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_blue_300));
                }
            } else if (numb >= 0.9) {
                percent.setTextColor(android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_orange_500));
            } else if (numb >= 0.7) {
                percent.setTextColor(android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_orange_400));
            } else {
                percent.setTextColor(android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_orange_300));
            }
        }
        final android.widget.ImageView downvotebutton = ((android.widget.ImageView) (holder.downvote));
        final android.widget.ImageView upvotebutton = ((android.widget.ImageView) (holder.upvote));
        if (submission.isArchived()) {
            downvotebutton.setVisibility(android.view.View.GONE);
            upvotebutton.setVisibility(android.view.View.GONE);
        } else if (me.ccrama.redditslide.Authentication.isLoggedIn && me.ccrama.redditslide.Authentication.didOnline) {
            if (me.ccrama.redditslide.SettingValues.actionbarVisible && (downvotebutton.getVisibility() != android.view.View.VISIBLE)) {
                downvotebutton.setVisibility(android.view.View.VISIBLE);
                upvotebutton.setVisibility(android.view.View.VISIBLE);
            }
        }
        // Set the colors and styles for the score text depending on what state it is in
        // Also set content descriptions
        switch (me.ccrama.redditslide.ActionStates.getVoteDirection(submission)) {
            case UPVOTE :
                {
                    holder.score.setTextColor(android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_orange_500));
                    upvotebutton.setColorFilter(android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_orange_500), android.graphics.PorterDuff.Mode.SRC_ATOP);
                    upvotebutton.setContentDescription(mContext.getString(me.ccrama.redditslide.R.string.btn_upvoted));
                    holder.score.setTypeface(null, android.graphics.Typeface.BOLD);
                    downvotebutton.setColorFilter(((holder.itemView.getTag(holder.itemView.getId()) != null) && holder.itemView.getTag(holder.itemView.getId()).equals("none")) || full ? me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.getCurrentTintColor(mContext) : me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.getWhiteTintColor(), android.graphics.PorterDuff.Mode.SRC_ATOP);
                    downvotebutton.setContentDescription(mContext.getString(me.ccrama.redditslide.R.string.btn_downvote));
                    if (submission.getVote() != net.dean.jraw.models.VoteDirection.UPVOTE) {
                        if (submission.getVote() == net.dean.jraw.models.VoteDirection.DOWNVOTE)
                            ++submissionScore;

                        ++submissionScore;// offset the score by +1

                    }
                    break;
                }
            case DOWNVOTE :
                {
                    holder.score.setTextColor(android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_blue_500));
                    downvotebutton.setColorFilter(android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_blue_500), android.graphics.PorterDuff.Mode.SRC_ATOP);
                    downvotebutton.setContentDescription(mContext.getString(me.ccrama.redditslide.R.string.btn_downvoted));
                    holder.score.setTypeface(null, android.graphics.Typeface.BOLD);
                    upvotebutton.setColorFilter(((holder.itemView.getTag(holder.itemView.getId()) != null) && holder.itemView.getTag(holder.itemView.getId()).equals("none")) || full ? me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.getCurrentTintColor(mContext) : me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.getWhiteTintColor(), android.graphics.PorterDuff.Mode.SRC_ATOP);
                    upvotebutton.setContentDescription(mContext.getString(me.ccrama.redditslide.R.string.btn_upvote));
                    if (submission.getVote() != net.dean.jraw.models.VoteDirection.DOWNVOTE) {
                        if (submission.getVote() == net.dean.jraw.models.VoteDirection.UPVOTE)
                            --submissionScore;

                        --submissionScore;// offset the score by +1

                    }
                    break;
                }
            case NO_VOTE :
                {
                    holder.score.setTextColor(holder.comments.getCurrentTextColor());
                    holder.score.setTypeface(null, android.graphics.Typeface.NORMAL);
                    downvotebutton.setColorFilter(((holder.itemView.getTag(holder.itemView.getId()) != null) && holder.itemView.getTag(holder.itemView.getId()).equals("none")) || full ? me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.getCurrentTintColor(mContext) : me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.getWhiteTintColor(), android.graphics.PorterDuff.Mode.SRC_ATOP);
                    upvotebutton.setContentDescription(mContext.getString(me.ccrama.redditslide.R.string.btn_upvote));
                    upvotebutton.setColorFilter(((holder.itemView.getTag(holder.itemView.getId()) != null) && holder.itemView.getTag(holder.itemView.getId()).equals("none")) || full ? me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.getCurrentTintColor(mContext) : me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.getWhiteTintColor(), android.graphics.PorterDuff.Mode.SRC_ATOP);
                    downvotebutton.setContentDescription(mContext.getString(me.ccrama.redditslide.R.string.btn_downvote));
                    break;
                }
        }
        // if the submission is already at 0pts, keep it at 0pts
        submissionScore = (submissionScore < 0) ? 0 : submissionScore;
        if ((submissionScore >= 10000) && me.ccrama.redditslide.SettingValues.abbreviateScores) {
            holder.score.setText(java.lang.String.format(java.util.Locale.getDefault(), "%.1fk", ((double) (submissionScore)) / 1000));
        } else {
            holder.score.setText(java.lang.String.format(java.util.Locale.getDefault(), "%d", submissionScore));
        }
        // Save the score so we can use it in the OnClickListeners for the vote buttons
        final int SUBMISSION_SCORE = submissionScore;
        final android.widget.ImageView hideButton = ((android.widget.ImageView) (holder.hide));
        if (hideButton != null) {
            if (me.ccrama.redditslide.SettingValues.hideButton && me.ccrama.redditslide.Authentication.isLoggedIn) {
                hideButton.setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        hideSubmission(submission, posts, baseSub, recyclerview, mContext);
                    }
                });
            } else {
                hideButton.setVisibility(android.view.View.GONE);
            }
        }
        if (me.ccrama.redditslide.Authentication.isLoggedIn && me.ccrama.redditslide.Authentication.didOnline) {
            if (me.ccrama.redditslide.ActionStates.isSaved(submission)) {
                ((android.widget.ImageView) (holder.save)).setColorFilter(android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_amber_500), android.graphics.PorterDuff.Mode.SRC_ATOP);
                ((android.widget.ImageView) (holder.save)).setContentDescription(mContext.getString(me.ccrama.redditslide.R.string.btn_unsave));
            } else {
                ((android.widget.ImageView) (holder.save)).setColorFilter(((holder.itemView.getTag(holder.itemView.getId()) != null) && holder.itemView.getTag(holder.itemView.getId()).equals("none")) || full ? me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.getCurrentTintColor(mContext) : me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.getWhiteTintColor(), android.graphics.PorterDuff.Mode.SRC_ATOP);
                ((android.widget.ImageView) (holder.save)).setContentDescription(mContext.getString(me.ccrama.redditslide.R.string.btn_save));
            }
            holder.save.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    saveSubmission(submission, mContext, holder, full);
                }
            });
        }
        if ((((!me.ccrama.redditslide.SettingValues.saveButton) && (!full)) || (!me.ccrama.redditslide.Authentication.isLoggedIn)) || (!me.ccrama.redditslide.Authentication.didOnline)) {
            holder.save.setVisibility(android.view.View.GONE);
        }
        android.widget.ImageView thumbImage2 = ((android.widget.ImageView) (holder.thumbimage));
        if (holder.leadImage.thumbImage2 == null) {
            holder.leadImage.setThumbnail(thumbImage2);
        }
        final me.ccrama.redditslide.ContentType.Type type = me.ccrama.redditslide.ContentType.getContentType(submission);
        me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.addClickFunctions(holder.leadImage, type, mContext, submission, holder, full);
        if (thumbImage2 != null) {
            me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.addClickFunctions(thumbImage2, type, mContext, submission, holder, full);
        }
        if (full) {
            me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.addClickFunctions(holder.itemView.findViewById(me.ccrama.redditslide.R.id.wraparea), type, mContext, submission, holder, full);
        }
        if (full) {
            holder.leadImage.setWrapArea(holder.itemView.findViewById(me.ccrama.redditslide.R.id.wraparea));
        }
        if (full && ((((submission.getDataNode() != null) && submission.getDataNode().has("crosspost_parent_list")) && (submission.getDataNode().get("crosspost_parent_list") != null)) && (submission.getDataNode().get("crosspost_parent_list").get(0) != null))) {
            holder.itemView.findViewById(me.ccrama.redditslide.R.id.crosspost).setVisibility(android.view.View.VISIBLE);
            ((android.widget.TextView) (holder.itemView.findViewById(me.ccrama.redditslide.R.id.crossinfo))).setText(me.ccrama.redditslide.SubmissionCache.getCrosspostLine(submission, mContext));
            ((me.ccrama.redditslide.Reddit) (mContext.getApplicationContext())).getImageLoader().displayImage(submission.getDataNode().get("crosspost_parent_list").get(0).get("thumbnail").asText(), ((android.widget.ImageView) (holder.itemView.findViewById(me.ccrama.redditslide.R.id.crossthumb))));
            holder.itemView.findViewById(me.ccrama.redditslide.R.id.crosspost).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    me.ccrama.redditslide.OpenRedditLink.openUrl(mContext, submission.getDataNode().get("crosspost_parent_list").get(0).get("permalink").asText(), true);
                }
            });
        }
        holder.leadImage.setSubmission(submission, full, baseSub, type);
        holder.itemView.setOnLongClickListener(new android.view.View.OnLongClickListener() {
            @java.lang.Override
            public boolean onLongClick(android.view.View v) {
                if (offline) {
                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(holder.itemView, mContext.getString(me.ccrama.redditslide.R.string.offline_msg), android.support.design.widget.Snackbar.LENGTH_SHORT);
                    android.view.View view = s.getView();
                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(android.graphics.Color.WHITE);
                    s.show();
                } else if (me.ccrama.redditslide.SettingValues.actionbarTap && (!full)) {
                    me.ccrama.redditslide.Views.CreateCardView.toggleActionbar(holder.itemView);
                } else {
                    holder.itemView.findViewById(me.ccrama.redditslide.R.id.menu).callOnClick();
                }
                return true;
            }
        });
        doText(holder, submission, mContext, baseSub, full);
        if (((((((!full) && me.ccrama.redditslide.SettingValues.isSelftextEnabled(baseSub)) && submission.isSelfPost()) && (!submission.getSelftext().isEmpty())) && (!submission.isNsfw())) && (!submission.getDataNode().get("spoiler").asBoolean())) && (!submission.getDataNode().get("selftext_html").asText().trim().isEmpty())) {
            holder.body.setVisibility(android.view.View.VISIBLE);
            java.lang.String text = submission.getDataNode().get("selftext_html").asText();
            int typef = new me.ccrama.redditslide.Visuals.FontPreferences(mContext).getFontTypeComment().getTypeface();
            android.graphics.Typeface typeface;
            if (typef >= 0) {
                typeface = com.devspark.robototextview.RobotoTypefaces.obtainTypeface(mContext, typef);
            } else {
                typeface = android.graphics.Typeface.DEFAULT;
            }
            holder.body.setTypeface(typeface);
            holder.body.setTextHtml(android.text.Html.fromHtml(text.substring(0, text.contains("\n") ? text.indexOf("\n") : text.length())).toString().replace("<sup>", "<sup><small>").replace("</sup>", "</small></sup>"), "none ");
            holder.body.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    holder.itemView.callOnClick();
                }
            });
            holder.body.setOnLongClickListener(new android.view.View.OnLongClickListener() {
                @java.lang.Override
                public boolean onLongClick(android.view.View v) {
                    holder.menu.callOnClick();
                    return true;
                }
            });
        } else if (!full) {
            holder.body.setVisibility(android.view.View.GONE);
        }
        if (full) {
            if (!submission.getSelftext().isEmpty()) {
                int typef = new me.ccrama.redditslide.Visuals.FontPreferences(mContext).getFontTypeComment().getTypeface();
                android.graphics.Typeface typeface;
                if (typef >= 0) {
                    typeface = com.devspark.robototextview.RobotoTypefaces.obtainTypeface(mContext, typef);
                } else {
                    typeface = android.graphics.Typeface.DEFAULT;
                }
                holder.firstTextView.setTypeface(typeface);
                setViews(submission.getDataNode().get("selftext_html").asText(), submission.getSubredditName() == null ? "all" : submission.getSubredditName(), holder);
                holder.itemView.findViewById(me.ccrama.redditslide.R.id.body_area).setVisibility(android.view.View.VISIBLE);
            } else {
                holder.itemView.findViewById(me.ccrama.redditslide.R.id.body_area).setVisibility(android.view.View.GONE);
            }
        }
        try {
            final android.widget.TextView points = holder.score;
            final android.widget.TextView comments = holder.comments;
            if ((me.ccrama.redditslide.Authentication.isLoggedIn && (!offline)) && me.ccrama.redditslide.Authentication.didOnline) {
                {
                    downvotebutton.setOnClickListener(new android.view.View.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.view.View view) {
                            if (me.ccrama.redditslide.SettingValues.storeHistory && (!full)) {
                                if ((!submission.isNsfw()) || me.ccrama.redditslide.SettingValues.storeNSFWHistory) {
                                    me.ccrama.redditslide.HasSeen.addSeen(submission.getFullName());
                                    if (mContext instanceof me.ccrama.redditslide.Activities.MainActivity) {
                                        holder.title.setAlpha(0.54F);
                                        holder.body.setAlpha(0.54F);
                                    }
                                }
                            }
                            if (me.ccrama.redditslide.ActionStates.getVoteDirection(submission) != net.dean.jraw.models.VoteDirection.DOWNVOTE) {
                                // has not been downvoted
                                points.setTextColor(android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_blue_500));
                                downvotebutton.setColorFilter(android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_blue_500), android.graphics.PorterDuff.Mode.SRC_ATOP);
                                upvotebutton.setColorFilter(((holder.itemView.getTag(holder.itemView.getId()) != null) && holder.itemView.getTag(holder.itemView.getId()).equals("none")) || full ? me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.getCurrentTintColor(mContext) : me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.getWhiteTintColor(), android.graphics.PorterDuff.Mode.SRC_ATOP);
                                downvotebutton.setContentDescription(mContext.getString(me.ccrama.redditslide.R.string.btn_downvoted));
                                me.ccrama.redditslide.Views.AnimateHelper.setFlashAnimation(holder.itemView, downvotebutton, android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_blue_500));
                                holder.score.setTypeface(null, android.graphics.Typeface.BOLD);
                                final int DOWNVOTE_SCORE = (SUBMISSION_SCORE == 0) ? 0 : SUBMISSION_SCORE - 1;// if a post is at 0 votes, keep it at 0 when downvoting

                                new me.ccrama.redditslide.Vote(false, points, mContext).execute(submission);
                                me.ccrama.redditslide.ActionStates.setVoteDirection(submission, net.dean.jraw.models.VoteDirection.DOWNVOTE);
                                setSubmissionScoreText(submission, holder);
                            } else {
                                // un-downvoted a post
                                points.setTextColor(comments.getCurrentTextColor());
                                new me.ccrama.redditslide.Vote(points, mContext).execute(submission);
                                holder.score.setTypeface(null, android.graphics.Typeface.NORMAL);
                                me.ccrama.redditslide.ActionStates.setVoteDirection(submission, net.dean.jraw.models.VoteDirection.NO_VOTE);
                                downvotebutton.setColorFilter(((holder.itemView.getTag(holder.itemView.getId()) != null) && holder.itemView.getTag(holder.itemView.getId()).equals("none")) || full ? me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.getCurrentTintColor(mContext) : me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.getWhiteTintColor(), android.graphics.PorterDuff.Mode.SRC_ATOP);
                                downvotebutton.setContentDescription(mContext.getString(me.ccrama.redditslide.R.string.btn_downvote));
                                setSubmissionScoreText(submission, holder);
                            }
                            if (((!full) && (!me.ccrama.redditslide.SettingValues.actionbarVisible)) && (me.ccrama.redditslide.SettingValues.defaultCardView != me.ccrama.redditslide.Views.CreateCardView.CardEnum.DESKTOP)) {
                                me.ccrama.redditslide.Views.CreateCardView.toggleActionbar(holder.itemView);
                            }
                        }
                    });
                }
                {
                    upvotebutton.setOnClickListener(new android.view.View.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.view.View view) {
                            if (me.ccrama.redditslide.SettingValues.storeHistory && (!full)) {
                                if ((!submission.isNsfw()) || me.ccrama.redditslide.SettingValues.storeNSFWHistory) {
                                    me.ccrama.redditslide.HasSeen.addSeen(submission.getFullName());
                                    if (mContext instanceof me.ccrama.redditslide.Activities.MainActivity) {
                                        holder.title.setAlpha(0.54F);
                                        holder.body.setAlpha(0.54F);
                                    }
                                }
                            }
                            if (me.ccrama.redditslide.ActionStates.getVoteDirection(submission) != net.dean.jraw.models.VoteDirection.UPVOTE) {
                                // has not been upvoted
                                points.setTextColor(android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_orange_500));
                                upvotebutton.setColorFilter(android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_orange_500), android.graphics.PorterDuff.Mode.SRC_ATOP);
                                downvotebutton.setColorFilter(((holder.itemView.getTag(holder.itemView.getId()) != null) && holder.itemView.getTag(holder.itemView.getId()).equals("none")) || full ? me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.getCurrentTintColor(mContext) : me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.getWhiteTintColor(), android.graphics.PorterDuff.Mode.SRC_ATOP);
                                upvotebutton.setContentDescription(mContext.getString(me.ccrama.redditslide.R.string.btn_upvoted));
                                me.ccrama.redditslide.Views.AnimateHelper.setFlashAnimation(holder.itemView, upvotebutton, android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_orange_500));
                                holder.score.setTypeface(null, android.graphics.Typeface.BOLD);
                                new me.ccrama.redditslide.Vote(true, points, mContext).execute(submission);
                                me.ccrama.redditslide.ActionStates.setVoteDirection(submission, net.dean.jraw.models.VoteDirection.UPVOTE);
                                setSubmissionScoreText(submission, holder);
                            } else {
                                // un-upvoted a post
                                points.setTextColor(comments.getCurrentTextColor());
                                new me.ccrama.redditslide.Vote(points, mContext).execute(submission);
                                holder.score.setTypeface(null, android.graphics.Typeface.NORMAL);
                                me.ccrama.redditslide.ActionStates.setVoteDirection(submission, net.dean.jraw.models.VoteDirection.NO_VOTE);
                                upvotebutton.setColorFilter(((holder.itemView.getTag(holder.itemView.getId()) != null) && holder.itemView.getTag(holder.itemView.getId()).equals("none")) || full ? me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.getCurrentTintColor(mContext) : me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.getWhiteTintColor(), android.graphics.PorterDuff.Mode.SRC_ATOP);
                                upvotebutton.setContentDescription(mContext.getString(me.ccrama.redditslide.R.string.btn_upvote));
                                setSubmissionScoreText(submission, holder);
                            }
                            if (((!full) && (!me.ccrama.redditslide.SettingValues.actionbarVisible)) && (me.ccrama.redditslide.SettingValues.defaultCardView != me.ccrama.redditslide.Views.CreateCardView.CardEnum.DESKTOP)) {
                                me.ccrama.redditslide.Views.CreateCardView.toggleActionbar(holder.itemView);
                            }
                        }
                    });
                }
            } else {
                upvotebutton.setVisibility(android.view.View.GONE);
                downvotebutton.setVisibility(android.view.View.GONE);
            }
        } catch (java.lang.Exception ignored) {
            ignored.printStackTrace();
        }
        final android.view.View edit = holder.edit;
        if (((me.ccrama.redditslide.Authentication.name != null) && me.ccrama.redditslide.Authentication.name.toLowerCase(java.util.Locale.ENGLISH).equals(submission.getAuthor().toLowerCase(java.util.Locale.ENGLISH))) && me.ccrama.redditslide.Authentication.didOnline) {
            edit.setVisibility(android.view.View.VISIBLE);
            edit.setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View v) {
                    new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.util.ArrayList<java.lang.String>>() {
                        java.util.List<net.dean.jraw.models.FlairTemplate> flairlist;

                        @java.lang.Override
                        protected java.util.ArrayList<java.lang.String> doInBackground(java.lang.Void... params) {
                            net.dean.jraw.fluent.FlairReference allFlairs = new net.dean.jraw.fluent.FluentRedditClient(me.ccrama.redditslide.Authentication.reddit).subreddit(submission.getSubredditName()).flair();
                            try {
                                flairlist = allFlairs.options(submission);
                                final java.util.ArrayList<java.lang.String> finalFlairs = new java.util.ArrayList<>();
                                for (net.dean.jraw.models.FlairTemplate temp : flairlist) {
                                    finalFlairs.add(temp.getText());
                                }
                                return finalFlairs;
                            } catch (java.lang.Exception e) {
                                e.printStackTrace();
                                // sub probably has no flairs?
                            }
                            return null;
                        }

                        @java.lang.Override
                        public void onPostExecute(final java.util.ArrayList<java.lang.String> data) {
                            final boolean flair = (data != null) && (!data.isEmpty());
                            int[] attrs = new int[]{ me.ccrama.redditslide.R.attr.tintColor };
                            android.content.res.TypedArray ta = mContext.obtainStyledAttributes(attrs);
                            final int color2 = ta.getColor(0, android.graphics.Color.WHITE);
                            android.graphics.drawable.Drawable edit_drawable = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.edit);
                            android.graphics.drawable.Drawable nsfw_drawable = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.hide);
                            android.graphics.drawable.Drawable delete_drawable = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.delete);
                            android.graphics.drawable.Drawable flair_drawable = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.fontsizedarker);
                            edit_drawable.setColorFilter(color2, android.graphics.PorterDuff.Mode.SRC_ATOP);
                            nsfw_drawable.setColorFilter(color2, android.graphics.PorterDuff.Mode.SRC_ATOP);
                            delete_drawable.setColorFilter(color2, android.graphics.PorterDuff.Mode.SRC_ATOP);
                            flair_drawable.setColorFilter(color2, android.graphics.PorterDuff.Mode.SRC_ATOP);
                            ta.recycle();
                            com.cocosw.bottomsheet.BottomSheet.Builder b = new com.cocosw.bottomsheet.BottomSheet.Builder(mContext).title(android.text.Html.fromHtml(submission.getTitle()));
                            if (submission.isSelfPost()) {
                                b.sheet(1, edit_drawable, mContext.getString(me.ccrama.redditslide.R.string.edit_selftext));
                            }
                            if (submission.isNsfw()) {
                                b.sheet(4, nsfw_drawable, mContext.getString(me.ccrama.redditslide.R.string.mod_btn_unmark_nsfw));
                            } else {
                                b.sheet(4, nsfw_drawable, mContext.getString(me.ccrama.redditslide.R.string.mod_btn_mark_nsfw));
                            }
                            if (submission.getDataNode().get("spoiler").asBoolean()) {
                                b.sheet(5, nsfw_drawable, mContext.getString(me.ccrama.redditslide.R.string.mod_btn_unmark_spoiler));
                            } else {
                                b.sheet(5, nsfw_drawable, mContext.getString(me.ccrama.redditslide.R.string.mod_btn_mark_spoiler));
                            }
                            b.sheet(2, delete_drawable, mContext.getString(me.ccrama.redditslide.R.string.delete_submission));
                            if (flair) {
                                b.sheet(3, flair_drawable, mContext.getString(me.ccrama.redditslide.R.string.set_submission_flair));
                            }
                            b.listener(new android.content.DialogInterface.OnClickListener() {
                                @java.lang.Override
                                public void onClick(android.content.DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 1 :
                                            {
                                                android.view.LayoutInflater inflater = mContext.getLayoutInflater();
                                                final android.view.View dialoglayout = inflater.inflate(me.ccrama.redditslide.R.layout.edit_comment, null);
                                                final com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext);
                                                final android.widget.EditText e = dialoglayout.findViewById(me.ccrama.redditslide.R.id.entry);
                                                e.setText(org.apache.commons.text.StringEscapeUtils.unescapeHtml4(submission.getSelftext()));
                                                me.ccrama.redditslide.Views.DoEditorActions.doActions(e, dialoglayout, ((android.support.v7.app.AppCompatActivity) (mContext)).getSupportFragmentManager(), mContext, null, null);
                                                builder.setCancelable(false).setView(dialoglayout);
                                                final android.app.Dialog d = builder.create();
                                                d.getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                                                d.show();
                                                dialoglayout.findViewById(me.ccrama.redditslide.R.id.cancel).setOnClickListener(new android.view.View.OnClickListener() {
                                                    @java.lang.Override
                                                    public void onClick(android.view.View v) {
                                                        d.dismiss();
                                                    }
                                                });
                                                dialoglayout.findViewById(me.ccrama.redditslide.R.id.submit).setOnClickListener(new android.view.View.OnClickListener() {
                                                    @java.lang.Override
                                                    public void onClick(android.view.View v) {
                                                        final java.lang.String text = e.getText().toString();
                                                        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
                                                            @java.lang.Override
                                                            protected java.lang.Void doInBackground(java.lang.Void... params) {
                                                                try {
                                                                    new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).updateContribution(submission, text);
                                                                    if (adapter != null) {
                                                                        adapter.dataSet.reloadSubmission(adapter);
                                                                    }
                                                                    d.dismiss();
                                                                } catch (java.lang.Exception e) {
                                                                    mContext.runOnUiThread(new java.lang.Runnable() {
                                                                        @java.lang.Override
                                                                        public void run() {
                                                                            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.comment_delete_err).setMessage(me.ccrama.redditslide.R.string.comment_delete_err_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                                                                                @java.lang.Override
                                                                                public void onClick(android.content.DialogInterface dialog, int which) {
                                                                                    dialog.dismiss();
                                                                                    doInBackground();
                                                                                }
                                                                            }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, new android.content.DialogInterface.OnClickListener() {
                                                                                @java.lang.Override
                                                                                public void onClick(android.content.DialogInterface dialog, int which) {
                                                                                    dialog.dismiss();
                                                                                }
                                                                            }).show();
                                                                        }
                                                                    });
                                                                }
                                                                return null;
                                                            }

                                                            @java.lang.Override
                                                            protected void onPostExecute(java.lang.Void aVoid) {
                                                                if (adapter != null) {
                                                                    adapter.notifyItemChanged(1);
                                                                }
                                                            }
                                                        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                                                    }
                                                });
                                            }
                                            break;
                                        case 2 :
                                            {
                                                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.really_delete_submission).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                                                    @java.lang.Override
                                                    public void onClick(android.content.DialogInterface dialog, int which) {
                                                        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
                                                            @java.lang.Override
                                                            protected java.lang.Void doInBackground(java.lang.Void... params) {
                                                                try {
                                                                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).delete(submission);
                                                                } catch (net.dean.jraw.ApiException e) {
                                                                    e.printStackTrace();
                                                                }
                                                                return null;
                                                            }

                                                            @java.lang.Override
                                                            protected void onPostExecute(java.lang.Void aVoid) {
                                                                mContext.runOnUiThread(new java.lang.Runnable() {
                                                                    @java.lang.Override
                                                                    public void run() {
                                                                        holder.title.setTextHtml(mContext.getString(me.ccrama.redditslide.R.string.content_deleted));
                                                                        if (holder.firstTextView != null) {
                                                                            holder.firstTextView.setText(me.ccrama.redditslide.R.string.content_deleted);
                                                                            holder.commentOverflow.setVisibility(android.view.View.GONE);
                                                                        } else if (holder.itemView.findViewById(me.ccrama.redditslide.R.id.body) != null) {
                                                                            ((android.widget.TextView) (holder.itemView.findViewById(me.ccrama.redditslide.R.id.body))).setText(me.ccrama.redditslide.R.string.content_deleted);
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                                                    }
                                                }).setNegativeButton(me.ccrama.redditslide.R.string.btn_cancel, null).show();
                                            }
                                            break;
                                        case 3 :
                                            {
                                                new com.afollestad.materialdialogs.MaterialDialog.Builder(mContext).items(data).title(me.ccrama.redditslide.R.string.sidebar_select_flair).itemsCallback(new com.afollestad.materialdialogs.MaterialDialog.ListCallback() {
                                                    @java.lang.Override
                                                    public void onSelection(com.afollestad.materialdialogs.MaterialDialog dialog, android.view.View itemView, int which, java.lang.CharSequence text) {
                                                        final net.dean.jraw.models.FlairTemplate t = flairlist.get(which);
                                                        if (t.isTextEditable()) {
                                                            new com.afollestad.materialdialogs.MaterialDialog.Builder(mContext).title(me.ccrama.redditslide.R.string.mod_btn_submission_flair_text).input(mContext.getString(me.ccrama.redditslide.R.string.mod_flair_hint), t.getText(), true, new com.afollestad.materialdialogs.MaterialDialog.InputCallback() {
                                                                @java.lang.Override
                                                                public void onInput(com.afollestad.materialdialogs.MaterialDialog dialog, java.lang.CharSequence input) {
                                                                }
                                                            }).positiveText(me.ccrama.redditslide.R.string.btn_set).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                                                                @java.lang.Override
                                                                public void onClick(com.afollestad.materialdialogs.MaterialDialog dialog, com.afollestad.materialdialogs.DialogAction which) {
                                                                    final java.lang.String flair = dialog.getInputEditText().getText().toString();
                                                                    new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
                                                                        @java.lang.Override
                                                                        protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                                                                            try {
                                                                                new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).setFlair(submission.getSubredditName(), t, flair, submission);
                                                                                return true;
                                                                            } catch (net.dean.jraw.ApiException e) {
                                                                                e.printStackTrace();
                                                                                return false;
                                                                            }
                                                                        }

                                                                        @java.lang.Override
                                                                        protected void onPostExecute(java.lang.Boolean done) {
                                                                            android.support.design.widget.Snackbar s = null;
                                                                            if (done) {
                                                                                if (holder.itemView != null) {
                                                                                    s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.snackbar_flair_success, android.support.design.widget.Snackbar.LENGTH_SHORT);
                                                                                    me.ccrama.redditslide.SubmissionCache.updateTitleFlair(submission, flair, mContext);
                                                                                    holder.title.setText(me.ccrama.redditslide.SubmissionCache.getTitleLine(submission, mContext));
                                                                                }
                                                                            } else if (holder.itemView != null) {
                                                                                s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.snackbar_flair_error, android.support.design.widget.Snackbar.LENGTH_SHORT);
                                                                            }
                                                                            if (s != null) {
                                                                                android.view.View view = s.getView();
                                                                                android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                                                                                tv.setTextColor(android.graphics.Color.WHITE);
                                                                                s.show();
                                                                            }
                                                                        }
                                                                    }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                                                                }
                                                            }).negativeText(me.ccrama.redditslide.R.string.btn_cancel).show();
                                                        } else {
                                                            new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
                                                                @java.lang.Override
                                                                protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                                                                    try {
                                                                        new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).setFlair(submission.getSubredditName(), t, null, submission);
                                                                        return true;
                                                                    } catch (net.dean.jraw.ApiException e) {
                                                                        e.printStackTrace();
                                                                        return false;
                                                                    }
                                                                }

                                                                @java.lang.Override
                                                                protected void onPostExecute(java.lang.Boolean done) {
                                                                    android.support.design.widget.Snackbar s = null;
                                                                    if (done) {
                                                                        if (holder.itemView != null) {
                                                                            s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.snackbar_flair_success, android.support.design.widget.Snackbar.LENGTH_SHORT);
                                                                            me.ccrama.redditslide.SubmissionCache.updateTitleFlair(submission, t.getCssClass(), mContext);
                                                                            holder.title.setText(me.ccrama.redditslide.SubmissionCache.getTitleLine(submission, mContext));
                                                                        }
                                                                    } else if (holder.itemView != null) {
                                                                        s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.snackbar_flair_error, android.support.design.widget.Snackbar.LENGTH_SHORT);
                                                                    }
                                                                    if (s != null) {
                                                                        android.view.View view = s.getView();
                                                                        android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                                                                        tv.setTextColor(android.graphics.Color.WHITE);
                                                                        s.show();
                                                                    }
                                                                }
                                                            }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                                                        }
                                                    }
                                                }).show();
                                            }
                                            break;
                                        case 4 :
                                            if (submission.isNsfw()) {
                                                unNsfwSubmission(mContext, submission, holder);
                                            } else {
                                                setPostNsfw(mContext, submission, holder);
                                            }
                                            break;
                                        case 5 :
                                            if (submission.getDataNode().get("spoiler").asBoolean()) {
                                                unSpoiler(mContext, submission, holder);
                                            } else {
                                                setSpoiler(mContext, submission, holder);
                                            }
                                            break;
                                    }
                                }
                            }).show();
                        }
                    }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                }
            });
        } else {
            edit.setVisibility(android.view.View.GONE);
        }
        if (me.ccrama.redditslide.HasSeen.getSeen(submission) && (!full)) {
            holder.title.setAlpha(0.54F);
            holder.body.setAlpha(0.54F);
        } else {
            holder.title.setAlpha(1.0F);
            if (!full) {
                holder.body.setAlpha(1.0F);
            }
        }
    }

    private void setSubmissionScoreText(net.dean.jraw.models.Submission submission, me.ccrama.redditslide.Adapters.SubmissionViewHolder holder) {
        int submissionScore = submission.getScore();
        switch (me.ccrama.redditslide.ActionStates.getVoteDirection(submission)) {
            case UPVOTE :
                {
                    if (submission.getVote() != net.dean.jraw.models.VoteDirection.UPVOTE) {
                        if (submission.getVote() == net.dean.jraw.models.VoteDirection.DOWNVOTE)
                            ++submissionScore;

                        ++submissionScore;// offset the score by +1

                    }
                    break;
                }
            case DOWNVOTE :
                {
                    if (submission.getVote() != net.dean.jraw.models.VoteDirection.DOWNVOTE) {
                        if (submission.getVote() == net.dean.jraw.models.VoteDirection.UPVOTE)
                            --submissionScore;

                        --submissionScore;// offset the score by +1

                    }
                    break;
                }
            case NO_VOTE :
                if ((submission.getVote() == net.dean.jraw.models.VoteDirection.UPVOTE) && submission.getAuthor().equalsIgnoreCase(me.ccrama.redditslide.Authentication.name)) {
                    submissionScore--;
                }
                break;
        }
        // if the submission is already at 0pts, keep it at 0pts
        submissionScore = (submissionScore < 0) ? 0 : submissionScore;
        if ((submissionScore >= 10000) && me.ccrama.redditslide.SettingValues.abbreviateScores) {
            holder.score.setText(java.lang.String.format(java.util.Locale.getDefault(), "%.1fk", ((double) (submissionScore)) / 1000));
        } else {
            holder.score.setText(java.lang.String.format(java.util.Locale.getDefault(), "%d", submissionScore));
        }
    }

    private void setViews(java.lang.String rawHTML, java.lang.String subredditName, me.ccrama.redditslide.Adapters.SubmissionViewHolder holder) {
        if (rawHTML.isEmpty()) {
            return;
        }
        java.util.List<java.lang.String> blocks = me.ccrama.redditslide.util.SubmissionParser.getBlocks(rawHTML);
        int startIndex = 0;
        if ((!blocks.get(0).startsWith("<table>")) && (!blocks.get(0).startsWith("<pre>"))) {
            holder.firstTextView.setTextHtml(blocks.get(0), subredditName);
            startIndex = 1;
        }
        if (blocks.size() > 1) {
            if (startIndex == 0) {
                holder.commentOverflow.setViews(blocks, subredditName);
            } else {
                holder.commentOverflow.setViews(blocks.subList(startIndex, blocks.size()), subredditName);
            }
        }
    }

    public static class AsyncReportTask extends android.os.AsyncTask<java.lang.String, java.lang.Void, java.lang.Void> {
        private net.dean.jraw.models.Submission submission;

        private android.view.View contextView;

        public AsyncReportTask(net.dean.jraw.models.Submission submission, android.view.View contextView) {
            this.submission = submission;
            this.contextView = contextView;
        }

        @java.lang.Override
        protected java.lang.Void doInBackground(java.lang.String... reason) {
            try {
                new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).report(submission, reason[0]);
            } catch (net.dean.jraw.ApiException e) {
                e.printStackTrace();
            }
            return null;
        }

        @java.lang.Override
        protected void onPostExecute(java.lang.Void aVoid) {
            if (contextView != null) {
                try {
                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(contextView, me.ccrama.redditslide.R.string.msg_report_sent, android.support.design.widget.Snackbar.LENGTH_SHORT);
                    android.view.View view = s.getView();
                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(android.graphics.Color.WHITE);
                    s.show();
                } catch (java.lang.Exception ignored) {
                }
            }
        }
    }
}