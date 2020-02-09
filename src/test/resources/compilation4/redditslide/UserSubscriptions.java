package me.ccrama.redditslide;
import me.ccrama.redditslide.Activities.MultiredditOverview;
import java.util.Locale;
import me.ccrama.redditslide.Activities.NewsActivity;
import me.ccrama.redditslide.DragSort.ReorderSubreddits;
import java.util.HashMap;
import java.util.ArrayList;
import me.ccrama.redditslide.Activities.Login;
import me.ccrama.redditslide.Activities.MainActivity;
import me.ccrama.redditslide.util.NetworkUtil;
import me.ccrama.redditslide.Toolbox.Toolbox;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Collections;
/**
 * Created by carlo_000 on 1/16/2016.
 */
public class UserSubscriptions {
    public static final java.lang.String SUB_NAME_TO_PROPERTIES = "multiNameToSubs";

    public static final java.util.List<java.lang.String> defaultSubs = java.util.Arrays.asList("frontpage", "all", "announcements", "Art", "AskReddit", "askscience", "aww", "blog", "books", "creepy", "dataisbeautiful", "DIY", "Documentaries", "EarthPorn", "explainlikeimfive", "Fitness", "food", "funny", "Futurology", "gadgets", "gaming", "GetMotivated", "gifs", "history", "IAmA", "InternetIsBeautiful", "Jokes", "LifeProTips", "listentothis", "mildlyinteresting", "movies", "Music", "news", "nosleep", "nottheonion", "OldSchoolCool", "personalfinance", "philosophy", "photoshopbattles", "pics", "science", "Showerthoughts", "space", "sports", "television", "tifu", "todayilearned", "TwoXChromosomes", "UpliftingNews", "videos", "worldnews", "WritingPrompts");

    public static final java.util.List<java.lang.String> specialSubreddits = java.util.Arrays.asList("frontpage", "all", "random", "randnsfw", "myrandom", "friends", "mod", "popular");

    public static android.content.SharedPreferences subscriptions;

    public static android.content.SharedPreferences multiNameToSubs;

    public static android.content.SharedPreferences newsNameToSubs;

    public static android.content.SharedPreferences news;

    public static android.content.SharedPreferences pinned;

    public static void setSubNameToProperties(java.lang.String name, java.lang.String descrption) {
        me.ccrama.redditslide.UserSubscriptions.multiNameToSubs.edit().putString(name, descrption).apply();
    }

    public static java.util.Map<java.lang.String, java.lang.String> getMultiNameToSubs(boolean all) {
        java.util.Map<java.lang.String, java.lang.String> multiNameToSubsMapBase = new java.util.HashMap<>();
        java.util.Map<java.lang.String, ?> multiNameToSubsObject = me.ccrama.redditslide.UserSubscriptions.multiNameToSubs.getAll();
        for (java.util.Map.Entry<java.lang.String, ?> entry : multiNameToSubsObject.entrySet()) {
            multiNameToSubsMapBase.put(entry.getKey(), entry.getValue().toString());
        }
        if (all)
            multiNameToSubsMapBase.putAll(me.ccrama.redditslide.UserSubscriptions.getSubsNameToMulti());

        java.util.Map<java.lang.String, java.lang.String> multiNameToSubsMap = new java.util.HashMap<>();
        for (java.util.Map.Entry<java.lang.String, java.lang.String> entries : multiNameToSubsMapBase.entrySet()) {
            multiNameToSubsMap.put(entries.getKey().toLowerCase(java.util.Locale.ENGLISH), entries.getValue());
        }
        return multiNameToSubsMap;
    }

    public static java.util.Map<java.lang.String, java.lang.String> getNewsNameToSubs(boolean all) {
        java.util.Map<java.lang.String, java.lang.String> multiNameToSubsMapBase = new java.util.HashMap<>();
        java.util.Map<java.lang.String, ?> multiNameToSubsObject = me.ccrama.redditslide.UserSubscriptions.newsNameToSubs.getAll();
        for (java.util.Map.Entry<java.lang.String, ?> entry : multiNameToSubsObject.entrySet()) {
            multiNameToSubsMapBase.put(entry.getKey(), entry.getValue().toString());
        }
        if (all)
            multiNameToSubsMapBase.putAll(me.ccrama.redditslide.UserSubscriptions.getSubsNameToMulti());

        java.util.Map<java.lang.String, java.lang.String> multiNameToSubsMap = new java.util.HashMap<>();
        for (java.util.Map.Entry<java.lang.String, java.lang.String> entries : multiNameToSubsMapBase.entrySet()) {
            multiNameToSubsMap.put(entries.getKey().toLowerCase(java.util.Locale.ENGLISH), entries.getValue());
        }
        return multiNameToSubsMap;
    }

    private static java.util.Map<java.lang.String, java.lang.String> getSubsNameToMulti() {
        java.util.Map<java.lang.String, java.lang.String> multiNameToSubsMap = new java.util.HashMap<>();
        java.util.Map<java.lang.String, ?> multiNameToSubsObject = me.ccrama.redditslide.UserSubscriptions.multiNameToSubs.getAll();
        for (java.util.Map.Entry<java.lang.String, ?> entry : multiNameToSubsObject.entrySet()) {
            multiNameToSubsMap.put(entry.getValue().toString(), entry.getKey());
        }
        return multiNameToSubsMap;
    }

