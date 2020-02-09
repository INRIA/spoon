package me.ccrama.redditslide.ForceTouch.callback;
import me.ccrama.redditslide.ForceTouch.PeekView;
import me.ccrama.redditslide.ForceTouch.builder.Peek;
/**
 * Provides callbacks for the lifecycle events of the PeekView
 */
public interface OnPeek {
    void onInflated(me.ccrama.redditslide.ForceTouch.PeekView rootView, android.view.View contentView);

    void shown();

    void dismissed();
}