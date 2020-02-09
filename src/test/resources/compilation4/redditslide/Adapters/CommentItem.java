package me.ccrama.redditslide.Adapters;
/**
 * Created by carlo_000 on 10/27/2015.
 */
public class CommentItem extends me.ccrama.redditslide.Adapters.CommentObject {
    public CommentItem(net.dean.jraw.models.CommentNode node) {
        comment = node;
        this.name = comment.getComment().getFullName();
    }

    @java.lang.Override
    public boolean isComment() {
        return true;
    }
}