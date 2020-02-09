package me.ccrama.redditslide.Synccit.http;
import java.util.concurrent.TimeUnit;
import java.util.List;
class HttpClientFactory {
    @java.lang.SuppressWarnings("unused")
    private static final java.lang.String TAG = me.ccrama.redditslide.Synccit.http.HttpClientFactory.class.getSimpleName();

    private static okhttp3.OkHttpClient client;

    private static final int SOCKET_OPERATION_TIMEOUT = 60 * 1000;

    private static final java.util.List<okhttp3.Protocol> PROTOCOLS = okhttp3.internal.Util.immutableList(okhttp3.Protocol.HTTP_1_1);

    static synchronized okhttp3.OkHttpClient getOkHttpClient() {
        if (me.ccrama.redditslide.Synccit.http.HttpClientFactory.client == null) {
            me.ccrama.redditslide.Synccit.http.HttpClientFactory.client = me.ccrama.redditslide.Synccit.http.HttpClientFactory.createOkHttpClient();
        }
        return me.ccrama.redditslide.Synccit.http.HttpClientFactory.client;
    }

    private static okhttp3.OkHttpClient createOkHttpClient() {
        return new okhttp3.OkHttpClient.Builder().protocols(me.ccrama.redditslide.Synccit.http.HttpClientFactory.PROTOCOLS).connectTimeout(me.ccrama.redditslide.Synccit.http.HttpClientFactory.SOCKET_OPERATION_TIMEOUT, java.util.concurrent.TimeUnit.MILLISECONDS).readTimeout(me.ccrama.redditslide.Synccit.http.HttpClientFactory.SOCKET_OPERATION_TIMEOUT, java.util.concurrent.TimeUnit.MILLISECONDS).build();
    }
}