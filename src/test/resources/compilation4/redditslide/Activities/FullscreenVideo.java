package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
/**
 * Created by ccrama on 3/5/2015.
 */
public class FullscreenVideo extends me.ccrama.redditslide.Activities.FullScreenActivity {
    public static final java.lang.String EXTRA_HTML = "html";

    private android.webkit.WebView v;

    @java.lang.Override
    public void finish() {
        super.finish();
        v.loadUrl("about:blank");
        overridePendingTransition(0, me.ccrama.redditslide.R.anim.fade_out);
    }

    public void onCreate(android.os.Bundle savedInstanceState) {
        overrideRedditSwipeAnywhere();
        super.onCreate(savedInstanceState);
        setContentView(me.ccrama.redditslide.R.layout.activity_video);
        java.lang.String data = getIntent().getExtras().getString(me.ccrama.redditslide.Activities.FullscreenVideo.EXTRA_HTML);
        v = ((android.webkit.WebView) (findViewById(me.ccrama.redditslide.R.id.webgif)));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            android.view.Window window = this.getWindow();
            window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(android.graphics.Color.BLACK);
        }
        java.lang.String dat = data;
        final android.webkit.WebSettings settings = v.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setPluginState(android.webkit.WebSettings.PluginState.ON);
        v.setWebChromeClient(new android.webkit.WebChromeClient());
        me.ccrama.redditslide.util.LogUtil.v(dat);
        if (dat.contains("src=\"")) {
            int start = dat.indexOf("src=\"") + 5;
            dat = dat.substring(start, dat.indexOf("\"", start));
            if (dat.startsWith("//")) {
                dat = "https:" + dat;
            }
            me.ccrama.redditslide.util.LogUtil.v(dat);
            setShareUrl(dat);
            v.loadUrl(dat);
            if ((dat.contains("youtube.co") || dat.contains("youtu.be")) && (!me.ccrama.redditslide.Reddit.appRestart.contains("showYouTubePopup"))) {
                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setTitle(getString(me.ccrama.redditslide.R.string.load_videos_internally)).setMessage(getString(me.ccrama.redditslide.R.string.load_videos_internally_content)).setPositiveButton(getString(me.ccrama.redditslide.R.string.btn_sure), new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        try {
                            startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=" + getString(me.ccrama.redditslide.R.string.youtube_plugin_package))));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("http://play.google.com/store/apps/details?id=ccrama.me.slideyoutubeplugin")));
                        }
                    }
                }).setNegativeButton(getString(me.ccrama.redditslide.R.string.btn_no), null).setNeutralButton(getString(me.ccrama.redditslide.R.string.do_not_show_again), new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        me.ccrama.redditslide.Reddit.appRestart.edit().putBoolean("showYouTubePopup", false).apply();
                    }
                }).show();
            }
        } else {
            me.ccrama.redditslide.util.LogUtil.v(dat);
            v.loadDataWithBaseURL("", dat, "text/html", "utf-8", "");
        }
    }
}