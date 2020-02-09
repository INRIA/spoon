package me.ccrama.redditslide.Toolbox;
import com.google.gson.GsonBuilder;
import me.ccrama.redditslide.Reddit;
import java.util.HashMap;
import me.ccrama.redditslide.Authentication;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.Map;
import com.google.gson.JsonParseException;
import java.util.TreeMap;
import com.google.gson.Gson;
/**
 * Main class for /r/toolbox functionality
 */
public class Toolbox {
    public static final java.util.Map<java.lang.String, java.util.Map<java.lang.String, java.lang.String>> DEFAULT_USERNOTE_TYPES = new java.util.HashMap<>();

    private static final long CACHE_TIME_NONEXISTANT = 604800000;// 7 days


    private static final long CACHE_TIME_CONFIG = 86400000;// 24 hours


    private static final long CACHE_TIME_USERNOTES = 3600000;// 1 hour


    private static java.util.Map<java.lang.String, me.ccrama.redditslide.Toolbox.Usernotes> notes = new java.util.TreeMap<>(java.lang.String.CASE_INSENSITIVE_ORDER);

    private static java.util.Map<java.lang.String, me.ccrama.redditslide.Toolbox.ToolboxConfig> toolboxConfigs = new java.util.TreeMap<>(java.lang.String.CASE_INSENSITIVE_ORDER);

    private static android.content.SharedPreferences cache = me.ccrama.redditslide.Reddit.getAppContext().getSharedPreferences("toolbox_cache", 0);

    static {
        // Set the default usernote types. Yes this is ugly but java sucks for stuff like this.
        java.util.Map<java.lang.String, java.lang.String> goodUser = new java.util.HashMap<>();
        goodUser.put("color", "#008000");
        goodUser.put("text", "Good\u00a0Contributor");
        java.util.Map<java.lang.String, java.lang.String> spamWatch = new java.util.HashMap<>();
        spamWatch.put("color", "#ff00ff");
        spamWatch.put("text", "Spam\u00a0Watch");
        java.util.Map<java.lang.String, java.lang.String> spamWarn = new java.util.HashMap<>();
        spamWarn.put("color", "#800080");
        spamWarn.put("text", "Spam\u00a0Warning");
        java.util.Map<java.lang.String, java.lang.String> abuseWarn = new java.util.HashMap<>();
        abuseWarn.put("color", "#ffa500");
        abuseWarn.put("text", "Abuse\u00a0Warning");
        java.util.Map<java.lang.String, java.lang.String> ban = new java.util.HashMap<>();
        ban.put("color", "#ff0000");
        ban.put("text", "Ban");
        java.util.Map<java.lang.String, java.lang.String> permBan = new java.util.HashMap<>();
        permBan.put("color", "#8b0000");
        permBan.put("text", "Permanent\u00a0Ban");
        java.util.Map<java.lang.String, java.lang.String> botBan = new java.util.HashMap<>();
        botBan.put("color", "#000000");
        botBan.put("text", "Bot\u00a0Ban");
        me.ccrama.redditslide.Toolbox.Toolbox.DEFAULT_USERNOTE_TYPES.put("gooduser", goodUser);
        me.ccrama.redditslide.Toolbox.Toolbox.DEFAULT_USERNOTE_TYPES.put("spamwatch", spamWatch);
        me.ccrama.redditslide.Toolbox.Toolbox.DEFAULT_USERNOTE_TYPES.put("spamwarn", spamWarn);
        me.ccrama.redditslide.Toolbox.Toolbox.DEFAULT_USERNOTE_TYPES.put("abusewarn", abuseWarn);
        me.ccrama.redditslide.Toolbox.Toolbox.DEFAULT_USERNOTE_TYPES.put("ban", ban);
        me.ccrama.redditslide.Toolbox.Toolbox.DEFAULT_USERNOTE_TYPES.put("permban", permBan);
        me.ccrama.redditslide.Toolbox.Toolbox.DEFAULT_USERNOTE_TYPES.put("botban", botBan);
    }

    /**
     * Gets a subreddit's usernotes if we have loaded them
     *
     * @param subreddit
     * 		Sub to get notes for
     * @return Usernotes
     */
    public static me.ccrama.redditslide.Toolbox.Usernotes getUsernotes(java.lang.String subreddit) {
        return me.ccrama.redditslide.Toolbox.Toolbox.notes.get(subreddit);
    }

