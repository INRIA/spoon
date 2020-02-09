package me.ccrama.redditslide.Tumblr;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import me.ccrama.redditslide.util.HttpUtil;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Constants;
import com.google.gson.JsonElement;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * Created by carlo_000 on 2/1/2016.
 */
public class TumblrUtils {
    public static android.content.SharedPreferences tumblrRequests;

    private static java.lang.String cutEnds(java.lang.String s) {
        if (s.endsWith("/")) {
            return s.substring(0, s.length() - 1);
        } else {
            return s;
        }
    }

    public static class GetTumblrPostWithCallback extends android.os.AsyncTask<java.lang.String, java.lang.Void, java.util.ArrayList<com.google.gson.JsonElement>> {
        public java.lang.String blog;

        public java.lang.String id;

        public android.app.Activity baseActivity;

        private okhttp3.OkHttpClient client;

        private com.google.gson.Gson gson;

        public void onError() {
        }

        public GetTumblrPostWithCallback(@org.jetbrains.annotations.NotNull
        java.lang.String url, @org.jetbrains.annotations.NotNull
        android.app.Activity baseActivity) {
            this.baseActivity = baseActivity;
            android.net.Uri i = android.net.Uri.parse(url);
            id = i.getPathSegments().get(1);
            blog = i.getHost().split("\\.")[0];
            client = me.ccrama.redditslide.Reddit.client;
            gson = new com.google.gson.Gson();
        }

        public void doWithData(java.util.List<me.ccrama.redditslide.Tumblr.Photo> data) {
            if ((data == null) || data.isEmpty()) {
                onError();
            }
        }

        com.google.gson.JsonElement[] target;

        int count;

        int done;

        me.ccrama.redditslide.Tumblr.TumblrPost post;

        public void parseJson(com.google.gson.JsonElement baseData) {
            try {
                post = new com.fasterxml.jackson.databind.ObjectMapper().readValue(baseData.toString(), me.ccrama.redditslide.Tumblr.TumblrPost.class);
                baseActivity.runOnUiThread(new java.lang.Runnable() {
                    @java.lang.Override
                    public void run() {
                        doWithData(post.getResponse().getPosts().get(0).getPhotos());
                    }
                });
            } catch (java.io.IOException e) {
                e.printStackTrace();
                me.ccrama.redditslide.util.LogUtil.e(e, ("parseJson error, baseData [" + baseData) + "]");
            }
        }

        @java.lang.Override
        protected java.util.ArrayList<com.google.gson.JsonElement> doInBackground(final java.lang.String... sub) {
            if (baseActivity != null) {
                java.lang.String apiUrl = (((("https://api.tumblr.com/v2/blog/" + blog) + "/posts?api_key=") + me.ccrama.redditslide.Constants.TUMBLR_API_KEY) + "&id=") + id;
                me.ccrama.redditslide.util.LogUtil.v(apiUrl);
                if (me.ccrama.redditslide.Tumblr.TumblrUtils.tumblrRequests.contains(apiUrl) && new com.google.gson.JsonParser().parse(me.ccrama.redditslide.Tumblr.TumblrUtils.tumblrRequests.getString(apiUrl, "")).getAsJsonObject().has("response")) {
                    parseJson(new com.google.gson.JsonParser().parse(me.ccrama.redditslide.Tumblr.TumblrUtils.tumblrRequests.getString(apiUrl, "")).getAsJsonObject());
                } else {
                    me.ccrama.redditslide.util.LogUtil.v(apiUrl);
                    final com.google.gson.JsonObject result = me.ccrama.redditslide.util.HttpUtil.getJsonObject(client, gson, apiUrl);
                    if ((((result != null) && result.has("response")) && result.get("response").getAsJsonObject().has("posts")) && result.get("response").getAsJsonObject().get("posts").getAsJsonArray().get(0).getAsJsonObject().has("photos")) {
                        me.ccrama.redditslide.Tumblr.TumblrUtils.tumblrRequests.edit().putString(apiUrl, result.toString()).apply();
                        parseJson(result);
                    } else {
                        onError();
                    }
                }
                return null;
            }
            return null;
        }
    }
}