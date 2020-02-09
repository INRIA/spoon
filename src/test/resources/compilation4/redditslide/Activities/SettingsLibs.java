package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.R;
public class SettingsLibs extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    @java.lang.Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_settings_libs);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.settings_about_libs, true, true);
        com.mikepenz.aboutlibraries.ui.LibsSupportFragment fragment = new com.mikepenz.aboutlibraries.LibsBuilder().supportFragment();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(me.ccrama.redditslide.R.id.root_fragment, fragment).commit();
        }
    }
}