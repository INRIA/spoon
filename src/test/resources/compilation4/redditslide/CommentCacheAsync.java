package me.ccrama.redditslide;
import java.util.Locale;
import java.util.Set;
import java.util.HashMap;
import me.ccrama.redditslide.Autocache.AutoCacheScheduler;
import java.util.ArrayList;
import me.ccrama.redditslide.Notifications.NotificationJobScheduler;
import java.util.concurrent.Executors;
import com.fasterxml.jackson.databind.JsonNode;
import me.ccrama.redditslide.util.NetworkUtil;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.Activities.CommentsScreenSingle;
import java.util.concurrent.ExecutorService;
import me.ccrama.redditslide.util.GifUtils;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
/**
 * Created by carlo_000 on 4/18/2016.
 */
public class CommentCacheAsync extends android.os.AsyncTask {
    public static final java.lang.String SAVED_SUBMISSIONS = "read later";

    java.util.List<net.dean.jraw.models.Submission> alreadyReceived;

    android.app.NotificationManager mNotifyManager;

    public CommentCacheAsync(java.util.List<net.dean.jraw.models.Submission> submissions, android.content.Context c, java.lang.String subreddit, boolean[] otherChoices) {
        alreadyReceived = submissions;
        this.context = c;
        this.subs = new java.lang.String[]{ subreddit };
        this.otherChoices = otherChoices;
    }

    public CommentCacheAsync(java.util.List<net.dean.jraw.models.Submission> submissions, android.app.Activity mContext, java.lang.String baseSub, java.lang.String alternateSubName) {
        this(submissions, mContext, baseSub, new boolean[]{ true, true });
    }

    public CommentCacheAsync(android.content.Context c, java.lang.String[] subreddits) {
        this.context = c;
        this.subs = subreddits;
    }

    java.lang.String[] subs;

    android.content.Context context;

    android.support.v4.app.NotificationCompat.Builder mBuilder;

    boolean[] otherChoices;

