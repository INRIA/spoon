/**
 * Created by ccrama on 3/22/2015.
 */
package me.ccrama.redditslide.Adapters;
import me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager;
import java.util.Locale;
import me.ccrama.redditslide.Activities.CommentsScreen;
import me.ccrama.redditslide.util.OnSingleClickListener;
import java.util.ArrayList;
import me.ccrama.redditslide.Activities.MainActivity;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Activities.SubredditView;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder;
import me.ccrama.redditslide.Views.CreateCardView;
import me.ccrama.redditslide.Authentication;
import java.util.List;
import me.ccrama.redditslide.Fragments.SubmissionsView;
import me.ccrama.redditslide.ActionStates;
public class SubmissionAdapter extends android.support.v7.widget.RecyclerView.Adapter<android.support.v7.widget.RecyclerView.ViewHolder> implements me.ccrama.redditslide.Adapters.BaseAdapter {
    private final android.support.v7.widget.RecyclerView listView;

    public final java.lang.String subreddit;

    public android.app.Activity context;

    private final boolean custom;

    public me.ccrama.redditslide.Adapters.SubredditPosts dataSet;

    public java.util.List<net.dean.jraw.models.Submission> seen;

    private final int LOADING_SPINNER = 5;

    private final int NO_MORE = 3;

    private final int SPACER = 6;

    me.ccrama.redditslide.Adapters.SubmissionDisplay displayer;

    public SubmissionAdapter(android.app.Activity context, me.ccrama.redditslide.Adapters.SubredditPosts dataSet, android.support.v7.widget.RecyclerView listView, java.lang.String subreddit, me.ccrama.redditslide.Adapters.SubmissionDisplay displayer) {
        this.subreddit = subreddit.toLowerCase(java.util.Locale.ENGLISH);
        this.listView = listView;
        this.dataSet = dataSet;
        this.context = context;
        this.seen = new java.util.ArrayList<>();
        custom = me.ccrama.redditslide.SettingValues.prefs.contains(me.ccrama.redditslide.Reddit.PREF_LAYOUT + subreddit.toLowerCase(java.util.Locale.ENGLISH));
        this.displayer = displayer;
        me.ccrama.redditslide.Activities.MainActivity.randomoverride = "";
    }

    @java.lang.Override
    public void setError(java.lang.Boolean b) {
        listView.setAdapter(new me.ccrama.redditslide.Adapters.ErrorAdapter());
        isError = true;
        listView.setLayoutManager(me.ccrama.redditslide.Fragments.SubmissionsView.createLayoutManager(me.ccrama.redditslide.Fragments.SubmissionsView.getNumColumns(context.getResources().getConfiguration().orientation, context)));
    }

    public boolean isError;

    @java.lang.Override
    public long getItemId(int position) {
        if ((position <= 0) && (!dataSet.posts.isEmpty())) {
            return SPACER;
        } else if (!dataSet.posts.isEmpty()) {
            position -= 1;
        }
        if ((((position == dataSet.posts.size()) && (!dataSet.posts.isEmpty())) && (!dataSet.offline)) && (!dataSet.nomore)) {
            return LOADING_SPINNER;
        } else if ((position == dataSet.posts.size()) && (dataSet.offline || dataSet.nomore)) {
            return NO_MORE;
        }
        return dataSet.posts.get(position).getCreated().getTime();
    }

    @java.lang.Override
    public void undoSetError() {
        listView.setAdapter(this);
        isError = false;
        listView.setLayoutManager(me.ccrama.redditslide.Fragments.SubmissionsView.createLayoutManager(me.ccrama.redditslide.Fragments.SubmissionsView.getNumColumns(context.getResources().getConfiguration().orientation, context)));
    }

    @java.lang.Override
    public int getItemViewType(int position) {
        if ((position <= 0) && (!dataSet.posts.isEmpty())) {
            return SPACER;
        } else if (!dataSet.posts.isEmpty()) {
            position -= 1;
        }
        if ((((position == dataSet.posts.size()) && (!dataSet.posts.isEmpty())) && (!dataSet.offline)) && (!dataSet.nomore)) {
            return LOADING_SPINNER;
        } else if ((position == dataSet.posts.size()) && (dataSet.offline || dataSet.nomore)) {
            return NO_MORE;
        }
        int SUBMISSION = 1;
        return SUBMISSION;
    }