    public static void doMainActivitySubs(me.ccrama.redditslide.Activities.MainActivity c) {
        if (me.ccrama.redditslide.util.NetworkUtil.isConnected(c)) {
            java.lang.String s = me.ccrama.redditslide.UserSubscriptions.subscriptions.getString(me.ccrama.redditslide.Authentication.name, "");
            if (s.isEmpty()) {
                // get online subs
                c.updateSubs(me.ccrama.redditslide.UserSubscriptions.syncSubscriptionsOverwrite(c));
            } else {
                me.ccrama.redditslide.CaseInsensitiveArrayList subredditsForHome = new me.ccrama.redditslide.CaseInsensitiveArrayList();
                for (java.lang.String s2 : s.split(",")) {
                    subredditsForHome.add(s2.toLowerCase(java.util.Locale.ENGLISH));
                }
                c.updateSubs(subredditsForHome);
            }
            c.updateMultiNameToSubs(me.ccrama.redditslide.UserSubscriptions.getMultiNameToSubs(false));
        } else {
            java.lang.String s = me.ccrama.redditslide.UserSubscriptions.subscriptions.getString(me.ccrama.redditslide.Authentication.name, "");
            java.util.List<java.lang.String> subredditsForHome = new me.ccrama.redditslide.CaseInsensitiveArrayList();
            if (!s.isEmpty()) {
                for (java.lang.String s2 : s.split(",")) {
                    subredditsForHome.add(s2.toLowerCase(java.util.Locale.ENGLISH));
                }
            }
            me.ccrama.redditslide.CaseInsensitiveArrayList finals = new me.ccrama.redditslide.CaseInsensitiveArrayList();
            java.util.List<java.lang.String> offline = me.ccrama.redditslide.OfflineSubreddit.getAllFormatted();
            for (java.lang.String subs : subredditsForHome) {
                if (offline.contains(subs)) {
                    finals.add(subs);
                }
            }
            for (java.lang.String subs : offline) {
                if (!finals.contains(subs)) {
                    finals.add(subs);
                }
            }
            c.updateSubs(finals);
            c.updateMultiNameToSubs(me.ccrama.redditslide.UserSubscriptions.getMultiNameToSubs(false));
        }
    }

    public static void doNewsSubs(me.ccrama.redditslide.Activities.NewsActivity c) {
        if (me.ccrama.redditslide.util.NetworkUtil.isConnected(c)) {
            java.lang.String s = me.ccrama.redditslide.UserSubscriptions.news.getString("subs", "news,android");
            if (s.isEmpty()) {
                // get online subs
                c.updateSubs(me.ccrama.redditslide.UserSubscriptions.syncSubscriptionsOverwrite(c));
            } else {
                me.ccrama.redditslide.CaseInsensitiveArrayList subredditsForHome = new me.ccrama.redditslide.CaseInsensitiveArrayList();
                for (java.lang.String s2 : s.split(",")) {
                    subredditsForHome.add(s2.toLowerCase(java.util.Locale.ENGLISH));
                }
                c.updateSubs(subredditsForHome);
            }
            c.updateMultiNameToSubs(me.ccrama.redditslide.UserSubscriptions.getNewsNameToSubs(false));
        } else {
            java.lang.String s = me.ccrama.redditslide.UserSubscriptions.news.getString("subs", "news,android");
            java.util.List<java.lang.String> subredditsForHome = new me.ccrama.redditslide.CaseInsensitiveArrayList();
            if (!s.isEmpty()) {
                for (java.lang.String s2 : s.split(",")) {
                    subredditsForHome.add(s2.toLowerCase(java.util.Locale.ENGLISH));
                }
            }
            me.ccrama.redditslide.CaseInsensitiveArrayList finals = new me.ccrama.redditslide.CaseInsensitiveArrayList();
            java.util.List<java.lang.String> offline = me.ccrama.redditslide.OfflineSubreddit.getAllFormatted();
            for (java.lang.String subs : subredditsForHome) {
                if (offline.contains(subs)) {
                    finals.add(subs);
                }
            }
            for (java.lang.String subs : offline) {
                if (!finals.contains(subs)) {
                    finals.add(subs);
                }
            }
            c.updateSubs(finals);
            c.updateMultiNameToSubs(me.ccrama.redditslide.UserSubscriptions.getMultiNameToSubs(false));
        }
    }

    public static void doCachedModSubs() {
        if ((me.ccrama.redditslide.UserSubscriptions.modOf == null) || me.ccrama.redditslide.UserSubscriptions.modOf.isEmpty()) {
            java.lang.String s = me.ccrama.redditslide.UserSubscriptions.subscriptions.getString(me.ccrama.redditslide.Authentication.name + "mod", "");
            if (!s.isEmpty()) {
                me.ccrama.redditslide.UserSubscriptions.modOf = new me.ccrama.redditslide.CaseInsensitiveArrayList();
                for (java.lang.String s2 : s.split(",")) {
                    me.ccrama.redditslide.UserSubscriptions.modOf.add(s2.toLowerCase(java.util.Locale.ENGLISH));
                }
            }
        }
    }

