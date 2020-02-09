package me.ccrama.redditslide;
import java.util.Locale;
import java.util.HashMap;
import java.io.FileReader;
import java.util.ArrayList;
import java.io.IOException;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Comparator;
import java.io.FileWriter;
import java.io.BufferedReader;
import com.fasterxml.jackson.databind.ObjectReader;
import java.util.List;
import java.io.File;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
/**
 * Created by carlo_000 on 11/19/2015.
 */
public class OfflineSubreddit {
    public static java.lang.Long currentid = 0L;

    private static java.lang.String savedSubmissionsSubreddit = "";

    public long time;

    public java.util.ArrayList<net.dean.jraw.models.Submission> submissions;

    public java.lang.String subreddit;

    public boolean base;

    public static void writeSubmission(com.fasterxml.jackson.databind.JsonNode node, net.dean.jraw.models.Submission s, android.content.Context c) {
        me.ccrama.redditslide.OfflineSubreddit.writeSubmissionToStorage(s, node, c);
    }

    static java.io.File cacheDirectory;

    public static java.io.File getCacheDirectory(android.content.Context context) {
        if ((me.ccrama.redditslide.OfflineSubreddit.cacheDirectory == null) && (context != null)) {
            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED) && (context.getExternalCacheDir() != null)) {
                me.ccrama.redditslide.OfflineSubreddit.cacheDirectory = context.getExternalCacheDir();
            }
            me.ccrama.redditslide.OfflineSubreddit.cacheDirectory = context.getCacheDir();
        }
        return me.ccrama.redditslide.OfflineSubreddit.cacheDirectory;
    }

    public me.ccrama.redditslide.OfflineSubreddit overwriteSubmissions(java.util.List<net.dean.jraw.models.Submission> data) {
        submissions = new java.util.ArrayList<>(data);
        return this;
    }

    public static void writeSubmissionToStorage(net.dean.jraw.models.Submission s, com.fasterxml.jackson.databind.JsonNode node, android.content.Context c) {
        java.io.File toStore = new java.io.File((me.ccrama.redditslide.OfflineSubreddit.getCacheDirectory(c) + java.io.File.separator) + s.getFullName());
        try {
            java.io.FileWriter writer = new java.io.FileWriter(toStore);
            writer.append(node.toString());
            writer.flush();
            writer.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isStored(java.lang.String name, android.content.Context c) {
        return new java.io.File((me.ccrama.redditslide.OfflineSubreddit.getCacheDirectory(c) + java.io.File.separator) + name).exists();
    }

    public void writeToMemory(android.content.Context c) {
        if (me.ccrama.redditslide.OfflineSubreddit.cache == null)
            me.ccrama.redditslide.OfflineSubreddit.cache = new java.util.HashMap<>();

        if (subreddit != null) {
            java.lang.String title = (subreddit.toLowerCase(java.util.Locale.ENGLISH) + ",") + (base ? 0 : time);
            java.lang.String fullNames = "";
            me.ccrama.redditslide.OfflineSubreddit.cache.put(title, this);
            for (net.dean.jraw.models.Submission sub : new java.util.ArrayList<>(submissions)) {
                fullNames += sub.getFullName() + ",";
                if (!isStored(sub.getFullName(), c)) {
                    me.ccrama.redditslide.OfflineSubreddit.writeSubmissionToStorage(sub, sub.getDataNode(), c);
                }
            }
            if (fullNames.length() > 0) {
                me.ccrama.redditslide.Reddit.cachedData.edit().putString(title, fullNames.substring(0, fullNames.length() - 1)).apply();
            }
            me.ccrama.redditslide.OfflineSubreddit.cache.put(title, this);
        }
    }

    public void writeToMemoryNoStorage() {
        if (me.ccrama.redditslide.OfflineSubreddit.cache == null)
            me.ccrama.redditslide.OfflineSubreddit.cache = new java.util.HashMap<>();

        if (subreddit != null) {
            java.lang.String title = (subreddit.toLowerCase(java.util.Locale.ENGLISH) + ",") + (base ? 0 : time);
            java.lang.String fullNames = "";
            for (net.dean.jraw.models.Submission sub : submissions) {
                fullNames += sub.getFullName() + ",";
            }
            if (fullNames.length() > 0) {
                me.ccrama.redditslide.Reddit.cachedData.edit().putString(title, fullNames.substring(0, fullNames.length() - 1)).apply();
            }
            me.ccrama.redditslide.OfflineSubreddit.cache.put(title, this);
        }
    }

    public void writeToMemory(java.util.ArrayList<java.lang.String> names) {
        if ((subreddit != null) && (!names.isEmpty())) {
            java.lang.String title = (subreddit.toLowerCase(java.util.Locale.ENGLISH) + ",") + time;
            java.lang.String fullNames = "";
            for (java.lang.String sub : names) {
                fullNames += sub + ",";
            }
            if (subreddit.equals(me.ccrama.redditslide.CommentCacheAsync.SAVED_SUBMISSIONS)) {
                java.util.Map<java.lang.String, ?> offlineSubs = me.ccrama.redditslide.Reddit.cachedData.getAll();
                for (java.lang.String offlineSub : offlineSubs.keySet()) {
                    if (offlineSub.contains(me.ccrama.redditslide.CommentCacheAsync.SAVED_SUBMISSIONS)) {
                        me.ccrama.redditslide.OfflineSubreddit.savedSubmissionsSubreddit = offlineSub;
                        break;
                    }
                }
                java.lang.String savedSubmissions = me.ccrama.redditslide.Reddit.cachedData.getString(me.ccrama.redditslide.OfflineSubreddit.savedSubmissionsSubreddit, fullNames);
                me.ccrama.redditslide.Reddit.cachedData.edit().remove(me.ccrama.redditslide.OfflineSubreddit.savedSubmissionsSubreddit).apply();
                if (!savedSubmissions.equals(fullNames)) {
                    savedSubmissions = fullNames.concat(savedSubmissions);
                }
                saveToCache(title, savedSubmissions);
            } else {
                saveToCache(title, fullNames);
            }
        }
    }

    public void deleteFromMemory(java.lang.String name) {
        if (subreddit != null) {
            java.lang.String title = (subreddit.toLowerCase(java.util.Locale.ENGLISH) + ",") + time;
            if (subreddit.equals(me.ccrama.redditslide.CommentCacheAsync.SAVED_SUBMISSIONS)) {
                java.util.Map<java.lang.String, ?> offlineSubs = me.ccrama.redditslide.Reddit.cachedData.getAll();
                for (java.lang.String offlineSub : offlineSubs.keySet()) {
                    if (offlineSub.contains(me.ccrama.redditslide.CommentCacheAsync.SAVED_SUBMISSIONS)) {
                        me.ccrama.redditslide.OfflineSubreddit.savedSubmissionsSubreddit = offlineSub;
                        break;
                    }
                }
                java.lang.String savedSubmissions = me.ccrama.redditslide.Reddit.cachedData.getString(me.ccrama.redditslide.OfflineSubreddit.savedSubmissionsSubreddit, name);
                if (!savedSubmissions.equals(name)) {
                    me.ccrama.redditslide.Reddit.cachedData.edit().remove(me.ccrama.redditslide.OfflineSubreddit.savedSubmissionsSubreddit).apply();
                    java.lang.String modifiedSavedSubmissions = savedSubmissions.replace(name + ",", "");
                    saveToCache(title, modifiedSavedSubmissions);
                }
            }
        }
    }

    private void saveToCache(java.lang.String title, java.lang.String submissions) {
        me.ccrama.redditslide.Reddit.cachedData.edit().putString(title, submissions).apply();
    }

    public static me.ccrama.redditslide.OfflineSubreddit getSubreddit(java.lang.String subreddit, boolean offline, android.content.Context c) {
        return me.ccrama.redditslide.OfflineSubreddit.getSubreddit(subreddit, 0L, offline, c);
    }

    public static me.ccrama.redditslide.OfflineSubreddit getSubNoLoad(java.lang.String s) {
        s = s.toLowerCase(java.util.Locale.ENGLISH);
        me.ccrama.redditslide.OfflineSubreddit o = new me.ccrama.redditslide.OfflineSubreddit();
        o.subreddit = s.toLowerCase(java.util.Locale.ENGLISH);
        o.base = true;
        o.time = 0;
        o.submissions = new java.util.ArrayList<>();
        return o;
    }

    private static java.util.HashMap<java.lang.String, me.ccrama.redditslide.OfflineSubreddit> cache;

    public static me.ccrama.redditslide.OfflineSubreddit getSubreddit(java.lang.String subreddit, java.lang.Long time, boolean offline, android.content.Context c) {
        if (me.ccrama.redditslide.OfflineSubreddit.cache == null)
            me.ccrama.redditslide.OfflineSubreddit.cache = new java.util.HashMap<>();

        java.lang.String title;
        if (subreddit != null) {
            title = (subreddit.toLowerCase(java.util.Locale.ENGLISH) + ",") + time;
        } else {
            title = "";
            subreddit = "";
        }
        if (me.ccrama.redditslide.OfflineSubreddit.cache.containsKey(title)) {
            return me.ccrama.redditslide.OfflineSubreddit.cache.get(title);
        } else {
            subreddit = subreddit.toLowerCase(java.util.Locale.ENGLISH);
            me.ccrama.redditslide.OfflineSubreddit o = new me.ccrama.redditslide.OfflineSubreddit();
            o.subreddit = subreddit.toLowerCase(java.util.Locale.ENGLISH);
            if (time == 0) {
                o.base = true;
            }
            o.time = time;
            java.lang.String[] split = me.ccrama.redditslide.Reddit.cachedData.getString((subreddit.toLowerCase(java.util.Locale.ENGLISH) + ",") + time, "").split(",");
            if (split.length > 0) {
                o.time = time;
                o.submissions = new java.util.ArrayList<>();
                com.fasterxml.jackson.databind.ObjectMapper mapperBase = new com.fasterxml.jackson.databind.ObjectMapper();
                com.fasterxml.jackson.databind.ObjectReader reader = mapperBase.reader();
                for (java.lang.String s : split) {
                    if (!s.contains("_"))
                        s = "t3_" + s;

                    if (!s.isEmpty()) {
                        try {
                            net.dean.jraw.models.Submission sub = me.ccrama.redditslide.OfflineSubreddit.getSubmissionFromStorage(s, c, offline, reader);
                            if (sub != null) {
                                o.submissions.add(sub);
                            }
                        } catch (java.io.IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                o.submissions = new java.util.ArrayList<>();
            }
            me.ccrama.redditslide.OfflineSubreddit.cache.put(title, o);
            return o;
        }
    }

    public static net.dean.jraw.models.Submission getSubmissionFromStorage(java.lang.String fullName, android.content.Context c, boolean offline, com.fasterxml.jackson.databind.ObjectReader reader) throws java.io.IOException {
        java.lang.String gotten = me.ccrama.redditslide.OfflineSubreddit.getStringFromFile(fullName, c);
        if (!gotten.isEmpty()) {
            if (gotten.startsWith("[") && offline) {
                return net.dean.jraw.models.meta.SubmissionSerializer.withComments(reader.readTree(gotten), net.dean.jraw.models.CommentSort.CONFIDENCE);
            } else if (gotten.startsWith("[")) {
                com.fasterxml.jackson.databind.JsonNode elem = reader.readTree(gotten);
                return new net.dean.jraw.models.Submission(elem.get(0).get("data").get("children").get(0).get("data"));
            } else {
                return new net.dean.jraw.models.Submission(reader.readTree(gotten));
            }
        }
        return null;
    }

    public static java.lang.String getStringFromFile(java.lang.String name, android.content.Context c) {
        java.io.File f = new java.io.File((me.ccrama.redditslide.OfflineSubreddit.getCacheDirectory(c) + java.io.File.separator) + name);
        if (f.exists()) {
            try {
                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(f));
                char[] chars = new char[((int) (f.length()))];
                reader.read(chars);
                reader.close();
                return new java.lang.String(chars);
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        } else {
            return "";
        }
        return "";
    }

    public static me.ccrama.redditslide.OfflineSubreddit newSubreddit(java.lang.String subreddit) {
        subreddit = subreddit.toLowerCase(java.util.Locale.ENGLISH);
        me.ccrama.redditslide.OfflineSubreddit o = new me.ccrama.redditslide.OfflineSubreddit();
        o.subreddit = subreddit.toLowerCase(java.util.Locale.ENGLISH);
        o.base = false;
        o.time = java.lang.System.currentTimeMillis();
        o.submissions = new java.util.ArrayList<>();
        return o;
    }

    public void clearPost(net.dean.jraw.models.Submission s) {
        if (submissions != null) {
            net.dean.jraw.models.Submission toRemove = null;
            for (net.dean.jraw.models.Submission s2 : submissions) {
                if (s.getFullName().equals(s2.getFullName())) {
                    toRemove = s2;
                }
            }
            if (toRemove != null) {
                submissions.remove(toRemove);
            }
        }
    }

    int savedIndex;

    net.dean.jraw.models.Submission savedSubmission;

    public void hide(int index) {
        hide(index, true);
    }

    public void hideMulti(int index) {
        if (submissions != null) {
            submissions.remove(index);
        }
    }

    public void hide(int index, boolean save) {
        if (submissions != null) {
            savedSubmission = submissions.get(index);
            submissions.remove(index);
            savedIndex = index;
            writeToMemoryNoStorage();
        }
    }

    public void unhideLast() {
        if ((submissions != null) && (savedSubmission != null)) {
            submissions.add(savedIndex, savedSubmission);
            writeToMemoryNoStorage();
        }
    }

    public static java.util.ArrayList<java.lang.String> getAll(java.lang.String subreddit) {
        subreddit = subreddit.toLowerCase(java.util.Locale.ENGLISH);
        java.util.ArrayList<java.lang.String> base = new java.util.ArrayList<>();
        for (java.lang.String s : me.ccrama.redditslide.Reddit.cachedData.getAll().keySet()) {
            if (s.startsWith(subreddit) && s.contains(",")) {
                base.add(s);
            }
        }
        java.util.Collections.sort(base, new me.ccrama.redditslide.OfflineSubreddit.MultiComparator());
        return base;
    }

    public static class MultiComparator<T> implements java.util.Comparator<T> {
        public int compare(T o1, T o2) {
            double first = java.lang.Double.valueOf(((java.lang.String) (o1)).split(",")[1]);
            double second = java.lang.Double.valueOf(((java.lang.String) (o2)).split(",")[1]);
            int comparison = (first >= second) ? first == second ? 0 : -1 : 1;
            if (comparison != 0)
                return comparison;

            return 0;
        }
    }

    public static java.util.ArrayList<java.lang.String> getAll() {
        java.util.ArrayList<java.lang.String> keys = new java.util.ArrayList<>();
        for (java.lang.String s : me.ccrama.redditslide.Reddit.cachedData.getAll().keySet()) {
            if (s.contains(",") && (!s.startsWith("multi"))) {
                keys.add(s);
            }
        }
        return keys;
    }

    public static java.util.ArrayList<java.lang.String> getAllFormatted() {
        java.util.ArrayList<java.lang.String> keys = new java.util.ArrayList<>();
        for (java.lang.String s : me.ccrama.redditslide.Reddit.cachedData.getAll().keySet()) {
            if ((s.contains(",") && (!keys.contains(s.substring(0, s.indexOf(","))))) && (!s.startsWith("multi"))) {
                keys.add(s.substring(0, s.indexOf(",")));
            }
        }
        return keys;
    }
}