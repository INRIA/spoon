package me.ccrama.redditslide.Adapters;
import me.ccrama.redditslide.SubmissionCache;
import java.util.Locale;
import me.ccrama.redditslide.ContentType;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import me.ccrama.redditslide.PostLoader;
import me.ccrama.redditslide.PostMatch;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.OfflineSubreddit;
import me.ccrama.redditslide.util.NetworkUtil;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.HasSeen;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Constants;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.UserSubscriptions;
import java.util.List;
import me.ccrama.redditslide.Synccit.MySynccitReadTask;
import me.ccrama.redditslide.LastComments;
/**
 * This class is reponsible for loading subreddit specific submissions
 * {@link loadMore(Context, SubmissionDisplay, boolean, String)} is implemented
 * asynchronously.
 * <p>
 * Created by ccrama on 9/17/2015.
 */
public class MultiredditPosts implements me.ccrama.redditslide.PostLoader {
    public java.util.List<net.dean.jraw.models.Submission> posts;

    public boolean nomore = false;

    public boolean stillShow;

    public boolean offline;

    public boolean loading;

    public java.lang.String profile;

    private net.dean.jraw.paginators.MultiRedditPaginator paginator;

    android.content.Context c;

    me.ccrama.redditslide.Adapters.MultiredditAdapter adapter;

    public MultiredditPosts(java.lang.String multireddit) {
        this(multireddit, "");
    }

    public MultiredditPosts(java.lang.String multireddit, java.lang.String profile) {
        posts = new java.util.ArrayList<>();
        me.ccrama.redditslide.util.LogUtil.e(("MJWHITTA: Profile is " + profile) + ".");
        me.ccrama.redditslide.util.LogUtil.e(("MJWHITTA: Multireddit is " + multireddit) + ".");
        if (profile.isEmpty()) {
            this.multiReddit = me.ccrama.redditslide.UserSubscriptions.getMultiredditByDisplayName(multireddit);
        } else {
            this.multiReddit = me.ccrama.redditslide.UserSubscriptions.getPublicMultiredditByDisplayName(profile, multireddit);
        }
        this.profile = profile;
    }

    @java.lang.Override
    public void loadMore(android.content.Context context, me.ccrama.redditslide.Adapters.SubmissionDisplay displayer, boolean reset) {
        this.c = context;
        new me.ccrama.redditslide.Adapters.MultiredditPosts.LoadData(context, displayer, reset).execute(multiReddit);
    }

    public void loadMore(android.content.Context context, me.ccrama.redditslide.Adapters.SubmissionDisplay displayer, boolean reset, me.ccrama.redditslide.Adapters.MultiredditAdapter adapter) {
        this.adapter = adapter;
        this.c = context;
        loadMore(context, displayer, reset);
    }

