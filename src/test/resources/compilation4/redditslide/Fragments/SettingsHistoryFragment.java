package me.ccrama.redditslide.Fragments;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.UserSubscriptions;
import me.ccrama.redditslide.SettingValues;
public class SettingsHistoryFragment {
    private android.app.Activity context;

    public SettingsHistoryFragment(android.app.Activity context) {
        this.context = context;
    }

    public void Bind() {
        {
            android.support.v7.widget.SwitchCompat storeHistory = ((android.support.v7.widget.SwitchCompat) (context.findViewById(me.ccrama.redditslide.R.id.settings_history_storehistory)));
            storeHistory.setChecked(me.ccrama.redditslide.SettingValues.storeHistory);
            storeHistory.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.storeHistory = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_STORE_HISTORY, isChecked).apply();
                    if (isChecked) {
                        context.findViewById(me.ccrama.redditslide.R.id.settings_history_scrollseen).setEnabled(true);
                        context.findViewById(me.ccrama.redditslide.R.id.settings_history_storensfw).setEnabled(true);
                    } else {
                        ((android.support.v7.widget.SwitchCompat) (context.findViewById(me.ccrama.redditslide.R.id.settings_history_storensfw))).setChecked(false);
                        ((android.support.v7.widget.SwitchCompat) (context.findViewById(me.ccrama.redditslide.R.id.settings_history_storensfw))).setEnabled(false);
                        me.ccrama.redditslide.SettingValues.storeNSFWHistory = false;
                        me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_STORE_NSFW_HISTORY, false).apply();
                        ((android.support.v7.widget.SwitchCompat) (context.findViewById(me.ccrama.redditslide.R.id.settings_history_scrollseen))).setChecked(false);
                        ((android.support.v7.widget.SwitchCompat) (context.findViewById(me.ccrama.redditslide.R.id.settings_history_scrollseen))).setEnabled(false);
                        me.ccrama.redditslide.SettingValues.scrollSeen = false;
                        me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_SCROLL_SEEN, false).apply();
                    }
                }
            });
        }
        context.findViewById(me.ccrama.redditslide.R.id.settings_history_clearposts).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                com.lusfold.androidkeyvaluestore.KVStore.getInstance().clearTable();
                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(context).setTitle(me.ccrama.redditslide.R.string.alert_history_cleared).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, null).show();
            }
        });
        context.findViewById(me.ccrama.redditslide.R.id.settings_history_clearsubs).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                me.ccrama.redditslide.UserSubscriptions.subscriptions.edit().remove("subhistory").apply();
                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(context).setTitle(me.ccrama.redditslide.R.string.alert_history_cleared).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, null).show();
            }
        });
        {
            android.support.v7.widget.SwitchCompat nsfw = ((android.support.v7.widget.SwitchCompat) (context.findViewById(me.ccrama.redditslide.R.id.settings_history_storensfw)));
            nsfw.setChecked(me.ccrama.redditslide.SettingValues.storeNSFWHistory);
            nsfw.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.storeNSFWHistory = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_STORE_NSFW_HISTORY, isChecked).apply();
                }
            });
        }
        {
            android.support.v7.widget.SwitchCompat single = ((android.support.v7.widget.SwitchCompat) (context.findViewById(me.ccrama.redditslide.R.id.settings_history_scrollseen)));
            single.setChecked(me.ccrama.redditslide.SettingValues.scrollSeen);
            single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.scrollSeen = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_SCROLL_SEEN, isChecked).apply();
                }
            });
        }
    }
}