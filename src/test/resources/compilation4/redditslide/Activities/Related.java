package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Views.PreCachingLayoutManager;
import me.ccrama.redditslide.Constants;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.Adapters.ContributionAdapter;
import me.ccrama.redditslide.ColorPreferences;
import me.ccrama.redditslide.Adapters.SubredditSearchPosts;
import me.ccrama.redditslide.handler.ToolbarScrollHideHandler;
public class Related extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    // todo NFC support
    public static final java.lang.String EXTRA_URL = "url";

    private int totalItemCount;

    private int visibleItemCount;

    private int pastVisiblesItems;

    private me.ccrama.redditslide.Adapters.ContributionAdapter adapter;

    private me.ccrama.redditslide.Adapters.SubredditSearchPosts posts;

    @java.lang.Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case me.ccrama.redditslide.R.id.home :
                onBackPressed();
                return true;
        }
        return false;
    }

    java.lang.String url;

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        overrideSwipeFromAnywhere();
        super.onCreate(savedInstanceState);
        applyColorTheme("");
        setContentView(me.ccrama.redditslide.R.layout.activity_search);
        android.content.Intent intent = getIntent();
        if (intent.hasExtra(android.content.Intent.EXTRA_TEXT) && (!intent.getExtras().getString(android.content.Intent.EXTRA_TEXT, "").isEmpty())) {
            url = intent.getStringExtra(android.content.Intent.EXTRA_TEXT);
        }
        if (intent.hasExtra(me.ccrama.redditslide.Activities.Related.EXTRA_URL)) {
            url = intent.getStringExtra(me.ccrama.redditslide.Activities.Related.EXTRA_URL);
        }
        if ((url == null) || url.isEmpty()) {
            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setTitle("URL is empty").setMessage("Try again with a different link!").setCancelable(false).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                @java.lang.Override
                public void onClick(android.content.DialogInterface dialogInterface, int i) {
                    finish();
                }
            }).show();
        } else {
        }
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, "Related links", true, true);
        assert mToolbar != null;// it won't be, trust me

        mToolbar.setPopupTheme(new me.ccrama.redditslide.ColorPreferences(this).getFontStyle().getBaseId());
        final android.support.v7.widget.RecyclerView rv = ((android.support.v7.widget.RecyclerView) (findViewById(me.ccrama.redditslide.R.id.vertical_content)));
        final me.ccrama.redditslide.Views.PreCachingLayoutManager mLayoutManager;
        mLayoutManager = new me.ccrama.redditslide.Views.PreCachingLayoutManager(this);
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
                if ((!posts.loading) && (((visibleItemCount + pastVisiblesItems) + 5) >= totalItemCount)) {
                    posts.loading = true;
                    posts.loadMore(adapter, "", "url:" + url, false);
                }
            }
        });
        final android.support.v4.widget.SwipeRefreshLayout mSwipeRefreshLayout = ((android.support.v4.widget.SwipeRefreshLayout) (findViewById(me.ccrama.redditslide.R.id.activity_main_swipe_refresh_layout)));
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
        posts = new me.ccrama.redditslide.Adapters.SubredditSearchPosts("", "url:" + url, this, false);
        adapter = new me.ccrama.redditslide.Adapters.ContributionAdapter(this, posts, rv);
        rv.setAdapter(adapter);
        posts.bindAdapter(adapter, mSwipeRefreshLayout);
        // TODO catch errors
        mSwipeRefreshLayout.setOnRefreshListener(new android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener() {
            @java.lang.Override
            public void onRefresh() {
                posts.loadMore(adapter, "", "url:" + url, true);
                // TODO catch errors
            }
        });
    }
}