package me.ccrama.redditslide.Adapters;
import java.util.Locale;
import me.ccrama.redditslide.Activities.NewsActivity;
import me.ccrama.redditslide.ContentType;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import me.ccrama.redditslide.Activities.MainActivity;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Activities.SubredditView;
import me.ccrama.redditslide.util.NetworkUtil;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.TimeUtils;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.HasSeen;
import me.ccrama.redditslide.Reddit;
import java.util.List;
import me.ccrama.redditslide.BuildConfig;
import me.ccrama.redditslide.LastComments;
import java.util.Collections;
import me.ccrama.redditslide.SubmissionCache;
import java.net.UnknownHostException;
import me.ccrama.redditslide.PostLoader;
import me.ccrama.redditslide.PostMatch;
import me.ccrama.redditslide.OfflineSubreddit;
import me.ccrama.redditslide.Constants;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.Activities.BaseActivity;
import me.ccrama.redditslide.Fragments.SubmissionsView;
import me.ccrama.redditslide.Synccit.MySynccitReadTask;
/**
 * This class is reponsible for loading subreddit specific submissions {@link loadMore(Context,
 * SubmissionDisplay, boolean, String)} is implemented asynchronously. <p/> Created by ccrama on
 * 9/17/2015.
 */
public class SubredditPostsRealm implements me.ccrama.redditslide.PostLoader {
    public java.util.List<net.dean.jraw.models.Submission> posts;

    public java.lang.String subreddit;

    public java.lang.String subredditRandom;

    public boolean nomore = false;

    public boolean offline;

    public boolean forced;

    public boolean loading;

    private net.dean.jraw.paginators.Paginator paginator;

    public me.ccrama.redditslide.OfflineSubreddit cached;

    android.content.Context c;

    boolean force18;

    public SubredditPostsRealm(java.lang.String subreddit, android.content.Context c) {
        posts = new java.util.ArrayList<>();
        this.subreddit = subreddit;
        this.c = c;
    }

    public SubredditPostsRealm(java.lang.String subreddit, android.content.Context c, boolean force18) {
        posts = new java.util.ArrayList<>();
        this.subreddit = subreddit;
        this.c = c;
        this.force18 = force18;
    }

    @java.lang.Override
    public void loadMore(final android.content.Context context, final me.ccrama.redditslide.Adapters.SubmissionDisplay display, final boolean reset) {
        new me.ccrama.redditslide.Adapters.SubredditPostsRealm.LoadData(context, display, reset).execute(subreddit);
    }

