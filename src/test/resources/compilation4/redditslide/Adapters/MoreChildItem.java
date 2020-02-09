package me.ccrama.redditslide.Adapters;
/**
 * Created by carlo_000 on 1/23/2016.
 */
public class MoreChildItem extends me.ccrama.redditslide.Adapters.CommentObject {
    public net.dean.jraw.models.MoreChildren children;

    @java.lang.Override
    public boolean isComment() {
        return false;
    }

    public MoreChildItem(net.dean.jraw.models.CommentNode node, net.dean.jraw.models.MoreChildren children) {
        comment = node;
        this.children = children;
        this.name = comment.getComment().getFullName() + "more";
    }
}