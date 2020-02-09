package me.ccrama.redditslide;
import me.ccrama.redditslide.util.LogUtil;
import java.util.Calendar;
import java.util.UUID;
import java.util.HashSet;
import me.ccrama.redditslide.util.NetworkUtil;
/**
 * Created by ccrama on 3/30/2015.
 */
public class Authentication {
    private static final java.lang.String CLIENT_ID = "KI2Nl9A_ouG9Qw";

    private static final java.lang.String REDIRECT_URL = "http://www.ccrama.me";

    public static boolean isLoggedIn;

    public static net.dean.jraw.RedditClient reddit;

    public static net.dean.jraw.models.LoggedInAccount me;

    public static boolean mod;

    public static java.lang.String name;

    public static android.content.SharedPreferences authentication;

    public static java.lang.String refresh;

    public boolean hasDone;

    public static boolean didOnline;

    private static net.dean.jraw.http.OkHttpAdapter httpAdapter;

    public static void resetAdapter() {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
            @java.lang.Override
            protected java.lang.Void doInBackground(java.lang.Void... params) {
                if ((me.ccrama.redditslide.Authentication.httpAdapter != null) && (me.ccrama.redditslide.Authentication.httpAdapter.getNativeClient() != null)) {
                    me.ccrama.redditslide.Authentication.httpAdapter.getNativeClient().connectionPool().evictAll();
                }
                return null;
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public Authentication(android.content.Context context) {
        me.ccrama.redditslide.Reddit.setDefaultErrorHandler(context);
        if (me.ccrama.redditslide.util.NetworkUtil.isConnected(context)) {
            hasDone = true;
            me.ccrama.redditslide.Authentication.httpAdapter = new net.dean.jraw.http.OkHttpAdapter(me.ccrama.redditslide.Reddit.client, okhttp3.Protocol.HTTP_2);
            me.ccrama.redditslide.Authentication.isLoggedIn = false;
            me.ccrama.redditslide.Authentication.reddit = new net.dean.jraw.RedditClient(net.dean.jraw.http.UserAgent.of("android:me.ccrama.RedditSlide:v" + me.ccrama.redditslide.BuildConfig.VERSION_NAME), me.ccrama.redditslide.Authentication.httpAdapter);
            me.ccrama.redditslide.Authentication.reddit.setRetryLimit(2);
            if (me.ccrama.redditslide.BuildConfig.DEBUG)
                me.ccrama.redditslide.Authentication.reddit.setLoggingMode(net.dean.jraw.http.LoggingMode.ALWAYS);

            me.ccrama.redditslide.Authentication.didOnline = true;
            new me.ccrama.redditslide.Authentication.VerifyCredentials(context).execute();
        } else {
            me.ccrama.redditslide.Authentication.isLoggedIn = me.ccrama.redditslide.Reddit.appRestart.getBoolean("loggedin", false);
            me.ccrama.redditslide.Authentication.name = me.ccrama.redditslide.Reddit.appRestart.getString("name", "");
            if ((me.ccrama.redditslide.Authentication.name.isEmpty() || (!me.ccrama.redditslide.Authentication.isLoggedIn)) && (!me.ccrama.redditslide.Authentication.authentication.getString("lasttoken", "").isEmpty())) {
                for (java.lang.String s : me.ccrama.redditslide.Authentication.authentication.getStringSet("accounts", new java.util.HashSet<java.lang.String>())) {
                    if (s.contains(me.ccrama.redditslide.Authentication.authentication.getString("lasttoken", ""))) {
                        me.ccrama.redditslide.Authentication.name = s.split(":")[0];
                        break;
                    }
                }
                me.ccrama.redditslide.Authentication.isLoggedIn = true;
            }
        }
    }

    public void updateToken(android.content.Context c) {
        if (me.ccrama.redditslide.BuildConfig.DEBUG)
            me.ccrama.redditslide.util.LogUtil.v("Executing update token");

        if (me.ccrama.redditslide.Authentication.reddit == null) {
            hasDone = true;
            me.ccrama.redditslide.Authentication.isLoggedIn = false;
            me.ccrama.redditslide.Authentication.reddit = new net.dean.jraw.RedditClient(net.dean.jraw.http.UserAgent.of("android:me.ccrama.RedditSlide:v" + me.ccrama.redditslide.BuildConfig.VERSION_NAME));
            me.ccrama.redditslide.Authentication.reddit.setLoggingMode(net.dean.jraw.http.LoggingMode.ALWAYS);
            me.ccrama.redditslide.Authentication.didOnline = true;
            new me.ccrama.redditslide.Authentication.VerifyCredentials(c).execute();
        } else {
            new me.ccrama.redditslide.Authentication.UpdateToken(c).execute();
        }
    }

    public static boolean authedOnce;

    public class UpdateToken extends android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void> {
        android.content.Context context;

        public UpdateToken(android.content.Context c) {
            this.context = c;
        }

        @java.lang.Override
        protected java.lang.Void doInBackground(java.lang.Void... params) {
            if (me.ccrama.redditslide.Authentication.authedOnce && me.ccrama.redditslide.util.NetworkUtil.isConnected(context)) {
                me.ccrama.redditslide.Authentication.didOnline = true;
                if ((me.ccrama.redditslide.Authentication.name != null) && (!me.ccrama.redditslide.Authentication.name.isEmpty())) {
                    android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "REAUTH");
                    if (me.ccrama.redditslide.Authentication.isLoggedIn) {
                        try {
                            final net.dean.jraw.http.oauth.Credentials credentials = net.dean.jraw.http.oauth.Credentials.installedApp(me.ccrama.redditslide.Authentication.CLIENT_ID, me.ccrama.redditslide.Authentication.REDIRECT_URL);
                            android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "REAUTH LOGGED IN");
                            net.dean.jraw.http.oauth.OAuthHelper oAuthHelper = me.ccrama.redditslide.Authentication.reddit.getOAuthHelper();
                            oAuthHelper.setRefreshToken(me.ccrama.redditslide.Authentication.refresh);
                            net.dean.jraw.http.oauth.OAuthData finalData;
                            if (me.ccrama.redditslide.Authentication.authentication.contains("backedCreds") && (me.ccrama.redditslide.Authentication.authentication.getLong("expires", 0) > java.util.Calendar.getInstance().getTimeInMillis())) {
                                finalData = oAuthHelper.refreshToken(credentials, me.ccrama.redditslide.Authentication.authentication.getString("backedCreds", ""));// does a request

                            } else {
                                finalData = oAuthHelper.refreshToken(credentials);// does a request

                                me.ccrama.redditslide.Authentication.authentication.edit().putLong("expires", java.util.Calendar.getInstance().getTimeInMillis() + 3000000).commit();
                            }
                            me.ccrama.redditslide.Authentication.authentication.edit().putString("backedCreds", finalData.getDataNode().toString()).commit();
                            me.ccrama.redditslide.Authentication.reddit.authenticate(finalData);
                            me.ccrama.redditslide.Authentication.refresh = oAuthHelper.getRefreshToken();
                            me.ccrama.redditslide.Authentication.refresh = me.ccrama.redditslide.Authentication.reddit.getOAuthHelper().getRefreshToken();
                            if (me.ccrama.redditslide.Authentication.reddit.isAuthenticated()) {
                                if (me.ccrama.redditslide.Authentication.me == null) {
                                    me.ccrama.redditslide.Authentication.me = me.ccrama.redditslide.Authentication.reddit.me();
                                }
                                me.ccrama.redditslide.Authentication.isLoggedIn = true;
                            }
                            android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "AUTHENTICATED");
                        } catch (java.lang.Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        final net.dean.jraw.http.oauth.Credentials fcreds = net.dean.jraw.http.oauth.Credentials.userlessApp(me.ccrama.redditslide.Authentication.CLIENT_ID, java.util.UUID.randomUUID());
                        net.dean.jraw.http.oauth.OAuthData authData;
                        if (me.ccrama.redditslide.BuildConfig.DEBUG)
                            me.ccrama.redditslide.util.LogUtil.v("Not logged in");

                        try {
                            authData = me.ccrama.redditslide.Authentication.reddit.getOAuthHelper().easyAuth(fcreds);
                            me.ccrama.redditslide.Authentication.authentication.edit().putLong("expires", java.util.Calendar.getInstance().getTimeInMillis() + 3000000).commit();
                            me.ccrama.redditslide.Authentication.authentication.edit().putString("backedCreds", authData.getDataNode().toString()).commit();
                            me.ccrama.redditslide.Authentication.name = "LOGGEDOUT";
                            me.ccrama.redditslide.Authentication.mod = false;
                            me.ccrama.redditslide.Authentication.reddit.authenticate(authData);
                            android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "REAUTH LOGGED IN");
                        } catch (java.lang.Exception e) {
                            try {
                                ((android.app.Activity) (context)).runOnUiThread(new java.lang.Runnable() {
                                    @java.lang.Override
                                    public void run() {
                                        try {
                                            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(context).setTitle(me.ccrama.redditslide.R.string.err_general).setMessage(me.ccrama.redditslide.R.string.err_no_connection).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                                                @java.lang.Override
                                                public void onClick(android.content.DialogInterface dialog, int which) {
                                                    new me.ccrama.redditslide.Authentication.UpdateToken(context).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                                                }
                                            }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, new android.content.DialogInterface.OnClickListener() {
                                                @java.lang.Override
                                                public void onClick(android.content.DialogInterface dialog, int which) {
                                                    me.ccrama.redditslide.Reddit.forceRestart(context, false);
                                                }
                                            }).show();
                                        } catch (java.lang.Exception ignored) {
                                        }
                                    }
                                });
                            } catch (java.lang.Exception e2) {
                                android.widget.Toast.makeText(context, "Reddit could not be reached. Try again soon", android.widget.Toast.LENGTH_SHORT).show();
                            }
                            // TODO fail
                        }
                    }
                }
            }
            if (me.ccrama.redditslide.BuildConfig.DEBUG)
                me.ccrama.redditslide.util.LogUtil.v("Done loading token");

            return null;
        }
    }

    public static class VerifyCredentials extends android.os.AsyncTask<java.lang.String, java.lang.Void, java.lang.Void> {
        android.content.Context mContext;

        java.lang.String lastToken;

        boolean single;

        public VerifyCredentials(android.content.Context context) {
            mContext = context;
            lastToken = me.ccrama.redditslide.Authentication.authentication.getString("lasttoken", "");
        }

        @java.lang.Override
        protected java.lang.Void doInBackground(java.lang.String... subs) {
            me.ccrama.redditslide.Authentication.doVerify(lastToken, me.ccrama.redditslide.Authentication.reddit, single, mContext);
            return null;
        }
    }

    public static void doVerify(java.lang.String lastToken, net.dean.jraw.RedditClient baseReddit, boolean single, android.content.Context mContext) {
        try {
            java.lang.String token = lastToken;
            if (me.ccrama.redditslide.BuildConfig.DEBUG)
                me.ccrama.redditslide.util.LogUtil.v("TOKEN IS " + token);

            if (!token.isEmpty()) {
                net.dean.jraw.http.oauth.Credentials credentials = net.dean.jraw.http.oauth.Credentials.installedApp(me.ccrama.redditslide.Authentication.CLIENT_ID, me.ccrama.redditslide.Authentication.REDIRECT_URL);
                net.dean.jraw.http.oauth.OAuthHelper oAuthHelper = baseReddit.getOAuthHelper();
                oAuthHelper.setRefreshToken(token);
                try {
                    net.dean.jraw.http.oauth.OAuthData finalData;
                    if (((!single) && me.ccrama.redditslide.Authentication.authentication.contains("backedCreds")) && (me.ccrama.redditslide.Authentication.authentication.getLong("expires", 0) > java.util.Calendar.getInstance().getTimeInMillis())) {
                        finalData = oAuthHelper.refreshToken(credentials, me.ccrama.redditslide.Authentication.authentication.getString("backedCreds", ""));
                    } else {
                        finalData = oAuthHelper.refreshToken(credentials);// does a request

                        if (!single) {
                            me.ccrama.redditslide.Authentication.authentication.edit().putLong("expires", java.util.Calendar.getInstance().getTimeInMillis() + 3000000).apply();
                        }
                    }
                    baseReddit.authenticate(finalData);
                    if (!single) {
                        me.ccrama.redditslide.Authentication.authentication.edit().putString("backedCreds", finalData.getDataNode().toString()).apply();
                        me.ccrama.redditslide.Authentication.refresh = oAuthHelper.getRefreshToken();
                        if (me.ccrama.redditslide.BuildConfig.DEBUG) {
                            me.ccrama.redditslide.util.LogUtil.v("ACCESS TOKEN IS " + finalData.getAccessToken());
                        }
                        me.ccrama.redditslide.Authentication.isLoggedIn = true;
                        me.ccrama.redditslide.UserSubscriptions.doCachedModSubs();
                    }
                } catch (java.lang.Exception e) {
                    e.printStackTrace();
                    if (e instanceof net.dean.jraw.http.NetworkException) {
                        android.widget.Toast.makeText(mContext, (("Error " + ((net.dean.jraw.http.NetworkException) (e)).getResponse().getStatusMessage()) + ": ") + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
                    }
                }
                me.ccrama.redditslide.Authentication.didOnline = true;
            } else if (!single) {
                if (me.ccrama.redditslide.BuildConfig.DEBUG)
                    me.ccrama.redditslide.util.LogUtil.v("NOT LOGGED IN");

                final net.dean.jraw.http.oauth.Credentials fcreds = net.dean.jraw.http.oauth.Credentials.userlessApp(me.ccrama.redditslide.Authentication.CLIENT_ID, java.util.UUID.randomUUID());
                net.dean.jraw.http.oauth.OAuthData authData;
                try {
                    authData = me.ccrama.redditslide.Authentication.reddit.getOAuthHelper().easyAuth(fcreds);
                    me.ccrama.redditslide.Authentication.authentication.edit().putLong("expires", java.util.Calendar.getInstance().getTimeInMillis() + 3000000).apply();
                    me.ccrama.redditslide.Authentication.authentication.edit().putString("backedCreds", authData.getDataNode().toString()).apply();
                    me.ccrama.redditslide.Authentication.reddit.authenticate(authData);
                    me.ccrama.redditslide.Authentication.name = "LOGGEDOUT";
                    me.ccrama.redditslide.Reddit.notFirst = true;
                    me.ccrama.redditslide.Authentication.didOnline = true;
                } catch (java.lang.Exception e) {
                    e.printStackTrace();
                    if (e instanceof net.dean.jraw.http.NetworkException) {
                        android.widget.Toast.makeText(mContext, (("Error " + ((net.dean.jraw.http.NetworkException) (e)).getResponse().getStatusMessage()) + ": ") + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
                    }
                }
            }
            if (!single)
                me.ccrama.redditslide.Authentication.authedOnce = true;

        } catch (java.lang.Exception e) {
            // TODO fail
        }
    }
}