package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.Fragments.SettingsHandlingFragment;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.SettingValues;
/**
 * Created by l3d00m on 11/13/2015.
 */
public class SettingsHandling extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    private me.ccrama.redditslide.Fragments.SettingsHandlingFragment fragment = new me.ccrama.redditslide.Fragments.SettingsHandlingFragment(this);

    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_settings_handling);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.settings_link_handling, true, true);
        ((android.view.ViewGroup) (findViewById(me.ccrama.redditslide.R.id.settings_handling))).addView(getLayoutInflater().inflate(me.ccrama.redditslide.R.layout.activity_settings_handling_child, null));
        fragment.Bind();
    }

    @java.lang.Override
    public void onPause() {
        super.onPause();
        android.content.SharedPreferences.Editor e = me.ccrama.redditslide.SettingValues.prefs.edit();
        e.putStringSet(me.ccrama.redditslide.SettingValues.PREF_ALWAYS_EXTERNAL, me.ccrama.redditslide.SettingValues.alwaysExternal);
        e.apply();
    }
}