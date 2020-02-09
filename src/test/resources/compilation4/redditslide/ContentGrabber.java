package me.ccrama.redditslide;
import java.util.Locale;
/**
 * Created by ccrama on 9/17/2015.
 */
public class ContentGrabber {
    /* Inbox Data */
    public enum InboxValue {

        INBOX(me.ccrama.redditslide.R.string.mail_tab_inbox),
        UNREAD(me.ccrama.redditslide.R.string.mail_tab_unread),
        MESSAGES(me.ccrama.redditslide.R.string.mail_tab_messages),
        SENT(me.ccrama.redditslide.R.string.mail_tab_sent),
        MENTIONS(me.ccrama.redditslide.R.string.mail_tab_mentions);
        private final int displayName;

        InboxValue(int resource) {
            this.displayName = resource;
        }

        public int getDisplayName() {
            return displayName;
        }

        public java.lang.String getWhereName() {
            return this.name().toLowerCase(java.util.Locale.ENGLISH);
        }
    }

    public enum ModValue {

        NODMAIL("Mod Mail"),
        MODQUEUE("Modqueue"),
        REPORTS("Reports"),
        UNMODERATED("Unmoderated"),
        SPAM("Spam"),
        EDITED("Edited");
        final java.lang.String displayName;

        ModValue(java.lang.String s) {
            this.displayName = s;
        }

        public java.lang.String getDisplayName() {
            return displayName;
        }

        public java.lang.String getWhereName() {
            return displayName.toLowerCase(java.util.Locale.ENGLISH);
        }
    }
}