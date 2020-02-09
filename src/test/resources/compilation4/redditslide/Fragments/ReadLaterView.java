package me.ccrama.redditslide.Fragments;
import me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager;
import me.ccrama.redditslide.Adapters.HistoryPosts;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Views.PreCachingLayoutManager;
import me.ccrama.redditslide.Constants;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.Adapters.ContributionAdapter;
import me.ccrama.redditslide.handler.ToolbarScrollHideHandler;
public class ReadLaterView extends android.support.v4.app.Fragment {
    private int totalItemCount;

    private int visibleItemCount;

    private int pastVisiblesItems;

    private me.ccrama.redditslide.Adapters.ContributionAdapter adapter;

    private me.ccrama.redditslide.Adapters.HistoryPosts posts;

    @java.lang.Override
    public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
        android.view.View v = inflater.inflate(me.ccrama.redditslide.R.layout.fragment_verticalcontent, container, false);
        final android.support.v7.widget.RecyclerView rv = ((android.support.v7.widget.RecyclerView) (v.findViewById(me.ccrama.redditslide.R.id.vertical_content)));
        final me.ccrama.redditslide.Views.PreCachingLayoutManager mLayoutManager;
        mLayoutManager = new me.ccrama.redditslide.Views.PreCachingLayoutManager(getContext());
        rv.setLayoutManager(mLayoutManager);
        v.findViewById(me.ccrama.redditslide.R.id.post_floating_action_button).setVisibility(android.view.View.GONE);
        final android.support.v4.widget.SwipeRefreshLayout mSwipeRefreshLayout = ((android.support.v4.widget.SwipeRefreshLayout) (v.findViewById(me.ccrama.redditslide.R.id.activity_main_swipe_refresh_layout)));
        mSwipeRefreshLayout.setColorSchemeColors(me.ccrama.redditslide.Visuals.Palette.getColors("default", getActivity()));
        // If we use 'findViewById(R.id.header).getMeasuredHeight()', 0 is always returned.
        // So, we estimate the height of the header in dp
        mSwipeRefreshLayout.setProgressViewOffset(false, me.ccrama.redditslide.Constants.SINGLE_HEADER_VIEW_OFFSET - me.ccrama.redditslide.Constants.PTR_OFFSET_TOP, me.ccrama.redditslide.Constants.SINGLE_HEADER_VIEW_OFFSET + me.ccrama.redditslide.Constants.PTR_OFFSET_BOTTOM);
        mSwipeRefreshLayout.post(new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        posts = new me.ccrama.redditslide.Adapters.HistoryPosts("readLater");
        adapter = new me.ccrama.redditslide.Adapters.ContributionAdapter(getActivity(), posts, rv);
        rv.setAdapter(adapter);
        posts.bindAdapter(adapter, mSwipeRefreshLayout);
        // TODO catch errors
        mSwipeRefreshLayout.setOnRefreshListener(new android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener() {
            @java.lang.Override
            public void onRefresh() {
                posts.loadMore(adapter, true);
                // TODO catch errors
            }
        });
        rv.addOnScrollListener(new me.ccrama.redditslide.handler.ToolbarScrollHideHandler(((android.support.v7.widget.Toolbar) (getActivity().findViewById(me.ccrama.redditslide.R.id.toolbar))), getActivity().findViewById(me.ccrama.redditslide.R.id.header)) {
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
                if (!posts.loading) {
                    if ((((visibleItemCount + pastVisiblesItems) + 5) >= totalItemCount) && (!posts.nomore)) {
                        posts.loading = true;
                        posts.loadMore(adapter, false);
                    }
                }
            }
        });
        return v;
    }

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}