    int tag = 1;

    @java.lang.Override
    public android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup viewGroup, int i) {
        tag++;
        if (i == SPACER) {
            android.view.View v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.spacer, viewGroup, false);
            return new me.ccrama.redditslide.Adapters.SubmissionAdapter.SpacerViewHolder(v);
        } else if (i == LOADING_SPINNER) {
            android.view.View v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.loadingmore, viewGroup, false);
            return new me.ccrama.redditslide.Adapters.SubmissionAdapter.SubmissionFooterViewHolder(v);
        } else if (i == NO_MORE) {
            android.view.View v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.nomoreposts, viewGroup, false);
            return new me.ccrama.redditslide.Adapters.SubmissionAdapter.SubmissionFooterViewHolder(v);
        } else {
            android.view.View v = me.ccrama.redditslide.Views.CreateCardView.CreateView(viewGroup);
            return new me.ccrama.redditslide.Adapters.SubmissionViewHolder(v);
        }
    }

    int clicked;

    public void refreshView() {
        final android.support.v7.widget.RecyclerView.ItemAnimator a = listView.getItemAnimator();
        listView.setItemAnimator(null);
        notifyItemChanged(clicked);
        listView.postDelayed(new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                listView.setItemAnimator(a);
            }
        }, 500);
    }

    public void refreshView(boolean ignore18) {
        final android.support.v7.widget.RecyclerView.ItemAnimator a = listView.getItemAnimator();
        listView.setItemAnimator(null);
        notifyItemChanged(clicked);
        listView.postDelayed(new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                listView.setItemAnimator(a);
            }
        }, 500);
    }

    public void refreshView(java.util.ArrayList<java.lang.Integer> seen) {
        listView.setItemAnimator(null);
        final android.support.v7.widget.RecyclerView.ItemAnimator a = listView.getItemAnimator();
        for (int i : seen) {
            notifyItemChanged(i + 1);
        }
        listView.postDelayed(new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                listView.setItemAnimator(a);
            }
        }, 500);
    }

    @java.lang.Override
    public void onBindViewHolder(final android.support.v7.widget.RecyclerView.ViewHolder holder2, final int pos) {
        int i = (pos != 0) ? pos - 1 : pos;
        if (holder2 instanceof me.ccrama.redditslide.Adapters.SubmissionViewHolder) {
            final me.ccrama.redditslide.Adapters.SubmissionViewHolder holder = ((me.ccrama.redditslide.Adapters.SubmissionViewHolder) (holder2));
            final net.dean.jraw.models.Submission submission = dataSet.posts.get(i);
            me.ccrama.redditslide.Views.CreateCardView.colorCard(submission.getSubredditName().toLowerCase(java.util.Locale.ENGLISH), holder.itemView, subreddit, ((((subreddit.equals("frontpage") || subreddit.equals("mod")) || subreddit.equals("friends")) || subreddit.equals("all")) || subreddit.contains(".")) || subreddit.contains("+"));
            holder.itemView.setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View v) {
                    if (me.ccrama.redditslide.Authentication.didOnline || (submission.getComments() != null)) {
                        holder.title.setAlpha(0.54F);
                        holder.body.setAlpha(0.54F);
                        if (context instanceof me.ccrama.redditslide.Activities.MainActivity) {
                            final me.ccrama.redditslide.Activities.MainActivity a = ((me.ccrama.redditslide.Activities.MainActivity) (context));
                            if ((a.singleMode && a.commentPager) && (a.adapter instanceof me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapterComment)) {
                                if (a.openingComments != submission) {
                                    clicked = holder2.getAdapterPosition();
                                    a.openingComments = submission;
                                    a.toOpenComments = a.pager.getCurrentItem() + 1;
                                    a.currentComment = holder.getAdapterPosition() - 1;
                                    ((me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapterComment) (a.adapter)).storedFragment = a.adapter.getCurrentFragment();
                                    ((me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapterComment) (a.adapter)).size = a.toOpenComments + 1;
                                    try {
                                        a.adapter.notifyDataSetChanged();
                                    } catch (java.lang.Exception ignored) {
                                    }
                                }
                                a.pager.postDelayed(new java.lang.Runnable() {
                                    @java.lang.Override
                                    public void run() {
                                        a.pager.setCurrentItem(a.pager.getCurrentItem() + 1, true);
                                    }
                                }, 400);
                            } else {
                                android.content.Intent i2 = new android.content.Intent(context, me.ccrama.redditslide.Activities.CommentsScreen.class);
                                i2.putExtra(me.ccrama.redditslide.Activities.CommentsScreen.EXTRA_PAGE, holder2.getAdapterPosition() - 1);
                                i2.putExtra(me.ccrama.redditslide.Activities.CommentsScreen.EXTRA_SUBREDDIT, subreddit);
                                i2.putExtra("fullname", submission.getFullName());
                                context.startActivityForResult(i2, 940);
                                clicked = holder2.getAdapterPosition();
                            }
                        } else if (context instanceof me.ccrama.redditslide.Activities.SubredditView) {
                            final me.ccrama.redditslide.Activities.SubredditView a = ((me.ccrama.redditslide.Activities.SubredditView) (context));
                            if (a.singleMode && a.commentPager) {
                                if (a.openingComments != submission) {
                                    clicked = holder2.getAdapterPosition();
                                    a.openingComments = submission;
                                    a.currentComment = holder.getAdapterPosition() - 1;
                                    ((me.ccrama.redditslide.Activities.SubredditView.OverviewPagerAdapterComment) (a.adapter)).storedFragment = a.adapter.getCurrentFragment();
                                    ((me.ccrama.redditslide.Activities.SubredditView.OverviewPagerAdapterComment) (a.adapter)).size = 3;
                                    a.adapter.notifyDataSetChanged();
                                }
                                a.pager.postDelayed(new java.lang.Runnable() {
                                    @java.lang.Override
                                    public void run() {
                                        a.pager.setCurrentItem(a.pager.getCurrentItem() + 1, true);
                                    }
                                }, 400);
                            } else {
                                android.content.Intent i2 = new android.content.Intent(context, me.ccrama.redditslide.Activities.CommentsScreen.class);
                                i2.putExtra(me.ccrama.redditslide.Activities.CommentsScreen.EXTRA_PAGE, holder2.getAdapterPosition() - 1);
                                i2.putExtra(me.ccrama.redditslide.Activities.CommentsScreen.EXTRA_SUBREDDIT, subreddit);
                                i2.putExtra("fullname", submission.getFullName());
                                context.startActivityForResult(i2, 940);
                                clicked = holder2.getAdapterPosition();
                            }
                        }
                    } else if (!me.ccrama.redditslide.Reddit.appRestart.contains("offlinepopup")) {
                        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(context).setTitle(me.ccrama.redditslide.R.string.cache_no_comments_found).setMessage(me.ccrama.redditslide.R.string.cache_no_comments_found_message).setCancelable(false).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                me.ccrama.redditslide.Reddit.appRestart.edit().putString("offlinepopup", "").apply();
                            }
                        }).show();
                    } else {
                        android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.cache_no_comments_found_snackbar, android.support.design.widget.Snackbar.LENGTH_SHORT);
                        s.setAction(me.ccrama.redditslide.R.string.misc_more_info, new android.view.View.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.view.View v) {
                                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(context).setTitle(me.ccrama.redditslide.R.string.cache_no_comments_found).setMessage(me.ccrama.redditslide.R.string.cache_no_comments_found_message).setCancelable(false).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                                    @java.lang.Override
                                    public void onClick(android.content.DialogInterface dialog, int which) {
                                        me.ccrama.redditslide.Reddit.appRestart.edit().putString("offlinepopup", "").apply();
                                    }
                                }).show();
                            }
                        });
                        android.view.View view = s.getView();
                        android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(android.graphics.Color.WHITE);
                        s.show();
                    }
                }
            });
            new me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder().populateSubmissionViewHolder(holder, submission, context, false, false, dataSet.posts, listView, custom, dataSet.offline, dataSet.subreddit.toLowerCase(java.util.Locale.ENGLISH), null);
        }
        if (holder2 instanceof me.ccrama.redditslide.Adapters.SubmissionAdapter.SubmissionFooterViewHolder) {
            android.os.Handler handler = new android.os.Handler();
            final java.lang.Runnable r = new java.lang.Runnable() {
                public void run() {
                    notifyItemChanged(dataSet.posts.size() + 1);// the loading spinner to replaced by nomoreposts.xml

                }
            };
            handler.post(r);
            if (holder2.itemView.findViewById(me.ccrama.redditslide.R.id.reload) != null) {
                holder2.itemView.findViewById(me.ccrama.redditslide.R.id.reload).setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        ((me.ccrama.redditslide.Fragments.SubmissionsView) (displayer)).forceRefresh();
                    }
                });
            }
        }
        if (holder2 instanceof me.ccrama.redditslide.Adapters.SubmissionAdapter.SpacerViewHolder) {
            android.view.View header = context.findViewById(me.ccrama.redditslide.R.id.header);
            int height = header.getHeight();
            if (height == 0) {
                header.measure(android.view.View.MeasureSpec.UNSPECIFIED, android.view.View.MeasureSpec.UNSPECIFIED);
                height = header.getMeasuredHeight();
                holder2.itemView.findViewById(me.ccrama.redditslide.R.id.height).setLayoutParams(new android.widget.LinearLayout.LayoutParams(holder2.itemView.getWidth(), height));
                if (listView.getLayoutManager() instanceof me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager) {
                    android.support.v7.widget.StaggeredGridLayoutManager.LayoutParams layoutParams = new android.support.v7.widget.StaggeredGridLayoutManager.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, height);
                    layoutParams.setFullSpan(true);
                    holder2.itemView.setLayoutParams(layoutParams);
                }
            } else {
                holder2.itemView.findViewById(me.ccrama.redditslide.R.id.height).setLayoutParams(new android.widget.LinearLayout.LayoutParams(holder2.itemView.getWidth(), height));
                if (listView.getLayoutManager() instanceof me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager) {
                    android.support.v7.widget.StaggeredGridLayoutManager.LayoutParams layoutParams = new android.support.v7.widget.StaggeredGridLayoutManager.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, height);
                    layoutParams.setFullSpan(true);
                    holder2.itemView.setLayoutParams(layoutParams);
                }
            }
        }
    }

    public class SubmissionFooterViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public SubmissionFooterViewHolder(android.view.View itemView) {
            super(itemView);
        }
    }

    public class SpacerViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public SpacerViewHolder(android.view.View itemView) {
            super(itemView);
        }
    }

    @java.lang.Override
    public int getItemCount() {
        if ((dataSet.posts == null) || dataSet.posts.isEmpty()) {
            return 0;
        } else {
            return dataSet.posts.size() + 2;// Always account for footer

        }
    }

    public class AsyncSave extends android.os.AsyncTask<net.dean.jraw.models.Submission, java.lang.Void, java.lang.Void> {
        android.view.View v;

        public AsyncSave(android.view.View v) {
            this.v = v;
        }

        @java.lang.Override
        protected java.lang.Void doInBackground(net.dean.jraw.models.Submission... submissions) {
            try {
                if (me.ccrama.redditslide.ActionStates.isSaved(submissions[0])) {
                    new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).unsave(submissions[0]);
                    final android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(v, me.ccrama.redditslide.R.string.submission_info_unsaved, android.support.design.widget.Snackbar.LENGTH_SHORT);
                    context.runOnUiThread(new java.lang.Runnable() {
                        @java.lang.Override
                        public void run() {
                            android.view.View view = s.getView();
                            android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                            tv.setTextColor(android.graphics.Color.WHITE);
                            s.show();
                        }
                    });
                    submissions[0].saved = false;
                    v = null;
                } else {
                    new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).save(submissions[0]);
                    final android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(v, me.ccrama.redditslide.R.string.submission_info_saved, android.support.design.widget.Snackbar.LENGTH_SHORT);
                    context.runOnUiThread(new java.lang.Runnable() {
                        @java.lang.Override
                        public void run() {
                            android.view.View view = s.getView();
                            android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                            tv.setTextColor(android.graphics.Color.WHITE);
                            s.show();
                        }
                    });
                    submissions[0].saved = true;
                    v = null;
                }
            } catch (java.lang.Exception e) {
                return null;
            }
            return null;
        }
    }

    public void performClick(int adapterPosition) {
        if (listView != null) {
            android.support.v7.widget.RecyclerView.ViewHolder holder = listView.findViewHolderForLayoutPosition(adapterPosition);
            if (holder != null) {
                android.view.View view = holder.itemView;
                if (view != null) {
                    view.performClick();
                }
            }
        }
    }
}