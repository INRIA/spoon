package me.ccrama.redditslide.Fragments;
import me.ccrama.redditslide.Adapters.ModeratorAdapter;
import me.ccrama.redditslide.Adapters.ModLogAdapter;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Views.PreCachingLayoutManager;
import me.ccrama.redditslide.Constants;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.Adapters.ModeratorPosts;
import me.ccrama.redditslide.Adapters.ModLogPosts;
import me.ccrama.redditslide.handler.ToolbarScrollHideHandler;
import me.ccrama.redditslide.Activities.ModQueue;
public class ModLog extends android.support.v4.app.Fragment {
    public me.ccrama.redditslide.Adapters.ModLogAdapter adapter;

    private me.ccrama.redditslide.Adapters.ModLogPosts posts;

    @java.lang.Override
    public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
        android.view.View v = inflater.inflate(me.ccrama.redditslide.R.layout.fragment_verticalcontent, container, false);
        android.support.v7.widget.RecyclerView rv = ((android.support.v7.widget.RecyclerView) (v.findViewById(me.ccrama.redditslide.R.id.vertical_content)));
        final me.ccrama.redditslide.Views.PreCachingLayoutManager mLayoutManager;
        mLayoutManager = new me.ccrama.redditslide.Views.PreCachingLayoutManager(getActivity());
        rv.setLayoutManager(mLayoutManager);
        v.findViewById(me.ccrama.redditslide.R.id.post_floating_action_button).setVisibility(android.view.View.GONE);
        final android.support.v4.widget.SwipeRefreshLayout mSwipeRefreshLayout = ((android.support.v4.widget.SwipeRefreshLayout) (v.findViewById(me.ccrama.redditslide.R.id.activity_main_swipe_refresh_layout)));
        mSwipeRefreshLayout.setColorSchemeColors(me.ccrama.redditslide.Visuals.Palette.getColors("mod", getActivity()));
        // If we use 'findViewById(R.id.header).getMeasuredHeight()', 0 is always returned.
        // So, we estimate the height of the header in dp
        mSwipeRefreshLayout.setProgressViewOffset(false, me.ccrama.redditslide.Constants.TAB_HEADER_VIEW_OFFSET - me.ccrama.redditslide.Constants.PTR_OFFSET_TOP, me.ccrama.redditslide.Constants.TAB_HEADER_VIEW_OFFSET + me.ccrama.redditslide.Constants.PTR_OFFSET_BOTTOM);
        mSwipeRefreshLayout.post(new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        posts = new me.ccrama.redditslide.Adapters.ModLogPosts();
        adapter = new me.ccrama.redditslide.Adapters.ModLogAdapter(getActivity(), posts, rv);
        rv.setAdapter(adapter);
        rv.setOnScrollListener(new me.ccrama.redditslide.handler.ToolbarScrollHideHandler(((me.ccrama.redditslide.Activities.ModQueue) (getActivity())).mToolbar, getActivity().findViewById(me.ccrama.redditslide.R.id.header)));
        posts.bindAdapter(adapter, mSwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener() {
            @java.lang.Override
            public void onRefresh() {
                posts.loadMore(adapter);
            }
        });
        return v;
    }

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.os.Bundle bundle = this.getArguments();
    }
}