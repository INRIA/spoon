package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager;
import java.util.Locale;
import me.ccrama.redditslide.Views.PreCachingLayoutManager;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.Adapters.ContributionAdapter;
import me.ccrama.redditslide.ColorPreferences;
import me.ccrama.redditslide.Adapters.SubredditSearchPosts;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.util.SortingUtil;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Constants;
import org.apache.commons.lang3.StringUtils;
import me.ccrama.redditslide.handler.ToolbarScrollHideHandler;
public class Search extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    // todo NFC support
    public static final java.lang.String EXTRA_TERM = "term";

    public static final java.lang.String EXTRA_SUBREDDIT = "subreddit";

    public static final java.lang.String EXTRA_MULTIREDDIT = "multi";

    public static final java.lang.String EXTRA_SITE = "site";

    public static final java.lang.String EXTRA_URL = "url";

    public static final java.lang.String EXTRA_SELF = "self";

    public static final java.lang.String EXTRA_NSFW = "nsfw";

    public static final java.lang.String EXTRA_AUTHOR = "author";

    private int totalItemCount;

    private int visibleItemCount;

    private int pastVisiblesItems;

    private me.ccrama.redditslide.Adapters.ContributionAdapter adapter;

    private java.lang.String where;

    private java.lang.String subreddit;

    // private String site;
    // private String url;
    // private boolean self;
    // private boolean nsfw;
    // private String author;
    private me.ccrama.redditslide.Adapters.SubredditSearchPosts posts;

    @java.lang.Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        android.view.MenuInflater inflater = getMenuInflater();
        inflater.inflate(me.ccrama.redditslide.R.menu.menu_search, menu);
        // if (mShowInfoButton) menu.findItem(R.id.action_info).setVisible(true);
        // else menu.findItem(R.id.action_info).setVisible(false);
        return true;
    }

    public void reloadSubs() {
        posts.refreshLayout.setRefreshing(true);
        posts.reset(time);
    }

    public void openTimeFramePopup() {
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
                reloadSubs();
                // When the .name() is returned for both of the ENUMs, it will be in all caps.
                // So, make it lowercase, then capitalize the first letter of each.
                getSupportActionBar().setSubtitle((org.apache.commons.lang3.StringUtils.capitalize(me.ccrama.redditslide.util.SortingUtil.search.name().toLowerCase(java.util.Locale.ENGLISH)) + " › ") + org.apache.commons.lang3.StringUtils.capitalize(time.name().toLowerCase(java.util.Locale.ENGLISH)));
            }
        };
        com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this);
        builder.setTitle(me.ccrama.redditslide.R.string.sorting_time_choose);
        builder.setSingleChoiceItems(me.ccrama.redditslide.util.SortingUtil.getSortingTimesStrings(), me.ccrama.redditslide.util.SortingUtil.getSortingSearchId(this), l2);
        builder.show();
    }

    public void openSearchTypePopup() {
        final android.content.DialogInterface.OnClickListener l2 = new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0 :
                        me.ccrama.redditslide.util.SortingUtil.search = net.dean.jraw.paginators.SubmissionSearchPaginator.SearchSort.RELEVANCE;
                        break;
                    case 1 :
                        me.ccrama.redditslide.util.SortingUtil.search = net.dean.jraw.paginators.SubmissionSearchPaginator.SearchSort.TOP;
                        break;
                    case 2 :
                        me.ccrama.redditslide.util.SortingUtil.search = net.dean.jraw.paginators.SubmissionSearchPaginator.SearchSort.NEW;
                        break;
                    case 3 :
                        me.ccrama.redditslide.util.SortingUtil.search = net.dean.jraw.paginators.SubmissionSearchPaginator.SearchSort.COMMENTS;
                        break;
                }
                reloadSubs();
                // When the .name() is returned for both of the ENUMs, it will be in all caps.
                // So, make it lowercase, then capitalize the first letter of each.
                getSupportActionBar().setSubtitle((org.apache.commons.lang3.StringUtils.capitalize(me.ccrama.redditslide.util.SortingUtil.search.name().toLowerCase(java.util.Locale.ENGLISH)) + " › ") + org.apache.commons.lang3.StringUtils.capitalize(time.name().toLowerCase(java.util.Locale.ENGLISH)));
            }
        };
        com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this);
        builder.setTitle(me.ccrama.redditslide.R.string.sorting_choose);
        builder.setSingleChoiceItems(me.ccrama.redditslide.util.SortingUtil.getSearch(), me.ccrama.redditslide.util.SortingUtil.getSearchType(), l2);
        builder.show();
    }

    public net.dean.jraw.paginators.TimePeriod time;

    @java.lang.Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case me.ccrama.redditslide.R.id.home :
                onBackPressed();
                return true;
            case me.ccrama.redditslide.R.id.time :
                openTimeFramePopup();
                return true;
            case me.ccrama.redditslide.R.id.edit :
                com.afollestad.materialdialogs.MaterialDialog.Builder builder = new com.afollestad.materialdialogs.MaterialDialog.Builder(this).title(me.ccrama.redditslide.R.string.search_title).alwaysCallInputCallback().input(getString(me.ccrama.redditslide.R.string.search_msg), where, new com.afollestad.materialdialogs.MaterialDialog.InputCallback() {
                    @java.lang.Override
                    public void onInput(com.afollestad.materialdialogs.MaterialDialog materialDialog, java.lang.CharSequence charSequence) {
                        where = charSequence.toString();
                    }
                });
                // Add "search current sub" if it is not frontpage/all/random
                builder.positiveText("Search").onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                    @java.lang.Override
                    public void onClick(@android.support.annotation.NonNull
                    com.afollestad.materialdialogs.MaterialDialog materialDialog, @android.support.annotation.NonNull
                    com.afollestad.materialdialogs.DialogAction dialogAction) {
                        android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.Search.this, me.ccrama.redditslide.Activities.Search.class);
                        i.putExtra(me.ccrama.redditslide.Activities.Search.EXTRA_TERM, where);
                        if (multireddit) {
                            i.putExtra(me.ccrama.redditslide.Activities.Search.EXTRA_MULTIREDDIT, subreddit);
                        } else {
                            i.putExtra(me.ccrama.redditslide.Activities.Search.EXTRA_SUBREDDIT, subreddit);
                        }
                        startActivity(i);
                        overridePendingTransition(0, 0);
                        finish();
                        overridePendingTransition(0, 0);
                    }
                });
                builder.show();
                return true;
            case me.ccrama.redditslide.R.id.sort :
                openSearchTypePopup();
                return true;
        }
        return false;
    }

    public boolean multireddit;

    android.support.v7.widget.RecyclerView rv;

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        overrideSwipeFromAnywhere();
        super.onCreate(savedInstanceState);
        applyColorTheme("");
        setContentView(me.ccrama.redditslide.R.layout.activity_search);
        where = getIntent().getExtras().getString(me.ccrama.redditslide.Activities.Search.EXTRA_TERM, "");
        if (getIntent().hasExtra(me.ccrama.redditslide.Activities.Search.EXTRA_MULTIREDDIT)) {
            multireddit = true;
            subreddit = getIntent().getExtras().getString(me.ccrama.redditslide.Activities.Search.EXTRA_MULTIREDDIT);
        } else {
            if (getIntent().hasExtra(me.ccrama.redditslide.Activities.Search.EXTRA_AUTHOR)) {
                where = (where + "&author=") + getIntent().getExtras().getString(me.ccrama.redditslide.Activities.Search.EXTRA_AUTHOR);
            }
            if (getIntent().hasExtra(me.ccrama.redditslide.Activities.Search.EXTRA_NSFW)) {
                where = (where + "&nsfw=") + (getIntent().getExtras().getBoolean(me.ccrama.redditslide.Activities.Search.EXTRA_NSFW) ? "yes" : "no");
            }
            if (getIntent().hasExtra(me.ccrama.redditslide.Activities.Search.EXTRA_SELF)) {
                where = (where + "&selftext=") + (getIntent().getExtras().getBoolean(me.ccrama.redditslide.Activities.Search.EXTRA_SELF) ? "yes" : "no");
            }
            if (getIntent().hasExtra(me.ccrama.redditslide.Activities.Search.EXTRA_SITE)) {
                where = (where + "&site=") + getIntent().getExtras().getString(me.ccrama.redditslide.Activities.Search.EXTRA_SITE);
            }
            if (getIntent().hasExtra(me.ccrama.redditslide.Activities.Search.EXTRA_URL)) {
                where = (where + "&url=") + getIntent().getExtras().getString(me.ccrama.redditslide.Activities.Search.EXTRA_URL);
            }
            subreddit = getIntent().getExtras().getString(me.ccrama.redditslide.Activities.Search.EXTRA_SUBREDDIT, "");
        }
        where = org.apache.commons.text.StringEscapeUtils.unescapeHtml4(where);
        setupSubredditAppBar(me.ccrama.redditslide.R.id.toolbar, "Search", true, subreddit.toLowerCase(java.util.Locale.ENGLISH));
        time = net.dean.jraw.paginators.TimePeriod.ALL;
        getSupportActionBar().setTitle(android.text.Html.fromHtml(where));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        assert mToolbar != null;// it won't be, trust me

        mToolbar.setNavigationOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                onBackPressed();// Simulate a system's "Back" button functionality.

            }
        });
        mToolbar.setPopupTheme(new me.ccrama.redditslide.ColorPreferences(this).getFontStyle().getBaseId());
        // When the .name() is returned for both of the ENUMs, it will be in all caps.
        // So, make it lowercase, then capitalize the first letter of each.
        getSupportActionBar().setSubtitle((org.apache.commons.lang3.StringUtils.capitalize(me.ccrama.redditslide.util.SortingUtil.search.name().toLowerCase(java.util.Locale.ENGLISH)) + " › ") + org.apache.commons.lang3.StringUtils.capitalize(time.name().toLowerCase(java.util.Locale.ENGLISH)));
        rv = ((android.support.v7.widget.RecyclerView) (findViewById(me.ccrama.redditslide.R.id.vertical_content)));
        final android.support.v7.widget.RecyclerView.LayoutManager mLayoutManager;
        mLayoutManager = createLayoutManager(me.ccrama.redditslide.Activities.Search.getNumColumns(getResources().getConfiguration().orientation, this));
        rv.setLayoutManager(mLayoutManager);
        rv.addOnScrollListener(new me.ccrama.redditslide.handler.ToolbarScrollHideHandler(mToolbar, findViewById(me.ccrama.redditslide.R.id.header)) {
            @java.lang.Override
            public void onScrolled(android.support.v7.widget.RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = rv.getLayoutManager().getChildCount();
                totalItemCount = rv.getLayoutManager().getItemCount();
                if (rv.getLayoutManager() instanceof me.ccrama.redditslide.Views.PreCachingLayoutManager) {
                    pastVisiblesItems = ((me.ccrama.redditslide.Views.PreCachingLayoutManager) (rv.getLayoutManager())).findFirstVisibleItemPosition();
                } else {
                    int[] firstVisibleItems = null;
                    firstVisibleItems = ((me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager) (rv.getLayoutManager())).findFirstVisibleItemPositions(firstVisibleItems);
                    if ((firstVisibleItems != null) && (firstVisibleItems.length > 0)) {
                        pastVisiblesItems = firstVisibleItems[0];
                    }
                }
                if (((!posts.loading) && (((visibleItemCount + pastVisiblesItems) + 5) >= totalItemCount)) && (!posts.nomore)) {
                    posts.loading = true;
                    posts.loadMore(adapter, subreddit, where, false, multireddit, time);
                }
            }
        });
        final android.support.v4.widget.SwipeRefreshLayout mSwipeRefreshLayout = ((android.support.v4.widget.SwipeRefreshLayout) (findViewById(me.ccrama.redditslide.R.id.activity_main_swipe_refresh_layout)));
        mSwipeRefreshLayout.setColorSchemeColors(me.ccrama.redditslide.Visuals.Palette.getColors(subreddit, this));
        // If we use 'findViewById(R.id.header).getMeasuredHeight()', 0 is always returned.
        // So, we estimate the height of the header in dp.
        mSwipeRefreshLayout.setProgressViewOffset(false, me.ccrama.redditslide.Constants.SINGLE_HEADER_VIEW_OFFSET - me.ccrama.redditslide.Constants.PTR_OFFSET_TOP, me.ccrama.redditslide.Constants.SINGLE_HEADER_VIEW_OFFSET + me.ccrama.redditslide.Constants.PTR_OFFSET_BOTTOM);
        mSwipeRefreshLayout.post(new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        posts = new me.ccrama.redditslide.Adapters.SubredditSearchPosts(subreddit, where.toLowerCase(java.util.Locale.ENGLISH), this, multireddit);
        adapter = new me.ccrama.redditslide.Adapters.ContributionAdapter(this, posts, rv);
        rv.setAdapter(adapter);
        posts.bindAdapter(adapter, mSwipeRefreshLayout);
        // TODO catch errors
        mSwipeRefreshLayout.setOnRefreshListener(new android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener() {
            @java.lang.Override
            public void onRefresh() {
                posts.loadMore(adapter, subreddit, where, true, multireddit, time);
                // TODO catch errors
            }
        });
    }

    @java.lang.Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        final int currentOrientation = newConfig.orientation;
        final me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager mLayoutManager = ((me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager) (rv.getLayoutManager()));
        mLayoutManager.setSpanCount(me.ccrama.redditslide.Activities.Search.getNumColumns(currentOrientation, this));
    }

    @android.support.annotation.NonNull
    private android.support.v7.widget.RecyclerView.LayoutManager createLayoutManager(final int numColumns) {
        return new me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager(numColumns, me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager.VERTICAL);
    }

    public static int getNumColumns(final int orientation, android.content.Context context) {
        final int numColumns;
        boolean singleColumnMultiWindow = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            singleColumnMultiWindow = ((android.app.Activity) (context)).isInMultiWindowMode() && me.ccrama.redditslide.SettingValues.singleColumnMultiWindow;
        }
        if (((orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) && me.ccrama.redditslide.SettingValues.isPro) && (!singleColumnMultiWindow)) {
            numColumns = me.ccrama.redditslide.Reddit.dpWidth;
        } else if ((orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) && me.ccrama.redditslide.SettingValues.dualPortrait) {
            numColumns = 2;
        } else {
            numColumns = 1;
        }
        return numColumns;
    }
}