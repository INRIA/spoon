package me.ccrama.redditslide.util;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import me.ccrama.redditslide.Reddit;
import java.io.IOException;
public class TitleExtractor {
    /* the CASE_INSENSITIVE flag accounts for
    sites that use uppercase title tags.
    the DOTALL flag accounts for sites that have
    line feeds in the title text
     */
    private static final java.util.regex.Pattern TITLE_TAG = java.util.regex.Pattern.compile("<title[^>]*>(.*?)</title>", java.util.regex.Pattern.CASE_INSENSITIVE | java.util.regex.Pattern.DOTALL);

    private TitleExtractor() {
    }

    /**
     *
     *
     * @param url
     * 		the HTML page
     * @return title text (null if document isn't HTML or lacks a title tag)
     * @throws IOException
     * 		
     */
    public static java.lang.String getPageTitle(java.lang.String url) throws java.io.IOException {
        okhttp3.OkHttpClient client = me.ccrama.redditslide.Reddit.client;
        okhttp3.Request request = new okhttp3.Request.Builder().url(me.ccrama.redditslide.util.LinkUtil.formatURL(url).toString()).addHeader("Accept", "text/html").build();
        okhttp3.Response response = client.newCall(request).execute();
        if (!response.isSuccessful())
            return null;

        java.util.regex.Matcher matcher = me.ccrama.redditslide.util.TitleExtractor.TITLE_TAG.matcher(response.body().string());
        response.body().close();
        if (matcher.find()) {
            return matcher.group(1).replaceAll("\\s+", " ");
        } else {
            return null;
        }
    }
}