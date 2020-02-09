package me.ccrama.redditslide.Activities;
import java.util.Set;
import me.ccrama.redditslide.Fragments.InboxPage;
import me.ccrama.redditslide.Autocache.AutoCacheScheduler;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.ColorPreferences;
import me.ccrama.redditslide.Fragments.SettingsGeneralFragment;
import me.ccrama.redditslide.ContentGrabber;
import me.ccrama.redditslide.Notifications.NotificationJobScheduler;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.UserSubscriptions;
import java.util.HashSet;
/**
 * Created by ccrama on 9/17/2015.
 */
public class Inbox extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    public static final java.lang.String EXTRA_UNREAD = "unread";

    public me.ccrama.redditslide.Activities.Inbox.OverviewPagerAdapter adapter;

    private android.support.design.widget.TabLayout tabs;

    private android.support.v4.view.ViewPager pager;

    @java.lang.Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        android.view.MenuInflater inflater = getMenuInflater();
        inflater.inflate(me.ccrama.redditslide.R.menu.menu_inbox, menu);
        // if (mShowInfoButton) menu.findItem(R.id.action_info).setVisible(true);
        // else menu.findItem(R.id.action_info).setVisible(false);
        return true;
    }

    private boolean changed;

    public long last;

    @java.lang.Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case me.ccrama.redditslide.R.id.home :
                onBackPressed();
                break;
            case me.ccrama.redditslide.R.id.notifs :
                android.view.LayoutInflater inflater = getLayoutInflater();
                final android.view.View dialoglayout = inflater.inflate(me.ccrama.redditslide.R.layout.inboxfrequency, null);
                me.ccrama.redditslide.Fragments.SettingsGeneralFragment.setupNotificationSettings(dialoglayout, this);
                break;
            case me.ccrama.redditslide.R.id.compose :
                android.content.Intent i = new android.content.Intent(this, me.ccrama.redditslide.Activities.SendMessage.class);
                startActivity(i);
                break;
            case me.ccrama.redditslide.R.id.read :
                changed = false;
                new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
                    @java.lang.Override
                    protected java.lang.Void doInBackground(java.lang.Void... params) {
                        try {
                            new net.dean.jraw.managers.InboxManager(me.ccrama.redditslide.Authentication.reddit).setAllRead();
                            changed = true;
                        } catch (java.lang.Exception ignored) {
                            ignored.printStackTrace();
                        }
                        return null;
                    }

                    @java.lang.Override
                    protected void onPostExecute(java.lang.Void aVoid) {
                        if (changed) {
                            // restart the fragment
                            adapter.notifyDataSetChanged();
                            try {
                                final int CURRENT_TAB = tabs.getSelectedTabPosition();
                                adapter = new me.ccrama.redditslide.Activities.Inbox.OverviewPagerAdapter(getSupportFragmentManager());
                                pager.setAdapter(adapter);
                                tabs.setupWithViewPager(pager);
                                scrollToTabAfterLayout(CURRENT_TAB);
                                pager.setCurrentItem(CURRENT_TAB);
                            } catch (java.lang.Exception e) {
                            }
                        }
                    }
                }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method to scroll the TabLayout to a specific index
     *
     * @param tabPosition
     * 		index to scroll to
     */
    private void scrollToTabAfterLayout(final int tabPosition) {
        if (tabs != null) {
            final android.view.ViewTreeObserver observer = tabs.getViewTreeObserver();
            if (observer.isAlive()) {
                observer.dispatchOnGlobalLayout();// In case a previous call is waiting when this call is made

                observer.addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {
                    @java.lang.Override
                    public void onGlobalLayout() {
                        tabs.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        tabs.getTabAt(tabPosition).select();
                    }
                });
            }
        }
    }

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstance) {
        overrideSwipeFromAnywhere();
        if (((me.ccrama.redditslide.Authentication.reddit == null) || (!me.ccrama.redditslide.Authentication.reddit.isAuthenticated())) || (me.ccrama.redditslide.Authentication.me == null)) {
            me.ccrama.redditslide.util.LogUtil.v("Reauthenticating");
            new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
                @java.lang.Override
                protected java.lang.Void doInBackground(java.lang.Void... params) {
                    if (me.ccrama.redditslide.Authentication.reddit == null) {
                        new me.ccrama.redditslide.Authentication(getApplicationContext());
                    }
                    try {
                        me.ccrama.redditslide.Authentication.me = me.ccrama.redditslide.Authentication.reddit.me();
                        me.ccrama.redditslide.Authentication.mod = me.ccrama.redditslide.Authentication.me.isMod();
                        me.ccrama.redditslide.Authentication.authentication.edit().putBoolean(me.ccrama.redditslide.Reddit.SHARED_PREF_IS_MOD, me.ccrama.redditslide.Authentication.mod).apply();
                        if (me.ccrama.redditslide.Reddit.notificationTime != (-1)) {
                            me.ccrama.redditslide.Reddit.notifications = new me.ccrama.redditslide.Notifications.NotificationJobScheduler(me.ccrama.redditslide.Activities.Inbox.this);
                            me.ccrama.redditslide.Reddit.notifications.start(getApplicationContext());
                        }
                        if (me.ccrama.redditslide.Reddit.cachedData.contains("toCache")) {
                            me.ccrama.redditslide.Reddit.autoCache = new me.ccrama.redditslide.Autocache.AutoCacheScheduler(me.ccrama.redditslide.Activities.Inbox.this);
                            me.ccrama.redditslide.Reddit.autoCache.start(getApplicationContext());
                        }
                        final java.lang.String name = me.ccrama.redditslide.Authentication.me.getFullName();
                        me.ccrama.redditslide.Authentication.name = name;
                        me.ccrama.redditslide.util.LogUtil.v("AUTHENTICATED");
                        me.ccrama.redditslide.UserSubscriptions.doCachedModSubs();
                        if (me.ccrama.redditslide.Authentication.reddit.isAuthenticated()) {
                            final java.util.Set<java.lang.String> accounts = me.ccrama.redditslide.Authentication.authentication.getStringSet("accounts", new java.util.HashSet<java.lang.String>());
                            if (accounts.contains(name)) {
                                // convert to new system
                                accounts.remove(name);
                                accounts.add((name + ":") + me.ccrama.redditslide.Authentication.refresh);
                                me.ccrama.redditslide.Authentication.authentication.edit().putStringSet("accounts", accounts).apply();// force commit

                            }
                            me.ccrama.redditslide.Authentication.isLoggedIn = true;
                            me.ccrama.redditslide.Reddit.notFirst = true;
                        }
                    } catch (java.lang.Exception ignored) {
                    }
                    return null;
                }
            }.execute();
        }
        super.onCreate(savedInstance);
        last = me.ccrama.redditslide.SettingValues.prefs.getLong("lastInbox", java.lang.System.currentTimeMillis() - ((60 * 1000) * 60));
        me.ccrama.redditslide.SettingValues.prefs.edit().putLong("lastInbox", java.lang.System.currentTimeMillis()).apply();
        applyColorTheme("");
        setContentView(me.ccrama.redditslide.R.layout.activity_inbox);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.title_inbox, true, true);
        mToolbar.setPopupTheme(new me.ccrama.redditslide.ColorPreferences(this).getFontStyle().getBaseId());
        tabs = ((android.support.design.widget.TabLayout) (findViewById(me.ccrama.redditslide.R.id.sliding_tabs)));
        tabs.setTabMode(android.support.design.widget.TabLayout.MODE_SCROLLABLE);
        tabs.setSelectedTabIndicatorColor(new me.ccrama.redditslide.ColorPreferences(this).getColor("no sub"));
        pager = ((android.support.v4.view.ViewPager) (findViewById(me.ccrama.redditslide.R.id.content_view)));
        findViewById(me.ccrama.redditslide.R.id.header).setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getDefaultColor());
        adapter = new me.ccrama.redditslide.Activities.Inbox.OverviewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        if ((getIntent() != null) && getIntent().hasExtra(me.ccrama.redditslide.Activities.Inbox.EXTRA_UNREAD)) {
            pager.setCurrentItem(1);
        }
        tabs.setupWithViewPager(pager);
        pager.setOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {
            @java.lang.Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @java.lang.Override
            public void onPageSelected(int position) {
                findViewById(me.ccrama.redditslide.R.id.header).animate().translationY(0).setInterpolator(new android.view.animation.LinearInterpolator()).setDuration(180);
                if ((position == 3) && (findViewById(me.ccrama.redditslide.R.id.read) != null)) {
                    findViewById(me.ccrama.redditslide.R.id.read).setVisibility(android.view.View.GONE);
                } else if (findViewById(me.ccrama.redditslide.R.id.read) != null) {
                    findViewById(me.ccrama.redditslide.R.id.read).setVisibility(android.view.View.VISIBLE);
                }
            }

            @java.lang.Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public class OverviewPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
        public OverviewPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @java.lang.Override
        public android.support.v4.app.Fragment getItem(int i) {
            android.support.v4.app.Fragment f = new me.ccrama.redditslide.Fragments.InboxPage();
            android.os.Bundle args = new android.os.Bundle();
            args.putString("id", me.ccrama.redditslide.ContentGrabber.InboxValue.values()[i].getWhereName());
            f.setArguments(args);
            return f;
        }

        @java.lang.Override
        public int getCount() {
            return me.ccrama.redditslide.ContentGrabber.InboxValue.values().length;
        }

        @java.lang.Override
        public java.lang.CharSequence getPageTitle(int position) {
            return getString(me.ccrama.redditslide.ContentGrabber.InboxValue.values()[position].getDisplayName());
        }
    }

    @java.lang.Override
    public void onResume() {
        super.onResume();
        android.view.inputmethod.InputMethodManager keyboard = ((android.view.inputmethod.InputMethodManager) (getSystemService(android.content.Context.INPUT_METHOD_SERVICE)));
        keyboard.hideSoftInputFromWindow(getWindow().getAttributes().token, 0);
    }
}