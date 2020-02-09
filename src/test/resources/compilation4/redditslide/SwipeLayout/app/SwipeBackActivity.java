package me.ccrama.redditslide.SwipeLayout.app;
import me.ccrama.redditslide.SwipeLayout.SwipeBackLayout;
import me.ccrama.redditslide.SwipeLayout.Utils;
// * By ikew0ng
public class SwipeBackActivity extends android.support.v7.app.AppCompatActivity implements me.ccrama.redditslide.SwipeLayout.app.SwipeBackActivityBase {
    private me.ccrama.redditslide.SwipeLayout.app.SwipeBackActivityHelper mHelper;

    @java.lang.Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new me.ccrama.redditslide.SwipeLayout.app.SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
    }

    @java.lang.Override
    protected void onPostCreate(android.os.Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @java.lang.Override
    public android.view.View findViewById(int id) {
        android.view.View v = super.findViewById(id);
        if ((v == null) && (mHelper != null))
            return mHelper.findViewById(id);

        return v;
    }

    @java.lang.Override
    public me.ccrama.redditslide.SwipeLayout.SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @java.lang.Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @java.lang.Override
    public void scrollToFinishActivity() {
        me.ccrama.redditslide.SwipeLayout.Utils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }
}