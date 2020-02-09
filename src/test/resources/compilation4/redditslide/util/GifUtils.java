package me.ccrama.redditslide.util;
import java.io.OutputStream;
import com.google.gson.JsonObject;
import java.text.DecimalFormat;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import me.ccrama.redditslide.SettingValues;
import org.jetbrains.annotations.NotNull;
import java.nio.ByteBuffer;
import me.ccrama.redditslide.Activities.MediaView;
import com.google.gson.Gson;
import me.ccrama.redditslide.Activities.Website;
import me.ccrama.redditslide.Views.MediaVideoView;
import java.io.FileInputStream;
import java.nio.channels.WritableByteChannel;
import java.net.HttpURLConnection;
import java.net.URL;
import me.ccrama.redditslide.Fragments.FolderChooserDialogCreate;
import me.ccrama.redditslide.R;
import java.io.FileOutputStream;
import me.ccrama.redditslide.Reddit;
import java.nio.BufferOverflowException;
import java.util.UUID;
import java.io.File;
/**
 * Created by carlo_000 on 1/29/2016.
 */
public class GifUtils {
    private GifUtils() {
    }

    public static java.lang.String getSmallerGfy(java.lang.String gfyUrl) {
        gfyUrl = gfyUrl.replaceAll("fat|zippy|giant", "thumbs");
        if (!gfyUrl.endsWith("-mobile.mp4"))
            gfyUrl = gfyUrl.replaceAll("\\.mp4", "-mobile.mp4");

        return gfyUrl;
    }

    public static void doNotifGif(java.io.File f, android.app.Activity c) {
        android.content.Intent mediaScanIntent = me.ccrama.redditslide.util.FileUtil.getFileIntent(f, new android.content.Intent(android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE), c);
        c.sendBroadcast(mediaScanIntent);
        final android.content.Intent shareIntent = me.ccrama.redditslide.util.FileUtil.getFileIntent(f, new android.content.Intent(android.content.Intent.ACTION_VIEW), c);
        android.app.PendingIntent contentIntent = android.app.PendingIntent.getActivity(c, 0, shareIntent, android.app.PendingIntent.FLAG_CANCEL_CURRENT);
        android.app.Notification notif = new android.support.v4.app.NotificationCompat.Builder(c).setContentTitle(c.getString(me.ccrama.redditslide.R.string.gif_saved)).setSmallIcon(me.ccrama.redditslide.R.drawable.save_png).setContentIntent(contentIntent).setChannelId(me.ccrama.redditslide.Reddit.CHANNEL_IMG).build();
        android.app.NotificationManager mNotificationManager = ((android.app.NotificationManager) (c.getSystemService(android.app.Activity.NOTIFICATION_SERVICE)));
        mNotificationManager.notify(((int) (java.lang.System.currentTimeMillis())), notif);
    }

