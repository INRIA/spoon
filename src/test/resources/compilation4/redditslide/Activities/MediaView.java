package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.ContentType;
import me.ccrama.redditslide.util.ShareUtil;
import java.net.MalformedURLException;
import com.google.gson.JsonObject;
import java.net.URISyntaxException;
import me.ccrama.redditslide.Notifications.ImageDownloadNotificationService;
import me.ccrama.redditslide.SettingValues;
import java.net.URI;
import com.google.gson.Gson;
import me.ccrama.redditslide.Views.MediaVideoView;
import me.ccrama.redditslide.util.NetworkUtil;
import me.ccrama.redditslide.util.LogUtil;
import java.net.URL;
import java.io.BufferedInputStream;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.SecretConstants;
import me.ccrama.redditslide.Reddit;
import java.util.UUID;
import java.io.InputStream;
import me.ccrama.redditslide.ColorPreferences;
import me.ccrama.redditslide.util.HttpUtil;
import java.io.IOException;
import me.ccrama.redditslide.Views.SubsamplingScaleImageView;
import me.ccrama.redditslide.SubmissionViews.OpenVRedditTask;
import java.net.URLConnection;
import me.ccrama.redditslide.Fragments.FolderChooserDialogCreate;
import java.io.FileOutputStream;
import me.ccrama.redditslide.util.LinkUtil;
import static me.ccrama.redditslide.Activities.AlbumPager.readableFileSize;
import me.ccrama.redditslide.Views.ImageSource;
import me.ccrama.redditslide.util.GifUtils;
import me.ccrama.redditslide.Fragments.SubmissionsView;
import java.io.File;
import me.ccrama.redditslide.util.FileUtil;
/**
 * Created by ccrama on 3/5/2015.
 */
public class MediaView extends me.ccrama.redditslide.Activities.FullScreenActivity implements me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.FolderCallback {
    public static final java.lang.String EXTRA_URL = "url";

    public static final java.lang.String SUBREDDIT = "sub";

    public static final java.lang.String ADAPTER_POSITION = "adapter_position";

    public static final java.lang.String SUBMISSION_URL = "submission";

    public static final java.lang.String EXTRA_DISPLAY_URL = "displayUrl";

    public static final java.lang.String EXTRA_LQ = "lq";

    public static final java.lang.String EXTRA_SHARE_URL = "urlShare";

    public static java.lang.String fileLoc;

    public java.lang.String subreddit;

    public static java.lang.Runnable doOnClick;

    public static boolean didLoadGif;

    public float previous;

    public boolean hidden;

    public boolean imageShown;

    public java.lang.String actuallyLoaded;

    public boolean isGif;

    private android.app.NotificationManager mNotifyManager;

    private android.support.v4.app.NotificationCompat.Builder mBuilder;

    private long stopPosition;

    private me.ccrama.redditslide.util.GifUtils.AsyncLoadGif gif;

    private java.lang.String contentUrl;

    private me.ccrama.redditslide.Views.MediaVideoView videoView;

    private com.google.gson.Gson gson;

    private java.lang.String mashapeKey;

    public static void animateIn(android.view.View l) {
        l.setVisibility(android.view.View.VISIBLE);
        android.animation.ValueAnimator mAnimator = me.ccrama.redditslide.Activities.MediaView.slideAnimator(0, me.ccrama.redditslide.Reddit.dpToPxVertical(56), l);
        mAnimator.start();
    }

    public static void fadeIn(android.view.View l) {
        android.animation.ValueAnimator mAnimator = me.ccrama.redditslide.Activities.MediaView.fadeAnimator(0.66F, 1, l);
        mAnimator.start();
    }

