package me.ccrama.redditslide.Fragments;
import java.util.Locale;
import me.ccrama.redditslide.ContentType;
import java.util.HashMap;
import java.util.ArrayList;
import me.ccrama.redditslide.Activities.Tumblr;
import me.ccrama.redditslide.Activities.MediaView;
import me.ccrama.redditslide.util.NetworkUtil;
import me.ccrama.redditslide.Views.PreCachingLayoutManagerComments;
import me.ccrama.redditslide.SpoilerRobotoTextView;
import me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder;
import me.ccrama.redditslide.Activities.Album;
import me.ccrama.redditslide.Activities.TumblrPager;
import me.ccrama.redditslide.UserSubscriptions;
import java.util.List;
import me.ccrama.redditslide.handler.ToolbarScrollHideHandler;
import java.util.HashSet;
import me.ccrama.redditslide.Activities.FullscreenVideo;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.ccrama.redditslide.Adapters.CommentUrlObject;
import me.ccrama.redditslide.ImageFlairs;
import me.ccrama.redditslide.Activities.CommentSearch;
import me.ccrama.redditslide.Activities.CommentsScreen;
import me.ccrama.redditslide.Adapters.CommentNavType;
import me.ccrama.redditslide.util.SubmissionParser;
import me.ccrama.redditslide.PostMatch;
import me.ccrama.redditslide.DataShare;
import me.ccrama.redditslide.Adapters.SettingsSubAdapter;
import me.ccrama.redditslide.Adapters.CommentObject;
import me.ccrama.redditslide.OfflineSubreddit;
import me.ccrama.redditslide.Activities.AlbumPager;
import me.ccrama.redditslide.Activities.Related;
import me.ccrama.redditslide.Adapters.CommentItem;
import me.ccrama.redditslide.util.LinkUtil;
import me.ccrama.redditslide.Views.DoEditorActions;
import me.ccrama.redditslide.util.OnSingleClickListener;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.Activities.ShadowboxComments;
import me.ccrama.redditslide.Activities.MainActivity;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Activities.Profile;
import me.ccrama.redditslide.Activities.SubredditView;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.TimeUtils;
import me.ccrama.redditslide.Adapters.SubmissionComments;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Views.CommentOverflow;
import me.ccrama.redditslide.Adapters.MoreChildItem;
import me.ccrama.redditslide.Activities.SendMessage;
import java.util.Calendar;
import me.ccrama.redditslide.ColorPreferences;
import java.io.IOException;
import me.ccrama.redditslide.Drafts;
import me.ccrama.redditslide.Constants;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.Activities.Submit;
import me.ccrama.redditslide.Activities.Wiki;
import me.ccrama.redditslide.Adapters.CommentAdapter;
/**
 * Fragment which displays comment trees.
 *
 * @see CommentsScreen
 */
public class CommentPage extends android.support.v4.app.Fragment implements android.support.v7.widget.Toolbar.OnMenuItemClickListener {
    boolean np;

    public boolean archived;

    public boolean locked;

    public boolean contest;

    boolean loadMore;

    private android.support.v4.widget.SwipeRefreshLayout mSwipeRefreshLayout;

    public android.support.v7.widget.RecyclerView rv;

    private int page;

    private me.ccrama.redditslide.Adapters.SubmissionComments comments;

    private boolean single;

    public me.ccrama.redditslide.Adapters.CommentAdapter adapter;

    private java.lang.String fullname;

    private java.lang.String context;

    private int contextNumber;

    private android.content.ContextWrapper contextThemeWrapper;

    private me.ccrama.redditslide.Views.PreCachingLayoutManagerComments mLayoutManager;

    public java.lang.String subreddit;

    public boolean loaded = false;

    public boolean overrideFab;

    private boolean upvoted = false;

    private boolean downvoted = false;

    private boolean currentlySubbed;

    private boolean collapsed = me.ccrama.redditslide.SettingValues.collapseCommentsDefault;

    public void doResult(android.content.Intent data) {
        if (data.hasExtra("fullname")) {
            java.lang.String fullname = data.getExtras().getString("fullname");
            adapter.currentSelectedItem = fullname;
            adapter.reset(getContext(), comments, rv, comments.submission, true);
            adapter.notifyDataSetChanged();
            int i = 2;
            for (me.ccrama.redditslide.Adapters.CommentObject n : comments.comments) {
                if ((n instanceof me.ccrama.redditslide.Adapters.CommentItem) && n.comment.getComment().getFullName().contains(fullname)) {
                    ((me.ccrama.redditslide.Views.PreCachingLayoutManagerComments) (rv.getLayoutManager())).scrollToPositionWithOffset(i, toolbar.getHeight());
                    break;
                }
                i++;
            }
        }
    }

