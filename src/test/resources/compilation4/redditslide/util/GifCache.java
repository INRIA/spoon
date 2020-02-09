package me.ccrama.redditslide.util;
import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import me.ccrama.redditslide.ImageLoaderUtils;
import java.io.File;
/**
 * Created by carlo_000 on 5/5/2016.
 */
public class GifCache {
    public static long discCacheSize = 100000000L;// 100mb


    public static com.nostra13.universalimageloader.cache.disc.DiskCache discCache;

    public static void init(android.content.Context c) {
        java.io.File dir = me.ccrama.redditslide.ImageLoaderUtils.getCacheDirectoryGif(c);
        try {
            dir.mkdir();
            me.ccrama.redditslide.util.GifCache.discCache = new com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache(dir, new com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator(), me.ccrama.redditslide.util.GifCache.discCacheSize);
            ((com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache) (me.ccrama.redditslide.util.GifCache.discCache)).setBufferSize(5 * 1024);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            me.ccrama.redditslide.util.GifCache.discCache = new com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache(dir);
        }
    }

    public static java.io.File getGif(java.net.URL url) {
        return me.ccrama.redditslide.util.GifCache.discCache.get(url.toString());
    }

    public static void writeGif(java.lang.String url, java.io.InputStream stream, com.nostra13.universalimageloader.utils.IoUtils.CopyListener listener) {
        try {
            me.ccrama.redditslide.util.LogUtil.v(me.ccrama.redditslide.util.GifCache.discCache.save(url, stream, listener) + "DONE ");
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        } finally {
            com.nostra13.universalimageloader.utils.IoUtils.closeSilently(stream);
        }
    }

    public static boolean fileExists(java.net.URL url) {
        return me.ccrama.redditslide.util.GifCache.discCache.get(url.toString()) != null;
    }
}