package me.ccrama.redditslide;
/**
 * Constants used throughout the app
 */
public class Constants {
    public static final int PAGINATOR_POST_LIMIT = 25;

    /**
     * Maximum posts to request from Reddit *
     */
    /**
     * This is the estimated height of the Tabs view mode in dp.
     * Use this for calculating the SwipeToRefresh (PTR) progresses indicator offset when using
     * "Tabs" view mode.
     */
    public static final int TAB_HEADER_VIEW_OFFSET = me.ccrama.redditslide.Reddit.dpToPxVertical(108);

    /**
     * This is the estimated height of the toolbar height in dp.
     * Use this for calculating the SwipeToRefresh (PTR) progresses indicator offset when using
     * "Single" view mode.
     */
    public static final int SINGLE_HEADER_VIEW_OFFSET = me.ccrama.redditslide.Reddit.dpToPxVertical(56);

    /**
     * These offsets are used for the SwipeToRefresh (PTR) progress indicator.
     * The TOP offset is used for the starting point of the indicator (underneath the toolbar).
     * The BOTTOM offset is used for the end point of the indicator (below the toolbar).
     * This is used whenever we call mSwipeRefreshLayout.setProgressViewOffset().
     */
    public static final int PTR_OFFSET_TOP = me.ccrama.redditslide.Reddit.dpToPxVertical(40);

    public static final int PTR_OFFSET_BOTTOM = me.ccrama.redditslide.Reddit.dpToPxVertical(18);

    /**
     * Drawer swipe edge (navdrawer). The higher the value, the more sensitive the navdrawer
     * swipe area becomes. This is a percentage of the screen width.
     */
    public static final float DRAWER_SWIPE_EDGE = 0.07F;

    public static final float DRAWER_SWIPE_EDGE_TABLET = 0.03F;

    /**
     * The client ID to use when making requests to the Imgur Mashape API
     */
    public static final java.lang.String IMGUR_MASHAPE_CLIENT_ID = "bef87913eb202e9";

    public static final java.lang.String TUMBLR_API_KEY = "qr0mPKRNb46Q5HwjkQjALEsA7m4Ub5MKvwv2qXmGHQJjG2B3gl";

    public static final int SUBREDDIT_SEARCH_METHOD_DRAWER = 1;

    public static final int SUBREDDIT_SEARCH_METHOD_TOOLBAR = 2;

    public static final int SUBREDDIT_SEARCH_METHOD_BOTH = 3;

    public static final int FAB_DISMISS = 1;

    public static final int FAB_POST = 2;

    public static final int FAB_SEARCH = 3;

    public enum BackButtonBehaviorOptions {

        Default(0),
        ConfirmExit(1),
        OpenDrawer(2);
        private final int mValue;

        BackButtonBehaviorOptions(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }
    }
}