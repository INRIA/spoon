package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Fragments.SettingsHistoryFragment;
public class SettingsHistory extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    private me.ccrama.redditslide.Fragments.SettingsHistoryFragment fragment = new me.ccrama.redditslide.Fragments.SettingsHistoryFragment(this);

    @java.lang.Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_settings_history);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.settings_title_history, true, true);
        ((android.view.ViewGroup) (findViewById(me.ccrama.redditslide.R.id.settings_history))).addView(getLayoutInflater().inflate(me.ccrama.redditslide.R.layout.activity_settings_history_child, null));
        fragment.Bind();
    }
}