package me.ccrama.redditslide.ImgurAlbum;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import me.ccrama.redditslide.util.HttpUtil;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import me.ccrama.redditslide.util.LogUtil;
import com.google.gson.JsonArray;
import me.ccrama.redditslide.SecretConstants;
import me.ccrama.redditslide.Reddit;
import com.google.gson.JsonElement;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * Created by carlo_000 on 2/1/2016.
 */
public class AlbumUtils {
    public static android.content.SharedPreferences albumRequests;

    private static java.lang.String getHash(java.lang.String s) {
        if (s.contains("/comment/")) {
            s = s.substring(0, s.indexOf("/comment"));
        }
        java.lang.String next = s.substring(s.lastIndexOf("/"), s.length());
        if (next.contains(".")) {
            next = next.substring(0, next.indexOf("."));
        }
        if (next.startsWith("/")) {
            next = next.substring(1, next.length());
        }
        if (next.length() < 5) {
            return me.ccrama.redditslide.ImgurAlbum.AlbumUtils.getHash(s.replace(next, ""));
        } else {
            return next;
        }
    }

    private static java.lang.String cutEnds(java.lang.String s) {
        if (s.endsWith("/")) {
            return s.substring(0, s.length() - 1);
        } else {
            return s;
        }
    }

    public static class GetAlbumWithCallback extends android.os.AsyncTask<java.lang.String, java.lang.Void, java.util.ArrayList<com.google.gson.JsonElement>> {
        public java.lang.String hash;

        public android.app.Activity baseActivity;

        private okhttp3.OkHttpClient client;

        private com.google.gson.Gson gson;

        private java.lang.String mashapeKey;

        public void onError() {
        }

        public GetAlbumWithCallback(@org.jetbrains.annotations.NotNull
        java.lang.String url, @org.jetbrains.annotations.NotNull
        android.app.Activity baseActivity) {
            this.baseActivity = baseActivity;
            if (url.contains("/layout/")) {
                url = url.substring(0, url.indexOf("/layout"));
            }
            java.lang.String rawDat = me.ccrama.redditslide.ImgurAlbum.AlbumUtils.cutEnds(url);
            if (rawDat.endsWith("/")) {
                rawDat = rawDat.substring(0, rawDat.length() - 1);
            }
            if (rawDat.substring(rawDat.lastIndexOf("/") + 1, rawDat.length()).length() < 4) {
                rawDat = rawDat.replace(rawDat.substring(rawDat.lastIndexOf("/"), rawDat.length()), "");
            }
            if (rawDat.contains("?")) {
                rawDat = rawDat.substring(0, rawDat.indexOf("?"));
            }
            hash = me.ccrama.redditslide.ImgurAlbum.AlbumUtils.getHash(rawDat);
            client = me.ccrama.redditslide.Reddit.client;
            gson = new com.google.gson.Gson();
            mashapeKey = me.ccrama.redditslide.SecretConstants.getImgurApiKey(baseActivity);
        }

        public void doWithData(java.util.List<me.ccrama.redditslide.ImgurAlbum.Image> data) {
            if ((data == null) || data.isEmpty()) {
                onError();
            }
        }

        public void doWithDataSingle(final me.ccrama.redditslide.ImgurAlbum.SingleImage data) {
            doWithData(new java.util.ArrayList<me.ccrama.redditslide.ImgurAlbum.Image>() {
                {
                    this.add(convertToSingle(data));
                }
            });
        }

