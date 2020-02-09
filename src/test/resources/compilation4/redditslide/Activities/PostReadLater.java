package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.ColorPreferences;
import me.ccrama.redditslide.Fragments.ReadLaterView;
/**
 * Created by ccrama on 9/17/2015.
 */
public class PostReadLater extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    private android.support.v4.view.ViewPager pager;

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstance) {
        overrideSwipeFromAnywhere();
        super.onCreate(savedInstance);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_read_later);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, "Read later", true, true);
        mToolbar.setPopupTheme(new me.ccrama.redditslide.ColorPreferences(this).getFontStyle().getBaseId());
        pager = ((android.support.v4.view.ViewPager) (findViewById(me.ccrama.redditslide.R.id.content_view)));
        pager.setAdapter(new me.ccrama.redditslide.Activities.PostReadLater.ReadLaterAdaptor(getSupportFragmentManager()));
    }

    public class ReadLaterAdaptor extends android.support.v4.app.FragmentStatePagerAdapter {
        public ReadLaterAdaptor(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @java.lang.Override
        public android.support.v4.app.Fragment getItem(int i) {
            android.support.v4.app.Fragment f = new me.ccrama.redditslide.Fragments.ReadLaterView();
            return f;
        }

        @java.lang.Override
        public int getCount() {
            return 1;
        }
    }
}