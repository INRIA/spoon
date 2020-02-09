package me.ccrama.redditslide.Synccit.http;
import java.util.ArrayList;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.List;
public abstract class HttpPostTask<Result> extends android.os.AsyncTask<java.lang.String, java.lang.Long, Result> {
    private static final java.lang.String TAG = me.ccrama.redditslide.Synccit.http.HttpPostTask.class.getSimpleName();

    private static final int CONNECTION_TIMEOUT_MILLIS = 5000;

    private static final int SOCKET_TIMEOUT_MILLIS = 20000;

    private final okhttp3.OkHttpClient mClient = me.ccrama.redditslide.Synccit.http.HttpClientFactory.getOkHttpClient().newBuilder().readTimeout(me.ccrama.redditslide.Synccit.http.HttpPostTask.SOCKET_TIMEOUT_MILLIS, java.util.concurrent.TimeUnit.MILLISECONDS).connectTimeout(me.ccrama.redditslide.Synccit.http.HttpPostTask.CONNECTION_TIMEOUT_MILLIS, java.util.concurrent.TimeUnit.MILLISECONDS).build();

    private java.lang.String mUrl;

    /**
     *
     *
     * @param url
     * 		Required.
     */
    public HttpPostTask(java.lang.String url) {
        mUrl = url;
    }

    /**
     * params come in pairs: key/value
     */
    @java.lang.Override
    protected Result doInBackground(java.lang.String... params) {
        java.util.List<android.util.Pair<java.lang.String, java.lang.String>> nvps = getPostArgs(params);
        okhttp3.MultipartBody.Builder formBuilder = new okhttp3.MultipartBody.Builder().setType(okhttp3.MultipartBody.FORM);
        for (android.util.Pair<java.lang.String, java.lang.String> p : nvps) {
            formBuilder.addFormDataPart(p.first, p.second);
        }
        okhttp3.Request request = new okhttp3.Request.Builder().url(mUrl).header("User-Agent", getUserAgent()).addHeader("Content-Encoding", "gzip").post(formBuilder.build()).build();
        try {
            okhttp3.Response res = mClient.newCall(request).execute();
            if (!res.isSuccessful()) {
                throw new java.io.IOException("Unexpected code " + res);
            }
            return onInput(res.body().string());
        } catch (java.lang.Exception ex) {
            android.util.Log.e(me.ccrama.redditslide.Synccit.http.HttpPostTask.TAG, "Error during POST", ex);
        }
        return null;
    }

    private java.util.List<android.util.Pair<java.lang.String, java.lang.String>> getPostArgs(java.lang.String... params) {
        java.util.List<android.util.Pair<java.lang.String, java.lang.String>> nvps = new java.util.ArrayList<>();
        for (int i = 0; i < params.length; i += 2) {
            try {
                nvps.add(new android.util.Pair<>(params[i], params[i + 1]));
            } catch (java.lang.ArrayIndexOutOfBoundsException ex) {
                android.util.Log.e(me.ccrama.redditslide.Synccit.http.HttpPostTask.TAG, "Params didn't come in name/value pairs", ex);
            }
        }
        return nvps;
    }

    protected Result onInput(java.lang.String in) throws java.lang.Exception {
        return null;
    }

    protected abstract java.lang.String getUserAgent();
}