        public me.ccrama.redditslide.ImgurAlbum.Image convertToSingle(me.ccrama.redditslide.ImgurAlbum.SingleImage data) {
            try {
                final me.ccrama.redditslide.ImgurAlbum.Image toDo = new me.ccrama.redditslide.ImgurAlbum.Image();
                toDo.setAnimated(data.getAnimated() || data.getLink().contains(".gif"));
                toDo.setDescription(data.getDescription());
                if (data.getAdditionalProperties().keySet().contains("mp4")) {
                    toDo.setHash(me.ccrama.redditslide.ImgurAlbum.AlbumUtils.getHash(data.getAdditionalProperties().get("mp4").toString()));
                } else {
                    toDo.setHash(me.ccrama.redditslide.ImgurAlbum.AlbumUtils.getHash(data.getLink()));
                }
                toDo.setTitle(data.getTitle());
                toDo.setExt(data.getLink().substring(data.getLink().lastIndexOf("."), data.getLink().length()));
                toDo.setHeight(data.getHeight());
                toDo.setWidth(data.getWidth());
                return toDo;
            } catch (java.lang.Exception e) {
                me.ccrama.redditslide.util.LogUtil.e(e, ("convertToSingle error, data [" + data) + "]");
                onError();
                return null;
            }
        }

        com.google.gson.JsonElement[] target;

        int count;

        int done;

        me.ccrama.redditslide.ImgurAlbum.AlbumImage album;

        public void parseJson(com.google.gson.JsonElement baseData) {
            try {
                if (!baseData.toString().contains("\"data\":[]")) {
                    album = new com.fasterxml.jackson.databind.ObjectMapper().readValue(baseData.toString(), me.ccrama.redditslide.ImgurAlbum.AlbumImage.class);
                    baseActivity.runOnUiThread(new java.lang.Runnable() {
                        @java.lang.Override
                        public void run() {
                            doWithData(album.getData().getImages());
                        }
                    });
                } else {
                    java.lang.String apiUrl = ("https://imgur-apiv3.p.mashape.com/3/image/" + hash) + ".json";
                    me.ccrama.redditslide.util.LogUtil.v(apiUrl);
                    com.google.gson.JsonObject result = me.ccrama.redditslide.util.HttpUtil.getImgurMashapeJsonObject(client, gson, apiUrl, mashapeKey);
                    try {
                        if (result == null) {
                            onError();
                            return;
                        }
                        final me.ccrama.redditslide.ImgurAlbum.SingleImage single = new com.fasterxml.jackson.databind.ObjectMapper().readValue(result.toString(), me.ccrama.redditslide.ImgurAlbum.SingleAlbumImage.class).getData();
                        if (single.getLink() != null) {
                            baseActivity.runOnUiThread(new java.lang.Runnable() {
                                @java.lang.Override
                                public void run() {
                                    doWithDataSingle(single);
                                }
                            });
                        } else {
                            onError();
                        }
                    } catch (java.lang.Exception e) {
                        me.ccrama.redditslide.util.LogUtil.e(e, "Error " + apiUrl);
                    }
                }
            } catch (java.io.IOException e) {
                me.ccrama.redditslide.util.LogUtil.e(e, ("parseJson error, baseData [" + baseData) + "]");
            }
        }

