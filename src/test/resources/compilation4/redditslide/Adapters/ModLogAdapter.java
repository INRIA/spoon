/**
 * Created by ccrama on 3/22/2015.
 */
package me.ccrama.redditslide.Adapters;
import java.util.Locale;
import me.ccrama.redditslide.TimeUtils;
import me.ccrama.redditslide.SpoilerRobotoTextView;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.Views.RoundedBackgroundSpan;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.OpenRedditLink;
public class ModLogAdapter extends android.support.v7.widget.RecyclerView.Adapter<android.support.v7.widget.RecyclerView.ViewHolder> implements me.ccrama.redditslide.Adapters.BaseAdapter {
    private final int SPACER = 6;

    public static final int MESSAGE = 2;

    public final android.app.Activity mContext;

    private final android.support.v7.widget.RecyclerView listView;

    public me.ccrama.redditslide.Adapters.ModLogPosts dataSet;

    public ModLogAdapter(android.app.Activity mContext, me.ccrama.redditslide.Adapters.ModLogPosts dataSet, android.support.v7.widget.RecyclerView listView) {
        this.mContext = mContext;
        this.listView = listView;
        this.dataSet = dataSet;
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
        if ((position == 0) && (!dataSet.posts.isEmpty())) {
            return SPACER;
        } else if (!dataSet.posts.isEmpty()) {
            position -= 1;
        }
        return me.ccrama.redditslide.Adapters.ModLogAdapter.MESSAGE;
    }

