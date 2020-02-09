package me.ccrama.redditslide.Fragments;
import me.ccrama.redditslide.Activities.CommentsScreen;
import me.ccrama.redditslide.Activities.Shadowbox;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Adapters.AlbumView;
import me.ccrama.redditslide.ImgurAlbum.AlbumUtils;
import me.ccrama.redditslide.Tumblr.TumblrUtils;
import java.util.List;
import me.ccrama.redditslide.ImgurAlbum.Image;
import me.ccrama.redditslide.SubmissionViews.PopulateShadowboxInfo;
import me.ccrama.redditslide.Tumblr.Photo;
import org.jetbrains.annotations.NotNull;
import me.ccrama.redditslide.Adapters.TumblrView;
/**
 * Created by ccrama on 6/2/2015.
 */
public class TumblrFull extends android.support.v4.app.Fragment {
    private android.view.View list;

    private int i = 0;

    private net.dean.jraw.models.Submission s;

    boolean hidden;

    android.view.View rootView;

    @java.lang.Override
    public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
        rootView = inflater.inflate(me.ccrama.redditslide.R.layout.submission_albumcard, container, false);
        me.ccrama.redditslide.SubmissionViews.PopulateShadowboxInfo.doActionbar(s, rootView, getActivity(), true);
        list = rootView.findViewById(me.ccrama.redditslide.R.id.images);
        list.setVisibility(android.view.View.VISIBLE);
        final android.support.v7.widget.LinearLayoutManager layoutManager = new android.support.v7.widget.LinearLayoutManager(getActivity());
        layoutManager.setOrientation(android.support.v7.widget.LinearLayoutManager.VERTICAL);
        ((android.support.v7.widget.RecyclerView) (list)).setLayoutManager(layoutManager);
        ((android.support.v7.widget.RecyclerView) (list)).setOnScrollListener(new android.support.v7.widget.RecyclerView.OnScrollListener() {
            @java.lang.Override
            public void onScrolled(android.support.v7.widget.RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                android.animation.ValueAnimator va = null;
                if ((dy > 0) && (!hidden)) {
                    hidden = true;
                    if ((va != null) && va.isRunning())
                        va.cancel();

                    final android.view.View base = rootView.findViewById(me.ccrama.redditslide.R.id.base);
                    va = android.animation.ValueAnimator.ofFloat(1.0F, 0.2F);
                    int mDuration = 250;// in millis

                    va.setDuration(mDuration);
                    va.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(android.animation.ValueAnimator animation) {
                            java.lang.Float value = ((java.lang.Float) (animation.getAnimatedValue()));
                            base.setAlpha(value);
                        }
                    });
                    va.start();
                } else if (hidden && (dy <= 0)) {
                    final android.view.View base = rootView.findViewById(me.ccrama.redditslide.R.id.base);
                    if ((va != null) && va.isRunning())
                        va.cancel();

                    hidden = false;
                    va = android.animation.ValueAnimator.ofFloat(0.2F, 1.0F);
                    int mDuration = 250;// in millis

                    va.setDuration(mDuration);
                    va.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(android.animation.ValueAnimator animation) {
                            java.lang.Float value = ((java.lang.Float) (animation.getAnimatedValue()));
                            base.setAlpha(value);
                        }
                    });
                    va.start();
                }
            }

            @java.lang.Override
            public void onScrollStateChanged(android.support.v7.widget.RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        final android.view.View.OnClickListener openClick = new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                ((com.sothree.slidinguppanel.SlidingUpPanelLayout) (rootView.findViewById(me.ccrama.redditslide.R.id.sliding_layout))).setPanelState(com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        };
        rootView.findViewById(me.ccrama.redditslide.R.id.base).setOnClickListener(openClick);
        final android.view.View title = rootView.findViewById(me.ccrama.redditslide.R.id.title);
        title.getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {
            @java.lang.Override
            public void onGlobalLayout() {
                ((com.sothree.slidinguppanel.SlidingUpPanelLayout) (rootView.findViewById(me.ccrama.redditslide.R.id.sliding_layout))).setPanelHeight(title.getMeasuredHeight());
                title.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        ((com.sothree.slidinguppanel.SlidingUpPanelLayout) (rootView.findViewById(me.ccrama.redditslide.R.id.sliding_layout))).addPanelSlideListener(new com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener() {
            @java.lang.Override
            public void onPanelSlide(android.view.View panel, float slideOffset) {
            }

            @java.lang.Override
            public void onPanelStateChanged(android.view.View panel, com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState previousState, com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState newState) {
                if (newState == com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.EXPANDED) {
                    rootView.findViewById(me.ccrama.redditslide.R.id.base).setOnClickListener(new android.view.View.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.view.View v) {
                            android.content.Intent i2 = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.CommentsScreen.class);
                            i2.putExtra(me.ccrama.redditslide.Activities.CommentsScreen.EXTRA_PAGE, i);
                            i2.putExtra(me.ccrama.redditslide.Activities.CommentsScreen.EXTRA_SUBREDDIT, ((me.ccrama.redditslide.Activities.Shadowbox) (getActivity())).subreddit);
                            getActivity().startActivity(i2);
                        }
                    });
                } else {
                    rootView.findViewById(me.ccrama.redditslide.R.id.base).setOnClickListener(openClick);
                }
            }
        });
        new me.ccrama.redditslide.Fragments.TumblrFull.LoadIntoRecycler(s.getUrl(), getActivity()).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
        return rootView;
    }

    public class LoadIntoRecycler extends me.ccrama.redditslide.Tumblr.TumblrUtils.GetTumblrPostWithCallback {
        java.lang.String url;

        public LoadIntoRecycler(@org.jetbrains.annotations.NotNull
        java.lang.String url, @org.jetbrains.annotations.NotNull
        android.app.Activity baseActivity) {
            super(url, baseActivity);
            // todo htis dontClose = true;
            this.url = url;
        }

        @java.lang.Override
        public void doWithData(final java.util.List<me.ccrama.redditslide.Tumblr.Photo> jsonElements) {
            super.doWithData(jsonElements);
            me.ccrama.redditslide.Adapters.TumblrView adapter = new me.ccrama.redditslide.Adapters.TumblrView(baseActivity, jsonElements, 0, s.getSubredditName());
            ((android.support.v7.widget.RecyclerView) (list)).setAdapter(adapter);
        }
    }

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.os.Bundle bundle = this.getArguments();
        i = bundle.getInt("page", 0);
        if ((((me.ccrama.redditslide.Activities.Shadowbox) (getActivity())).subredditPosts == null) || (((me.ccrama.redditslide.Activities.Shadowbox) (getActivity())).subredditPosts.getPosts().size() < bundle.getInt("page", 0))) {
            getActivity().finish();
        } else {
            s = ((me.ccrama.redditslide.Activities.Shadowbox) (getActivity())).subredditPosts.getPosts().get(bundle.getInt("page", 0));
        }
    }
}