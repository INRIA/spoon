package me.ccrama.redditslide.util;
import com.google.gson.JsonSyntaxException;
import me.ccrama.redditslide.SecretConstants;
import java.util.HashMap;
import com.google.gson.JsonObject;
import me.ccrama.redditslide.Constants;
import java.io.IOException;
import java.util.Map;
import com.google.gson.Gson;
/**
 * A class that helps with HTTP requests and response parsing.
 *
 * Created by Fernando Barillas on 7/13/16.
 */
public class HttpUtil {
    /**
     * Gets a JsonObject by calling apiUrl and parsing the JSON response String. This method should
     * be used when calling the Imgur Mashape API (https://imgur-apiv3.p.mashape.com/) since it
     * requires special headers in the requests.
     *
     * @param client
     * 		The OkHTTP client to use to make the request
     * @param gson
     * 		The GSON instance to use to parse the response String
     * @param apiUrl
     * 		The URL to call to get the response from
     * @param mashapeKey
     * 		The Mashape API key to use when the request is made
     * @return A JsonObject representation of the API response, null when there was an error or
    Exception thrown by the HTTP call
     */
    public static com.google.gson.JsonObject getImgurMashapeJsonObject(final okhttp3.OkHttpClient client, final com.google.gson.Gson gson, final java.lang.String apiUrl, final java.lang.String mashapeKey) {
        java.util.Map<java.lang.String, java.lang.String> imgurHeadersMap = new java.util.HashMap<>();
        imgurHeadersMap.put("X-Mashape-Key", mashapeKey);
        imgurHeadersMap.put("Authorization", "Client-ID " + me.ccrama.redditslide.Constants.IMGUR_MASHAPE_CLIENT_ID);
        return me.ccrama.redditslide.util.HttpUtil.getJsonObject(client, gson, apiUrl, imgurHeadersMap);
    }

    /**
     * Gets a JsonObject by calling apiUrl and parsing the JSON response String. This method accepts
     * a Map that can contain custom headers to include in the request.
     *
     * @param client
     * 		The OkHTTP client to use to make the request
     * @param gson
     * 		The GSON instance to use to parse the response String
     * @param apiUrl
     * 		The URL to call to get the response from
     * @param headersMap
     * 		The headers to include in the request. Can be null to not add any headers
     * @return A JsonObject representation of the API response, null when there was an error or
    Exception thrown by the HTTP call
     */
    public static com.google.gson.JsonObject getJsonObject(final okhttp3.OkHttpClient client, final com.google.gson.Gson gson, final java.lang.String apiUrl, @android.support.annotation.Nullable
    final java.util.Map<java.lang.String, java.lang.String> headersMap) {
        if (((client == null) || (gson == null)) || android.text.TextUtils.isEmpty(apiUrl))
            return null;

        okhttp3.Request.Builder builder = new okhttp3.Request.Builder().url(apiUrl);
        if ((headersMap != null) && (headersMap.size() > 0)) {
            // Add headers to the request if headers are available
            for (java.util.Map.Entry<java.lang.String, java.lang.String> entry : headersMap.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        okhttp3.Request request = builder.build();
        try {
            okhttp3.Response response = client.newCall(request).execute();
            if (!response.isSuccessful())
                throw new java.io.IOException("Unexpected code " + response);

            okhttp3.ResponseBody responseBody = response.body();
            java.lang.String json = responseBody.string();
            responseBody.close();
            return gson.fromJson(json, com.google.gson.JsonObject.class);
        } catch (com.google.gson.JsonSyntaxException | java.io.IOException e) {
            me.ccrama.redditslide.util.LogUtil.e(e, "Error " + apiUrl);
        }
        return null;
    }

    /**
     * Gets a JsonObject by calling apiUrl and parsing the JSON response String
     *
     * @param client
     * 		The OkHTTP client to use to make the request
     * @param gson
     * 		The GSON instance to use to parse the response String
     * @param apiUrl
     * 		The URL to call to get the response from
     * @return A JsonObject representation of the API response, null when there was an error or
    Exception thrown by the HTTP call
     */
    public static com.google.gson.JsonObject getJsonObject(final okhttp3.OkHttpClient client, final com.google.gson.Gson gson, final java.lang.String apiUrl) {
        return me.ccrama.redditslide.util.HttpUtil.getJsonObject(client, gson, apiUrl, null);
    }
}