    public static void cacheModOf() {
        me.ccrama.redditslide.UserSubscriptions.subscriptions.edit().putString(me.ccrama.redditslide.Authentication.name + "mod", me.ccrama.redditslide.Reddit.arrayToString(me.ccrama.redditslide.UserSubscriptions.modOf)).apply();
    }

    public static class SyncMultireddits extends android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean> {
        android.content.Context c;

        public SyncMultireddits(android.content.Context c) {
            this.c = c;
        }

        @java.lang.Override
        public void onPostExecute(java.lang.Boolean b) {
            android.content.Intent i = new android.content.Intent(c, me.ccrama.redditslide.Activities.MultiredditOverview.class);
            c.startActivity(i);
            ((android.app.Activity) (c)).finish();
        }

        @java.lang.Override
        public java.lang.Boolean doInBackground(java.lang.Void... params) {
            me.ccrama.redditslide.UserSubscriptions.syncMultiReddits(c);
            return null;
        }
    }

    public static me.ccrama.redditslide.CaseInsensitiveArrayList getSubscriptions(android.content.Context c) {
        java.lang.String s = me.ccrama.redditslide.UserSubscriptions.subscriptions.getString(me.ccrama.redditslide.Authentication.name, "");
        if (s.isEmpty()) {
            // get online subs
            return me.ccrama.redditslide.UserSubscriptions.syncSubscriptionsOverwrite(c);
        } else {
            me.ccrama.redditslide.CaseInsensitiveArrayList subredditsForHome = new me.ccrama.redditslide.CaseInsensitiveArrayList();
            for (java.lang.String s2 : s.split(",")) {
                if (!subredditsForHome.contains(s2))
                    subredditsForHome.add(s2);

            }
            return subredditsForHome;
        }
    }

    public static me.ccrama.redditslide.CaseInsensitiveArrayList pins;

    public static me.ccrama.redditslide.CaseInsensitiveArrayList getPinned() {
        java.lang.String s = me.ccrama.redditslide.UserSubscriptions.pinned.getString(me.ccrama.redditslide.Authentication.name, "");
        if (s.isEmpty()) {
            // get online subs
            return new me.ccrama.redditslide.CaseInsensitiveArrayList();
        } else if (me.ccrama.redditslide.UserSubscriptions.pins == null) {
            me.ccrama.redditslide.UserSubscriptions.pins = new me.ccrama.redditslide.CaseInsensitiveArrayList();
            for (java.lang.String s2 : s.split(",")) {
                if (!me.ccrama.redditslide.UserSubscriptions.pins.contains(s2))
                    me.ccrama.redditslide.UserSubscriptions.pins.add(s2);

            }
            return me.ccrama.redditslide.UserSubscriptions.pins;
        } else {
            return me.ccrama.redditslide.UserSubscriptions.pins;
        }
    }

    public static me.ccrama.redditslide.CaseInsensitiveArrayList getSubscriptionsForShortcut(android.content.Context c) {
        java.lang.String s = me.ccrama.redditslide.UserSubscriptions.subscriptions.getString(me.ccrama.redditslide.Authentication.name, "");
        if (s.isEmpty()) {
            // get online subs
            return me.ccrama.redditslide.UserSubscriptions.syncSubscriptionsOverwrite(c);
        } else {
            me.ccrama.redditslide.CaseInsensitiveArrayList subredditsForHome = new me.ccrama.redditslide.CaseInsensitiveArrayList();
            for (java.lang.String s2 : s.split(",")) {
                if (!s2.contains("/m/"))
                    subredditsForHome.add(s2.toLowerCase(java.util.Locale.ENGLISH));

            }
            return subredditsForHome;
        }
    }

    public static boolean hasSubs() {
        java.lang.String s = me.ccrama.redditslide.UserSubscriptions.subscriptions.getString(me.ccrama.redditslide.Authentication.name, "");
        return s.isEmpty();
    }

    public static me.ccrama.redditslide.CaseInsensitiveArrayList modOf;

    public static java.util.ArrayList<net.dean.jraw.models.MultiReddit> multireddits;

    public static java.util.HashMap<java.lang.String, java.util.List<net.dean.jraw.models.MultiReddit>> public_multireddits = new java.util.HashMap<java.lang.String, java.util.List<net.dean.jraw.models.MultiReddit>>();

    public static void doOnlineSyncing() {
        if (me.ccrama.redditslide.Authentication.mod) {
            me.ccrama.redditslide.UserSubscriptions.doModOf();
            if (me.ccrama.redditslide.UserSubscriptions.modOf != null) {
                for (java.lang.String sub : me.ccrama.redditslide.UserSubscriptions.modOf) {
                    me.ccrama.redditslide.Toolbox.Toolbox.ensureConfigCachedLoaded(sub);
                    me.ccrama.redditslide.Toolbox.Toolbox.ensureUsernotesCachedLoaded(sub);
                }
            }
        }
        me.ccrama.redditslide.UserSubscriptions.doFriendsOf();
        me.ccrama.redditslide.UserSubscriptions.loadMultireddits();
    }

