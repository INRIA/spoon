package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager;
import me.ccrama.redditslide.Views.PreCachingLayoutManager;
import java.util.HashMap;
import me.ccrama.redditslide.Visuals.Palette;
import java.util.ArrayList;
import me.ccrama.redditslide.ColorPreferences;
import me.ccrama.redditslide.Fragments.NewsView;
import me.ccrama.redditslide.CaseInsensitiveArrayList;
import me.ccrama.redditslide.Synccit.MySynccitUpdateTask;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.util.NetworkUtil;
import java.lang.reflect.Field;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.Views.ToggleSwipeViewPager;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Adapters.SubredditPostsRealm;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.UserSubscriptions;
import java.util.List;
import java.util.Map;
import me.ccrama.redditslide.util.NetworkStateReceiver;
import me.ccrama.redditslide.Synccit.SynccitRead;
public class NewsActivity extends me.ccrama.redditslide.Activities.BaseActivity implements me.ccrama.redditslide.util.NetworkStateReceiver.NetworkStateReceiverListener {
    public static final java.lang.String IS_ONLINE = "online";

    // Instance state keys
    static final java.lang.String SUBS = "news";

    private static final java.lang.String EXTRA_PAGE_TO = "PAGE_TO";

    public android.view.View header;

    public static me.ccrama.redditslide.Activities.Loader loader;

    public static java.util.Map<java.lang.String, java.lang.String> newsSubToMap = new java.util.HashMap<>();

    public final long ANIMATE_DURATION = 250;// duration of animations


    private final long ANIMATE_DURATION_OFFSET = 45;

    // offset for smoothing out the exit animations
    public me.ccrama.redditslide.Views.ToggleSwipeViewPager pager;

    public me.ccrama.redditslide.CaseInsensitiveArrayList usedArray;

    public me.ccrama.redditslide.Activities.NewsActivity.OverviewPagerAdapter adapter;

    public android.support.design.widget.TabLayout mTabLayout;

    public java.lang.String selectedSub;// currently selected subreddit


    public boolean inNightMode;

    boolean changed;

    boolean currentlySubbed;

    int back;

    private int headerHeight;// height of the header


    public int reloadItemNumber = -2;

    @java.lang.Override
    public void onBackPressed() {
        finish();
    }

