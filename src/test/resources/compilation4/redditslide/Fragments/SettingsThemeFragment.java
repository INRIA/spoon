package me.ccrama.redditslide.Fragments;
import me.ccrama.redditslide.util.OnSingleClickListener;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.ColorPreferences;
import java.util.ArrayList;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Activities.Slide;
import me.ccrama.redditslide.Activities.BaseActivity;
import org.apache.commons.lang3.ArrayUtils;
import java.util.List;
import java.util.Arrays;
public class SettingsThemeFragment<ActivityType extends me.ccrama.redditslide.Activities.BaseActivity & me.ccrama.redditslide.Fragments.SettingsFragment.RestartActivity> {
    private ActivityType context;

    public static boolean changed;

    int back;

    public SettingsThemeFragment(ActivityType context) {
        this.context = context;
    }

    public void Bind() {
        back = new me.ccrama.redditslide.ColorPreferences(context).getFontStyle().getThemeType();
        context.findViewById(me.ccrama.redditslide.R.id.settings_theme_accent).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                android.view.LayoutInflater inflater = context.getLayoutInflater();
                final android.view.View dialoglayout = inflater.inflate(me.ccrama.redditslide.R.layout.chooseaccent, null);
                com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(context);
                final android.widget.TextView title = dialoglayout.findViewById(me.ccrama.redditslide.R.id.title);
                title.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getDefaultColor());
                final uz.shift.colorpicker.LineColorPicker colorPicker = dialoglayout.findViewById(me.ccrama.redditslide.R.id.picker3);
                int[] arrs = new int[me.ccrama.redditslide.ColorPreferences.getNumColorsFromThemeType(0)];
                int i = 0;
                for (me.ccrama.redditslide.ColorPreferences.Theme type : me.ccrama.redditslide.ColorPreferences.Theme.values()) {
                    if (type.getThemeType() == me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Dark.getValue()) {
                        arrs[i] = android.support.v4.content.ContextCompat.getColor(context, type.getColor());
                        i++;
                    }
                }
                colorPicker.setColors(arrs);
                colorPicker.setSelectedColor(android.support.v4.content.ContextCompat.getColor(context, new me.ccrama.redditslide.ColorPreferences(context).getFontStyle().getColor()));
                dialoglayout.findViewById(me.ccrama.redditslide.R.id.ok).setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
                        int color = colorPicker.getColor();
                        me.ccrama.redditslide.ColorPreferences.Theme t = null;
                        for (me.ccrama.redditslide.ColorPreferences.Theme type : me.ccrama.redditslide.ColorPreferences.Theme.values()) {
                            if ((android.support.v4.content.ContextCompat.getColor(context, type.getColor()) == color) && (back == type.getThemeType())) {
                                t = type;
                                me.ccrama.redditslide.util.LogUtil.v("Setting to " + t.getTitle());
                                break;
                            }
                        }
                        new me.ccrama.redditslide.ColorPreferences(context).setFontStyle(t);
                        context.restartActivity();
                    }
                });
                builder.setView(dialoglayout);
                builder.show();
            }
        });
        context.findViewById(me.ccrama.redditslide.R.id.settings_theme_theme).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                android.view.LayoutInflater inflater = context.getLayoutInflater();
                final android.view.View dialoglayout = inflater.inflate(me.ccrama.redditslide.R.layout.choosethemesmall, null);
                com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(context);
                final android.widget.TextView title = dialoglayout.findViewById(me.ccrama.redditslide.R.id.title);
                title.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getDefaultColor());
                if (me.ccrama.redditslide.SettingValues.isNight()) {
                    dialoglayout.findViewById(me.ccrama.redditslide.R.id.nightmsg).setVisibility(android.view.View.VISIBLE);
                }
                for (final android.util.Pair<java.lang.Integer, java.lang.Integer> pair : me.ccrama.redditslide.ColorPreferences.themePairList) {
                    dialoglayout.findViewById(pair.first).setOnClickListener(new android.view.View.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.view.View v) {
                            me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
                            java.lang.String[] names = new me.ccrama.redditslide.ColorPreferences(context).getFontStyle().getTitle().split("_");
                            java.lang.String name = names[names.length - 1];
                            final java.lang.String newName = name.replace("(", "");
                            for (me.ccrama.redditslide.ColorPreferences.Theme theme : me.ccrama.redditslide.ColorPreferences.Theme.values()) {
                                if (theme.toString().contains(newName) && (theme.getThemeType() == pair.second)) {
                                    back = theme.getThemeType();
                                    new me.ccrama.redditslide.ColorPreferences(context).setFontStyle(theme);
                                    context.restartActivity();
                                    break;
                                }
                            }
                        }
                    });
                }
                builder.setView(dialoglayout);
                builder.show();
            }
        });
        context.findViewById(me.ccrama.redditslide.R.id.settings_theme_main).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                android.view.LayoutInflater inflater = context.getLayoutInflater();
                final android.widget.LinearLayout dialoglayout = ((android.widget.LinearLayout) (inflater.inflate(me.ccrama.redditslide.R.layout.choosemain, null)));
                final com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(context);
                final android.widget.TextView title = dialoglayout.findViewById(me.ccrama.redditslide.R.id.title);
                title.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getDefaultColor());
                final uz.shift.colorpicker.LineColorPicker colorPicker = dialoglayout.findViewById(me.ccrama.redditslide.R.id.picker);
                final uz.shift.colorpicker.LineColorPicker colorPicker2 = dialoglayout.findViewById(me.ccrama.redditslide.R.id.picker2);
                colorPicker.setColors(me.ccrama.redditslide.ColorPreferences.getBaseColors(context));
                int currentColor = me.ccrama.redditslide.Visuals.Palette.getDefaultColor();
                for (int i : colorPicker.getColors()) {
                    for (int i2 : me.ccrama.redditslide.ColorPreferences.getColors(context.getBaseContext(), i)) {
                        if (i2 == currentColor) {
                            colorPicker.setSelectedColor(i);
                            colorPicker2.setColors(me.ccrama.redditslide.ColorPreferences.getColors(context.getBaseContext(), i));
                            colorPicker2.setSelectedColor(i2);
                            break;
                        }
                    }
                }
                colorPicker.setOnColorChangedListener(new uz.shift.colorpicker.OnColorChangedListener() {
                    @java.lang.Override
                    public void onColorChanged(int c) {
                        me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
                        colorPicker2.setColors(me.ccrama.redditslide.ColorPreferences.getColors(context.getBaseContext(), c));
                        colorPicker2.setSelectedColor(c);
                    }
                });
                colorPicker2.setOnColorChangedListener(new uz.shift.colorpicker.OnColorChangedListener() {
                    @java.lang.Override
                    public void onColorChanged(int i) {
                        me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
                        title.setBackgroundColor(colorPicker2.getColor());
                        if (context.findViewById(me.ccrama.redditslide.R.id.toolbar) != null)
                            context.findViewById(me.ccrama.redditslide.R.id.toolbar).setBackgroundColor(colorPicker2.getColor());

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            android.view.Window window = context.getWindow();
                            window.setStatusBarColor(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(colorPicker2.getColor()));
                        }
                        context.setRecentBar(context.getString(me.ccrama.redditslide.R.string.title_theme_settings), colorPicker2.getColor());
                    }
                });
                dialoglayout.findViewById(me.ccrama.redditslide.R.id.ok).setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        if (me.ccrama.redditslide.SettingValues.colorIcon) {
                            context.getPackageManager().setComponentEnabledSetting(new android.content.ComponentName(context, me.ccrama.redditslide.ColorPreferences.getIconName(context, me.ccrama.redditslide.Reddit.colors.getInt("DEFAULTCOLOR", 0))), android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED, android.content.pm.PackageManager.DONT_KILL_APP);
                            context.getPackageManager().setComponentEnabledSetting(new android.content.ComponentName(context, me.ccrama.redditslide.ColorPreferences.getIconName(context, colorPicker2.getColor())), android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED, android.content.pm.PackageManager.DONT_KILL_APP);
                        }
                        me.ccrama.redditslide.Reddit.colors.edit().putInt("DEFAULTCOLOR", colorPicker2.getColor()).apply();
                        context.restartActivity();
                    }
                });
                builder.setView(dialoglayout);
                builder.show();
            }
        });
        // Color tinting mode
        final android.support.v7.widget.SwitchCompat s2 = ((android.support.v7.widget.SwitchCompat) (context.findViewById(me.ccrama.redditslide.R.id.settings_theme_tint_everywhere)));
        ((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_theme_tint_current))).setText(me.ccrama.redditslide.SettingValues.colorBack ? me.ccrama.redditslide.SettingValues.colorSubName ? context.getString(me.ccrama.redditslide.R.string.subreddit_name_tint) : context.getString(me.ccrama.redditslide.R.string.card_background_tint) : context.getString(me.ccrama.redditslide.R.string.misc_none));
        boolean enabled = !((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_theme_tint_current))).getText().equals(context.getString(me.ccrama.redditslide.R.string.misc_none));
        context.findViewById(me.ccrama.redditslide.R.id.settings_theme_dotint).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                android.support.v7.widget.PopupMenu popup = new android.support.v7.widget.PopupMenu(context, v);
                popup.getMenuInflater().inflate(me.ccrama.redditslide.R.menu.color_tinting_mode_settings, popup.getMenu());
                popup.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(android.view.MenuItem item) {
                        switch (item.getItemId()) {
                            case me.ccrama.redditslide.R.id.none :
                                me.ccrama.redditslide.SettingValues.colorBack = false;
                                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_COLOR_BACK, false).apply();
                                break;
                            case me.ccrama.redditslide.R.id.background :
                                me.ccrama.redditslide.SettingValues.colorBack = true;
                                me.ccrama.redditslide.SettingValues.colorSubName = false;
                                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_COLOR_BACK, true).apply();
                                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_COLOR_SUB_NAME, false).apply();
                                break;
                            case me.ccrama.redditslide.R.id.name :
                                me.ccrama.redditslide.SettingValues.colorBack = true;
                                me.ccrama.redditslide.SettingValues.colorSubName = true;
                                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_COLOR_BACK, true).apply();
                                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_COLOR_SUB_NAME, true).apply();
                                break;
                        }
                        ((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_theme_tint_current))).setText(me.ccrama.redditslide.SettingValues.colorBack ? me.ccrama.redditslide.SettingValues.colorSubName ? context.getString(me.ccrama.redditslide.R.string.subreddit_name_tint) : context.getString(me.ccrama.redditslide.R.string.card_background_tint) : context.getString(me.ccrama.redditslide.R.string.misc_none));
                        boolean enabled = !((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_theme_tint_current))).getText().equals(context.getString(me.ccrama.redditslide.R.string.misc_none));
                        s2.setEnabled(enabled);
                        s2.setChecked(me.ccrama.redditslide.SettingValues.colorEverywhere);
                        s2.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                            @java.lang.Override
                            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                                me.ccrama.redditslide.SettingValues.colorEverywhere = isChecked;
                                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_COLOR_EVERYWHERE, isChecked).apply();
                            }
                        });
                        return true;
                    }
                });
                popup.show();
            }
        });
        s2.setEnabled(enabled);
        s2.setChecked(me.ccrama.redditslide.SettingValues.colorEverywhere);
        s2.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                me.ccrama.redditslide.SettingValues.colorEverywhere = isChecked;
                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_COLOR_EVERYWHERE, isChecked).apply();
            }
        });
        final android.support.v7.widget.SwitchCompat colorNavbarSwitch = ((android.support.v7.widget.SwitchCompat) (context.findViewById(me.ccrama.redditslide.R.id.settings_theme_color_navigation_bar)));
        colorNavbarSwitch.setChecked(me.ccrama.redditslide.SettingValues.colorNavBar);
        colorNavbarSwitch.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
                me.ccrama.redditslide.SettingValues.colorNavBar = isChecked;
                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_COLOR_NAV_BAR, isChecked).apply();
                context.themeSystemBars("");
                if (!isChecked) {
                    context.getWindow().setNavigationBarColor(android.graphics.Color.TRANSPARENT);
                }
            }
        });
        final android.support.v7.widget.SwitchCompat colorIcon = ((android.support.v7.widget.SwitchCompat) (context.findViewById(me.ccrama.redditslide.R.id.settings_theme_color_icon)));
        colorIcon.setChecked(me.ccrama.redditslide.SettingValues.colorIcon);
        colorIcon.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                me.ccrama.redditslide.SettingValues.colorIcon = isChecked;
                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_COLOR_ICON, isChecked).apply();
                if (isChecked) {
                    context.getPackageManager().setComponentEnabledSetting(new android.content.ComponentName(context, me.ccrama.redditslide.Activities.Slide.class.getPackage().getName() + ".Slide"), android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED, android.content.pm.PackageManager.DONT_KILL_APP);
                    context.getPackageManager().setComponentEnabledSetting(new android.content.ComponentName(context, me.ccrama.redditslide.ColorPreferences.getIconName(context, me.ccrama.redditslide.Reddit.colors.getInt("DEFAULTCOLOR", 0))), android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED, android.content.pm.PackageManager.DONT_KILL_APP);
                } else {
                    context.getPackageManager().setComponentEnabledSetting(new android.content.ComponentName(context, me.ccrama.redditslide.ColorPreferences.getIconName(context, me.ccrama.redditslide.Reddit.colors.getInt("DEFAULTCOLOR", 0))), android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED, android.content.pm.PackageManager.DONT_KILL_APP);
                    context.getPackageManager().setComponentEnabledSetting(new android.content.ComponentName(context, me.ccrama.redditslide.Activities.Slide.class.getPackage().getName() + ".Slide"), android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED, android.content.pm.PackageManager.DONT_KILL_APP);
                }
            }
        });
        final android.widget.LinearLayout nightMode = ((android.widget.LinearLayout) (context.findViewById(me.ccrama.redditslide.R.id.settings_theme_night)));
        assert nightMode != null;
        nightMode.setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
            @java.lang.Override
            public void onSingleClick(android.view.View view) {
                if (me.ccrama.redditslide.SettingValues.isPro) {
                    android.view.LayoutInflater inflater = context.getLayoutInflater();
                    final android.view.View dialoglayout = inflater.inflate(me.ccrama.redditslide.R.layout.nightmode, null);
                    final com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(context);
                    final android.app.Dialog dialog = builder.setView(dialoglayout).create();
                    dialog.show();
                    dialog.setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {
                        @java.lang.Override
                        public void onDismiss(android.content.DialogInterface dialog) {
                            // todo save
                        }
                    });
                    final android.widget.Spinner startSpinner = dialoglayout.findViewById(me.ccrama.redditslide.R.id.start_spinner);
                    final android.widget.Spinner endSpinner = dialoglayout.findViewById(me.ccrama.redditslide.R.id.end_spinner);
                    android.support.v7.widget.AppCompatSpinner nightModeStateSpinner = dialog.findViewById(me.ccrama.redditslide.R.id.night_mode_state);
                    nightModeStateSpinner.setAdapter(me.ccrama.redditslide.Fragments.SettingsThemeFragment.NightModeArrayAdapter.createFromResource(dialog.getContext(), me.ccrama.redditslide.R.array.night_mode_state, android.R.layout.simple_spinner_dropdown_item));
                    nightModeStateSpinner.setSelection(me.ccrama.redditslide.SettingValues.nightModeState);
                    nightModeStateSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                        @java.lang.Override
                        public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                            startSpinner.setEnabled(position == me.ccrama.redditslide.SettingValues.NightModeState.MANUAL.ordinal());
                            endSpinner.setEnabled(position == me.ccrama.redditslide.SettingValues.NightModeState.MANUAL.ordinal());
                            me.ccrama.redditslide.SettingValues.nightModeState = position;
                            me.ccrama.redditslide.SettingValues.prefs.edit().putInt(me.ccrama.redditslide.SettingValues.PREF_NIGHT_MODE_STATE, position).apply();
                            me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
                        }

                        @java.lang.Override
                        public void onNothingSelected(android.widget.AdapterView<?> parent) {
                        }
                    });
                    for (final android.util.Pair<java.lang.Integer, java.lang.Integer> pair : me.ccrama.redditslide.ColorPreferences.themePairList) {
                        android.widget.RadioButton radioButton = dialoglayout.findViewById(pair.first);
                        if (radioButton != null) {
                            if (me.ccrama.redditslide.SettingValues.nightTheme == pair.second) {
                                radioButton.setChecked(true);
                            }
                            radioButton.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                                @java.lang.Override
                                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
                                        me.ccrama.redditslide.SettingValues.nightTheme = pair.second;
                                        me.ccrama.redditslide.SettingValues.prefs.edit().putInt(me.ccrama.redditslide.SettingValues.PREF_NIGHT_THEME, pair.second).apply();
                                    }
                                }
                            });
                        }
                    }
                    {
                        startSpinner.setEnabled(me.ccrama.redditslide.SettingValues.nightModeState == me.ccrama.redditslide.SettingValues.NightModeState.MANUAL.ordinal());
                        endSpinner.setEnabled(me.ccrama.redditslide.SettingValues.nightModeState == me.ccrama.redditslide.SettingValues.NightModeState.MANUAL.ordinal());
                        final java.util.List<java.lang.String> timesStart = new java.util.ArrayList<java.lang.String>() {
                            {
                                add("6pm");
                                add("7pm");
                                add("8pm");
                                add("9pm");
                                add("10pm");
                                add("11pm");
                            }
                        };
                        dialoglayout.findViewById(me.ccrama.redditslide.R.id.start_spinner_layout).setVisibility(android.view.View.VISIBLE);
                        final android.widget.ArrayAdapter<java.lang.String> startAdapter = new android.widget.ArrayAdapter<>(context, android.R.layout.simple_spinner_item, timesStart);
                        startAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        startSpinner.setAdapter(startAdapter);
                        // set the currently selected pref
                        startSpinner.setSelection(startAdapter.getPosition(java.lang.Integer.toString(me.ccrama.redditslide.SettingValues.nightStart).concat("pm")));
                        startSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                            @java.lang.Override
                            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                                // get the time, but remove the "pm" from the string when parsing
                                final int time = java.lang.Integer.parseInt(((java.lang.String) (startSpinner.getItemAtPosition(position))).replaceAll("pm", ""));
                                me.ccrama.redditslide.SettingValues.nightStart = time;
                                me.ccrama.redditslide.SettingValues.prefs.edit().putInt(me.ccrama.redditslide.SettingValues.PREF_NIGHT_START, time).apply();
                            }

                            @java.lang.Override
                            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                            }
                        });
                    }
                    {
                        final java.util.List<java.lang.String> timesEnd = new java.util.ArrayList<java.lang.String>() {
                            {
                                add("12am");
                                add("1am");
                                add("2am");
                                add("3am");
                                add("4am");
                                add("5am");
                                add("6am");
                                add("7am");
                                add("8am");
                                add("9am");
                                add("10am");
                            }
                        };
                        dialoglayout.findViewById(me.ccrama.redditslide.R.id.end_spinner_layout).setVisibility(android.view.View.VISIBLE);
                        final android.widget.ArrayAdapter<java.lang.String> endAdapter = new android.widget.ArrayAdapter<>(context, android.R.layout.simple_spinner_item, timesEnd);
                        endAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        endSpinner.setAdapter(endAdapter);
                        // set the currently selected pref
                        endSpinner.setSelection(endAdapter.getPosition(java.lang.Integer.toString(me.ccrama.redditslide.SettingValues.nightEnd).concat("am")));
                        endSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                            @java.lang.Override
                            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                                // get the time, but remove the "am" from the string when parsing
                                final int time = java.lang.Integer.parseInt(((java.lang.String) (endSpinner.getItemAtPosition(position))).replaceAll("am", ""));
                                me.ccrama.redditslide.SettingValues.nightEnd = time;
                                me.ccrama.redditslide.SettingValues.prefs.edit().putInt(me.ccrama.redditslide.SettingValues.PREF_NIGHT_END, time).apply();
                            }

                            @java.lang.Override
                            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                            }
                        });
                    }
                    {
                        android.widget.Button okayButton = dialoglayout.findViewById(me.ccrama.redditslide.R.id.ok);
                        okayButton.setOnClickListener(new android.view.View.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.view.View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                } else {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(context).setTitle(me.ccrama.redditslide.R.string.general_nighttheme_ispro).setMessage(me.ccrama.redditslide.R.string.pro_upgrade_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes_exclaim, new android.content.DialogInterface.OnClickListener() {
                        public void onClick(android.content.DialogInterface dialog, int whichButton) {
                            try {
                                context.startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=" + context.getString(me.ccrama.redditslide.R.string.ui_unlock_package))));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                context.startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("http://play.google.com/store/apps/details?id=" + context.getString(me.ccrama.redditslide.R.string.ui_unlock_package))));
                            }
                        }
                    }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no_danks, new android.content.DialogInterface.OnClickListener() {
                        public void onClick(android.content.DialogInterface dialog, int whichButton) {
                        }
                    }).show();
                }
            }
        });
    }

    private static class NightModeArrayAdapter {
        public static android.widget.ArrayAdapter<? extends java.lang.CharSequence> createFromResource(@android.support.annotation.NonNull
        android.content.Context context, @android.support.annotation.ArrayRes
        int textArrayResId, @android.support.annotation.LayoutRes
        int layoutTypeResId) {
            java.lang.CharSequence[] strings = context.getResources().getTextArray(textArrayResId);
            if (!me.ccrama.redditslide.Reddit.canUseNightModeAuto) {
                strings = org.apache.commons.lang3.ArrayUtils.remove(strings, me.ccrama.redditslide.SettingValues.NightModeState.AUTOMATIC.ordinal());
            }
            return new android.widget.ArrayAdapter<>(context, layoutTypeResId, java.util.Arrays.asList(strings));
        }
    }
}