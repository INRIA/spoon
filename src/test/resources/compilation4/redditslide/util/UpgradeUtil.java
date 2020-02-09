/* Copyright (c) 2016. ccrama

Slide is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.ccrama.redditslide.util;
import java.util.Locale;
import java.util.Set;
import me.ccrama.redditslide.SettingValues;
import java.util.Arrays;
import java.util.HashSet;
public class UpgradeUtil {
    // Increment for each needed change
    private static final int VERSION = 2;

    private UpgradeUtil() {
    }

    /**
     * Runs any upgrade actions required between versions in an organised way
     */
    public static void upgrade(android.content.Context context) {
        android.content.SharedPreferences colors = context.getSharedPreferences("COLOR", 0);
        android.content.SharedPreferences upgradePrefs = context.getSharedPreferences("upgradeUtil", 0);
        // Exit if this is the first start
        if ((colors != null) && (!colors.contains("Tutorial"))) {
            upgradePrefs.edit().putInt("VERSION", me.ccrama.redditslide.util.UpgradeUtil.VERSION).apply();
            return;
        }
        final int CURRENT = upgradePrefs.getInt("VERSION", 0);
        // Exit if we're up to date
        if (CURRENT == me.ccrama.redditslide.util.UpgradeUtil.VERSION)
            return;

        if (CURRENT < 1) {
            android.content.SharedPreferences prefs = context.getSharedPreferences("SETTINGS", 0);
            java.lang.String domains = prefs.getString(me.ccrama.redditslide.SettingValues.PREF_ALWAYS_EXTERNAL, "");
            domains = domains.replaceFirst("(?<=^|,)youtube.co(?=$|,)", "youtube.com").replaceFirst("(?<=^|,)play.google.co(?=$|,)", "play.google.com");
            prefs.edit().putString(me.ccrama.redditslide.SettingValues.PREF_ALWAYS_EXTERNAL, domains).apply();
        }
        // migrate old filters
        if (CURRENT < 2) {
            android.content.SharedPreferences prefs = context.getSharedPreferences("SETTINGS", 0);
            android.content.SharedPreferences.Editor prefsEditor = prefs.edit();
            java.lang.String titleFilterStr = prefs.getString(me.ccrama.redditslide.SettingValues.PREF_TITLE_FILTERS, "");
            java.lang.String textFilterStr = prefs.getString(me.ccrama.redditslide.SettingValues.PREF_TEXT_FILTERS, "");
            java.lang.String flairFilterStr = prefs.getString(me.ccrama.redditslide.SettingValues.PREF_FLAIR_FILTERS, "");
            java.lang.String subredditFilterStr = prefs.getString(me.ccrama.redditslide.SettingValues.PREF_SUBREDDIT_FILTERS, "");
            java.lang.String domainFilterStr = prefs.getString(me.ccrama.redditslide.SettingValues.PREF_DOMAIN_FILTERS, "");
            java.lang.String usersFilterStr = prefs.getString(me.ccrama.redditslide.SettingValues.PREF_USER_FILTERS, "");
            java.lang.String alwaysExternalStr = prefs.getString(me.ccrama.redditslide.SettingValues.PREF_ALWAYS_EXTERNAL, "");
            prefsEditor.remove(me.ccrama.redditslide.SettingValues.PREF_TITLE_FILTERS);
            prefsEditor.remove(me.ccrama.redditslide.SettingValues.PREF_TEXT_FILTERS);
            prefsEditor.remove(me.ccrama.redditslide.SettingValues.PREF_FLAIR_FILTERS);
            prefsEditor.remove(me.ccrama.redditslide.SettingValues.PREF_SUBREDDIT_FILTERS);
            prefsEditor.remove(me.ccrama.redditslide.SettingValues.PREF_DOMAIN_FILTERS);
            prefsEditor.remove(me.ccrama.redditslide.SettingValues.PREF_USER_FILTERS);
            prefsEditor.remove(me.ccrama.redditslide.SettingValues.PREF_ALWAYS_EXTERNAL);
            java.util.Set<java.lang.String> titleFilters = (titleFilterStr.isEmpty()) ? new java.util.HashSet<>() : new java.util.HashSet<>(java.util.Arrays.asList(titleFilterStr.replaceAll("^[,\\s]+", "").toLowerCase(java.util.Locale.ENGLISH).split("[,\\s]+")));
            java.util.Set<java.lang.String> textFilters = (textFilterStr.isEmpty()) ? new java.util.HashSet<>() : new java.util.HashSet<>(java.util.Arrays.asList(textFilterStr.replaceAll("^[,\\s]+", "").toLowerCase(java.util.Locale.ENGLISH).split("[,\\s]+")));
            java.util.Set<java.lang.String> flairFilters = (flairFilterStr.isEmpty()) ? new java.util.HashSet<>() : new java.util.HashSet<>(java.util.Arrays.asList(flairFilterStr.replaceAll("^[,]+", "").toLowerCase(java.util.Locale.ENGLISH).split("[,]+")));
            // verify flairs filters are valid
            java.util.HashSet<java.lang.String> invalid = new java.util.HashSet<>();
            for (java.lang.String s : flairFilters) {
                if (!s.contains(":")) {
                    invalid.add(s);
                }
            }
            flairFilters.removeAll(invalid);
            java.util.Set<java.lang.String> subredditFilters = (subredditFilterStr.isEmpty()) ? new java.util.HashSet<>() : new java.util.HashSet<>(java.util.Arrays.asList(subredditFilterStr.replaceAll("^[,\\s]+", "").toLowerCase(java.util.Locale.ENGLISH).split("[,\\s]+")));
            java.util.Set<java.lang.String> domainFilters = (domainFilterStr.isEmpty()) ? new java.util.HashSet<>() : new java.util.HashSet<>(java.util.Arrays.asList(domainFilterStr.replaceAll("^[,\\s]+", "").toLowerCase(java.util.Locale.ENGLISH).split("[,\\s]+")));
            java.util.Set<java.lang.String> usersFilters = (usersFilterStr.isEmpty()) ? new java.util.HashSet<>() : new java.util.HashSet<>(java.util.Arrays.asList(usersFilterStr.replaceAll("^[,\\s]+", "").toLowerCase(java.util.Locale.ENGLISH).split("[,\\s]+")));
            java.util.Set<java.lang.String> alwaysExternal = (alwaysExternalStr.isEmpty()) ? new java.util.HashSet<>() : new java.util.HashSet<>(java.util.Arrays.asList(alwaysExternalStr.replaceAll("^[,\\s]+", "").toLowerCase(java.util.Locale.ENGLISH).split("[,\\s]+")));
            prefsEditor.putStringSet(me.ccrama.redditslide.SettingValues.PREF_TITLE_FILTERS, titleFilters);
            prefsEditor.putStringSet(me.ccrama.redditslide.SettingValues.PREF_TEXT_FILTERS, textFilters);
            prefsEditor.putStringSet(me.ccrama.redditslide.SettingValues.PREF_FLAIR_FILTERS, flairFilters);
            prefsEditor.putStringSet(me.ccrama.redditslide.SettingValues.PREF_SUBREDDIT_FILTERS, subredditFilters);
            prefsEditor.putStringSet(me.ccrama.redditslide.SettingValues.PREF_DOMAIN_FILTERS, domainFilters);
            prefsEditor.putStringSet(me.ccrama.redditslide.SettingValues.PREF_USER_FILTERS, usersFilters);
            prefsEditor.putStringSet(me.ccrama.redditslide.SettingValues.PREF_ALWAYS_EXTERNAL, alwaysExternal);
            prefsEditor.apply();
        }
        upgradePrefs.edit().putInt("VERSION", me.ccrama.redditslide.util.UpgradeUtil.VERSION).apply();
    }
}