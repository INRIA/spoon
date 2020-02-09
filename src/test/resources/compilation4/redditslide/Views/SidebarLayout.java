package me.ccrama.redditslide.Views;
import me.ccrama.redditslide.R;
import java.util.ArrayList;
import java.util.List;
/**
 * Drawer that allows for horizontal scrolling views.
 * <p/>
 * Required since if the drawer is on the right, swiping right would close
 * the drawer instead of scrolling horizontally.
 * <p/>
 * Only supports R.id.commentOverflow for now, but could be updated to support
 * any view.
 */
public class SidebarLayout extends android.support.v4.widget.DrawerLayout {
    private java.util.List<android.view.View> scrollableViews = new java.util.ArrayList<>();

    public SidebarLayout(android.content.Context context) {
        super(context);
    }

    public SidebarLayout(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    public SidebarLayout(android.content.Context context, android.util.AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addScrollable(android.view.View view) {
        scrollableViews.add(view);
    }

    /**
     * Override to check if the pressed location corresponds to a scrollable
     * view.
     * <p/>
     * Since the sidebar is a ScrollView, the absolute event position is
     * the scroll y position + ev.getY(). The absolute position of the
     * horizontal scrolling views is the View.getHitRect position (relative
     * to the parent commentOverflow) + commentOverflow.getTop().
     * <p/>
     * See activity_overview.xml to get an idea of the view structure.
     *
     * @param ev
     * 		
     * @return false if the event corresponds to a scrollable, super otherwise.
     */
    @java.lang.Override
    public boolean onInterceptTouchEvent(android.view.MotionEvent ev) {
        android.view.View sidebarScrollView = findViewById(me.ccrama.redditslide.R.id.sidebar_scroll);
        android.view.View commentOverflow = findViewById(me.ccrama.redditslide.R.id.commentOverflow);
        int yOffset = sidebarScrollView.getScrollY();
        for (android.view.View view : scrollableViews) {
            android.graphics.Rect rect = new android.graphics.Rect();
            view.getHitRect(rect);
            if (rect.contains(((int) (ev.getX())), (((int) (ev.getY())) - commentOverflow.getTop()) + yOffset)) {
                return false;
            }
        }
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (java.lang.Exception e) {
            return false;
        }
    }
}