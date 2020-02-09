package me.ccrama.redditslide.Views;
/**
 * A simple ViewPager subclass that allows swiping between pages to be enabled or disabled at
 * runtime.
 */
public class ToggleSwipeViewPager extends android.support.v4.view.ViewPager {
    private boolean mEnableSwiping = true;

    private boolean swipeLeftOnly = false;

    private boolean mSwipeDisabledUntilRelease = false;

    private float mStartDragX;

    public ToggleSwipeViewPager(android.content.Context context) {
        super(context);
    }

    public ToggleSwipeViewPager(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    @java.lang.Override
    public boolean onTouchEvent(android.view.MotionEvent ev) {
        return (mEnableSwiping || swipeLeftOnly) && super.onTouchEvent(ev);
    }

    @java.lang.Override
    public boolean onInterceptTouchEvent(android.view.MotionEvent ev) {
        if (ev.getAction() == android.view.MotionEvent.ACTION_UP) {
            if (mSwipeDisabledUntilRelease) {
                setSwipingEnabled(true);
                mSwipeDisabledUntilRelease = false;
            }
        }
        try {
            return (mEnableSwiping || swipeLeftOnly) && super.onInterceptTouchEvent(ev);
        } catch (java.lang.IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void setSwipeLeftOnly(boolean enabled) {
        swipeLeftOnly = enabled;
    }

    public void setSwipingEnabled(boolean enabled) {
        mEnableSwiping = enabled;
    }

    public void disableSwipingUntilRelease() {
        setSwipingEnabled(false);
        mSwipeDisabledUntilRelease = true;
    }
}