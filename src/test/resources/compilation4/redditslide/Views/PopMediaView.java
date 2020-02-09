package me.ccrama.redditslide.Views;
import me.ccrama.redditslide.ContentType;
import com.google.gson.JsonObject;
import me.ccrama.redditslide.util.HttpUtil;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URI;
import java.net.URLConnection;
import com.google.gson.Gson;
import me.ccrama.redditslide.util.NetworkUtil;
import me.ccrama.redditslide.util.LogUtil;
import java.net.URL;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.SecretConstants;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.util.GifUtils;
import java.io.File;
/**
 * Created by ccrama on 3/5/2015.
 */
public class PopMediaView {
    public static boolean shouldTruncate(java.lang.String url) {
        try {
            final java.net.URI uri = new java.net.URI(url);
            final java.lang.String path = uri.getPath();
            return ((!me.ccrama.redditslide.ContentType.isGif(uri)) && (!me.ccrama.redditslide.ContentType.isImage(uri))) && path.contains(".");
        } catch (java.net.URISyntaxException e) {
            return false;
        }
    }

    okhttp3.OkHttpClient client;

    com.google.gson.Gson gson;

    java.lang.String mashapeKey;

    public void doPop(android.view.View v, java.lang.String contentUrl, android.content.Context c) {
        client = me.ccrama.redditslide.Reddit.client;
        gson = new com.google.gson.Gson();
        mashapeKey = me.ccrama.redditslide.SecretConstants.getImgurApiKey(c);
        if (contentUrl.contains("reddituploads.com")) {
            contentUrl = android.text.Html.fromHtml(contentUrl).toString();
        }
        if ((contentUrl != null) && me.ccrama.redditslide.Views.PopMediaView.shouldTruncate(contentUrl)) {
            contentUrl = contentUrl.substring(0, contentUrl.lastIndexOf("."));
        }
        doLoad(contentUrl, v);
    }

    public void doLoad(final java.lang.String contentUrl, android.view.View v) {
        switch (me.ccrama.redditslide.ContentType.getContentType(contentUrl)) {
            case DEVIANTART :
                doLoadDeviantArt(contentUrl, v);
                break;
            case IMAGE :
                doLoadImage(contentUrl, v);
                break;
            case IMGUR :
                doLoadImgur(contentUrl, v);
                break;
            case VID_ME :
            case STREAMABLE :
            case GIF :
                doLoadGif(contentUrl, v);
                break;
        }
    }

    public void doLoadGif(final java.lang.String dat, android.view.View v) {
        v.findViewById(me.ccrama.redditslide.R.id.gifarea).setVisibility(android.view.View.VISIBLE);
        me.ccrama.redditslide.Views.MediaVideoView videoView = ((me.ccrama.redditslide.Views.MediaVideoView) (v.findViewById(me.ccrama.redditslide.R.id.gif)));
        videoView.clearFocus();
        v.findViewById(me.ccrama.redditslide.R.id.submission_image).setVisibility(android.view.View.GONE);
        final android.widget.ProgressBar loader = ((android.widget.ProgressBar) (v.findViewById(me.ccrama.redditslide.R.id.gifprogress)));
        v.findViewById(me.ccrama.redditslide.R.id.progress).setVisibility(android.view.View.GONE);
        me.ccrama.redditslide.util.GifUtils.AsyncLoadGif gif = new me.ccrama.redditslide.util.GifUtils.AsyncLoadGif(((android.app.Activity) (v.getContext())), ((me.ccrama.redditslide.Views.MediaVideoView) (v.findViewById(me.ccrama.redditslide.R.id.gif))), loader, null, null, false, true, true, "");
        gif.execute(dat);
    }

