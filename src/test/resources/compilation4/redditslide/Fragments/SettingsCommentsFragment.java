package me.ccrama.redditslide.Fragments;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.SettingValues;
public class SettingsCommentsFragment {
    private android.app.Activity context;

    public SettingsCommentsFragment(android.app.Activity context) {
        this.context = context;
    }

    public void Bind() {
        {
            android.support.v7.widget.SwitchCompat single = context.findViewById(me.ccrama.redditslide.R.id.settings_comments_commentnav);
            single.setChecked(me.ccrama.redditslide.SettingValues.fastscroll);
            single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.fastscroll = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_FASTSCROLL, isChecked).apply();
                    // Disable autohidenav and showcollapseexpand if commentNav isn't checked
                    if (!isChecked) {
                        context.findViewById(me.ccrama.redditslide.R.id.settings_comments_autohidenav).setEnabled(false);
                        ((android.support.v7.widget.SwitchCompat) (context.findViewById(me.ccrama.redditslide.R.id.settings_comments_autohidenav))).setChecked(me.ccrama.redditslide.SettingValues.commentAutoHide);
                        context.findViewById(me.ccrama.redditslide.R.id.settings_comments_auto_hide_the_comment_nav_bar_text).setAlpha(0.25F);
                        context.findViewById(me.ccrama.redditslide.R.id.settings_comments_show_collapse_expand).setEnabled(false);
                        ((android.support.v7.widget.SwitchCompat) (context.findViewById(me.ccrama.redditslide.R.id.settings_comments_show_collapse_expand))).setChecked(me.ccrama.redditslide.SettingValues.commentAutoHide);
                        context.findViewById(me.ccrama.redditslide.R.id.show_collapse_expand_nav_bar).setAlpha(0.25F);
                    } else {
                        context.findViewById(me.ccrama.redditslide.R.id.settings_comments_autohidenav).setEnabled(true);
                        context.findViewById(me.ccrama.redditslide.R.id.settings_comments_auto_hide_the_comment_nav_bar_text).setAlpha(1.0F);
                        context.findViewById(me.ccrama.redditslide.R.id.settings_comments_show_collapse_expand).setEnabled(true);
                        context.findViewById(me.ccrama.redditslide.R.id.show_collapse_expand_nav_bar).setAlpha(1.0F);
                    }
                }
            });
        }
        {
            android.support.v7.widget.SwitchCompat single = context.findViewById(me.ccrama.redditslide.R.id.settings_comments_fab);
            single.setChecked(me.ccrama.redditslide.SettingValues.fabComments);
            single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.fabComments = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_COMMENT_FAB, isChecked).apply();
                }
            });
        }
        {
            android.support.v7.widget.SwitchCompat single = context.findViewById(me.ccrama.redditslide.R.id.settings_comments_show_collapse_expand);
            single.setChecked(me.ccrama.redditslide.SettingValues.showCollapseExpand);
            if (!((android.support.v7.widget.SwitchCompat) (context.findViewById(me.ccrama.redditslide.R.id.settings_comments_commentnav))).isChecked()) {
                single.setEnabled(false);
                context.findViewById(me.ccrama.redditslide.R.id.show_collapse_expand_nav_bar).setAlpha(0.25F);
            }
            single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.showCollapseExpand = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_SHOW_COLLAPSE_EXPAND, isChecked).apply();
                }
            });
        }
        {
            android.support.v7.widget.SwitchCompat single = context.findViewById(me.ccrama.redditslide.R.id.settings_comments_rightHandedComments);
            single.setChecked(me.ccrama.redditslide.SettingValues.rightHandedCommentMenu);
            single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.rightHandedCommentMenu = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_RIGHT_HANDED_COMMENT_MENU, isChecked).apply();
                }
            });
        }
        {
            android.support.v7.widget.SwitchCompat single = context.findViewById(me.ccrama.redditslide.R.id.settings_comments_color);
            single.setChecked(me.ccrama.redditslide.SettingValues.colorCommentDepth);
            single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.colorCommentDepth = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_COLOR_COMMENT_DEPTH, isChecked).apply();
                }
            });
        }
        {
            android.support.v7.widget.SwitchCompat single = context.findViewById(me.ccrama.redditslide.R.id.settings_comments_colored_time);
            single.setChecked(me.ccrama.redditslide.SettingValues.highlightTime);
            single.setEnabled(me.ccrama.redditslide.SettingValues.commentLastVisit);
            single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.highlightTime = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_HIGHLIGHT_TIME, isChecked).apply();
                }
            });
        }
        {
            android.support.v7.widget.SwitchCompat single = context.findViewById(me.ccrama.redditslide.R.id.settings_comments_gestures);
            single.setChecked(me.ccrama.redditslide.SettingValues.voteGestures);
            single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.voteGestures = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_VOTE_GESTURES, isChecked).apply();
                }
            });
        }
        {
            android.support.v7.widget.SwitchCompat single = context.findViewById(me.ccrama.redditslide.R.id.settings_comments_percentvote);
            single.setChecked(me.ccrama.redditslide.SettingValues.upvotePercentage);
            single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.upvotePercentage = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_UPVOTE_PERCENTAGE, isChecked).apply();
                }
            });
        }
        {
            android.support.v7.widget.SwitchCompat single = context.findViewById(me.ccrama.redditslide.R.id.settings_comments_autohidenav);
            single.setChecked(me.ccrama.redditslide.SettingValues.commentAutoHide);
            if (!((android.support.v7.widget.SwitchCompat) (context.findViewById(me.ccrama.redditslide.R.id.settings_comments_commentnav))).isChecked()) {
                single.setEnabled(false);
                context.findViewById(me.ccrama.redditslide.R.id.settings_comments_auto_hide_the_comment_nav_bar_text).setAlpha(0.25F);
            }
            single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.commentAutoHide = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_AUTOHIDE_COMMENTS, isChecked).apply();
                }
            });
        }
        {
            android.support.v7.widget.SwitchCompat single = context.findViewById(me.ccrama.redditslide.R.id.settings_comments_opColor);
            single.setChecked(me.ccrama.redditslide.SettingValues.highlightCommentOP);
            single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.highlightCommentOP = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_HIGHLIGHT_COMMENT_OP, isChecked).apply();
                }
            });
        }
        {
            android.support.v7.widget.SwitchCompat single = context.findViewById(me.ccrama.redditslide.R.id.settings_comments_collapse);
            single.setChecked(me.ccrama.redditslide.SettingValues.collapseComments);
            single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.collapseComments = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_COLLAPSE_COMMENTS, isChecked).apply();
                }
            });
        }
        {
            android.support.v7.widget.SwitchCompat single = context.findViewById(me.ccrama.redditslide.R.id.settings_comments_collapse_comments_default);
            single.setChecked(me.ccrama.redditslide.SettingValues.collapseCommentsDefault);
            single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.collapseCommentsDefault = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_COLLAPSE_COMMENTS_DEFAULT, isChecked).apply();
                }
            });
        }
        android.support.v7.widget.SwitchCompat check = context.findViewById(me.ccrama.redditslide.R.id.settings_comments_swapGesture);
        check.setChecked(me.ccrama.redditslide.SettingValues.swap);
        check.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                me.ccrama.redditslide.SettingValues.swap = isChecked;
                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_SWAP, isChecked).apply();
            }
        });
        {
            android.support.v7.widget.SwitchCompat single = context.findViewById(me.ccrama.redditslide.R.id.settings_comments_volumenavcomments);
            single.setChecked(me.ccrama.redditslide.SettingValues.commentVolumeNav);
            single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.commentVolumeNav = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_COMMENT_NAV, isChecked).apply();
                }
            });
        }
        {
            android.support.v7.widget.SwitchCompat single = context.findViewById(me.ccrama.redditslide.R.id.settings_comments_wide);
            single.setChecked(me.ccrama.redditslide.SettingValues.largeDepth);
            single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.largeDepth = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_LARGE_DEPTH, isChecked).apply();
                }
            });
        }
        {
            android.support.v7.widget.SwitchCompat single = context.findViewById(me.ccrama.redditslide.R.id.settings_comments_cropimage);
            single.setChecked(me.ccrama.redditslide.SettingValues.cropImage);
            single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.cropImage = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_CROP_IMAGE, isChecked).apply();
                }
            });
        }
    }
}