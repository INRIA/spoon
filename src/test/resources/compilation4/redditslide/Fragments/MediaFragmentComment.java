package me.ccrama.redditslide.Fragments;
import me.ccrama.redditslide.ContentType;
import com.google.gson.JsonObject;
import me.ccrama.redditslide.Activities.ShadowboxComments;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Activities.MediaView;
import com.google.gson.Gson;
import me.ccrama.redditslide.Activities.Website;
import me.ccrama.redditslide.Views.MediaVideoView;
import me.ccrama.redditslide.util.NetworkUtil;
import me.ccrama.redditslide.util.LogUtil;
import java.net.URL;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.SecretConstants;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Adapters.CommentUrlObject;
import me.ccrama.redditslide.util.HttpUtil;
import java.io.IOException;
import me.ccrama.redditslide.Views.SubsamplingScaleImageView;
import java.net.URLConnection;
import me.ccrama.redditslide.util.LinkUtil;
import me.ccrama.redditslide.Views.ImageSource;
import me.ccrama.redditslide.util.GifUtils;
import me.ccrama.redditslide.SubmissionViews.PopulateShadowboxInfo;
import java.io.File;
import me.ccrama.redditslide.OpenRedditLink;
/**
 * Created by ccrama on 6/2/2015.
 */
public class MediaFragmentComment extends android.support.v4.app.Fragment {
    public java.lang.String contentUrl;

    public java.lang.String sub;

    public java.lang.String actuallyLoaded;

    public int i;

    private android.view.ViewGroup rootView;

    private me.ccrama.redditslide.Views.MediaVideoView videoView;

    private boolean imageShown;

    private float previous;

    private boolean hidden;

    private long stopPosition;

    public boolean isGif;

    private me.ccrama.redditslide.util.GifUtils.AsyncLoadGif gif;

    private me.ccrama.redditslide.Adapters.CommentUrlObject s;

    private okhttp3.OkHttpClient client;

    private com.google.gson.Gson gson;

    private java.lang.String mashapeKey;

    @java.lang.Override
    public void onDestroy() {
        super.onDestroy();
        me.ccrama.redditslide.util.LogUtil.v("Destroying");
        ((me.ccrama.redditslide.Views.SubsamplingScaleImageView) (rootView.findViewById(me.ccrama.redditslide.R.id.submission_image))).recycle();
    }

