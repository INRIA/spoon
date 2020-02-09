package me.ccrama.redditslide.Adapters;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import me.ccrama.redditslide.Authentication;
import java.util.List;
/**
 * Created by ccrama on 9/17/2015.
 */
public class ModeratorPosts {
    public java.util.ArrayList<net.dean.jraw.models.PublicContribution> posts;

    public boolean loading;

    private android.support.v4.widget.SwipeRefreshLayout refreshLayout;

    private java.lang.String where;

    private java.lang.String subreddit;

    private me.ccrama.redditslide.Adapters.ModeratorAdapter adapter;

    private net.dean.jraw.paginators.ModeratorPaginator paginator;

    public ModeratorPosts(java.util.ArrayList<net.dean.jraw.models.PublicContribution> firstData, net.dean.jraw.paginators.ModeratorPaginator paginator) {
        posts = firstData;
        this.paginator = paginator;
    }

    public ModeratorPosts(java.lang.String where, java.lang.String subreddit) {
        this.where = where;
        this.subreddit = subreddit;
    }

    public void bindAdapter(me.ccrama.redditslide.Adapters.ModeratorAdapter a, android.support.v4.widget.SwipeRefreshLayout layout) {
        this.adapter = a;
        this.refreshLayout = layout;
        loadMore(a, where, subreddit);
    }

    public void loadMore(me.ccrama.redditslide.Adapters.ModeratorAdapter adapter, java.lang.String where, java.lang.String subreddit) {
        this.subreddit = subreddit;
        new me.ccrama.redditslide.Adapters.ModeratorPosts.LoadData(true).execute(where);
    }

    public void addData(java.util.List<net.dean.jraw.models.PublicContribution> data) {
        posts.addAll(data);
    }

    public class LoadData extends android.os.AsyncTask<java.lang.String, java.lang.Void, java.util.ArrayList<net.dean.jraw.models.PublicContribution>> {
        final boolean reset;

        public LoadData(boolean reset) {
            this.reset = reset;
        }

        @java.lang.Override
        public void onPostExecute(java.util.ArrayList<net.dean.jraw.models.PublicContribution> subs) {
            if (subs != null) {
                if (reset || (posts == null)) {
                    posts = new java.util.ArrayList(new java.util.LinkedHashSet(subs));
                } else {
                    posts.addAll(subs);
                    posts = new java.util.ArrayList(new java.util.LinkedHashSet(posts));
                }
                loading = false;
                refreshLayout.setRefreshing(false);
                adapter.dataSet = me.ccrama.redditslide.Adapters.ModeratorPosts.this;
                adapter.notifyDataSetChanged();
            } else {
                adapter.setError(true);
                refreshLayout.setRefreshing(false);
            }
        }

        @java.lang.Override
        protected java.util.ArrayList<net.dean.jraw.models.PublicContribution> doInBackground(java.lang.String... subredditPaginators) {
            try {
                if (reset || (paginator == null)) {
                    paginator = new net.dean.jraw.paginators.ModeratorPaginator(me.ccrama.redditslide.Authentication.reddit, subreddit, where);
                }
                paginator.setIncludeComments(true);
                paginator.setIncludeSubmissions(true);
                if (paginator.hasNext()) {
                    java.util.ArrayList<net.dean.jraw.models.PublicContribution> done = new java.util.ArrayList<>(paginator.next());
                    return done;
                }
                return null;
            } catch (java.lang.Exception e) {
                return null;
            }
        }
    }
}