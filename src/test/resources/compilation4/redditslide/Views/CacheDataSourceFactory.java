package me.ccrama.redditslide.Views;
import me.ccrama.redditslide.util.LogUtil;
import java.util.LinkedList;
import me.ccrama.redditslide.R;
import java.util.List;
import me.ccrama.redditslide.SettingValues;
import java.io.File;
class CacheDataSourceFactory implements com.google.android.exoplayer2.upstream.DataSource.Factory {
    private final android.content.Context context;

    private final com.google.android.exoplayer2.upstream.DefaultDataSourceFactory defaultDatasourceFactory;

    private final long maxFileSize;

    private final long maxCacheSize;

    @java.lang.Override
    public com.google.android.exoplayer2.upstream.DataSource createDataSource() {
        com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor evictor = new com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor(maxCacheSize);
        com.google.android.exoplayer2.upstream.cache.SimpleCache simpleCache = new com.google.android.exoplayer2.upstream.cache.SimpleCache(new java.io.File(context.getCacheDir(), "media"), evictor);
        return new com.google.android.exoplayer2.upstream.cache.CacheDataSource(simpleCache, defaultDatasourceFactory.createDataSource(), new com.google.android.exoplayer2.upstream.FileDataSource(), new com.google.android.exoplayer2.upstream.cache.CacheDataSink(simpleCache, maxFileSize), com.google.android.exoplayer2.upstream.cache.CacheDataSource.FLAG_BLOCK_ON_CACHE | com.google.android.exoplayer2.upstream.cache.CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR, null);
    }

    CacheDataSourceFactory(android.content.Context context, long maxCacheSize, long maxFileSize) {
        super();
        this.context = context;
        this.maxCacheSize = maxCacheSize;
        this.maxFileSize = maxFileSize;
        java.lang.String userAgent = com.google.android.exoplayer2.util.Util.getUserAgent(context, context.getString(me.ccrama.redditslide.R.string.app_name));
        com.google.android.exoplayer2.upstream.DefaultBandwidthMeter bandwidthMeter = new com.google.android.exoplayer2.upstream.DefaultBandwidthMeter();
        defaultDatasourceFactory = new com.google.android.exoplayer2.upstream.DefaultDataSourceFactory(this.context, bandwidthMeter, new com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory(userAgent, bandwidthMeter));
    }
}