package me.ccrama.redditslide;
import java.net.URL;
import java.util.Locale;
import java.util.Set;
import java.net.MalformedURLException;
/**
 * Created by carlo_000 on 1/13/2016.
 */
public class PostMatch {
    /**
     * Checks if a string is totally or partially contained in a set of strings
     *
     * @param target
     * 		string to check
     * @param strings
     * 		set of strings to check in
     * @param totalMatch
     * 		only allow total match, no partial matches
     * @return if the string is contained in the set of strings
     */
    public static boolean contains(java.lang.String target, java.util.Set<java.lang.String> strings, boolean totalMatch) {
        // filters are always stored lowercase
        if (totalMatch) {
            return strings.contains(target.toLowerCase(java.util.Locale.ENGLISH).trim());
        } else if (strings.contains(target.toLowerCase(java.util.Locale.ENGLISH).trim())) {
            return true;
        } else {
            for (java.lang.String s : strings) {
                if (target.toLowerCase(java.util.Locale.ENGLISH).trim().contains(s)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Checks if a domain should be filtered or not: returns true if the target domain ends with the
     * comparison domain and if supplied, target path begins with the comparison path
     *
     * @param target
     * 		URL to check
     * @param strings
     * 		The URLs to check against
     * @return If the target is covered by any strings
     * @throws MalformedURLException
     * 		
     */
    public static boolean isDomain(java.lang.String target, java.util.Set<java.lang.String> strings) throws java.net.MalformedURLException {
        java.net.URL domain = new java.net.URL(target);
        for (java.lang.String s : strings) {
            if (!s.contains("/")) {
                if (me.ccrama.redditslide.ContentType.hostContains(domain.getHost(), s)) {
                    return true;
                } else {
                    continue;
                }
            }
            if (!s.contains("://")) {
                s = "http://" + s;
            }
            try {
                java.net.URL comparison = new java.net.URL(s.toLowerCase(java.util.Locale.ENGLISH));
                if (me.ccrama.redditslide.ContentType.hostContains(domain.getHost(), comparison.getHost()) && domain.getPath().startsWith(comparison.getPath())) {
                    return true;
                }
            } catch (java.net.MalformedURLException ignored) {
            }
        }
        return false;
    }

    public static boolean openExternal(java.lang.String url) {
        try {
            return me.ccrama.redditslide.PostMatch.isDomain(url.toLowerCase(java.util.Locale.ENGLISH), me.ccrama.redditslide.SettingValues.alwaysExternal);
        } catch (java.net.MalformedURLException e) {
            return false;
        }
    }

    public static android.content.SharedPreferences filters;

    public static boolean doesMatch(net.dean.jraw.models.Submission s, java.lang.String baseSubreddit, boolean ignore18) {
        if (me.ccrama.redditslide.Hidden.id.contains(s.getFullName()))
            return true;
        // if it's hidden we're not going to show it regardless

        java.lang.String title = s.getTitle();
        java.lang.String body = s.getSelftext();
        java.lang.String domain = s.getUrl();
        java.lang.String subreddit = s.getSubredditName();
        java.lang.String flair = (s.getSubmissionFlair().getText() != null) ? s.getSubmissionFlair().getText() : "";
        if (me.ccrama.redditslide.PostMatch.contains(title, me.ccrama.redditslide.SettingValues.titleFilters, false))
            return true;

        if (me.ccrama.redditslide.PostMatch.contains(body, me.ccrama.redditslide.SettingValues.textFilters, false))
            return true;

        if (me.ccrama.redditslide.PostMatch.contains(s.getAuthor(), me.ccrama.redditslide.SettingValues.userFilters, false))
            return true;

        try {
            if (me.ccrama.redditslide.PostMatch.isDomain(domain.toLowerCase(java.util.Locale.ENGLISH), me.ccrama.redditslide.SettingValues.domainFilters))
                return true;

        } catch (java.net.MalformedURLException ignored) {
        }
        if ((!subreddit.equalsIgnoreCase(baseSubreddit)) && me.ccrama.redditslide.PostMatch.contains(subreddit, me.ccrama.redditslide.SettingValues.subredditFilters, true)) {
            return true;
        }
        boolean contentMatch = false;
        if ((baseSubreddit == null) || baseSubreddit.isEmpty()) {
            baseSubreddit = "frontpage";
        }
        baseSubreddit = baseSubreddit.toLowerCase(java.util.Locale.ENGLISH);
        boolean gifs = me.ccrama.redditslide.PostMatch.isGif(baseSubreddit);
        boolean images = me.ccrama.redditslide.PostMatch.isImage(baseSubreddit);
        boolean nsfw = me.ccrama.redditslide.PostMatch.isNsfw(baseSubreddit);
        boolean albums = me.ccrama.redditslide.PostMatch.isAlbums(baseSubreddit);
        boolean urls = me.ccrama.redditslide.PostMatch.isUrls(baseSubreddit);
        boolean selftext = me.ccrama.redditslide.PostMatch.isSelftext(baseSubreddit);
        boolean videos = me.ccrama.redditslide.PostMatch.isVideo(baseSubreddit);
        if (s.isNsfw()) {
            if (!me.ccrama.redditslide.SettingValues.showNSFWContent) {
                contentMatch = true;
            }
            if (ignore18) {
                contentMatch = false;
            }
            if (nsfw) {
                contentMatch = true;
            }
        }
        switch (me.ccrama.redditslide.ContentType.getContentType(s)) {
            case REDDIT :
            case EMBEDDED :
            case LINK :
                if (urls) {
                    contentMatch = true;
                }
                break;
            case SELF :
            case NONE :
                if (selftext) {
                    contentMatch = true;
                }
                break;
            case ALBUM :
                if (albums) {
                    contentMatch = true;
                }
                break;
            case IMAGE :
            case DEVIANTART :
            case IMGUR :
            case XKCD :
                if (images) {
                    contentMatch = true;
                }
                break;
            case GIF :
                if (gifs) {
                    contentMatch = true;
                }
                break;
            case VID_ME :
            case STREAMABLE :
            case VIDEO :
                if (videos) {
                    contentMatch = true;
                }
                break;
        }
        if (!flair.isEmpty())
            for (java.lang.String flairText : me.ccrama.redditslide.SettingValues.flairFilters) {
                if (flairText.toLowerCase(java.util.Locale.ENGLISH).startsWith(baseSubreddit)) {
                    java.lang.String[] split = flairText.split(":");
                    if (split[0].equalsIgnoreCase(baseSubreddit)) {
                        if (flair.equalsIgnoreCase(split[1].trim())) {
                            contentMatch = true;
                            break;
                        }
                    }
                }
            }

        return contentMatch;
    }

    public static boolean doesMatch(net.dean.jraw.models.Submission s) {
        java.lang.String title = s.getTitle();
        java.lang.String body = s.getSelftext();
        java.lang.String domain = s.getUrl();
        java.lang.String subreddit = s.getSubredditName();
        boolean titlec;
        boolean bodyc;
        boolean domainc = false;
        boolean subredditc;
        titlec = me.ccrama.redditslide.PostMatch.contains(title, me.ccrama.redditslide.SettingValues.titleFilters, false);
        bodyc = me.ccrama.redditslide.PostMatch.contains(body, me.ccrama.redditslide.SettingValues.textFilters, false);
        try {
            domainc = me.ccrama.redditslide.PostMatch.isDomain(domain.toLowerCase(java.util.Locale.ENGLISH), me.ccrama.redditslide.SettingValues.domainFilters);
        } catch (java.net.MalformedURLException ignored) {
        }
        subredditc = ((subreddit != null) && (!subreddit.isEmpty())) && me.ccrama.redditslide.PostMatch.contains(subreddit, me.ccrama.redditslide.SettingValues.subredditFilters, true);
        return ((titlec || bodyc) || domainc) || subredditc;
    }

    public static void setChosen(boolean[] values, java.lang.String subreddit) {
        subreddit = subreddit.toLowerCase(java.util.Locale.ENGLISH);
        android.content.SharedPreferences.Editor e = me.ccrama.redditslide.PostMatch.filters.edit();
        e.putBoolean(subreddit + "_gifsFilter", values[2]);
        e.putBoolean(subreddit + "_albumsFilter", values[1]);
        e.putBoolean(subreddit + "_imagesFilter", values[0]);
        e.putBoolean(subreddit + "_nsfwFilter", values[6]);
        e.putBoolean(subreddit + "_selftextFilter", values[5]);
        e.putBoolean(subreddit + "_urlsFilter", values[4]);
        e.putBoolean(subreddit + "_videoFilter", values[3]);
        e.apply();
    }

    public static boolean isGif(java.lang.String baseSubreddit) {
        return me.ccrama.redditslide.PostMatch.filters.getBoolean(baseSubreddit + "_gifsFilter", false);
    }

    public static boolean isImage(java.lang.String baseSubreddit) {
        return me.ccrama.redditslide.PostMatch.filters.getBoolean(baseSubreddit + "_imagesFilter", false);
    }

    public static boolean isAlbums(java.lang.String baseSubreddit) {
        return me.ccrama.redditslide.PostMatch.filters.getBoolean(baseSubreddit + "_albumsFilter", false);
    }

    public static boolean isNsfw(java.lang.String baseSubreddit) {
        return me.ccrama.redditslide.PostMatch.filters.getBoolean(baseSubreddit + "_nsfwFilter", false);
    }

    public static boolean isSelftext(java.lang.String baseSubreddit) {
        return me.ccrama.redditslide.PostMatch.filters.getBoolean(baseSubreddit + "_selftextFilter", false);
    }

    public static boolean isUrls(java.lang.String baseSubreddit) {
        return me.ccrama.redditslide.PostMatch.filters.getBoolean(baseSubreddit + "_urlsFilter", false);
    }

    public static boolean isVideo(java.lang.String baseSubreddit) {
        return me.ccrama.redditslide.PostMatch.filters.getBoolean(baseSubreddit + "_videoFilter", false);
    }
}