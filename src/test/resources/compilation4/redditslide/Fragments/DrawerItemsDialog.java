package me.ccrama.redditslide.Fragments;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.SettingValues;
public class DrawerItemsDialog extends com.afollestad.materialdialogs.MaterialDialog {
    public DrawerItemsDialog(final com.afollestad.materialdialogs.MaterialDialog.Builder builder) {
        super(builder.customView(me.ccrama.redditslide.R.layout.dialog_drawer_items, false).title(me.ccrama.redditslide.R.string.settings_general_title_drawer_items).positiveText(android.R.string.ok).canceledOnTouchOutside(false).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
            @java.lang.Override
            public void onClick(@android.support.annotation.NonNull
            com.afollestad.materialdialogs.MaterialDialog dialog, @android.support.annotation.NonNull
            com.afollestad.materialdialogs.DialogAction which) {
                if (me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed) {
                    me.ccrama.redditslide.SettingValues.prefs.edit().putLong(me.ccrama.redditslide.SettingValues.PREF_SELECTED_DRAWER_ITEMS, me.ccrama.redditslide.SettingValues.selectedDrawerItems).apply();
                }
            }
        }));
    }

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (me.ccrama.redditslide.SettingValues.selectedDrawerItems == (-1)) {
            me.ccrama.redditslide.SettingValues.selectedDrawerItems = 0;
            for (final me.ccrama.redditslide.Fragments.DrawerItemsDialog.SettingsDrawerEnum settingDrawerItem : me.ccrama.redditslide.Fragments.DrawerItemsDialog.SettingsDrawerEnum.values()) {
                me.ccrama.redditslide.SettingValues.selectedDrawerItems += settingDrawerItem.value;
            }
            me.ccrama.redditslide.SettingValues.prefs.edit().putLong(me.ccrama.redditslide.SettingValues.PREF_SELECTED_DRAWER_ITEMS, me.ccrama.redditslide.SettingValues.selectedDrawerItems).apply();
        }
        setupViews();
    }

    @java.lang.Override
    public void onStop() {
        super.onStop();
        me.ccrama.redditslide.SettingValues.prefs.edit().putLong(me.ccrama.redditslide.SettingValues.PREF_SELECTED_DRAWER_ITEMS, me.ccrama.redditslide.SettingValues.selectedDrawerItems).apply();
    }

    private void setupViews() {
        for (final me.ccrama.redditslide.Fragments.DrawerItemsDialog.SettingsDrawerEnum settingDrawerItem : me.ccrama.redditslide.Fragments.DrawerItemsDialog.SettingsDrawerEnum.values()) {
            findViewById(settingDrawerItem.layoutId).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    android.widget.CheckBox checkBox = ((android.widget.CheckBox) (findViewById(settingDrawerItem.checkboxId)));
                    if (checkBox.isChecked()) {
                        me.ccrama.redditslide.SettingValues.selectedDrawerItems -= settingDrawerItem.value;
                    } else {
                        me.ccrama.redditslide.SettingValues.selectedDrawerItems += settingDrawerItem.value;
                    }
                    checkBox.setChecked(!checkBox.isChecked());
                }
            });
            me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
            ((android.widget.CheckBox) (findViewById(settingDrawerItem.checkboxId))).setChecked((me.ccrama.redditslide.SettingValues.selectedDrawerItems & settingDrawerItem.value) != 0);
        }
    }

    public enum SettingsDrawerEnum {

        PROFILE(1, me.ccrama.redditslide.R.id.settings_drawer_profile, me.ccrama.redditslide.R.id.settings_drawer_profile_checkbox, me.ccrama.redditslide.R.id.prof_click),
        INBOX(1 << 1, me.ccrama.redditslide.R.id.settings_drawer_inbox, me.ccrama.redditslide.R.id.settings_drawer_inbox_checkbox, me.ccrama.redditslide.R.id.inbox),
        MULTIREDDITS(1 << 2, me.ccrama.redditslide.R.id.settings_drawer_multireddits, me.ccrama.redditslide.R.id.settings_drawer_multireddits_checkbox, me.ccrama.redditslide.R.id.multi),
        GOTO_PROFILE(1 << 3, me.ccrama.redditslide.R.id.settings_drawer_goto_profile, me.ccrama.redditslide.R.id.settings_drawer_goto_profile_checkbox, me.ccrama.redditslide.R.id.prof),
        DISCOVER(1 << 4, me.ccrama.redditslide.R.id.settings_drawer_discover, me.ccrama.redditslide.R.id.settings_drawer_discover_checkbox, me.ccrama.redditslide.R.id.discover);
        public long value;

        @android.support.annotation.IdRes
        public int layoutId;

        @android.support.annotation.IdRes
        public int checkboxId;

        @android.support.annotation.IdRes
        public int drawerId;

        SettingsDrawerEnum(long value, @android.support.annotation.IdRes
        int layoutId, @android.support.annotation.IdRes
        int checkboxId, @android.support.annotation.IdRes
        int drawerId) {
            this.value = value;
            this.layoutId = layoutId;
            this.checkboxId = checkboxId;
            this.drawerId = drawerId;
        }
    }
}