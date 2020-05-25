package me.ccrama.redditslide.Activities;
import java.util.Set;
import me.ccrama.redditslide.Fragments.BlankFragment;
import me.ccrama.redditslide.Autocache.AutoCacheScheduler;
import me.ccrama.redditslide.Fragments.CommentPage;
import java.util.ArrayList;
import me.ccrama.redditslide.Notifications.NotificationJobScheduler;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.HasSeen;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.UserSubscriptions;
import me.ccrama.redditslide.LastComments;
import java.util.HashSet;
/**
 * Created by ccrama on 9/17/2015.
 * <p/>
 * This activity takes parameters for a submission id (through intent or direct link), retrieves the
 * Submission object, and then displays the submission with its comments.
 */
public class A extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    me.ccrama.redditslide.Activities.A.OverviewPagerAdapter comments;

    boolean np;

    private android.support.v4.view.ViewPager pager;

    private java.lang.String subreddit;

    private java.lang.String name;

    private java.lang.String context;

    private int contextNumber;

    @java.lang.Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 14) && (comments != null)) {
            comments.notifyDataSetChanged();
        }
    }

    public static final java.lang.String EXTRA_SUBREDDIT = "subreddit";

    public static final java.lang.String EXTRA_CONTEXT = "context";

    public static final java.lang.String EXTRA_CONTEXT_NUMBER = "contextNumber";

    public static final java.lang.String EXTRA_SUBMISSION = "submission";

    public static final java.lang.String EXTRA_NP = "np";

    public static final java.lang.String EXTRA_LOADMORE = "loadmore";

    @java.lang.Override
    public boolean dispatchKeyEvent(android.view.KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (me.ccrama.redditslide.SettingValues.commentVolumeNav) {
            switch (keyCode) {
                case android.view.KeyEvent.KEYCODE_VOLUME_UP :
                    return ((me.ccrama.redditslide.Fragments.CommentPage) (comments.getCurrentFragment())).onKeyDown(keyCode, event);
                case android.view.KeyEvent.KEYCODE_VOLUME_DOWN :
                    return ((me.ccrama.redditslide.Fragments.CommentPage) (comments.getCurrentFragment())).onKeyDown(keyCode, event);
                default :
                    return super.dispatchKeyEvent(event);
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstance) {
        disableSwipeBackLayout();
        getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().getDecorView().setBackgroundDrawable(null);
        super.onCreate(savedInstance);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_slide);
        name = getIntent().getExtras().getString(me.ccrama.redditslide.Activities.A.EXTRA_SUBMISSION, "");
        subreddit = getIntent().getExtras().getString(me.ccrama.redditslide.Activities.A.EXTRA_SUBREDDIT, "");
        np = getIntent().getExtras().getBoolean(me.ccrama.redditslide.Activities.A.EXTRA_NP, false);
        context = getIntent().getExtras().getString(me.ccrama.redditslide.Activities.A.EXTRA_CONTEXT, "");
        contextNumber = getIntent().getExtras().getInt(me.ccrama.redditslide.Activities.A.EXTRA_CONTEXT_NUMBER, 5);
        if (subreddit.equals(me.ccrama.redditslide.Reddit.EMPTY_STRING)) {
            new me.ccrama.redditslide.Activities.A.AsyncGetSubredditName().execute(name);
            android.util.TypedValue typedValue = new android.util.TypedValue();
            getTheme().resolveAttribute(me.ccrama.redditslide.R.attr.activity_background, typedValue, true);
            int color = typedValue.data;
            findViewById(me.ccrama.redditslide.R.id.content_view).setBackgroundColor(color);
        } else {
            setupAdapter();
        }
        if (me.ccrama.redditslide.Authentication.isLoggedIn && (me.ccrama.redditslide.Authentication.me == null)) {
            new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
                @java.lang.Override
                protected java.lang.Void doInBackground(java.lang.Void... params) {
                    if (me.ccrama.redditslide.Authentication.reddit == null) {
                        new me.ccrama.redditslide.Authentication(getApplicationContext());
                    } else {
                        try {
                            me.ccrama.redditslide.Authentication.me = me.ccrama.redditslide.Authentication.reddit.me();
                            me.ccrama.redditslide.Authentication.mod = me.ccrama.redditslide.Authentication.me.isMod();
                            me.ccrama.redditslide.Authentication.authentication.edit().putBoolean(me.ccrama.redditslide.Reddit.SHARED_PREF_IS_MOD, me.ccrama.redditslide.Authentication.mod).apply();
                            if (me.ccrama.redditslide.Reddit.notificationTime != (-1)) {
                                me.ccrama.redditslide.Reddit.notifications = new me.ccrama.redditslide.Notifications.NotificationJobScheduler(me.ccrama.redditslide.Activities.A.this);
                                me.ccrama.redditslide.Reddit.notifications.start(getApplicationContext());
                            }
                            if (me.ccrama.redditslide.Reddit.cachedData.contains("toCache")) {
                                me.ccrama.redditslide.Reddit.autoCache = new me.ccrama.redditslide.Autocache.AutoCacheScheduler(me.ccrama.redditslide.Activities.A.this);
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
                        } catch (java.lang.Exception e) {
                            new me.ccrama.redditslide.Authentication(getApplicationContext());
                        }
                    }
                    return null;
                }
            }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public int adjustAlpha(float factor) {
        int alpha = java.lang.Math.round(android.graphics.Color.alpha(android.graphics.Color.BLACK) * factor);
        int red = android.graphics.Color.red(android.graphics.Color.BLACK);
        int green = android.graphics.Color.green(android.graphics.Color.BLACK);
        int blue = android.graphics.Color.blue(android.graphics.Color.BLACK);
        return android.graphics.Color.argb(alpha, red, green, blue);
    }

    private void setupAdapter() {
        themeSystemBars(subreddit);
        setRecentBar(subreddit);
        pager = ((android.support.v4.view.ViewPager) (findViewById(me.ccrama.redditslide.R.id.content_view)));
        comments = new me.ccrama.redditslide.Activities.A.OverviewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(comments);
        pager.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        pager.setCurrentItem(1);
        pager.setOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {
            @java.lang.Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if ((position == 0) && (positionOffsetPixels == 0)) {
                    finish();
                }
                if ((position == 0) && (((me.ccrama.redditslide.Activities.A.OverviewPagerAdapter) (pager.getAdapter())).blankPage != null)) {
                    ((me.ccrama.redditslide.Activities.A.OverviewPagerAdapter) (pager.getAdapter())).blankPage.doOffset(positionOffset);
                    pager.setBackgroundColor(adjustAlpha(positionOffset * 0.7F));
                }
            }

            @java.lang.Override
            public void onPageSelected(int position) {
            }

            @java.lang.Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    boolean locked;

    boolean archived;

    boolean contest;

    private class AsyncGetSubredditName extends android.os.AsyncTask<java.lang.String, java.lang.Void, java.lang.String> {
        @java.lang.Override
        protected void onPostExecute(java.lang.String s) {
            subreddit = s;
            setupAdapter();
        }

        @java.lang.Override
        protected java.lang.String doInBackground(java.lang.String... params) {
            try {
                final net.dean.jraw.models.Submission s = me.ccrama.redditslide.Authentication.reddit.getSubmission(params[0]);
                if (me.ccrama.redditslide.SettingValues.storeHistory) {
                    if ((me.ccrama.redditslide.SettingValues.storeNSFWHistory && s.isNsfw()) || (!s.isNsfw())) {
                        me.ccrama.redditslide.HasSeen.addSeen(s.getFullName());
                    }
                    me.ccrama.redditslide.LastComments.setComments(s);
                }
                me.ccrama.redditslide.HasSeen.setHasSeenSubmission(new java.util.ArrayList<net.dean.jraw.models.Submission>() {
                    {
                        this.add(s);
                    }
                });
                locked = s.isLocked();
                archived = s.isArchived();
                contest = s.getDataNode().get("contest_mode").asBoolean();
                if (s.getSubredditName() == null) {
                    subreddit = "Promoted";
                } else {
                    subreddit = s.getSubredditName();
                }
                return subreddit;
            } catch (java.lang.Exception e) {
                try {
                    runOnUiThread(new java.lang.Runnable() {
                        @java.lang.Override
                        public void run() {
                            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.A.this).setTitle(me.ccrama.redditslide.R.string.submission_not_found).setMessage(me.ccrama.redditslide.R.string.submission_not_found_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                                @java.lang.Override
                                public void onClick(android.content.DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {
                                @java.lang.Override
                                public void onDismiss(android.content.DialogInterface dialog) {
                                    finish();
                                }
                            }).show();
                        }
                    });
                } catch (java.lang.Exception ignored) {
                }
                return null;
            }
        }
    }

    public class OverviewPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
        private android.support.v4.app.Fragment mCurrentFragment;

        public me.ccrama.redditslide.Fragments.BlankFragment blankPage;

        public OverviewPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

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
        public android.support.v4.app.Fragment getItem(int i) {
            if (i == 0) {
                blankPage = new me.ccrama.redditslide.Fragments.BlankFragment();
                return blankPage;
            } else {
                android.support.v4.app.Fragment f = new me.ccrama.redditslide.Fragments.CommentPage();
                android.os.Bundle args = new android.os.Bundle();
                if (name.contains("t3_"))
                    name = name.substring(3, name.length());

                args.putString("id", name);
                args.putString("context", context);
                if (me.ccrama.redditslide.SettingValues.storeHistory) {
                    if (((context != null) && (!context.isEmpty())) && (!context.equals(me.ccrama.redditslide.Reddit.EMPTY_STRING))) {
                        me.ccrama.redditslide.HasSeen.addSeen("t1_" + context);
                    } else {
                        me.ccrama.redditslide.HasSeen.addSeen(name);
                    }
                }
                args.putBoolean("archived", archived);
                args.putBoolean("locked", locked);
                args.putBoolean("contest", contest);
                args.putInt("contextNumber", contextNumber);
                args.putString("subreddit", subreddit);
                args.putBoolean("single", getIntent().getBooleanExtra(me.ccrama.redditslide.Activities.A.EXTRA_LOADMORE, true));
                args.putBoolean("np", np);
                f.setArguments(args);
                return f;
            }
        }

        @java.lang.Override
        public int getCount() {
            return 2;
        }
    }
}