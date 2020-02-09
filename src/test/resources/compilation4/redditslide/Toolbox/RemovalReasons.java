package me.ccrama.redditslide.Toolbox;
import java.io.UnsupportedEncodingException;
import com.google.gson.annotations.SerializedName;
import java.net.URLDecoder;
import java.util.List;
public class RemovalReasons {
    @com.google.gson.annotations.SerializedName("pmsubject")
    private java.lang.String pmSubject;

    private java.lang.String header;

    private java.lang.String footer;

    @com.google.gson.annotations.SerializedName("logsub")
    private java.lang.String logSub;

    @com.google.gson.annotations.SerializedName("logtitle")
    private java.lang.String logTitle;

    @com.google.gson.annotations.SerializedName("logreason")
    private java.lang.String logReason;

    @com.google.gson.annotations.SerializedName("bantitle")
    private java.lang.String banTitle;// Is this even used by Toolbox? For mod button bans maybe (not a removal reason thing...)?


    private java.util.List<me.ccrama.redditslide.Toolbox.RemovalReasons.RemovalReason> reasons;

    public RemovalReasons() {
    }

    public java.lang.String getPmSubject() {
        if (pmSubject.isEmpty()) {
            return "Your {kind} was removed from /r/{subreddit}";
        }
        return pmSubject;
    }

    public java.lang.String getHeader() {
        try {
            return java.net.URLDecoder.decode(header, "UTF-8");// header is url encoded

        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
    }

    public java.lang.String getFooter() {
        try {
            return java.net.URLDecoder.decode(footer, "UTF-8");// footer is url encoded

        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
    }

    public java.lang.String getLogSub() {
        return logSub;
    }

    public java.lang.String getLogTitle() {
        if (logTitle.isEmpty()) {
            return "Removed: {kind} by /u/{author} to /r/{subreddit}";
        }
        return logTitle;
    }

    public java.lang.String getLogReason() {
        return logReason;
    }

    public java.util.List<me.ccrama.redditslide.Toolbox.RemovalReasons.RemovalReason> getReasons() {
        return reasons;
    }

    /**
     * Class defining an individual removal reason
     */
    public class RemovalReason {
        private java.lang.String title;

        private java.lang.String text;

        private java.lang.String flairText;

        private java.lang.String flairCSS;

        public RemovalReason() {
        }

        public java.lang.String getTitle() {
            return title;
        }

        public java.lang.String getText() {
            try {
                return java.net.URLDecoder.decode(text, "UTF-8");// text is url encoded

            } catch (java.io.UnsupportedEncodingException e) {
                return null;
            }
        }

        public java.lang.String getFlairText() {
            return flairText;
        }

        public java.lang.String getFlairCSS() {
            return flairCSS;
        }
    }
}