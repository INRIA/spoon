package me.ccrama.redditslide.Fragments;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.util.LinkUtil;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.Activities.Settings;
import me.ccrama.redditslide.SettingValues;
public class SettingsRedditFragment {
    private android.app.Activity context;

    public SettingsRedditFragment(android.app.Activity context) {
        this.context = context;
    }

    public void Bind() {
        {
            final android.support.v7.widget.SwitchCompat thumbnails = context.findViewById(me.ccrama.redditslide.R.id.settings_reddit_nsfwcontent);
            thumbnails.setChecked(me.ccrama.redditslide.SettingValues.showNSFWContent);
            thumbnails.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.showNSFWContent = isChecked;
                    me.ccrama.redditslide.Activities.Settings.changed = true;
                    if (isChecked) {
                        context.findViewById(me.ccrama.redditslide.R.id.settings_reddit_nsfwrpev).setEnabled(true);
                        context.findViewById(me.ccrama.redditslide.R.id.settings_reddit_nsfwrpev_text).setAlpha(1.0F);
                        ((android.support.v7.widget.SwitchCompat) (context.findViewById(me.ccrama.redditslide.R.id.settings_reddit_nsfwrpev))).setChecked(me.ccrama.redditslide.SettingValues.getIsNSFWEnabled());
                        context.findViewById(me.ccrama.redditslide.R.id.settings_reddit_nsfwcollection).setEnabled(true);
                        context.findViewById(me.ccrama.redditslide.R.id.settings_reddit_nsfwcollection_text).setAlpha(1.0F);
                        ((android.support.v7.widget.SwitchCompat) (context.findViewById(me.ccrama.redditslide.R.id.settings_reddit_nsfwcollection))).setChecked(me.ccrama.redditslide.SettingValues.hideNSFWCollection);
                    } else {
                        ((android.support.v7.widget.SwitchCompat) (context.findViewById(me.ccrama.redditslide.R.id.settings_reddit_nsfwrpev))).setChecked(false);
                        context.findViewById(me.ccrama.redditslide.R.id.settings_reddit_nsfwrpev).setEnabled(false);
                        context.findViewById(me.ccrama.redditslide.R.id.settings_reddit_nsfwrpev_text).setAlpha(0.25F);
                        ((android.support.v7.widget.SwitchCompat) (context.findViewById(me.ccrama.redditslide.R.id.settings_reddit_nsfwcollection))).setChecked(false);
                        context.findViewById(me.ccrama.redditslide.R.id.settings_reddit_nsfwcollection).setEnabled(false);
                        context.findViewById(me.ccrama.redditslide.R.id.settings_reddit_nsfwcollection_text).setAlpha(0.25F);
                    }
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_SHOW_NSFW_CONTENT, isChecked).apply();
                }
            });
        }
        {
            final android.support.v7.widget.SwitchCompat thumbnails = context.findViewById(me.ccrama.redditslide.R.id.settings_reddit_nsfwrpev);
            if (!((android.support.v7.widget.SwitchCompat) (context.findViewById(me.ccrama.redditslide.R.id.settings_reddit_nsfwcontent))).isChecked()) {
                thumbnails.setChecked(true);
                thumbnails.setEnabled(false);
                context.findViewById(me.ccrama.redditslide.R.id.settings_reddit_nsfwrpev_text).setAlpha(0.25F);
            } else {
                thumbnails.setChecked(me.ccrama.redditslide.SettingValues.getIsNSFWEnabled());
            }
            thumbnails.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.Activities.Settings.changed = true;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_HIDE_NSFW_PREVIEW + me.ccrama.redditslide.Authentication.name, isChecked).apply();
                }
            });
        }
        {
            final android.support.v7.widget.SwitchCompat thumbnails = context.findViewById(me.ccrama.redditslide.R.id.settings_reddit_nsfwcollection);
            if (!((android.support.v7.widget.SwitchCompat) (context.findViewById(me.ccrama.redditslide.R.id.settings_reddit_nsfwcontent))).isChecked()) {
                thumbnails.setChecked(true);
                thumbnails.setEnabled(false);
                context.findViewById(me.ccrama.redditslide.R.id.settings_reddit_nsfwcollection_text).setAlpha(0.25F);
            } else {
                thumbnails.setChecked(me.ccrama.redditslide.SettingValues.hideNSFWCollection);
            }
            thumbnails.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.Activities.Settings.changed = true;
                    me.ccrama.redditslide.SettingValues.hideNSFWCollection = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_HIDE_NSFW_COLLECTION, isChecked).apply();
                }
            });
        }
        {
            final android.support.v7.widget.SwitchCompat thumbnails = context.findViewById(me.ccrama.redditslide.R.id.settings_reddit_ignorepref);
            thumbnails.setChecked(me.ccrama.redditslide.SettingValues.ignoreSubSetting);
            thumbnails.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.Activities.Settings.changed = true;
                    me.ccrama.redditslide.SettingValues.ignoreSubSetting = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_IGNORE_SUB_SETTINGS, isChecked).apply();
                }
            });
        }
        context.findViewById(me.ccrama.redditslide.R.id.settings_reddit_viewRedditPrefs).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                me.ccrama.redditslide.util.LinkUtil.openUrl("https://www.reddit.com/prefs/", me.ccrama.redditslide.Visuals.Palette.getDefaultColor(), context);
            }
        });
    }
}