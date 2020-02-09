package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager;
import java.util.Locale;
import me.ccrama.redditslide.util.EditTextValidator;
import me.ccrama.redditslide.ContentType;
import java.util.HashMap;
import me.ccrama.redditslide.Autocache.AutoCacheScheduler;
import java.util.ArrayList;
import me.ccrama.redditslide.Adapters.SideArrayAdapter;
import java.util.concurrent.Executors;
import com.fasterxml.jackson.databind.JsonNode;
import me.ccrama.redditslide.util.NetworkUtil;
import me.ccrama.redditslide.SpoilerRobotoTextView;
import me.ccrama.redditslide.UserSubscriptions;
import java.util.List;
import me.ccrama.redditslide.BuildConfig;
import java.util.HashSet;
import java.util.Collections;
import me.ccrama.redditslide.Fragments.SettingsThemeFragment;
import me.ccrama.redditslide.ImageFlairs;
import me.ccrama.redditslide.Fragments.CommentPage;
import me.ccrama.redditslide.CommentCacheAsync;
import me.ccrama.redditslide.Fragments.SettingsGeneralFragment;
import me.ccrama.redditslide.Notifications.CheckForMail;
import me.ccrama.redditslide.util.SubmissionParser;
import me.ccrama.redditslide.Notifications.NotificationJobScheduler;
import me.ccrama.redditslide.PostMatch;
import me.ccrama.redditslide.CaseInsensitiveArrayList;
import me.ccrama.redditslide.Synccit.MySynccitUpdateTask;
import me.ccrama.redditslide.Adapters.SettingsSubAdapter;
import me.ccrama.redditslide.FDroid;
import me.ccrama.redditslide.Views.ToggleSwipeViewPager;
import java.util.Map;
import java.util.Arrays;
import me.ccrama.redditslide.Fragments.DrawerItemsDialog;
import me.ccrama.redditslide.OpenRedditLink;
import me.ccrama.redditslide.Adapters.SubredditPosts;
import java.util.Set;
import me.ccrama.redditslide.Views.PreCachingLayoutManager;
import me.ccrama.redditslide.util.OnSingleClickListener;
import me.ccrama.redditslide.Visuals.Palette;
import static me.ccrama.redditslide.UserSubscriptions.modOf;
import me.ccrama.redditslide.SettingValues;
import java.lang.reflect.Field;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.TimeUtils;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.HasSeen;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Views.CommentOverflow;
import me.ccrama.redditslide.ColorPreferences;
import me.ccrama.redditslide.util.SortingUtil;
import me.ccrama.redditslide.Views.SidebarLayout;
import me.ccrama.redditslide.Constants;
import me.ccrama.redditslide.Authentication;
import java.util.concurrent.ExecutorService;
import me.ccrama.redditslide.Fragments.SubmissionsView;
import me.ccrama.redditslide.util.NetworkStateReceiver;
import me.ccrama.redditslide.Synccit.SynccitRead;
public class MainActivity extends me.ccrama.redditslide.Activities.BaseActivity implements me.ccrama.redditslide.util.NetworkStateReceiver.NetworkStateReceiverListener {
    public static final java.lang.String EXTRA_PAGE_TO = "pageTo";

    public static final java.lang.String IS_ONLINE = "online";

    // Instance state keys
    static final java.lang.String SUBS = "subscriptions";

    static final java.lang.String LOGGED_IN = "loggedIn";

    static final java.lang.String USERNAME = "username";

    static final int TUTORIAL_RESULT = 55;

    static final int INBOX_RESULT = 66;

    static final int RESET_ADAPTER_RESULT = 3;

    static final int SETTINGS_RESULT = 2;

    public static me.ccrama.redditslide.Activities.Loader loader;

    public static boolean datasetChanged;

    public static java.util.Map<java.lang.String, java.lang.String> multiNameToSubsMap = new java.util.HashMap<>();

    public static boolean checkedPopups;

    public static java.lang.String shouldLoad;

    public static boolean isRestart;

    public static int restartPage;

    public final long ANIMATE_DURATION = 250;// duration of animations


    private final long ANIMATE_DURATION_OFFSET = 45;// offset for smoothing out the exit animations


    public boolean singleMode;

    public me.ccrama.redditslide.Views.ToggleSwipeViewPager pager;

    public me.ccrama.redditslide.CaseInsensitiveArrayList usedArray;

    public android.support.v4.widget.DrawerLayout drawerLayout;

    public android.view.View hea;

    public android.widget.EditText drawerSearch;

    public android.view.View header;

    public java.lang.String subToDo;

    public me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapter adapter;

    public int toGoto = 0;

    public boolean first = true;

    public android.support.design.widget.TabLayout mTabLayout;

    public android.widget.ListView drawerSubList;

    public java.lang.String selectedSub;// currently selected subreddit


    public java.lang.Runnable doImage;

    public android.content.Intent data;

    public boolean commentPager = false;

    public java.lang.Runnable runAfterLoad;

    public boolean canSubmit;

    // if the view mode is set to Subreddit Tabs, save the title ("Slide" or "Slide (debug)")
    public java.lang.String tabViewModeTitle;

    public int currentComment;

    public net.dean.jraw.models.Submission openingComments;

    public int toOpenComments = -1;

    public boolean inNightMode;

    boolean changed;

    java.lang.String term;

    android.view.View headerMain;

    com.afollestad.materialdialogs.MaterialDialog d;

    android.os.AsyncTask<android.view.View, java.lang.Void, android.view.View> currentFlair;

    me.ccrama.redditslide.SpoilerRobotoTextView sidebarBody;

    me.ccrama.redditslide.Views.CommentOverflow sidebarOverflow;

    android.view.View accountsArea;

    me.ccrama.redditslide.Adapters.SideArrayAdapter sideArrayAdapter;

    android.view.Menu menu;

    android.os.AsyncTask caching;

    boolean currentlySubbed;

    int back;

    private me.ccrama.redditslide.Activities.MainActivity.AsyncGetSubreddit mAsyncGetSubreddit = null;

    private int headerHeight;// height of the header


    public int reloadItemNumber = -2;

