package me.ccrama.redditslide;
import java.util.Locale;
public class UserTags {
    /**
     * Gets the tag for a specific username.
     *
     * @param username
     * 		The username to find the tag of
     * @return String for the tag
     */
    public static java.lang.String getUserTag(java.lang.String username) {
        return me.ccrama.redditslide.Reddit.tags.getString("user-tag" + username.toLowerCase(java.util.Locale.ENGLISH), "");
    }

    /**
     * Gets whether a username is tagged.
     *
     * @param username
     * 		The username to find the tag of
     * @return Boolean if username is tagged
     */
    public static boolean isUserTagged(java.lang.String username) {
        return me.ccrama.redditslide.Reddit.tags.contains("user-tag" + username.toLowerCase(java.util.Locale.ENGLISH));
    }

    public static void setUserTag(final java.lang.String username, java.lang.String tag) {
        me.ccrama.redditslide.Reddit.tags.edit().putString("user-tag" + username.toLowerCase(java.util.Locale.ENGLISH), tag).apply();
    }

    public static void removeUserTag(final java.lang.String username) {
        me.ccrama.redditslide.Reddit.tags.edit().remove("user-tag" + username.toLowerCase(java.util.Locale.ENGLISH)).apply();
    }
}