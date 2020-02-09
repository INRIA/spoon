package me.ccrama.redditslide.Toolbox;
import com.google.gson.annotations.SerializedName;
/**
 * Defines a Usernote so GSON can deserialize it
 */
public class Usernote {
    @com.google.gson.annotations.SerializedName("n")
    private java.lang.String noteText;

    @com.google.gson.annotations.SerializedName("l")
    private java.lang.String link;

    @com.google.gson.annotations.SerializedName("t")
    private long time;

    @com.google.gson.annotations.SerializedName("m")
    private int mod;

    @com.google.gson.annotations.SerializedName("w")
    private int warning;

    public Usernote() {
        // for GSON
    }

    public Usernote(java.lang.String noteText, java.lang.String link, long time, int mod, int warning) {
        this.noteText = noteText;
        this.link = link;
        this.time = time;
        this.mod = mod;
        this.warning = warning;
    }

    public java.lang.String getNoteText() {
        return noteText;
    }

    public long getTime() {
        return time * 1000;// * 1000 so it makes sense as a long

    }

    public int getMod() {
        return mod;
    }

    public java.lang.String getLink() {
        return link;
    }

    public int getWarning() {
        return warning;
    }

    @java.lang.Override
    public boolean equals(java.lang.Object obj) {
        if (obj instanceof me.ccrama.redditslide.Toolbox.Usernote) {
            return ((((((me.ccrama.redditslide.Toolbox.Usernote) (obj)).warning == warning) && (((me.ccrama.redditslide.Toolbox.Usernote) (obj)).mod == mod)) && (((me.ccrama.redditslide.Toolbox.Usernote) (obj)).time == time)) && ((me.ccrama.redditslide.Toolbox.Usernote) (obj)).noteText.equals(noteText)) && ((me.ccrama.redditslide.Toolbox.Usernote) (obj)).link.equals(link);
        }
        return false;
    }

    /**
     * Identify what type of link a usernote points to, if any
     *
     * @return Type of link
     */
    public me.ccrama.redditslide.Toolbox.Usernote.UsernoteLinkType getLinkType() {
        if (link.isEmpty()) {
            return null;
        }
        if (link.startsWith("m,")) {
            return me.ccrama.redditslide.Toolbox.Usernote.UsernoteLinkType.MODMAIL;
        } else if (link.startsWith("l,") && (link.split(",").length == 3)) {
            return me.ccrama.redditslide.Toolbox.Usernote.UsernoteLinkType.COMMENT;
        } else if (link.startsWith("l,")) {
            return me.ccrama.redditslide.Toolbox.Usernote.UsernoteLinkType.POST;
        } else {
            return null;
        }
    }

    /**
     * Gets the Usernote's link as a URL
     *
     * @return String of usernote's URL.
     */
    public java.lang.String getLinkAsURL(java.lang.String subreddit) {
        if (link.isEmpty()) {
            return null;
        }
        if (getLinkType() == me.ccrama.redditslide.Toolbox.Usernote.UsernoteLinkType.MODMAIL) {
            return "https://www.reddit.com/message/messages/" + link.substring(3);
        } else {
            java.lang.String[] split = link.split(",");
            return ((("https://www.reddit.com/r/" + subreddit) + "/comments/") + split[1]) + (getLinkType() == me.ccrama.redditslide.Toolbox.Usernote.UsernoteLinkType.COMMENT ? "/_/" + split[2] : "");
        }
    }

    public enum UsernoteLinkType {

        POST,
        COMMENT,
        MODMAIL;}
}