    @java.lang.Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        if (requestCode == me.ccrama.redditslide.Activities.MainActivity.SETTINGS_RESULT) {
            int current = pager.getCurrentItem();
            if (commentPager && (current == currentComment)) {
                current = current - 1;
            }
            if (current < 0)
                current = 0;

            adapter = new me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapter(getSupportFragmentManager());
            pager.setAdapter(adapter);
            pager.setCurrentItem(current);
            if (mTabLayout != null) {
                mTabLayout.setupWithViewPager(pager);
                scrollToTabAfterLayout(current);
            }
            setToolbarClick();
        } else if (((requestCode == 2001) || (requestCode == 2002)) && (resultCode == android.app.Activity.RESULT_OK)) {
            if ((me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_DRAWER) || (me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_BOTH)) {
                drawerLayout.closeDrawers();
                drawerSearch.setText("");
            }
            // clear the text from the toolbar search field
            if (findViewById(me.ccrama.redditslide.R.id.toolbar_search) != null) {
                ((android.widget.AutoCompleteTextView) (findViewById(me.ccrama.redditslide.R.id.toolbar_search))).setText("");
            }
            android.view.View view = this.getCurrentFocus();
            if (view != null) {
                android.view.inputmethod.InputMethodManager imm = ((android.view.inputmethod.InputMethodManager) (getSystemService(android.content.Context.INPUT_METHOD_SERVICE)));
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } else if ((requestCode == 2002) && (resultCode != android.app.Activity.RESULT_OK)) {
            mToolbar.performLongClick();// search was init from the toolbar, so return focus to the toolbar

        } else if ((requestCode == 423) && (resultCode == android.app.Activity.RESULT_OK)) {
            ((me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapterComment) (adapter)).mCurrentComments.doResult(data);
        } else if (requestCode == 940) {
            if ((adapter != null) && (adapter.getCurrentFragment() != null)) {
                if (resultCode == android.app.Activity.RESULT_OK) {
                    java.util.ArrayList<java.lang.Integer> posts = data.getIntegerArrayListExtra("seen");
                    ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).adapter.refreshView(posts);
                    if ((data.hasExtra("lastPage") && (data.getIntExtra("lastPage", 0) != 0)) && (((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).rv.getLayoutManager() instanceof android.support.v7.widget.LinearLayoutManager)) {
                        ((android.support.v7.widget.LinearLayoutManager) (((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).rv.getLayoutManager())).scrollToPositionWithOffset(data.getIntExtra("lastPage", 0) + 1, mToolbar.getHeight());
                    }
                } else {
                    ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).adapter.refreshView();
                }
            }
        } else if (requestCode == me.ccrama.redditslide.Activities.MainActivity.RESET_ADAPTER_RESULT) {
            resetAdapter();
            setDrawerSubList();
        } else if (requestCode == me.ccrama.redditslide.Activities.MainActivity.TUTORIAL_RESULT) {
            me.ccrama.redditslide.UserSubscriptions.doMainActivitySubs(this);
        } else if (requestCode == me.ccrama.redditslide.Activities.MainActivity.INBOX_RESULT) {
            // update notification badge
            new me.ccrama.redditslide.Activities.MainActivity.AsyncNotificationBadge().executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (requestCode == 3333) {
            this.data = data;
            if (doImage != null) {
                android.os.Handler handler = new android.os.Handler();
                handler.post(doImage);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        /* todo  if(resultCode == 4 && UserSubscriptions.hasChanged){
        UserSubscriptions.hasChanged = false;
        sideArrayAdapter.setSideItems(UserSubscriptions.getAllSubreddits(this));
        sideArrayAdapter.notifyDataSetChanged();
        }
         */
    }

    @java.lang.Override
    public void onBackPressed() {
        if (((drawerLayout != null) && drawerLayout.isDrawerOpen(android.support.v4.view.GravityCompat.START)) || ((drawerLayout != null) && drawerLayout.isDrawerOpen(android.support.v4.view.GravityCompat.END))) {
            drawerLayout.closeDrawers();
        } else if (commentPager && (pager.getCurrentItem() == toOpenComments)) {
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        } else if (((me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_TOOLBAR) || (me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_BOTH)) && (findViewById(me.ccrama.redditslide.R.id.toolbar_search).getVisibility() == android.view.View.VISIBLE)) {
            findViewById(me.ccrama.redditslide.R.id.close_search_toolbar).performClick();// close GO_TO_SUB_FIELD

        } else if (me.ccrama.redditslide.SettingValues.backButtonBehavior == me.ccrama.redditslide.Constants.BackButtonBehaviorOptions.OpenDrawer.getValue()) {
            drawerLayout.openDrawer(android.view.Gravity.START);
        } else if (me.ccrama.redditslide.SettingValues.backButtonBehavior == me.ccrama.redditslide.Constants.BackButtonBehaviorOptions.ConfirmExit.getValue()) {
            final com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this);
            builder.setTitle(me.ccrama.redditslide.R.string.general_confirm_exit);
            builder.setMessage(me.ccrama.redditslide.R.string.general_confirm_exit_msg);
            builder.setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                @java.lang.Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton(me.ccrama.redditslide.R.string.btn_no, new android.content.DialogInterface.OnClickListener() {
                @java.lang.Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } else {
            super.onBackPressed();
        }
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
                                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.MainActivity.this).setTitle(me.ccrama.redditslide.R.string.err_permission).setMessage(me.ccrama.redditslide.R.string.err_permission_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                                    @java.lang.Override
                                    public void onClick(android.content.DialogInterface dialog, int which) {
                                        android.support.v4.app.ActivityCompat.requestPermissions(me.ccrama.redditslide.Activities.MainActivity.this, new java.lang.String[]{ android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
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
    public void onSaveInstanceState(android.os.Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putStringArrayList(me.ccrama.redditslide.Activities.MainActivity.SUBS, usedArray);
        savedInstanceState.putBoolean(me.ccrama.redditslide.Activities.MainActivity.LOGGED_IN, me.ccrama.redditslide.Authentication.isLoggedIn);
        savedInstanceState.putBoolean(me.ccrama.redditslide.Activities.MainActivity.IS_ONLINE, me.ccrama.redditslide.Authentication.didOnline);
        savedInstanceState.putString(me.ccrama.redditslide.Activities.MainActivity.USERNAME, me.ccrama.redditslide.Authentication.name);
    }

    @java.lang.Override
    public boolean dispatchKeyEvent(android.view.KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (((((pager != null) && me.ccrama.redditslide.SettingValues.commentPager) && (pager.getCurrentItem() == toOpenComments)) && me.ccrama.redditslide.SettingValues.commentVolumeNav) && (pager.getAdapter() instanceof me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapterComment)) {
            if (me.ccrama.redditslide.SettingValues.commentVolumeNav) {
                switch (keyCode) {
                    case android.view.KeyEvent.KEYCODE_VOLUME_UP :
                        return ((me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapterComment) (pager.getAdapter())).mCurrentComments.onKeyDown(keyCode, event);
                    case android.view.KeyEvent.KEYCODE_VOLUME_DOWN :
                        return ((me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapterComment) (pager.getAdapter())).mCurrentComments.onKeyDown(keyCode, event);
                    default :
                        return super.dispatchKeyEvent(event);
                }
            } else {
                return super.dispatchKeyEvent(event);
            }
        }
        if (event.getAction() != android.view.KeyEvent.ACTION_DOWN)
            return super.dispatchKeyEvent(event);

        switch (keyCode) {
            case android.view.KeyEvent.KEYCODE_SEARCH :
                return onKeyDown(keyCode, event);
            default :
                return super.dispatchKeyEvent(event);
        }
    }

    @java.lang.Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        android.view.MenuInflater inflater = getMenuInflater();
        if (me.ccrama.redditslide.util.NetworkUtil.isConnected(this)) {
            if (me.ccrama.redditslide.SettingValues.expandedToolbar) {
                inflater.inflate(me.ccrama.redditslide.R.menu.menu_subreddit_overview_expanded, menu);
            } else {
                inflater.inflate(me.ccrama.redditslide.R.menu.menu_subreddit_overview, menu);
            }
            // Hide the "Share Slide" menu if the user has Pro installed
            if (me.ccrama.redditslide.SettingValues.isPro) {
                menu.findItem(me.ccrama.redditslide.R.id.share).setVisible(false);
            }
            if (me.ccrama.redditslide.SettingValues.fab && (me.ccrama.redditslide.SettingValues.fabType == me.ccrama.redditslide.Constants.FAB_DISMISS)) {
                menu.findItem(me.ccrama.redditslide.R.id.hide_posts).setVisible(false);
            }
        } else {
            inflater.inflate(me.ccrama.redditslide.R.menu.menu_subreddit_overview_offline, menu);
        }
        return true;
    }

    @java.lang.Override
    public boolean onPrepareOptionsMenu(android.view.Menu menu) {
        super.onPrepareOptionsMenu(menu);
        this.menu = menu;
        /**
         * Hide the "Submit" and "Sidebar" menu items if the currently viewed sub is a multi,
         * domain, the frontpage, or /r/all. If the subreddit has a "." in it, we know it's a domain because
         * subreddits aren't allowed to have hard-stops in the name.
         */
        if (me.ccrama.redditslide.Authentication.didOnline && (usedArray != null)) {
            final java.lang.String subreddit = usedArray.get(pager.getCurrentItem());
            if ((((subreddit.contains("/m/") || subreddit.contains(".")) || subreddit.contains("+")) || subreddit.equals("frontpage")) || subreddit.equals("all")) {
                if (menu.findItem(me.ccrama.redditslide.R.id.submit) != null) {
                    menu.findItem(me.ccrama.redditslide.R.id.submit).setVisible(false);
                }
                if (menu.findItem(me.ccrama.redditslide.R.id.sidebar) != null) {
                    menu.findItem(me.ccrama.redditslide.R.id.sidebar).setVisible(false);
                }
            } else {
                if (menu.findItem(me.ccrama.redditslide.R.id.submit) != null) {
                    menu.findItem(me.ccrama.redditslide.R.id.submit).setVisible(true);
                }
                if (menu.findItem(me.ccrama.redditslide.R.id.sidebar) != null) {
                    menu.findItem(me.ccrama.redditslide.R.id.sidebar).setVisible(true);
                }
            }
            menu.findItem(me.ccrama.redditslide.R.id.theme).setOnMenuItemClickListener(new android.view.MenuItem.OnMenuItemClickListener() {
                @java.lang.Override
                public boolean onMenuItemClick(android.view.MenuItem item) {
                    int style = new me.ccrama.redditslide.ColorPreferences(me.ccrama.redditslide.Activities.MainActivity.this).getThemeSubreddit(subreddit);
                    final android.content.Context contextThemeWrapper = new android.support.v7.view.ContextThemeWrapper(me.ccrama.redditslide.Activities.MainActivity.this, style);
                    android.view.LayoutInflater localInflater = getLayoutInflater().cloneInContext(contextThemeWrapper);
                    final android.view.View dialoglayout = localInflater.inflate(me.ccrama.redditslide.R.layout.colorsub, null);
                    java.util.ArrayList<java.lang.String> arrayList = new java.util.ArrayList<>();
                    arrayList.add(subreddit);
                    me.ccrama.redditslide.Adapters.SettingsSubAdapter.showSubThemeEditor(arrayList, me.ccrama.redditslide.Activities.MainActivity.this, dialoglayout);
                    /* boolean old = SettingValues.isPicsEnabled(selectedSub);
                    SettingValues.setPicsEnabled(selectedSub, !item.isChecked());
                    item.setChecked(!item.isChecked());
                    reloadSubs();
                    invalidateOptionsMenu();
                     */
                    return false;
                }
            });
        }
        return true;
    }

    @java.lang.Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        final java.lang.String subreddit = usedArray.get(me.ccrama.redditslide.Reddit.currentPosition);
        switch (item.getItemId()) {
            case me.ccrama.redditslide.R.id.filter :
                filterContent(me.ccrama.redditslide.Activities.MainActivity.shouldLoad);
                return true;
            case me.ccrama.redditslide.R.id.sidebar :
                if ((((((!subreddit.equals("all")) && (!subreddit.equals("frontpage"))) && (!subreddit.contains("."))) && (!subreddit.contains("+"))) && (!subreddit.contains("."))) && (!subreddit.contains("/m/"))) {
                    drawerLayout.openDrawer(android.support.v4.view.GravityCompat.END);
                } else {
                    android.widget.Toast.makeText(this, me.ccrama.redditslide.R.string.sidebar_notfound, android.widget.Toast.LENGTH_SHORT).show();
                }
                return true;
            case me.ccrama.redditslide.R.id.night :
                {
                    android.view.LayoutInflater inflater = getLayoutInflater();
                    final android.view.View dialoglayout = inflater.inflate(me.ccrama.redditslide.R.layout.choosethemesmall, null);
                    com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this);
                    final android.widget.TextView title = dialoglayout.findViewById(me.ccrama.redditslide.R.id.title);
                    title.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getDefaultColor());
                    builder.setView(dialoglayout);
                    final android.app.Dialog d = builder.show();
                    back = new me.ccrama.redditslide.ColorPreferences(this).getFontStyle().getThemeType();
                    if (me.ccrama.redditslide.SettingValues.isNight()) {
                        dialoglayout.findViewById(me.ccrama.redditslide.R.id.nightmsg).setVisibility(android.view.View.VISIBLE);
                    }
                    for (final android.util.Pair<java.lang.Integer, java.lang.Integer> pair : me.ccrama.redditslide.ColorPreferences.themePairList) {
                        dialoglayout.findViewById(pair.first).setOnClickListener(new android.view.View.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.view.View v) {
                                java.lang.String[] names = new me.ccrama.redditslide.ColorPreferences(me.ccrama.redditslide.Activities.MainActivity.this).getFontStyle().getTitle().split("_");
                                java.lang.String name = names[names.length - 1];
                                final java.lang.String newName = name.replace("(", "");
                                for (me.ccrama.redditslide.ColorPreferences.Theme theme : me.ccrama.redditslide.ColorPreferences.Theme.values()) {
                                    if (theme.toString().contains(newName) && (theme.getThemeType() == pair.second)) {
                                        back = theme.getThemeType();
                                        new me.ccrama.redditslide.ColorPreferences(me.ccrama.redditslide.Activities.MainActivity.this).setFontStyle(theme);
                                        d.dismiss();
                                        restartTheme();
                                        break;
                                    }
                                }
                            }
                        });
                    }
                }
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
            case me.ccrama.redditslide.R.id.search :
                com.afollestad.materialdialogs.MaterialDialog.Builder builder = new com.afollestad.materialdialogs.MaterialDialog.Builder(this).title(me.ccrama.redditslide.R.string.search_title).alwaysCallInputCallback().input(getString(me.ccrama.redditslide.R.string.search_msg), "", new com.afollestad.materialdialogs.MaterialDialog.InputCallback() {
                    @java.lang.Override
                    public void onInput(com.afollestad.materialdialogs.MaterialDialog materialDialog, java.lang.CharSequence charSequence) {
                        term = charSequence.toString();
                    }
                });
                // Add "search current sub" if it is not frontpage/all/random
                if (((((((((!subreddit.equalsIgnoreCase("frontpage")) && (!subreddit.equalsIgnoreCase("all"))) && (!subreddit.contains("."))) && (!subreddit.contains("/m/"))) && (!subreddit.equalsIgnoreCase("friends"))) && (!subreddit.equalsIgnoreCase("random"))) && (!subreddit.equalsIgnoreCase("popular"))) && (!subreddit.equalsIgnoreCase("myrandom"))) && (!subreddit.equalsIgnoreCase("randnsfw"))) {
                    builder.positiveText(getString(me.ccrama.redditslide.R.string.search_subreddit, subreddit)).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                        @java.lang.Override
                        public void onClick(@android.support.annotation.NonNull
                        com.afollestad.materialdialogs.MaterialDialog materialDialog, @android.support.annotation.NonNull
                        com.afollestad.materialdialogs.DialogAction dialogAction) {
                            android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.Search.class);
                            i.putExtra(me.ccrama.redditslide.Activities.Search.EXTRA_TERM, term);
                            i.putExtra(me.ccrama.redditslide.Activities.Search.EXTRA_SUBREDDIT, subreddit);
                            android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), (("INTENT SHOWS " + term) + " AND ") + subreddit);
                            startActivity(i);
                        }
                    });
                    builder.neutralText(me.ccrama.redditslide.R.string.search_all).onNeutral(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                        @java.lang.Override
                        public void onClick(@android.support.annotation.NonNull
                        com.afollestad.materialdialogs.MaterialDialog materialDialog, @android.support.annotation.NonNull
                        com.afollestad.materialdialogs.DialogAction dialogAction) {
                            android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.Search.class);
                            i.putExtra(me.ccrama.redditslide.Activities.Search.EXTRA_TERM, term);
                            startActivity(i);
                        }
                    });
                } else {
                    builder.positiveText(me.ccrama.redditslide.R.string.search_all).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                        @java.lang.Override
                        public void onClick(@android.support.annotation.NonNull
                        com.afollestad.materialdialogs.MaterialDialog materialDialog, @android.support.annotation.NonNull
                        com.afollestad.materialdialogs.DialogAction dialogAction) {
                            android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.Search.class);
                            i.putExtra(me.ccrama.redditslide.Activities.Search.EXTRA_TERM, term);
                            startActivity(i);
                        }
                    });
                }
                builder.show();
                return true;
            case me.ccrama.redditslide.R.id.save :
                saveOffline(((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).posts.posts, ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).posts.subreddit);
                return true;
            case me.ccrama.redditslide.R.id.hide_posts :
                ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).clearSeenPosts(false);
                return true;
            case me.ccrama.redditslide.R.id.share :
                me.ccrama.redditslide.Reddit.defaultShareText("Slide for Reddit", "https://play.google.com/store/apps/details?id=me.ccrama.redditslide", this);
                return true;
            case me.ccrama.redditslide.R.id.submit :
                {
                    android.content.Intent i = new android.content.Intent(this, me.ccrama.redditslide.Activities.Submit.class);
                    i.putExtra(me.ccrama.redditslide.Activities.Submit.EXTRA_SUBREDDIT, subreddit);
                    startActivity(i);
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
                                java.util.List<net.dean.jraw.models.Submission> posts = ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).posts.posts;
                                if ((posts != null) && (!posts.isEmpty())) {
                                    android.content.Intent i2 = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.Gallery.class);
                                    i2.putExtra("offline", ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).posts.cached != null ? ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).posts.cached.time : 0L);
                                    i2.putExtra(me.ccrama.redditslide.Activities.Gallery.EXTRA_SUBREDDIT, ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).posts.subreddit);
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
                    java.util.List<net.dean.jraw.models.Submission> posts = ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).posts.posts;
                    if ((posts != null) && (!posts.isEmpty())) {
                        android.content.Intent i2 = new android.content.Intent(this, me.ccrama.redditslide.Activities.Shadowbox.class);
                        i2.putExtra(me.ccrama.redditslide.Activities.Shadowbox.EXTRA_PAGE, getCurrentPage());
                        i2.putExtra("offline", ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).posts.cached != null ? ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).posts.cached.time : 0L);
                        i2.putExtra(me.ccrama.redditslide.Activities.Shadowbox.EXTRA_SUBREDDIT, ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).posts.subreddit);
                        startActivity(i2);
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
                    if (me.ccrama.redditslide.SettingValues.previews > 0) {
                        b.setNeutralButton(("Preview (" + me.ccrama.redditslide.SettingValues.previews) + ")", new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                me.ccrama.redditslide.SettingValues.prefs.edit().putInt(me.ccrama.redditslide.SettingValues.PREVIEWS_LEFT, me.ccrama.redditslide.SettingValues.previews - 1).apply();
                                me.ccrama.redditslide.SettingValues.previews = me.ccrama.redditslide.SettingValues.prefs.getInt(me.ccrama.redditslide.SettingValues.PREVIEWS_LEFT, 10);
                                java.util.List<net.dean.jraw.models.Submission> posts = ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).posts.posts;
                                if ((posts != null) && (!posts.isEmpty())) {
                                    android.content.Intent i2 = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.Shadowbox.class);
                                    i2.putExtra(me.ccrama.redditslide.Activities.Shadowbox.EXTRA_PAGE, getCurrentPage());
                                    i2.putExtra("offline", ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).posts.cached != null ? ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).posts.cached.time : 0L);
                                    i2.putExtra(me.ccrama.redditslide.Activities.Shadowbox.EXTRA_SUBREDDIT, ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).posts.subreddit);
                                    startActivity(i2);
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

    @java.lang.Override
    protected void onCreate(final android.os.Bundle savedInstanceState) {
        inNightMode = me.ccrama.redditslide.SettingValues.isNight();
        disableSwipeBackLayout();
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & android.content.Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created
            finish();
            return;
        }
        if (!me.ccrama.redditslide.Activities.Slide.hasStarted) {
            me.ccrama.redditslide.Activities.Slide.hasStarted = true;
        }
        if (android.support.v4.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                android.support.v4.app.ActivityCompat.requestPermissions(this, new java.lang.String[]{ android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        boolean first = false;
        if ((me.ccrama.redditslide.Reddit.colors != null) && (!me.ccrama.redditslide.Reddit.colors.contains("firstStart53"))) {
            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setTitle("Content settings have moved!").setMessage("NSFW content is now disabled by default. If you are over the age of 18, to re-enable NSFW content, visit Settings > Content settings").setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, null).setCancelable(false).show();
            me.ccrama.redditslide.Reddit.colors.edit().putBoolean("firstStart53", true).apply();
        }
        if ((me.ccrama.redditslide.Reddit.colors != null) && (!me.ccrama.redditslide.Reddit.colors.contains("Tutorial"))) {
            first = true;
            if (me.ccrama.redditslide.Reddit.appRestart == null) {
                me.ccrama.redditslide.Reddit.appRestart = getSharedPreferences("appRestart", 0);
            }
            me.ccrama.redditslide.Reddit.appRestart.edit().putBoolean("firststart52", true).apply();
            android.content.Intent i = new android.content.Intent(this, me.ccrama.redditslide.Activities.Tutorial.class);
            doForcePrefs();
            startActivity(i);
        } else if ((me.ccrama.redditslide.Authentication.didOnline && me.ccrama.redditslide.util.NetworkUtil.isConnected(this)) && (!me.ccrama.redditslide.Activities.MainActivity.checkedPopups)) {
            runAfterLoad = new java.lang.Runnable() {
                @java.lang.Override
                public void run() {
                    runAfterLoad = null;
                    if (me.ccrama.redditslide.Authentication.isLoggedIn) {
                        new me.ccrama.redditslide.Activities.MainActivity.AsyncNotificationBadge().executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                    if (!me.ccrama.redditslide.Reddit.appRestart.getString(me.ccrama.redditslide.Notifications.CheckForMail.SUBS_TO_GET, "").isEmpty()) {
                        new me.ccrama.redditslide.Notifications.CheckForMail.AsyncGetSubs(me.ccrama.redditslide.Activities.MainActivity.this).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                    new android.os.AsyncTask<java.lang.Void, java.lang.Void, net.dean.jraw.models.Submission>() {
                        @java.lang.Override
                        protected net.dean.jraw.models.Submission doInBackground(java.lang.Void... params) {
                            if (me.ccrama.redditslide.Authentication.isLoggedIn)
                                me.ccrama.redditslide.UserSubscriptions.doOnlineSyncing();

                            try {
                                net.dean.jraw.paginators.SubredditPaginator p = new net.dean.jraw.paginators.SubredditPaginator(me.ccrama.redditslide.Authentication.reddit, "slideforreddit");
                                p.setLimit(2);
                                java.util.ArrayList<net.dean.jraw.models.Submission> posts = new java.util.ArrayList<>(p.next());
                                for (net.dean.jraw.models.Submission s : posts) {
                                    java.lang.String version = me.ccrama.redditslide.BuildConfig.VERSION_NAME;
                                    if (version.length() > 5) {
                                        version = version.substring(0, version.lastIndexOf("."));
                                    }
                                    if ((((s.isStickied() && (s.getSubmissionFlair().getText() != null)) && s.getSubmissionFlair().getText().equalsIgnoreCase("Announcement")) && (!me.ccrama.redditslide.Reddit.appRestart.contains("announcement" + s.getFullName()))) && s.getTitle().contains(version)) {
                                        me.ccrama.redditslide.Reddit.appRestart.edit().putBoolean("announcement" + s.getFullName(), true).apply();
                                        return s;
                                    } else if (((((me.ccrama.redditslide.BuildConfig.VERSION_NAME.contains("alpha") && s.isStickied()) && (s.getSubmissionFlair().getText() != null)) && s.getSubmissionFlair().getText().equalsIgnoreCase("Alpha")) && (!me.ccrama.redditslide.Reddit.appRestart.contains("announcement" + s.getFullName()))) && s.getTitle().contains(me.ccrama.redditslide.BuildConfig.VERSION_NAME)) {
                                        me.ccrama.redditslide.Reddit.appRestart.edit().putBoolean("announcement" + s.getFullName(), true).apply();
                                        return s;
                                    } else if (((s.isStickied() && s.getSubmissionFlair().getText().equalsIgnoreCase("PRO")) && (!me.ccrama.redditslide.SettingValues.isPro)) && (!me.ccrama.redditslide.Reddit.appRestart.contains("announcement" + s.getFullName()))) {
                                        me.ccrama.redditslide.Reddit.appRestart.edit().putBoolean("announcement" + s.getFullName(), true).apply();
                                        return s;
                                    }
                                }
                            } catch (java.lang.Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @java.lang.Override
                        protected void onPostExecute(final net.dean.jraw.models.Submission s) {
                            me.ccrama.redditslide.Activities.MainActivity.checkedPopups = true;
                            if (s != null) {
                                me.ccrama.redditslide.Reddit.appRestart.edit().putString("page", s.getDataNode().get("selftext_html").asText()).apply();
                                me.ccrama.redditslide.Reddit.appRestart.edit().putString("title", s.getTitle()).apply();
                                me.ccrama.redditslide.Reddit.appRestart.edit().putString("url", s.getUrl()).apply();
                                java.lang.String title;
                                if (s.getTitle().toLowerCase(java.util.Locale.ENGLISH).contains("release")) {
                                    title = getString(me.ccrama.redditslide.R.string.btn_changelog);
                                } else {
                                    title = getString(me.ccrama.redditslide.R.string.btn_view);
                                }
                                android.support.design.widget.Snackbar snack = android.support.design.widget.Snackbar.make(pager, s.getTitle(), android.support.design.widget.Snackbar.LENGTH_INDEFINITE).setAction(title, new me.ccrama.redditslide.util.OnSingleClickListener() {
                                    @java.lang.Override
                                    public void onSingleClick(android.view.View v) {
                                        android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.Announcement.class);
                                        startActivity(i);
                                    }
                                });
                                android.view.View view = snack.getView();
                                android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                                tv.setTextColor(android.graphics.Color.WHITE);
                                snack.show();
                            }
                        }
                    }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                    // todo this  new AsyncStartNotifSocket().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            };
        }
        if ((savedInstanceState != null) && (!changed)) {
            me.ccrama.redditslide.Authentication.isLoggedIn = savedInstanceState.getBoolean(me.ccrama.redditslide.Activities.MainActivity.LOGGED_IN);
            me.ccrama.redditslide.Authentication.name = savedInstanceState.getString(me.ccrama.redditslide.Activities.MainActivity.USERNAME, "LOGGEDOUT");
            me.ccrama.redditslide.Authentication.didOnline = savedInstanceState.getBoolean(me.ccrama.redditslide.Activities.MainActivity.IS_ONLINE);
        } else {
            changed = false;
        }
        if (getIntent().getBooleanExtra("EXIT", false))
            finish();

        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_overview);
        mToolbar = ((android.support.v7.widget.Toolbar) (findViewById(me.ccrama.redditslide.R.id.toolbar)));
        mToolbar.setPopupTheme(new me.ccrama.redditslide.ColorPreferences(this).getFontStyle().getBaseId());
        setSupportActionBar(mToolbar);
        if ((getIntent() != null) && getIntent().hasExtra(me.ccrama.redditslide.Activities.MainActivity.EXTRA_PAGE_TO)) {
            toGoto = getIntent().getIntExtra(me.ccrama.redditslide.Activities.MainActivity.EXTRA_PAGE_TO, 0);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            android.view.Window window = this.getWindow();
            window.setStatusBarColor(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(me.ccrama.redditslide.Visuals.Palette.getDefaultColor())));
        }
        mTabLayout = ((android.support.design.widget.TabLayout) (findViewById(me.ccrama.redditslide.R.id.sliding_tabs)));
        header = findViewById(me.ccrama.redditslide.R.id.header);
        // Gets the height of the header
        if (header != null) {
            header.getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {
                @java.lang.Override
                public void onGlobalLayout() {
                    headerHeight = header.getHeight();
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        header.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        header.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            });
        }
        pager = ((me.ccrama.redditslide.Views.ToggleSwipeViewPager) (findViewById(me.ccrama.redditslide.R.id.content_view)));
        singleMode = me.ccrama.redditslide.SettingValues.single;
        if (singleMode) {
            commentPager = me.ccrama.redditslide.SettingValues.commentPager;
        }
        // Inflate tabs if single mode is disabled
        if (!singleMode) {
            mTabLayout = ((android.support.design.widget.TabLayout) (((android.view.ViewStub) (findViewById(me.ccrama.redditslide.R.id.stub_tabs))).inflate()));
        }
        // Disable swiping if single mode is enabled
        if (singleMode) {
            pager.setSwipingEnabled(false);
        }
        sidebarBody = ((me.ccrama.redditslide.SpoilerRobotoTextView) (findViewById(me.ccrama.redditslide.R.id.sidebar_text)));
        sidebarOverflow = ((me.ccrama.redditslide.Views.CommentOverflow) (findViewById(me.ccrama.redditslide.R.id.commentOverflow)));
        if ((!me.ccrama.redditslide.Reddit.appRestart.getBoolean("isRestarting", false)) && me.ccrama.redditslide.Reddit.colors.contains("Tutorial")) {
            me.ccrama.redditslide.util.LogUtil.v("Starting main " + me.ccrama.redditslide.Authentication.name);
            me.ccrama.redditslide.Authentication.isLoggedIn = me.ccrama.redditslide.Reddit.appRestart.getBoolean("loggedin", false);
            me.ccrama.redditslide.Authentication.name = me.ccrama.redditslide.Reddit.appRestart.getString("name", "LOGGEDOUT");
            me.ccrama.redditslide.UserSubscriptions.doMainActivitySubs(this);
        } else if (!first) {
            me.ccrama.redditslide.util.LogUtil.v("Starting main 2 " + me.ccrama.redditslide.Authentication.name);
            me.ccrama.redditslide.Authentication.isLoggedIn = me.ccrama.redditslide.Reddit.appRestart.getBoolean("loggedin", false);
            me.ccrama.redditslide.Authentication.name = me.ccrama.redditslide.Reddit.appRestart.getString("name", "LOGGEDOUT");
            me.ccrama.redditslide.Reddit.appRestart.edit().putBoolean("isRestarting", false).commit();
            me.ccrama.redditslide.Reddit.isRestarting = false;
            me.ccrama.redditslide.UserSubscriptions.doMainActivitySubs(this);
        }
        final android.content.SharedPreferences seen = getSharedPreferences("SEEN", 0);
        if (((!seen.contains("isCleared")) && (!seen.getAll().isEmpty())) || (!me.ccrama.redditslide.Reddit.appRestart.contains("hasCleared"))) {
            new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
                @java.lang.Override
                protected java.lang.Void doInBackground(java.lang.Void... params) {
                    com.lusfold.androidkeyvaluestore.core.KVManger m = com.lusfold.androidkeyvaluestore.KVStore.getInstance();
                    java.util.Map<java.lang.String, ?> values = seen.getAll();
                    for (java.util.Map.Entry<java.lang.String, ?> entry : values.entrySet()) {
                        if ((entry.getKey().length() == 6) && (entry.getValue() instanceof java.lang.Boolean)) {
                            m.insert(entry.getKey(), "true");
                        } else if (entry.getValue() instanceof java.lang.Long) {
                            m.insert(entry.getKey(), java.lang.String.valueOf(seen.getLong(entry.getKey(), 0)));
                        }
                    }
                    seen.edit().clear().putBoolean("isCleared", true).apply();
                    if (getSharedPreferences("HIDDEN_POSTS", 0).getAll().size() != 0) {
                        getSharedPreferences("HIDDEN", 0).edit().clear().apply();
                        getSharedPreferences("HIDDEN_POSTS", 0).edit().clear().apply();
                    }
                    if (!me.ccrama.redditslide.Reddit.appRestart.contains("hasCleared")) {
                        android.content.SharedPreferences.Editor e = me.ccrama.redditslide.Reddit.appRestart.edit();
                        java.util.Map<java.lang.String, ?> toClear = me.ccrama.redditslide.Reddit.appRestart.getAll();
                        for (java.util.Map.Entry<java.lang.String, ?> entry : toClear.entrySet()) {
                            if ((entry.getValue() instanceof java.lang.String) && (((java.lang.String) (entry.getValue())).length() > 300)) {
                                e.remove(entry.getKey());
                            }
                        }
                        e.putBoolean("hasCleared", true);
                        e.apply();
                    }
                    return null;
                }

                @java.lang.Override
                protected void onPostExecute(java.lang.Void aVoid) {
                    dismissProgressDialog();
                }

                @java.lang.Override
                protected void onPreExecute() {
                    d = new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.MainActivity.this).title(me.ccrama.redditslide.R.string.misc_setting_up).content(me.ccrama.redditslide.R.string.misc_setting_up_message).progress(true, 100).cancelable(false).build();
                    d.show();
                }
            }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if (((!me.ccrama.redditslide.FDroid.isFDroid) && me.ccrama.redditslide.Authentication.isLoggedIn) && me.ccrama.redditslide.util.NetworkUtil.isConnected(this)) {
            // Display an snackbar that asks the user to rate the app after this
            // activity was created 6 times, never again when once clicked or with a maximum of
            // two times.
            /* .withSnack(new CustomSnack(new Intent(MainActivity.this, SettingsReddit.class), "Thumbnails are disabled", "Change", "THUMBNAIL_INFO")
            .withConditions(new AfterNumberOfOpportunities(2),
            new WithLimitedNumberOfTimes(2), new NeverAgainWhenClickedOnce())
            .withDuration(BaseSnack.DURATION_LONG))
             */
            org.ligi.snackengage.SnackEngage.from(this).withSnack(new org.ligi.snackengage.snacks.RateSnack().withConditions(new org.ligi.snackengage.conditions.NeverAgainWhenClickedOnce(), new org.ligi.snackengage.conditions.AfterNumberOfOpportunities(10), new org.ligi.snackengage.conditions.WithLimitedNumberOfTimes(2)).overrideActionText(getString(me.ccrama.redditslide.R.string.misc_rate_msg)).overrideTitleText(getString(me.ccrama.redditslide.R.string.misc_rate_title)).withDuration(org.ligi.snackengage.snacks.BaseSnack.DURATION_LONG)).build().engageWhenAppropriate();
        }
        if ((me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_TOOLBAR) || (me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_BOTH)) {
            setupSubredditSearchToolbar();
        }
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
        me.ccrama.redditslide.util.LogUtil.v("Installed browsers");
        android.content.Intent intent = new android.content.Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setData(android.net.Uri.parse("http://ccrama.me/"));
        java.util.List<android.content.pm.ResolveInfo> allApps = getPackageManager().queryIntentActivities(intent, android.content.pm.PackageManager.GET_DISABLED_COMPONENTS);
        for (android.content.pm.ResolveInfo i : allApps) {
            if (i.activityInfo.isEnabled())
                me.ccrama.redditslide.util.LogUtil.v(i.activityInfo.packageName);

        }
    }

    @java.lang.Override
    public void networkAvailable() {
        if ((runAfterLoad == null) && (me.ccrama.redditslide.Reddit.authentication != null)) {
            me.ccrama.redditslide.Authentication.resetAdapter();
        }
    }

    me.ccrama.redditslide.util.NetworkStateReceiver networkStateReceiver;

    @java.lang.Override
    public void networkUnavailable() {
    }

    public void checkClipboard() {
        try {
            android.content.ClipboardManager clipboard = ((android.content.ClipboardManager) (getSystemService(android.content.Context.CLIPBOARD_SERVICE)));
            if (clipboard.hasPrimaryClip()) {
                android.content.ClipData data = clipboard.getPrimaryClip();
                final java.lang.String s = ((java.lang.String) (data.getItemAt(0).getText()));
                if (!s.isEmpty()) {
                    if ((me.ccrama.redditslide.ContentType.getContentType(s) == me.ccrama.redditslide.ContentType.Type.REDDIT) && (!me.ccrama.redditslide.HasSeen.getSeen(s))) {
                        android.support.design.widget.Snackbar snack = android.support.design.widget.Snackbar.make(mToolbar, "Reddit link found in your clipboard", android.support.design.widget.Snackbar.LENGTH_LONG);
                        snack.setAction("OPEN", new android.view.View.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.view.View view) {
                                me.ccrama.redditslide.OpenRedditLink.openUrl(me.ccrama.redditslide.Activities.MainActivity.this, s, false);
                            }
                        });
                        snack.show();
                    }
                }
            }
        } catch (java.lang.Exception e) {
        }
    }

    @java.lang.Override
    public void onResume() {
        super.onResume();
        if ((((me.ccrama.redditslide.Authentication.isLoggedIn && me.ccrama.redditslide.Authentication.didOnline) && me.ccrama.redditslide.util.NetworkUtil.isConnected(this)) && (headerMain != null)) && (runAfterLoad == null)) {
            new me.ccrama.redditslide.Activities.MainActivity.AsyncNotificationBadge().executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (me.ccrama.redditslide.Authentication.isLoggedIn && me.ccrama.redditslide.Authentication.name.equalsIgnoreCase("loggedout")) {
            restartTheme();// force a restart because we should not be here

        }
        if (((!inNightMode) && me.ccrama.redditslide.SettingValues.isNight()) || (inNightMode && (!me.ccrama.redditslide.SettingValues.isNight()))) {
            ((android.support.v7.widget.SwitchCompat) (drawerLayout.findViewById(me.ccrama.redditslide.R.id.toggle_night_mode))).setChecked(me.ccrama.redditslide.SettingValues.isNight());
            restartTheme();
        }
        checkClipboard();
        if ((pager != null) && commentPager) {
            if ((pager.getCurrentItem() != toOpenComments) && (me.ccrama.redditslide.Activities.MainActivity.shouldLoad != null)) {
                if (((usedArray != null) && (!me.ccrama.redditslide.Activities.MainActivity.shouldLoad.contains("+"))) && (usedArray.indexOf(me.ccrama.redditslide.Activities.MainActivity.shouldLoad) != pager.getCurrentItem())) {
                    pager.setCurrentItem(toOpenComments - 1);
                }
            }
        }
        me.ccrama.redditslide.Reddit.setDefaultErrorHandler(this);
        if (sideArrayAdapter != null) {
            sideArrayAdapter.updateHistory(me.ccrama.redditslide.UserSubscriptions.getHistory());
        }
        /* remove   if (datasetChanged && UserSubscriptions.hasSubs() && !usedArray.isEmpty()) {
        usedArray = new ArrayList<>(UserSubscriptions.getSubscriptions(this));
        adapter.notifyDataSetChanged();
        sideArrayAdapter.notifyDataSetChanged();
        datasetChanged = false;
        if (mTabLayout != null) {
        mTabLayout.setupWithViewPager(pager);
        scrollToTabAfterLayout(pager.getCurrentItem());
        }
        setToolbarClick();
        }
         */
        // Only refresh the view if a Setting was altered
        if (me.ccrama.redditslide.Activities.Settings.changed || me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed) {
            reloadSubs();
            // If the user changed a Setting regarding the app's theme, restartTheme()
            /* todo maybe later || (usedArray != null && usedArray.size() != UserSubscriptions.getSubscriptions(this).size()) */
            if (me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed) {
                restartTheme();
            }
            // Need to change the subreddit search method
            if (me.ccrama.redditslide.Fragments.SettingsGeneralFragment.searchChanged) {
                setDrawerSubList();
                if (me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_DRAWER) {
                    mToolbar.setOnLongClickListener(null);// remove the long click listener from the toolbar

                    findViewById(me.ccrama.redditslide.R.id.drawer_divider).setVisibility(android.view.View.GONE);
                } else if (me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_TOOLBAR) {
                    setupSubredditSearchToolbar();
                } else if (me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_BOTH) {
                    findViewById(me.ccrama.redditslide.R.id.drawer_divider).setVisibility(android.view.View.GONE);
                    setupSubredditSearchToolbar();
                    setDrawerSubList();
                }
                me.ccrama.redditslide.Fragments.SettingsGeneralFragment.searchChanged = false;
            }
            me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = false;
            me.ccrama.redditslide.Activities.Settings.changed = false;
            setToolbarClick();
        }
    }

    @java.lang.Override
    public void onDestroy() {
        try {
            unregisterReceiver(networkStateReceiver);
        } catch (java.lang.Exception ignored) {
        }
        dismissProgressDialog();
        me.ccrama.redditslide.Activities.Slide.hasStarted = false;
        super.onDestroy();
    }

    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (event.getAction() != android.view.KeyEvent.ACTION_DOWN)
            return true;

        if (keyCode == android.view.KeyEvent.KEYCODE_SEARCH) {
            return onOptionsItemSelected(menu.findItem(me.ccrama.redditslide.R.id.search));
        }
        return super.onKeyDown(keyCode, event);
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

    public java.util.HashMap<java.lang.String, java.lang.String> accounts = new java.util.HashMap<>();

    public void doDrawer() {
        drawerSubList = ((android.widget.ListView) (findViewById(me.ccrama.redditslide.R.id.drawerlistview)));
        drawerSubList.setDividerHeight(0);
        drawerSubList.setDescendantFocusability(android.widget.ListView.FOCUS_BEFORE_DESCENDANTS);
        final android.view.LayoutInflater inflater = getLayoutInflater();
        final android.view.View header;
        if (me.ccrama.redditslide.Authentication.isLoggedIn && me.ccrama.redditslide.Authentication.didOnline) {
            header = inflater.inflate(me.ccrama.redditslide.R.layout.drawer_loggedin, drawerSubList, false);
            headerMain = header;
            hea = header.findViewById(me.ccrama.redditslide.R.id.back);
            drawerSubList.addHeaderView(header, null, false);
            ((android.widget.TextView) (header.findViewById(me.ccrama.redditslide.R.id.name))).setText(me.ccrama.redditslide.Authentication.name);
            header.findViewById(me.ccrama.redditslide.R.id.multi).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View view) {
                    if (runAfterLoad == null) {
                        android.content.Intent inte = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.MultiredditOverview.class);
                        me.ccrama.redditslide.Activities.MainActivity.this.startActivity(inte);
                    }
                }
            });
            header.findViewById(me.ccrama.redditslide.R.id.multi).setOnLongClickListener(new android.view.View.OnLongClickListener() {
                @java.lang.Override
                public boolean onLongClick(android.view.View v) {
                    new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.MainActivity.this).inputRange(3, 20).alwaysCallInputCallback().input(getString(me.ccrama.redditslide.R.string.user_enter), null, new com.afollestad.materialdialogs.MaterialDialog.InputCallback() {
                        @java.lang.Override
                        public void onInput(@android.support.annotation.NonNull
                        com.afollestad.materialdialogs.MaterialDialog dialog, java.lang.CharSequence input) {
                            final android.widget.EditText editText = dialog.getInputEditText();
                            me.ccrama.redditslide.util.EditTextValidator.validateUsername(editText);
                            if ((input.length() >= 3) && (input.length() <= 20)) {
                                dialog.getActionButton(com.afollestad.materialdialogs.DialogAction.POSITIVE).setEnabled(true);
                            }
                        }
                    }).positiveText(me.ccrama.redditslide.R.string.user_btn_gotomultis).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                        @java.lang.Override
                        public void onClick(@android.support.annotation.NonNull
                        com.afollestad.materialdialogs.MaterialDialog dialog, @android.support.annotation.NonNull
                        com.afollestad.materialdialogs.DialogAction which) {
                            if (runAfterLoad == null) {
                                android.content.Intent inte = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.MultiredditOverview.class);
                                inte.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_PROFILE, dialog.getInputEditText().getText().toString());
                                me.ccrama.redditslide.Activities.MainActivity.this.startActivity(inte);
                            }
                        }
                    }).negativeText(me.ccrama.redditslide.R.string.btn_cancel).show();
                    return true;
                }
            });
            header.findViewById(me.ccrama.redditslide.R.id.discover).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View view) {
                    android.content.Intent inte = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.Discover.class);
                    me.ccrama.redditslide.Activities.MainActivity.this.startActivity(inte);
                }
            });
            header.findViewById(me.ccrama.redditslide.R.id.prof_click).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View view) {
                    android.content.Intent inte = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.Profile.class);
                    inte.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_PROFILE, me.ccrama.redditslide.Authentication.name);
                    me.ccrama.redditslide.Activities.MainActivity.this.startActivity(inte);
                }
            });
            header.findViewById(me.ccrama.redditslide.R.id.saved).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View view) {
                    android.content.Intent inte = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.Profile.class);
                    inte.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_PROFILE, me.ccrama.redditslide.Authentication.name);
                    inte.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_SAVED, true);
                    me.ccrama.redditslide.Activities.MainActivity.this.startActivity(inte);
                }
            });
            header.findViewById(me.ccrama.redditslide.R.id.later).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View view) {
                    android.content.Intent inte = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.PostReadLater.class);
                    me.ccrama.redditslide.Activities.MainActivity.this.startActivity(inte);
                }
            });
            header.findViewById(me.ccrama.redditslide.R.id.history).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View view) {
                    android.content.Intent inte = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.Profile.class);
                    inte.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_PROFILE, me.ccrama.redditslide.Authentication.name);
                    inte.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_HISTORY, true);
                    me.ccrama.redditslide.Activities.MainActivity.this.startActivity(inte);
                }
            });
            header.findViewById(me.ccrama.redditslide.R.id.commented).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View view) {
                    android.content.Intent inte = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.Profile.class);
                    inte.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_PROFILE, me.ccrama.redditslide.Authentication.name);
                    inte.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_COMMENT, true);
                    me.ccrama.redditslide.Activities.MainActivity.this.startActivity(inte);
                }
            });
            header.findViewById(me.ccrama.redditslide.R.id.submitted).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View view) {
                    android.content.Intent inte = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.Profile.class);
                    inte.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_PROFILE, me.ccrama.redditslide.Authentication.name);
                    inte.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_SUBMIT, true);
                    me.ccrama.redditslide.Activities.MainActivity.this.startActivity(inte);
                }
            });
            header.findViewById(me.ccrama.redditslide.R.id.upvoted).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View view) {
                    android.content.Intent inte = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.Profile.class);
                    inte.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_PROFILE, me.ccrama.redditslide.Authentication.name);
                    inte.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_UPVOTE, true);
                    me.ccrama.redditslide.Activities.MainActivity.this.startActivity(inte);
                }
            });
            /**
             * If the user is a known mod, show the "Moderation" drawer item quickly to
             * stop the UI from jumping
             */
            if (((me.ccrama.redditslide.UserSubscriptions.modOf != null) && (!me.ccrama.redditslide.UserSubscriptions.modOf.isEmpty())) && me.ccrama.redditslide.Authentication.mod) {
                header.findViewById(me.ccrama.redditslide.R.id.mod).setVisibility(android.view.View.VISIBLE);
            }
            // update notification badge
            final android.widget.LinearLayout profStuff = header.findViewById(me.ccrama.redditslide.R.id.accountsarea);
            profStuff.setVisibility(android.view.View.GONE);
            findViewById(me.ccrama.redditslide.R.id.back).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    if (profStuff.getVisibility() == android.view.View.GONE) {
                        expand(profStuff);
                        header.setContentDescription(getResources().getString(me.ccrama.redditslide.R.string.btn_collapse));
                        me.ccrama.redditslide.Activities.MainActivity.flipAnimator(false, header.findViewById(me.ccrama.redditslide.R.id.headerflip)).start();
                    } else {
                        collapse(profStuff);
                        header.setContentDescription(getResources().getString(me.ccrama.redditslide.R.string.btn_expand));
                        me.ccrama.redditslide.Activities.MainActivity.flipAnimator(true, header.findViewById(me.ccrama.redditslide.R.id.headerflip)).start();
                    }
                }
            });
            for (java.lang.String s : me.ccrama.redditslide.Authentication.authentication.getStringSet("accounts", new java.util.HashSet<java.lang.String>())) {
                if (s.contains(":")) {
                    accounts.put(s.split(":")[0], s.split(":")[1]);
                } else {
                    accounts.put(s, "");
                }
            }
            final java.util.ArrayList<java.lang.String> keys = new java.util.ArrayList<>(accounts.keySet());
            final android.widget.LinearLayout accountList = header.findViewById(me.ccrama.redditslide.R.id.accountsarea);
            for (final java.lang.String accName : keys) {
                me.ccrama.redditslide.util.LogUtil.v(accName);
                final android.view.View t = getLayoutInflater().inflate(me.ccrama.redditslide.R.layout.account_textview_white, accountList, false);
                ((android.widget.TextView) (t.findViewById(me.ccrama.redditslide.R.id.name))).setText(accName);
                me.ccrama.redditslide.util.LogUtil.v("Adding click to " + ((android.widget.TextView) (t.findViewById(me.ccrama.redditslide.R.id.name))).getText());
                t.findViewById(me.ccrama.redditslide.R.id.remove).setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.MainActivity.this).setTitle(me.ccrama.redditslide.R.string.profile_remove).setMessage(me.ccrama.redditslide.R.string.profile_remove_account).setNegativeButton(me.ccrama.redditslide.R.string.btn_delete, new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialog2, int which2) {
                                java.util.Set<java.lang.String> accounts2 = me.ccrama.redditslide.Authentication.authentication.getStringSet("accounts", new java.util.HashSet<java.lang.String>());
                                java.util.Set<java.lang.String> done = new java.util.HashSet<>();
                                for (java.lang.String s : accounts2) {
                                    if (!s.contains(accName)) {
                                        done.add(s);
                                    }
                                }
                                me.ccrama.redditslide.Authentication.authentication.edit().putStringSet("accounts", done).commit();
                                dialog2.dismiss();
                                accountList.removeView(t);
                                if (accName.equalsIgnoreCase(me.ccrama.redditslide.Authentication.name)) {
                                    boolean d = false;
                                    for (java.lang.String s : keys) {
                                        if (!s.equalsIgnoreCase(accName)) {
                                            d = true;
                                            me.ccrama.redditslide.util.LogUtil.v("Switching to " + s);
                                            for (java.util.Map.Entry<java.lang.String, java.lang.String> e : accounts.entrySet()) {
                                                me.ccrama.redditslide.util.LogUtil.v((e.getKey() + ":") + e.getValue());
                                            }
                                            if (accounts.containsKey(s) && (!accounts.get(s).isEmpty())) {
                                                me.ccrama.redditslide.Authentication.authentication.edit().putString("lasttoken", accounts.get(s)).remove("backedCreds").commit();
                                            } else {
                                                java.util.ArrayList<java.lang.String> tokens = new java.util.ArrayList<>(me.ccrama.redditslide.Authentication.authentication.getStringSet("tokens", new java.util.HashSet<java.lang.String>()));
                                                int index = keys.indexOf(s);
                                                if (keys.indexOf(s) > tokens.size()) {
                                                    index -= 1;
                                                }
                                                me.ccrama.redditslide.Authentication.authentication.edit().putString("lasttoken", tokens.get(index)).remove("backedCreds").commit();
                                            }
                                            me.ccrama.redditslide.Authentication.name = s;
                                            me.ccrama.redditslide.UserSubscriptions.switchAccounts();
                                            me.ccrama.redditslide.Reddit.forceRestart(me.ccrama.redditslide.Activities.MainActivity.this, true);
                                            break;
                                        }
                                    }
                                    if (!d) {
                                        me.ccrama.redditslide.Authentication.name = "LOGGEDOUT";
                                        me.ccrama.redditslide.Authentication.isLoggedIn = false;
                                        me.ccrama.redditslide.Authentication.authentication.edit().remove("lasttoken").remove("backedCreds").commit();
                                        me.ccrama.redditslide.UserSubscriptions.switchAccounts();
                                        me.ccrama.redditslide.Reddit.forceRestart(me.ccrama.redditslide.Activities.MainActivity.this, true);
                                    }
                                } else {
                                    accounts.remove(accName);
                                    keys.remove(accName);
                                }
                            }
                        }).setPositiveButton(me.ccrama.redditslide.R.string.btn_cancel, null).show();
                    }
                });
                t.setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        java.lang.String accName = ((android.widget.TextView) (t.findViewById(me.ccrama.redditslide.R.id.name))).getText().toString();
                        me.ccrama.redditslide.util.LogUtil.v("Found name is " + accName);
                        if (!accName.equalsIgnoreCase(me.ccrama.redditslide.Authentication.name)) {
                            me.ccrama.redditslide.util.LogUtil.v("Switching to " + accName);
                            if (!accounts.get(accName).isEmpty()) {
                                me.ccrama.redditslide.util.LogUtil.v("Using token " + accounts.get(accName));
                                me.ccrama.redditslide.Authentication.authentication.edit().putString("lasttoken", accounts.get(accName)).remove("backedCreds").apply();
                            } else {
                                java.util.ArrayList<java.lang.String> tokens = new java.util.ArrayList<>(me.ccrama.redditslide.Authentication.authentication.getStringSet("tokens", new java.util.HashSet<java.lang.String>()));
                                me.ccrama.redditslide.Authentication.authentication.edit().putString("lasttoken", tokens.get(keys.indexOf(accName))).remove("backedCreds").apply();
                            }
                            me.ccrama.redditslide.Authentication.name = accName;
                            me.ccrama.redditslide.UserSubscriptions.switchAccounts();
                            me.ccrama.redditslide.Reddit.forceRestart(me.ccrama.redditslide.Activities.MainActivity.this, true);
                        }
                    }
                });
                accountList.addView(t);
            }
            header.findViewById(me.ccrama.redditslide.R.id.godown).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View view) {
                    android.widget.LinearLayout body = header.findViewById(me.ccrama.redditslide.R.id.expand_profile);
                    if (body.getVisibility() == android.view.View.GONE) {
                        expand(body);
                        me.ccrama.redditslide.Activities.MainActivity.flipAnimator(false, view).start();
                        view.findViewById(me.ccrama.redditslide.R.id.godown).setContentDescription(getResources().getString(me.ccrama.redditslide.R.string.btn_collapse));
                    } else {
                        collapse(body);
                        me.ccrama.redditslide.Activities.MainActivity.flipAnimator(true, view).start();
                        view.findViewById(me.ccrama.redditslide.R.id.godown).setContentDescription(getResources().getString(me.ccrama.redditslide.R.string.btn_expand));
                    }
                }
            });
            header.findViewById(me.ccrama.redditslide.R.id.guest_mode).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View v) {
                    me.ccrama.redditslide.Authentication.name = "LOGGEDOUT";
                    me.ccrama.redditslide.Authentication.isLoggedIn = false;
                    me.ccrama.redditslide.Authentication.authentication.edit().remove("lasttoken").remove("backedCreds").apply();
                    me.ccrama.redditslide.UserSubscriptions.switchAccounts();
                    me.ccrama.redditslide.Reddit.forceRestart(me.ccrama.redditslide.Activities.MainActivity.this, true);
                }
            });
            header.findViewById(me.ccrama.redditslide.R.id.add).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View view) {
                    android.content.Intent inte = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.Login.class);
                    me.ccrama.redditslide.Activities.MainActivity.this.startActivity(inte);
                }
            });
            header.findViewById(me.ccrama.redditslide.R.id.offline).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View view) {
                    me.ccrama.redditslide.Reddit.appRestart.edit().putBoolean("forceoffline", true).commit();
                    me.ccrama.redditslide.Reddit.forceRestart(me.ccrama.redditslide.Activities.MainActivity.this, false);
                }
            });
            header.findViewById(me.ccrama.redditslide.R.id.inbox).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View view) {
                    android.content.Intent inte = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.Inbox.class);
                    me.ccrama.redditslide.Activities.MainActivity.this.startActivityForResult(inte, me.ccrama.redditslide.Activities.MainActivity.INBOX_RESULT);
                }
            });
            headerMain = header;
            if (runAfterLoad == null) {
                new me.ccrama.redditslide.Activities.MainActivity.AsyncNotificationBadge().executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } else if (me.ccrama.redditslide.Authentication.didOnline) {
            header = inflater.inflate(me.ccrama.redditslide.R.layout.drawer_loggedout, drawerSubList, false);
            drawerSubList.addHeaderView(header, null, false);
            headerMain = header;
            hea = header.findViewById(me.ccrama.redditslide.R.id.back);
            final android.widget.LinearLayout profStuff = header.findViewById(me.ccrama.redditslide.R.id.accountsarea);
            profStuff.setVisibility(android.view.View.GONE);
            findViewById(me.ccrama.redditslide.R.id.back).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    if (profStuff.getVisibility() == android.view.View.GONE) {
                        expand(profStuff);
                        me.ccrama.redditslide.Activities.MainActivity.flipAnimator(false, header.findViewById(me.ccrama.redditslide.R.id.headerflip)).start();
                        header.findViewById(me.ccrama.redditslide.R.id.headerflip).setContentDescription(getResources().getString(me.ccrama.redditslide.R.string.btn_collapse));
                    } else {
                        collapse(profStuff);
                        me.ccrama.redditslide.Activities.MainActivity.flipAnimator(true, header.findViewById(me.ccrama.redditslide.R.id.headerflip)).start();
                        header.findViewById(me.ccrama.redditslide.R.id.headerflip).setContentDescription(getResources().getString(me.ccrama.redditslide.R.string.btn_expand));
                    }
                }
            });
            final java.util.HashMap<java.lang.String, java.lang.String> accounts = new java.util.HashMap<>();
            for (java.lang.String s : me.ccrama.redditslide.Authentication.authentication.getStringSet("accounts", new java.util.HashSet<java.lang.String>())) {
                if (s.contains(":")) {
                    accounts.put(s.split(":")[0], s.split(":")[1]);
                } else {
                    accounts.put(s, "");
                }
            }
            final java.util.ArrayList<java.lang.String> keys = new java.util.ArrayList<>(accounts.keySet());
            final android.widget.LinearLayout accountList = header.findViewById(me.ccrama.redditslide.R.id.accountsarea);
            for (final java.lang.String accName : keys) {
                me.ccrama.redditslide.util.LogUtil.v(accName);
                final android.view.View t = getLayoutInflater().inflate(me.ccrama.redditslide.R.layout.account_textview_white, accountList, false);
                ((android.widget.TextView) (t.findViewById(me.ccrama.redditslide.R.id.name))).setText(accName);
                t.findViewById(me.ccrama.redditslide.R.id.remove).setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.MainActivity.this).setTitle(me.ccrama.redditslide.R.string.profile_remove).setMessage(me.ccrama.redditslide.R.string.profile_remove_account).setNegativeButton(me.ccrama.redditslide.R.string.btn_delete, new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialog2, int which2) {
                                java.util.Set<java.lang.String> accounts2 = me.ccrama.redditslide.Authentication.authentication.getStringSet("accounts", new java.util.HashSet<java.lang.String>());
                                java.util.Set<java.lang.String> done = new java.util.HashSet<>();
                                for (java.lang.String s : accounts2) {
                                    if (!s.contains(accName)) {
                                        done.add(s);
                                    }
                                }
                                me.ccrama.redditslide.Authentication.authentication.edit().putStringSet("accounts", done).commit();
                                dialog2.dismiss();
                                accountList.removeView(t);
                                if (accName.equalsIgnoreCase(me.ccrama.redditslide.Authentication.name)) {
                                    boolean d = false;
                                    for (java.lang.String s : keys) {
                                        if (!s.equalsIgnoreCase(accName)) {
                                            d = true;
                                            me.ccrama.redditslide.util.LogUtil.v("Switching to " + s);
                                            if (!accounts.get(s).isEmpty()) {
                                                me.ccrama.redditslide.Authentication.authentication.edit().putString("lasttoken", accounts.get(s)).remove("backedCreds").commit();
                                            } else {
                                                java.util.ArrayList<java.lang.String> tokens = new java.util.ArrayList<>(me.ccrama.redditslide.Authentication.authentication.getStringSet("tokens", new java.util.HashSet<java.lang.String>()));
                                                me.ccrama.redditslide.Authentication.authentication.edit().putString("lasttoken", tokens.get(keys.indexOf(s))).remove("backedCreds").commit();
                                            }
                                            me.ccrama.redditslide.Authentication.name = s;
                                            me.ccrama.redditslide.UserSubscriptions.switchAccounts();
                                            me.ccrama.redditslide.Reddit.forceRestart(me.ccrama.redditslide.Activities.MainActivity.this, true);
                                        }
                                    }
                                    if (!d) {
                                        me.ccrama.redditslide.Authentication.name = "LOGGEDOUT";
                                        me.ccrama.redditslide.Authentication.isLoggedIn = false;
                                        me.ccrama.redditslide.Authentication.authentication.edit().remove("lasttoken").remove("backedCreds").commit();
                                        me.ccrama.redditslide.UserSubscriptions.switchAccounts();
                                        me.ccrama.redditslide.Reddit.forceRestart(me.ccrama.redditslide.Activities.MainActivity.this, true);
                                    }
                                } else {
                                    accounts.remove(accName);
                                    keys.remove(accName);
                                }
                            }
                        }).setPositiveButton(me.ccrama.redditslide.R.string.btn_cancel, null).show();
                    }
                });
                t.setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                    @java.lang.Override
                    public void onSingleClick(android.view.View v) {
                        if (!accName.equalsIgnoreCase(me.ccrama.redditslide.Authentication.name)) {
                            if (!accounts.get(accName).isEmpty()) {
                                me.ccrama.redditslide.Authentication.authentication.edit().putString("lasttoken", accounts.get(accName)).remove("backedCreds").commit();
                            } else {
                                java.util.ArrayList<java.lang.String> tokens = new java.util.ArrayList<>(me.ccrama.redditslide.Authentication.authentication.getStringSet("tokens", new java.util.HashSet<java.lang.String>()));
                                me.ccrama.redditslide.Authentication.authentication.edit().putString("lasttoken", tokens.get(keys.indexOf(accName))).remove("backedCreds").commit();
                            }
                            me.ccrama.redditslide.Authentication.isLoggedIn = true;
                            me.ccrama.redditslide.Authentication.name = accName;
                            me.ccrama.redditslide.UserSubscriptions.switchAccounts();
                            me.ccrama.redditslide.Reddit.forceRestart(me.ccrama.redditslide.Activities.MainActivity.this, true);
                        }
                    }
                });
                accountList.addView(t);
            }
            header.findViewById(me.ccrama.redditslide.R.id.add).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View view) {
                    android.content.Intent inte = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.Login.class);
                    me.ccrama.redditslide.Activities.MainActivity.this.startActivity(inte);
                }
            });
            header.findViewById(me.ccrama.redditslide.R.id.offline).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View view) {
                    me.ccrama.redditslide.Reddit.appRestart.edit().putBoolean("forceoffline", true).commit();
                    me.ccrama.redditslide.Reddit.forceRestart(me.ccrama.redditslide.Activities.MainActivity.this, false);
                }
            });
            headerMain = header;
            header.findViewById(me.ccrama.redditslide.R.id.multi).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View view) {
                    new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.MainActivity.this).inputRange(3, 20).alwaysCallInputCallback().input(getString(me.ccrama.redditslide.R.string.user_enter), null, new com.afollestad.materialdialogs.MaterialDialog.InputCallback() {
                        @java.lang.Override
                        public void onInput(@android.support.annotation.NonNull
                        com.afollestad.materialdialogs.MaterialDialog dialog, java.lang.CharSequence input) {
                            final android.widget.EditText editText = dialog.getInputEditText();
                            me.ccrama.redditslide.util.EditTextValidator.validateUsername(editText);
                            if ((input.length() >= 3) && (input.length() <= 20)) {
                                dialog.getActionButton(com.afollestad.materialdialogs.DialogAction.POSITIVE).setEnabled(true);
                            }
                        }
                    }).positiveText(me.ccrama.redditslide.R.string.user_btn_gotomultis).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                        @java.lang.Override
                        public void onClick(@android.support.annotation.NonNull
                        com.afollestad.materialdialogs.MaterialDialog dialog, @android.support.annotation.NonNull
                        com.afollestad.materialdialogs.DialogAction which) {
                            if (runAfterLoad == null) {
                                android.content.Intent inte = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.MultiredditOverview.class);
                                inte.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_PROFILE, dialog.getInputEditText().getText().toString());
                                me.ccrama.redditslide.Activities.MainActivity.this.startActivity(inte);
                            }
                        }
                    }).negativeText(me.ccrama.redditslide.R.string.btn_cancel).show();
                }
            });
        } else {
            header = inflater.inflate(me.ccrama.redditslide.R.layout.drawer_offline, drawerSubList, false);
            headerMain = header;
            drawerSubList.addHeaderView(header, null, false);
            hea = header.findViewById(me.ccrama.redditslide.R.id.back);
            header.findViewById(me.ccrama.redditslide.R.id.online).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View view) {
                    me.ccrama.redditslide.Reddit.appRestart.edit().remove("forceoffline").commit();
                    me.ccrama.redditslide.Reddit.forceRestart(me.ccrama.redditslide.Activities.MainActivity.this, false);
                }
            });
        }
        final android.widget.LinearLayout expandSettings = header.findViewById(me.ccrama.redditslide.R.id.expand_settings);
        header.findViewById(me.ccrama.redditslide.R.id.godown_settings).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                if (expandSettings.getVisibility() == android.view.View.GONE) {
                    expand(expandSettings);
                    header.findViewById(me.ccrama.redditslide.R.id.godown_settings).setContentDescription(getResources().getString(me.ccrama.redditslide.R.string.btn_collapse));
                    me.ccrama.redditslide.Activities.MainActivity.flipAnimator(false, v).start();
                } else {
                    collapse(expandSettings);
                    header.findViewById(me.ccrama.redditslide.R.id.godown_settings).setContentDescription(getResources().getString(me.ccrama.redditslide.R.string.btn_expand));
                    me.ccrama.redditslide.Activities.MainActivity.flipAnimator(true, v).start();
                }
            }
        });
        {
            // Set up quick setting toggles
            final android.support.v7.widget.SwitchCompat toggleNightMode = expandSettings.findViewById(me.ccrama.redditslide.R.id.toggle_night_mode);
            if (me.ccrama.redditslide.SettingValues.isPro) {
                toggleNightMode.setVisibility(android.view.View.VISIBLE);
                toggleNightMode.setChecked(inNightMode);
                toggleNightMode.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                    @java.lang.Override
                    public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                        me.ccrama.redditslide.SettingValues.forcedNightModeState = (isChecked) ? me.ccrama.redditslide.SettingValues.ForcedState.FORCED_ON : me.ccrama.redditslide.SettingValues.ForcedState.FORCED_OFF;
                        restartTheme();
                    }
                });
            }
            final android.support.v7.widget.SwitchCompat toggleImmersiveMode = expandSettings.findViewById(me.ccrama.redditslide.R.id.toggle_immersive_mode);
            toggleImmersiveMode.setChecked(me.ccrama.redditslide.SettingValues.immersiveMode);
            toggleImmersiveMode.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.immersiveMode = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_IMMERSIVE_MODE, isChecked).apply();
                    if (isChecked) {
                        hideDecor();
                    } else {
                        showDecor();
                    }
                }
            });
            final android.support.v7.widget.SwitchCompat toggleNSFW = expandSettings.findViewById(me.ccrama.redditslide.R.id.toggle_nsfw);
            toggleNSFW.setChecked(me.ccrama.redditslide.SettingValues.showNSFWContent);
            toggleNSFW.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.showNSFWContent = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_SHOW_NSFW_CONTENT, isChecked).apply();
                    reloadSubs();
                }
            });
            final android.support.v7.widget.SwitchCompat toggleRightThumbnails = expandSettings.findViewById(me.ccrama.redditslide.R.id.toggle_right_thumbnails);
            toggleRightThumbnails.setChecked(me.ccrama.redditslide.SettingValues.switchThumb);
            toggleRightThumbnails.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.switchThumb = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_SWITCH_THUMB, isChecked).apply();
                    reloadSubs();
                }
            });
            final android.support.v7.widget.SwitchCompat toggleReaderMode = expandSettings.findViewById(me.ccrama.redditslide.R.id.toggle_reader_mode);
            toggleReaderMode.setChecked(me.ccrama.redditslide.SettingValues.readerMode);
            toggleReaderMode.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.readerMode = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_READER_MODE, isChecked).apply();
                }
            });
        }
        header.findViewById(me.ccrama.redditslide.R.id.manage).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
            @java.lang.Override
            public void onSingleClick(android.view.View view) {
                android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.ManageOfflineContent.class);
                startActivity(i);
            }
        });
        if (me.ccrama.redditslide.Authentication.didOnline) {
            android.view.View support = header.findViewById(me.ccrama.redditslide.R.id.support);
            if (me.ccrama.redditslide.SettingValues.isPro) {
                support.setVisibility(android.view.View.GONE);
            } else {
                header.findViewById(me.ccrama.redditslide.R.id.support).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                    @java.lang.Override
                    public void onSingleClick(android.view.View view) {
                        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.MainActivity.this).setTitle(me.ccrama.redditslide.R.string.settings_support_slide).setMessage(me.ccrama.redditslide.R.string.pro_upgrade_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes_exclaim, new android.content.DialogInterface.OnClickListener() {
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
            header.findViewById(me.ccrama.redditslide.R.id.prof).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View view) {
                    new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.MainActivity.this).inputRange(3, 20).alwaysCallInputCallback().input(getString(me.ccrama.redditslide.R.string.user_enter), null, new com.afollestad.materialdialogs.MaterialDialog.InputCallback() {
                        @java.lang.Override
                        public void onInput(@android.support.annotation.NonNull
                        com.afollestad.materialdialogs.MaterialDialog dialog, java.lang.CharSequence input) {
                            final android.widget.EditText editText = dialog.getInputEditText();
                            me.ccrama.redditslide.util.EditTextValidator.validateUsername(editText);
                            if ((input.length() >= 3) && (input.length() <= 20)) {
                                dialog.getActionButton(com.afollestad.materialdialogs.DialogAction.POSITIVE).setEnabled(true);
                            }
                        }
                    }).positiveText(me.ccrama.redditslide.R.string.user_btn_goto).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                        @java.lang.Override
                        public void onClick(@android.support.annotation.NonNull
                        com.afollestad.materialdialogs.MaterialDialog dialog, @android.support.annotation.NonNull
                        com.afollestad.materialdialogs.DialogAction which) {
                            android.content.Intent inte = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.Profile.class);
                            // noinspection ConstantConditions
                            inte.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_PROFILE, dialog.getInputEditText().getText().toString());
                            me.ccrama.redditslide.Activities.MainActivity.this.startActivity(inte);
                        }
                    }).negativeText(me.ccrama.redditslide.R.string.btn_cancel).show();
                }
            });
        }
        header.findViewById(me.ccrama.redditslide.R.id.settings).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
            @java.lang.Override
            public void onSingleClick(android.view.View v) {
                android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.Settings.class);
                startActivity(i);
                // Cancel sub loading because exiting the settings will reload it anyway
                if (mAsyncGetSubreddit != null)
                    mAsyncGetSubreddit.cancel(true);

                drawerLayout.closeDrawers();
            }
        });
        /* footer.findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        Intent inte = new Intent(Overview.this, Setting.class);
        Overview.this.startActivityForResult(inte, 3);
        }
        });
         */
        final android.support.v7.widget.Toolbar toolbar = ((android.support.v7.widget.Toolbar) (findViewById(me.ccrama.redditslide.R.id.toolbar)));
        final android.support.v7.app.ActionBarDrawerToggle actionBarDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this, drawerLayout, toolbar, me.ccrama.redditslide.R.string.btn_open, me.ccrama.redditslide.R.string.btn_close) {
            @java.lang.Override
            public void onDrawerSlide(android.view.View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, 0);// this disables the animation

            }

            @java.lang.Override
            public void onDrawerOpened(android.view.View drawerView) {
                super.onDrawerOpened(drawerView);
                if (drawerLayout.isDrawerOpen(android.support.v4.view.GravityCompat.END)) {
                    int current = pager.getCurrentItem();
                    if ((current == toOpenComments) && (toOpenComments != 0)) {
                        current -= 1;
                    }
                    java.lang.String compare = usedArray.get(current);
                    if ((compare.equals("random") || compare.equals("myrandom")) || compare.equals("randnsfw")) {
                        if (((adapter != null) && (adapter.getCurrentFragment() != null)) && (((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).adapter.dataSet.subredditRandom != null)) {
                            java.lang.String sub = ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).adapter.dataSet.subredditRandom;
                            doSubSidebarNoLoad(sub);
                            doSubSidebar(sub);
                        }
                    } else {
                        doSubSidebar(usedArray.get(current));
                    }
                }
            }

            @java.lang.Override
            public void onDrawerClosed(android.view.View view) {
                super.onDrawerClosed(view);
                android.view.inputmethod.InputMethodManager imm = ((android.view.inputmethod.InputMethodManager) (getSystemService(android.content.Context.INPUT_METHOD_SERVICE)));
                imm.hideSoftInputFromWindow(drawerLayout.getWindowToken(), 0);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        header.findViewById(me.ccrama.redditslide.R.id.back).setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getColor("alsdkfjasld"));
        accountsArea = header.findViewById(me.ccrama.redditslide.R.id.accountsarea);
        if (accountsArea != null) {
            accountsArea.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getDarkerColor("alsdkfjasld"));
        }
        setDrawerSubList();
        hideDrawerItems();
    }

    public void hideDrawerItems() {
        for (me.ccrama.redditslide.Fragments.DrawerItemsDialog.SettingsDrawerEnum settingDrawerItem : me.ccrama.redditslide.Fragments.DrawerItemsDialog.SettingsDrawerEnum.values()) {
            android.view.View drawerItem = drawerSubList.findViewById(settingDrawerItem.drawerId);
            if (((drawerItem != null) && (drawerItem.getVisibility() == android.view.View.VISIBLE)) && ((me.ccrama.redditslide.SettingValues.selectedDrawerItems & settingDrawerItem.value) == 0)) {
                drawerItem.setVisibility(android.view.View.GONE);
            }
        }
    }

    public void doForcePrefs() {
        java.util.HashSet<java.lang.String> domains = new java.util.HashSet<>();
        for (java.lang.String s : me.ccrama.redditslide.SettingValues.alwaysExternal) {
            if (!s.isEmpty()) {
                s = s.trim();
                final java.lang.String finalS = s;
                if (!finalS.contains("youtu"))
                    domains.add(finalS);

            }
        }
        // Make some domains open externally by default, can be used with Chrome Customtabs if they remove the option in settings
        domains.add("youtube.com");
        domains.add("youtu.be");
        domains.add("play.google.com");
        me.ccrama.redditslide.SettingValues.prefs.edit().putStringSet(me.ccrama.redditslide.SettingValues.PREF_ALWAYS_EXTERNAL, domains).apply();
        me.ccrama.redditslide.SettingValues.alwaysExternal = domains;
    }

    public void doFriends(final java.util.List<java.lang.String> friends) {
        runOnUiThread(new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                if (((friends != null) && (!friends.isEmpty())) && (headerMain.findViewById(me.ccrama.redditslide.R.id.friends) != null)) {
                    headerMain.findViewById(me.ccrama.redditslide.R.id.friends).setVisibility(android.view.View.VISIBLE);
                    headerMain.findViewById(me.ccrama.redditslide.R.id.friends).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                        @java.lang.Override
                        public void onSingleClick(android.view.View view) {
                            new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.MainActivity.this).title("Friends").items(friends).itemsCallback(new com.afollestad.materialdialogs.MaterialDialog.ListCallback() {
                                @java.lang.Override
                                public void onSelection(com.afollestad.materialdialogs.MaterialDialog dialog, android.view.View itemView, int which, java.lang.CharSequence text) {
                                    android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.Profile.class);
                                    i.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_PROFILE, friends.get(which));
                                    startActivity(i);
                                    dialog.dismiss();
                                }
                            }).show();
                        }
                    });
                } else if (me.ccrama.redditslide.Authentication.isLoggedIn && (headerMain.findViewById(me.ccrama.redditslide.R.id.friends) != null)) {
                    headerMain.findViewById(me.ccrama.redditslide.R.id.friends).setVisibility(android.view.View.GONE);
                }
            }
        });
    }

    public void doPageSelectedComments(int position) {
        pager.setSwipeLeftOnly(false);
        header.animate().translationY(0).setInterpolator(new android.view.animation.LinearInterpolator()).setDuration(180);
        me.ccrama.redditslide.Reddit.currentPosition = position;
        if ((position + 1) != currentComment) {
            doSubSidebarNoLoad(usedArray.get(position));
        }
        me.ccrama.redditslide.Fragments.SubmissionsView page = ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment()));
        if ((page != null) && (page.adapter != null)) {
            me.ccrama.redditslide.Adapters.SubredditPosts p = page.adapter.dataSet;
            if (p.offline && (p.cached != null)) {
                android.widget.Toast.makeText(this, getString(me.ccrama.redditslide.R.string.offline_last_update, me.ccrama.redditslide.TimeUtils.getTimeAgo(p.cached.time, this)), android.widget.Toast.LENGTH_LONG).show();
            }
        }
        if (hea != null) {
            hea.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getColor(usedArray.get(position)));
            if (accountsArea != null) {
                accountsArea.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(usedArray.get(position)));
            }
        }
        header.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getColor(usedArray.get(position)));
        themeSystemBars(usedArray.get(position));
        setRecentBar(usedArray.get(position));
        if (me.ccrama.redditslide.SettingValues.single) {
            getSupportActionBar().setTitle(usedArray.get(position));
        } else if (mTabLayout != null) {
            mTabLayout.setSelectedTabIndicatorColor(new me.ccrama.redditslide.ColorPreferences(this).getColor(usedArray.get(position)));
        }
        selectedSub = usedArray.get(position);
    }

    public void doSubOnlyStuff(final net.dean.jraw.models.Subreddit subreddit) {
        findViewById(me.ccrama.redditslide.R.id.loader).setVisibility(android.view.View.GONE);
        if (subreddit.getSubredditType() != null) {
            canSubmit = !subreddit.getSubredditType().equals("RESTRICTED");
        } else {
            canSubmit = true;
        }
        if ((subreddit.getSidebar() != null) && (!subreddit.getSidebar().isEmpty())) {
            findViewById(me.ccrama.redditslide.R.id.sidebar_text).setVisibility(android.view.View.VISIBLE);
            final java.lang.String text = subreddit.getDataNode().get("description_html").asText().trim();
            setViews(text, subreddit.getDisplayName(), sidebarBody, sidebarOverflow);
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
        {
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
                                    me.ccrama.redditslide.UserSubscriptions.syncMultiReddits(me.ccrama.redditslide.Activities.MainActivity.this);
                                }
                                for (net.dean.jraw.models.MultiReddit r : me.ccrama.redditslide.UserSubscriptions.multireddits) {
                                    multis.put(r.getDisplayName(), r);
                                }
                                return null;
                            }

                            @java.lang.Override
                            protected void onPostExecute(java.lang.Void aVoid) {
                                new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.MainActivity.this).title(getString(me.ccrama.redditslide.R.string.multi_add_to, subreddit.getDisplayName())).items(multis.keySet()).itemsCallback(new com.afollestad.materialdialogs.MaterialDialog.ListCallback() {
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
                                                    me.ccrama.redditslide.UserSubscriptions.syncMultiReddits(me.ccrama.redditslide.Activities.MainActivity.this);
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
                            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.MainActivity.this).setTitle(getString(me.ccrama.redditslide.R.string.sub_post_notifs_title, sub)).setMessage(me.ccrama.redditslide.R.string.sub_post_notifs_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                                @java.lang.Override
                                public void onClick(android.content.DialogInterface dialog, int which) {
                                    new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.MainActivity.this).title(me.ccrama.redditslide.R.string.sub_post_notifs_threshold).items(new java.lang.String[]{ "1", "5", "10", "20", "40", "50" }).alwaysCallSingleChoiceCallback().itemsCallbackSingleChoice(0, new com.afollestad.materialdialogs.MaterialDialog.ListCallbackSingleChoice() {
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
                            android.widget.Toast.makeText(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.R.string.sub_post_notifs_err, android.widget.Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        android.content.Intent cancelIntent = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.CancelSubNotifs.class);
                        cancelIntent.putExtra(me.ccrama.redditslide.Activities.CancelSubNotifs.EXTRA_SUB, subreddit.getDisplayName());
                        startActivity(cancelIntent);
                    }
                }
            });
        }
        {
            final android.widget.TextView subscribe = ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.subscribe)));
            currentlySubbed = ((!me.ccrama.redditslide.Authentication.isLoggedIn) && usedArray.contains(subreddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH))) || subreddit.isUserSubscriber();
            doSubscribeButtonText(currentlySubbed, subscribe);
            assert subscribe != null;
            subscribe.setOnClickListener(new android.view.View.OnClickListener() {
                private void doSubscribe() {
                    if (me.ccrama.redditslide.Authentication.isLoggedIn) {
                        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.MainActivity.this).setTitle(getString(me.ccrama.redditslide.R.string.subscribe_to, subreddit.getDisplayName())).setPositiveButton(me.ccrama.redditslide.R.string.reorder_add_subscribe, new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
                                    @java.lang.Override
                                    public void onPostExecute(java.lang.Boolean success) {
                                        if (!success) {
                                            // If subreddit was removed from account or not
                                            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.MainActivity.this).setTitle(me.ccrama.redditslide.R.string.force_change_subscription).setMessage(me.ccrama.redditslide.R.string.force_change_subscription_desc).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                                                @java.lang.Override
                                                public void onClick(android.content.DialogInterface dialog, int which) {
                                                    changeSubscription(subreddit, true);// Force add the subscription

                                                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(mToolbar, getString(me.ccrama.redditslide.R.string.misc_subscribed), android.support.design.widget.Snackbar.LENGTH_LONG);
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
                        }).setNeutralButton(me.ccrama.redditslide.R.string.btn_add_to_sublist, new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                changeSubscription(subreddit, true);// Force add the subscription

                                android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(mToolbar, me.ccrama.redditslide.R.string.sub_added, android.support.design.widget.Snackbar.LENGTH_LONG);
                                android.view.View view = s.getView();
                                android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                                tv.setTextColor(android.graphics.Color.WHITE);
                                s.show();
                            }
                        }).setNegativeButton(me.ccrama.redditslide.R.string.btn_cancel, null).show();
                    } else {
                        changeSubscription(subreddit, true);
                    }
                }

                private void doUnsubscribe() {
                    if (me.ccrama.redditslide.Authentication.didOnline) {
                        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.MainActivity.this).setTitle(getString(me.ccrama.redditslide.R.string.unsubscribe_from, subreddit.getDisplayName())).setPositiveButton(me.ccrama.redditslide.R.string.reorder_remove_unsubsribe, new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
                                    @java.lang.Override
                                    public void onPostExecute(java.lang.Boolean success) {
                                        if (!success) {
                                            // If subreddit was removed from account or not
                                            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.MainActivity.this).setTitle(me.ccrama.redditslide.R.string.force_change_subscription).setMessage(me.ccrama.redditslide.R.string.force_change_subscription_desc).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                                                @java.lang.Override
                                                public void onClick(android.content.DialogInterface dialog, int which) {
                                                    changeSubscription(subreddit, false);// Force add the subscription

                                                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(mToolbar, getString(me.ccrama.redditslide.R.string.misc_unsubscribed), android.support.design.widget.Snackbar.LENGTH_LONG);
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

                                android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(mToolbar, me.ccrama.redditslide.R.string.misc_unsubscribed, android.support.design.widget.Snackbar.LENGTH_LONG);
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
            });
        }
        if (!subreddit.getPublicDescription().isEmpty()) {
            findViewById(me.ccrama.redditslide.R.id.sub_title).setVisibility(android.view.View.VISIBLE);
            setViews(subreddit.getDataNode().get("public_description_html").asText(), subreddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), ((me.ccrama.redditslide.SpoilerRobotoTextView) (findViewById(me.ccrama.redditslide.R.id.sub_title))), ((me.ccrama.redditslide.Views.CommentOverflow) (findViewById(me.ccrama.redditslide.R.id.sub_title_overflow))));
        } else {
            findViewById(me.ccrama.redditslide.R.id.sub_title).setVisibility(android.view.View.GONE);
        }
        ((android.widget.ImageView) (findViewById(me.ccrama.redditslide.R.id.subimage))).setImageResource(0);
        if (subreddit.getDataNode().has("icon_img") && (!subreddit.getDataNode().get("icon_img").asText().isEmpty())) {
            findViewById(me.ccrama.redditslide.R.id.subimage).setVisibility(android.view.View.VISIBLE);
            ((me.ccrama.redditslide.Reddit) (getApplication())).getImageLoader().displayImage(subreddit.getDataNode().get("icon_img").asText(), ((android.widget.ImageView) (findViewById(me.ccrama.redditslide.R.id.subimage))));
        } else {
            findViewById(me.ccrama.redditslide.R.id.subimage).setVisibility(android.view.View.GONE);
        }
        ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.subscribers))).setText(getString(me.ccrama.redditslide.R.string.subreddit_subscribers_string, subreddit.getLocalizedSubscriberCount()));
        findViewById(me.ccrama.redditslide.R.id.subscribers).setVisibility(android.view.View.VISIBLE);
        ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.active_users))).setText(getString(me.ccrama.redditslide.R.string.subreddit_active_users_string_new, subreddit.getLocalizedAccountsActive()));
        findViewById(me.ccrama.redditslide.R.id.active_users).setVisibility(android.view.View.VISIBLE);
    }

    net.dean.jraw.paginators.Sorting sorts;

    public void doSubSidebar(final java.lang.String subreddit) {
        if (mAsyncGetSubreddit != null) {
            mAsyncGetSubreddit.cancel(true);
        }
        findViewById(me.ccrama.redditslide.R.id.loader).setVisibility(android.view.View.VISIBLE);
        invalidateOptionsMenu();
        if (((((((!subreddit.equalsIgnoreCase("all")) && (!subreddit.equalsIgnoreCase("frontpage"))) && (!subreddit.equalsIgnoreCase("friends"))) && (!subreddit.equalsIgnoreCase("mod"))) && (!subreddit.contains("+"))) && (!subreddit.contains("."))) && (!subreddit.contains("/m/"))) {
            if (drawerLayout != null) {
                drawerLayout.setDrawerLockMode(android.support.v4.widget.DrawerLayout.LOCK_MODE_UNLOCKED, android.support.v4.view.GravityCompat.END);
            }
            mAsyncGetSubreddit = new me.ccrama.redditslide.Activities.MainActivity.AsyncGetSubreddit();
            mAsyncGetSubreddit.execute(subreddit);
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
                        android.content.Intent inte = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.Submit.class);
                        if ((!subreddit.contains("/m/")) && canSubmit) {
                            inte.putExtra(me.ccrama.redditslide.Activities.Submit.EXTRA_SUBREDDIT, subreddit);
                        }
                        me.ccrama.redditslide.Activities.MainActivity.this.startActivity(inte);
                    }
                });
            }
            dialoglayout.findViewById(me.ccrama.redditslide.R.id.wiki).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.Wiki.class);
                    i.putExtra(me.ccrama.redditslide.Activities.Wiki.EXTRA_SUBREDDIT, subreddit);
                    startActivity(i);
                }
            });
            dialoglayout.findViewById(me.ccrama.redditslide.R.id.syncflair).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    me.ccrama.redditslide.ImageFlairs.syncFlairs(me.ccrama.redditslide.Activities.MainActivity.this, subreddit);
                }
            });
            dialoglayout.findViewById(me.ccrama.redditslide.R.id.submit).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.Submit.class);
                    if (((!subreddit.contains("/m/")) || (!subreddit.contains("."))) && canSubmit) {
                        i.putExtra(me.ccrama.redditslide.Activities.Submit.EXTRA_SUBREDDIT, subreddit);
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
                    com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.MainActivity.this);
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
                    int style = new me.ccrama.redditslide.ColorPreferences(me.ccrama.redditslide.Activities.MainActivity.this).getThemeSubreddit(subreddit);
                    final android.content.Context contextThemeWrapper = new android.support.v7.view.ContextThemeWrapper(me.ccrama.redditslide.Activities.MainActivity.this, style);
                    android.view.LayoutInflater localInflater = getLayoutInflater().cloneInContext(contextThemeWrapper);
                    final android.view.View dialoglayout = localInflater.inflate(me.ccrama.redditslide.R.layout.colorsub, null);
                    java.util.ArrayList<java.lang.String> arrayList = new java.util.ArrayList<>();
                    arrayList.add(subreddit);
                    me.ccrama.redditslide.Adapters.SettingsSubAdapter.showSubThemeEditor(arrayList, me.ccrama.redditslide.Activities.MainActivity.this, dialoglayout);
                }
            });
            dialoglayout.findViewById(me.ccrama.redditslide.R.id.mods).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    final android.app.Dialog d = new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.MainActivity.this).title(me.ccrama.redditslide.R.string.sidebar_findingmods).cancelable(true).content(me.ccrama.redditslide.R.string.misc_please_wait).progress(true, 100).show();
                    new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
                        java.util.ArrayList<net.dean.jraw.models.UserRecord> mods;

                        @java.lang.Override
                        protected java.lang.Void doInBackground(java.lang.Void... params) {
                            mods = new java.util.ArrayList<>();
                            net.dean.jraw.paginators.UserRecordPaginator paginator = new net.dean.jraw.paginators.UserRecordPaginator(me.ccrama.redditslide.Authentication.reddit, subreddit, "moderators");
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
                            new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.MainActivity.this).title(getString(me.ccrama.redditslide.R.string.sidebar_submods, subreddit)).items(names).itemsCallback(new com.afollestad.materialdialogs.MaterialDialog.ListCallback() {
                                @java.lang.Override
                                public void onSelection(com.afollestad.materialdialogs.MaterialDialog dialog, android.view.View itemView, int which, java.lang.CharSequence text) {
                                    android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.Profile.class);
                                    i.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_PROFILE, names.get(which));
                                    startActivity(i);
                                }
                            }).positiveText(me.ccrama.redditslide.R.string.btn_message).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                                @java.lang.Override
                                public void onClick(@android.support.annotation.NonNull
                                com.afollestad.materialdialogs.MaterialDialog dialog, @android.support.annotation.NonNull
                                com.afollestad.materialdialogs.DialogAction which) {
                                    android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.SendMessage.class);
                                    i.putExtra(me.ccrama.redditslide.Activities.SendMessage.EXTRA_NAME, "/r/" + subreddit);
                                    startActivity(i);
                                }
                            }).show();
                        }
                    }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                }
            });
            dialoglayout.findViewById(me.ccrama.redditslide.R.id.flair).setVisibility(android.view.View.GONE);
            if (me.ccrama.redditslide.Authentication.didOnline && me.ccrama.redditslide.Authentication.isLoggedIn) {
                if (currentFlair != null)
                    currentFlair.cancel(true);

                currentFlair = new android.os.AsyncTask<android.view.View, java.lang.Void, android.view.View>() {
                    java.util.List<net.dean.jraw.models.FlairTemplate> flairs;

                    java.util.ArrayList<java.lang.String> flairText;

                    java.lang.String current;

                    net.dean.jraw.managers.AccountManager m;

                    @java.lang.Override
                    protected android.view.View doInBackground(android.view.View... params) {
                        try {
                            m = new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit);
                            com.fasterxml.jackson.databind.JsonNode node = m.getFlairChoicesRootNode(subreddit, null);
                            flairs = m.getFlairChoices(subreddit, node);
                            net.dean.jraw.models.FlairTemplate currentF = m.getCurrentFlair(subreddit, node);
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
                                    new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.MainActivity.this).items(flairText).title(me.ccrama.redditslide.R.string.sidebar_select_flair).itemsCallback(new com.afollestad.materialdialogs.MaterialDialog.ListCallback() {
                                        @java.lang.Override
                                        public void onSelection(com.afollestad.materialdialogs.MaterialDialog dialog, android.view.View itemView, int which, java.lang.CharSequence text) {
                                            final net.dean.jraw.models.FlairTemplate t = flairs.get(which);
                                            if (t.isTextEditable()) {
                                                new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.MainActivity.this).title(me.ccrama.redditslide.R.string.sidebar_select_flair_text).input(getString(me.ccrama.redditslide.R.string.mod_flair_hint), t.getText(), true, new com.afollestad.materialdialogs.MaterialDialog.InputCallback() {
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
                                                                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).setFlair(subreddit, t, flair, me.ccrama.redditslide.Authentication.name);
                                                                    net.dean.jraw.models.FlairTemplate currentF = m.getCurrentFlair(subreddit);
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
                                                            new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).setFlair(subreddit, t, null, me.ccrama.redditslide.Authentication.name);
                                                            net.dean.jraw.models.FlairTemplate currentF = m.getCurrentFlair(subreddit);
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
                };
                currentFlair.execute(((android.view.View) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.flair))));
            }
        } else if (drawerLayout != null) {
            drawerLayout.setDrawerLockMode(android.support.v4.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED, android.support.v4.view.GravityCompat.END);
        }
    }

    net.dean.jraw.paginators.TimePeriod time = net.dean.jraw.paginators.TimePeriod.DAY;

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
                if (me.ccrama.redditslide.SettingValues.hasSort(sub)) {
                    net.dean.jraw.paginators.Sorting sortingis = me.ccrama.redditslide.SettingValues.getBaseSubmissionSort(sub);
                    sort.setText(sortingis.name() + ((sortingis == net.dean.jraw.paginators.Sorting.CONTROVERSIAL) || (sortingis == net.dean.jraw.paginators.Sorting.TOP) ? " of " + me.ccrama.redditslide.SettingValues.getBaseTimePeriod(sub).name() : ""));
                } else {
                    sort.setText("Set default sorting");
                }
                reloadSubs();
            }
        };
        com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this);
        builder.setTitle(me.ccrama.redditslide.R.string.sorting_choose);
        builder.setSingleChoiceItems(me.ccrama.redditslide.util.SortingUtil.getSortingTimesStrings(), me.ccrama.redditslide.util.SortingUtil.getSortingTimeId(""), l2);
        builder.show();
    }

    public void doSubSidebarNoLoad(final java.lang.String subreddit) {
        if (mAsyncGetSubreddit != null) {
            mAsyncGetSubreddit.cancel(true);
        }
        findViewById(me.ccrama.redditslide.R.id.loader).setVisibility(android.view.View.GONE);
        invalidateOptionsMenu();
        if (((((((!subreddit.equalsIgnoreCase("all")) && (!subreddit.equalsIgnoreCase("frontpage"))) && (!subreddit.equalsIgnoreCase("friends"))) && (!subreddit.equalsIgnoreCase("mod"))) && (!subreddit.contains("+"))) && (!subreddit.contains("."))) && (!subreddit.contains("/m/"))) {
            if (drawerLayout != null) {
                drawerLayout.setDrawerLockMode(android.support.v4.widget.DrawerLayout.LOCK_MODE_UNLOCKED, android.support.v4.view.GravityCompat.END);
            }
            findViewById(me.ccrama.redditslide.R.id.sidebar_text).setVisibility(android.view.View.GONE);
            findViewById(me.ccrama.redditslide.R.id.sub_title).setVisibility(android.view.View.GONE);
            findViewById(me.ccrama.redditslide.R.id.subscribers).setVisibility(android.view.View.GONE);
            findViewById(me.ccrama.redditslide.R.id.active_users).setVisibility(android.view.View.GONE);
            findViewById(me.ccrama.redditslide.R.id.header_sub).setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getColor(subreddit));
            ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.sub_infotitle))).setText(subreddit);
            // Sidebar buttons should use subreddit's accent color
            int subColor = new me.ccrama.redditslide.ColorPreferences(this).getColor(subreddit);
            ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.theme_text))).setTextColor(subColor);
            ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.wiki_text))).setTextColor(subColor);
            ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.post_text))).setTextColor(subColor);
            ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.mods_text))).setTextColor(subColor);
            ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.flair_text))).setTextColor(subColor);
            ((android.widget.TextView) (drawerLayout.findViewById(me.ccrama.redditslide.R.id.sorting).findViewById(me.ccrama.redditslide.R.id.sort))).setTextColor(subColor);
            ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.sync))).setTextColor(subColor);
        } else if (drawerLayout != null) {
            drawerLayout.setDrawerLockMode(android.support.v4.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED, android.support.v4.view.GravityCompat.END);
        }
    }

    /**
     * Starts the enter animations for various UI components of the toolbar subreddit search
     *
     * @param ANIMATION_DURATION
     * 		duration of the animation in ms
     * @param SUGGESTIONS_BACKGROUND
     * 		background of subreddit suggestions list
     * @param GO_TO_SUB_FIELD
     * 		search field in toolbar
     * @param CLOSE_BUTTON
     * 		button that clears the search and closes the search UI
     */
    public void enterAnimationsForToolbarSearch(final long ANIMATION_DURATION, final android.support.v7.widget.CardView SUGGESTIONS_BACKGROUND, final android.widget.AutoCompleteTextView GO_TO_SUB_FIELD, final android.widget.ImageView CLOSE_BUTTON) {
        SUGGESTIONS_BACKGROUND.animate().translationY(headerHeight).setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator()).setDuration(ANIMATION_DURATION + ANIMATE_DURATION_OFFSET).start();
        GO_TO_SUB_FIELD.animate().alpha(1.0F).setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator()).setDuration(ANIMATION_DURATION).start();
        CLOSE_BUTTON.animate().alpha(1.0F).setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator()).setDuration(ANIMATION_DURATION).start();
    }

    /**
     * Starts the exit animations for various UI components of the toolbar subreddit search
     *
     * @param ANIMATION_DURATION
     * 		duration of the animation in ms
     * @param SUGGESTIONS_BACKGROUND
     * 		background of subreddit suggestions list
     * @param GO_TO_SUB_FIELD
     * 		search field in toolbar
     * @param CLOSE_BUTTON
     * 		button that clears the search and closes the search UI
     */
    public void exitAnimationsForToolbarSearch(final long ANIMATION_DURATION, final android.support.v7.widget.CardView SUGGESTIONS_BACKGROUND, final android.widget.AutoCompleteTextView GO_TO_SUB_FIELD, final android.widget.ImageView CLOSE_BUTTON) {
        SUGGESTIONS_BACKGROUND.animate().translationY(-SUGGESTIONS_BACKGROUND.getHeight()).setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator()).setDuration(ANIMATION_DURATION + ANIMATE_DURATION_OFFSET).start();
        GO_TO_SUB_FIELD.animate().alpha(0.0F).setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator()).setDuration(ANIMATION_DURATION).start();
        CLOSE_BUTTON.animate().alpha(0.0F).setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator()).setDuration(ANIMATION_DURATION).start();
        // Helps smooth the transition between the toolbar title being reset and the search elements
        // fading out.
        final long OFFSET_ANIM = (ANIMATION_DURATION == 0) ? 0 : ANIMATE_DURATION_OFFSET;
        // Hide the various UI components after the animations are complete and
        // reset the toolbar title
        new android.os.Handler().postDelayed(new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                SUGGESTIONS_BACKGROUND.setVisibility(android.view.View.GONE);
                GO_TO_SUB_FIELD.setVisibility(android.view.View.GONE);
                CLOSE_BUTTON.setVisibility(android.view.View.GONE);
                if (me.ccrama.redditslide.SettingValues.single) {
                    getSupportActionBar().setTitle(selectedSub);
                } else {
                    getSupportActionBar().setTitle(tabViewModeTitle);
                }
            }
        }, ANIMATION_DURATION + ANIMATE_DURATION_OFFSET);
    }

    public void filterContent(final java.lang.String subreddit) {
        final boolean[] chosen = new boolean[]{ me.ccrama.redditslide.PostMatch.isImage(subreddit.toLowerCase(java.util.Locale.ENGLISH)), me.ccrama.redditslide.PostMatch.isAlbums(subreddit.toLowerCase(java.util.Locale.ENGLISH)), me.ccrama.redditslide.PostMatch.isGif(subreddit.toLowerCase(java.util.Locale.ENGLISH)), me.ccrama.redditslide.PostMatch.isVideo(subreddit.toLowerCase(java.util.Locale.ENGLISH)), me.ccrama.redditslide.PostMatch.isUrls(subreddit.toLowerCase(java.util.Locale.ENGLISH)), me.ccrama.redditslide.PostMatch.isSelftext(subreddit.toLowerCase(java.util.Locale.ENGLISH)), me.ccrama.redditslide.PostMatch.isNsfw(subreddit.toLowerCase(java.util.Locale.ENGLISH)) };
        final java.lang.String currentSubredditName = usedArray.get(me.ccrama.redditslide.Reddit.currentPosition);
        // Title of the filter dialog
        java.lang.String filterTitle;
        if (currentSubredditName.contains("/m/")) {
            filterTitle = getString(me.ccrama.redditslide.R.string.content_to_hide, currentSubredditName);
        } else if (currentSubredditName.equals("frontpage")) {
            filterTitle = getString(me.ccrama.redditslide.R.string.content_to_hide, "frontpage");
        } else {
            filterTitle = getString(me.ccrama.redditslide.R.string.content_to_hide, "/r/" + currentSubredditName);
        }
        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setTitle(filterTitle).alwaysCallMultiChoiceCallback().setMultiChoiceItems(new java.lang.String[]{ getString(me.ccrama.redditslide.R.string.image_downloads), getString(me.ccrama.redditslide.R.string.type_albums), getString(me.ccrama.redditslide.R.string.type_gifs), getString(me.ccrama.redditslide.R.string.type_videos), getString(me.ccrama.redditslide.R.string.type_links), getString(me.ccrama.redditslide.R.string.type_selftext), getString(me.ccrama.redditslide.R.string.type_nsfw_content) }, chosen, new android.content.DialogInterface.OnMultiChoiceClickListener() {
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
        if (adapter.getCurrentFragment() == null) {
            return 0;
        }
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

    public void openPopup() {
        android.support.v7.widget.PopupMenu popup = new android.support.v7.widget.PopupMenu(this, findViewById(me.ccrama.redditslide.R.id.anchor), android.view.Gravity.RIGHT);
        java.lang.String id = ((me.ccrama.redditslide.Fragments.SubmissionsView) (((me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapter) (pager.getAdapter())).getCurrentFragment())).id;
        final android.text.Spannable[] base = me.ccrama.redditslide.util.SortingUtil.getSortingSpannables(id);
        for (android.text.Spannable s : base) {
            // Do not add option for "Best" in any subreddit except for the frontpage.
            if ((!id.equals("frontpage")) && s.toString().equals(getString(me.ccrama.redditslide.R.string.sorting_best))) {
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
                        me.ccrama.redditslide.util.SortingUtil.setSorting(((me.ccrama.redditslide.Fragments.SubmissionsView) (((me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapter) (pager.getAdapter())).getCurrentFragment())).id, net.dean.jraw.paginators.Sorting.HOT);
                        reloadSubs();
                        break;
                    case 1 :
                        me.ccrama.redditslide.util.SortingUtil.setSorting(((me.ccrama.redditslide.Fragments.SubmissionsView) (((me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapter) (pager.getAdapter())).getCurrentFragment())).id, net.dean.jraw.paginators.Sorting.NEW);
                        reloadSubs();
                        break;
                    case 2 :
                        me.ccrama.redditslide.util.SortingUtil.setSorting(((me.ccrama.redditslide.Fragments.SubmissionsView) (((me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapter) (pager.getAdapter())).getCurrentFragment())).id, net.dean.jraw.paginators.Sorting.RISING);
                        reloadSubs();
                        break;
                    case 3 :
                        me.ccrama.redditslide.util.SortingUtil.setSorting(((me.ccrama.redditslide.Fragments.SubmissionsView) (((me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapter) (pager.getAdapter())).getCurrentFragment())).id, net.dean.jraw.paginators.Sorting.TOP);
                        openPopupTime();
                        break;
                    case 4 :
                        me.ccrama.redditslide.util.SortingUtil.setSorting(((me.ccrama.redditslide.Fragments.SubmissionsView) (((me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapter) (pager.getAdapter())).getCurrentFragment())).id, net.dean.jraw.paginators.Sorting.CONTROVERSIAL);
                        openPopupTime();
                        break;
                    case 5 :
                        me.ccrama.redditslide.util.SortingUtil.setSorting(((me.ccrama.redditslide.Fragments.SubmissionsView) (((me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapter) (pager.getAdapter())).getCurrentFragment())).id, net.dean.jraw.paginators.Sorting.BEST);
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
        java.lang.String id = ((me.ccrama.redditslide.Fragments.SubmissionsView) (((me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapter) (pager.getAdapter())).getCurrentFragment())).id;
        final android.text.Spannable[] base = me.ccrama.redditslide.util.SortingUtil.getSortingTimesSpannables(id);
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
                        me.ccrama.redditslide.util.SortingUtil.setTime(((me.ccrama.redditslide.Fragments.SubmissionsView) (((me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapter) (pager.getAdapter())).getCurrentFragment())).id, net.dean.jraw.paginators.TimePeriod.HOUR);
                        reloadSubs();
                        break;
                    case 1 :
                        me.ccrama.redditslide.util.SortingUtil.setTime(((me.ccrama.redditslide.Fragments.SubmissionsView) (((me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapter) (pager.getAdapter())).getCurrentFragment())).id, net.dean.jraw.paginators.TimePeriod.DAY);
                        reloadSubs();
                        break;
                    case 2 :
                        me.ccrama.redditslide.util.SortingUtil.setTime(((me.ccrama.redditslide.Fragments.SubmissionsView) (((me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapter) (pager.getAdapter())).getCurrentFragment())).id, net.dean.jraw.paginators.TimePeriod.WEEK);
                        reloadSubs();
                        break;
                    case 3 :
                        me.ccrama.redditslide.util.SortingUtil.setTime(((me.ccrama.redditslide.Fragments.SubmissionsView) (((me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapter) (pager.getAdapter())).getCurrentFragment())).id, net.dean.jraw.paginators.TimePeriod.MONTH);
                        reloadSubs();
                        break;
                    case 4 :
                        me.ccrama.redditslide.util.SortingUtil.setTime(((me.ccrama.redditslide.Fragments.SubmissionsView) (((me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapter) (pager.getAdapter())).getCurrentFragment())).id, net.dean.jraw.paginators.TimePeriod.YEAR);
                        reloadSubs();
                        break;
                    case 5 :
                        me.ccrama.redditslide.util.SortingUtil.setTime(((me.ccrama.redditslide.Fragments.SubmissionsView) (((me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapter) (pager.getAdapter())).getCurrentFragment())).id, net.dean.jraw.paginators.TimePeriod.ALL);
                        reloadSubs();
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    public static java.lang.String randomoverride;

    public void reloadSubs() {
        int current = pager.getCurrentItem();
        if (commentPager && (current == currentComment)) {
            current = current - 1;
        }
        if (current < 0) {
            current = 0;
        }
        reloadItemNumber = current;
        if (adapter instanceof me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapterComment) {
            pager.setAdapter(null);
            adapter = new me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapterComment(getSupportFragmentManager());
            pager.setAdapter(adapter);
        } else {
            adapter = new me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapter(getSupportFragmentManager());
            pager.setAdapter(adapter);
        }
        reloadItemNumber = -2;
        me.ccrama.redditslide.Activities.MainActivity.shouldLoad = usedArray.get(current);
        pager.setCurrentItem(current);
        if (mTabLayout != null) {
            mTabLayout.setupWithViewPager(pager);
            scrollToTabAfterLayout(current);
        }
        if (me.ccrama.redditslide.SettingValues.single) {
            getSupportActionBar().setTitle(me.ccrama.redditslide.Activities.MainActivity.shouldLoad);
        }
        setToolbarClick();
        if ((me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_TOOLBAR) || (me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_BOTH)) {
            setupSubredditSearchToolbar();
        }
    }

    public void resetAdapter() {
        if (me.ccrama.redditslide.UserSubscriptions.hasSubs()) {
            runOnUiThread(new java.lang.Runnable() {
                @java.lang.Override
                public void run() {
                    usedArray = new me.ccrama.redditslide.CaseInsensitiveArrayList(me.ccrama.redditslide.UserSubscriptions.getSubscriptions(me.ccrama.redditslide.Activities.MainActivity.this));
                    adapter = new me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapter(getSupportFragmentManager());
                    pager.setAdapter(adapter);
                    if (mTabLayout != null) {
                        mTabLayout.setupWithViewPager(pager);
                        scrollToTabAfterLayout(usedArray.indexOf(subToDo));
                    }
                    setToolbarClick();
                    pager.setCurrentItem(usedArray.indexOf(subToDo));
                    int color = me.ccrama.redditslide.Visuals.Palette.getColor(subToDo);
                    hea.setBackgroundColor(color);
                    header.setBackgroundColor(color);
                    if (accountsArea != null) {
                        accountsArea.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(color));
                    }
                    themeSystemBars(subToDo);
                    setRecentBar(subToDo);
                }
            });
        }
    }

    public void restartTheme() {
        me.ccrama.redditslide.Activities.MainActivity.isRestart = true;
        me.ccrama.redditslide.Activities.MainActivity.restartPage = getCurrentPage();
        android.content.Intent intent = this.getIntent();
        int page = pager.getCurrentItem();
        if (currentComment == page)
            page -= 1;

        intent.putExtra(me.ccrama.redditslide.Activities.MainActivity.EXTRA_PAGE_TO, page);
        finish();
        startActivity(intent);
        overridePendingTransition(me.ccrama.redditslide.R.anim.fade_in_real, me.ccrama.redditslide.R.anim.fading_out_real);
    }

    public void saveOffline(final java.util.List<net.dean.jraw.models.Submission> submissions, final java.lang.String subreddit) {
        final boolean[] chosen = new boolean[2];
        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setTitle(me.ccrama.redditslide.R.string.save_for_offline_viewing).setMultiChoiceItems(new java.lang.String[]{ getString(me.ccrama.redditslide.R.string.type_gifs) }, new boolean[]{ false }, new android.content.DialogInterface.OnMultiChoiceClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialog, int which, boolean isChecked) {
                chosen[which] = isChecked;
            }
        }).setPositiveButton(me.ccrama.redditslide.R.string.btn_save, new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                caching = new me.ccrama.redditslide.CommentCacheAsync(submissions, me.ccrama.redditslide.Activities.MainActivity.this, subreddit, chosen).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }).setPositiveButton(me.ccrama.redditslide.R.string.btn_save, new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                java.util.concurrent.ExecutorService service = java.util.concurrent.Executors.newSingleThreadExecutor();
                new me.ccrama.redditslide.CommentCacheAsync(submissions, me.ccrama.redditslide.Activities.MainActivity.this, subreddit, chosen).executeOnExecutor(service);
            }
        }).show();
    }

    public void scrollToTop() {
        int[] firstVisibleItems;
        int pastVisiblesItems = 0;
        if (adapter.getCurrentFragment() == null)
            return;

        firstVisibleItems = ((me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager) (((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).rv.getLayoutManager())).findFirstVisibleItemPositions(null);
        if ((firstVisibleItems != null) && (firstVisibleItems.length > 0)) {
            for (int firstVisibleItem : firstVisibleItems) {
                pastVisiblesItems = firstVisibleItem;
            }
        }
        if (pastVisiblesItems > 8) {
            ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).rv.scrollToPosition(0);
            header.animate().translationY(header.getHeight()).setInterpolator(new android.view.animation.LinearInterpolator()).setDuration(0);
        } else {
            ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).rv.smoothScrollToPosition(0);
        }
        ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment())).resetScroll();
    }

    public void setDataSet(java.util.List<java.lang.String> data) {
        if ((data != null) && (!data.isEmpty())) {
            usedArray = new me.ccrama.redditslide.CaseInsensitiveArrayList(data);
            if (adapter == null) {
                if (commentPager && singleMode) {
                    adapter = new me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapterComment(getSupportFragmentManager());
                } else {
                    adapter = new me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapter(getSupportFragmentManager());
                }
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
            me.ccrama.redditslide.Activities.MainActivity.shouldLoad = usedArray.get(toGoto);
            selectedSub = usedArray.get(toGoto);
            themeSystemBars(usedArray.get(toGoto));
            final java.lang.String USEDARRAY_0 = usedArray.get(0);
            header.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getColor(USEDARRAY_0));
            if (hea != null) {
                hea.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getColor(USEDARRAY_0));
                if (accountsArea != null) {
                    accountsArea.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(USEDARRAY_0));
                }
            }
            if (!me.ccrama.redditslide.SettingValues.single) {
                mTabLayout.setSelectedTabIndicatorColor(new me.ccrama.redditslide.ColorPreferences(this).getColor(USEDARRAY_0));
                pager.setCurrentItem(toGoto);
                mTabLayout.setupWithViewPager(pager);
                if (mTabLayout != null) {
                    mTabLayout.setupWithViewPager(pager);
                    scrollToTabAfterLayout(toGoto);
                }
            } else {
                getSupportActionBar().setTitle(usedArray.get(toGoto));
                pager.setCurrentItem(toGoto);
            }
            setToolbarClick();
            setRecentBar(usedArray.get(toGoto));
            doSubSidebarNoLoad(usedArray.get(toGoto));
        } else if (me.ccrama.redditslide.util.NetworkUtil.isConnected(this)) {
            me.ccrama.redditslide.UserSubscriptions.doMainActivitySubs(this);
        }
    }

    public void setDrawerSubList() {
        java.util.ArrayList<java.lang.String> copy;
        if (me.ccrama.redditslide.util.NetworkUtil.isConnected(this)) {
            copy = new java.util.ArrayList<>(usedArray);
        } else {
            copy = me.ccrama.redditslide.UserSubscriptions.getAllUserSubreddits(this);
        }
        copy.removeAll(java.util.Arrays.asList("", null));
        sideArrayAdapter = new me.ccrama.redditslide.Adapters.SideArrayAdapter(this, copy, me.ccrama.redditslide.UserSubscriptions.getAllSubreddits(this), drawerSubList);
        drawerSubList.setAdapter(sideArrayAdapter);
        if (((me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_DRAWER) || (me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_BOTH)) || (me.ccrama.redditslide.SettingValues.subredditSearchMethod != me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_TOOLBAR)) {
            drawerSearch = headerMain.findViewById(me.ccrama.redditslide.R.id.sort);
            drawerSearch.setVisibility(android.view.View.VISIBLE);
            drawerSubList.setFocusable(false);
            headerMain.findViewById(me.ccrama.redditslide.R.id.close_search_drawer).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    drawerSearch.setText("");
                }
            });
            drawerSearch.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
                @java.lang.Override
                public void onFocusChange(android.view.View v, boolean hasFocus) {
                    if (hasFocus) {
                        getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                        drawerSubList.smoothScrollToPositionFromTop(1, drawerSearch.getHeight(), 100);
                    } else {
                        getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    }
                }
            });
            drawerSearch.setOnEditorActionListener(new android.widget.TextView.OnEditorActionListener() {
                @java.lang.Override
                public boolean onEditorAction(android.widget.TextView arg0, int arg1, android.view.KeyEvent arg2) {
                    if (arg1 == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                        // If it the input text doesn't match a subreddit from the list exactly, openInSubView is true
                        if (((sideArrayAdapter.fitems == null) || sideArrayAdapter.openInSubView) || (!usedArray.contains(drawerSearch.getText().toString().toLowerCase(java.util.Locale.ENGLISH)))) {
                            android.content.Intent inte = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.SubredditView.class);
                            inte.putExtra(me.ccrama.redditslide.Activities.SubredditView.EXTRA_SUBREDDIT, drawerSearch.getText().toString());
                            me.ccrama.redditslide.Activities.MainActivity.this.startActivityForResult(inte, 2001);
                        } else {
                            if (commentPager && (adapter instanceof me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapterComment)) {
                                openingComments = null;
                                toOpenComments = -1;
                                ((me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapterComment) (adapter)).size = usedArray.size() + 1;
                                adapter.notifyDataSetChanged();
                                if (usedArray.contains(drawerSearch.getText().toString().toLowerCase(java.util.Locale.ENGLISH))) {
                                    doPageSelectedComments(usedArray.indexOf(drawerSearch.getText().toString().toLowerCase(java.util.Locale.ENGLISH)));
                                } else {
                                    doPageSelectedComments(usedArray.indexOf(sideArrayAdapter.fitems.get(0)));
                                }
                            }
                            if (usedArray.contains(drawerSearch.getText().toString().toLowerCase(java.util.Locale.ENGLISH))) {
                                pager.setCurrentItem(usedArray.indexOf(drawerSearch.getText().toString().toLowerCase(java.util.Locale.ENGLISH)));
                            } else {
                                pager.setCurrentItem(usedArray.indexOf(sideArrayAdapter.fitems.get(0)));
                            }
                            drawerLayout.closeDrawers();
                            drawerSearch.setText("");
                            android.view.View view = me.ccrama.redditslide.Activities.MainActivity.this.getCurrentFocus();
                            if (view != null) {
                                android.view.inputmethod.InputMethodManager imm = ((android.view.inputmethod.InputMethodManager) (getSystemService(android.content.Context.INPUT_METHOD_SERVICE)));
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                        }
                    }
                    return false;
                }
            });
            final android.view.View close = findViewById(me.ccrama.redditslide.R.id.close_search_drawer);
            close.setVisibility(android.view.View.GONE);
            drawerSearch.addTextChangedListener(new android.text.TextWatcher() {
                @java.lang.Override
                public void beforeTextChanged(java.lang.CharSequence charSequence, int i, int i2, int i3) {
                }

                @java.lang.Override
                public void onTextChanged(java.lang.CharSequence charSequence, int i, int i2, int i3) {
                }

                @java.lang.Override
                public void afterTextChanged(android.text.Editable editable) {
                    final java.lang.String result = editable.toString();
                    if (result.isEmpty()) {
                        close.setVisibility(android.view.View.GONE);
                    } else {
                        close.setVisibility(android.view.View.VISIBLE);
                    }
                    sideArrayAdapter.getFilter().filter(result);
                }
            });
        } else if (drawerSearch != null) {
            drawerSearch.setOnClickListener(null);// remove the touch listener on the drawer search field

            drawerSearch.setVisibility(android.view.View.GONE);
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

    public void updateColor(int color, java.lang.String subreddit) {
        hea.setBackgroundColor(color);
        header.setBackgroundColor(color);
        if (accountsArea != null) {
            accountsArea.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(color));
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            android.view.Window window = getWindow();
            window.setStatusBarColor(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(color));
        }
        setRecentBar(subreddit, color);
        findViewById(me.ccrama.redditslide.R.id.header_sub).setBackgroundColor(color);
    }

    public void updateMultiNameToSubs(java.util.Map<java.lang.String, java.lang.String> subs) {
        me.ccrama.redditslide.Activities.MainActivity.multiNameToSubsMap = subs;
    }

    public void updateSubs(java.util.ArrayList<java.lang.String> subs) {
        if (subs.isEmpty() && (!me.ccrama.redditslide.util.NetworkUtil.isConnected(this))) {
            findViewById(me.ccrama.redditslide.R.id.toolbar).setVisibility(android.view.View.GONE);
            d = new com.afollestad.materialdialogs.MaterialDialog.Builder(this).title(me.ccrama.redditslide.R.string.offline_no_content_found).positiveText(me.ccrama.redditslide.R.string.offline_enter_online).negativeText(me.ccrama.redditslide.R.string.btn_close).cancelable(false).onNegative(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                @java.lang.Override
                public void onClick(@android.support.annotation.NonNull
                com.afollestad.materialdialogs.MaterialDialog dialog, @android.support.annotation.NonNull
                com.afollestad.materialdialogs.DialogAction which) {
                    finish();
                }
            }).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                @java.lang.Override
                public void onClick(@android.support.annotation.NonNull
                com.afollestad.materialdialogs.MaterialDialog dialog, @android.support.annotation.NonNull
                com.afollestad.materialdialogs.DialogAction which) {
                    me.ccrama.redditslide.Reddit.appRestart.edit().remove("forceoffline").commit();
                    me.ccrama.redditslide.Reddit.forceRestart(me.ccrama.redditslide.Activities.MainActivity.this, false);
                }
            }).show();
        } else {
            drawerLayout = ((android.support.v4.widget.DrawerLayout) (findViewById(me.ccrama.redditslide.R.id.drawer_layout)));
            if (!getResources().getBoolean(me.ccrama.redditslide.R.bool.isTablet)) {
                me.ccrama.redditslide.Activities.MainActivity.setDrawerEdge(this, me.ccrama.redditslide.Constants.DRAWER_SWIPE_EDGE, drawerLayout);
            } else {
                me.ccrama.redditslide.Activities.MainActivity.setDrawerEdge(this, me.ccrama.redditslide.Constants.DRAWER_SWIPE_EDGE_TABLET, drawerLayout);
            }
            if (me.ccrama.redditslide.Activities.MainActivity.loader != null) {
                header.setVisibility(android.view.View.VISIBLE);
                setDataSet(subs);
                doDrawer();
                try {
                    setDataSet(subs);
                } catch (java.lang.Exception ignored) {
                }
                me.ccrama.redditslide.Activities.MainActivity.loader.finish();
                me.ccrama.redditslide.Activities.MainActivity.loader = null;
            } else {
                setDataSet(subs);
                doDrawer();
            }
        }
        if (me.ccrama.redditslide.util.NetworkUtil.isConnected(this)) {
            if (me.ccrama.redditslide.Authentication.isLoggedIn && (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1)) {
                android.content.pm.ShortcutManager shortcutManager = getSystemService(android.content.pm.ShortcutManager.class);
                java.util.ArrayList<android.content.pm.ShortcutInfo> shortcuts = new java.util.ArrayList<>();
                shortcuts.add(new android.content.pm.ShortcutInfo.Builder(this, "inbox").setShortLabel("Inbox").setLongLabel("Open your Inbox").setIcon(getIcon("inbox", me.ccrama.redditslide.R.drawable.sidebar_inbox)).setIntent(new android.content.Intent(android.content.Intent.ACTION_VIEW, new android.net.Uri.Builder().build(), this, me.ccrama.redditslide.Activities.Inbox.class)).build());
                shortcuts.add(new android.content.pm.ShortcutInfo.Builder(this, "submit").setShortLabel("Submit").setLongLabel("Create new Submission").setIcon(getIcon("submit", me.ccrama.redditslide.R.drawable.edit)).setIntent(new android.content.Intent(android.content.Intent.ACTION_VIEW, new android.net.Uri.Builder().build(), this, me.ccrama.redditslide.Activities.Submit.class)).build());
                int count = 0;
                for (java.lang.String s : subs) {
                    if ((count == 2) || (count == subs.size())) {
                        break;
                    }
                    if (!s.contains("/m/")) {
                        android.content.Intent sub = new android.content.Intent(android.content.Intent.ACTION_VIEW, new android.net.Uri.Builder().build(), this, me.ccrama.redditslide.Activities.SubredditView.class);
                        sub.putExtra(me.ccrama.redditslide.Activities.SubredditView.EXTRA_SUBREDDIT, s);
                        shortcuts.add(new android.content.pm.ShortcutInfo.Builder(this, "sub" + s).setShortLabel((s.equalsIgnoreCase("frontpage") ? "" : "/r/") + s).setLongLabel((s.equalsIgnoreCase("frontpage") ? "" : "/r/") + s).setIcon(getIcon(s, me.ccrama.redditslide.R.drawable.sub)).setIntent(sub).build());
                        count++;
                    }
                }
                java.util.Collections.reverse(shortcuts);
                shortcutManager.setDynamicShortcuts(shortcuts);
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
                android.content.pm.ShortcutManager shortcutManager = getSystemService(android.content.pm.ShortcutManager.class);
                java.util.ArrayList<android.content.pm.ShortcutInfo> shortcuts = new java.util.ArrayList<>();
                int count = 0;
                for (java.lang.String s : subs) {
                    if ((count == 4) || (count == subs.size())) {
                        break;
                    }
                    if (!s.contains("/m/")) {
                        android.content.Intent sub = new android.content.Intent(android.content.Intent.ACTION_VIEW, new android.net.Uri.Builder().build(), this, me.ccrama.redditslide.Activities.SubredditView.class);
                        sub.putExtra(me.ccrama.redditslide.Activities.SubredditView.EXTRA_SUBREDDIT, s);
                        new android.content.pm.ShortcutInfo.Builder(this, "sub" + s).setShortLabel((s.equalsIgnoreCase("frontpage") ? "" : "/r/") + s).setLongLabel((s.equalsIgnoreCase("frontpage") ? "" : "/r/") + s).setIcon(getIcon(s, me.ccrama.redditslide.R.drawable.sub)).setIntent(sub).build();
                        count++;
                    }
                }
                java.util.Collections.reverse(shortcuts);
                shortcutManager.setDynamicShortcuts(shortcuts);
            }
        }
    }

    private android.graphics.drawable.Icon getIcon(java.lang.String subreddit, @android.support.annotation.DrawableRes
    int overlay) {
        android.graphics.Bitmap color = android.graphics.Bitmap.createBitmap(me.ccrama.redditslide.Activities.MainActivity.toDp(this, 148), me.ccrama.redditslide.Activities.MainActivity.toDp(this, 148), android.graphics.Bitmap.Config.RGB_565);
        color.eraseColor(me.ccrama.redditslide.Visuals.Palette.getColor(subreddit));
        if (color != null) {
            color.recycle();
        }
        color = me.ccrama.redditslide.Activities.MainActivity.clipToCircle(color);
        android.graphics.Bitmap over = me.ccrama.redditslide.Activities.MainActivity.drawableToBitmap(android.support.v4.content.res.ResourcesCompat.getDrawable(getResources(), overlay, null));
        android.graphics.Canvas canvas = new android.graphics.Canvas(color);
        canvas.drawBitmap(over, (color.getWidth() / 2) - (over.getWidth() / 2), (color.getHeight() / 2) - (over.getHeight() / 2), null);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            return android.graphics.drawable.Icon.createWithBitmap(color);
        }
        return null;
    }

    public static android.graphics.Bitmap drawableToBitmap(android.graphics.drawable.Drawable drawable) {
        if (drawable instanceof android.graphics.drawable.BitmapDrawable) {
            return ((android.graphics.drawable.BitmapDrawable) (drawable)).getBitmap();
        }
        android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), android.graphics.Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static int toDp(android.content.Context context, int px) {
        return me.ccrama.redditslide.Activities.MainActivity.convert(context, px, android.util.TypedValue.COMPLEX_UNIT_PX);
    }

    private static int convert(android.content.Context context, int amount, int conversionUnit) {
        if (amount < 0) {
            throw new java.lang.IllegalArgumentException("px should not be less than zero");
        }
        android.content.res.Resources r = context.getResources();
        return ((int) (android.util.TypedValue.applyDimension(conversionUnit, amount, r.getDisplayMetrics())));
    }

    public static android.graphics.Bitmap clipToCircle(android.graphics.Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final android.graphics.Bitmap outputBitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888);
        final android.graphics.Path path = new android.graphics.Path();
        path.addCircle(((float) (width / 2)), ((float) (height / 2)), ((float) (java.lang.Math.min(width, height / 2))), android.graphics.Path.Direction.CCW);
        final android.graphics.Canvas canvas = new android.graphics.Canvas(outputBitmap);
        canvas.clipPath(path);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return outputBitmap;
    }

    private static android.animation.ValueAnimator flipAnimator(boolean isFlipped, final android.view.View v) {
        android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofFloat(isFlipped ? -1.0F : 1.0F, isFlipped ? 1.0F : -1.0F);
        animator.setInterpolator(new android.support.v4.view.animation.FastOutSlowInInterpolator());
        animator.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {
            @java.lang.Override
            public void onAnimationUpdate(android.animation.ValueAnimator valueAnimator) {
                // Update Height
                v.setScaleY(((java.lang.Float) (valueAnimator.getAnimatedValue())));
            }
        });
        return animator;
    }

    private void changeSubscription(net.dean.jraw.models.Subreddit subreddit, boolean isChecked) {
        currentlySubbed = isChecked;
        if (isChecked) {
            me.ccrama.redditslide.UserSubscriptions.addSubreddit(subreddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), this);
        } else {
            me.ccrama.redditslide.UserSubscriptions.removeSubreddit(subreddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), this);
            pager.setCurrentItem(pager.getCurrentItem() - 1);
            restartTheme();
        }
    }

    private void collapse(final android.widget.LinearLayout v) {
        int finalHeight = v.getHeight();
        android.animation.ValueAnimator mAnimator = slideAnimator(finalHeight, 0, v);
        mAnimator.addListener(new android.animation.Animator.AnimatorListener() {
            @java.lang.Override
            public void onAnimationStart(android.animation.Animator animation) {
            }

            @java.lang.Override
            public void onAnimationEnd(android.animation.Animator animator) {
                v.setVisibility(android.view.View.GONE);
            }

            @java.lang.Override
            public void onAnimationCancel(android.animation.Animator animation) {
            }

            @java.lang.Override
            public void onAnimationRepeat(android.animation.Animator animation) {
            }
        });
        mAnimator.start();
    }

    private void dismissProgressDialog() {
        if ((d != null) && d.isShowing()) {
            d.dismiss();
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

    private void expand(android.widget.LinearLayout v) {
        // set Visible
        v.setVisibility(android.view.View.VISIBLE);
        final int widthSpec = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
        v.measure(widthSpec, heightSpec);
        android.animation.ValueAnimator mAnimator = slideAnimator(0, v.getMeasuredHeight(), v);
        mAnimator.start();
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

    private void setViews(java.lang.String rawHTML, java.lang.String subredditName, me.ccrama.redditslide.SpoilerRobotoTextView firstTextView, me.ccrama.redditslide.Views.CommentOverflow commentOverflow) {
        if (rawHTML.isEmpty()) {
            return;
        }
        java.util.List<java.lang.String> blocks = me.ccrama.redditslide.util.SubmissionParser.getBlocks(rawHTML);
        int startIndex = 0;
        // the <div class="md"> case is when the body contains a table or code block first
        if (!blocks.get(0).equals("<div class=\"md\">")) {
            firstTextView.setVisibility(android.view.View.VISIBLE);
            firstTextView.setTextHtml(blocks.get(0), subredditName);
            firstTextView.setLinkTextColor(new me.ccrama.redditslide.ColorPreferences(this).getColor(subredditName));
            startIndex = 1;
        } else {
            firstTextView.setText("");
            firstTextView.setVisibility(android.view.View.GONE);
        }
        if (blocks.size() > 1) {
            if (startIndex == 0) {
                commentOverflow.setViews(blocks, subredditName);
            } else {
                commentOverflow.setViews(blocks.subList(startIndex, blocks.size()), subredditName);
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

    /**
     * If the user has the Subreddit Search method set to "long press on toolbar title", an
     * OnLongClickListener needs to be set for the toolbar as well as handling all of the relevant
     * onClicks for the views of the search bar.
     */
    private void setupSubredditSearchToolbar() {
        if (!me.ccrama.redditslide.util.NetworkUtil.isConnected(this)) {
            findViewById(me.ccrama.redditslide.R.id.drawer_divider).setVisibility(android.view.View.GONE);
        } else if ((((me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_TOOLBAR) || (me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_BOTH)) && (usedArray != null)) && (!usedArray.isEmpty())) {
            if (findViewById(me.ccrama.redditslide.R.id.drawer_divider) != null) {
                if (me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_BOTH) {
                    findViewById(me.ccrama.redditslide.R.id.drawer_divider).setVisibility(android.view.View.GONE);
                } else {
                    findViewById(me.ccrama.redditslide.R.id.drawer_divider).setVisibility(android.view.View.VISIBLE);
                }
            }
            final android.widget.ListView TOOLBAR_SEARCH_SUGGEST_LIST = ((android.widget.ListView) (findViewById(me.ccrama.redditslide.R.id.toolbar_search_suggestions_list)));
            final java.util.ArrayList<java.lang.String> subs_copy = new java.util.ArrayList<>(usedArray);
            final me.ccrama.redditslide.Adapters.SideArrayAdapter TOOLBAR_SEARCH_SUGGEST_ADAPTER = new me.ccrama.redditslide.Adapters.SideArrayAdapter(this, subs_copy, me.ccrama.redditslide.UserSubscriptions.getAllSubreddits(this), TOOLBAR_SEARCH_SUGGEST_LIST);
            if (TOOLBAR_SEARCH_SUGGEST_LIST != null) {
                TOOLBAR_SEARCH_SUGGEST_LIST.setAdapter(TOOLBAR_SEARCH_SUGGEST_ADAPTER);
            }
            if (mToolbar != null) {
                mToolbar.setOnLongClickListener(new android.view.View.OnLongClickListener() {
                    @java.lang.Override
                    public boolean onLongClick(android.view.View v) {
                        final android.widget.AutoCompleteTextView GO_TO_SUB_FIELD = ((android.widget.AutoCompleteTextView) (findViewById(me.ccrama.redditslide.R.id.toolbar_search)));
                        final android.widget.ImageView CLOSE_BUTTON = ((android.widget.ImageView) (findViewById(me.ccrama.redditslide.R.id.close_search_toolbar)));
                        final android.support.v7.widget.CardView SUGGESTIONS_BACKGROUND = ((android.support.v7.widget.CardView) (findViewById(me.ccrama.redditslide.R.id.toolbar_search_suggestions)));
                        // if the view mode is set to Subreddit Tabs, save the title ("Slide" or "Slide (debug)")
                        tabViewModeTitle = (!me.ccrama.redditslide.SettingValues.single) ? getSupportActionBar().getTitle().toString() : null;
                        getSupportActionBar().setTitle("");// clear title to make room for search field

                        if (((GO_TO_SUB_FIELD != null) && (CLOSE_BUTTON != null)) && (SUGGESTIONS_BACKGROUND != null)) {
                            GO_TO_SUB_FIELD.setVisibility(android.view.View.VISIBLE);
                            CLOSE_BUTTON.setVisibility(android.view.View.VISIBLE);
                            SUGGESTIONS_BACKGROUND.setVisibility(android.view.View.VISIBLE);
                            // run enter animations
                            enterAnimationsForToolbarSearch(ANIMATE_DURATION, SUGGESTIONS_BACKGROUND, GO_TO_SUB_FIELD, CLOSE_BUTTON);
                            // Get focus of the search field and show the keyboard
                            GO_TO_SUB_FIELD.requestFocus();
                            android.view.inputmethod.InputMethodManager imm = ((android.view.inputmethod.InputMethodManager) (getSystemService(android.content.Context.INPUT_METHOD_SERVICE)));
                            imm.toggleSoftInput(android.view.inputmethod.InputMethodManager.SHOW_FORCED, android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY);
                            // Close the search UI and keyboard when clicking the close button
                            CLOSE_BUTTON.setOnClickListener(new android.view.View.OnClickListener() {
                                @java.lang.Override
                                public void onClick(android.view.View v) {
                                    final android.view.View view = me.ccrama.redditslide.Activities.MainActivity.this.getCurrentFocus();
                                    if (view != null) {
                                        // Hide the keyboard
                                        android.view.inputmethod.InputMethodManager imm = ((android.view.inputmethod.InputMethodManager) (getSystemService(android.content.Context.INPUT_METHOD_SERVICE)));
                                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                    }
                                    // run the exit animations
                                    exitAnimationsForToolbarSearch(ANIMATE_DURATION, SUGGESTIONS_BACKGROUND, GO_TO_SUB_FIELD, CLOSE_BUTTON);
                                    // clear sub text when close button is clicked
                                    GO_TO_SUB_FIELD.setText("");
                                }
                            });
                            GO_TO_SUB_FIELD.setOnEditorActionListener(new android.widget.TextView.OnEditorActionListener() {
                                @java.lang.Override
                                public boolean onEditorAction(android.widget.TextView arg0, int arg1, android.view.KeyEvent arg2) {
                                    if (arg1 == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                                        // If it the input text doesn't match a subreddit from the list exactly, openInSubView is true
                                        if (((sideArrayAdapter.fitems == null) || sideArrayAdapter.openInSubView) || (!usedArray.contains(GO_TO_SUB_FIELD.getText().toString().toLowerCase(java.util.Locale.ENGLISH)))) {
                                            android.content.Intent intent = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.SubredditView.class);
                                            intent.putExtra(me.ccrama.redditslide.Activities.SubredditView.EXTRA_SUBREDDIT, GO_TO_SUB_FIELD.getText().toString());
                                            me.ccrama.redditslide.Activities.MainActivity.this.startActivityForResult(intent, 2002);
                                        } else {
                                            if (commentPager && (adapter instanceof me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapterComment)) {
                                                openingComments = null;
                                                toOpenComments = -1;
                                                ((me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapterComment) (adapter)).size = usedArray.size() + 1;
                                                adapter.notifyDataSetChanged();
                                                if (usedArray.contains(GO_TO_SUB_FIELD.getText().toString().toLowerCase(java.util.Locale.ENGLISH))) {
                                                    doPageSelectedComments(usedArray.indexOf(GO_TO_SUB_FIELD.getText().toString().toLowerCase(java.util.Locale.ENGLISH)));
                                                } else {
                                                    doPageSelectedComments(usedArray.indexOf(sideArrayAdapter.fitems.get(0)));
                                                }
                                            }
                                            if (usedArray.contains(GO_TO_SUB_FIELD.getText().toString().toLowerCase(java.util.Locale.ENGLISH))) {
                                                pager.setCurrentItem(usedArray.indexOf(GO_TO_SUB_FIELD.getText().toString().toLowerCase(java.util.Locale.ENGLISH)));
                                            } else {
                                                pager.setCurrentItem(usedArray.indexOf(sideArrayAdapter.fitems.get(0)));
                                            }
                                        }
                                        android.view.View view = me.ccrama.redditslide.Activities.MainActivity.this.getCurrentFocus();
                                        if (view != null) {
                                            // Hide the keyboard
                                            android.view.inputmethod.InputMethodManager imm = ((android.view.inputmethod.InputMethodManager) (getSystemService(android.content.Context.INPUT_METHOD_SERVICE)));
                                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                        }
                                        SUGGESTIONS_BACKGROUND.setVisibility(android.view.View.GONE);
                                        GO_TO_SUB_FIELD.setVisibility(android.view.View.GONE);
                                        CLOSE_BUTTON.setVisibility(android.view.View.GONE);
                                        if (me.ccrama.redditslide.SettingValues.single) {
                                            getSupportActionBar().setTitle(selectedSub);
                                        } else {
                                            // Set the title back to "Slide" or "Slide (debug)"
                                            getSupportActionBar().setTitle(tabViewModeTitle);
                                        }
                                    }
                                    return false;
                                }
                            });
                            GO_TO_SUB_FIELD.addTextChangedListener(new android.text.TextWatcher() {
                                @java.lang.Override
                                public void beforeTextChanged(java.lang.CharSequence charSequence, int i, int i2, int i3) {
                                }

                                @java.lang.Override
                                public void onTextChanged(java.lang.CharSequence charSequence, int i, int i2, int i3) {
                                }

                                @java.lang.Override
                                public void afterTextChanged(android.text.Editable editable) {
                                    final java.lang.String RESULT = GO_TO_SUB_FIELD.getText().toString().replaceAll(" ", "");
                                    TOOLBAR_SEARCH_SUGGEST_ADAPTER.getFilter().filter(RESULT);
                                }
                            });
                        }
                        return true;
                    }
                });
            }
        }
    }

    private android.animation.ValueAnimator slideAnimator(int start, int end, final android.view.View v) {
        android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofInt(start, end);
        animator.setInterpolator(new android.support.v4.view.animation.FastOutSlowInInterpolator());
        animator.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {
            @java.lang.Override
            public void onAnimationUpdate(android.animation.ValueAnimator valueAnimator) {
                // Update Height
                int value = ((java.lang.Integer) (valueAnimator.getAnimatedValue()));
                android.view.ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = value;
                v.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    /* Todo once API allows for getting the websocket URL  public class AsyncStartNotifSocket extends AsyncTask<Void, Void, Void>{
    @Override
    protected Void doInBackground(Void... params) {
    try {
    String access = Authentication.authentication.getString("websocket_url", "");

    LogUtil.v(access);
    com.neovisionaries.ws.client.WebSocket ws = new WebSocketFactory().createSocket(access);
    ws.addListener(new WebSocketListener() {
    @Override
    public void onStateChanged(com.neovisionaries.ws.client.WebSocket websocket, WebSocketState newState) throws Exception {

    }

    @Override
    public void onConnected(com.neovisionaries.ws.client.WebSocket websocket, Map<String, List<String>> headers) throws Exception {

    }

    @Override
    public void onConnectError(com.neovisionaries.ws.client.WebSocket websocket, WebSocketException cause) throws Exception {

    }

    @Override
    public void onDisconnected(com.neovisionaries.ws.client.WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {

    }

    @Override
    public void onFrame(com.neovisionaries.ws.client.WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onContinuationFrame(com.neovisionaries.ws.client.WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onTextFrame(com.neovisionaries.ws.client.WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onBinaryFrame(com.neovisionaries.ws.client.WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onCloseFrame(com.neovisionaries.ws.client.WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onPingFrame(com.neovisionaries.ws.client.WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onPongFrame(com.neovisionaries.ws.client.WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onTextMessage(com.neovisionaries.ws.client.WebSocket websocket, String s) throws Exception {
    LogUtil.v("Recieved" + s);

    }

    @Override
    public void onBinaryMessage(com.neovisionaries.ws.client.WebSocket websocket, byte[] binary) throws Exception {

    }

    @Override
    public void onSendingFrame(com.neovisionaries.ws.client.WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onFrameSent(com.neovisionaries.ws.client.WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onFrameUnsent(com.neovisionaries.ws.client.WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onError(com.neovisionaries.ws.client.WebSocket websocket, WebSocketException cause) throws Exception {

    }

    @Override
    public void onFrameError(com.neovisionaries.ws.client.WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onMessageError(com.neovisionaries.ws.client.WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames) throws Exception {

    }

    @Override
    public void onMessageDecompressionError(com.neovisionaries.ws.client.WebSocket websocket, WebSocketException cause, byte[] compressed) throws Exception {

    }

    @Override
    public void onTextMessageError(com.neovisionaries.ws.client.WebSocket websocket, WebSocketException cause, byte[] data) throws Exception {

    }

    @Override
    public void onSendError(com.neovisionaries.ws.client.WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onUnexpectedError(com.neovisionaries.ws.client.WebSocket websocket, WebSocketException cause) throws Exception {

    }

    @Override
    public void handleCallbackError(com.neovisionaries.ws.client.WebSocket websocket, Throwable cause) throws Exception {

    }

    @Override
    public void onSendingHandshake(com.neovisionaries.ws.client.WebSocket websocket, String requestLine, List<String[]> headers) throws Exception {

    }
    });
    ws.connect();
    } catch (IOException e) {
    e.printStackTrace();
    } catch (WebSocketException e) {
    e.printStackTrace();
    }
    return null;
    }
    }
     */
    public class AsyncGetSubreddit extends android.os.AsyncTask<java.lang.String, java.lang.Void, net.dean.jraw.models.Subreddit> {
        @java.lang.Override
        public void onPostExecute(net.dean.jraw.models.Subreddit subreddit) {
            if (subreddit != null)
                doSubOnlyStuff(subreddit);

        }

        @java.lang.Override
        protected net.dean.jraw.models.Subreddit doInBackground(java.lang.String... params) {
            try {
                return me.ccrama.redditslide.Authentication.reddit.getSubreddit(params[0]);
            } catch (java.lang.Exception e) {
                return null;
            }
        }
    }

    public class AsyncNotificationBadge extends android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void> {
        int count;

        boolean restart;

        int modCount;

        @java.lang.Override
        protected java.lang.Void doInBackground(java.lang.Void... params) {
            try {
                net.dean.jraw.models.LoggedInAccount me;
                if (me.ccrama.redditslide.Authentication.me == null) {
                    me.ccrama.redditslide.Authentication.me = me.ccrama.redditslide.Authentication.reddit.me();
                    me = me.ccrama.redditslide.Authentication.me;
                    if (me.ccrama.redditslide.Authentication.name.equalsIgnoreCase("loggedout")) {
                        me.ccrama.redditslide.Authentication.name = me.getFullName();
                        me.ccrama.redditslide.Reddit.appRestart.edit().putString("name", me.ccrama.redditslide.Authentication.name).apply();
                        restart = true;
                        return null;
                    }
                    me.ccrama.redditslide.Authentication.mod = me.isMod();
                    me.ccrama.redditslide.Authentication.authentication.edit().putBoolean(me.ccrama.redditslide.Reddit.SHARED_PREF_IS_MOD, me.ccrama.redditslide.Authentication.mod).apply();
                    if (me.ccrama.redditslide.Reddit.notificationTime != (-1)) {
                        me.ccrama.redditslide.Reddit.notifications = new me.ccrama.redditslide.Notifications.NotificationJobScheduler(me.ccrama.redditslide.Activities.MainActivity.this);
                        me.ccrama.redditslide.Reddit.notifications.start(getApplicationContext());
                    }
                    if (me.ccrama.redditslide.Reddit.cachedData.contains("toCache")) {
                        me.ccrama.redditslide.Reddit.autoCache = new me.ccrama.redditslide.Autocache.AutoCacheScheduler(me.ccrama.redditslide.Activities.MainActivity.this);
                        me.ccrama.redditslide.Reddit.autoCache.start(getApplicationContext());
                    }
                    final java.lang.String name = me.getFullName();
                    me.ccrama.redditslide.Authentication.name = name;
                    me.ccrama.redditslide.util.LogUtil.v("AUTHENTICATED");
                    if (me.ccrama.redditslide.Authentication.reddit.isAuthenticated()) {
                        final java.util.Set<java.lang.String> accounts = me.ccrama.redditslide.Authentication.authentication.getStringSet("accounts", new java.util.HashSet<java.lang.String>());
                        if (accounts.contains(name)) {
                            // convert to new system
                            accounts.remove(name);
                            accounts.add((name + ":") + me.ccrama.redditslide.Authentication.refresh);
                            me.ccrama.redditslide.Authentication.authentication.edit().putStringSet("accounts", accounts).commit();// force commit

                        }
                        me.ccrama.redditslide.Authentication.isLoggedIn = true;
                        me.ccrama.redditslide.Reddit.notFirst = true;
                    }
                } else {
                    me = me.ccrama.redditslide.Authentication.reddit.me();
                }
                count = me.getInboxCount();// Force reload of the LoggedInAccount object

                me.ccrama.redditslide.UserSubscriptions.doFriendsOfMain(me.ccrama.redditslide.Activities.MainActivity.this);
            } catch (java.lang.Exception e) {
                android.util.Log.w(me.ccrama.redditslide.util.LogUtil.getTag(), "Cannot fetch inbox count");
                count = -1;
            }
            return null;
        }

        @java.lang.Override
        protected void onPostExecute(java.lang.Void aVoid) {
            if (restart) {
                restartTheme();
                return;
            }
            if (me.ccrama.redditslide.Authentication.mod && me.ccrama.redditslide.Authentication.didOnline) {
                android.widget.RelativeLayout mod = headerMain.findViewById(me.ccrama.redditslide.R.id.mod);
                mod.setVisibility(android.view.View.VISIBLE);
                mod.setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                    @java.lang.Override
                    public void onSingleClick(android.view.View view) {
                        if ((me.ccrama.redditslide.UserSubscriptions.modOf != null) && (!me.ccrama.redditslide.UserSubscriptions.modOf.isEmpty())) {
                            android.content.Intent inte = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.ModQueue.class);
                            me.ccrama.redditslide.Activities.MainActivity.this.startActivity(inte);
                        }
                    }
                });
            }
            if (count != (-1)) {
                int oldCount = me.ccrama.redditslide.Reddit.appRestart.getInt("inbox", 0);
                if (count > oldCount) {
                    final android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(mToolbar, getResources().getQuantityString(me.ccrama.redditslide.R.plurals.new_messages, count - oldCount, count - oldCount), android.support.design.widget.Snackbar.LENGTH_LONG).setAction(me.ccrama.redditslide.R.string.btn_view, new me.ccrama.redditslide.util.OnSingleClickListener() {
                        @java.lang.Override
                        public void onSingleClick(android.view.View v) {
                            android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.MainActivity.this, me.ccrama.redditslide.Activities.Inbox.class);
                            i.putExtra(me.ccrama.redditslide.Activities.Inbox.EXTRA_UNREAD, true);
                            startActivity(i);
                        }
                    });
                    android.view.View view = s.getView();
                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(android.graphics.Color.WHITE);
                    s.show();
                }
                me.ccrama.redditslide.Reddit.appRestart.edit().putInt("inbox", count).apply();
            }
            android.view.View badge = headerMain.findViewById(me.ccrama.redditslide.R.id.count);
            if (count == 0) {
                if (badge != null) {
                    badge.setVisibility(android.view.View.GONE);
                }
                android.app.NotificationManager notificationManager = ((android.app.NotificationManager) (getSystemService(android.content.Context.NOTIFICATION_SERVICE)));
                notificationManager.cancel(0);
            } else if (count != (-1)) {
                if (badge != null) {
                    badge.setVisibility(android.view.View.VISIBLE);
                }
                ((android.widget.TextView) (headerMain.findViewById(me.ccrama.redditslide.R.id.count))).setText(java.lang.String.format(java.util.Locale.getDefault(), "%d", count));
            }
            /* Todo possibly
            View modBadge = headerMain.findViewById(R.id.count_mod);

            if (modCount == 0) {
            if (modBadge != null) modBadge.setVisibility(View.GONE);
            } else if (modCount != -1) {
            if (modBadge != null) modBadge.setVisibility(View.VISIBLE);
            ((TextView) headerMain.findViewById(R.id.count)).setText(String.format(Locale.getDefault(), "%d", count));
            }
             */
        }
    }

    public class OverviewPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
        protected me.ccrama.redditslide.Fragments.SubmissionsView mCurrentFragment;

        public OverviewPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
            pager.clearOnPageChangeListeners();
            pager.addOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {
                @java.lang.Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    if (positionOffset == 0) {
                        header.animate().translationY(0).setInterpolator(new android.view.animation.LinearInterpolator()).setDuration(180);
                        doSubSidebarNoLoad(usedArray.get(position));
                    }
                }

                @java.lang.Override
                public void onPageSelected(final int position) {
                    me.ccrama.redditslide.Reddit.currentPosition = position;
                    selectedSub = usedArray.get(position);
                    me.ccrama.redditslide.Fragments.SubmissionsView page = ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment()));
                    if (hea != null) {
                        hea.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getColor(selectedSub));
                        if (accountsArea != null) {
                            accountsArea.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(selectedSub));
                        }
                    }
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
                    if (me.ccrama.redditslide.SettingValues.single || (mTabLayout == null)) {
                        // Smooth out the fading animation for the toolbar subreddit search UI
                        if (((me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_TOOLBAR) || (me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_BOTH)) && (findViewById(me.ccrama.redditslide.R.id.toolbar_search).getVisibility() == android.view.View.VISIBLE)) {
                            new android.os.Handler().postDelayed(new java.lang.Runnable() {
                                @java.lang.Override
                                public void run() {
                                    getSupportActionBar().setTitle(selectedSub);
                                }
                            }, ANIMATE_DURATION + ANIMATE_DURATION_OFFSET);
                        } else {
                            getSupportActionBar().setTitle(selectedSub);
                        }
                    } else {
                        mTabLayout.setSelectedTabIndicatorColor(new me.ccrama.redditslide.ColorPreferences(me.ccrama.redditslide.Activities.MainActivity.this).getColor(selectedSub));
                    }
                    if ((page != null) && (page.adapter != null)) {
                        me.ccrama.redditslide.Adapters.SubredditPosts p = page.adapter.dataSet;
                        if (p.offline && (!me.ccrama.redditslide.Activities.MainActivity.isRestart)) {
                            p.doMainActivityOffline(me.ccrama.redditslide.Activities.MainActivity.this, p.displayer);
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
            me.ccrama.redditslide.Fragments.SubmissionsView f = new me.ccrama.redditslide.Fragments.SubmissionsView();
            android.os.Bundle args = new android.os.Bundle();
            java.lang.String name;
            if (me.ccrama.redditslide.Activities.MainActivity.multiNameToSubsMap.containsKey(usedArray.get(i))) {
                name = me.ccrama.redditslide.Activities.MainActivity.multiNameToSubsMap.get(usedArray.get(i));
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
                me.ccrama.redditslide.Activities.MainActivity.shouldLoad = usedArray.get(reloadItemNumber);
                if (me.ccrama.redditslide.Activities.MainActivity.multiNameToSubsMap.containsKey(usedArray.get(reloadItemNumber))) {
                    me.ccrama.redditslide.Activities.MainActivity.shouldLoad = me.ccrama.redditslide.Activities.MainActivity.multiNameToSubsMap.get(usedArray.get(reloadItemNumber));
                } else {
                    me.ccrama.redditslide.Activities.MainActivity.shouldLoad = usedArray.get(reloadItemNumber);
                }
            }
        }

        @java.lang.Override
        public android.os.Parcelable saveState() {
            return null;
        }

        public void doSetPrimary(java.lang.Object object, int position) {
            if ((((object != null) && (getCurrentFragment() != object)) && (position != toOpenComments)) && (object instanceof me.ccrama.redditslide.Fragments.SubmissionsView)) {
                me.ccrama.redditslide.Activities.MainActivity.shouldLoad = usedArray.get(position);
                if (me.ccrama.redditslide.Activities.MainActivity.multiNameToSubsMap.containsKey(usedArray.get(position))) {
                    me.ccrama.redditslide.Activities.MainActivity.shouldLoad = me.ccrama.redditslide.Activities.MainActivity.multiNameToSubsMap.get(usedArray.get(position));
                } else {
                    me.ccrama.redditslide.Activities.MainActivity.shouldLoad = usedArray.get(position);
                }
                mCurrentFragment = ((me.ccrama.redditslide.Fragments.SubmissionsView) (object));
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
                return me.ccrama.redditslide.Activities.MainActivity.abbreviate(usedArray.get(position), 25);
            } else {
                return "";
            }
        }
    }

    public class OverviewPagerAdapterComment extends me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapter {
        public int size = usedArray.size();

        public android.support.v4.app.Fragment storedFragment;

        private me.ccrama.redditslide.Fragments.CommentPage mCurrentComments;

        public OverviewPagerAdapterComment(android.support.v4.app.FragmentManager fm) {
            super(fm);
            pager.clearOnPageChangeListeners();
            pager.addOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {
                @java.lang.Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    if (positionOffset == 0) {
                        if (position != toOpenComments) {
                            pager.setSwipeLeftOnly(true);
                            header.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getColor(usedArray.get(position)));
                            doPageSelectedComments(position);
                            if (((position == (toOpenComments - 1)) && (adapter != null)) && (adapter.getCurrentFragment() != null)) {
                                me.ccrama.redditslide.Fragments.SubmissionsView page = ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment()));
                                if ((page != null) && (page.adapter != null)) {
                                    page.adapter.refreshView();
                                }
                            }
                        } else {
                            if (mAsyncGetSubreddit != null) {
                                mAsyncGetSubreddit.cancel(true);
                            }
                            if (header.getTranslationY() == 0) {
                                header.animate().translationY((-header.getHeight()) * 1.5F).setInterpolator(new android.view.animation.LinearInterpolator()).setDuration(180);
                            }
                            pager.setSwipeLeftOnly(true);
                            themeSystemBars(openingComments.getSubredditName().toLowerCase(java.util.Locale.ENGLISH));
                            setRecentBar(openingComments.getSubredditName().toLowerCase(java.util.Locale.ENGLISH));
                        }
                    }
                }

                @java.lang.Override
                public void onPageSelected(final int position) {
                    if (((position == (toOpenComments - 1)) && (adapter != null)) && (adapter.getCurrentFragment() != null)) {
                        me.ccrama.redditslide.Fragments.SubmissionsView page = ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment()));
                        if ((page != null) && (page.adapter != null)) {
                            page.adapter.refreshView();
                            me.ccrama.redditslide.Adapters.SubredditPosts p = page.adapter.dataSet;
                            if (p.offline && (!me.ccrama.redditslide.Activities.MainActivity.isRestart)) {
                                p.doMainActivityOffline(me.ccrama.redditslide.Activities.MainActivity.this, p.displayer);
                            }
                        }
                    } else {
                        me.ccrama.redditslide.Fragments.SubmissionsView page = ((me.ccrama.redditslide.Fragments.SubmissionsView) (adapter.getCurrentFragment()));
                        if ((page != null) && (page.adapter != null)) {
                            me.ccrama.redditslide.Adapters.SubredditPosts p = page.adapter.dataSet;
                            if (p.offline && (!me.ccrama.redditslide.Activities.MainActivity.isRestart)) {
                                p.doMainActivityOffline(me.ccrama.redditslide.Activities.MainActivity.this, p.displayer);
                            }
                        }
                    }
                }

                @java.lang.Override
                public void onPageScrollStateChanged(int state) {
                }
            });
            notifyDataSetChanged();
        }

        @java.lang.Override
        public int getCount() {
            if (usedArray == null) {
                return 1;
            } else {
                return size;
            }
        }

        @java.lang.Override
        public android.support.v4.app.Fragment getItem(int i) {
            if ((openingComments == null) || (i != toOpenComments)) {
                me.ccrama.redditslide.Fragments.SubmissionsView f = new me.ccrama.redditslide.Fragments.SubmissionsView();
                android.os.Bundle args = new android.os.Bundle();
                if (usedArray.size() > i) {
                    if (me.ccrama.redditslide.Activities.MainActivity.multiNameToSubsMap.containsKey(usedArray.get(i))) {
                        // if (usedArray.get(i).co
                        args.putString("id", me.ccrama.redditslide.Activities.MainActivity.multiNameToSubsMap.get(usedArray.get(i)));
                    } else {
                        args.putString("id", usedArray.get(i));
                    }
                }
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
                args.putString("baseSubreddit", subToDo);
                f.setArguments(args);
                return f;
            }
        }

        @java.lang.Override
        public android.os.Parcelable saveState() {
            return null;
        }

        @java.lang.Override
        public void doSetPrimary(java.lang.Object object, int position) {
            if (position != toOpenComments) {
                if (me.ccrama.redditslide.Activities.MainActivity.multiNameToSubsMap.containsKey(usedArray.get(position))) {
                    me.ccrama.redditslide.Activities.MainActivity.shouldLoad = me.ccrama.redditslide.Activities.MainActivity.multiNameToSubsMap.get(usedArray.get(position));
                } else {
                    me.ccrama.redditslide.Activities.MainActivity.shouldLoad = usedArray.get(position);
                }
                if (getCurrentFragment() != object) {
                    mCurrentFragment = ((me.ccrama.redditslide.Fragments.SubmissionsView) (object));
                    if (((mCurrentFragment != null) && (mCurrentFragment.posts == null)) && mCurrentFragment.isAdded()) {
                        mCurrentFragment.doAdapter();
                    }
                }
            } else if (object instanceof me.ccrama.redditslide.Fragments.CommentPage) {
                mCurrentComments = ((me.ccrama.redditslide.Fragments.CommentPage) (object));
            }
        }

        public android.support.v4.app.Fragment getCurrentFragment() {
            return mCurrentFragment;
        }

        @java.lang.Override
        public int getItemPosition(java.lang.Object object) {
            if (object != storedFragment)
                return android.support.v4.view.PagerAdapter.POSITION_NONE;

            return android.support.v4.view.PagerAdapter.POSITION_UNCHANGED;
        }

        @java.lang.Override
        public java.lang.CharSequence getPageTitle(int position) {
            if ((usedArray != null) && (position != toOpenComments)) {
                return me.ccrama.redditslide.Activities.MainActivity.abbreviate(usedArray.get(position), 25);
            } else {
                return "";
            }
        }
    }
}