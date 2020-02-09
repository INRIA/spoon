package me.ccrama.redditslide.util;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder;
import me.ccrama.redditslide.Activities.MakeExternal;
import java.io.UnsupportedEncodingException;
import android.support.customtabs.*;
import me.ccrama.redditslide.Activities.Crosspost;
import java.net.URLDecoder;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Activities.ReaderMode;
import me.ccrama.redditslide.Activities.Website;
public class LinkUtil {
    private static android.support.customtabs.CustomTabsSession mCustomTabsSession;

    private static android.support.customtabs.CustomTabsClient mClient;

    private static android.support.customtabs.CustomTabsServiceConnection mConnection;

    public static final java.lang.String EXTRA_URL = "url";

    public static final java.lang.String EXTRA_COLOR = "color";

    public static final java.lang.String ADAPTER_POSITION = "adapter_position";

    private LinkUtil() {
    }

    public static android.graphics.Bitmap drawableToBitmap(android.graphics.drawable.Drawable drawable) {
        if (drawable instanceof android.graphics.drawable.BitmapDrawable) {
            return ((android.graphics.drawable.BitmapDrawable) (drawable)).getBitmap();
        }
        android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), android.graphics.Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Attempts to open the {@code url} in a custom tab. If no custom tab activity can be found,
     * falls back to opening externally
     *
     * @param url
     * 		URL to open
     * @param color
     * 		Color to provide to the browser UI if applicable
     * @param contextActivity
     * 		The current activity
     * @param packageName
     * 		The package name recommended to use for connecting to custom tabs
     * 		related components.
     */
    public static void openCustomTab(@android.support.annotation.NonNull
    java.lang.String url, int color, @android.support.annotation.NonNull
    android.app.Activity contextActivity, @android.support.annotation.NonNull
    java.lang.String packageName) {
        android.content.Intent intent = new android.content.Intent(contextActivity, me.ccrama.redditslide.Activities.MakeExternal.class);
        intent.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_URL, url);
        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(contextActivity, 0, intent, 0);
        android.support.customtabs.CustomTabsIntent.Builder builder = new android.support.customtabs.CustomTabsIntent.Builder(me.ccrama.redditslide.util.LinkUtil.getSession()).setToolbarColor(color).setShowTitle(true).setStartAnimations(contextActivity, me.ccrama.redditslide.R.anim.slide_up_fade_in, 0).setExitAnimations(contextActivity, 0, me.ccrama.redditslide.R.anim.slide_down_fade_out).addDefaultShareMenuItem().addMenuItem(contextActivity.getString(me.ccrama.redditslide.R.string.open_links_externally), pendingIntent).setCloseButtonIcon(me.ccrama.redditslide.util.LinkUtil.drawableToBitmap(android.support.v4.content.ContextCompat.getDrawable(contextActivity, me.ccrama.redditslide.R.drawable.ic_arrow_back_white_24dp)));
        try {
            android.support.customtabs.CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.intent.setPackage(packageName);
            customTabsIntent.launchUrl(contextActivity, me.ccrama.redditslide.util.LinkUtil.formatURL(org.apache.commons.text.StringEscapeUtils.unescapeHtml4(url)));
        } catch (android.content.ActivityNotFoundException anfe) {
            android.util.Log.w(me.ccrama.redditslide.util.LogUtil.getTag(), "Unknown url: " + anfe);
            me.ccrama.redditslide.util.LinkUtil.openExternally(url);
        }
    }

    public static void openUrl(@android.support.annotation.NonNull
    java.lang.String url, int color, @android.support.annotation.NonNull
    android.app.Activity contextActivity, @android.support.annotation.Nullable
    java.lang.Integer adapterPosition, @android.support.annotation.Nullable
    net.dean.jraw.models.Submission submission) {
        if ((!(contextActivity instanceof me.ccrama.redditslide.Activities.ReaderMode)) && ((me.ccrama.redditslide.SettingValues.readerMode && (!me.ccrama.redditslide.SettingValues.readerNight)) || ((me.ccrama.redditslide.SettingValues.readerMode && me.ccrama.redditslide.SettingValues.readerNight) && me.ccrama.redditslide.SettingValues.isNight()))) {
            android.content.Intent i = new android.content.Intent(contextActivity, me.ccrama.redditslide.Activities.ReaderMode.class);
            me.ccrama.redditslide.util.LinkUtil.openIntentThemed(i, url, color, contextActivity, adapterPosition, submission);
        } else if (me.ccrama.redditslide.SettingValues.linkHandlingMode == me.ccrama.redditslide.Fragments.SettingsHandlingFragment.LinkHandlingMode.EXTERNAL.getValue()) {
            me.ccrama.redditslide.util.LinkUtil.openExternally(url);
        } else {
            java.lang.String packageName = me.ccrama.redditslide.util.CustomTabsHelper.getPackageNameToUse(contextActivity);
            if ((me.ccrama.redditslide.SettingValues.linkHandlingMode == me.ccrama.redditslide.Fragments.SettingsHandlingFragment.LinkHandlingMode.CUSTOM_TABS.getValue()) && (packageName != null)) {
                me.ccrama.redditslide.util.LinkUtil.openCustomTab(url, color, contextActivity, packageName);
            } else {
                android.content.Intent i = new android.content.Intent(contextActivity, me.ccrama.redditslide.Activities.Website.class);
                me.ccrama.redditslide.util.LinkUtil.openIntentThemed(i, url, color, contextActivity, adapterPosition, submission);
            }
        }
    }

    private static void openIntentThemed(@android.support.annotation.NonNull
    android.content.Intent intent, @android.support.annotation.NonNull
    java.lang.String url, int color, @android.support.annotation.NonNull
    android.app.Activity contextActivity, @android.support.annotation.Nullable
    java.lang.Integer adapterPosition, @android.support.annotation.Nullable
    net.dean.jraw.models.Submission submission) {
        intent.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_URL, url);
        if ((adapterPosition != null) && (submission != null)) {
            me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.addAdaptorPosition(intent, submission, adapterPosition);
        }
        intent.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_COLOR, color);
        contextActivity.startActivity(intent);
    }

    /**
     * Corrects mistakes users might make when typing URLs, e.g. case sensitivity in the scheme
     * and converts to Uri
     *
     * @param url
     * 		URL to correct
     * @return corrected as a Uri
     */
    public static android.net.Uri formatURL(java.lang.String url) {
        if (url.startsWith("//")) {
            url = "https:" + url;
        }
        if (url.startsWith("/")) {
            url = "https://reddit.com" + url;
        }
        if ((!url.contains("://")) && (!url.startsWith("mailto:"))) {
            url = "http://" + url;
        }
        android.net.Uri uri = android.net.Uri.parse(url);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            return uri.normalizeScheme();
        } else {
            return uri;
        }
    }

    public static boolean tryOpenWithVideoPlugin(@android.support.annotation.NonNull
    java.lang.String url) {
        if (me.ccrama.redditslide.Reddit.videoPlugin) {
            try {
                android.content.Intent sharingIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setClassName(me.ccrama.redditslide.Reddit.getAppContext().getString(me.ccrama.redditslide.R.string.youtube_plugin_package), me.ccrama.redditslide.Reddit.getAppContext().getString(me.ccrama.redditslide.R.string.youtube_plugin_class));
                sharingIntent.putExtra("url", me.ccrama.redditslide.util.LinkUtil.removeUnusedParameters(url));
                sharingIntent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                me.ccrama.redditslide.Reddit.getAppContext().startActivity(sharingIntent);
                return true;
            } catch (java.lang.Exception ignored) {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Opens the {@code url} using the method the user has set in their preferences (custom tabs,
     * internal, external) falling back as needed
     *
     * @param url
     * 		URL to open
     * @param color
     * 		Color to provide to the browser UI if applicable
     * @param contextActivity
     * 		The current activity
     */
    public static void openUrl(@android.support.annotation.NonNull
    java.lang.String url, int color, @android.support.annotation.NonNull
    android.app.Activity contextActivity) {
        me.ccrama.redditslide.util.LinkUtil.openUrl(url, color, contextActivity, null, null);
    }

    /**
     * Opens the {@code uri} externally or shows an application chooser if it is set to open in this
     * application
     *
     * @param url
     * 		URL to open
     */
    public static void openExternally(java.lang.String url) {
        url = org.apache.commons.text.StringEscapeUtils.unescapeHtml4(android.text.Html.fromHtml(url).toString());
        android.net.Uri uri = me.ccrama.redditslide.util.LinkUtil.formatURL(url);
        final android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW, uri);
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        me.ccrama.redditslide.util.LinkUtil.overridePackage(intent);
        me.ccrama.redditslide.Reddit.getAppContext().startActivity(intent);
    }

    public static android.support.customtabs.CustomTabsSession getSession() {
        if (me.ccrama.redditslide.util.LinkUtil.mClient == null) {
            me.ccrama.redditslide.util.LinkUtil.mCustomTabsSession = null;
        } else if (me.ccrama.redditslide.util.LinkUtil.mCustomTabsSession == null) {
            me.ccrama.redditslide.util.LinkUtil.mCustomTabsSession = me.ccrama.redditslide.util.LinkUtil.mClient.newSession(new android.support.customtabs.CustomTabsCallback() {
                @java.lang.Override
                public void onNavigationEvent(int navigationEvent, android.os.Bundle extras) {
                    android.util.Log.w(me.ccrama.redditslide.util.LogUtil.getTag(), "onNavigationEvent: Code = " + navigationEvent);
                }
            });
        }
        return me.ccrama.redditslide.util.LinkUtil.mCustomTabsSession;
    }

    public static void copyUrl(java.lang.String url, android.content.Context context) {
        url = org.apache.commons.text.StringEscapeUtils.unescapeHtml4(android.text.Html.fromHtml(url).toString());
        android.content.ClipboardManager clipboard = ((android.content.ClipboardManager) (context.getSystemService(android.content.Context.CLIPBOARD_SERVICE)));
        android.content.ClipData clip = android.content.ClipData.newPlainText("Link", url);
        clipboard.setPrimaryClip(clip);
        android.widget.Toast.makeText(context, me.ccrama.redditslide.R.string.submission_link_copied, android.widget.Toast.LENGTH_SHORT).show();
    }

    public static void crosspost(net.dean.jraw.models.Submission submission, android.app.Activity mContext) {
        me.ccrama.redditslide.Activities.Crosspost.toCrosspost = submission;
        mContext.startActivity(new android.content.Intent(mContext, me.ccrama.redditslide.Activities.Crosspost.class));
    }

    public static void overridePackage(android.content.Intent intent) {
        java.lang.String packageName = me.ccrama.redditslide.Reddit.getAppContext().getPackageManager().resolveActivity(intent, 0).activityInfo.packageName;
        // Gets the default app from a URL that is most likely never link handled by another app, hopefully guaranteeing a browser
        java.lang.String browserPackageName = me.ccrama.redditslide.Reddit.getAppContext().getPackageManager().resolveActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("http://ccrama.me/")), 0).activityInfo.packageName;
        java.lang.String packageToSet = packageName;
        if (packageName.equals(me.ccrama.redditslide.Reddit.getAppContext().getPackageName())) {
            packageToSet = browserPackageName;
        }
        if (packageToSet.equals(browserPackageName) && ((me.ccrama.redditslide.SettingValues.selectedBrowser != null) && (!me.ccrama.redditslide.SettingValues.selectedBrowser.isEmpty()))) {
            try {
                me.ccrama.redditslide.Reddit.getAppContext().getPackageManager().getPackageInfo(me.ccrama.redditslide.SettingValues.selectedBrowser, android.content.pm.PackageManager.GET_ACTIVITIES);
                packageToSet = me.ccrama.redditslide.SettingValues.selectedBrowser;
            } catch (android.content.pm.PackageManager.NameNotFoundException ignored) {
            }
        }
        if (!packageToSet.equals(packageName)) {
            intent.setPackage(packageToSet);
        }
    }

    public static java.lang.String removeUnusedParameters(java.lang.String url) {
        java.lang.String returnUrl = url;
        try {
            java.lang.String[] urlParts = url.split("\\?");
            if (urlParts.length > 1) {
                java.lang.String[] paramArray = urlParts[1].split("&");
                java.lang.StringBuilder stringBuilder = new java.lang.StringBuilder();
                stringBuilder.append(urlParts[0]);
                for (int i = 0; i < paramArray.length; i++) {
                    java.lang.String[] paramPairArray = paramArray[i].split("=");
                    if (paramPairArray.length > 1) {
                        if (i == 0) {
                            stringBuilder.append("?");
                        } else {
                            stringBuilder.append("&");
                        }
                        stringBuilder.append(java.net.URLDecoder.decode(paramPairArray[0], "UTF-8"));
                        stringBuilder.append("=");
                        stringBuilder.append(java.net.URLDecoder.decode(paramPairArray[1], "UTF-8"));
                    }
                }
                returnUrl = stringBuilder.toString();
            }
            return returnUrl;
        } catch (java.io.UnsupportedEncodingException ignored) {
            return returnUrl;
        }
    }
}