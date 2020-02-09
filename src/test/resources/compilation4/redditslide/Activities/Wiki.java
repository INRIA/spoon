package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.Views.ToggleSwipeViewPager;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.ColorPreferences;
import java.util.ArrayList;
import me.ccrama.redditslide.Fragments.WikiPage;
import java.util.List;
/**
 * Created by ccrama on 9/17/2015.
 */
public class Wiki extends me.ccrama.redditslide.Activities.BaseActivityAnim implements me.ccrama.redditslide.Fragments.WikiPage.WikiPageListener {
    public static final java.lang.String EXTRA_SUBREDDIT = "subreddit";

    public static final java.lang.String EXTRA_PAGE = "page";

    private android.support.design.widget.TabLayout tabs;

    private me.ccrama.redditslide.Views.ToggleSwipeViewPager pager;

    private java.lang.String subreddit;

    private me.ccrama.redditslide.Activities.Wiki.OverviewPagerAdapter adapter;

    private java.util.List<java.lang.String> pages;

    private java.lang.String page;

    private static java.lang.String globalCustomCss;

    private static java.lang.String globalCustomJavaScript;

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstance) {
        overrideSwipeFromAnywhere();
        super.onCreate(savedInstance);
        subreddit = getIntent().getExtras().getString(me.ccrama.redditslide.Activities.Wiki.EXTRA_SUBREDDIT, "");
        setShareUrl(("https://reddit.com/r/" + subreddit) + "/wiki/");
        applyColorTheme(subreddit);
        createCustomCss();
        createCustomJavaScript();
        setContentView(me.ccrama.redditslide.R.layout.activity_slidetabs);
        setupSubredditAppBar(me.ccrama.redditslide.R.id.toolbar, ("/r/" + subreddit) + " wiki", true, subreddit);
        if (getIntent().hasExtra(me.ccrama.redditslide.Activities.Wiki.EXTRA_PAGE)) {
            page = getIntent().getExtras().getString(me.ccrama.redditslide.Activities.Wiki.EXTRA_PAGE);
            me.ccrama.redditslide.util.LogUtil.v("Page is " + page);
        } else {
            page = "index";
        }
        tabs = ((android.support.design.widget.TabLayout) (findViewById(me.ccrama.redditslide.R.id.sliding_tabs)));
        tabs.setTabMode(android.support.design.widget.TabLayout.MODE_SCROLLABLE);
        tabs.setSelectedTabIndicatorColor(new me.ccrama.redditslide.ColorPreferences(this).getColor("no sub"));
        pager = ((me.ccrama.redditslide.Views.ToggleSwipeViewPager) (findViewById(me.ccrama.redditslide.R.id.content_view)));
        findViewById(me.ccrama.redditslide.R.id.header).setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getColor(subreddit));
        new me.ccrama.redditslide.Activities.Wiki.AsyncGetWiki().executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void createCustomCss() {
        java.lang.StringBuilder customCssBuilder = new java.lang.StringBuilder();
        customCssBuilder.append("<style>");
        android.content.res.TypedArray ta = obtainStyledAttributes(new int[]{ me.ccrama.redditslide.R.attr.activity_background, me.ccrama.redditslide.R.attr.fontColor, me.ccrama.redditslide.R.attr.colorAccent });
        customCssBuilder.append("html { ").append("background: ".concat(me.ccrama.redditslide.Activities.Wiki.getHexFromColorInt(ta.getColor(0, android.graphics.Color.WHITE))).concat(";")).append("color: ".concat(me.ccrama.redditslide.Activities.Wiki.getHexFromColorInt(ta.getColor(1, android.graphics.Color.BLACK))).concat(";")).append("; }");
        customCssBuilder.append("a { ").append("color: ".concat(me.ccrama.redditslide.Activities.Wiki.getHexFromColorInt(ta.getColor(2, android.graphics.Color.BLUE))).concat(";")).append("; }");
        ta.recycle();
        customCssBuilder.append("table, code { display: block; overflow-x: scroll; }");
        customCssBuilder.append("table { white-space: nowrap; }");
        customCssBuilder.append("</style>");
        me.ccrama.redditslide.Activities.Wiki.globalCustomCss = customCssBuilder.toString();
    }

    private void createCustomJavaScript() {
        me.ccrama.redditslide.Activities.Wiki.globalCustomJavaScript = "<script type=\"text/javascript\">" + ((((((((("window.addEventListener('touchstart', function onSlideUserTouch(e) {" + "var element = e.target;") + "while(element) {") + "if(element.tagName && (element.tagName.toLowerCase() === 'table' || element.tagName.toLowerCase() === 'code')) {") + "Slide.overflowTouched();") + "return;") + "} else {") + "element = element.parentNode;") + "}}}, false)") + "</script>");
    }

    private static java.lang.String getHexFromColorInt(@android.support.annotation.ColorInt
    int colorInt) {
        return java.lang.String.format("#%06X", 0xffffff & colorInt);
    }

    public static java.lang.String getGlobalCustomCss() {
        return me.ccrama.redditslide.Activities.Wiki.globalCustomCss;
    }

    public static java.lang.String getGlobalCustomJavaScript() {
        return me.ccrama.redditslide.Activities.Wiki.globalCustomJavaScript;
    }

    public net.dean.jraw.managers.WikiManager wiki;

    @java.lang.Override
    public void embeddedWikiLinkClicked(java.lang.String wikiPageTitle) {
        if (pages.contains(wikiPageTitle)) {
            pager.setCurrentItem(pages.indexOf(wikiPageTitle));
        } else {
            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setTitle(me.ccrama.redditslide.R.string.page_not_found).setMessage(me.ccrama.redditslide.R.string.page_does_not_exist).setPositiveButton(android.R.string.ok, new android.content.DialogInterface.OnClickListener() {
                @java.lang.Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        }
    }

    @java.lang.Override
    public void overflowTouched() {
        pager.disableSwipingUntilRelease();
    }

    private class AsyncGetWiki extends android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void> {
        @java.lang.Override
        protected java.lang.Void doInBackground(java.lang.Void... params) {
            wiki = new net.dean.jraw.managers.WikiManager(me.ccrama.redditslide.Authentication.reddit);
            try {
                pages = wiki.getPages(subreddit);
                java.util.List<java.lang.String> toRemove = new java.util.ArrayList<>();
                for (java.lang.String s : pages) {
                    if (s.startsWith("config")) {
                        toRemove.add(s);
                    }
                }
                pages.removeAll(toRemove);
                adapter = new me.ccrama.redditslide.Activities.Wiki.OverviewPagerAdapter(getSupportFragmentManager());
            } catch (java.lang.Exception e) {
                runOnUiThread(new java.lang.Runnable() {
                    @java.lang.Override
                    public void run() {
                        try {
                            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.Wiki.this).setTitle(me.ccrama.redditslide.R.string.wiki_err).setMessage(me.ccrama.redditslide.R.string.wiki_err_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_close, new android.content.DialogInterface.OnClickListener() {
                                @java.lang.Override
                                public void onClick(android.content.DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            }).setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {
                                @java.lang.Override
                                public void onDismiss(android.content.DialogInterface dialog) {
                                    finish();
                                }
                            }).show();
                        } catch (java.lang.Exception ignored) {
                        }
                    }
                });
            }
            return null;
        }

        @java.lang.Override
        public void onPostExecute(java.lang.Void d) {
            if (adapter != null) {
                pager.setAdapter(adapter);
                tabs.setupWithViewPager(pager);
                if (pages.contains(page)) {
                    pager.setCurrentItem(pages.indexOf(page));
                }
            } else {
                try {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.Wiki.this).setTitle(me.ccrama.redditslide.R.string.wiki_err).setMessage(me.ccrama.redditslide.R.string.wiki_err_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_close, new android.content.DialogInterface.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    }).setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {
                        @java.lang.Override
                        public void onDismiss(android.content.DialogInterface dialog) {
                            finish();
                        }
                    }).show();
                } catch (java.lang.Exception e) {
                }
            }
        }
    }

    public class OverviewPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
        public OverviewPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @java.lang.Override
        public android.support.v4.app.Fragment getItem(int i) {
            android.support.v4.app.Fragment f = new me.ccrama.redditslide.Fragments.WikiPage();
            ((me.ccrama.redditslide.Fragments.WikiPage) (f)).setListener(me.ccrama.redditslide.Activities.Wiki.this);
            android.os.Bundle args = new android.os.Bundle();
            args.putString("title", pages.get(i));
            args.putString("subreddit", subreddit);
            f.setArguments(args);
            return f;
        }

        @java.lang.Override
        public int getCount() {
            if (pages == null) {
                return 1;
            } else {
                return pages.size();
            }
        }

        @java.lang.Override
        public java.lang.CharSequence getPageTitle(int position) {
            return pages.get(position);
        }
    }
}