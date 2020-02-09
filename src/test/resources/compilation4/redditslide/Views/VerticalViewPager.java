package me.ccrama.redditslide.Views;
/**
 * Created by carlo_000 on 12/7/2015.
 */
public class VerticalViewPager extends android.support.v4.view.ViewPager {
    public VerticalViewPager(android.content.Context context) {
        super(context);
        init();
    }

    public VerticalViewPager(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // The majority of the magic happens here
        setPageTransformer(true, new me.ccrama.redditslide.Views.VerticalViewPager.VerticalPageTransformer());
        // The easiest way to get rid of the overscroll drawing that happens on the left and right
        setOverScrollMode(android.view.View.OVER_SCROLL_NEVER);
    }

    private class VerticalPageTransformer implements android.support.v4.view.ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75F;

        @java.lang.Override
        public void transformPage(android.view.View view, float position) {
            if (position < (-1)) {
                // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);
            } else if (position <= 0) {
                // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                // Counteract the default slide transition
                view.setTranslationX(view.getWidth() * (-position));
                // set Y position to swipe in from top
                float yPosition = position * view.getHeight();
                view.setTranslationY(yPosition);
                view.setScaleX(1);
                view.setScaleY(1);
            } else if (position <= 1) {
                // [0,1]
                view.setAlpha(1);
                // Counteract the default slide transition
                view.setTranslationX(view.getWidth() * (-position));
                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = me.ccrama.redditslide.Views.VerticalViewPager.VerticalPageTransformer.MIN_SCALE + ((1 - me.ccrama.redditslide.Views.VerticalViewPager.VerticalPageTransformer.MIN_SCALE) * (1 - java.lang.Math.abs(position)));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
            } else {
                // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    /**
     * Swaps the X and Y coordinates of your touch event.
     */
    private android.view.MotionEvent swapXY(android.view.MotionEvent ev) {
        float width = getWidth();
        float height = getHeight();
        float newX = (ev.getY() / height) * width;
        float newY = (ev.getX() / width) * height;
        ev.setLocation(newX, newY);
        return ev;
    }

    @java.lang.Override
    public boolean onInterceptTouchEvent(android.view.MotionEvent ev) {
        boolean intercepted = super.onInterceptTouchEvent(swapXY(ev));
        swapXY(ev);// return touch coordinates to original reference frame for any child views

        return intercepted;
    }

    @java.lang.Override
    public boolean onTouchEvent(android.view.MotionEvent ev) {
        return super.onTouchEvent(swapXY(ev));
    }
}