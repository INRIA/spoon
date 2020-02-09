package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager;
import java.util.Locale;
import me.ccrama.redditslide.Views.PreCachingLayoutManager;
import java.util.HashMap;
import me.ccrama.redditslide.util.OnSingleClickListener;
import me.ccrama.redditslide.Visuals.Palette;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.JsonNode;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.SpoilerRobotoTextView;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Views.CommentOverflow;
import me.ccrama.redditslide.UserSubscriptions;
import java.util.List;
import me.ccrama.redditslide.ImageFlairs;
import me.ccrama.redditslide.Fragments.BlankFragment;
import me.ccrama.redditslide.Fragments.CommentPage;
import me.ccrama.redditslide.ColorPreferences;
import me.ccrama.redditslide.Notifications.CheckForMail;
import me.ccrama.redditslide.util.SubmissionParser;
import me.ccrama.redditslide.PostMatch;
import me.ccrama.redditslide.Adapters.SettingsSubAdapter;
import me.ccrama.redditslide.OfflineSubreddit;
import me.ccrama.redditslide.Views.ToggleSwipeViewPager;
import me.ccrama.redditslide.util.SortingUtil;
import me.ccrama.redditslide.Views.SidebarLayout;
import me.ccrama.redditslide.Constants;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.Fragments.SubmissionsView;
public class SubredditView extends me.ccrama.redditslide.Activities.BaseActivity {
    public static final java.lang.String EXTRA_SUBREDDIT = "subreddit";

    public boolean canSubmit = true;

    public java.lang.String subreddit;

    public net.dean.jraw.models.Submission openingComments;

    public int currentComment;

    public me.ccrama.redditslide.Activities.SubredditView.OverviewPagerAdapter adapter;

    public java.lang.String term;

    public me.ccrama.redditslide.Views.ToggleSwipeViewPager pager;

    public boolean singleMode;

    public boolean commentPager;

    public boolean loaded;

    android.view.View header;

    net.dean.jraw.models.Subreddit sub;

    private android.support.v4.widget.DrawerLayout drawerLayout;

    private boolean currentlySubbed = false;

