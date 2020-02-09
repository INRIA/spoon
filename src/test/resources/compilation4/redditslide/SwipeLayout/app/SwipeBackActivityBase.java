package me.ccrama.redditslide.SwipeLayout.app;
import me.ccrama.redditslide.SwipeLayout.SwipeBackLayout;
/**
 *
 *
 * @author Yrom
 */
public interface SwipeBackActivityBase {
    /**
     *
     *
     * @return the SwipeBackLayout associated with this activity.
     */
    me.ccrama.redditslide.SwipeLayout.SwipeBackLayout getSwipeBackLayout();

    void setSwipeBackEnable(boolean enable);

    /**
     * Scroll out contentView and finish the activity
     */
    void scrollToFinishActivity();
}