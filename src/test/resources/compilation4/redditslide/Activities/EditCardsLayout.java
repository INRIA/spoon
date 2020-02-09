package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.SubmissionCache;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Views.CreateCardView;
import me.ccrama.redditslide.SettingValues;
import java.util.Map;
/**
 * Created by ccrama on 9/17/2015.
 */
public class EditCardsLayout extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstance) {
        overrideRedditSwipeAnywhere();
        overrideSwipeFromAnywhere();
        super.onCreate(savedInstance);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_settings_theme_card);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.settings_layout_default, true, true);
        final android.widget.LinearLayout layout = ((android.widget.LinearLayout) (findViewById(me.ccrama.redditslide.R.id.card)));
        layout.removeAllViews();
        layout.addView(me.ccrama.redditslide.Views.CreateCardView.CreateView(layout));
        // View type//
        // Cards or List//
        ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.view_current))).setText(me.ccrama.redditslide.Views.CreateCardView.isCard() ? me.ccrama.redditslide.Views.CreateCardView.isMiddle() ? getString(me.ccrama.redditslide.R.string.mode_centered) : getString(me.ccrama.redditslide.R.string.mode_card) : me.ccrama.redditslide.Views.CreateCardView.isDesktop() ? getString(me.ccrama.redditslide.R.string.mode_desktop_compact) : getString(me.ccrama.redditslide.R.string.mode_list));
        findViewById(me.ccrama.redditslide.R.id.view).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                android.support.v7.widget.PopupMenu popup = new android.support.v7.widget.PopupMenu(me.ccrama.redditslide.Activities.EditCardsLayout.this, v);
                popup.getMenuInflater().inflate(me.ccrama.redditslide.R.menu.card_mode_settings, popup.getMenu());
                popup.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(android.view.MenuItem item) {
                        switch (item.getItemId()) {
                            case me.ccrama.redditslide.R.id.center :
                                layout.removeAllViews();
                                layout.addView(me.ccrama.redditslide.Views.CreateCardView.setMiddleCard(true, layout));
                                break;
                            case me.ccrama.redditslide.R.id.card :
                                layout.removeAllViews();
                                layout.addView(me.ccrama.redditslide.Views.CreateCardView.setCardViewType(me.ccrama.redditslide.Views.CreateCardView.CardEnum.LARGE, layout));
                                break;
                            case me.ccrama.redditslide.R.id.list :
                                layout.removeAllViews();
                                layout.addView(me.ccrama.redditslide.Views.CreateCardView.setCardViewType(me.ccrama.redditslide.Views.CreateCardView.CardEnum.LIST, layout));
                                break;
                            case me.ccrama.redditslide.R.id.desktop :
                                layout.removeAllViews();
                                layout.addView(me.ccrama.redditslide.Views.CreateCardView.setCardViewType(me.ccrama.redditslide.Views.CreateCardView.CardEnum.DESKTOP, layout));
                                break;
                        }
                        ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.view_current))).setText(me.ccrama.redditslide.Views.CreateCardView.isCard() ? me.ccrama.redditslide.Views.CreateCardView.isMiddle() ? getString(me.ccrama.redditslide.R.string.mode_centered) : getString(me.ccrama.redditslide.R.string.mode_card) : me.ccrama.redditslide.Views.CreateCardView.isDesktop() ? getString(me.ccrama.redditslide.R.string.mode_desktop_compact) : getString(me.ccrama.redditslide.R.string.mode_list));
                        return true;
                    }
                });
                popup.show();
            }
        });
        {
            android.support.v7.widget.SwitchCompat single = ((android.support.v7.widget.SwitchCompat) (findViewById(me.ccrama.redditslide.R.id.commentlast)));
            single.setChecked(me.ccrama.redditslide.SettingValues.commentLastVisit);
            single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.commentLastVisit = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_COMMENT_LAST_VISIT, isChecked).apply();
                }
            });
        }
        {
            android.support.v7.widget.SwitchCompat single = ((android.support.v7.widget.SwitchCompat) (findViewById(me.ccrama.redditslide.R.id.domain)));
            single.setChecked(me.ccrama.redditslide.SettingValues.showDomain);
            single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.showDomain = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_SHOW_DOMAIN, isChecked).apply();
                }
            });
        }
        {
            android.support.v7.widget.SwitchCompat single2 = ((android.support.v7.widget.SwitchCompat) (findViewById(me.ccrama.redditslide.R.id.selftextcomment)));
            single2.setChecked(me.ccrama.redditslide.SettingValues.hideSelftextLeadImage);
            single2.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.hideSelftextLeadImage = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_SELFTEXT_IMAGE_COMMENT, isChecked).apply();
                }
            });
        }
        {
            android.support.v7.widget.SwitchCompat single2 = ((android.support.v7.widget.SwitchCompat) (findViewById(me.ccrama.redditslide.R.id.abbreviateScores)));
            single2.setChecked(me.ccrama.redditslide.SettingValues.abbreviateScores);
            single2.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.abbreviateScores = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_ABBREVIATE_SCORES, isChecked).apply();
                }
            });
        }
        {
            android.support.v7.widget.SwitchCompat single2 = ((android.support.v7.widget.SwitchCompat) (findViewById(me.ccrama.redditslide.R.id.titleTop)));
            single2.setChecked(me.ccrama.redditslide.SettingValues.titleTop);
            single2.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.titleTop = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_TITLE_TOP, isChecked).apply();
                }
            });
        }
        {
            android.support.v7.widget.SwitchCompat single = ((android.support.v7.widget.SwitchCompat) (findViewById(me.ccrama.redditslide.R.id.votes)));
            single.setChecked(me.ccrama.redditslide.SettingValues.votesInfoLine);
            single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.votesInfoLine = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_VOTES_INFO_LINE, isChecked).apply();
                    me.ccrama.redditslide.SubmissionCache.evictAll();
                }
            });
        }
        {
            android.support.v7.widget.SwitchCompat single = ((android.support.v7.widget.SwitchCompat) (findViewById(me.ccrama.redditslide.R.id.contenttype)));
            single.setChecked(me.ccrama.redditslide.SettingValues.typeInfoLine);
            single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.typeInfoLine = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_TYPE_INFO_LINE, isChecked).apply();
                    me.ccrama.redditslide.SubmissionCache.evictAll();
                }
            });
        }
        {
            android.support.v7.widget.SwitchCompat single = ((android.support.v7.widget.SwitchCompat) (findViewById(me.ccrama.redditslide.R.id.selftext)));
            single.setChecked(me.ccrama.redditslide.SettingValues.cardText);
            single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.cardText = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_CARD_TEXT, isChecked).apply();
                }
            });
        }
        // Pic modes//
        final android.widget.TextView CURRENT_PICTURE = ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.picture_current)));
        assert CURRENT_PICTURE != null;// it won't be

        if (me.ccrama.redditslide.SettingValues.bigPicCropped) {
            CURRENT_PICTURE.setText(me.ccrama.redditslide.R.string.mode_cropped);
        } else if (me.ccrama.redditslide.SettingValues.bigPicEnabled) {
            CURRENT_PICTURE.setText(me.ccrama.redditslide.R.string.mode_bigpic);
        } else {
            CURRENT_PICTURE.setText(me.ccrama.redditslide.R.string.mode_thumbnail);
        }
        findViewById(me.ccrama.redditslide.R.id.picture).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                android.support.v7.widget.PopupMenu popup = new android.support.v7.widget.PopupMenu(me.ccrama.redditslide.Activities.EditCardsLayout.this, v);
                popup.getMenuInflater().inflate(me.ccrama.redditslide.R.menu.pic_mode_settings, popup.getMenu());
                popup.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(android.view.MenuItem item) {
                        switch (item.getItemId()) {
                            case me.ccrama.redditslide.R.id.bigpic :
                                layout.removeAllViews();
                                layout.addView(me.ccrama.redditslide.Views.CreateCardView.setBigPicEnabled(true, layout));
                                {
                                    android.content.SharedPreferences.Editor e = me.ccrama.redditslide.SettingValues.prefs.edit();
                                    for (java.util.Map.Entry<java.lang.String, ?> map : me.ccrama.redditslide.SettingValues.prefs.getAll().entrySet()) {
                                        if (map.getKey().startsWith("picsenabled")) {
                                            e.remove(map.getKey());// reset all overridden values

                                        }
                                    }
                                    e.apply();
                                }
                                break;
                            case me.ccrama.redditslide.R.id.cropped :
                                layout.removeAllViews();
                                layout.addView(me.ccrama.redditslide.Views.CreateCardView.setBigPicCropped(true, layout));
                                break;
                            case me.ccrama.redditslide.R.id.thumbnail :
                                layout.removeAllViews();
                                layout.addView(me.ccrama.redditslide.Views.CreateCardView.setBigPicEnabled(false, layout));
                                {
                                    android.content.SharedPreferences.Editor e = me.ccrama.redditslide.SettingValues.prefs.edit();
                                    for (java.util.Map.Entry<java.lang.String, ?> map : me.ccrama.redditslide.SettingValues.prefs.getAll().entrySet()) {
                                        if (map.getKey().startsWith("picsenabled")) {
                                            e.remove(map.getKey());// reset all overridden values

                                        }
                                    }
                                    e.apply();
                                }
                                break;
                        }
                        if (me.ccrama.redditslide.SettingValues.bigPicCropped) {
                            CURRENT_PICTURE.setText(me.ccrama.redditslide.R.string.mode_cropped);
                        } else if (me.ccrama.redditslide.SettingValues.bigPicEnabled) {
                            CURRENT_PICTURE.setText(me.ccrama.redditslide.R.string.mode_bigpic);
                        } else {
                            CURRENT_PICTURE.setText(me.ccrama.redditslide.R.string.mode_thumbnail);
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
        final android.support.v7.widget.SwitchCompat bigThumbnails = ((android.support.v7.widget.SwitchCompat) (findViewById(me.ccrama.redditslide.R.id.bigThumbnails)));
        assert bigThumbnails != null;// def won't be null

        bigThumbnails.setChecked(me.ccrama.redditslide.SettingValues.bigThumbnails);
        bigThumbnails.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean("bigThumbnails", isChecked).apply();
                me.ccrama.redditslide.SettingValues.bigThumbnails = isChecked;
                if ((!me.ccrama.redditslide.SettingValues.bigPicCropped) && (!me.ccrama.redditslide.SettingValues.bigPicCropped)) {
                    layout.removeAllViews();
                    layout.addView(me.ccrama.redditslide.Views.CreateCardView.setBigPicEnabled(false, layout));
                    {
                        android.content.SharedPreferences.Editor e = me.ccrama.redditslide.SettingValues.prefs.edit();
                        for (java.util.Map.Entry<java.lang.String, ?> map : me.ccrama.redditslide.SettingValues.prefs.getAll().entrySet()) {
                            if (map.getKey().startsWith("picsenabled")) {
                                e.remove(map.getKey());// reset all overridden values

                            }
                        }
                        e.apply();
                    }
                }
            }
        });
        // Actionbar//
        ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.actionbar_current))).setText(!me.ccrama.redditslide.SettingValues.actionbarVisible ? me.ccrama.redditslide.SettingValues.actionbarTap ? getString(me.ccrama.redditslide.R.string.tap_actionbar) : getString(me.ccrama.redditslide.R.string.press_actionbar) : getString(me.ccrama.redditslide.R.string.always_actionbar));
        findViewById(me.ccrama.redditslide.R.id.actionbar).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                android.support.v7.widget.PopupMenu popup = new android.support.v7.widget.PopupMenu(me.ccrama.redditslide.Activities.EditCardsLayout.this, v);
                popup.getMenuInflater().inflate(me.ccrama.redditslide.R.menu.actionbar_mode, popup.getMenu());
                popup.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(android.view.MenuItem item) {
                        switch (item.getItemId()) {
                            case me.ccrama.redditslide.R.id.always :
                                me.ccrama.redditslide.SettingValues.actionbarTap = false;
                                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_ACTIONBAR_TAP, false).apply();
                                layout.removeAllViews();
                                layout.addView(me.ccrama.redditslide.Views.CreateCardView.setActionbarVisible(true, layout));
                                break;
                            case me.ccrama.redditslide.R.id.tap :
                                me.ccrama.redditslide.SettingValues.actionbarTap = true;
                                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_ACTIONBAR_TAP, true).apply();
                                layout.removeAllViews();
                                layout.addView(me.ccrama.redditslide.Views.CreateCardView.setActionbarVisible(false, layout));
                                break;
                            case me.ccrama.redditslide.R.id.button :
                                me.ccrama.redditslide.SettingValues.actionbarTap = false;
                                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_ACTIONBAR_TAP, false).apply();
                                layout.removeAllViews();
                                layout.addView(me.ccrama.redditslide.Views.CreateCardView.setActionbarVisible(false, layout));
                                break;
                        }
                        ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.actionbar_current))).setText(!me.ccrama.redditslide.SettingValues.actionbarVisible ? me.ccrama.redditslide.SettingValues.actionbarTap ? getString(me.ccrama.redditslide.R.string.tap_actionbar) : getString(me.ccrama.redditslide.R.string.press_actionbar) : getString(me.ccrama.redditslide.R.string.always_actionbar));
                        return true;
                    }
                });
                popup.show();
            }
        });
        // Other buttons//
        final android.support.v7.widget.AppCompatCheckBox hidebutton = ((android.support.v7.widget.AppCompatCheckBox) (findViewById(me.ccrama.redditslide.R.id.hidebutton)));
        layout.findViewById(me.ccrama.redditslide.R.id.hide).setVisibility(me.ccrama.redditslide.SettingValues.hideButton && me.ccrama.redditslide.SettingValues.actionbarVisible ? android.view.View.VISIBLE : android.view.View.GONE);
        layout.findViewById(me.ccrama.redditslide.R.id.save).setVisibility(me.ccrama.redditslide.SettingValues.saveButton && me.ccrama.redditslide.SettingValues.actionbarVisible ? android.view.View.VISIBLE : android.view.View.GONE);
        hidebutton.setChecked(me.ccrama.redditslide.SettingValues.hideButton);
        hidebutton.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                me.ccrama.redditslide.SettingValues.hideButton = isChecked;
                layout.findViewById(me.ccrama.redditslide.R.id.hide).setVisibility(me.ccrama.redditslide.SettingValues.hideButton && me.ccrama.redditslide.SettingValues.actionbarVisible ? android.view.View.VISIBLE : android.view.View.GONE);
                layout.findViewById(me.ccrama.redditslide.R.id.save).setVisibility(me.ccrama.redditslide.SettingValues.saveButton && me.ccrama.redditslide.SettingValues.actionbarVisible ? android.view.View.VISIBLE : android.view.View.GONE);
                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_HIDEBUTTON, isChecked).apply();
            }
        });
        final android.support.v7.widget.AppCompatCheckBox savebutton = ((android.support.v7.widget.AppCompatCheckBox) (findViewById(me.ccrama.redditslide.R.id.savebutton)));
        layout.findViewById(me.ccrama.redditslide.R.id.save).setVisibility(me.ccrama.redditslide.SettingValues.saveButton && me.ccrama.redditslide.SettingValues.actionbarVisible ? android.view.View.VISIBLE : android.view.View.GONE);
        savebutton.setChecked(me.ccrama.redditslide.SettingValues.saveButton);
        savebutton.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                me.ccrama.redditslide.SettingValues.saveButton = isChecked;
                layout.findViewById(me.ccrama.redditslide.R.id.hide).setVisibility(me.ccrama.redditslide.SettingValues.hideButton && me.ccrama.redditslide.SettingValues.actionbarVisible ? android.view.View.VISIBLE : android.view.View.GONE);
                layout.findViewById(me.ccrama.redditslide.R.id.save).setVisibility(me.ccrama.redditslide.SettingValues.saveButton && me.ccrama.redditslide.SettingValues.actionbarVisible ? android.view.View.VISIBLE : android.view.View.GONE);
                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_SAVE_BUTTON, isChecked).apply();
            }
        });
        // Smaller tags//
        final android.support.v7.widget.SwitchCompat smallTag = ((android.support.v7.widget.SwitchCompat) (findViewById(me.ccrama.redditslide.R.id.tagsetting)));
        smallTag.setChecked(me.ccrama.redditslide.SettingValues.smallTag);
        smallTag.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                layout.removeAllViews();
                layout.addView(me.ccrama.redditslide.Views.CreateCardView.setSmallTag(isChecked, layout));
            }
        });
        // Actionbar//
        // Enable, collapse//
        final android.support.v7.widget.SwitchCompat switchThumb = ((android.support.v7.widget.SwitchCompat) (findViewById(me.ccrama.redditslide.R.id.action)));
        switchThumb.setChecked(me.ccrama.redditslide.SettingValues.switchThumb);
        switchThumb.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                layout.removeAllViews();
                layout.addView(me.ccrama.redditslide.Views.CreateCardView.setSwitchThumb(isChecked, layout));
            }
        });
    }
}