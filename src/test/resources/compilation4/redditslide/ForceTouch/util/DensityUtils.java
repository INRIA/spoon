package me.ccrama.redditslide.ForceTouch.util;
/**
 * Convert between DP and PX
 */
public class DensityUtils {
    public static int toPx(android.content.Context context, int dp) {
        return me.ccrama.redditslide.ForceTouch.util.DensityUtils.convert(context, dp, android.util.TypedValue.COMPLEX_UNIT_DIP);
    }

    public static int toDp(android.content.Context context, int px) {
        return me.ccrama.redditslide.ForceTouch.util.DensityUtils.convert(context, px, android.util.TypedValue.COMPLEX_UNIT_PX);
    }

    private static int convert(android.content.Context context, int amount, int conversionUnit) {
        if (amount < 0) {
            throw new java.lang.IllegalArgumentException("px should not be less than zero");
        }
        android.content.res.Resources r = context.getResources();
        return ((int) (android.util.TypedValue.applyDimension(conversionUnit, amount, r.getDisplayMetrics())));
    }
}