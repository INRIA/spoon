package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.SwipeLayout.app.SwipeBackActivityBase;
/**
 * Used as the base if an enter or exit animation is required (if the user can swipe out of the
 * activity)
 */
public class BaseActivityAnim extends me.ccrama.redditslide.Activities.BaseActivity implements me.ccrama.redditslide.SwipeLayout.app.SwipeBackActivityBase {
    @java.lang.Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, me.ccrama.redditslide.R.anim.slide_out);
    }

    @java.lang.Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (me.ccrama.redditslide.Reddit.peek) {
            overridePendingTransition(me.ccrama.redditslide.R.anim.pop_in, 0);
        } else {
            overridePendingTransition(me.ccrama.redditslide.R.anim.slide_in, 0);
        }
    }
}