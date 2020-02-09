package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.SpoilerRobotoTextView;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.util.LinkUtil;
import me.ccrama.redditslide.Constants;
import me.ccrama.redditslide.Visuals.Palette;
public class ReaderMode extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    private int mSubredditColor;

    public static java.lang.String html;

    me.ccrama.redditslide.SpoilerRobotoTextView v;

    private java.lang.String url;

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        overrideSwipeFromAnywhere();
        super.onCreate(savedInstanceState);
        applyColorTheme("");
        setContentView(me.ccrama.redditslide.R.layout.activity_reader);
        mSubredditColor = getIntent().getExtras().getInt(me.ccrama.redditslide.util.LinkUtil.EXTRA_COLOR, me.ccrama.redditslide.Visuals.Palette.getDefaultColor());
        setSupportActionBar(((android.support.v7.widget.Toolbar) (findViewById(me.ccrama.redditslide.R.id.toolbar))));
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, "", true, mSubredditColor, me.ccrama.redditslide.R.id.appbar);
        if (getIntent().hasExtra("url")) {
            url = getIntent().getExtras().getString(me.ccrama.redditslide.util.LinkUtil.EXTRA_URL, "");
            ((android.support.v7.widget.Toolbar) (findViewById(me.ccrama.redditslide.R.id.toolbar))).setTitle(url);
        }
        v = ((me.ccrama.redditslide.SpoilerRobotoTextView) (findViewById(me.ccrama.redditslide.R.id.body)));
        final android.support.v4.widget.SwipeRefreshLayout mSwipeRefreshLayout = ((android.support.v4.widget.SwipeRefreshLayout) (this.findViewById(me.ccrama.redditslide.R.id.refresh)));
        mSwipeRefreshLayout.setColorSchemeColors(me.ccrama.redditslide.Visuals.Palette.getColors("", this));
        // If we use 'findViewById(R.id.header).getMeasuredHeight()', 0 is always returned.
        // So, we estimate the height of the header in dp.
        mSwipeRefreshLayout.setProgressViewOffset(false, me.ccrama.redditslide.Constants.SINGLE_HEADER_VIEW_OFFSET - me.ccrama.redditslide.Constants.PTR_OFFSET_TOP, me.ccrama.redditslide.Constants.SINGLE_HEADER_VIEW_OFFSET + me.ccrama.redditslide.Constants.PTR_OFFSET_BOTTOM);
        mSwipeRefreshLayout.post(new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        new me.ccrama.redditslide.Activities.ReaderMode.AsyncGetArticle().execute();
    }

    private void display(java.lang.String title, java.lang.String web) {
        v.setTextHtml(web, "nosub");
        if ((title != null) && (!title.isEmpty())) {
            ((android.support.v7.widget.Toolbar) (findViewById(me.ccrama.redditslide.R.id.toolbar))).setTitle(title);
        } else {
            int index = v.getText().toString().indexOf("\n");
            if (index < 0) {
                index = 0;
            }
            ((android.support.v7.widget.Toolbar) (findViewById(me.ccrama.redditslide.R.id.toolbar))).setTitle(v.getText().toString().substring(0, index));
        }
    }

    public class AsyncGetArticle extends android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void> {
        java.lang.String articleText;

        java.lang.String title;

        @java.lang.Override
        protected java.lang.Void doInBackground(java.lang.Void... params) {
            try {
                if (url != null) {
                    org.jsoup.Connection connection = org.jsoup.Jsoup.connect(me.ccrama.redditslide.Activities.ReaderMode.this.url);
                    org.jsoup.nodes.Document document = connection.get();
                    me.ccrama.redditslide.Activities.ReaderMode.html = document.html();
                    title = document.title();
                    com.wuman.jreadability.Readability readability = new com.wuman.jreadability.Readability(document);
                    readability.init();
                    articleText = readability.outerHtml();
                } else {
                    com.wuman.jreadability.Readability readability = new com.wuman.jreadability.Readability(org.apache.commons.text.StringEscapeUtils.unescapeJava(me.ccrama.redditslide.Activities.ReaderMode.html));// URL

                    readability.init();
                    articleText = readability.outerHtml();
                }
            } catch (java.lang.Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @java.lang.Override
        protected void onPostExecute(java.lang.Void aVoid) {
            ((android.support.v4.widget.SwipeRefreshLayout) (me.ccrama.redditslide.Activities.ReaderMode.this.findViewById(me.ccrama.redditslide.R.id.refresh))).setRefreshing(false);
            me.ccrama.redditslide.Activities.ReaderMode.this.findViewById(me.ccrama.redditslide.R.id.refresh).setEnabled(false);
            if (articleText != null) {
                display(title, articleText);
            } else {
                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.ReaderMode.this).setTitle(me.ccrama.redditslide.R.string.internal_browser_extracting_error).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNeutralButton("Open in web view", new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.ReaderMode.this, me.ccrama.redditslide.Activities.Website.class);
                        i.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_URL, url);
                        startActivity(i);
                        finish();
                    }
                }).setCancelable(false).show();
            }
        }

        @java.lang.Override
        protected void onPreExecute() {
        }
    }

    @java.lang.Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        android.view.MenuInflater inflater = getMenuInflater();
        if (url != null) {
            inflater.inflate(me.ccrama.redditslide.R.menu.menu_reader, menu);
        }
        return true;
    }

    @java.lang.Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                return true;
            case me.ccrama.redditslide.R.id.web :
                me.ccrama.redditslide.util.LinkUtil.openUrl(url, mSubredditColor, this);
                finish();
                return true;
            case me.ccrama.redditslide.R.id.share :
                me.ccrama.redditslide.Reddit.defaultShareText(((android.support.v7.widget.Toolbar) (findViewById(me.ccrama.redditslide.R.id.toolbar))).getTitle().toString(), url, this);
                return true;
        }
        return false;
    }
}