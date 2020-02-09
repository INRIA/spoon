package me.ccrama.redditslide.Fragments;
import me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager;
import java.util.Locale;
import me.ccrama.redditslide.Visuals.Palette;
import java.util.ArrayList;
import me.ccrama.redditslide.Adapters.SubmissionDisplay;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Adapters.MultiredditPosts;
import me.ccrama.redditslide.OfflineSubreddit;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.HasSeen;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Views.CreateCardView;
import me.ccrama.redditslide.Activities.Search;
import me.ccrama.redditslide.Constants;
import me.ccrama.redditslide.Activities.Submit;
import me.ccrama.redditslide.Hidden;
import me.ccrama.redditslide.UserSubscriptions;
import java.util.List;
import me.ccrama.redditslide.Adapters.MultiredditAdapter;
import me.ccrama.redditslide.handler.ToolbarScrollHideHandler;
public class MultiredditView extends android.support.v4.app.Fragment implements me.ccrama.redditslide.Adapters.SubmissionDisplay {
    private static final java.lang.String EXTRA_PROFILE = "profile";

    public me.ccrama.redditslide.Adapters.MultiredditAdapter adapter;

    public me.ccrama.redditslide.Adapters.MultiredditPosts posts;

    public android.support.v7.widget.RecyclerView rv;

    public android.support.design.widget.FloatingActionButton fab;

    public int diff;

    private android.support.v4.widget.SwipeRefreshLayout refreshLayout;

    private int id;

    private int totalItemCount;

    private int visibleItemCount;

    private int pastVisiblesItems;

    private java.lang.String profile;

    @android.support.annotation.NonNull
    private android.support.v7.widget.RecyclerView.LayoutManager createLayoutManager(final int numColumns) {
        return new me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager(numColumns, me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager.VERTICAL);
    }

