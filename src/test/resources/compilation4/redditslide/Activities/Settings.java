package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.DragSort.ReorderSubreddits;
import me.ccrama.redditslide.util.OnSingleClickListener;
import me.ccrama.redditslide.Visuals.Palette;
import java.util.ArrayList;
import me.ccrama.redditslide.SettingValues;
import com.google.common.base.Strings;
import me.ccrama.redditslide.Fragments.*;
import me.ccrama.redditslide.util.NetworkUtil;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.FDroid;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Authentication;
import java.util.List;
import java.io.File;
import java.util.Arrays;
/**
 * Created by ccrama on 3/5/2015.
 */
public class Settings extends me.ccrama.redditslide.Activities.BaseActivity implements me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.FolderCallback , me.ccrama.redditslide.Fragments.SettingsFragment.RestartActivity {
    private static final int RESTART_SETTINGS_RESULT = 2;

    private int scrollY;

    private android.content.SharedPreferences.OnSharedPreferenceChangeListener prefsListener;

    private java.lang.String prev_text;

    public static boolean changed;// whether or not a Setting was changed


    private me.ccrama.redditslide.Fragments.SettingsGeneralFragment mSettingsGeneralFragment = new me.ccrama.redditslide.Fragments.SettingsGeneralFragment(this);

    private me.ccrama.redditslide.Fragments.ManageOfflineContentFragment mManageOfflineContentFragment = new me.ccrama.redditslide.Fragments.ManageOfflineContentFragment(this);

    private me.ccrama.redditslide.Fragments.SettingsThemeFragment mSettingsThemeFragment = new me.ccrama.redditslide.Fragments.SettingsThemeFragment(this);

    private me.ccrama.redditslide.Fragments.SettingsFontFragment mSettingsFontFragment = new me.ccrama.redditslide.Fragments.SettingsFontFragment(this);

    private me.ccrama.redditslide.Fragments.SettingsCommentsFragment mSettingsCommentsFragment = new me.ccrama.redditslide.Fragments.SettingsCommentsFragment(this);

    private me.ccrama.redditslide.Fragments.SettingsHandlingFragment mSettingsHandlingFragment = new me.ccrama.redditslide.Fragments.SettingsHandlingFragment(this);

    private me.ccrama.redditslide.Fragments.SettingsHistoryFragment mSettingsHistoryFragment = new me.ccrama.redditslide.Fragments.SettingsHistoryFragment(this);

    private me.ccrama.redditslide.Fragments.SettingsDataFragment mSettingsDataFragment = new me.ccrama.redditslide.Fragments.SettingsDataFragment(this);

    private me.ccrama.redditslide.Fragments.SettingsRedditFragment mSettingsRedditFragment = new me.ccrama.redditslide.Fragments.SettingsRedditFragment(this);

    private java.util.List<java.lang.Integer> settings_activities = new java.util.ArrayList<>(java.util.Arrays.asList(me.ccrama.redditslide.R.layout.activity_settings_general_child, me.ccrama.redditslide.R.layout.activity_manage_history_child, me.ccrama.redditslide.R.layout.activity_settings_theme_child, me.ccrama.redditslide.R.layout.activity_settings_font_child, me.ccrama.redditslide.R.layout.activity_settings_comments_child, me.ccrama.redditslide.R.layout.activity_settings_handling_child, me.ccrama.redditslide.R.layout.activity_settings_history_child, me.ccrama.redditslide.R.layout.activity_settings_datasaving_child, me.ccrama.redditslide.R.layout.activity_settings_reddit_child));