    @java.lang.Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && (videoView != null)) {
            videoView.seekTo(0);
            videoView.start();
        }
    }

    @java.lang.Override
    public void onResume() {
        super.onResume();
        if (videoView != null) {
            videoView.seekTo(((int) (stopPosition)));
            videoView.start();
        }
    }

    @java.lang.Override
    public void onSaveInstanceState(android.os.Bundle outState) {
        super.onSaveInstanceState(outState);
        if (videoView != null) {
            stopPosition = videoView.getCurrentPosition();
            videoView.pause();
            ((com.sothree.slidinguppanel.SlidingUpPanelLayout) (rootView.findViewById(me.ccrama.redditslide.R.id.sliding_layout))).setPanelState(com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED);
            outState.putLong("position", stopPosition);
        }
    }

    @java.lang.Override
    public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
        rootView = ((android.view.ViewGroup) (inflater.inflate(me.ccrama.redditslide.R.layout.submission_mediacard, container, false)));
        if ((savedInstanceState != null) && savedInstanceState.containsKey("position")) {
            stopPosition = savedInstanceState.getLong("position");
        }
        final com.sothree.slidinguppanel.SlidingUpPanelLayout slideLayout = rootView.findViewById(me.ccrama.redditslide.R.id.sliding_layout);
        me.ccrama.redditslide.SubmissionViews.PopulateShadowboxInfo.doActionbar(s.comment, rootView, getActivity(), true);
        rootView.findViewById(me.ccrama.redditslide.R.id.thumbimage2).setVisibility(android.view.View.GONE);
        me.ccrama.redditslide.ContentType.Type type = me.ccrama.redditslide.ContentType.getContentType(contentUrl);
        if (!me.ccrama.redditslide.ContentType.fullImage(type)) {
            me.ccrama.redditslide.Fragments.MediaFragmentComment.addClickFunctions(rootView.findViewById(me.ccrama.redditslide.R.id.submission_image), slideLayout, rootView, type, getActivity(), s);
        } else {
            rootView.findViewById(me.ccrama.redditslide.R.id.thumbimage2).setVisibility(android.view.View.GONE);
            me.ccrama.redditslide.Fragments.MediaFragmentComment.addClickFunctions(rootView.findViewById(me.ccrama.redditslide.R.id.submission_image), slideLayout, rootView, type, getActivity(), s);
        }
        doLoad(contentUrl);
        final android.view.View.OnClickListener openClick = new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                ((com.sothree.slidinguppanel.SlidingUpPanelLayout) (rootView.findViewById(me.ccrama.redditslide.R.id.sliding_layout))).setPanelState(com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        };
        rootView.findViewById(me.ccrama.redditslide.R.id.base).setOnClickListener(openClick);
        final android.view.View title = rootView.findViewById(me.ccrama.redditslide.R.id.title);
        title.getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {
            @java.lang.Override
            public void onGlobalLayout() {
                ((com.sothree.slidinguppanel.SlidingUpPanelLayout) (rootView.findViewById(me.ccrama.redditslide.R.id.sliding_layout))).setPanelHeight(title.getMeasuredHeight());
                title.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        ((com.sothree.slidinguppanel.SlidingUpPanelLayout) (rootView.findViewById(me.ccrama.redditslide.R.id.sliding_layout))).addPanelSlideListener(new com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener() {
            @java.lang.Override
            public void onPanelSlide(android.view.View panel, float slideOffset) {
            }

            @java.lang.Override
            public void onPanelStateChanged(android.view.View panel, com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState previousState, com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState newState) {
                if (newState == com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.EXPANDED) {
                    final net.dean.jraw.models.Comment c = s.comment.getComment();
                    rootView.findViewById(me.ccrama.redditslide.R.id.base).setOnClickListener(new android.view.View.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.view.View v) {
                            java.lang.String url = (((((("https://reddit.com" + "/r/") + c.getSubredditName()) + "/comments/") + c.getDataNode().get("link_id").asText().substring(3, c.getDataNode().get("link_id").asText().length())) + "/nothing/") + c.getId()) + "?context=3";
                            new me.ccrama.redditslide.OpenRedditLink(getActivity(), url);
                        }
                    });
                } else {
                    rootView.findViewById(me.ccrama.redditslide.R.id.base).setOnClickListener(openClick);
                }
            }
        });
        return rootView;
    }

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.os.Bundle bundle = this.getArguments();
        i = bundle.getInt("page");
        s = ((me.ccrama.redditslide.Activities.ShadowboxComments) (getActivity())).comments.get(i);
        sub = s.comment.getComment().getSubredditName();
        contentUrl = bundle.getString("contentUrl");
        client = me.ccrama.redditslide.Reddit.client;
        gson = new com.google.gson.Gson();
        mashapeKey = me.ccrama.redditslide.SecretConstants.getImgurApiKey(getContext());
    }

    public void doLoad(final java.lang.String contentUrl) {
        switch (me.ccrama.redditslide.ContentType.getContentType(contentUrl)) {
            case DEVIANTART :
                doLoadDeviantArt(contentUrl);
                break;
            case IMAGE :
                doLoadImage(contentUrl);
                break;
            case IMGUR :
                doLoadImgur(contentUrl);
                break;
            case XKCD :
                doLoadXKCD(contentUrl);
                break;
            case VID_ME :
            case STREAMABLE :
            case GIF :
                doLoadGif(contentUrl);
                break;
        }
    }

    public void doLoadXKCD(java.lang.String url) {
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        if (me.ccrama.redditslide.util.NetworkUtil.isConnected(getContext())) {
            final java.lang.String apiUrl = url + "info.0.json";
            me.ccrama.redditslide.util.LogUtil.v(apiUrl);
            final java.lang.String finalUrl = url;
            new android.os.AsyncTask<java.lang.Void, java.lang.Void, com.google.gson.JsonObject>() {
                @java.lang.Override
                protected com.google.gson.JsonObject doInBackground(java.lang.Void... params) {
                    return me.ccrama.redditslide.util.HttpUtil.getJsonObject(client, gson, apiUrl);
                }

                @java.lang.Override
                protected void onPostExecute(final com.google.gson.JsonObject result) {
                    if (((result != null) && (!result.isJsonNull())) && result.has("error")) {
                        me.ccrama.redditslide.util.LogUtil.v("Error loading content");
                    } else {
                        try {
                            if (((result != null) && (!result.isJsonNull())) && result.has("img")) {
                                doLoadImage(result.get("img").getAsString());
                                rootView.findViewById(me.ccrama.redditslide.R.id.submission_image).setOnLongClickListener(new android.view.View.OnLongClickListener() {
                                    @java.lang.Override
                                    public boolean onLongClick(android.view.View v) {
                                        try {
                                            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(getContext()).setTitle(result.get("safe_title").getAsString()).setMessage(result.get("alt").getAsString()).show();
                                        } catch (java.lang.Exception ignored) {
                                        }
                                        return true;
                                    }
                                });
                            } else {
                                android.content.Intent i = new android.content.Intent(getContext(), me.ccrama.redditslide.Activities.Website.class);
                                i.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_URL, finalUrl);
                                getContext().startActivity(i);
                            }
                        } catch (java.lang.Exception e2) {
                            e2.printStackTrace();
                            android.content.Intent i = new android.content.Intent(getContext(), me.ccrama.redditslide.Activities.Website.class);
                            i.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_URL, finalUrl);
                            getContext().startActivity(i);
                        }
                    }
                }
            }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private static void addClickFunctions(final android.view.View base, final com.sothree.slidinguppanel.SlidingUpPanelLayout slidingPanel, final android.view.View clickingArea, final me.ccrama.redditslide.ContentType.Type type, final android.app.Activity contextActivity, final me.ccrama.redditslide.Adapters.CommentUrlObject submission) {
        base.setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                if (slidingPanel.getPanelState() == com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.EXPANDED) {
                    slidingPanel.setPanelState(com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED);
                } else {
                    switch (type) {
                        case IMAGE :
                            if (me.ccrama.redditslide.SettingValues.image) {
                                android.content.Intent myIntent = new android.content.Intent(contextActivity, me.ccrama.redditslide.Activities.MediaView.class);
                                java.lang.String url;
                                url = submission.getUrl();
                                myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_DISPLAY_URL, submission.getUrl());
                                myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_URL, url);
                                myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.SUBREDDIT, submission.getSubredditName());
                                myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_SHARE_URL, submission.getUrl());
                                contextActivity.startActivity(myIntent);
                            } else {
                                me.ccrama.redditslide.util.LinkUtil.openExternally(submission.getUrl());
                            }
                            break;
                    }
                }
            }
        });
    }

    public void doLoadGif(final java.lang.String dat) {
        isGif = true;
        videoView = rootView.findViewById(me.ccrama.redditslide.R.id.gif);
        videoView.clearFocus();
        rootView.findViewById(me.ccrama.redditslide.R.id.gifarea).setVisibility(android.view.View.VISIBLE);
        rootView.findViewById(me.ccrama.redditslide.R.id.submission_image).setVisibility(android.view.View.GONE);
        final android.widget.ProgressBar loader = rootView.findViewById(me.ccrama.redditslide.R.id.gifprogress);
        rootView.findViewById(me.ccrama.redditslide.R.id.progress).setVisibility(android.view.View.GONE);
        gif = new me.ccrama.redditslide.util.GifUtils.AsyncLoadGif(getActivity(), ((me.ccrama.redditslide.Views.MediaVideoView) (rootView.findViewById(me.ccrama.redditslide.R.id.gif))), loader, rootView.findViewById(me.ccrama.redditslide.R.id.placeholder), false, false, true, sub);
        gif.execute(dat);
    }

    public void doLoadDeviantArt(java.lang.String url) {
        final java.lang.String apiUrl = "http://backend.deviantart.com/oembed?url=" + url;
        me.ccrama.redditslide.util.LogUtil.v(apiUrl);
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, com.google.gson.JsonObject>() {
            @java.lang.Override
            protected com.google.gson.JsonObject doInBackground(java.lang.Void... params) {
                return me.ccrama.redditslide.util.HttpUtil.getJsonObject(client, gson, apiUrl);
            }

            @java.lang.Override
            protected void onPostExecute(com.google.gson.JsonObject result) {
                me.ccrama.redditslide.util.LogUtil.v((("doLoad onPostExecute() called with: " + "result = [") + result) + "]");
                if (((result != null) && (!result.isJsonNull())) && (result.has("fullsize_url") || result.has("url"))) {
                    java.lang.String url;
                    if (result.has("fullsize_url")) {
                        url = result.get("fullsize_url").getAsString();
                    } else {
                        url = result.get("url").getAsString();
                    }
                    doLoadImage(url);
                } else {
                    android.content.Intent i = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.Website.class);
                    i.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_URL, contentUrl);
                    getActivity().startActivity(i);
                }
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void doLoadImgur(java.lang.String url) {
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        final java.lang.String finalUrl = url;
        java.lang.String hash = url.substring(url.lastIndexOf("/"), url.length());
        if (me.ccrama.redditslide.util.NetworkUtil.isConnected(getActivity())) {
            if (hash.startsWith("/"))
                hash = hash.substring(1, hash.length());

            final java.lang.String apiUrl = ("https://imgur-apiv3.p.mashape.com/3/image/" + hash) + ".json";
            me.ccrama.redditslide.util.LogUtil.v(apiUrl);
            new android.os.AsyncTask<java.lang.Void, java.lang.Void, com.google.gson.JsonObject>() {
                @java.lang.Override
                protected com.google.gson.JsonObject doInBackground(java.lang.Void... params) {
                    return me.ccrama.redditslide.util.HttpUtil.getImgurMashapeJsonObject(client, gson, apiUrl, mashapeKey);
                }

                @java.lang.Override
                protected void onPostExecute(com.google.gson.JsonObject result) {
                    if (((result != null) && (!result.isJsonNull())) && result.has("error")) {
                        me.ccrama.redditslide.util.LogUtil.v("Error loading content");
                        getActivity().finish();
                    } else {
                        try {
                            if (((result != null) && (!result.isJsonNull())) && result.has("image")) {
                                java.lang.String type = result.get("image").getAsJsonObject().get("image").getAsJsonObject().get("type").getAsString();
                                java.lang.String urls = result.get("image").getAsJsonObject().get("links").getAsJsonObject().get("original").getAsString();
                                if (type.contains("gif")) {
                                    doLoadGif(urls);
                                } else if (!imageShown) {
                                    // only load if there is no image
                                    doLoadImage(urls);
                                }
                            } else if ((result != null) && result.has("data")) {
                                java.lang.String type = result.get("data").getAsJsonObject().get("type").getAsString();
                                java.lang.String urls = result.get("data").getAsJsonObject().get("link").getAsString();
                                java.lang.String mp4 = "";
                                if (result.get("data").getAsJsonObject().has("mp4")) {
                                    mp4 = result.get("data").getAsJsonObject().get("mp4").getAsString();
                                }
                                if (type.contains("gif")) {
                                    doLoadGif((mp4 == null) || mp4.isEmpty() ? urls : mp4);
                                } else if (!imageShown) {
                                    // only load if there is no image
                                    doLoadImage(urls);
                                }
                            } else if (!imageShown)
                                doLoadImage(finalUrl);

                        } catch (java.lang.Exception e) {
                            me.ccrama.redditslide.util.LogUtil.e(e, ((("Error loading Imgur image finalUrl = [" + finalUrl) + "], apiUrl = [") + apiUrl) + "]");
                            // todo open it?
                        }
                    }
                }
            }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void doLoadImage(java.lang.String contentUrl) {
        if ((contentUrl != null) && contentUrl.contains("bildgur.de")) {
            contentUrl = contentUrl.replace("b.bildgur.de", "i.imgur.com");
        }
        if ((contentUrl != null) && me.ccrama.redditslide.ContentType.isImgurLink(contentUrl)) {
            contentUrl = contentUrl + ".png";
        }
        rootView.findViewById(me.ccrama.redditslide.R.id.gifprogress).setVisibility(android.view.View.GONE);
        if ((contentUrl != null) && contentUrl.contains("m.imgur.com")) {
            contentUrl = contentUrl.replace("m.imgur.com", "i.imgur.com");
        }
        if ((((contentUrl != null) && (!contentUrl.startsWith("https://i.redditmedia.com"))) && (!contentUrl.startsWith("https://i.reddituploads.com"))) && (!contentUrl.contains("imgur.com"))) {
            // we can assume redditmedia and imgur links are to direct images and not websites
            rootView.findViewById(me.ccrama.redditslide.R.id.progress).setVisibility(android.view.View.VISIBLE);
            ((android.widget.ProgressBar) (rootView.findViewById(me.ccrama.redditslide.R.id.progress))).setIndeterminate(true);
            final java.lang.String finalUrl2 = contentUrl;
            new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
                @java.lang.Override
                protected java.lang.Void doInBackground(java.lang.Void... params) {
                    try {
                        java.net.URL obj = new java.net.URL(finalUrl2);
                        java.net.URLConnection conn = obj.openConnection();
                        final java.lang.String type = conn.getHeaderField("Content-Type");
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new java.lang.Runnable() {
                                @java.lang.Override
                                public void run() {
                                    if ((((!imageShown) && (type != null)) && (!type.isEmpty())) && type.startsWith("image/")) {
                                        // is image
                                        if (type.contains("gif")) {
                                            doLoadGif(finalUrl2.replace(".jpg", ".gif").replace(".png", ".gif"));
                                        } else if (!imageShown) {
                                            displayImage(finalUrl2);
                                        }
                                        actuallyLoaded = finalUrl2;
                                    } else if (!imageShown) {
                                        android.content.Intent i = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.Website.class);
                                        i.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_URL, finalUrl2);
                                        getActivity().startActivity(i);
                                    }
                                }
                            });
                        }
                    } catch (java.io.IOException e) {
                        me.ccrama.redditslide.util.LogUtil.e(e, ("Error loading image finalUrl2 = [" + finalUrl2) + "]");
                    }
                    return null;
                }

                @java.lang.Override
                protected void onPostExecute(java.lang.Void aVoid) {
                    rootView.findViewById(me.ccrama.redditslide.R.id.progress).setVisibility(android.view.View.GONE);
                }
            }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            displayImage(contentUrl);
        }
        actuallyLoaded = contentUrl;
    }

    public void displayImage(final java.lang.String url) {
        if (!imageShown) {
            actuallyLoaded = url;
            final me.ccrama.redditslide.Views.SubsamplingScaleImageView i = rootView.findViewById(me.ccrama.redditslide.R.id.submission_image);
            i.setMinimumDpi(70);
            i.setMinimumTileDpi(240);
            final android.widget.ProgressBar bar = rootView.findViewById(me.ccrama.redditslide.R.id.progress);
            bar.setIndeterminate(false);
            bar.setProgress(0);
            final android.os.Handler handler = new android.os.Handler();
            final java.lang.Runnable progressBarDelayRunner = new java.lang.Runnable() {
                public void run() {
                    bar.setVisibility(android.view.View.VISIBLE);
                }
            };
            handler.postDelayed(progressBarDelayRunner, 500);
            android.widget.ImageView fakeImage = new android.widget.ImageView(getActivity());
            fakeImage.setLayoutParams(new android.widget.LinearLayout.LayoutParams(i.getWidth(), i.getHeight()));
            fakeImage.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
            java.io.File f = ((me.ccrama.redditslide.Reddit) (getActivity().getApplicationContext())).getImageLoader().getDiscCache().get(url);
            if ((f != null) && f.exists()) {
                imageShown = true;
                try {
                    i.setImage(me.ccrama.redditslide.Views.ImageSource.uri(f.getAbsolutePath()));
                } catch (java.lang.Exception e) {
                    // todo  i.setImage(ImageSource.bitmap(loadedImage));
                }
                rootView.findViewById(me.ccrama.redditslide.R.id.progress).setVisibility(android.view.View.GONE);
                handler.removeCallbacks(progressBarDelayRunner);
                previous = i.scale;
                final float base = i.scale;
                i.setOnZoomChangedListener(new me.ccrama.redditslide.Views.SubsamplingScaleImageView.OnZoomChangedListener() {
                    @java.lang.Override
                    public void onZoomLevelChanged(float zoom) {
                        if (((zoom > previous) && (!hidden)) && (zoom > base)) {
                            hidden = true;
                            final android.view.View base = rootView.findViewById(me.ccrama.redditslide.R.id.base);
                            android.animation.ValueAnimator va = android.animation.ValueAnimator.ofFloat(1.0F, 0.2F);
                            int mDuration = 250;// in millis

                            va.setDuration(mDuration);
                            va.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {
                                public void onAnimationUpdate(android.animation.ValueAnimator animation) {
                                    java.lang.Float value = ((java.lang.Float) (animation.getAnimatedValue()));
                                    base.setAlpha(value);
                                }
                            });
                            va.start();
                            // hide
                        } else if ((zoom <= previous) && hidden) {
                            hidden = false;
                            final android.view.View base = rootView.findViewById(me.ccrama.redditslide.R.id.base);
                            android.animation.ValueAnimator va = android.animation.ValueAnimator.ofFloat(0.2F, 1.0F);
                            int mDuration = 250;// in millis

                            va.setDuration(mDuration);
                            va.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {
                                public void onAnimationUpdate(android.animation.ValueAnimator animation) {
                                    java.lang.Float value = ((java.lang.Float) (animation.getAnimatedValue()));
                                    base.setAlpha(value);
                                }
                            });
                            va.start();
                            // unhide
                        }
                        previous = zoom;
                    }
                });
            } else {
                ((me.ccrama.redditslide.Reddit) (getActivity().getApplicationContext())).getImageLoader().displayImage(url, new com.nostra13.universalimageloader.core.imageaware.ImageViewAware(fakeImage), new com.nostra13.universalimageloader.core.DisplayImageOptions.Builder().resetViewBeforeLoading(true).cacheOnDisk(true).imageScaleType(com.nostra13.universalimageloader.core.assist.ImageScaleType.NONE).cacheInMemory(false).build(), new com.nostra13.universalimageloader.core.listener.ImageLoadingListener() {
                    private android.view.View mView;

                    @java.lang.Override
                    public void onLoadingStarted(java.lang.String imageUri, android.view.View view) {
                        imageShown = true;
                        mView = view;
                    }

                    @java.lang.Override
                    public void onLoadingFailed(java.lang.String imageUri, android.view.View view, com.nostra13.universalimageloader.core.assist.FailReason failReason) {
                        android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "LOADING FAILED");
                    }

                    @java.lang.Override
                    public void onLoadingComplete(java.lang.String imageUri, android.view.View view, android.graphics.Bitmap loadedImage) {
                        imageShown = true;
                        java.io.File f = null;
                        if (getActivity() != null) {
                            f = ((me.ccrama.redditslide.Reddit) (getActivity().getApplicationContext())).getImageLoader().getDiscCache().get(url);
                        }
                        if ((f != null) && f.exists()) {
                            i.setImage(me.ccrama.redditslide.Views.ImageSource.uri(f.getAbsolutePath()));
                        } else {
                            i.setImage(me.ccrama.redditslide.Views.ImageSource.bitmap(loadedImage));
                        }
                        rootView.findViewById(me.ccrama.redditslide.R.id.progress).setVisibility(android.view.View.GONE);
                        handler.removeCallbacks(progressBarDelayRunner);
                        previous = i.scale;
                        final float base = i.scale;
                        i.setOnZoomChangedListener(new me.ccrama.redditslide.Views.SubsamplingScaleImageView.OnZoomChangedListener() {
                            @java.lang.Override
                            public void onZoomLevelChanged(float zoom) {
                                if (((zoom > previous) && (!hidden)) && (zoom > base)) {
                                    hidden = true;
                                    final android.view.View base = rootView.findViewById(me.ccrama.redditslide.R.id.base);
                                    android.animation.ValueAnimator va = android.animation.ValueAnimator.ofFloat(1.0F, 0.2F);
                                    int mDuration = 250;// in millis

                                    va.setDuration(mDuration);
                                    va.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {
                                        public void onAnimationUpdate(android.animation.ValueAnimator animation) {
                                            java.lang.Float value = ((java.lang.Float) (animation.getAnimatedValue()));
                                            base.setAlpha(value);
                                        }
                                    });
                                    va.start();
                                    // hide
                                } else if ((zoom <= previous) && hidden) {
                                    hidden = false;
                                    final android.view.View base = rootView.findViewById(me.ccrama.redditslide.R.id.base);
                                    android.animation.ValueAnimator va = android.animation.ValueAnimator.ofFloat(0.2F, 1.0F);
                                    int mDuration = 250;// in millis

                                    va.setDuration(mDuration);
                                    va.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {
                                        public void onAnimationUpdate(android.animation.ValueAnimator animation) {
                                            java.lang.Float value = ((java.lang.Float) (animation.getAnimatedValue()));
                                            base.setAlpha(value);
                                        }
                                    });
                                    va.start();
                                    // unhide
                                }
                                previous = zoom;
                            }
                        });
                    }

                    @java.lang.Override
                    public void onLoadingCancelled(java.lang.String imageUri, android.view.View view) {
                        android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "LOADING CANCELLED");
                    }
                }, new com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener() {
                    @java.lang.Override
                    public void onProgressUpdate(java.lang.String imageUri, android.view.View view, int current, int total) {
                        ((android.widget.ProgressBar) (rootView.findViewById(me.ccrama.redditslide.R.id.progress))).setProgress(java.lang.Math.round((100.0F * current) / total));
                    }
                });
            }
        }
    }
}