    public void loadMore(android.content.Context context, me.ccrama.redditslide.Adapters.SubmissionDisplay display, boolean reset, java.lang.String subreddit) {
        this.subreddit = subreddit;
        loadMore(context, display, reset);
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

    public java.util.ArrayList<java.lang.String> all;

    @java.lang.Override
    public java.util.List<net.dean.jraw.models.Submission> getPosts() {
        return posts;
    }

    @java.lang.Override
    public boolean hasMore() {
        return !nomore;
    }

    public boolean skipOne;

    boolean authedOnce = false;

    boolean usedOffline;

    public long currentid;

    public me.ccrama.redditslide.Adapters.SubmissionDisplay displayer;

    /**
     * Asynchronous task for loading data
     */
    private class LoadData extends android.os.AsyncTask<java.lang.String, java.lang.Void, java.util.List<net.dean.jraw.models.Submission>> {
        final boolean reset;

        android.content.Context context;

        public LoadData(android.content.Context context, me.ccrama.redditslide.Adapters.SubmissionDisplay display, boolean reset) {
            this.context = context;
            displayer = display;
            this.reset = reset;
        }

        public int start;

        @java.lang.Override
        public void onPostExecute(final java.util.List<net.dean.jraw.models.Submission> submissions) {
            loading = false;
            if (error != null) {
                if (error instanceof net.dean.jraw.http.NetworkException) {
                    net.dean.jraw.http.NetworkException e = ((net.dean.jraw.http.NetworkException) (error));
                    if ((e.getResponse().getStatusCode() == 403) && (!authedOnce)) {
                        if ((me.ccrama.redditslide.Reddit.authentication != null) && me.ccrama.redditslide.Authentication.didOnline) {
                            me.ccrama.redditslide.Reddit.authentication.updateToken(context);
                        } else if (me.ccrama.redditslide.util.NetworkUtil.isConnected(context) && (me.ccrama.redditslide.Reddit.authentication == null)) {
                            me.ccrama.redditslide.Reddit.authentication = new me.ccrama.redditslide.Authentication(context);
                        }
                        authedOnce = true;
                        loadMore(context, displayer, reset, subreddit);
                        return;
                    } else {
                        android.widget.Toast.makeText(context, ("A server error occurred, " + e.getResponse().getStatusCode()) + (e.getResponse().getStatusMessage().isEmpty() ? "" : ": " + e.getResponse().getStatusMessage()), android.widget.Toast.LENGTH_SHORT).show();
                    }
                }
                if (error.getCause() instanceof java.net.UnknownHostException) {
                    android.widget.Toast.makeText(context, "Loading failed, please check your internet connection", android.widget.Toast.LENGTH_LONG).show();
                }
                displayer.updateError();
            } else if ((submissions != null) && (!submissions.isEmpty())) {
                if ((displayer instanceof me.ccrama.redditslide.Fragments.SubmissionsView) && ((me.ccrama.redditslide.Fragments.SubmissionsView) (displayer)).adapter.isError) {
                    ((me.ccrama.redditslide.Fragments.SubmissionsView) (displayer)).adapter.undoSetError();
                }
                java.lang.String[] ids = new java.lang.String[submissions.size()];
                int i = 0;
                for (net.dean.jraw.models.Submission s : submissions) {
                    ids[i] = s.getId();
                    i++;
                }
                // update online
                displayer.updateSuccess(posts, start);
                currentid = 0;
                me.ccrama.redditslide.OfflineSubreddit.currentid = currentid;
                if (c instanceof me.ccrama.redditslide.Activities.BaseActivity) {
                    ((me.ccrama.redditslide.Activities.BaseActivity) (c)).setShareUrl("https://reddit.com/r/" + subreddit);
                }
                if ((subreddit.equals("random") || subreddit.equals("myrandom")) || subreddit.equals("randnsfw")) {
                    subredditRandom = submissions.get(0).getSubredditName();
                }
                if ((context instanceof me.ccrama.redditslide.Activities.SubredditView) && ((subreddit.equals("random") || subreddit.equals("myrandom")) || subreddit.equals("randnsfw"))) {
                    ((me.ccrama.redditslide.Activities.SubredditView) (context)).subreddit = subredditRandom;
                    ((me.ccrama.redditslide.Activities.SubredditView) (context)).executeAsyncSubreddit(subredditRandom);
                }
                if ((!me.ccrama.redditslide.SettingValues.synccitName.isEmpty()) && (!offline)) {
                    new me.ccrama.redditslide.Synccit.MySynccitReadTask(displayer).execute(ids);
                }
            } else if (submissions != null) {
                // end of submissions
                nomore = true;
                displayer.updateSuccess(posts, posts.size() + 1);
            } else if (((!all.isEmpty()) && (!nomore)) && me.ccrama.redditslide.SettingValues.cache) {
                if (context instanceof me.ccrama.redditslide.Activities.NewsActivity) {
                    doNewsActivityOffline(context, displayer);
                }
            } else if (!nomore) {
                // error
                me.ccrama.redditslide.util.LogUtil.v("Setting error");
                displayer.updateError();
            }
        }

        @java.lang.Override
        protected java.util.List<net.dean.jraw.models.Submission> doInBackground(java.lang.String... subredditPaginators) {
            if (me.ccrama.redditslide.BuildConfig.DEBUG)
                me.ccrama.redditslide.util.LogUtil.v("Loading data");

            if ((!me.ccrama.redditslide.util.NetworkUtil.isConnected(context)) && (!me.ccrama.redditslide.Authentication.didOnline)) {
                android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "Using offline data");
                offline = true;
                usedOffline = true;
                all = me.ccrama.redditslide.OfflineSubreddit.getAll(subreddit);
                return null;
            } else {
                offline = false;
                usedOffline = false;
            }
            if (reset || (paginator == null)) {
                offline = false;
                nomore = false;
                java.lang.String sub = subredditPaginators[0].toLowerCase(java.util.Locale.ENGLISH);
                me.ccrama.redditslide.util.LogUtil.v(sub);
                if (sub.equals("frontpage")) {
                    paginator = new net.dean.jraw.paginators.SubredditPaginator(me.ccrama.redditslide.Authentication.reddit);
                } else if (!sub.contains(".")) {
                    paginator = new net.dean.jraw.paginators.SubredditPaginator(me.ccrama.redditslide.Authentication.reddit, sub);
                } else {
                    paginator = new net.dean.jraw.paginators.DomainPaginator(me.ccrama.redditslide.Authentication.reddit, sub);
                }
                paginator.setSorting(me.ccrama.redditslide.SettingValues.getSubmissionSort(subreddit));
                paginator.setTimePeriod(me.ccrama.redditslide.SettingValues.getSubmissionTimePeriod(subreddit));
                paginator.setLimit(me.ccrama.redditslide.Constants.PAGINATOR_POST_LIMIT);
            }
            java.util.List<net.dean.jraw.models.Submission> filteredSubmissions = getNextFiltered();
            if (!(me.ccrama.redditslide.SettingValues.noImages && (((!me.ccrama.redditslide.util.NetworkUtil.isConnectedWifi(c)) && me.ccrama.redditslide.SettingValues.lowResMobile) || me.ccrama.redditslide.SettingValues.lowResAlways))) {
                loadPhotos(filteredSubmissions);
            }
            if (me.ccrama.redditslide.SettingValues.storeHistory) {
                me.ccrama.redditslide.HasSeen.setHasSeenSubmission(filteredSubmissions);
                me.ccrama.redditslide.LastComments.setCommentsSince(filteredSubmissions);
            }
            me.ccrama.redditslide.SubmissionCache.cacheSubmissions(filteredSubmissions, context, subreddit);
            if ((reset || offline) || (posts == null)) {
                posts = new java.util.ArrayList(new java.util.LinkedHashSet(filteredSubmissions));
                start = -1;
            } else {
                posts.addAll(filteredSubmissions);
                posts = new java.util.ArrayList(new java.util.LinkedHashSet(posts));
                offline = false;
            }
            if (!usedOffline) {
                me.ccrama.redditslide.OfflineSubreddit.getSubNoLoad(subreddit.toLowerCase(java.util.Locale.ENGLISH)).overwriteSubmissions(posts).writeToMemory(context);
            }
            start = 0;
            if (posts != null) {
                start = posts.size() + 1;
            }
            return filteredSubmissions;
        }

        public java.util.ArrayList<net.dean.jraw.models.Submission> getNextFiltered() {
            java.util.ArrayList<net.dean.jraw.models.Submission> filteredSubmissions = new java.util.ArrayList<>();
            java.util.ArrayList<net.dean.jraw.models.Submission> adding = new java.util.ArrayList<>();
            try {
                if ((paginator != null) && paginator.hasNext()) {
                    if (force18 && (paginator instanceof net.dean.jraw.paginators.SubredditPaginator)) {
                        ((net.dean.jraw.paginators.SubredditPaginator) (paginator)).setObeyOver18(false);
                    }
                    adding.addAll(paginator.next());
                } else {
                    nomore = true;
                }
                for (net.dean.jraw.models.Submission s : adding) {
                    if (!me.ccrama.redditslide.PostMatch.doesMatch(s, paginator instanceof net.dean.jraw.paginators.SubredditPaginator ? ((net.dean.jraw.paginators.SubredditPaginator) (paginator)).getSubreddit() : ((net.dean.jraw.paginators.DomainPaginator) (paginator)).getDomain(), force18)) {
                        filteredSubmissions.add(s);
                    }
                }
                if (((paginator != null) && paginator.hasNext()) && filteredSubmissions.isEmpty()) {
                    filteredSubmissions.addAll(getNextFiltered());
                }
            } catch (java.lang.Exception e) {
                e.printStackTrace();
                error = e;
                if ((e.getMessage() != null) && e.getMessage().contains("Forbidden")) {
                    me.ccrama.redditslide.Reddit.authentication.updateToken(context);
                }
            }
            return filteredSubmissions;
        }

        java.lang.Exception error;
    }