    @java.lang.Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        if (requestCode == me.ccrama.redditslide.Activities.Settings.RESTART_SETTINGS_RESULT) {
            restartActivity();
        }
    }

    public void restartActivity() {
        android.content.Intent i = new android.content.Intent(this, me.ccrama.redditslide.Activities.Settings.class);
        i.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION);
        i.putExtra("position", scrollY);
        i.putExtra("prev_text", prev_text);
        startActivity(i);
        overridePendingTransition(0, 0);
        finish();
        overridePendingTransition(0, 0);
    }

    @java.lang.Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        android.view.MenuInflater inflater = getMenuInflater();
        inflater.inflate(me.ccrama.redditslide.R.menu.settings, menu);
        return true;
    }

    @java.lang.Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                if (findViewById(me.ccrama.redditslide.R.id.settings_search).getVisibility() == android.view.View.VISIBLE) {
                    findViewById(me.ccrama.redditslide.R.id.settings_search).setVisibility(android.view.View.GONE);
                    findViewById(me.ccrama.redditslide.R.id.search).setVisibility(android.view.View.VISIBLE);
                } else {
                    onBackPressed();
                }
                return true;
            case me.ccrama.redditslide.R.id.search :
                {
                    findViewById(me.ccrama.redditslide.R.id.settings_search).setVisibility(android.view.View.VISIBLE);
                    findViewById(me.ccrama.redditslide.R.id.search).setVisibility(android.view.View.GONE);
                }
                return true;
            default :
                return false;
        }
    }

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_settings);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.title_settings, true, true);
        if ((getIntent() != null) && (!com.google.common.base.Strings.isNullOrEmpty(getIntent().getStringExtra("prev_text")))) {
            prev_text = getIntent().getStringExtra("prev_text");
        } else if (savedInstanceState != null) {
            prev_text = savedInstanceState.getString("prev_text");
        }
        if (!com.google.common.base.Strings.isNullOrEmpty(prev_text)) {
            ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.settings_search))).setText(prev_text);
        }
        BuildLayout(prev_text);
    }

    @java.lang.Override
    protected void onSaveInstanceState(android.os.Bundle outState) {
        outState.putInt("position", scrollY);
        outState.putString("prev_text", prev_text);
        super.onSaveInstanceState(outState);
    }

    @java.lang.Override
    public boolean dispatchKeyEvent(android.view.KeyEvent event) {
        if ((event.getKeyCode() == android.view.KeyEvent.KEYCODE_SEARCH) && (event.getAction() == android.view.KeyEvent.ACTION_DOWN)) {
            onOptionsItemSelected(mToolbar.getMenu().findItem(me.ccrama.redditslide.R.id.search));
            // (findViewById(R.id.settings_search)).requestFocus();
            android.view.MotionEvent motionEventDown = android.view.MotionEvent.obtain(android.os.SystemClock.uptimeMillis(), android.os.SystemClock.uptimeMillis(), android.view.MotionEvent.ACTION_DOWN, 0, 0, 0);
            android.view.MotionEvent motionEventUp = android.view.MotionEvent.obtain(android.os.SystemClock.uptimeMillis(), android.os.SystemClock.uptimeMillis(), android.view.MotionEvent.ACTION_UP, 0, 0, 0);
            findViewById(me.ccrama.redditslide.R.id.settings_search).dispatchTouchEvent(motionEventDown);
            findViewById(me.ccrama.redditslide.R.id.settings_search).dispatchTouchEvent(motionEventUp);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void BuildLayout(java.lang.String text) {
        android.widget.LinearLayout parent = ((android.widget.LinearLayout) (findViewById(me.ccrama.redditslide.R.id.settings_parent)));
        /* Clear the settings out, then re-add the default top-level settings */
        parent.removeAllViews();
        parent.addView(getLayoutInflater().inflate(me.ccrama.redditslide.R.layout.activity_settings_child, null));
        Bind();
        /* The EditView contains text that we can use to search for matching settings */
        if (!com.google.common.base.Strings.isNullOrEmpty(text)) {
            android.view.LayoutInflater inflater = getLayoutInflater();
            for (java.lang.Integer activity : settings_activities) {
                parent.addView(inflater.inflate(activity, null));
            }
            mSettingsGeneralFragment.Bind();
            mManageOfflineContentFragment.Bind();
            mSettingsThemeFragment.Bind();
            mSettingsFontFragment.Bind();
            mSettingsCommentsFragment.Bind();
            mSettingsHandlingFragment.Bind();
            mSettingsHistoryFragment.Bind();
            mSettingsDataFragment.Bind();
            mSettingsRedditFragment.Bind();
            /* Go through each subview and scan it for matching text, non-matches */
            loopViews(parent, text.toLowerCase(), true, "");
        }
        /* Try to clean up the mess we've made */
        java.lang.System.gc();
    }

    private void Bind() {
        me.ccrama.redditslide.SettingValues.expandedSettings = true;
        setSettingItems();
        final android.widget.ScrollView mScrollView = ((android.widget.ScrollView) (findViewById(me.ccrama.redditslide.R.id.base)));
        prefsListener = new android.content.SharedPreferences.OnSharedPreferenceChangeListener() {
            @java.lang.Override
            public void onSharedPreferenceChanged(android.content.SharedPreferences sharedPreferences, java.lang.String key) {
                me.ccrama.redditslide.Activities.Settings.changed = true;
            }
        };
        me.ccrama.redditslide.SettingValues.prefs.registerOnSharedPreferenceChangeListener(prefsListener);
        mScrollView.post(new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                android.view.ViewTreeObserver observer = mScrollView.getViewTreeObserver();
                if (getIntent().hasExtra("position")) {
                    mScrollView.scrollTo(0, getIntent().getIntExtra("position", 0));
                }
                if (getIntent().hasExtra("prev_text")) {
                    prev_text = getIntent().getStringExtra("prev_text");
                }
                observer.addOnScrollChangedListener(new android.view.ViewTreeObserver.OnScrollChangedListener() {
                    @java.lang.Override
                    public void onScrollChanged() {
                        scrollY = mScrollView.getScrollY();
                    }
                });
            }
        });
    }

    private boolean loopViews(android.view.ViewGroup parent, java.lang.String text, boolean isRootViewGroup, java.lang.String indent) {
        boolean foundText = false;
        boolean prev_child_is_View = false;
        for (int i = 0; i < parent.getChildCount(); i++) {
            android.view.View child = parent.getChildAt(i);
            boolean childRemoved = false;
            /* Found some text, remove labels and check for matches on non-labels */
            if (child instanceof android.widget.TextView) {
                // Found text at the top-level that is probably a label, or an explicitly tagged label
                if (isRootViewGroup || ((child.getTag() != null) && child.getTag().toString().equals("label"))) {
                    parent.removeView(child);
                    childRemoved = true;
                    i--;
                } else if (((android.widget.TextView) (child)).getText().toString().toLowerCase().contains(text)) {
                    foundText = true;
                }
                // No match
            } else if (((child != null) && prev_child_is_View) && child.getClass().equals(android.view.View.class)) {
                parent.removeView(child);
                childRemoved = true;
                i--;
            } else if (child instanceof android.view.ViewGroup) {
                // Look for matching TextView in the ViewGroup, remove the ViewGroup if no match is found
                if (!this.loopViews(((android.view.ViewGroup) (child)), text, false, indent + "  ")) {
                    parent.removeView(child);
                    childRemoved = true;
                    i--;
                } else {
                    foundText = true;
                }
            }
            if ((child != null) && (!childRemoved)) {
                prev_child_is_View = child.getClass().equals(android.view.View.class);
            }
        }
        return foundText;
    }

    private void setSettingItems() {
        android.view.View pro = findViewById(me.ccrama.redditslide.R.id.settings_child_pro);
        if (me.ccrama.redditslide.SettingValues.isPro) {
            pro.setVisibility(android.view.View.GONE);
        } else {
            pro.setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View v) {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.Settings.this).setTitle(me.ccrama.redditslide.R.string.settings_support_slide).setMessage(me.ccrama.redditslide.R.string.pro_upgrade_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes_exclaim, new android.content.DialogInterface.OnClickListener() {
                        public void onClick(android.content.DialogInterface dialog, int whichButton) {
                            try {
                                startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=" + getString(me.ccrama.redditslide.R.string.ui_unlock_package))));
                            } catch (android.content.ActivityNotFoundException e) {
                                startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("http://play.google.com/store/apps/details?id=" + getString(me.ccrama.redditslide.R.string.ui_unlock_package))));
                            }
                        }
                    }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no_danks, new android.content.DialogInterface.OnClickListener() {
                        public void onClick(android.content.DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    }).show();
                }
            });
        }
        ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.settings_search))).addTextChangedListener(new android.text.TextWatcher() {
            @java.lang.Override
            public void beforeTextChanged(java.lang.CharSequence charSequence, int i, int i1, int i2) {
            }

            @java.lang.Override
            public void onTextChanged(java.lang.CharSequence s, int start, int before, int count) {
                java.lang.String text = s.toString().trim();
                /* No idea why, but this event can fire many times when there is no change */
                if (text.equalsIgnoreCase(prev_text))
                    return;

                BuildLayout(text);
                prev_text = text;
            }

            @java.lang.Override
            public void afterTextChanged(android.text.Editable editable) {
            }
        });
        findViewById(me.ccrama.redditslide.R.id.settings_child_general).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
            @java.lang.Override
            public void onSingleClick(android.view.View v) {
                android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.Settings.this, me.ccrama.redditslide.Activities.SettingsGeneral.class);
                startActivityForResult(i, me.ccrama.redditslide.Activities.Settings.RESTART_SETTINGS_RESULT);
            }
        });
        findViewById(me.ccrama.redditslide.R.id.settings_child_history).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
            @java.lang.Override
            public void onSingleClick(android.view.View v) {
                android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.Settings.this, me.ccrama.redditslide.Activities.SettingsHistory.class);
                startActivity(i);
            }
        });
        findViewById(me.ccrama.redditslide.R.id.settings_child_about).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
            @java.lang.Override
            public void onSingleClick(android.view.View v) {
                android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.Settings.this, me.ccrama.redditslide.Activities.SettingsAbout.class);
                startActivity(i);
            }
        });
        findViewById(me.ccrama.redditslide.R.id.settings_child_offline).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View view) {
                android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.Settings.this, me.ccrama.redditslide.Activities.ManageOfflineContent.class);
                startActivity(i);
            }
        });
        findViewById(me.ccrama.redditslide.R.id.settings_child_datasave).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
            @java.lang.Override
            public void onSingleClick(android.view.View v) {
                android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.Settings.this, me.ccrama.redditslide.Activities.SettingsData.class);
                startActivity(i);
            }
        });
        findViewById(me.ccrama.redditslide.R.id.settings_child_subtheme).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
            @java.lang.Override
            public void onSingleClick(android.view.View v) {
                android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.Settings.this, me.ccrama.redditslide.Activities.SettingsSubreddit.class);
                startActivityForResult(i, me.ccrama.redditslide.Activities.Settings.RESTART_SETTINGS_RESULT);
            }
        });
        findViewById(me.ccrama.redditslide.R.id.settings_child_filter).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
            @java.lang.Override
            public void onSingleClick(android.view.View v) {
                android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.Settings.this, me.ccrama.redditslide.Activities.SettingsFilter.class);
                startActivity(i);
            }
        });
        findViewById(me.ccrama.redditslide.R.id.settings_child_synccit).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
            @java.lang.Override
            public void onSingleClick(android.view.View v) {
                android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.Settings.this, me.ccrama.redditslide.Activities.SettingsSynccit.class);
                startActivity(i);
            }
        });
        findViewById(me.ccrama.redditslide.R.id.settings_child_reorder).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
            @java.lang.Override
            public void onSingleClick(android.view.View view) {
                android.content.Intent inte = new android.content.Intent(me.ccrama.redditslide.Activities.Settings.this, me.ccrama.redditslide.DragSort.ReorderSubreddits.class);
                me.ccrama.redditslide.Activities.Settings.this.startActivity(inte);
            }
        });
        findViewById(me.ccrama.redditslide.R.id.settings_child_maintheme).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
            @java.lang.Override
            public void onSingleClick(android.view.View v) {
                android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.Settings.this, me.ccrama.redditslide.Activities.SettingsTheme.class);
                startActivityForResult(i, me.ccrama.redditslide.Activities.Settings.RESTART_SETTINGS_RESULT);
            }
        });
        findViewById(me.ccrama.redditslide.R.id.settings_child_handling).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
            @java.lang.Override
            public void onSingleClick(android.view.View v) {
                android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.Settings.this, me.ccrama.redditslide.Activities.SettingsHandling.class);
                startActivity(i);
            }
        });
        findViewById(me.ccrama.redditslide.R.id.settings_child_layout).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
            @java.lang.Override
            public void onSingleClick(android.view.View v) {
                android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.Settings.this, me.ccrama.redditslide.Activities.EditCardsLayout.class);
                startActivity(i);
            }
        });
        findViewById(me.ccrama.redditslide.R.id.settings_child_backup).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
            @java.lang.Override
            public void onSingleClick(android.view.View v) {
                android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.Settings.this, me.ccrama.redditslide.Activities.SettingsBackup.class);
                startActivity(i);
            }
        });
        findViewById(me.ccrama.redditslide.R.id.settings_child_font).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
            @java.lang.Override
            public void onSingleClick(android.view.View v) {
                android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.Settings.this, me.ccrama.redditslide.Activities.SettingsFont.class);
                startActivity(i);
            }
        });
        findViewById(me.ccrama.redditslide.R.id.settings_child_tablet).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
            @java.lang.Override
            public void onSingleClick(android.view.View view) {
                /* Intent inte = new Intent(Overview.this, Overview.class);
                inte.putExtra("type", UpdateSubreddits.COLLECTIONS);
                Overview.this.startActivity(inte);
                 */
                if (me.ccrama.redditslide.SettingValues.isPro) {
                    android.view.LayoutInflater inflater = getLayoutInflater();
                    final android.view.View dialoglayout = inflater.inflate(me.ccrama.redditslide.R.layout.tabletui, null);
                    final com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.Settings.this);
                    final android.content.res.Resources res = getResources();
                    dialoglayout.findViewById(me.ccrama.redditslide.R.id.title).setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getDefaultColor());
                    // todo final Slider portrait = (Slider) dialoglayout.findViewById(R.id.portrait);
                    final android.widget.SeekBar landscape = dialoglayout.findViewById(me.ccrama.redditslide.R.id.landscape);
                    // todo  portrait.setBackgroundColor(Palette.getDefaultColor());
                    landscape.setProgress(me.ccrama.redditslide.Reddit.dpWidth - 1);
                    ((android.widget.TextView) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.progressnumber))).setText(res.getQuantityString(me.ccrama.redditslide.R.plurals.landscape_columns, landscape.getProgress() + 1, landscape.getProgress() + 1));
                    landscape.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
                        @java.lang.Override
                        public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                            ((android.widget.TextView) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.progressnumber))).setText(res.getQuantityString(me.ccrama.redditslide.R.plurals.landscape_columns, landscape.getProgress() + 1, landscape.getProgress() + 1));
                            me.ccrama.redditslide.Activities.Settings.changed = true;
                        }

                        @java.lang.Override
                        public void onStartTrackingTouch(android.widget.SeekBar seekBar) {
                        }

                        @java.lang.Override
                        public void onStopTrackingTouch(android.widget.SeekBar seekBar) {
                        }
                    });
                    final android.app.Dialog dialog = builder.setView(dialoglayout).create();
                    dialog.show();
                    dialog.setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {
                        @java.lang.Override
                        public void onDismiss(android.content.DialogInterface dialog) {
                            me.ccrama.redditslide.Reddit.dpWidth = landscape.getProgress() + 1;
                            me.ccrama.redditslide.Reddit.colors.edit().putInt("tabletOVERRIDE", landscape.getProgress() + 1).apply();
                        }
                    });
                    android.support.v7.widget.SwitchCompat s = dialog.findViewById(me.ccrama.redditslide.R.id.dualcolumns);
                    s.setChecked(me.ccrama.redditslide.SettingValues.dualPortrait);
                    s.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                        @java.lang.Override
                        public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                            me.ccrama.redditslide.SettingValues.dualPortrait = isChecked;
                            me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_DUAL_PORTRAIT, isChecked).apply();
                        }
                    });
                    android.support.v7.widget.SwitchCompat s2 = dialog.findViewById(me.ccrama.redditslide.R.id.fullcomment);
                    s2.setChecked(me.ccrama.redditslide.SettingValues.fullCommentOverride);
                    s2.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                        @java.lang.Override
                        public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                            me.ccrama.redditslide.SettingValues.fullCommentOverride = isChecked;
                            me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_FULL_COMMENT_OVERRIDE, isChecked).apply();
                        }
                    });
                    android.support.v7.widget.SwitchCompat s3 = dialog.findViewById(me.ccrama.redditslide.R.id.singlecolumnmultiwindow);
                    s3.setChecked(me.ccrama.redditslide.SettingValues.singleColumnMultiWindow);
                    s3.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                        @java.lang.Override
                        public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                            me.ccrama.redditslide.SettingValues.singleColumnMultiWindow = isChecked;
                            me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_SINGLE_COLUMN_MULTI, isChecked).apply();
                        }
                    });
                } else {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.Settings.this).setTitle("Mutli-Column Settings are a Pro feature").setMessage(me.ccrama.redditslide.R.string.pro_upgrade_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes_exclaim, new android.content.DialogInterface.OnClickListener() {
                        public void onClick(android.content.DialogInterface dialog, int whichButton) {
                            try {
                                startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=" + getString(me.ccrama.redditslide.R.string.ui_unlock_package))));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("http://play.google.com/store/apps/details?id=" + getString(me.ccrama.redditslide.R.string.ui_unlock_package))));
                            }
                        }
                    }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no_danks, new android.content.DialogInterface.OnClickListener() {
                        public void onClick(android.content.DialogInterface dialog, int whichButton) {
                        }
                    }).show();
                }
            }
        });
        if (me.ccrama.redditslide.FDroid.isFDroid) {
            ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.settings_child_donatetext))).setText("Donate via PayPal");
        }
        findViewById(me.ccrama.redditslide.R.id.settings_child_support).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
            @java.lang.Override
            public void onSingleClick(android.view.View v) {
                if (me.ccrama.redditslide.FDroid.isFDroid) {
                    android.content.Intent browserIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=56FKCCYLX7L72"));
                    startActivity(browserIntent);
                } else {
                    android.content.Intent inte = new android.content.Intent(me.ccrama.redditslide.Activities.Settings.this, me.ccrama.redditslide.Activities.DonateView.class);
                    me.ccrama.redditslide.Activities.Settings.this.startActivity(inte);
                }
            }
        });
        findViewById(me.ccrama.redditslide.R.id.settings_child_comments).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
            @java.lang.Override
            public void onSingleClick(android.view.View v) {
                android.content.Intent inte = new android.content.Intent(me.ccrama.redditslide.Activities.Settings.this, me.ccrama.redditslide.Activities.SettingsComments.class);
                me.ccrama.redditslide.Activities.Settings.this.startActivity(inte);
            }
        });
        if (me.ccrama.redditslide.Authentication.isLoggedIn && me.ccrama.redditslide.util.NetworkUtil.isConnected(this)) {
            findViewById(me.ccrama.redditslide.R.id.settings_child_reddit_settings).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View v) {
                    android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.Settings.this, me.ccrama.redditslide.Activities.SettingsReddit.class);
                    startActivity(i);
                }
            });
        } else {
            findViewById(me.ccrama.redditslide.R.id.settings_child_reddit_settings).setEnabled(false);
            findViewById(me.ccrama.redditslide.R.id.settings_child_reddit_settings).setAlpha(0.25F);
        }
        if (me.ccrama.redditslide.Authentication.mod) {
            findViewById(me.ccrama.redditslide.R.id.settings_child_moderation).setVisibility(android.view.View.VISIBLE);
            findViewById(me.ccrama.redditslide.R.id.settings_child_moderation).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.Settings.this, me.ccrama.redditslide.Activities.SettingsModeration.class);
                    startActivity(i);
                }
            });
        }
    }

    @java.lang.Override
    public void onFolderSelection(@android.support.annotation.NonNull
    me.ccrama.redditslide.Fragments.FolderChooserDialogCreate dialog, @android.support.annotation.NonNull
    java.io.File folder) {
        mSettingsGeneralFragment.onFolderSelection(dialog, folder);
    }

    @java.lang.Override
    public void onDestroy() {
        super.onDestroy();
        me.ccrama.redditslide.SettingValues.prefs.unregisterOnSharedPreferenceChangeListener(prefsListener);
    }
}