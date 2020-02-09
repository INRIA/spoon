package me.ccrama.redditslide.Fragments;
import me.ccrama.redditslide.Activities.CommentsScreen;
import me.ccrama.redditslide.SpoilerRobotoTextView;
import me.ccrama.redditslide.Activities.Shadowbox;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.util.SubmissionParser;
import me.ccrama.redditslide.Views.CommentOverflow;
import java.util.List;
import me.ccrama.redditslide.SubmissionViews.PopulateShadowboxInfo;
/**
 * Created by ccrama on 6/2/2015.
 */
public class SelftextFull extends android.support.v4.app.Fragment {
    private int i = 0;

    private net.dean.jraw.models.Submission s;

    @java.lang.Override
    public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
        android.view.ViewGroup rootView = ((android.view.ViewGroup) (inflater.inflate(me.ccrama.redditslide.R.layout.submission_textcard, container, false)));
        me.ccrama.redditslide.SubmissionViews.PopulateShadowboxInfo.doActionbar(s, rootView, getActivity(), true);
        if (!s.getSelftext().isEmpty()) {
            setViews(s.getDataNode().get("selftext_html").asText(), s.getSubredditName(), rootView);
        }
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

    private void setViews(java.lang.String rawHTML, java.lang.String subredditName, android.view.View base) {
        if (rawHTML.isEmpty()) {
            return;
        }
        java.util.List<java.lang.String> blocks = me.ccrama.redditslide.util.SubmissionParser.getBlocks(rawHTML);
        int startIndex = 0;
        if ((!blocks.get(0).startsWith("<table>")) && (!blocks.get(0).startsWith("<pre>"))) {
            ((me.ccrama.redditslide.SpoilerRobotoTextView) (base.findViewById(me.ccrama.redditslide.R.id.firstTextView))).setTextHtml(blocks.get(0), subredditName);
            startIndex = 1;
        }
        me.ccrama.redditslide.Views.CommentOverflow overflow = ((me.ccrama.redditslide.Views.CommentOverflow) (base.findViewById(me.ccrama.redditslide.R.id.commentOverflow)));
        if (blocks.size() > 1) {
            if (startIndex == 0) {
                overflow.setViews(blocks, subredditName);
            } else {
                overflow.setViews(blocks.subList(startIndex, blocks.size()), subredditName);
            }
        }
    }
}