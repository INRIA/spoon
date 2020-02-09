package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.Fragments.BlankFragment;
import me.ccrama.redditslide.Views.PreCachingLayoutManager;
import java.util.ArrayList;
import me.ccrama.redditslide.ColorPreferences;
import java.io.IOException;
import me.ccrama.redditslide.Tumblr.TumblrUtils;
import me.ccrama.redditslide.Notifications.ImageDownloadNotificationService;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Tumblr.Photo;
import org.jetbrains.annotations.NotNull;
import me.ccrama.redditslide.Views.ToolbarColorizeHelper;
import me.ccrama.redditslide.Fragments.FolderChooserDialogCreate;
import me.ccrama.redditslide.R;
import java.io.FileOutputStream;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.util.LinkUtil;
import java.util.List;
import java.util.UUID;
import me.ccrama.redditslide.Fragments.SubmissionsView;
import java.io.File;
import me.ccrama.redditslide.Adapters.TumblrView;
/**
 * Created by ccrama on 9/7/2016. <p/> This class is responsible for accessing the Tumblr api to get
 * the image-related json data from a URL. It extends FullScreenActivity and supports swipe from
 * anywhere.
 */
public class Tumblr extends me.ccrama.redditslide.Activities.FullScreenActivity implements me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.FolderCallback {
    public static final java.lang.String EXTRA_URL = "url";

    private java.util.List<me.ccrama.redditslide.Tumblr.Photo> images;

    public static final java.lang.String SUBREDDIT = "subreddit";

    private int adapterPosition;

    public java.lang.String subreddit;

    @java.lang.Override
    public void onFolderSelection(me.ccrama.redditslide.Fragments.FolderChooserDialogCreate dialog, java.io.File folder) {
        if (folder != null) {
            me.ccrama.redditslide.Reddit.appRestart.edit().putString("imagelocation", folder.getAbsolutePath()).apply();
            android.widget.Toast.makeText(this, getString(me.ccrama.redditslide.R.string.settings_set_image_location, folder.getAbsolutePath()), android.widget.Toast.LENGTH_LONG).show();
        }
    }

