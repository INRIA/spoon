package me.ccrama.redditslide.Fragments;
import me.ccrama.redditslide.Activities.CommentsScreen;
import me.ccrama.redditslide.Activities.Shadowbox;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.SubmissionViews.PopulateShadowboxInfo;
/**
 * Created by ccrama on 6/2/2015.
 */
public class TitleFull extends android.support.v4.app.Fragment {
    private int i = 0;

    private net.dean.jraw.models.Submission s;

    @java.lang.Override
    public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
        android.view.ViewGroup rootView = ((android.view.ViewGroup) (inflater.inflate(me.ccrama.redditslide.R.layout.submission_titlecard, container, false)));
        me.ccrama.redditslide.SubmissionViews.PopulateShadowboxInfo.doActionbar(s, rootView, getActivity(), true);
        rootView.findViewById(me.ccrama.redditslide.R.id.desc).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                android.content.Intent i2 = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.CommentsScreen.class);
                i2.putExtra(me.ccrama.redditslide.Activities.CommentsScreen.EXTRA_PAGE, i);
                i2.putExtra(me.ccrama.redditslide.Activities.CommentsScreen.EXTRA_SUBREDDIT, sub);
                getActivity().startActivity(i2);
            }
        });
        return rootView;
    }

    public java.lang.String sub;

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.os.Bundle bundle = this.getArguments();
        i = bundle.getInt("page", 0);
        sub = bundle.getString("sub");
        if ((((me.ccrama.redditslide.Activities.Shadowbox) (getActivity())).subredditPosts == null) || (((me.ccrama.redditslide.Activities.Shadowbox) (getActivity())).subredditPosts.getPosts().size() < bundle.getInt("page", 0))) {
            getActivity().finish();
        } else {
            s = ((me.ccrama.redditslide.Activities.Shadowbox) (getActivity())).subredditPosts.getPosts().get(bundle.getInt("page", 0));
        }
    }
}