    @java.lang.Override
    public void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 423) && (resultCode == android.app.Activity.RESULT_OK)) {
            doResult(data);
        } else if (requestCode == 3333) {
            for (android.support.v4.app.Fragment fragment : getFragmentManager().getFragments()) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    me.ccrama.redditslide.handler.ToolbarScrollHideHandler toolbarScroll;

    public android.support.v7.widget.Toolbar toolbar;

    public int headerHeight;

    public int shownHeaders = 0;

    public void doTopBar(net.dean.jraw.models.Submission s) {
        archived = s.isArchived();
        locked = s.isLocked();
        contest = s.getDataNode().get("contest_mode").asBoolean();
        doTopBar();
    }

    public void doTopBarNotify(net.dean.jraw.models.Submission submission, me.ccrama.redditslide.Adapters.CommentAdapter adapter2) {
        doTopBar(submission);
        if (adapter2 != null)
            adapter2.notifyItemChanged(0);

    }

    public void doRefresh(boolean b) {
        if (b) {
            v.findViewById(me.ccrama.redditslide.R.id.progress).setVisibility(android.view.View.VISIBLE);
        } else {
            v.findViewById(me.ccrama.redditslide.R.id.progress).setVisibility(android.view.View.GONE);
        }
    }

    public void doTopBar() {
        final android.view.View loadallV = v.findViewById(me.ccrama.redditslide.R.id.loadall);
        final android.view.View npV = v.findViewById(me.ccrama.redditslide.R.id.np);
        final android.view.View archivedV = v.findViewById(me.ccrama.redditslide.R.id.archived);
        final android.view.View lockedV = v.findViewById(me.ccrama.redditslide.R.id.locked);
        final android.view.View headerV = v.findViewById(me.ccrama.redditslide.R.id.toolbar);
        final android.view.View contestV = v.findViewById(me.ccrama.redditslide.R.id.contest);
        shownHeaders = 0;
        headerV.measure(android.view.View.MeasureSpec.UNSPECIFIED, android.view.View.MeasureSpec.UNSPECIFIED);
        loadallV.setVisibility(android.view.View.VISIBLE);
        npV.setVisibility(android.view.View.VISIBLE);
        archivedV.setVisibility(android.view.View.VISIBLE);
        lockedV.setVisibility(android.view.View.VISIBLE);
        contestV.setVisibility(android.view.View.VISIBLE);
        if (!loadMore) {
            loadallV.setVisibility(android.view.View.GONE);
        } else {
            loadallV.measure(android.view.View.MeasureSpec.UNSPECIFIED, android.view.View.MeasureSpec.UNSPECIFIED);
            shownHeaders += loadallV.getMeasuredHeight();
            loadallV.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    doRefresh(true);
                    shownHeaders -= loadallV.getMeasuredHeight();
                    headerHeight = headerV.getMeasuredHeight() + shownHeaders;
                    loadallV.setVisibility(android.view.View.GONE);
                    if (adapter != null) {
                        adapter.notifyItemChanged(0);
                    }
                    // avoid crashes when load more is clicked before loading is finished
                    if (comments.mLoadData != null) {
                        comments.mLoadData.cancel(true);
                    }
                    comments = new me.ccrama.redditslide.Adapters.SubmissionComments(fullname, me.ccrama.redditslide.Fragments.CommentPage.this, mSwipeRefreshLayout);
                    comments.setSorting(net.dean.jraw.models.CommentSort.CONFIDENCE);
                    loadMore = false;
                    mSwipeRefreshLayout.setProgressViewOffset(false, me.ccrama.redditslide.Constants.SINGLE_HEADER_VIEW_OFFSET - me.ccrama.redditslide.Constants.PTR_OFFSET_TOP, me.ccrama.redditslide.Constants.SINGLE_HEADER_VIEW_OFFSET + (me.ccrama.redditslide.Constants.PTR_OFFSET_BOTTOM + shownHeaders));
                }
            });
        }
        if ((!np) && (!archived)) {
            npV.setVisibility(android.view.View.GONE);
            archivedV.setVisibility(android.view.View.GONE);
        } else if (archived) {
            archivedV.measure(android.view.View.MeasureSpec.UNSPECIFIED, android.view.View.MeasureSpec.UNSPECIFIED);
            shownHeaders += archivedV.getMeasuredHeight();
            npV.setVisibility(android.view.View.GONE);
            archivedV.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getColor(subreddit));
        } else {
            npV.measure(android.view.View.MeasureSpec.UNSPECIFIED, android.view.View.MeasureSpec.UNSPECIFIED);
            shownHeaders += npV.getMeasuredHeight();
            archivedV.setVisibility(android.view.View.GONE);
            npV.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getColor(subreddit));
        }
        if (locked) {
            lockedV.measure(android.view.View.MeasureSpec.UNSPECIFIED, android.view.View.MeasureSpec.UNSPECIFIED);
            shownHeaders += lockedV.getMeasuredHeight();
            lockedV.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getColor(subreddit));
        } else {
            lockedV.setVisibility(android.view.View.GONE);
        }
        if (contest) {
            contestV.measure(android.view.View.MeasureSpec.UNSPECIFIED, android.view.View.MeasureSpec.UNSPECIFIED);
            shownHeaders += contestV.getMeasuredHeight();
            contestV.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getColor(subreddit));
        } else {
            contestV.setVisibility(android.view.View.GONE);
        }
        headerHeight = headerV.getMeasuredHeight() + shownHeaders;
        // If we use 'findViewById(R.id.header).getMeasuredHeight()', 0 is always returned.
        // So, we estimate the height of the header in dp. Account for show headers.
        mSwipeRefreshLayout.setProgressViewOffset(false, me.ccrama.redditslide.Constants.SINGLE_HEADER_VIEW_OFFSET - me.ccrama.redditslide.Constants.PTR_OFFSET_TOP, me.ccrama.redditslide.Constants.SINGLE_HEADER_VIEW_OFFSET + (me.ccrama.redditslide.Constants.PTR_OFFSET_BOTTOM + shownHeaders));
    }

    android.view.View v;

    public android.view.View fastScroll;

    public android.support.design.widget.FloatingActionButton fab;

    public int diff;

    @java.lang.Override
    public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
        android.view.LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        v = localInflater.inflate(me.ccrama.redditslide.R.layout.fragment_verticalcontenttoolbar, container, false);
        rv = v.findViewById(me.ccrama.redditslide.R.id.vertical_content);
        rv.setLayoutManager(mLayoutManager);
        rv.getLayoutManager().scrollToPosition(0);
        toolbar = v.findViewById(me.ccrama.redditslide.R.id.toolbar);
        toolbar.setPopupTheme(new me.ccrama.redditslide.ColorPreferences(getActivity()).getFontStyle().getBaseId());
        if ((((!me.ccrama.redditslide.SettingValues.fabComments) || archived) || np) || locked) {
            v.findViewById(me.ccrama.redditslide.R.id.comment_floating_action_button).setVisibility(android.view.View.GONE);
        } else {
            fab = v.findViewById(me.ccrama.redditslide.R.id.comment_floating_action_button);
            if (me.ccrama.redditslide.SettingValues.fastscroll) {
                android.widget.FrameLayout.LayoutParams fabs = ((android.widget.FrameLayout.LayoutParams) (fab.getLayoutParams()));
                fabs.setMargins(fabs.leftMargin, fabs.topMargin, fabs.rightMargin, fabs.bottomMargin * 3);
                fab.setLayoutParams(fabs);
            }
            fab.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    final com.afollestad.materialdialogs.MaterialDialog replyDialog = new com.afollestad.materialdialogs.MaterialDialog.Builder(getActivity()).customView(me.ccrama.redditslide.R.layout.edit_comment, false).cancelable(false).build();
                    final android.view.View replyView = replyDialog.getCustomView();
                    // Make the account selector visible
                    replyView.findViewById(me.ccrama.redditslide.R.id.profile).setVisibility(android.view.View.VISIBLE);
                    final android.widget.EditText e = replyView.findViewById(me.ccrama.redditslide.R.id.entry);
                    // Tint the replyLine appropriately if the base theme is Light or Sepia
                    if ((me.ccrama.redditslide.SettingValues.currentTheme == 1) || (me.ccrama.redditslide.SettingValues.currentTheme == 5)) {
                        final int TINT = android.support.v4.content.ContextCompat.getColor(getContext(), me.ccrama.redditslide.R.color.md_grey_600);
                        e.setHintTextColor(TINT);
                        e.getBackground().setColorFilter(TINT, android.graphics.PorterDuff.Mode.SRC_IN);
                    }
                    me.ccrama.redditslide.Views.DoEditorActions.doActions(e, replyView, getActivity().getSupportFragmentManager(), getActivity(), adapter.submission.isSelfPost() ? adapter.submission.getSelftext() : null, new java.lang.String[]{ adapter.submission.getAuthor() });
                    replyDialog.getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    replyView.findViewById(me.ccrama.redditslide.R.id.cancel).setOnClickListener(new android.view.View.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.view.View v) {
                            replyDialog.dismiss();
                        }
                    });
                    final android.widget.TextView profile = replyView.findViewById(me.ccrama.redditslide.R.id.profile);
                    final java.lang.String[] changedProfile = new java.lang.String[]{ me.ccrama.redditslide.Authentication.name };
                    profile.setText("/u/".concat(changedProfile[0]));
                    profile.setOnClickListener(new android.view.View.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.view.View v) {
                            final java.util.HashMap<java.lang.String, java.lang.String> accounts = new java.util.HashMap<>();
                            for (java.lang.String s : me.ccrama.redditslide.Authentication.authentication.getStringSet("accounts", new java.util.HashSet<java.lang.String>())) {
                                if (s.contains(":")) {
                                    accounts.put(s.split(":")[0], s.split(":")[1]);
                                } else {
                                    accounts.put(s, "");
                                }
                            }
                            final java.util.ArrayList<java.lang.String> keys = new java.util.ArrayList<>(accounts.keySet());
                            final int i = keys.indexOf(changedProfile[0]);
                            com.afollestad.materialdialogs.MaterialDialog.Builder builder = new com.afollestad.materialdialogs.MaterialDialog.Builder(getContext());
                            builder.title(getString(me.ccrama.redditslide.R.string.replies_switch_accounts));
                            builder.items(keys.toArray(new java.lang.String[keys.size()]));
                            builder.itemsCallbackSingleChoice(i, new com.afollestad.materialdialogs.MaterialDialog.ListCallbackSingleChoice() {
                                @java.lang.Override
                                public boolean onSelection(com.afollestad.materialdialogs.MaterialDialog dialog, android.view.View itemView, int which, java.lang.CharSequence text) {
                                    changedProfile[0] = keys.get(which);
                                    profile.setText("/u/".concat(changedProfile[0]));
                                    return true;
                                }
                            });
                            builder.alwaysCallSingleChoiceCallback();
                            builder.negativeText(me.ccrama.redditslide.R.string.btn_cancel);
                            builder.show();
                        }
                    });
                    replyView.findViewById(me.ccrama.redditslide.R.id.submit).setOnClickListener(new android.view.View.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.view.View v) {
                            adapter.dataSet.refreshLayout.setRefreshing(true);
                            adapter.new ReplyTaskComment(adapter.submission, changedProfile[0]).execute(e.getText().toString());
                            replyDialog.dismiss();
                        }
                    });
                    replyDialog.show();
                }
            });
        }
        if (fab != null)
            fab.show();

        resetScroll(false);
        fastScroll = v.findViewById(me.ccrama.redditslide.R.id.commentnav);
        if (!me.ccrama.redditslide.SettingValues.fastscroll) {
            fastScroll.setVisibility(android.view.View.GONE);
        } else {
            if (!me.ccrama.redditslide.SettingValues.showCollapseExpand) {
                v.findViewById(me.ccrama.redditslide.R.id.collapse_expand).setVisibility(android.view.View.GONE);
            } else {
                v.findViewById(me.ccrama.redditslide.R.id.collapse_expand).setVisibility(android.view.View.VISIBLE);
                v.findViewById(me.ccrama.redditslide.R.id.collapse_expand).setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        if (adapter != null) {
                            if (collapsed) {
                                adapter.expandAll();
                                collapsed = !collapsed;
                            } else {
                                adapter.collapseAll();
                                collapsed = !collapsed;
                            }
                        }
                    }
                });
            }
            v.findViewById(me.ccrama.redditslide.R.id.down).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    if (((adapter != null) && (adapter.keys != null)) && (adapter.keys.size() > 0)) {
                        goDown();
                    }
                }
            });
            v.findViewById(me.ccrama.redditslide.R.id.up).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    if (((adapter != null) && (adapter.keys != null)) && (adapter.keys.size() > 0))
                        goUp();

                }
            });
            v.findViewById(me.ccrama.redditslide.R.id.nav).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    if ((adapter != null) && (adapter.currentComments != null)) {
                        int parentCount;
                        int opCount;
                        int linkCount;
                        int awardCount;
                        parentCount = 0;
                        opCount = 0;
                        linkCount = 0;
                        awardCount = 0;
                        java.lang.String op = adapter.submission.getAuthor();
                        for (me.ccrama.redditslide.Adapters.CommentObject o : adapter.currentComments) {
                            if ((o.comment != null) && (!(o instanceof me.ccrama.redditslide.Adapters.MoreChildItem))) {
                                if (o.comment.isTopLevel())
                                    parentCount++;

                                if (((o.comment.getComment().getTimesGilded() > 0) || (o.comment.getComment().getTimesSilvered() > 0)) || (o.comment.getComment().getTimesPlatinized() > 0))
                                    awardCount++;

                                if ((o.comment.getComment().getAuthor() != null) && o.comment.getComment().getAuthor().equals(op)) {
                                    opCount++;
                                }
                                if (o.comment.getComment().getDataNode().has("body_html") && o.comment.getComment().getDataNode().get("body_html").asText().contains("&lt;/a")) {
                                    linkCount++;
                                }
                            }
                        }
                        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(getActivity()).setTitle(me.ccrama.redditslide.R.string.set_nav_mode).setSingleChoiceItems(me.ccrama.redditslide.Reddit.stringToArray((((((((((((((((((("Parent comment (" + parentCount) + ")") + ",") + "Children comment (highlight child comment & navigate)") + ",") + "OP (") + opCount) + ")") + ",") + "Time") + ",") + "Link (") + linkCount) + ")") + ",") + (me.ccrama.redditslide.Authentication.isLoggedIn ? "You" + "," : "")) + "Awarded (") + awardCount) + ")").toArray(new java.lang.String[me.ccrama.redditslide.Authentication.isLoggedIn ? 6 : 5]), getCurrentSort(), new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0 :
                                        currentSort = me.ccrama.redditslide.Adapters.CommentNavType.PARENTS;
                                        break;
                                    case 1 :
                                        currentSort = me.ccrama.redditslide.Adapters.CommentNavType.CHILDREN;
                                        break;
                                    case 2 :
                                        currentSort = me.ccrama.redditslide.Adapters.CommentNavType.OP;
                                        break;
                                    case 3 :
                                        currentSort = me.ccrama.redditslide.Adapters.CommentNavType.TIME;
                                        android.view.LayoutInflater inflater = getActivity().getLayoutInflater();
                                        final android.view.View dialoglayout = inflater.inflate(me.ccrama.redditslide.R.layout.commenttime, null);
                                        final com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(getActivity());
                                        final com.rey.material.widget.Slider landscape = dialoglayout.findViewById(me.ccrama.redditslide.R.id.landscape);
                                        final android.widget.TextView since = dialoglayout.findViewById(me.ccrama.redditslide.R.id.time_string);
                                        landscape.setValueRange(60, 18000, false);
                                        landscape.setOnPositionChangeListener(new com.rey.material.widget.Slider.OnPositionChangeListener() {
                                            @java.lang.Override
                                            public void onPositionChanged(com.rey.material.widget.Slider slider, boolean b, float v, float v1, int i, int i1) {
                                                java.util.Calendar c = java.util.Calendar.getInstance();
                                                sortTime = c.getTimeInMillis() - (i1 * 1000);
                                                int commentcount = 0;
                                                for (me.ccrama.redditslide.Adapters.CommentObject o : adapter.currentComments) {
                                                    if (((o.comment != null) && o.comment.getComment().getDataNode().has("created")) && (o.comment.getComment().getCreated().getTime() > sortTime)) {
                                                        commentcount += 1;
                                                    }
                                                }
                                                since.setText(((me.ccrama.redditslide.TimeUtils.getTimeAgo(sortTime, getActivity()) + " (") + commentcount) + " comments)");
                                            }
                                        });
                                        landscape.setValue(600, false);
                                        builder.setView(dialoglayout);
                                        builder.setPositiveButton(me.ccrama.redditslide.R.string.btn_set, null).show();
                                        break;
                                    case 5 :
                                        currentSort = (me.ccrama.redditslide.Authentication.isLoggedIn) ? me.ccrama.redditslide.Adapters.CommentNavType.YOU : me.ccrama.redditslide.Adapters.CommentNavType.GILDED;// gilded is 5 if not logged in

                                        break;
                                    case 4 :
                                        currentSort = me.ccrama.redditslide.Adapters.CommentNavType.LINK;
                                        break;
                                    case 6 :
                                        currentSort = me.ccrama.redditslide.Adapters.CommentNavType.GILDED;
                                        break;
                                }
                            }
                        }).show();
                    }
                }
            });
        }
        v.findViewById(me.ccrama.redditslide.R.id.up).setOnLongClickListener(new android.view.View.OnLongClickListener() {
            @java.lang.Override
            public boolean onLongClick(android.view.View v) {
                // Scroll to top
                rv.getLayoutManager().scrollToPosition(1);
                return true;
            }
        });
        if (me.ccrama.redditslide.SettingValues.voteGestures) {
            v.findViewById(me.ccrama.redditslide.R.id.up).setOnTouchListener(new me.ccrama.redditslide.Fragments.OnFlingGestureListener() {
                @java.lang.Override
                public void onRightToLeft() {
                }

                @java.lang.Override
                public void onLeftToRight() {
                }

                @java.lang.Override
                public void onBottomToTop() {
                    adapter.submissionViewHolder.upvote.performClick();
                    android.content.Context context = getContext();
                    int duration = android.widget.Toast.LENGTH_SHORT;
                    java.lang.CharSequence text;
                    if (!upvoted) {
                        text = getString(me.ccrama.redditslide.R.string.profile_upvoted);
                        downvoted = false;
                    } else {
                        text = getString(me.ccrama.redditslide.R.string.vote_removed);
                    }
                    upvoted = !upvoted;
                    android.widget.Toast toast = android.widget.Toast.makeText(context, text, duration);
                    toast.show();
                }

                @java.lang.Override
                public void onTopToBottom() {
                }
            });
        }
        if (me.ccrama.redditslide.SettingValues.voteGestures) {
            v.findViewById(me.ccrama.redditslide.R.id.down).setOnTouchListener(new me.ccrama.redditslide.Fragments.OnFlingGestureListener() {
                @java.lang.Override
                public void onRightToLeft() {
                }

                @java.lang.Override
                public void onLeftToRight() {
                }

                @java.lang.Override
                public void onBottomToTop() {
                    adapter.submissionViewHolder.downvote.performClick();
                    android.content.Context context = getContext();
                    int duration = android.widget.Toast.LENGTH_SHORT;
                    java.lang.CharSequence text;
                    if (!downvoted) {
                        text = getString(me.ccrama.redditslide.R.string.profile_downvoted);
                        upvoted = false;
                    } else {
                        text = getString(me.ccrama.redditslide.R.string.vote_removed);
                    }
                    downvoted = !downvoted;
                    android.widget.Toast toast = android.widget.Toast.makeText(context, text, duration);
                    toast.show();
                }

                @java.lang.Override
                public void onTopToBottom() {
                }
            });
        }
        toolbar.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getColor(subreddit));
        mSwipeRefreshLayout = v.findViewById(me.ccrama.redditslide.R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(me.ccrama.redditslide.Visuals.Palette.getColors(subreddit, getActivity()));
        mSwipeRefreshLayout.setOnRefreshListener(new android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener() {
            @java.lang.Override
            public void onRefresh() {
                if (comments != null) {
                    comments.loadMore(adapter, subreddit, true);
                } else {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                // TODO catch errors
            }
        });
        toolbar.setTitle(subreddit);
        toolbar.setNavigationIcon(me.ccrama.redditslide.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                getActivity().onBackPressed();
            }
        });
        toolbar.inflateMenu(me.ccrama.redditslide.R.menu.menu_comment_items);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                ((android.support.v7.widget.LinearLayoutManager) (rv.getLayoutManager())).scrollToPositionWithOffset(1, headerHeight);
                resetScroll(false);
            }
        });
        addClickFunctionSubName(toolbar);
        doTopBar();
        if (me.ccrama.redditslide.Authentication.didOnline && (!me.ccrama.redditslide.util.NetworkUtil.isConnectedNoOverride(getActivity()))) {
            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(getActivity()).setTitle(me.ccrama.redditslide.R.string.err_title).setMessage(me.ccrama.redditslide.R.string.err_connection_failed_msg).setNegativeButton(me.ccrama.redditslide.R.string.btn_close, new android.content.DialogInterface.OnClickListener() {
                @java.lang.Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    if (!(getActivity() instanceof me.ccrama.redditslide.Activities.MainActivity)) {
                        getActivity().finish();
                    }
                }
            }).setPositiveButton(me.ccrama.redditslide.R.string.btn_offline, new android.content.DialogInterface.OnClickListener() {
                @java.lang.Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    me.ccrama.redditslide.Reddit.appRestart.edit().putBoolean("forceoffline", true).commit();
                    me.ccrama.redditslide.Reddit.forceRestart(getActivity(), false);
                }
            }).show();
        }
        if ((!(getActivity() instanceof me.ccrama.redditslide.Activities.CommentsScreen)) || (((me.ccrama.redditslide.Activities.CommentsScreen) (getActivity())).currentPage == page)) {
            doAdapter(true);
        } else {
            doAdapter(false);
        }
        return v;
    }

    public boolean onMenuItemClick(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case me.ccrama.redditslide.R.id.search :
                {
                    if ((comments.comments != null) && (comments.submission != null)) {
                        me.ccrama.redditslide.DataShare.sharedComments = comments.comments;
                        me.ccrama.redditslide.DataShare.subAuthor = comments.submission.getAuthor();
                        android.content.Intent i = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.CommentSearch.class);
                        if (getActivity() instanceof me.ccrama.redditslide.Activities.MainActivity) {
                            getActivity().startActivityForResult(i, 423);
                        } else {
                            startActivityForResult(i, 423);
                        }
                    }
                }
                return true;
            case me.ccrama.redditslide.R.id.sidebar :
                doSidebarOpen();
                return true;
            case me.ccrama.redditslide.R.id.related :
                if (adapter.submission.isSelfPost()) {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(getActivity()).setTitle("Selftext posts have no related submissions").setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, null).show();
                } else {
                    android.content.Intent i = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.Related.class);
                    i.putExtra(me.ccrama.redditslide.Activities.Related.EXTRA_URL, adapter.submission.getUrl());
                    startActivity(i);
                }
                return true;
            case me.ccrama.redditslide.R.id.shadowbox :
                if (me.ccrama.redditslide.SettingValues.isPro) {
                    if ((comments.comments != null) && (comments.submission != null)) {
                        me.ccrama.redditslide.Activities.ShadowboxComments.comments = new java.util.ArrayList<>();
                        for (me.ccrama.redditslide.Adapters.CommentObject c : comments.comments) {
                            if (c instanceof me.ccrama.redditslide.Adapters.CommentItem) {
                                if (c.comment.getComment().getDataNode().get("body_html").asText().contains("&lt;/a")) {
                                    java.lang.String body = c.comment.getComment().getDataNode().get("body_html").asText();
                                    java.lang.String url;
                                    java.lang.String[] split = body.split("&lt;a href=\"");
                                    if (split.length > 1) {
                                        for (java.lang.String chunk : split) {
                                            url = chunk.substring(0, chunk.indexOf("\"", 1));
                                            me.ccrama.redditslide.ContentType.Type t = me.ccrama.redditslide.ContentType.getContentType(url);
                                            if (me.ccrama.redditslide.ContentType.mediaType(t)) {
                                                me.ccrama.redditslide.Activities.ShadowboxComments.comments.add(new me.ccrama.redditslide.Adapters.CommentUrlObject(c.comment, url, subreddit));
                                            }
                                        }
                                    } else {
                                        int start = body.indexOf("&lt;a href=\"");
                                        url = body.substring(start, body.indexOf("\"", start + 1));
                                        me.ccrama.redditslide.ContentType.Type t = me.ccrama.redditslide.ContentType.getContentType(url);
                                        if (me.ccrama.redditslide.ContentType.mediaType(t)) {
                                            me.ccrama.redditslide.Activities.ShadowboxComments.comments.add(new me.ccrama.redditslide.Adapters.CommentUrlObject(c.comment, url, subreddit));
                                        }
                                    }
                                }
                            }
                        }
                        if (!me.ccrama.redditslide.Activities.ShadowboxComments.comments.isEmpty()) {
                            android.content.Intent i = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.ShadowboxComments.class);
                            startActivity(i);
                        } else {
                            android.support.design.widget.Snackbar.make(mSwipeRefreshLayout, me.ccrama.redditslide.R.string.shadowbox_comments_nolinks, android.support.design.widget.Snackbar.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    com.afollestad.materialdialogs.AlertDialogWrapper.Builder b = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(getContext()).setTitle(me.ccrama.redditslide.R.string.general_shadowbox_comments_ispro).setMessage(me.ccrama.redditslide.R.string.pro_upgrade_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes_exclaim, new android.content.DialogInterface.OnClickListener() {
                        public void onClick(android.content.DialogInterface dialog, int whichButton) {
                            try {
                                startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=" + getString(me.ccrama.redditslide.R.string.ui_unlock_package))));
                            } catch (android.content.ActivityNotFoundException e) {
                                startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("http://play.google.com/store/apps/details?id=" + getString(me.ccrama.redditslide.R.string.ui_unlock_package))));
                            }
                        }
                    }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no_danks, new android.content.DialogInterface.OnClickListener() {
                        public void onClick(android.content.DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    });
                    b.show();
                }
                return true;
            case me.ccrama.redditslide.R.id.sort :
                {
                    openPopup(toolbar);
                    return true;
                }
            case me.ccrama.redditslide.R.id.content :
                {
                    if ((adapter != null) && (adapter.submission != null)) {
                        if (!me.ccrama.redditslide.PostMatch.openExternal(adapter.submission.getUrl())) {
                            me.ccrama.redditslide.ContentType.Type type = me.ccrama.redditslide.ContentType.getContentType(adapter.submission);
                            switch (type) {
                                case VID_ME :
                                case STREAMABLE :
                                    if (me.ccrama.redditslide.SettingValues.video) {
                                        android.content.Intent myIntent = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.MediaView.class);
                                        myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.SUBREDDIT, subreddit);
                                        myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_URL, adapter.submission.getUrl());
                                        getActivity().startActivity(myIntent);
                                    } else {
                                        me.ccrama.redditslide.util.LinkUtil.openExternally(adapter.submission.getUrl());
                                    }
                                    break;
                                case IMGUR :
                                case XKCD :
                                    android.content.Intent i2 = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.MediaView.class);
                                    i2.putExtra(me.ccrama.redditslide.Activities.MediaView.SUBREDDIT, subreddit);
                                    if ((adapter.submission.getDataNode().has("preview") && adapter.submission.getDataNode().get("preview").get("images").get(0).get("source").has("height")) && (type != me.ccrama.redditslide.ContentType.Type.XKCD)) {
                                        // Load the preview image which has probably already been cached in memory instead of the direct link
                                        java.lang.String previewUrl = adapter.submission.getDataNode().get("preview").get("images").get(0).get("source").get("url").asText();
                                        i2.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_DISPLAY_URL, previewUrl);
                                    }
                                    i2.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_URL, adapter.submission.getUrl());
                                    getActivity().startActivity(i2);
                                    break;
                                case EMBEDDED :
                                    if (me.ccrama.redditslide.SettingValues.video) {
                                        java.lang.String data = adapter.submission.getDataNode().get("media_embed").get("content").asText();
                                        {
                                            android.content.Intent i = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.FullscreenVideo.class);
                                            i.putExtra(me.ccrama.redditslide.Activities.FullscreenVideo.EXTRA_HTML, data);
                                            getActivity().startActivity(i);
                                        }
                                    } else {
                                        me.ccrama.redditslide.util.LinkUtil.openExternally(adapter.submission.getUrl());
                                    }
                                    break;
                                case REDDIT :
                                    me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.openRedditContent(adapter.submission.getUrl(), getActivity());
                                    break;
                                case LINK :
                                    me.ccrama.redditslide.util.LinkUtil.openUrl(adapter.submission.getUrl(), me.ccrama.redditslide.Visuals.Palette.getColor(adapter.submission.getSubredditName()), getActivity());
                                    break;
                                case NONE :
                                case SELF :
                                    if (adapter.submission.getSelftext().isEmpty()) {
                                        android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(rv, me.ccrama.redditslide.R.string.submission_nocontent, android.support.design.widget.Snackbar.LENGTH_SHORT);
                                        android.view.View view = s.getView();
                                        android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                                        tv.setTextColor(android.graphics.Color.WHITE);
                                        s.show();
                                    } else {
                                        android.view.LayoutInflater inflater = getActivity().getLayoutInflater();
                                        final android.view.View dialoglayout = inflater.inflate(me.ccrama.redditslide.R.layout.parent_comment_dialog, null);
                                        final com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(getActivity());
                                        adapter.setViews(adapter.submission.getDataNode().get("selftext_html").asText(), adapter.submission.getSubredditName(), ((me.ccrama.redditslide.SpoilerRobotoTextView) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.firstTextView))), ((me.ccrama.redditslide.Views.CommentOverflow) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.commentOverflow))));
                                        builder.setView(dialoglayout);
                                        builder.show();
                                    }
                                    break;
                                case ALBUM :
                                    if (me.ccrama.redditslide.SettingValues.album) {
                                        if (me.ccrama.redditslide.SettingValues.albumSwipe) {
                                            android.content.Intent i = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.AlbumPager.class);
                                            i.putExtra(me.ccrama.redditslide.Activities.Album.EXTRA_URL, adapter.submission.getUrl());
                                            i.putExtra(me.ccrama.redditslide.Activities.AlbumPager.SUBREDDIT, subreddit);
                                            getActivity().startActivity(i);
                                            getActivity().overridePendingTransition(me.ccrama.redditslide.R.anim.slideright, me.ccrama.redditslide.R.anim.fade_out);
                                        } else {
                                            android.content.Intent i = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.Album.class);
                                            i.putExtra(me.ccrama.redditslide.Activities.Album.EXTRA_URL, adapter.submission.getUrl());
                                            i.putExtra(me.ccrama.redditslide.Activities.Album.SUBREDDIT, subreddit);
                                            getActivity().startActivity(i);
                                            getActivity().overridePendingTransition(me.ccrama.redditslide.R.anim.slideright, me.ccrama.redditslide.R.anim.fade_out);
                                        }
                                    } else {
                                        me.ccrama.redditslide.util.LinkUtil.openExternally(adapter.submission.getUrl());
                                    }
                                    break;
                                case TUMBLR :
                                    if (me.ccrama.redditslide.SettingValues.image) {
                                        if (me.ccrama.redditslide.SettingValues.albumSwipe) {
                                            android.content.Intent i = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.TumblrPager.class);
                                            i.putExtra(me.ccrama.redditslide.Activities.Album.EXTRA_URL, adapter.submission.getUrl());
                                            i.putExtra(me.ccrama.redditslide.Activities.TumblrPager.SUBREDDIT, subreddit);
                                            getActivity().startActivity(i);
                                            getActivity().overridePendingTransition(me.ccrama.redditslide.R.anim.slideright, me.ccrama.redditslide.R.anim.fade_out);
                                        } else {
                                            android.content.Intent i = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.Tumblr.class);
                                            i.putExtra(me.ccrama.redditslide.Activities.Tumblr.SUBREDDIT, subreddit);
                                            i.putExtra(me.ccrama.redditslide.Activities.Album.EXTRA_URL, adapter.submission.getUrl());
                                            getActivity().startActivity(i);
                                            getActivity().overridePendingTransition(me.ccrama.redditslide.R.anim.slideright, me.ccrama.redditslide.R.anim.fade_out);
                                        }
                                    } else {
                                        me.ccrama.redditslide.util.LinkUtil.openExternally(adapter.submission.getUrl());
                                    }
                                    break;
                                case IMAGE :
                                    me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.openImage(type, getActivity(), adapter.submission, null, -1);
                                    break;
                                case VREDDIT_REDIRECT :
                                case VREDDIT_DIRECT :
                                case GIF :
                                    me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder.openGif(getActivity(), adapter.submission, -1);
                                    break;
                                case VIDEO :
                                    if (!me.ccrama.redditslide.util.LinkUtil.tryOpenWithVideoPlugin(adapter.submission.getUrl())) {
                                        me.ccrama.redditslide.util.LinkUtil.openUrl(adapter.submission.getUrl(), me.ccrama.redditslide.Visuals.Palette.getStatusBarColor(), getActivity());
                                    }
                            }
                        } else {
                            me.ccrama.redditslide.util.LinkUtil.openExternally(adapter.submission.getUrl());
                        }
                    }
                }
                return true;
            case me.ccrama.redditslide.R.id.reload :
                if (comments != null) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    comments.loadMore(adapter, subreddit);
                }
                return true;
            case me.ccrama.redditslide.R.id.collapse :
                {
                    if (adapter != null) {
                        adapter.collapseAll();
                    }
                }
                return true;
            case android.R.id.home :
                getActivity().onBackPressed();
                return true;
        }
        return false;
    }

    private void doSidebarOpen() {
        new me.ccrama.redditslide.Fragments.CommentPage.AsyncGetSubreddit().execute(subreddit);
    }

    private class AsyncGetSubreddit extends android.os.AsyncTask<java.lang.String, java.lang.Void, net.dean.jraw.models.Subreddit> {
        @java.lang.Override
        public void onPostExecute(final net.dean.jraw.models.Subreddit baseSub) {
            try {
                d.dismiss();
            } catch (java.lang.Exception e) {
            }
            if (baseSub != null) {
                currentlySubbed = me.ccrama.redditslide.Authentication.isLoggedIn && baseSub.isUserSubscriber();
                subreddit = baseSub.getDisplayName();
                try {
                    android.view.View sidebar = getActivity().getLayoutInflater().inflate(me.ccrama.redditslide.R.layout.subinfo, null);
                    {
                        sidebar.findViewById(me.ccrama.redditslide.R.id.loader).setVisibility(android.view.View.GONE);
                        sidebar.findViewById(me.ccrama.redditslide.R.id.sidebar_text).setVisibility(android.view.View.GONE);
                        sidebar.findViewById(me.ccrama.redditslide.R.id.sub_title).setVisibility(android.view.View.GONE);
                        sidebar.findViewById(me.ccrama.redditslide.R.id.subscribers).setVisibility(android.view.View.GONE);
                        sidebar.findViewById(me.ccrama.redditslide.R.id.active_users).setVisibility(android.view.View.GONE);
                        sidebar.findViewById(me.ccrama.redditslide.R.id.header_sub).setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getColor(subreddit));
                        ((android.widget.TextView) (sidebar.findViewById(me.ccrama.redditslide.R.id.sub_infotitle))).setText(subreddit);
                        // Sidebar buttons should use subreddit's accent color
                        int subColor = new me.ccrama.redditslide.ColorPreferences(getContext()).getColor(subreddit);
                        ((android.widget.TextView) (sidebar.findViewById(me.ccrama.redditslide.R.id.theme_text))).setTextColor(subColor);
                        ((android.widget.TextView) (sidebar.findViewById(me.ccrama.redditslide.R.id.wiki_text))).setTextColor(subColor);
                        ((android.widget.TextView) (sidebar.findViewById(me.ccrama.redditslide.R.id.post_text))).setTextColor(subColor);
                        ((android.widget.TextView) (sidebar.findViewById(me.ccrama.redditslide.R.id.mods_text))).setTextColor(subColor);
                        ((android.widget.TextView) (sidebar.findViewById(me.ccrama.redditslide.R.id.flair_text))).setTextColor(subColor);
                    }
                    {
                        sidebar.findViewById(me.ccrama.redditslide.R.id.loader).setVisibility(android.view.View.VISIBLE);
                        loaded = true;
                        final android.view.View dialoglayout = sidebar;
                        {
                            android.view.View submit = dialoglayout.findViewById(me.ccrama.redditslide.R.id.submit);
                            if ((!me.ccrama.redditslide.Authentication.isLoggedIn) || (!me.ccrama.redditslide.Authentication.didOnline)) {
                                submit.setVisibility(android.view.View.GONE);
                            }
                            if (me.ccrama.redditslide.SettingValues.fab && (me.ccrama.redditslide.SettingValues.fabType == me.ccrama.redditslide.Constants.FAB_POST)) {
                                submit.setVisibility(android.view.View.GONE);
                            }
                            submit.setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                                @java.lang.Override
                                public void onSingleClick(android.view.View view) {
                                    android.content.Intent inte = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.Submit.class);
                                    inte.putExtra(me.ccrama.redditslide.Activities.Submit.EXTRA_SUBREDDIT, subreddit);
                                    getActivity().startActivity(inte);
                                }
                            });
                        }
                        dialoglayout.findViewById(me.ccrama.redditslide.R.id.wiki).setOnClickListener(new android.view.View.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.view.View v) {
                                android.content.Intent i = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.Wiki.class);
                                i.putExtra(me.ccrama.redditslide.Activities.Wiki.EXTRA_SUBREDDIT, subreddit);
                                startActivity(i);
                            }
                        });
                        dialoglayout.findViewById(me.ccrama.redditslide.R.id.submit).setOnClickListener(new android.view.View.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.view.View v) {
                                android.content.Intent i = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.Submit.class);
                                i.putExtra(me.ccrama.redditslide.Activities.Submit.EXTRA_SUBREDDIT, subreddit);
                                startActivity(i);
                            }
                        });
                        dialoglayout.findViewById(me.ccrama.redditslide.R.id.syncflair).setOnClickListener(new android.view.View.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.view.View v) {
                                me.ccrama.redditslide.ImageFlairs.syncFlairs(getContext(), subreddit);
                            }
                        });
                        dialoglayout.findViewById(me.ccrama.redditslide.R.id.theme).setOnClickListener(new android.view.View.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.view.View v) {
                                int style = new me.ccrama.redditslide.ColorPreferences(getActivity()).getThemeSubreddit(subreddit);
                                final android.content.Context contextThemeWrapper = new android.support.v7.view.ContextThemeWrapper(getActivity(), style);
                                android.view.LayoutInflater localInflater = getActivity().getLayoutInflater().cloneInContext(contextThemeWrapper);
                                final android.view.View dialoglayout = localInflater.inflate(me.ccrama.redditslide.R.layout.colorsub, null);
                                java.util.ArrayList<java.lang.String> arrayList = new java.util.ArrayList<>();
                                arrayList.add(subreddit);
                                me.ccrama.redditslide.Adapters.SettingsSubAdapter.showSubThemeEditor(arrayList, getActivity(), dialoglayout);
                            }
                        });
                        dialoglayout.findViewById(me.ccrama.redditslide.R.id.mods).setOnClickListener(new android.view.View.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.view.View v) {
                                final android.app.Dialog d = new com.afollestad.materialdialogs.MaterialDialog.Builder(getActivity()).title(me.ccrama.redditslide.R.string.sidebar_findingmods).cancelable(true).content(me.ccrama.redditslide.R.string.misc_please_wait).progress(true, 100).show();
                                new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
                                    java.util.ArrayList<net.dean.jraw.models.UserRecord> mods;

                                    @java.lang.Override
                                    protected java.lang.Void doInBackground(java.lang.Void... params) {
                                        mods = new java.util.ArrayList<>();
                                        net.dean.jraw.paginators.UserRecordPaginator paginator = new net.dean.jraw.paginators.UserRecordPaginator(me.ccrama.redditslide.Authentication.reddit, subreddit, "moderators");
                                        paginator.setSorting(net.dean.jraw.paginators.Sorting.HOT);
                                        paginator.setTimePeriod(net.dean.jraw.paginators.TimePeriod.ALL);
                                        while (paginator.hasNext()) {
                                            mods.addAll(paginator.next());
                                        } 
                                        return null;
                                    }

                                    @java.lang.Override
                                    protected void onPostExecute(java.lang.Void aVoid) {
                                        final java.util.ArrayList<java.lang.String> names = new java.util.ArrayList<>();
                                        for (net.dean.jraw.models.UserRecord rec : mods) {
                                            names.add(rec.getFullName());
                                        }
                                        d.dismiss();
                                        new com.afollestad.materialdialogs.MaterialDialog.Builder(getActivity()).title(getString(me.ccrama.redditslide.R.string.sidebar_submods, subreddit)).items(names).itemsCallback(new com.afollestad.materialdialogs.MaterialDialog.ListCallback() {
                                            @java.lang.Override
                                            public void onSelection(com.afollestad.materialdialogs.MaterialDialog dialog, android.view.View itemView, int which, java.lang.CharSequence text) {
                                                android.content.Intent i = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.Profile.class);
                                                i.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_PROFILE, names.get(which));
                                                startActivity(i);
                                            }
                                        }).positiveText(me.ccrama.redditslide.R.string.btn_message).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                                            @java.lang.Override
                                            public void onClick(@android.support.annotation.NonNull
                                            com.afollestad.materialdialogs.MaterialDialog dialog, @android.support.annotation.NonNull
                                            com.afollestad.materialdialogs.DialogAction which) {
                                                android.content.Intent i = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.SendMessage.class);
                                                i.putExtra(me.ccrama.redditslide.Activities.SendMessage.EXTRA_NAME, "/r/" + subreddit);
                                                startActivity(i);
                                            }
                                        }).show();
                                    }
                                }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        });
                        dialoglayout.findViewById(me.ccrama.redditslide.R.id.flair).setVisibility(android.view.View.GONE);
                    }
                    {
                        sidebar.findViewById(me.ccrama.redditslide.R.id.loader).setVisibility(android.view.View.GONE);
                        if ((baseSub.getSidebar() != null) && (!baseSub.getSidebar().isEmpty())) {
                            sidebar.findViewById(me.ccrama.redditslide.R.id.sidebar_text).setVisibility(android.view.View.VISIBLE);
                            final java.lang.String text = baseSub.getDataNode().get("description_html").asText();
                            final me.ccrama.redditslide.SpoilerRobotoTextView body = sidebar.findViewById(me.ccrama.redditslide.R.id.sidebar_text);
                            me.ccrama.redditslide.Views.CommentOverflow overflow = sidebar.findViewById(me.ccrama.redditslide.R.id.commentOverflow);
                            setViews(text, baseSub.getDisplayName(), body, overflow);
                        } else {
                            sidebar.findViewById(me.ccrama.redditslide.R.id.sidebar_text).setVisibility(android.view.View.GONE);
                        }
                        android.view.View collection = sidebar.findViewById(me.ccrama.redditslide.R.id.collection);
                        if (me.ccrama.redditslide.Authentication.isLoggedIn) {
                            collection.setOnClickListener(new android.view.View.OnClickListener() {
                                @java.lang.Override
                                public void onClick(android.view.View v) {
                                    new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
                                        java.util.HashMap<java.lang.String, net.dean.jraw.models.MultiReddit> multis = new java.util.HashMap<java.lang.String, net.dean.jraw.models.MultiReddit>();

                                        @java.lang.Override
                                        protected java.lang.Void doInBackground(java.lang.Void... params) {
                                            if (me.ccrama.redditslide.UserSubscriptions.multireddits == null) {
                                                me.ccrama.redditslide.UserSubscriptions.syncMultiReddits(getContext());
                                            }
                                            for (net.dean.jraw.models.MultiReddit r : me.ccrama.redditslide.UserSubscriptions.multireddits) {
                                                multis.put(r.getDisplayName(), r);
                                            }
                                            return null;
                                        }

                                        @java.lang.Override
                                        protected void onPostExecute(java.lang.Void aVoid) {
                                            new com.afollestad.materialdialogs.MaterialDialog.Builder(getContext()).title(("Add /r/" + baseSub.getDisplayName()) + " to").items(multis.keySet()).itemsCallback(new com.afollestad.materialdialogs.MaterialDialog.ListCallback() {
                                                @java.lang.Override
                                                public void onSelection(com.afollestad.materialdialogs.MaterialDialog dialog, android.view.View itemView, final int which, java.lang.CharSequence text) {
                                                    new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
                                                        @java.lang.Override
                                                        protected java.lang.Void doInBackground(java.lang.Void... params) {
                                                            try {
                                                                final java.lang.String multiName = multis.keySet().toArray(new java.lang.String[multis.size()])[which];
                                                                java.util.List<java.lang.String> subs = new java.util.ArrayList<java.lang.String>();
                                                                for (net.dean.jraw.models.MultiSubreddit sub : multis.get(multiName).getSubreddits()) {
                                                                    subs.add(sub.getDisplayName());
                                                                }
                                                                subs.add(baseSub.getDisplayName());
                                                                new net.dean.jraw.managers.MultiRedditManager(me.ccrama.redditslide.Authentication.reddit).createOrUpdate(new net.dean.jraw.http.MultiRedditUpdateRequest.Builder(me.ccrama.redditslide.Authentication.name, multiName).subreddits(subs).build());
                                                                me.ccrama.redditslide.UserSubscriptions.syncMultiReddits(getContext());
                                                                getActivity().runOnUiThread(new java.lang.Runnable() {
                                                                    @java.lang.Override
                                                                    public void run() {
                                                                        android.support.design.widget.Snackbar.make(toolbar, getString(me.ccrama.redditslide.R.string.multi_subreddit_added, multiName), android.support.design.widget.Snackbar.LENGTH_LONG).show();
                                                                    }
                                                                });
                                                            } catch (net.dean.jraw.http.NetworkException | net.dean.jraw.ApiException e) {
                                                                getActivity().runOnUiThread(new java.lang.Runnable() {
                                                                    @java.lang.Override
                                                                    public void run() {
                                                                        getActivity().runOnUiThread(new java.lang.Runnable() {
                                                                            @java.lang.Override
                                                                            public void run() {
                                                                                android.support.design.widget.Snackbar.make(toolbar, getString(me.ccrama.redditslide.R.string.multi_error), android.support.design.widget.Snackbar.LENGTH_LONG).setAction(me.ccrama.redditslide.R.string.btn_ok, new android.view.View.OnClickListener() {
                                                                                    @java.lang.Override
                                                                                    public void onClick(android.view.View v) {
                                                                                    }
                                                                                }).show();
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                                e.printStackTrace();
                                                            }
                                                            return null;
                                                        }
                                                    }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                                                }
                                            }).show();
                                        }
                                    }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                                }
                            });
                        } else {
                            collection.setVisibility(android.view.View.GONE);
                        }
                        {
                            final android.widget.TextView subscribe = sidebar.findViewById(me.ccrama.redditslide.R.id.subscribe);
                            currentlySubbed = ((!me.ccrama.redditslide.Authentication.isLoggedIn) && me.ccrama.redditslide.UserSubscriptions.getSubscriptions(getActivity()).contains(baseSub.getDisplayName().toLowerCase(java.util.Locale.ENGLISH))) || (me.ccrama.redditslide.Authentication.isLoggedIn && baseSub.isUserSubscriber());
                            doSubscribeButtonText(currentlySubbed, subscribe);
                            subscribe.setOnClickListener(new android.view.View.OnClickListener() {
                                private void doSubscribe() {
                                    if (me.ccrama.redditslide.Authentication.isLoggedIn) {
                                        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(getActivity()).setTitle(getString(me.ccrama.redditslide.R.string.subscribe_to, baseSub.getDisplayName())).setPositiveButton(me.ccrama.redditslide.R.string.reorder_add_subscribe, new android.content.DialogInterface.OnClickListener() {
                                            @java.lang.Override
                                            public void onClick(android.content.DialogInterface dialog, int which) {
                                                new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
                                                    @java.lang.Override
                                                    public void onPostExecute(java.lang.Boolean success) {
                                                        if (!success) {
                                                            // If subreddit was removed from account or not
                                                            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(getActivity()).setTitle(me.ccrama.redditslide.R.string.force_change_subscription).setMessage(me.ccrama.redditslide.R.string.force_change_subscription_desc).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                                                                @java.lang.Override
                                                                public void onClick(android.content.DialogInterface dialog, int which) {
                                                                    changeSubscription(baseSub, true);// Force add the subscription

                                                                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(toolbar, getString(me.ccrama.redditslide.R.string.misc_subscribed), android.support.design.widget.Snackbar.LENGTH_SHORT);
                                                                    android.view.View view = s.getView();
                                                                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                                                                    tv.setTextColor(android.graphics.Color.WHITE);
                                                                    s.show();
                                                                }
                                                            }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, new android.content.DialogInterface.OnClickListener() {
                                                                @java.lang.Override
                                                                public void onClick(android.content.DialogInterface dialog, int which) {
                                                                }
                                                            }).setCancelable(false).show();
                                                        } else {
                                                            changeSubscription(baseSub, true);
                                                        }
                                                    }

                                                    @java.lang.Override
                                                    protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                                                        try {
                                                            new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).subscribe(baseSub);
                                                        } catch (net.dean.jraw.http.NetworkException e) {
                                                            return false;// Either network crashed or trying to unsubscribe to a subreddit that the account isn't subscribed to

                                                        }
                                                        return true;
                                                    }
                                                }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                                            }
                                        }).setNegativeButton(me.ccrama.redditslide.R.string.btn_cancel, null).setNeutralButton(me.ccrama.redditslide.R.string.btn_add_to_sublist, new android.content.DialogInterface.OnClickListener() {
                                            @java.lang.Override
                                            public void onClick(android.content.DialogInterface dialog, int which) {
                                                changeSubscription(baseSub, true);// Force add the subscription

                                                android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(toolbar, me.ccrama.redditslide.R.string.sub_added, android.support.design.widget.Snackbar.LENGTH_SHORT);
                                                android.view.View view = s.getView();
                                                android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                                                tv.setTextColor(android.graphics.Color.WHITE);
                                                s.show();
                                            }
                                        }).show();
                                    } else {
                                        changeSubscription(baseSub, true);
                                    }
                                }

                                @java.lang.Override
                                public void onClick(android.view.View v) {
                                    if (!currentlySubbed) {
                                        doSubscribe();
                                        doSubscribeButtonText(currentlySubbed, subscribe);
                                    } else {
                                        doUnsubscribe();
                                        doSubscribeButtonText(currentlySubbed, subscribe);
                                    }
                                }

                                private void doUnsubscribe() {
                                    if (me.ccrama.redditslide.Authentication.didOnline) {
                                        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(getContext()).setTitle(getString(me.ccrama.redditslide.R.string.unsubscribe_from, baseSub.getDisplayName())).setPositiveButton(me.ccrama.redditslide.R.string.reorder_remove_unsubsribe, new android.content.DialogInterface.OnClickListener() {
                                            @java.lang.Override
                                            public void onClick(android.content.DialogInterface dialog, int which) {
                                                new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
                                                    @java.lang.Override
                                                    public void onPostExecute(java.lang.Boolean success) {
                                                        if (!success) {
                                                            // If subreddit was removed from account or not
                                                            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(getContext()).setTitle(me.ccrama.redditslide.R.string.force_change_subscription).setMessage(me.ccrama.redditslide.R.string.force_change_subscription_desc).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                                                                @java.lang.Override
                                                                public void onClick(android.content.DialogInterface dialog, int which) {
                                                                    changeSubscription(baseSub, false);// Force add the subscription

                                                                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(toolbar, getString(me.ccrama.redditslide.R.string.misc_unsubscribed), android.support.design.widget.Snackbar.LENGTH_SHORT);
                                                                    android.view.View view = s.getView();
                                                                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                                                                    tv.setTextColor(android.graphics.Color.WHITE);
                                                                    s.show();
                                                                }
                                                            }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, new android.content.DialogInterface.OnClickListener() {
                                                                @java.lang.Override
                                                                public void onClick(android.content.DialogInterface dialog, int which) {
                                                                }
                                                            }).setCancelable(false).show();
                                                        } else {
                                                            changeSubscription(baseSub, false);
                                                        }
                                                    }

                                                    @java.lang.Override
                                                    protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                                                        try {
                                                            new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).unsubscribe(baseSub);
                                                        } catch (net.dean.jraw.http.NetworkException e) {
                                                            return false;// Either network crashed or trying to unsubscribe to a subreddit that the account isn't subscribed to

                                                        }
                                                        return true;
                                                    }
                                                }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                                            }
                                        }).setNeutralButton(me.ccrama.redditslide.R.string.just_unsub, new android.content.DialogInterface.OnClickListener() {
                                            @java.lang.Override
                                            public void onClick(android.content.DialogInterface dialog, int which) {
                                                changeSubscription(baseSub, false);// Force add the subscription

                                                android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(toolbar, me.ccrama.redditslide.R.string.misc_unsubscribed, android.support.design.widget.Snackbar.LENGTH_SHORT);
                                                android.view.View view = s.getView();
                                                android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                                                tv.setTextColor(android.graphics.Color.WHITE);
                                                s.show();
                                            }
                                        }).setNegativeButton(me.ccrama.redditslide.R.string.btn_cancel, null).show();
                                    } else {
                                        changeSubscription(baseSub, false);
                                    }
                                }
                            });
                        }
                        if (!baseSub.getPublicDescription().isEmpty()) {
                            sidebar.findViewById(me.ccrama.redditslide.R.id.sub_title).setVisibility(android.view.View.VISIBLE);
                            setViews(baseSub.getDataNode().get("public_description_html").asText(), baseSub.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), ((me.ccrama.redditslide.SpoilerRobotoTextView) (sidebar.findViewById(me.ccrama.redditslide.R.id.sub_title))), ((me.ccrama.redditslide.Views.CommentOverflow) (sidebar.findViewById(me.ccrama.redditslide.R.id.sub_title_overflow))));
                        } else {
                            sidebar.findViewById(me.ccrama.redditslide.R.id.sub_title).setVisibility(android.view.View.GONE);
                        }
                        if (baseSub.getDataNode().has("icon_img") && (!baseSub.getDataNode().get("icon_img").asText().isEmpty())) {
                            ((me.ccrama.redditslide.Reddit) (getContext().getApplicationContext())).getImageLoader().displayImage(baseSub.getDataNode().get("icon_img").asText(), ((android.widget.ImageView) (sidebar.findViewById(me.ccrama.redditslide.R.id.subimage))));
                        } else {
                            sidebar.findViewById(me.ccrama.redditslide.R.id.subimage).setVisibility(android.view.View.GONE);
                        }
                        ((android.widget.TextView) (sidebar.findViewById(me.ccrama.redditslide.R.id.subscribers))).setText(getString(me.ccrama.redditslide.R.string.subreddit_subscribers_string, baseSub.getLocalizedSubscriberCount()));
                        sidebar.findViewById(me.ccrama.redditslide.R.id.subscribers).setVisibility(android.view.View.VISIBLE);
                        ((android.widget.TextView) (sidebar.findViewById(me.ccrama.redditslide.R.id.active_users))).setText(getString(me.ccrama.redditslide.R.string.subreddit_active_users_string_new, baseSub.getLocalizedAccountsActive()));
                        sidebar.findViewById(me.ccrama.redditslide.R.id.active_users).setVisibility(android.view.View.VISIBLE);
                    }
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(getContext()).setPositiveButton(me.ccrama.redditslide.R.string.btn_close, null).setView(sidebar).show();
                } catch (java.lang.NullPointerException e) {
                    // activity has been killed
                }
            }
        }

        @java.lang.Override
        protected net.dean.jraw.models.Subreddit doInBackground(final java.lang.String... params) {
            try {
                return me.ccrama.redditslide.Authentication.reddit.getSubreddit(params[0]);
            } catch (java.lang.Exception e) {
                try {
                    d.dismiss();
                } catch (java.lang.Exception ignored) {
                }
                return null;
            }
        }

        android.app.Dialog d;

        @java.lang.Override
        protected void onPreExecute() {
            d = new com.afollestad.materialdialogs.MaterialDialog.Builder(getActivity()).title(me.ccrama.redditslide.R.string.subreddit_sidebar_progress).progress(true, 100).content(me.ccrama.redditslide.R.string.misc_please_wait).cancelable(false).show();
        }
    }

    public net.dean.jraw.models.CommentSort commentSorting;

    private void addClickFunctionSubName(android.support.v7.widget.Toolbar toolbar) {
        android.widget.TextView titleTv = null;
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            android.view.View view = toolbar.getChildAt(i);
            java.lang.CharSequence text = null;
            if ((view instanceof android.widget.TextView) && ((text = ((android.widget.TextView) (view)).getText()) != null)) {
                titleTv = ((android.widget.TextView) (view));
            }
        }
        if (titleTv != null) {
            final java.lang.String text = titleTv.getText().toString();
            titleTv.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    android.content.Intent i = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.SubredditView.class);
                    i.putExtra(me.ccrama.redditslide.Activities.SubredditView.EXTRA_SUBREDDIT, text);
                    startActivity(i);
                }
            });
        }
    }

    public void doAdapter(boolean load) {
        commentSorting = me.ccrama.redditslide.SettingValues.getCommentSorting(subreddit);
        if (load)
            doRefresh(true);

        if (load)
            loaded = true;

        if ((((((!single) && (getActivity() instanceof me.ccrama.redditslide.Activities.CommentsScreen)) && (((me.ccrama.redditslide.Activities.CommentsScreen) (getActivity())).subredditPosts != null)) && me.ccrama.redditslide.Authentication.didOnline) && (((me.ccrama.redditslide.Activities.CommentsScreen) (getActivity())).currentPosts != null)) && (((me.ccrama.redditslide.Activities.CommentsScreen) (getActivity())).currentPosts.size() > page)) {
            try {
                comments = new me.ccrama.redditslide.Adapters.SubmissionComments(fullname, this, mSwipeRefreshLayout);
            } catch (java.lang.IndexOutOfBoundsException e) {
                return;
            }
            net.dean.jraw.models.Submission s = ((me.ccrama.redditslide.Activities.CommentsScreen) (getActivity())).currentPosts.get(page);
            if (((s != null) && s.getDataNode().has("suggested_sort")) && (!s.getDataNode().get("suggested_sort").asText().equalsIgnoreCase("null"))) {
                java.lang.String sorting = s.getDataNode().get("suggested_sort").asText().toUpperCase();
                sorting = sorting.replace("İ", "I");
                commentSorting = net.dean.jraw.models.CommentSort.valueOf(sorting);
            } else if (s != null) {
                commentSorting = me.ccrama.redditslide.SettingValues.getCommentSorting(s.getSubredditName());
            }
            if (load)
                comments.setSorting(commentSorting);

            if (adapter == null) {
                adapter = new me.ccrama.redditslide.Adapters.CommentAdapter(this, comments, rv, s, getFragmentManager());
                rv.setAdapter(adapter);
            }
        } else if (getActivity() instanceof me.ccrama.redditslide.Activities.MainActivity) {
            if (me.ccrama.redditslide.Authentication.didOnline) {
                comments = new me.ccrama.redditslide.Adapters.SubmissionComments(fullname, this, mSwipeRefreshLayout);
                net.dean.jraw.models.Submission s = ((me.ccrama.redditslide.Activities.MainActivity) (getActivity())).openingComments;
                if (((s != null) && s.getDataNode().has("suggested_sort")) && (!s.getDataNode().get("suggested_sort").asText().equalsIgnoreCase("null"))) {
                    java.lang.String sorting = s.getDataNode().get("suggested_sort").asText().toUpperCase();
                    sorting = sorting.replace("İ", "I");
                    commentSorting = net.dean.jraw.models.CommentSort.valueOf(sorting);
                } else if (s != null) {
                    commentSorting = me.ccrama.redditslide.SettingValues.getCommentSorting(s.getSubredditName());
                }
                if (load)
                    comments.setSorting(commentSorting);

                if (adapter == null) {
                    adapter = new me.ccrama.redditslide.Adapters.CommentAdapter(this, comments, rv, s, getFragmentManager());
                    rv.setAdapter(adapter);
                }
            } else {
                net.dean.jraw.models.Submission s = ((me.ccrama.redditslide.Activities.MainActivity) (getActivity())).openingComments;
                doRefresh(false);
                comments = new me.ccrama.redditslide.Adapters.SubmissionComments(fullname, this, mSwipeRefreshLayout, s);
                if (adapter == null) {
                    adapter = new me.ccrama.redditslide.Adapters.CommentAdapter(this, comments, rv, s, getFragmentManager());
                    rv.setAdapter(adapter);
                }
            }
        } else {
            net.dean.jraw.models.Submission s = null;
            try {
                s = me.ccrama.redditslide.OfflineSubreddit.getSubmissionFromStorage(fullname.contains("_") ? fullname : "t3_" + fullname, getContext(), !me.ccrama.redditslide.util.NetworkUtil.isConnected(getActivity()), new com.fasterxml.jackson.databind.ObjectMapper().reader());
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            if ((s != null) && (s.getComments() != null)) {
                doRefresh(false);
                comments = new me.ccrama.redditslide.Adapters.SubmissionComments(fullname, this, mSwipeRefreshLayout, s);
                if (adapter == null) {
                    adapter = new me.ccrama.redditslide.Adapters.CommentAdapter(this, comments, rv, s, getFragmentManager());
                    rv.setAdapter(adapter);
                }
            } else if (context.isEmpty()) {
                comments = new me.ccrama.redditslide.Adapters.SubmissionComments(fullname, this, mSwipeRefreshLayout);
                comments.setSorting(commentSorting);
                if (adapter == null) {
                    if (s != null) {
                        adapter = new me.ccrama.redditslide.Adapters.CommentAdapter(this, comments, rv, s, getFragmentManager());
                    }
                    rv.setAdapter(adapter);
                }
            } else if (context.equals(me.ccrama.redditslide.Reddit.EMPTY_STRING)) {
                comments = new me.ccrama.redditslide.Adapters.SubmissionComments(fullname, this, mSwipeRefreshLayout);
                if (load)
                    comments.setSorting(commentSorting);

            } else {
                comments = new me.ccrama.redditslide.Adapters.SubmissionComments(fullname, this, mSwipeRefreshLayout, context, contextNumber);
                if (load)
                    comments.setSorting(commentSorting);

            }
        }
    }

    public void doData(java.lang.Boolean b) {
        if ((adapter == null) || single) {
            adapter = new me.ccrama.redditslide.Adapters.CommentAdapter(this, comments, rv, comments.submission, getFragmentManager());
            rv.setAdapter(adapter);
            adapter.currentSelectedItem = context;
            if (context.isEmpty()) {
                if (me.ccrama.redditslide.SettingValues.collapseCommentsDefault) {
                    adapter.collapseAll();
                }
            }
            adapter.reset(getContext(), comments, rv, comments.submission, b);
        } else if (!b) {
            try {
                adapter.reset(getContext(), comments, rv, getActivity() instanceof me.ccrama.redditslide.Activities.MainActivity ? ((me.ccrama.redditslide.Activities.MainActivity) (getActivity())).openingComments : comments.submission, b);
                if (me.ccrama.redditslide.SettingValues.collapseCommentsDefault) {
                    adapter.collapseAll();
                }
            } catch (java.lang.Exception ignored) {
            }
        } else {
            adapter.reset(getContext(), comments, rv, comments.submission, b);
            if (me.ccrama.redditslide.SettingValues.collapseCommentsDefault) {
                adapter.collapseAll();
            }
            adapter.notifyItemChanged(1);
        }
    }

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.os.Bundle bundle = this.getArguments();
        subreddit = bundle.getString("subreddit", "");
        fullname = bundle.getString("id", "");
        page = bundle.getInt("page", 0);
        single = bundle.getBoolean("single", false);
        context = bundle.getString("context", "");
        contextNumber = bundle.getInt("contextNumber", 5);
        np = bundle.getBoolean("np", false);
        archived = bundle.getBoolean("archived", false);
        locked = bundle.getBoolean("locked", false);
        contest = bundle.getBoolean("contest", false);
        loadMore = (!context.isEmpty()) && (!context.equals(me.ccrama.redditslide.Reddit.EMPTY_STRING));
        if (!single)
            loadMore = false;

        int subredditStyle = new me.ccrama.redditslide.ColorPreferences(getActivity()).getThemeSubreddit(subreddit);
        contextThemeWrapper = new android.support.v7.view.ContextThemeWrapper(getActivity(), subredditStyle);
        mLayoutManager = new me.ccrama.redditslide.Views.PreCachingLayoutManagerComments(getActivity());
    }

    @java.lang.Override
    public void onDestroy() {
        super.onDestroy();
        if (comments != null)
            comments.cancelLoad();

        if ((adapter != null) && (adapter.currentComments != null)) {
            if ((adapter.currentlyEditing != null) && (!adapter.currentlyEditing.getText().toString().isEmpty())) {
                me.ccrama.redditslide.Drafts.addDraft(adapter.currentlyEditing.getText().toString());
                android.widget.Toast.makeText(getActivity().getApplicationContext(), me.ccrama.redditslide.R.string.msg_save_draft, android.widget.Toast.LENGTH_LONG).show();
            }
        }
    }

    public int getCurrentSort() {
        switch (currentSort) {
            case PARENTS :
                return 0;
            case CHILDREN :
                return 1;
            case TIME :
                return 3;
            case GILDED :
                return 6;
            case OP :
                return 3;
            case YOU :
                return 5;
            case LINK :
                return 4;
        }
        return 0;
    }

    public void resetScroll(boolean override) {
        if (toolbarScroll == null) {
            toolbarScroll = new me.ccrama.redditslide.handler.ToolbarScrollHideHandler(toolbar, v.findViewById(me.ccrama.redditslide.R.id.header), v.findViewById(me.ccrama.redditslide.R.id.progress), me.ccrama.redditslide.SettingValues.commentAutoHide ? v.findViewById(me.ccrama.redditslide.R.id.commentnav) : null) {
                @java.lang.Override
                public void onScrolled(android.support.v7.widget.RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (me.ccrama.redditslide.SettingValues.fabComments) {
                        if ((recyclerView.getScrollState() == android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING) && (!overrideFab)) {
                            diff += dy;
                        } else if (!overrideFab) {
                            diff = 0;
                        }
                        if ((fab != null) && (!overrideFab)) {
                            if ((dy <= 0) && (fab.getId() != 0)) {
                                if ((recyclerView.getScrollState() != android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING) || (diff < ((-fab.getHeight()) * 2))) {
                                    fab.show();
                                }
                            } else {
                                fab.hide();
                            }
                        }
                    }
                }
            };
            rv.addOnScrollListener(toolbarScroll);
        } else {
            toolbarScroll.reset = true;
        }
    }

    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        // This is the filter
        if (event.getAction() != android.view.KeyEvent.ACTION_DOWN)
            return true;

        if (keyCode == android.view.KeyEvent.KEYCODE_VOLUME_DOWN) {
            goDown();
            return true;
        } else if (keyCode == android.view.KeyEvent.KEYCODE_VOLUME_UP) {
            goUp();
            return true;
        } else if (keyCode == android.view.KeyEvent.KEYCODE_SEARCH) {
            return onMenuItemClick(toolbar.getMenu().findItem(me.ccrama.redditslide.R.id.search));
        }
        return false;
    }

    private void reloadSubs() {
        mSwipeRefreshLayout.setRefreshing(true);
        comments.setSorting(commentSorting);
        rv.scrollToPosition(0);
    }

    private void openPopup(android.view.View view) {
        if ((comments.comments != null) && (!comments.comments.isEmpty())) {
            final android.content.DialogInterface.OnClickListener l2 = new android.content.DialogInterface.OnClickListener() {
                @java.lang.Override
                public void onClick(android.content.DialogInterface dialogInterface, int i) {
                    switch (i) {
                        case 0 :
                            commentSorting = net.dean.jraw.models.CommentSort.CONFIDENCE;
                            break;
                        case 1 :
                            commentSorting = net.dean.jraw.models.CommentSort.TOP;
                            break;
                        case 2 :
                            commentSorting = net.dean.jraw.models.CommentSort.NEW;
                            break;
                        case 3 :
                            commentSorting = net.dean.jraw.models.CommentSort.CONTROVERSIAL;
                            break;
                        case 4 :
                            commentSorting = net.dean.jraw.models.CommentSort.OLD;
                            break;
                        case 5 :
                            commentSorting = net.dean.jraw.models.CommentSort.QA;
                            break;
                    }
                }
            };
            final int i = (commentSorting == net.dean.jraw.models.CommentSort.CONFIDENCE) ? 0 : commentSorting == net.dean.jraw.models.CommentSort.TOP ? 1 : commentSorting == net.dean.jraw.models.CommentSort.NEW ? 2 : commentSorting == net.dean.jraw.models.CommentSort.CONTROVERSIAL ? 3 : commentSorting == net.dean.jraw.models.CommentSort.OLD ? 4 : commentSorting == net.dean.jraw.models.CommentSort.QA ? 5 : 0;
            com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(getActivity());
            builder.setTitle(me.ccrama.redditslide.R.string.sorting_choose);
            android.content.res.Resources res = getActivity().getBaseContext().getResources();
            builder.setSingleChoiceItems(new java.lang.String[]{ res.getString(me.ccrama.redditslide.R.string.sorting_best), res.getString(me.ccrama.redditslide.R.string.sorting_top), res.getString(me.ccrama.redditslide.R.string.sorting_new), res.getString(me.ccrama.redditslide.R.string.sorting_controversial), res.getString(me.ccrama.redditslide.R.string.sorting_old), res.getString(me.ccrama.redditslide.R.string.sorting_ama) }, i, l2);
            builder.alwaysCallSingleChoiceCallback();
            builder.setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                @java.lang.Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    reloadSubs();
                }
            }).setNeutralButton(getString(me.ccrama.redditslide.R.string.sorting_defaultfor, subreddit), new android.content.DialogInterface.OnClickListener() {
                @java.lang.Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    me.ccrama.redditslide.SettingValues.setDefaultCommentSorting(commentSorting, subreddit);
                    reloadSubs();
                }
            });
            builder.show();
        }
    }

    public void doGoUp(int old) {
        int depth = -1;
        if (adapter.currentlySelected != null) {
            depth = adapter.currentNode.getDepth();
        }
        int pos = (old < 2) ? 0 : old - 1;
        for (int i = pos - 1; i >= 0; i--) {
            try {
                me.ccrama.redditslide.Adapters.CommentObject o = adapter.currentComments.get(adapter.getRealPosition(i));
                if ((o instanceof me.ccrama.redditslide.Adapters.CommentItem) && ((pos - 1) != i)) {
                    boolean matches = false;
                    switch (currentSort) {
                        case PARENTS :
                            matches = o.comment.isTopLevel();
                            break;
                        case CHILDREN :
                            if (depth == (-1)) {
                                matches = o.comment.isTopLevel();
                            } else {
                                matches = o.comment.getDepth() == depth;
                                if (matches) {
                                    adapter.currentNode = o.comment;
                                    adapter.currentSelectedItem = o.comment.getComment().getFullName();
                                }
                            }
                            break;
                        case TIME :
                            matches = (o.comment.getComment() != null) && (o.comment.getComment().getCreated().getTime() > sortTime);
                            break;
                        case GILDED :
                            matches = ((o.comment.getComment().getTimesGilded() > 0) || (o.comment.getComment().getTimesSilvered() > 0)) || (o.comment.getComment().getTimesPlatinized() > 0);
                            break;
                        case OP :
                            matches = (adapter.submission != null) && o.comment.getComment().getAuthor().equals(adapter.submission.getAuthor());
                            break;
                        case YOU :
                            matches = (adapter.submission != null) && o.comment.getComment().getAuthor().equals(me.ccrama.redditslide.Authentication.name);
                            break;
                        case LINK :
                            matches = o.comment.getComment().getDataNode().get("body_html").asText().contains("&lt;/a");
                            break;
                    }
                    if (matches) {
                        if ((i + 2) == old) {
                            doGoUp(old - 1);
                        } else {
                            ((me.ccrama.redditslide.Views.PreCachingLayoutManagerComments) (rv.getLayoutManager())).scrollToPositionWithOffset(i + 2, ((android.view.View) (toolbar.getParent())).getTranslationY() != 0 ? 0 : v.findViewById(me.ccrama.redditslide.R.id.header).getHeight());
                        }
                        break;
                    }
                }
            } catch (java.lang.Exception ignored) {
            }
        }
    }

    private void goUp() {
        int toGoto = mLayoutManager.findFirstVisibleItemPosition();
        if (((adapter != null) && (adapter.currentComments != null)) && (!adapter.currentComments.isEmpty())) {
            if ((adapter.currentlyEditing != null) && (!adapter.currentlyEditing.getText().toString().isEmpty())) {
                final int finalToGoto = toGoto;
                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(getActivity()).setTitle(me.ccrama.redditslide.R.string.discard_comment_title).setMessage(me.ccrama.redditslide.R.string.comment_discard_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        adapter.currentlyEditing = null;
                        doGoUp(finalToGoto);
                    }
                }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, null).show();
            } else {
                doGoUp(toGoto);
            }
        }
    }

    public void doGoDown(int old) {
        int depth = -1;
        if (adapter.currentlySelected != null) {
            depth = adapter.currentNode.getDepth();
        }
        int pos = old - 2;
        if (pos < 0)
            pos = 0;

        java.lang.String original = adapter.currentComments.get(adapter.getRealPosition(pos)).getName();
        if (old < 2) {
            ((me.ccrama.redditslide.Views.PreCachingLayoutManagerComments) (rv.getLayoutManager())).scrollToPositionWithOffset(2, ((android.view.View) (toolbar.getParent())).getTranslationY() != 0 ? 0 : v.findViewById(me.ccrama.redditslide.R.id.header).getHeight());
        } else {
            for (int i = pos + 1; i < adapter.currentComments.size(); i++) {
                try {
                    me.ccrama.redditslide.Adapters.CommentObject o = adapter.currentComments.get(adapter.getRealPosition(i));
                    if (o instanceof me.ccrama.redditslide.Adapters.CommentItem) {
                        boolean matches = false;
                        switch (currentSort) {
                            case PARENTS :
                                matches = o.comment.isTopLevel();
                                break;
                            case CHILDREN :
                                if (depth == (-1)) {
                                    matches = o.comment.isTopLevel();
                                } else {
                                    matches = o.comment.getDepth() == depth;
                                    if (matches) {
                                        adapter.currentNode = o.comment;
                                        adapter.currentSelectedItem = o.comment.getComment().getFullName();
                                    }
                                }
                                break;
                            case TIME :
                                matches = o.comment.getComment().getCreated().getTime() > sortTime;
                                break;
                            case GILDED :
                                matches = ((o.comment.getComment().getTimesGilded() > 0) || (o.comment.getComment().getTimesSilvered() > 0)) || (o.comment.getComment().getTimesPlatinized() > 0);
                                break;
                            case OP :
                                matches = (adapter.submission != null) && o.comment.getComment().getAuthor().equals(adapter.submission.getAuthor());
                                break;
                            case YOU :
                                matches = (adapter.submission != null) && o.comment.getComment().getAuthor().equals(me.ccrama.redditslide.Authentication.name);
                                break;
                            case LINK :
                                matches = o.comment.getComment().getDataNode().get("body_html").asText().contains("&lt;/a");
                                break;
                        }
                        if (matches) {
                            if (o.getName().equals(original)) {
                                doGoDown(i + 2);
                            } else {
                                ((me.ccrama.redditslide.Views.PreCachingLayoutManagerComments) (rv.getLayoutManager())).scrollToPositionWithOffset(i + 2, ((android.view.View) (toolbar.getParent())).getTranslationY() != 0 ? 0 : v.findViewById(me.ccrama.redditslide.R.id.header).getHeight());
                            }
                            break;
                        }
                    }
                } catch (java.lang.Exception ignored) {
                }
            }
        }
    }

    private void goDown() {
        ((android.view.View) (toolbar.getParent())).setTranslationY(-((android.view.View) (toolbar.getParent())).getHeight());
        int toGoto = mLayoutManager.findFirstVisibleItemPosition();
        if (((adapter != null) && (adapter.currentComments != null)) && (!adapter.currentComments.isEmpty())) {
            if ((adapter.currentlyEditing != null) && (!adapter.currentlyEditing.getText().toString().isEmpty())) {
                final int finalToGoto = toGoto;
                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(getActivity()).setTitle(me.ccrama.redditslide.R.string.discard_comment_title).setMessage(me.ccrama.redditslide.R.string.comment_discard_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        adapter.currentlyEditing = null;
                        doGoDown(finalToGoto);
                    }
                }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, null).show();
            } else {
                doGoDown(toGoto);
            }
        }
    }

    private void changeSubscription(net.dean.jraw.models.Subreddit subreddit, boolean isChecked) {
        me.ccrama.redditslide.UserSubscriptions.addSubreddit(subreddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), getContext());
        android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(toolbar, isChecked ? getString(me.ccrama.redditslide.R.string.misc_subscribed) : getString(me.ccrama.redditslide.R.string.misc_unsubscribed), android.support.design.widget.Snackbar.LENGTH_SHORT);
        android.view.View view = s.getView();
        android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(android.graphics.Color.WHITE);
        s.show();
    }

    private void setViews(java.lang.String rawHTML, java.lang.String subreddit, me.ccrama.redditslide.SpoilerRobotoTextView firstTextView, me.ccrama.redditslide.Views.CommentOverflow commentOverflow) {
        if (rawHTML.isEmpty()) {
            return;
        }
        java.util.List<java.lang.String> blocks = me.ccrama.redditslide.util.SubmissionParser.getBlocks(rawHTML);
        int startIndex = 0;
        // the <div class="md"> case is when the body contains a table or code block first
        if (!blocks.get(0).equals("<div class=\"md\">")) {
            firstTextView.setVisibility(android.view.View.VISIBLE);
            firstTextView.setTextHtml(blocks.get(0), subreddit);
            startIndex = 1;
        } else {
            firstTextView.setText("");
            firstTextView.setVisibility(android.view.View.GONE);
        }
        if (blocks.size() > 1) {
            if (startIndex == 0) {
                commentOverflow.setViews(blocks, subreddit);
            } else {
                commentOverflow.setViews(blocks.subList(startIndex, blocks.size()), subreddit);
            }
        } else {
            commentOverflow.removeAllViews();
        }
    }

    private void doSubscribeButtonText(boolean currentlySubbed, android.widget.TextView subscribe) {
        if (me.ccrama.redditslide.Authentication.didOnline) {
            if (currentlySubbed) {
                subscribe.setText(me.ccrama.redditslide.R.string.unsubscribe_caps);
            } else {
                subscribe.setText(me.ccrama.redditslide.R.string.subscribe_caps);
            }
        } else if (currentlySubbed) {
            subscribe.setText(me.ccrama.redditslide.R.string.btn_remove_from_sublist);
        } else {
            subscribe.setText(me.ccrama.redditslide.R.string.btn_add_to_sublist);
        }
    }

    me.ccrama.redditslide.Adapters.CommentNavType currentSort = me.ccrama.redditslide.Adapters.CommentNavType.PARENTS;

    long sortTime = 0;
}