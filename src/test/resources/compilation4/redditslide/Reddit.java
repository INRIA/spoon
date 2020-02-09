package me.ccrama.redditslide;
import com.google.common.collect.HashBiMap;
import java.net.Inet4Address;
import com.google.common.collect.BiMap;
import me.ccrama.redditslide.Autocache.AutoCacheScheduler;
import me.ccrama.redditslide.Visuals.Palette;
import java.net.UnknownHostException;
import me.ccrama.redditslide.Notifications.NotificationJobScheduler;
import me.ccrama.redditslide.Tumblr.TumblrUtils;
import me.ccrama.redditslide.Activities.MainActivity;
import java.lang.ref.WeakReference;
import org.apache.commons.lang3.tuple.Triple;
import me.ccrama.redditslide.util.*;
import java.io.StringWriter;
import me.ccrama.redditslide.ImgurAlbum.AlbumUtils;
import me.ccrama.redditslide.Notifications.NotificationPiggyback;
import java.io.Writer;
import java.net.InetAddress;
import java.io.PrintWriter;
/**
 * Created by ccrama on 9/17/2015.
 */
public class Reddit extends android.support.multidex.MultiDexApplication implements android.app.Application.ActivityLifecycleCallbacks {
    private static android.app.Application mApplication;

    public static final java.lang.String EMPTY_STRING = "NOTHING";

    public static final long enter_animation_time_original = 600;

    public static final java.lang.String PREF_LAYOUT = "PRESET";

    public static final java.lang.String SHARED_PREF_IS_MOD = "is_mod";

    public static com.danikula.videocache.HttpProxyCacheServer proxy;

    public static me.ccrama.redditslide.util.IabHelper mHelper;

    public static long enter_animation_time = me.ccrama.redditslide.Reddit.enter_animation_time_original;

    public static final int enter_animation_time_multiplier = 1;

    public static me.ccrama.redditslide.Authentication authentication;

    public static android.content.SharedPreferences colors;

    public static android.content.SharedPreferences appRestart;

    public static android.content.SharedPreferences tags;

    public static int dpWidth;

    public static int notificationTime;

    public static boolean videoPlugin;

    public static me.ccrama.redditslide.Notifications.NotificationJobScheduler notifications;

    public static boolean isLoading = false;

    public static final long time = java.lang.System.currentTimeMillis();

    public static boolean fabClear;

    public static java.util.ArrayList<java.lang.Integer> lastPosition;

    public static int currentPosition;

    public static android.content.SharedPreferences cachedData;

    public static final boolean noGapps = true;// for testing


    public static boolean overrideLanguage;

    public static boolean isRestarting;

    public static me.ccrama.redditslide.Autocache.AutoCacheScheduler autoCache;

    public static boolean peek;

    public boolean active;

    private com.nostra13.universalimageloader.core.ImageLoader defaultImageLoader;

    public static okhttp3.OkHttpClient client;

    public static boolean canUseNightModeAuto = false;

    public static void forceRestart(android.content.Context context, boolean forceLoadScreen) {
        if (forceLoadScreen) {
            me.ccrama.redditslide.Reddit.appRestart.edit().putString("startScreen", "").apply();
            me.ccrama.redditslide.Reddit.appRestart.edit().putBoolean("isRestarting", true).apply();
        }
        if (me.ccrama.redditslide.Reddit.appRestart.contains("back")) {
            me.ccrama.redditslide.Reddit.appRestart.edit().remove("back").apply();
        }
        me.ccrama.redditslide.Reddit.appRestart.edit().putBoolean("isRestarting", true).apply();
        me.ccrama.redditslide.Reddit.isRestarting = true;
        com.jakewharton.processphoenix.ProcessPhoenix.triggerRebirth(context, new android.content.Intent(context, me.ccrama.redditslide.Activities.MainActivity.class));
    }

    private static int dpToPx(int dp, float xy) {
        return java.lang.Math.round((dp * xy) / android.util.DisplayMetrics.DENSITY_DEFAULT);
    }

    public static int dpToPxVertical(int dp) {
        return me.ccrama.redditslide.Reddit.dpToPx(dp, android.content.res.Resources.getSystem().getDisplayMetrics().ydpi);
    }