    @java.lang.Override
    public void onPause() {
        super.onPause();
        changed = false;
        if (!me.ccrama.redditslide.SettingValues.synccitName.isEmpty()) {
            new me.ccrama.redditslide.Synccit.MySynccitUpdateTask().execute(me.ccrama.redditslide.Synccit.SynccitRead.newVisited.toArray(new java.lang.String[me.ccrama.redditslide.Synccit.SynccitRead.newVisited.size()]));
        }
        if (((me.ccrama.redditslide.Authentication.isLoggedIn && (me.ccrama.redditslide.Authentication.me != null)) && me.ccrama.redditslide.Authentication.me.hasGold()) && (!me.ccrama.redditslide.Synccit.SynccitRead.newVisited.isEmpty())) {
            new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
                @java.lang.Override
                protected java.lang.Void doInBackground(java.lang.Void... params) {
                    try {
                        java.lang.String[] returned = new java.lang.String[me.ccrama.redditslide.Synccit.SynccitRead.newVisited.size()];
                        int i = 0;
                        for (java.lang.String s : me.ccrama.redditslide.Synccit.SynccitRead.newVisited) {
                            if (!s.contains("t3_")) {
                                s = "t3_" + s;
                            }
                            returned[i] = s;
                            i++;
                        }
                        new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).storeVisits(returned);
                        me.ccrama.redditslide.Synccit.SynccitRead.newVisited = new java.util.ArrayList<>();
                    } catch (java.lang.Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
        }
        // Upon leaving MainActivity--hide the toolbar search if it is visible
        if (findViewById(me.ccrama.redditslide.R.id.toolbar_search).getVisibility() == android.view.View.VISIBLE) {
            findViewById(me.ccrama.redditslide.R.id.close_search_toolbar).performClick();
        }
    }

    @java.lang.Override
    public void onRequestPermissionsResult(int requestCode, java.lang.String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1 :
                {
                    // If request is cancelled, the result arrays are empty.
                    if ((grantResults.length > 0) && (grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED)) {
                        // permission was granted, yay! Do the
                        // contacts-related task you need to do.
                    } else {
                        runOnUiThread(new java.lang.Runnable() {
                            @java.lang.Override
                            public void run() {
                                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.NewsActivity.this).setTitle(me.ccrama.redditslide.R.string.err_permission).setMessage(me.ccrama.redditslide.R.string.err_permission_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                                    @java.lang.Override
                                    public void onClick(android.content.DialogInterface dialog, int which) {
                                        android.support.v4.app.ActivityCompat.requestPermissions(me.ccrama.redditslide.Activities.NewsActivity.this, new java.lang.String[]{ android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
                                    }
                                }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, new android.content.DialogInterface.OnClickListener() {
                                    @java.lang.Override
                                    public void onClick(android.content.DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                            }
                        });
                    }
                }
                // other 'case' lines to check for other
                // permissions this app might request
        }
    }

    @java.lang.Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
            changed = true;
        } else if (newConfig.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
            changed = true;
        }
    }

    @java.lang.Override
    protected void onCreate(final android.os.Bundle savedInstanceState) {
        inNightMode = me.ccrama.redditslide.SettingValues.isNight();
        disableSwipeBackLayout();
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_news);
        mToolbar = ((android.support.v7.widget.Toolbar) (findViewById(me.ccrama.redditslide.R.id.toolbar)));
        mToolbar.setPopupTheme(new me.ccrama.redditslide.ColorPreferences(this).getFontStyle().getBaseId());
        setSupportActionBar(mToolbar);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            android.view.Window window = this.getWindow();
            window.setStatusBarColor(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(me.ccrama.redditslide.Visuals.Palette.getDefaultColor())));
        }
        mTabLayout = ((android.support.design.widget.TabLayout) (findViewById(me.ccrama.redditslide.R.id.sliding_tabs)));
        header = findViewById(me.ccrama.redditslide.R.id.header);
        pager = ((me.ccrama.redditslide.Views.ToggleSwipeViewPager) (findViewById(me.ccrama.redditslide.R.id.content_view)));
        mTabLayout = ((android.support.design.widget.TabLayout) (findViewById(me.ccrama.redditslide.R.id.sliding_tabs)));
        me.ccrama.redditslide.UserSubscriptions.doNewsSubs(this);
        /**
         * int for the current base theme selected.
         * 0 = Dark, 1 = Light, 2 = AMOLED, 3 = Dark blue, 4 = AMOLED with contrast, 5 = Sepia
         */
        me.ccrama.redditslide.SettingValues.currentTheme = new me.ccrama.redditslide.ColorPreferences(this).getFontStyle().getThemeType();
        networkStateReceiver = new me.ccrama.redditslide.util.NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        try {
            this.registerReceiver(networkStateReceiver, new android.content.IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        } catch (java.lang.Exception e) {
        }
    }

    @java.lang.Override
    public void networkAvailable() {
    }

    me.ccrama.redditslide.util.NetworkStateReceiver networkStateReceiver;

    @java.lang.Override
    public void networkUnavailable() {
    }

    @java.lang.Override
    public void onResume() {
        super.onResume();
        if (((!inNightMode) && me.ccrama.redditslide.SettingValues.isNight()) || (inNightMode && (!me.ccrama.redditslide.SettingValues.isNight()))) {
            restartTheme();
        }
        me.ccrama.redditslide.Reddit.setDefaultErrorHandler(this);
    }

    @java.lang.Override
    public void onDestroy() {
        try {
            unregisterReceiver(networkStateReceiver);
        } catch (java.lang.Exception ignored) {
        }
        me.ccrama.redditslide.Activities.Slide.hasStarted = false;
        super.onDestroy();
    }

    public static java.lang.String abbreviate(final java.lang.String str, final int maxWidth) {
        if (str.length() <= maxWidth) {
            return str;
        }
        final java.lang.String abrevMarker = "...";
        return str.substring(0, maxWidth - 3) + abrevMarker;
    }

    /**
     * Set the drawer edge (i.e. how sensitive the drawer is) Based on a given screen width
     * percentage.
     *
     * @param displayWidthPercentage
     * 		larger the value, the more sensitive the drawer swipe is;
     * 		percentage of screen width
     * @param drawerLayout
     * 		drawerLayout to adjust the swipe edge
     */
    public static void setDrawerEdge(android.app.Activity activity, final float displayWidthPercentage, android.support.v4.widget.DrawerLayout drawerLayout) {
        try {
            java.lang.reflect.Field mDragger = drawerLayout.getClass().getSuperclass().getDeclaredField("mLeftDragger");
            mDragger.setAccessible(true);
            android.support.v4.widget.ViewDragHelper leftDragger = ((android.support.v4.widget.ViewDragHelper) (mDragger.get(drawerLayout)));
            java.lang.reflect.Field mEdgeSize = leftDragger.getClass().getDeclaredField("mEdgeSize");
            mEdgeSize.setAccessible(true);
            final int currentEdgeSize = mEdgeSize.getInt(leftDragger);
            android.graphics.Point displaySize = new android.graphics.Point();
            activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
            mEdgeSize.setInt(leftDragger, java.lang.Math.max(currentEdgeSize, ((int) (displaySize.x * displayWidthPercentage))));
        } catch (java.lang.Exception e) {
            me.ccrama.redditslide.util.LogUtil.e(e + ": Exception thrown while changing navdrawer edge size");
        }
    }

    boolean isTop;

    private void changeTop() {
        reloadSubs();
        // test
    }

    public int getCurrentPage() {
        int position = 0;
        int currentOrientation = getResources().getConfiguration().orientation;
        if (adapter.getCurrentFragment() == null) {
            return 0;
        }
        if ((((me.ccrama.redditslide.Fragments.NewsView) (adapter.getCurrentFragment())).rv.getLayoutManager() instanceof android.support.v7.widget.LinearLayoutManager) && (currentOrientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE)) {
            position = ((android.support.v7.widget.LinearLayoutManager) (((me.ccrama.redditslide.Fragments.NewsView) (adapter.getCurrentFragment())).rv.getLayoutManager())).findFirstCompletelyVisibleItemPosition() - 1;
        } else if (((me.ccrama.redditslide.Fragments.NewsView) (adapter.getCurrentFragment())).rv.getLayoutManager() instanceof me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager) {
            int[] firstVisibleItems = null;
            firstVisibleItems = ((me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager) (((me.ccrama.redditslide.Fragments.NewsView) (adapter.getCurrentFragment())).rv.getLayoutManager())).findFirstCompletelyVisibleItemPositions(firstVisibleItems);
            if ((firstVisibleItems != null) && (firstVisibleItems.length > 0)) {
                position = firstVisibleItems[0] - 1;
            }
        } else {
            position = ((me.ccrama.redditslide.Views.PreCachingLayoutManager) (((me.ccrama.redditslide.Fragments.NewsView) (adapter.getCurrentFragment())).rv.getLayoutManager())).findFirstCompletelyVisibleItemPosition() - 1;
        }
        return position;
    }

    java.lang.String shouldLoad;

    public void reloadSubs() {
        int current = pager.getCurrentItem();
        if (current < 0) {
            current = 0;
        }
        reloadItemNumber = current;
        adapter = new me.ccrama.redditslide.Activities.NewsActivity.OverviewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        reloadItemNumber = -2;
        shouldLoad = usedArray.get(current);
        pager.setCurrentItem(current);
        if (mTabLayout != null) {
            mTabLayout.setupWithViewPager(pager);
            scrollToTabAfterLayout(current);
        }
        setToolbarClick();
    }

    public void restartTheme() {
        android.content.Intent intent = this.getIntent();
        int page = pager.getCurrentItem();
        intent.putExtra(me.ccrama.redditslide.Activities.NewsActivity.EXTRA_PAGE_TO, page);
        finish();
        startActivity(intent);
        overridePendingTransition(me.ccrama.redditslide.R.anim.fade_in_real, me.ccrama.redditslide.R.anim.fading_out_real);
    }

    public void scrollToTop() {
        int[] firstVisibleItems;
        int pastVisiblesItems = 0;
        if (adapter.getCurrentFragment() == null)
            return;

        firstVisibleItems = ((me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager) (((me.ccrama.redditslide.Fragments.NewsView) (adapter.getCurrentFragment())).rv.getLayoutManager())).findFirstVisibleItemPositions(null);
        if ((firstVisibleItems != null) && (firstVisibleItems.length > 0)) {
            for (int firstVisibleItem : firstVisibleItems) {
                pastVisiblesItems = firstVisibleItem;
            }
        }
        if (pastVisiblesItems > 8) {
            ((me.ccrama.redditslide.Fragments.NewsView) (adapter.getCurrentFragment())).rv.scrollToPosition(0);
            header.animate().translationY(header.getHeight()).setInterpolator(new android.view.animation.LinearInterpolator()).setDuration(0);
        } else {
            ((me.ccrama.redditslide.Fragments.NewsView) (adapter.getCurrentFragment())).rv.smoothScrollToPosition(0);
        }
        ((me.ccrama.redditslide.Fragments.NewsView) (adapter.getCurrentFragment())).resetScroll();
    }

    int toGoto;

    public void setDataSet(java.util.List<java.lang.String> data) {
        if ((data != null) && (!data.isEmpty())) {
            usedArray = new me.ccrama.redditslide.CaseInsensitiveArrayList(data);
            if (adapter == null) {
                adapter = new me.ccrama.redditslide.Activities.NewsActivity.OverviewPagerAdapter(getSupportFragmentManager());
            } else {
                adapter.notifyDataSetChanged();
            }
            pager.setAdapter(adapter);
            pager.setOffscreenPageLimit(1);
            if (toGoto == (-1)) {
                toGoto = 0;
            }
            if (toGoto >= usedArray.size()) {
                toGoto -= 1;
            }
            shouldLoad = usedArray.get(toGoto);
            selectedSub = usedArray.get(toGoto);
            themeSystemBars(usedArray.get(toGoto));
            final java.lang.String USEDARRAY_0 = usedArray.get(0);
            mTabLayout.setSelectedTabIndicatorColor(new me.ccrama.redditslide.ColorPreferences(this).getColor(USEDARRAY_0));
            pager.setCurrentItem(toGoto);
            mTabLayout.setupWithViewPager(pager);
            if (mTabLayout != null) {
                mTabLayout.setupWithViewPager(pager);
                scrollToTabAfterLayout(toGoto);
            }
            setToolbarClick();
        } else if (me.ccrama.redditslide.util.NetworkUtil.isConnected(this)) {
            me.ccrama.redditslide.UserSubscriptions.doNewsSubs(this);
        }
    }

    public void setToolbarClick() {
        if (mTabLayout != null) {
            mTabLayout.setOnTabSelectedListener(new android.support.design.widget.TabLayout.ViewPagerOnTabSelectedListener(pager) {
                @java.lang.Override
                public void onTabReselected(android.support.design.widget.TabLayout.Tab tab) {
                    super.onTabReselected(tab);
                    scrollToTop();
                }
            });
        } else {
            me.ccrama.redditslide.util.LogUtil.v("notnull");
            mToolbar.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    scrollToTop();
                }
            });
        }
    }

    public void updateMultiNameToSubs(java.util.Map<java.lang.String, java.lang.String> subs) {
        me.ccrama.redditslide.Activities.NewsActivity.newsSubToMap = subs;
    }

    public void updateSubs(java.util.ArrayList<java.lang.String> subs) {
        if (subs.isEmpty() && (!me.ccrama.redditslide.util.NetworkUtil.isConnected(this))) {
            // todo this
        } else if (me.ccrama.redditslide.Activities.NewsActivity.loader != null) {
            header.setVisibility(android.view.View.VISIBLE);
            setDataSet(subs);
            try {
                setDataSet(subs);
            } catch (java.lang.Exception ignored) {
            }
            me.ccrama.redditslide.Activities.NewsActivity.loader.finish();
            me.ccrama.redditslide.Activities.NewsActivity.loader = null;
        } else {
            setDataSet(subs);
        }
    }

    private void scrollToTabAfterLayout(final int tabIndex) {
        // from http://stackoverflow.com/a/34780589/3697225
        if (mTabLayout != null) {
            final android.view.ViewTreeObserver observer = mTabLayout.getViewTreeObserver();
            if (observer.isAlive()) {
                observer.dispatchOnGlobalLayout();// In case a previous call is waiting when this call is made

                observer.addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {
                    @java.lang.Override
                    public void onGlobalLayout() {
                        mTabLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        mTabLayout.getTabAt(tabIndex).select();
                    }
                });
            }
        }
    }

    public class OverviewPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
        protected me.ccrama.redditslide.Fragments.NewsView mCurrentFragment;

        public OverviewPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
            pager.clearOnPageChangeListeners();
            pager.addOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {
                @java.lang.Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    if (positionOffset == 0) {
                        header.animate().translationY(0).setInterpolator(new android.view.animation.LinearInterpolator()).setDuration(180);
                    }
                }

                @java.lang.Override
                public void onPageSelected(final int position) {
                    me.ccrama.redditslide.Reddit.currentPosition = position;
                    selectedSub = usedArray.get(position);
                    me.ccrama.redditslide.Fragments.NewsView page = ((me.ccrama.redditslide.Fragments.NewsView) (adapter.getCurrentFragment()));
                    int colorFrom = ((android.graphics.drawable.ColorDrawable) (header.getBackground())).getColor();
                    int colorTo = me.ccrama.redditslide.Visuals.Palette.getColor(selectedSub);
                    android.animation.ValueAnimator colorAnimation = android.animation.ValueAnimator.ofObject(new android.animation.ArgbEvaluator(), colorFrom, colorTo);
                    colorAnimation.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {
                        @java.lang.Override
                        public void onAnimationUpdate(android.animation.ValueAnimator animator) {
                            int color = ((int) (animator.getAnimatedValue()));
                            header.setBackgroundColor(color);
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                getWindow().setStatusBarColor(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(color));
                                if (me.ccrama.redditslide.SettingValues.colorNavBar) {
                                    getWindow().setNavigationBarColor(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(color));
                                }
                            }
                        }
                    });
                    colorAnimation.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
                    colorAnimation.setDuration(200);
                    colorAnimation.start();
                    setRecentBar(selectedSub);
                    mTabLayout.setSelectedTabIndicatorColor(new me.ccrama.redditslide.ColorPreferences(me.ccrama.redditslide.Activities.NewsActivity.this).getColor(selectedSub));
                    if ((page != null) && (page.adapter != null)) {
                        me.ccrama.redditslide.Adapters.SubredditPostsRealm p = page.adapter.dataSet;
                        if (p.offline) {
                            p.doNewsActivityOffline(me.ccrama.redditslide.Activities.NewsActivity.this, p.displayer);
                        }
                    }
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
        public int getCount() {
            if (usedArray == null) {
                return 1;
            } else {
                return usedArray.size();
            }
        }

        @java.lang.Override
        public android.support.v4.app.Fragment getItem(int i) {
            me.ccrama.redditslide.Fragments.NewsView f = new me.ccrama.redditslide.Fragments.NewsView();
            android.os.Bundle args = new android.os.Bundle();
            java.lang.String name;
            if (me.ccrama.redditslide.Activities.NewsActivity.newsSubToMap.containsKey(usedArray.get(i))) {
                name = me.ccrama.redditslide.Activities.NewsActivity.newsSubToMap.get(usedArray.get(i));
            } else {
                name = usedArray.get(i);
            }
            args.putString("id", name);
            f.setArguments(args);
            return f;
        }

        @java.lang.Override
        public void setPrimaryItem(android.view.ViewGroup container, int position, java.lang.Object object) {
            if ((reloadItemNumber == position) || (reloadItemNumber < 0)) {
                super.setPrimaryItem(container, position, object);
                if (usedArray.size() >= position)
                    doSetPrimary(object, position);

            } else {
                shouldLoad = usedArray.get(reloadItemNumber);
                if (me.ccrama.redditslide.Activities.NewsActivity.newsSubToMap.containsKey(usedArray.get(reloadItemNumber))) {
                    shouldLoad = me.ccrama.redditslide.Activities.NewsActivity.newsSubToMap.get(usedArray.get(reloadItemNumber));
                } else {
                    shouldLoad = usedArray.get(reloadItemNumber);
                }
            }
        }

        @java.lang.Override
        public android.os.Parcelable saveState() {
            return null;
        }

        public void doSetPrimary(java.lang.Object object, int position) {
            if (((object != null) && (getCurrentFragment() != object)) && (object instanceof me.ccrama.redditslide.Fragments.NewsView)) {
                shouldLoad = usedArray.get(position);
                if (me.ccrama.redditslide.Activities.NewsActivity.newsSubToMap.containsKey(usedArray.get(position))) {
                    shouldLoad = me.ccrama.redditslide.Activities.NewsActivity.newsSubToMap.get(usedArray.get(position));
                } else {
                    shouldLoad = usedArray.get(position);
                }
                mCurrentFragment = ((me.ccrama.redditslide.Fragments.NewsView) (object));
                if ((mCurrentFragment.posts == null) && mCurrentFragment.isAdded()) {
                    mCurrentFragment.doAdapter();
                }
            }
        }

        public android.support.v4.app.Fragment getCurrentFragment() {
            return mCurrentFragment;
        }

        @java.lang.Override
        public java.lang.CharSequence getPageTitle(int position) {
            if (usedArray != null) {
                return me.ccrama.redditslide.Activities.NewsActivity.abbreviate(usedArray.get(position), 25);
            } else {
                return "";
            }
        }
    }
}