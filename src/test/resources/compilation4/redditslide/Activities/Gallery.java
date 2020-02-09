package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager;
import me.ccrama.redditslide.ContentType;
import java.util.ArrayList;
import me.ccrama.redditslide.PostLoader;
import me.ccrama.redditslide.Fragments.MediaFragment;
import me.ccrama.redditslide.Adapters.SubmissionDisplay;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Adapters.MultiredditPosts;
import me.ccrama.redditslide.OfflineSubreddit;
import me.ccrama.redditslide.Fragments.SelftextFull;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Fragments.AlbumFull;
import me.ccrama.redditslide.Adapters.GalleryView;
import me.ccrama.redditslide.Fragments.TitleFull;
import me.ccrama.redditslide.Authentication;
import java.util.List;
import me.ccrama.redditslide.Adapters.SubredditPosts;
/**
 * Created by ccrama on 9/17/2015.
 */
public class Gallery extends me.ccrama.redditslide.Activities.FullScreenActivity implements me.ccrama.redditslide.Adapters.SubmissionDisplay {
    public static final java.lang.String EXTRA_PROFILE = "profile";

    public static final java.lang.String EXTRA_PAGE = "page";

    public static final java.lang.String EXTRA_SUBREDDIT = "subreddit";

    public static final java.lang.String EXTRA_MULTIREDDIT = "multireddit";

    public me.ccrama.redditslide.PostLoader subredditPosts;

    public java.lang.String subreddit;