    public static int dpToPxHorizontal(int dp) {
        return me.ccrama.redditslide.Reddit.dpToPx(dp, android.content.res.Resources.getSystem().getDisplayMetrics().xdpi);
    }

    public static void defaultShareText(java.lang.String title, java.lang.String url, android.content.Context c) {
        url = org.apache.commons.text.StringEscapeUtils.unescapeHtml4(android.text.Html.fromHtml(url).toString());
        android.content.Intent sharingIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        /* Decode html entities */
        title = org.apache.commons.text.StringEscapeUtils.unescapeHtml4(title);
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, url);
        c.startActivity(android.content.Intent.createChooser(sharingIntent, c.getString(me.ccrama.redditslide.R.string.title_share)));
    }

    public static boolean isPackageInstalled(java.lang.String s) {
        try {
            final android.content.pm.PackageInfo pi = me.ccrama.redditslide.Reddit.getAppContext().getPackageManager().getPackageInfo(s, 0);
            if ((pi != null) && pi.applicationInfo.enabled)
                return true;

        } catch (final java.lang.Throwable ignored) {
        }
        return false;
    }

    private static boolean isProPackageInstalled() {
        return me.ccrama.redditslide.Reddit.isPackageInstalled(me.ccrama.redditslide.Reddit.getAppContext().getString(me.ccrama.redditslide.R.string.ui_unlock_package));
    }

    private static boolean isVideoPluginInstalled() {
        return me.ccrama.redditslide.Reddit.isPackageInstalled(me.ccrama.redditslide.Reddit.getAppContext().getString(me.ccrama.redditslide.R.string.youtube_plugin_package));
    }

    public static com.google.common.collect.BiMap<java.lang.String, java.lang.String> getInstalledBrowsers() {
        int packageMatcher = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) ? android.content.pm.PackageManager.MATCH_ALL : android.content.pm.PackageManager.GET_DISABLED_COMPONENTS;
        final com.google.common.collect.BiMap<java.lang.String, java.lang.String> browserMap = com.google.common.collect.HashBiMap.create();
        final java.util.List<android.content.pm.ResolveInfo> resolveInfoList = me.ccrama.redditslide.Reddit.getAppContext().getPackageManager().queryIntentActivities(new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("http://ccrama.me")), packageMatcher);
        for (android.content.pm.ResolveInfo resolveInfo : resolveInfoList) {
            if (resolveInfo.activityInfo.enabled) {
                browserMap.put(resolveInfo.activityInfo.applicationInfo.packageName, me.ccrama.redditslide.Reddit.getAppContext().getPackageManager().getApplicationLabel(resolveInfo.activityInfo.applicationInfo).toString());
            }
        }
        return browserMap;
    }

    public static java.lang.String arrayToString(java.util.ArrayList<java.lang.String> array) {
        if (array != null) {
            java.lang.StringBuilder b = new java.lang.StringBuilder();
            for (java.lang.String s : array) {
                b.append(s).append(",");
            }
            java.lang.String f = b.toString();
            if (f.length() > 0) {
                f = f.substring(0, f.length() - 1);
            }
            return f;
        } else {
            return "";
        }
    }

    public static java.lang.String arrayToString(java.util.ArrayList<java.lang.String> array, java.lang.String separator) {
        if (array != null) {
            java.lang.StringBuilder b = new java.lang.StringBuilder();
            for (java.lang.String s : array) {
                b.append(s).append(separator);
            }
            java.lang.String f = b.toString();
            if (f.length() > 0) {
                f = f.substring(0, f.length() - separator.length());
            }
            return f;
        } else {
            return "";
        }
    }

    public static java.util.ArrayList<java.lang.String> stringToArray(java.lang.String string) {
        java.util.ArrayList<java.lang.String> f = new java.util.ArrayList<>();
        java.util.Collections.addAll(f, string.split(","));
        return f;
    }

    @java.lang.Override
    public void onLowMemory() {
        super.onLowMemory();
        getImageLoader().clearMemoryCache();
    }

    public com.nostra13.universalimageloader.core.ImageLoader getImageLoader() {
        if ((defaultImageLoader == null) || (!defaultImageLoader.isInited())) {
            me.ccrama.redditslide.ImageLoaderUtils.initImageLoader(getApplicationContext());
            defaultImageLoader = me.ccrama.redditslide.ImageLoaderUtils.imageLoader;
        }
        return defaultImageLoader;
    }

    public static boolean notFirst = false;

    @java.lang.Override
    public void onActivityResumed(android.app.Activity activity) {
        doLanguages();
        if (me.ccrama.redditslide.Reddit.client == null) {
            okhttp3.OkHttpClient.Builder builder = new okhttp3.OkHttpClient.Builder();
            builder.dns(new me.ccrama.redditslide.Reddit.GfycatIpv4Dns());
            me.ccrama.redditslide.Reddit.client = builder.build();
        }
        if (((me.ccrama.redditslide.Reddit.authentication != null) && me.ccrama.redditslide.Authentication.didOnline) && (me.ccrama.redditslide.Authentication.authentication.getLong("expires", 0) <= java.util.Calendar.getInstance().getTimeInMillis())) {
            me.ccrama.redditslide.Reddit.authentication.updateToken(activity);
        } else if (me.ccrama.redditslide.util.NetworkUtil.isConnected(activity) && (me.ccrama.redditslide.Reddit.authentication == null)) {
            me.ccrama.redditslide.Reddit.authentication = new me.ccrama.redditslide.Authentication(this);
        }
    }

    @java.lang.Override
    public void onActivityPaused(android.app.Activity activity) {
    }

    public static void setDefaultErrorHandler(android.content.Context base) {
        // START code adapted from https://github.com/QuantumBadger/RedReader/
        final java.lang.Thread.UncaughtExceptionHandler androidHandler = java.lang.Thread.getDefaultUncaughtExceptionHandler();
        final java.lang.ref.WeakReference<android.content.Context> cont = new java.lang.ref.WeakReference<>(base);
        java.lang.Thread.setDefaultUncaughtExceptionHandler(new java.lang.Thread.UncaughtExceptionHandler() {
            public void uncaughtException(java.lang.Thread thread, java.lang.Throwable t) {
                if (cont.get() != null) {
                    final android.content.Context c = cont.get();
                    java.io.Writer writer = new java.io.StringWriter();
                    java.io.PrintWriter printWriter = new java.io.PrintWriter(writer);
                    t.printStackTrace(printWriter);
                    java.lang.String stacktrace = writer.toString().replace(";", ",");
                    if ((stacktrace.contains("UnknownHostException") || stacktrace.contains("SocketTimeoutException")) || stacktrace.contains("ConnectException")) {
                        // is offline
                        final android.os.Handler mHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                        mHandler.post(new java.lang.Runnable() {
                            @java.lang.Override
                            public void run() {
                                try {
                                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(c).setTitle(me.ccrama.redditslide.R.string.err_title).setMessage(me.ccrama.redditslide.R.string.err_connection_failed_msg).setNegativeButton(me.ccrama.redditslide.R.string.btn_close, new android.content.DialogInterface.OnClickListener() {
                                        @java.lang.Override
                                        public void onClick(android.content.DialogInterface dialog, int which) {
                                            if (!(c instanceof me.ccrama.redditslide.Activities.MainActivity)) {
                                                ((android.app.Activity) (c)).finish();
                                            }
                                        }
                                    }).setPositiveButton(me.ccrama.redditslide.R.string.btn_offline, new android.content.DialogInterface.OnClickListener() {
                                        @java.lang.Override
                                        public void onClick(android.content.DialogInterface dialog, int which) {
                                            me.ccrama.redditslide.Reddit.appRestart.edit().putBoolean("forceoffline", true).apply();
                                            me.ccrama.redditslide.Reddit.forceRestart(c, false);
                                        }
                                    }).show();
                                } catch (java.lang.Exception ignored) {
                                }
                            }
                        });
                    } else if (stacktrace.contains("403 Forbidden") || stacktrace.contains("401 Unauthorized")) {
                        // Un-authenticated
                        final android.os.Handler mHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                        mHandler.post(new java.lang.Runnable() {
                            @java.lang.Override
                            public void run() {
                                try {
                                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(c).setTitle(me.ccrama.redditslide.R.string.err_title).setMessage(me.ccrama.redditslide.R.string.err_refused_request_msg).setNegativeButton("No", new android.content.DialogInterface.OnClickListener() {
                                        @java.lang.Override
                                        public void onClick(android.content.DialogInterface dialog, int which) {
                                            if (!(c instanceof me.ccrama.redditslide.Activities.MainActivity)) {
                                                ((android.app.Activity) (c)).finish();
                                            }
                                        }
                                    }).setPositiveButton("Yes", new android.content.DialogInterface.OnClickListener() {
                                        @java.lang.Override
                                        public void onClick(android.content.DialogInterface dialog, int which) {
                                            me.ccrama.redditslide.Reddit.authentication.updateToken(c);
                                        }
                                    }).show();
                                } catch (java.lang.Exception ignored) {
                                }
                            }
                        });
                    } else if (stacktrace.contains("404 Not Found") || stacktrace.contains("400 Bad Request")) {
                        final android.os.Handler mHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                        mHandler.post(new java.lang.Runnable() {
                            @java.lang.Override
                            public void run() {
                                try {
                                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(c).setTitle(me.ccrama.redditslide.R.string.err_title).setMessage(me.ccrama.redditslide.R.string.err_could_not_find_content_msg).setNegativeButton("Close", new android.content.DialogInterface.OnClickListener() {
                                        @java.lang.Override
                                        public void onClick(android.content.DialogInterface dialog, int which) {
                                            if (!(c instanceof me.ccrama.redditslide.Activities.MainActivity)) {
                                                ((android.app.Activity) (c)).finish();
                                            }
                                        }
                                    }).show();
                                } catch (java.lang.Exception ignored) {
                                }
                            }
                        });
                    } else if (t instanceof net.dean.jraw.http.NetworkException) {
                        android.widget.Toast.makeText(c, (("Error " + ((net.dean.jraw.http.NetworkException) (t)).getResponse().getStatusMessage()) + ": ") + t.getMessage(), android.widget.Toast.LENGTH_LONG).show();
                    } else if ((t instanceof java.lang.NullPointerException) && t.getMessage().contains("Attempt to invoke virtual method 'android.content.Context android.view.ViewGroup.getContext()' on a null object reference")) {
                        t.printStackTrace();
                    } else if (t instanceof com.afollestad.materialdialogs.MaterialDialog.DialogException) {
                        t.printStackTrace();
                    } else if ((t instanceof java.lang.IllegalArgumentException) && t.getMessage().contains("pointerIndex out of range")) {
                        t.printStackTrace();
                    } else {
                        me.ccrama.redditslide.Reddit.appRestart.edit().putString("startScreen", "a").apply();// Force reload of data after crash incase state was not saved

                        try {
                            android.content.SharedPreferences prefs = c.getSharedPreferences("STACKTRACE", android.content.Context.MODE_PRIVATE);
                            prefs.edit().putString("stacktrace", stacktrace).apply();
                        } catch (java.lang.Throwable ignored) {
                        }
                        androidHandler.uncaughtException(thread, t);
                    }
                } else {
                    androidHandler.uncaughtException(thread, t);
                }
            }
        });
        // END adaptation
    }

    @java.lang.Override
    public void onActivityStopped(android.app.Activity activity) {
    }

    @java.lang.Override
    public void onActivityCreated(android.app.Activity activity, android.os.Bundle savedInstanceState) {
        doLanguages();
    }

    @java.lang.Override
    public void onActivityStarted(android.app.Activity activity) {
    }

    @java.lang.Override
    public void onActivitySaveInstanceState(android.app.Activity activity, android.os.Bundle outState) {
    }

    @java.lang.Override
    public void onActivityDestroyed(android.app.Activity activity) {
    }

    @java.lang.Override
    public void onCreate() {
        super.onCreate();
        me.ccrama.redditslide.Reddit.mApplication = this;
        // LeakCanary.install(this);
        if (com.jakewharton.processphoenix.ProcessPhoenix.isPhoenixProcess(this)) {
            return;
        }
        me.ccrama.redditslide.Reddit.proxy = new com.danikula.videocache.HttpProxyCacheServer.Builder(this).maxCacheSize(5 * 1024).maxCacheFilesCount(20).build();
        me.ccrama.redditslide.util.UpgradeUtil.upgrade(getApplicationContext());
        doMainStuff();
    }

    public void doMainStuff() {
        android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "ON CREATED AGAIN");
        if (me.ccrama.redditslide.Reddit.client == null) {
            me.ccrama.redditslide.Reddit.client = new okhttp3.OkHttpClient();
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            me.ccrama.redditslide.Reddit.setCanUseNightModeAuto();
        }
        me.ccrama.redditslide.Reddit.overrideLanguage = getSharedPreferences("SETTINGS", 0).getBoolean(me.ccrama.redditslide.SettingValues.PREF_OVERRIDE_LANGUAGE, false);
        me.ccrama.redditslide.Reddit.appRestart = getSharedPreferences("appRestart", 0);
        me.ccrama.redditslide.ImgurAlbum.AlbumUtils.albumRequests = getSharedPreferences("albums", 0);
        me.ccrama.redditslide.Tumblr.TumblrUtils.tumblrRequests = getSharedPreferences("tumblr", 0);
        me.ccrama.redditslide.Reddit.cachedData = getSharedPreferences("cache", 0);
        if (!me.ccrama.redditslide.Reddit.cachedData.contains("hasReset")) {
            me.ccrama.redditslide.Reddit.cachedData.edit().clear().putBoolean("hasReset", true).apply();
        }
        registerActivityLifecycleCallbacks(this);
        me.ccrama.redditslide.Authentication.authentication = getSharedPreferences("AUTH", 0);
        me.ccrama.redditslide.UserSubscriptions.subscriptions = getSharedPreferences("SUBSNEW", 0);
        me.ccrama.redditslide.UserSubscriptions.multiNameToSubs = getSharedPreferences("MULTITONAME", 0);
        me.ccrama.redditslide.UserSubscriptions.newsNameToSubs = getSharedPreferences("NEWSMULTITONAME", 0);
        me.ccrama.redditslide.UserSubscriptions.news = getSharedPreferences("NEWS", 0);
        me.ccrama.redditslide.UserSubscriptions.newsNameToSubs.edit().putString("android", "android+androidapps+googlepixel").putString("news", "worldnews+news+politics").apply();
        me.ccrama.redditslide.UserSubscriptions.pinned = getSharedPreferences("PINNED", 0);
        me.ccrama.redditslide.PostMatch.filters = getSharedPreferences("FILTERS", 0);
        me.ccrama.redditslide.ImageFlairs.flairs = getSharedPreferences("FLAIRS", 0);
        me.ccrama.redditslide.SettingValues.setAllValues(getSharedPreferences("SETTINGS", 0));
        me.ccrama.redditslide.util.SortingUtil.defaultSorting = me.ccrama.redditslide.SettingValues.defaultSorting;
        me.ccrama.redditslide.util.SortingUtil.timePeriod = me.ccrama.redditslide.SettingValues.timePeriod;
        me.ccrama.redditslide.Reddit.colors = getSharedPreferences("COLOR", 0);
        me.ccrama.redditslide.Reddit.tags = getSharedPreferences("TAGS", 0);
        com.lusfold.androidkeyvaluestore.KVStore.init(this, "SEEN");
        doLanguages();
        me.ccrama.redditslide.Reddit.lastPosition = new java.util.ArrayList<>();
        if (me.ccrama.redditslide.BuildConfig.FLAVOR == "withGPlay") {
            new me.ccrama.redditslide.Reddit.SetupIAB().execute();
        }
        if (!me.ccrama.redditslide.Reddit.appRestart.contains("startScreen")) {
            me.ccrama.redditslide.Authentication.isLoggedIn = me.ccrama.redditslide.Reddit.appRestart.getBoolean("loggedin", false);
            me.ccrama.redditslide.Authentication.name = me.ccrama.redditslide.Reddit.appRestart.getString("name", "LOGGEDOUT");
            active = true;
        } else {
            me.ccrama.redditslide.Reddit.appRestart.edit().remove("startScreen").apply();
        }
        me.ccrama.redditslide.Reddit.authentication = new me.ccrama.redditslide.Authentication(this);
        me.ccrama.redditslide.util.AdBlocker.init(this);
        me.ccrama.redditslide.Authentication.mod = me.ccrama.redditslide.Authentication.authentication.getBoolean(me.ccrama.redditslide.Reddit.SHARED_PREF_IS_MOD, false);
        me.ccrama.redditslide.Reddit.enter_animation_time = me.ccrama.redditslide.Reddit.enter_animation_time_original * me.ccrama.redditslide.Reddit.enter_animation_time_multiplier;
        me.ccrama.redditslide.Reddit.fabClear = me.ccrama.redditslide.Reddit.colors.getBoolean(me.ccrama.redditslide.SettingValues.PREF_FAB_CLEAR, false);
        int widthDp = this.getResources().getConfiguration().screenWidthDp;
        int heightDp = this.getResources().getConfiguration().screenHeightDp;
        int fina = (widthDp > heightDp) ? widthDp : heightDp;
        fina += 99;
        if (me.ccrama.redditslide.Reddit.colors.contains("tabletOVERRIDE")) {
            me.ccrama.redditslide.Reddit.dpWidth = me.ccrama.redditslide.Reddit.colors.getInt("tabletOVERRIDE", fina / 300);
        } else {
            me.ccrama.redditslide.Reddit.dpWidth = fina / 300;
        }
        if (me.ccrama.redditslide.Reddit.colors.contains("notificationOverride")) {
            me.ccrama.redditslide.Reddit.notificationTime = me.ccrama.redditslide.Reddit.colors.getInt("notificationOverride", 360);
        } else {
            me.ccrama.redditslide.Reddit.notificationTime = 360;
        }
        me.ccrama.redditslide.SettingValues.isPro = me.ccrama.redditslide.Reddit.isProPackageInstalled() || me.ccrama.redditslide.FDroid.isFDroid;
        me.ccrama.redditslide.Reddit.videoPlugin = me.ccrama.redditslide.Reddit.isVideoPluginInstalled();
        me.ccrama.redditslide.util.GifCache.init(this);
        setupNotificationChannels();
    }

    public void doLanguages() {
        if (me.ccrama.redditslide.SettingValues.overrideLanguage) {
            java.util.Locale locale = new java.util.Locale("en_US");
            java.util.Locale.setDefault(locale);
            android.content.res.Configuration config = getResources().getConfiguration();
            config.locale = locale;
            getResources().updateConfiguration(config, null);
        }
    }

    public boolean isNotificationAccessEnabled() {
        android.app.ActivityManager manager = ((android.app.ActivityManager) (getSystemService(android.content.Context.ACTIVITY_SERVICE)));
        if (manager != null) {
            for (android.app.ActivityManager.RunningServiceInfo service : manager.getRunningServices(java.lang.Integer.MAX_VALUE)) {
                if (me.ccrama.redditslide.Notifications.NotificationPiggyback.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static final java.lang.String CHANNEL_IMG = "IMG_DOWNLOADS";

    public static final java.lang.String CHANNEL_COMMENT_CACHE = "POST_SYNC";

    public static final java.lang.String CHANNEL_MAIL = "MAIL_NOTIFY";

    public static final java.lang.String CHANNEL_MODMAIL = "MODMAIL_NOTIFY";

    public static final java.lang.String CHANNEL_SUBCHECKING = "SUB_CHECK_NOTIFY";

    public void setupNotificationChannels() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Each triple contains the channel ID, name, and importance level
            java.util.List<org.apache.commons.lang3.tuple.Triple<java.lang.String, java.lang.String, java.lang.Integer>> notificationTripleList = new java.util.ArrayList<org.apache.commons.lang3.tuple.Triple<java.lang.String, java.lang.String, java.lang.Integer>>() {
                {
                    add(org.apache.commons.lang3.tuple.Triple.of(me.ccrama.redditslide.Reddit.CHANNEL_IMG, "Image downloads", android.app.NotificationManager.IMPORTANCE_LOW));
                    add(org.apache.commons.lang3.tuple.Triple.of(me.ccrama.redditslide.Reddit.CHANNEL_COMMENT_CACHE, "Comment caching", android.app.NotificationManager.IMPORTANCE_LOW));
                    add(org.apache.commons.lang3.tuple.Triple.of(me.ccrama.redditslide.Reddit.CHANNEL_MAIL, "Reddit mail", android.app.NotificationManager.IMPORTANCE_HIGH));
                    add(org.apache.commons.lang3.tuple.Triple.of(me.ccrama.redditslide.Reddit.CHANNEL_MODMAIL, "Reddit modmail", android.app.NotificationManager.IMPORTANCE_HIGH));
                    add(org.apache.commons.lang3.tuple.Triple.of(me.ccrama.redditslide.Reddit.CHANNEL_SUBCHECKING, "Submission post checking", android.app.NotificationManager.IMPORTANCE_LOW));
                }
            };
            android.app.NotificationManager notificationManager = ((android.app.NotificationManager) (getSystemService(android.content.Context.NOTIFICATION_SERVICE)));
            for (org.apache.commons.lang3.tuple.Triple<java.lang.String, java.lang.String, java.lang.Integer> notificationTriple : notificationTripleList) {
                android.app.NotificationChannel notificationChannel = new android.app.NotificationChannel(notificationTriple.getLeft(), notificationTriple.getMiddle(), notificationTriple.getRight());
                notificationChannel.enableLights(true);
                notificationChannel.setShowBadge(notificationTriple.getRight() == android.app.NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setLightColor(notificationTriple.getLeft().contains("MODMAIL") ? getResources().getColor(me.ccrama.redditslide.R.color.md_red_500, null) : me.ccrama.redditslide.Visuals.Palette.getColor(""));
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(notificationChannel);
                }
            }
        }
    }

    private static class SetupIAB extends android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void> {
        @java.lang.Override
        protected java.lang.Void doInBackground(java.lang.Void... params) {
            if (me.ccrama.redditslide.Reddit.mHelper == null) {
                try {
                    me.ccrama.redditslide.Reddit.mHelper = new me.ccrama.redditslide.util.IabHelper(me.ccrama.redditslide.Reddit.getAppContext(), me.ccrama.redditslide.SecretConstants.getBase64EncodedPublicKey(me.ccrama.redditslide.Reddit.getAppContext()));
                    me.ccrama.redditslide.Reddit.mHelper.startSetup(new me.ccrama.redditslide.util.IabHelper.OnIabSetupFinishedListener() {
                        public void onIabSetupFinished(me.ccrama.redditslide.util.IabResult result) {
                            if (!result.isSuccess()) {
                                me.ccrama.redditslide.util.LogUtil.e("Problem setting up In-app Billing: " + result);
                            }
                        }
                    });
                } catch (java.lang.Exception ignored) {
                    ignored.printStackTrace();
                }
            }
            return null;
        }
    }

    // IPV6 workaround by /u/talklittle
    public class GfycatIpv4Dns implements okhttp3.Dns {
        @java.lang.Override
        public java.util.List<java.net.InetAddress> lookup(java.lang.String hostname) throws java.net.UnknownHostException {
            if (me.ccrama.redditslide.ContentType.hostContains(hostname, "gfycat.com")) {
                java.net.InetAddress[] addresses = java.net.InetAddress.getAllByName(hostname);
                if ((addresses == null) || (addresses.length == 0)) {
                    throw new java.net.UnknownHostException("Bad host: " + hostname);
                }
                // prefer IPv4; list IPv4 first
                java.util.ArrayList<java.net.InetAddress> result = new java.util.ArrayList<>();
                for (java.net.InetAddress address : addresses) {
                    if (address instanceof java.net.Inet4Address) {
                        result.add(address);
                    }
                }
                for (java.net.InetAddress address : addresses) {
                    if (!(address instanceof java.net.Inet4Address)) {
                        result.add(address);
                    }
                }
                return result;
            } else {
                return okhttp3.Dns.SYSTEM.lookup(hostname);
            }
        }
    }

    public static android.content.Context getAppContext() {
        return me.ccrama.redditslide.Reddit.mApplication.getApplicationContext();
    }

    @android.annotation.TargetApi(android.os.Build.VERSION_CODES.M)
    private static void setCanUseNightModeAuto() {
        android.app.UiModeManager uiModeManager = ((android.app.UiModeManager) (me.ccrama.redditslide.Reddit.getAppContext().getSystemService(android.content.Context.UI_MODE_SERVICE)));
        if (uiModeManager != null) {
            uiModeManager.setNightMode(android.app.UiModeManager.MODE_NIGHT_AUTO);
            me.ccrama.redditslide.Reddit.canUseNightModeAuto = true;
        } else {
            me.ccrama.redditslide.Reddit.canUseNightModeAuto = false;
        }
    }
}