package me.ccrama.redditslide.Fragments;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Views.PreCachingLayoutManager;
import me.ccrama.redditslide.Constants;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.ColorPreferences;
import me.ccrama.redditslide.Activities.BaseActivity;
import java.util.List;
import me.ccrama.redditslide.Adapters.SubredditNames;
import me.ccrama.redditslide.handler.ToolbarScrollHideHandler;
import me.ccrama.redditslide.Adapters.SubredditAdapter;
public class SubredditListView extends android.support.v4.app.Fragment {
    public me.ccrama.redditslide.Adapters.SubredditNames posts;

    public android.support.v7.widget.RecyclerView rv;

    private int visibleItemCount;

    private int pastVisiblesItems;

    private int totalItemCount;

    public me.ccrama.redditslide.Adapters.SubredditAdapter adapter;

    public java.lang.String where;

    private android.support.v4.widget.SwipeRefreshLayout mSwipeRefreshLayout;

    @java.lang.Override
    public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
        final android.content.Context contextThemeWrapper = new android.support.v7.view.ContextThemeWrapper(getActivity(), new me.ccrama.redditslide.ColorPreferences(inflater.getContext()).getThemeSubreddit(where));
        android.view.View v = ((android.view.LayoutInflater) (contextThemeWrapper.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE))).inflate(me.ccrama.redditslide.R.layout.fragment_verticalcontent, container, false);
        rv = ((android.support.v7.widget.RecyclerView) (v.findViewById(me.ccrama.redditslide.R.id.vertical_content)));
        final android.support.v7.widget.RecyclerView.LayoutManager mLayoutManager = new me.ccrama.redditslide.Views.PreCachingLayoutManager(getActivity());
        rv.setLayoutManager(mLayoutManager);
        rv.setItemAnimator(new com.mikepenz.itemanimators.SlideUpAlphaAnimator().withInterpolator(new android.support.v4.view.animation.LinearOutSlowInInterpolator()));
        mSwipeRefreshLayout = ((android.support.v4.widget.SwipeRefreshLayout) (v.findViewById(me.ccrama.redditslide.R.id.activity_main_swipe_refresh_layout)));
        mSwipeRefreshLayout.setColorSchemeColors(me.ccrama.redditslide.Visuals.Palette.getColors("no sub", getContext()));
        // If we use 'findViewById(R.id.header).getMeasuredHeight()', 0 is always returned.
        // So, we estimate the height of the header in dp
        mSwipeRefreshLayout.setProgressViewOffset(false, me.ccrama.redditslide.Constants.TAB_HEADER_VIEW_OFFSET - me.ccrama.redditslide.Constants.PTR_OFFSET_TOP, me.ccrama.redditslide.Constants.TAB_HEADER_VIEW_OFFSET + me.ccrama.redditslide.Constants.PTR_OFFSET_BOTTOM);
        v.findViewById(me.ccrama.redditslide.R.id.post_floating_action_button).setVisibility(android.view.View.GONE);
        doAdapter();
        return v;
    }

    public boolean main;

    public void doAdapter() {
        mSwipeRefreshLayout.post(new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        posts = new me.ccrama.redditslide.Adapters.SubredditNames(where, getContext(), this);
        adapter = new me.ccrama.redditslide.Adapters.SubredditAdapter(getActivity(), posts, rv, where, this);
        rv.setAdapter(adapter);
        posts.loadMore(mSwipeRefreshLayout.getContext(), true, where);
        mSwipeRefreshLayout.setOnRefreshListener(new android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener() {
            @java.lang.Override
            public void onRefresh() {
                refresh();
            }
        });
        rv.addOnScrollListener(new me.ccrama.redditslide.handler.ToolbarScrollHideHandler(((me.ccrama.redditslide.Activities.BaseActivity) (getActivity())).mToolbar, getActivity().findViewById(me.ccrama.redditslide.R.id.header)) {
            @java.lang.Override
            public void onScrolled(android.support.v7.widget.RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if ((!posts.loading) && (!posts.nomore)) {
                    visibleItemCount = rv.getLayoutManager().getChildCount();
                    totalItemCount = rv.getLayoutManager().getItemCount();
                    pastVisiblesItems = ((android.support.v7.widget.LinearLayoutManager) (rv.getLayoutManager())).findFirstVisibleItemPosition();
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        posts.loading = true;
                        me.ccrama.redditslide.util.LogUtil.v("Loading more");
                        posts.loadMore(mSwipeRefreshLayout.getContext(), false, where);
                    }
                }
            }
        });
    }

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.os.Bundle bundle = this.getArguments();
        where = bundle.getString("id", "");
    }

    private void refresh() {
        posts.loadMore(mSwipeRefreshLayout.getContext(), true, where);
    }

    public void updateSuccess(final java.util.List<net.dean.jraw.models.Subreddit> submissions, final int startIndex) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new java.lang.Runnable() {
                @java.lang.Override
                public void run() {
                    if (mSwipeRefreshLayout != null) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    if (startIndex > 0) {
                        adapter.notifyItemRangeInserted(startIndex + 1, posts.posts.size());
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    public void updateError() {
        mSwipeRefreshLayout.setRefreshing(false);
        adapter.setError(true);
    }
}