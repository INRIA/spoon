package me.ccrama.redditslide.SwipeLayout.app;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.SwipeLayout.SwipeBackLayout;
import me.ccrama.redditslide.SwipeLayout.Utils;
/**
 *
 *
 * @author Yrom
 */
public class SwipeBackActivityHelper {
    private android.app.Activity mActivity;

    private me.ccrama.redditslide.SwipeLayout.SwipeBackLayout mSwipeBackLayout;

    public SwipeBackActivityHelper(android.app.Activity activity) {
        mActivity = activity;
    }

    @java.lang.SuppressWarnings("deprecation")
    public void onActivityCreate() {
        mActivity.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        mActivity.getWindow().getDecorView().setBackgroundDrawable(null);
        mSwipeBackLayout = ((me.ccrama.redditslide.SwipeLayout.SwipeBackLayout) (android.view.LayoutInflater.from(mActivity).inflate(me.ccrama.redditslide.R.layout.swipeback_layout, null)));
        mSwipeBackLayout.addSwipeListener(new me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.SwipeListener() {
            @java.lang.Override
            public void onScrollStateChange(int state, float scrollPercent) {
            }

            @java.lang.Override
            public void onEdgeTouch(int edgeFlag) {
                me.ccrama.redditslide.SwipeLayout.Utils.convertActivityToTranslucent(mActivity);
            }

            @java.lang.Override
            public void onScrollOverThreshold() {
            }
        });
    }

    public void onPostCreate() {
        mSwipeBackLayout.attachToActivity(mActivity);
    }

    public android.view.View findViewById(int id) {
        if (mSwipeBackLayout != null) {
            return mSwipeBackLayout.findViewById(id);
        }
        return null;
    }

    public me.ccrama.redditslide.SwipeLayout.SwipeBackLayout getSwipeBackLayout() {
        return mSwipeBackLayout;
    }
}