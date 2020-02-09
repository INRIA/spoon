package me.ccrama.redditslide.Adapters;
import me.ccrama.redditslide.SpoilerRobotoTextView;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Views.CommentOverflow;
/**
 * Created by ccrama on 9/17/2015.
 */
public class MessageViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
    public final android.widget.TextView title;

    public final me.ccrama.redditslide.SpoilerRobotoTextView content;

    public final android.widget.TextView time;

    public final android.widget.TextView user;

    public final me.ccrama.redditslide.Views.CommentOverflow commentOverflow;

    public MessageViewHolder(android.view.View v) {
        super(v);
        title = ((android.widget.TextView) (v.findViewById(me.ccrama.redditslide.R.id.title)));
        title.setMaxLines(1);
        title.setEllipsize(android.text.TextUtils.TruncateAt.END);
        content = ((me.ccrama.redditslide.SpoilerRobotoTextView) (v.findViewById(me.ccrama.redditslide.R.id.content)));
        time = ((android.widget.TextView) (v.findViewById(me.ccrama.redditslide.R.id.time)));
        commentOverflow = ((me.ccrama.redditslide.Views.CommentOverflow) (v.findViewById(me.ccrama.redditslide.R.id.commentOverflow)));
        user = ((android.widget.TextView) (v.findViewById(me.ccrama.redditslide.R.id.user)));
    }
}