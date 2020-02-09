package me.ccrama.redditslide.Adapters;
import me.ccrama.redditslide.R;
/**
 * Created by ccrama on 9/17/2015.
 */
public class MoreCommentViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
    public final android.widget.TextView content;

    public final android.view.View loading;

    public final android.view.View dots;

    public MoreCommentViewHolder(android.view.View v) {
        super(v);
        dots = v.findViewById(me.ccrama.redditslide.R.id.dot);
        content = ((android.widget.TextView) (v.findViewById(me.ccrama.redditslide.R.id.content)));
        loading = v.findViewById(me.ccrama.redditslide.R.id.loading);
    }
}