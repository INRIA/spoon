package me.ccrama.redditslide.ForceTouch.util;
import me.ccrama.redditslide.ForceTouch.PeekViewActivity;
import me.ccrama.redditslide.ForceTouch.builder.Peek;
public class GestureListener extends android.view.GestureDetector.SimpleOnGestureListener {
    private me.ccrama.redditslide.ForceTouch.PeekViewActivity activity;

    private android.view.View base;

    private me.ccrama.redditslide.ForceTouch.builder.Peek peek;

    public GestureListener(me.ccrama.redditslide.ForceTouch.PeekViewActivity activity, android.view.View base, me.ccrama.redditslide.ForceTouch.builder.Peek peek) {
        this.activity = activity;
        this.base = base;
        this.peek = peek;
    }

    @java.lang.Override
    public boolean onSingleTapConfirmed(android.view.MotionEvent event) {
        base.performClick();
        return true;
    }

    @java.lang.Override
    public void onLongPress(android.view.MotionEvent event) {
        peek.show(activity, event);
    }
}