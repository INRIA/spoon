package me.ccrama.redditslide.Adapters;
import me.ccrama.redditslide.SpoilerRobotoTextView;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Views.CommentOverflow;
/**
 * Created by ccrama on 9/17/2015.
 */
public class CommentViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
    public final android.widget.TextView childrenNumber;

    public final android.view.View dot;

    public final android.widget.LinearLayout menuArea;

    public final int textColorUp;

    public final android.widget.TextView content;

    public final int textColorDown;

    public final int textColorRegular;

    public final me.ccrama.redditslide.SpoilerRobotoTextView firstTextView;

    public final me.ccrama.redditslide.Views.CommentOverflow commentOverflow;

    public final android.view.View background;

    public final android.widget.ImageView imageFlair;

    public CommentViewHolder(android.view.View v) {
        super(v);
        background = v.findViewById(me.ccrama.redditslide.R.id.background);
        dot = v.findViewById(me.ccrama.redditslide.R.id.dot);
        menuArea = ((android.widget.LinearLayout) (v.findViewById(me.ccrama.redditslide.R.id.menuarea)));
        childrenNumber = ((android.widget.TextView) (v.findViewById(me.ccrama.redditslide.R.id.commentnumber)));
        firstTextView = ((me.ccrama.redditslide.SpoilerRobotoTextView) (v.findViewById(me.ccrama.redditslide.R.id.firstTextView)));
        textColorDown = android.support.v4.content.ContextCompat.getColor(v.getContext(), me.ccrama.redditslide.R.color.md_blue_500);
        textColorRegular = firstTextView.getCurrentTextColor();
        textColorUp = android.support.v4.content.ContextCompat.getColor(v.getContext(), me.ccrama.redditslide.R.color.md_orange_500);
        content = ((android.widget.TextView) (v.findViewById(me.ccrama.redditslide.R.id.content)));
        imageFlair = ((android.widget.ImageView) (v.findViewById(me.ccrama.redditslide.R.id.flair)));
        commentOverflow = ((me.ccrama.redditslide.Views.CommentOverflow) (v.findViewById(me.ccrama.redditslide.R.id.commentOverflow)));
    }
}