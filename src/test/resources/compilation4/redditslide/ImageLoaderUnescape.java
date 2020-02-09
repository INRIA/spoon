package me.ccrama.redditslide;
import me.ccrama.redditslide.util.OkHttpImageDownloader;
import java.io.IOException;
import java.io.File;
class ImageLoaderUnescape extends com.nostra13.universalimageloader.core.ImageLoader {
    @java.lang.Override
    public void displayImage(java.lang.String uri, com.nostra13.universalimageloader.core.imageaware.ImageAware imageAware, com.nostra13.universalimageloader.core.DisplayImageOptions options, com.nostra13.universalimageloader.core.assist.ImageSize targetSize, com.nostra13.universalimageloader.core.listener.ImageLoadingListener listener, com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener progressListener) {
        java.lang.String newUri = org.apache.commons.text.StringEscapeUtils.unescapeHtml4(uri);
        super.displayImage(newUri, imageAware, options, targetSize, listener, progressListener);
    }

    private static volatile me.ccrama.redditslide.ImageLoaderUnescape instance;

    public static me.ccrama.redditslide.ImageLoaderUnescape getInstance() {
        if (me.ccrama.redditslide.ImageLoaderUnescape.instance == null) {
            synchronized(com.nostra13.universalimageloader.core.ImageLoader.class) {
                if (me.ccrama.redditslide.ImageLoaderUnescape.instance == null) {
                    me.ccrama.redditslide.ImageLoaderUnescape.instance = new me.ccrama.redditslide.ImageLoaderUnescape();
                }
            }
        }
        return me.ccrama.redditslide.ImageLoaderUnescape.instance;
    }
}