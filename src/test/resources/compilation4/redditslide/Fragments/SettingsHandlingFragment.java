package me.ccrama.redditslide.Fragments;
import com.google.common.collect.HashBiMap;
import java.util.Locale;
import me.ccrama.redditslide.R;
import com.google.common.collect.BiMap;
import me.ccrama.redditslide.Reddit;
import java.util.HashMap;
import me.ccrama.redditslide.SettingValues;
public class SettingsHandlingFragment implements android.widget.CompoundButton.OnCheckedChangeListener {
    private android.app.Activity context;

    android.widget.EditText domain;

    public SettingsHandlingFragment(android.app.Activity context) {
        this.context = context;
    }

    public void Bind() {
        // todo web stuff
        android.support.v7.widget.SwitchCompat image = context.findViewById(me.ccrama.redditslide.R.id.settings_handling_image);
        android.support.v7.widget.SwitchCompat gif = context.findViewById(me.ccrama.redditslide.R.id.settings_handling_gif);
        android.support.v7.widget.SwitchCompat album = context.findViewById(me.ccrama.redditslide.R.id.settings_handling_album);
        android.support.v7.widget.SwitchCompat peek = context.findViewById(me.ccrama.redditslide.R.id.settings_handling_peek);
        android.support.v7.widget.SwitchCompat shortlink = context.findViewById(me.ccrama.redditslide.R.id.settings_handling_shortlink);
        image.setChecked(me.ccrama.redditslide.SettingValues.image);
        gif.setChecked(me.ccrama.redditslide.SettingValues.gif);
        album.setChecked(me.ccrama.redditslide.SettingValues.album);
        peek.setChecked(me.ccrama.redditslide.SettingValues.peek);
        shortlink.setChecked(!me.ccrama.redditslide.SettingValues.shareLongLink);
        image.setOnCheckedChangeListener(this);
        gif.setOnCheckedChangeListener(this);
        album.setOnCheckedChangeListener(this);
        peek.setOnCheckedChangeListener(this);
        shortlink.setOnCheckedChangeListener(this);
        if (!me.ccrama.redditslide.Reddit.videoPlugin) {
            context.findViewById(me.ccrama.redditslide.R.id.settings_handling_video).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    try {
                        context.startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=" + context.getString(me.ccrama.redditslide.R.string.youtube_plugin_package))));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        context.startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("http://play.google.com/store/apps/details?id=ccrama.me.slideyoutubeplugin")));
                    }
                }
            });
        } else {
            context.findViewById(me.ccrama.redditslide.R.id.settings_handling_video).setVisibility(android.view.View.GONE);
        }
        final android.support.v7.widget.SwitchCompat readerMode = context.findViewById(me.ccrama.redditslide.R.id.settings_handling_reader_mode);
        readerMode.setChecked(me.ccrama.redditslide.SettingValues.readerMode);
        readerMode.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                me.ccrama.redditslide.SettingValues.readerMode = isChecked;
                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_READER_MODE, me.ccrama.redditslide.SettingValues.readerMode).apply();
                context.findViewById(me.ccrama.redditslide.R.id.settings_handling_readernight).setEnabled(me.ccrama.redditslide.SettingValues.NightModeState.isEnabled() && me.ccrama.redditslide.SettingValues.readerMode);
            }
        });
        final android.support.v7.widget.SwitchCompat readernight = context.findViewById(me.ccrama.redditslide.R.id.settings_handling_readernight);
        readernight.setEnabled(me.ccrama.redditslide.SettingValues.NightModeState.isEnabled() && me.ccrama.redditslide.SettingValues.readerMode);
        readernight.setChecked(me.ccrama.redditslide.SettingValues.readerNight);
        readernight.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                me.ccrama.redditslide.SettingValues.readerNight = isChecked;
                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_READER_NIGHT, isChecked).apply();
            }
        });
        setUpBrowserLinkHandling();
        /* activity_settings_handling_child.xml does not load these elements so we need to null check */
        if ((context.findViewById(me.ccrama.redditslide.R.id.domain) != null) & (context.findViewById(me.ccrama.redditslide.R.id.domainlist) != null)) {
            domain = context.findViewById(me.ccrama.redditslide.R.id.domain);
            domain.setOnEditorActionListener(new android.widget.TextView.OnEditorActionListener() {
                @java.lang.Override
                public boolean onEditorAction(android.widget.TextView v, int actionId, android.view.KeyEvent event) {
                    if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                        me.ccrama.redditslide.SettingValues.alwaysExternal.add(domain.getText().toString().toLowerCase(java.util.Locale.ENGLISH).trim());
                        domain.setText("");
                        updateFilters();
                    }
                    return false;
                }
            });
            updateFilters();
        }
    }

    private void setUpBrowserLinkHandling() {
        android.widget.RadioGroup radioGroup = context.findViewById(me.ccrama.redditslide.R.id.settings_handling_select_browser_type);
        radioGroup.check(me.ccrama.redditslide.Fragments.SettingsHandlingFragment.LinkHandlingMode.idResFromValue(me.ccrama.redditslide.SettingValues.linkHandlingMode));
        radioGroup.setOnCheckedChangeListener(new android.widget.RadioGroup.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.RadioGroup group, int checkedId) {
                me.ccrama.redditslide.SettingValues.linkHandlingMode = me.ccrama.redditslide.Fragments.SettingsHandlingFragment.LinkHandlingMode.valueFromIdRes(checkedId);
                me.ccrama.redditslide.SettingValues.prefs.edit().putInt(me.ccrama.redditslide.SettingValues.PREF_LINK_HANDLING_MODE, me.ccrama.redditslide.SettingValues.linkHandlingMode).apply();
            }
        });
        final com.google.common.collect.BiMap<java.lang.String, java.lang.String> installedBrowsers = me.ccrama.redditslide.Reddit.getInstalledBrowsers();
        if (!installedBrowsers.containsKey(me.ccrama.redditslide.SettingValues.selectedBrowser)) {
            me.ccrama.redditslide.SettingValues.selectedBrowser = "";
            me.ccrama.redditslide.SettingValues.prefs.edit().putString(me.ccrama.redditslide.SettingValues.PREF_SELECTED_BROWSER, me.ccrama.redditslide.SettingValues.selectedBrowser).apply();
        }
        ((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_handling_browser))).setText(installedBrowsers.get(me.ccrama.redditslide.SettingValues.selectedBrowser));
        if (installedBrowsers.size() <= 1) {
            context.findViewById(me.ccrama.redditslide.R.id.settings_handling_select_browser).setVisibility(android.view.View.GONE);
        } else {
            context.findViewById(me.ccrama.redditslide.R.id.settings_handling_select_browser).setVisibility(android.view.View.VISIBLE);
            context.findViewById(me.ccrama.redditslide.R.id.settings_handling_select_browser).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    final android.support.v7.widget.PopupMenu popupMenu = new android.support.v7.widget.PopupMenu(context, v);
                    for (java.lang.String name : installedBrowsers.values()) {
                        popupMenu.getMenu().add(name);
                    }
                    popupMenu.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
                        @java.lang.Override
                        public boolean onMenuItemClick(android.view.MenuItem item) {
                            me.ccrama.redditslide.SettingValues.selectedBrowser = installedBrowsers.inverse().get(item.getTitle());
                            me.ccrama.redditslide.SettingValues.prefs.edit().putString(me.ccrama.redditslide.SettingValues.PREF_SELECTED_BROWSER, me.ccrama.redditslide.SettingValues.selectedBrowser).apply();
                            ((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_handling_browser))).setText(item.getTitle());
                            return true;
                        }
                    });
                    popupMenu.show();
                }
            });
        }
    }

    @java.lang.Override
    public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case me.ccrama.redditslide.R.id.settings_handling_image :
                me.ccrama.redditslide.SettingValues.image = isChecked;
                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_IMAGE, isChecked).apply();
                break;
            case me.ccrama.redditslide.R.id.settings_handling_gif :
                me.ccrama.redditslide.SettingValues.gif = isChecked;
                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_GIF, isChecked).apply();
                break;
            case me.ccrama.redditslide.R.id.settings_handling_album :
                me.ccrama.redditslide.SettingValues.album = isChecked;
                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_ALBUM, isChecked).apply();
                break;
            case me.ccrama.redditslide.R.id.settings_handling_shortlink :
                me.ccrama.redditslide.SettingValues.shareLongLink = !isChecked;
                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_LONG_LINK, !isChecked).apply();
                break;
            case me.ccrama.redditslide.R.id.settings_handling_peek :
                me.ccrama.redditslide.SettingValues.peek = isChecked;
                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_PEEK, isChecked).apply();
                break;
        }
    }

    private void updateFilters() {
        ((android.widget.LinearLayout) (context.findViewById(me.ccrama.redditslide.R.id.domainlist))).removeAllViews();
        for (java.lang.String s : me.ccrama.redditslide.SettingValues.alwaysExternal) {
            if ((!s.isEmpty()) && ((!me.ccrama.redditslide.Reddit.videoPlugin) || ((!s.contains("youtube.co")) && (!s.contains("youtu.be"))))) {
                final android.view.View t = context.getLayoutInflater().inflate(me.ccrama.redditslide.R.layout.account_textview, context.findViewById(me.ccrama.redditslide.R.id.domainlist), false);
                ((android.widget.TextView) (t.findViewById(me.ccrama.redditslide.R.id.name))).setText(s);
                t.findViewById(me.ccrama.redditslide.R.id.remove).setOnClickListener((android.view.View v) -> {
                    me.ccrama.redditslide.SettingValues.alwaysExternal.remove(s);
                    updateFilters();
                });
                ((android.widget.LinearLayout) (context.findViewById(me.ccrama.redditslide.R.id.domainlist))).addView(t);
            }
        }
    }

    public enum LinkHandlingMode {

        EXTERNAL(0, me.ccrama.redditslide.R.id.settings_handling_browser_type_external_browser),
        INTERNAL(1, me.ccrama.redditslide.R.id.settings_handling_browser_type_internal_browser),
        CUSTOM_TABS(2, me.ccrama.redditslide.R.id.settings_handling_browser_type_custom_tabs);
        private static com.google.common.collect.BiMap<java.lang.Integer, java.lang.Integer> sBiMap = com.google.common.collect.HashBiMap.create(new java.util.HashMap<java.lang.Integer, java.lang.Integer>() {
            {
                put(me.ccrama.redditslide.Fragments.SettingsHandlingFragment.LinkHandlingMode.EXTERNAL.getValue(), me.ccrama.redditslide.Fragments.SettingsHandlingFragment.LinkHandlingMode.EXTERNAL.getIdRes());
                put(me.ccrama.redditslide.Fragments.SettingsHandlingFragment.LinkHandlingMode.INTERNAL.getValue(), me.ccrama.redditslide.Fragments.SettingsHandlingFragment.LinkHandlingMode.INTERNAL.getIdRes());
                put(me.ccrama.redditslide.Fragments.SettingsHandlingFragment.LinkHandlingMode.CUSTOM_TABS.getValue(), me.ccrama.redditslide.Fragments.SettingsHandlingFragment.LinkHandlingMode.CUSTOM_TABS.getIdRes());
            }
        });

        private int mValue;

        @android.support.annotation.IdRes
        private int mIdRes;

        LinkHandlingMode(int value, @android.support.annotation.IdRes
        int stringRes) {
            mValue = value;
            mIdRes = stringRes;
        }

        public int getValue() {
            return mValue;
        }

        public int getIdRes() {
            return mIdRes;
        }

        public static int idResFromValue(int value) {
            return me.ccrama.redditslide.Fragments.SettingsHandlingFragment.LinkHandlingMode.sBiMap.get(value);
        }

        public static int valueFromIdRes(@android.support.annotation.IdRes
        int idRes) {
            return me.ccrama.redditslide.Fragments.SettingsHandlingFragment.LinkHandlingMode.sBiMap.inverse().get(idRes);
        }
    }
}