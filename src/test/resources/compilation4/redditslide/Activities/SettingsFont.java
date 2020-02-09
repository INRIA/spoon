package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.Fragments.SettingsFontFragment;
import me.ccrama.redditslide.R;
/**
 * Created by l3d00m on 11/13/2015.
 */
public class SettingsFont extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    private me.ccrama.redditslide.Fragments.SettingsFontFragment fragment = new me.ccrama.redditslide.Fragments.SettingsFontFragment(this);

    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_settings_font);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.settings_title_font, true, true);
        ((android.view.ViewGroup) (findViewById(me.ccrama.redditslide.R.id.settings_font))).addView(getLayoutInflater().inflate(me.ccrama.redditslide.R.layout.activity_settings_font_child, null));
        fragment.Bind();
    }
}