    /**
     * Gets a subreddit's toolbox config if we have loaded it
     *
     * @param subreddit
     * 		Sub to get config fore
     * @return Toolbox config
     */
    public static me.ccrama.redditslide.Toolbox.ToolboxConfig getConfig(java.lang.String subreddit) {
        return me.ccrama.redditslide.Toolbox.Toolbox.toolboxConfigs.get(subreddit);
    }

    public static void createUsernotes(java.lang.String subreddit) {
        me.ccrama.redditslide.Toolbox.Toolbox.notes.put(subreddit, new me.ccrama.redditslide.Toolbox.Usernotes(6, new me.ccrama.redditslide.Toolbox.Usernotes.UsernotesConstants(new java.lang.String[]{  }, new java.lang.String[]{  }), new java.util.TreeMap<>(java.lang.String.CASE_INSENSITIVE_ORDER), subreddit));
    }

    /**
     * Ensures that a subreddit's config is cached
     *
     * @param subreddit
     * 		Subreddit to cache
     */
    public static void ensureConfigCachedLoaded(java.lang.String subreddit) {
        me.ccrama.redditslide.Toolbox.Toolbox.ensureConfigCachedLoaded(subreddit, true);
    }

    /**
     * Ensures that a subreddit's config is cached
     *
     * @param subreddit
     * 		Subreddit to cache
     * @param thorough
     * 		Whether to reload from net/cache if toolboxConfigs already contains something for subreddit
     */
    public static void ensureConfigCachedLoaded(java.lang.String subreddit, boolean thorough) {
        if ((!thorough) && me.ccrama.redditslide.Toolbox.Toolbox.toolboxConfigs.containsKey(subreddit)) {
            return;
        }
        long lastCached = me.ccrama.redditslide.Toolbox.Toolbox.cache.getLong(subreddit + "_config_timestamp", -1);
        boolean exists = me.ccrama.redditslide.Toolbox.Toolbox.cache.getBoolean(subreddit + "_config_exists", true);
        if ((((!exists) && ((java.lang.System.currentTimeMillis() - lastCached) > me.ccrama.redditslide.Toolbox.Toolbox.CACHE_TIME_NONEXISTANT))// Sub doesn't have config
         || ((java.lang.System.currentTimeMillis() - lastCached) > me.ccrama.redditslide.Toolbox.Toolbox.CACHE_TIME_CONFIG))// Config outdated
         || (lastCached == (-1))) {
            // Config not cached
            new me.ccrama.redditslide.Toolbox.Toolbox.AsyncLoadToolboxConfig().executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR, subreddit);
        } else {
            com.google.gson.Gson gson = new com.google.gson.Gson();
            try {
                me.ccrama.redditslide.Toolbox.ToolboxConfig result = gson.fromJson(me.ccrama.redditslide.Toolbox.Toolbox.cache.getString(subreddit + "_config_data", null), me.ccrama.redditslide.Toolbox.ToolboxConfig.class);
                if ((result != null) && (result.getSchema() == 1)) {
                    me.ccrama.redditslide.Toolbox.Toolbox.toolboxConfigs.put(subreddit, result);
                }
            } catch (com.google.gson.JsonParseException e) {
                // cached config was invalid
                new me.ccrama.redditslide.Toolbox.Toolbox.AsyncLoadToolboxConfig().executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR, subreddit);
            }
        }
    }

    /**
     * Ensures that a subreddit's usernotes are cached
     *
     * @param subreddit
     * 		Subreddit to cache
     */
    public static void ensureUsernotesCachedLoaded(java.lang.String subreddit) {
        me.ccrama.redditslide.Toolbox.Toolbox.ensureUsernotesCachedLoaded(subreddit, true);
    }

    /**
     * Ensures that a subreddit's usernotes are cached
     *
     * @param subreddit
     * 		Subreddit to cache
     * @param thorough
     * 		Whether to reload from net/cache if notes already contains something for subreddit
     */
    public static void ensureUsernotesCachedLoaded(java.lang.String subreddit, boolean thorough) {
        if ((!thorough) && me.ccrama.redditslide.Toolbox.Toolbox.notes.containsKey(subreddit)) {
            return;
        }
        long lastCached = me.ccrama.redditslide.Toolbox.Toolbox.cache.getLong(subreddit + "_usernotes_timestamp", -1);
        boolean exists = me.ccrama.redditslide.Toolbox.Toolbox.cache.getBoolean(subreddit + "_usernotes_exists", true);
        if ((((!exists) && ((java.lang.System.currentTimeMillis() - lastCached) > me.ccrama.redditslide.Toolbox.Toolbox.CACHE_TIME_NONEXISTANT))// Sub doesn't have usernotes
         || ((java.lang.System.currentTimeMillis() - lastCached) > me.ccrama.redditslide.Toolbox.Toolbox.CACHE_TIME_USERNOTES))// Usernotes outdated
         || (lastCached == (-1))) {
            // Usernotes not cached
            new me.ccrama.redditslide.Toolbox.Toolbox.AsyncLoadUsernotes().executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR, subreddit);
        } else {
            com.google.gson.Gson gson = new com.google.gson.GsonBuilder().registerTypeAdapter(new com.google.gson.reflect.TypeToken<java.util.Map<java.lang.String, java.util.List<me.ccrama.redditslide.Toolbox.Usernote>>>() {}.getType(), new me.ccrama.redditslide.Toolbox.Usernotes.BlobDeserializer()).create();
            try {
                me.ccrama.redditslide.Toolbox.Usernotes result = gson.fromJson(me.ccrama.redditslide.Toolbox.Toolbox.cache.getString(subreddit + "_usernotes_data", null), me.ccrama.redditslide.Toolbox.Usernotes.class);
                if ((result != null) && (result.getSchema() == 6)) {
                    result.setSubreddit(subreddit);
                    me.ccrama.redditslide.Toolbox.Toolbox.notes.put(subreddit, result);
                } else {
                    me.ccrama.redditslide.Toolbox.Toolbox.notes.remove(subreddit);
                }
            } catch (com.google.gson.JsonParseException e) {
                // cached usernotes were invalid
                new me.ccrama.redditslide.Toolbox.Toolbox.AsyncLoadUsernotes().executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR, subreddit);
            }
        }
    }

    /**
     * Download a subreddit's usernotes
     *
     * @param subreddit
     * 		
     */
    public static void downloadUsernotes(java.lang.String subreddit) {
        net.dean.jraw.managers.WikiManager manager = new net.dean.jraw.managers.WikiManager(me.ccrama.redditslide.Authentication.reddit);
        com.google.gson.Gson gson = new com.google.gson.GsonBuilder().registerTypeAdapter(new com.google.gson.reflect.TypeToken<java.util.Map<java.lang.String, java.util.List<me.ccrama.redditslide.Toolbox.Usernote>>>() {}.getType(), new me.ccrama.redditslide.Toolbox.Usernotes.BlobDeserializer()).create();
        try {
            java.lang.String data = manager.get(subreddit, "usernotes").getContent();
            me.ccrama.redditslide.Toolbox.Usernotes result = gson.fromJson(data, me.ccrama.redditslide.Toolbox.Usernotes.class);
            me.ccrama.redditslide.Toolbox.Toolbox.cache.edit().putLong(subreddit + "_usernotes_timestamp", java.lang.System.currentTimeMillis()).apply();
            if ((result != null) && (result.getSchema() == 6)) {
                result.setSubreddit(subreddit);
                me.ccrama.redditslide.Toolbox.Toolbox.notes.put(subreddit, result);
                me.ccrama.redditslide.Toolbox.Toolbox.cache.edit().putBoolean(subreddit + "_usernotes_exists", true).putString(subreddit + "_usernotes_data", data).apply();
            } else {
                me.ccrama.redditslide.Toolbox.Toolbox.cache.edit().putBoolean(subreddit + "_usernotes_exists", false).apply();
            }
        } catch (net.dean.jraw.http.NetworkException | com.google.gson.JsonParseException e) {
            if (e instanceof com.google.gson.JsonParseException) {
                me.ccrama.redditslide.Toolbox.Toolbox.notes.remove(subreddit);
            }
            me.ccrama.redditslide.Toolbox.Toolbox.cache.edit().putLong(subreddit + "_usernotes_timestamp", java.lang.System.currentTimeMillis()).putBoolean(subreddit + "_usernotes_exists", false).apply();
        }
    }

    /**
     * Download a subreddit's Toolbox config
     *
     * @param subreddit
     * 		
     */
    public static void downloadToolboxConfig(java.lang.String subreddit) {
        net.dean.jraw.managers.WikiManager manager = new net.dean.jraw.managers.WikiManager(me.ccrama.redditslide.Authentication.reddit);
        com.google.gson.Gson gson = new com.google.gson.Gson();
        try {
            java.lang.String data = manager.get(subreddit, "toolbox").getContent();
            me.ccrama.redditslide.Toolbox.ToolboxConfig result = gson.fromJson(data, me.ccrama.redditslide.Toolbox.ToolboxConfig.class);
            me.ccrama.redditslide.Toolbox.Toolbox.cache.edit().putLong(subreddit + "_config_timestamp", java.lang.System.currentTimeMillis()).apply();
            if ((result != null) && (result.getSchema() == 1)) {
                me.ccrama.redditslide.Toolbox.Toolbox.toolboxConfigs.put(subreddit, result);
                me.ccrama.redditslide.Toolbox.Toolbox.cache.edit().putBoolean(subreddit + "_config_exists", true).putString(subreddit + "_config_data", data).apply();
            } else {
                me.ccrama.redditslide.Toolbox.Toolbox.cache.edit().putBoolean(subreddit + "_config_exists", false).apply();
            }
        } catch (net.dean.jraw.http.NetworkException | com.google.gson.JsonParseException e) {
            if (e instanceof com.google.gson.JsonParseException) {
                me.ccrama.redditslide.Toolbox.Toolbox.toolboxConfigs.remove(subreddit);
            }
            me.ccrama.redditslide.Toolbox.Toolbox.cache.edit().putLong(subreddit + "_config_timestamp", java.lang.System.currentTimeMillis()).putBoolean(subreddit + "_config_exists", false).apply();
        }
    }

    /**
     * Upload a subreddit's usernotes to the wiki
     *
     * @param subreddit
     * 		Sub to upload usernotes for
     * @param editReason
     * 		Reason for the wiki edit
     */
    public static void uploadUsernotes(java.lang.String subreddit, java.lang.String editReason) {
        net.dean.jraw.managers.WikiManager manager = new net.dean.jraw.managers.WikiManager(me.ccrama.redditslide.Authentication.reddit);
        com.google.gson.Gson gson = new com.google.gson.GsonBuilder().registerTypeAdapter(new com.google.gson.reflect.TypeToken<java.util.Map<java.lang.String, java.util.List<me.ccrama.redditslide.Toolbox.Usernote>>>() {}.getType(), new me.ccrama.redditslide.Toolbox.Usernotes.BlobSerializer()).disableHtmlEscaping().create();
        java.lang.String data = gson.toJson(me.ccrama.redditslide.Toolbox.Toolbox.getUsernotes(subreddit));
        try {
            manager.edit(subreddit, "usernotes", data, ("\"" + editReason) + "\" via Slide");
            me.ccrama.redditslide.Toolbox.Toolbox.cache.edit().putBoolean(subreddit + "_usernotes_exists", true).putLong(subreddit + "_usernotes_timestamp", java.lang.System.currentTimeMillis()).putString(subreddit + "_usernotes_data", data).apply();
        } catch (net.dean.jraw.http.NetworkException | net.dean.jraw.ApiException e) {
            me.ccrama.redditslide.Toolbox.Toolbox.ensureUsernotesCachedLoaded(subreddit);// load back from cache if we failed to upload. keeps state correct

        }
    }

    private static class AsyncLoadUsernotes extends android.os.AsyncTask<java.lang.String, java.lang.Void, java.lang.Void> {
        @java.lang.Override
        protected java.lang.Void doInBackground(java.lang.String... subreddit) {
            me.ccrama.redditslide.Toolbox.Toolbox.downloadUsernotes(subreddit[0]);
            return null;
        }
    }

    private static class AsyncLoadToolboxConfig extends android.os.AsyncTask<java.lang.String, java.lang.Void, java.lang.Void> {
        @java.lang.Override
        protected java.lang.Void doInBackground(java.lang.String... subreddit) {
            me.ccrama.redditslide.Toolbox.Toolbox.downloadToolboxConfig(subreddit[0]);
            return null;
        }
    }
}