    private int getNumColumns(final int orientation) {
        final int numColumns;
        boolean singleColumnMultiWindow = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            singleColumnMultiWindow = getActivity().isInMultiWindowMode() && me.ccrama.redditslide.SettingValues.singleColumnMultiWindow;
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

    @java.lang.Override
    public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
        android.view.View v = inflater.inflate(me.ccrama.redditslide.R.layout.fragment_verticalcontent, container, false);
        rv = v.findViewById(me.ccrama.redditslide.R.id.vertical_content);
        final android.support.v7.widget.RecyclerView.LayoutManager mLayoutManager = createLayoutManager(getNumColumns(getResources().getConfiguration().orientation));
        rv.setLayoutManager(mLayoutManager);
        if (me.ccrama.redditslide.SettingValues.fab) {
            fab = v.findViewById(me.ccrama.redditslide.R.id.post_floating_action_button);
            if (me.ccrama.redditslide.SettingValues.fabType == me.ccrama.redditslide.Constants.FAB_POST) {
                fab.setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        final java.util.ArrayList<java.lang.String> subs = new java.util.ArrayList<>();
                        for (net.dean.jraw.models.MultiSubreddit s : posts.multiReddit.getSubreddits()) {
                            subs.add(s.getDisplayName());
                        }
                        new com.afollestad.materialdialogs.MaterialDialog.Builder(getActivity()).title(me.ccrama.redditslide.R.string.multi_submit_which_sub).items(subs).itemsCallback(new com.afollestad.materialdialogs.MaterialDialog.ListCallback() {
                            @java.lang.Override
                            public void onSelection(com.afollestad.materialdialogs.MaterialDialog dialog, android.view.View itemView, int which, java.lang.CharSequence text) {
                                android.content.Intent i = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.Submit.class);
                                i.putExtra(me.ccrama.redditslide.Activities.Submit.EXTRA_SUBREDDIT, subs.get(which));
                                startActivity(i);
                            }
                        }).show();
                    }
                });
            } else if (me.ccrama.redditslide.SettingValues.fabType == me.ccrama.redditslide.Constants.FAB_SEARCH) {
                fab.setImageResource(me.ccrama.redditslide.R.drawable.search);
                fab.setOnClickListener(new android.view.View.OnClickListener() {
                    java.lang.String term;

                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        com.afollestad.materialdialogs.MaterialDialog.Builder builder = new com.afollestad.materialdialogs.MaterialDialog.Builder(getActivity()).title(me.ccrama.redditslide.R.string.search_title).alwaysCallInputCallback().input(getString(me.ccrama.redditslide.R.string.search_msg), "", new com.afollestad.materialdialogs.MaterialDialog.InputCallback() {
                            @java.lang.Override
                            public void onInput(com.afollestad.materialdialogs.MaterialDialog materialDialog, java.lang.CharSequence charSequence) {
                                term = charSequence.toString();
                            }
                        });
                        builder.positiveText(getString(me.ccrama.redditslide.R.string.search_subreddit, "/m/" + posts.multiReddit.getDisplayName())).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                            @java.lang.Override
                            public void onClick(@android.support.annotation.NonNull
                            com.afollestad.materialdialogs.MaterialDialog materialDialog, @android.support.annotation.NonNull
                            com.afollestad.materialdialogs.DialogAction dialogAction) {
                                android.content.Intent i = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.Search.class);
                                i.putExtra(me.ccrama.redditslide.Activities.Search.EXTRA_TERM, term);
                                i.putExtra(me.ccrama.redditslide.Activities.Search.EXTRA_MULTIREDDIT, posts.multiReddit.getDisplayName());
                                startActivity(i);
                            }
                        });
                        builder.show();
                    }
                });
            } else {
                fab.setImageResource(me.ccrama.redditslide.R.drawable.hide);
                fab.setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        if (!me.ccrama.redditslide.Reddit.fabClear) {
                            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(getActivity()).setTitle(me.ccrama.redditslide.R.string.settings_fabclear).setMessage(me.ccrama.redditslide.R.string.settings_fabclear_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                                @java.lang.Override
                                public void onClick(android.content.DialogInterface dialog, int which) {
                                    me.ccrama.redditslide.Reddit.colors.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_FAB_CLEAR, true).apply();
                                    me.ccrama.redditslide.Reddit.fabClear = true;
                                    clearSeenPosts(false);
                                }
                            }).show();
                        } else {
                            clearSeenPosts(false);
                        }
                    }
                });
                fab.setOnLongClickListener(new android.view.View.OnLongClickListener() {
                    @java.lang.Override
                    public boolean onLongClick(android.view.View v) {
                        if (!me.ccrama.redditslide.Reddit.fabClear) {
                            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(getActivity()).setTitle(me.ccrama.redditslide.R.string.settings_fabclear).setMessage(me.ccrama.redditslide.R.string.settings_fabclear_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                                @java.lang.Override
                                public void onClick(android.content.DialogInterface dialog, int which) {
                                    me.ccrama.redditslide.Reddit.colors.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_FAB_CLEAR, true).apply();
                                    me.ccrama.redditslide.Reddit.fabClear = true;
                                    clearSeenPosts(true);
                                }
                            }).show();
                        } else {
                            clearSeenPosts(true);
                        }
                        /* ToDo Make a sncakbar with an undo option of the clear all
                        View.OnClickListener undoAction = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        adapter.dataSet.posts = original;
                        for(Submission post : adapter.dataSet.posts){
                        if(HasSeen.getSeen(post.getFullName()))
                        Hidden.undoHidden(post);
                        }
                        }
                        };
                         */
                        android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(rv, getResources().getString(me.ccrama.redditslide.R.string.posts_hidden_forever), android.support.design.widget.Snackbar.LENGTH_LONG);
                        android.view.View view = s.getView();
                        android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(android.graphics.Color.WHITE);
                        s.show();
                        return false;
                    }
                });
            }
        } else {
            v.findViewById(me.ccrama.redditslide.R.id.post_floating_action_button).setVisibility(android.view.View.GONE);
        }
        refreshLayout = v.findViewById(me.ccrama.redditslide.R.id.activity_main_swipe_refresh_layout);
        /**
         * If using List view mode, we need to remove the start margin from the SwipeRefreshLayout.
         * The scrollbar style of "outsideInset" creates a 4dp padding around it. To counter this,
         * change the scrollbar style to "insideOverlay" when list view is enabled.
         * To recap: this removes the margins from the start/end so list view is full-width.
         */
        if (me.ccrama.redditslide.SettingValues.defaultCardView == me.ccrama.redditslide.Views.CreateCardView.CardEnum.LIST) {
            android.widget.RelativeLayout.LayoutParams params = new android.widget.RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.setMarginStart(0);
            }
            rv.setScrollBarStyle(android.view.View.SCROLLBARS_INSIDE_OVERLAY);
            refreshLayout.setLayoutParams(params);
        }
        java.util.List<net.dean.jraw.models.MultiReddit> multireddits;
        if (profile.isEmpty()) {
            multireddits = me.ccrama.redditslide.UserSubscriptions.multireddits;
        } else {
            multireddits = me.ccrama.redditslide.UserSubscriptions.public_multireddits.get(profile);
        }
        if ((multireddits != null) && (!multireddits.isEmpty())) {
            refreshLayout.setColorSchemeColors(me.ccrama.redditslide.Visuals.Palette.getColors(multireddits.get(id).getDisplayName(), getActivity()));
        }
        // If we use 'findViewById(R.id.header).getMeasuredHeight()', 0 is always returned.
        // So, we estimate the height of the header in dp
        refreshLayout.setProgressViewOffset(false, me.ccrama.redditslide.Constants.TAB_HEADER_VIEW_OFFSET - me.ccrama.redditslide.Constants.PTR_OFFSET_TOP, me.ccrama.redditslide.Constants.TAB_HEADER_VIEW_OFFSET + me.ccrama.redditslide.Constants.PTR_OFFSET_BOTTOM);
        refreshLayout.post(new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
        if ((multireddits != null) && (!multireddits.isEmpty())) {
            posts = new me.ccrama.redditslide.Adapters.MultiredditPosts(multireddits.get(id).getDisplayName(), profile);
            adapter = new me.ccrama.redditslide.Adapters.MultiredditAdapter(getActivity(), posts, rv, refreshLayout, this);
            rv.setAdapter(adapter);
            rv.setItemAnimator(new com.mikepenz.itemanimators.SlideUpAlphaAnimator().withInterpolator(new android.support.v4.view.animation.LinearOutSlowInInterpolator()));
            posts.loadMore(getActivity(), this, true, adapter);
            refreshLayout.setOnRefreshListener(new android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener() {
                @java.lang.Override
                public void onRefresh() {
                    posts.loadMore(getActivity(), me.ccrama.redditslide.Fragments.MultiredditView.this, true, adapter);
                    // TODO catch errors
                }
            });
            if (fab != null) {
                fab.show();
            }
            rv.addOnScrollListener(new me.ccrama.redditslide.handler.ToolbarScrollHideHandler(((android.support.v7.widget.Toolbar) (getActivity().findViewById(me.ccrama.redditslide.R.id.toolbar))), getActivity().findViewById(me.ccrama.redditslide.R.id.header)) {
                @java.lang.Override
                public void onScrolled(android.support.v7.widget.RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    visibleItemCount = rv.getLayoutManager().getChildCount();
                    totalItemCount = rv.getLayoutManager().getItemCount();
                    int[] firstVisibleItems;
                    firstVisibleItems = ((me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager) (rv.getLayoutManager())).findFirstVisibleItemPositions(null);
                    if ((firstVisibleItems != null) && (firstVisibleItems.length > 0)) {
                        for (int firstVisibleItem : firstVisibleItems) {
                            pastVisiblesItems = firstVisibleItem;
                            if ((me.ccrama.redditslide.SettingValues.scrollSeen && (pastVisiblesItems > 0)) && me.ccrama.redditslide.SettingValues.storeHistory) {
                                me.ccrama.redditslide.HasSeen.addSeenScrolling(posts.posts.get(pastVisiblesItems - 1).getFullName());
                            }
                        }
                    }
                    if (!posts.loading) {
                        if ((((visibleItemCount + pastVisiblesItems) + 5) >= totalItemCount) && (!posts.nomore)) {
                            posts.loading = true;
                            posts.loadMore(getActivity(), me.ccrama.redditslide.Fragments.MultiredditView.this, false, adapter);
                        }
                    }
                    if (recyclerView.getScrollState() == android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING) {
                        diff += dy;
                    } else {
                        diff = 0;
                    }
                    if (fab != null) {
                        if (((dy <= 0) && (fab.getId() != 0)) && me.ccrama.redditslide.SettingValues.fab) {
                            if ((recyclerView.getScrollState() != android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING) || (diff < ((-fab.getHeight()) * 2)))
                                fab.show();

                        } else {
                            fab.hide();
                        }
                    }
                }
            });
        }
        return v;
    }

    private java.util.List<net.dean.jraw.models.Submission> clearSeenPosts(boolean forever) {
        if (posts.posts != null) {
            java.util.List<net.dean.jraw.models.Submission> originalDataSetPosts = posts.posts;
            me.ccrama.redditslide.OfflineSubreddit o = me.ccrama.redditslide.OfflineSubreddit.getSubreddit("multi" + posts.multiReddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), false, getActivity());
            for (int i = posts.posts.size(); i > (-1); i--) {
                try {
                    if (me.ccrama.redditslide.HasSeen.getSeen(posts.posts.get(i))) {
                        if (forever) {
                            me.ccrama.redditslide.Hidden.setHidden(posts.posts.get(i));
                        }
                        o.clearPost(posts.posts.get(i));
                        posts.posts.remove(i);
                        if (posts.posts.isEmpty()) {
                            adapter.notifyDataSetChanged();
                        } else {
                            rv.setItemAnimator(new com.mikepenz.itemanimators.AlphaInAnimator());
                            adapter.notifyItemRemoved(i + 1);
                        }
                    }
                } catch (java.lang.IndexOutOfBoundsException e) {
                    // Let the loop reset itself
                }
            }
            o.writeToMemoryNoStorage();
            rv.setItemAnimator(new com.mikepenz.itemanimators.SlideUpAlphaAnimator().withInterpolator(new android.support.v4.view.animation.LinearOutSlowInInterpolator()));
            return originalDataSetPosts;
        }
        return null;
    }

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.os.Bundle bundle = this.getArguments();
        id = bundle.getInt("id", 0);
        profile = bundle.getString(me.ccrama.redditslide.Fragments.MultiredditView.EXTRA_PROFILE, "");
    }

    @java.lang.Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        final int currentOrientation = newConfig.orientation;
        final me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager mLayoutManager = ((me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager) (rv.getLayoutManager()));
        mLayoutManager.setSpanCount(getNumColumns(currentOrientation));
    }

    @java.lang.Override
    public void updateSuccess(java.util.List<net.dean.jraw.models.Submission> submissions, final int startIndex) {
        adapter.context.runOnUiThread(new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                refreshLayout.setRefreshing(false);
                if (startIndex != (-1)) {
                    adapter.notifyItemRangeInserted(startIndex + 1, posts.posts.size());
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @java.lang.Override
    public void updateOffline(java.util.List<net.dean.jraw.models.Submission> submissions, long cacheTime) {
        adapter.setError(true);
        refreshLayout.setRefreshing(false);
    }

    @java.lang.Override
    public void updateOfflineError() {
    }

    @java.lang.Override
    public void updateError() {
    }

    @java.lang.Override
    public void updateViews() {
        try {
            adapter.notifyItemRangeChanged(0, adapter.dataSet.getPosts().size());
        } catch (java.lang.Exception e) {
        }
    }

    @java.lang.Override
    public void onAdapterUpdated() {
        adapter.notifyDataSetChanged();
    }
}