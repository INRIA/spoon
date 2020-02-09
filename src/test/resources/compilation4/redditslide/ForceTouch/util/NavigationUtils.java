package me.ccrama.redditslide.ForceTouch.util;
import java.util.Locale;
/**
 * Gen
 */
public class NavigationUtils {
    public static int getStatusBarHeight(android.content.Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getNavBarHeight(android.content.Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        } else if (me.ccrama.redditslide.ForceTouch.util.NavigationUtils.hasNavBar(context)) {
            me.ccrama.redditslide.ForceTouch.util.DensityUtils.toDp(context, 48);
        }
        return result;
    }

    public static boolean hasNavBar(android.content.Context context) {
        boolean hasBackKey = android.view.KeyCharacterMap.deviceHasKey(android.view.KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = android.view.KeyCharacterMap.deviceHasKey(android.view.KeyEvent.KEYCODE_HOME);
        if (hasBackKey && hasHomeKey) {
            if (android.os.Build.MANUFACTURER.toLowerCase(java.util.Locale.ENGLISH).contains("samsung") && (!android.os.Build.MODEL.toLowerCase(java.util.Locale.ENGLISH).contains("nexus"))) {
                return false;
            }
            android.content.res.Resources resources = context.getResources();
            int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
            if (id > 0) {
                return resources.getBoolean(id);
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
}