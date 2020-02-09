package me.ccrama.redditslide.Fragments;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.SettingValues;
public class SettingsDataFragment {
    private android.app.Activity context;

    public SettingsDataFragment(android.app.Activity context) {
        this.context = context;
    }

    public void Bind() {
        // Image mode multi choice
        ((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_datasaving_currentmode))).setText(me.ccrama.redditslide.SettingValues.noImages ? context.getString(me.ccrama.redditslide.R.string.never_load_images) : me.ccrama.redditslide.SettingValues.lqLow ? context.getString(me.ccrama.redditslide.R.string.load_low_quality) : me.ccrama.redditslide.SettingValues.lqMid ? context.getString(me.ccrama.redditslide.R.string.load_medium_quality) : context.getString(me.ccrama.redditslide.R.string.load_high_quality));
        context.findViewById(me.ccrama.redditslide.R.id.settings_datasaving_datasavequality).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                if (!((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_datasaving_lowquality))).getText().equals(context.getString(me.ccrama.redditslide.R.string.never))) {
                    android.support.v7.widget.PopupMenu popup = new android.support.v7.widget.PopupMenu(context, v);
                    popup.getMenuInflater().inflate(me.ccrama.redditslide.R.menu.imagequality_mode, popup.getMenu());
                    popup.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(android.view.MenuItem item) {
                            switch (item.getItemId()) {
                                case me.ccrama.redditslide.R.id.never :
                                    me.ccrama.redditslide.SettingValues.noImages = true;
                                    me.ccrama.redditslide.SettingValues.loadImageLq = true;
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_NO_IMAGES, true).apply();
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_IMAGE_LQ, true).apply();
                                    break;
                                case me.ccrama.redditslide.R.id.low :
                                    me.ccrama.redditslide.SettingValues.loadImageLq = true;
                                    me.ccrama.redditslide.SettingValues.noImages = false;
                                    me.ccrama.redditslide.SettingValues.lqLow = true;
                                    me.ccrama.redditslide.SettingValues.lqMid = false;
                                    me.ccrama.redditslide.SettingValues.lqHigh = false;
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_IMAGE_LQ, true).apply();
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_NO_IMAGES, false).apply();
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_LQ_LOW, true).apply();
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_LQ_MID, false).apply();
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_LQ_HIGH, false).apply();
                                    break;
                                case me.ccrama.redditslide.R.id.medium :
                                    me.ccrama.redditslide.SettingValues.loadImageLq = true;
                                    me.ccrama.redditslide.SettingValues.noImages = false;
                                    me.ccrama.redditslide.SettingValues.lqLow = false;
                                    me.ccrama.redditslide.SettingValues.lqMid = true;
                                    me.ccrama.redditslide.SettingValues.lqHigh = false;
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_IMAGE_LQ, true).apply();
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_NO_IMAGES, false).apply();
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_LQ_LOW, false).apply();
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_LQ_MID, true).apply();
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_LQ_HIGH, false).apply();
                                    break;
                                case me.ccrama.redditslide.R.id.high :
                                    me.ccrama.redditslide.SettingValues.loadImageLq = true;
                                    me.ccrama.redditslide.SettingValues.noImages = false;
                                    me.ccrama.redditslide.SettingValues.lqLow = false;
                                    me.ccrama.redditslide.SettingValues.lqMid = false;
                                    me.ccrama.redditslide.SettingValues.lqHigh = true;
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_IMAGE_LQ, true).apply();
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_NO_IMAGES, false).apply();
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_LQ_LOW, false).apply();
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_LQ_MID, false).apply();
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_LQ_HIGH, true).apply();
                                    break;
                            }
                            ((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_datasaving_currentmode))).setText(me.ccrama.redditslide.SettingValues.noImages ? context.getString(me.ccrama.redditslide.R.string.never_load_images) : me.ccrama.redditslide.SettingValues.lqLow ? context.getString(me.ccrama.redditslide.R.string.load_low_quality) : me.ccrama.redditslide.SettingValues.lqMid ? context.getString(me.ccrama.redditslide.R.string.load_medium_quality) : context.getString(me.ccrama.redditslide.R.string.load_high_quality));
                            return true;
                        }
                    });
                    popup.show();
                }
            }
        });
        // Datasaving type multi choice
        ((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_datasaving_lowquality))).setText(me.ccrama.redditslide.SettingValues.lowResMobile ? me.ccrama.redditslide.SettingValues.lowResAlways ? context.getString(me.ccrama.redditslide.R.string.datasave_always) : context.getString(me.ccrama.redditslide.R.string.datasave_mobile) : context.getString(me.ccrama.redditslide.R.string.never));
        context.findViewById(me.ccrama.redditslide.R.id.settings_datasaving_datasavetype).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                android.support.v7.widget.PopupMenu popup = new android.support.v7.widget.PopupMenu(context, v);
                popup.getMenuInflater().inflate(me.ccrama.redditslide.R.menu.imagequality_settings, popup.getMenu());
                popup.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(android.view.MenuItem item) {
                        switch (item.getItemId()) {
                            case me.ccrama.redditslide.R.id.never :
                                me.ccrama.redditslide.SettingValues.lowResMobile = false;
                                me.ccrama.redditslide.SettingValues.lowResAlways = false;
                                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_LOW_RES_MOBILE, false).apply();
                                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_LOW_RES_ALWAYS, false).apply();
                                break;
                            case me.ccrama.redditslide.R.id.mobile :
                                me.ccrama.redditslide.SettingValues.lowResMobile = true;
                                me.ccrama.redditslide.SettingValues.lowResAlways = false;
                                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_LOW_RES_MOBILE, true).apply();
                                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_LOW_RES_ALWAYS, false).apply();
                                break;
                            case me.ccrama.redditslide.R.id.always :
                                me.ccrama.redditslide.SettingValues.lowResMobile = true;
                                me.ccrama.redditslide.SettingValues.lowResAlways = true;
                                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_LOW_RES_MOBILE, true).apply();
                                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_LOW_RES_ALWAYS, true).apply();
                                break;
                        }
                        ((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_datasaving_lowquality))).setText(me.ccrama.redditslide.SettingValues.lowResMobile ? me.ccrama.redditslide.SettingValues.lowResAlways ? context.getString(me.ccrama.redditslide.R.string.datasave_always) : context.getString(me.ccrama.redditslide.R.string.datasave_mobile) : context.getString(me.ccrama.redditslide.R.string.never));
                        if (((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_datasaving_lowquality))).getText().equals(context.getString(me.ccrama.redditslide.R.string.never))) {
                            context.findViewById(me.ccrama.redditslide.R.id.settings_datasaving_datasavequality).setAlpha(0.25F);
                            ((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_datasaving_currentmode))).setText("Enable datasaving mode");
                        } else {
                            context.findViewById(me.ccrama.redditslide.R.id.settings_datasaving_datasavequality).setAlpha(1.0F);
                            ((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_datasaving_currentmode))).setText(me.ccrama.redditslide.SettingValues.noImages ? context.getString(me.ccrama.redditslide.R.string.never_load_images) : me.ccrama.redditslide.SettingValues.lqLow ? context.getString(me.ccrama.redditslide.R.string.load_low_quality) : me.ccrama.redditslide.SettingValues.lqMid ? context.getString(me.ccrama.redditslide.R.string.load_medium_quality) : context.getString(me.ccrama.redditslide.R.string.load_high_quality));
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
        if (((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_datasaving_lowquality))).getText().equals(context.getString(me.ccrama.redditslide.R.string.never))) {
            context.findViewById(me.ccrama.redditslide.R.id.settings_datasaving_datasavequality).setAlpha(0.25F);
            ((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_datasaving_currentmode))).setText("Enable datasaving mode");
        }
    }
}