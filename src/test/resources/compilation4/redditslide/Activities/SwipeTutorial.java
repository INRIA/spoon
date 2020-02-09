package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.R;
/**
 * Created by ccrama on 3/5/2015.
 */
public class SwipeTutorial extends me.ccrama.redditslide.Activities.BaseActivity {
    @java.lang.Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(me.ccrama.redditslide.R.layout.swipe_tutorial);
        if (getIntent().hasExtra("subtitle")) {
            ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.subtitle))).setText(getIntent().getStringExtra("subtitle"));
        }
    }
}