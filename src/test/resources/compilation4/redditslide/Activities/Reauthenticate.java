package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.util.LogUtil;
import java.util.Set;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import java.util.ArrayList;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.UserSubscriptions;
import java.util.HashSet;
/**
 * Created by ccrama on 5/27/2015.
 */
public class Reauthenticate extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    private static final java.lang.String CLIENT_ID = "KI2Nl9A_ouG9Qw";

    private static final java.lang.String REDIRECT_URL = "http://www.ccrama.me";

    android.app.Dialog d;

    java.util.ArrayList<java.lang.String> subNames;

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstance) {
        super.onCreate(savedInstance);
        applyColorTheme("");
        setContentView(me.ccrama.redditslide.R.layout.activity_login);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, "Re-authenticate", true, true);
        java.lang.String[] scopes = new java.lang.String[]{ "identity", "modcontributors", "modconfig", "modothers", "modwiki", "creddits", "livemanage", "account", "privatemessages", "modflair", "modlog", "report", "modposts", "modwiki", "read", "vote", "edit", "submit", "subscribe", "save", "wikiread", "flair", "history", "mysubreddits", "wikiedit" };
        final net.dean.jraw.http.oauth.OAuthHelper oAuthHelper = me.ccrama.redditslide.Authentication.reddit.getOAuthHelper();
        final net.dean.jraw.http.oauth.Credentials credentials = net.dean.jraw.http.oauth.Credentials.installedApp(me.ccrama.redditslide.Activities.Reauthenticate.CLIENT_ID, me.ccrama.redditslide.Activities.Reauthenticate.REDIRECT_URL);
        java.lang.String authorizationUrl = oAuthHelper.getAuthorizationUrl(credentials, true, scopes).toExternalForm();
        authorizationUrl = authorizationUrl.replace("www.", "i.");
        authorizationUrl = authorizationUrl.replace("%3A%2F%2Fi", "://www");
        android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "Auth URL: " + authorizationUrl);
        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
        cookieManager.removeAllCookie();
        final android.webkit.WebView webView = ((android.webkit.WebView) (findViewById(me.ccrama.redditslide.R.id.web)));
        webView.loadUrl(authorizationUrl);
        webView.setWebChromeClient(new android.webkit.WebChromeClient() {
            @java.lang.Override
            public void onProgressChanged(android.webkit.WebView view, int newProgress) {
                // activity.setProgress(newProgress * 1000);
            }
        });
        webView.setWebViewClient(new android.webkit.WebViewClient() {
            @java.lang.Override
            public void onPageStarted(android.webkit.WebView view, java.lang.String url, android.graphics.Bitmap favicon) {
                if (url.contains("code=")) {
                    android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "WebView URL: " + url);
                    // Authentication code received, prevent HTTP call from being made.
                    webView.stopLoading();
                    new me.ccrama.redditslide.Activities.Reauthenticate.UserChallengeTask(oAuthHelper, credentials).execute(url);
                    webView.setVisibility(android.view.View.GONE);
                    webView.clearCache(true);
                    webView.clearHistory();
                }
            }
        });
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
            com.afollestad.materialdialogs.MaterialDialog.Builder builder = new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.Reauthenticate.this).title(me.ccrama.redditslide.R.string.login_authenticating).progress(true, 0).content(me.ccrama.redditslide.R.string.misc_please_wait).cancelable(false);
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
                    java.lang.String toRemove = "";
                    for (java.lang.String s : accounts) {
                        if (s.contains(me.getFullName())) {
                            toRemove = s;
                        }
                    }
                    if (!toRemove.isEmpty())
                        accounts.remove(toRemove);

                    accounts.add((me.getFullName() + ":") + refreshToken);
                    me.ccrama.redditslide.Authentication.name = me.getFullName();
                    editor.putStringSet("accounts", accounts);
                    java.util.Set<java.lang.String> tokens = me.ccrama.redditslide.Authentication.authentication.getStringSet("tokens", new java.util.HashSet<java.lang.String>());
                    tokens.add(refreshToken);
                    editor.putStringSet("tokens", tokens);
                    editor.putString("lasttoken", refreshToken);
                    editor.remove("backedCreds");
                    me.ccrama.redditslide.Reddit.appRestart.edit().remove("back").apply();
                    editor.apply();
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
            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.Reauthenticate.this).setTitle(me.ccrama.redditslide.R.string.reauth_complete).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                @java.lang.Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    finish();
                }
            }).setCancelable(false).setOnCancelListener(new android.content.DialogInterface.OnCancelListener() {
                @java.lang.Override
                public void onCancel(android.content.DialogInterface dialog) {
                    finish();
                }
            }).show();
        }
    }
}