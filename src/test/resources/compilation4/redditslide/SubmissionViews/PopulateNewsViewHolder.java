package me.ccrama.redditslide.SubmissionViews;
import java.util.Locale;
import me.ccrama.redditslide.ContentType;
import me.ccrama.redditslide.util.OnSingleClickListener;
import me.ccrama.redditslide.Visuals.Palette;
import java.util.ArrayList;
import me.ccrama.redditslide.Activities.Tumblr;
import me.ccrama.redditslide.Activities.MainActivity;
import me.ccrama.redditslide.Activities.PostReadLater;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Activities.MediaView;
import me.ccrama.redditslide.Activities.Profile;
import me.ccrama.redditslide.Activities.SubredditView;
import me.ccrama.redditslide.util.NetworkUtil;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.HasSeen;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Views.CreateCardView;
import me.ccrama.redditslide.Activities.Album;
import me.ccrama.redditslide.Activities.Search;
import me.ccrama.redditslide.Activities.TumblrPager;
import me.ccrama.redditslide.Hidden;
import java.util.List;
import me.ccrama.redditslide.LastComments;
import me.ccrama.redditslide.ActionStates;
import me.ccrama.redditslide.Activities.FullscreenVideo;
import me.ccrama.redditslide.Adapters.NewsViewHolder;
import me.ccrama.redditslide.Activities.MultiredditOverview;
import me.ccrama.redditslide.SubmissionCache;
import me.ccrama.redditslide.ForceTouch.PeekViewActivity;
import me.ccrama.redditslide.CommentCacheAsync;
import me.ccrama.redditslide.PostMatch;
import me.ccrama.redditslide.DataShare;
import me.ccrama.redditslide.OfflineSubreddit;
import me.ccrama.redditslide.Activities.AlbumPager;
import me.ccrama.redditslide.util.LinkUtil;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.ReadLater;
import me.ccrama.redditslide.util.GifUtils;
import me.ccrama.redditslide.Fragments.SubmissionsView;
import java.util.Arrays;
import me.ccrama.redditslide.OpenRedditLink;
import me.ccrama.redditslide.Adapters.CommentAdapter;
/**
 * Created by ccrama on 9/19/2015.
 */
public class PopulateNewsViewHolder {
    public PopulateNewsViewHolder() {
    }

    public static int getStyleAttribColorValue(final android.content.Context context, final int attribResId, final int defaultValue) {
        final android.util.TypedValue tv = new android.util.TypedValue();
        final boolean found = context.getTheme().resolveAttribute(attribResId, tv, true);
        return found ? tv.data : defaultValue;
    }