    public void doNewsActivityOffline(final android.content.Context c, final me.ccrama.redditslide.Adapters.SubmissionDisplay displayer) {
        me.ccrama.redditslide.util.LogUtil.v(subreddit);
        if (all == null) {
            all = me.ccrama.redditslide.OfflineSubreddit.getAll(subreddit);
        }
        java.util.Collections.rotate(all, -1);// Move 0, or "submission only", to the end

        offline = true;
        final java.lang.String[] titles = new java.lang.String[all.size()];
        final java.lang.String[] base = new java.lang.String[all.size()];
        int i = 0;
        for (java.lang.String s : all) {
            java.lang.String[] split = s.split(",");
            titles[i] = (java.lang.Long.valueOf(split[1]) == 0) ? c.getString(me.ccrama.redditslide.R.string.settings_backup_submission_only) : me.ccrama.redditslide.TimeUtils.getTimeAgo(java.lang.Long.valueOf(split[1]), c) + c.getString(me.ccrama.redditslide.R.string.settings_backup_comments);
            base[i] = s;
            i++;
        }
        ((me.ccrama.redditslide.Activities.NewsActivity) (c)).getSupportActionBar().setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_LIST);
        ((me.ccrama.redditslide.Activities.NewsActivity) (c)).getSupportActionBar().setListNavigationCallbacks(new me.ccrama.redditslide.Adapters.OfflineSubAdapter(c, android.R.layout.simple_list_item_1, titles), new android.support.v7.app.ActionBar.OnNavigationListener() {
            @java.lang.Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                final java.lang.String[] s2 = base[itemPosition].split(",");
                me.ccrama.redditslide.OfflineSubreddit.currentid = java.lang.Long.valueOf(s2[1]);
                currentid = me.ccrama.redditslide.OfflineSubreddit.currentid;
                new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
                    me.ccrama.redditslide.OfflineSubreddit cached;

                    @java.lang.Override
                    protected java.lang.Void doInBackground(java.lang.Void... params) {
                        cached = me.ccrama.redditslide.OfflineSubreddit.getSubreddit(subreddit, java.lang.Long.valueOf(s2[1]), true, c);
                        java.util.List<net.dean.jraw.models.Submission> finalSubs = new java.util.ArrayList<>();
                        for (net.dean.jraw.models.Submission s : cached.submissions) {
                            if (!me.ccrama.redditslide.PostMatch.doesMatch(s, subreddit, force18)) {
                                finalSubs.add(s);
                            }
                        }
                        posts = finalSubs;
                        return null;
                    }

                    @java.lang.Override
                    protected void onPostExecute(java.lang.Void aVoid) {
                        if (cached.submissions.isEmpty()) {
                            displayer.updateOfflineError();
                        }
                        // update offline
                        displayer.updateOffline(posts, java.lang.Long.valueOf(s2[1]));
                    }
                }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                return true;
            }
        });
    }
}