    private static android.animation.ValueAnimator fadeAnimator(float start, float end, final android.view.View v) {
        android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofFloat(start, end);
        animator.setInterpolator(new android.support.v4.view.animation.FastOutSlowInInterpolator());
        animator.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {
            @java.lang.Override
            public void onAnimationUpdate(android.animation.ValueAnimator valueAnimator) {
                // Update Height
                float value = ((java.lang.Float) (valueAnimator.getAnimatedValue()));
                v.setAlpha(value);
            }
        });
        return animator;
    }

    private static android.animation.ValueAnimator slideAnimator(int start, int end, final android.view.View v) {
        android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofInt(start, end);
        animator.setInterpolator(new android.support.v4.view.animation.FastOutSlowInInterpolator());
        animator.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {
            @java.lang.Override
            public void onAnimationUpdate(android.animation.ValueAnimator valueAnimator) {
                // Update Height
                int value = ((java.lang.Integer) (valueAnimator.getAnimatedValue()));
                android.view.ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = value;
                v.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    public static void animateOut(final android.view.View l) {
        android.animation.ValueAnimator mAnimator = me.ccrama.redditslide.Activities.MediaView.slideAnimator(me.ccrama.redditslide.Reddit.dpToPxVertical(36), 0, l);
        mAnimator.addListener(new android.animation.Animator.AnimatorListener() {
            @java.lang.Override
            public void onAnimationStart(android.animation.Animator animation) {
            }

            @java.lang.Override
            public void onAnimationEnd(android.animation.Animator animation) {
                l.setVisibility(android.view.View.GONE);
            }

            @java.lang.Override
            public void onAnimationCancel(android.animation.Animator animation) {
            }

            @java.lang.Override
            public void onAnimationRepeat(android.animation.Animator animation) {
            }
        });
        mAnimator.start();
    }

    public static void fadeOut(final android.view.View l) {
        android.animation.ValueAnimator mAnimator = me.ccrama.redditslide.Activities.MediaView.fadeAnimator(1, 0.66F, l);
        mAnimator.start();
    }

    public static boolean shouldTruncate(java.lang.String url) {
        try {
            final java.net.URI uri = new java.net.URI(url);
            final java.lang.String path = uri.getPath();
            return ((!me.ccrama.redditslide.ContentType.isGif(uri)) && (!me.ccrama.redditslide.ContentType.isImage(uri))) && path.contains(".");
        } catch (java.net.URISyntaxException e) {
            return false;
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

    public void showBottomSheetImage() {
        int[] attrs = new int[]{ me.ccrama.redditslide.R.attr.tintColor };
        android.content.res.TypedArray ta = obtainStyledAttributes(attrs);
        int color = ta.getColor(0, android.graphics.Color.WHITE);
        android.graphics.drawable.Drawable external = getResources().getDrawable(me.ccrama.redditslide.R.drawable.openexternal);
        android.graphics.drawable.Drawable share = getResources().getDrawable(me.ccrama.redditslide.R.drawable.share);
        android.graphics.drawable.Drawable image = getResources().getDrawable(me.ccrama.redditslide.R.drawable.image);
        android.graphics.drawable.Drawable save = getResources().getDrawable(me.ccrama.redditslide.R.drawable.save);
        android.graphics.drawable.Drawable file = getResources().getDrawable(me.ccrama.redditslide.R.drawable.savecontent);
        android.graphics.drawable.Drawable thread = getResources().getDrawable(me.ccrama.redditslide.R.drawable.commentchange);
        external.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        share.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        image.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        save.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        file.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        thread.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        ta.recycle();
        com.cocosw.bottomsheet.BottomSheet.Builder b = new com.cocosw.bottomsheet.BottomSheet.Builder(this).title(contentUrl);
        b.sheet(2, external, getString(me.ccrama.redditslide.R.string.submission_link_extern));
        b.sheet(5, share, getString(me.ccrama.redditslide.R.string.submission_link_share));
        if (!isGif)
            b.sheet(3, image, getString(me.ccrama.redditslide.R.string.share_image));

        b.sheet(4, save, "Save " + (isGif ? "MP4" : "image"));
        if (((((isGif && (!contentUrl.contains(".mp4"))) && (!contentUrl.contains("streamable.com"))) && (!contentUrl.contains("gfycat.com"))) && (!contentUrl.contains("v.redd.it"))) && (!contentUrl.contains("vid.me"))) {
            java.lang.String type = contentUrl.substring(contentUrl.lastIndexOf(".") + 1, contentUrl.length()).toUpperCase();
            try {
                if (type.equals("GIFV") && new java.net.URL(contentUrl).getHost().equals("i.imgur.com")) {
                    type = "GIF";
                    contentUrl = contentUrl.replace(".gifv", ".gif");
                    // todo possibly share gifs  b.sheet(9, share, "Share GIF");
                }
            } catch (java.net.MalformedURLException e) {
                e.printStackTrace();
            }
            b.sheet(6, file, getString(me.ccrama.redditslide.R.string.mediaview_save, type));
        }
        if (contentUrl.contains("v.redd.it")) {
            b.sheet(15, thread, "View video thread");
        }
        b.listener(new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                switch (which) {
                    case 2 :
                        {
                            me.ccrama.redditslide.util.LinkUtil.openExternally(contentUrl);
                            break;
                        }
                    case 3 :
                        {
                            me.ccrama.redditslide.util.ShareUtil.shareImage(actuallyLoaded, me.ccrama.redditslide.Activities.MediaView.this);
                            break;
                        }
                    case 5 :
                        {
                            me.ccrama.redditslide.Reddit.defaultShareText("", org.apache.commons.text.StringEscapeUtils.unescapeHtml4(contentUrl), me.ccrama.redditslide.Activities.MediaView.this);
                            break;
                        }
                    case 6 :
                        {
                            saveFile(contentUrl);
                        }
                        break;
                    case 15 :
                        {
                            new me.ccrama.redditslide.SubmissionViews.OpenVRedditTask(me.ccrama.redditslide.Activities.MediaView.this, subreddit).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR, contentUrl);
                        }
                        break;
                    case 9 :
                        {
                            shareGif(contentUrl);
                        }
                        break;
                    case 4 :
                        {
                            doImageSave();
                            break;
                        }
                }
            }
        });
        b.show();
    }

    public void doImageSave() {
        if (!isGif) {
            if (me.ccrama.redditslide.Reddit.appRestart.getString("imagelocation", "").isEmpty()) {
                showFirstDialog();
            } else if (!new java.io.File(me.ccrama.redditslide.Reddit.appRestart.getString("imagelocation", "")).exists()) {
                showErrorDialog();
            } else {
                android.content.Intent i = new android.content.Intent(this, me.ccrama.redditslide.Notifications.ImageDownloadNotificationService.class);
                // always download the original file, or use the cached original if that is currently displayed
                i.putExtra("actuallyLoaded", contentUrl);
                if ((subreddit != null) && (!subreddit.isEmpty()))
                    i.putExtra("subreddit", subreddit);

                startService(i);
            }
        } else {
            me.ccrama.redditslide.Activities.MediaView.doOnClick.run();
        }
    }

    public void saveFile(final java.lang.String baseUrl) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
            @java.lang.Override
            protected java.lang.Void doInBackground(java.lang.Void... params) {
                if (me.ccrama.redditslide.Reddit.appRestart.getString("imagelocation", "").isEmpty()) {
                    showFirstDialog();
                } else if (!new java.io.File(me.ccrama.redditslide.Reddit.appRestart.getString("imagelocation", "")).exists()) {
                    showErrorDialog();
                } else {
                    final java.io.File f = new java.io.File(((me.ccrama.redditslide.Reddit.appRestart.getString("imagelocation", "") + java.io.File.separator) + java.util.UUID.randomUUID().toString()) + baseUrl.substring(baseUrl.lastIndexOf(".")));
                    mNotifyManager = ((android.app.NotificationManager) (getSystemService(android.content.Context.NOTIFICATION_SERVICE)));
                    mBuilder = new android.support.v4.app.NotificationCompat.Builder(me.ccrama.redditslide.Activities.MediaView.this);
                    mBuilder.setChannelId(me.ccrama.redditslide.Reddit.CHANNEL_IMG);
                    mBuilder.setContentTitle(getString(me.ccrama.redditslide.R.string.mediaview_saving, baseUrl)).setSmallIcon(me.ccrama.redditslide.R.drawable.download_png);
                    try {
                        final java.net.URL url = new java.net.URL(baseUrl);// wont exist on server yet, just load the full version

                        java.net.URLConnection ucon = url.openConnection();
                        ucon.setReadTimeout(5000);
                        ucon.setConnectTimeout(10000);
                        java.io.InputStream is = ucon.getInputStream();
                        java.io.BufferedInputStream inStream = new java.io.BufferedInputStream(is, 1024 * 5);
                        int length = ucon.getContentLength();
                        f.createNewFile();
                        java.io.FileOutputStream outStream = new java.io.FileOutputStream(f);
                        byte[] buff = new byte[5 * 1024];
                        int len;
                        int last = 0;
                        while ((len = inStream.read(buff)) != (-1)) {
                            outStream.write(buff, 0, len);
                            int percent = java.lang.Math.round((100.0F * f.length()) / length);
                            if (percent > last) {
                                last = percent;
                                mBuilder.setProgress(length, ((int) (f.length())), false);
                                mNotifyManager.notify(1, mBuilder.build());
                            }
                        } 
                        outStream.flush();
                        outStream.close();
                        inStream.close();
                        android.media.MediaScannerConnection.scanFile(me.ccrama.redditslide.Activities.MediaView.this, new java.lang.String[]{ f.getAbsolutePath() }, null, new android.media.MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(java.lang.String path, android.net.Uri uri) {
                                android.content.Intent mediaScanIntent = me.ccrama.redditslide.util.FileUtil.getFileIntent(f, new android.content.Intent(android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE), me.ccrama.redditslide.Activities.MediaView.this);
                                me.ccrama.redditslide.Activities.MediaView.this.sendBroadcast(mediaScanIntent);
                                final android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
                                android.app.PendingIntent contentIntent = android.app.PendingIntent.getActivity(me.ccrama.redditslide.Activities.MediaView.this, 0, shareIntent, android.app.PendingIntent.FLAG_CANCEL_CURRENT);
                                android.app.Notification notif = new android.support.v4.app.NotificationCompat.Builder(me.ccrama.redditslide.Activities.MediaView.this).setContentTitle(getString(me.ccrama.redditslide.R.string.gif_saved)).setSmallIcon(me.ccrama.redditslide.R.drawable.save_png).setChannelId(me.ccrama.redditslide.Reddit.CHANNEL_IMG).setContentIntent(contentIntent).build();
                                android.app.NotificationManager mNotificationManager = ((android.app.NotificationManager) (getSystemService(android.app.Activity.NOTIFICATION_SERVICE)));
                                mNotificationManager.notify(1, notif);
                            }
                        });
                    } catch (java.lang.Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void shareGif(final java.lang.String baseUrl) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
            @java.lang.Override
            protected java.lang.Void doInBackground(java.lang.Void... params) {
                if (me.ccrama.redditslide.Reddit.appRestart.getString("imagelocation", "").isEmpty()) {
                    showFirstDialog();
                } else if (!new java.io.File(me.ccrama.redditslide.Reddit.appRestart.getString("imagelocation", "")).exists()) {
                    showErrorDialog();
                } else {
                    final java.io.File f = new java.io.File(((me.ccrama.redditslide.Reddit.appRestart.getString("imagelocation", "") + java.io.File.separator) + java.util.UUID.randomUUID().toString()) + baseUrl.substring(baseUrl.lastIndexOf(".")));
                    mNotifyManager = ((android.app.NotificationManager) (getSystemService(android.content.Context.NOTIFICATION_SERVICE)));
                    mBuilder = new android.support.v4.app.NotificationCompat.Builder(me.ccrama.redditslide.Activities.MediaView.this);
                    mBuilder.setContentTitle(getString(me.ccrama.redditslide.R.string.mediaview_saving, baseUrl)).setChannelId(me.ccrama.redditslide.Reddit.CHANNEL_IMG).setSmallIcon(me.ccrama.redditslide.R.drawable.save);
                    try {
                        final java.net.URL url = new java.net.URL(baseUrl);// wont exist on server yet, just load the full version

                        java.net.URLConnection ucon = url.openConnection();
                        ucon.setReadTimeout(5000);
                        ucon.setConnectTimeout(10000);
                        java.io.InputStream is = ucon.getInputStream();
                        java.io.BufferedInputStream inStream = new java.io.BufferedInputStream(is, 1024 * 5);
                        int length = ucon.getContentLength();
                        f.createNewFile();
                        java.io.FileOutputStream outStream = new java.io.FileOutputStream(f);
                        byte[] buff = new byte[5 * 1024];
                        int len;
                        int last = 0;
                        while ((len = inStream.read(buff)) != (-1)) {
                            outStream.write(buff, 0, len);
                            int percent = java.lang.Math.round((100.0F * f.length()) / length);
                            if (percent > last) {
                                last = percent;
                                mBuilder.setProgress(length, ((int) (f.length())), false);
                                mNotifyManager.notify(1, mBuilder.build());
                            }
                        } 
                        outStream.flush();
                        outStream.close();
                        inStream.close();
                        android.media.MediaScannerConnection.scanFile(me.ccrama.redditslide.Activities.MediaView.this, new java.lang.String[]{ f.getAbsolutePath() }, null, new android.media.MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(java.lang.String path, android.net.Uri uri) {
                                android.content.Intent mediaScanIntent = me.ccrama.redditslide.util.FileUtil.getFileIntent(f, new android.content.Intent(android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE), me.ccrama.redditslide.Activities.MediaView.this);
                                me.ccrama.redditslide.Activities.MediaView.this.sendBroadcast(mediaScanIntent);
                                final android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
                                startActivity(android.content.Intent.createChooser(shareIntent, "Share GIF"));
                                android.app.NotificationManager mNotificationManager = ((android.app.NotificationManager) (getSystemService(android.app.Activity.NOTIFICATION_SERVICE)));
                                mNotificationManager.cancel(1);
                            }
                        });
                    } catch (java.lang.Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @java.lang.Override
    public void onDestroy() {
        super.onDestroy();
        ((me.ccrama.redditslide.Views.SubsamplingScaleImageView) (findViewById(me.ccrama.redditslide.R.id.submission_image))).recycle();
        if (gif != null) {
            gif.cancel(true);
            gif.cancel();
        }
        me.ccrama.redditslide.Activities.MediaView.doOnClick = null;
        if (((!me.ccrama.redditslide.Activities.MediaView.didLoadGif) && (me.ccrama.redditslide.Activities.MediaView.fileLoc != null)) && (!me.ccrama.redditslide.Activities.MediaView.fileLoc.isEmpty())) {
            new java.io.File(me.ccrama.redditslide.Activities.MediaView.fileLoc).delete();
        }
    }

    @java.lang.Override
    public void onSaveInstanceState(android.os.Bundle outState) {
        super.onSaveInstanceState(outState);
        if (videoView != null) {
            stopPosition = videoView.getCurrentPosition();
            videoView.pause();
            outState.putLong("position", stopPosition);
        }
    }

    /* Possible drag to exit implementation in the future
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

    if(event.getF) {
    // peekView.doScroll(event);

    FrameLayout.LayoutParams params =
    (FrameLayout.LayoutParams) base.getLayoutParams();

    switch (event.getAction()) {
    case MotionEvent.ACTION_MOVE:

    params.topMargin = (int) -((origY - event.getY()));
    if (event.getY() != origY) {
    params.leftMargin = twelve *2;
    params.rightMargin = twelve * 2;
    } else {
    params.leftMargin = 0;
    params.rightMargin = 0;
    }

    if (event.getY() != (origY)) {
    shouldClose = true;
    } else if (event.getY() == (origY)) {
    shouldClose = false;
    }
    base.setLayoutParams(params);
    break;
    case MotionEvent.ACTION_DOWN:
    origY = event.getY();
    break;
    }
    }
    // we don't want to pass along the touch event or else it will just scroll under the PeekView}

    if (event.getAction() == MotionEvent.ACTION_UP && shouldClose) {
    finish();
    return false;
    }

    return super.dispatchTouchEvent(event);
    }
     */
    public void hideOnLongClick() {
        findViewById(me.ccrama.redditslide.R.id.gifheader).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                if (findViewById(me.ccrama.redditslide.R.id.gifheader).getVisibility() == android.view.View.GONE) {
                    me.ccrama.redditslide.Activities.MediaView.animateIn(findViewById(me.ccrama.redditslide.R.id.gifheader));
                    me.ccrama.redditslide.Activities.MediaView.fadeOut(findViewById(me.ccrama.redditslide.R.id.black));
                    getWindow().getDecorView().setSystemUiVisibility(0);
                } else {
                    me.ccrama.redditslide.Activities.MediaView.animateOut(findViewById(me.ccrama.redditslide.R.id.gifheader));
                    me.ccrama.redditslide.Activities.MediaView.fadeIn(findViewById(me.ccrama.redditslide.R.id.black));
                    getWindow().getDecorView().setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_LOW_PROFILE);
                }
            }
        });
        findViewById(me.ccrama.redditslide.R.id.submission_image).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v2) {
                if (findViewById(me.ccrama.redditslide.R.id.gifheader).getVisibility() == android.view.View.GONE) {
                    me.ccrama.redditslide.Activities.MediaView.animateIn(findViewById(me.ccrama.redditslide.R.id.gifheader));
                    me.ccrama.redditslide.Activities.MediaView.fadeOut(findViewById(me.ccrama.redditslide.R.id.black));
                    getWindow().getDecorView().setSystemUiVisibility(0);
                } else {
                    finish();
                }
            }
        });
    }

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        overrideRedditSwipeAnywhere();
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(new me.ccrama.redditslide.ColorPreferences(this).getDarkThemeSubreddit(""), true);
        gson = new com.google.gson.Gson();
        mashapeKey = me.ccrama.redditslide.SecretConstants.getImgurApiKey(this);
        if ((savedInstanceState != null) && savedInstanceState.containsKey("position")) {
            stopPosition = savedInstanceState.getLong("position");
        }
        me.ccrama.redditslide.Activities.MediaView.doOnClick = new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
            }
        };
        setContentView(me.ccrama.redditslide.R.layout.activity_media);
        // Keep the screen on
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final java.lang.String firstUrl = getIntent().getExtras().getString(me.ccrama.redditslide.Activities.MediaView.EXTRA_DISPLAY_URL, "");
        contentUrl = getIntent().getExtras().getString(me.ccrama.redditslide.Activities.MediaView.EXTRA_URL);
        if ((contentUrl == null) || contentUrl.isEmpty()) {
            finish();
            return;
        }
        setShareUrl(contentUrl);
        if (contentUrl.contains("reddituploads.com")) {
            contentUrl = android.text.Html.fromHtml(contentUrl).toString();
        }
        if ((contentUrl != null) && me.ccrama.redditslide.Activities.MediaView.shouldTruncate(contentUrl)) {
            contentUrl = contentUrl.substring(0, contentUrl.lastIndexOf("."));
        }
        actuallyLoaded = contentUrl;
        if (getIntent().hasExtra(me.ccrama.redditslide.Activities.MediaView.SUBMISSION_URL)) {
            final int commentUrl = getIntent().getExtras().getInt(me.ccrama.redditslide.Activities.MediaView.ADAPTER_POSITION);
            findViewById(me.ccrama.redditslide.R.id.comments).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    finish();
                    me.ccrama.redditslide.Fragments.SubmissionsView.datachanged(commentUrl);
                }
            });
        } else {
            findViewById(me.ccrama.redditslide.R.id.comments).setVisibility(android.view.View.GONE);
        }
        if (getIntent().hasExtra(me.ccrama.redditslide.Activities.MediaView.SUBREDDIT)) {
            subreddit = getIntent().getExtras().getString(me.ccrama.redditslide.Activities.MediaView.SUBREDDIT);
        }
        findViewById(me.ccrama.redditslide.R.id.mute).setVisibility(android.view.View.GONE);
        if (getIntent().hasExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_LQ)) {
            java.lang.String lqUrl = getIntent().getStringExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_DISPLAY_URL);
            displayImage(lqUrl);
            findViewById(me.ccrama.redditslide.R.id.hq).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    imageShown = false;
                    doLoad(contentUrl);
                    findViewById(me.ccrama.redditslide.R.id.hq).setVisibility(android.view.View.GONE);
                }
            });
        } else if ((me.ccrama.redditslide.ContentType.isImgurImage(contentUrl) && me.ccrama.redditslide.SettingValues.loadImageLq) && (me.ccrama.redditslide.SettingValues.lowResAlways || ((!me.ccrama.redditslide.util.NetworkUtil.isConnectedWifi(this)) && me.ccrama.redditslide.SettingValues.lowResMobile))) {
            java.lang.String url = contentUrl;
            url = (url.substring(0, url.lastIndexOf(".")) + (me.ccrama.redditslide.SettingValues.lqLow ? "m" : me.ccrama.redditslide.SettingValues.lqMid ? "l" : "h")) + url.substring(url.lastIndexOf("."), url.length());
            displayImage(url);
            findViewById(me.ccrama.redditslide.R.id.hq).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    imageShown = false;
                    doLoad(contentUrl);
                    findViewById(me.ccrama.redditslide.R.id.hq).setVisibility(android.view.View.GONE);
                }
            });
        } else {
            if (((!firstUrl.isEmpty()) && (contentUrl != null)) && me.ccrama.redditslide.ContentType.displayImage(me.ccrama.redditslide.ContentType.getContentType(contentUrl))) {
                ((android.widget.ProgressBar) (findViewById(me.ccrama.redditslide.R.id.progress))).setIndeterminate(true);
                if (me.ccrama.redditslide.ContentType.isImgurHash(firstUrl)) {
                    displayImage(firstUrl + ".png");
                } else {
                    displayImage(firstUrl);
                }
            } else if (firstUrl.isEmpty()) {
                imageShown = false;
                ((android.widget.ProgressBar) (findViewById(me.ccrama.redditslide.R.id.progress))).setIndeterminate(true);
            }
            findViewById(me.ccrama.redditslide.R.id.hq).setVisibility(android.view.View.GONE);
            doLoad(contentUrl);
        }
        if (!me.ccrama.redditslide.Reddit.appRestart.contains("tutorialSwipe")) {
            startActivityForResult(new android.content.Intent(this, me.ccrama.redditslide.Activities.SwipeTutorial.class), 3);
        }
        findViewById(me.ccrama.redditslide.R.id.more).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                showBottomSheetImage();
            }
        });
        findViewById(me.ccrama.redditslide.R.id.save).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                doImageSave();
            }
        });
        hideOnLongClick();
    }

    private me.ccrama.redditslide.ContentType.Type contentType = me.ccrama.redditslide.ContentType.Type.IMAGE;

    public void doLoad(final java.lang.String contentUrl) {
        contentType = me.ccrama.redditslide.ContentType.getContentType(contentUrl);
        switch (contentType) {
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
            case VREDDIT_DIRECT :
            case VREDDIT_REDIRECT :
            case GIF :
                doLoadGif(contentUrl);
                break;
        }
    }

    public void doLoadGif(final java.lang.String dat) {
        isGif = true;
        findViewById(me.ccrama.redditslide.R.id.hq).setVisibility(android.view.View.GONE);
        videoView = ((me.ccrama.redditslide.Views.MediaVideoView) (findViewById(me.ccrama.redditslide.R.id.gif)));
        findViewById(me.ccrama.redditslide.R.id.black).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                if (findViewById(me.ccrama.redditslide.R.id.gifheader).getVisibility() == android.view.View.GONE) {
                    me.ccrama.redditslide.Activities.MediaView.animateIn(findViewById(me.ccrama.redditslide.R.id.gifheader));
                    me.ccrama.redditslide.Activities.MediaView.fadeOut(findViewById(me.ccrama.redditslide.R.id.black));
                }
            }
        });
        videoView.clearFocus();
        findViewById(me.ccrama.redditslide.R.id.gifarea).setVisibility(android.view.View.VISIBLE);
        findViewById(me.ccrama.redditslide.R.id.submission_image).setVisibility(android.view.View.GONE);
        final android.widget.ProgressBar loader = ((android.widget.ProgressBar) (findViewById(me.ccrama.redditslide.R.id.gifprogress)));
        findViewById(me.ccrama.redditslide.R.id.progress).setVisibility(android.view.View.GONE);
        gif = new me.ccrama.redditslide.util.GifUtils.AsyncLoadGif(this, ((me.ccrama.redditslide.Views.MediaVideoView) (findViewById(me.ccrama.redditslide.R.id.gif))), loader, findViewById(me.ccrama.redditslide.R.id.placeholder), me.ccrama.redditslide.Activities.MediaView.doOnClick, true, false, true, ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.size))), subreddit);
        if (contentType != me.ccrama.redditslide.ContentType.Type.GIF) {
            videoView.mute = findViewById(me.ccrama.redditslide.R.id.mute);
            if (contentType != me.ccrama.redditslide.ContentType.Type.VREDDIT_DIRECT) {
                videoView.mute.setVisibility(android.view.View.VISIBLE);
            }
            gif.setMute(videoView.mute);
        }
        gif.execute(dat);
        findViewById(me.ccrama.redditslide.R.id.more).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                showBottomSheetImage();
            }
        });
    }

    public void doLoadImgur(java.lang.String url) {
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        final java.lang.String finalUrl = url;
        java.lang.String hash = url.substring(url.lastIndexOf("/"), url.length());
        if (me.ccrama.redditslide.util.NetworkUtil.isConnected(this)) {
            if (hash.startsWith("/"))
                hash = hash.substring(1, hash.length());

            final java.lang.String apiUrl = ("https://imgur-apiv3.p.mashape.com/3/image/" + hash) + ".json";
            me.ccrama.redditslide.util.LogUtil.v(apiUrl);
            new android.os.AsyncTask<java.lang.Void, java.lang.Void, com.google.gson.JsonObject>() {
                @java.lang.Override
                protected com.google.gson.JsonObject doInBackground(java.lang.Void... params) {
                    return me.ccrama.redditslide.util.HttpUtil.getImgurMashapeJsonObject(me.ccrama.redditslide.Reddit.client, gson, apiUrl, mashapeKey);
                }

                @java.lang.Override
                protected void onPostExecute(com.google.gson.JsonObject result) {
                    if (((result != null) && (!result.isJsonNull())) && result.has("error")) {
                        me.ccrama.redditslide.util.LogUtil.v("Error loading content");
                        me.ccrama.redditslide.Activities.MediaView.this.finish();
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
                            e2.printStackTrace();
                            android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.MediaView.this, me.ccrama.redditslide.Activities.Website.class);
                            i.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_URL, finalUrl);
                            me.ccrama.redditslide.Activities.MediaView.this.startActivity(i);
                            finish();
                        }
                    }
                }
            }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void doLoadXKCD(java.lang.String url) {
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        if (me.ccrama.redditslide.util.NetworkUtil.isConnected(this)) {
            final java.lang.String apiUrl = url + "info.0.json";
            me.ccrama.redditslide.util.LogUtil.v(apiUrl);
            final java.lang.String finalUrl = url;
            new android.os.AsyncTask<java.lang.Void, java.lang.Void, com.google.gson.JsonObject>() {
                @java.lang.Override
                protected com.google.gson.JsonObject doInBackground(java.lang.Void... params) {
                    return me.ccrama.redditslide.util.HttpUtil.getJsonObject(me.ccrama.redditslide.Reddit.client, gson, apiUrl);
                }

                @java.lang.Override
                protected void onPostExecute(final com.google.gson.JsonObject result) {
                    if (((result != null) && (!result.isJsonNull())) && result.has("error")) {
                        me.ccrama.redditslide.util.LogUtil.v("Error loading content");
                        me.ccrama.redditslide.Activities.MediaView.this.finish();
                    } else {
                        try {
                            if (((result != null) && (!result.isJsonNull())) && result.has("img")) {
                                doLoadImage(result.get("img").getAsString());
                                findViewById(me.ccrama.redditslide.R.id.submission_image).setOnLongClickListener(new android.view.View.OnLongClickListener() {
                                    @java.lang.Override
                                    public boolean onLongClick(android.view.View v) {
                                        try {
                                            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.MediaView.this).setTitle(result.get("safe_title").getAsString()).setMessage(result.get("alt").getAsString()).show();
                                        } catch (java.lang.Exception ignored) {
                                        }
                                        return true;
                                    }
                                });
                            } else {
                                android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.MediaView.this, me.ccrama.redditslide.Activities.Website.class);
                                i.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_URL, finalUrl);
                                me.ccrama.redditslide.Activities.MediaView.this.startActivity(i);
                                finish();
                            }
                        } catch (java.lang.Exception e2) {
                            e2.printStackTrace();
                            android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.MediaView.this, me.ccrama.redditslide.Activities.Website.class);
                            i.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_URL, finalUrl);
                            me.ccrama.redditslide.Activities.MediaView.this.startActivity(i);
                            finish();
                        }
                    }
                }
            }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void doLoadDeviantArt(java.lang.String url) {
        final java.lang.String apiUrl = "http://backend.deviantart.com/oembed?url=" + url;
        me.ccrama.redditslide.util.LogUtil.v(apiUrl);
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, com.google.gson.JsonObject>() {
            @java.lang.Override
            protected com.google.gson.JsonObject doInBackground(java.lang.Void... params) {
                return me.ccrama.redditslide.util.HttpUtil.getJsonObject(me.ccrama.redditslide.Reddit.client, gson, apiUrl);
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
                    android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.MediaView.this, me.ccrama.redditslide.Activities.Website.class);
                    i.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_URL, contentUrl);
                    me.ccrama.redditslide.Activities.MediaView.this.startActivity(i);
                    finish();
                }
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void doLoadImage(java.lang.String contentUrl) {
        if ((contentUrl != null) && contentUrl.contains("bildgur.de")) {
            contentUrl = contentUrl.replace("b.bildgur.de", "i.imgur.com");
        }
        if ((contentUrl != null) && me.ccrama.redditslide.ContentType.isImgurLink(contentUrl)) {
            contentUrl = contentUrl + ".png";
        }
        findViewById(me.ccrama.redditslide.R.id.gifprogress).setVisibility(android.view.View.GONE);
        if ((contentUrl != null) && contentUrl.contains("m.imgur.com")) {
            contentUrl = contentUrl.replace("m.imgur.com", "i.imgur.com");
        }
        if (contentUrl == null) {
            finish();
            // todo maybe something better
        }
        if ((((contentUrl != null) && (!contentUrl.startsWith("https://i.redditmedia.com"))) && (!contentUrl.startsWith("https://i.reddituploads.com"))) && (!contentUrl.contains("imgur.com"))) {
            // we can assume redditmedia and imgur links are to direct images and not websites
            findViewById(me.ccrama.redditslide.R.id.progress).setVisibility(android.view.View.VISIBLE);
            ((android.widget.ProgressBar) (findViewById(me.ccrama.redditslide.R.id.progress))).setIndeterminate(true);
            final java.lang.String finalUrl2 = contentUrl;
            new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
                @java.lang.Override
                protected java.lang.Void doInBackground(java.lang.Void... params) {
                    try {
                        java.net.URL obj = new java.net.URL(finalUrl2);
                        java.net.URLConnection conn = obj.openConnection();
                        final java.lang.String type = conn.getHeaderField("Content-Type");
                        runOnUiThread(new java.lang.Runnable() {
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
                                    android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.MediaView.this, me.ccrama.redditslide.Activities.Website.class);
                                    i.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_URL, finalUrl2);
                                    me.ccrama.redditslide.Activities.MediaView.this.startActivity(i);
                                    finish();
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
                    findViewById(me.ccrama.redditslide.R.id.progress).setVisibility(android.view.View.GONE);
                }
            }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            displayImage(contentUrl);
        }
        actuallyLoaded = contentUrl;
    }

    @java.lang.Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        if (requestCode == 3) {
            me.ccrama.redditslide.Reddit.appRestart.edit().putBoolean("tutorialSwipe", true).apply();
        }
    }

    public void displayImage(final java.lang.String urlB) {
        me.ccrama.redditslide.util.LogUtil.v("Displaying " + urlB);
        final java.lang.String url = org.apache.commons.text.StringEscapeUtils.unescapeHtml4(urlB);
        if (!imageShown) {
            actuallyLoaded = url;
            final me.ccrama.redditslide.Views.SubsamplingScaleImageView i = ((me.ccrama.redditslide.Views.SubsamplingScaleImageView) (findViewById(me.ccrama.redditslide.R.id.submission_image)));
            i.setMinimumDpi(70);
            i.setMinimumTileDpi(240);
            final android.widget.ProgressBar bar = ((android.widget.ProgressBar) (findViewById(me.ccrama.redditslide.R.id.progress)));
            bar.setIndeterminate(false);
            bar.setProgress(0);
            final android.os.Handler handler = new android.os.Handler();
            final java.lang.Runnable progressBarDelayRunner = new java.lang.Runnable() {
                public void run() {
                    bar.setVisibility(android.view.View.VISIBLE);
                }
            };
            handler.postDelayed(progressBarDelayRunner, 500);
            android.widget.ImageView fakeImage = new android.widget.ImageView(this);
            fakeImage.setLayoutParams(new android.widget.LinearLayout.LayoutParams(i.getWidth(), i.getHeight()));
            fakeImage.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
            java.io.File f = ((me.ccrama.redditslide.Reddit) (getApplicationContext())).getImageLoader().getDiscCache().get(url);
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
                } catch (java.lang.Exception e) {
                    imageShown = false;
                    // todo  i.setImage(ImageSource.bitmap(loadedImage));
                }
                findViewById(me.ccrama.redditslide.R.id.progress).setVisibility(android.view.View.GONE);
                handler.removeCallbacks(progressBarDelayRunner);
                previous = i.scale;
                final float base = i.scale;
                i.postDelayed(new java.lang.Runnable() {
                    @java.lang.Override
                    public void run() {
                        i.setOnZoomChangedListener(new me.ccrama.redditslide.Views.SubsamplingScaleImageView.OnZoomChangedListener() {
                            @java.lang.Override
                            public void onZoomLevelChanged(float zoom) {
                                if (((zoom > previous) && (!hidden)) && (zoom > base)) {
                                    hidden = true;
                                    final android.view.View base = findViewById(me.ccrama.redditslide.R.id.gifheader);
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
                                    final android.view.View base = findViewById(me.ccrama.redditslide.R.id.gifheader);
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
                }, 2000);
            } else {
                final android.widget.TextView size = ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.size)));
                ((me.ccrama.redditslide.Reddit) (getApplication())).getImageLoader().displayImage(url, new com.nostra13.universalimageloader.core.imageaware.ImageViewAware(fakeImage), new com.nostra13.universalimageloader.core.DisplayImageOptions.Builder().resetViewBeforeLoading(true).cacheOnDisk(true).imageScaleType(com.nostra13.universalimageloader.core.assist.ImageScaleType.NONE).cacheInMemory(false).build(), new com.nostra13.universalimageloader.core.listener.ImageLoadingListener() {
                    private android.view.View mView;

                    @java.lang.Override
                    public void onLoadingStarted(java.lang.String imageUri, android.view.View view) {
                        imageShown = true;
                        size.setVisibility(android.view.View.VISIBLE);
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
                        size.setVisibility(android.view.View.GONE);
                        java.io.File f = ((me.ccrama.redditslide.Reddit) (getApplicationContext())).getImageLoader().getDiscCache().get(url);
                        if ((f != null) && f.exists()) {
                            i.setImage(me.ccrama.redditslide.Views.ImageSource.uri(f.getAbsolutePath()));
                        } else {
                            i.setImage(me.ccrama.redditslide.Views.ImageSource.bitmap(loadedImage));
                        }
                        findViewById(me.ccrama.redditslide.R.id.progress).setVisibility(android.view.View.GONE);
                        handler.removeCallbacks(progressBarDelayRunner);
                        previous = i.scale;
                        final float base = i.scale;
                        i.setOnZoomChangedListener(new me.ccrama.redditslide.Views.SubsamplingScaleImageView.OnZoomChangedListener() {
                            @java.lang.Override
                            public void onZoomLevelChanged(float zoom) {
                                if (((zoom > previous) && (!hidden)) && (zoom > base)) {
                                    hidden = true;
                                    final android.view.View base = findViewById(me.ccrama.redditslide.R.id.gifheader);
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
                                    final android.view.View base = findViewById(me.ccrama.redditslide.R.id.gifheader);
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
                        size.setText(me.ccrama.redditslide.Activities.AlbumPager.readableFileSize(total));
                        ((android.widget.ProgressBar) (findViewById(me.ccrama.redditslide.R.id.progress))).setProgress(java.lang.Math.round((100.0F * current) / total));
                    }
                });
            }
        }
    }

    public void showFirstDialog() {
        runOnUiThread(new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                try {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.MediaView.this).setTitle(me.ccrama.redditslide.R.string.set_save_location).setMessage(me.ccrama.redditslide.R.string.set_save_location_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            // changes initial path, defaults to external storage directory
                            // changes label of the choose button
                            new me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.Builder(me.ccrama.redditslide.Activities.MediaView.this).chooseButton(me.ccrama.redditslide.R.string.btn_select).initialPath(android.os.Environment.getExternalStorageDirectory().getPath()).show();
                        }
                    }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, null).show();
                } catch (java.lang.Exception ignored) {
                }
            }
        });
    }

    public void showErrorDialog() {
        runOnUiThread(new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.MediaView.this).setTitle(me.ccrama.redditslide.R.string.err_something_wrong).setMessage(me.ccrama.redditslide.R.string.err_couldnt_save_choose_new).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        // changes initial path, defaults to external storage directory
                        // changes label of the choose button
                        new me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.Builder(me.ccrama.redditslide.Activities.MediaView.this).chooseButton(me.ccrama.redditslide.R.string.btn_select).initialPath(android.os.Environment.getExternalStorageDirectory().getPath()).show();
                    }
                }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, null).show();
            }
        });
    }

    @java.lang.Override
    public void onFolderSelection(me.ccrama.redditslide.Fragments.FolderChooserDialogCreate dialog, java.io.File folder) {
        if (folder != null) {
            me.ccrama.redditslide.Reddit.appRestart.edit().putString("imagelocation", folder.getAbsolutePath()).apply();
            android.widget.Toast.makeText(this, getString(me.ccrama.redditslide.R.string.settings_set_image_location, folder.getAbsolutePath()), android.widget.Toast.LENGTH_LONG).show();
        }
    }
}