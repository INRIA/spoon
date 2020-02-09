/**
 * Created by ccrama on 3/22/2015.
 */
package me.ccrama.redditslide.Adapters;
import me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager;
import java.util.Locale;
import me.ccrama.redditslide.Activities.CommentsScreen;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder;
import me.ccrama.redditslide.Views.CreateCardView;
import java.util.ArrayList;
import me.ccrama.redditslide.Authentication;
import java.util.List;
import me.ccrama.redditslide.Fragments.MultiredditView;
import me.ccrama.redditslide.ActionStates;
public class MultiredditAdapter extends android.support.v7.widget.RecyclerView.Adapter<android.support.v7.widget.RecyclerView.ViewHolder> implements me.ccrama.redditslide.Adapters.BaseAdapter {
    private final android.support.v7.widget.RecyclerView listView;

    public android.app.Activity context;

    public me.ccrama.redditslide.Adapters.MultiredditPosts dataSet;

    public java.util.List<net.dean.jraw.models.Submission> seen;

    private final int LOADING_SPINNER = 5;

    private final int NO_MORE = 3;

    private final int SPACER = 6;

    android.support.v4.widget.SwipeRefreshLayout refreshLayout;

    me.ccrama.redditslide.Fragments.MultiredditView baseView;

    public MultiredditAdapter(android.app.Activity context, me.ccrama.redditslide.Adapters.MultiredditPosts dataSet, android.support.v7.widget.RecyclerView listView, android.support.v4.widget.SwipeRefreshLayout refreshLayout, me.ccrama.redditslide.Fragments.MultiredditView baseView) {
        this.listView = listView;
        this.dataSet = dataSet;
        this.context = context;
        this.seen = new java.util.ArrayList<>();
        this.refreshLayout = refreshLayout;
        this.baseView = baseView;
    }

    @java.lang.Override
    public void setError(java.lang.Boolean b) {
        listView.setAdapter(new me.ccrama.redditslide.Adapters.ErrorAdapter());
    }

    @java.lang.Override
    public void undoSetError() {
        listView.setAdapter(this);
    }

    @java.lang.Override
    public int getItemViewType(int position) {
        if ((position <= 0) && (!dataSet.posts.isEmpty())) {
            return SPACER;
        } else if (!dataSet.posts.isEmpty()) {
            position -= 1;
        }
        if (((position == dataSet.posts.size()) && (!dataSet.posts.isEmpty())) && (!dataSet.nomore)) {
            return LOADING_SPINNER;
        } else if ((position == dataSet.posts.size()) && dataSet.nomore) {
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
            return new me.ccrama.redditslide.Adapters.MultiredditAdapter.SpacerViewHolder(v);
        } else if (i == LOADING_SPINNER) {
            android.view.View v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.loadingmore, viewGroup, false);
            return new me.ccrama.redditslide.Adapters.MultiredditAdapter.SubmissionFooterViewHolder(v);
        } else if (i == NO_MORE) {
            android.view.View v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.nomoreposts, viewGroup, false);
            return new me.ccrama.redditslide.Adapters.MultiredditAdapter.SubmissionFooterViewHolder(v);
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
            me.ccrama.redditslide.Views.CreateCardView.colorCard(submission.getSubredditName().toLowerCase(java.util.Locale.ENGLISH), holder.itemView, "multi" + dataSet.multiReddit.getDisplayName(), true);
            holder.itemView.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View arg0) {
                    if (me.ccrama.redditslide.Authentication.didOnline || (submission.getComments() != null)) {
                        holder.title.setAlpha(0.65F);
                        holder.leadImage.setAlpha(0.65F);
                        holder.thumbimage.setAlpha(0.65F);
                        android.content.Intent i2 = new android.content.Intent(context, me.ccrama.redditslide.Activities.CommentsScreen.class);
                        i2.putExtra(me.ccrama.redditslide.Activities.CommentsScreen.EXTRA_PAGE, holder2.getAdapterPosition() - 1);
                        i2.putExtra(me.ccrama.redditslide.Activities.CommentsScreen.EXTRA_MULTIREDDIT, dataSet.multiReddit.getDisplayName());
                        context.startActivityForResult(i2, 940);
                        i2.putExtra("fullname", submission.getFullName());
                        clicked = holder2.getAdapterPosition();
                    } else {
                        android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.offline_comments_not_loaded, android.support.design.widget.Snackbar.LENGTH_SHORT);
                        android.view.View view = s.getView();
                        android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(android.graphics.Color.WHITE);
                        s.show();
                    }
                }
            });
            final boolean saved = submission.isSaved();
            new me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder().populateSubmissionViewHolder(holder, submission, context, false, false, dataSet.posts, listView, true, false, "multi" + dataSet.multiReddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), null);
        }
        if (holder2 instanceof me.ccrama.redditslide.Adapters.MultiredditAdapter.SubmissionFooterViewHolder) {
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
                        dataSet.loadMore(context, baseView, true, me.ccrama.redditslide.Adapters.MultiredditAdapter.this);
                    }
                });
            }
        }
        if (holder2 instanceof me.ccrama.redditslide.Adapters.MultiredditAdapter.SpacerViewHolder) {
            final int height = context.findViewById(me.ccrama.redditslide.R.id.header).getHeight();
            holder2.itemView.findViewById(me.ccrama.redditslide.R.id.height).setLayoutParams(new android.widget.LinearLayout.LayoutParams(holder2.itemView.getWidth(), height));
            if (listView.getLayoutManager() instanceof me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager) {
                android.support.v7.widget.StaggeredGridLayoutManager.LayoutParams layoutParams = new android.support.v7.widget.StaggeredGridLayoutManager.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, height);
                layoutParams.setFullSpan(true);
                holder2.itemView.setLayoutParams(layoutParams);
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

    static void fixSliding(int position) {
        try {
            me.ccrama.redditslide.Reddit.lastPosition.add(position, 0);
        } catch (java.lang.IndexOutOfBoundsException e) {
            me.ccrama.redditslide.Adapters.MultiredditAdapter.fixSliding(position - 1);
        }
    }
}