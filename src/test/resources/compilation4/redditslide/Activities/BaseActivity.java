package me.ccrama.redditslide.Activities;
import java.util.Locale;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Visuals.FontPreferences;
import me.ccrama.redditslide.ForceTouch.PeekViewActivity;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.SwipeLayout.SwipeBackLayout;
import me.ccrama.redditslide.SwipeLayout.Utils;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.ColorPreferences;
import me.ccrama.redditslide.SwipeLayout.app.SwipeBackActivityBase;
import me.ccrama.redditslide.SwipeLayout.app.SwipeBackActivityHelper;
import me.ccrama.redditslide.SettingValues;
/**
 * This is an activity which is the base for most of Slide's activities. It has support for handling
 * of swiping, setting up the AppBar (toolbar), and coloring of applicable views.
 */
public class BaseActivity extends me.ccrama.redditslide.ForceTouch.PeekViewActivity implements android.nfc.NfcAdapter.CreateNdefMessageCallback , android.nfc.NfcAdapter.OnNdefPushCompleteCallback , me.ccrama.redditslide.SwipeLayout.app.SwipeBackActivityBase {
    @android.support.annotation.Nullable
    public android.support.v7.widget.Toolbar mToolbar;

    protected me.ccrama.redditslide.SwipeLayout.app.SwipeBackActivityHelper mHelper;

    protected boolean overrideRedditSwipeAnywhere = false;

    protected boolean enableSwipeBackLayout = true;

    protected boolean overrideSwipeFromAnywhere = false;

    protected boolean verticalExit = false;

    android.nfc.NfcAdapter mNfcAdapter;

