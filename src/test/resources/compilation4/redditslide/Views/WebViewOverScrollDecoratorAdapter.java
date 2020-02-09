package me.ccrama.redditslide.Views;
/**
 * Created by Carlos on 8/19/2016.
 */
public class WebViewOverScrollDecoratorAdapter implements me.everything.android.ui.overscroll.adapters.IOverScrollDecoratorAdapter {
    protected final android.webkit.WebView mView;

    public WebViewOverScrollDecoratorAdapter(android.webkit.WebView view) {
        mView = view;
    }

    @java.lang.Override
    public android.view.View getView() {
        return mView;
    }

    @java.lang.Override
    public boolean isInAbsoluteStart() {
        return !mView.canScrollHorizontally(-1);
    }

    @java.lang.Override
    public boolean isInAbsoluteEnd() {
        return !mView.canScrollHorizontally(1);
    }
}