    public static void showErrorDialog(final android.app.Activity a) {
        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(a).setTitle(me.ccrama.redditslide.R.string.err_something_wrong).setMessage(me.ccrama.redditslide.R.string.err_couldnt_save_choose_new).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                // changes initial path, defaults to external storage directory
                // changes label of the choose button
                new me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.Builder(((me.ccrama.redditslide.Activities.MediaView) (a))).chooseButton(me.ccrama.redditslide.R.string.btn_select).initialPath(android.os.Environment.getExternalStorageDirectory().getPath()).show();
            }
        }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, null).show();
    }

    public static void showFirstDialog(final android.app.Activity a) {
        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(a).setTitle(me.ccrama.redditslide.R.string.set_gif_save_loc).setMessage(me.ccrama.redditslide.R.string.set_gif_save_loc_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                // changes initial path, defaults to external storage directory
                // changes label of the choose button
                new me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.Builder(((me.ccrama.redditslide.Activities.MediaView) (a))).chooseButton(me.ccrama.redditslide.R.string.btn_select).initialPath(android.os.Environment.getExternalStorageDirectory().getPath()).show();
            }
        }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, null).show();
    }

    public static void saveGif(java.io.File from, android.app.Activity a, java.lang.String subreddit) {
        me.ccrama.redditslide.util.LogUtil.v(from.getAbsolutePath());
        try {
            android.widget.Toast.makeText(a, a.getString(me.ccrama.redditslide.R.string.mediaview_notif_title), android.widget.Toast.LENGTH_SHORT).show();
        } catch (java.lang.Exception ignored) {
        }
        if (me.ccrama.redditslide.Reddit.appRestart.getString("imagelocation", "").isEmpty()) {
            me.ccrama.redditslide.util.GifUtils.showFirstDialog(a);
        } else if (!new java.io.File(me.ccrama.redditslide.Reddit.appRestart.getString("imagelocation", "")).exists()) {
            me.ccrama.redditslide.util.GifUtils.showErrorDialog(a);
        } else {
            if (me.ccrama.redditslide.SettingValues.imageSubfolders && (!subreddit.isEmpty())) {
                java.io.File directory = new java.io.File(me.ccrama.redditslide.Reddit.appRestart.getString("imagelocation", "") + (me.ccrama.redditslide.SettingValues.imageSubfolders && (!subreddit.isEmpty()) ? java.io.File.separator + subreddit : ""));
                directory.mkdirs();
            }
            java.io.File f = new java.io.File((((me.ccrama.redditslide.Reddit.appRestart.getString("imagelocation", "") + (me.ccrama.redditslide.SettingValues.imageSubfolders && (!subreddit.isEmpty()) ? java.io.File.separator + subreddit : "")) + java.io.File.separator) + java.util.UUID.randomUUID().toString()) + ".mp4");
            java.io.FileOutputStream out = null;
            java.io.InputStream in = null;
            try {
                in = new java.io.FileInputStream(from);
                out = new java.io.FileOutputStream(f);
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                } 
                out.close();
            } catch (java.lang.Exception e) {
                e.printStackTrace();
                me.ccrama.redditslide.util.LogUtil.e((((("Error saving GIF called with: " + "from = [") + from) + "], in = [") + in) + "]");
                me.ccrama.redditslide.util.GifUtils.showErrorDialog(a);
            } finally {
                try {
                    if (out != null) {
                        out.close();
                        me.ccrama.redditslide.util.GifUtils.doNotifGif(f, a);
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (java.io.IOException e) {
                    me.ccrama.redditslide.util.LogUtil.e((((("Error closing GIF called with: " + "from = [") + from) + "], out = [") + out) + "]");
                    me.ccrama.redditslide.util.GifUtils.showErrorDialog(a);
                }
            }
        }
    }

    public static class AsyncLoadGif extends android.os.AsyncTask<java.lang.String, java.lang.Void, java.lang.Void> {
        private android.app.Activity c;

        private me.ccrama.redditslide.Views.MediaVideoView video;

        private android.widget.ProgressBar progressBar;

        private android.view.View placeholder;

        private android.view.View gifSave;

        private boolean closeIfNull;

        private boolean hideControls;

        private boolean autostart;

        private java.lang.Runnable doOnClick;

        private android.view.View mute;

        public java.lang.String subreddit = "";

        private boolean cacheOnly;

        private android.widget.TextView size;

        public AsyncLoadGif(@org.jetbrains.annotations.NotNull
        android.app.Activity c, @org.jetbrains.annotations.NotNull
        me.ccrama.redditslide.Views.MediaVideoView video, @android.support.annotation.Nullable
        android.widget.ProgressBar p, @android.support.annotation.Nullable
        android.view.View placeholder, @android.support.annotation.Nullable
        java.lang.Runnable gifSave, @org.jetbrains.annotations.NotNull
        boolean closeIfNull, @org.jetbrains.annotations.NotNull
        boolean hideControls, boolean autostart, java.lang.String subreddit) {
            this.c = c;
            this.subreddit = subreddit;
            this.video = video;
            this.progressBar = p;
            this.closeIfNull = closeIfNull;
            this.placeholder = placeholder;
            this.doOnClick = gifSave;
            this.hideControls = hideControls;
            this.autostart = autostart;
        }

        public AsyncLoadGif(@org.jetbrains.annotations.NotNull
        android.app.Activity c, @org.jetbrains.annotations.NotNull
        me.ccrama.redditslide.Views.MediaVideoView video, @android.support.annotation.Nullable
        android.widget.ProgressBar p, @android.support.annotation.Nullable
        android.view.View placeholder, @android.support.annotation.Nullable
        java.lang.Runnable gifSave, @org.jetbrains.annotations.NotNull
        boolean closeIfNull, @org.jetbrains.annotations.NotNull
        boolean hideControls, boolean autostart, android.widget.TextView size, java.lang.String subreddit) {
            this.c = c;
            this.video = video;
            this.subreddit = subreddit;
            this.progressBar = p;
            this.closeIfNull = closeIfNull;
            this.placeholder = placeholder;
            this.doOnClick = gifSave;
            this.hideControls = hideControls;
            this.autostart = autostart;
            this.size = size;
        }

        public void onError() {
        }

        public void setMuteVisibility(final boolean visible) {
            if (mute != null) {
                c.runOnUiThread(new java.lang.Runnable() {
                    @java.lang.Override
                    public void run() {
                        if (!visible) {
                            mute.setVisibility(android.view.View.GONE);
                        } else {
                            mute.setVisibility(android.view.View.VISIBLE);
                        }
                    }
                });
            }
        }

        public void setMute(android.view.View muteView) {
            mute = muteView;
        }

        public AsyncLoadGif(@org.jetbrains.annotations.NotNull
        android.app.Activity c, @org.jetbrains.annotations.NotNull
        me.ccrama.redditslide.Views.MediaVideoView video, @android.support.annotation.Nullable
        android.widget.ProgressBar p, @android.support.annotation.Nullable
        android.view.View placeholder, @org.jetbrains.annotations.NotNull
        boolean closeIfNull, @org.jetbrains.annotations.NotNull
        boolean hideControls, boolean autostart, java.lang.String subreddit) {
            this.c = c;
            this.video = video;
            this.subreddit = subreddit;
            this.progressBar = p;
            this.closeIfNull = closeIfNull;
            this.placeholder = placeholder;
            this.hideControls = hideControls;
            this.autostart = autostart;
        }

        public AsyncLoadGif() {
            cacheOnly = true;
        }

        public void cancel() {
            me.ccrama.redditslide.util.LogUtil.v("cancelling");
            video.suspend();
        }

        @java.lang.Override
        protected void onPreExecute() {
            super.onPreExecute();
            gson = new com.google.gson.Gson();
        }

        com.google.gson.Gson gson;

        public enum VideoType {

            IMGUR,
            VID_ME,
            STREAMABLE,
            GFYCAT,
            DIRECT,
            OTHER,
            VREDDIT;
            public boolean shouldLoadPreview() {
                return this == me.ccrama.redditslide.util.GifUtils.AsyncLoadGif.VideoType.OTHER;
            }
        }

        public java.lang.String formatUrl(java.lang.String s) {
            if (s.endsWith("v") && (!s.contains("streamable.com"))) {
                s = s.substring(0, s.length() - 1);
            } else if (s.contains("gfycat") && ((!s.contains("mp4")) && (!s.contains("webm")))) {
                if (s.contains("-size_restricted"))
                    s = s.replace("-size_restricted", "");

            }
            if (((s.contains(".webm") || s.contains(".gif")) && (!s.contains(".gifv"))) && s.contains("imgur.com")) {
                s = s.replace(".gif", ".mp4");
                s = s.replace(".webm", ".mp4");
            }
            if (s.endsWith("/"))
                s = s.substring(0, s.length() - 1);

            if (s.endsWith("?r"))
                s = s.substring(0, s.length() - 2);

            if (s.contains("v.redd.it") && (!s.contains("DASH"))) {
                if (s.endsWith("/")) {
                    s = s.substring(s.length() - 2);
                }
                s = s + "/DASH_9_6_M";
            }
            return s;
        }

        public static me.ccrama.redditslide.util.GifUtils.AsyncLoadGif.VideoType getVideoType(java.lang.String url) {
            if (url.contains("v.redd.it")) {
                return me.ccrama.redditslide.util.GifUtils.AsyncLoadGif.VideoType.VREDDIT;
            }
            if (((url.contains(".mp4") || url.contains("webm")) || url.contains("redditmedia.com")) || url.contains("preview.redd.it")) {
                return me.ccrama.redditslide.util.GifUtils.AsyncLoadGif.VideoType.DIRECT;
            }
            if (url.contains("gfycat") && (!url.contains("mp4")))
                return me.ccrama.redditslide.util.GifUtils.AsyncLoadGif.VideoType.GFYCAT;

            if (url.contains("imgur.com"))
                return me.ccrama.redditslide.util.GifUtils.AsyncLoadGif.VideoType.IMGUR;

            if (url.contains("vid.me"))
                return me.ccrama.redditslide.util.GifUtils.AsyncLoadGif.VideoType.VID_ME;

            if (url.contains("streamable.com"))
                return me.ccrama.redditslide.util.GifUtils.AsyncLoadGif.VideoType.STREAMABLE;

            return me.ccrama.redditslide.util.GifUtils.AsyncLoadGif.VideoType.OTHER;
        }

        okhttp3.OkHttpClient client = me.ccrama.redditslide.Reddit.client;

        public void loadGfycat(java.lang.String name, com.google.gson.Gson gson) throws java.lang.Exception {
            me.ccrama.redditslide.util.GifUtils.showProgressBar(c, progressBar, false);
            if (!name.startsWith("/"))
                name = "/" + name;

            if (name.contains("-")) {
                name = name.split("-")[0];
            }
            java.lang.String gfycatUrl = "https://api.gfycat.com/v1/gfycats" + name;
            me.ccrama.redditslide.util.LogUtil.v(gfycatUrl);
            final com.google.gson.JsonObject result = me.ccrama.redditslide.util.HttpUtil.getJsonObject(client, gson, gfycatUrl);
            java.lang.String obj = "";
            if (((result == null) || (result.get("gfyItem") == null)) || result.getAsJsonObject("gfyItem").get("mp4Url").isJsonNull()) {
                onError();
                if (closeIfNull) {
                    c.runOnUiThread(new java.lang.Runnable() {
                        @java.lang.Override
                        public void run() {
                            try {
                                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(c).setTitle(me.ccrama.redditslide.R.string.gif_err_title).setMessage(me.ccrama.redditslide.R.string.gif_err_msg).setCancelable(false).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                                    @java.lang.Override
                                    public void onClick(android.content.DialogInterface dialog, int which) {
                                        c.finish();
                                    }
                                }).create().show();
                            } catch (java.lang.Exception e) {
                            }
                        }
                    });
                }
            } else if (result.getAsJsonObject("gfyItem").has("mobileUrl")) {
                obj = result.getAsJsonObject("gfyItem").get("mobileUrl").getAsString();
            } else {
                obj = result.getAsJsonObject("gfyItem").get("mp4Url").getAsString();
            }
            me.ccrama.redditslide.util.GifUtils.showProgressBar(c, progressBar, false);
            final java.net.URL finalUrl = new java.net.URL(obj);
            writeGif(finalUrl, progressBar, c, subreddit);
        }

        @java.lang.Override
        protected java.lang.Void doInBackground(java.lang.String... sub) {
            me.ccrama.redditslide.Activities.MediaView.didLoadGif = false;
            com.google.gson.Gson gson = new com.google.gson.Gson();
            final java.lang.String url = formatUrl(sub[0]);
            me.ccrama.redditslide.util.GifUtils.AsyncLoadGif.VideoType videoType = me.ccrama.redditslide.util.GifUtils.AsyncLoadGif.getVideoType(url);
            me.ccrama.redditslide.util.LogUtil.v((url + ", VideoType: ") + videoType);
            switch (videoType) {
                case VREDDIT :
                    try {
                        WriteGifMuxed(new java.net.URL(url), progressBar, c, subreddit);
                    } catch (java.lang.Exception e) {
                        me.ccrama.redditslide.util.LogUtil.e(e, "Error loading URL " + url);// Most likely is an image, not a gif!

                        if (((c instanceof me.ccrama.redditslide.Activities.MediaView) && url.contains("imgur.com")) && url.endsWith(".mp4")) {
                            c.runOnUiThread(new java.lang.Runnable() {
                                @java.lang.Override
                                public void run() {
                                    c.startActivity(new android.content.Intent(c, me.ccrama.redditslide.Activities.MediaView.class).putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_URL, url.replace(".mp4", ".png")));// Link is likely an image and not a gif

                                    c.finish();
                                }
                            });
                        } else if (closeIfNull) {
                            android.content.Intent web = new android.content.Intent(c, me.ccrama.redditslide.Activities.Website.class);
                            web.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_URL, url);
                            web.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_COLOR, android.graphics.Color.BLACK);
                            c.startActivity(web);
                            c.finish();
                        }
                    }
                    break;
                case GFYCAT :
                    java.lang.String name = url.substring(url.lastIndexOf("/", url.length()));
                    java.lang.String gfycatUrl = "https://gfycat.com/cajax/get" + name;
                    try {
                        loadGfycat(name, gson);
                    } catch (java.lang.Exception e) {
                        me.ccrama.redditslide.util.LogUtil.e(e, ((("Error loading gfycat video url = [" + url) + "] gfycatUrl = [") + gfycatUrl) + "]");
                    }
                    break;
                case DIRECT :
                    setMuteVisibility(true);
                case IMGUR :
                    try {
                        writeGif(new java.net.URL(url), progressBar, c, subreddit);
                    } catch (java.lang.Exception e) {
                        me.ccrama.redditslide.util.LogUtil.e(e, "Error loading URL " + url);// Most likely is an image, not a gif!

                        if (((c instanceof me.ccrama.redditslide.Activities.MediaView) && url.contains("imgur.com")) && url.endsWith(".mp4")) {
                            c.runOnUiThread(new java.lang.Runnable() {
                                @java.lang.Override
                                public void run() {
                                    c.startActivity(new android.content.Intent(c, me.ccrama.redditslide.Activities.MediaView.class).putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_URL, url.replace(".mp4", ".png")));// Link is likely an image and not a gif

                                    c.finish();
                                }
                            });
                        } else if (closeIfNull) {
                            android.content.Intent web = new android.content.Intent(c, me.ccrama.redditslide.Activities.Website.class);
                            web.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_URL, url);
                            web.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_COLOR, android.graphics.Color.BLACK);
                            c.startActivity(web);
                            c.finish();
                        }
                    }
                    break;
                case STREAMABLE :
                    java.lang.String hash = url.substring(url.lastIndexOf("/") + 1, url.length());
                    java.lang.String streamableUrl = "https://api.streamable.com/videos/" + hash;
                    me.ccrama.redditslide.util.LogUtil.v(streamableUrl);
                    try {
                        final com.google.gson.JsonObject result = me.ccrama.redditslide.util.HttpUtil.getJsonObject(client, gson, streamableUrl);
                        java.lang.String obj = "";
                        if (((result == null) || (result.get("files") == null)) || (!(result.getAsJsonObject("files").has("mp4") || result.getAsJsonObject("files").has("mp4-mobile")))) {
                            onError();
                            if (closeIfNull) {
                                c.runOnUiThread(new java.lang.Runnable() {
                                    @java.lang.Override
                                    public void run() {
                                        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(c).setTitle(me.ccrama.redditslide.R.string.error_video_not_found).setMessage(me.ccrama.redditslide.R.string.error_video_message).setCancelable(false).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                                            @java.lang.Override
                                            public void onClick(android.content.DialogInterface dialog, int which) {
                                                c.finish();
                                            }
                                        }).create().show();
                                    }
                                });
                            }
                        } else if (result.getAsJsonObject().get("files").getAsJsonObject().has("mp4-mobile") && (!result.getAsJsonObject().get("files").getAsJsonObject().get("mp4-mobile").getAsJsonObject().get("url").getAsString().isEmpty())) {
                            obj = "https:" + result.getAsJsonObject().get("files").getAsJsonObject().get("mp4-mobile").getAsJsonObject().get("url").getAsString();
                        } else {
                            obj = "https:" + result.getAsJsonObject().get("files").getAsJsonObject().get("mp4").getAsJsonObject().get("url").getAsString();
                        }
                        final java.net.URL finalUrl = new java.net.URL(obj);
                        writeGif(finalUrl, progressBar, c, subreddit);
                    } catch (java.lang.Exception e) {
                        me.ccrama.redditslide.util.LogUtil.e(e, ((("Error loading streamable video url = [" + url) + "] streamableUrl = [") + streamableUrl) + "]");
                        c.runOnUiThread(new java.lang.Runnable() {
                            @java.lang.Override
                            public void run() {
                                onError();
                            }
                        });
                        if (closeIfNull) {
                            c.runOnUiThread(new java.lang.Runnable() {
                                @java.lang.Override
                                public void run() {
                                    try {
                                        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(c).setTitle(me.ccrama.redditslide.R.string.error_video_not_found).setMessage(me.ccrama.redditslide.R.string.error_video_message).setCancelable(false).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                                            @java.lang.Override
                                            public void onClick(android.content.DialogInterface dialog, int which) {
                                                c.finish();
                                            }
                                        }).create().show();
                                    } catch (java.lang.Exception e) {
                                    }
                                }
                            });
                        }
                    }
                    break;
                case VID_ME :
                    java.lang.String vidmeUrl = "https://api.vid.me/videoByUrl?url=" + url;
                    me.ccrama.redditslide.util.LogUtil.v(vidmeUrl);
                    try {
                        final com.google.gson.JsonObject result = me.ccrama.redditslide.util.HttpUtil.getJsonObject(client, gson, vidmeUrl);
                        java.lang.String obj = "";
                        if ((((((result == null) || result.isJsonNull()) || (!result.has("video"))) || result.get("video").isJsonNull()) || (!result.get("video").getAsJsonObject().has("complete_url"))) || result.get("video").getAsJsonObject().get("complete_url").isJsonNull()) {
                            onError();
                            if (closeIfNull) {
                                android.content.Intent web = new android.content.Intent(c, me.ccrama.redditslide.Activities.Website.class);
                                web.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_URL, url);
                                web.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_COLOR, android.graphics.Color.BLACK);
                                c.startActivity(web);
                                c.finish();
                            }
                        } else {
                            obj = result.getAsJsonObject().get("video").getAsJsonObject().get("complete_url").getAsString();
                        }
                        final java.net.URL finalUrl = new java.net.URL(obj);
                        writeGif(finalUrl, progressBar, c, subreddit);
                    } catch (java.lang.Exception e) {
                        me.ccrama.redditslide.util.LogUtil.e(e, ((("Error loading vid.me video url = [" + url) + "] vidmeUrl = [") + vidmeUrl) + "]");
                    }
                    break;
                case OTHER :
                    me.ccrama.redditslide.util.LogUtil.e("We shouldn't be here!");
                    if (closeIfNull) {
                        android.content.Intent web = new android.content.Intent(c, me.ccrama.redditslide.Activities.Website.class);
                        web.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_URL, url);
                        web.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_COLOR, android.graphics.Color.BLACK);
                        c.startActivity(web);
                        c.finish();
                    }
                    break;
            }
            return null;
        }

        public static java.lang.String readableFileSize(long size) {
            if (size <= 0)
                return "0";

            final java.lang.String[] units = new java.lang.String[]{ "B", "kB", "MB", "GB", "TB" };
            int digitGroups = ((int) (java.lang.Math.log10(size) / java.lang.Math.log10(1024)));
            return (new java.text.DecimalFormat("#,##0.#").format(size / java.lang.Math.pow(1024, digitGroups)) + " ") + units[digitGroups];
        }

        public static void getRemoteFileSize(java.lang.String url, okhttp3.OkHttpClient client, final android.widget.TextView sizeText, android.app.Activity c) {
            okhttp3.Request request = new okhttp3.Request.Builder().url(url).head().build();
            okhttp3.Response response = null;
            try {
                response = client.newCall(request).execute();
                final long size = response.body().contentLength();
                response.close();
                c.runOnUiThread(new java.lang.Runnable() {
                    @java.lang.Override
                    public void run() {
                        sizeText.setText(me.ccrama.redditslide.util.GifUtils.AsyncLoadGif.readableFileSize(size));
                    }
                });
                return;
            } catch (java.io.IOException e) {
                if (response != null) {
                    response.close();
                }
                e.printStackTrace();
            }
            c.runOnUiThread(new java.lang.Runnable() {
                @java.lang.Override
                public void run() {
                    sizeText.setVisibility(android.view.View.GONE);
                }
            });
        }

        public void writeGif(final java.net.URL url, final android.widget.ProgressBar progressBar, final android.app.Activity c, final java.lang.String subreddit) {
            if (((size != null) && (c != null)) && (!me.ccrama.redditslide.util.GifUtils.getProxy().isCached(url.toString()))) {
                me.ccrama.redditslide.util.GifUtils.AsyncLoadGif.getRemoteFileSize(url.toString(), client, size, c);
            }
            c.runOnUiThread(new java.lang.Runnable() {
                @java.lang.Override
                public void run() {
                    java.lang.String toLoad = me.ccrama.redditslide.util.GifUtils.getProxy().getProxyUrl(url.toString());
                    video.setVideoPath(toLoad);
                    video.setOnPreparedListener(new com.devbrackets.android.exomedia.listener.OnPreparedListener() {
                        @java.lang.Override
                        public void onPrepared() {
                            if (placeholder != null)
                                placeholder.setVisibility(android.view.View.GONE);

                            me.ccrama.redditslide.util.LogUtil.v("Prepared");
                        }
                    });
                    if (autostart) {
                        video.start();
                    }
                    if (me.ccrama.redditslide.util.GifUtils.getProxy().isCached(url.toString())) {
                        progressBar.setVisibility(android.view.View.GONE);
                        if (gifSave != null) {
                            gifSave.setOnClickListener(new android.view.View.OnClickListener() {
                                @java.lang.Override
                                public void onClick(android.view.View v) {
                                    me.ccrama.redditslide.util.GifUtils.saveGif(me.ccrama.redditslide.util.GifUtils.getProxy().getCacheFile(url.toString()), c, subreddit);
                                }
                            });
                        } else if (doOnClick != null) {
                            me.ccrama.redditslide.Activities.MediaView.doOnClick = new java.lang.Runnable() {
                                @java.lang.Override
                                public void run() {
                                    me.ccrama.redditslide.util.GifUtils.saveGif(me.ccrama.redditslide.util.GifUtils.getProxy().getCacheFile(url.toString()), c, subreddit);
                                }
                            };
                        }
                    } else {
                        me.ccrama.redditslide.util.GifUtils.getProxy().registerCacheListener(new com.danikula.videocache.CacheListener() {
                            @java.lang.Override
                            public void onCacheAvailable(final java.io.File cacheFile, final java.lang.String url, final int percent) {
                                if ((progressBar != null) && (c != null)) {
                                    progressBar.setProgress(percent);
                                    if (percent == 100) {
                                        progressBar.setVisibility(android.view.View.GONE);
                                        me.ccrama.redditslide.util.GifUtils.getProxy().unregisterCacheListener(this);
                                        if (size != null)
                                            size.setVisibility(android.view.View.GONE);

                                        if (gifSave != null) {
                                            gifSave.setOnClickListener(new android.view.View.OnClickListener() {
                                                @java.lang.Override
                                                public void onClick(android.view.View v) {
                                                    me.ccrama.redditslide.util.GifUtils.saveGif(me.ccrama.redditslide.util.GifUtils.getProxy().getCacheFile(url), c, subreddit);
                                                }
                                            });
                                        } else if (doOnClick != null) {
                                            me.ccrama.redditslide.Activities.MediaView.doOnClick = new java.lang.Runnable() {
                                                @java.lang.Override
                                                public void run() {
                                                    me.ccrama.redditslide.util.GifUtils.saveGif(me.ccrama.redditslide.util.GifUtils.getProxy().getCacheFile(url), c, subreddit);
                                                }
                                            };
                                        }
                                    }
                                }
                                if (percent == 100) {
                                    me.ccrama.redditslide.Activities.MediaView.didLoadGif = true;
                                }
                            }
                        }, url.toString());
                    }
                }
            });
        }

        public void WriteGifMuxed(final java.net.URL url, final android.widget.ProgressBar progressBar, final android.app.Activity c, final java.lang.String subreddit) {
            if (((size != null) && (c != null)) && (!me.ccrama.redditslide.util.GifUtils.getProxy().isCached(url.toString()))) {
                me.ccrama.redditslide.util.GifUtils.AsyncLoadGif.getRemoteFileSize(url.toString(), client, size, c);
            }
            java.io.File videoFile = me.ccrama.redditslide.util.GifUtils.getProxy().getCacheFile(url.toString());
            if (videoFile.length() <= 0) {
                try {
                    if (!videoFile.exists()) {
                        if (!videoFile.getParentFile().exists()) {
                            videoFile.getParentFile().mkdirs();
                        }
                        videoFile.createNewFile();
                    }
                    java.net.HttpURLConnection conv = ((java.net.HttpURLConnection) (url.openConnection()));
                    conv.setRequestMethod("GET");
                    // c.setDoOutput(true);
                    conv.connect();
                    java.lang.String downloadsPath = c.getCacheDir().getAbsolutePath();
                    java.lang.String fileName = "video.mp4";// temporary location for video

                    java.io.File videoOutput = new java.io.File(downloadsPath, fileName);
                    java.net.HttpURLConnection cona = ((java.net.HttpURLConnection) (new java.net.URL(url.toString().substring(0, url.toString().lastIndexOf("/") + 1) + "audio").openConnection()));
                    cona.setRequestMethod("GET");
                    if (!videoOutput.exists()) {
                        videoOutput.createNewFile();
                    }
                    java.io.FileOutputStream fos = new java.io.FileOutputStream(videoOutput);
                    java.io.InputStream is = conv.getInputStream();
                    int fileLength = conv.getContentLength() + cona.getContentLength();
                    byte[] data = new byte[4096];
                    long total = 0;
                    int count;
                    while ((count = is.read(data)) != (-1)) {
                        // allow canceling with back button
                        if (isCancelled()) {
                            is.close();
                        }
                        total += count;
                        // publishing the progress....
                        // only if total length is known
                        if (fileLength > 0) {
                            publishProgress(((int) ((total * 100) / fileLength)), url);
                        }
                        fos.write(data, 0, count);
                    } 
                    fos.close();
                    is.close();
                    // c.setDoOutput(true);
                    cona.connect();
                    java.lang.String fileNameAudio = "audio.mp4";// temporary location for audio

                    java.io.File audioOutput = new java.io.File(downloadsPath, fileNameAudio);
                    java.io.File muxedPath = new java.io.File(downloadsPath, "muxedvideo.mp4");
                    muxedPath.createNewFile();
                    if (!audioOutput.exists()) {
                        audioOutput.createNewFile();
                    }
                    fos = new java.io.FileOutputStream(audioOutput);
                    int stat = cona.getResponseCode();
                    if (stat != 403) {
                        java.io.InputStream isa = cona.getInputStream();
                        byte[] dataa = new byte[4096];
                        int counta;
                        while ((counta = isa.read(dataa)) != (-1)) {
                            // allow canceling with back button
                            if (isCancelled()) {
                                isa.close();
                            }
                            total += counta;
                            // publishing the progress....
                            // only if total length is known
                            if (fileLength > 0) {
                                publishProgress(((int) ((total * 100) / fileLength)), url);
                            }
                            fos.write(dataa, 0, counta);
                        } 
                        fos.close();
                        isa.close();
                        publishProgressInd();
                        me.ccrama.redditslide.util.GifUtils.mux(videoOutput.getAbsolutePath(), audioOutput.getAbsolutePath(), muxedPath.getAbsolutePath());
                        me.ccrama.redditslide.util.GifUtils.AsyncLoadGif.copy(muxedPath, videoFile);
                        new java.io.File(videoFile.getAbsolutePath() + ".a").createNewFile();
                        setMuteVisibility(true);
                    } else {
                        me.ccrama.redditslide.util.GifUtils.AsyncLoadGif.copy(videoOutput, videoFile);
                        // no audio!
                        setMuteVisibility(false);
                    }
                } catch (java.lang.Exception e) {
                    e.printStackTrace();
                }
            } else {
                java.io.File isAudio = new java.io.File(videoFile.getAbsolutePath() + ".a");
                if (isAudio.exists()) {
                    setMuteVisibility(true);
                }
            }
            c.runOnUiThread(new java.lang.Runnable() {
                @java.lang.Override
                public void run() {
                    java.lang.String toLoad = me.ccrama.redditslide.util.GifUtils.getProxy().getCacheFile(url.toString()).getAbsolutePath();
                    video.setVideoPath(toLoad);
                    video.setOnPreparedListener(new com.devbrackets.android.exomedia.listener.OnPreparedListener() {
                        @java.lang.Override
                        public void onPrepared() {
                            if (placeholder != null)
                                placeholder.setVisibility(android.view.View.GONE);

                            me.ccrama.redditslide.util.LogUtil.v("Prepared");
                        }
                    });
                    if (autostart) {
                        video.start();
                    }
                    progressBar.setVisibility(android.view.View.GONE);
                    if (gifSave != null) {
                        gifSave.setOnClickListener(new android.view.View.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.view.View v) {
                                me.ccrama.redditslide.util.GifUtils.saveGif(me.ccrama.redditslide.util.GifUtils.getProxy().getCacheFile(url.toString()), c, subreddit);
                            }
                        });
                    } else if (doOnClick != null) {
                        me.ccrama.redditslide.Activities.MediaView.doOnClick = new java.lang.Runnable() {
                            @java.lang.Override
                            public void run() {
                                me.ccrama.redditslide.util.GifUtils.saveGif(me.ccrama.redditslide.util.GifUtils.getProxy().getCacheFile(url.toString()), c, subreddit);
                                try {
                                    android.widget.Toast.makeText(c, c.getString(me.ccrama.redditslide.R.string.mediaview_notif_title), android.widget.Toast.LENGTH_SHORT).show();
                                } catch (java.lang.Exception ignored) {
                                }
                            }
                        };
                    }
                }
            });
        }

        private void publishProgressInd() {
            c.runOnUiThread(new java.lang.Runnable() {
                @java.lang.Override
                public void run() {
                    if ((progressBar != null) && (c != null)) {
                        progressBar.setVisibility(android.view.View.VISIBLE);
                        progressBar.setIndeterminate(true);
                    }
                }
            });
        }

        private void publishProgress(final int percent, final java.net.URL url) {
            c.runOnUiThread(new java.lang.Runnable() {
                @java.lang.Override
                public void run() {
                    if ((progressBar != null) && (c != null)) {
                        progressBar.setProgress(percent);
                        if (percent == 100) {
                            progressBar.setVisibility(android.view.View.GONE);
                            if (size != null)
                                size.setVisibility(android.view.View.GONE);

                        }
                    }
                    if (percent == 100) {
                        me.ccrama.redditslide.Activities.MediaView.didLoadGif = true;
                    }
                }
            });
        }

        // Code from https://stackoverflow.com/a/9293885/3697225
        public static void copy(java.io.File src, java.io.File dst) throws java.io.IOException {
            java.io.InputStream in = new java.io.FileInputStream(src);
            try {
                java.io.OutputStream out = new java.io.FileOutputStream(dst);
                try {
                    // Transfer bytes from in to out
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    } 
                } finally {
                    out.close();
                }
            } finally {
                in.close();
            }
        }
    }

    /**
     * Shows a ProgressBar in the UI. If this method is called from a non-main thread, it will run
     * the UI code on the main thread
     *
     * @param activity
     * 		The activity context to use to display the ProgressBar
     * @param progressBar
     * 		The ProgressBar to display
     * @param isIndeterminate
     * 		True to show an indeterminate ProgressBar, false otherwise
     */
    private static void showProgressBar(final android.app.Activity activity, final android.widget.ProgressBar progressBar, final boolean isIndeterminate) {
        if (activity == null)
            return;

        if (android.os.Looper.myLooper() == android.os.Looper.getMainLooper()) {
            // Current Thread is Main Thread.
            if (progressBar != null)
                progressBar.setIndeterminate(isIndeterminate);

        } else {
            activity.runOnUiThread(new java.lang.Runnable() {
                @java.lang.Override
                public void run() {
                    if (progressBar != null)
                        progressBar.setIndeterminate(isIndeterminate);

                }
            });
        }
    }

    public static com.danikula.videocache.HttpProxyCacheServer getProxy() {
        return me.ccrama.redditslide.Reddit.proxy;
    }

    public static boolean mux(java.lang.String videoFile, java.lang.String audioFile, java.lang.String outputFile) {
        com.googlecode.mp4parser.authoring.Movie video;
        try {
            new com.googlecode.mp4parser.authoring.container.mp4.MovieCreator();
            video = com.googlecode.mp4parser.authoring.container.mp4.MovieCreator.build(videoFile);
        } catch (java.lang.RuntimeException e) {
            e.printStackTrace();
            return false;
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return false;
        }
        com.googlecode.mp4parser.authoring.Movie audio;
        try {
            new com.googlecode.mp4parser.authoring.container.mp4.MovieCreator();
            audio = com.googlecode.mp4parser.authoring.container.mp4.MovieCreator.build(audioFile);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return false;
        } catch (java.lang.NullPointerException e) {
            e.printStackTrace();
            return false;
        }
        com.googlecode.mp4parser.authoring.Track audioTrack = audio.getTracks().get(0);
        com.googlecode.mp4parser.authoring.tracks.CroppedTrack croppedTrack = new com.googlecode.mp4parser.authoring.tracks.CroppedTrack(audioTrack, 0, audioTrack.getSamples().size());
        video.addTrack(croppedTrack);
        com.coremedia.iso.boxes.Container out = new com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder().build(video);
        java.io.FileOutputStream fos;
        try {
            fos = new java.io.FileOutputStream(outputFile);
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        me.ccrama.redditslide.util.GifUtils.BufferedWritableFileByteChannel byteBufferByteChannel = new me.ccrama.redditslide.util.GifUtils.BufferedWritableFileByteChannel(fos);
        try {
            out.writeContainer(byteBufferByteChannel);
            byteBufferByteChannel.close();
            fos.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static class BufferedWritableFileByteChannel implements java.nio.channels.WritableByteChannel {
        private static final int BUFFER_CAPACITY = 1000000;

        private boolean isOpen = true;

        private final java.io.OutputStream outputStream;

        private final java.nio.ByteBuffer byteBuffer;

        private final byte[] rawBuffer = new byte[me.ccrama.redditslide.util.GifUtils.BufferedWritableFileByteChannel.BUFFER_CAPACITY];

        private BufferedWritableFileByteChannel(java.io.OutputStream outputStream) {
            this.outputStream = outputStream;
            this.byteBuffer = java.nio.ByteBuffer.wrap(rawBuffer);
        }

        @java.lang.Override
        public int write(java.nio.ByteBuffer inputBuffer) {
            int inputBytes = inputBuffer.remaining();
            if (inputBytes > byteBuffer.remaining()) {
                dumpToFile();
                byteBuffer.clear();
                if (inputBytes > byteBuffer.remaining()) {
                    throw new java.nio.BufferOverflowException();
                }
            }
            byteBuffer.put(inputBuffer);
            return inputBytes;
        }

        @java.lang.Override
        public boolean isOpen() {
            return isOpen;
        }

        @java.lang.Override
        public void close() {
            dumpToFile();
            isOpen = false;
        }

        private void dumpToFile() {
            try {
                outputStream.write(rawBuffer, 0, byteBuffer.position());
            } catch (java.io.IOException e) {
                throw new java.lang.RuntimeException(e);
            }
        }
    }

    public static final java.lang.String AUDIO_RECORDING_FILE_NAME = "audio_Capturing-190814-034638.422.wav";// Input PCM file


    public static final java.lang.String COMPRESSED_AUDIO_FILE_NAME = "convertedmp4.m4a";

    // Output MP4/M4A file
    public static final java.lang.String COMPRESSED_AUDIO_FILE_MIME_TYPE = "audio/mp4a-latm";

    public static final int COMPRESSED_AUDIO_FILE_BIT_RATE = 64000;// 64kbps


    public static final int SAMPLING_RATE = 48000;

    public static final int BUFFER_SIZE = 48000;

    public static final int CODEC_TIMEOUT_IN_MS = 5000;

    java.lang.String LOGTAG = "CONVERT AUDIO";

    java.lang.Runnable convert = new java.lang.Runnable() {
        @android.annotation.TargetApi(android.os.Build.VERSION_CODES.JELLY_BEAN_MR2)
        @java.lang.Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            try {
                java.lang.String filePath = (android.os.Environment.getExternalStorageDirectory().getPath() + "/") + me.ccrama.redditslide.util.GifUtils.AUDIO_RECORDING_FILE_NAME;
                java.io.File inputFile = new java.io.File(filePath);
                java.io.FileInputStream fis = new java.io.FileInputStream(inputFile);
                java.io.File outputFile = new java.io.File((android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/") + me.ccrama.redditslide.util.GifUtils.COMPRESSED_AUDIO_FILE_NAME);
                if (outputFile.exists())
                    outputFile.delete();

                android.media.MediaMuxer mux = new android.media.MediaMuxer(outputFile.getAbsolutePath(), android.media.MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                android.media.MediaFormat outputFormat = android.media.MediaFormat.createAudioFormat(me.ccrama.redditslide.util.GifUtils.COMPRESSED_AUDIO_FILE_MIME_TYPE, me.ccrama.redditslide.util.GifUtils.SAMPLING_RATE, 1);
                outputFormat.setInteger(android.media.MediaFormat.KEY_AAC_PROFILE, android.media.MediaCodecInfo.CodecProfileLevel.AACObjectLC);
                outputFormat.setInteger(android.media.MediaFormat.KEY_BIT_RATE, me.ccrama.redditslide.util.GifUtils.COMPRESSED_AUDIO_FILE_BIT_RATE);
                outputFormat.setInteger(android.media.MediaFormat.KEY_MAX_INPUT_SIZE, 16384);
                android.media.MediaCodec codec = android.media.MediaCodec.createEncoderByType(me.ccrama.redditslide.util.GifUtils.COMPRESSED_AUDIO_FILE_MIME_TYPE);
                codec.configure(outputFormat, null, null, android.media.MediaCodec.CONFIGURE_FLAG_ENCODE);
                codec.start();
                java.nio.ByteBuffer[] codecInputBuffers = codec.getInputBuffers();// Note: Array of buffers

                java.nio.ByteBuffer[] codecOutputBuffers = codec.getOutputBuffers();
                android.media.MediaCodec.BufferInfo outBuffInfo = new android.media.MediaCodec.BufferInfo();
                byte[] tempBuffer = new byte[me.ccrama.redditslide.util.GifUtils.BUFFER_SIZE];
                boolean hasMoreData = true;
                double presentationTimeUs = 0;
                int audioTrackIdx = 0;
                int totalBytesRead = 0;
                int percentComplete = 0;
                do {
                    int inputBufIndex = 0;
                    while ((inputBufIndex != (-1)) && hasMoreData) {
                        inputBufIndex = codec.dequeueInputBuffer(me.ccrama.redditslide.util.GifUtils.CODEC_TIMEOUT_IN_MS);
                        if (inputBufIndex >= 0) {
                            java.nio.ByteBuffer dstBuf = codecInputBuffers[inputBufIndex];
                            dstBuf.clear();
                            int bytesRead = fis.read(tempBuffer, 0, dstBuf.limit());
                            android.util.Log.e("bytesRead", "Readed " + bytesRead);
                            if (bytesRead == (-1)) {
                                // -1 implies EOS
                                hasMoreData = false;
                                codec.queueInputBuffer(inputBufIndex, 0, 0, ((long) (presentationTimeUs)), android.media.MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                            } else {
                                totalBytesRead += bytesRead;
                                dstBuf.put(tempBuffer, 0, bytesRead);
                                codec.queueInputBuffer(inputBufIndex, 0, bytesRead, ((long) (presentationTimeUs)), 0);
                                presentationTimeUs = (1000000L * (totalBytesRead / 2)) / me.ccrama.redditslide.util.GifUtils.SAMPLING_RATE;
                            }
                        }
                    } 
                    // Drain audio
                    int outputBufIndex = 0;
                    while (outputBufIndex != android.media.MediaCodec.INFO_TRY_AGAIN_LATER) {
                        outputBufIndex = codec.dequeueOutputBuffer(outBuffInfo, me.ccrama.redditslide.util.GifUtils.CODEC_TIMEOUT_IN_MS);
                        if (outputBufIndex >= 0) {
                            java.nio.ByteBuffer encodedData = codecOutputBuffers[outputBufIndex];
                            encodedData.position(outBuffInfo.offset);
                            encodedData.limit(outBuffInfo.offset + outBuffInfo.size);
                            if (((outBuffInfo.flags & android.media.MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) && (outBuffInfo.size != 0)) {
                                codec.releaseOutputBuffer(outputBufIndex, false);
                            } else {
                                mux.writeSampleData(audioTrackIdx, codecOutputBuffers[outputBufIndex], outBuffInfo);
                                codec.releaseOutputBuffer(outputBufIndex, false);
                            }
                        } else if (outputBufIndex == android.media.MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                            outputFormat = codec.getOutputFormat();
                            android.util.Log.v(LOGTAG, "Output format changed - " + outputFormat);
                            audioTrackIdx = mux.addTrack(outputFormat);
                            mux.start();
                        } else if (outputBufIndex == android.media.MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                            android.util.Log.e(LOGTAG, "Output buffers changed during encode!");
                        } else if (outputBufIndex == android.media.MediaCodec.INFO_TRY_AGAIN_LATER) {
                            // NO OP
                        } else {
                            android.util.Log.e(LOGTAG, "Unknown return code from dequeueOutputBuffer - " + outputBufIndex);
                        }
                    } 
                    percentComplete = ((int) (java.lang.Math.round((((float) (totalBytesRead)) / ((float) (inputFile.length()))) * 100.0)));
                    android.util.Log.v(LOGTAG, "Conversion % - " + percentComplete);
                } while (outBuffInfo.flags != android.media.MediaCodec.BUFFER_FLAG_END_OF_STREAM );
                fis.close();
                mux.stop();
                mux.release();
                android.util.Log.v(LOGTAG, "Compression done ...");
            } catch (java.io.FileNotFoundException e) {
                android.util.Log.e(LOGTAG, "File not found!", e);
            } catch (java.io.IOException e) {
                android.util.Log.e(LOGTAG, "IO exception!", e);
            }
            // mStop = false;
            // Notify UI thread...
        }
    };
}