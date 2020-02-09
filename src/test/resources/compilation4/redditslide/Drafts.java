package me.ccrama.redditslide;
import java.util.ArrayList;
import java.util.Arrays;
/**
 * Created by l3d00m on 11/13/2015.
 */
public class Drafts {
    public static java.util.ArrayList<java.lang.String> getDrafts() {
        java.util.ArrayList<java.lang.String> drafts = new java.util.ArrayList<>();
        for (java.lang.String s : java.util.Arrays.asList(me.ccrama.redditslide.Authentication.authentication.getString(me.ccrama.redditslide.SettingValues.PREF_DRAFTS, "").split("</newdraft>"))) {
            if (!s.trim().isEmpty()) {
                drafts.add(s);
            }
        }
        return drafts;
    }

    public static void addDraft(java.lang.String s) {
        java.util.ArrayList<java.lang.String> drafts = me.ccrama.redditslide.Drafts.getDrafts();
        drafts.add(s);
        me.ccrama.redditslide.Drafts.save(drafts);
    }

    public static void deleteDraft(int position) {
        java.util.ArrayList<java.lang.String> drafts = me.ccrama.redditslide.Drafts.getDrafts();
        drafts.remove(position);
        me.ccrama.redditslide.Drafts.save(drafts);
    }

    public static void save(java.util.ArrayList<java.lang.String> drafts) {
        android.content.SharedPreferences.Editor e = me.ccrama.redditslide.Authentication.authentication.edit();
        e.putString(me.ccrama.redditslide.SettingValues.PREF_DRAFTS, me.ccrama.redditslide.Reddit.arrayToString(drafts, "</newdraft>"));
        e.commit();
    }
}