package me.ccrama.redditslide;
public interface ClickableText {
    /**
     * Callback for when a link is clicked
     *
     * @param url
     * 		the url link (e.g. #s for some spoilers)
     * @param xOffset
     * 		the last index of the url text (not the link)
     * @param subreddit
     * 		
     */
    void onLinkClick(java.lang.String url, int xOffset, java.lang.String subreddit, android.text.style.URLSpan span);

    void onLinkLongClick(java.lang.String url, android.view.MotionEvent event);
}