    private static void addClickFunctions(final android.view.View base, final me.ccrama.redditslide.ContentType.Type type, final android.app.Activity contextActivity, final net.dean.jraw.models.Submission submission, final me.ccrama.redditslide.Adapters.NewsViewHolder holder, final boolean full) {
        base.setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
            @java.lang.Override
            public void onSingleClick(android.view.View v) {
                if (me.ccrama.redditslide.util.NetworkUtil.isConnected(contextActivity) || ((!me.ccrama.redditslide.util.NetworkUtil.isConnected(contextActivity)) && me.ccrama.redditslide.ContentType.fullImage(type))) {
                    if (me.ccrama.redditslide.SettingValues.storeHistory && (!full)) {
                        if ((!submission.isNsfw()) || me.ccrama.redditslide.SettingValues.storeNSFWHistory) {
                            me.ccrama.redditslide.HasSeen.addSeen(submission.getFullName());
                            if (((((contextActivity instanceof me.ccrama.redditslide.Activities.MainActivity) || (contextActivity instanceof me.ccrama.redditslide.Activities.MultiredditOverview)) || (contextActivity instanceof me.ccrama.redditslide.Activities.SubredditView)) || (contextActivity instanceof me.ccrama.redditslide.Activities.Search)) || (contextActivity instanceof me.ccrama.redditslide.Activities.Profile)) {
                                holder.title.setAlpha(0.54F);
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
                                        contextActivity.startActivity(myIntent);
                                    } else {
                                        me.ccrama.redditslide.util.LinkUtil.openExternally(submission.getUrl());
                                    }
                                    break;
                                case IMGUR :
                                    me.ccrama.redditslide.SubmissionViews.PopulateNewsViewHolder.openImage(type, contextActivity, submission, holder.leadImage, holder.getAdapterPosition());
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
                                    me.ccrama.redditslide.SubmissionViews.PopulateNewsViewHolder.openRedditContent(submission.getUrl(), contextActivity);
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
                                        me.ccrama.redditslide.SubmissionViews.PopulateNewsViewHolder.addAdaptorPosition(i, submission, holder.getAdapterPosition());
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
                                        me.ccrama.redditslide.SubmissionViews.PopulateNewsViewHolder.addAdaptorPosition(i, submission, holder.getAdapterPosition());
                                        contextActivity.startActivity(i);
                                        contextActivity.overridePendingTransition(me.ccrama.redditslide.R.anim.slideright, me.ccrama.redditslide.R.anim.fade_out);
                                    } else {
                                        me.ccrama.redditslide.util.LinkUtil.openExternally(submission.getUrl());
                                    }
                                    break;
                                case DEVIANTART :
                                case XKCD :
                                case IMAGE :
                                    me.ccrama.redditslide.SubmissionViews.PopulateNewsViewHolder.openImage(type, contextActivity, submission, holder.leadImage, holder.getAdapterPosition());
                                    break;
                                case GIF :
                                    me.ccrama.redditslide.SubmissionViews.PopulateNewsViewHolder.openGif(contextActivity, submission, holder.getAdapterPosition());
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
                previewUrl = org.apache.commons.text.StringEscapeUtils.escapeHtml4(submission.getDataNode().get("preview").get("images").get(0).get("source").get("url").asText());
                if ((baseView == null) || ((!me.ccrama.redditslide.SettingValues.loadImageLq) && baseView.lq)) {
                    myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_DISPLAY_URL, previewUrl);
                } else {
                    myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_DISPLAY_URL, baseView.loadedUrl);
                }
            }
            myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_URL, url);
            me.ccrama.redditslide.SubmissionViews.PopulateNewsViewHolder.addAdaptorPosition(myIntent, submission, adapterPosition);
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
            if ((((t == me.ccrama.redditslide.util.GifUtils.AsyncLoadGif.VideoType.DIRECT) && submission.getDataNode().has("preview")) && submission.getDataNode().get("preview").get("images").get(0).has("variants")) && submission.getDataNode().get("preview").get("images").get(0).get("variants").has("mp4")) {
                myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_URL, org.apache.commons.text.StringEscapeUtils.unescapeJson(submission.getDataNode().get("preview").get("images").get(0).get("variants").get("mp4").get("source").get("url").asText()).replace("&amp;", "&"));
            } else if ((((t == me.ccrama.redditslide.util.GifUtils.AsyncLoadGif.VideoType.DIRECT) && submission.getDataNode().has("media")) && submission.getDataNode().get("media").has("reddit_video")) && submission.getDataNode().get("media").get("reddit_video").has("fallback_url")) {
                myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_URL, org.apache.commons.text.StringEscapeUtils.unescapeJson(submission.getDataNode().get("media").get("reddit_video").get("fallback_url").asText()).replace("&amp;", "&"));
            } else {
                myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_URL, submission.getUrl());
            }
            if (submission.getDataNode().has("preview") && submission.getDataNode().get("preview").get("images").get(0).get("source").has("height")) {
                // Load the preview image which has probably already been cached in memory instead of the direct link
                java.lang.String previewUrl = org.apache.commons.text.StringEscapeUtils.escapeHtml4(submission.getDataNode().get("preview").get("images").get(0).get("source").get("url").asText());
                myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_DISPLAY_URL, previewUrl);
            }
            me.ccrama.redditslide.SubmissionViews.PopulateNewsViewHolder.addAdaptorPosition(myIntent, submission, adapterPosition);
            contextActivity.startActivity(myIntent);
        } else {
            me.ccrama.redditslide.util.LinkUtil.openExternally(submission.getUrl());
        }
    }

    public static int getCurrentTintColor(android.content.Context v) {
        return me.ccrama.redditslide.SubmissionViews.PopulateNewsViewHolder.getStyleAttribColorValue(v, me.ccrama.redditslide.R.attr.tintColor, android.graphics.Color.WHITE);
    }

    public java.lang.String reason;

    boolean[] chosen = new boolean[]{ false, false, false };

    boolean[] oldChosen = new boolean[]{ false, false, false };

    public static int getWhiteTintColor() {
        return me.ccrama.redditslide.Visuals.Palette.ThemeEnum.DARK.getTint();
    }

    public <T extends net.dean.jraw.models.Contribution> void showBottomSheet(final android.app.Activity mContext, final net.dean.jraw.models.Submission submission, final me.ccrama.redditslide.Adapters.NewsViewHolder holder, final java.util.List<T> posts, final java.lang.String baseSub, final android.support.v7.widget.RecyclerView recyclerview, final boolean full) {
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
                    case 5 :
                        hideSubmission(submission, posts, baseSub, recyclerview, mContext);
                        break;
                    case 7 :
                        me.ccrama.redditslide.util.LinkUtil.openExternally(submission.getUrl());
                        if (submission.isNsfw() && (!me.ccrama.redditslide.SettingValues.storeNSFWHistory)) {
                            // Do nothing if the post is NSFW and storeNSFWHistory is not enabled
                        } else if (me.ccrama.redditslide.SettingValues.storeHistory) {
                            me.ccrama.redditslide.HasSeen.addSeen(submission.getFullName());
                        }
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
                                new me.ccrama.redditslide.SubmissionViews.PopulateNewsViewHolder.AsyncReportTask(submission, holder.itemView).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR, reportReason);
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
                        me.ccrama.redditslide.Reddit.defaultShareText(android.text.Html.fromHtml(submission.getTitle()).toString(), "https://reddit.com" + submission.getPermalink(), mContext);
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

    public void doText(me.ccrama.redditslide.Adapters.NewsViewHolder holder, net.dean.jraw.models.Submission submission, android.content.Context mContext, java.lang.String baseSub) {
        android.text.SpannableStringBuilder t = me.ccrama.redditslide.SubmissionCache.getTitleLine(submission, mContext);
        android.text.SpannableStringBuilder l = me.ccrama.redditslide.SubmissionCache.getInfoLine(submission, mContext, baseSub);
        int[] textSizeAttr = new int[]{ me.ccrama.redditslide.R.attr.font_cardtitle, me.ccrama.redditslide.R.attr.font_cardinfo };
        android.content.res.TypedArray a = mContext.obtainStyledAttributes(textSizeAttr);
        int textSizeT = a.getDimensionPixelSize(0, 18);
        int textSizeI = a.getDimensionPixelSize(1, 14);
        a.recycle();
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
        holder.title.setText(s);
    }

    public <T extends net.dean.jraw.models.Contribution> void populateNewsViewHolder(final me.ccrama.redditslide.Adapters.NewsViewHolder holder, final net.dean.jraw.models.Submission submission, final android.app.Activity mContext, boolean fullscreen, final boolean full, final java.util.List<T> posts, final android.support.v7.widget.RecyclerView recyclerview, final boolean same, final boolean offline, final java.lang.String baseSub, @android.support.annotation.Nullable
    final me.ccrama.redditslide.Adapters.CommentAdapter adapter) {
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
        // Save the score so we can use it in the OnClickListeners for the vote buttons
        android.widget.ImageView thumbImage2 = holder.thumbnail;
        if (holder.leadImage.thumbImage2 == null) {
            holder.leadImage.setThumbnail(thumbImage2);
        }
        final me.ccrama.redditslide.ContentType.Type type = me.ccrama.redditslide.ContentType.getContentType(submission);
        me.ccrama.redditslide.SubmissionViews.PopulateNewsViewHolder.addClickFunctions(holder.itemView, type, mContext, submission, holder, full);
        holder.comment.setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                me.ccrama.redditslide.OpenRedditLink.openUrl(mContext, submission.getPermalink(), true);
            }
        });
        if (thumbImage2 != null) {
            me.ccrama.redditslide.SubmissionViews.PopulateNewsViewHolder.addClickFunctions(thumbImage2, type, mContext, submission, holder, full);
        }
        holder.leadImage.setSubmissionNews(submission, full, baseSub, type);
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
        doText(holder, submission, mContext, baseSub);
        if (me.ccrama.redditslide.HasSeen.getSeen(submission) && (!full)) {
            holder.title.setAlpha(0.54F);
        } else {
            holder.title.setAlpha(1.0F);
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
            android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(contextView, me.ccrama.redditslide.R.string.msg_report_sent, android.support.design.widget.Snackbar.LENGTH_SHORT);
            android.view.View view = s.getView();
            android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(android.graphics.Color.WHITE);
            s.show();
        }
    }
}