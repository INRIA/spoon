package me.ccrama.redditslide.Fragments;
/**
 * Created by PKhurana on 7/8/16.
 * Adopted from http://illusionsandroid.blogspot.com/2011/05/adding-fling-gesture-listener-to-view.html
 */
public abstract class OnFlingGestureListener implements android.view.View.OnTouchListener {
    private final android.view.GestureDetector gdt = new android.view.GestureDetector(new me.ccrama.redditslide.Fragments.OnFlingGestureListener.GestureListener());

    @java.lang.Override
    public boolean onTouch(final android.view.View v, final android.view.MotionEvent event) {
        return gdt.onTouchEvent(event);
    }

    private final class GestureListener extends android.view.GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_MIN_DISTANCE = 60;

        private static final int SWIPE_THRESHOLD_VELOCITY = 100;

        @java.lang.Override
        public boolean onFling(android.view.MotionEvent e1, android.view.MotionEvent e2, float velocityX, float velocityY) {
            if (((e1.getX() - e2.getX()) > me.ccrama.redditslide.Fragments.OnFlingGestureListener.GestureListener.SWIPE_MIN_DISTANCE) && (java.lang.Math.abs(velocityX) > me.ccrama.redditslide.Fragments.OnFlingGestureListener.GestureListener.SWIPE_THRESHOLD_VELOCITY)) {
                onRightToLeft();
                return true;
            } else if (((e2.getX() - e1.getX()) > me.ccrama.redditslide.Fragments.OnFlingGestureListener.GestureListener.SWIPE_MIN_DISTANCE) && (java.lang.Math.abs(velocityX) > me.ccrama.redditslide.Fragments.OnFlingGestureListener.GestureListener.SWIPE_THRESHOLD_VELOCITY)) {
                onLeftToRight();
                return true;
            }
            if (((e1.getY() - e2.getY()) > me.ccrama.redditslide.Fragments.OnFlingGestureListener.GestureListener.SWIPE_MIN_DISTANCE) && (java.lang.Math.abs(velocityY) > me.ccrama.redditslide.Fragments.OnFlingGestureListener.GestureListener.SWIPE_THRESHOLD_VELOCITY)) {
                onBottomToTop();
                return true;
            } else if (((e2.getY() - e1.getY()) > me.ccrama.redditslide.Fragments.OnFlingGestureListener.GestureListener.SWIPE_MIN_DISTANCE) && (java.lang.Math.abs(velocityY) > me.ccrama.redditslide.Fragments.OnFlingGestureListener.GestureListener.SWIPE_THRESHOLD_VELOCITY)) {
                onTopToBottom();
                return true;
            }
            return false;
        }
    }

    public abstract void onRightToLeft();

    public abstract void onLeftToRight();

    public abstract void onBottomToTop();

    public abstract void onTopToBottom();
}