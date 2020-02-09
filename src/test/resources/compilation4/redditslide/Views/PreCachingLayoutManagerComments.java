package me.ccrama.redditslide.Views;
import me.ccrama.redditslide.util.LogUtil;
/**
 * Created by carlo_000 on 10/12/2015.
 */
public class PreCachingLayoutManagerComments extends android.support.v7.widget.LinearLayoutManager {
    private static final int DEFAULT_EXTRA_LAYOUT_SPACE = 900;

    private final android.content.Context context;

    private int extraLayoutSpace = 0;

    @java.lang.Override
    public void onLayoutChildren(android.support.v7.widget.RecyclerView.Recycler recycler, android.support.v7.widget.RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (java.lang.IndexOutOfBoundsException e) {
            me.ccrama.redditslide.util.LogUtil.v("Met a IOOBE in RecyclerView");
        }
    }

    public PreCachingLayoutManagerComments(android.content.Context context) {
        super(context);
        this.context = context;
    }

    public PreCachingLayoutManagerComments(android.content.Context context, int extraLayoutSpace) {
        super(context);
        this.context = context;
        this.extraLayoutSpace = extraLayoutSpace;
    }

    public PreCachingLayoutManagerComments(android.content.Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        this.context = context;
    }

    public void setExtraLayoutSpace(int extraLayoutSpace) {
        this.extraLayoutSpace = extraLayoutSpace;
    }

    @java.lang.Override
    protected int getExtraLayoutSpace(android.support.v7.widget.RecyclerView.State state) {
        /* if (extraLayoutSpace > 0) {
        return extraLayoutSpace;
        }
        return DEFAULT_EXTRA_LAYOUT_SPACE;
         */
        return super.getExtraLayoutSpace(state);
    }
}