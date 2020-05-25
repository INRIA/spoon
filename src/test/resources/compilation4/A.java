package me.ccrama.redditslide;
/**
 * Created by ccrama on 9/19/2015.
 */
public class A extends android.os.AsyncTask<net.dean.jraw.models.PublicContribution, java.lang.Void, java.lang.Void> {
    private final net.dean.jraw.models.VoteDirection direction;

    private android.view.View v;

    private android.content.Context c;

    public A(java.lang.Boolean b, android.view.View v, android.content.Context c) {
        direction = (b) ? net.dean.jraw.models.VoteDirection.UPVOTE : net.dean.jraw.models.VoteDirection.DOWNVOTE;
        this.v = v;
        this.c = c;
        me.ccrama.redditslide.Reddit.setDefaultErrorHandler(c);
    }

    public A(android.view.View v, android.content.Context c) {
        direction = net.dean.jraw.models.VoteDirection.NO_VOTE;
        this.v = v;
        this.c = c;
    }

    @java.lang.Override
    protected java.lang.Void doInBackground(net.dean.jraw.models.PublicContribution... sub) {
        if (me.ccrama.redditslide.Authentication.isLoggedIn) {
            try {
                new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).vote(sub[0], direction);
            } catch (net.dean.jraw.ApiException | java.lang.RuntimeException e) {
                ((android.app.Activity) (c)).runOnUiThread(new java.lang.Runnable() {
                    public void run() {
                        try {
                            if (((v != null) && (c != null)) && (v.getContext() != null)) {
                                android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(v, me.ccrama.redditslide.R.string.vote_err, android.support.design.widget.Snackbar.LENGTH_SHORT);
                                android.view.View view = s.getView();
                                android.widget.TextView tv = ((android.widget.TextView) (view.findViewById(android.support.design.R.id.snackbar_text)));
                                tv.setTextColor(android.graphics.Color.WHITE);
                                s.show();
                            }
                        } catch (java.lang.Exception ignored) {
                        }
                        c = null;
                        v = null;
                    }
                });
                e.printStackTrace();
            }
        } else {
            ((android.app.Activity) (c)).runOnUiThread(new java.lang.Runnable() {
                public void run() {
                    try {
                        if (((v != null) && (c != null)) && (v.getContext() != null)) {
                            android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(v, me.ccrama.redditslide.R.string.vote_err_login, android.support.design.widget.Snackbar.LENGTH_SHORT);
                            android.view.View view = s.getView();
                            android.widget.TextView tv = ((android.widget.TextView) (view.findViewById(android.support.design.R.id.snackbar_text)));
                            tv.setTextColor(android.graphics.Color.WHITE);
                            s.show();
                        }
                    } catch (java.lang.Exception ignored) {
                    }
                    c = null;
                    v = null;
                }
            });
        }
        return null;
    }
}