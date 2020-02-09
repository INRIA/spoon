package me.ccrama.redditslide.util;
public class LogUtil {
    private static final int CALLING_METHOD_INDEX;

    /**
     * Get the stacktrace index of the method that called this class
     *
     * Variation of http://stackoverflow.com/a/8592871/4026792
     */
    static {
        int i = 1;
        for (java.lang.StackTraceElement ste : java.lang.Thread.currentThread().getStackTrace()) {
            i++;
            if (ste.getClassName().equals(me.ccrama.redditslide.util.LogUtil.class.getName())) {
                break;
            }
        }
        me.ccrama.redditslide.util.LogUtil.CALLING_METHOD_INDEX = i;
    }

    /**
     * Source: http://stackoverflow.com/a/24586896/4026792
     *
     * @return Log tag in format (CLASSNAME.java:LINENUMBER); which makes it clickable in logcat
     */
    public static java.lang.String getTag() {
        try {
            final java.lang.StackTraceElement ste = java.lang.Thread.currentThread().getStackTrace()[me.ccrama.redditslide.util.LogUtil.CALLING_METHOD_INDEX];
            return ((("(" + ste.getFileName()) + ":") + ste.getLineNumber()) + ")";
        } catch (java.lang.Exception e) {
            return "Slide";
        }
    }

    public static void v(java.lang.String message) {
        android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), message);
    }

    public static void d(java.lang.String message) {
        android.util.Log.d(me.ccrama.redditslide.util.LogUtil.getTag(), message);
    }

    public static void i(java.lang.String message) {
        android.util.Log.i(me.ccrama.redditslide.util.LogUtil.getTag(), message);
    }

    public static void w(java.lang.String message) {
        android.util.Log.w(me.ccrama.redditslide.util.LogUtil.getTag(), message);
    }

    public static void e(java.lang.String message) {
        android.util.Log.e(me.ccrama.redditslide.util.LogUtil.getTag(), message);
    }

    public static void e(java.lang.Throwable tr, java.lang.String message) {
        android.util.Log.e(me.ccrama.redditslide.util.LogUtil.getTag(), message, tr);
    }
}