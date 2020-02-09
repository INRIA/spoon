package me.ccrama.redditslide.Activities;
import java.util.Locale;
import me.ccrama.redditslide.Fragments.BlankFragment;
import me.ccrama.redditslide.Fragments.CommentPage;
import java.util.ArrayList;
import me.ccrama.redditslide.Adapters.SubmissionDisplay;
import me.ccrama.redditslide.*;
import java.util.List;
import me.ccrama.redditslide.Adapters.MultiredditPosts;
import me.ccrama.redditslide.Adapters.SubredditPosts;
/**
 * This activity is responsible for the view when clicking on a post, showing the post and its
 * comments underneath with the slide left/right for the next post.
 * <p/>
 * When the end of the currently loaded posts is being reached, more posts are loaded asynchronously
 * in {@link OverviewPagerAdapter}.
 * <p/>
 * Comments are displayed in the {@link CommentPage} fragment.
 * <p/>
 * Created by ccrama on 9/17/2015.
 */
public class CommentsScreen extends me.ccrama.redditslide.Activities.BaseActivityAnim implements me.ccrama.redditslide.Adapters.SubmissionDisplay {
    public static final java.lang.String EXTRA_PROFILE = "profile";

    public static final java.lang.String EXTRA_PAGE = "page";

    public static final java.lang.String EXTRA_SUBREDDIT = "subreddit";

    public static final java.lang.String EXTRA_MULTIREDDIT = "multireddit";

    public java.util.ArrayList<net.dean.jraw.models.Submission> currentPosts;

    public me.ccrama.redditslide.PostLoader subredditPosts;

    int firstPage;

    me.ccrama.redditslide.Activities.CommentsScreen.OverviewPagerAdapter comments;

    private java.lang.String subreddit;

    private java.lang.String baseSubreddit;

    java.lang.String multireddit;

    java.lang.String profile;

    @java.lang.Override
    public boolean dispatchKeyEvent(android.view.KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (me.ccrama.redditslide.SettingValues.commentVolumeNav) {
            switch (keyCode) {
                case android.view.KeyEvent.KEYCODE_VOLUME_UP :
                    return ((me.ccrama.redditslide.Fragments.CommentPage) (comments.getCurrentFragment())).onKeyDown(keyCode, event);
                case android.view.KeyEvent.KEYCODE_VOLUME_DOWN :
                    return ((me.ccrama.redditslide.Fragments.CommentPage) (comments.getCurrentFragment())).onKeyDown(keyCode, event);
                case android.view.KeyEvent.KEYCODE_SEARCH :
                    return ((me.ccrama.redditslide.Fragments.CommentPage) (comments.getCurrentFragment())).onKeyDown(keyCode, event);
                default :
                    return super.dispatchKeyEvent(event);
            }
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    @java.lang.Override
    public void onPause() {
        super.onPause();
        android.view.inputmethod.InputMethodManager imm = ((android.view.inputmethod.InputMethodManager) (getSystemService(me.ccrama.redditslide.Activities.BaseActivityAnim.INPUT_METHOD_SERVICE)));
        imm.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), 0);
    }

    @java.lang.Override
    public void onDestroy() {
        super.onDestroy();
        if (!me.ccrama.redditslide.Reddit.appRestart.contains("tutorialSwipeComment")) {
            me.ccrama.redditslide.Reddit.appRestart.edit().putBoolean("tutorialSwipeComment", true).apply();
        } else if (!me.ccrama.redditslide.Reddit.appRestart.contains("tutorial_comm")) {
            me.ccrama.redditslide.Reddit.appRestart.edit().putBoolean("tutorial_comm", true).apply();
        }
    }

