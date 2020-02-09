package me.ccrama.redditslide.Activities;
import java.util.Locale;
import me.ccrama.redditslide.Visuals.GetClosestColor;
import me.ccrama.redditslide.Visuals.Palette;
import java.util.ArrayList;
import me.ccrama.redditslide.ColorPreferences;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Adapters.SettingsSubAdapter;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.UserSubscriptions;
import java.util.List;
import me.ccrama.redditslide.Fragments.SettingsThemeFragment;
/**
 * Created by ccrama on 3/5/2015.
 */
public class SettingsSubreddit extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    public me.ccrama.redditslide.Adapters.SettingsSubAdapter mSettingsSubAdapter;

    java.util.ArrayList<java.lang.String> changedSubs = new java.util.ArrayList<>();

    private android.support.v7.widget.RecyclerView recycler;

    @java.lang.Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        if (requestCode == 2) {
            android.content.Intent i = new android.content.Intent(this, me.ccrama.redditslide.Activities.SettingsSubreddit.class);
            i.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
            overridePendingTransition(0, 0);
        }
    }

    int done;

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_settings_subreddit);
        me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.title_subreddit_settings, true, true);
        recycler = ((android.support.v7.widget.RecyclerView) (findViewById(me.ccrama.redditslide.R.id.subslist)));
        recycler.setLayoutManager(new android.support.v7.widget.LinearLayoutManager(this));
        reloadSubList();
        findViewById(me.ccrama.redditslide.R.id.reset).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.SettingsSubreddit.this).setTitle(me.ccrama.redditslide.R.string.clear_all_sub_themes).setMessage(me.ccrama.redditslide.R.string.clear_all_sub_themes_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        for (java.lang.String s : changedSubs) {
                            me.ccrama.redditslide.Visuals.Palette.removeColor(s);
                            me.ccrama.redditslide.SettingValues.prefs.edit().remove(me.ccrama.redditslide.Reddit.PREF_LAYOUT + s).apply();
                            new me.ccrama.redditslide.ColorPreferences(me.ccrama.redditslide.Activities.SettingsSubreddit.this).removeFontStyle(s);
                            me.ccrama.redditslide.SettingValues.resetPicsEnabled(s);
                        }
                        reloadSubList();
                    }
                }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, null).show();
            }
        });
        findViewById(me.ccrama.redditslide.R.id.post_floating_action_button).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View view) {
                final java.util.ArrayList<java.lang.String> subs = me.ccrama.redditslide.UserSubscriptions.sort(me.ccrama.redditslide.UserSubscriptions.getSubscriptions(me.ccrama.redditslide.Activities.SettingsSubreddit.this));
                final java.lang.CharSequence[] subsAsChar = subs.toArray(new java.lang.CharSequence[subs.size()]);
                com.afollestad.materialdialogs.MaterialDialog.Builder builder = new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.SettingsSubreddit.this);
                builder.title(me.ccrama.redditslide.R.string.dialog_choose_subreddits_to_edit).items(subsAsChar).itemsCallbackMultiChoice(null, new com.afollestad.materialdialogs.MaterialDialog.ListCallbackMultiChoice() {
                    @java.lang.Override
                    public boolean onSelection(com.afollestad.materialdialogs.MaterialDialog dialog, java.lang.Integer[] which, java.lang.CharSequence[] text) {
                        java.util.ArrayList<java.lang.String> selectedSubs = new java.util.ArrayList<>();
                        for (int i : which) {
                            selectedSubs.add(subsAsChar[i].toString());
                        }
                        if (mSettingsSubAdapter != null)
                            mSettingsSubAdapter.prepareAndShowSubEditor(selectedSubs);

                        return true;
                    }
                }).positiveText(me.ccrama.redditslide.R.string.btn_select).negativeText(me.ccrama.redditslide.R.string.btn_cancel).show();
            }
        });
        findViewById(me.ccrama.redditslide.R.id.color).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                if (me.ccrama.redditslide.Authentication.isLoggedIn) {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.SettingsSubreddit.this).setTitle(me.ccrama.redditslide.R.string.dialog_color_sync_title).setMessage(me.ccrama.redditslide.R.string.dialog_color_sync_message).setPositiveButton(me.ccrama.redditslide.R.string.misc_continue, new android.content.DialogInterface.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            final com.afollestad.materialdialogs.MaterialDialog d = new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.SettingsSubreddit.this).title(me.ccrama.redditslide.R.string.general_sub_sync).content(me.ccrama.redditslide.R.string.misc_please_wait).progress(false, 100).cancelable(false).show();
                            new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
                                @java.lang.Override
                                protected java.lang.Void doInBackground(java.lang.Void... params) {
                                    java.util.ArrayList<net.dean.jraw.models.Subreddit> subColors = me.ccrama.redditslide.UserSubscriptions.syncSubredditsGetObject();
                                    d.setMaxProgress(subColors.size());
                                    int i = 0;
                                    done = 0;
                                    for (net.dean.jraw.models.Subreddit s : subColors) {
                                        if ((s.getDataNode().has("key_color") && (!s.getDataNode().get("key_color").asText().isEmpty())) && (me.ccrama.redditslide.Visuals.Palette.getColor(s.getDisplayName().toLowerCase(java.util.Locale.ENGLISH)) == me.ccrama.redditslide.Visuals.Palette.getDefaultColor())) {
                                            me.ccrama.redditslide.Visuals.Palette.setColor(s.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), me.ccrama.redditslide.Visuals.GetClosestColor.getClosestColor(s.getDataNode().get("key_color").asText(), me.ccrama.redditslide.Activities.SettingsSubreddit.this));
                                            done++;
                                        }
                                        d.setProgress(i);
                                        i++;
                                        if (i == d.getMaxProgress()) {
                                            d.dismiss();
                                        }
                                    }
                                    return null;
                                }

                                @java.lang.Override
                                protected void onPostExecute(java.lang.Void aVoid) {
                                    reloadSubList();
                                    android.content.res.Resources res = getResources();
                                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.SettingsSubreddit.this).setTitle(me.ccrama.redditslide.R.string.color_sync_complete).setMessage(res.getQuantityString(me.ccrama.redditslide.R.plurals.color_sync_colored, done, done)).setPositiveButton(getString(me.ccrama.redditslide.R.string.btn_ok), null).show();
                                }
                            }.execute();
                            d.show();
                        }
                    }).setNegativeButton(me.ccrama.redditslide.R.string.btn_cancel, null).show();
                } else {
                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(mToolbar, me.ccrama.redditslide.R.string.err_color_sync_login, android.support.design.widget.Snackbar.LENGTH_SHORT);
                    android.view.View view = s.getView();
                    android.widget.TextView tv = ((android.widget.TextView) (view.findViewById(android.support.design.R.id.snackbar_text)));
                    tv.setTextColor(android.graphics.Color.WHITE);
                    s.show();
                }
            }
        });
    }

    public void reloadSubList() {
        changedSubs.clear();
        java.util.List<java.lang.String> allSubs = me.ccrama.redditslide.UserSubscriptions.sort(me.ccrama.redditslide.UserSubscriptions.getAllUserSubreddits(this));
        // Check which subreddits are different
        me.ccrama.redditslide.ColorPreferences colorPrefs = new me.ccrama.redditslide.ColorPreferences(this);
        int defaultFont = colorPrefs.getFontStyle().getColor();
        for (java.lang.String s : allSubs) {
            if ((((me.ccrama.redditslide.Visuals.Palette.getColor(s) != me.ccrama.redditslide.Visuals.Palette.getDefaultColor()) || me.ccrama.redditslide.SettingValues.prefs.contains(me.ccrama.redditslide.Reddit.PREF_LAYOUT + s)) || (colorPrefs.getFontStyleSubreddit(s).getColor() != defaultFont)) || me.ccrama.redditslide.SettingValues.prefs.contains("picsenabled" + s.toLowerCase(java.util.Locale.ENGLISH))) {
                changedSubs.add(s);
            }
        }
        mSettingsSubAdapter = new me.ccrama.redditslide.Adapters.SettingsSubAdapter(this, changedSubs);
        recycler.setAdapter(mSettingsSubAdapter);
        final android.support.design.widget.FloatingActionButton fab = ((android.support.design.widget.FloatingActionButton) (findViewById(me.ccrama.redditslide.R.id.post_floating_action_button)));
        recycler.setOnScrollListener(new android.support.v7.widget.RecyclerView.OnScrollListener() {
            @java.lang.Override
            public void onScrolled(android.support.v7.widget.RecyclerView recyclerView, int dx, int dy) {
                if ((dy <= 0) && (fab.getId() != 0)) {
                    fab.show();
                } else {
                    fab.hide();
                }
            }

            @java.lang.Override
            public void onScrollStateChanged(android.support.v7.widget.RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        fab.show();
    }
}