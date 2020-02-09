package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.ContentType;
import me.ccrama.redditslide.Adapters.ImageGridAdapterTumblr;
import me.ccrama.redditslide.util.ShareUtil;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.net.URISyntaxException;
import me.ccrama.redditslide.Notifications.ImageDownloadNotificationService;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Tumblr.Photo;
import org.jetbrains.annotations.NotNull;
import java.net.URI;
import me.ccrama.redditslide.Views.MediaVideoView;
import me.ccrama.redditslide.util.NetworkUtil;
import java.net.URL;
import me.ccrama.redditslide.Views.ToolbarColorizeHelper;
import me.ccrama.redditslide.SpoilerRobotoTextView;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import java.util.List;
import me.ccrama.redditslide.Fragments.BlankFragment;
import me.ccrama.redditslide.ColorPreferences;
import me.ccrama.redditslide.util.SubmissionParser;
import me.ccrama.redditslide.Tumblr.TumblrUtils;
import me.ccrama.redditslide.Views.SubsamplingScaleImageView;
import me.ccrama.redditslide.Fragments.FolderChooserDialogCreate;
import me.ccrama.redditslide.Visuals.FontPreferences;
import me.ccrama.redditslide.util.LinkUtil;
import me.ccrama.redditslide.Views.ImageSource;
import me.ccrama.redditslide.util.GifUtils;
import me.ccrama.redditslide.Fragments.SubmissionsView;
import java.io.File;
/**
 * Created by ccrama on 1/25/2016. <p/> This is an extension of Album.java which utilizes a
 * ViewPager for Imgur content instead of a RecyclerView (horizontal vs vertical). It also supports
 * gifs and progress bars which Album.java doesn't.
 */
public class TumblrPager extends me.ccrama.redditslide.Activities.FullScreenActivity implements me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.FolderCallback {
    private static int adapterPosition;

    public static final java.lang.String SUBREDDIT = "subreddit";

