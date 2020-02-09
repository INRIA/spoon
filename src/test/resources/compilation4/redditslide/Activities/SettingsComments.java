package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.Fragments.SettingsCommentsFragment;
import me.ccrama.redditslide.R;
public class SettingsComments extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    private me.ccrama.redditslide.Fragments.SettingsCommentsFragment fragment = new me.ccrama.redditslide.Fragments.SettingsCommentsFragment(this);

    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_settings_comments);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.settings_title_comments, true, true);
        ((android.view.ViewGroup) (findViewById(me.ccrama.redditslide.R.id.settings_comments))).addView(getLayoutInflater().inflate(me.ccrama.redditslide.R.layout.activity_settings_comments_child, null));
        fragment.Bind();
    }
}