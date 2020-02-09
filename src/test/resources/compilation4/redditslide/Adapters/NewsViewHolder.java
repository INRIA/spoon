package me.ccrama.redditslide.Adapters;
import me.ccrama.redditslide.SpoilerRobotoTextView;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.SubmissionViews.HeaderImageLinkView;
/**
 * Created by ccrama on 11/19/2017.
 */
public class NewsViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
    public final me.ccrama.redditslide.SpoilerRobotoTextView title;

    public final android.view.View menu;

    public final android.view.View comment;

    public final me.ccrama.redditslide.SubmissionViews.HeaderImageLinkView leadImage;

    public final android.widget.RelativeLayout innerRelative;

    public final android.widget.ImageView thumbnail;

    public NewsViewHolder(android.view.View v) {
        super(v);
        title = ((me.ccrama.redditslide.SpoilerRobotoTextView) (v.findViewById(me.ccrama.redditslide.R.id.title)));
        comment = v.findViewById(me.ccrama.redditslide.R.id.comments);
        menu = v.findViewById(me.ccrama.redditslide.R.id.more);
        leadImage = ((me.ccrama.redditslide.SubmissionViews.HeaderImageLinkView) (v.findViewById(me.ccrama.redditslide.R.id.headerimage)));
        innerRelative = ((android.widget.RelativeLayout) (v.findViewById(me.ccrama.redditslide.R.id.innerrelative)));
        thumbnail = v.findViewById(me.ccrama.redditslide.R.id.thumbimage2);
    }
}