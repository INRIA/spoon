package me.ccrama.redditslide.Views;
import me.ccrama.redditslide.ContentType;
import java.util.HashMap;
import me.ccrama.redditslide.Adapters.ImageGridAdapter;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import me.ccrama.redditslide.ImgurAlbum.Image;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Tumblr.Photo;
import com.google.gson.Gson;
import me.ccrama.redditslide.util.NetworkUtil;
import me.ccrama.redditslide.util.LogUtil;
import java.net.URL;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.SecretConstants;
import me.ccrama.redditslide.Reddit;
import java.util.List;
import me.ccrama.redditslide.ForceTouch.PeekViewActivity;
import me.ccrama.redditslide.util.HttpUtil;
import java.io.IOException;
import me.ccrama.redditslide.Tumblr.TumblrUtils;
import java.net.URLConnection;
import me.ccrama.redditslide.ImgurAlbum.AlbumUtils;
import me.ccrama.redditslide.util.GifUtils;
import java.io.File;
import java.util.Map;
import me.ccrama.redditslide.util.AdBlocker;
/**
 * Created by ccrama on 3/5/2015.
 */
public class PeekMediaView extends android.widget.RelativeLayout {
    me.ccrama.redditslide.ContentType.Type contentType;

    private me.ccrama.redditslide.util.GifUtils.AsyncLoadGif gif;

    private me.ccrama.redditslide.Views.MediaVideoView videoView;

    public android.webkit.WebView website;

    private android.widget.ProgressBar progress;

    private me.ccrama.redditslide.Views.SubsamplingScaleImageView image;

