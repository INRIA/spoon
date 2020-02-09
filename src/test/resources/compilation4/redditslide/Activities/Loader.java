/**
 * Created by carlo_000 on 1/20/2016.
 */
package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.R;
/**
 * Created by ccrama on 9/17/2015.
 */
public class Loader extends me.ccrama.redditslide.Activities.BaseActivity {
    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstance) {
        disableSwipeBackLayout();
        super.onCreate(savedInstance);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_loading);
        me.ccrama.redditslide.Activities.MainActivity.loader = this;
    }
}