    public static me.ccrama.redditslide.CaseInsensitiveArrayList toreturn;

    public static me.ccrama.redditslide.CaseInsensitiveArrayList friends = new me.ccrama.redditslide.CaseInsensitiveArrayList();

    public static me.ccrama.redditslide.CaseInsensitiveArrayList syncSubscriptionsOverwrite(final android.content.Context c) {
        me.ccrama.redditslide.UserSubscriptions.toreturn = new me.ccrama.redditslide.CaseInsensitiveArrayList();
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
            @java.lang.Override
            protected java.lang.Void doInBackground(java.lang.Void... params) {
                me.ccrama.redditslide.UserSubscriptions.toreturn = me.ccrama.redditslide.UserSubscriptions.syncSubreddits(c);
                me.ccrama.redditslide.UserSubscriptions.toreturn = me.ccrama.redditslide.UserSubscriptions.sort(me.ccrama.redditslide.UserSubscriptions.toreturn);
                me.ccrama.redditslide.UserSubscriptions.setSubscriptions(me.ccrama.redditslide.UserSubscriptions.toreturn);
                return null;
            }
        }.execute();
        if (me.ccrama.redditslide.UserSubscriptions.toreturn.isEmpty()) {
            // failed, load defaults
            me.ccrama.redditslide.UserSubscriptions.toreturn.addAll(me.ccrama.redditslide.UserSubscriptions.defaultSubs);
        }
        return me.ccrama.redditslide.UserSubscriptions.toreturn;
    }

    public static me.ccrama.redditslide.CaseInsensitiveArrayList syncSubreddits(android.content.Context c) {
        me.ccrama.redditslide.CaseInsensitiveArrayList toReturn = new me.ccrama.redditslide.CaseInsensitiveArrayList();
        if (me.ccrama.redditslide.Authentication.isLoggedIn && me.ccrama.redditslide.util.NetworkUtil.isConnected(c)) {
            net.dean.jraw.paginators.UserSubredditsPaginator pag = new net.dean.jraw.paginators.UserSubredditsPaginator(me.ccrama.redditslide.Authentication.reddit, "subscriber");
            pag.setLimit(100);
            try {
                while (pag.hasNext()) {
                    for (net.dean.jraw.models.Subreddit s : pag.next()) {
                        toReturn.add(s.getDisplayName().toLowerCase(java.util.Locale.ENGLISH));
                    }
                } 
                if (toReturn.isEmpty() && me.ccrama.redditslide.UserSubscriptions.subscriptions.getString(me.ccrama.redditslide.Authentication.name, "").isEmpty()) {
                    me.ccrama.redditslide.UserSubscriptions.toreturn.addAll(me.ccrama.redditslide.UserSubscriptions.defaultSubs);
                }
            } catch (java.lang.Exception e) {
                // failed;
                e.printStackTrace();
            }
            me.ccrama.redditslide.UserSubscriptions.addSubsToHistory(toReturn, true);
            return toReturn;
        } else {
            toReturn.addAll(me.ccrama.redditslide.UserSubscriptions.defaultSubs);
            return toReturn;
        }
    }

    public static void syncMultiReddits(android.content.Context c) {
        try {
            me.ccrama.redditslide.UserSubscriptions.multireddits = new java.util.ArrayList<>(new net.dean.jraw.managers.MultiRedditManager(me.ccrama.redditslide.Authentication.reddit).mine());
            for (net.dean.jraw.models.MultiReddit multiReddit : me.ccrama.redditslide.UserSubscriptions.multireddits) {
                if (me.ccrama.redditslide.Activities.MainActivity.multiNameToSubsMap.containsKey(me.ccrama.redditslide.DragSort.ReorderSubreddits.MULTI_REDDIT + multiReddit.getDisplayName())) {
                    java.lang.StringBuilder concatenatedSubs = new java.lang.StringBuilder();
                    for (net.dean.jraw.models.MultiSubreddit subreddit : multiReddit.getSubreddits()) {
                        concatenatedSubs.append(subreddit.getDisplayName());
                        concatenatedSubs.append("+");
                    }
                    me.ccrama.redditslide.Activities.MainActivity.multiNameToSubsMap.put(me.ccrama.redditslide.DragSort.ReorderSubreddits.MULTI_REDDIT + multiReddit.getDisplayName(), concatenatedSubs.toString());
                    me.ccrama.redditslide.UserSubscriptions.setSubNameToProperties(me.ccrama.redditslide.DragSort.ReorderSubreddits.MULTI_REDDIT + multiReddit.getDisplayName(), concatenatedSubs.toString());
                }
            }
        } catch (net.dean.jraw.ApiException e) {
            e.printStackTrace();
        } catch (net.dean.jraw.http.NetworkException e) {
            e.printStackTrace();
        }
    }

    public static void setSubscriptions(me.ccrama.redditslide.CaseInsensitiveArrayList subs) {
        me.ccrama.redditslide.UserSubscriptions.subscriptions.edit().putString(me.ccrama.redditslide.Authentication.name, me.ccrama.redditslide.Reddit.arrayToString(subs)).apply();
    }

    public static void setPinned(me.ccrama.redditslide.CaseInsensitiveArrayList subs) {
        me.ccrama.redditslide.UserSubscriptions.pinned.edit().putString(me.ccrama.redditslide.Authentication.name, me.ccrama.redditslide.Reddit.arrayToString(subs)).apply();
        me.ccrama.redditslide.UserSubscriptions.pins = null;
    }

    public static void switchAccounts() {
        android.content.SharedPreferences.Editor editor = me.ccrama.redditslide.Reddit.appRestart.edit();
        editor.putBoolean("back", true);
        editor.putString("subs", "");
        me.ccrama.redditslide.Authentication.authentication.edit().remove("backedCreds").remove("expires").commit();
        editor.putBoolean("loggedin", me.ccrama.redditslide.Authentication.isLoggedIn);
        editor.putString("name", me.ccrama.redditslide.Authentication.name);
        editor.commit();
    }

    /**
     *
     *
     * @return list of multireddits if they are available, null if could not fetch multireddits
     */
    public static void getMultireddits(final me.ccrama.redditslide.UserSubscriptions.MultiCallback callback) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.util.List<net.dean.jraw.models.MultiReddit>>() {
            @java.lang.Override
            protected java.util.List<net.dean.jraw.models.MultiReddit> doInBackground(java.lang.Void... params) {
                me.ccrama.redditslide.UserSubscriptions.loadMultireddits();
                return me.ccrama.redditslide.UserSubscriptions.multireddits;
            }

            @java.lang.Override
            protected void onPostExecute(java.util.List<net.dean.jraw.models.MultiReddit> multiReddits) {
                callback.onComplete(multiReddits);
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public interface MultiCallback {
        void onComplete(java.util.List<net.dean.jraw.models.MultiReddit> multis);
    }

    public static void loadMultireddits() {
        if ((me.ccrama.redditslide.Authentication.isLoggedIn && me.ccrama.redditslide.Authentication.didOnline) && ((me.ccrama.redditslide.UserSubscriptions.multireddits == null) || me.ccrama.redditslide.UserSubscriptions.multireddits.isEmpty())) {
            try {
                me.ccrama.redditslide.UserSubscriptions.multireddits = new java.util.ArrayList<>(new net.dean.jraw.managers.MultiRedditManager(me.ccrama.redditslide.Authentication.reddit).mine());
            } catch (java.lang.Exception e) {
                me.ccrama.redditslide.UserSubscriptions.multireddits = null;
                e.printStackTrace();
            }
        }
    }

    /**
     *
     *
     * @return list of multireddits if they are available, null if could not fetch multireddits
     */
    public static void getPublicMultireddits(me.ccrama.redditslide.UserSubscriptions.MultiCallback callback, final java.lang.String profile) {
        if (profile.isEmpty()) {
            me.ccrama.redditslide.UserSubscriptions.getMultireddits(callback);
        }
        if (me.ccrama.redditslide.UserSubscriptions.public_multireddits.get(profile) == null) {
            // It appears your own multis are pre-loaded at some point
            // but some other user's multis obviously can't be so
            // don't return until we've loaded them.
            me.ccrama.redditslide.UserSubscriptions.loadPublicMultireddits(callback, profile);
        } else {
            callback.onComplete(me.ccrama.redditslide.UserSubscriptions.public_multireddits.get(profile));
        }
    }

    private static void loadPublicMultireddits(final me.ccrama.redditslide.UserSubscriptions.MultiCallback callback, final java.lang.String profile) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.util.List<net.dean.jraw.models.MultiReddit>>() {
            @java.lang.Override
            protected java.util.List<net.dean.jraw.models.MultiReddit> doInBackground(java.lang.Void... params) {
                try {
                    me.ccrama.redditslide.UserSubscriptions.public_multireddits.put(profile, new java.util.ArrayList(new net.dean.jraw.managers.MultiRedditManager(me.ccrama.redditslide.Authentication.reddit).getPublicMultis(profile)));
                } catch (java.lang.Exception e) {
                    me.ccrama.redditslide.UserSubscriptions.public_multireddits.put(profile, null);
                    e.printStackTrace();
                }
                return me.ccrama.redditslide.UserSubscriptions.public_multireddits.get(profile);
            }

            @java.lang.Override
            protected void onPostExecute(java.util.List<net.dean.jraw.models.MultiReddit> multiReddits) {
                callback.onComplete(multiReddits);
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static me.ccrama.redditslide.CaseInsensitiveArrayList doModOf() {
        me.ccrama.redditslide.CaseInsensitiveArrayList finished = new me.ccrama.redditslide.CaseInsensitiveArrayList();
        net.dean.jraw.paginators.UserSubredditsPaginator pag = new net.dean.jraw.paginators.UserSubredditsPaginator(me.ccrama.redditslide.Authentication.reddit, "moderator");
        pag.setLimit(100);
        try {
            while (pag.hasNext()) {
                for (net.dean.jraw.models.Subreddit s : pag.next()) {
                    finished.add(s.getDisplayName().toLowerCase(java.util.Locale.ENGLISH));
                }
            } 
            me.ccrama.redditslide.UserSubscriptions.modOf = finished;
            me.ccrama.redditslide.UserSubscriptions.cacheModOf();
        } catch (java.lang.Exception e) {
            // failed;
            e.printStackTrace();
        }
        return finished;
    }

    public static void doFriendsOfMain(me.ccrama.redditslide.Activities.MainActivity main) {
        main.doFriends(me.ccrama.redditslide.UserSubscriptions.doFriendsOf());
    }

    private static java.util.List<java.lang.String> doFriendsOf() {
        if ((me.ccrama.redditslide.UserSubscriptions.friends == null) || me.ccrama.redditslide.UserSubscriptions.friends.isEmpty()) {
            me.ccrama.redditslide.UserSubscriptions.friends = new me.ccrama.redditslide.CaseInsensitiveArrayList();
            me.ccrama.redditslide.CaseInsensitiveArrayList finished = new me.ccrama.redditslide.CaseInsensitiveArrayList();
            net.dean.jraw.paginators.ImportantUserPaginator pag = new net.dean.jraw.paginators.ImportantUserPaginator(me.ccrama.redditslide.Authentication.reddit, "friends");
            pag.setLimit(100);
            try {
                while (pag.hasNext()) {
                    for (net.dean.jraw.models.UserRecord s : pag.next()) {
                        finished.add(s.getFullName());
                    }
                } 
                me.ccrama.redditslide.UserSubscriptions.friends = finished;
                return me.ccrama.redditslide.UserSubscriptions.friends;
            } catch (java.lang.Exception e) {
                // failed;
                e.printStackTrace();
            }
        }
        return me.ccrama.redditslide.UserSubscriptions.friends;
    }

    public static net.dean.jraw.models.MultiReddit getMultiredditByDisplayName(java.lang.String displayName) {
        if (me.ccrama.redditslide.UserSubscriptions.multireddits != null) {
            for (net.dean.jraw.models.MultiReddit multiReddit : me.ccrama.redditslide.UserSubscriptions.multireddits) {
                if (multiReddit.getDisplayName().equals(displayName)) {
                    return multiReddit;
                }
            }
        }
        return null;
    }

    public static net.dean.jraw.models.MultiReddit getPublicMultiredditByDisplayName(java.lang.String profile, java.lang.String displayName) {
        if (profile.isEmpty()) {
            return me.ccrama.redditslide.UserSubscriptions.getMultiredditByDisplayName(displayName);
        }
        if (me.ccrama.redditslide.UserSubscriptions.public_multireddits.get(profile) != null) {
            for (net.dean.jraw.models.MultiReddit multiReddit : me.ccrama.redditslide.UserSubscriptions.public_multireddits.get(profile)) {
                if (multiReddit.getDisplayName().equals(displayName)) {
                    return multiReddit;
                }
            }
        }
        return null;
    }

    // Gets user subscriptions + top 500 subs + subs in history
    public static me.ccrama.redditslide.CaseInsensitiveArrayList getAllSubreddits(android.content.Context c) {
        me.ccrama.redditslide.CaseInsensitiveArrayList finalReturn = new me.ccrama.redditslide.CaseInsensitiveArrayList();
        me.ccrama.redditslide.CaseInsensitiveArrayList history = me.ccrama.redditslide.UserSubscriptions.getHistory();
        me.ccrama.redditslide.CaseInsensitiveArrayList defaults = me.ccrama.redditslide.UserSubscriptions.getDefaults(c);
        finalReturn.addAll(me.ccrama.redditslide.UserSubscriptions.getSubscriptions(c));
        for (java.lang.String s : finalReturn) {
            if (history.contains(s)) {
                history.remove(s);
            }
            if (defaults.contains(s)) {
                defaults.remove(s);
            }
        }
        for (java.lang.String s : history) {
            if (defaults.contains(s)) {
                defaults.remove(s);
            }
        }
        for (java.lang.String s : history) {
            if (!finalReturn.contains(s)) {
                finalReturn.add(s);
            }
        }
        for (java.lang.String s : defaults) {
            if (!finalReturn.contains(s)) {
                finalReturn.add(s);
            }
        }
        return finalReturn;
    }

    // Gets user subscriptions + top 500 subs + subs in history
    public static me.ccrama.redditslide.CaseInsensitiveArrayList getAllUserSubreddits(android.content.Context c) {
        me.ccrama.redditslide.CaseInsensitiveArrayList finalReturn = new me.ccrama.redditslide.CaseInsensitiveArrayList();
        finalReturn.addAll(me.ccrama.redditslide.UserSubscriptions.getSubscriptions(c));
        finalReturn.removeAll(me.ccrama.redditslide.UserSubscriptions.getHistory());
        finalReturn.addAll(me.ccrama.redditslide.UserSubscriptions.getHistory());
        return finalReturn;
    }

    public static me.ccrama.redditslide.CaseInsensitiveArrayList getHistory() {
        java.lang.String[] hist = me.ccrama.redditslide.UserSubscriptions.subscriptions.getString("subhistory", "").toLowerCase(java.util.Locale.ENGLISH).split(",");
        me.ccrama.redditslide.CaseInsensitiveArrayList history = new me.ccrama.redditslide.CaseInsensitiveArrayList();
        java.util.Collections.addAll(history, hist);
        return history;
    }

    public static me.ccrama.redditslide.CaseInsensitiveArrayList getDefaults(android.content.Context c) {
        me.ccrama.redditslide.CaseInsensitiveArrayList history = new me.ccrama.redditslide.CaseInsensitiveArrayList();
        java.util.Collections.addAll(history, c.getString(me.ccrama.redditslide.R.string.top_500_csv).split(","));
        return history;
    }

    public static void addSubreddit(java.lang.String s, android.content.Context c) {
        me.ccrama.redditslide.CaseInsensitiveArrayList subs = me.ccrama.redditslide.UserSubscriptions.getSubscriptions(c);
        subs.add(s);
        if (me.ccrama.redditslide.SettingValues.alphabetizeOnSubscribe) {
            me.ccrama.redditslide.UserSubscriptions.setSubscriptions(me.ccrama.redditslide.UserSubscriptions.sortNoExtras(subs));
        } else {
            me.ccrama.redditslide.UserSubscriptions.setSubscriptions(subs);
        }
    }

    public static void removeSubreddit(java.lang.String s, android.content.Context c) {
        me.ccrama.redditslide.CaseInsensitiveArrayList subs = me.ccrama.redditslide.UserSubscriptions.getSubscriptions(c);
        subs.remove(s);
        me.ccrama.redditslide.UserSubscriptions.setSubscriptions(subs);
    }

    public static void addPinned(java.lang.String s, android.content.Context c) {
        me.ccrama.redditslide.CaseInsensitiveArrayList subs = me.ccrama.redditslide.UserSubscriptions.getPinned();
        subs.add(s);
        me.ccrama.redditslide.UserSubscriptions.setPinned(subs);
    }

    public static void removePinned(java.lang.String s, android.content.Context c) {
        me.ccrama.redditslide.CaseInsensitiveArrayList subs = me.ccrama.redditslide.UserSubscriptions.getPinned();
        subs.remove(s);
        me.ccrama.redditslide.UserSubscriptions.setPinned(subs);
    }

    // Sets sub as "searched for", will apply to all accounts
    public static void addSubToHistory(java.lang.String s) {
        java.lang.String history = me.ccrama.redditslide.UserSubscriptions.subscriptions.getString("subhistory", "");
        if (!history.contains(s.toLowerCase(java.util.Locale.ENGLISH))) {
            history += "," + s.toLowerCase(java.util.Locale.ENGLISH);
            me.ccrama.redditslide.UserSubscriptions.subscriptions.edit().putString("subhistory", history).apply();
        }
    }

    // Sets a list of subreddits as "searched for", will apply to all accounts
    public static void addSubsToHistory(java.util.ArrayList<net.dean.jraw.models.Subreddit> s2) {
        java.lang.String history = me.ccrama.redditslide.UserSubscriptions.subscriptions.getString("subhistory", "").toLowerCase(java.util.Locale.ENGLISH);
        for (net.dean.jraw.models.Subreddit s : s2) {
            if (!history.contains(s.getDisplayName().toLowerCase(java.util.Locale.ENGLISH))) {
                history += "," + s.getDisplayName().toLowerCase(java.util.Locale.ENGLISH);
            }
        }
        me.ccrama.redditslide.UserSubscriptions.subscriptions.edit().putString("subhistory", history).apply();
    }

    public static void addSubsToHistory(me.ccrama.redditslide.CaseInsensitiveArrayList s2, boolean b) {
        java.lang.String history = me.ccrama.redditslide.UserSubscriptions.subscriptions.getString("subhistory", "").toLowerCase(java.util.Locale.ENGLISH);
        for (java.lang.String s : s2) {
            if (!history.contains(s.toLowerCase(java.util.Locale.ENGLISH))) {
                history += "," + s.toLowerCase(java.util.Locale.ENGLISH);
            }
        }
        me.ccrama.redditslide.UserSubscriptions.subscriptions.edit().putString("subhistory", history).apply();
    }

    public static java.util.ArrayList<net.dean.jraw.models.Subreddit> syncSubredditsGetObject() {
        java.util.ArrayList<net.dean.jraw.models.Subreddit> toReturn = new java.util.ArrayList<>();
        if (me.ccrama.redditslide.Authentication.isLoggedIn) {
            net.dean.jraw.paginators.UserSubredditsPaginator pag = new net.dean.jraw.paginators.UserSubredditsPaginator(me.ccrama.redditslide.Authentication.reddit, "subscriber");
            pag.setLimit(100);
            try {
                while (pag.hasNext()) {
                    for (net.dean.jraw.models.Subreddit s : pag.next()) {
                        toReturn.add(s);
                    }
                } 
            } catch (java.lang.Exception e) {
                // failed;
                e.printStackTrace();
            }
            me.ccrama.redditslide.UserSubscriptions.addSubsToHistory(toReturn);
            return toReturn;
        }
        return toReturn;
    }

    public static void syncSubredditsGetObjectAsync(final me.ccrama.redditslide.Activities.Login mainActivity) {
        final java.util.ArrayList<net.dean.jraw.models.Subreddit> toReturn = new java.util.ArrayList<>();
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
            @java.lang.Override
            protected java.lang.Void doInBackground(java.lang.Void... params) {
                if (me.ccrama.redditslide.Authentication.isLoggedIn) {
                    net.dean.jraw.paginators.UserSubredditsPaginator pag = new net.dean.jraw.paginators.UserSubredditsPaginator(me.ccrama.redditslide.Authentication.reddit, "subscriber");
                    pag.setLimit(100);
                    try {
                        while (pag.hasNext()) {
                            for (net.dean.jraw.models.Subreddit s : pag.next()) {
                                toReturn.add(s);
                            }
                        } 
                    } catch (java.lang.Exception e) {
                        // failed;
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @java.lang.Override
            protected void onPostExecute(java.lang.Void aVoid) {
                mainActivity.doLastStuff(toReturn);
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Sorts the subreddit ArrayList, keeping special subreddits at the top of the list (e.g.
     * frontpage, all, the random subreddits). Always adds frontpage and all
     *
     * @param unsorted
     * 		the ArrayList to sort
     * @return the sorted ArrayList
     * @see #sortNoExtras(CaseInsensitiveArrayList)
     */
    public static me.ccrama.redditslide.CaseInsensitiveArrayList sort(me.ccrama.redditslide.CaseInsensitiveArrayList unsorted) {
        me.ccrama.redditslide.CaseInsensitiveArrayList subs = new me.ccrama.redditslide.CaseInsensitiveArrayList(unsorted);
        if (!subs.contains("frontpage")) {
            subs.add("frontpage");
        }
        if (!subs.contains("all")) {
            subs.add("all");
        }
        return me.ccrama.redditslide.UserSubscriptions.sortNoExtras(subs);
    }

    /**
     * Sorts the subreddit ArrayList, keeping special subreddits at the top of the list (e.g.
     * frontpage, all, the random subreddits)
     *
     * @param unsorted
     * 		the ArrayList to sort
     * @return the sorted ArrayList
     * @see #sort(CaseInsensitiveArrayList)
     */
    public static me.ccrama.redditslide.CaseInsensitiveArrayList sortNoExtras(me.ccrama.redditslide.CaseInsensitiveArrayList unsorted) {
        java.util.List<java.lang.String> subs = new me.ccrama.redditslide.CaseInsensitiveArrayList(unsorted);
        me.ccrama.redditslide.CaseInsensitiveArrayList finals = new me.ccrama.redditslide.CaseInsensitiveArrayList();
        for (java.lang.String subreddit : me.ccrama.redditslide.UserSubscriptions.getPinned()) {
            if (subs.contains(subreddit)) {
                subs.remove(subreddit);
                finals.add(subreddit);
            }
        }
        for (java.lang.String subreddit : me.ccrama.redditslide.UserSubscriptions.specialSubreddits) {
            if (subs.contains(subreddit)) {
                subs.remove(subreddit);
                finals.add(subreddit);
            }
        }
        java.util.Collections.sort(subs, java.lang.String.CASE_INSENSITIVE_ORDER);
        finals.addAll(subs);
        return finals;
    }

    public static boolean isSubscriber(java.lang.String s, android.content.Context c) {
        return me.ccrama.redditslide.UserSubscriptions.getSubscriptions(c).contains(s.toLowerCase(java.util.Locale.ENGLISH));
    }

    public static class SubscribeTask extends android.os.AsyncTask<java.lang.String, java.lang.Void, java.lang.Void> {
        android.content.Context context;

        public SubscribeTask(android.content.Context context) {
            this.context = context;
        }

        @java.lang.Override
        protected java.lang.Void doInBackground(java.lang.String... subreddits) {
            final net.dean.jraw.managers.AccountManager m = new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit);
            for (java.lang.String subreddit : subreddits) {
                try {
                    m.subscribe(me.ccrama.redditslide.Authentication.reddit.getSubreddit(subreddit));
                } catch (java.lang.Exception e) {
                    android.widget.Toast.makeText(context, "Couldn't subscribe, subreddit is private, quarantined, or invite only", android.widget.Toast.LENGTH_SHORT).show();
                }
            }
            return null;
        }
    }

    public static class UnsubscribeTask extends android.os.AsyncTask<java.lang.String, java.lang.Void, java.lang.Void> {
        @java.lang.Override
        protected java.lang.Void doInBackground(java.lang.String... subreddits) {
            final net.dean.jraw.managers.AccountManager m = new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit);
            try {
                for (java.lang.String subreddit : subreddits) {
                    m.unsubscribe(me.ccrama.redditslide.Authentication.reddit.getSubreddit(subreddit));
                }
            } catch (java.lang.Exception e) {
            }
            return null;
        }
    }
}