    public PeekMediaView(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public PeekMediaView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PeekMediaView(android.content.Context context) {
        super(context);
        init();
    }

    boolean web;

    float origY = 0;

    public void doClose() {
        website.setVisibility(android.view.View.GONE);
        website.loadUrl("about:blank");
        videoView.stopPlayback();
        if (gif != null)
            gif.cancel(true);

    }

    public void doScroll(android.view.MotionEvent event) {
        if (origY == 0) {
            origY = event.getY();
        }
        if ((web && website.canScrollVertically((origY - event.getY()) > 0 ? 0 : 1)) && (java.lang.Math.abs(origY - event.getY()) > (website.getHeight() / 4))) {
            website.scrollBy(0, ((int) (-(origY - event.getY()))) / 5);
        }
    }

    public void setUrl(java.lang.String url) {
        contentType = me.ccrama.redditslide.ContentType.getContentType(url);
        switch (contentType) {
            case ALBUM :
                doLoadAlbum(url);
                progress.setIndeterminate(true);
                break;
            case TUMBLR :
                doLoadTumblr(url);
                progress.setIndeterminate(true);
                break;
            case EMBEDDED :
            case EXTERNAL :
            case LINK :
            case VIDEO :
            case SELF :
            case SPOILER :
            case NONE :
                doLoadLink(url);
                progress.setIndeterminate(false);
                break;
            case REDDIT :
                progress.setIndeterminate(true);
                doLoadReddit(url);
                break;
            case DEVIANTART :
                doLoadDeviantArt(url);
                progress.setIndeterminate(false);
                break;
            case IMAGE :
                doLoadImage(url);
                progress.setIndeterminate(false);
                break;
            case XKCD :
                doLoadXKCD(url);
                progress.setIndeterminate(false);
                break;
            case IMGUR :
                doLoadImgur(url);
                progress.setIndeterminate(false);
                break;
            case GIF :
            case STREAMABLE :
            case VID_ME :
                doLoadGif(url);
                progress.setIndeterminate(false);
                break;
        }
    }

    private void doLoadAlbum(final java.lang.String url) {
        new me.ccrama.redditslide.ImgurAlbum.AlbumUtils.GetAlbumWithCallback(url, ((me.ccrama.redditslide.ForceTouch.PeekViewActivity) (getContext()))) {
            @java.lang.Override
            public void onError() {
                ((me.ccrama.redditslide.ForceTouch.PeekViewActivity) (getContext())).runOnUiThread(new java.lang.Runnable() {
                    @java.lang.Override
                    public void run() {
                        doLoadLink(url);
                    }
                });
            }

            @java.lang.Override
            public void doWithData(final java.util.List<me.ccrama.redditslide.ImgurAlbum.Image> jsonElements) {
                super.doWithData(jsonElements);
                progress.setVisibility(android.view.View.GONE);
                images = new java.util.ArrayList<>(jsonElements);
                displayImage(images.get(0).getImageUrl());
                if (images.size() > 1) {
                    android.widget.GridView grid = ((android.widget.GridView) (findViewById(me.ccrama.redditslide.R.id.grid_area)));
                    grid.setNumColumns(5);
                    grid.setVisibility(android.view.View.VISIBLE);
                    grid.setAdapter(new me.ccrama.redditslide.Adapters.ImageGridAdapter(getContext(), images));
                }
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void doLoadTumblr(final java.lang.String url) {
        new me.ccrama.redditslide.Tumblr.TumblrUtils.GetTumblrPostWithCallback(url, ((me.ccrama.redditslide.ForceTouch.PeekViewActivity) (getContext()))) {
            @java.lang.Override
            public void onError() {
                ((me.ccrama.redditslide.ForceTouch.PeekViewActivity) (getContext())).runOnUiThread(new java.lang.Runnable() {
                    @java.lang.Override
                    public void run() {
                        doLoadLink(url);
                    }
                });
            }

            @java.lang.Override
            public void doWithData(final java.util.List<me.ccrama.redditslide.Tumblr.Photo> jsonElements) {
                super.doWithData(jsonElements);
                progress.setVisibility(android.view.View.GONE);
                tumblrImages = new java.util.ArrayList<>(jsonElements);
                displayImage(tumblrImages.get(0).getOriginalSize().getUrl());
                if (tumblrImages.size() > 1) {
                    android.widget.GridView grid = ((android.widget.GridView) (findViewById(me.ccrama.redditslide.R.id.grid_area)));
                    grid.setNumColumns(5);
                    grid.setVisibility(android.view.View.VISIBLE);
                    grid.setAdapter(new me.ccrama.redditslide.Adapters.ImageGridAdapter(getContext(), tumblrImages, true));
                }
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    java.util.List<me.ccrama.redditslide.ImgurAlbum.Image> images;

    java.util.List<me.ccrama.redditslide.Tumblr.Photo> tumblrImages;

    android.webkit.WebChromeClient client;

    android.webkit.WebViewClient webClient;

    public void setValue(int newProgress) {
        progress.setProgress(newProgress);
        if (newProgress == 100) {
            progress.setVisibility(android.view.View.GONE);
        } else if (progress.getVisibility() == android.view.View.GONE) {
            progress.setVisibility(android.view.View.VISIBLE);
        }
    }

    private class MyWebViewClient extends android.webkit.WebChromeClient {
        @java.lang.Override
        public void onProgressChanged(android.webkit.WebView view, int newProgress) {
            setValue(newProgress);
            super.onProgressChanged(view, newProgress);
        }
    }

    public void doLoadXKCD(final java.lang.String url) {
        if (me.ccrama.redditslide.util.NetworkUtil.isConnected(getContext())) {
            final java.lang.String apiUrl = (url.endsWith("/") ? url : url + "/") + "info.0.json";
            new android.os.AsyncTask<java.lang.Void, java.lang.Void, com.google.gson.JsonObject>() {
                @java.lang.Override
                protected com.google.gson.JsonObject doInBackground(java.lang.Void... params) {
                    return me.ccrama.redditslide.util.HttpUtil.getJsonObject(me.ccrama.redditslide.Reddit.client, new com.google.gson.Gson(), apiUrl);
                }

                @java.lang.Override
                protected void onPostExecute(final com.google.gson.JsonObject result) {
                    if (((result != null) && (!result.isJsonNull())) && result.has("error")) {
                        doLoadLink(url);
                    } else {
                        try {
                            if (((result != null) && (!result.isJsonNull())) && result.has("img")) {
                                doLoadImage(result.get("img").getAsString());
                            } else {
                                doLoadLink(url);
                            }
                        } catch (java.lang.Exception e2) {
                            doLoadLink(url);
                        }
                    }
                }
            }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void doLoadLink(java.lang.String url) {
        client = new me.ccrama.redditslide.Views.PeekMediaView.MyWebViewClient();
        web = true;
        webClient = new android.webkit.WebViewClient() {
            @java.lang.Override
            public void onPageFinished(android.webkit.WebView view, java.lang.String url) {
                website.loadUrl("javascript:(function() { document.getElementsByTagName('video')[0].play(); })()");
            }

            private java.util.Map<java.lang.String, java.lang.Boolean> loadedUrls = new java.util.HashMap<>();

            @java.lang.Override
            public android.webkit.WebResourceResponse shouldInterceptRequest(android.webkit.WebView view, java.lang.String url) {
                boolean ad;
                if (!loadedUrls.containsKey(url)) {
                    ad = me.ccrama.redditslide.util.AdBlocker.isAd(url, getContext());
                    loadedUrls.put(url, ad);
                } else {
                    ad = loadedUrls.get(url);
                }
                return ad && me.ccrama.redditslide.SettingValues.isPro ? me.ccrama.redditslide.util.AdBlocker.createEmptyResource() : super.shouldInterceptRequest(view, url);
            }
        };
        website.setVisibility(android.view.View.VISIBLE);
        website.setWebChromeClient(client);
        website.setWebViewClient(webClient);
        website.getSettings().setBuiltInZoomControls(true);
        website.getSettings().setDisplayZoomControls(false);
        website.getSettings().setJavaScriptEnabled(true);
        website.getSettings().setLoadWithOverviewMode(true);
        website.getSettings().setUseWideViewPort(true);
        website.setDownloadListener(new android.webkit.DownloadListener() {
            public void onDownloadStart(java.lang.String url, java.lang.String userAgent, java.lang.String contentDisposition, java.lang.String mimetype, long contentLength) {
                // Downloads using download manager on default browser
                android.content.Intent i = new android.content.Intent(android.content.Intent.ACTION_VIEW);
                i.setData(android.net.Uri.parse(url));
                getContext().startActivity(i);
            }
        });
        website.loadUrl(url);
    }

    private void doLoadReddit(java.lang.String url) {
        me.ccrama.redditslide.Views.RedditItemView v = ((me.ccrama.redditslide.Views.RedditItemView) (findViewById(me.ccrama.redditslide.R.id.reddit_item)));
        v.loadUrl(this, url, progress);
    }

    public void doLoadDeviantArt(java.lang.String url) {
        final java.lang.String apiUrl = "http://backend.deviantart.com/oembed?url=" + url;
        me.ccrama.redditslide.util.LogUtil.v(apiUrl);
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, com.google.gson.JsonObject>() {
            @java.lang.Override
            protected com.google.gson.JsonObject doInBackground(java.lang.Void... params) {
                return me.ccrama.redditslide.util.HttpUtil.getJsonObject(me.ccrama.redditslide.Reddit.client, new com.google.gson.Gson(), apiUrl);
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
                    // todo error out
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
        if (me.ccrama.redditslide.util.NetworkUtil.isConnected(getContext())) {
            if (hash.startsWith("/"))
                hash = hash.substring(1, hash.length());

            final java.lang.String apiUrl = ("https://imgur-apiv3.p.mashape.com/3/image/" + hash) + ".json";
            me.ccrama.redditslide.util.LogUtil.v(apiUrl);
            new android.os.AsyncTask<java.lang.Void, java.lang.Void, com.google.gson.JsonObject>() {
                @java.lang.Override
                protected com.google.gson.JsonObject doInBackground(java.lang.Void... params) {
                    return me.ccrama.redditslide.util.HttpUtil.getImgurMashapeJsonObject(me.ccrama.redditslide.Reddit.client, new com.google.gson.Gson(), apiUrl, me.ccrama.redditslide.SecretConstants.getImgurApiKey(getContext()));
                }

                @java.lang.Override
                protected void onPostExecute(com.google.gson.JsonObject result) {
                    if (((result != null) && (!result.isJsonNull())) && result.has("error")) {
                        // /todo error out
                    } else {
                        try {
                            if (((result != null) && (!result.isJsonNull())) && result.has("image")) {
                                java.lang.String type = result.get("image").getAsJsonObject().get("image").getAsJsonObject().get("type").getAsString();
                                java.lang.String urls = result.get("image").getAsJsonObject().get("links").getAsJsonObject().get("original").getAsString();
                                if (type.contains("gif")) {
                                    doLoadGif(urls);
                                } else if (!imageShown) {
                                    // only load if there is no image
                                    displayImage(urls);
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
                                    displayImage(urls);
                                }
                            } else if (!imageShown)
                                doLoadImage(finalUrl);

                        } catch (java.lang.Exception e2) {
                            // todo error out
                        }
                    }
                }
            }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    boolean imageShown;

    public void doLoadImage(java.lang.String contentUrl) {
        if ((contentUrl != null) && contentUrl.contains("bildgur.de")) {
            contentUrl = contentUrl.replace("b.bildgur.de", "i.imgur.com");
        }
        if ((contentUrl != null) && me.ccrama.redditslide.ContentType.isImgurLink(contentUrl)) {
            contentUrl = contentUrl + ".png";
        }
        if ((contentUrl != null) && contentUrl.contains("m.imgur.com")) {
            contentUrl = contentUrl.replace("m.imgur.com", "i.imgur.com");
        }
        if (contentUrl == null) {
            // todo error out
        }
        if ((((contentUrl != null) && (!contentUrl.startsWith("https://i.redditmedia.com"))) && (!contentUrl.startsWith("https://i.reddituploads.com"))) && (!contentUrl.contains("imgur.com"))) {
            // we can assume redditmedia and imgur links are to direct images and not websites
            progress.setVisibility(android.view.View.VISIBLE);
            progress.setIndeterminate(true);
            final java.lang.String finalUrl2 = contentUrl;
            new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
                @java.lang.Override
                protected java.lang.Void doInBackground(java.lang.Void... params) {
                    try {
                        java.net.URL obj = new java.net.URL(finalUrl2);
                        java.net.URLConnection conn = obj.openConnection();
                        final java.lang.String type = conn.getHeaderField("Content-Type");
                        ((me.ccrama.redditslide.ForceTouch.PeekViewActivity) (getContext())).runOnUiThread(new java.lang.Runnable() {
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
                                    // todo error out
                                }
                            }
                        });
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @java.lang.Override
                protected void onPostExecute(java.lang.Void aVoid) {
                    progress.setVisibility(android.view.View.GONE);
                }
            }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            displayImage(contentUrl);
        }
    }

    java.lang.String actuallyLoaded;

    public void doLoadGif(final java.lang.String dat) {
        videoView = ((me.ccrama.redditslide.Views.MediaVideoView) (findViewById(me.ccrama.redditslide.R.id.gif)));
        videoView.clearFocus();
        findViewById(me.ccrama.redditslide.R.id.gifarea).setVisibility(android.view.View.VISIBLE);
        findViewById(me.ccrama.redditslide.R.id.submission_image).setVisibility(android.view.View.GONE);
        progress.setVisibility(android.view.View.VISIBLE);
        gif = new me.ccrama.redditslide.util.GifUtils.AsyncLoadGif(((me.ccrama.redditslide.ForceTouch.PeekViewActivity) (getContext())), ((me.ccrama.redditslide.Views.MediaVideoView) (findViewById(me.ccrama.redditslide.R.id.gif))), progress, null, false, true, true, "") {
            @java.lang.Override
            public void onError() {
                doLoadLink(dat);
            }
        };
        gif.execute(dat);
    }

    public void displayImage(final java.lang.String urlB) {
        me.ccrama.redditslide.util.LogUtil.v("Displaying " + urlB);
        final java.lang.String url = org.apache.commons.text.StringEscapeUtils.unescapeHtml4(urlB);
        if (!imageShown) {
            actuallyLoaded = url;
            final me.ccrama.redditslide.Views.SubsamplingScaleImageView i = ((me.ccrama.redditslide.Views.SubsamplingScaleImageView) (findViewById(me.ccrama.redditslide.R.id.submission_image)));
            i.setMinimumDpi(70);
            i.setMinimumTileDpi(240);
            progress.setIndeterminate(false);
            progress.setProgress(0);
            final android.os.Handler handler = new android.os.Handler();
            final java.lang.Runnable progressBarDelayRunner = new java.lang.Runnable() {
                public void run() {
                    progress.setVisibility(android.view.View.VISIBLE);
                }
            };
            handler.postDelayed(progressBarDelayRunner, 500);
            android.widget.ImageView fakeImage = new android.widget.ImageView(getContext());
            fakeImage.setLayoutParams(new android.widget.LinearLayout.LayoutParams(i.getWidth(), i.getHeight()));
            fakeImage.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
            java.io.File f = ((me.ccrama.redditslide.Reddit) (getContext().getApplicationContext())).getImageLoader().getDiscCache().get(url);
            if ((f != null) && f.exists()) {
                imageShown = true;
                i.setOnImageEventListener(new me.ccrama.redditslide.Views.SubsamplingScaleImageView.OnImageEventListener() {
                    @java.lang.Override
                    public void onReady() {
                    }

                    @java.lang.Override
                    public void onImageLoaded() {
                    }

                    @java.lang.Override
                    public void onPreviewLoadError(java.lang.Exception e) {
                    }

                    @java.lang.Override
                    public void onImageLoadError(java.lang.Exception e) {
                        imageShown = false;
                        me.ccrama.redditslide.util.LogUtil.v("No image displayed");
                    }

                    @java.lang.Override
                    public void onTileLoadError(java.lang.Exception e) {
                    }
                });
                try {
                    i.setImage(me.ccrama.redditslide.Views.ImageSource.uri(f.getAbsolutePath()));
                    i.setZoomEnabled(false);
                } catch (java.lang.Exception e) {
                    imageShown = false;
                    // todo  i.setImage(ImageSource.bitmap(loadedImage));
                }
                progress.setVisibility(android.view.View.GONE);
                handler.removeCallbacks(progressBarDelayRunner);
            } else {
                ((me.ccrama.redditslide.Reddit) (getContext().getApplicationContext())).getImageLoader().displayImage(url, new com.nostra13.universalimageloader.core.imageaware.ImageViewAware(fakeImage), new com.nostra13.universalimageloader.core.DisplayImageOptions.Builder().resetViewBeforeLoading(true).cacheOnDisk(true).imageScaleType(com.nostra13.universalimageloader.core.assist.ImageScaleType.NONE).cacheInMemory(false).build(), new com.nostra13.universalimageloader.core.listener.ImageLoadingListener() {
                    private android.view.View mView;

                    @java.lang.Override
                    public void onLoadingStarted(java.lang.String imageUri, android.view.View view) {
                        imageShown = true;
                        mView = view;
                    }

                    @java.lang.Override
                    public void onLoadingFailed(java.lang.String imageUri, android.view.View view, com.nostra13.universalimageloader.core.assist.FailReason failReason) {
                        android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "LOADING FAILED");
                        imageShown = false;
                    }

                    @java.lang.Override
                    public void onLoadingComplete(java.lang.String imageUri, android.view.View view, android.graphics.Bitmap loadedImage) {
                        imageShown = true;
                        java.io.File f = ((me.ccrama.redditslide.Reddit) (getContext().getApplicationContext())).getImageLoader().getDiscCache().get(url);
                        if ((f != null) && f.exists()) {
                            i.setImage(me.ccrama.redditslide.Views.ImageSource.uri(f.getAbsolutePath()));
                        } else {
                            i.setImage(me.ccrama.redditslide.Views.ImageSource.bitmap(loadedImage));
                        }
                        progress.setVisibility(android.view.View.GONE);
                        handler.removeCallbacks(progressBarDelayRunner);
                    }

                    @java.lang.Override
                    public void onLoadingCancelled(java.lang.String imageUri, android.view.View view) {
                        android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "LOADING CANCELLED");
                    }
                }, new com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener() {
                    @java.lang.Override
                    public void onProgressUpdate(java.lang.String imageUri, android.view.View view, int current, int total) {
                        progress.setProgress(java.lang.Math.round((100.0F * current) / total));
                    }
                });
            }
        }
    }

    private void init() {
        android.view.View.inflate(getContext(), me.ccrama.redditslide.R.layout.peek_media_view, this);
        this.image = ((me.ccrama.redditslide.Views.SubsamplingScaleImageView) (findViewById(me.ccrama.redditslide.R.id.submission_image)));
        this.videoView = ((me.ccrama.redditslide.Views.MediaVideoView) (findViewById(me.ccrama.redditslide.R.id.gif)));
        this.website = ((android.webkit.WebView) (findViewById(me.ccrama.redditslide.R.id.website)));
        this.progress = ((android.widget.ProgressBar) (findViewById(me.ccrama.redditslide.R.id.progress)));
    }
}