package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.SpoilerRobotoTextView;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Views.SidebarLayout;
import me.ccrama.redditslide.ColorPreferences;
import me.ccrama.redditslide.util.SubmissionParser;
import me.ccrama.redditslide.Views.CommentOverflow;
import java.util.List;
import me.ccrama.redditslide.Views.TitleTextView;
import me.ccrama.redditslide.OpenRedditLink;
public class Announcement extends me.ccrama.redditslide.Activities.BaseActivity {
    @java.lang.Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstance) {
        overridePendingTransition(me.ccrama.redditslide.R.anim.fade_in_real, 0);
        disableSwipeBackLayout();
        applyColorTheme();
        setTheme(me.ccrama.redditslide.R.style.popup);
        supportRequestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        super.onCreate(savedInstance);
        setContentView(me.ccrama.redditslide.R.layout.submission_dialog);
        setViews(me.ccrama.redditslide.Reddit.appRestart.getString("page", ""), "NO SUB", ((me.ccrama.redditslide.SpoilerRobotoTextView) (findViewById(me.ccrama.redditslide.R.id.firstTextView))), ((me.ccrama.redditslide.Views.CommentOverflow) (findViewById(me.ccrama.redditslide.R.id.commentOverflow))));
        ((me.ccrama.redditslide.Views.TitleTextView) (findViewById(me.ccrama.redditslide.R.id.title))).setText(me.ccrama.redditslide.Reddit.appRestart.getString("title", ""));
        findViewById(me.ccrama.redditslide.R.id.ok).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                finish();
            }
        });
        findViewById(me.ccrama.redditslide.R.id.comments).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                new me.ccrama.redditslide.OpenRedditLink(me.ccrama.redditslide.Activities.Announcement.this, me.ccrama.redditslide.Reddit.appRestart.getString("url", ""));
                finish();
            }
        });
    }

    private void setViews(java.lang.String rawHTML, java.lang.String subredditName, me.ccrama.redditslide.SpoilerRobotoTextView firstTextView, me.ccrama.redditslide.Views.CommentOverflow commentOverflow) {
        if (rawHTML.isEmpty()) {
            return;
        }
        java.util.List<java.lang.String> blocks = me.ccrama.redditslide.util.SubmissionParser.getBlocks(rawHTML);
        int startIndex = 0;
        // the <div class="md"> case is when the body contains a table or code block first
        if (!blocks.get(0).equals("<div class=\"md\">")) {
            firstTextView.setVisibility(android.view.View.VISIBLE);
            firstTextView.setTextHtml(blocks.get(0), subredditName);
            firstTextView.setLinkTextColor(new me.ccrama.redditslide.ColorPreferences(this).getColor(subredditName));
            startIndex = 1;
        } else {
            firstTextView.setText("");
            firstTextView.setVisibility(android.view.View.GONE);
        }
        if (blocks.size() > 1) {
            if (startIndex == 0) {
                commentOverflow.setViews(blocks, subredditName);
            } else {
                commentOverflow.setViews(blocks.subList(startIndex, blocks.size()), subredditName);
            }
            me.ccrama.redditslide.Views.SidebarLayout sidebar = ((me.ccrama.redditslide.Views.SidebarLayout) (findViewById(me.ccrama.redditslide.R.id.drawer_layout)));
            for (int i = 0; i < commentOverflow.getChildCount(); i++) {
                android.view.View maybeScrollable = commentOverflow.getChildAt(i);
                if (maybeScrollable instanceof android.widget.HorizontalScrollView) {
                    sidebar.addScrollable(maybeScrollable);
                }
            }
        } else {
            commentOverflow.removeAllViews();
        }
    }
}