    @java.lang.Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        if (id == me.ccrama.redditslide.R.id.vertical) {
            me.ccrama.redditslide.SettingValues.albumSwipe = false;
            me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_ALBUM_SWIPE, false).apply();
            android.content.Intent i = new android.content.Intent(this, me.ccrama.redditslide.Activities.Tumblr.class);
            if (getIntent().hasExtra(me.ccrama.redditslide.Activities.MediaView.SUBMISSION_URL)) {
                i.putExtra(me.ccrama.redditslide.Activities.MediaView.SUBMISSION_URL, getIntent().getStringExtra(me.ccrama.redditslide.Activities.MediaView.SUBMISSION_URL));
            }
            if (getIntent().hasExtra(me.ccrama.redditslide.Activities.TumblrPager.SUBREDDIT)) {
                i.putExtra(me.ccrama.redditslide.Activities.TumblrPager.SUBREDDIT, getIntent().getStringExtra(me.ccrama.redditslide.Activities.TumblrPager.SUBREDDIT));
            }
            i.putExtras(getIntent());
            startActivity(i);
            finish();
        }
        if (id == me.ccrama.redditslide.R.id.grid) {
            mToolbar.findViewById(me.ccrama.redditslide.R.id.grid).callOnClick();
        }
        if (id == me.ccrama.redditslide.R.id.external) {
            me.ccrama.redditslide.util.LinkUtil.openExternally(getIntent().getExtras().getString("url", ""));
        }
        if (id == me.ccrama.redditslide.R.id.comments) {
            int adapterPosition = getIntent().getIntExtra(me.ccrama.redditslide.Activities.MediaView.ADAPTER_POSITION, -1);
            finish();
            me.ccrama.redditslide.Fragments.SubmissionsView.datachanged(adapterPosition);
            // getIntent().getStringExtra(MediaView.SUBMISSION_SUBREDDIT));
            // SubmissionAdapter.setOpen(this, getIntent().getStringExtra(MediaView.SUBMISSION_URL));
        }
        if (id == me.ccrama.redditslide.R.id.download) {
            int index = 0;
            for (final me.ccrama.redditslide.Tumblr.Photo elem : images) {
                doImageSave(false, elem.getOriginalSize().getUrl(), index);
                index++;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @java.lang.Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        if (requestCode == 3) {
            me.ccrama.redditslide.Reddit.appRestart.edit().putBoolean("tutorialSwipe", true).apply();
        }
    }

    public void onCreate(android.os.Bundle savedInstanceState) {
        overrideSwipeFromAnywhere();
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(new me.ccrama.redditslide.ColorPreferences(this).getDarkThemeSubreddit(me.ccrama.redditslide.ColorPreferences.FONT_STYLE), true);
        setContentView(me.ccrama.redditslide.R.layout.album_pager);
        // Keep the screen on
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mToolbar = ((android.support.v7.widget.Toolbar) (findViewById(me.ccrama.redditslide.R.id.toolbar)));
        mToolbar.setTitle(me.ccrama.redditslide.R.string.type_album);
        me.ccrama.redditslide.Views.ToolbarColorizeHelper.colorizeToolbar(mToolbar, android.graphics.Color.WHITE, this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent().hasExtra(me.ccrama.redditslide.Activities.TumblrPager.SUBREDDIT)) {
            this.subreddit = getIntent().getStringExtra(me.ccrama.redditslide.Activities.TumblrPager.SUBREDDIT);
        }
        mToolbar.setPopupTheme(new me.ccrama.redditslide.ColorPreferences(this).getDarkThemeSubreddit(me.ccrama.redditslide.ColorPreferences.FONT_STYLE));
        me.ccrama.redditslide.Activities.TumblrPager.adapterPosition = getIntent().getIntExtra(me.ccrama.redditslide.Activities.MediaView.ADAPTER_POSITION, -1);
        java.lang.String url = getIntent().getExtras().getString("url", "");
        setShareUrl(url);
        new me.ccrama.redditslide.Activities.TumblrPager.LoadIntoPager(url, this).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
        if (!me.ccrama.redditslide.Reddit.appRestart.contains("tutorialSwipe")) {
            startActivityForResult(new android.content.Intent(this, me.ccrama.redditslide.Activities.SwipeTutorial.class), 3);
        }
    }

    public class LoadIntoPager extends me.ccrama.redditslide.Tumblr.TumblrUtils.GetTumblrPostWithCallback {
        java.lang.String url;

        public LoadIntoPager(@org.jetbrains.annotations.NotNull
        java.lang.String url, @org.jetbrains.annotations.NotNull
        android.app.Activity baseActivity) {
            super(url, baseActivity);
            this.url = url;
        }

        @java.lang.Override
        public void onError() {
            android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.TumblrPager.this, me.ccrama.redditslide.Activities.Website.class);
            i.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_URL, url);
            startActivity(i);
            finish();
        }

        @java.lang.Override
        public void doWithData(final java.util.List<me.ccrama.redditslide.Tumblr.Photo> jsonElements) {
            super.doWithData(jsonElements);
            findViewById(me.ccrama.redditslide.R.id.progress).setVisibility(android.view.View.GONE);
            images = new java.util.ArrayList<>(jsonElements);
            p = ((android.support.v4.view.ViewPager) (findViewById(me.ccrama.redditslide.R.id.images_horizontal)));
            if (getSupportActionBar() != null) {
                getSupportActionBar().setSubtitle((1 + "/") + images.size());
            }
            me.ccrama.redditslide.Activities.TumblrPager.AlbumViewPager adapter = new me.ccrama.redditslide.Activities.TumblrPager.AlbumViewPager(getSupportFragmentManager());
            p.setAdapter(adapter);
            p.setCurrentItem(1);
            findViewById(me.ccrama.redditslide.R.id.grid).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    android.view.LayoutInflater l = getLayoutInflater();
                    android.view.View body = l.inflate(me.ccrama.redditslide.R.layout.album_grid_dialog, null, false);
                    com.afollestad.materialdialogs.AlertDialogWrapper.Builder b = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.TumblrPager.this);
                    android.widget.GridView gridview = body.findViewById(me.ccrama.redditslide.R.id.images);
                    gridview.setAdapter(new me.ccrama.redditslide.Adapters.ImageGridAdapterTumblr(me.ccrama.redditslide.Activities.TumblrPager.this, images));
                    b.setView(body);
                    final android.app.Dialog d = b.create();
                    gridview.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
                        public void onItemClick(android.widget.AdapterView<?> parent, android.view.View v, int position, long id) {
                            p.setCurrentItem(position + 1);
                            d.dismiss();
                        }
                    });
                    d.show();
                }
            });
            p.setOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {
                @java.lang.Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    if (position != 0) {
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setSubtitle((position + "/") + images.size());
                        }
                    }
                    if ((position == 0) && (positionOffset < 0.2)) {
                        finish();
                    }
                }

                @java.lang.Override
                public void onPageSelected(int position) {
                }

                @java.lang.Override
                public void onPageScrollStateChanged(int state) {
                }
            });
            adapter.notifyDataSetChanged();
        }
    }

    android.support.v4.view.ViewPager p;

    public java.util.List<me.ccrama.redditslide.Tumblr.Photo> images;

    public java.lang.String subreddit;

    @java.lang.Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        android.view.MenuInflater inflater = getMenuInflater();
        inflater.inflate(me.ccrama.redditslide.R.menu.album_pager, menu);
        me.ccrama.redditslide.Activities.TumblrPager.adapterPosition = getIntent().getIntExtra(me.ccrama.redditslide.Activities.MediaView.ADAPTER_POSITION, -1);
        if (me.ccrama.redditslide.Activities.TumblrPager.adapterPosition < 0) {
            menu.findItem(me.ccrama.redditslide.R.id.comments).setVisible(false);
        }
        return true;
    }

    public class AlbumViewPager extends android.support.v4.app.FragmentStatePagerAdapter {
        public AlbumViewPager(android.support.v4.app.FragmentManager m) {
            super(m);
        }

        @java.lang.Override
        public android.support.v4.app.Fragment getItem(int i) {
            if (i == 0) {
                android.support.v4.app.Fragment blankFragment = new me.ccrama.redditslide.Fragments.BlankFragment();
                return blankFragment;
            }
            i--;
            me.ccrama.redditslide.Tumblr.Photo current = images.get(i);
            try {
                if (me.ccrama.redditslide.ContentType.isGif(new java.net.URI(current.getOriginalSize().getUrl()))) {
                    // do gif stuff
                    android.support.v4.app.Fragment f = new me.ccrama.redditslide.Activities.TumblrPager.Gif();
                    android.os.Bundle args = new android.os.Bundle();
                    args.putInt("page", i);
                    f.setArguments(args);
                    return f;
                } else {
                    android.support.v4.app.Fragment f = new me.ccrama.redditslide.Activities.TumblrPager.ImageFullNoSubmission();
                    android.os.Bundle args = new android.os.Bundle();
                    args.putInt("page", i);
                    f.setArguments(args);
                    return f;
                }
            } catch (java.net.URISyntaxException e) {
                android.support.v4.app.Fragment f = new me.ccrama.redditslide.Activities.TumblrPager.ImageFullNoSubmission();
                android.os.Bundle args = new android.os.Bundle();
                args.putInt("page", i);
                f.setArguments(args);
                return f;
            }
        }

        @java.lang.Override
        public int getCount() {
            if (images == null) {
                return 0;
            }
            return images.size() + 1;
        }
    }

    public static class Gif extends android.support.v4.app.Fragment {
        private int i = 0;

        private android.view.View gif;

        android.view.ViewGroup rootView;

        android.widget.ProgressBar loader;

        @java.lang.Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            if (this.isVisible()) {
                // If we are becoming invisible, then...
                if (!isVisibleToUser) {
                    ((me.ccrama.redditslide.Views.MediaVideoView) (gif)).pause();
                    gif.setVisibility(android.view.View.GONE);
                }
                // If we are becoming visible, then...
                if (isVisibleToUser) {
                    ((me.ccrama.redditslide.Views.MediaVideoView) (gif)).start();
                    gif.setVisibility(android.view.View.VISIBLE);
                }
            }
        }

        @java.lang.Override
        public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
            rootView = ((android.view.ViewGroup) (inflater.inflate(me.ccrama.redditslide.R.layout.submission_gifcard_album, container, false)));
            loader = rootView.findViewById(me.ccrama.redditslide.R.id.gifprogress);
            gif = rootView.findViewById(me.ccrama.redditslide.R.id.gif);
            gif.setVisibility(android.view.View.VISIBLE);
            final me.ccrama.redditslide.Views.MediaVideoView v = ((me.ccrama.redditslide.Views.MediaVideoView) (gif));
            v.clearFocus();
            final java.lang.String url = ((me.ccrama.redditslide.Activities.TumblrPager) (getActivity())).images.get(i).getOriginalSize().getUrl();
            new me.ccrama.redditslide.util.GifUtils.AsyncLoadGif(getActivity(), ((me.ccrama.redditslide.Views.MediaVideoView) (rootView.findViewById(me.ccrama.redditslide.R.id.gif))), loader, null, new java.lang.Runnable() {
                @java.lang.Override
                public void run() {
                }
            }, false, true, true, ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.size))), ((me.ccrama.redditslide.Activities.TumblrPager) (getActivity())).subreddit).execute(url);
            rootView.findViewById(me.ccrama.redditslide.R.id.more).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    ((me.ccrama.redditslide.Activities.TumblrPager) (getActivity())).showBottomSheetImage(url, true, i);
                }
            });
            rootView.findViewById(me.ccrama.redditslide.R.id.save).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    me.ccrama.redditslide.Activities.MediaView.doOnClick.run();
                }
            });
            return rootView;
        }

        @java.lang.Override
        public void onCreate(android.os.Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            android.os.Bundle bundle = this.getArguments();
            i = bundle.getInt("page", 0);
        }
    }

    public void showBottomSheetImage(final java.lang.String contentUrl, final boolean isGif, final int index) {
        int[] attrs = new int[]{ me.ccrama.redditslide.R.attr.tintColor };
        android.content.res.TypedArray ta = obtainStyledAttributes(attrs);
        int color = ta.getColor(0, android.graphics.Color.WHITE);
        android.graphics.drawable.Drawable external = getResources().getDrawable(me.ccrama.redditslide.R.drawable.openexternal);
        android.graphics.drawable.Drawable share = getResources().getDrawable(me.ccrama.redditslide.R.drawable.share);
        android.graphics.drawable.Drawable image = getResources().getDrawable(me.ccrama.redditslide.R.drawable.image);
        android.graphics.drawable.Drawable save = getResources().getDrawable(me.ccrama.redditslide.R.drawable.save);
        external.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        share.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        image.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        save.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        ta.recycle();
        com.cocosw.bottomsheet.BottomSheet.Builder b = new com.cocosw.bottomsheet.BottomSheet.Builder(this).title(contentUrl);
        b.sheet(2, external, getString(me.ccrama.redditslide.R.string.submission_link_extern));
        b.sheet(5, share, getString(me.ccrama.redditslide.R.string.submission_link_share));
        if (!isGif)
            b.sheet(3, image, getString(me.ccrama.redditslide.R.string.share_image));

        b.sheet(4, save, getString(me.ccrama.redditslide.R.string.submission_save_image));
        b.listener(new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                switch (which) {
                    case 2 :
                        {
                            me.ccrama.redditslide.util.LinkUtil.openExternally(contentUrl);
                        }
                        break;
                    case 3 :
                        {
                            me.ccrama.redditslide.util.ShareUtil.shareImage(contentUrl, me.ccrama.redditslide.Activities.TumblrPager.this);
                        }
                        break;
                    case 5 :
                        {
                            me.ccrama.redditslide.Reddit.defaultShareText("", contentUrl, me.ccrama.redditslide.Activities.TumblrPager.this);
                        }
                        break;
                    case 4 :
                        {
                            doImageSave(isGif, contentUrl, index);
                        }
                        break;
                }
            }
        });
        b.show();
    }

    public void doImageSave(boolean isGif, java.lang.String contentUrl, int index) {
        if (!isGif) {
            if (me.ccrama.redditslide.Reddit.appRestart.getString("imagelocation", "").isEmpty()) {
                showFirstDialog();
            } else if (!new java.io.File(me.ccrama.redditslide.Reddit.appRestart.getString("imagelocation", "")).exists()) {
                showErrorDialog();
            } else {
                android.content.Intent i = new android.content.Intent(this, me.ccrama.redditslide.Notifications.ImageDownloadNotificationService.class);
                i.putExtra("actuallyLoaded", contentUrl);
                if ((subreddit != null) && (!subreddit.isEmpty()))
                    i.putExtra("subreddit", subreddit);

                i.putExtra("index", index);
                startService(i);
            }
        } else {
            me.ccrama.redditslide.Activities.MediaView.doOnClick.run();
        }
    }

    public static class ImageFullNoSubmission extends android.support.v4.app.Fragment {
        private int i = 0;

        public ImageFullNoSubmission() {
        }

        @java.lang.Override
        public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
            final android.view.ViewGroup rootView = ((android.view.ViewGroup) (inflater.inflate(me.ccrama.redditslide.R.layout.album_image_pager, container, false)));
            final me.ccrama.redditslide.Tumblr.Photo current = ((me.ccrama.redditslide.Activities.TumblrPager) (getActivity())).images.get(i);
            final java.lang.String url = current.getOriginalSize().getUrl();
            boolean lq = false;
            if (((me.ccrama.redditslide.SettingValues.loadImageLq && (me.ccrama.redditslide.SettingValues.lowResAlways || ((!me.ccrama.redditslide.util.NetworkUtil.isConnectedWifi(getActivity())) && me.ccrama.redditslide.SettingValues.lowResMobile))) && (current.getAltSizes() != null)) && (!current.getAltSizes().isEmpty())) {
                java.lang.String lqurl = current.getAltSizes().get(current.getAltSizes().size() / 2).getUrl();
                me.ccrama.redditslide.Activities.TumblrPager.loadImage(rootView, this, lqurl);
                lq = true;
            } else {
                me.ccrama.redditslide.Activities.TumblrPager.loadImage(rootView, this, url);
            }
            {
                rootView.findViewById(me.ccrama.redditslide.R.id.more).setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        ((me.ccrama.redditslide.Activities.TumblrPager) (getActivity())).showBottomSheetImage(url, false, i);
                    }
                });
                {
                    rootView.findViewById(me.ccrama.redditslide.R.id.save).setOnClickListener(new android.view.View.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.view.View v2) {
                            ((me.ccrama.redditslide.Activities.TumblrPager) (getActivity())).doImageSave(false, url, i);
                        }
                    });
                }
            }
            {
                java.lang.String title = "";
                java.lang.String description = "";
                if (current.getCaption() != null) {
                    java.util.List<java.lang.String> text = me.ccrama.redditslide.util.SubmissionParser.getBlocks(current.getCaption());
                    description = text.get(0).trim();
                }
                if (title.isEmpty() && description.isEmpty()) {
                    rootView.findViewById(me.ccrama.redditslide.R.id.panel).setVisibility(android.view.View.GONE);
                    rootView.findViewById(me.ccrama.redditslide.R.id.margin).setPadding(0, 0, 0, 0);
                } else if (title.isEmpty()) {
                    me.ccrama.redditslide.Activities.TumblrPager.setTextWithLinks(description, ((me.ccrama.redditslide.SpoilerRobotoTextView) (rootView.findViewById(me.ccrama.redditslide.R.id.title))));
                } else {
                    me.ccrama.redditslide.Activities.TumblrPager.setTextWithLinks(title, ((me.ccrama.redditslide.SpoilerRobotoTextView) (rootView.findViewById(me.ccrama.redditslide.R.id.title))));
                    me.ccrama.redditslide.Activities.TumblrPager.setTextWithLinks(description, ((me.ccrama.redditslide.SpoilerRobotoTextView) (rootView.findViewById(me.ccrama.redditslide.R.id.body))));
                }
                {
                    int type = new me.ccrama.redditslide.Visuals.FontPreferences(getContext()).getFontTypeComment().getTypeface();
                    android.graphics.Typeface typeface;
                    if (type >= 0) {
                        typeface = com.devspark.robototextview.RobotoTypefaces.obtainTypeface(getContext(), type);
                    } else {
                        typeface = android.graphics.Typeface.DEFAULT;
                    }
                    ((me.ccrama.redditslide.SpoilerRobotoTextView) (rootView.findViewById(me.ccrama.redditslide.R.id.body))).setTypeface(typeface);
                }
                {
                    int type = new me.ccrama.redditslide.Visuals.FontPreferences(getContext()).getFontTypeTitle().getTypeface();
                    android.graphics.Typeface typeface;
                    if (type >= 0) {
                        typeface = com.devspark.robototextview.RobotoTypefaces.obtainTypeface(getContext(), type);
                    } else {
                        typeface = android.graphics.Typeface.DEFAULT;
                    }
                    ((me.ccrama.redditslide.SpoilerRobotoTextView) (rootView.findViewById(me.ccrama.redditslide.R.id.title))).setTypeface(typeface);
                }
                final com.sothree.slidinguppanel.SlidingUpPanelLayout l = rootView.findViewById(me.ccrama.redditslide.R.id.sliding_layout);
                rootView.findViewById(me.ccrama.redditslide.R.id.title).setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        l.setPanelState(com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.EXPANDED);
                    }
                });
                rootView.findViewById(me.ccrama.redditslide.R.id.body).setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        l.setPanelState(com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.EXPANDED);
                    }
                });
            }
            if (lq) {
                rootView.findViewById(me.ccrama.redditslide.R.id.hq).setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        me.ccrama.redditslide.Activities.TumblrPager.loadImage(rootView, me.ccrama.redditslide.Activities.TumblrPager.ImageFullNoSubmission.this, url);
                        rootView.findViewById(me.ccrama.redditslide.R.id.hq).setVisibility(android.view.View.GONE);
                    }
                });
            } else {
                rootView.findViewById(me.ccrama.redditslide.R.id.hq).setVisibility(android.view.View.GONE);
            }
            if (getActivity().getIntent().hasExtra(me.ccrama.redditslide.Activities.MediaView.SUBMISSION_URL)) {
                rootView.findViewById(me.ccrama.redditslide.R.id.comments).setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        getActivity().finish();
                        me.ccrama.redditslide.Fragments.SubmissionsView.datachanged(me.ccrama.redditslide.Activities.TumblrPager.adapterPosition);
                    }
                });
            } else {
                rootView.findViewById(me.ccrama.redditslide.R.id.comments).setVisibility(android.view.View.GONE);
            }
            return rootView;
        }

        @java.lang.Override
        public void onCreate(android.os.Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            android.os.Bundle bundle = this.getArguments();
            i = bundle.getInt("page", 0);
        }
    }

    public static void setTextWithLinks(java.lang.String s, me.ccrama.redditslide.SpoilerRobotoTextView text) {
        java.lang.String[] parts = s.split("\\s+");
        java.lang.StringBuilder b = new java.lang.StringBuilder();
        for (java.lang.String item : parts)
            try {
                java.net.URL url = new java.net.URL(item);
                b.append(" <a href=\"").append(url).append("\">").append(url).append("</a>");
            } catch (java.net.MalformedURLException e) {
                b.append(" ").append(item);
            }

        text.setTextHtml(b.toString(), "no sub");
    }

    public static java.lang.String readableFileSize(long size) {
        if (size <= 0)
            return "0";

        final java.lang.String[] units = new java.lang.String[]{ "B", "kB", "MB", "GB", "TB" };
        int digitGroups = ((int) (java.lang.Math.log10(size) / java.lang.Math.log10(1024)));
        return (new java.text.DecimalFormat("#,##0.#").format(size / java.lang.Math.pow(1024, digitGroups)) + " ") + units[digitGroups];
    }

    private static void loadImage(final android.view.View rootView, android.support.v4.app.Fragment f, java.lang.String url) {
        final me.ccrama.redditslide.Views.SubsamplingScaleImageView image = rootView.findViewById(me.ccrama.redditslide.R.id.image);
        image.setMinimumDpi(70);
        image.setMinimumTileDpi(240);
        android.widget.ImageView fakeImage = new android.widget.ImageView(f.getActivity());
        final android.widget.TextView size = rootView.findViewById(me.ccrama.redditslide.R.id.size);
        fakeImage.setLayoutParams(new android.widget.LinearLayout.LayoutParams(image.getWidth(), image.getHeight()));
        fakeImage.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
        ((me.ccrama.redditslide.Reddit) (f.getActivity().getApplication())).getImageLoader().displayImage(url, new com.nostra13.universalimageloader.core.imageaware.ImageViewAware(fakeImage), new com.nostra13.universalimageloader.core.DisplayImageOptions.Builder().resetViewBeforeLoading(true).cacheOnDisk(true).imageScaleType(com.nostra13.universalimageloader.core.assist.ImageScaleType.NONE).cacheInMemory(false).build(), new com.nostra13.universalimageloader.core.listener.ImageLoadingListener() {
            private android.view.View mView;

            @java.lang.Override
            public void onLoadingStarted(java.lang.String imageUri, android.view.View view) {
                mView = view;
                size.setVisibility(android.view.View.VISIBLE);
            }

            @java.lang.Override
            public void onLoadingFailed(java.lang.String imageUri, android.view.View view, com.nostra13.universalimageloader.core.assist.FailReason failReason) {
                android.util.Log.v("Slide", "LOADING FAILED");
            }

            @java.lang.Override
            public void onLoadingComplete(java.lang.String imageUri, android.view.View view, android.graphics.Bitmap loadedImage) {
                size.setVisibility(android.view.View.GONE);
                image.setImage(me.ccrama.redditslide.Views.ImageSource.bitmap(loadedImage));
                rootView.findViewById(me.ccrama.redditslide.R.id.progress).setVisibility(android.view.View.GONE);
            }

            @java.lang.Override
            public void onLoadingCancelled(java.lang.String imageUri, android.view.View view) {
                android.util.Log.v("Slide", "LOADING CANCELLED");
            }
        }, new com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener() {
            @java.lang.Override
            public void onProgressUpdate(java.lang.String imageUri, android.view.View view, int current, int total) {
                size.setText(me.ccrama.redditslide.Activities.TumblrPager.readableFileSize(total));
                ((android.widget.ProgressBar) (rootView.findViewById(me.ccrama.redditslide.R.id.progress))).setProgress(java.lang.Math.round((100.0F * current) / total));
            }
        });
    }

    public void showFirstDialog() {
        runOnUiThread(new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.TumblrPager.this).setTitle(me.ccrama.redditslide.R.string.set_save_location).setMessage(me.ccrama.redditslide.R.string.set_save_location_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        // changes initial path, defaults to external storage directory
                        // changes label of the choose button
                        new me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.Builder(me.ccrama.redditslide.Activities.TumblrPager.this).chooseButton(me.ccrama.redditslide.R.string.btn_select).initialPath(android.os.Environment.getExternalStorageDirectory().getPath()).show();
                    }
                }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, null).show();
            }
        });
    }

    public void showErrorDialog() {
        runOnUiThread(new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.TumblrPager.this).setTitle(me.ccrama.redditslide.R.string.err_something_wrong).setMessage(me.ccrama.redditslide.R.string.err_couldnt_save_choose_new).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        // changes initial path, defaults to external storage directory
                        // changes label of the choose button
                        new me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.Builder(me.ccrama.redditslide.Activities.TumblrPager.this).chooseButton(me.ccrama.redditslide.R.string.btn_select).initialPath(android.os.Environment.getExternalStorageDirectory().getPath()).show();
                    }
                }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, null).show();
            }
        });
    }

    @java.lang.Override
    public void onFolderSelection(me.ccrama.redditslide.Fragments.FolderChooserDialogCreate dialog, java.io.File folder) {
        if (folder != null) {
            me.ccrama.redditslide.Reddit.appRestart.edit().putString("imagelocation", folder.getAbsolutePath()).apply();
            android.widget.Toast.makeText(this, getString(me.ccrama.redditslide.R.string.settings_set_image_location, folder.getAbsolutePath()) + folder.getAbsolutePath(), android.widget.Toast.LENGTH_LONG).show();
        }
    }
}