package me.ccrama.redditslide.Views;
import me.ccrama.redditslide.util.LogUtil;
/**
 * Created by carlo_000 on 4/8/2016.
 */
public class CatchStaggeredGridLayoutManager extends android.support.v7.widget.StaggeredGridLayoutManager {
    public CatchStaggeredGridLayoutManager(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CatchStaggeredGridLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }

    @java.lang.Override
    public void onLayoutChildren(android.support.v7.widget.RecyclerView.Recycler recycler, android.support.v7.widget.RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (java.lang.IndexOutOfBoundsException e) {
            me.ccrama.redditslide.util.LogUtil.v("Met a IOOBE in RecyclerView");
        }
    }
}