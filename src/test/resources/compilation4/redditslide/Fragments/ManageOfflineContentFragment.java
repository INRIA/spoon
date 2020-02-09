package me.ccrama.redditslide.Fragments;
import me.ccrama.redditslide.Autocache.AutoCacheScheduler;
import java.util.Calendar;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import me.ccrama.redditslide.ColorPreferences;
import me.ccrama.redditslide.CommentCacheAsync;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.OfflineSubreddit;
import me.ccrama.redditslide.util.NetworkUtil;
import me.ccrama.redditslide.TimeUtils;
import java.text.SimpleDateFormat;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.UserSubscriptions;
import java.util.List;
import java.util.Map;
import java.util.Collections;
public class ManageOfflineContentFragment {
    private android.app.Activity context;

    public ManageOfflineContentFragment(android.app.Activity context) {
        this.context = context;
    }

    public void Bind() {
        if (!me.ccrama.redditslide.util.NetworkUtil.isConnected(context))
            me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;

        context.findViewById(me.ccrama.redditslide.R.id.manage_history_clear_all).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                boolean wifi = me.ccrama.redditslide.Reddit.cachedData.getBoolean("wifiOnly", false);
                java.lang.String sync = me.ccrama.redditslide.Reddit.cachedData.getString("toCache", "");
                int hour = me.ccrama.redditslide.Reddit.cachedData.getInt("hour", 0);
                int minute = me.ccrama.redditslide.Reddit.cachedData.getInt("minute", 0);
                me.ccrama.redditslide.Reddit.cachedData.edit().clear().apply();
                me.ccrama.redditslide.Reddit.cachedData.edit().putBoolean("wifiOnly", wifi).putString("toCache", sync).putInt("hour", hour).putInt("minute", minute).apply();
                context.finish();
            }
        });
        if (me.ccrama.redditslide.util.NetworkUtil.isConnectedNoOverride(context)) {
            context.findViewById(me.ccrama.redditslide.R.id.manage_history_sync_now).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    new me.ccrama.redditslide.CommentCacheAsync(context, me.ccrama.redditslide.Reddit.cachedData.getString("toCache", "").split(",")).execute();
                }
            });
        } else {
            context.findViewById(me.ccrama.redditslide.R.id.manage_history_sync_now).setVisibility(android.view.View.GONE);
        }
        {
            android.support.v7.widget.SwitchCompat single = context.findViewById(me.ccrama.redditslide.R.id.manage_history_wifi);
            single.setChecked(me.ccrama.redditslide.Reddit.cachedData.getBoolean("wifiOnly", false));
            single.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.Reddit.cachedData.edit().putBoolean("wifiOnly", isChecked).apply();
                }
            });
        }
        updateBackup();
        updateFilters();
        final java.util.List<java.lang.String> commentDepths = com.google.common.collect.ImmutableList.of("2", "4", "6", "8", "10");
        final java.lang.String[] commentDepthArray = new java.lang.String[commentDepths.size()];
        context.findViewById(me.ccrama.redditslide.R.id.manage_history_comments_depth).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                final java.lang.String commentDepth = me.ccrama.redditslide.SettingValues.prefs.getString(me.ccrama.redditslide.SettingValues.COMMENT_DEPTH, "2");
                com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(context);
                builder.setTitle(me.ccrama.redditslide.R.string.comments_depth);
                builder.setSingleChoiceItems(commentDepths.toArray(commentDepthArray), commentDepths.indexOf(commentDepth), new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        me.ccrama.redditslide.SettingValues.prefs.edit().putString(me.ccrama.redditslide.SettingValues.COMMENT_DEPTH, commentDepths.get(which)).apply();
                    }
                });
                builder.show();
            }
        });
        final java.util.List<java.lang.String> commentCounts = com.google.common.collect.ImmutableList.of("20", "40", "60", "80", "100");
        final java.lang.String[] commentCountArray = new java.lang.String[commentCounts.size()];
        context.findViewById(me.ccrama.redditslide.R.id.manage_history_comments_count).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                final java.lang.String commentCount = me.ccrama.redditslide.SettingValues.prefs.getString(me.ccrama.redditslide.SettingValues.COMMENT_COUNT, "2");
                com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(context);
                builder.setTitle(me.ccrama.redditslide.R.string.comments_count);
                builder.setSingleChoiceItems(commentCounts.toArray(commentCountArray), commentCounts.indexOf(commentCount), new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        me.ccrama.redditslide.SettingValues.prefs.edit().putString(me.ccrama.redditslide.SettingValues.COMMENT_COUNT, commentCounts.get(which)).apply();
                    }
                });
                builder.show();
            }
        });
        context.findViewById(me.ccrama.redditslide.R.id.manage_history_autocache).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                java.util.List<java.lang.String> sorted = me.ccrama.redditslide.UserSubscriptions.sort(me.ccrama.redditslide.UserSubscriptions.getSubscriptions(context));
                final java.lang.String[] all = new java.lang.String[sorted.size()];
                boolean[] checked = new boolean[all.length];
                int i = 0;
                java.util.List<java.lang.String> s2 = new java.util.ArrayList<>();
                java.util.Collections.addAll(s2, me.ccrama.redditslide.Reddit.cachedData.getString("toCache", "").split(","));
                for (java.lang.String s : sorted) {
                    all[i] = s;
                    if (s2.contains(s)) {
                        checked[i] = true;
                    }
                    i++;
                }
                final java.util.ArrayList<java.lang.String> toCheck = new java.util.ArrayList<>();
                toCheck.addAll(s2);
                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(context).alwaysCallMultiChoiceCallback().setMultiChoiceItems(all, checked, new android.content.DialogInterface.OnMultiChoiceClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which, boolean isChecked) {
                        if (!isChecked) {
                            toCheck.remove(all[which]);
                        } else {
                            toCheck.add(all[which]);
                        }
                    }
                }).setTitle(me.ccrama.redditslide.R.string.multireddit_selector).setPositiveButton(context.getString(me.ccrama.redditslide.R.string.btn_add).toUpperCase(), new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        me.ccrama.redditslide.Reddit.cachedData.edit().putString("toCache", me.ccrama.redditslide.Reddit.arrayToString(toCheck)).apply();
                        updateBackup();
                    }
                }).show();
            }
        });
        updateTime();
        context.findViewById(me.ccrama.redditslide.R.id.manage_history_autocache_time_touch).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                final com.rey.material.app.TimePickerDialog d = new com.rey.material.app.TimePickerDialog(context);
                d.hour(me.ccrama.redditslide.Reddit.cachedData.getInt("hour", 0));
                d.minute(me.ccrama.redditslide.Reddit.cachedData.getInt("minute", 0));
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
                    d.applyStyle(new me.ccrama.redditslide.ColorPreferences(context).getFontStyle().getBaseId());

                d.positiveAction("SET");
                android.util.TypedValue typedValue = new android.util.TypedValue();
                android.content.res.Resources.Theme theme = context.getTheme();
                theme.resolveAttribute(me.ccrama.redditslide.R.attr.activity_background, typedValue, true);
                int color = typedValue.data;
                d.backgroundColor(color);
                d.actionTextColor(context.getResources().getColor(new me.ccrama.redditslide.ColorPreferences(context).getFontStyle().getColor()));
                d.positiveActionClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        me.ccrama.redditslide.Reddit.cachedData.edit().putInt("hour", d.getHour()).putInt("minute", d.getMinute()).commit();
                        me.ccrama.redditslide.Reddit.autoCache = new me.ccrama.redditslide.Autocache.AutoCacheScheduler(context);
                        me.ccrama.redditslide.Reddit.autoCache.start(context.getApplicationContext());
                        updateTime();
                        d.dismiss();
                    }
                });
                theme.resolveAttribute(me.ccrama.redditslide.R.attr.fontColor, typedValue, true);
                int color2 = typedValue.data;
                d.setTitle(context.getString(me.ccrama.redditslide.R.string.choose_sync_time));
                d.titleColor(color2);
                d.show();
            }
        });
    }

    public void updateTime() {
        android.widget.TextView text = context.findViewById(me.ccrama.redditslide.R.id.manage_history_autocache_time);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, me.ccrama.redditslide.Reddit.cachedData.getInt("hour", 0));
        cal.set(java.util.Calendar.MINUTE, me.ccrama.redditslide.Reddit.cachedData.getInt("minute", 0));
        if (text != null) {
            text.setText(context.getString(me.ccrama.redditslide.R.string.settings_backup_occurs, new java.text.SimpleDateFormat("hh:mm a").format(cal.getTime())));
        }
    }

    public void updateBackup() {
        subsToBack = new java.util.ArrayList<>();
        java.util.Collections.addAll(subsToBack, me.ccrama.redditslide.Reddit.cachedData.getString("toCache", "").split(","));
        android.widget.TextView text = context.findViewById(me.ccrama.redditslide.R.id.manage_history_autocache_text);
        if ((!me.ccrama.redditslide.Reddit.cachedData.getString("toCache", "").contains(",")) || subsToBack.isEmpty()) {
            text.setText(me.ccrama.redditslide.R.string.settings_backup_none);
        } else {
            java.lang.String toSay = "";
            for (java.lang.String s : subsToBack) {
                if (!s.isEmpty())
                    toSay = (toSay + s) + ", ";

            }
            toSay = toSay.substring(0, toSay.length() - 2);
            toSay += context.getString(me.ccrama.redditslide.R.string.settings_backup_will_backup);
            text.setText(toSay);
        }
    }

    public java.util.ArrayList<java.lang.String> domains = new java.util.ArrayList<>();

    java.util.List<java.lang.String> subsToBack;

    public void updateFilters() {
        if (context.findViewById(me.ccrama.redditslide.R.id.manage_history_domainlist) != null) {
            java.util.Map<java.lang.String, java.lang.String> multiNameToSubsMap = me.ccrama.redditslide.UserSubscriptions.getMultiNameToSubs(true);
            domains = new java.util.ArrayList<>();
            ((android.widget.LinearLayout) (context.findViewById(me.ccrama.redditslide.R.id.manage_history_domainlist))).removeAllViews();
            for (final java.lang.String s : me.ccrama.redditslide.OfflineSubreddit.getAll()) {
                if (!s.isEmpty()) {
                    java.lang.String[] split = s.split(",");
                    java.lang.String sub = split[0];
                    if (multiNameToSubsMap.containsKey(sub)) {
                        sub = multiNameToSubsMap.get(sub);
                    }
                    final java.lang.String name = ((sub.contains("/m/") ? sub : "/r/" + sub) + " → ") + (java.lang.Long.valueOf(split[1]) == 0 ? context.getString(me.ccrama.redditslide.R.string.settings_backup_submission_only) : me.ccrama.redditslide.TimeUtils.getTimeAgo(java.lang.Long.valueOf(split[1]), context) + context.getString(me.ccrama.redditslide.R.string.settings_backup_comments));
                    domains.add(name);
                    final android.view.View t = context.getLayoutInflater().inflate(me.ccrama.redditslide.R.layout.account_textview, ((android.widget.LinearLayout) (context.findViewById(me.ccrama.redditslide.R.id.manage_history_domainlist))), false);
                    ((android.widget.TextView) (t.findViewById(me.ccrama.redditslide.R.id.name))).setText(name);
                    t.findViewById(me.ccrama.redditslide.R.id.remove).setOnClickListener(new android.view.View.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.view.View v) {
                            domains.remove(name);
                            me.ccrama.redditslide.Reddit.cachedData.edit().remove(s).apply();
                            updateFilters();
                        }
                    });
                    ((android.widget.LinearLayout) (context.findViewById(me.ccrama.redditslide.R.id.manage_history_domainlist))).addView(t);
                }
            }
        }
    }
}