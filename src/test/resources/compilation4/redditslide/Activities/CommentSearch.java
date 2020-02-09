package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Views.PreCachingLayoutManager;
import me.ccrama.redditslide.Adapters.CommentItem;
import java.util.ArrayList;
import me.ccrama.redditslide.Adapters.CommentAdapterSearch;
import java.util.List;
import me.ccrama.redditslide.DataShare;
import me.ccrama.redditslide.Adapters.CommentObject;
/**
 * Created by ccrama on 9/17/2015.
 * <p/>
 * This activity takes the shared comment data and allows for searching through the text of the
 * CommentNodes.
 */
public class CommentSearch extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstance) {
        overrideRedditSwipeAnywhere();
        super.onCreate(savedInstance);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_filtercomments);
        final android.widget.EditText search = ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.search)));
        android.support.v7.widget.RecyclerView rv = ((android.support.v7.widget.RecyclerView) (findViewById(me.ccrama.redditslide.R.id.vertical_content)));
        final me.ccrama.redditslide.Views.PreCachingLayoutManager mLayoutManager;
        mLayoutManager = new me.ccrama.redditslide.Views.PreCachingLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);
        java.util.ArrayList<net.dean.jraw.models.CommentNode> comments = new java.util.ArrayList<>();
        java.util.List<me.ccrama.redditslide.Adapters.CommentObject> commentsOld = me.ccrama.redditslide.DataShare.sharedComments;
        if ((commentsOld != null) && (!commentsOld.isEmpty()))
            for (me.ccrama.redditslide.Adapters.CommentObject o : commentsOld) {
                if (o instanceof me.ccrama.redditslide.Adapters.CommentItem)
                    comments.add(o.comment);

            }
        else
            finish();

        final me.ccrama.redditslide.Adapters.CommentAdapterSearch adapter = new me.ccrama.redditslide.Adapters.CommentAdapterSearch(this, comments);
        rv.setAdapter(adapter);
        search.addTextChangedListener(new android.text.TextWatcher() {
            @java.lang.Override
            public void beforeTextChanged(java.lang.CharSequence charSequence, int i, int i2, int i3) {
            }

            @java.lang.Override
            public void onTextChanged(java.lang.CharSequence charSequence, int i, int i2, int i3) {
            }

            @java.lang.Override
            public void afterTextChanged(android.text.Editable editable) {
                java.lang.String result = search.getText().toString();
                adapter.setResult(result);
                adapter.getFilter().filter(result);
            }
        });
    }
}