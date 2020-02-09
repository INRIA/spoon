package me.ccrama.redditslide.Fragments;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Visuals.FontPreferences;
import me.ccrama.redditslide.SettingValues;
public class SettingsFontFragment {
    private static java.lang.String getFontName(int resource) {
        switch (resource) {
            case me.ccrama.redditslide.R.string.font_size_huge :
                return "Huge";
            case me.ccrama.redditslide.R.string.font_size_larger :
                return "Larger";
            case me.ccrama.redditslide.R.string.font_size_large :
                return "Large";
            case me.ccrama.redditslide.R.string.font_size_medium :
                return "Medium";
            case me.ccrama.redditslide.R.string.font_size_small :
                return "Small";
            case me.ccrama.redditslide.R.string.font_size_smaller :
                return "Smaller";
            case me.ccrama.redditslide.R.string.font_size_tiny :
                return "Tiny";
            default :
                return "Medium";
        }
    }

    private android.app.Activity context;

    public SettingsFontFragment(android.app.Activity context) {
        this.context = context;
    }

    public void Bind() {
        final android.widget.TextView colorComment = ((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_commentFont)));
        colorComment.setText(new me.ccrama.redditslide.Visuals.FontPreferences(context).getCommentFontStyle().getTitle());
        context.findViewById(me.ccrama.redditslide.R.id.settings_font_commentfontsize).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                android.support.v7.widget.PopupMenu popup = new android.support.v7.widget.PopupMenu(context, v);
                popup.getMenu().add(0, me.ccrama.redditslide.R.string.font_size_huge, 0, me.ccrama.redditslide.R.string.font_size_huge);
                popup.getMenu().add(0, me.ccrama.redditslide.R.string.font_size_larger, 0, me.ccrama.redditslide.R.string.font_size_larger);
                popup.getMenu().add(0, me.ccrama.redditslide.R.string.font_size_large, 0, me.ccrama.redditslide.R.string.font_size_large);
                popup.getMenu().add(0, me.ccrama.redditslide.R.string.font_size_medium, 0, me.ccrama.redditslide.R.string.font_size_medium);
                popup.getMenu().add(0, me.ccrama.redditslide.R.string.font_size_small, 0, me.ccrama.redditslide.R.string.font_size_small);
                popup.getMenu().add(0, me.ccrama.redditslide.R.string.font_size_smaller, 0, me.ccrama.redditslide.R.string.font_size_smaller);
                // registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(android.view.MenuItem item) {
                        new me.ccrama.redditslide.Visuals.FontPreferences(context).setCommentFontStyle(me.ccrama.redditslide.Visuals.FontPreferences.FontStyleComment.valueOf(me.ccrama.redditslide.Fragments.SettingsFontFragment.getFontName(item.getItemId())));
                        colorComment.setText(new me.ccrama.redditslide.Visuals.FontPreferences(context).getCommentFontStyle().getTitle());
                        me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
                        return true;
                    }
                });
                popup.show();
            }
        });
        final android.widget.TextView colorPost = ((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_postFont)));
        colorPost.setText(new me.ccrama.redditslide.Visuals.FontPreferences(context).getPostFontStyle().getTitle());
        context.findViewById(me.ccrama.redditslide.R.id.settings_font_postfontsize).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                android.support.v7.widget.PopupMenu popup = new android.support.v7.widget.PopupMenu(context, v);
                popup.getMenu().add(0, me.ccrama.redditslide.R.string.font_size_huge, 0, me.ccrama.redditslide.R.string.font_size_huge);
                popup.getMenu().add(0, me.ccrama.redditslide.R.string.font_size_larger, 0, me.ccrama.redditslide.R.string.font_size_larger);
                popup.getMenu().add(0, me.ccrama.redditslide.R.string.font_size_large, 0, me.ccrama.redditslide.R.string.font_size_large);
                popup.getMenu().add(0, me.ccrama.redditslide.R.string.font_size_medium, 0, me.ccrama.redditslide.R.string.font_size_medium);
                popup.getMenu().add(0, me.ccrama.redditslide.R.string.font_size_small, 0, me.ccrama.redditslide.R.string.font_size_small);
                popup.getMenu().add(0, me.ccrama.redditslide.R.string.font_size_smaller, 0, me.ccrama.redditslide.R.string.font_size_smaller);
                popup.getMenu().add(0, me.ccrama.redditslide.R.string.font_size_tiny, 0, me.ccrama.redditslide.R.string.font_size_tiny);
                // registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(android.view.MenuItem item) {
                        new me.ccrama.redditslide.Visuals.FontPreferences(context).setPostFontStyle(me.ccrama.redditslide.Visuals.FontPreferences.FontStyle.valueOf(me.ccrama.redditslide.Fragments.SettingsFontFragment.getFontName(item.getItemId())));
                        colorPost.setText(new me.ccrama.redditslide.Visuals.FontPreferences(context).getPostFontStyle().getTitle());
                        me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
                        return true;
                    }
                });
                popup.show();
            }
        });
        switch (new me.ccrama.redditslide.Visuals.FontPreferences(context).getFontTypeComment()) {
            case Regular :
                ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_creg))).setChecked(true);
                break;
            case Slab :
                ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_cslab))).setChecked(true);
                break;
            case Condensed :
                ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_ccond))).setChecked(true);
                break;
            case Light :
                ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_clight))).setChecked(true);
                break;
            case System :
                ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_cnone))).setChecked(true);
                break;
        }
        ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_ccond))).setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
                    new me.ccrama.redditslide.Visuals.FontPreferences(context).setCommentFont(me.ccrama.redditslide.Visuals.FontPreferences.FontTypeComment.Condensed);
                }
            }
        });
        ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_cslab))).setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
                    new me.ccrama.redditslide.Visuals.FontPreferences(context).setCommentFont(me.ccrama.redditslide.Visuals.FontPreferences.FontTypeComment.Slab);
                }
            }
        });
        ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_creg))).setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
                    new me.ccrama.redditslide.Visuals.FontPreferences(context).setCommentFont(me.ccrama.redditslide.Visuals.FontPreferences.FontTypeComment.Regular);
                }
            }
        });
        ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_clight))).setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
                    new me.ccrama.redditslide.Visuals.FontPreferences(context).setCommentFont(me.ccrama.redditslide.Visuals.FontPreferences.FontTypeComment.Light);
                }
            }
        });
        ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_cnone))).setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
                    new me.ccrama.redditslide.Visuals.FontPreferences(context).setCommentFont(me.ccrama.redditslide.Visuals.FontPreferences.FontTypeComment.System);
                }
            }
        });
        switch (new me.ccrama.redditslide.Visuals.FontPreferences(context).getFontTypeTitle()) {
            case Regular :
                ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_sreg))).setChecked(true);
                break;
            case Light :
                ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_sregl))).setChecked(true);
                break;
            case Slab :
                ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_sslabl))).setChecked(true);
                break;
            case SlabReg :
                ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_sslab))).setChecked(true);
                break;
            case CondensedReg :
                ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_scond))).setChecked(true);
                break;
            case CondensedBold :
                ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_scondb))).setChecked(true);
                break;
            case Condensed :
                ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.scondl))).setChecked(true);
                break;
            case Bold :
                ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_sbold))).setChecked(true);
                break;
            case Medium :
                ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_smed))).setChecked(true);
                break;
            case System :
                ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_snone))).setChecked(true);
                break;
        }
        ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_scond))).setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
                    new me.ccrama.redditslide.Visuals.FontPreferences(context).setTitleFont(me.ccrama.redditslide.Visuals.FontPreferences.FontTypeTitle.CondensedReg);
                }
            }
        });
        ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_sslab))).setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
                    new me.ccrama.redditslide.Visuals.FontPreferences(context).setTitleFont(me.ccrama.redditslide.Visuals.FontPreferences.FontTypeTitle.SlabReg);
                }
            }
        });
        ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.scondl))).setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
                    new me.ccrama.redditslide.Visuals.FontPreferences(context).setTitleFont(me.ccrama.redditslide.Visuals.FontPreferences.FontTypeTitle.Condensed);
                }
            }
        });
        ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_sbold))).setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
                    new me.ccrama.redditslide.Visuals.FontPreferences(context).setTitleFont(me.ccrama.redditslide.Visuals.FontPreferences.FontTypeTitle.Bold);
                }
            }
        });
        ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_smed))).setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
                    new me.ccrama.redditslide.Visuals.FontPreferences(context).setTitleFont(me.ccrama.redditslide.Visuals.FontPreferences.FontTypeTitle.Medium);
                }
            }
        });
        ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_sslabl))).setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
                    new me.ccrama.redditslide.Visuals.FontPreferences(context).setTitleFont(me.ccrama.redditslide.Visuals.FontPreferences.FontTypeTitle.Slab);
                }
            }
        });
        ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_sreg))).setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
                    new me.ccrama.redditslide.Visuals.FontPreferences(context).setTitleFont(me.ccrama.redditslide.Visuals.FontPreferences.FontTypeTitle.Regular);
                }
            }
        });
        ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_sregl))).setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
                    new me.ccrama.redditslide.Visuals.FontPreferences(context).setTitleFont(me.ccrama.redditslide.Visuals.FontPreferences.FontTypeTitle.Light);
                }
            }
        });
        ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_snone))).setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
                    new me.ccrama.redditslide.Visuals.FontPreferences(context).setTitleFont(me.ccrama.redditslide.Visuals.FontPreferences.FontTypeTitle.System);
                }
            }
        });
        ((com.devspark.robototextview.widget.RobotoRadioButton) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_scondb))).setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
                    new me.ccrama.redditslide.Visuals.FontPreferences(context).setTitleFont(me.ccrama.redditslide.Visuals.FontPreferences.FontTypeTitle.CondensedBold);
                }
            }
        });
        {
            android.support.v7.widget.SwitchCompat single = ((android.support.v7.widget.SwitchCompat) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_linktype)));
            single.setChecked(me.ccrama.redditslide.SettingValues.typeInText);
            single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.typeInText = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_TYPE_IN_TEXT, isChecked).apply();
                }
            });
        }
        {
            android.support.v7.widget.SwitchCompat single = ((android.support.v7.widget.SwitchCompat) (context.findViewById(me.ccrama.redditslide.R.id.settings_font_enlarge_links)));
            single.setChecked(me.ccrama.redditslide.SettingValues.largeLinks);
            single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.largeLinks = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_LARGE_LINKS, isChecked).apply();
                }
            });
        }
    }
}