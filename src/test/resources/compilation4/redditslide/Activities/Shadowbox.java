package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.ContentType;
import me.ccrama.redditslide.PostLoader;
import me.ccrama.redditslide.Fragments.MediaFragment;
import me.ccrama.redditslide.Adapters.SubmissionDisplay;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Adapters.MultiredditPosts;
import me.ccrama.redditslide.OfflineSubreddit;
import me.ccrama.redditslide.Fragments.SelftextFull;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.HasSeen;
import me.ccrama.redditslide.Fragments.AlbumFull;
import me.ccrama.redditslide.Fragments.TitleFull;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.Fragments.TumblrFull;
import java.util.List;
import me.ccrama.redditslide.LastComments;
import me.ccrama.redditslide.Adapters.SubredditPosts;
/**
 * Created by ccrama on 9/17/2015.
 */
public class Shadowbox extends me.ccrama.redditslide.Activities.FullScreenActivity implements me.ccrama.redditslide.Adapters.SubmissionDisplay {
    public static final java.lang.String EXTRA_PROFILE = "profile";

    public static final java.lang.String EXTRA_PAGE = "page";

    public static final java.lang.String EXTRA_SUBREDDIT = "subreddit";

    public static final java.lang.String EXTRA_MULTIREDDIT = "multireddit";

    public me.ccrama.redditslide.PostLoader subredditPosts;

    public java.lang.String subreddit;

    int firstPage;

    private int count;

