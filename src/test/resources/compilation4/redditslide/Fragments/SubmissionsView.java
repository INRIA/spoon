package me.ccrama.redditslide.Fragments;
import me.ccrama.redditslide.Adapters.SubmissionAdapter;
import me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager;
import java.util.Locale;
import me.ccrama.redditslide.Activities.*;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.ColorPreferences;
import me.ccrama.redditslide.Adapters.SubmissionDisplay;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.OfflineSubreddit;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.HasSeen;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Views.CreateCardView;
import me.ccrama.redditslide.Constants;
import me.ccrama.redditslide.Hidden;
import java.util.List;
import me.ccrama.redditslide.handler.ToolbarScrollHideHandler;
import me.ccrama.redditslide.Adapters.SubredditPosts;
public class SubmissionsView extends android.support.v4.app.Fragment implements me.ccrama.redditslide.Adapters.SubmissionDisplay {
    private static int adapterPosition;

    private static int currentPosition;

    public me.ccrama.redditslide.Adapters.SubredditPosts posts;

    public android.support.v7.widget.RecyclerView rv;

    public me.ccrama.redditslide.Adapters.SubmissionAdapter adapter;

    public java.lang.String id;

    public boolean main;

    public boolean forced;

    int diff;

    boolean forceLoad;

    private android.support.design.widget.FloatingActionButton fab;

    private int visibleItemCount;

    private int pastVisiblesItems;

    private int totalItemCount;

    private android.support.v4.widget.SwipeRefreshLayout mSwipeRefreshLayout;

    private static net.dean.jraw.models.Submission currentSubmission;

