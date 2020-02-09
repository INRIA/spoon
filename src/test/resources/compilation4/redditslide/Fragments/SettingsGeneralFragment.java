package me.ccrama.redditslide.Fragments;
import java.util.Locale;
import java.util.HashMap;
import me.ccrama.redditslide.Activities.SettingsViewType;
import me.ccrama.redditslide.util.OnSingleClickListener;
import me.ccrama.redditslide.Visuals.Palette;
import java.util.ArrayList;
import me.ccrama.redditslide.Notifications.CheckForMail;
import me.ccrama.redditslide.Notifications.NotificationJobScheduler;
import me.ccrama.redditslide.*;
import me.ccrama.redditslide.util.SortingUtil;
import me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.FolderCallback;
import java.util.List;
import java.io.File;
import java.util.Arrays;
/**
 * Created by ccrama on 3/5/2015.
 */
public class SettingsGeneralFragment<ActivityType extends android.support.v7.app.AppCompatActivity & me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.FolderCallback> implements me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.FolderCallback {
    private ActivityType context;

    public static boolean searchChanged;// whether or not the subreddit search method changed


    public SettingsGeneralFragment(ActivityType context) {
        this.context = context;
    }

    public static void setupNotificationSettings(android.view.View dialoglayout, final android.app.Activity context) {
        final com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(context);
        final com.rey.material.widget.Slider landscape = dialoglayout.findViewById(me.ccrama.redditslide.R.id.landscape);
        final android.widget.CheckBox checkBox = dialoglayout.findViewById(me.ccrama.redditslide.R.id.load);
        final android.widget.CheckBox sound = dialoglayout.findViewById(me.ccrama.redditslide.R.id.sound);
        sound.setChecked(me.ccrama.redditslide.SettingValues.notifSound);
        sound.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_SOUND_NOTIFS, isChecked).apply();
                me.ccrama.redditslide.SettingValues.notifSound = isChecked;
            }
        });
        if (me.ccrama.redditslide.Reddit.notificationTime == (-1)) {
            checkBox.setChecked(false);
            checkBox.setText(context.getString(me.ccrama.redditslide.R.string.settings_mail_check));
        } else {
            checkBox.setChecked(true);
            landscape.setValue(me.ccrama.redditslide.Reddit.notificationTime / 15, false);
            checkBox.setText(context.getString(me.ccrama.redditslide.R.string.settings_notification_newline, me.ccrama.redditslide.TimeUtils.getTimeInHoursAndMins(me.ccrama.redditslide.Reddit.notificationTime, context.getBaseContext())));
        }
        landscape.setOnPositionChangeListener(new com.rey.material.widget.Slider.OnPositionChangeListener() {
            @java.lang.Override
            public void onPositionChanged(com.rey.material.widget.Slider slider, boolean b, float v, float v1, int i, int i1) {
                if (checkBox.isChecked()) {
                    checkBox.setText(context.getString(me.ccrama.redditslide.R.string.settings_notification, me.ccrama.redditslide.TimeUtils.getTimeInHoursAndMins(i1 * 15, context.getBaseContext())));
                }
            }
        });
        checkBox.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @java.lang.Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    me.ccrama.redditslide.Reddit.notificationTime = -1;
                    me.ccrama.redditslide.Reddit.colors.edit().putInt("notificationOverride", -1).apply();
                    checkBox.setText(context.getString(me.ccrama.redditslide.R.string.settings_mail_check));
                    landscape.setValue(0, true);
                    if (me.ccrama.redditslide.Reddit.notifications != null) {
                        me.ccrama.redditslide.Reddit.notifications.cancel(context.getApplication());
                    }
                } else {
                    me.ccrama.redditslide.Reddit.notificationTime = 60;
                    landscape.setValue(4, true);
                    checkBox.setText(context.getString(me.ccrama.redditslide.R.string.settings_notification, me.ccrama.redditslide.TimeUtils.getTimeInHoursAndMins(me.ccrama.redditslide.Reddit.notificationTime, context.getBaseContext())));
                }
            }
        });
        dialoglayout.findViewById(me.ccrama.redditslide.R.id.title).setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getDefaultColor());
        // todo final Slider portrait = (Slider) dialoglayout.findViewById(R.id.portrait);
        // todo  portrait.setBackgroundColor(Palette.getDefaultColor());
        final android.app.Dialog dialog = builder.setView(dialoglayout).create();
        dialog.show();
        dialog.setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {
            @java.lang.Override
            public void onDismiss(android.content.DialogInterface dialog) {
                if (checkBox.isChecked()) {
                    me.ccrama.redditslide.Reddit.notificationTime = landscape.getValue() * 15;
                    me.ccrama.redditslide.Reddit.colors.edit().putInt("notificationOverride", landscape.getValue() * 15).apply();
                    if (me.ccrama.redditslide.Reddit.notifications == null) {
                        me.ccrama.redditslide.Reddit.notifications = new me.ccrama.redditslide.Notifications.NotificationJobScheduler(context.getApplication());
                    }
                    me.ccrama.redditslide.Reddit.notifications.cancel(context.getApplication());
                    me.ccrama.redditslide.Reddit.notifications.start(context.getApplication());
                }
            }
        });
        dialoglayout.findViewById(me.ccrama.redditslide.R.id.save).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View d) {
                if (checkBox.isChecked()) {
                    me.ccrama.redditslide.Reddit.notificationTime = landscape.getValue() * 15;
                    me.ccrama.redditslide.Reddit.colors.edit().putInt("notificationOverride", landscape.getValue() * 15).apply();
                    if (me.ccrama.redditslide.Reddit.notifications == null) {
                        me.ccrama.redditslide.Reddit.notifications = new me.ccrama.redditslide.Notifications.NotificationJobScheduler(context.getApplication());
                    }
                    me.ccrama.redditslide.Reddit.notifications.cancel(context.getApplication());
                    me.ccrama.redditslide.Reddit.notifications.start(context.getApplication());
                    dialog.dismiss();
                    if (context instanceof me.ccrama.redditslide.Activities.SettingsGeneral) {
                        ((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_general_notifications_current))).setText(context.getString(me.ccrama.redditslide.R.string.settings_notification_short, me.ccrama.redditslide.TimeUtils.getTimeInHoursAndMins(me.ccrama.redditslide.Reddit.notificationTime, context.getBaseContext())));
                    }
                } else {
                    me.ccrama.redditslide.Reddit.notificationTime = -1;
                    me.ccrama.redditslide.Reddit.colors.edit().putInt("notificationOverride", -1).apply();
                    if (me.ccrama.redditslide.Reddit.notifications == null) {
                        me.ccrama.redditslide.Reddit.notifications = new me.ccrama.redditslide.Notifications.NotificationJobScheduler(context.getApplication());
                    }
                    me.ccrama.redditslide.Reddit.notifications.cancel(context.getApplication());
                    dialog.dismiss();
                    if (context instanceof me.ccrama.redditslide.Activities.SettingsGeneral) {
                        ((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_general_notifications_current))).setText(me.ccrama.redditslide.R.string.settings_notifdisabled);
                    }
                }
            }
        });
    }

    /* Allow SettingsGeneral and Settings Activity classes to use the same XML functionality */
    public void Bind() {
        context.findViewById(me.ccrama.redditslide.R.id.settings_general_drawer_items).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                new me.ccrama.redditslide.Fragments.DrawerItemsDialog(new com.afollestad.materialdialogs.MaterialDialog.Builder(context)).show();
            }
        });
        {
            android.support.v7.widget.SwitchCompat single = context.findViewById(me.ccrama.redditslide.R.id.settings_general_immersivemode);
            if (single != null) {
                single.setChecked(me.ccrama.redditslide.SettingValues.immersiveMode);
                single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                    @java.lang.Override
                    public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                        me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
                        me.ccrama.redditslide.SettingValues.immersiveMode = isChecked;
                        me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_IMMERSIVE_MODE, isChecked).apply();
                    }
                });
            }
        }
        {
            android.support.v7.widget.SwitchCompat single = context.findViewById(me.ccrama.redditslide.R.id.settings_general_forcelanguage);
            if (single != null) {
                single.setChecked(me.ccrama.redditslide.SettingValues.overrideLanguage);
                single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                    @java.lang.Override
                    public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                        me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
                        me.ccrama.redditslide.SettingValues.overrideLanguage = isChecked;
                        me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_OVERRIDE_LANGUAGE, isChecked).apply();
                    }
                });
            }
        }
        {
            android.support.v7.widget.SwitchCompat single = context.findViewById(me.ccrama.redditslide.R.id.settings_general_subfolder);
            if (single != null) {
                single.setChecked(me.ccrama.redditslide.SettingValues.imageSubfolders);
                single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                    @java.lang.Override
                    public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                        me.ccrama.redditslide.SettingValues.imageSubfolders = isChecked;
                        me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_IMAGE_SUBFOLDERS, isChecked).apply();
                    }
                });
            }
        }
        {
            if (context.findViewById(me.ccrama.redditslide.R.id.settings_general_download) != null) {
                context.findViewById(me.ccrama.redditslide.R.id.settings_general_download).setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        // changes initial path, defaults to external storage directory
                        // changes label of the choose button
                        new me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.Builder(me.ccrama.redditslide.Fragments.SettingsGeneralFragment.this.context).chooseButton(me.ccrama.redditslide.R.string.btn_select).initialPath(android.os.Environment.getExternalStorageDirectory().getPath()).show();
                    }
                });
            }
        }
        if (context.findViewById(me.ccrama.redditslide.R.id.settings_general_location) != null) {
            java.lang.String loc = me.ccrama.redditslide.Reddit.appRestart.getString("imagelocation", context.getString(me.ccrama.redditslide.R.string.settings_image_location_unset));
            ((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_general_location))).setText(loc);
        }
        {
            android.support.v7.widget.SwitchCompat single = context.findViewById(me.ccrama.redditslide.R.id.settings_general_expandedmenu);
            if (single != null) {
                single.setChecked(me.ccrama.redditslide.SettingValues.expandedToolbar);
                single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                    @java.lang.Override
                    public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                        me.ccrama.redditslide.SettingValues.expandedToolbar = isChecked;
                        me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_EXPANDED_TOOLBAR, isChecked).apply();
                    }
                });
            }
        }
        if (context.findViewById(me.ccrama.redditslide.R.id.settings_general_viewtype) != null) {
            context.findViewById(me.ccrama.redditslide.R.id.settings_general_viewtype).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View v) {
                    android.content.Intent i = new android.content.Intent(context, me.ccrama.redditslide.Activities.SettingsViewType.class);
                    context.startActivity(i);
                }
            });
        }
        // FAB multi choice//
        if ((context.findViewById(me.ccrama.redditslide.R.id.settings_general_fab_current) != null) && (context.findViewById(me.ccrama.redditslide.R.id.settings_general_fab) != null)) {
            ((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_general_fab_current))).setText(me.ccrama.redditslide.SettingValues.fab ? me.ccrama.redditslide.SettingValues.fabType == me.ccrama.redditslide.Constants.FAB_DISMISS ? context.getString(me.ccrama.redditslide.R.string.fab_hide) : context.getString(me.ccrama.redditslide.R.string.fab_create) : context.getString(me.ccrama.redditslide.R.string.fab_disabled));
            context.findViewById(me.ccrama.redditslide.R.id.settings_general_fab).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    android.support.v7.widget.PopupMenu popup = new android.support.v7.widget.PopupMenu(context, v);
                    popup.getMenuInflater().inflate(me.ccrama.redditslide.R.menu.fab_settings, popup.getMenu());
                    popup.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(android.view.MenuItem item) {
                            switch (item.getItemId()) {
                                case me.ccrama.redditslide.R.id.disabled :
                                    me.ccrama.redditslide.SettingValues.fab = false;
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_FAB, false).apply();
                                    break;
                                case me.ccrama.redditslide.R.id.hide :
                                    me.ccrama.redditslide.SettingValues.fab = true;
                                    me.ccrama.redditslide.SettingValues.fabType = me.ccrama.redditslide.Constants.FAB_DISMISS;
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putInt(me.ccrama.redditslide.SettingValues.PREF_FAB_TYPE, me.ccrama.redditslide.Constants.FAB_DISMISS).apply();
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_FAB, true).apply();
                                    break;
                                case me.ccrama.redditslide.R.id.create :
                                    me.ccrama.redditslide.SettingValues.fab = true;
                                    me.ccrama.redditslide.SettingValues.fabType = me.ccrama.redditslide.Constants.FAB_POST;
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putInt(me.ccrama.redditslide.SettingValues.PREF_FAB_TYPE, me.ccrama.redditslide.Constants.FAB_POST).apply();
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_FAB, true).apply();
                                    break;
                                case me.ccrama.redditslide.R.id.search :
                                    me.ccrama.redditslide.SettingValues.fab = true;
                                    me.ccrama.redditslide.SettingValues.fabType = me.ccrama.redditslide.Constants.FAB_SEARCH;
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putInt(me.ccrama.redditslide.SettingValues.PREF_FAB_TYPE, me.ccrama.redditslide.Constants.FAB_SEARCH).apply();
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_FAB, true).apply();
                                    break;
                            }
                            final android.widget.TextView fabTitle = context.findViewById(me.ccrama.redditslide.R.id.settings_general_fab_current);
                            if (me.ccrama.redditslide.SettingValues.fab) {
                                if (me.ccrama.redditslide.SettingValues.fabType == me.ccrama.redditslide.Constants.FAB_DISMISS) {
                                    fabTitle.setText(me.ccrama.redditslide.R.string.fab_hide);
                                } else if (me.ccrama.redditslide.SettingValues.fabType == me.ccrama.redditslide.Constants.FAB_POST) {
                                    fabTitle.setText(me.ccrama.redditslide.R.string.fab_create);
                                } else {
                                    fabTitle.setText(me.ccrama.redditslide.R.string.fab_search);
                                }
                            } else {
                                fabTitle.setText(me.ccrama.redditslide.R.string.fab_disabled);
                            }
                            return true;
                        }
                    });
                    popup.show();
                }
            });
        }
        // SettingValues.subredditSearchMethod == 1 for drawer, 2 for toolbar, 3 for both
        final android.widget.TextView currentMethodTitle = context.findViewById(me.ccrama.redditslide.R.id.settings_general_subreddit_search_method_current);
        if (currentMethodTitle != null) {
            if (me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_DRAWER) {
                currentMethodTitle.setText(context.getString(me.ccrama.redditslide.R.string.subreddit_search_method_drawer));
            } else if (me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_TOOLBAR) {
                currentMethodTitle.setText(context.getString(me.ccrama.redditslide.R.string.subreddit_search_method_toolbar));
            } else if (me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_BOTH) {
                currentMethodTitle.setText(context.getString(me.ccrama.redditslide.R.string.subreddit_search_method_both));
            }
        }
        if (context.findViewById(me.ccrama.redditslide.R.id.settings_general_subreddit_search_method) != null) {
            context.findViewById(me.ccrama.redditslide.R.id.settings_general_subreddit_search_method).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    android.support.v7.widget.PopupMenu popup = new android.support.v7.widget.PopupMenu(me.ccrama.redditslide.Fragments.SettingsGeneralFragment.this.context, v);
                    popup.getMenuInflater().inflate(me.ccrama.redditslide.R.menu.subreddit_search_settings, popup.getMenu());
                    popup.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(android.view.MenuItem item) {
                            switch (item.getItemId()) {
                                case me.ccrama.redditslide.R.id.subreddit_search_drawer :
                                    me.ccrama.redditslide.SettingValues.subredditSearchMethod = me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_DRAWER;
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putInt(me.ccrama.redditslide.SettingValues.PREF_SUBREDDIT_SEARCH_METHOD, me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_DRAWER).apply();
                                    me.ccrama.redditslide.Fragments.SettingsGeneralFragment.searchChanged = true;
                                    break;
                                case me.ccrama.redditslide.R.id.subreddit_search_toolbar :
                                    me.ccrama.redditslide.SettingValues.subredditSearchMethod = me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_TOOLBAR;
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putInt(me.ccrama.redditslide.SettingValues.PREF_SUBREDDIT_SEARCH_METHOD, me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_TOOLBAR).apply();
                                    me.ccrama.redditslide.Fragments.SettingsGeneralFragment.searchChanged = true;
                                    break;
                                case me.ccrama.redditslide.R.id.subreddit_search_both :
                                    me.ccrama.redditslide.SettingValues.subredditSearchMethod = me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_BOTH;
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putInt(me.ccrama.redditslide.SettingValues.PREF_SUBREDDIT_SEARCH_METHOD, me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_BOTH).apply();
                                    me.ccrama.redditslide.Fragments.SettingsGeneralFragment.searchChanged = true;
                                    break;
                            }
                            if (me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_DRAWER) {
                                currentMethodTitle.setText(context.getString(me.ccrama.redditslide.R.string.subreddit_search_method_drawer));
                            } else if (me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_TOOLBAR) {
                                currentMethodTitle.setText(context.getString(me.ccrama.redditslide.R.string.subreddit_search_method_toolbar));
                            } else if (me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_BOTH) {
                                currentMethodTitle.setText(context.getString(me.ccrama.redditslide.R.string.subreddit_search_method_both));
                            }
                            return true;
                        }
                    });
                    popup.show();
                }
            });
        }
        final android.widget.TextView currentBackButtonTitle = context.findViewById(me.ccrama.redditslide.R.id.settings_general_back_button_behavior_current);
        if (me.ccrama.redditslide.SettingValues.backButtonBehavior == me.ccrama.redditslide.Constants.BackButtonBehaviorOptions.ConfirmExit.getValue()) {
            currentBackButtonTitle.setText(context.getString(me.ccrama.redditslide.R.string.back_button_behavior_confirm_exit));
        } else if (me.ccrama.redditslide.SettingValues.backButtonBehavior == me.ccrama.redditslide.Constants.BackButtonBehaviorOptions.OpenDrawer.getValue()) {
            currentBackButtonTitle.setText(context.getString(me.ccrama.redditslide.R.string.back_button_behavior_drawer));
        } else {
            currentBackButtonTitle.setText(context.getString(me.ccrama.redditslide.R.string.back_button_behavior_default));
        }
        context.findViewById(me.ccrama.redditslide.R.id.settings_general_back_button_behavior).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                android.support.v7.widget.PopupMenu popup = new android.support.v7.widget.PopupMenu(context, v);
                popup.getMenuInflater().inflate(me.ccrama.redditslide.R.menu.back_button_behavior_settings, popup.getMenu());
                popup.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(android.view.MenuItem item) {
                        switch (item.getItemId()) {
                            case me.ccrama.redditslide.R.id.back_button_behavior_default :
                                me.ccrama.redditslide.SettingValues.backButtonBehavior = me.ccrama.redditslide.Constants.BackButtonBehaviorOptions.Default.getValue();
                                me.ccrama.redditslide.SettingValues.prefs.edit().putInt(me.ccrama.redditslide.SettingValues.PREF_BACK_BUTTON_BEHAVIOR, me.ccrama.redditslide.Constants.BackButtonBehaviorOptions.Default.getValue()).apply();
                                break;
                            case me.ccrama.redditslide.R.id.back_button_behavior_confirm_exit :
                                me.ccrama.redditslide.SettingValues.backButtonBehavior = me.ccrama.redditslide.Constants.BackButtonBehaviorOptions.ConfirmExit.getValue();
                                me.ccrama.redditslide.SettingValues.prefs.edit().putInt(me.ccrama.redditslide.SettingValues.PREF_BACK_BUTTON_BEHAVIOR, me.ccrama.redditslide.Constants.BackButtonBehaviorOptions.ConfirmExit.getValue()).apply();
                                break;
                            case me.ccrama.redditslide.R.id.back_button_behavior_open_drawer :
                                me.ccrama.redditslide.SettingValues.backButtonBehavior = me.ccrama.redditslide.Constants.BackButtonBehaviorOptions.OpenDrawer.getValue();
                                me.ccrama.redditslide.SettingValues.prefs.edit().putInt(me.ccrama.redditslide.SettingValues.PREF_BACK_BUTTON_BEHAVIOR, me.ccrama.redditslide.Constants.BackButtonBehaviorOptions.OpenDrawer.getValue()).apply();
                                break;
                        }
                        if (me.ccrama.redditslide.SettingValues.backButtonBehavior == me.ccrama.redditslide.Constants.BackButtonBehaviorOptions.ConfirmExit.getValue()) {
                            currentBackButtonTitle.setText(context.getString(me.ccrama.redditslide.R.string.back_button_behavior_confirm_exit));
                        } else if (me.ccrama.redditslide.SettingValues.backButtonBehavior == me.ccrama.redditslide.Constants.BackButtonBehaviorOptions.OpenDrawer.getValue()) {
                            currentBackButtonTitle.setText(context.getString(me.ccrama.redditslide.R.string.back_button_behavior_drawer));
                        } else {
                            currentBackButtonTitle.setText(context.getString(me.ccrama.redditslide.R.string.back_button_behavior_default));
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
        if ((context.findViewById(me.ccrama.redditslide.R.id.settings_general_notifications_current) != null) && (context.findViewById(me.ccrama.redditslide.R.id.settings_general_sub_notifs_current) != null)) {
            if (me.ccrama.redditslide.Reddit.notificationTime > 0) {
                ((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_general_notifications_current))).setText(context.getString(me.ccrama.redditslide.R.string.settings_notification_short, me.ccrama.redditslide.TimeUtils.getTimeInHoursAndMins(me.ccrama.redditslide.Reddit.notificationTime, context.getBaseContext())));
                setSubText();
            } else {
                ((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_general_notifications_current))).setText(me.ccrama.redditslide.R.string.settings_notifdisabled);
                ((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_general_sub_notifs_current))).setText(me.ccrama.redditslide.R.string.settings_enable_notifs);
            }
        }
        if (me.ccrama.redditslide.Authentication.isLoggedIn) {
            if (context.findViewById(me.ccrama.redditslide.R.id.settings_general_notifications) != null) {
                context.findViewById(me.ccrama.redditslide.R.id.settings_general_notifications).setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        android.view.LayoutInflater inflater = context.getLayoutInflater();
                        final android.view.View dialoglayout = inflater.inflate(me.ccrama.redditslide.R.layout.inboxfrequency, null);
                        me.ccrama.redditslide.Fragments.SettingsGeneralFragment.setupNotificationSettings(dialoglayout, me.ccrama.redditslide.Fragments.SettingsGeneralFragment.this.context);
                    }
                });
            }
            if (context.findViewById(me.ccrama.redditslide.R.id.settings_general_sub_notifications) != null) {
                context.findViewById(me.ccrama.redditslide.R.id.settings_general_sub_notifications).setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        showSelectDialog();
                    }
                });
            }
        } else {
            if (context.findViewById(me.ccrama.redditslide.R.id.settings_general_notifications) != null) {
                context.findViewById(me.ccrama.redditslide.R.id.settings_general_notifications).setEnabled(false);
                context.findViewById(me.ccrama.redditslide.R.id.settings_general_notifications).setAlpha(0.25F);
            }
            if (context.findViewById(me.ccrama.redditslide.R.id.settings_general_sub_notifications) != null) {
                context.findViewById(me.ccrama.redditslide.R.id.settings_general_sub_notifications).setEnabled(false);
                context.findViewById(me.ccrama.redditslide.R.id.settings_general_sub_notifications).setAlpha(0.25F);
            }
        }
        if (context.findViewById(me.ccrama.redditslide.R.id.settings_general_sorting_current) != null) {
            ((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_general_sorting_current))).setText(me.ccrama.redditslide.util.SortingUtil.getSortingStrings()[me.ccrama.redditslide.util.SortingUtil.getSortingId("")]);
        }
        {
            if (context.findViewById(me.ccrama.redditslide.R.id.settings_general_sorting) != null) {
                context.findViewById(me.ccrama.redditslide.R.id.settings_general_sorting).setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        final android.content.DialogInterface.OnClickListener l2 = new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialogInterface, int i) {
                                switch (i) {
                                    case 0 :
                                        me.ccrama.redditslide.util.SortingUtil.defaultSorting = net.dean.jraw.paginators.Sorting.HOT;
                                        break;
                                    case 1 :
                                        me.ccrama.redditslide.util.SortingUtil.defaultSorting = net.dean.jraw.paginators.Sorting.NEW;
                                        break;
                                    case 2 :
                                        me.ccrama.redditslide.util.SortingUtil.defaultSorting = net.dean.jraw.paginators.Sorting.RISING;
                                        break;
                                    case 3 :
                                        me.ccrama.redditslide.util.SortingUtil.defaultSorting = net.dean.jraw.paginators.Sorting.TOP;
                                        askTimePeriod();
                                        return;
                                    case 4 :
                                        me.ccrama.redditslide.util.SortingUtil.defaultSorting = net.dean.jraw.paginators.Sorting.CONTROVERSIAL;
                                        askTimePeriod();
                                        return;
                                }
                                me.ccrama.redditslide.SettingValues.prefs.edit().putString("defaultSorting", me.ccrama.redditslide.util.SortingUtil.defaultSorting.name()).apply();
                                me.ccrama.redditslide.SettingValues.defaultSorting = me.ccrama.redditslide.util.SortingUtil.defaultSorting;
                                if (context.findViewById(me.ccrama.redditslide.R.id.settings_general_sorting_current) != null) {
                                    ((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_general_sorting_current))).setText(me.ccrama.redditslide.util.SortingUtil.getSortingStrings()[me.ccrama.redditslide.util.SortingUtil.getSortingId("")]);
                                }
                            }
                        };
                        com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Fragments.SettingsGeneralFragment.this.context);
                        builder.setTitle(me.ccrama.redditslide.R.string.sorting_choose);
                        // Remove the "Best" sorting option from settings because it is only supported on the frontpage.
                        int skip = -1;
                        java.util.List<java.lang.String> sortingStrings = new java.util.ArrayList<>(java.util.Arrays.asList(me.ccrama.redditslide.util.SortingUtil.getSortingStrings()));
                        for (int i = 0; i < sortingStrings.size(); i++) {
                            if (sortingStrings.get(i).equals(context.getString(me.ccrama.redditslide.R.string.sorting_best))) {
                                skip = i;
                                break;
                            }
                        }
                        if (skip != (-1)) {
                            sortingStrings.remove(skip);
                        }
                        builder.setSingleChoiceItems(sortingStrings.toArray(new java.lang.String[0]), me.ccrama.redditslide.util.SortingUtil.getSortingId(""), l2);
                        builder.show();
                    }
                });
            }
        }
        me.ccrama.redditslide.Fragments.SettingsGeneralFragment.doNotifText(context);
        {
            final int i2 = (me.ccrama.redditslide.SettingValues.defaultCommentSorting == net.dean.jraw.models.CommentSort.CONFIDENCE) ? 0 : me.ccrama.redditslide.SettingValues.defaultCommentSorting == net.dean.jraw.models.CommentSort.TOP ? 1 : me.ccrama.redditslide.SettingValues.defaultCommentSorting == net.dean.jraw.models.CommentSort.NEW ? 2 : me.ccrama.redditslide.SettingValues.defaultCommentSorting == net.dean.jraw.models.CommentSort.CONTROVERSIAL ? 3 : me.ccrama.redditslide.SettingValues.defaultCommentSorting == net.dean.jraw.models.CommentSort.OLD ? 4 : me.ccrama.redditslide.SettingValues.defaultCommentSorting == net.dean.jraw.models.CommentSort.QA ? 5 : 0;
            if (context.findViewById(me.ccrama.redditslide.R.id.settings_general_sorting_current_comment) != null) {
                ((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_general_sorting_current_comment))).setText(me.ccrama.redditslide.util.SortingUtil.getSortingCommentsStrings()[i2]);
            }
            if (context.findViewById(me.ccrama.redditslide.R.id.settings_general_sorting_comment) != null) {
                context.findViewById(me.ccrama.redditslide.R.id.settings_general_sorting_comment).setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        final android.content.DialogInterface.OnClickListener l2 = new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialogInterface, int i) {
                                net.dean.jraw.models.CommentSort commentSorting = me.ccrama.redditslide.SettingValues.defaultCommentSorting;
                                switch (i) {
                                    case 0 :
                                        commentSorting = net.dean.jraw.models.CommentSort.CONFIDENCE;
                                        break;
                                    case 1 :
                                        commentSorting = net.dean.jraw.models.CommentSort.TOP;
                                        break;
                                    case 2 :
                                        commentSorting = net.dean.jraw.models.CommentSort.NEW;
                                        break;
                                    case 3 :
                                        commentSorting = net.dean.jraw.models.CommentSort.CONTROVERSIAL;
                                        break;
                                    case 4 :
                                        commentSorting = net.dean.jraw.models.CommentSort.OLD;
                                        break;
                                    case 5 :
                                        commentSorting = net.dean.jraw.models.CommentSort.QA;
                                        break;
                                }
                                me.ccrama.redditslide.SettingValues.prefs.edit().putString("defaultCommentSortingNew", commentSorting.name()).apply();
                                me.ccrama.redditslide.SettingValues.defaultCommentSorting = commentSorting;
                                if (context.findViewById(me.ccrama.redditslide.R.id.settings_general_sorting_current_comment) != null) {
                                    ((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_general_sorting_current_comment))).setText(me.ccrama.redditslide.util.SortingUtil.getSortingCommentsStrings()[i]);
                                }
                            }
                        };
                        com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Fragments.SettingsGeneralFragment.this.context);
                        builder.setTitle(me.ccrama.redditslide.R.string.sorting_choose);
                        android.content.res.Resources res = context.getBaseContext().getResources();
                        builder.setSingleChoiceItems(new java.lang.String[]{ res.getString(me.ccrama.redditslide.R.string.sorting_best), res.getString(me.ccrama.redditslide.R.string.sorting_top), res.getString(me.ccrama.redditslide.R.string.sorting_new), res.getString(me.ccrama.redditslide.R.string.sorting_controversial), res.getString(me.ccrama.redditslide.R.string.sorting_old), res.getString(me.ccrama.redditslide.R.string.sorting_ama) }, i2, l2);
                        builder.show();
                    }
                });
            }
        }
    }

    public static void doNotifText(final android.app.Activity context) {
        {
            android.view.View notifs = context.findViewById(me.ccrama.redditslide.R.id.settings_general_redditnotifs);
            if (notifs != null) {
                if ((!me.ccrama.redditslide.Reddit.isPackageInstalled("com.reddit.frontpage")) || (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2)) {
                    notifs.setVisibility(android.view.View.GONE);
                    if (context.findViewById(me.ccrama.redditslide.R.id.settings_general_installreddit) != null) {
                        context.findViewById(me.ccrama.redditslide.R.id.settings_general_installreddit).setVisibility(android.view.View.VISIBLE);
                    }
                } else if (((me.ccrama.redditslide.Reddit) (context.getApplication())).isNotificationAccessEnabled()) {
                    android.support.v7.widget.SwitchCompat single = context.findViewById(me.ccrama.redditslide.R.id.settings_general_piggyback);
                    if (single != null) {
                        single.setChecked(true);
                        single.setEnabled(false);
                    }
                } else {
                    final android.support.v7.widget.SwitchCompat single = context.findViewById(me.ccrama.redditslide.R.id.settings_general_piggyback);
                    if (single != null) {
                        single.setChecked(false);
                        single.setEnabled(true);
                        single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                            @java.lang.Override
                            public void onCheckedChanged(android.widget.CompoundButton compoundButton, boolean b) {
                                single.setChecked(false);
                                android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(single, "Give Slide notification access", android.support.design.widget.Snackbar.LENGTH_LONG);
                                s.setAction("Go to settings", new android.view.View.OnClickListener() {
                                    @java.lang.Override
                                    public void onClick(android.view.View view) {
                                        context.startActivity(new android.content.Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                                    }
                                });
                                s.show();
                            }
                        });
                    }
                }
            }
        }
    }

    private void askTimePeriod() {
        final android.content.DialogInterface.OnClickListener l2 = new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0 :
                        me.ccrama.redditslide.util.SortingUtil.timePeriod = net.dean.jraw.paginators.TimePeriod.HOUR;
                        break;
                    case 1 :
                        me.ccrama.redditslide.util.SortingUtil.timePeriod = net.dean.jraw.paginators.TimePeriod.DAY;
                        break;
                    case 2 :
                        me.ccrama.redditslide.util.SortingUtil.timePeriod = net.dean.jraw.paginators.TimePeriod.WEEK;
                        break;
                    case 3 :
                        me.ccrama.redditslide.util.SortingUtil.timePeriod = net.dean.jraw.paginators.TimePeriod.MONTH;
                        break;
                    case 4 :
                        me.ccrama.redditslide.util.SortingUtil.timePeriod = net.dean.jraw.paginators.TimePeriod.YEAR;
                        break;
                    case 5 :
                        me.ccrama.redditslide.util.SortingUtil.timePeriod = net.dean.jraw.paginators.TimePeriod.ALL;
                        break;
                }
                me.ccrama.redditslide.SettingValues.prefs.edit().putString("defaultSorting", me.ccrama.redditslide.util.SortingUtil.defaultSorting.name()).apply();
                me.ccrama.redditslide.SettingValues.prefs.edit().putString("timePeriod", me.ccrama.redditslide.util.SortingUtil.timePeriod.name()).apply();
                me.ccrama.redditslide.SettingValues.defaultSorting = me.ccrama.redditslide.util.SortingUtil.defaultSorting;
                me.ccrama.redditslide.SettingValues.timePeriod = me.ccrama.redditslide.util.SortingUtil.timePeriod;
                ((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_general_sorting_current))).setText((me.ccrama.redditslide.util.SortingUtil.getSortingStrings()[me.ccrama.redditslide.util.SortingUtil.getSortingId("")] + " > ") + me.ccrama.redditslide.util.SortingUtil.getSortingTimesStrings()[me.ccrama.redditslide.util.SortingUtil.getSortingTimeId("")]);
            }
        };
        com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this.context);
        builder.setTitle(me.ccrama.redditslide.R.string.sorting_choose);
        builder.setSingleChoiceItems(me.ccrama.redditslide.util.SortingUtil.getSortingTimesStrings(), me.ccrama.redditslide.util.SortingUtil.getSortingTimeId(""), l2);
        builder.show();
    }

    private void setSubText() {
        java.util.ArrayList<java.lang.String> rawSubs = me.ccrama.redditslide.Reddit.stringToArray(me.ccrama.redditslide.Reddit.appRestart.getString(me.ccrama.redditslide.Notifications.CheckForMail.SUBS_TO_GET, ""));
        java.lang.String subText = context.getString(me.ccrama.redditslide.R.string.sub_post_notifs_settings_none);
        java.lang.StringBuilder subs = new java.lang.StringBuilder();
        for (java.lang.String s : rawSubs) {
            if (!s.isEmpty()) {
                try {
                    java.lang.String[] split = s.split(":");
                    subs.append(split[0]);
                    subs.append("(+").append(split[1]).append(")");
                    subs.append(", ");
                } catch (java.lang.Exception ignored) {
                }
            }
        }
        if (!subs.toString().isEmpty()) {
            subText = subs.toString().substring(0, subs.toString().length() - 2);
        }
        ((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_general_sub_notifs_current))).setText(subText);
    }

    private java.lang.String input;

    private void showSelectDialog() {
        java.util.ArrayList<java.lang.String> rawSubs = me.ccrama.redditslide.Reddit.stringToArray(me.ccrama.redditslide.Reddit.appRestart.getString(me.ccrama.redditslide.Notifications.CheckForMail.SUBS_TO_GET, ""));
        java.util.HashMap<java.lang.String, java.lang.Integer> subThresholds = new java.util.HashMap<>();
        for (java.lang.String s : rawSubs) {
            try {
                java.lang.String[] split = s.split(":");
                subThresholds.put(split[0].toLowerCase(java.util.Locale.ENGLISH), java.lang.Integer.valueOf(split[1]));
            } catch (java.lang.Exception ignored) {
            }
        }
        // Get list of user's subscriptions
        me.ccrama.redditslide.CaseInsensitiveArrayList subs = me.ccrama.redditslide.UserSubscriptions.getSubscriptions(context);
        // Add any subs that the user has notifications for but isn't subscribed to
        for (java.lang.String s : subThresholds.keySet()) {
            if (!subs.contains(s)) {
                subs.add(s);
            }
        }
        java.util.List<java.lang.String> sorted = me.ccrama.redditslide.UserSubscriptions.sort(subs);
        // Array of all subs
        java.lang.String[] all = new java.lang.String[sorted.size()];
        // Contains which subreddits are checked
        boolean[] checked = new boolean[all.length];
        // Remove special subreddits from list and store it in "all"
        int i = 0;
        for (java.lang.String s : sorted) {
            if (((((!s.equals("all")) && (!s.equals("frontpage"))) && (!s.contains("+"))) && (!s.contains("."))) && (!s.contains("/m/"))) {
                all[i] = s.toLowerCase(java.util.Locale.ENGLISH);
                i++;
            }
        }
        // Remove empty entries & store which subreddits are checked
        java.util.List<java.lang.String> list = new java.util.ArrayList<>();
        i = 0;
        for (java.lang.String s : all) {
            if ((s != null) && (!s.isEmpty())) {
                list.add(s);
                if (subThresholds.keySet().contains(s)) {
                    checked[i] = true;
                }
                i++;
            }
        }
        // Convert List back to Array
        all = list.toArray(new java.lang.String[list.size()]);
        final java.util.ArrayList<java.lang.String> toCheck = new java.util.ArrayList<>(subThresholds.keySet());
        final java.lang.String[] finalAll = all;
        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this.context).setMultiChoiceItems(finalAll, checked, new android.content.DialogInterface.OnMultiChoiceClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialog, int which, boolean isChecked) {
                if (!isChecked) {
                    toCheck.remove(finalAll[which]);
                } else {
                    toCheck.add(finalAll[which]);
                }
            }
        }).alwaysCallMultiChoiceCallback().setTitle(me.ccrama.redditslide.R.string.sub_post_notifs_title_settings).setPositiveButton(context.getString(me.ccrama.redditslide.R.string.btn_add).toUpperCase(), new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                showThresholdDialog(toCheck, false);
            }
        }).setNegativeButton(me.ccrama.redditslide.R.string.sub_post_notifs_settings_search, new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Fragments.SettingsGeneralFragment.this.context).title(me.ccrama.redditslide.R.string.reorder_add_subreddit).inputRangeRes(2, 21, me.ccrama.redditslide.R.color.md_red_500).alwaysCallInputCallback().input(context.getString(me.ccrama.redditslide.R.string.reorder_subreddit_name), null, false, new com.afollestad.materialdialogs.MaterialDialog.InputCallback() {
                    @java.lang.Override
                    public void onInput(com.afollestad.materialdialogs.MaterialDialog dialog, java.lang.CharSequence raw) {
                        input = raw.toString().replaceAll("\\s", "");// remove whitespace from input

                    }
                }).positiveText(me.ccrama.redditslide.R.string.btn_add).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                    @java.lang.Override
                    public void onClick(@android.support.annotation.NonNull
                    com.afollestad.materialdialogs.MaterialDialog dialog, @android.support.annotation.NonNull
                    com.afollestad.materialdialogs.DialogAction which) {
                        new AsyncGetSubreddit().execute(input);
                    }
                }).negativeText(me.ccrama.redditslide.R.string.btn_cancel).onNegative(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                    @java.lang.Override
                    public void onClick(@android.support.annotation.NonNull
                    com.afollestad.materialdialogs.MaterialDialog dialog, @android.support.annotation.NonNull
                    com.afollestad.materialdialogs.DialogAction which) {
                    }
                }).show();
            }
        }).show();
    }

    private void showThresholdDialog(java.util.ArrayList<java.lang.String> strings, boolean search) {
        final java.util.ArrayList<java.lang.String> subsRaw = me.ccrama.redditslide.Reddit.stringToArray(me.ccrama.redditslide.Reddit.appRestart.getString(me.ccrama.redditslide.Notifications.CheckForMail.SUBS_TO_GET, ""));
        if (!search) {
            // NOT a sub searched for, was instead a list of all subs
            for (java.lang.String raw : new java.util.ArrayList<>(subsRaw)) {
                if (!strings.contains(raw.split(":")[0])) {
                    subsRaw.remove(raw);
                }
            }
        }
        final java.util.ArrayList<java.lang.String> subs = new java.util.ArrayList<>();
        for (java.lang.String s : subsRaw) {
            try {
                subs.add(s.split(":")[0].toLowerCase(java.util.Locale.ENGLISH));
            } catch (java.lang.Exception e) {
            }
        }
        final java.util.ArrayList<java.lang.String> toAdd = new java.util.ArrayList<>();
        for (java.lang.String s : strings) {
            if (!subs.contains(s.toLowerCase(java.util.Locale.ENGLISH))) {
                toAdd.add(s.toLowerCase(java.util.Locale.ENGLISH));
            }
        }
        if (!toAdd.isEmpty()) {
            new com.afollestad.materialdialogs.MaterialDialog.Builder(this.context).title(me.ccrama.redditslide.R.string.sub_post_notifs_threshold).items(new java.lang.String[]{ "1", "5", "10", "20", "40", "50" }).alwaysCallSingleChoiceCallback().itemsCallbackSingleChoice(0, new com.afollestad.materialdialogs.MaterialDialog.ListCallbackSingleChoice() {
                @java.lang.Override
                public boolean onSelection(com.afollestad.materialdialogs.MaterialDialog dialog, android.view.View itemView, int which, java.lang.CharSequence text) {
                    for (java.lang.String s : toAdd) {
                        subsRaw.add((s + ":") + text);
                    }
                    saveAndUpdateSubs(subsRaw);
                    return true;
                }
            }).cancelable(false).show();
        } else {
            saveAndUpdateSubs(subsRaw);
        }
    }

    private void saveAndUpdateSubs(java.util.ArrayList<java.lang.String> subs) {
        me.ccrama.redditslide.Reddit.appRestart.edit().putString(me.ccrama.redditslide.Notifications.CheckForMail.SUBS_TO_GET, me.ccrama.redditslide.Reddit.arrayToString(subs)).commit();
        setSubText();
    }

    private class AsyncGetSubreddit extends android.os.AsyncTask<java.lang.String, java.lang.Void, net.dean.jraw.models.Subreddit> {
        @java.lang.Override
        public void onPostExecute(net.dean.jraw.models.Subreddit subreddit) {
            if (((subreddit != null) || input.equalsIgnoreCase("friends")) || input.equalsIgnoreCase("mod")) {
                java.util.ArrayList<java.lang.String> singleSub = new java.util.ArrayList<>();
                singleSub.add(subreddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH));
                showThresholdDialog(singleSub, true);
            }
        }

        @java.lang.Override
        protected net.dean.jraw.models.Subreddit doInBackground(final java.lang.String... params) {
            try {
                return me.ccrama.redditslide.Authentication.reddit.getSubreddit(params[0]);
            } catch (java.lang.Exception e) {
                context.runOnUiThread(new java.lang.Runnable() {
                    @java.lang.Override
                    public void run() {
                        try {
                            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Fragments.SettingsGeneralFragment.this.context).setTitle(me.ccrama.redditslide.R.string.subreddit_err).setMessage(context.getString(me.ccrama.redditslide.R.string.subreddit_err_msg, params[0])).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                                @java.lang.Override
                                public void onClick(android.content.DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {
                                @java.lang.Override
                                public void onDismiss(android.content.DialogInterface dialog) {
                                }
                            }).show();
                        } catch (java.lang.Exception ignored) {
                        }
                    }
                });
                return null;
            }
        }
    }

    public void onFolderSelection(me.ccrama.redditslide.Fragments.FolderChooserDialogCreate dialog, java.io.File folder) {
        if (folder != null) {
            me.ccrama.redditslide.Reddit.appRestart.edit().putString("imagelocation", folder.getAbsolutePath()).apply();
            android.widget.Toast.makeText(context, context.getString(me.ccrama.redditslide.R.string.settings_set_image_location, folder.getAbsolutePath()), android.widget.Toast.LENGTH_LONG).show();
            ((android.widget.TextView) (context.findViewById(me.ccrama.redditslide.R.id.settings_general_location))).setText(folder.getAbsolutePath());
        }
    }
}