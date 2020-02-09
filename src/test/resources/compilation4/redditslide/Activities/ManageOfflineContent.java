package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Fragments.ManageOfflineContentFragment;
/**
 * Created by l3d00m on 11/13/2015.
 */
public class ManageOfflineContent extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    me.ccrama.redditslide.Fragments.ManageOfflineContentFragment fragment = new me.ccrama.redditslide.Fragments.ManageOfflineContentFragment(this);

    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_manage_history);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.manage_offline_content, true, true);
        ((android.view.ViewGroup) (findViewById(me.ccrama.redditslide.R.id.manage_history))).addView(getLayoutInflater().inflate(me.ccrama.redditslide.R.layout.activity_manage_history_child, null));
        fragment.Bind();
    }
}