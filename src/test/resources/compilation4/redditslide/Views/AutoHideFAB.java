package me.ccrama.redditslide.Views;
/**
 * Created by ccrama on 7/20/2015.
 */
class AutoHideFAB extends android.support.design.widget.FloatingActionButton.Behavior {
    public AutoHideFAB(android.content.Context context, android.util.AttributeSet attributeSet) {
        super();
    }

    @java.lang.Override
    public void onNestedScroll(android.support.design.widget.CoordinatorLayout coordinatorLayout, android.support.design.widget.FloatingActionButton child, android.view.View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if ((dyConsumed > 0) && (child.getVisibility() == android.view.View.VISIBLE)) {
            child.hide();
        } else if ((dyConsumed < 0) && (child.getVisibility() == android.view.View.GONE)) {
            child.show();
        }
    }

    @java.lang.Override
    public boolean onStartNestedScroll(android.support.design.widget.CoordinatorLayout coordinatorLayout, android.support.design.widget.FloatingActionButton child, android.view.View directTargetChild, android.view.View target, int nestedScrollAxes) {
        return nestedScrollAxes == android.support.v4.view.ViewCompat.SCROLL_AXIS_VERTICAL;
    }
}