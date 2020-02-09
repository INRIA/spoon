/**
 * Created by ccrama on 3/22/2015.
 */
package me.ccrama.redditslide.Adapters;
import me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager;
import java.util.Locale;
import me.ccrama.redditslide.SpoilerRobotoTextView;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.util.OnSingleClickListener;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.util.SubmissionParser;
import me.ccrama.redditslide.Views.CommentOverflow;
import java.util.List;
import me.ccrama.redditslide.Activities.SubredditView;
import me.ccrama.redditslide.Fragments.SubredditListView;
public class SubredditAdapter extends android.support.v7.widget.RecyclerView.Adapter<android.support.v7.widget.RecyclerView.ViewHolder> implements me.ccrama.redditslide.Adapters.BaseAdapter {
    private final android.support.v7.widget.RecyclerView listView;

    public android.app.Activity context;

    public me.ccrama.redditslide.Adapters.SubredditNames dataSet;

    private final int LOADING_SPINNER = 5;

    private final int NO_MORE = 3;

    private final int SPACER = 6;

    me.ccrama.redditslide.Fragments.SubredditListView displayer;

    public SubredditAdapter(android.app.Activity context, me.ccrama.redditslide.Adapters.SubredditNames dataSet, android.support.v7.widget.RecyclerView listView, java.lang.String where, me.ccrama.redditslide.Fragments.SubredditListView displayer) {
        java.lang.String where1 = where.toLowerCase(java.util.Locale.ENGLISH);
        this.listView = listView;
        this.dataSet = dataSet;
        this.context = context;
        this.displayer = displayer;
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
        int SUBREDDIT = 1;
        return SUBREDDIT;
    }

    int tag = 1;

    @java.lang.Override
    public android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup viewGroup, int i) {
        tag++;
        if (i == SPACER) {
            android.view.View v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.spacer, viewGroup, false);
            return new me.ccrama.redditslide.Adapters.SubredditAdapter.SpacerViewHolder(v);
        } else if (i == LOADING_SPINNER) {
            android.view.View v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.loadingmore, viewGroup, false);
            return new me.ccrama.redditslide.Adapters.SubredditAdapter.SubmissionFooterViewHolder(v);
        } else if (i == NO_MORE) {
            android.view.View v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.nomoreposts, viewGroup, false);
            return new me.ccrama.redditslide.Adapters.SubredditAdapter.SubmissionFooterViewHolder(v);
        } else {
            android.view.View v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.subfordiscover, viewGroup, false);
            return new me.ccrama.redditslide.Adapters.SubredditViewHolder(v);
        }
    }

    @java.lang.Override
    public void onBindViewHolder(final android.support.v7.widget.RecyclerView.ViewHolder holder2, final int pos) {
        int i = (pos != 0) ? pos - 1 : pos;
        if (holder2 instanceof me.ccrama.redditslide.Adapters.SubredditViewHolder) {
            final me.ccrama.redditslide.Adapters.SubredditViewHolder holder = ((me.ccrama.redditslide.Adapters.SubredditViewHolder) (holder2));
            final net.dean.jraw.models.Subreddit sub = dataSet.posts.get(i);
            holder.name.setText(sub.getDisplayName());
            if (sub.getLocalizedSubscriberCount() != null) {
                holder.subscribers.setText(context.getString(me.ccrama.redditslide.R.string.subreddit_subscribers_string, sub.getLocalizedSubscriberCount()));
            } else {
                holder.subscribers.setVisibility(android.view.View.GONE);
            }
            holder.color.setBackgroundResource(me.ccrama.redditslide.R.drawable.circle);
            holder.color.getBackground().setColorFilter(me.ccrama.redditslide.Visuals.Palette.getColor(sub.getDisplayName().toLowerCase(java.util.Locale.ENGLISH)), android.graphics.PorterDuff.Mode.MULTIPLY);
            holder.itemView.setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View view) {
                    android.content.Intent inte = new android.content.Intent(context, me.ccrama.redditslide.Activities.SubredditView.class);
                    inte.putExtra(me.ccrama.redditslide.Activities.SubredditView.EXTRA_SUBREDDIT, sub.getDisplayName());
                    context.startActivityForResult(inte, 4);
                }
            });
            holder.overflow.setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View view) {
                    android.content.Intent inte = new android.content.Intent(context, me.ccrama.redditslide.Activities.SubredditView.class);
                    inte.putExtra(me.ccrama.redditslide.Activities.SubredditView.EXTRA_SUBREDDIT, sub.getDisplayName());
                    context.startActivityForResult(inte, 4);
                }
            });
            holder.body.setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View view) {
                    android.content.Intent inte = new android.content.Intent(context, me.ccrama.redditslide.Activities.SubredditView.class);
                    inte.putExtra(me.ccrama.redditslide.Activities.SubredditView.EXTRA_SUBREDDIT, sub.getDisplayName());
                    context.startActivityForResult(inte, 4);
                }
            });
            if (sub.getDataNode().get("public_description_html").asText().equals("null")) {
                holder.body.setVisibility(android.view.View.GONE);
                holder.overflow.setVisibility(android.view.View.GONE);
            } else {
                holder.body.setVisibility(android.view.View.VISIBLE);
                holder.overflow.setVisibility(android.view.View.VISIBLE);
                setViews(sub.getDataNode().get("public_description_html").asText().trim(), sub.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), holder.body, holder.overflow);
            }
            try {
                int state = (sub.isUserSubscriber()) ? android.view.View.VISIBLE : android.view.View.INVISIBLE;
                holder.subbed.setVisibility(state);
            } catch (java.lang.Exception e) {
                holder.subbed.setVisibility(android.view.View.INVISIBLE);
            }
        } else if (holder2 instanceof me.ccrama.redditslide.Adapters.SubredditAdapter.SubmissionFooterViewHolder) {
            android.os.Handler handler = new android.os.Handler();
            final java.lang.Runnable r = new java.lang.Runnable() {
                public void run() {
                    notifyItemChanged(dataSet.posts.size() + 1);// the loading spinner to replaced by nomoreposts.xml

                }
            };
            handler.post(r);
            if (holder2.itemView.findViewById(me.ccrama.redditslide.R.id.reload) != null) {
                holder2.itemView.setVisibility(android.view.View.INVISIBLE);
            }
        }
        if (holder2 instanceof me.ccrama.redditslide.Adapters.SubredditAdapter.SpacerViewHolder) {
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

    private void setViews(java.lang.String rawHTML, java.lang.String subredditName, me.ccrama.redditslide.SpoilerRobotoTextView firstTextView, me.ccrama.redditslide.Views.CommentOverflow commentOverflow) {
        if (rawHTML.isEmpty()) {
            return;
        }
        java.util.List<java.lang.String> blocks = me.ccrama.redditslide.util.SubmissionParser.getBlocks(rawHTML);
        int startIndex = 0;
        // the <div class="md"> case is when the body contains a table or code block first
        if (!blocks.get(0).equals("<div class=\"md\">")) {
            firstTextView.setVisibility(android.view.View.VISIBLE);
            firstTextView.setTextHtml(blocks.get(0), subredditName);
            startIndex = 1;
        } else {
            firstTextView.setText("");
            firstTextView.setVisibility(android.view.View.GONE);
        }
        if (blocks.size() > 1) {
            if (startIndex == 0) {
                commentOverflow.setViews(blocks, subredditName);
            } else {
                commentOverflow.setViews(blocks.subList(startIndex, blocks.size()), subredditName);
            }
        } else {
            commentOverflow.removeAllViews();
        }
    }
}