    public void loadPhotos(net.dean.jraw.models.Submission submission, android.content.Context c) {
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

    @java.lang.Override
    public java.lang.Void doInBackground(java.lang.Object[] params) {
        if ((me.ccrama.redditslide.Authentication.isLoggedIn && (me.ccrama.redditslide.Authentication.me == null)) || (me.ccrama.redditslide.Authentication.reddit == null)) {
            if (me.ccrama.redditslide.Authentication.reddit == null) {
                new me.ccrama.redditslide.Authentication(context);
            }
            if (me.ccrama.redditslide.Authentication.reddit != null) {
                try {
                    me.ccrama.redditslide.Authentication.me = me.ccrama.redditslide.Authentication.reddit.me();
                    me.ccrama.redditslide.Authentication.mod = me.ccrama.redditslide.Authentication.me.isMod();
                    me.ccrama.redditslide.Authentication.authentication.edit().putBoolean(me.ccrama.redditslide.Reddit.SHARED_PREF_IS_MOD, me.ccrama.redditslide.Authentication.mod).apply();
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
                } catch (java.lang.Exception e) {
                    new me.ccrama.redditslide.Authentication(context);
                }
            }
        }
        java.util.Map<java.lang.String, java.lang.String> multiNameToSubsMap = me.ccrama.redditslide.UserSubscriptions.getMultiNameToSubs(true);
        if (me.ccrama.redditslide.Authentication.reddit == null)
            me.ccrama.redditslide.Reddit.authentication = new me.ccrama.redditslide.Authentication(context);

        java.util.ArrayList<java.lang.String> success = new java.util.ArrayList<>();
        java.util.ArrayList<java.lang.String> error = new java.util.ArrayList<>();
        for (final java.lang.String fSub : subs) {
            final java.lang.String sub;
            final java.lang.String name = fSub;
            net.dean.jraw.models.CommentSort sortType = me.ccrama.redditslide.SettingValues.getCommentSorting(name);
            if (multiNameToSubsMap.containsKey(fSub)) {
                sub = multiNameToSubsMap.get(fSub);
            } else {
                sub = fSub;
            }
            if (!sub.isEmpty()) {
                if (!sub.equals(me.ccrama.redditslide.CommentCacheAsync.SAVED_SUBMISSIONS)) {
                    mNotifyManager = ((android.app.NotificationManager) (context.getSystemService(android.content.Context.NOTIFICATION_SERVICE)));
                    mBuilder = new android.support.v4.app.NotificationCompat.Builder(context);
                    mBuilder.setOngoing(true);
                    mBuilder.setChannelId(me.ccrama.redditslide.Reddit.CHANNEL_COMMENT_CACHE);
                    mBuilder.setContentTitle(context.getString(me.ccrama.redditslide.R.string.offline_caching_title, sub.equalsIgnoreCase("frontpage") ? name : name.contains("/m/") ? name : "/r/" + name)).setSmallIcon(me.ccrama.redditslide.R.drawable.save_png);
                }
                java.util.List<net.dean.jraw.models.Submission> submissions = new java.util.ArrayList<>();
                java.util.ArrayList<java.lang.String> newFullnames = new java.util.ArrayList<>();
                int count = 0;
                if (alreadyReceived != null) {
                    submissions.addAll(alreadyReceived);
                } else {
                    net.dean.jraw.paginators.SubredditPaginator p;
                    if (name.equalsIgnoreCase("frontpage")) {
                        p = new net.dean.jraw.paginators.SubredditPaginator(me.ccrama.redditslide.Authentication.reddit);
                    } else {
                        p = new net.dean.jraw.paginators.SubredditPaginator(me.ccrama.redditslide.Authentication.reddit, sub);
                    }
                    p.setLimit(me.ccrama.redditslide.Constants.PAGINATOR_POST_LIMIT);
                    try {
                        submissions.addAll(p.next());
                    } catch (java.lang.Exception e) {
                        e.printStackTrace();
                    }
                }
                int commentDepth = java.lang.Integer.valueOf(me.ccrama.redditslide.SettingValues.prefs.getString(me.ccrama.redditslide.SettingValues.COMMENT_DEPTH, "5"));
                int commentCount = java.lang.Integer.valueOf(me.ccrama.redditslide.SettingValues.prefs.getString(me.ccrama.redditslide.SettingValues.COMMENT_COUNT, "50"));
                android.util.Log.v("CommentCacheAsync", "comment count " + commentCount);
                int random = ((int) (java.lang.Math.random() * 100));
                for (final net.dean.jraw.models.Submission s : submissions) {
                    try {
                        com.fasterxml.jackson.databind.JsonNode n = getSubmission(new net.dean.jraw.http.SubmissionRequest.Builder(s.getId()).limit(commentCount).depth(commentDepth).sort(sortType).build());
                        net.dean.jraw.models.Submission s2 = net.dean.jraw.models.meta.SubmissionSerializer.withComments(n, net.dean.jraw.models.CommentSort.CONFIDENCE);
                        me.ccrama.redditslide.OfflineSubreddit.writeSubmission(n, s2, context);
                        newFullnames.add(s2.getFullName());
                        if (!me.ccrama.redditslide.SettingValues.noImages)
                            loadPhotos(s, context);

                        switch (me.ccrama.redditslide.ContentType.getContentType(s)) {
                            case GIF :
                                if (otherChoices[0]) {
                                    if (context instanceof android.app.Activity) {
                                        ((android.app.Activity) (context)).runOnUiThread(new java.lang.Runnable() {
                                            @java.lang.Override
                                            public void run() {
                                                java.util.concurrent.ExecutorService service = java.util.concurrent.Executors.newSingleThreadExecutor();
                                                new me.ccrama.redditslide.util.GifUtils.AsyncLoadGif().executeOnExecutor(service, s.getUrl());
                                            }
                                        });
                                    }
                                }
                                break;
                            case ALBUM :
                                // todo this AlbumUtils.saveAlbumToCache(context, s.getUrl());
                                if (otherChoices[1]) {
                                    break;
                                }
                        }
                    } catch (java.lang.Exception ignored) {
                    }
                    count = count + 1;
                    if (mBuilder != null) {
                        mBuilder.setProgress(submissions.size(), count, false);
                        mNotifyManager.notify(random, mBuilder.build());
                    }
                }
                me.ccrama.redditslide.OfflineSubreddit.newSubreddit(sub).writeToMemory(newFullnames);
                if (mBuilder != null) {
                    mNotifyManager.cancel(random);
                }
                if (!submissions.isEmpty())
                    success.add(sub);

            }
        }
        if (mBuilder != null) {
            // Removes the progress bar
            mBuilder.setContentText(context.getString(me.ccrama.redditslide.R.string.offline_caching_complete)).setSubText(success.size() + " subreddits cached").setProgress(0, 0, false);
            mBuilder.setOngoing(false);
            mNotifyManager.notify(2001, mBuilder.build());
        }
        return null;
    }

    public com.fasterxml.jackson.databind.JsonNode getSubmission(net.dean.jraw.http.SubmissionRequest request) throws net.dean.jraw.http.NetworkException {
        java.util.Map<java.lang.String, java.lang.String> args = new java.util.HashMap<>();
        if (request.getDepth() != null)
            args.put("depth", java.lang.Integer.toString(request.getDepth()));

        if (request.getContext() != null) {
            args.put("context", java.lang.Integer.toString(request.getContext()));
        }
        if (request.getLimit() != null)
            args.put("limit", java.lang.Integer.toString(request.getLimit()));

        if ((request.getFocus() != null) && (!net.dean.jraw.util.JrawUtils.isFullname(request.getFocus()))) {
            args.put("comment", request.getFocus());
        }
        net.dean.jraw.models.CommentSort sort = request.getSort();
        // Reddit sorts by confidence by default
        if (sort == null) {
            sort = net.dean.jraw.models.CommentSort.CONFIDENCE;
        }
        args.put("sort", sort.name().toLowerCase(java.util.Locale.ENGLISH));
        try {
            net.dean.jraw.http.RestResponse response = me.ccrama.redditslide.Authentication.reddit.execute(me.ccrama.redditslide.Authentication.reddit.request().path(java.lang.String.format("/comments/%s", request.getId())).query(args).build());
            return response.getJson();
        } catch (java.lang.Exception e) {
            return null;
        }
    }
}