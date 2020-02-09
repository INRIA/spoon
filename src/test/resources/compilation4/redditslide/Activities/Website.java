package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.util.LogUtil;
import java.util.HashMap;
import me.ccrama.redditslide.util.LinkUtil;
import me.ccrama.redditslide.Visuals.Palette;
import java.util.ArrayList;
import java.net.URISyntaxException;
import me.ccrama.redditslide.*;
import me.ccrama.redditslide.Fragments.SubmissionsView;
import java.net.URI;
import java.util.Map;
import me.ccrama.redditslide.util.AdBlocker;
public class Website extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    android.webkit.WebView v;

    java.lang.String url;

    int subredditColor;

    me.ccrama.redditslide.Activities.Website.MyWebViewClient client;

    me.ccrama.redditslide.Activities.Website.AdBlockWebViewClient webClient;

    android.widget.ProgressBar p;

    public static java.lang.String getDomainName(java.lang.String url) {
        java.net.URI uri;
        try {
            uri = new java.net.URI(url);
            java.lang.String domain = uri.getHost();
            if (domain == null)
                return "";

            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (java.net.URISyntaxException e) {
            e.printStackTrace();
        }
        return url;
    }

    @java.lang.Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        android.view.MenuInflater inflater = getMenuInflater();
        inflater.inflate(me.ccrama.redditslide.R.menu.menu_website, menu);
        // if (mShowInfoButton) menu.findItem(R.id.action_info).setVisible(true);
        // else menu.findItem(R.id.action_info).setVisible(false);
        android.view.MenuItem item = menu.findItem(me.ccrama.redditslide.R.id.store_cookies);
        item.setChecked(me.ccrama.redditslide.SettingValues.cookies);
        if (!getIntent().hasExtra(me.ccrama.redditslide.util.LinkUtil.ADAPTER_POSITION)) {
            menu.findItem(me.ccrama.redditslide.R.id.comments).setVisible(false);
        }
        return true;
    }

    @java.lang.Override
    public void onBackPressed() {
        if (v.canGoBack()) {
            v.goBack();
        } else if (!isFinishing()) {
            super.onBackPressed();
        }
    }

    @java.lang.Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                return true;
            case me.ccrama.redditslide.R.id.refresh :
                v.reload();
                return true;
            case me.ccrama.redditslide.R.id.back :
                v.goBack();
                return true;
            case me.ccrama.redditslide.R.id.comments :
                final int commentUrl = getIntent().getExtras().getInt(me.ccrama.redditslide.util.LinkUtil.ADAPTER_POSITION);
                finish();
                me.ccrama.redditslide.Fragments.SubmissionsView.datachanged(commentUrl);
                break;
            case me.ccrama.redditslide.R.id.external :
                android.content.Intent inte = new android.content.Intent(this, me.ccrama.redditslide.Activities.MakeExternal.class);
                inte.putExtra("url", url);
                startActivity(inte);
                return true;
            case me.ccrama.redditslide.R.id.store_cookies :
                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_COOKIES, !me.ccrama.redditslide.SettingValues.cookies).apply();
                me.ccrama.redditslide.SettingValues.cookies = !me.ccrama.redditslide.SettingValues.cookies;
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
                return true;
            case me.ccrama.redditslide.R.id.read :
                v.evaluateJavascript("(function(){return \"<html>\" + document.documentElement.innerHTML + \"</html>\";})();", new android.webkit.ValueCallback<java.lang.String>() {
                    @java.lang.Override
                    public void onReceiveValue(java.lang.String html) {
                        android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.Website.this, me.ccrama.redditslide.Activities.ReaderMode.class);
                        if ((html != null) && (!html.isEmpty())) {
                            me.ccrama.redditslide.Activities.ReaderMode.html = html;
                            me.ccrama.redditslide.util.LogUtil.v(html);
                        } else {
                            me.ccrama.redditslide.Activities.ReaderMode.html = "";
                            i.putExtra("url", v.getUrl());
                        }
                        i.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_COLOR, subredditColor);
                        startActivity(i);
                    }
                });
                return true;
            case me.ccrama.redditslide.R.id.chrome :
                me.ccrama.redditslide.util.LinkUtil.openExternally(v.getUrl());
                return true;
            case me.ccrama.redditslide.R.id.share :
                me.ccrama.redditslide.Reddit.defaultShareText(v.getTitle(), v.getUrl(), this);
                return true;
        }
        return false;
    }

    // Stop audio
    @java.lang.Override
    public void finish() {
        super.finish();
        v.loadUrl("about:blank");
    }

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        overrideSwipeFromAnywhere();
        super.onCreate(savedInstanceState);
        applyColorTheme("");
        setContentView(me.ccrama.redditslide.R.layout.activity_web);
        url = getIntent().getExtras().getString(me.ccrama.redditslide.util.LinkUtil.EXTRA_URL, "");
        subredditColor = getIntent().getExtras().getInt(me.ccrama.redditslide.util.LinkUtil.EXTRA_COLOR, me.ccrama.redditslide.Visuals.Palette.getDefaultColor());
        setSupportActionBar(((android.support.v7.widget.Toolbar) (findViewById(me.ccrama.redditslide.R.id.toolbar))));
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, "", true, subredditColor, me.ccrama.redditslide.R.id.appbar);
        mToolbar.setPopupTheme(new me.ccrama.redditslide.ColorPreferences(this).getFontStyle().getBaseId());
        p = ((android.widget.ProgressBar) (findViewById(me.ccrama.redditslide.R.id.progress)));
        v = ((android.webkit.WebView) (findViewById(me.ccrama.redditslide.R.id.web)));
        client = new me.ccrama.redditslide.Activities.Website.MyWebViewClient();
        webClient = new me.ccrama.redditslide.Activities.Website.AdBlockWebViewClient();
        if (!me.ccrama.redditslide.SettingValues.cookies) {
            android.webkit.CookieSyncManager.createInstance(this);
            android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
            try {
                cookieManager.removeAllCookies(null);
                android.webkit.CookieManager.getInstance().flush();
                cookieManager.setAcceptCookie(false);
            } catch (java.lang.NoSuchMethodError e) {
                // Although these were added in api 12, some devices don't have this method
            }
            android.webkit.WebSettings ws = v.getSettings();
            ws.setSaveFormData(false);
            ws.setSavePassword(false);
        }
        /* todo in the future, drag left and right to go back and forward in history

        IOverScrollDecor decor = new HorizontalOverScrollBounceEffectDecorator(new WebViewOverScrollDecoratorAdapter(v));

        decor.setOverScrollStateListener(new IOverScrollStateListener() {
        @Override
        public void onOverScrollStateChange(IOverScrollDecor decor, int oldState, int newState) {
        switch (newState) {
        case IOverScrollState.STATE_IDLE:
        // No over-scroll is in effect.
        break;
        case IOverScrollState.STATE_DRAG_START_SIDE:
        break;
        case IOverScrollState.STATE_DRAG_END_SIDE:
        break;
        case IOverScrollState.STATE_BOUNCE_BACK:
        if (oldState == IOverScrollState.STATE_DRAG_START_SIDE) {
        if(v.canGoBack())
        v.goBack();
        } else { // i.e. (oldState == STATE_DRAG_END_SIDE)
        if(v.canGoForward())
        v.goForward();
        }
        break;
        }
        }
        });
         */
        v.setWebChromeClient(client);
        v.setWebViewClient(webClient);
        v.getSettings().setBuiltInZoomControls(true);
        v.getSettings().setDisplayZoomControls(false);
        v.getSettings().setJavaScriptEnabled(true);
        v.getSettings().setLoadWithOverviewMode(true);
        v.getSettings().setUseWideViewPort(true);
        v.setDownloadListener(new android.webkit.DownloadListener() {
            public void onDownloadStart(java.lang.String url, java.lang.String userAgent, java.lang.String contentDisposition, java.lang.String mimetype, long contentLength) {
                // Downloads using download manager on default browser
                android.content.Intent i = new android.content.Intent(android.content.Intent.ACTION_VIEW);
                i.setData(android.net.Uri.parse(url));
                startActivity(i);
            }
        });
        v.loadUrl(url);
    }

    public void setValue(int newProgress) {
        p.setProgress(newProgress);
        if (newProgress == 100) {
            p.setVisibility(android.view.View.GONE);
        } else if (p.getVisibility() == android.view.View.GONE) {
            p.setVisibility(android.view.View.VISIBLE);
        }
    }

    private class MyWebViewClient extends android.webkit.WebChromeClient {
        private android.webkit.WebChromeClient.CustomViewCallback fullscreenCallback;

        @java.lang.Override
        public void onProgressChanged(android.webkit.WebView view, int newProgress) {
            me.ccrama.redditslide.Activities.Website.this.setValue(newProgress);
            super.onProgressChanged(view, newProgress);
        }

        @java.lang.Override
        public void onReceivedTitle(android.webkit.WebView view, java.lang.String title) {
            try {
                super.onReceivedTitle(view, title);
                if (getSupportActionBar() != null) {
                    if (!title.isEmpty()) {
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle(title);
                            setShareUrl(url);
                            if (url.contains("/")) {
                                getSupportActionBar().setSubtitle(me.ccrama.redditslide.Activities.Website.getDomainName(url));
                            }
                            currentURL = url;
                        }
                    } else if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(me.ccrama.redditslide.Activities.Website.getDomainName(url));
                    }
                }
            } catch (java.lang.Exception ignored) {
            }
        }

        @java.lang.Override
        public void onShowCustomView(android.view.View view, android.webkit.WebChromeClient.CustomViewCallback callback) {
            this.fullscreenCallback = callback;
            findViewById(me.ccrama.redditslide.R.id.appbar).setVisibility(android.view.View.INVISIBLE);
            android.view.WindowManager.LayoutParams attributes = getWindow().getAttributes();
            attributes.flags |= android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
            attributes.flags |= android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
            getWindow().setAttributes(attributes);
            getWindow().getDecorView().setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            android.widget.FrameLayout fullscreenViewFrame = ((android.widget.FrameLayout) (findViewById(me.ccrama.redditslide.R.id.web_fullscreen)));
            fullscreenViewFrame.addView(view);
        }

        @java.lang.Override
        public void onHideCustomView() {
            android.widget.FrameLayout fullscreenViewFrame = ((android.widget.FrameLayout) (findViewById(me.ccrama.redditslide.R.id.web_fullscreen)));
            fullscreenViewFrame.removeAllViews();
            android.view.WindowManager.LayoutParams attributes = getWindow().getAttributes();
            attributes.flags &= ~android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
            attributes.flags &= ~android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
            getWindow().setAttributes(attributes);
            getWindow().getDecorView().setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_VISIBLE);
            findViewById(me.ccrama.redditslide.R.id.appbar).setVisibility(android.view.View.VISIBLE);
            if (this.fullscreenCallback != null) {
                this.fullscreenCallback.onCustomViewHidden();
                this.fullscreenCallback = null;
            }
        }
    }

    public static java.util.ArrayList<java.lang.String> triedURLS;

    public java.lang.String currentURL;

    // Method adapted from http://www.hidroh.com/2016/05/19/hacking-up-ad-blocker-android/
    public class AdBlockWebViewClient extends android.webkit.WebViewClient {
        private java.util.Map<java.lang.String, java.lang.Boolean> loadedUrls = new java.util.HashMap<>();

        @java.lang.Override
        public android.webkit.WebResourceResponse shouldInterceptRequest(android.webkit.WebView view, java.lang.String url) {
            boolean ad;
            if (!loadedUrls.containsKey(url)) {
                ad = me.ccrama.redditslide.util.AdBlocker.isAd(url, me.ccrama.redditslide.Activities.Website.this);
                loadedUrls.put(url, ad);
            } else {
                ad = loadedUrls.get(url);
            }
            return (ad && ((currentURL != null) && (!currentURL.contains("twitter.com")))) && me.ccrama.redditslide.SettingValues.isPro ? me.ccrama.redditslide.util.AdBlocker.createEmptyResource() : super.shouldInterceptRequest(view, url);
        }

        @java.lang.Override
        public boolean shouldOverrideUrlLoading(android.webkit.WebView view, java.lang.String url) {
            me.ccrama.redditslide.ContentType.Type type = me.ccrama.redditslide.ContentType.getContentType(url);
            if (me.ccrama.redditslide.Activities.Website.triedURLS == null) {
                me.ccrama.redditslide.Activities.Website.triedURLS = new java.util.ArrayList<>();
            }
            if (((!me.ccrama.redditslide.PostMatch.openExternal(url)) || (type == me.ccrama.redditslide.ContentType.Type.VIDEO)) && (!me.ccrama.redditslide.Activities.Website.triedURLS.contains(url))) {
                me.ccrama.redditslide.Activities.Website.triedURLS.add(url);
                switch (type) {
                    case DEVIANTART :
                    case IMGUR :
                        if (me.ccrama.redditslide.SettingValues.image) {
                            android.content.Intent intent2 = new android.content.Intent(view.getContext(), me.ccrama.redditslide.Activities.MediaView.class);
                            intent2.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_URL, url);
                            view.getContext().startActivity(intent2);
                            return true;
                        }
                        return super.shouldOverrideUrlLoading(view, url);
                    case REDDIT :
                        if (!url.contains("inapp=false")) {
                            boolean opened = me.ccrama.redditslide.OpenRedditLink.openUrl(view.getContext(), url, false);
                            if (!opened) {
                                return super.shouldOverrideUrlLoading(view, url);
                            }
                        } else {
                            return false;
                        }
                        return true;
                    case STREAMABLE :
                    case VID_ME :
                        if (me.ccrama.redditslide.SettingValues.video) {
                            android.content.Intent myIntent = new android.content.Intent(view.getContext(), me.ccrama.redditslide.Activities.MediaView.class);
                            myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_URL, url);
                            view.getContext().startActivity(myIntent);
                            return true;
                        }
                        return super.shouldOverrideUrlLoading(view, url);
                    case ALBUM :
                        if (me.ccrama.redditslide.SettingValues.album) {
                            if (me.ccrama.redditslide.SettingValues.albumSwipe) {
                                android.content.Intent i = new android.content.Intent(view.getContext(), me.ccrama.redditslide.Activities.AlbumPager.class);
                                i.putExtra(me.ccrama.redditslide.Activities.Album.EXTRA_URL, url);
                                view.getContext().startActivity(i);
                            } else {
                                android.content.Intent i = new android.content.Intent(view.getContext(), me.ccrama.redditslide.Activities.Album.class);
                                i.putExtra(me.ccrama.redditslide.Activities.Album.EXTRA_URL, url);
                                view.getContext().startActivity(i);
                            }
                            return true;
                        }
                        return super.shouldOverrideUrlLoading(view, url);
                    case IMAGE :
                        if (me.ccrama.redditslide.SettingValues.image) {
                            android.content.Intent myIntent = new android.content.Intent(view.getContext(), me.ccrama.redditslide.Activities.MediaView.class);
                            myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_URL, url);
                            view.getContext().startActivity(myIntent);
                            return true;
                        }
                        return super.shouldOverrideUrlLoading(view, url);
                    case GIF :
                        if (me.ccrama.redditslide.SettingValues.gif) {
                            android.content.Intent myIntent = new android.content.Intent(view.getContext(), me.ccrama.redditslide.Activities.MediaView.class);
                            myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_URL, url);
                            view.getContext().startActivity(myIntent);
                            return true;
                        }
                        return super.shouldOverrideUrlLoading(view, url);
                    case VIDEO :
                        if (!me.ccrama.redditslide.util.LinkUtil.tryOpenWithVideoPlugin(url)) {
                            return super.shouldOverrideUrlLoading(view, url);
                        }
                    case EXTERNAL :
                    default :
                        return super.shouldOverrideUrlLoading(view, url);
                }
            } else {
                return super.shouldOverrideUrlLoading(view, url);
            }
        }
    }
}