    @java.lang.Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        if (id == me.ccrama.redditslide.R.id.slider) {
            me.ccrama.redditslide.SettingValues.albumSwipe = true;
            me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_ALBUM_SWIPE, true).apply();
            android.content.Intent i = new android.content.Intent(this, me.ccrama.redditslide.Activities.TumblrPager.class);
            int adapterPosition = getIntent().getIntExtra(me.ccrama.redditslide.Activities.MediaView.ADAPTER_POSITION, -1);
            i.putExtra(me.ccrama.redditslide.Activities.MediaView.ADAPTER_POSITION, adapterPosition);
            if (getIntent().hasExtra(me.ccrama.redditslide.Activities.MediaView.SUBMISSION_URL)) {
                i.putExtra(me.ccrama.redditslide.Activities.MediaView.SUBMISSION_URL, getIntent().getStringExtra(me.ccrama.redditslide.Activities.MediaView.SUBMISSION_URL));
            }
            if (getIntent().hasExtra(me.ccrama.redditslide.Activities.Tumblr.SUBREDDIT)) {
                i.putExtra(me.ccrama.redditslide.Activities.Tumblr.SUBREDDIT, getIntent().getStringExtra(me.ccrama.redditslide.Activities.Tumblr.SUBREDDIT));
            }
            i.putExtra("url", url);
            startActivity(i);
            finish();
        }
        if (id == me.ccrama.redditslide.R.id.grid) {
            mToolbar.findViewById(me.ccrama.redditslide.R.id.grid).callOnClick();
        }
        if (id == me.ccrama.redditslide.R.id.comments) {
            me.ccrama.redditslide.Fragments.SubmissionsView.datachanged(adapterPosition);
            finish();
        }
        if (id == me.ccrama.redditslide.R.id.external) {
            me.ccrama.redditslide.util.LinkUtil.openExternally(url);
        }
        if (id == me.ccrama.redditslide.R.id.download) {
            for (final me.ccrama.redditslide.Tumblr.Photo elem : images) {
                doImageSave(false, elem.getOriginalSize().getUrl());
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void doImageSave(boolean isGif, java.lang.String contentUrl) {
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

                startService(i);
            }
        } else {
            me.ccrama.redditslide.Activities.MediaView.doOnClick.run();
        }
    }

    public void showFirstDialog() {
        try {
            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setTitle(me.ccrama.redditslide.R.string.set_save_location).setMessage(me.ccrama.redditslide.R.string.set_save_location_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                @java.lang.Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    // changes initial path, defaults to external storage directory
                    // changes label of the choose button
                    new me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.Builder(me.ccrama.redditslide.Activities.Tumblr.this).chooseButton(me.ccrama.redditslide.R.string.btn_select).initialPath(android.os.Environment.getExternalStorageDirectory().getPath()).show();
                }
            }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, null).show();
        } catch (java.lang.Exception ignored) {
        }
    }

    public void showErrorDialog() {
        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setTitle(me.ccrama.redditslide.R.string.err_something_wrong).setMessage(me.ccrama.redditslide.R.string.err_couldnt_save_choose_new).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                // changes initial path, defaults to external storage directory
                // changes label of the choose button
                new me.ccrama.redditslide.Fragments.FolderChooserDialogCreate.Builder(me.ccrama.redditslide.Activities.Tumblr.this).chooseButton(me.ccrama.redditslide.R.string.btn_select).initialPath(android.os.Environment.getExternalStorageDirectory().getPath()).show();
            }
        }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, null).show();
    }

    private void saveImageGallery(final android.graphics.Bitmap bitmap, java.lang.String URL) {
        if (me.ccrama.redditslide.Reddit.appRestart.getString("imagelocation", "").isEmpty()) {
            showFirstDialog();
        } else if (!new java.io.File(me.ccrama.redditslide.Reddit.appRestart.getString("imagelocation", "")).exists()) {
            showErrorDialog();
        } else {
            java.io.File f = new java.io.File(((me.ccrama.redditslide.Reddit.appRestart.getString("imagelocation", "") + java.io.File.separator) + java.util.UUID.randomUUID().toString()) + ".png");
            java.io.FileOutputStream out = null;
            try {
                f.createNewFile();
                out = new java.io.FileOutputStream(f);
                bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out);
            } catch (java.lang.Exception e) {
                e.printStackTrace();
                showErrorDialog();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                    showErrorDialog();
                }
            }
        }
    }

    public java.lang.String url;

    @java.lang.Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        android.view.MenuInflater inflater = getMenuInflater();
        inflater.inflate(me.ccrama.redditslide.R.menu.album_vertical, menu);
        adapterPosition = getIntent().getIntExtra(me.ccrama.redditslide.Activities.MediaView.ADAPTER_POSITION, -1);
        if (adapterPosition < 0) {
            menu.findItem(me.ccrama.redditslide.R.id.comments).setVisible(false);
        }
        return true;
    }

    public me.ccrama.redditslide.Activities.Tumblr.OverviewPagerAdapter album;

    public void onCreate(android.os.Bundle savedInstanceState) {
        overrideSwipeFromAnywhere();
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(new me.ccrama.redditslide.ColorPreferences(this).getDarkThemeSubreddit(me.ccrama.redditslide.ColorPreferences.FONT_STYLE), true);
        setContentView(me.ccrama.redditslide.R.layout.album);
        // Keep the screen on
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final android.support.v4.view.ViewPager pager = ((android.support.v4.view.ViewPager) (findViewById(me.ccrama.redditslide.R.id.images)));
        album = new me.ccrama.redditslide.Activities.Tumblr.OverviewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(album);
        pager.setCurrentItem(1);
        if (getIntent().hasExtra(me.ccrama.redditslide.Activities.Tumblr.SUBREDDIT)) {
            subreddit = getIntent().getStringExtra(me.ccrama.redditslide.Activities.Tumblr.SUBREDDIT);
        }
        pager.addOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {
            @java.lang.Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if ((position == 0) && (positionOffsetPixels == 0)) {
                    finish();
                }
                if ((position == 0) && (((me.ccrama.redditslide.Activities.Tumblr.OverviewPagerAdapter) (pager.getAdapter())).blankPage != null)) {
                    if (((me.ccrama.redditslide.Activities.Tumblr.OverviewPagerAdapter) (pager.getAdapter())).blankPage != null) {
                        ((me.ccrama.redditslide.Activities.Tumblr.OverviewPagerAdapter) (pager.getAdapter())).blankPage.doOffset(positionOffset);
                    }
                    ((me.ccrama.redditslide.Activities.Tumblr.OverviewPagerAdapter) (pager.getAdapter())).blankPage.realBack.setBackgroundColor(adjustAlpha(positionOffset * 0.7F));
                }
            }

            @java.lang.Override
            public void onPageSelected(int position) {
            }

            @java.lang.Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        if (!me.ccrama.redditslide.Reddit.appRestart.contains("tutorialSwipe")) {
            startActivityForResult(new android.content.Intent(this, me.ccrama.redditslide.Activities.SwipeTutorial.class), 3);
        }
    }

    @java.lang.Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        if (requestCode == 3) {
            me.ccrama.redditslide.Reddit.appRestart.edit().putBoolean("tutorialSwipe", true).apply();
        }
    }

    public class OverviewPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
        public me.ccrama.redditslide.Fragments.BlankFragment blankPage;

        public me.ccrama.redditslide.Activities.Tumblr.AlbumFrag album;

        public OverviewPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @java.lang.Override
        public android.support.v4.app.Fragment getItem(int i) {
            if (i == 0) {
                blankPage = new me.ccrama.redditslide.Fragments.BlankFragment();
                return blankPage;
            } else {
                album = new me.ccrama.redditslide.Activities.Tumblr.AlbumFrag();
                return album;
            }
        }

        @java.lang.Override
        public int getCount() {
            return 2;
        }
    }

    public int adjustAlpha(float factor) {
        int alpha = java.lang.Math.round(android.graphics.Color.alpha(android.graphics.Color.BLACK) * factor);
        int red = android.graphics.Color.red(android.graphics.Color.BLACK);
        int green = android.graphics.Color.green(android.graphics.Color.BLACK);
        int blue = android.graphics.Color.blue(android.graphics.Color.BLACK);
        return android.graphics.Color.argb(alpha, red, green, blue);
    }

    public static class AlbumFrag extends android.support.v4.app.Fragment {
        android.view.View rootView;

        public android.support.v7.widget.RecyclerView recyclerView;

        @java.lang.Override
        public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
            rootView = inflater.inflate(me.ccrama.redditslide.R.layout.fragment_verticalalbum, container, false);
            final me.ccrama.redditslide.Views.PreCachingLayoutManager mLayoutManager;
            mLayoutManager = new me.ccrama.redditslide.Views.PreCachingLayoutManager(getActivity());
            recyclerView = rootView.findViewById(me.ccrama.redditslide.R.id.images);
            recyclerView.setLayoutManager(mLayoutManager);
            ((me.ccrama.redditslide.Activities.Tumblr) (getActivity())).url = getActivity().getIntent().getExtras().getString(me.ccrama.redditslide.Activities.Tumblr.EXTRA_URL, "");
            ((me.ccrama.redditslide.Activities.BaseActivity) (getActivity())).setShareUrl(((me.ccrama.redditslide.Activities.Tumblr) (getActivity())).url);
            new me.ccrama.redditslide.Activities.Tumblr.AlbumFrag.LoadIntoRecycler(((me.ccrama.redditslide.Activities.Tumblr) (getActivity())).url, getActivity()).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
            ((me.ccrama.redditslide.Activities.Tumblr) (getActivity())).mToolbar = rootView.findViewById(me.ccrama.redditslide.R.id.toolbar);
            ((me.ccrama.redditslide.Activities.Tumblr) (getActivity())).mToolbar.setTitle(me.ccrama.redditslide.R.string.type_album);
            me.ccrama.redditslide.Views.ToolbarColorizeHelper.colorizeToolbar(((me.ccrama.redditslide.Activities.Tumblr) (getActivity())).mToolbar, android.graphics.Color.WHITE, getActivity());
            ((me.ccrama.redditslide.Activities.Tumblr) (getActivity())).setSupportActionBar(((me.ccrama.redditslide.Activities.Tumblr) (getActivity())).mToolbar);
            ((me.ccrama.redditslide.Activities.Tumblr) (getActivity())).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((me.ccrama.redditslide.Activities.Tumblr) (getActivity())).mToolbar.setPopupTheme(new me.ccrama.redditslide.ColorPreferences(getActivity()).getDarkThemeSubreddit(me.ccrama.redditslide.ColorPreferences.FONT_STYLE));
            return rootView;
        }

        public class LoadIntoRecycler extends me.ccrama.redditslide.Tumblr.TumblrUtils.GetTumblrPostWithCallback {
            java.lang.String url;

            public LoadIntoRecycler(@org.jetbrains.annotations.NotNull
            java.lang.String url, @org.jetbrains.annotations.NotNull
            android.app.Activity baseActivity) {
                super(url, baseActivity);
                this.url = url;
            }

            @java.lang.Override
            public void onError() {
                android.content.Intent i = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.Website.class);
                i.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_URL, url);
                startActivity(i);
                getActivity().finish();
            }

            @java.lang.Override
            public void doWithData(final java.util.List<me.ccrama.redditslide.Tumblr.Photo> jsonElements) {
                super.doWithData(jsonElements);
                if (getActivity() != null) {
                    getActivity().findViewById(me.ccrama.redditslide.R.id.progress).setVisibility(android.view.View.GONE);
                    ((me.ccrama.redditslide.Activities.Tumblr) (getActivity())).images = new java.util.ArrayList<>(jsonElements);
                    me.ccrama.redditslide.Adapters.TumblrView adapter = new me.ccrama.redditslide.Adapters.TumblrView(baseActivity, ((me.ccrama.redditslide.Activities.Tumblr) (getActivity())).images, getActivity().findViewById(me.ccrama.redditslide.R.id.toolbar).getHeight(), ((me.ccrama.redditslide.Activities.Tumblr) (getActivity())).subreddit);
                    recyclerView.setAdapter(adapter);
                }
            }
        }
    }
}