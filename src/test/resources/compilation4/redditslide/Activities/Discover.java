package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.ColorPreferences;
import me.ccrama.redditslide.Fragments.SubredditListView;
/**
 * Created by ccrama on 9/17/2015.
 */
public class Discover extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    public me.ccrama.redditslide.Activities.Discover.OverviewPagerAdapter adapter;

    @java.lang.Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        android.view.MenuInflater inflater = getMenuInflater();
        inflater.inflate(me.ccrama.redditslide.R.menu.menu_discover, menu);
        return true;
    }

    @java.lang.Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                onBackPressed();
                return true;
            case me.ccrama.redditslide.R.id.search :
                {
                    new com.afollestad.materialdialogs.MaterialDialog.Builder(this).alwaysCallInputCallback().inputType(android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS).inputRange(3, 100).input(getString(me.ccrama.redditslide.R.string.discover_search), null, new com.afollestad.materialdialogs.MaterialDialog.InputCallback() {
                        @java.lang.Override
                        public void onInput(com.afollestad.materialdialogs.MaterialDialog dialog, java.lang.CharSequence input) {
                            if (input.length() >= 3) {
                                dialog.getActionButton(com.afollestad.materialdialogs.DialogAction.POSITIVE).setEnabled(true);
                            } else {
                                dialog.getActionButton(com.afollestad.materialdialogs.DialogAction.POSITIVE).setEnabled(false);
                            }
                        }
                    }).positiveText(me.ccrama.redditslide.R.string.search_all).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                        @java.lang.Override
                        public void onClick(@android.support.annotation.NonNull
                        com.afollestad.materialdialogs.MaterialDialog dialog, @android.support.annotation.NonNull
                        com.afollestad.materialdialogs.DialogAction which) {
                            android.content.Intent inte = new android.content.Intent(me.ccrama.redditslide.Activities.Discover.this, me.ccrama.redditslide.Activities.SubredditSearch.class);
                            inte.putExtra("term", dialog.getInputEditText().getText().toString());
                            me.ccrama.redditslide.Activities.Discover.this.startActivity(inte);
                        }
                    }).negativeText(me.ccrama.redditslide.R.string.btn_cancel).show();
                }
                return true;
            default :
                return false;
        }
    }

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstance) {
        overrideSwipeFromAnywhere();
        super.onCreate(savedInstance);
        applyColorTheme("");
        setContentView(me.ccrama.redditslide.R.layout.activity_multireddits);
        ((android.support.v4.widget.DrawerLayout) (findViewById(me.ccrama.redditslide.R.id.drawer_layout))).setDrawerLockMode(android.support.v4.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.discover_title, true, false);
        mToolbar.setPopupTheme(new me.ccrama.redditslide.ColorPreferences(this).getFontStyle().getBaseId());
        findViewById(me.ccrama.redditslide.R.id.header).setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getDefaultColor());
        android.support.design.widget.TabLayout tabs = ((android.support.design.widget.TabLayout) (findViewById(me.ccrama.redditslide.R.id.sliding_tabs)));
        tabs.setTabMode(android.support.design.widget.TabLayout.MODE_FIXED);
        tabs.setSelectedTabIndicatorColor(new me.ccrama.redditslide.ColorPreferences(this).getColor("no sub"));
        android.support.v4.view.ViewPager pager = ((android.support.v4.view.ViewPager) (findViewById(me.ccrama.redditslide.R.id.content_view)));
        pager.setAdapter(new me.ccrama.redditslide.Activities.Discover.OverviewPagerAdapter(getSupportFragmentManager()));
        tabs.setupWithViewPager(pager);
        pager.setOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {
            @java.lang.Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @java.lang.Override
            public void onPageSelected(int position) {
                findViewById(me.ccrama.redditslide.R.id.header).animate().translationY(0).setInterpolator(new android.view.animation.LinearInterpolator()).setDuration(180);
            }

            @java.lang.Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public class OverviewPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
        public OverviewPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @java.lang.Override
        public android.support.v4.app.Fragment getItem(int i) {
            android.support.v4.app.Fragment f = new me.ccrama.redditslide.Fragments.SubredditListView();
            android.os.Bundle args = new android.os.Bundle();
            args.putString("id", i == 1 ? "trending" : "popular");
            f.setArguments(args);
            return f;
        }

        @java.lang.Override
        public int getCount() {
            return 2;
        }

        @java.lang.Override
        public java.lang.CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getString(me.ccrama.redditslide.R.string.discover_popular);
            } else {
                return getString(me.ccrama.redditslide.R.string.discover_trending);
            }
        }
    }
}