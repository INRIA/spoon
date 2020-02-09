package me.ccrama.redditslide.Adapters;
import me.ccrama.redditslide.SpoilerRobotoTextView;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Views.CommentOverflow;
import me.ccrama.redditslide.SubmissionViews.HeaderImageLinkView;
/**
 * Created by ccrama on 9/17/2015.
 */
public class SubmissionViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
    public final me.ccrama.redditslide.SpoilerRobotoTextView title;

    public final android.widget.TextView contentTitle;

    public final android.widget.TextView contentURL;

    public final android.widget.TextView score;

    public final android.widget.TextView comments;

    public final android.widget.TextView info;

    public final android.view.View menu;

    public final android.view.View mod;

    public final android.view.View hide;

    public final android.view.View upvote;

    public final android.view.View thumbimage;

    public final android.view.View secondMenu;

    public final android.view.View downvote;

    public final android.view.View edit;

    public final me.ccrama.redditslide.SubmissionViews.HeaderImageLinkView leadImage;

    public final me.ccrama.redditslide.SpoilerRobotoTextView firstTextView;

    public final me.ccrama.redditslide.Views.CommentOverflow commentOverflow;

    public final android.view.View save;

    public final android.widget.TextView flairText;

    public final me.ccrama.redditslide.SpoilerRobotoTextView body;

    public final android.widget.RelativeLayout innerRelative;

    public SubmissionViewHolder(android.view.View v) {
        super(v);
        title = ((me.ccrama.redditslide.SpoilerRobotoTextView) (v.findViewById(me.ccrama.redditslide.R.id.title)));
        info = ((android.widget.TextView) (v.findViewById(me.ccrama.redditslide.R.id.information)));
        hide = v.findViewById(me.ccrama.redditslide.R.id.hide);
        menu = v.findViewById(me.ccrama.redditslide.R.id.menu);
        mod = v.findViewById(me.ccrama.redditslide.R.id.mod);
        downvote = v.findViewById(me.ccrama.redditslide.R.id.downvote);
        upvote = v.findViewById(me.ccrama.redditslide.R.id.upvote);
        leadImage = ((me.ccrama.redditslide.SubmissionViews.HeaderImageLinkView) (v.findViewById(me.ccrama.redditslide.R.id.headerimage)));
        contentTitle = ((android.widget.TextView) (v.findViewById(me.ccrama.redditslide.R.id.contenttitle)));
        secondMenu = v.findViewById(me.ccrama.redditslide.R.id.secondMenu);
        flairText = ((android.widget.TextView) (v.findViewById(me.ccrama.redditslide.R.id.text)));
        thumbimage = v.findViewById(me.ccrama.redditslide.R.id.thumbimage2);
        contentURL = ((android.widget.TextView) (v.findViewById(me.ccrama.redditslide.R.id.contenturl)));
        save = v.findViewById(me.ccrama.redditslide.R.id.save);
        edit = v.findViewById(me.ccrama.redditslide.R.id.edit);
        body = ((me.ccrama.redditslide.SpoilerRobotoTextView) (v.findViewById(me.ccrama.redditslide.R.id.body)));
        score = ((android.widget.TextView) (v.findViewById(me.ccrama.redditslide.R.id.score)));
        comments = ((android.widget.TextView) (v.findViewById(me.ccrama.redditslide.R.id.comments)));
        firstTextView = ((me.ccrama.redditslide.SpoilerRobotoTextView) (v.findViewById(me.ccrama.redditslide.R.id.firstTextView)));
        commentOverflow = ((me.ccrama.redditslide.Views.CommentOverflow) (v.findViewById(me.ccrama.redditslide.R.id.commentOverflow)));
        innerRelative = ((android.widget.RelativeLayout) (v.findViewById(me.ccrama.redditslide.R.id.innerrelative)));
    }
}