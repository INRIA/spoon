package me.ccrama.redditslide.Adapters;
import me.ccrama.redditslide.SpoilerRobotoTextView;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Views.CommentOverflow;
/**
 * Created by ccrama on 9/17/2015.
 */
public class SubredditViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
    public final me.ccrama.redditslide.SpoilerRobotoTextView body;

    public final me.ccrama.redditslide.Views.CommentOverflow overflow;

    public final android.view.View color;

    public final android.widget.TextView name;

    public final android.widget.TextView subscribers;

    public final android.view.View subbed;

    public SubredditViewHolder(android.view.View v) {
        super(v);
        color = v.findViewById(me.ccrama.redditslide.R.id.color);
        name = ((android.widget.TextView) (v.findViewById(me.ccrama.redditslide.R.id.name)));
        subscribers = v.findViewById(me.ccrama.redditslide.R.id.subscribers);
        subbed = v.findViewById(me.ccrama.redditslide.R.id.subbed);
        body = ((me.ccrama.redditslide.SpoilerRobotoTextView) (v.findViewById(me.ccrama.redditslide.R.id.body)));
        overflow = ((me.ccrama.redditslide.Views.CommentOverflow) (v.findViewById(me.ccrama.redditslide.R.id.overflow)));
    }
}