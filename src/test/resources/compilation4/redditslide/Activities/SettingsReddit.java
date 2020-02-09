package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Fragments.SettingsRedditFragment;
/**
 * Created by l3d00m on 11/13/2015.
 */
public class SettingsReddit extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    me.ccrama.redditslide.Fragments.SettingsRedditFragment fragment = new me.ccrama.redditslide.Fragments.SettingsRedditFragment(this);

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_settings_reddit);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.settings_reddit_prefs, true, true);
        ((android.view.ViewGroup) (findViewById(me.ccrama.redditslide.R.id.settings_reddit))).addView(getLayoutInflater().inflate(me.ccrama.redditslide.R.layout.activity_settings_reddit_child, null));
        fragment.Bind();
    }
}