    @java.lang.Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        // Check which request we're responding to
        if (requestCode == 2) {
            // Make sure the request was successful
            pager.setAdapter(new me.ccrama.redditslide.Activities.SubredditView.OverviewPagerAdapter(getSupportFragmentManager()));
        } else if (requestCode == 1) {
            restartTheme();
        } else if (requestCode == 940) {
            if ((adapter != null) && (adapter.getCurrentFragment() != null)) {
                if (resultCode == android.app.Activity.RESULT_OK) {
                    me.ccrama.redditslide.util.LogUtil.v("Doing hide posts");
                    java.util.ArrayList<java.lang.Integer> posts = data.getIntegerArrayListExtra("seen");
                    ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).adapter.refreshView(posts);
                    if ((data.hasExtra("lastPage") && (data.getIntExtra("lastPage", 0) != 0)) && (((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).rv.getLayoutManager() instanceof android.support.v7.widget.LinearLayoutManager)) {
                        ((android.support.v7.widget.LinearLayoutManager) (((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).rv.getLayoutManager())).scrollToPositionWithOffset(data.getIntExtra("lastPage", 0) + 1, mToolbar.getHeight());
                    }
                } else {
                    ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).adapter.refreshView();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @java.lang.Override
    public void onBackPressed() {
        if (((drawerLayout != null) && drawerLayout.isDrawerOpen(android.support.v4.view.GravityCompat.START)) || ((drawerLayout != null) && drawerLayout.isDrawerOpen(android.support.v4.view.GravityCompat.END))) {
            drawerLayout.closeDrawers();
        } else if (commentPager && (pager.getCurrentItem() == 2)) {
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        } else {
            super.onBackPressed();
        }
    }

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        overrideSwipeFromAnywhere();
        if (me.ccrama.redditslide.SettingValues.commentPager && me.ccrama.redditslide.SettingValues.single) {
            disableSwipeBackLayout();
        }
        getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().getDecorView().setBackgroundDrawable(null);
        super.onCreate(savedInstanceState);
        if (!me.ccrama.redditslide.Activities.SubredditView.restarting) {
            overridePendingTransition(me.ccrama.redditslide.R.anim.slideright, 0);
        } else {
            me.ccrama.redditslide.Activities.SubredditView.restarting = false;
        }
        subreddit = getIntent().getExtras().getString(me.ccrama.redditslide.Activities.SubredditView.EXTRA_SUBREDDIT, "");
        applyColorTheme(subreddit);
        setContentView(me.ccrama.redditslide.R.layout.activity_singlesubreddit);
        setupSubredditAppBar(me.ccrama.redditslide.R.id.toolbar, subreddit, true, subreddit);
        header = findViewById(me.ccrama.redditslide.R.id.header);
        drawerLayout = ((android.support.v4.widget.DrawerLayout) (findViewById(me.ccrama.redditslide.R.id.drawer_layout)));
        setResult(3);
        mToolbar.setPopupTheme(new me.ccrama.redditslide.ColorPreferences(this).getFontStyle().getBaseId());
        pager = ((me.ccrama.redditslide.Views.ToggleSwipeViewPager) (findViewById(me.ccrama.redditslide.R.id.content_view)));
        singleMode = me.ccrama.redditslide.SettingValues.single;
        commentPager = false;
        if (singleMode)
            commentPager = me.ccrama.redditslide.SettingValues.commentPager;

        if (commentPager) {
            adapter = new me.ccrama.redditslide.Activities.SubredditView.OverviewPagerAdapterComment(getSupportFragmentManager());
            pager.setSwipeLeftOnly(false);
            pager.setSwipingEnabled(true);
        } else {
            adapter = new me.ccrama.redditslide.Activities.SubredditView.OverviewPagerAdapter(getSupportFragmentManager());
        }
        pager.setAdapter(adapter);
        pager.setCurrentItem(1);
        mToolbar.setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                int[] firstVisibleItems;
                int pastVisiblesItems = 0;
                firstVisibleItems = ((me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager) (((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).rv.getLayoutManager())).findFirstVisibleItemPositions(null);
                if ((firstVisibleItems != null) && (firstVisibleItems.length > 0)) {
                    for (int firstVisibleItem : firstVisibleItems) {
                        pastVisiblesItems = firstVisibleItem;
                    }
                }
                if (pastVisiblesItems > 8) {
                    ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).rv.scrollToPosition(0);
                    header.animate().translationY(header.getHeight()).setInterpolator(new android.view.animation.LinearInterpolator()).setDuration(180);
                } else {
                    ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).rv.smoothScrollToPosition(0);
                }
                ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).resetScroll();
            }
        });
        if (((((((((!subreddit.equals("random")) && (!subreddit.equals("all"))) && (!subreddit.equals("frontpage"))) && (!subreddit.equals("friends"))) && (!subreddit.equals("mod"))) && (!subreddit.equals("myrandom"))) && (!subreddit.equals("randnsfw"))) && (!subreddit.equals("popular"))) && (!subreddit.contains("+"))) {
            executeAsyncSubreddit(subreddit);
        } else {
            drawerLayout.setDrawerLockMode(android.support.v4.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED, android.support.v4.view.GravityCompat.END);
        }
    }

    @java.lang.Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        android.view.MenuInflater inflater = getMenuInflater();
        if (me.ccrama.redditslide.SettingValues.expandedToolbar) {
            inflater.inflate(me.ccrama.redditslide.R.menu.menu_single_subreddit_expanded, menu);
        } else {
            inflater.inflate(me.ccrama.redditslide.R.menu.menu_single_subreddit, menu);
        }
        if (me.ccrama.redditslide.SettingValues.fab && (me.ccrama.redditslide.SettingValues.fabType == me.ccrama.redditslide.Constants.FAB_DISMISS)) {
            menu.findItem(me.ccrama.redditslide.R.id.hide_posts).setVisible(false);
        }
        return true;
    }

    @java.lang.Override
    public boolean onPrepareOptionsMenu(android.view.Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // Hide the "Submit" menu item if the currently viewed sub is the frontpage or /r/all.
        if ((((subreddit.equals("frontpage") || subreddit.equals("all")) || subreddit.equals("popular")) || subreddit.equals("friends")) || subreddit.equals("mod")) {
            menu.findItem(me.ccrama.redditslide.R.id.submit).setVisible(false);
            menu.findItem(me.ccrama.redditslide.R.id.sidebar).setVisible(false);
        }
        mToolbar.getMenu().findItem(me.ccrama.redditslide.R.id.theme).setOnMenuItemClickListener(new android.view.MenuItem.OnMenuItemClickListener() {
            @java.lang.Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
                int style = new me.ccrama.redditslide.ColorPreferences(me.ccrama.redditslide.Activities.SubredditView.this).getThemeSubreddit(subreddit);
                final android.content.Context contextThemeWrapper = new android.support.v7.view.ContextThemeWrapper(me.ccrama.redditslide.Activities.SubredditView.this, style);
                android.view.LayoutInflater localInflater = getLayoutInflater().cloneInContext(contextThemeWrapper);
                final android.view.View dialoglayout = localInflater.inflate(me.ccrama.redditslide.R.layout.colorsub, null);
                java.util.ArrayList<java.lang.String> arrayList = new java.util.ArrayList<>();
                arrayList.add(subreddit);
                me.ccrama.redditslide.Adapters.SettingsSubAdapter.showSubThemeEditor(arrayList, me.ccrama.redditslide.Activities.SubredditView.this, dialoglayout);
                return false;
            }
        });
        return true;
    }

    @java.lang.Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                onBackPressed();
                return true;
            case me.ccrama.redditslide.R.id.filter :
                filterContent(subreddit);
                return true;
            case me.ccrama.redditslide.R.id.submit :
                android.content.Intent i = new android.content.Intent(this, me.ccrama.redditslide.Activities.Submit.class);
                if (canSubmit)
                    i.putExtra(me.ccrama.redditslide.Activities.Submit.EXTRA_SUBREDDIT, subreddit);

                startActivity(i);
                return true;
            case me.ccrama.redditslide.R.id.action_refresh :
                if ((adapter != null) && (adapter.getCurrentFragment() != null)) {
                    ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).forceRefresh();
                }
                return true;
            case me.ccrama.redditslide.R.id.action_sort :
                if (subreddit.equalsIgnoreCase("friends")) {
                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(findViewById(me.ccrama.redditslide.R.id.anchor), getString(me.ccrama.redditslide.R.string.friends_sort_error), android.support.design.widget.Snackbar.LENGTH_SHORT);
                    android.view.View view = s.getView();
                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(android.graphics.Color.WHITE);
                    s.show();
                } else {
                    openPopup();
                }
                return true;
            case me.ccrama.redditslide.R.id.gallery :
                if (me.ccrama.redditslide.SettingValues.isPro) {
                    java.util.List<net.dean.jraw.models.Submission> posts = ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).posts.posts;
                    if ((posts != null) && (!posts.isEmpty())) {
                        android.content.Intent i2 = new android.content.Intent(this, me.ccrama.redditslide.Activities.Gallery.class);
                        i2.putExtra("offline", ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).posts.cached != null ? ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).posts.cached.time : 0L);
                        i2.putExtra(me.ccrama.redditslide.Activities.Gallery.EXTRA_SUBREDDIT, ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).posts.subreddit);
                        startActivity(i2);
                    }
                } else {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setTitle(me.ccrama.redditslide.R.string.general_gallerymode_ispro).setMessage(me.ccrama.redditslide.R.string.pro_upgrade_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes_exclaim, new android.content.DialogInterface.OnClickListener() {
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
                return true;
            case me.ccrama.redditslide.R.id.search :
                com.afollestad.materialdialogs.MaterialDialog.Builder builder = new com.afollestad.materialdialogs.MaterialDialog.Builder(this).title(me.ccrama.redditslide.R.string.search_title).alwaysCallInputCallback().input(getString(me.ccrama.redditslide.R.string.search_msg), "", new com.afollestad.materialdialogs.MaterialDialog.InputCallback() {
                    @java.lang.Override
                    public void onInput(com.afollestad.materialdialogs.MaterialDialog materialDialog, java.lang.CharSequence charSequence) {
                        term = charSequence.toString();
                    }
                }).neutralText(me.ccrama.redditslide.R.string.search_all).onNeutral(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                    @java.lang.Override
                    public void onClick(@android.support.annotation.NonNull
                    com.afollestad.materialdialogs.MaterialDialog materialDialog, @android.support.annotation.NonNull
                    com.afollestad.materialdialogs.DialogAction dialogAction) {
                        android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.SubredditView.this, me.ccrama.redditslide.Activities.Search.class);
                        i.putExtra(me.ccrama.redditslide.Activities.Search.EXTRA_TERM, term);
                        startActivity(i);
                    }
                });
                // Add "search current sub" if it is not frontpage/all/random
                if ((((((((!subreddit.equalsIgnoreCase("frontpage")) && (!subreddit.equalsIgnoreCase("all"))) && (!subreddit.equalsIgnoreCase("random"))) && (!subreddit.equalsIgnoreCase("popular"))) && (!subreddit.equals("myrandom"))) && (!subreddit.equals("randnsfw"))) && (!subreddit.equalsIgnoreCase("friends"))) && (!subreddit.equalsIgnoreCase("mod"))) {
                    builder.positiveText(getString(me.ccrama.redditslide.R.string.search_subreddit, subreddit)).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                        @java.lang.Override
                        public void onClick(@android.support.annotation.NonNull
                        com.afollestad.materialdialogs.MaterialDialog materialDialog, @android.support.annotation.NonNull
                        com.afollestad.materialdialogs.DialogAction dialogAction) {
                            android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.SubredditView.this, me.ccrama.redditslide.Activities.Search.class);
                            i.putExtra(me.ccrama.redditslide.Activities.Search.EXTRA_TERM, term);
                            i.putExtra(me.ccrama.redditslide.Activities.Search.EXTRA_SUBREDDIT, subreddit);
                            android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), (("INTENT SHOWS " + term) + " AND ") + subreddit);
                            startActivity(i);
                        }
                    });
                }
                builder.show();
                return true;
            case me.ccrama.redditslide.R.id.sidebar :
                drawerLayout.openDrawer(android.view.Gravity.RIGHT);
                return true;
            case me.ccrama.redditslide.R.id.hide_posts :
                ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).clearSeenPosts(false);
                return true;
            case me.ccrama.redditslide.R.id.action_shadowbox :
                if (me.ccrama.redditslide.SettingValues.isPro) {
                    java.util.List<net.dean.jraw.models.Submission> posts = ((me.ccrama.redditslide.Fragments.SubmissionsView) (((me.ccrama.redditslide.Activities.SubredditView.OverviewPagerAdapter) (pager.getAdapter())).getCurrentFragment())).posts.posts;
                    if ((posts != null) && (!posts.isEmpty())) {
                        android.content.Intent i2 = new android.content.Intent(this, me.ccrama.redditslide.Activities.Shadowbox.class);
                        i2.putExtra(me.ccrama.redditslide.Activities.Shadowbox.EXTRA_PAGE, getCurrentPage());
                        i2.putExtra(me.ccrama.redditslide.Activities.Shadowbox.EXTRA_SUBREDDIT, ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).posts.subreddit);
                        startActivity(i2);
                    }
                } else {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setTitle(me.ccrama.redditslide.R.string.general_shadowbox_ispro).setMessage(me.ccrama.redditslide.R.string.pro_upgrade_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes_exclaim, new android.content.DialogInterface.OnClickListener() {
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
                return true;
            default :
                return false;
        }
    }

    @java.lang.Override
    public void onDestroy() {
        super.onDestroy();
        if (sub != null) {
            if (sub.isNsfw() && ((!me.ccrama.redditslide.SettingValues.storeHistory) || (!me.ccrama.redditslide.SettingValues.storeNSFWHistory))) {
                android.content.SharedPreferences.Editor e = me.ccrama.redditslide.Reddit.cachedData.edit();
                for (java.lang.String s : me.ccrama.redditslide.OfflineSubreddit.getAll(sub.getDisplayName())) {
                    e.remove(s);
                }
                e.apply();
            } else if (!me.ccrama.redditslide.SettingValues.storeHistory) {
                android.content.SharedPreferences.Editor e = me.ccrama.redditslide.Reddit.cachedData.edit();
                for (java.lang.String s : me.ccrama.redditslide.OfflineSubreddit.getAll(sub.getDisplayName())) {
                    e.remove(s);
                }
                e.apply();
            }
        }
    }

    public int adjustAlpha(float factor) {
        int alpha = java.lang.Math.round(android.graphics.Color.alpha(android.graphics.Color.BLACK) * factor);
        int red = android.graphics.Color.red(android.graphics.Color.BLACK);
        int green = android.graphics.Color.green(android.graphics.Color.BLACK);
        int blue = android.graphics.Color.blue(android.graphics.Color.BLACK);
        return android.graphics.Color.argb(alpha, red, green, blue);
    }

    public void doPageSelectedComments(int position) {
        header.animate().translationY(0).setInterpolator(new android.view.animation.LinearInterpolator()).setDuration(180);
        pager.setSwipeLeftOnly(false);
        me.ccrama.redditslide.Reddit.currentPosition = position;
        if (((position == 1) && (adapter != null)) && (adapter.getCurrentFragment() != null)) {
            ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).adapter.refreshView();
        }
    }

    public void doSubSidebar(final java.lang.String subOverride) {
        findViewById(me.ccrama.redditslide.R.id.loader).setVisibility(android.view.View.VISIBLE);
        invalidateOptionsMenu();
        if (((((((((((!subOverride.equalsIgnoreCase("all")) && (!subOverride.equalsIgnoreCase("frontpage"))) && (!subOverride.equalsIgnoreCase("random"))) && (!subOverride.equalsIgnoreCase("popular"))) && (!subOverride.equalsIgnoreCase("myrandom"))) && (!subOverride.equalsIgnoreCase("randnsfw"))) && (!subOverride.equalsIgnoreCase("friends"))) && (!subOverride.equalsIgnoreCase("mod"))) && (!subOverride.contains("+"))) && (!subOverride.contains("."))) && (!subOverride.contains("/m/"))) {
            if (drawerLayout != null) {
                drawerLayout.setDrawerLockMode(android.support.v4.widget.DrawerLayout.LOCK_MODE_UNLOCKED, android.support.v4.view.GravityCompat.END);
            }
            loaded = true;
            final android.view.View dialoglayout = findViewById(me.ccrama.redditslide.R.id.sidebarsub);
            {
                android.view.View submit = dialoglayout.findViewById(me.ccrama.redditslide.R.id.submit);
                if ((!me.ccrama.redditslide.Authentication.isLoggedIn) || (!me.ccrama.redditslide.Authentication.didOnline)) {
                    submit.setVisibility(android.view.View.GONE);
                }
                if (me.ccrama.redditslide.SettingValues.fab && (me.ccrama.redditslide.SettingValues.fabType == me.ccrama.redditslide.Constants.FAB_POST)) {
                    submit.setVisibility(android.view.View.GONE);
                }
                submit.setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                    @java.lang.Override
                    public void onSingleClick(android.view.View view) {
                        android.content.Intent inte = new android.content.Intent(me.ccrama.redditslide.Activities.SubredditView.this, me.ccrama.redditslide.Activities.Submit.class);
                        if ((!subOverride.contains("/m/")) && canSubmit) {
                            inte.putExtra(me.ccrama.redditslide.Activities.Submit.EXTRA_SUBREDDIT, subOverride);
                        }
                        me.ccrama.redditslide.Activities.SubredditView.this.startActivity(inte);
                    }
                });
            }
            dialoglayout.findViewById(me.ccrama.redditslide.R.id.wiki).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.SubredditView.this, me.ccrama.redditslide.Activities.Wiki.class);
                    i.putExtra(me.ccrama.redditslide.Activities.Wiki.EXTRA_SUBREDDIT, subOverride);
                    startActivity(i);
                }
            });
            dialoglayout.findViewById(me.ccrama.redditslide.R.id.syncflair).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    me.ccrama.redditslide.ImageFlairs.syncFlairs(me.ccrama.redditslide.Activities.SubredditView.this, subreddit);
                }
            });
            dialoglayout.findViewById(me.ccrama.redditslide.R.id.submit).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.SubredditView.this, me.ccrama.redditslide.Activities.Submit.class);
                    if (((!subOverride.contains("/m/")) || (!subOverride.contains("."))) && canSubmit) {
                        i.putExtra(me.ccrama.redditslide.Activities.Submit.EXTRA_SUBREDDIT, subOverride);
                    }
                    startActivity(i);
                }
            });
            final android.widget.TextView sort = dialoglayout.findViewById(me.ccrama.redditslide.R.id.sort);
            net.dean.jraw.paginators.Sorting sortingis = net.dean.jraw.paginators.Sorting.HOT;
            if (me.ccrama.redditslide.SettingValues.hasSort(subreddit)) {
                sortingis = me.ccrama.redditslide.SettingValues.getBaseSubmissionSort(subreddit);
                sort.setText(sortingis.name() + ((sortingis == net.dean.jraw.paginators.Sorting.CONTROVERSIAL) || (sortingis == net.dean.jraw.paginators.Sorting.TOP) ? " of " + me.ccrama.redditslide.SettingValues.getBaseTimePeriod(subreddit).name() : ""));
            } else {
                sort.setText("Set default sorting");
            }
            final int sortid = me.ccrama.redditslide.util.SortingUtil.getSortingId(sortingis);
            dialoglayout.findViewById(me.ccrama.redditslide.R.id.sorting).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    final android.content.DialogInterface.OnClickListener l2 = new android.content.DialogInterface.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.content.DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case 0 :
                                    sorts = net.dean.jraw.paginators.Sorting.HOT;
                                    break;
                                case 1 :
                                    sorts = net.dean.jraw.paginators.Sorting.NEW;
                                    break;
                                case 2 :
                                    sorts = net.dean.jraw.paginators.Sorting.RISING;
                                    break;
                                case 3 :
                                    sorts = net.dean.jraw.paginators.Sorting.TOP;
                                    askTimePeriod(sorts, subreddit, dialoglayout);
                                    return;
                                case 4 :
                                    sorts = net.dean.jraw.paginators.Sorting.CONTROVERSIAL;
                                    askTimePeriod(sorts, subreddit, dialoglayout);
                                    return;
                            }
                            me.ccrama.redditslide.SettingValues.setSubSorting(sorts, time, subreddit);
                            net.dean.jraw.paginators.Sorting sortingis = me.ccrama.redditslide.SettingValues.getBaseSubmissionSort(subreddit);
                            sort.setText(sortingis.name() + ((sortingis == net.dean.jraw.paginators.Sorting.CONTROVERSIAL) || (sortingis == net.dean.jraw.paginators.Sorting.TOP) ? " of " + me.ccrama.redditslide.SettingValues.getBaseTimePeriod(subreddit).name() : ""));
                            reloadSubs();
                        }
                    };
                    com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.SubredditView.this);
                    builder.setTitle(me.ccrama.redditslide.R.string.sorting_choose);
                    builder.setSingleChoiceItems(me.ccrama.redditslide.util.SortingUtil.getSortingStrings(), sortid, l2);
                    builder.setNegativeButton("Reset default sorting", new android.content.DialogInterface.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            me.ccrama.redditslide.SettingValues.prefs.edit().remove("defaultSort" + subreddit.toLowerCase(java.util.Locale.ENGLISH)).apply();
                            me.ccrama.redditslide.SettingValues.prefs.edit().remove("defaultTime" + subreddit.toLowerCase(java.util.Locale.ENGLISH)).apply();
                            final android.widget.TextView sort = dialoglayout.findViewById(me.ccrama.redditslide.R.id.sort);
                            if (me.ccrama.redditslide.SettingValues.hasSort(subreddit)) {
                                net.dean.jraw.paginators.Sorting sortingis = me.ccrama.redditslide.SettingValues.getBaseSubmissionSort(subreddit);
                                sort.setText(sortingis.name() + ((sortingis == net.dean.jraw.paginators.Sorting.CONTROVERSIAL) || (sortingis == net.dean.jraw.paginators.Sorting.TOP) ? " of " + me.ccrama.redditslide.SettingValues.getBaseTimePeriod(subreddit).name() : ""));
                            } else {
                                sort.setText("Set default sorting");
                            }
                            reloadSubs();
                        }
                    });
                    builder.show();
                }
            });
            dialoglayout.findViewById(me.ccrama.redditslide.R.id.theme).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    int style = new me.ccrama.redditslide.ColorPreferences(me.ccrama.redditslide.Activities.SubredditView.this).getThemeSubreddit(subOverride);
                    final android.content.Context contextThemeWrapper = new android.support.v7.view.ContextThemeWrapper(me.ccrama.redditslide.Activities.SubredditView.this, style);
                    android.view.LayoutInflater localInflater = getLayoutInflater().cloneInContext(contextThemeWrapper);
                    final android.view.View dialoglayout = localInflater.inflate(me.ccrama.redditslide.R.layout.colorsub, null);
                    java.util.ArrayList<java.lang.String> arrayList = new java.util.ArrayList<>();
                    arrayList.add(subOverride);
                    me.ccrama.redditslide.Adapters.SettingsSubAdapter.showSubThemeEditor(arrayList, me.ccrama.redditslide.Activities.SubredditView.this, dialoglayout);
                }
            });
            dialoglayout.findViewById(me.ccrama.redditslide.R.id.mods).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    final android.app.Dialog d = new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.SubredditView.this).title(me.ccrama.redditslide.R.string.sidebar_findingmods).cancelable(true).content(me.ccrama.redditslide.R.string.misc_please_wait).progress(true, 100).show();
                    new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
                        java.util.ArrayList<net.dean.jraw.models.UserRecord> mods;

                        @java.lang.Override
                        protected java.lang.Void doInBackground(java.lang.Void... params) {
                            mods = new java.util.ArrayList<>();
                            net.dean.jraw.paginators.UserRecordPaginator paginator = new net.dean.jraw.paginators.UserRecordPaginator(me.ccrama.redditslide.Authentication.reddit, subOverride, "moderators");
                            paginator.setSorting(net.dean.jraw.paginators.Sorting.HOT);
                            paginator.setTimePeriod(net.dean.jraw.paginators.TimePeriod.ALL);
                            while (paginator.hasNext()) {
                                mods.addAll(paginator.next());
                            } 
                            return null;
                        }

                        @java.lang.Override
                        protected void onPostExecute(java.lang.Void aVoid) {
                            final java.util.ArrayList<java.lang.String> names = new java.util.ArrayList<>();
                            for (net.dean.jraw.models.UserRecord rec : mods) {
                                names.add(rec.getFullName());
                            }
                            d.dismiss();
                            new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.SubredditView.this).title(getString(me.ccrama.redditslide.R.string.sidebar_submods, subreddit)).items(names).itemsCallback(new com.afollestad.materialdialogs.MaterialDialog.ListCallback() {
                                @java.lang.Override
                                public void onSelection(com.afollestad.materialdialogs.MaterialDialog dialog, android.view.View itemView, int which, java.lang.CharSequence text) {
                                    android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.SubredditView.this, me.ccrama.redditslide.Activities.Profile.class);
                                    i.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_PROFILE, names.get(which));
                                    startActivity(i);
                                }
                            }).positiveText(me.ccrama.redditslide.R.string.btn_message).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                                @java.lang.Override
                                public void onClick(@android.support.annotation.NonNull
                                com.afollestad.materialdialogs.MaterialDialog dialog, @android.support.annotation.NonNull
                                com.afollestad.materialdialogs.DialogAction which) {
                                    android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.SubredditView.this, me.ccrama.redditslide.Activities.SendMessage.class);
                                    i.putExtra(me.ccrama.redditslide.Activities.SendMessage.EXTRA_NAME, "/r/" + subOverride);
                                    startActivity(i);
                                }
                            }).show();
                        }
                    }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                }
            });
            dialoglayout.findViewById(me.ccrama.redditslide.R.id.flair).setVisibility(android.view.View.GONE);
            if (me.ccrama.redditslide.Authentication.didOnline && me.ccrama.redditslide.Authentication.isLoggedIn) {
                new android.os.AsyncTask<android.view.View, java.lang.Void, android.view.View>() {
                    java.util.List<net.dean.jraw.models.FlairTemplate> flairs;

                    java.util.ArrayList<java.lang.String> flairText;

                    java.lang.String current;

                    net.dean.jraw.managers.AccountManager m;

                    @java.lang.Override
                    protected android.view.View doInBackground(android.view.View... params) {
                        try {
                            m = new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit);
                            com.fasterxml.jackson.databind.JsonNode node = m.getFlairChoicesRootNode(subOverride, null);
                            flairs = m.getFlairChoices(subOverride, node);
                            net.dean.jraw.models.FlairTemplate currentF = m.getCurrentFlair(subOverride, node);
                            if (currentF != null) {
                                if (currentF.getText().isEmpty()) {
                                    current = ("[" + currentF.getCssClass()) + "]";
                                } else {
                                    current = currentF.getText();
                                }
                            }
                            flairText = new java.util.ArrayList<>();
                            for (net.dean.jraw.models.FlairTemplate temp : flairs) {
                                if (temp.getText().isEmpty()) {
                                    flairText.add(("[" + temp.getCssClass()) + "]");
                                } else {
                                    flairText.add(temp.getText());
                                }
                            }
                        } catch (java.lang.Exception e1) {
                            e1.printStackTrace();
                        }
                        return params[0];
                    }

                    @java.lang.Override
                    protected void onPostExecute(android.view.View flair) {
                        if ((((flairs != null) && (!flairs.isEmpty())) && (flairText != null)) && (!flairText.isEmpty())) {
                            flair.setVisibility(android.view.View.VISIBLE);
                            if (current != null) {
                                ((android.widget.TextView) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.flair_text))).setText(getString(me.ccrama.redditslide.R.string.sidebar_flair, current));
                            }
                            flair.setOnClickListener(new android.view.View.OnClickListener() {
                                @java.lang.Override
                                public void onClick(android.view.View v) {
                                    new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.SubredditView.this).items(flairText).title(me.ccrama.redditslide.R.string.sidebar_select_flair).itemsCallback(new com.afollestad.materialdialogs.MaterialDialog.ListCallback() {
                                        @java.lang.Override
                                        public void onSelection(com.afollestad.materialdialogs.MaterialDialog dialog, android.view.View itemView, int which, java.lang.CharSequence text) {
                                            final net.dean.jraw.models.FlairTemplate t = flairs.get(which);
                                            if (t.isTextEditable()) {
                                                new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.SubredditView.this).title(me.ccrama.redditslide.R.string.sidebar_select_flair_text).input(getString(me.ccrama.redditslide.R.string.mod_flair_hint), t.getText(), true, new com.afollestad.materialdialogs.MaterialDialog.InputCallback() {
                                                    @java.lang.Override
                                                    public void onInput(com.afollestad.materialdialogs.MaterialDialog dialog, java.lang.CharSequence input) {
                                                    }
                                                }).positiveText(me.ccrama.redditslide.R.string.btn_set).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                                                    @java.lang.Override
                                                    public void onClick(com.afollestad.materialdialogs.MaterialDialog dialog, com.afollestad.materialdialogs.DialogAction which) {
                                                        final java.lang.String flair = dialog.getInputEditText().getText().toString();
                                                        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
                                                            @java.lang.Override
                                                            protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                                                                try {
                                                                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).setFlair(subOverride, t, flair, me.ccrama.redditslide.Authentication.name);
                                                                    net.dean.jraw.models.FlairTemplate currentF = m.getCurrentFlair(subOverride);
                                                                    if (currentF.getText().isEmpty()) {
                                                                        current = ("[" + currentF.getCssClass()) + "]";
                                                                    } else {
                                                                        current = currentF.getText();
                                                                    }
                                                                    return true;
                                                                } catch (java.lang.Exception e) {
                                                                    e.printStackTrace();
                                                                    return false;
                                                                }
                                                            }

                                                            @java.lang.Override
                                                            protected void onPostExecute(java.lang.Boolean done) {
                                                                android.support.design.widget.Snackbar s;
                                                                if (done) {
                                                                    if (current != null) {
                                                                        ((android.widget.TextView) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.flair_text))).setText(getString(me.ccrama.redditslide.R.string.sidebar_flair, current));
                                                                    }
                                                                    s = android.support.design.widget.Snackbar.make(mToolbar, me.ccrama.redditslide.R.string.snackbar_flair_success, android.support.design.widget.Snackbar.LENGTH_SHORT);
                                                                } else {
                                                                    s = android.support.design.widget.Snackbar.make(mToolbar, me.ccrama.redditslide.R.string.snackbar_flair_error, android.support.design.widget.Snackbar.LENGTH_SHORT);
                                                                }
                                                                if (s != null) {
                                                                    android.view.View view = s.getView();
                                                                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                                                                    tv.setTextColor(android.graphics.Color.WHITE);
                                                                    s.show();
                                                                }
                                                            }
                                                        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                                                    }
                                                }).negativeText(me.ccrama.redditslide.R.string.btn_cancel).show();
                                            } else {
                                                new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
                                                    @java.lang.Override
                                                    protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                                                        try {
                                                            new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).setFlair(subOverride, t, null, me.ccrama.redditslide.Authentication.name);
                                                            net.dean.jraw.models.FlairTemplate currentF = m.getCurrentFlair(subOverride);
                                                            if (currentF.getText().isEmpty()) {
                                                                current = ("[" + currentF.getCssClass()) + "]";
                                                            } else {
                                                                current = currentF.getText();
                                                            }
                                                            return true;
                                                        } catch (java.lang.Exception e) {
                                                            e.printStackTrace();
                                                            return false;
                                                        }
                                                    }

                                                    @java.lang.Override
                                                    protected void onPostExecute(java.lang.Boolean done) {
                                                        android.support.design.widget.Snackbar s;
                                                        if (done) {
                                                            if (current != null) {
                                                                ((android.widget.TextView) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.flair_text))).setText(getString(me.ccrama.redditslide.R.string.sidebar_flair, current));
                                                            }
                                                            s = android.support.design.widget.Snackbar.make(mToolbar, me.ccrama.redditslide.R.string.snackbar_flair_success, android.support.design.widget.Snackbar.LENGTH_SHORT);
                                                        } else {
                                                            s = android.support.design.widget.Snackbar.make(mToolbar, me.ccrama.redditslide.R.string.snackbar_flair_error, android.support.design.widget.Snackbar.LENGTH_SHORT);
                                                        }
                                                        if (s != null) {
                                                            android.view.View view = s.getView();
                                                            android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                                                            tv.setTextColor(android.graphics.Color.WHITE);
                                                            s.show();
                                                        }
                                                    }
                                                }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                                            }
                                        }
                                    }).show();
                                }
                            });
                        }
                    }
                }.execute(((android.view.View) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.flair))));
            }
        } else if (drawerLayout != null) {
            drawerLayout.setDrawerLockMode(android.support.v4.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED, android.support.v4.view.GravityCompat.END);
        }
    }

    public void doSubSidebarNoLoad(final java.lang.String subOverride) {
        findViewById(me.ccrama.redditslide.R.id.loader).setVisibility(android.view.View.GONE);
        invalidateOptionsMenu();
        if (((((((!subOverride.equalsIgnoreCase("all")) && (!subOverride.equalsIgnoreCase("frontpage"))) && (!subOverride.equalsIgnoreCase("friends"))) && (!subOverride.equalsIgnoreCase("mod"))) && (!subOverride.contains("+"))) && (!subOverride.contains("."))) && (!subOverride.contains("/m/"))) {
            if (drawerLayout != null) {
                drawerLayout.setDrawerLockMode(android.support.v4.widget.DrawerLayout.LOCK_MODE_UNLOCKED, android.support.v4.view.GravityCompat.END);
            }
            findViewById(me.ccrama.redditslide.R.id.sidebar_text).setVisibility(android.view.View.GONE);
            findViewById(me.ccrama.redditslide.R.id.sub_title).setVisibility(android.view.View.GONE);
            findViewById(me.ccrama.redditslide.R.id.subscribers).setVisibility(android.view.View.GONE);
            findViewById(me.ccrama.redditslide.R.id.active_users).setVisibility(android.view.View.GONE);
            findViewById(me.ccrama.redditslide.R.id.header_sub).setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getColor(subOverride));
            ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.sub_infotitle))).setText(subOverride);
            // Sidebar buttons should use subOverride's accent color
            int subColor = new me.ccrama.redditslide.ColorPreferences(this).getColor(subOverride);
            ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.theme_text))).setTextColor(subColor);
            ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.wiki_text))).setTextColor(subColor);
            ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.post_text))).setTextColor(subColor);
            ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.mods_text))).setTextColor(subColor);
            ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.flair_text))).setTextColor(subColor);
            ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.sorting).findViewById(me.ccrama.redditslide.R.id.sort))).setTextColor(subColor);
            ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.sync))).setTextColor(subColor);
        } else if (drawerLayout != null) {
            drawerLayout.setDrawerLockMode(android.support.v4.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED, android.support.v4.view.GravityCompat.END);
        }
    }

    public void executeAsyncSubreddit(java.lang.String sub) {
        new me.ccrama.redditslide.Activities.SubredditView.AsyncGetSubreddit().execute(sub);
    }

    public void filterContent(final java.lang.String subreddit) {
        final boolean[] chosen = new boolean[]{ me.ccrama.redditslide.PostMatch.isImage(subreddit.toLowerCase(java.util.Locale.ENGLISH)), me.ccrama.redditslide.PostMatch.isAlbums(subreddit.toLowerCase(java.util.Locale.ENGLISH)), me.ccrama.redditslide.PostMatch.isGif(subreddit.toLowerCase(java.util.Locale.ENGLISH)), me.ccrama.redditslide.PostMatch.isVideo(subreddit.toLowerCase(java.util.Locale.ENGLISH)), me.ccrama.redditslide.PostMatch.isUrls(subreddit.toLowerCase(java.util.Locale.ENGLISH)), me.ccrama.redditslide.PostMatch.isSelftext(subreddit.toLowerCase(java.util.Locale.ENGLISH)), me.ccrama.redditslide.PostMatch.isNsfw(subreddit.toLowerCase(java.util.Locale.ENGLISH)) };
        final java.lang.String FILTER_TITLE = (subreddit.equals("frontpage")) ? getString(me.ccrama.redditslide.R.string.content_to_hide, "frontpage") : getString(me.ccrama.redditslide.R.string.content_to_hide, "/r/" + subreddit);
        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setTitle(FILTER_TITLE).alwaysCallMultiChoiceCallback().setMultiChoiceItems(new java.lang.String[]{ getString(me.ccrama.redditslide.R.string.image_downloads), getString(me.ccrama.redditslide.R.string.type_albums), getString(me.ccrama.redditslide.R.string.type_gifs), getString(me.ccrama.redditslide.R.string.type_videos), getString(me.ccrama.redditslide.R.string.type_links), getString(me.ccrama.redditslide.R.string.type_selftext), getString(me.ccrama.redditslide.R.string.type_nsfw_content) }, chosen, new android.content.DialogInterface.OnMultiChoiceClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialog, int which, boolean isChecked) {
                chosen[which] = isChecked;
            }
        }).setPositiveButton(me.ccrama.redditslide.R.string.btn_save, new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                me.ccrama.redditslide.PostMatch.setChosen(chosen, subreddit);
                reloadSubs();
            }
        }).setNegativeButton(me.ccrama.redditslide.R.string.btn_cancel, null).show();
    }

    public int getCurrentPage() {
        int position = 0;
        int currentOrientation = getResources().getConfiguration().orientation;
        if ((((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).rv.getLayoutManager() instanceof android.support.v7.widget.LinearLayoutManager) && (currentOrientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE)) {
            position = ((android.support.v7.widget.LinearLayoutManager) (((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).rv.getLayoutManager())).findFirstCompletelyVisibleItemPosition() - 1;
        } else if (((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).rv.getLayoutManager() instanceof me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager) {
            int[] firstVisibleItems = null;
            firstVisibleItems = ((me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager) (((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).rv.getLayoutManager())).findFirstCompletelyVisibleItemPositions(firstVisibleItems);
            if ((firstVisibleItems != null) && (firstVisibleItems.length > 0)) {
                position = firstVisibleItems[0] - 1;
            }
        } else {
            position = ((me.ccrama.redditslide.Views.PreCachingLayoutManager) (((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).rv.getLayoutManager())).findFirstCompletelyVisibleItemPosition() - 1;
        }
        return position;
    }

    net.dean.jraw.paginators.TimePeriod time = net.dean.jraw.paginators.TimePeriod.DAY;

    net.dean.jraw.paginators.Sorting sorts;

    private void askTimePeriod(final net.dean.jraw.paginators.Sorting sort, final java.lang.String sub, final android.view.View dialoglayout) {
        final android.content.DialogInterface.OnClickListener l2 = new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0 :
                        time = net.dean.jraw.paginators.TimePeriod.HOUR;
                        break;
                    case 1 :
                        time = net.dean.jraw.paginators.TimePeriod.DAY;
                        break;
                    case 2 :
                        time = net.dean.jraw.paginators.TimePeriod.WEEK;
                        break;
                    case 3 :
                        time = net.dean.jraw.paginators.TimePeriod.MONTH;
                        break;
                    case 4 :
                        time = net.dean.jraw.paginators.TimePeriod.YEAR;
                        break;
                    case 5 :
                        time = net.dean.jraw.paginators.TimePeriod.ALL;
                        break;
                }
                me.ccrama.redditslide.SettingValues.setSubSorting(sort, time, sub);
                me.ccrama.redditslide.util.SortingUtil.setSorting(sub, sort);
                me.ccrama.redditslide.util.SortingUtil.setTime(sub, time);
                final android.widget.TextView sort = dialoglayout.findViewById(me.ccrama.redditslide.R.id.sort);
                net.dean.jraw.paginators.Sorting sortingis = me.ccrama.redditslide.SettingValues.getBaseSubmissionSort("Default sorting: " + subreddit);
                sort.setText(sortingis.name() + ((sortingis == net.dean.jraw.paginators.Sorting.CONTROVERSIAL) || (sortingis == net.dean.jraw.paginators.Sorting.TOP) ? " of " + me.ccrama.redditslide.SettingValues.getBaseTimePeriod(subreddit).name() : ""));
                reloadSubs();
            }
        };
        com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this);
        builder.setTitle(me.ccrama.redditslide.R.string.sorting_choose);
        builder.setSingleChoiceItems(me.ccrama.redditslide.util.SortingUtil.getSortingTimesStrings(), me.ccrama.redditslide.util.SortingUtil.getSortingTimeId(""), l2);
        builder.show();
    }

    public void openPopup() {
        android.support.v7.widget.PopupMenu popup = new android.support.v7.widget.PopupMenu(this, findViewById(me.ccrama.redditslide.R.id.anchor), android.view.Gravity.RIGHT);
        final android.text.Spannable[] base = me.ccrama.redditslide.util.SortingUtil.getSortingSpannables(subreddit);
        for (android.text.Spannable s : base) {
            // Do not add option for "Best" in any subreddit except for the frontpage.
            if ((!subreddit.toLowerCase().equals("frontpage")) && s.toString().equals(getString(me.ccrama.redditslide.R.string.sorting_best))) {
                continue;
            }
            android.view.MenuItem m = popup.getMenu().add(s);
        }
        popup.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(android.view.MenuItem item) {
                me.ccrama.redditslide.util.LogUtil.v("Chosen is " + item.getOrder());
                int i = 0;
                for (android.text.Spannable s : base) {
                    if (s.equals(item.getTitle())) {
                        break;
                    }
                    i++;
                }
                switch (i) {
                    case 0 :
                        me.ccrama.redditslide.util.SortingUtil.setSorting(subreddit, net.dean.jraw.paginators.Sorting.HOT);
                        reloadSubs();
                        break;
                    case 1 :
                        me.ccrama.redditslide.util.SortingUtil.setSorting(subreddit, net.dean.jraw.paginators.Sorting.NEW);
                        reloadSubs();
                        break;
                    case 2 :
                        me.ccrama.redditslide.util.SortingUtil.setSorting(subreddit, net.dean.jraw.paginators.Sorting.RISING);
                        reloadSubs();
                        break;
                    case 3 :
                        me.ccrama.redditslide.util.SortingUtil.setSorting(subreddit, net.dean.jraw.paginators.Sorting.TOP);
                        openPopupTime();
                        break;
                    case 4 :
                        me.ccrama.redditslide.util.SortingUtil.setSorting(subreddit, net.dean.jraw.paginators.Sorting.CONTROVERSIAL);
                        openPopupTime();
                        break;
                    case 5 :
                        me.ccrama.redditslide.util.SortingUtil.setSorting(subreddit, net.dean.jraw.paginators.Sorting.BEST);
                        reloadSubs();
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    public void openPopupTime() {
        android.support.v7.widget.PopupMenu popup = new android.support.v7.widget.PopupMenu(this, findViewById(me.ccrama.redditslide.R.id.anchor), android.view.Gravity.RIGHT);
        final android.text.Spannable[] base = me.ccrama.redditslide.util.SortingUtil.getSortingTimesSpannables(subreddit);
        for (android.text.Spannable s : base) {
            android.view.MenuItem m = popup.getMenu().add(s);
        }
        popup.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(android.view.MenuItem item) {
                me.ccrama.redditslide.util.LogUtil.v("Chosen is " + item.getOrder());
                int i = 0;
                for (android.text.Spannable s : base) {
                    if (s.equals(item.getTitle())) {
                        break;
                    }
                    i++;
                }
                switch (i) {
                    case 0 :
                        me.ccrama.redditslide.util.SortingUtil.setTime(subreddit, net.dean.jraw.paginators.TimePeriod.HOUR);
                        reloadSubs();
                        break;
                    case 1 :
                        me.ccrama.redditslide.util.SortingUtil.setTime(subreddit, net.dean.jraw.paginators.TimePeriod.DAY);
                        reloadSubs();
                        break;
                    case 2 :
                        me.ccrama.redditslide.util.SortingUtil.setTime(subreddit, net.dean.jraw.paginators.TimePeriod.WEEK);
                        reloadSubs();
                        break;
                    case 3 :
                        me.ccrama.redditslide.util.SortingUtil.setTime(subreddit, net.dean.jraw.paginators.TimePeriod.MONTH);
                        reloadSubs();
                        break;
                    case 4 :
                        me.ccrama.redditslide.util.SortingUtil.setTime(subreddit, net.dean.jraw.paginators.TimePeriod.YEAR);
                        reloadSubs();
                        break;
                    case 5 :
                        me.ccrama.redditslide.util.SortingUtil.setTime(subreddit, net.dean.jraw.paginators.TimePeriod.ALL);
                        reloadSubs();
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    public static boolean restarting;

    public void restartTheme() {
        android.content.Intent intent = this.getIntent();
        intent.putExtra(me.ccrama.redditslide.Activities.SubredditView.EXTRA_SUBREDDIT, subreddit);
        finish();
        me.ccrama.redditslide.Activities.SubredditView.restarting = true;
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void changeSubscription(net.dean.jraw.models.Subreddit subreddit, boolean isChecked) {
        if (isChecked) {
            me.ccrama.redditslide.UserSubscriptions.addSubreddit(subreddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), this);
        } else {
            me.ccrama.redditslide.UserSubscriptions.removeSubreddit(subreddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), this);
            pager.setCurrentItem(pager.getCurrentItem() - 1);
            restartTheme();
        }
        android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(mToolbar, isChecked ? getString(me.ccrama.redditslide.R.string.misc_subscribed) : getString(me.ccrama.redditslide.R.string.misc_unsubscribed), android.support.design.widget.Snackbar.LENGTH_SHORT);
        android.view.View view = s.getView();
        android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(android.graphics.Color.WHITE);
        s.show();
    }

    private void doSubOnlyStuff(final net.dean.jraw.models.Subreddit subreddit) {
        if (!isFinishing()) {
            findViewById(me.ccrama.redditslide.R.id.loader).setVisibility(android.view.View.GONE);
            if (subreddit.getDataNode().has("subreddit_type") && (!subreddit.getDataNode().get("subreddit_type").isNull())) {
                canSubmit = !subreddit.getDataNode().get("subreddit_type").asText().toUpperCase().equals("RESTRICTED");
            }
            if ((subreddit.getSidebar() != null) && (!subreddit.getSidebar().isEmpty())) {
                findViewById(me.ccrama.redditslide.R.id.sidebar_text).setVisibility(android.view.View.VISIBLE);
                final java.lang.String text = subreddit.getDataNode().get("description_html").asText().trim();
                final me.ccrama.redditslide.SpoilerRobotoTextView body = ((me.ccrama.redditslide.SpoilerRobotoTextView) (findViewById(me.ccrama.redditslide.R.id.sidebar_text)));
                me.ccrama.redditslide.Views.CommentOverflow overflow = ((me.ccrama.redditslide.Views.CommentOverflow) (findViewById(me.ccrama.redditslide.R.id.commentOverflow)));
                setViews(text, subreddit.getDisplayName(), body, overflow);
                // get all subs that have Notifications enabled
                java.util.ArrayList<java.lang.String> rawSubs = me.ccrama.redditslide.Reddit.stringToArray(me.ccrama.redditslide.Reddit.appRestart.getString(me.ccrama.redditslide.Notifications.CheckForMail.SUBS_TO_GET, ""));
                java.util.HashMap<java.lang.String, java.lang.Integer> subThresholds = new java.util.HashMap<>();
                for (java.lang.String s : rawSubs) {
                    try {
                        java.lang.String[] split = s.split(":");
                        subThresholds.put(split[0].toLowerCase(java.util.Locale.ENGLISH), java.lang.Integer.valueOf(split[1]));
                    } catch (java.lang.Exception ignored) {
                        // do nothing
                    }
                }
                // whether or not this subreddit was in the keySet
                boolean isNotified = subThresholds.keySet().contains(subreddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH));
                ((android.support.v7.widget.AppCompatCheckBox) (findViewById(me.ccrama.redditslide.R.id.notify_posts_state))).setChecked(isNotified);
            } else {
                findViewById(me.ccrama.redditslide.R.id.sidebar_text).setVisibility(android.view.View.GONE);
            }
            android.view.View collection = findViewById(me.ccrama.redditslide.R.id.collection);
            if (me.ccrama.redditslide.Authentication.isLoggedIn) {
                collection.setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
                            java.util.HashMap<java.lang.String, net.dean.jraw.models.MultiReddit> multis = new java.util.HashMap<java.lang.String, net.dean.jraw.models.MultiReddit>();

                            @java.lang.Override
                            protected java.lang.Void doInBackground(java.lang.Void... params) {
                                if (me.ccrama.redditslide.UserSubscriptions.multireddits == null) {
                                    me.ccrama.redditslide.UserSubscriptions.syncMultiReddits(me.ccrama.redditslide.Activities.SubredditView.this);
                                }
                                for (net.dean.jraw.models.MultiReddit r : me.ccrama.redditslide.UserSubscriptions.multireddits) {
                                    multis.put(r.getDisplayName(), r);
                                }
                                return null;
                            }

                            @java.lang.Override
                            protected void onPostExecute(java.lang.Void aVoid) {
                                new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.SubredditView.this).title(("Add /r/" + subreddit.getDisplayName()) + " to").items(multis.keySet()).itemsCallback(new com.afollestad.materialdialogs.MaterialDialog.ListCallback() {
                                    @java.lang.Override
                                    public void onSelection(com.afollestad.materialdialogs.MaterialDialog dialog, android.view.View itemView, final int which, java.lang.CharSequence text) {
                                        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
                                            @java.lang.Override
                                            protected java.lang.Void doInBackground(java.lang.Void... params) {
                                                try {
                                                    final java.lang.String multiName = multis.keySet().toArray(new java.lang.String[multis.size()])[which];
                                                    java.util.List<java.lang.String> subs = new java.util.ArrayList<java.lang.String>();
                                                    for (net.dean.jraw.models.MultiSubreddit sub : multis.get(multiName).getSubreddits()) {
                                                        subs.add(sub.getDisplayName());
                                                    }
                                                    subs.add(subreddit.getDisplayName());
                                                    new net.dean.jraw.managers.MultiRedditManager(me.ccrama.redditslide.Authentication.reddit).createOrUpdate(new net.dean.jraw.http.MultiRedditUpdateRequest.Builder(me.ccrama.redditslide.Authentication.name, multiName).subreddits(subs).build());
                                                    me.ccrama.redditslide.UserSubscriptions.syncMultiReddits(me.ccrama.redditslide.Activities.SubredditView.this);
                                                    runOnUiThread(new java.lang.Runnable() {
                                                        @java.lang.Override
                                                        public void run() {
                                                            drawerLayout.closeDrawers();
                                                            android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(mToolbar, getString(me.ccrama.redditslide.R.string.multi_subreddit_added, multiName), android.support.design.widget.Snackbar.LENGTH_LONG);
                                                            android.view.View view = s.getView();
                                                            android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                                                            tv.setTextColor(android.graphics.Color.WHITE);
                                                            s.show();
                                                        }
                                                    });
                                                } catch (net.dean.jraw.http.NetworkException | net.dean.jraw.ApiException e) {
                                                    runOnUiThread(new java.lang.Runnable() {
                                                        @java.lang.Override
                                                        public void run() {
                                                            runOnUiThread(new java.lang.Runnable() {
                                                                @java.lang.Override
                                                                public void run() {
                                                                    android.support.design.widget.Snackbar.make(mToolbar, getString(me.ccrama.redditslide.R.string.multi_error), android.support.design.widget.Snackbar.LENGTH_LONG).setAction(me.ccrama.redditslide.R.string.btn_ok, new android.view.View.OnClickListener() {
                                                                        @java.lang.Override
                                                                        public void onClick(android.view.View v) {
                                                                        }
                                                                    }).show();
                                                                }
                                                            });
                                                        }
                                                    });
                                                    e.printStackTrace();
                                                }
                                                return null;
                                            }
                                        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                                    }
                                }).show();
                            }
                        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                });
            } else {
                collection.setVisibility(android.view.View.GONE);
            }
            {
                final android.widget.TextView subscribe = ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.subscribe)));
                currentlySubbed = ((!me.ccrama.redditslide.Authentication.isLoggedIn) && me.ccrama.redditslide.UserSubscriptions.getSubscriptions(this).contains(subreddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH))) || (me.ccrama.redditslide.Authentication.isLoggedIn && subreddit.isUserSubscriber());
                doSubscribeButtonText(currentlySubbed, subscribe);
                assert subscribe != null;
                subscribe.setOnClickListener(new android.view.View.OnClickListener() {
                    private void doSubscribe() {
                        if (me.ccrama.redditslide.Authentication.isLoggedIn) {
                            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.SubredditView.this).setTitle(getString(me.ccrama.redditslide.R.string.subscribe_to, subreddit.getDisplayName())).setPositiveButton(me.ccrama.redditslide.R.string.reorder_add_subscribe, new android.content.DialogInterface.OnClickListener() {
                                @java.lang.Override
                                public void onClick(android.content.DialogInterface dialog, int which) {
                                    new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
                                        @java.lang.Override
                                        public void onPostExecute(java.lang.Boolean success) {
                                            if (!success) {
                                                // If subreddit was removed from account or not
                                                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.SubredditView.this).setTitle(me.ccrama.redditslide.R.string.force_change_subscription).setMessage(me.ccrama.redditslide.R.string.force_change_subscription_desc).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                                                    @java.lang.Override
                                                    public void onClick(android.content.DialogInterface dialog, int which) {
                                                        changeSubscription(subreddit, true);// Force add the subscription

                                                        android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(mToolbar, getString(me.ccrama.redditslide.R.string.misc_subscribed), android.support.design.widget.Snackbar.LENGTH_SHORT);
                                                        android.view.View view = s.getView();
                                                        android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                                                        tv.setTextColor(android.graphics.Color.WHITE);
                                                        s.show();
                                                    }
                                                }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, new android.content.DialogInterface.OnClickListener() {
                                                    @java.lang.Override
                                                    public void onClick(android.content.DialogInterface dialog, int which) {
                                                    }
                                                }).setCancelable(false).show();
                                            } else {
                                                changeSubscription(subreddit, true);
                                            }
                                        }

                                        @java.lang.Override
                                        protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                                            try {
                                                new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).subscribe(subreddit);
                                            } catch (net.dean.jraw.http.NetworkException e) {
                                                return false;// Either network crashed or trying to unsubscribe to a subreddit that the account isn't subscribed to

                                            }
                                            return true;
                                        }
                                    }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                                }
                            }).setNegativeButton(me.ccrama.redditslide.R.string.btn_cancel, null).setNeutralButton(me.ccrama.redditslide.R.string.btn_add_to_sublist, new android.content.DialogInterface.OnClickListener() {
                                @java.lang.Override
                                public void onClick(android.content.DialogInterface dialog, int which) {
                                    changeSubscription(subreddit, true);// Force add the subscription

                                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(mToolbar, me.ccrama.redditslide.R.string.sub_added, android.support.design.widget.Snackbar.LENGTH_SHORT);
                                    android.view.View view = s.getView();
                                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                                    tv.setTextColor(android.graphics.Color.WHITE);
                                    s.show();
                                }
                            }).show();
                        } else {
                            changeSubscription(subreddit, true);
                        }
                    }

                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        if (!currentlySubbed) {
                            doSubscribe();
                            doSubscribeButtonText(currentlySubbed, subscribe);
                        } else {
                            doUnsubscribe();
                            doSubscribeButtonText(currentlySubbed, subscribe);
                        }
                    }

                    private void doUnsubscribe() {
                        if (me.ccrama.redditslide.Authentication.didOnline) {
                            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.SubredditView.this).setTitle(getString(me.ccrama.redditslide.R.string.unsubscribe_from, subreddit.getDisplayName())).setPositiveButton(me.ccrama.redditslide.R.string.reorder_remove_unsubsribe, new android.content.DialogInterface.OnClickListener() {
                                @java.lang.Override
                                public void onClick(android.content.DialogInterface dialog, int which) {
                                    new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
                                        @java.lang.Override
                                        public void onPostExecute(java.lang.Boolean success) {
                                            if (!success) {
                                                // If subreddit was removed from account or not
                                                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.SubredditView.this).setTitle(me.ccrama.redditslide.R.string.force_change_subscription).setMessage(me.ccrama.redditslide.R.string.force_change_subscription_desc).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                                                    @java.lang.Override
                                                    public void onClick(android.content.DialogInterface dialog, int which) {
                                                        changeSubscription(subreddit, false);// Force add the subscription

                                                        android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(mToolbar, getString(me.ccrama.redditslide.R.string.misc_unsubscribed), android.support.design.widget.Snackbar.LENGTH_SHORT);
                                                        android.view.View view = s.getView();
                                                        android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                                                        tv.setTextColor(android.graphics.Color.WHITE);
                                                        s.show();
                                                    }
                                                }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, new android.content.DialogInterface.OnClickListener() {
                                                    @java.lang.Override
                                                    public void onClick(android.content.DialogInterface dialog, int which) {
                                                    }
                                                }).setCancelable(false).show();
                                            } else {
                                                changeSubscription(subreddit, false);
                                            }
                                        }

                                        @java.lang.Override
                                        protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                                            try {
                                                new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).unsubscribe(subreddit);
                                            } catch (net.dean.jraw.http.NetworkException e) {
                                                return false;// Either network crashed or trying to unsubscribe to a subreddit that the account isn't subscribed to

                                            }
                                            return true;
                                        }
                                    }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                                }
                            }).setNeutralButton(me.ccrama.redditslide.R.string.just_unsub, new android.content.DialogInterface.OnClickListener() {
                                @java.lang.Override
                                public void onClick(android.content.DialogInterface dialog, int which) {
                                    changeSubscription(subreddit, false);// Force add the subscription

                                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(mToolbar, me.ccrama.redditslide.R.string.misc_unsubscribed, android.support.design.widget.Snackbar.LENGTH_SHORT);
                                    android.view.View view = s.getView();
                                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                                    tv.setTextColor(android.graphics.Color.WHITE);
                                    s.show();
                                }
                            }).setNegativeButton(me.ccrama.redditslide.R.string.btn_cancel, null).show();
                        } else {
                            changeSubscription(subreddit, false);
                        }
                    }
                });
            }
            {
                final android.support.v7.widget.AppCompatCheckBox notifyStateCheckBox = ((android.support.v7.widget.AppCompatCheckBox) (findViewById(me.ccrama.redditslide.R.id.notify_posts_state)));
                assert notifyStateCheckBox != null;
                notifyStateCheckBox.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                    @java.lang.Override
                    public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            final java.lang.String sub = subreddit.getDisplayName();
                            if (((((((!sub.equalsIgnoreCase("all")) && (!sub.equalsIgnoreCase("frontpage"))) && (!sub.equalsIgnoreCase("friends"))) && (!sub.equalsIgnoreCase("mod"))) && (!sub.contains("+"))) && (!sub.contains("."))) && (!sub.contains("/m/"))) {
                                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.SubredditView.this).setTitle(getString(me.ccrama.redditslide.R.string.sub_post_notifs_title, sub)).setMessage(me.ccrama.redditslide.R.string.sub_post_notifs_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                                    @java.lang.Override
                                    public void onClick(android.content.DialogInterface dialog, int which) {
                                        new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.SubredditView.this).title(me.ccrama.redditslide.R.string.sub_post_notifs_threshold).items(new java.lang.String[]{ "1", "5", "10", "20", "40", "50" }).alwaysCallSingleChoiceCallback().itemsCallbackSingleChoice(0, new com.afollestad.materialdialogs.MaterialDialog.ListCallbackSingleChoice() {
                                            @java.lang.Override
                                            public boolean onSelection(com.afollestad.materialdialogs.MaterialDialog dialog, android.view.View itemView, int which, java.lang.CharSequence text) {
                                                java.util.ArrayList<java.lang.String> subs = me.ccrama.redditslide.Reddit.stringToArray(me.ccrama.redditslide.Reddit.appRestart.getString(me.ccrama.redditslide.Notifications.CheckForMail.SUBS_TO_GET, ""));
                                                subs.add((sub + ":") + text);
                                                me.ccrama.redditslide.Reddit.appRestart.edit().putString(me.ccrama.redditslide.Notifications.CheckForMail.SUBS_TO_GET, me.ccrama.redditslide.Reddit.arrayToString(subs)).commit();
                                                return true;
                                            }
                                        }).cancelable(false).show();
                                    }
                                }).setNegativeButton(me.ccrama.redditslide.R.string.btn_cancel, null).setNegativeButton(me.ccrama.redditslide.R.string.btn_cancel, new android.content.DialogInterface.OnClickListener() {
                                    @java.lang.Override
                                    public void onClick(android.content.DialogInterface dialog, int which) {
                                        notifyStateCheckBox.setChecked(false);
                                    }
                                }).setOnCancelListener(new android.content.DialogInterface.OnCancelListener() {
                                    @java.lang.Override
                                    public void onCancel(android.content.DialogInterface dialog) {
                                        notifyStateCheckBox.setChecked(false);
                                    }
                                }).show();
                            } else {
                                notifyStateCheckBox.setChecked(false);
                                android.widget.Toast.makeText(me.ccrama.redditslide.Activities.SubredditView.this, me.ccrama.redditslide.R.string.sub_post_notifs_err, android.widget.Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            android.content.Intent cancelIntent = new android.content.Intent(me.ccrama.redditslide.Activities.SubredditView.this, me.ccrama.redditslide.Activities.CancelSubNotifs.class);
                            cancelIntent.putExtra(me.ccrama.redditslide.Activities.CancelSubNotifs.EXTRA_SUB, subreddit.getDisplayName());
                            startActivity(cancelIntent);
                        }
                    }
                });
            }
            if (!subreddit.getPublicDescription().isEmpty()) {
                findViewById(me.ccrama.redditslide.R.id.sub_title).setVisibility(android.view.View.VISIBLE);
                setViews(subreddit.getDataNode().get("public_description_html").asText(), subreddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), ((me.ccrama.redditslide.SpoilerRobotoTextView) (findViewById(me.ccrama.redditslide.R.id.sub_title))), ((me.ccrama.redditslide.Views.CommentOverflow) (findViewById(me.ccrama.redditslide.R.id.sub_title_overflow))));
            } else {
                findViewById(me.ccrama.redditslide.R.id.sub_title).setVisibility(android.view.View.GONE);
            }
            if (subreddit.getDataNode().has("icon_img") && (!subreddit.getDataNode().get("icon_img").asText().isEmpty())) {
                ((me.ccrama.redditslide.Reddit) (getApplication())).getImageLoader().displayImage(subreddit.getDataNode().get("icon_img").asText(), ((android.widget.ImageView) (findViewById(me.ccrama.redditslide.R.id.subimage))));
            } else {
                findViewById(me.ccrama.redditslide.R.id.subimage).setVisibility(android.view.View.GONE);
            }
            ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.subscribers))).setText(getString(me.ccrama.redditslide.R.string.subreddit_subscribers_string, subreddit.getLocalizedSubscriberCount()));
            findViewById(me.ccrama.redditslide.R.id.subscribers).setVisibility(android.view.View.VISIBLE);
            ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.active_users))).setText(getString(me.ccrama.redditslide.R.string.subreddit_active_users_string_new, subreddit.getLocalizedAccountsActive()));
            findViewById(me.ccrama.redditslide.R.id.active_users).setVisibility(android.view.View.VISIBLE);
        }
    }

    private void doSubscribeButtonText(boolean currentlySubbed, android.widget.TextView subscribe) {
        if (me.ccrama.redditslide.Authentication.didOnline) {
            if (currentlySubbed) {
                subscribe.setText(me.ccrama.redditslide.R.string.unsubscribe_caps);
            } else {
                subscribe.setText(me.ccrama.redditslide.R.string.subscribe_caps);
            }
        } else if (currentlySubbed) {
            subscribe.setText(me.ccrama.redditslide.R.string.btn_remove_from_sublist);
        } else {
            subscribe.setText(me.ccrama.redditslide.R.string.btn_add_to_sublist);
        }
    }

    private void reloadSubs() {
        restartTheme();
    }

    private void setViews(java.lang.String rawHTML, java.lang.String subreddit, me.ccrama.redditslide.SpoilerRobotoTextView firstTextView, me.ccrama.redditslide.Views.CommentOverflow commentOverflow) {
        if (rawHTML.isEmpty()) {
            return;
        }
        java.util.List<java.lang.String> blocks = me.ccrama.redditslide.util.SubmissionParser.getBlocks(rawHTML);
        int startIndex = 0;
        // the <div class="md"> case is when the body contains a table or code block first
        if (!blocks.get(0).equals("<div class=\"md\">")) {
            firstTextView.setVisibility(android.view.View.VISIBLE);
            firstTextView.setTextHtml(blocks.get(0), subreddit);
            startIndex = 1;
        } else {
            firstTextView.setText("");
            firstTextView.setVisibility(android.view.View.GONE);
        }
        if (blocks.size() > 1) {
            if (startIndex == 0) {
                commentOverflow.setViews(blocks, subreddit);
            } else {
                commentOverflow.setViews(blocks.subList(startIndex, blocks.size()), subreddit);
            }
            me.ccrama.redditslide.Views.SidebarLayout sidebar = ((me.ccrama.redditslide.Views.SidebarLayout) (findViewById(me.ccrama.redditslide.R.id.drawer_layout)));
            for (int i = 0; i < commentOverflow.getChildCount(); i++) {
                android.view.View maybeScrollable = commentOverflow.getChildAt(i);
                if (maybeScrollable instanceof android.widget.HorizontalScrollView) {
                    sidebar.addScrollable(maybeScrollable);
                }
            }
        } else {
            commentOverflow.removeAllViews();
        }
    }

    public class OverviewPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
        private me.ccrama.redditslide.Fragments.SubmissionsView mCurrentFragment;

        private me.ccrama.redditslide.Fragments.BlankFragment blankPage;

        public OverviewPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
            pager.clearOnPageChangeListeners();
            pager.addOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {
                @java.lang.Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    if (position == 0) {
                        android.support.design.widget.CoordinatorLayout.LayoutParams params = ((android.support.design.widget.CoordinatorLayout.LayoutParams) (header.getLayoutParams()));
                        params.setMargins(header.getWidth() - positionOffsetPixels, 0, -(header.getWidth() - positionOffsetPixels), 0);
                        header.setLayoutParams(params);
                        if (positionOffsetPixels == 0) {
                            finish();
                            overridePendingTransition(0, me.ccrama.redditslide.R.anim.fade_out);
                        }
                    }
                    if (position == 0) {
                        ((me.ccrama.redditslide.Activities.SubredditView.OverviewPagerAdapter) (pager.getAdapter())).blankPage.doOffset(positionOffset);
                        pager.setBackgroundColor(adjustAlpha(positionOffset * 0.7F));
                    }
                }

                @java.lang.Override
                public void onPageSelected(final int position) {
                }

                @java.lang.Override
                public void onPageScrollStateChanged(int state) {
                }
            });
            if (pager.getAdapter() != null) {
                pager.setCurrentItem(1);
            }
        }

        @java.lang.Override
        public int getCount() {
            return 2;
        }

        @java.lang.Override
        public android.support.v4.app.Fragment getItem(int i) {
            if (i == 1) {
                me.ccrama.redditslide.Fragments.SubmissionsView f = new me.ccrama.redditslide.Fragments.SubmissionsView();
                android.os.Bundle args = new android.os.Bundle();
                args.putString("id", subreddit);
                f.setArguments(args);
                return f;
            } else {
                blankPage = new me.ccrama.redditslide.Fragments.BlankFragment();
                return blankPage;
            }
        }

        @java.lang.Override
        public void setPrimaryItem(android.view.ViewGroup container, int position, java.lang.Object object) {
            super.setPrimaryItem(container, position, object);
            doSetPrimary(object, position);
        }

        @java.lang.Override
        public android.os.Parcelable saveState() {
            return null;
        }

        public void doSetPrimary(java.lang.Object object, int position) {
            if ((((object != null) && (getCurrentFragment() != object)) && (position != 3)) && (object instanceof me.ccrama.redditslide.Fragments.SubmissionsView)) {
                mCurrentFragment = ((me.ccrama.redditslide.Fragments.SubmissionsView) (object));
                if ((mCurrentFragment.posts == null) && mCurrentFragment.isAdded()) {
                    mCurrentFragment.doAdapter();
                }
            }
        }

        public android.support.v4.app.Fragment getCurrentFragment() {
            return mCurrentFragment;
        }
    }

    public class OverviewPagerAdapterComment extends me.ccrama.redditslide.Activities.SubredditView.OverviewPagerAdapter {
        public int size = 2;

        public android.support.v4.app.Fragment storedFragment;

        me.ccrama.redditslide.Fragments.BlankFragment blankPage;

        private me.ccrama.redditslide.Fragments.SubmissionsView mCurrentFragment;

        public OverviewPagerAdapterComment(android.support.v4.app.FragmentManager fm) {
            super(fm);
            pager.clearOnPageChangeListeners();
            pager.addOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {
                @java.lang.Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    if (position == 0) {
                        android.support.design.widget.CoordinatorLayout.LayoutParams params = ((android.support.design.widget.CoordinatorLayout.LayoutParams) (header.getLayoutParams()));
                        params.setMargins(header.getWidth() - positionOffsetPixels, 0, -(header.getWidth() - positionOffsetPixels), 0);
                        header.setLayoutParams(params);
                        if (positionOffsetPixels == 0) {
                            finish();
                            overridePendingTransition(0, me.ccrama.redditslide.R.anim.fade_out);
                        }
                        blankPage.doOffset(positionOffset);
                        pager.setBackgroundColor(adjustAlpha(positionOffset * 0.7F));
                    } else if (positionOffset == 0) {
                        if (position == 1) {
                            doPageSelectedComments(position);
                        } else {
                            // todo if (mAsyncGetSubreddit != null) {
                            // mAsyncGetSubreddit.cancel(true);
                            // }
                            if (header.getTranslationY() == 0) {
                                header.animate().translationY(-header.getHeight()).setInterpolator(new android.view.animation.LinearInterpolator()).setDuration(180);
                            }
                            pager.setSwipeLeftOnly(true);
                            themeSystemBars(openingComments.getSubredditName().toLowerCase(java.util.Locale.ENGLISH));
                            setRecentBar(openingComments.getSubredditName().toLowerCase(java.util.Locale.ENGLISH));
                        }
                    }
                }

                @java.lang.Override
                public void onPageSelected(int position) {
                }

                @java.lang.Override
                public void onPageScrollStateChanged(int state) {
                }
            });
            if (pager.getAdapter() != null) {
                pager.getAdapter().notifyDataSetChanged();
                pager.setCurrentItem(1);
                pager.setCurrentItem(0);
            }
        }

        @java.lang.Override
        public android.os.Parcelable saveState() {
            return null;
        }

        public android.support.v4.app.Fragment getCurrentFragment() {
            return mCurrentFragment;
        }

        @java.lang.Override
        public void doSetPrimary(java.lang.Object object, int position) {
            if ((position != 2) && (position != 0)) {
                if (getCurrentFragment() != object) {
                    mCurrentFragment = ((me.ccrama.redditslide.Fragments.SubmissionsView) (object));
                    if (((mCurrentFragment != null) && (mCurrentFragment.posts == null)) && mCurrentFragment.isAdded()) {
                        mCurrentFragment.doAdapter();
                    }
                }
            }
        }

        @java.lang.Override
        public int getItemPosition(java.lang.Object object) {
            if (object != storedFragment)
                return android.support.v4.view.PagerAdapter.POSITION_NONE;

            return android.support.v4.view.PagerAdapter.POSITION_UNCHANGED;
        }

        @java.lang.Override
        public android.support.v4.app.Fragment getItem(int i) {
            if (i == 0) {
                blankPage = new me.ccrama.redditslide.Fragments.BlankFragment();
                return blankPage;
            } else if ((openingComments == null) || (i != 2)) {
                me.ccrama.redditslide.Fragments.SubmissionsView f = new me.ccrama.redditslide.Fragments.SubmissionsView();
                android.os.Bundle args = new android.os.Bundle();
                args.putString("id", subreddit);
                f.setArguments(args);
                return f;
            } else {
                android.support.v4.app.Fragment f = new me.ccrama.redditslide.Fragments.CommentPage();
                android.os.Bundle args = new android.os.Bundle();
                java.lang.String name = openingComments.getFullName();
                args.putString("id", name.substring(3, name.length()));
                args.putBoolean("archived", openingComments.isArchived());
                args.putBoolean("contest", openingComments.getDataNode().get("contest_mode").asBoolean());
                args.putBoolean("locked", openingComments.isLocked());
                args.putInt("page", currentComment);
                args.putString("subreddit", openingComments.getSubredditName());
                args.putString("baseSubreddit", subreddit);
                f.setArguments(args);
                return f;
            }
        }

        @java.lang.Override
        public int getCount() {
            return size;
        }
    }

    private class AsyncGetSubreddit extends android.os.AsyncTask<java.lang.String, java.lang.Void, net.dean.jraw.models.Subreddit> {
        @java.lang.Override
        public void onPostExecute(net.dean.jraw.models.Subreddit subreddit) {
            if (subreddit != null) {
                setResult(android.app.Activity.RESULT_OK);
                sub = subreddit;
                try {
                    doSubSidebarNoLoad(sub.getDisplayName());
                    doSubSidebar(sub.getDisplayName());
                    doSubOnlyStuff(sub);
                } catch (java.lang.NullPointerException e) {
                    // activity has been killed
                    if (!isFinishing())
                        finish();

                }
                me.ccrama.redditslide.Activities.SubredditView.this.subreddit = sub.getDisplayName();
                if ((subreddit.isNsfw() && me.ccrama.redditslide.SettingValues.storeHistory) && me.ccrama.redditslide.SettingValues.storeNSFWHistory) {
                    me.ccrama.redditslide.UserSubscriptions.addSubToHistory(subreddit.getDisplayName());
                } else if (me.ccrama.redditslide.SettingValues.storeHistory && (!subreddit.isNsfw())) {
                    me.ccrama.redditslide.UserSubscriptions.addSubToHistory(subreddit.getDisplayName());
                }
                // Over 18 interstitial for signed out users or those who haven't enabled NSFW content
                if (subreddit.isNsfw() && (!me.ccrama.redditslide.SettingValues.showNSFWContent)) {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.SubredditView.this).setTitle(getString(me.ccrama.redditslide.R.string.over18_title, subreddit.getDisplayName())).setMessage((getString(me.ccrama.redditslide.R.string.over18_desc) + "\n\n") + getString(me.ccrama.redditslide.Authentication.isLoggedIn ? me.ccrama.redditslide.R.string.over18_desc_loggedin : me.ccrama.redditslide.R.string.over18_desc_loggedout)).setCancelable(false).setPositiveButton(me.ccrama.redditslide.R.string.misc_continue, new android.content.DialogInterface.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).doAdapter(true);
                        }
                    }).setNeutralButton(me.ccrama.redditslide.R.string.btn_go_back, new android.content.DialogInterface.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            finish();
                            overridePendingTransition(0, me.ccrama.redditslide.R.anim.fade_out);
                        }
                    }).show();
                }
            }
        }

        @java.lang.Override
        protected net.dean.jraw.models.Subreddit doInBackground(final java.lang.String... params) {
            try {
                net.dean.jraw.models.Subreddit result = me.ccrama.redditslide.Authentication.reddit.getSubreddit(params[0]);
                if (result.isNsfw() == null) {
                    // Sub is probably a user profile backing subreddit for a deleted/suspended user
                    throw new java.lang.Exception("Sub has null values where it shouldn't");
                }
                return result;
            } catch (java.lang.Exception e) {
                runOnUiThread(new java.lang.Runnable() {
                    @java.lang.Override
                    public void run() {
                        try {
                            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.SubredditView.this).setTitle(me.ccrama.redditslide.R.string.subreddit_err).setMessage(getString(me.ccrama.redditslide.R.string.subreddit_err_msg_new, params[0])).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                                @java.lang.Override
                                public void onClick(android.content.DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    setResult(4);
                                    finish();
                                }
                            }).setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {
                                @java.lang.Override
                                public void onDismiss(android.content.DialogInterface dialog) {
                                    setResult(4);
                                    finish();
                                }
                            }).show();
                        } catch (java.lang.Exception ignored) {
                        }
                    }
                });
                e.printStackTrace();
                return null;
            }
        }
    }
}