    @java.lang.Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 14) {
            comments.notifyDataSetChanged();
            // todo make this work
        }
        if (requestCode == 333) {
            me.ccrama.redditslide.Reddit.appRestart.edit().putBoolean("tutorialSwipeComments", true).apply();
        }
    }

    public int currentPage;

    public java.util.ArrayList<java.lang.Integer> seen;

    public int adjustAlpha(float factor) {
        int alpha = java.lang.Math.round(android.graphics.Color.alpha(android.graphics.Color.BLACK) * factor);
        int red = android.graphics.Color.red(android.graphics.Color.BLACK);
        int green = android.graphics.Color.green(android.graphics.Color.BLACK);
        int blue = android.graphics.Color.blue(android.graphics.Color.BLACK);
        return android.graphics.Color.argb(alpha, red, green, blue);
    }

    public boolean popup;

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstance) {
        popup = (me.ccrama.redditslide.SettingValues.isPro && (getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE)) && (!me.ccrama.redditslide.SettingValues.fullCommentOverride);
        seen = new java.util.ArrayList<>();
        if (popup) {
            disableSwipeBackLayout();
            applyColorTheme();
            setTheme(me.ccrama.redditslide.R.style.popup);
            supportRequestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            super.onCreate(savedInstance);
            setContentView(me.ccrama.redditslide.R.layout.activity_slide_popup);
        } else {
            overrideSwipeFromAnywhere();
            applyColorTheme();
            getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
            getWindow().getDecorView().setBackgroundDrawable(null);
            super.onCreate(savedInstance);
            setContentView(me.ccrama.redditslide.R.layout.activity_slide);
        }
        me.ccrama.redditslide.Reddit.setDefaultErrorHandler(this);
        firstPage = getIntent().getExtras().getInt(me.ccrama.redditslide.Activities.CommentsScreen.EXTRA_PAGE, -1);
        baseSubreddit = getIntent().getExtras().getString(me.ccrama.redditslide.Activities.CommentsScreen.EXTRA_SUBREDDIT);
        subreddit = baseSubreddit;
        multireddit = getIntent().getExtras().getString(me.ccrama.redditslide.Activities.CommentsScreen.EXTRA_MULTIREDDIT);
        profile = getIntent().getExtras().getString(me.ccrama.redditslide.Activities.CommentsScreen.EXTRA_PROFILE, "");
        currentPosts = new java.util.ArrayList<>();
        if (multireddit != null) {
            subredditPosts = new me.ccrama.redditslide.Adapters.MultiredditPosts(multireddit, profile);
        } else {
            baseSubreddit = subreddit.toLowerCase(java.util.Locale.ENGLISH);
            subredditPosts = new me.ccrama.redditslide.Adapters.SubredditPosts(baseSubreddit, this);
        }
        if ((firstPage == android.support.v7.widget.RecyclerView.NO_POSITION) || (firstPage < 0)) {
            firstPage = 0;
            // IS SINGLE POST
        } else {
            me.ccrama.redditslide.OfflineSubreddit o = me.ccrama.redditslide.OfflineSubreddit.getSubreddit(multireddit == null ? baseSubreddit : "multi" + multireddit, me.ccrama.redditslide.OfflineSubreddit.currentid, !me.ccrama.redditslide.Authentication.didOnline, this);
            subredditPosts.getPosts().addAll(o.submissions);
            currentPosts.addAll(subredditPosts.getPosts());
        }
        if (getIntent().hasExtra("fullname")) {
            java.lang.String fullname = getIntent().getStringExtra("fullname");
            for (int i = 0; i < currentPosts.size(); i++) {
                if (currentPosts.get(i).getFullName().equals(fullname)) {
                    if (i != firstPage)
                        firstPage = i;

                    break;
                }
            }
        }
        if (((currentPosts.isEmpty() || (currentPosts.size() < firstPage)) || (currentPosts.get(firstPage) == null)) || (firstPage < 0)) {
            finish();
        } else {
            updateSubredditAndSubmission(currentPosts.get(firstPage));
            final android.support.v4.view.ViewPager pager = ((android.support.v4.view.ViewPager) (findViewById(me.ccrama.redditslide.R.id.content_view)));
            comments = new me.ccrama.redditslide.Activities.CommentsScreen.OverviewPagerAdapter(getSupportFragmentManager());
            pager.setAdapter(comments);
            currentPage = firstPage;
            pager.setCurrentItem(firstPage + 1);
            pager.addOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {
                @java.lang.Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    if ((position <= firstPage) && (positionOffsetPixels == 0)) {
                        finish();
                    }
                    if ((position == firstPage) && (!popup)) {
                        if (((me.ccrama.redditslide.Activities.CommentsScreen.OverviewPagerAdapter) (pager.getAdapter())).blankPage != null) {
                            ((me.ccrama.redditslide.Activities.CommentsScreen.OverviewPagerAdapter) (pager.getAdapter())).blankPage.doOffset(positionOffset);
                        }
                        pager.setBackgroundColor(adjustAlpha(positionOffset * 0.7F));
                    }
                }

                @java.lang.Override
                public void onPageSelected(int position) {
                    if ((position != firstPage) && (position < currentPosts.size())) {
                        position = position - 1;
                        if (position < 0)
                            position = 0;

                        updateSubredditAndSubmission(currentPosts.get(position));
                        if (((currentPosts.size() - 2) <= position) && subredditPosts.hasMore()) {
                            subredditPosts.loadMore(me.ccrama.redditslide.Activities.CommentsScreen.this.getApplicationContext(), me.ccrama.redditslide.Activities.CommentsScreen.this, false);
                        }
                        currentPage = position;
                        seen.add(position);
                        android.os.Bundle conData = new android.os.Bundle();
                        conData.putIntegerArrayList("seen", seen);
                        conData.putInt("lastPage", position);
                        android.content.Intent intent = new android.content.Intent();
                        intent.putExtras(conData);
                        setResult(android.app.Activity.RESULT_OK, intent);
                    }
                }

                @java.lang.Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }
        if (!me.ccrama.redditslide.Reddit.appRestart.contains("tutorialSwipeComments")) {
            android.content.Intent i = new android.content.Intent(this, me.ccrama.redditslide.Activities.SwipeTutorial.class);
            i.putExtra("subtitle", "Swipe from the left edge to exit comments.\n\nYou can swipe in the middle to get to the previous/next submission.");
            startActivityForResult(i, 333);
        }
    }

    private void updateSubredditAndSubmission(net.dean.jraw.models.Submission post) {
        subreddit = post.getSubredditName();
        if (post.getSubredditName() == null) {
            subreddit = "Promoted";
        }
        themeSystemBars(subreddit);
        setRecentBar(subreddit);
    }

    @java.lang.Override
    public void updateSuccess(final java.util.List<net.dean.jraw.models.Submission> submissions, final int startIndex) {
        if (me.ccrama.redditslide.SettingValues.storeHistory)
            me.ccrama.redditslide.LastComments.setCommentsSince(submissions);

        currentPosts.clear();
        currentPosts.addAll(submissions);
        runOnUiThread(new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                if (startIndex != (-1)) {
                    // TODO determine correct behaviour
                    // comments.notifyItemRangeInserted(startIndex, posts.posts.size());
                    comments.notifyDataSetChanged();
                } else {
                    comments.notifyDataSetChanged();
                }
            }
        });
    }

    @java.lang.Override
    public void updateOffline(java.util.List<net.dean.jraw.models.Submission> submissions, final long cacheTime) {
        runOnUiThread(new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                comments.notifyDataSetChanged();
            }
        });
    }

    @java.lang.Override
    public void updateOfflineError() {
    }

    @java.lang.Override
    public void updateError() {
    }

    @java.lang.Override
    public void updateViews() {
    }

    @java.lang.Override
    public void onAdapterUpdated() {
        comments.notifyDataSetChanged();
    }

    public class OverviewPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
        private me.ccrama.redditslide.Fragments.CommentPage mCurrentFragment;

        public me.ccrama.redditslide.Fragments.BlankFragment blankPage;

        public OverviewPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        public android.support.v4.app.Fragment getCurrentFragment() {
            return mCurrentFragment;
        }

        @java.lang.Override
        public void setPrimaryItem(android.view.ViewGroup container, int position, java.lang.Object object) {
            super.setPrimaryItem(container, position, object);
            if (((getCurrentFragment() != object) && (object != null)) && (object instanceof me.ccrama.redditslide.Fragments.CommentPage)) {
                mCurrentFragment = ((me.ccrama.redditslide.Fragments.CommentPage) (object));
                if ((!mCurrentFragment.loaded) && mCurrentFragment.isAdded()) {
                    mCurrentFragment.doAdapter(true);
                }
            }
        }

        @java.lang.Override
        public android.support.v4.app.Fragment getItem(int i) {
            if ((i <= firstPage) || (i == 0)) {
                blankPage = new me.ccrama.redditslide.Fragments.BlankFragment();
                return blankPage;
            } else {
                i = i - 1;
                android.support.v4.app.Fragment f = new me.ccrama.redditslide.Fragments.CommentPage();
                android.os.Bundle args = new android.os.Bundle();
                java.lang.String name = currentPosts.get(i).getFullName();
                args.putString("id", name.substring(3, name.length()));
                args.putBoolean("archived", currentPosts.get(i).isArchived());
                args.putBoolean("contest", currentPosts.get(i).getDataNode().get("contest_mode").asBoolean());
                args.putBoolean("locked", currentPosts.get(i).isLocked());
                args.putInt("page", i);
                args.putString("subreddit", currentPosts.get(i).getSubredditName());
                args.putString("baseSubreddit", multireddit == null ? baseSubreddit : "multi" + multireddit);
                f.setArguments(args);
                return f;
            }
        }

        @java.lang.Override
        public int getCount() {
            return currentPosts.size() + 1;
        }
    }
}