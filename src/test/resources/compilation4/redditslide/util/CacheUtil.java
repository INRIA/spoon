package me.ccrama.redditslide.util;
import me.ccrama.redditslide.ImageLoaderUtils;
import java.io.File;
/**
 * Created by carlo_000 on 3/6/2016.
 * <p/>
 * Adapted from http://stackoverflow.com/a/10069679/3697225
 */
public class CacheUtil {
    private static final long MAX_SIZE = 75000000L;// 75MB


    private CacheUtil() {
    }

    public static void makeRoom(android.app.Activity context, int length) {
        java.io.File cacheDir = me.ccrama.redditslide.ImageLoaderUtils.getCacheDirectoryGif(context);
        me.ccrama.redditslide.util.CacheUtil.cleanDir(cacheDir);
    }

    private static void cleanDir(java.io.File dir) {
        java.io.File[] files = dir.listFiles();
        for (java.io.File file : files) {
            if ((file.lastModified() + 1000) < java.lang.System.currentTimeMillis()) {
                // more than a day old
                file.delete();
            }
        }
    }

    private static long getDirSize(java.io.File dir) {
        long size = 0;
        if (!dir.exists()) {
            dir.mkdir();
        }
        java.io.File[] files = dir.listFiles();
        for (java.io.File file : files) {
            if (file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }
}