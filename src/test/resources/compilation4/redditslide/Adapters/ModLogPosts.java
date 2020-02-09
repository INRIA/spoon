package me.ccrama.redditslide.Adapters;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import me.ccrama.redditslide.Authentication;
import java.util.List;
/**
 * Created by ccrama on 9/17/2015.
 */
public class ModLogPosts {
    public java.util.ArrayList<net.dean.jraw.models.ModAction> posts;

    public boolean loading;

    private android.support.v4.widget.SwipeRefreshLayout refreshLayout;

    private me.ccrama.redditslide.Adapters.ModLogAdapter adapter;

    private net.dean.jraw.paginators.ModLogPaginator paginator;

    public ModLogPosts(java.util.ArrayList<net.dean.jraw.models.ModAction> firstData, net.dean.jraw.paginators.ModLogPaginator paginator) {
        posts = firstData;
        this.paginator = paginator;
    }

    public ModLogPosts() {
    }

    public void bindAdapter(me.ccrama.redditslide.Adapters.ModLogAdapter a, android.support.v4.widget.SwipeRefreshLayout layout) {
        this.adapter = a;
        this.refreshLayout = layout;
        loadMore(a);
    }

    public void loadMore(me.ccrama.redditslide.Adapters.ModLogAdapter adapter) {
        new me.ccrama.redditslide.Adapters.ModLogPosts.LoadData(true).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void addData(java.util.List<net.dean.jraw.models.ModAction> data) {
        posts.addAll(data);
    }

    public class LoadData extends android.os.AsyncTask<java.lang.String, java.lang.Void, java.util.ArrayList<net.dean.jraw.models.ModAction>> {
        final boolean reset;

        public LoadData(boolean reset) {
            this.reset = reset;
        }

        @java.lang.Override
        public void onPostExecute(java.util.ArrayList<net.dean.jraw.models.ModAction> subs) {
            if (subs != null) {
                if (reset || (posts == null)) {
                    posts = new java.util.ArrayList(new java.util.LinkedHashSet(subs));
                } else {
                    posts.addAll(subs);
                    posts = new java.util.ArrayList(new java.util.LinkedHashSet(posts));
                }
                loading = false;
                refreshLayout.setRefreshing(false);
                adapter.dataSet = me.ccrama.redditslide.Adapters.ModLogPosts.this;
                adapter.notifyDataSetChanged();
            } else {
                adapter.setError(true);
                refreshLayout.setRefreshing(false);
            }
        }

        @java.lang.Override
        protected java.util.ArrayList<net.dean.jraw.models.ModAction> doInBackground(java.lang.String... subredditPaginators) {
            try {
                if (reset || (paginator == null)) {
                    paginator = new net.dean.jraw.paginators.ModLogPaginator(me.ccrama.redditslide.Authentication.reddit, "mod");
                }
                if (paginator.hasNext()) {
                    java.util.ArrayList<net.dean.jraw.models.ModAction> done = new java.util.ArrayList<>(paginator.next());
                    return done;
                }
                return null;
            } catch (java.lang.Exception e) {
                return null;
            }
        }
    }
}