    public void doLoadImgur(java.lang.String url, final android.view.View v) {
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        final java.lang.String finalUrl = url;
        java.lang.String hash = url.substring(url.lastIndexOf("/"), url.length());
        if (me.ccrama.redditslide.util.NetworkUtil.isConnected(v.getContext())) {
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
                    } else {
                        try {
                            if (((result != null) && (!result.isJsonNull())) && result.has("image")) {
                                java.lang.String type = result.get("image").getAsJsonObject().get("image").getAsJsonObject().get("type").getAsString();
                                java.lang.String urls = result.get("image").getAsJsonObject().get("links").getAsJsonObject().get("original").getAsString();
                                if (type.contains("gif")) {
                                    doLoadGif(urls, v);
                                } else {
                                    // only load if there is no image
                                    doLoadImage(urls, v);
                                }
                            } else if ((result != null) && result.has("data")) {
                                java.lang.String type = result.get("data").getAsJsonObject().get("type").getAsString();
                                java.lang.String urls = result.get("data").getAsJsonObject().get("link").getAsString();
                                java.lang.String mp4 = "";
                                if (result.get("data").getAsJsonObject().has("mp4")) {
                                    mp4 = result.get("data").getAsJsonObject().get("mp4").getAsString();
                                }
                                if (type.contains("gif")) {
                                    doLoadGif((mp4 == null) || mp4.isEmpty() ? urls : mp4, v);
                                } else {
                                    // only load if there is no image
                                    doLoadImage(urls, v);
                                }
                            } else {
                                doLoadImage(finalUrl, v);
                            }
                        } catch (java.lang.Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void doLoadDeviantArt(java.lang.String url, final android.view.View v) {
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
                    doLoadImage(url, v);
                }
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void doLoadImage(java.lang.String contentUrl, final android.view.View v) {
        if ((contentUrl != null) && contentUrl.contains("bildgur.de"))
            contentUrl = contentUrl.replace("b.bildgur.de", "i.imgur.com");

        if ((contentUrl != null) && me.ccrama.redditslide.ContentType.isImgurLink(contentUrl)) {
            contentUrl = contentUrl + ".png";
        }
        v.findViewById(me.ccrama.redditslide.R.id.gifprogress).setVisibility(android.view.View.GONE);
        if ((contentUrl != null) && contentUrl.contains("m.imgur.com"))
            contentUrl = contentUrl.replace("m.imgur.com", "i.imgur.com");

        if (contentUrl == null) {
            // todo maybe something better
        }
        if ((((contentUrl != null) && (!contentUrl.startsWith("https://i.redditmedia.com"))) && (!contentUrl.startsWith("https://i.reddituploads.com"))) && (!contentUrl.contains("imgur.com"))) {
            // we can assume redditmedia and imgur links are to direct images and not websites
            v.findViewById(me.ccrama.redditslide.R.id.progress).setVisibility(android.view.View.VISIBLE);
            ((android.widget.ProgressBar) (v.findViewById(me.ccrama.redditslide.R.id.progress))).setIndeterminate(true);
            final java.lang.String finalUrl2 = contentUrl;
            new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
                java.lang.String type;

                @java.lang.Override
                protected java.lang.Void doInBackground(java.lang.Void... params) {
                    try {
                        java.net.URL obj = new java.net.URL(finalUrl2);
                        java.net.URLConnection conn = obj.openConnection();
                        type = conn.getHeaderField("Content-Type");
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @java.lang.Override
                protected void onPostExecute(java.lang.Void aVoid) {
                    if (((type != null) && (!type.isEmpty())) && type.startsWith("image/")) {
                        // is image
                        if (type.contains("gif")) {
                            doLoadGif(finalUrl2.replace(".jpg", ".gif").replace(".png", ".gif"), v);
                        } else {
                            displayImage(finalUrl2, v);
                        }
                    }
                    v.findViewById(me.ccrama.redditslide.R.id.progress).setVisibility(android.view.View.GONE);
                }
            }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            displayImage(contentUrl, v);
        }
    }

    public void displayImage(final java.lang.String url, final android.view.View v) {
        final me.ccrama.redditslide.Views.SubsamplingScaleImageView i = ((me.ccrama.redditslide.Views.SubsamplingScaleImageView) (v.findViewById(me.ccrama.redditslide.R.id.submission_image)));
        i.setMinimumDpi(70);
        i.setMinimumTileDpi(240);
        final android.widget.ProgressBar bar = ((android.widget.ProgressBar) (v.findViewById(me.ccrama.redditslide.R.id.progress)));
        bar.setIndeterminate(false);
        bar.setProgress(0);
        final android.os.Handler handler = new android.os.Handler();
        final java.lang.Runnable progressBarDelayRunner = new java.lang.Runnable() {
            public void run() {
                bar.setVisibility(android.view.View.VISIBLE);
            }
        };
        handler.postDelayed(progressBarDelayRunner, 500);
        android.widget.ImageView fakeImage = new android.widget.ImageView(v.getContext());
        fakeImage.setLayoutParams(new android.widget.LinearLayout.LayoutParams(i.getWidth(), i.getHeight()));
        fakeImage.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
        java.io.File f = ((me.ccrama.redditslide.Reddit) (v.getContext().getApplicationContext())).getImageLoader().getDiscCache().get(url);
        if ((f != null) && f.exists()) {
            try {
                i.setImage(me.ccrama.redditslide.Views.ImageSource.uri(f.getAbsolutePath()));
            } catch (java.lang.Exception e) {
                // todo  i.setImage(ImageSource.bitmap(loadedImage));
            }
            v.findViewById(me.ccrama.redditslide.R.id.progress).setVisibility(android.view.View.GONE);
            handler.removeCallbacks(progressBarDelayRunner);
        } else {
            ((me.ccrama.redditslide.Reddit) (v.getContext().getApplicationContext())).getImageLoader().displayImage(url, new com.nostra13.universalimageloader.core.imageaware.ImageViewAware(fakeImage), new com.nostra13.universalimageloader.core.DisplayImageOptions.Builder().resetViewBeforeLoading(true).cacheOnDisk(true).imageScaleType(com.nostra13.universalimageloader.core.assist.ImageScaleType.NONE).cacheInMemory(false).build(), new com.nostra13.universalimageloader.core.listener.ImageLoadingListener() {
                private android.view.View mView;

                @java.lang.Override
                public void onLoadingStarted(java.lang.String imageUri, android.view.View view) {
                    mView = view;
                }

                @java.lang.Override
                public void onLoadingFailed(java.lang.String imageUri, android.view.View view, com.nostra13.universalimageloader.core.assist.FailReason failReason) {
                    android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "LOADING FAILED");
                }

                @java.lang.Override
                public void onLoadingComplete(java.lang.String imageUri, android.view.View view, android.graphics.Bitmap loadedImage) {
                    java.io.File f = ((me.ccrama.redditslide.Reddit) (v.getContext().getApplicationContext())).getImageLoader().getDiscCache().get(url);
                    if ((f != null) && f.exists()) {
                        i.setImage(me.ccrama.redditslide.Views.ImageSource.uri(f.getAbsolutePath()));
                    } else {
                        i.setImage(me.ccrama.redditslide.Views.ImageSource.bitmap(loadedImage));
                    }
                    v.findViewById(me.ccrama.redditslide.R.id.progress).setVisibility(android.view.View.GONE);
                    handler.removeCallbacks(progressBarDelayRunner);
                }

                @java.lang.Override
                public void onLoadingCancelled(java.lang.String imageUri, android.view.View view) {
                    android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "LOADING CANCELLED");
                }
            }, new com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener() {
                @java.lang.Override
                public void onProgressUpdate(java.lang.String imageUri, android.view.View view, int current, int total) {
                    ((android.widget.ProgressBar) (v.findViewById(me.ccrama.redditslide.R.id.progress))).setProgress(java.lang.Math.round((100.0F * current) / total));
                }
            });
        }
    }
}