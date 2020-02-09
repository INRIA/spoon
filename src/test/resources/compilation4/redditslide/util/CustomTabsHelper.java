package me.ccrama.redditslide.util;
import java.util.ArrayList;
import java.util.List;
import me.ccrama.redditslide.SettingValues;
/**
 * Helper class for Custom Tabs.
 */
public class CustomTabsHelper {
    private static final java.lang.String TAG = "CustomTabsHelper";

    static final java.lang.String STABLE_PACKAGE = "com.android.chrome";

    static final java.lang.String BETA_PACKAGE = "com.chrome.beta";

    static final java.lang.String DEV_PACKAGE = "com.chrome.dev";

    static final java.lang.String LOCAL_PACKAGE = "com.google.android.apps.chrome";

    private static final java.lang.String EXTRA_CUSTOM_TABS_KEEP_ALIVE = "android.support.customtabs.extra.KEEP_ALIVE";

    private static final java.lang.String ACTION_CUSTOM_TABS_CONNECTION = "android.support.customtabs.action.CustomTabsService";

    private static java.lang.String sPackageNameToUse;

    private CustomTabsHelper() {
    }

    /**
     * Goes through all apps that handle VIEW intents and have a warmup service. Picks
     * the one chosen by the user if there is one, otherwise makes a best effort to return a
     * valid package name.
     *
     * This is <strong>not</strong> threadsafe.
     *
     * @param context
     * 		{@link Context} to use for accessing {@link PackageManager}.
     * @return The package name recommended to use for connecting to custom tabs related components.
     */
    public static java.lang.String getPackageNameToUse(android.content.Context context) {
        if (me.ccrama.redditslide.SettingValues.linkHandlingMode != me.ccrama.redditslide.Fragments.SettingsHandlingFragment.LinkHandlingMode.CUSTOM_TABS.getValue())
            return null;

        if (me.ccrama.redditslide.util.CustomTabsHelper.sPackageNameToUse != null)
            return me.ccrama.redditslide.util.CustomTabsHelper.sPackageNameToUse;

        android.content.pm.PackageManager pm = context.getPackageManager();
        // Get default VIEW intent handler.
        android.content.Intent activityIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("http://www.example.com"));
        android.content.pm.ResolveInfo defaultViewHandlerInfo = pm.resolveActivity(activityIntent, 0);
        java.lang.String defaultViewHandlerPackageName = null;
        if (defaultViewHandlerInfo != null) {
            defaultViewHandlerPackageName = defaultViewHandlerInfo.activityInfo.packageName;
        }
        // Get all apps that can handle VIEW intents.
        java.util.List<android.content.pm.ResolveInfo> resolvedActivityList = pm.queryIntentActivities(activityIntent, 0);
        java.util.List<java.lang.String> packagesSupportingCustomTabs = new java.util.ArrayList<>();
        for (android.content.pm.ResolveInfo info : resolvedActivityList) {
            android.content.Intent serviceIntent = new android.content.Intent();
            serviceIntent.setAction(me.ccrama.redditslide.util.CustomTabsHelper.ACTION_CUSTOM_TABS_CONNECTION);
            serviceIntent.setPackage(info.activityInfo.packageName);
            // Samsung browser custom tabs has a bug that harms user experience, pressing back
            // navigates between pages rather than exiting back to Slide
            // TODO: Reevaluate at a later date
            if ((pm.resolveService(serviceIntent, 0) != null) && (!info.activityInfo.packageName.equals("com.sec.android.app.sbrowser"))) {
                packagesSupportingCustomTabs.add(info.activityInfo.packageName);
            }
        }
        // Now packagesSupportingCustomTabs contains all apps that can handle both VIEW intents
        // and service calls.
        if (packagesSupportingCustomTabs.isEmpty()) {
            me.ccrama.redditslide.util.CustomTabsHelper.sPackageNameToUse = null;
        } else if (packagesSupportingCustomTabs.size() == 1) {
            me.ccrama.redditslide.util.CustomTabsHelper.sPackageNameToUse = packagesSupportingCustomTabs.get(0);
        } else if (((!android.text.TextUtils.isEmpty(defaultViewHandlerPackageName)) && (!me.ccrama.redditslide.util.CustomTabsHelper.hasSpecializedHandlerIntents(context, activityIntent))) && packagesSupportingCustomTabs.contains(defaultViewHandlerPackageName)) {
            me.ccrama.redditslide.util.CustomTabsHelper.sPackageNameToUse = defaultViewHandlerPackageName;
        } else if (packagesSupportingCustomTabs.contains(me.ccrama.redditslide.util.CustomTabsHelper.STABLE_PACKAGE)) {
            me.ccrama.redditslide.util.CustomTabsHelper.sPackageNameToUse = me.ccrama.redditslide.util.CustomTabsHelper.STABLE_PACKAGE;
        } else if (packagesSupportingCustomTabs.contains(me.ccrama.redditslide.util.CustomTabsHelper.BETA_PACKAGE)) {
            me.ccrama.redditslide.util.CustomTabsHelper.sPackageNameToUse = me.ccrama.redditslide.util.CustomTabsHelper.BETA_PACKAGE;
        } else if (packagesSupportingCustomTabs.contains(me.ccrama.redditslide.util.CustomTabsHelper.DEV_PACKAGE)) {
            me.ccrama.redditslide.util.CustomTabsHelper.sPackageNameToUse = me.ccrama.redditslide.util.CustomTabsHelper.DEV_PACKAGE;
        } else if (packagesSupportingCustomTabs.contains(me.ccrama.redditslide.util.CustomTabsHelper.LOCAL_PACKAGE)) {
            me.ccrama.redditslide.util.CustomTabsHelper.sPackageNameToUse = me.ccrama.redditslide.util.CustomTabsHelper.LOCAL_PACKAGE;
        }
        return me.ccrama.redditslide.util.CustomTabsHelper.sPackageNameToUse;
    }

    /**
     * Used to check whether there is a specialized handler for a given intent.
     *
     * @param intent
     * 		The intent to check with.
     * @return Whether there is a specialized handler for the given intent.
     */
    private static boolean hasSpecializedHandlerIntents(android.content.Context context, android.content.Intent intent) {
        try {
            android.content.pm.PackageManager pm = context.getPackageManager();
            java.util.List<android.content.pm.ResolveInfo> handlers = pm.queryIntentActivities(intent, android.content.pm.PackageManager.GET_RESOLVED_FILTER);
            if ((handlers == null) || handlers.isEmpty()) {
                return false;
            }
            for (android.content.pm.ResolveInfo resolveInfo : handlers) {
                android.content.IntentFilter filter = resolveInfo.filter;
                if (filter == null)
                    continue;

                if ((filter.countDataAuthorities() == 0) || (filter.countDataPaths() == 0))
                    continue;

                if (resolveInfo.activityInfo == null)
                    continue;

                return true;
            }
        } catch (java.lang.RuntimeException e) {
            android.util.Log.e(me.ccrama.redditslide.util.CustomTabsHelper.TAG, "Runtime exception while getting specialized handlers");
        }
        return false;
    }

    /**
     *
     *
     * @return All possible chrome package names that provide custom tabs feature.
     */
    public static java.lang.String[] getPackages() {
        return new java.lang.String[]{ "", me.ccrama.redditslide.util.CustomTabsHelper.STABLE_PACKAGE, me.ccrama.redditslide.util.CustomTabsHelper.BETA_PACKAGE, me.ccrama.redditslide.util.CustomTabsHelper.DEV_PACKAGE, me.ccrama.redditslide.util.CustomTabsHelper.LOCAL_PACKAGE };
    }
}