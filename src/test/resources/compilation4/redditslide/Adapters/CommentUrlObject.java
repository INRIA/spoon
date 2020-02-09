package me.ccrama.redditslide.Adapters;
/**
 * Created by carlo_000 on 10/27/2015.
 */
public class CommentUrlObject {
    public java.lang.String url;

    java.lang.String subredditName;

    public net.dean.jraw.models.CommentNode comment;

    public CommentUrlObject(net.dean.jraw.models.CommentNode comment, java.lang.String url, java.lang.String subredditName) {
        this.comment = comment;
        this.subredditName = subredditName;
        this.url = url;
    }

    public java.lang.String getSubredditName() {
        return subredditName;
    }

    public java.lang.String getUrl() {
        return url;
    }
}