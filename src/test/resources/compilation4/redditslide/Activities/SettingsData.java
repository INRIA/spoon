package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Fragments.SettingsDataFragment;
/**
 * Created by ccrama on 3/5/2015.
 */
public class SettingsData extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    private me.ccrama.redditslide.Fragments.SettingsDataFragment fragment = new me.ccrama.redditslide.Fragments.SettingsDataFragment(this);

    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_settings_datasaving);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.settings_data, true, true);
        ((android.view.ViewGroup) (findViewById(me.ccrama.redditslide.R.id.settings_datasaving))).addView(getLayoutInflater().inflate(me.ccrama.redditslide.R.layout.activity_settings_datasaving_child, null));
        fragment.Bind();
    }
}