    public void loadPhotos(java.util.List<net.dean.jraw.models.Submission> submissions) {
        for (net.dean.jraw.models.Submission submission : submissions) {
            java.lang.String url;
            me.ccrama.redditslide.ContentType.Type type = me.ccrama.redditslide.ContentType.getContentType(submission);
            if (submission.getThumbnails() != null) {
                if (((type == me.ccrama.redditslide.ContentType.Type.IMAGE) || (type == me.ccrama.redditslide.ContentType.Type.SELF)) || (submission.getThumbnailType() == net.dean.jraw.models.Submission.ThumbnailType.URL)) {
                    if (type == me.ccrama.redditslide.ContentType.Type.IMAGE) {
                        if ((((((!me.ccrama.redditslide.util.NetworkUtil.isConnectedWifi(c)) && me.ccrama.redditslide.SettingValues.lowResMobile) || me.ccrama.redditslide.SettingValues.lowResAlways) && (submission.getThumbnails() != null)) && (submission.getThumbnails().getVariations() != null)) && (submission.getThumbnails().getVariations().length > 0)) {
                            int length = submission.getThumbnails().getVariations().length;
                            if (me.ccrama.redditslide.SettingValues.lqLow && (length >= 3)) {
                                url = android.text.Html.fromHtml(submission.getThumbnails().getVariations()[2].getUrl()).toString();// unescape url characters

                            } else if (me.ccrama.redditslide.SettingValues.lqMid && (length >= 4)) {
                                url = android.text.Html.fromHtml(submission.getThumbnails().getVariations()[3].getUrl()).toString();// unescape url characters

                            } else if (length >= 5) {
                                url = android.text.Html.fromHtml(submission.getThumbnails().getVariations()[length - 1].getUrl()).toString();// unescape url characters

                            } else {
                                url = android.text.Html.fromHtml(submission.getThumbnails().getSource().getUrl()).toString();// unescape url characters

                            }
                        } else if (submission.getDataNode().has("preview") && submission.getDataNode().get("preview").get("images").get(0).get("source").has("height")) {
                            // Load the preview image which has probably already been cached in memory instead of the direct link
                            url = submission.getDataNode().get("preview").get("images").get(0).get("source").get("url").asText();
                        } else {
                            url = submission.getUrl();
                        }
                        ((me.ccrama.redditslide.Reddit) (c.getApplicationContext())).getImageLoader().loadImage(url, new com.nostra13.universalimageloader.core.listener.ImageLoadingListener() {
                            @java.lang.Override
                            public void onLoadingStarted(java.lang.String imageUri, android.view.View view) {
                            }

                            @java.lang.Override
                            public void onLoadingFailed(java.lang.String imageUri, android.view.View view, com.nostra13.universalimageloader.core.assist.FailReason failReason) {
                            }

                            @java.lang.Override
                            public void onLoadingComplete(java.lang.String imageUri, android.view.View view, android.graphics.Bitmap loadedImage) {
                            }

                            @java.lang.Override
                            public void onLoadingCancelled(java.lang.String imageUri, android.view.View view) {
                            }
                        });
                    } else if (submission.getThumbnails() != null) {
                        if ((((!me.ccrama.redditslide.util.NetworkUtil.isConnectedWifi(c)) && me.ccrama.redditslide.SettingValues.lowResMobile) || me.ccrama.redditslide.SettingValues.lowResAlways) && (submission.getThumbnails().getVariations().length != 0)) {
                            int length = submission.getThumbnails().getVariations().length;
                            if (me.ccrama.redditslide.SettingValues.lqLow && (length >= 3)) {
                                url = android.text.Html.fromHtml(submission.getThumbnails().getVariations()[2].getUrl()).toString();// unescape url characters

                            } else if (me.ccrama.redditslide.SettingValues.lqMid && (length >= 4)) {
                                url = android.text.Html.fromHtml(submission.getThumbnails().getVariations()[3].getUrl()).toString();// unescape url characters

                            } else if (length >= 5) {
                                url = android.text.Html.fromHtml(submission.getThumbnails().getVariations()[length - 1].getUrl()).toString();// unescape url characters

                            } else {
                                url = android.text.Html.fromHtml(submission.getThumbnails().getSource().getUrl()).toString();// unescape url characters

                            }
                        } else {
                            url = android.text.Html.fromHtml(submission.getThumbnails().getSource().getUrl()).toString();// unescape url characters

                        }
                        ((me.ccrama.redditslide.Reddit) (c.getApplicationContext())).getImageLoader().loadImage(url, new com.nostra13.universalimageloader.core.listener.ImageLoadingListener() {
                            @java.lang.Override
                            public void onLoadingStarted(java.lang.String imageUri, android.view.View view) {
                            }

                            @java.lang.Override
                            public void onLoadingFailed(java.lang.String imageUri, android.view.View view, com.nostra13.universalimageloader.core.assist.FailReason failReason) {
                            }

                            @java.lang.Override
                            public void onLoadingComplete(java.lang.String imageUri, android.view.View view, android.graphics.Bitmap loadedImage) {
                            }

                            @java.lang.Override
                            public void onLoadingCancelled(java.lang.String imageUri, android.view.View view) {
                            }
                        });
                    } else if ((submission.getThumbnail() != null) && ((submission.getThumbnailType() == net.dean.jraw.models.Submission.ThumbnailType.URL) || (submission.getThumbnailType() == net.dean.jraw.models.Submission.ThumbnailType.NSFW))) {
                        ((me.ccrama.redditslide.Reddit) (c.getApplicationContext())).getImageLoader().loadImage(submission.getUrl(), new com.nostra13.universalimageloader.core.listener.ImageLoadingListener() {
                            @java.lang.Override
                            public void onLoadingStarted(java.lang.String imageUri, android.view.View view) {
                            }

                            @java.lang.Override
                            public void onLoadingFailed(java.lang.String imageUri, android.view.View view, com.nostra13.universalimageloader.core.assist.FailReason failReason) {
                            }

                            @java.lang.Override
                            public void onLoadingComplete(java.lang.String imageUri, android.view.View view, android.graphics.Bitmap loadedImage) {
                            }

                            @java.lang.Override
                            public void onLoadingCancelled(java.lang.String imageUri, android.view.View view) {
                            }
                        });
                    }
                }
            }
        }
    }

    @java.lang.Override
    public java.util.List<net.dean.jraw.models.Submission> getPosts() {
        return posts;
    }

    public net.dean.jraw.models.MultiReddit multiReddit;

    @java.lang.Override
    public boolean hasMore() {
        return !nomore;
    }

    public boolean skipOne;

    boolean usedOffline;

    /**
     * Asynchronous task for loading data
     */
    private class LoadData extends android.os.AsyncTask<net.dean.jraw.models.MultiReddit, java.lang.Void, java.util.List<net.dean.jraw.models.Submission>> {
        final boolean reset;

        android.content.Context context;

        final me.ccrama.redditslide.Adapters.SubmissionDisplay displayer;

        public LoadData(android.content.Context context, me.ccrama.redditslide.Adapters.SubmissionDisplay displayer, boolean reset) {
            this.context = context;
            this.displayer = displayer;
            this.reset = reset;
        }

