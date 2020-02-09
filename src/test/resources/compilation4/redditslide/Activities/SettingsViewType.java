package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Fragments.SettingsThemeFragment;
/**
 * Created by ccrama on 3/5/2015.
 */
public class SettingsViewType extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_settings_viewtype);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.settings_view_type, true, true);
        // View type multi choice
        ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.currentViewType))).setText(me.ccrama.redditslide.SettingValues.single ? me.ccrama.redditslide.SettingValues.commentPager ? getString(me.ccrama.redditslide.R.string.view_type_comments) : getString(me.ccrama.redditslide.R.string.view_type_none) : getString(me.ccrama.redditslide.R.string.view_type_tabs));
        findViewById(me.ccrama.redditslide.R.id.viewtype).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                android.support.v7.widget.PopupMenu popup = new android.support.v7.widget.PopupMenu(me.ccrama.redditslide.Activities.SettingsViewType.this, v);
                popup.getMenuInflater().inflate(me.ccrama.redditslide.R.menu.view_type_settings, popup.getMenu());
                popup.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(android.view.MenuItem item) {
                        switch (item.getItemId()) {
                            case me.ccrama.redditslide.R.id.tabs :
                                me.ccrama.redditslide.SettingValues.single = false;
                                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_SINGLE, false).apply();
                                break;
                            case me.ccrama.redditslide.R.id.notabs :
                                me.ccrama.redditslide.SettingValues.single = true;
                                me.ccrama.redditslide.SettingValues.commentPager = false;
                                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_SINGLE, true).apply();
                                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_COMMENT_PAGER, false).apply();
                                break;
                            case me.ccrama.redditslide.R.id.comments :
                                me.ccrama.redditslide.SettingValues.single = true;
                                me.ccrama.redditslide.SettingValues.commentPager = true;
                                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_SINGLE, true).apply();
                                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_COMMENT_PAGER, true).apply();
                                break;
                        }
                        ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.currentViewType))).setText(me.ccrama.redditslide.SettingValues.single ? me.ccrama.redditslide.SettingValues.commentPager ? getString(me.ccrama.redditslide.R.string.view_type_comments) : getString(me.ccrama.redditslide.R.string.view_type_none) : getString(me.ccrama.redditslide.R.string.view_type_tabs));
                        me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
                        return true;
                    }
                });
                popup.show();
            }
        });
    }
}