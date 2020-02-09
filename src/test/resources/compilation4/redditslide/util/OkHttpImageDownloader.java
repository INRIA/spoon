/**
 * Created by Carlos on 9/12/2016.
 */
package me.ccrama.redditslide.util;
import me.ccrama.redditslide.Reddit;
import java.io.InputStream;
import java.io.IOException;
/**
 * Implementation of ImageDownloader which uses {@link com.squareup.okhttp.OkHttpClient} for image
 * stream retrieving.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @author Leo Link (mr[dot]leolink[at]gmail[dot]com)
 */
public class OkHttpImageDownloader extends com.nostra13.universalimageloader.core.download.BaseImageDownloader {
    public OkHttpImageDownloader(android.content.Context context) {
        super(context);
    }

    @java.lang.Override
    protected java.io.InputStream getStreamFromNetwork(java.lang.String imageUri, java.lang.Object extra) throws java.io.IOException {
        okhttp3.Request request = new okhttp3.Request.Builder().url(imageUri).build();
        okhttp3.ResponseBody responseBody = me.ccrama.redditslide.Reddit.client.newCall(request).execute().body();
        java.io.InputStream inputStream = responseBody.byteStream();
        int contentLength = ((int) (responseBody.contentLength()));
        return new com.nostra13.universalimageloader.core.assist.ContentLengthInputStream(inputStream, contentLength);
    }
}