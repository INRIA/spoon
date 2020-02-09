package me.ccrama.redditslide;
import java.util.ArrayList;
/**
 * Created by carlo_000 on 2/26/2016.
 */
public class ActionStates {
    public static final java.util.ArrayList<java.lang.String> upVotedFullnames = new java.util.ArrayList<>();

    public static final java.util.ArrayList<java.lang.String> downVotedFullnames = new java.util.ArrayList<>();

    public static final java.util.ArrayList<java.lang.String> unvotedFullnames = new java.util.ArrayList<>();

    public static final java.util.ArrayList<java.lang.String> savedFullnames = new java.util.ArrayList<>();

    public static final java.util.ArrayList<java.lang.String> unSavedFullnames = new java.util.ArrayList<>();

    public static net.dean.jraw.models.VoteDirection getVoteDirection(net.dean.jraw.models.PublicContribution s) {
        if (me.ccrama.redditslide.ActionStates.upVotedFullnames.contains(s.getFullName())) {
            return net.dean.jraw.models.VoteDirection.UPVOTE;
        } else if (me.ccrama.redditslide.ActionStates.downVotedFullnames.contains(s.getFullName())) {
            return net.dean.jraw.models.VoteDirection.DOWNVOTE;
        } else if (me.ccrama.redditslide.ActionStates.unvotedFullnames.contains(s.getFullName())) {
            return net.dean.jraw.models.VoteDirection.NO_VOTE;
        } else {
            return s.getVote();
        }
    }

    public static void setVoteDirection(net.dean.jraw.models.PublicContribution s, net.dean.jraw.models.VoteDirection direction) {
        java.lang.String fullname = s.getFullName();
        me.ccrama.redditslide.ActionStates.upVotedFullnames.remove(fullname);
        me.ccrama.redditslide.ActionStates.downVotedFullnames.remove(fullname);
        me.ccrama.redditslide.ActionStates.unvotedFullnames.remove(fullname);
        switch (direction) {
            case UPVOTE :
                me.ccrama.redditslide.ActionStates.upVotedFullnames.add(fullname);
                break;
            case DOWNVOTE :
                me.ccrama.redditslide.ActionStates.downVotedFullnames.add(fullname);
                break;
            case NO_VOTE :
                me.ccrama.redditslide.ActionStates.unvotedFullnames.add(fullname);
                break;
        }
    }

    public static boolean isSaved(net.dean.jraw.models.Submission s) {
        if (me.ccrama.redditslide.ActionStates.savedFullnames.contains(s.getFullName())) {
            return true;
        } else if (me.ccrama.redditslide.ActionStates.unSavedFullnames.contains(s.getFullName())) {
            return false;
        } else {
            return s.isSaved();
        }
    }

    public static boolean isSaved(net.dean.jraw.models.Comment s) {
        if (me.ccrama.redditslide.ActionStates.savedFullnames.contains(s.getFullName())) {
            return true;
        } else if (me.ccrama.redditslide.ActionStates.unSavedFullnames.contains(s.getFullName())) {
            return false;
        } else {
            return s.isSaved();
        }
    }

    public static void setSaved(net.dean.jraw.models.Submission s, boolean b) {
        java.lang.String fullname = s.getFullName();
        me.ccrama.redditslide.ActionStates.savedFullnames.remove(fullname);
        if (b) {
            me.ccrama.redditslide.ActionStates.savedFullnames.add(fullname);
        } else {
            me.ccrama.redditslide.ActionStates.unSavedFullnames.add(fullname);
        }
    }

    public static void setSaved(net.dean.jraw.models.Comment s, boolean b) {
        java.lang.String fullname = s.getFullName();
        me.ccrama.redditslide.ActionStates.savedFullnames.remove(fullname);
        if (b) {
            me.ccrama.redditslide.ActionStates.savedFullnames.add(fullname);
        } else {
            me.ccrama.redditslide.ActionStates.unSavedFullnames.add(fullname);
        }
    }
}