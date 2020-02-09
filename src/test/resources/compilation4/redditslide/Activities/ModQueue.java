package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.Fragments.ModPage;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Fragments.InboxPage;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.ColorPreferences;
import me.ccrama.redditslide.UserSubscriptions;
import me.ccrama.redditslide.Fragments.ModLog;
/**
 * Created by ccrama on 9/17/2015.
 */
public class ModQueue extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    public me.ccrama.redditslide.Activities.ModQueue.OverviewPagerAdapter adapter;

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstance) {
        overrideSwipeFromAnywhere();
        super.onCreate(savedInstance);
        applyColorTheme("");
        setContentView(me.ccrama.redditslide.R.layout.activity_inbox);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.drawer_moderation, true, true);
        android.support.design.widget.TabLayout tabs = ((android.support.design.widget.TabLayout) (findViewById(me.ccrama.redditslide.R.id.sliding_tabs)));
        tabs.setTabMode(android.support.design.widget.TabLayout.MODE_SCROLLABLE);
        tabs.setSelectedTabIndicatorColor(new me.ccrama.redditslide.ColorPreferences(this).getColor("no sub"));
        final android.view.View header = findViewById(me.ccrama.redditslide.R.id.header);
        android.support.v4.view.ViewPager pager = ((android.support.v4.view.ViewPager) (findViewById(me.ccrama.redditslide.R.id.content_view)));
        pager.setOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {
            @java.lang.Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @java.lang.Override
            public void onPageSelected(int position) {
                header.animate().translationY(0).setInterpolator(new android.view.animation.LinearInterpolator()).setDuration(180);
            }

            @java.lang.Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        findViewById(me.ccrama.redditslide.R.id.header).setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getDefaultColor());
        pager.setAdapter(new me.ccrama.redditslide.Activities.ModQueue.OverviewPagerAdapter(getSupportFragmentManager()));
        tabs.setupWithViewPager(pager);
    }

    public class OverviewPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
        private android.support.v4.app.Fragment mCurrentFragment;

        public OverviewPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        public android.support.v4.app.Fragment getCurrentFragment() {
            return mCurrentFragment;
        }

        @java.lang.Override
        public void setPrimaryItem(android.view.ViewGroup container, int position, java.lang.Object object) {
            if (getCurrentFragment() != object) {
                mCurrentFragment = ((android.support.v4.app.Fragment) (object));
            }
            super.setPrimaryItem(container, position, object);
        }

        @java.lang.Override
        public android.support.v4.app.Fragment getItem(int i) {
            if (i == 1) {
                android.support.v4.app.Fragment f = new me.ccrama.redditslide.Fragments.InboxPage();
                android.os.Bundle args = new android.os.Bundle();
                args.putString("id", "moderator");
                f.setArguments(args);
                return f;
            } else if (i == 0) {
                android.support.v4.app.Fragment f = new me.ccrama.redditslide.Fragments.InboxPage();
                android.os.Bundle args = new android.os.Bundle();
                args.putString("id", "moderator/unread");
                f.setArguments(args);
                return f;
            } else if (i == 3) {
                android.support.v4.app.Fragment f = new me.ccrama.redditslide.Fragments.ModPage();
                android.os.Bundle args = new android.os.Bundle();
                args.putString("id", "unmoderated");
                args.putString("subreddit", "mod");
                f.setArguments(args);
                return f;
            } else if (i == 4) {
                android.support.v4.app.Fragment f = new me.ccrama.redditslide.Fragments.ModLog();
                android.os.Bundle args = new android.os.Bundle();
                f.setArguments(args);
                return f;
            } else if (i == 2) {
                android.support.v4.app.Fragment f = new me.ccrama.redditslide.Fragments.ModPage();
                android.os.Bundle args = new android.os.Bundle();
                args.putString("id", "modqueue");
                args.putString("subreddit", "mod");
                f.setArguments(args);
                return f;
            } else {
                android.support.v4.app.Fragment f = new me.ccrama.redditslide.Fragments.ModPage();
                android.os.Bundle args = new android.os.Bundle();
                args.putString("id", "modqueue");
                args.putString("subreddit", me.ccrama.redditslide.UserSubscriptions.modOf.get(i - 5));
                f.setArguments(args);
                return f;
            }
        }

        @java.lang.Override
        public int getCount() {
            return me.ccrama.redditslide.UserSubscriptions.modOf == null ? 2 : me.ccrama.redditslide.UserSubscriptions.modOf.size() + 5;
        }

        @java.lang.Override
        public java.lang.CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getString(me.ccrama.redditslide.R.string.mod_mail_unread);
            } else if (position == 1) {
                return getString(me.ccrama.redditslide.R.string.mod_mail);
            } else if (position == 2) {
                return getString(me.ccrama.redditslide.R.string.mod_modqueue);
            } else if (position == 3) {
                return getString(me.ccrama.redditslide.R.string.mod_unmoderated);
            } else if (position == 4) {
                return getString(me.ccrama.redditslide.R.string.mod_log);
            } else {
                return me.ccrama.redditslide.UserSubscriptions.modOf.get(position - 5);
            }
        }
    }
}