    public android.support.v4.view.ViewPager pager;

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstance) {
        overrideSwipeFromAnywhere();
        subreddit = getIntent().getExtras().getString(me.ccrama.redditslide.Activities.Shadowbox.EXTRA_SUBREDDIT);
        firstPage = getIntent().getExtras().getInt(me.ccrama.redditslide.Activities.Shadowbox.EXTRA_PAGE, 0);
        subreddit = getIntent().getExtras().getString(me.ccrama.redditslide.Activities.Shadowbox.EXTRA_SUBREDDIT);
        java.lang.String multireddit = getIntent().getExtras().getString(me.ccrama.redditslide.Activities.Shadowbox.EXTRA_MULTIREDDIT);
        java.lang.String profile = getIntent().getExtras().getString(me.ccrama.redditslide.Activities.Shadowbox.EXTRA_PROFILE, "");
        if (multireddit != null) {
            subredditPosts = new me.ccrama.redditslide.Adapters.MultiredditPosts(multireddit, profile);
        } else {
            subredditPosts = new me.ccrama.redditslide.Adapters.SubredditPosts(subreddit, this);
        }
        subreddit = (multireddit == null) ? subreddit : "multi" + multireddit;
        if (multireddit == null) {
            setShareUrl("https://reddit.com/r/" + subreddit);
        }
        applyDarkColorTheme(subreddit);
        super.onCreate(savedInstance);
        setContentView(me.ccrama.redditslide.R.layout.activity_slide);
        long offline = getIntent().getLongExtra("offline", 0L);
        me.ccrama.redditslide.OfflineSubreddit submissions = me.ccrama.redditslide.OfflineSubreddit.getSubreddit(subreddit, offline, !me.ccrama.redditslide.Authentication.didOnline, this);
        subredditPosts.getPosts().addAll(submissions.submissions);
        count = subredditPosts.getPosts().size();
        pager = ((android.support.v4.view.ViewPager) (findViewById(me.ccrama.redditslide.R.id.content_view)));
        submissionsPager = new me.ccrama.redditslide.Activities.Shadowbox.OverviewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(submissionsPager);
        pager.setCurrentItem(firstPage);
        pager.setOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {
            @java.lang.Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @java.lang.Override
            public void onPageSelected(int position) {
                if (me.ccrama.redditslide.SettingValues.storeHistory) {
                    if (subredditPosts.getPosts().get(position).isNsfw() && (!me.ccrama.redditslide.SettingValues.storeNSFWHistory)) {
                    } else
                        me.ccrama.redditslide.HasSeen.addSeen(subredditPosts.getPosts().get(position).getFullName());

                }
            }

            @java.lang.Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    me.ccrama.redditslide.Activities.Shadowbox.OverviewPagerAdapter submissionsPager;

    @java.lang.Override
    public void updateSuccess(final java.util.List<net.dean.jraw.models.Submission> submissions, final int startIndex) {
        if (me.ccrama.redditslide.SettingValues.storeHistory)
            me.ccrama.redditslide.LastComments.setCommentsSince(submissions);

        runOnUiThread(new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                count = subredditPosts.getPosts().size();
                if (startIndex != (-1)) {
                    // TODO determine correct behaviour
                    // comments.notifyItemRangeInserted(startIndex, posts.posts.size());
                    submissionsPager.notifyDataSetChanged();
                } else {
                    submissionsPager.notifyDataSetChanged();
                }
            }
        });
    }

    @java.lang.Override
    public void updateOffline(java.util.List<net.dean.jraw.models.Submission> submissions, final long cacheTime) {
        runOnUiThread(new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                count = subredditPosts.getPosts().size();
                submissionsPager.notifyDataSetChanged();
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
        submissionsPager.notifyDataSetChanged();
    }

    public class OverviewPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
        public OverviewPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @java.lang.Override
        public android.support.v4.app.Fragment getItem(int i) {
            android.support.v4.app.Fragment f = null;
            me.ccrama.redditslide.ContentType.Type t = me.ccrama.redditslide.ContentType.getContentType(subredditPosts.getPosts().get(i));
            if (((subredditPosts.getPosts().size() - 2) <= i) && subredditPosts.hasMore()) {
                subredditPosts.loadMore(me.ccrama.redditslide.Activities.Shadowbox.this.getApplicationContext(), me.ccrama.redditslide.Activities.Shadowbox.this, false);
            }
            switch (t) {
                case GIF :
                case IMAGE :
                case IMGUR :
                case REDDIT :
                case EXTERNAL :
                case SPOILER :
                case DEVIANTART :
                case EMBEDDED :
                case XKCD :
                case VREDDIT_DIRECT :
                case VREDDIT_REDIRECT :
                case LINK :
                case VID_ME :
                case STREAMABLE :
                case VIDEO :
                    {
                        f = new me.ccrama.redditslide.Fragments.MediaFragment();
                        android.os.Bundle args = new android.os.Bundle();
                        net.dean.jraw.models.Submission submission = subredditPosts.getPosts().get(i);
                        java.lang.String previewUrl = "";
                        if (((t != me.ccrama.redditslide.ContentType.Type.XKCD) && submission.getDataNode().has("preview")) && submission.getDataNode().get("preview").get("images").get(0).get("source").has("height")) {
                            // Load the preview image which has probably already been cached in memory instead of the direct link
                            previewUrl = submission.getDataNode().get("preview").get("images").get(0).get("source").get("url").asText();
                        }
                        args.putString("contentUrl", submission.getUrl());
                        args.putString("firstUrl", previewUrl);
                        args.putInt("page", i);
                        args.putString("sub", subreddit);
                        f.setArguments(args);
                    }
                    break;
                case SELF :
                    {
                        if (subredditPosts.getPosts().get(i).getSelftext().isEmpty()) {
                            f = new me.ccrama.redditslide.Fragments.TitleFull();
                            android.os.Bundle args = new android.os.Bundle();
                            args.putInt("page", i);
                            args.putString("sub", subreddit);
                            f.setArguments(args);
                        } else {
                            f = new me.ccrama.redditslide.Fragments.SelftextFull();
                            android.os.Bundle args = new android.os.Bundle();
                            args.putInt("page", i);
                            args.putString("sub", subreddit);
                            f.setArguments(args);
                        }
                    }
                    break;
                case TUMBLR :
                    {
                        f = new me.ccrama.redditslide.Fragments.TumblrFull();
                        android.os.Bundle args = new android.os.Bundle();
                        args.putInt("page", i);
                        args.putString("sub", subreddit);
                        f.setArguments(args);
                    }
                    break;
                case ALBUM :
                    {
                        f = new me.ccrama.redditslide.Fragments.AlbumFull();
                        android.os.Bundle args = new android.os.Bundle();
                        args.putInt("page", i);
                        args.putString("sub", subreddit);
                        f.setArguments(args);
                    }
                    break;
                case NONE :
                    {
                        if (subredditPosts.getPosts().get(i).getSelftext().isEmpty()) {
                            f = new me.ccrama.redditslide.Fragments.TitleFull();
                            android.os.Bundle args = new android.os.Bundle();
                            args.putInt("page", i);
                            args.putString("sub", subreddit);
                            f.setArguments(args);
                        } else {
                            f = new me.ccrama.redditslide.Fragments.SelftextFull();
                            android.os.Bundle args = new android.os.Bundle();
                            args.putInt("page", i);
                            args.putString("sub", subreddit);
                            f.setArguments(args);
                        }
                    }
                    break;
            }
            return f;
        }

        @java.lang.Override
        public int getCount() {
            return count;
        }
    }
}