        @java.lang.Override
        public void onPostExecute(java.util.List<net.dean.jraw.models.Submission> submissions) {
            loading = false;
            if ((submissions != null) && (!submissions.isEmpty())) {
                // new submissions found
                int start = 0;
                if (posts != null) {
                    start = posts.size() + 1;
                }
                if ((reset || offline) || (posts == null)) {
                    posts = new java.util.ArrayList(new java.util.LinkedHashSet(submissions));
                    start = -1;
                } else {
                    posts.addAll(submissions);
                    posts = new java.util.ArrayList(new java.util.LinkedHashSet(posts));
                    offline = false;
                }
                if (!usedOffline)
                    me.ccrama.redditslide.OfflineSubreddit.getSubreddit("multi" + multiReddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), false, context).overwriteSubmissions(posts).writeToMemory(c);

                java.lang.String[] ids = new java.lang.String[submissions.size()];
                int i = 0;
                for (net.dean.jraw.models.Submission s : submissions) {
                    ids[i] = s.getId();
                    i++;
                }
                if ((!me.ccrama.redditslide.SettingValues.synccitName.isEmpty()) && (!offline)) {
                    new me.ccrama.redditslide.Synccit.MySynccitReadTask().execute(ids);
                }
                final int finalStart = start;
                // update online
                displayer.updateSuccess(posts, finalStart);
            } else if (submissions != null) {
                // end of submissions
                nomore = true;
            } else if (((!me.ccrama.redditslide.OfflineSubreddit.getSubreddit("multi" + multiReddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), false, context).submissions.isEmpty()) && (!nomore)) && me.ccrama.redditslide.SettingValues.cache) {
                offline = true;
                final me.ccrama.redditslide.OfflineSubreddit cached = me.ccrama.redditslide.OfflineSubreddit.getSubreddit("multi" + multiReddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), true, context);
                java.util.List<net.dean.jraw.models.Submission> finalSubs = new java.util.ArrayList<>();
                for (net.dean.jraw.models.Submission s : cached.submissions) {
                    if (!me.ccrama.redditslide.PostMatch.doesMatch(s, "multi" + multiReddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), false)) {
                        finalSubs.add(s);
                    }
                }
                posts = finalSubs;
                if (!cached.submissions.isEmpty()) {
                    stillShow = true;
                } else {
                    displayer.updateOfflineError();
                }
                // update offline
                displayer.updateOffline(submissions, cached.time);
            } else if (!nomore) {
                // error
                displayer.updateError();
            }
        }

        @java.lang.Override
        protected java.util.List<net.dean.jraw.models.Submission> doInBackground(net.dean.jraw.models.MultiReddit... subredditPaginators) {
            if (!me.ccrama.redditslide.util.NetworkUtil.isConnected(context)) {
                offline = true;
                return null;
            } else {
                offline = false;
            }
            stillShow = true;
            if (reset || (paginator == null)) {
                offline = false;
                paginator = new net.dean.jraw.paginators.MultiRedditPaginator(me.ccrama.redditslide.Authentication.reddit, subredditPaginators[0]);
                paginator.setSorting(me.ccrama.redditslide.SettingValues.getSubmissionSort("multi" + subredditPaginators[0].getDisplayName().toLowerCase(java.util.Locale.ENGLISH)));
                paginator.setTimePeriod(me.ccrama.redditslide.SettingValues.getSubmissionTimePeriod("multi" + subredditPaginators[0].getDisplayName().toLowerCase(java.util.Locale.ENGLISH)));
                paginator.setLimit(me.ccrama.redditslide.Constants.PAGINATOR_POST_LIMIT);
            }
            java.util.List<net.dean.jraw.models.Submission> things = new java.util.ArrayList<>();
            try {
                if ((paginator != null) && paginator.hasNext()) {
                    things.addAll(paginator.next());
                } else {
                    nomore = true;
                }
            } catch (java.lang.Exception e) {
                e.printStackTrace();
                if (e.getMessage().contains("Forbidden")) {
                    me.ccrama.redditslide.Reddit.authentication.updateToken(context);
                }
            }
            java.util.List<net.dean.jraw.models.Submission> filteredSubmissions = new java.util.ArrayList<>();
            for (net.dean.jraw.models.Submission s : things) {
                if (!me.ccrama.redditslide.PostMatch.doesMatch(s, paginator.getMultiReddit().getDisplayName(), false)) {
                    filteredSubmissions.add(s);
                }
            }
            me.ccrama.redditslide.HasSeen.setHasSeenSubmission(filteredSubmissions);
            me.ccrama.redditslide.SubmissionCache.cacheSubmissions(filteredSubmissions, context, paginator.getMultiReddit().getDisplayName());
            if (!(me.ccrama.redditslide.SettingValues.noImages && (((!me.ccrama.redditslide.util.NetworkUtil.isConnectedWifi(c)) && me.ccrama.redditslide.SettingValues.lowResMobile) || me.ccrama.redditslide.SettingValues.lowResAlways)))
                loadPhotos(filteredSubmissions);

            if (me.ccrama.redditslide.SettingValues.storeHistory)
                me.ccrama.redditslide.LastComments.setCommentsSince(filteredSubmissions);

            return filteredSubmissions;
        }
    }
}