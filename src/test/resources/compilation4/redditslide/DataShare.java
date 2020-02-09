package me.ccrama.redditslide;
import java.util.ArrayList;
import me.ccrama.redditslide.Adapters.CommentObject;
import me.ccrama.redditslide.Adapters.SubredditPosts;
/**
 * Created by ccrama on 9/19/2015.
 */
public class DataShare {
    public static net.dean.jraw.models.Submission sharedSubmission;

    // public static Submission notifs;
    public static net.dean.jraw.models.PrivateMessage sharedMessage;

    public static java.util.ArrayList<me.ccrama.redditslide.Adapters.CommentObject> sharedComments;

    public static java.lang.String subAuthor;

    public static me.ccrama.redditslide.Adapters.SubredditPosts sharedSub;
}