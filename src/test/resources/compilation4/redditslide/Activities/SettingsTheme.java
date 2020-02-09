package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Fragments.SettingsFragment;
import me.ccrama.redditslide.Fragments.SettingsThemeFragment;
/**
 * Created by ccrama on 3/5/2015.
 */
public class SettingsTheme extends me.ccrama.redditslide.Activities.BaseActivityAnim implements me.ccrama.redditslide.Fragments.SettingsFragment.RestartActivity {
    private me.ccrama.redditslide.Fragments.SettingsThemeFragment fragment = new me.ccrama.redditslide.Fragments.SettingsThemeFragment(this);

    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_settings_theme);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.title_edit_theme, true, true);
        ((android.view.ViewGroup) (findViewById(me.ccrama.redditslide.R.id.settings_theme))).addView(getLayoutInflater().inflate(me.ccrama.redditslide.R.layout.activity_settings_theme_child, null));
        fragment.Bind();
    }

    @java.lang.Override
    public void restartActivity() {
        android.content.Intent i = new android.content.Intent(this, me.ccrama.redditslide.Activities.SettingsTheme.class);
        i.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
        overridePendingTransition(0, 0);
        finish();
        overridePendingTransition(0, 0);
    }
}