        @java.lang.Override
        protected java.util.ArrayList<com.google.gson.JsonElement> doInBackground(final java.lang.String... sub) {
            if (hash.startsWith("/")) {
                hash = hash.substring(1, hash.length());
            }
            if (hash.contains(",")) {
                target = new com.google.gson.JsonElement[hash.split(",").length];
                count = 0;
                done = 0;
                for (java.lang.String s : hash.split(",")) {
                    final int pos = count;
                    count++;
                    java.lang.String apiUrl = ("https://imgur-apiv3.p.mashape.com/3/image/" + s) + ".json";
                    me.ccrama.redditslide.util.LogUtil.v(apiUrl);
                    com.google.gson.JsonObject result = me.ccrama.redditslide.util.HttpUtil.getImgurMashapeJsonObject(client, gson, apiUrl, mashapeKey);
                    target[pos] = result;
                    done += 1;
                    if (done == target.length) {
                        final java.util.ArrayList<me.ccrama.redditslide.ImgurAlbum.Image> jsons = new java.util.ArrayList<>();
                        for (com.google.gson.JsonElement el : target) {
                            if (el != null) {
                                try {
                                    me.ccrama.redditslide.ImgurAlbum.SingleImage single = new com.fasterxml.jackson.databind.ObjectMapper().readValue(el.toString(), me.ccrama.redditslide.ImgurAlbum.SingleAlbumImage.class).getData();
                                    me.ccrama.redditslide.util.LogUtil.v(el.toString());
                                    jsons.add(convertToSingle(single));
                                } catch (java.io.IOException e) {
                                    me.ccrama.redditslide.util.LogUtil.e(e, "Error " + apiUrl);
                                }
                            }
                        }
                        if (jsons.isEmpty()) {
                            onError();
                        } else {
                            baseActivity.runOnUiThread(new java.lang.Runnable() {
                                @java.lang.Override
                                public void run() {
                                    doWithData(jsons);
                                }
                            });
                        }
                    }
                }
            } else if (baseActivity != null) {
                final java.lang.String apiUrl = me.ccrama.redditslide.ImgurAlbum.AlbumUtils.getUrl(hash);
                if (me.ccrama.redditslide.ImgurAlbum.AlbumUtils.albumRequests.contains(apiUrl) && new com.google.gson.JsonParser().parse(me.ccrama.redditslide.ImgurAlbum.AlbumUtils.albumRequests.getString(apiUrl, "")).getAsJsonObject().has("data")) {
                    parseJson(new com.google.gson.JsonParser().parse(me.ccrama.redditslide.ImgurAlbum.AlbumUtils.albumRequests.getString(apiUrl, "")).getAsJsonObject());
                } else {
                    me.ccrama.redditslide.util.LogUtil.v(apiUrl);
                    // This call requires no mashape headers, don't pass in the headers Map
                    final com.google.gson.JsonObject result = me.ccrama.redditslide.util.HttpUtil.getJsonObject(client, gson, apiUrl);
                    if ((result != null) && result.has("data")) {
                        me.ccrama.redditslide.ImgurAlbum.AlbumUtils.albumRequests.edit().putString(apiUrl, result.toString()).apply();
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

    public static java.lang.String getUrl(java.lang.String hash) {
        return ("http://imgur.com/ajaxalbums/getimages/" + hash) + "/hit.json?all=true";
    }

    public static void preloadImages(android.content.Context c, com.google.gson.JsonObject result, boolean gallery) {
        if (gallery && (result != null)) {
            if (((result.has("data") && result.get("data").getAsJsonObject().has("image")) && result.get("data").getAsJsonObject().get("image").getAsJsonObject().has("album_images")) && result.get("data").getAsJsonObject().get("image").getAsJsonObject().get("album_images").getAsJsonObject().has("images")) {
                com.google.gson.JsonArray obj = result.getAsJsonObject("data").getAsJsonObject("image").getAsJsonObject("album_images").get("images").getAsJsonArray();
                if (((obj != null) && (!obj.isJsonNull())) && (obj.size() > 0)) {
                    for (com.google.gson.JsonElement o : obj) {
                        ((me.ccrama.redditslide.Reddit) (c.getApplicationContext())).getImageLoader().loadImage(("https://imgur.com/" + o.getAsJsonObject().get("hash").getAsString()) + ".png", new com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener());
                    }
                }
            }
        } else if (result != null) {
            if (result.has("album") && result.get("album").getAsJsonObject().has("images")) {
                com.google.gson.JsonObject obj = result.getAsJsonObject("album");
                if (((obj != null) && (!obj.isJsonNull())) && obj.has("images")) {
                    final com.google.gson.JsonArray jsonAuthorsArray = obj.get("images").getAsJsonArray();
                    for (com.google.gson.JsonElement o : jsonAuthorsArray) {
                        ((me.ccrama.redditslide.Reddit) (c.getApplicationContext())).getImageLoader().loadImage(o.getAsJsonObject().getAsJsonObject("links").get("original").getAsString(), new com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener());
                    }
                }
            }
        }
    }
}