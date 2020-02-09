package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Fragments.SettingsModerationFragment;
public class SettingsModeration extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    private me.ccrama.redditslide.Fragments.SettingsModerationFragment fragment = new me.ccrama.redditslide.Fragments.SettingsModerationFragment(this);

    @java.lang.Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_settings_moderation);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.settings_moderation, true, true);
        ((android.view.ViewGroup) (findViewById(me.ccrama.redditslide.R.id.settings_moderation))).addView(getLayoutInflater().inflate(me.ccrama.redditslide.R.layout.activity_settings_moderation_child, null));
        fragment.Bind();
    }
}