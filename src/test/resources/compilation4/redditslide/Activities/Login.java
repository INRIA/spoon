package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.util.LogUtil;
import java.util.Locale;
import java.util.Set;
import me.ccrama.redditslide.Visuals.GetClosestColor;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Visuals.Palette;
import java.util.ArrayList;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.CaseInsensitiveArrayList;
import me.ccrama.redditslide.UserSubscriptions;
import java.util.HashSet;
/**
 * Created by ccrama on 5/27/2015.
 */
public class Login extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    private static final java.lang.String CLIENT_ID = "KI2Nl9A_ouG9Qw";

    private static final java.lang.String REDIRECT_URL = "http://www.ccrama.me";

    android.app.Dialog d;

    me.ccrama.redditslide.CaseInsensitiveArrayList subNames;

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstance) {
        overrideSwipeFromAnywhere();
        super.onCreate(savedInstance);
        applyColorTheme("");
        try {
            setContentView(me.ccrama.redditslide.R.layout.activity_login);
        } catch (java.lang.Exception e) {
            finish();
            return;
        }
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.title_login, true, true);
        java.lang.String[] scopes = new java.lang.String[]{ "identity", "modcontributors", "modconfig", "modothers", "modwiki", "creddits", "livemanage", "account", "privatemessages", "modflair", "modlog", "report", "modposts", "modwiki", "read", "vote", "edit", "submit", "subscribe", "save", "wikiread", "flair", "history", "mysubreddits", "wikiedit" };
        if (me.ccrama.redditslide.Authentication.reddit == null) {
            new me.ccrama.redditslide.Authentication(getApplicationContext());
        }
        final net.dean.jraw.http.oauth.OAuthHelper oAuthHelper = me.ccrama.redditslide.Authentication.reddit.getOAuthHelper();
        final net.dean.jraw.http.oauth.Credentials credentials = net.dean.jraw.http.oauth.Credentials.installedApp(me.ccrama.redditslide.Activities.Login.CLIENT_ID, me.ccrama.redditslide.Activities.Login.REDIRECT_URL);
        java.lang.String authorizationUrl = oAuthHelper.getAuthorizationUrl(credentials, true, scopes).toExternalForm();
        authorizationUrl = authorizationUrl.replace("www.", "i.");
        authorizationUrl = authorizationUrl.replace("%3A%2F%2Fi", "://www");
        android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "Auth URL: " + authorizationUrl);
        final android.webkit.WebView webView = ((android.webkit.WebView) (findViewById(me.ccrama.redditslide.R.id.web)));
        webView.clearCache(true);
        webView.clearHistory();
        android.webkit.WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setMinimumFontSize(1);
        webSettings.setMinimumLogicalFontSize(1);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            android.webkit.CookieManager.getInstance().removeAllCookies(null);
            android.webkit.CookieManager.getInstance().flush();
        } else {
            android.webkit.CookieSyncManager cookieSyncMngr = android.webkit.CookieSyncManager.createInstance(this);
            cookieSyncMngr.startSync();
            android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
        webView.setWebViewClient(new android.webkit.WebViewClient() {
            @java.lang.Override
            public void onPageStarted(android.webkit.WebView view, java.lang.String url, android.graphics.Bitmap favicon) {
                me.ccrama.redditslide.util.LogUtil.v(url);
                if (url.contains("code=")) {
                    android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "WebView URL: " + url);
                    // Authentication code received, prevent HTTP call from being made.
                    webView.stopLoading();
                    new me.ccrama.redditslide.Activities.Login.UserChallengeTask(oAuthHelper, credentials).execute(url);
                    webView.setVisibility(android.view.View.GONE);
                }
            }
        });
        webView.loadUrl(authorizationUrl);
    }

    @java.lang.Override
    @android.annotation.TargetApi(android.os.Build.VERSION_CODES.O)
    protected void setAutofill() {
        getWindow().getDecorView().setImportantForAutofill(android.view.View.IMPORTANT_FOR_AUTOFILL_AUTO);
    }

    private void doSubStrings(java.util.ArrayList<net.dean.jraw.models.Subreddit> subs) {
        subNames = new me.ccrama.redditslide.CaseInsensitiveArrayList();
        for (net.dean.jraw.models.Subreddit s : subs) {
            subNames.add(s.getDisplayName().toLowerCase(java.util.Locale.ENGLISH));
        }
        subNames = me.ccrama.redditslide.UserSubscriptions.sort(subNames);
        if (!subNames.contains("slideforreddit")) {
            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setTitle(me.ccrama.redditslide.R.string.login_subscribe_rslideforreddit).setMessage(me.ccrama.redditslide.R.string.login_subscribe_rslideforreddit_desc).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                @java.lang.Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    subNames.add(2, "slideforreddit");
                    me.ccrama.redditslide.UserSubscriptions.setSubscriptions(subNames);
                    me.ccrama.redditslide.Reddit.forceRestart(me.ccrama.redditslide.Activities.Login.this, true);
                }
            }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, new android.content.DialogInterface.OnClickListener() {
                @java.lang.Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    me.ccrama.redditslide.UserSubscriptions.setSubscriptions(subNames);
                    me.ccrama.redditslide.Reddit.forceRestart(me.ccrama.redditslide.Activities.Login.this, true);
                }
            }).setCancelable(false).show();
        } else {
            me.ccrama.redditslide.UserSubscriptions.setSubscriptions(subNames);
            me.ccrama.redditslide.Reddit.forceRestart(this, true);
        }
    }

    public void doLastStuff(final java.util.ArrayList<net.dean.jraw.models.Subreddit> subs) {
        d.dismiss();
        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setTitle(me.ccrama.redditslide.R.string.login_sync_colors).setMessage(me.ccrama.redditslide.R.string.login_sync_colors_desc).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                for (net.dean.jraw.models.Subreddit s : subs) {
                    if ((s.getDataNode().has("key_color") && (!s.getDataNode().get("key_color").asText().isEmpty())) && (me.ccrama.redditslide.Visuals.Palette.getColor(s.getDisplayName().toLowerCase(java.util.Locale.ENGLISH)) == me.ccrama.redditslide.Visuals.Palette.getDefaultColor())) {
                        me.ccrama.redditslide.Visuals.Palette.setColor(s.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), me.ccrama.redditslide.Visuals.GetClosestColor.getClosestColor(s.getDataNode().get("key_color").asText(), me.ccrama.redditslide.Activities.Login.this));
                    }
                }
                doSubStrings(subs);
            }
        }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                doSubStrings(subs);
            }
        }).setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {
            @java.lang.Override
            public void onDismiss(android.content.DialogInterface dialog) {
                doSubStrings(subs);
            }
        }).create().show();
    }

    private final class UserChallengeTask extends android.os.AsyncTask<java.lang.String, java.lang.Void, net.dean.jraw.http.oauth.OAuthData> {
        private final net.dean.jraw.http.oauth.OAuthHelper mOAuthHelper;

        private final net.dean.jraw.http.oauth.Credentials mCredentials;

        private com.afollestad.materialdialogs.MaterialDialog mMaterialDialog;

        public UserChallengeTask(net.dean.jraw.http.oauth.OAuthHelper oAuthHelper, net.dean.jraw.http.oauth.Credentials credentials) {
            android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "UserChallengeTask()");
            mOAuthHelper = oAuthHelper;
            mCredentials = credentials;
        }

        @java.lang.Override
        protected void onPreExecute() {
            // Show a dialog to indicate progress
            com.afollestad.materialdialogs.MaterialDialog.Builder builder = new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.Login.this).title(me.ccrama.redditslide.R.string.login_authenticating).progress(true, 0).content(me.ccrama.redditslide.R.string.misc_please_wait).cancelable(false);
            mMaterialDialog = builder.build();
            mMaterialDialog.show();
        }

        @java.lang.Override
        protected net.dean.jraw.http.oauth.OAuthData doInBackground(java.lang.String... params) {
            try {
                net.dean.jraw.http.oauth.OAuthData oAuthData = mOAuthHelper.onUserChallenge(params[0], mCredentials);
                if (oAuthData != null) {
                    me.ccrama.redditslide.Authentication.reddit.authenticate(oAuthData);
                    me.ccrama.redditslide.Authentication.isLoggedIn = true;
                    java.lang.String refreshToken = me.ccrama.redditslide.Authentication.reddit.getOAuthData().getRefreshToken();
                    android.content.SharedPreferences.Editor editor = me.ccrama.redditslide.Authentication.authentication.edit();
                    java.util.Set<java.lang.String> accounts = me.ccrama.redditslide.Authentication.authentication.getStringSet("accounts", new java.util.HashSet<java.lang.String>());
                    net.dean.jraw.models.LoggedInAccount me = me.ccrama.redditslide.Authentication.reddit.me();
                    accounts.add((me.getFullName() + ":") + refreshToken);
                    me.ccrama.redditslide.Authentication.name = me.getFullName();
                    editor.putStringSet("accounts", accounts);
                    java.util.Set<java.lang.String> tokens = me.ccrama.redditslide.Authentication.authentication.getStringSet("tokens", new java.util.HashSet<java.lang.String>());
                    tokens.add(refreshToken);
                    editor.putStringSet("tokens", tokens);
                    editor.putString("lasttoken", refreshToken);
                    editor.remove("backedCreds");
                    me.ccrama.redditslide.Reddit.appRestart.edit().remove("back").commit();
                    editor.commit();
                } else {
                    android.util.Log.e(me.ccrama.redditslide.util.LogUtil.getTag(), "Passed in OAuthData was null");
                }
                return oAuthData;
            } catch (java.lang.IllegalStateException | net.dean.jraw.http.NetworkException | net.dean.jraw.http.oauth.OAuthException e) {
                // Handle me gracefully
                android.util.Log.e(me.ccrama.redditslide.util.LogUtil.getTag(), "OAuth failed");
                android.util.Log.e(me.ccrama.redditslide.util.LogUtil.getTag(), e.getMessage());
            }
            return null;
        }

        @java.lang.Override
        protected void onPostExecute(net.dean.jraw.http.oauth.OAuthData oAuthData) {
            // Dismiss old progress dialog
            mMaterialDialog.dismiss();
            if (oAuthData != null) {
                me.ccrama.redditslide.Reddit.appRestart.edit().putBoolean("firststarting", true).apply();
                me.ccrama.redditslide.UserSubscriptions.switchAccounts();
                d = new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.Login.this).cancelable(false).title(me.ccrama.redditslide.R.string.login_starting).progress(true, 0).content(me.ccrama.redditslide.R.string.login_starting_desc).build();
                d.show();
                me.ccrama.redditslide.UserSubscriptions.syncSubredditsGetObjectAsync(me.ccrama.redditslide.Activities.Login.this);
            } else {
                // Show a dialog if data is null
                com.afollestad.materialdialogs.MaterialDialog.Builder builder = new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.Login.this).title(me.ccrama.redditslide.R.string.err_authentication).content(me.ccrama.redditslide.R.string.login_failed_err_decline).neutralText(me.ccrama.redditslide.R.string.btn_ok).onNeutral(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                    @java.lang.Override
                    public void onClick(@android.support.annotation.Nullable
                    com.afollestad.materialdialogs.MaterialDialog dialog, @android.support.annotation.Nullable
                    com.afollestad.materialdialogs.DialogAction which) {
                        me.ccrama.redditslide.Reddit.forceRestart(me.ccrama.redditslide.Activities.Login.this, true);
                        finish();
                    }
                });
                builder.show();
            }
        }
    }
}