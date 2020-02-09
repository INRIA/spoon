package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.Fragments.FolderChooserDialogCreate;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Fragments.SettingsGeneralFragment;
import java.io.File;
/**
 * Created by ccrama on 3/5/2015.
 */
public class SettingsGeneral extends me.ccrama.redditslide.Activities.BaseActivityAnim implements me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.FolderCallback {
    private me.ccrama.redditslide.Fragments.SettingsGeneralFragment fragment = new me.ccrama.redditslide.Fragments.SettingsGeneralFragment(this);

    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_settings_general);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.settings_title_general, true, true);
        ((android.view.ViewGroup) (findViewById(me.ccrama.redditslide.R.id.settings_general))).addView(getLayoutInflater().inflate(me.ccrama.redditslide.R.layout.activity_settings_general_child, null));
        fragment.Bind();
    }

    @java.lang.Override
    public void onFolderSelection(@android.support.annotation.NonNull
    me.ccrama.redditslide.Fragments.FolderChooserDialogCreate dialog, @android.support.annotation.NonNull
    java.io.File folder) {
        fragment.onFolderSelection(dialog, folder);
    }

    @java.lang.Override
    public void onResume() {
        super.onResume();
        me.ccrama.redditslide.Fragments.SettingsGeneralFragment.doNotifText(this);
    }
}