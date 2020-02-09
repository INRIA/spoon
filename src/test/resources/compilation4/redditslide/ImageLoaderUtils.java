package me.ccrama.redditslide;
import me.ccrama.redditslide.util.OkHttpImageDownloader;
import java.io.IOException;
import java.io.File;
/**
 * Created by carlo_000 on 10/19/2015.
 */
/* Adapted from https://github.com/Kennyc1012/Opengur */
public class ImageLoaderUtils {
    public static me.ccrama.redditslide.ImageLoaderUnescape imageLoader;

    private ImageLoaderUtils() {
    }

    public static java.io.File getCacheDirectory(android.content.Context context) {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED) && (context.getExternalCacheDir() != null)) {
            return context.getExternalCacheDir();
        }
        return context.getCacheDir();
    }

    public static java.io.File getCacheDirectoryGif(android.content.Context context) {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED) && (context.getExternalCacheDir() != null)) {
            return new java.io.File((context.getExternalCacheDir() + java.io.File.separator) + "gifs");
        }
        return new java.io.File((context.getCacheDir() + java.io.File.separator) + "gifs");
    }

    public static void initImageLoader(android.content.Context context) {
        long discCacheSize = 1024 * 1024;
        com.nostra13.universalimageloader.cache.disc.DiskCache discCache;
        java.io.File dir = me.ccrama.redditslide.ImageLoaderUtils.getCacheDirectory(context);
        int threadPoolSize;
        discCacheSize *= 100;
        threadPoolSize = 7;
        if (discCacheSize > 0) {
            try {
                dir.mkdir();
                discCache = new com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache(dir, new com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator(), discCacheSize);
            } catch (java.io.IOException e) {
                discCache = new com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache(dir);
            }
        } else {
            discCache = new com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache(dir);
        }
        me.ccrama.redditslide.ImageLoaderUtils.options = new com.nostra13.universalimageloader.core.DisplayImageOptions.Builder().cacheOnDisk(true).bitmapConfig(android.graphics.Bitmap.Config.RGB_565).imageScaleType(com.nostra13.universalimageloader.core.assist.ImageScaleType.IN_SAMPLE_POWER_OF_2).cacheInMemory(false).resetViewBeforeLoading(false).displayer(new com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer(250)).build();
        com.nostra13.universalimageloader.core.ImageLoaderConfiguration config = new com.nostra13.universalimageloader.core.ImageLoaderConfiguration.Builder(context).threadPoolSize(threadPoolSize).denyCacheImageMultipleSizesInMemory().diskCache(discCache).threadPoolSize(4).imageDownloader(new me.ccrama.redditslide.util.OkHttpImageDownloader(context)).defaultDisplayImageOptions(me.ccrama.redditslide.ImageLoaderUtils.options).build();
        if (com.nostra13.universalimageloader.core.ImageLoader.getInstance().isInited()) {
            com.nostra13.universalimageloader.core.ImageLoader.getInstance().destroy();
        }
        me.ccrama.redditslide.ImageLoaderUtils.imageLoader = me.ccrama.redditslide.ImageLoaderUnescape.getInstance();
        me.ccrama.redditslide.ImageLoaderUtils.imageLoader.init(config);
    }

    public static com.nostra13.universalimageloader.core.DisplayImageOptions options;
}