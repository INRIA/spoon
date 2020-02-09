package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Visuals.Palette;
/**
 * Created by tomer aka rosenpin on 11/27/15.
 *
 * This Activity allows for fullscreen viewing without the statusbar visible
 */
public class FullScreenActivity extends me.ccrama.redditslide.Activities.BaseActivity {
    @java.lang.Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        // TODO something like this getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
        // WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (me.ccrama.redditslide.Reddit.peek) {
            overridePendingTransition(me.ccrama.redditslide.R.anim.pop_in, 0);
        } else {
            overridePendingTransition(me.ccrama.redditslide.R.anim.slide_in, 0);
        }
        setRecentBar(null, me.ccrama.redditslide.Visuals.Palette.getDefaultColor());
    }

    @java.lang.Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, me.ccrama.redditslide.R.anim.slide_out);
    }

    @java.lang.Override
    public void onPostCreate(android.os.Bundle savedInstanceState) {
        try {
            findViewById(android.R.id.content).getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {
                @java.lang.Override
                public void onGlobalLayout() {
                    // Blurry.with(FullScreenActivity.this).radius(2).sampling(5).animate().color(Color.parseColor("#99000000")).onto((ViewGroup) findViewById(android.R.id.content));
                }
            });
        } catch (java.lang.Exception e) {
        }
        super.onPostCreate(savedInstanceState);
    }
}