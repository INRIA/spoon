package me.ccrama.redditslide.Adapters;
/**
 * Created by carlo_000 on 10/27/2015.
 */
public class CommentObject {
    public java.lang.String name = "";

    public boolean isComment() {
        return false;
    }

    public java.lang.String getName() {
        return name;
    }

    public net.dean.jraw.models.CommentNode comment;
}