    public java.util.ArrayList<net.dean.jraw.models.Submission> baseSubs;

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstance) {
        overrideSwipeFromAnywhere();
        subreddit = getIntent().getExtras().getString(me.ccrama.redditslide.Activities.Gallery.EXTRA_SUBREDDIT);
        java.lang.String multireddit = getIntent().getExtras().getString(me.ccrama.redditslide.Activities.Gallery.EXTRA_MULTIREDDIT);
        java.lang.String profile = getIntent().getExtras().getString(me.ccrama.redditslide.Activities.Gallery.EXTRA_PROFILE, "");
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
        setContentView(me.ccrama.redditslide.R.layout.gallery);
        getWindow().getDecorView().setSystemUiVisibility(((((android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE | android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION) | android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) | android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) | android.view.View.SYSTEM_UI_FLAG_FULLSCREEN) | android.view.View.SYSTEM_UI_FLAG_IMMERSIVE);
        long offline = getIntent().getLongExtra("offline", 0L);
        final me.ccrama.redditslide.OfflineSubreddit submissions = me.ccrama.redditslide.OfflineSubreddit.getSubreddit(subreddit, offline, !me.ccrama.redditslide.Authentication.didOnline, this);
        baseSubs = new java.util.ArrayList<>();
        for (net.dean.jraw.models.Submission s : submissions.submissions) {
            if ((s.getThumbnails() != null) && (s.getThumbnails().getSource() != null)) {
                baseSubs.add(s);
            } else if (me.ccrama.redditslide.ContentType.getContentType(s) == me.ccrama.redditslide.ContentType.Type.IMAGE) {
                baseSubs.add(s);
            }
            subredditPosts.getPosts().add(s);
        }
        rv = ((android.support.v7.widget.RecyclerView) (findViewById(me.ccrama.redditslide.R.id.content_view)));
        recyclerAdapter = new me.ccrama.redditslide.Adapters.GalleryView(this, baseSubs, subreddit);
        android.support.v7.widget.RecyclerView.LayoutManager layoutManager = createLayoutManager(getNumColumns(getResources().getConfiguration().orientation));
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(recyclerAdapter);
        rv.addOnScrollListener(new android.support.v7.widget.RecyclerView.OnScrollListener() {
            @java.lang.Override
            public void onScrolled(android.support.v7.widget.RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int[] firstVisibleItems;
                firstVisibleItems = ((me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager) (rv.getLayoutManager())).findFirstVisibleItemPositions(null);
                if ((firstVisibleItems != null) && (firstVisibleItems.length > 0)) {
                    for (int firstVisibleItem : firstVisibleItems) {
                        pastVisiblesItems = firstVisibleItem;
                    }
                }
                if (((visibleItemCount + pastVisiblesItems) + 5) >= totalItemCount) {
                    if (subredditPosts instanceof me.ccrama.redditslide.Adapters.SubredditPosts) {
                        if (!((me.ccrama.redditslide.Adapters.SubredditPosts) (subredditPosts)).loading) {
                            ((me.ccrama.redditslide.Adapters.SubredditPosts) (subredditPosts)).loading = true;
                            ((me.ccrama.redditslide.Adapters.SubredditPosts) (subredditPosts)).loadMore(me.ccrama.redditslide.Activities.Gallery.this, me.ccrama.redditslide.Activities.Gallery.this, false, subreddit);
                        }
                    } else if (subredditPosts instanceof me.ccrama.redditslide.Adapters.MultiredditPosts) {
                        if (!((me.ccrama.redditslide.Adapters.MultiredditPosts) (subredditPosts)).loading) {
                            ((me.ccrama.redditslide.Adapters.MultiredditPosts) (subredditPosts)).loading = true;
                            subredditPosts.loadMore(me.ccrama.redditslide.Activities.Gallery.this, me.ccrama.redditslide.Activities.Gallery.this, false);
                        }
                    }
                }
            }
        });
    }

    me.ccrama.redditslide.Adapters.GalleryView recyclerAdapter;

    public int pastVisiblesItems;

    public int visibleItemCount;

    public int totalItemCount;

    android.support.v7.widget.RecyclerView rv;

    @java.lang.Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        final int currentOrientation = newConfig.orientation;
        final me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager mLayoutManager = ((me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager) (rv.getLayoutManager()));
        mLayoutManager.setSpanCount(getNumColumns(currentOrientation));
    }

    @java.lang.Override
    public void updateSuccess(final java.util.List<net.dean.jraw.models.Submission> submissions, final int startIndex) {
        runOnUiThread(new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                int startSize = baseSubs.size();
                for (net.dean.jraw.models.Submission s : submissions) {
                    if (((!baseSubs.contains(s)) && (s.getThumbnails() != null)) && (s.getThumbnails().getSource() != null)) {
                        baseSubs.add(s);
                    }
                }
                recyclerAdapter.notifyItemRangeInserted(startSize, baseSubs.size() - startSize);
            }
        });
    }

    @java.lang.Override
    public void updateOffline(java.util.List<net.dean.jraw.models.Submission> submissions, final long cacheTime) {
        runOnUiThread(new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                recyclerAdapter.notifyDataSetChanged();
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
        recyclerAdapter.notifyDataSetChanged();
    }

    @java.lang.Override
    public void onAdapterUpdated() {
        recyclerAdapter.notifyDataSetChanged();
    }

    @android.support.annotation.NonNull
    private android.support.v7.widget.RecyclerView.LayoutManager createLayoutManager(final int numColumns) {
        return new me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager(numColumns, me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager.VERTICAL);
    }

    private int getNumColumns(final int orientation) {
        final int numColumns;
        boolean singleColumnMultiWindow = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            singleColumnMultiWindow = this.isInMultiWindowMode() && me.ccrama.redditslide.SettingValues.singleColumnMultiWindow;
        }
        if (((orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) && me.ccrama.redditslide.SettingValues.isPro) && (!singleColumnMultiWindow)) {
            numColumns = me.ccrama.redditslide.Reddit.dpWidth;
        } else if ((orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) && me.ccrama.redditslide.SettingValues.dualPortrait) {
            numColumns = 2;
        } else {
            numColumns = 1;
        }
        return numColumns;
    }

    public class OverviewPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
        public OverviewPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @java.lang.Override
        public android.support.v4.app.Fragment getItem(int i) {
            android.support.v4.app.Fragment f = null;
            me.ccrama.redditslide.ContentType.Type t = me.ccrama.redditslide.ContentType.getContentType(baseSubs.get(i));
            if (((baseSubs.size() - 2) <= i) && subredditPosts.hasMore()) {
                subredditPosts.loadMore(me.ccrama.redditslide.Activities.Gallery.this.getApplicationContext(), me.ccrama.redditslide.Activities.Gallery.this, false);
            }
            switch (t) {
                case GIF :
                case IMAGE :
                case IMGUR :
                case REDDIT :
                case EXTERNAL :
                case SPOILER :
                case XKCD :
                case DEVIANTART :
                case EMBEDDED :
                case LINK :
                case VID_ME :
                case STREAMABLE :
                case VIDEO :
                    {
                        f = new me.ccrama.redditslide.Fragments.MediaFragment();
                        android.os.Bundle args = new android.os.Bundle();
                        net.dean.jraw.models.Submission submission = baseSubs.get(i);
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
                        if (baseSubs.get(i).getSelftext().isEmpty()) {
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
                        if (baseSubs.get(i).getSelftext().isEmpty()) {
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
            return baseSubs.size();
        }
    }
}