    /**
     * Enable fullscreen immersive mode if setting is checked
     */
    @java.lang.Override
    public void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (me.ccrama.redditslide.SettingValues.immersiveMode) {
            if (hasFocus) {
                hideDecor();
            }
        }
    }

    public void hideDecor() {
        try {
            if (me.ccrama.redditslide.SettingValues.immersiveMode) {
                final android.view.View decorView = getWindow().getDecorView();
                decorView.setSystemUiVisibility(((((android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE | android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION) | android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) | android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) | android.view.View.SYSTEM_UI_FLAG_FULLSCREEN) | android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                decorView.setOnSystemUiVisibilityChangeListener(new android.view.View.OnSystemUiVisibilityChangeListener() {
                    @java.lang.Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        if (visibility == 0) {
                            decorView.setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE | android.view.View.SYSTEM_UI_FLAG_FULLSCREEN);
                        } else {
                            decorView.setSystemUiVisibility(((((android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE | android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION) | android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) | android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) | android.view.View.SYSTEM_UI_FLAG_FULLSCREEN) | android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                        }
                    }
                });
            }
        } catch (java.lang.Exception ignored) {
        }
    }

    public void showDecor() {
        try {
            if (!me.ccrama.redditslide.SettingValues.immersiveMode) {
                final android.view.View decorView = getWindow().getDecorView();
                decorView.setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_VISIBLE);
                decorView.setOnSystemUiVisibilityChangeListener(null);
            }
        } catch (java.lang.Exception ignored) {
        }
    }

    @java.lang.Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            try {
                onBackPressed();
            } catch (java.lang.IllegalStateException ignored) {
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean shouldInterceptAlways = false;

    /**
     * Force English locale if setting is checked
     */
    public void applyOverrideLanguage() {
        if (me.ccrama.redditslide.SettingValues.overrideLanguage) {
            java.util.Locale locale = new java.util.Locale("en", "US");
            java.util.Locale.setDefault(locale);
            android.content.res.Configuration config = new android.content.res.Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }
    }

    @java.lang.Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        applyOverrideLanguage();
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setAutofill();
        }
        /**
         * Enable fullscreen immersive mode if setting is checked
         *
         * Adding this check in the onCreate method prevents the status/nav bars from appearing
         * briefly when changing from one activity to another
         */
        hideDecor();
        if (enableSwipeBackLayout) {
            mHelper = new me.ccrama.redditslide.SwipeLayout.app.SwipeBackActivityHelper(this);
            mHelper.onActivityCreate();
            if (me.ccrama.redditslide.SettingValues.swipeAnywhere || overrideRedditSwipeAnywhere) {
                if (overrideSwipeFromAnywhere) {
                    shouldInterceptAlways = true;
                } else {
                    if (verticalExit) {
                        mHelper.getSwipeBackLayout().setEdgeTrackingEnabled((me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_LEFT | me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_BOTTOM) | me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_TOP);
                    } else {
                        mHelper.getSwipeBackLayout().setEdgeTrackingEnabled(me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_LEFT | me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_TOP);
                    }
                    mHelper.getSwipeBackLayout().setFullScreenSwipeEnabled(true);
                }
            } else {
                shouldInterceptAlways = true;
            }
        }
    }

    @android.annotation.TargetApi(android.os.Build.VERSION_CODES.O)
    protected void setAutofill() {
        getWindow().getDecorView().setImportantForAutofill(android.view.View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
    }

    @java.lang.Override
    protected void onPostCreate(android.os.Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (enableSwipeBackLayout)
            mHelper.onPostCreate();

    }

    @java.lang.Override
    public android.view.View findViewById(int id) {
        android.view.View v = super.findViewById(id);
        if ((v == null) && (mHelper != null))
            return mHelper.findViewById(id);

        return v;
    }

    @java.lang.Override
    public me.ccrama.redditslide.SwipeLayout.SwipeBackLayout getSwipeBackLayout() {
        if (enableSwipeBackLayout) {
            return mHelper.getSwipeBackLayout();
        } else {
            return null;
        }
    }

    @java.lang.Override
    public void setSwipeBackEnable(boolean enable) {
        if (enableSwipeBackLayout)
            getSwipeBackLayout().setEnableGesture(enable);

    }

    @java.lang.Override
    public void scrollToFinishActivity() {
        if (enableSwipeBackLayout) {
            me.ccrama.redditslide.SwipeLayout.Utils.convertActivityToTranslucent(this);
            getSwipeBackLayout().scrollToFinishActivity();
        }
    }

    /**
     * Disables the Swipe-Back-Layout. Should be called before calling super.onCreate()
     */
    protected void disableSwipeBackLayout() {
        enableSwipeBackLayout = false;
    }

    protected void overrideSwipeFromAnywhere() {
        overrideSwipeFromAnywhere = true;
    }

    protected void swipeVerticalExit() {
        verticalExit = true;
    }

    protected void overrideRedditSwipeAnywhere() {
        overrideRedditSwipeAnywhere = true;
    }

    /**
     * Applies the activity's base color theme. Should be called before inflating any layouts.
     */
    protected void applyColorTheme() {
        getTheme().applyStyle(new me.ccrama.redditslide.Visuals.FontPreferences(this).getCommentFontStyle().getResId(), true);
        getTheme().applyStyle(new me.ccrama.redditslide.Visuals.FontPreferences(this).getPostFontStyle().getResId(), true);
        getTheme().applyStyle(new me.ccrama.redditslide.ColorPreferences(this).getFontStyle().getBaseId(), true);
    }

    /**
     * Applies the activity's base color theme based on the theme of a specific subreddit. Should be
     * called before inflating any layouts.
     *
     * @param subreddit
     * 		The subreddit to base the theme on
     */
    protected void applyColorTheme(java.lang.String subreddit) {
        getTheme().applyStyle(new me.ccrama.redditslide.Visuals.FontPreferences(this).getPostFontStyle().getResId(), true);
        getTheme().applyStyle(new me.ccrama.redditslide.ColorPreferences(this).getThemeSubreddit(subreddit), true);
        getTheme().applyStyle(new me.ccrama.redditslide.Visuals.FontPreferences(this).getCommentFontStyle().getResId(), true);
    }

    /**
     * Applies the activity's base color theme based on the theme of a specific subreddit. Should be
     * called before inflating any layouts.
     * <p/>
     * This will take the accent colors from the sub theme but return the AMOLED with contrast base
     * theme.
     *
     * @param subreddit
     * 		The subreddit to base the theme on
     */
    protected void applyDarkColorTheme(java.lang.String subreddit) {
        getTheme().applyStyle(new me.ccrama.redditslide.Visuals.FontPreferences(this).getPostFontStyle().getResId(), true);
        getTheme().applyStyle(new me.ccrama.redditslide.ColorPreferences(this).getDarkThemeSubreddit(subreddit), true);
        getTheme().applyStyle(new me.ccrama.redditslide.Visuals.FontPreferences(this).getCommentFontStyle().getResId(), true);
    }

    @java.lang.Override
    public void onResume() {
        super.onResume();
        me.ccrama.redditslide.Reddit.setDefaultErrorHandler(this);// set defualt reddit api issue handler

        hideDecor();
    }

    @java.lang.Override
    public void onDestroy() {
        super.onDestroy();
        me.ccrama.redditslide.Reddit.setDefaultErrorHandler(null);// remove defualt reddit api issue handler (mem leaks)

    }

    /**
     * Sets up the activity's support toolbar and colorizes the status bar.
     *
     * @param toolbar
     * 		The toolbar's id
     * @param title
     * 		String resource for the toolbar's title
     * @param enableUpButton
     * 		Whether or not the toolbar should have up navigation
     */
    protected void setupAppBar(@android.support.annotation.IdRes
    int toolbar, @android.support.annotation.StringRes
    int title, boolean enableUpButton, boolean colorToolbar) {
        setupAppBar(toolbar, getString(title), enableUpButton, colorToolbar);
    }

    /**
     * Sets up the activity's support toolbar and colorizes the status bar.
     *
     * @param toolbar
     * 		The toolbar's id
     * @param title
     * 		String to be set as the toolbar title
     * @param enableUpButton
     * 		Whether or not the toolbar should have up navigation
     */
    protected void setupAppBar(@android.support.annotation.IdRes
    int toolbar, java.lang.String title, boolean enableUpButton, boolean colorToolbar) {
        int systemBarColor = me.ccrama.redditslide.Visuals.Palette.getStatusBarColor();
        mToolbar = ((android.support.v7.widget.Toolbar) (findViewById(toolbar)));
        if (colorToolbar) {
            mToolbar.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getDefaultColor());
        }
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(enableUpButton);
            getSupportActionBar().setTitle(title);
        }
        themeSystemBars(systemBarColor);
        setRecentBar(title, systemBarColor);
    }

    /**
     * Sets up the activity's support toolbar and colorizes the status bar to a specific color
     *
     * @param toolbar
     * 		The toolbar's id
     * @param title
     * 		String to be set as the toolbar title
     * @param enableUpButton
     * 		Whether or not the toolbar should have up navigation
     * @param color
     * 		Color to color the tab bar
     */
    protected void setupAppBar(@android.support.annotation.IdRes
    int toolbar, java.lang.String title, boolean enableUpButton, int color, @android.support.annotation.IdRes
    int appbar) {
        int systemBarColor = me.ccrama.redditslide.Visuals.Palette.getDarkerColor(color);
        mToolbar = ((android.support.v7.widget.Toolbar) (findViewById(toolbar)));
        findViewById(appbar).setBackgroundColor(color);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(enableUpButton);
            getSupportActionBar().setTitle(title);
        }
        themeSystemBars(systemBarColor);
        setRecentBar(title, systemBarColor);
    }

    /**
     * Sets up the activity's support toolbar and colorizes the status bar. Applies color theming
     * based on the theme for the username specified.
     *
     * @param toolbar
     * 		The toolbar's id
     * @param title
     * 		String to be set as the toolbar title
     * @param enableUpButton
     * 		Whether or not the toolbar should have up navigation
     * @param username
     * 		The username to base the theme on
     */
    protected void setupUserAppBar(@android.support.annotation.IdRes
    int toolbar, @android.support.annotation.Nullable
    java.lang.String title, boolean enableUpButton, java.lang.String username) {
        int systemBarColor = me.ccrama.redditslide.Visuals.Palette.getUserStatusBarColor(username);
        mToolbar = ((android.support.v7.widget.Toolbar) (findViewById(toolbar)));
        mToolbar.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getColorUser(username));
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(enableUpButton);
            if (title != null) {
                getSupportActionBar().setTitle(title);
            }
        }
        themeSystemBars(systemBarColor);
        setRecentBar(title, systemBarColor);
    }

    /**
     * Sets up the activity's support toolbar and colorizes the status bar. Applies color theming
     * based on the theme for the subreddit specified.
     *
     * @param toolbar
     * 		The toolbar's id
     * @param title
     * 		String to be set as the toolbar title
     * @param enableUpButton
     * 		Whether or not the toolbar should have up navigation
     * @param subreddit
     * 		The subreddit to base the theme on
     */
    protected void setupSubredditAppBar(@android.support.annotation.IdRes
    int toolbar, java.lang.String title, boolean enableUpButton, java.lang.String subreddit) {
        mToolbar = ((android.support.v7.widget.Toolbar) (findViewById(toolbar)));
        mToolbar.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getColor(subreddit));
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(enableUpButton);
            getSupportActionBar().setTitle(title);
        }
        themeSystemBars(subreddit);
        setRecentBar(title, me.ccrama.redditslide.Visuals.Palette.getSubredditStatusBarColor(subreddit));
    }

    /**
     * Sets the status bar and navigation bar color for the activity based on a specific subreddit.
     *
     * @param subreddit
     * 		The subreddit to base the color on.
     */
    public void themeSystemBars(java.lang.String subreddit) {
        themeSystemBars(me.ccrama.redditslide.Visuals.Palette.getSubredditStatusBarColor(subreddit));
    }

    /**
     * Sets the status bar and navigation bar color for the activity
     *
     * @param color
     * 		The color to tint the bars with
     */
    protected void themeSystemBars(int color) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
            if (me.ccrama.redditslide.SettingValues.colorNavBar) {
                getWindow().setNavigationBarColor(color);
            }
        }
    }

    /**
     * Sets the title and color of the recent bar based on the subreddit
     *
     * @param subreddit
     * 		Name of the subreddit
     */
    public void setRecentBar(java.lang.String subreddit) {
        setRecentBar(subreddit, me.ccrama.redditslide.Visuals.Palette.getColor(subreddit));
    }

    public java.lang.String shareUrl;

    public void setShareUrl(java.lang.String url) {
        try {
            if (url != null) {
                shareUrl = url;
                mNfcAdapter = android.nfc.NfcAdapter.getDefaultAdapter(this);
                if (mNfcAdapter != null) {
                    // Register callback to set NDEF message
                    mNfcAdapter.setNdefPushMessageCallback(this, this);
                    // Register callback to listen for message-sent success
                    mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
                } else {
                    android.util.Log.i("LinkDetails", "NFC is not available on this device");
                }
            }
        } catch (java.lang.Exception e) {
        }
    }

    @java.lang.Override
    public android.nfc.NdefMessage createNdefMessage(android.nfc.NfcEvent event) {
        if (shareUrl != null) {
            return new android.nfc.NdefMessage(new android.nfc.NdefRecord[]{ android.nfc.NdefRecord.createUri(shareUrl) });
        }
        return null;
    }

    @java.lang.Override
    public void onNdefPushComplete(android.nfc.NfcEvent arg0) {
    }

    /**
     * Sets the title in the recent overview with the given title and the default color
     *
     * @param title
     * 		Title as string for the recent app bar
     * @param color
     * 		Color for the recent app bar
     */
    public void setRecentBar(@android.support.annotation.Nullable
    java.lang.String title, int color) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if ((title == null) || title.equals(""))
                title = getString(me.ccrama.redditslide.R.string.app_name);

            android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeResource(getResources(), title.equalsIgnoreCase("androidcirclejerk") ? me.ccrama.redditslide.R.drawable.matiasduarte : me.ccrama.redditslide.R.drawable.ic_launcher);
            setTaskDescription(new android.app.ActivityManager.TaskDescription(title, bitmap, color));
            bitmap.recycle();
        }
    }
}