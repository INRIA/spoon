package me.ccrama.redditslide.SwipeLayout;
import java.lang.reflect.Method;
/**
 * Created by Chaojun Wang on 6/9/14.
 */
public class Utils {
    private Utils() {
    }

    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} to a fullscreen opaque
     * Activity.
     * <p>
     * Call this whenever the background of a translucent Activity has changed
     * to become opaque. Doing so will allow the {@link android.view.Surface} of
     * the Activity behind to be released.
     * <p>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    public static void convertActivityFromTranslucent(android.app.Activity activity) {
        try {
            java.lang.reflect.Method method = android.app.Activity.class.getDeclaredMethod("convertFromTranslucent");
            method.setAccessible(true);
            method.invoke(activity);
        } catch (java.lang.Throwable ignored) {
        }
    }

    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} back from opaque to
     * translucent following a call to
     * {@link #convertActivityFromTranslucent(android.app.Activity)} .
     * <p>
     * Calling this allows the Activity behind this one to be seen again. Once
     * all such Activities have been redrawn
     * <p>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    public static void convertActivityToTranslucent(android.app.Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            me.ccrama.redditslide.SwipeLayout.Utils.convertActivityToTranslucentAfterL(activity);
        } else {
            me.ccrama.redditslide.SwipeLayout.Utils.convertActivityToTranslucentBeforeL(activity);
        }
    }

    /**
     * Calling the convertToTranslucent method on platforms before Android 5.0
     */
    public static void convertActivityToTranslucentBeforeL(android.app.Activity activity) {
        try {
            java.lang.Class<?>[] classes = android.app.Activity.class.getDeclaredClasses();
            java.lang.Class<?> translucentConversionListenerClazz = null;
            for (java.lang.Class clazz : classes) {
                if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz;
                }
            }
            java.lang.reflect.Method method = android.app.Activity.class.getDeclaredMethod("convertToTranslucent", translucentConversionListenerClazz);
            method.setAccessible(true);
            method.invoke(activity, new java.lang.Object[]{ null });
        } catch (java.lang.Throwable ignored) {
        }
    }

    /**
     * Calling the convertToTranslucent method on platforms after Android 5.0
     */
    private static void convertActivityToTranslucentAfterL(android.app.Activity activity) {
        try {
            java.lang.reflect.Method getActivityOptions = android.app.Activity.class.getDeclaredMethod("getActivityOptions");
            getActivityOptions.setAccessible(true);
            java.lang.Object options = getActivityOptions.invoke(activity);
            java.lang.Class<?>[] classes = android.app.Activity.class.getDeclaredClasses();
            java.lang.Class<?> translucentConversionListenerClazz = null;
            for (java.lang.Class clazz : classes) {
                if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz;
                }
            }
            java.lang.reflect.Method convertToTranslucent = android.app.Activity.class.getDeclaredMethod("convertToTranslucent", translucentConversionListenerClazz, android.app.ActivityOptions.class);
            convertToTranslucent.setAccessible(true);
            convertToTranslucent.invoke(activity, null, options);
        } catch (java.lang.Throwable ignored) {
        }
    }
}