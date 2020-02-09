package me.ccrama.redditslide.Adapters;
import java.util.ArrayList;
import me.ccrama.redditslide.Authentication;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
/**
 * Created by ccrama on 9/17/2015.
 */
public class InboxMessages extends me.ccrama.redditslide.Adapters.GeneralPosts {
    public java.util.ArrayList<net.dean.jraw.models.Message> posts;

    public boolean loading;

    private net.dean.jraw.paginators.Paginator<net.dean.jraw.models.Message> paginator;

    private android.support.v4.widget.SwipeRefreshLayout refreshLayout;

    public java.lang.String where;

    private me.ccrama.redditslide.Adapters.InboxAdapter adapter;

    public InboxMessages(java.util.ArrayList<net.dean.jraw.models.Message> firstData, net.dean.jraw.paginators.InboxPaginator paginator) {
        posts = firstData;
        this.paginator = paginator;
    }

    public InboxMessages(java.lang.String where) {
        this.where = where;
    }

    public void bindAdapter(me.ccrama.redditslide.Adapters.InboxAdapter a, android.support.v4.widget.SwipeRefreshLayout layout) {
        this.adapter = a;
        this.refreshLayout = layout;
        loadMore(a, where, true);
    }

    public void loadMore(me.ccrama.redditslide.Adapters.InboxAdapter adapter, java.lang.String where, boolean refresh) {
        new me.ccrama.redditslide.Adapters.InboxMessages.LoadData(refresh).execute(where);
    }

    public void addData(java.util.List<net.dean.jraw.models.Message> data) {
        posts.addAll(data);
    }

    public class LoadData extends android.os.AsyncTask<java.lang.String, java.lang.Void, java.util.ArrayList<net.dean.jraw.models.Message>> {
        final boolean reset;

        public LoadData(boolean reset) {
            this.reset = reset;
        }

        @java.lang.Override
        public void onPostExecute(java.util.ArrayList<net.dean.jraw.models.Message> subs) {
            if ((subs == null) && (!nomore)) {
                adapter.setError(true);
                refreshLayout.setRefreshing(false);
            } else if (!nomore) {
                if (subs.size() < 25) {
                    nomore = true;
                }
                if (reset) {
                    posts = subs;
                    ((android.app.Activity) (adapter.mContext)).runOnUiThread(new java.lang.Runnable() {
                        @java.lang.Override
                        public void run() {
                            refreshLayout.setRefreshing(false);
                            loading = false;
                            adapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    if (posts == null) {
                        posts = new java.util.ArrayList<>();
                    }
                    posts.addAll(subs);
                    ((android.app.Activity) (adapter.mContext)).runOnUiThread(new java.lang.Runnable() {
                        @java.lang.Override
                        public void run() {
                            refreshLayout.setRefreshing(false);
                            loading = false;
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }

        @java.lang.Override
        protected java.util.ArrayList<net.dean.jraw.models.Message> doInBackground(java.lang.String... subredditPaginators) {
            try {
                if (reset || (paginator == null)) {
                    paginator = new net.dean.jraw.paginators.InboxPaginator(me.ccrama.redditslide.Authentication.reddit, where);
                    paginator.setLimit(25);
                    nomore = false;
                }
                if (paginator.hasNext()) {
                    java.util.ArrayList<net.dean.jraw.models.Message> done = new java.util.ArrayList<>();
                    for (net.dean.jraw.models.Message m : paginator.next()) {
                        done.add(m);
                        if (((m.getDataNode().has("replies") && (!m.getDataNode().get("replies").toString().isEmpty())) && m.getDataNode().get("replies").has("data")) && m.getDataNode().get("replies").get("data").has("children")) {
                            com.fasterxml.jackson.databind.JsonNode n = m.getDataNode().get("replies").get("data").get("children");
                            for (com.fasterxml.jackson.databind.JsonNode o : n) {
                                done.add(new net.dean.jraw.models.PrivateMessage(o.get("data")));
                            }
                        }
                    }
                    return done;
                } else {
                    nomore = true;
                }
                return null;
            } catch (java.lang.Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}