package me.ccrama.redditslide.Adapters;
import me.ccrama.redditslide.SpoilerRobotoTextView;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Views.CommentOverflow;
/**
 * Created by ccrama on 9/17/2015.
 */
public class ProfileCommentViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
    public final android.widget.TextView title;

    public final android.widget.TextView user;

    public final android.widget.TextView score;

    public final android.widget.TextView time;

    public final android.view.View gild;

    public final me.ccrama.redditslide.SpoilerRobotoTextView content;

    public final me.ccrama.redditslide.Views.CommentOverflow overflow;

    public ProfileCommentViewHolder(android.view.View v) {
        super(v);
        title = ((android.widget.TextView) (v.findViewById(me.ccrama.redditslide.R.id.title)));
        user = v.findViewById(me.ccrama.redditslide.R.id.user);
        score = ((android.widget.TextView) (v.findViewById(me.ccrama.redditslide.R.id.score)));
        time = ((android.widget.TextView) (v.findViewById(me.ccrama.redditslide.R.id.time)));
        gild = v.findViewById(me.ccrama.redditslide.R.id.gildtext);
        content = ((me.ccrama.redditslide.SpoilerRobotoTextView) (v.findViewById(me.ccrama.redditslide.R.id.content)));
        overflow = ((me.ccrama.redditslide.Views.CommentOverflow) (v.findViewById(me.ccrama.redditslide.R.id.commentOverflow)));
    }
}