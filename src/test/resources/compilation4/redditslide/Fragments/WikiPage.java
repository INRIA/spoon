package me.ccrama.redditslide.Fragments;
import java.lang.ref.WeakReference;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Constants;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.Activities.Wiki;
import me.ccrama.redditslide.Views.GeneralSwipeRefreshLayout;
import me.ccrama.redditslide.BuildConfig;
import me.ccrama.redditslide.OpenRedditLink;
public class WikiPage extends android.support.v4.app.Fragment {
    private java.lang.String title;

    private java.lang.String subreddit;

    private java.lang.String wikiUrl;

    private me.ccrama.redditslide.Fragments.WikiPage.WikiPageListener listener;

    private android.webkit.WebView webView;

    private me.ccrama.redditslide.Views.GeneralSwipeRefreshLayout ref;

    @java.lang.Override
    public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
        return inflater.inflate(me.ccrama.redditslide.R.layout.justtext, container, false);
    }

    @java.lang.Override
    public void onViewCreated(@android.support.annotation.NonNull
    final android.view.View view, android.os.Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ref = view.findViewById(me.ccrama.redditslide.R.id.ref);
        webView = view.findViewById(me.ccrama.redditslide.R.id.wiki_web_view);
        setUpRefresh();
        setUpWebView();
        if (getActivity() != null) {
            new me.ccrama.redditslide.Fragments.WikiPage.WikiAsyncTask(this).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR, ((me.ccrama.redditslide.Activities.Wiki) (getActivity())).wiki, subreddit, title);
        }
    }

    private void setUpRefresh() {
        ref.setColorSchemeColors(me.ccrama.redditslide.Visuals.Palette.getColors(subreddit, getActivity()));
        // If we use 'findViewById(R.id.header).getMeasuredHeight()', 0 is always returned.
        // So, we estimate the height of the header in dp
        // Something isn't right with the Wiki layout though, so use the SINGLE_HEADER instead.
        ref.setProgressViewOffset(false, me.ccrama.redditslide.Constants.SINGLE_HEADER_VIEW_OFFSET - me.ccrama.redditslide.Constants.PTR_OFFSET_TOP, me.ccrama.redditslide.Constants.SINGLE_HEADER_VIEW_OFFSET + me.ccrama.redditslide.Constants.PTR_OFFSET_BOTTOM);
        ref.post(new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                ref.setRefreshing(true);
            }
        });
    }

    private void setUpWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new me.ccrama.redditslide.Fragments.WikiPage.WikiPageJavaScriptInterface(), "Slide");
        webView.setWebViewClient(new android.webkit.WebViewClient() {
            @java.lang.Override
            public boolean shouldOverrideUrlLoading(android.webkit.WebView view, java.lang.String url) {
                if (url.toLowerCase().startsWith(wikiUrl.toLowerCase()) && (listener != null)) {
                    java.lang.String pagePiece = url.toLowerCase().replace(wikiUrl.toLowerCase(), "").split("\\?")[0].split("#")[0];
                    listener.embeddedWikiLinkClicked(pagePiece);
                } else {
                    me.ccrama.redditslide.OpenRedditLink.openUrl(getContext(), url, true);
                }
                return true;
            }

            @java.lang.Override
            public void onPageFinished(android.webkit.WebView webView, java.lang.String url) {
                super.onPageFinished(webView, url);
                if (getView() != null) {
                    getView().findViewById(me.ccrama.redditslide.R.id.wiki_web_view).setVisibility(android.view.View.VISIBLE);
                    ref.setRefreshing(false);
                    ref.setEnabled(false);
                }
            }
        });
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            android.webkit.WebView.setWebContentsDebuggingEnabled(me.ccrama.redditslide.BuildConfig.DEBUG);
        }
    }

    private void onDomRetrieved(java.lang.String dom) {
        webView.loadDataWithBaseURL(wikiUrl, "<head>".concat(me.ccrama.redditslide.Activities.Wiki.getGlobalCustomCss()).concat(me.ccrama.redditslide.Activities.Wiki.getGlobalCustomJavaScript()).concat("</head>").concat(dom), "text/html", "utf-8", null);
    }

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.os.Bundle bundle = this.getArguments();
        title = bundle.getString("title", "");
        subreddit = bundle.getString("subreddit", "");
        wikiUrl = "https://www.reddit.com/r/".concat(subreddit).concat("/wiki/");
    }

    public void setListener(me.ccrama.redditslide.Fragments.WikiPage.WikiPageListener listener) {
        this.listener = listener;
    }

    private static class WikiAsyncTask extends android.os.AsyncTask<java.lang.Object, java.lang.Void, java.lang.String> {
        java.lang.ref.WeakReference<me.ccrama.redditslide.Fragments.WikiPage> wikiPageWeakReference;

        WikiAsyncTask(me.ccrama.redditslide.Fragments.WikiPage wikiPage) {
            wikiPageWeakReference = new java.lang.ref.WeakReference<>(wikiPage);
        }

        @java.lang.Override
        protected java.lang.String doInBackground(java.lang.Object[] params) {
            return org.apache.commons.text.StringEscapeUtils.unescapeHtml4(((net.dean.jraw.managers.WikiManager) (params[0])).get(((java.lang.String) (params[1])), ((java.lang.String) (params[2]))).getDataNode().get("content_html").asText());
        }

        @java.lang.Override
        protected void onPostExecute(java.lang.String dom) {
            if (wikiPageWeakReference.get() != null) {
                wikiPageWeakReference.get().onDomRetrieved(dom);
            }
        }
    }

    private class WikiPageJavaScriptInterface {
        @android.webkit.JavascriptInterface
        public void overflowTouched() {
            listener.overflowTouched();
        }
    }

    public interface WikiPageListener {
        void embeddedWikiLinkClicked(java.lang.String wikiPageTitle);

        void overflowTouched();
    }
}