    @java.lang.Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        final int currentOrientation = newConfig.orientation;
        final me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager mLayoutManager = ((me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager) (rv.getLayoutManager()));
        mLayoutManager.setSpanCount(me.ccrama.redditslide.Fragments.SubmissionsView.getNumColumns(currentOrientation, getActivity()));
    }

    java.lang.Runnable mLongPressRunnable;

    android.view.GestureDetector detector = new android.view.GestureDetector(getActivity(), new android.view.GestureDetector.SimpleOnGestureListener());

    float origY;

    @java.lang.Override
    public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
        final android.content.Context contextThemeWrapper = new android.support.v7.view.ContextThemeWrapper(getActivity(), new me.ccrama.redditslide.ColorPreferences(inflater.getContext()).getThemeSubreddit(id));
        final android.view.View v = ((android.view.LayoutInflater) (contextThemeWrapper.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE))).inflate(me.ccrama.redditslide.R.layout.fragment_verticalcontent, container, false);
        if (getActivity() instanceof me.ccrama.redditslide.Activities.MainActivity) {
            v.findViewById(me.ccrama.redditslide.R.id.back).setBackgroundResource(0);
        }
        rv = v.findViewById(me.ccrama.redditslide.R.id.vertical_content);
        rv.setHasFixedSize(true);
        final android.support.v7.widget.RecyclerView.LayoutManager mLayoutManager;
        mLayoutManager = me.ccrama.redditslide.Fragments.SubmissionsView.createLayoutManager(me.ccrama.redditslide.Fragments.SubmissionsView.getNumColumns(getResources().getConfiguration().orientation, getActivity()));
        if (!(getActivity() instanceof me.ccrama.redditslide.Activities.SubredditView)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                v.findViewById(me.ccrama.redditslide.R.id.back).setBackground(null);
            } else {
                v.findViewById(me.ccrama.redditslide.R.id.back).setBackgroundDrawable(null);
            }
        }
        rv.setLayoutManager(mLayoutManager);
        rv.setItemAnimator(new com.mikepenz.itemanimators.SlideUpAlphaAnimator().withInterpolator(new android.support.v4.view.animation.LinearOutSlowInInterpolator()));
        rv.getLayoutManager().scrollToPosition(0);
        mSwipeRefreshLayout = v.findViewById(me.ccrama.redditslide.R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(me.ccrama.redditslide.Visuals.Palette.getColors(id, getContext()));
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
            } else {
                android.support.v4.view.MarginLayoutParamsCompat.setMarginStart(params, 0);
            }
            rv.setScrollBarStyle(android.view.View.SCROLLBARS_INSIDE_OVERLAY);
            mSwipeRefreshLayout.setLayoutParams(params);
        }
        /**
         * If we use 'findViewById(R.id.header).getMeasuredHeight()', 0 is always returned.
         * So, we estimate the height of the header in dp.
         * If the view type is "single" (and therefore "commentPager"), we need a different offset
         */
        final int HEADER_OFFSET = (me.ccrama.redditslide.SettingValues.single || (getActivity() instanceof me.ccrama.redditslide.Activities.SubredditView)) ? me.ccrama.redditslide.Constants.SINGLE_HEADER_VIEW_OFFSET : me.ccrama.redditslide.Constants.TAB_HEADER_VIEW_OFFSET;
        mSwipeRefreshLayout.setProgressViewOffset(false, HEADER_OFFSET - me.ccrama.redditslide.Constants.PTR_OFFSET_TOP, HEADER_OFFSET + me.ccrama.redditslide.Constants.PTR_OFFSET_BOTTOM);
        if (me.ccrama.redditslide.SettingValues.fab) {
            fab = v.findViewById(me.ccrama.redditslide.R.id.post_floating_action_button);
            if (me.ccrama.redditslide.SettingValues.fabType == me.ccrama.redditslide.Constants.FAB_POST) {
                fab.setImageResource(me.ccrama.redditslide.R.drawable.add);
                fab.setContentDescription(getString(me.ccrama.redditslide.R.string.btn_fab_post));
                fab.setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        android.content.Intent inte = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.Submit.class);
                        inte.putExtra(me.ccrama.redditslide.Activities.Submit.EXTRA_SUBREDDIT, id);
                        getActivity().startActivity(inte);
                    }
                });
            } else if (me.ccrama.redditslide.SettingValues.fabType == me.ccrama.redditslide.Constants.FAB_SEARCH) {
                fab.setImageResource(me.ccrama.redditslide.R.drawable.search);
                fab.setContentDescription(getString(me.ccrama.redditslide.R.string.btn_fab_search));
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
                        // Add "search current sub" if it is not frontpage/all/random
                        if (((((((((!id.equalsIgnoreCase("frontpage")) && (!id.equalsIgnoreCase("all"))) && (!id.contains("."))) && (!id.contains("/m/"))) && (!id.equalsIgnoreCase("friends"))) && (!id.equalsIgnoreCase("random"))) && (!id.equalsIgnoreCase("popular"))) && (!id.equalsIgnoreCase("myrandom"))) && (!id.equalsIgnoreCase("randnsfw"))) {
                            builder.positiveText(getString(me.ccrama.redditslide.R.string.search_subreddit, id)).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                                @java.lang.Override
                                public void onClick(@android.support.annotation.NonNull
                                com.afollestad.materialdialogs.MaterialDialog materialDialog, @android.support.annotation.NonNull
                                com.afollestad.materialdialogs.DialogAction dialogAction) {
                                    android.content.Intent i = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.Search.class);
                                    i.putExtra(me.ccrama.redditslide.Activities.Search.EXTRA_TERM, term);
                                    i.putExtra(me.ccrama.redditslide.Activities.Search.EXTRA_SUBREDDIT, id);
                                    startActivity(i);
                                }
                            });
                            builder.neutralText(me.ccrama.redditslide.R.string.search_all).onNeutral(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                                @java.lang.Override
                                public void onClick(@android.support.annotation.NonNull
                                com.afollestad.materialdialogs.MaterialDialog materialDialog, @android.support.annotation.NonNull
                                com.afollestad.materialdialogs.DialogAction dialogAction) {
                                    android.content.Intent i = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.Search.class);
                                    i.putExtra(me.ccrama.redditslide.Activities.Search.EXTRA_TERM, term);
                                    startActivity(i);
                                }
                            });
                        } else {
                            builder.positiveText(me.ccrama.redditslide.R.string.search_all).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                                @java.lang.Override
                                public void onClick(@android.support.annotation.NonNull
                                com.afollestad.materialdialogs.MaterialDialog materialDialog, @android.support.annotation.NonNull
                                com.afollestad.materialdialogs.DialogAction dialogAction) {
                                    android.content.Intent i = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.Search.class);
                                    i.putExtra(me.ccrama.redditslide.Activities.Search.EXTRA_TERM, term);
                                    startActivity(i);
                                }
                            });
                        }
                        builder.show();
                    }
                });
            } else {
                fab.setImageResource(me.ccrama.redditslide.R.drawable.hide);
                fab.setContentDescription(getString(me.ccrama.redditslide.R.string.btn_fab_hide));
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
                final android.os.Handler handler = new android.os.Handler();
                fab.setOnTouchListener(new android.view.View.OnTouchListener() {
                    @java.lang.Override
                    public boolean onTouch(android.view.View v, android.view.MotionEvent event) {
                        detector.onTouchEvent(event);
                        if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                            origY = event.getY();
                            handler.postDelayed(mLongPressRunnable, android.view.ViewConfiguration.getLongPressTimeout());
                        }
                        if (((event.getAction() == android.view.MotionEvent.ACTION_MOVE) && (java.lang.Math.abs(event.getY() - origY) > (fab.getHeight() / 2))) || (event.getAction() == android.view.MotionEvent.ACTION_UP)) {
                            handler.removeCallbacks(mLongPressRunnable);
                        }
                        return false;
                    }
                });
                mLongPressRunnable = new java.lang.Runnable() {
                    public void run() {
                        fab.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
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
                        android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(rv, getResources().getString(me.ccrama.redditslide.R.string.posts_hidden_forever), android.support.design.widget.Snackbar.LENGTH_LONG);
                        /* Todo a way to unhide
                        s.setAction(R.string.btn_undo, new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                        }
                        });
                         */
                        android.view.View view = s.getView();
                        android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(android.graphics.Color.WHITE);
                        s.show();
                    }
                };
            }
        } else {
            v.findViewById(me.ccrama.redditslide.R.id.post_floating_action_button).setVisibility(android.view.View.GONE);
        }
        if (fab != null)
            fab.show();

        header = getActivity().findViewById(me.ccrama.redditslide.R.id.header);
        // TODO, have it so that if the user clicks anywhere in the rv to hide and cancel GoToSubreddit?
        // final TextInputEditText GO_TO_SUB_FIELD = (TextInputEditText) getActivity().findViewById(R.id.toolbar_search);
        // final Toolbar TOOLBAR = ((Toolbar) getActivity().findViewById(R.id.toolbar));
        // final String PREV_TITLE = TOOLBAR.getTitle().toString();
        // final ImageView CLOSE_BUTTON = (ImageView) getActivity().findViewById(R.id.close);
        // 
        // rv.setOnTouchListener(new View.OnTouchListener() {
        // @Override
        // public boolean onTouch(View v, MotionEvent event) {
        // System.out.println("touched");
        // InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        // imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        // 
        // GO_TO_SUB_FIELD.setText("");
        // GO_TO_SUB_FIELD.setVisibility(View.GONE);
        // CLOSE_BUTTON.setVisibility(View.GONE);
        // TOOLBAR.setTitle(PREV_TITLE);
        // 
        // return false;
        // }
        // });
        resetScroll();
        me.ccrama.redditslide.Reddit.isLoading = false;
        if ((((me.ccrama.redditslide.Activities.MainActivity.shouldLoad == null) || (id == null)) || ((me.ccrama.redditslide.Activities.MainActivity.shouldLoad != null) && me.ccrama.redditslide.Activities.MainActivity.shouldLoad.equals(id))) || (!(getActivity() instanceof me.ccrama.redditslide.Activities.MainActivity))) {
            doAdapter();
        }
        return v;
    }

    android.view.View header;

    me.ccrama.redditslide.handler.ToolbarScrollHideHandler toolbarScroll;

    @android.support.annotation.NonNull
    public static android.support.v7.widget.RecyclerView.LayoutManager createLayoutManager(final int numColumns) {
        return new me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager(numColumns, me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager.VERTICAL);
    }

    public static int getNumColumns(final int orientation, android.app.Activity context) {
        final int numColumns;
        boolean singleColumnMultiWindow = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            singleColumnMultiWindow = context.isInMultiWindowMode() && me.ccrama.redditslide.SettingValues.singleColumnMultiWindow;
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

    public void doAdapter() {
        if (!me.ccrama.redditslide.Activities.MainActivity.isRestart) {
            mSwipeRefreshLayout.post(new java.lang.Runnable() {
                @java.lang.Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
        }
        posts = new me.ccrama.redditslide.Adapters.SubredditPosts(id, getContext());
        adapter = new me.ccrama.redditslide.Adapters.SubmissionAdapter(getActivity(), posts, rv, id, this);
        adapter.setHasStableIds(true);
        rv.setAdapter(adapter);
        posts.loadMore(getActivity(), this, true);
        mSwipeRefreshLayout.setOnRefreshListener(new android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener() {
            @java.lang.Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    public void doAdapter(boolean force18) {
        mSwipeRefreshLayout.post(new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        posts = new me.ccrama.redditslide.Adapters.SubredditPosts(id, getContext(), force18);
        adapter = new me.ccrama.redditslide.Adapters.SubmissionAdapter(getActivity(), posts, rv, id, this);
        adapter.setHasStableIds(true);
        rv.setAdapter(adapter);
        posts.loadMore(getActivity(), this, true);
        mSwipeRefreshLayout.setOnRefreshListener(new android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener() {
            @java.lang.Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    public java.util.List<net.dean.jraw.models.Submission> clearSeenPosts(boolean forever) {
        if (adapter.dataSet.posts != null) {
            java.util.List<net.dean.jraw.models.Submission> originalDataSetPosts = adapter.dataSet.posts;
            me.ccrama.redditslide.OfflineSubreddit o = me.ccrama.redditslide.OfflineSubreddit.getSubreddit(id.toLowerCase(java.util.Locale.ENGLISH), false, getActivity());
            for (int i = adapter.dataSet.posts.size(); i > (-1); i--) {
                try {
                    if (me.ccrama.redditslide.HasSeen.getSeen(adapter.dataSet.posts.get(i))) {
                        if (forever) {
                            me.ccrama.redditslide.Hidden.setHidden(adapter.dataSet.posts.get(i));
                        }
                        o.clearPost(adapter.dataSet.posts.get(i));
                        adapter.dataSet.posts.remove(i);
                        if (adapter.dataSet.posts.isEmpty()) {
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
            adapter.notifyItemRangeChanged(0, adapter.dataSet.posts.size());
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
        id = bundle.getString("id", "");
        main = bundle.getBoolean("main", false);
        forceLoad = bundle.getBoolean("load", false);
    }

    @java.lang.Override
    public void onResume() {
        super.onResume();
        if (((adapter != null) && (me.ccrama.redditslide.Fragments.SubmissionsView.adapterPosition > 0)) && (me.ccrama.redditslide.Fragments.SubmissionsView.currentPosition == me.ccrama.redditslide.Fragments.SubmissionsView.adapterPosition)) {
            if ((adapter.dataSet.getPosts().size() >= (me.ccrama.redditslide.Fragments.SubmissionsView.adapterPosition - 1)) && (adapter.dataSet.getPosts().get(me.ccrama.redditslide.Fragments.SubmissionsView.adapterPosition - 1) == me.ccrama.redditslide.Fragments.SubmissionsView.currentSubmission)) {
                adapter.performClick(me.ccrama.redditslide.Fragments.SubmissionsView.adapterPosition);
                me.ccrama.redditslide.Fragments.SubmissionsView.adapterPosition = -1;
            }
        }
    }

    public static void datachanged(int adaptorPosition2) {
        me.ccrama.redditslide.Fragments.SubmissionsView.adapterPosition = adaptorPosition2;
    }

    private void refresh() {
        posts.forced = true;
        forced = true;
        posts.loadMore(mSwipeRefreshLayout.getContext(), this, true, id);
    }

    public void forceRefresh() {
        toolbarScroll.toolbarShow();
        rv.getLayoutManager().scrollToPosition(0);
        mSwipeRefreshLayout.post(new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                refresh();
            }
        });
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @java.lang.Override
    public void updateSuccess(final java.util.List<net.dean.jraw.models.Submission> submissions, final int startIndex) {
        if (getActivity() != null) {
            if (getActivity() instanceof me.ccrama.redditslide.Activities.MainActivity) {
                if (((me.ccrama.redditslide.Activities.MainActivity) (getActivity())).runAfterLoad != null) {
                    new android.os.Handler().post(((me.ccrama.redditslide.Activities.MainActivity) (getActivity())).runAfterLoad);
                }
            }
            getActivity().runOnUiThread(new java.lang.Runnable() {
                @java.lang.Override
                public void run() {
                    if (mSwipeRefreshLayout != null) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    if ((startIndex != (-1)) && (!forced)) {
                        adapter.notifyItemRangeInserted(startIndex + 1, posts.posts.size());
                        adapter.notifyDataSetChanged();
                    } else {
                        forced = false;
                        rv.scrollToPosition(0);
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            if (me.ccrama.redditslide.Activities.MainActivity.isRestart) {
                me.ccrama.redditslide.Activities.MainActivity.isRestart = false;
                posts.offline = false;
                rv.getLayoutManager().scrollToPosition(me.ccrama.redditslide.Activities.MainActivity.restartPage + 1);
            }
            if (startIndex < 10)
                resetScroll();

        }
    }

    @java.lang.Override
    public void updateOffline(java.util.List<net.dean.jraw.models.Submission> submissions, final long cacheTime) {
        if (getActivity() instanceof me.ccrama.redditslide.Activities.MainActivity) {
            if (((me.ccrama.redditslide.Activities.MainActivity) (getActivity())).runAfterLoad != null) {
                new android.os.Handler().post(((me.ccrama.redditslide.Activities.MainActivity) (getActivity())).runAfterLoad);
            }
        }
        if (this.isAdded()) {
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @java.lang.Override
    public void updateOfflineError() {
        if (getActivity() instanceof me.ccrama.redditslide.Activities.MainActivity) {
            if (((me.ccrama.redditslide.Activities.MainActivity) (getActivity())).runAfterLoad != null) {
                new android.os.Handler().post(((me.ccrama.redditslide.Activities.MainActivity) (getActivity())).runAfterLoad);
            }
        }
        mSwipeRefreshLayout.setRefreshing(false);
        adapter.setError(true);
    }

    @java.lang.Override
    public void updateError() {
        if (getActivity() instanceof me.ccrama.redditslide.Activities.MainActivity) {
            if (((me.ccrama.redditslide.Activities.MainActivity) (getActivity())).runAfterLoad != null) {
                new android.os.Handler().post(((me.ccrama.redditslide.Activities.MainActivity) (getActivity())).runAfterLoad);
            }
        }
        mSwipeRefreshLayout.setRefreshing(false);
        adapter.setError(true);
    }

    @java.lang.Override
    public void updateViews() {
        if (adapter.dataSet.posts != null) {
            for (int i = adapter.dataSet.posts.size(); i > (-1); i--) {
                try {
                    if (me.ccrama.redditslide.HasSeen.getSeen(adapter.dataSet.posts.get(i))) {
                        adapter.notifyItemChanged(i + 1);
                    }
                } catch (java.lang.IndexOutOfBoundsException e) {
                    // Let the loop reset itself
                }
            }
        }
    }

    @java.lang.Override
    public void onAdapterUpdated() {
        adapter.notifyDataSetChanged();
    }

    public void resetScroll() {
        if (toolbarScroll == null) {
            toolbarScroll = new me.ccrama.redditslide.handler.ToolbarScrollHideHandler(((me.ccrama.redditslide.Activities.BaseActivity) (getActivity())).mToolbar, header) {
                @java.lang.Override
                public void onScrolled(android.support.v7.widget.RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if ((((!posts.loading) && (!posts.nomore)) && (!posts.offline)) && (!adapter.isError)) {
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
                        if (((visibleItemCount + pastVisiblesItems) + 5) >= totalItemCount) {
                            posts.loading = true;
                            posts.loadMore(mSwipeRefreshLayout.getContext(), me.ccrama.redditslide.Fragments.SubmissionsView.this, false, posts.subreddit);
                        }
                    }
                    /* if(dy <= 0 && !down){
                    (getActivity()).findViewById(R.id.header).animate().translationY(((BaseActivity)getActivity()).mToolbar.getTop()).setInterpolator(new AccelerateInterpolator()).start();
                    down = true;
                    } else if(down){
                    (getActivity()).findViewById(R.id.header).animate().translationY(((BaseActivity)getActivity()).mToolbar.getTop()).setInterpolator(new AccelerateInterpolator()).start();
                    down = false;
                    }
                     */
                    // todo For future implementation instead of scrollFlags
                    if (recyclerView.getScrollState() == android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING) {
                        diff += dy;
                    } else {
                        diff = 0;
                    }
                    if (fab != null) {
                        if (((dy <= 0) && (fab.getId() != 0)) && me.ccrama.redditslide.SettingValues.fab) {
                            if ((recyclerView.getScrollState() != android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING) || (diff < ((-fab.getHeight()) * 2))) {
                                fab.show();
                            }
                        } else {
                            fab.hide();
                        }
                    }
                }

                @java.lang.Override
                public void onScrollStateChanged(android.support.v7.widget.RecyclerView recyclerView, int newState) {
                    // switch (newState) {
                    // case RecyclerView.SCROLL_STATE_IDLE:
                    // ((Reddit)getActivity().getApplicationContext()).getImageLoader().resume();
                    // break;
                    // case RecyclerView.SCROLL_STATE_DRAGGING:
                    // ((Reddit)getActivity().getApplicationContext()).getImageLoader().resume();
                    // break;
                    // case RecyclerView.SCROLL_STATE_SETTLING:
                    // ((Reddit)getActivity().getApplicationContext()).getImageLoader().pause();
                    // break;
                    // }
                    super.onScrollStateChanged(recyclerView, newState);
                    // If the toolbar search is open, and the user scrolls in the Main view--close the search UI
                    if (((getActivity() instanceof me.ccrama.redditslide.Activities.MainActivity) && ((me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_TOOLBAR) || (me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_BOTH))) && (((me.ccrama.redditslide.Activities.MainActivity) (getContext())).findViewById(me.ccrama.redditslide.R.id.toolbar_search).getVisibility() == android.view.View.VISIBLE)) {
                        ((me.ccrama.redditslide.Activities.MainActivity) (getContext())).findViewById(me.ccrama.redditslide.R.id.close_search_toolbar).performClick();
                    }
                }
            };
            rv.addOnScrollListener(toolbarScroll);
        } else {
            toolbarScroll.reset = true;
        }
    }

    public static void currentPosition(int adapterPosition) {
        me.ccrama.redditslide.Fragments.SubmissionsView.currentPosition = adapterPosition;
    }

    public static void currentSubmission(net.dean.jraw.models.Submission current) {
        me.ccrama.redditslide.Fragments.SubmissionsView.currentSubmission = current;
    }
}