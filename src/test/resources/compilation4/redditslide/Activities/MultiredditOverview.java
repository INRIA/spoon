package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager;
import java.util.Locale;
import me.ccrama.redditslide.Views.PreCachingLayoutManager;
import me.ccrama.redditslide.Visuals.Palette;
import java.util.ArrayList;
import me.ccrama.redditslide.ColorPreferences;
import me.ccrama.redditslide.CaseInsensitiveArrayList;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.util.SortingUtil;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.UserSubscriptions;
import java.util.List;
import me.ccrama.redditslide.Fragments.MultiredditView;
/**
 * Created by ccrama on 9/17/2015.
 */
public class MultiredditOverview extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    public static final java.lang.String EXTRA_PROFILE = "profile";

    public static final java.lang.String EXTRA_MULTI = "multi";

    public static net.dean.jraw.models.MultiReddit searchMulti;

    public me.ccrama.redditslide.Activities.MultiredditOverview.OverviewPagerAdapter adapter;

    private android.support.v4.view.ViewPager pager;

    private java.lang.String profile;

    private android.support.design.widget.TabLayout tabs;

    private java.util.List<net.dean.jraw.models.MultiReddit> usedArray;

    private java.lang.String initialMulti;

    @java.lang.Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        android.view.MenuInflater inflater = getMenuInflater();
        inflater.inflate(me.ccrama.redditslide.R.menu.menu_multireddits, menu);
        if (!profile.isEmpty()) {
            menu.findItem(me.ccrama.redditslide.R.id.action_edit).setVisible(false);
            menu.findItem(me.ccrama.redditslide.R.id.create).setVisible(false);
        }
        // if (mShowInfoButton) menu.findItem(R.id.action_info).setVisible(true);
        // else menu.findItem(R.id.action_info).setVisible(false);
        return true;
    }

    @java.lang.Override
    public boolean dispatchKeyEvent(android.view.KeyEvent event) {
        /* removed for now
        int keyCode = event.getKeyCode();
        switch (keyCode) {
        case KeyEvent.KEYCODE_VOLUME_UP:
        return ((MultiredditView) adapter.getCurrentFragment()).onKeyDown(keyCode);
        case KeyEvent.KEYCODE_VOLUME_DOWN:
        return ((MultiredditView) adapter.getCurrentFragment()).onKeyDown(keyCode);
        default:
        return super.dispatchKeyEvent(event);
        }
         */
        return super.dispatchKeyEvent(event);
    }

    public int getCurrentPage() {
        int position = 0;
        int currentOrientation = getResources().getConfiguration().orientation;
        if ((((me.ccrama.redditslide.Fragments.MultiredditView) (adapter.getCurrentFragment())).rv.getLayoutManager() instanceof android.support.v7.widget.LinearLayoutManager) && (currentOrientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE)) {
            position = ((android.support.v7.widget.LinearLayoutManager) (((me.ccrama.redditslide.Fragments.MultiredditView) (adapter.getCurrentFragment())).rv.getLayoutManager())).findFirstVisibleItemPosition() - 1;
        } else if (((me.ccrama.redditslide.Fragments.MultiredditView) (adapter.getCurrentFragment())).rv.getLayoutManager() instanceof me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager) {
            int[] firstVisibleItems = null;
            firstVisibleItems = ((me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager) (((me.ccrama.redditslide.Fragments.MultiredditView) (adapter.getCurrentFragment())).rv.getLayoutManager())).findFirstVisibleItemPositions(firstVisibleItems);
            if ((firstVisibleItems != null) && (firstVisibleItems.length > 0)) {
                position = firstVisibleItems[0] - 1;
            }
        } else {
            position = ((me.ccrama.redditslide.Views.PreCachingLayoutManager) (((me.ccrama.redditslide.Fragments.MultiredditView) (adapter.getCurrentFragment())).rv.getLayoutManager())).findFirstVisibleItemPosition() - 1;
        }
        return position;
    }

    java.lang.String term;

    @java.lang.Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                try {
                    onBackPressed();
                } catch (java.lang.Exception ignored) {
                }
                return true;
            case me.ccrama.redditslide.R.id.action_edit :
                {
                    if ((profile.isEmpty() && (me.ccrama.redditslide.UserSubscriptions.multireddits != null)) && (!me.ccrama.redditslide.UserSubscriptions.multireddits.isEmpty())) {
                        android.content.Intent i = new android.content.Intent(this, me.ccrama.redditslide.Activities.CreateMulti.class);
                        i.putExtra(me.ccrama.redditslide.Activities.CreateMulti.EXTRA_MULTI, me.ccrama.redditslide.UserSubscriptions.multireddits.get(pager.getCurrentItem()).getDisplayName());
                        startActivity(i);
                    }
                }
                return true;
            case me.ccrama.redditslide.R.id.search :
                {
                    me.ccrama.redditslide.UserSubscriptions.MultiCallback m = new me.ccrama.redditslide.UserSubscriptions.MultiCallback() {
                        @java.lang.Override
                        public void onComplete(java.util.List<net.dean.jraw.models.MultiReddit> multireddits) {
                            if ((multireddits != null) && (!multireddits.isEmpty())) {
                                me.ccrama.redditslide.Activities.MultiredditOverview.searchMulti = multireddits.get(pager.getCurrentItem());
                                com.afollestad.materialdialogs.MaterialDialog.Builder builder = new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.MultiredditOverview.this).title(me.ccrama.redditslide.R.string.search_title).alwaysCallInputCallback().input(getString(me.ccrama.redditslide.R.string.search_msg), "", new com.afollestad.materialdialogs.MaterialDialog.InputCallback() {
                                    @java.lang.Override
                                    public void onInput(com.afollestad.materialdialogs.MaterialDialog materialDialog, java.lang.CharSequence charSequence) {
                                        term = charSequence.toString();
                                    }
                                });
                                // Add "search current sub" if it is not frontpage/all/random
                                builder.positiveText(getString(me.ccrama.redditslide.R.string.search_subreddit, "/m/" + me.ccrama.redditslide.Activities.MultiredditOverview.searchMulti.getDisplayName())).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                                    @java.lang.Override
                                    public void onClick(@android.support.annotation.NonNull
                                    com.afollestad.materialdialogs.MaterialDialog materialDialog, @android.support.annotation.NonNull
                                    com.afollestad.materialdialogs.DialogAction dialogAction) {
                                        android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.MultiredditOverview.this, me.ccrama.redditslide.Activities.Search.class);
                                        i.putExtra(me.ccrama.redditslide.Activities.Search.EXTRA_TERM, term);
                                        i.putExtra(me.ccrama.redditslide.Activities.Search.EXTRA_MULTIREDDIT, me.ccrama.redditslide.Activities.MultiredditOverview.searchMulti.getDisplayName());
                                        startActivity(i);
                                    }
                                });
                                builder.show();
                            }
                        }
                    };
                    if (profile.isEmpty()) {
                        me.ccrama.redditslide.UserSubscriptions.getMultireddits(m);
                    } else {
                        me.ccrama.redditslide.UserSubscriptions.getPublicMultireddits(m, profile);
                    }
                }
                return true;
            case me.ccrama.redditslide.R.id.create :
                if (profile.isEmpty()) {
                    android.content.Intent i2 = new android.content.Intent(this, me.ccrama.redditslide.Activities.CreateMulti.class);
                    startActivity(i2);
                }
                return true;
            case me.ccrama.redditslide.R.id.action_sort :
                openPopup();
                return true;
            case me.ccrama.redditslide.R.id.subs :
                ((android.support.v4.widget.DrawerLayout) (findViewById(me.ccrama.redditslide.R.id.drawer_layout))).openDrawer(android.view.Gravity.RIGHT);
                return true;
            case me.ccrama.redditslide.R.id.gallery :
                if (me.ccrama.redditslide.SettingValues.isPro) {
                    java.util.List<net.dean.jraw.models.Submission> posts = ((me.ccrama.redditslide.Fragments.MultiredditView) (adapter.getCurrentFragment())).posts.posts;
                    if ((posts != null) && (!posts.isEmpty())) {
                        android.content.Intent i2 = new android.content.Intent(this, me.ccrama.redditslide.Activities.Gallery.class);
                        i2.putExtra(me.ccrama.redditslide.Activities.Gallery.EXTRA_PROFILE, profile);
                        i2.putExtra(me.ccrama.redditslide.Activities.Gallery.EXTRA_MULTIREDDIT, ((me.ccrama.redditslide.Fragments.MultiredditView) (adapter.getCurrentFragment())).posts.multiReddit.getDisplayName());
                        startActivity(i2);
                    }
                } else {
                    com.afollestad.materialdialogs.AlertDialogWrapper.Builder b = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setTitle(me.ccrama.redditslide.R.string.general_gallerymode_ispro).setMessage(me.ccrama.redditslide.R.string.pro_upgrade_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes_exclaim, new android.content.DialogInterface.OnClickListener() {
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
                    });
                    if (me.ccrama.redditslide.SettingValues.previews > 0) {
                        b.setNeutralButton(getString(me.ccrama.redditslide.R.string.pro_previews, me.ccrama.redditslide.SettingValues.previews), new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                me.ccrama.redditslide.SettingValues.prefs.edit().putInt(me.ccrama.redditslide.SettingValues.PREVIEWS_LEFT, me.ccrama.redditslide.SettingValues.previews - 1).apply();
                                me.ccrama.redditslide.SettingValues.previews = me.ccrama.redditslide.SettingValues.prefs.getInt(me.ccrama.redditslide.SettingValues.PREVIEWS_LEFT, 10);
                                java.util.List<net.dean.jraw.models.Submission> posts = ((me.ccrama.redditslide.Fragments.MultiredditView) (adapter.getCurrentFragment())).posts.posts;
                                if ((posts != null) && (!posts.isEmpty())) {
                                    android.content.Intent i2 = new android.content.Intent(me.ccrama.redditslide.Activities.MultiredditOverview.this, me.ccrama.redditslide.Activities.Gallery.class);
                                    i2.putExtra(me.ccrama.redditslide.Activities.Gallery.EXTRA_PROFILE, profile);
                                    i2.putExtra(me.ccrama.redditslide.Activities.Gallery.EXTRA_MULTIREDDIT, ((me.ccrama.redditslide.Fragments.MultiredditView) (adapter.getCurrentFragment())).posts.multiReddit.getDisplayName());
                                    startActivity(i2);
                                }
                            }
                        });
                    }
                    b.show();
                }
                return true;
            case me.ccrama.redditslide.R.id.action_shadowbox :
                if (me.ccrama.redditslide.SettingValues.isPro) {
                    java.util.List<net.dean.jraw.models.Submission> posts = ((me.ccrama.redditslide.Fragments.MultiredditView) (adapter.getCurrentFragment())).posts.posts;
                    if ((posts != null) && (!posts.isEmpty())) {
                        android.content.Intent i = new android.content.Intent(this, me.ccrama.redditslide.Activities.Shadowbox.class);
                        i.putExtra(me.ccrama.redditslide.Activities.Shadowbox.EXTRA_PAGE, getCurrentPage());
                        i.putExtra(me.ccrama.redditslide.Activities.Shadowbox.EXTRA_PROFILE, profile);
                        i.putExtra(me.ccrama.redditslide.Activities.Shadowbox.EXTRA_MULTIREDDIT, ((me.ccrama.redditslide.Fragments.MultiredditView) (adapter.getCurrentFragment())).posts.multiReddit.getDisplayName());
                        startActivity(i);
                    }
                } else {
                    com.afollestad.materialdialogs.AlertDialogWrapper.Builder b = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setTitle(me.ccrama.redditslide.R.string.general_shadowbox_ispro).setMessage(me.ccrama.redditslide.R.string.pro_upgrade_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes_exclaim, new android.content.DialogInterface.OnClickListener() {
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
                    });
                    if (((((me.ccrama.redditslide.SettingValues.previews > 0) && (adapter != null)) && (((me.ccrama.redditslide.Fragments.MultiredditView) (adapter.getCurrentFragment())).posts != null)) && (((me.ccrama.redditslide.Fragments.MultiredditView) (adapter.getCurrentFragment())).posts.posts != null)) && (!((me.ccrama.redditslide.Fragments.MultiredditView) (adapter.getCurrentFragment())).posts.posts.isEmpty())) {
                        b.setNeutralButton(getString(me.ccrama.redditslide.R.string.pro_previews, me.ccrama.redditslide.SettingValues.previews), new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                me.ccrama.redditslide.SettingValues.prefs.edit().putInt(me.ccrama.redditslide.SettingValues.PREVIEWS_LEFT, me.ccrama.redditslide.SettingValues.previews - 1).apply();
                                me.ccrama.redditslide.SettingValues.previews = me.ccrama.redditslide.SettingValues.prefs.getInt(me.ccrama.redditslide.SettingValues.PREVIEWS_LEFT, 10);
                                java.util.List<net.dean.jraw.models.Submission> posts = ((me.ccrama.redditslide.Fragments.MultiredditView) (adapter.getCurrentFragment())).posts.posts;
                                if ((posts != null) && (!posts.isEmpty())) {
                                    android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.MultiredditOverview.this, me.ccrama.redditslide.Activities.Shadowbox.class);
                                    i.putExtra(me.ccrama.redditslide.Activities.Shadowbox.EXTRA_PAGE, getCurrentPage());
                                    i.putExtra(me.ccrama.redditslide.Activities.Shadowbox.EXTRA_PROFILE, profile);
                                    i.putExtra(me.ccrama.redditslide.Activities.Shadowbox.EXTRA_MULTIREDDIT, ((me.ccrama.redditslide.Fragments.MultiredditView) (adapter.getCurrentFragment())).posts.multiReddit.getDisplayName());
                                    startActivity(i);
                                }
                            }
                        });
                    }
                    b.show();
                }
                return true;
            default :
                return false;
        }
    }

    private void buildDialog() {
        buildDialog(false);
    }

    private void buildDialog(boolean wasException) {
        try {
            com.afollestad.materialdialogs.AlertDialogWrapper.Builder b = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setCancelable(false).setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {
                @java.lang.Override
                public void onDismiss(android.content.DialogInterface dialog) {
                    finish();
                }
            });
            if (wasException) {
                b.setTitle(me.ccrama.redditslide.R.string.err_title).setMessage(me.ccrama.redditslide.R.string.err_loading_content).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        finish();
                    }
                });
            } else if (profile.isEmpty()) {
                b.setTitle(me.ccrama.redditslide.R.string.multireddit_err_title).setMessage(me.ccrama.redditslide.R.string.multireddit_err_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.MultiredditOverview.this, me.ccrama.redditslide.Activities.CreateMulti.class);
                        startActivity(i);
                    }
                }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        finish();
                    }
                });
            } else {
                b.setTitle(me.ccrama.redditslide.R.string.public_multireddit_err_title).setMessage(me.ccrama.redditslide.R.string.public_multireddit_err_msg).setNegativeButton(me.ccrama.redditslide.R.string.btn_go_back, new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        finish();
                    }
                });
            }
            b.show();
        } catch (java.lang.Exception e) {
        }
    }

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstance) {
        overrideSwipeFromAnywhere();
        super.onCreate(savedInstance);
        applyColorTheme("");
        setContentView(me.ccrama.redditslide.R.layout.activity_multireddits);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.title_multireddits, true, false);
        findViewById(me.ccrama.redditslide.R.id.header).setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getDefaultColor());
        tabs = ((android.support.design.widget.TabLayout) (findViewById(me.ccrama.redditslide.R.id.sliding_tabs)));
        tabs.setTabMode(android.support.design.widget.TabLayout.MODE_SCROLLABLE);
        pager = ((android.support.v4.view.ViewPager) (findViewById(me.ccrama.redditslide.R.id.content_view)));
        mToolbar.setPopupTheme(new me.ccrama.redditslide.ColorPreferences(this).getFontStyle().getBaseId());
        profile = "";
        initialMulti = "";
        if (getIntent().getExtras() != null) {
            profile = getIntent().getExtras().getString(me.ccrama.redditslide.Activities.MultiredditOverview.EXTRA_PROFILE, "");
            initialMulti = getIntent().getExtras().getString(me.ccrama.redditslide.Activities.MultiredditOverview.EXTRA_MULTI, "");
        }
        if (profile.equalsIgnoreCase(me.ccrama.redditslide.Authentication.name)) {
            profile = "";
        }
        me.ccrama.redditslide.UserSubscriptions.MultiCallback callback = new me.ccrama.redditslide.UserSubscriptions.MultiCallback() {
            @java.lang.Override
            public void onComplete(java.util.List<net.dean.jraw.models.MultiReddit> multiReddits) {
                if ((multiReddits != null) && (!multiReddits.isEmpty())) {
                    setDataSet(multiReddits);
                } else {
                    buildDialog();
                }
            }
        };
        if (profile.isEmpty()) {
            me.ccrama.redditslide.UserSubscriptions.getMultireddits(callback);
        } else {
            me.ccrama.redditslide.UserSubscriptions.getPublicMultireddits(callback, profile);
        }
    }

    public void openPopup() {
        android.support.v7.widget.PopupMenu popup = new android.support.v7.widget.PopupMenu(this, findViewById(me.ccrama.redditslide.R.id.anchor), android.view.Gravity.RIGHT);
        java.lang.String id = ((me.ccrama.redditslide.Fragments.MultiredditView) (((me.ccrama.redditslide.Activities.MultiredditOverview.OverviewPagerAdapter) (pager.getAdapter())).getCurrentFragment())).posts.multiReddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH);
        final android.text.Spannable[] base = me.ccrama.redditslide.util.SortingUtil.getSortingSpannables("multi" + id);
        for (android.text.Spannable s : base) {
            // Do not add option for "Best" in any subreddit except for the frontpage.
            if (s.toString().equals(getString(me.ccrama.redditslide.R.string.sorting_best))) {
                continue;
            }
            android.view.MenuItem m = popup.getMenu().add(s);
        }
        popup.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(android.view.MenuItem item) {
                int i = 0;
                for (android.text.Spannable s : base) {
                    if (s.equals(item.getTitle())) {
                        break;
                    }
                    i++;
                }
                me.ccrama.redditslide.util.LogUtil.v("Chosen is " + i);
                if (pager.getAdapter() != null) {
                    switch (i) {
                        case 0 :
                            me.ccrama.redditslide.util.SortingUtil.setSorting("multi" + ((me.ccrama.redditslide.Fragments.MultiredditView) (((me.ccrama.redditslide.Activities.MultiredditOverview.OverviewPagerAdapter) (pager.getAdapter())).getCurrentFragment())).posts.multiReddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), net.dean.jraw.paginators.Sorting.HOT);
                            reloadSubs();
                            break;
                        case 1 :
                            me.ccrama.redditslide.util.SortingUtil.setSorting("multi" + ((me.ccrama.redditslide.Fragments.MultiredditView) (((me.ccrama.redditslide.Activities.MultiredditOverview.OverviewPagerAdapter) (pager.getAdapter())).getCurrentFragment())).posts.multiReddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), net.dean.jraw.paginators.Sorting.NEW);
                            reloadSubs();
                            break;
                        case 2 :
                            me.ccrama.redditslide.util.SortingUtil.setSorting("multi" + ((me.ccrama.redditslide.Fragments.MultiredditView) (((me.ccrama.redditslide.Activities.MultiredditOverview.OverviewPagerAdapter) (pager.getAdapter())).getCurrentFragment())).posts.multiReddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), net.dean.jraw.paginators.Sorting.RISING);
                            reloadSubs();
                            break;
                        case 3 :
                            me.ccrama.redditslide.util.SortingUtil.setSorting("multi" + ((me.ccrama.redditslide.Fragments.MultiredditView) (((me.ccrama.redditslide.Activities.MultiredditOverview.OverviewPagerAdapter) (pager.getAdapter())).getCurrentFragment())).posts.multiReddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), net.dean.jraw.paginators.Sorting.TOP);
                            openPopupTime();
                            break;
                        case 4 :
                            me.ccrama.redditslide.util.SortingUtil.setSorting("multi" + ((me.ccrama.redditslide.Fragments.MultiredditView) (((me.ccrama.redditslide.Activities.MultiredditOverview.OverviewPagerAdapter) (pager.getAdapter())).getCurrentFragment())).posts.multiReddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), net.dean.jraw.paginators.Sorting.CONTROVERSIAL);
                            openPopupTime();
                            break;
                    }
                }
                return true;
            }
        });
        popup.show();
    }

    public void openPopupTime() {
        android.support.v7.widget.PopupMenu popup = new android.support.v7.widget.PopupMenu(this, findViewById(me.ccrama.redditslide.R.id.anchor), android.view.Gravity.RIGHT);
        java.lang.String id = ((me.ccrama.redditslide.Fragments.MultiredditView) (((me.ccrama.redditslide.Activities.MultiredditOverview.OverviewPagerAdapter) (pager.getAdapter())).getCurrentFragment())).posts.multiReddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH);
        final android.text.Spannable[] base = me.ccrama.redditslide.util.SortingUtil.getSortingTimesSpannables("multi" + id);
        for (android.text.Spannable s : base) {
            android.view.MenuItem m = popup.getMenu().add(s);
        }
        popup.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(android.view.MenuItem item) {
                int i = 0;
                for (android.text.Spannable s : base) {
                    if (s.equals(item.getTitle())) {
                        break;
                    }
                    i++;
                }
                me.ccrama.redditslide.util.LogUtil.v("Chosen is " + i);
                if (pager.getAdapter() != null) {
                    switch (i) {
                        case 0 :
                            me.ccrama.redditslide.util.SortingUtil.setTime("multi" + ((me.ccrama.redditslide.Fragments.MultiredditView) (((me.ccrama.redditslide.Activities.MultiredditOverview.OverviewPagerAdapter) (pager.getAdapter())).getCurrentFragment())).posts.multiReddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), net.dean.jraw.paginators.TimePeriod.HOUR);
                            reloadSubs();
                            break;
                        case 1 :
                            me.ccrama.redditslide.util.SortingUtil.setTime("multi" + ((me.ccrama.redditslide.Fragments.MultiredditView) (((me.ccrama.redditslide.Activities.MultiredditOverview.OverviewPagerAdapter) (pager.getAdapter())).getCurrentFragment())).posts.multiReddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), net.dean.jraw.paginators.TimePeriod.DAY);
                            reloadSubs();
                            break;
                        case 2 :
                            me.ccrama.redditslide.util.SortingUtil.setTime("multi" + ((me.ccrama.redditslide.Fragments.MultiredditView) (((me.ccrama.redditslide.Activities.MultiredditOverview.OverviewPagerAdapter) (pager.getAdapter())).getCurrentFragment())).posts.multiReddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), net.dean.jraw.paginators.TimePeriod.WEEK);
                            reloadSubs();
                            break;
                        case 3 :
                            me.ccrama.redditslide.util.SortingUtil.setTime("multi" + ((me.ccrama.redditslide.Fragments.MultiredditView) (((me.ccrama.redditslide.Activities.MultiredditOverview.OverviewPagerAdapter) (pager.getAdapter())).getCurrentFragment())).posts.multiReddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), net.dean.jraw.paginators.TimePeriod.MONTH);
                            reloadSubs();
                            break;
                        case 4 :
                            me.ccrama.redditslide.util.SortingUtil.setTime("multi" + ((me.ccrama.redditslide.Fragments.MultiredditView) (((me.ccrama.redditslide.Activities.MultiredditOverview.OverviewPagerAdapter) (pager.getAdapter())).getCurrentFragment())).posts.multiReddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), net.dean.jraw.paginators.TimePeriod.YEAR);
                            reloadSubs();
                            break;
                        case 5 :
                            me.ccrama.redditslide.util.SortingUtil.setTime("multi" + ((me.ccrama.redditslide.Fragments.MultiredditView) (((me.ccrama.redditslide.Activities.MultiredditOverview.OverviewPagerAdapter) (pager.getAdapter())).getCurrentFragment())).posts.multiReddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), net.dean.jraw.paginators.TimePeriod.ALL);
                            reloadSubs();
                            break;
                    }
                }
                return true;
            }
        });
        popup.show();
    }

    private void reloadSubs() {
        int current = pager.getCurrentItem();
        adapter = new me.ccrama.redditslide.Activities.MultiredditOverview.OverviewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setCurrentItem(current);
    }

    private void setDataSet(java.util.List<net.dean.jraw.models.MultiReddit> data) {
        try {
            usedArray = data;
            if (usedArray.isEmpty()) {
                buildDialog();
            } else {
                if (adapter == null) {
                    adapter = new me.ccrama.redditslide.Activities.MultiredditOverview.OverviewPagerAdapter(getSupportFragmentManager());
                } else {
                    adapter.notifyDataSetChanged();
                }
                pager.setAdapter(adapter);
                pager.setOffscreenPageLimit(1);
                tabs.setupWithViewPager(pager);
                if (!initialMulti.isEmpty()) {
                    for (int i = 0; i < usedArray.size(); i++) {
                        if (usedArray.get(i).getDisplayName().equalsIgnoreCase(initialMulti)) {
                            pager.setCurrentItem(i);
                            break;
                        }
                    }
                }
                tabs.setSelectedTabIndicatorColor(new me.ccrama.redditslide.ColorPreferences(this).getColor(usedArray.get(0).getDisplayName()));
                doDrawerSubs(0);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    android.view.Window window = this.getWindow();
                    window.setStatusBarColor(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(usedArray.get(0).getDisplayName()));
                }
                final android.view.View header = findViewById(me.ccrama.redditslide.R.id.header);
                tabs.setOnTabSelectedListener(new android.support.design.widget.TabLayout.ViewPagerOnTabSelectedListener(pager) {
                    @java.lang.Override
                    public void onTabReselected(android.support.design.widget.TabLayout.Tab tab) {
                        super.onTabReselected(tab);
                        int[] firstVisibleItems;
                        int pastVisiblesItems = 0;
                        firstVisibleItems = ((me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager) (((me.ccrama.redditslide.Fragments.MultiredditView) (adapter.getCurrentFragment())).rv.getLayoutManager())).findFirstVisibleItemPositions(null);
                        if ((firstVisibleItems != null) && (firstVisibleItems.length > 0)) {
                            for (int firstVisibleItem : firstVisibleItems) {
                                pastVisiblesItems = firstVisibleItem;
                            }
                        }
                        if (pastVisiblesItems > 8) {
                            ((me.ccrama.redditslide.Fragments.MultiredditView) (adapter.getCurrentFragment())).rv.scrollToPosition(0);
                            if (header != null) {
                                header.animate().translationY(header.getHeight()).setInterpolator(new android.view.animation.LinearInterpolator()).setDuration(0);
                            }
                        } else {
                            ((me.ccrama.redditslide.Fragments.MultiredditView) (adapter.getCurrentFragment())).rv.smoothScrollToPosition(0);
                        }
                    }
                });
                findViewById(me.ccrama.redditslide.R.id.header).setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getColor(usedArray.get(0).getDisplayName()));
            }
        } catch (java.lang.NullPointerException e) {
            buildDialog(true);
            android.util.Log.e(me.ccrama.redditslide.util.LogUtil.getTag(), "Cannot load multis:\n" + e);
        }
    }

    public void doDrawerSubs(int position) {
        net.dean.jraw.models.MultiReddit current = usedArray.get(position);
        android.widget.LinearLayout l = ((android.widget.LinearLayout) (findViewById(me.ccrama.redditslide.R.id.sidebar_scroll)));
        l.removeAllViews();
        me.ccrama.redditslide.CaseInsensitiveArrayList toSort = new me.ccrama.redditslide.CaseInsensitiveArrayList();
        for (net.dean.jraw.models.MultiSubreddit s : current.getSubreddits()) {
            toSort.add(s.getDisplayName().toLowerCase(java.util.Locale.ENGLISH));
        }
        for (java.lang.String sub : me.ccrama.redditslide.UserSubscriptions.sortNoExtras(toSort)) {
            final android.view.View convertView = getLayoutInflater().inflate(me.ccrama.redditslide.R.layout.subforsublist, l, false);
            final java.lang.String subreddit = sub;
            final android.widget.TextView t = convertView.findViewById(me.ccrama.redditslide.R.id.name);
            t.setText(subreddit);
            convertView.findViewById(me.ccrama.redditslide.R.id.color).setBackgroundResource(me.ccrama.redditslide.R.drawable.circle);
            convertView.findViewById(me.ccrama.redditslide.R.id.color).getBackground().setColorFilter(me.ccrama.redditslide.Visuals.Palette.getColor(subreddit), android.graphics.PorterDuff.Mode.MULTIPLY);
            convertView.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View view) {
                    android.content.Intent inte = new android.content.Intent(me.ccrama.redditslide.Activities.MultiredditOverview.this, me.ccrama.redditslide.Activities.SubredditView.class);
                    inte.putExtra(me.ccrama.redditslide.Activities.SubredditView.EXTRA_SUBREDDIT, subreddit);
                    me.ccrama.redditslide.Activities.MultiredditOverview.this.startActivityForResult(inte, 4);
                }
            });
            l.addView(convertView);
        }
    }

    public class OverviewPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
        public OverviewPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
            pager.addOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {
                @java.lang.Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @java.lang.Override
                public void onPageSelected(int position) {
                    findViewById(me.ccrama.redditslide.R.id.header).animate().translationY(0).setInterpolator(new android.view.animation.LinearInterpolator()).setDuration(180);
                    findViewById(me.ccrama.redditslide.R.id.header).setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getColor(usedArray.get(position).getDisplayName()));
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        android.view.Window window = getWindow();
                        window.setStatusBarColor(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(usedArray.get(position).getDisplayName()));
                    }
                    tabs.setSelectedTabIndicatorColor(new me.ccrama.redditslide.ColorPreferences(me.ccrama.redditslide.Activities.MultiredditOverview.this).getColor(usedArray.get(position).getDisplayName()));
                    doDrawerSubs(position);
                }

                @java.lang.Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }

        @java.lang.Override
        public android.support.v4.app.Fragment getItem(int i) {
            android.support.v4.app.Fragment f = new me.ccrama.redditslide.Fragments.MultiredditView();
            android.os.Bundle args = new android.os.Bundle();
            args.putInt("id", i);
            args.putString(me.ccrama.redditslide.Activities.MultiredditOverview.EXTRA_PROFILE, profile);
            f.setArguments(args);
            return f;
        }

        private android.support.v4.app.Fragment mCurrentFragment;

        public android.support.v4.app.Fragment getCurrentFragment() {
            return mCurrentFragment;
        }

        @java.lang.Override
        public void setPrimaryItem(android.view.ViewGroup container, int position, java.lang.Object object) {
            if (getCurrentFragment() != object) {
                mCurrentFragment = ((android.support.v4.app.Fragment) (object));
            }
            super.setPrimaryItem(container, position, object);
        }

        @java.lang.Override
        public int getCount() {
            if (usedArray == null) {
                return 1;
            } else {
                return usedArray.size();
            }
        }

        @java.lang.Override
        public java.lang.CharSequence getPageTitle(int position) {
            return usedArray.get(position).getFullName();
        }
    }

    @java.lang.Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        if (((requestCode == 940) && (adapter != null)) && (adapter.getCurrentFragment() != null)) {
            if (resultCode == android.app.Activity.RESULT_OK) {
                me.ccrama.redditslide.util.LogUtil.v("Doing hide posts");
                java.util.ArrayList<java.lang.Integer> posts = data.getIntegerArrayListExtra("seen");
                ((me.ccrama.redditslide.Fragments.MultiredditView) (adapter.getCurrentFragment())).adapter.refreshView(posts);
                if ((data.hasExtra("lastPage") && (data.getIntExtra("lastPage", 0) != 0)) && (((me.ccrama.redditslide.Fragments.MultiredditView) (adapter.getCurrentFragment())).rv.getLayoutManager() instanceof android.support.v7.widget.LinearLayoutManager)) {
                    ((android.support.v7.widget.LinearLayoutManager) (((me.ccrama.redditslide.Fragments.MultiredditView) (adapter.getCurrentFragment())).rv.getLayoutManager())).scrollToPositionWithOffset(data.getIntExtra("lastPage", 0) + 1, mToolbar.getHeight());
                }
            } else {
                ((me.ccrama.redditslide.Fragments.MultiredditView) (adapter.getCurrentFragment())).adapter.refreshView();
            }
        }
    }
}