    public class SpacerViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public SpacerViewHolder(android.view.View itemView) {
            super(itemView);
        }
    }

    @java.lang.Override
    public android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup viewGroup, int i) {
        if (i == SPACER) {
            android.view.View v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.spacer, viewGroup, false);
            return new me.ccrama.redditslide.Adapters.ModLogAdapter.SpacerViewHolder(v);
        } else {
            android.view.View v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.mod_action, viewGroup, false);
            return new me.ccrama.redditslide.Adapters.ModLogAdapter.ModLogViewHolder(v);
        }
    }

    public static class ModLogViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        me.ccrama.redditslide.SpoilerRobotoTextView body;

        android.widget.ImageView icon;

        public ModLogViewHolder(android.view.View itemView) {
            super(itemView);
            body = ((me.ccrama.redditslide.SpoilerRobotoTextView) (itemView.findViewById(me.ccrama.redditslide.R.id.body)));
            icon = ((android.widget.ImageView) (itemView.findViewById(me.ccrama.redditslide.R.id.action)));
        }
    }

    @java.lang.Override
    public void onBindViewHolder(final android.support.v7.widget.RecyclerView.ViewHolder firstHold, final int pos) {
        int i = (pos != 0) ? pos - 1 : pos;
        if (firstHold instanceof me.ccrama.redditslide.Adapters.ModLogAdapter.ModLogViewHolder) {
            me.ccrama.redditslide.Adapters.ModLogAdapter.ModLogViewHolder holder = ((me.ccrama.redditslide.Adapters.ModLogAdapter.ModLogViewHolder) (firstHold));
            final net.dean.jraw.models.ModAction a = dataSet.posts.get(i);
            android.text.SpannableStringBuilder b = new android.text.SpannableStringBuilder();
            android.text.SpannableStringBuilder titleString = new android.text.SpannableStringBuilder();
            java.lang.String spacer = mContext.getString(me.ccrama.redditslide.R.string.submission_properties_seperator);
            java.lang.String timeAgo = me.ccrama.redditslide.TimeUtils.getTimeAgo(a.getCreated().getTime(), mContext);
            java.lang.String time = ((timeAgo == null) || timeAgo.isEmpty()) ? "just now" : timeAgo;// some users were crashing here

            titleString.append(time);
            titleString.append(spacer);
            if (a.getSubreddit() != null) {
                java.lang.String subname = a.getSubreddit();
                android.text.SpannableStringBuilder subreddit = new android.text.SpannableStringBuilder("/r/" + subname);
                if (me.ccrama.redditslide.SettingValues.colorSubName && (me.ccrama.redditslide.Visuals.Palette.getColor(subname) != me.ccrama.redditslide.Visuals.Palette.getDefaultColor())) {
                    subreddit.setSpan(new android.text.style.ForegroundColorSpan(me.ccrama.redditslide.Visuals.Palette.getColor(subname)), 0, subreddit.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    subreddit.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, subreddit.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                titleString.append(subreddit);
            }
            b.append(titleString);
            b.append(spacer);
            android.text.SpannableStringBuilder author = new android.text.SpannableStringBuilder(a.getModerator());
            final int authorcolor = me.ccrama.redditslide.Visuals.Palette.getFontColorUser(a.getModerator());
            author.setSpan(new android.text.style.TypefaceSpan("sans-serif-condensed"), 0, author.length(), android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            author.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, author.length(), android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if ((me.ccrama.redditslide.Authentication.name != null) && a.getModerator().toLowerCase(java.util.Locale.ENGLISH).equals(me.ccrama.redditslide.Authentication.name.toLowerCase(java.util.Locale.ENGLISH))) {
                author.replace(0, author.length(), (" " + a.getModerator()) + " ");
                author.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_deep_orange_300, false), 0, author.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (authorcolor != 0) {
                author.setSpan(new android.text.style.ForegroundColorSpan(authorcolor), 0, author.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            author.setSpan(new android.text.style.RelativeSizeSpan(0.8F), 0, author.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            b.append(author);
            b.append("\n\n");
            b.append(((a.getAction() + " ") + (!a.getDataNode().get("target_title").isNull() ? ("\"" + a.getDataNode().get("target_title").asText()) + "\"" : "")) + (a.getTargetAuthor() != null ? " by /u/" + a.getTargetAuthor() : ""));
            if (a.getTargetPermalink() != null) {
                holder.itemView.setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        me.ccrama.redditslide.OpenRedditLink.openUrl(mContext, a.getTargetPermalink(), true);
                    }
                });
            }
            if (a.getDetails() != null) {
                android.text.SpannableStringBuilder description = new android.text.SpannableStringBuilder((" (" + a.getDetails()) + ")");
                description.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.ITALIC), 0, description.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                description.setSpan(new android.text.style.RelativeSizeSpan(0.8F), 0, description.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                b.append(description);
            }
            holder.body.setText(b);
            java.lang.String action = a.getAction();
            if (action.equals("removelink")) {
                holder.icon.setImageDrawable(android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.close, null));
            } else if (action.equals("approvecomment")) {
                holder.icon.setImageDrawable(android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.support, null));
            } else if (action.equals("removecomment")) {
                holder.icon.setImageDrawable(android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.commentchange, null));
            } else if (action.equals("approvelink")) {
                holder.icon.setImageDrawable(android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.support, null));
            } else if (action.equals("editflair")) {
                holder.icon.setImageDrawable(android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.flair, null));
            } else if (action.equals("distinguish")) {
                holder.icon.setImageDrawable(android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.iconstarfilled, null));
            } else if (action.equals("sticky")) {
                holder.icon.setImageDrawable(android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.lock, null));
            } else if (action.equals("unsticky")) {
                holder.icon.setImageDrawable(android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.lock, null));
            } else if (action.equals("ignorereports")) {
                holder.icon.setImageDrawable(android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.ignore, null));
            } else if (action.equals("unignorereports")) {
                holder.icon.setImageDrawable(android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.unignore, null));
            } else if (action.equals("marknsfw")) {
                holder.icon.setImageDrawable(android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.hide, null));
            } else if (action.equals("unmarknsfw")) {
                holder.icon.setImageDrawable(android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.hide, null));
            } else {
                holder.icon.setImageDrawable(android.support.v4.content.res.ResourcesCompat.getDrawable(mContext.getResources(), me.ccrama.redditslide.R.drawable.mod, null));
            }
        }
        if (firstHold instanceof me.ccrama.redditslide.Adapters.ModLogAdapter.SpacerViewHolder) {
            firstHold.itemView.findViewById(me.ccrama.redditslide.R.id.height).setLayoutParams(new android.widget.LinearLayout.LayoutParams(firstHold.itemView.getWidth(), mContext.findViewById(me.ccrama.redditslide.R.id.header).getHeight()));
        }
    }

    @java.lang.Override
    public int getItemCount() {
        if ((dataSet.posts == null) || dataSet.posts.isEmpty()) {
            return 0;
        } else {
            return dataSet.posts.size() + 1;
        }
    }
}