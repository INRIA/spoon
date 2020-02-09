package me.ccrama.redditslide.Adapters;
import me.ccrama.redditslide.util.LogUtil;
import java.util.HashMap;
import java.util.ArrayList;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.PostMatch;
import java.util.Map;
import java.util.TreeMap;
import java.util.Collections;
/**
 * Created by ccrama on 9/17/2015.
 */
public class HistoryPosts extends me.ccrama.redditslide.Adapters.GeneralPosts {
    private android.support.v4.widget.SwipeRefreshLayout refreshLayout;

    private me.ccrama.redditslide.Adapters.ContributionAdapter adapter;

    public boolean loading;

    java.lang.String prefix = "";

    public HistoryPosts() {
    }

    public HistoryPosts(java.lang.String prefix) {
        this.prefix = prefix;
    }

    public void bindAdapter(me.ccrama.redditslide.Adapters.ContributionAdapter a, android.support.v4.widget.SwipeRefreshLayout layout) {
        this.adapter = a;
        this.refreshLayout = layout;
        loadMore(a, true);
    }

    public void loadMore(me.ccrama.redditslide.Adapters.ContributionAdapter adapter, boolean reset) {
        new me.ccrama.redditslide.Adapters.HistoryPosts.LoadData(reset).execute();
    }

    net.dean.jraw.paginators.FullnamesPaginator paginator;

    public class LoadData extends android.os.AsyncTask<java.lang.String, java.lang.Void, java.util.ArrayList<net.dean.jraw.models.Contribution>> {
        final boolean reset;

        public LoadData(boolean reset) {
            this.reset = reset;
        }

        @java.lang.Override
        public void onPostExecute(java.util.ArrayList<net.dean.jraw.models.Contribution> submissions) {
            loading = false;
            if ((submissions != null) && (!submissions.isEmpty())) {
                // new submissions found
                int start = 0;
                if (posts != null) {
                    start = posts.size() + 1;
                }
                java.util.ArrayList<net.dean.jraw.models.Contribution> filteredSubmissions = new java.util.ArrayList<>();
                for (net.dean.jraw.models.Contribution c : submissions) {
                    if (c instanceof net.dean.jraw.models.Submission) {
                        if (!me.ccrama.redditslide.PostMatch.doesMatch(((net.dean.jraw.models.Submission) (c)))) {
                            filteredSubmissions.add(c);
                        }
                    } else {
                        filteredSubmissions.add(c);
                    }
                }
                if (reset || (posts == null)) {
                    posts = filteredSubmissions;
                    start = -1;
                } else {
                    posts.addAll(filteredSubmissions);
                }
                final int finalStart = start;
                // update online
                if (refreshLayout != null) {
                    refreshLayout.setRefreshing(false);
                }
                if (finalStart != (-1)) {
                    adapter.notifyItemRangeInserted(finalStart + 1, posts.size());
                } else {
                    adapter.notifyDataSetChanged();
                }
            } else {
                // end of submissions
                nomore = true;
                adapter.notifyDataSetChanged();
            }
            refreshLayout.setRefreshing(false);
        }

        @java.lang.Override
        protected java.util.ArrayList<net.dean.jraw.models.Contribution> doInBackground(java.lang.String... subredditPaginators) {
            java.util.ArrayList<net.dean.jraw.models.Contribution> newData = new java.util.ArrayList<>();
            try {
                if (reset || (paginator == null)) {
                    java.util.ArrayList<java.lang.String> ids = new java.util.ArrayList<>();
                    java.util.HashMap<java.lang.Long, java.lang.String> idsSorted = new java.util.HashMap<>();
                    java.util.Map<java.lang.String, java.lang.String> values;
                    if (prefix.isEmpty()) {
                        values = com.lusfold.androidkeyvaluestore.KVStore.getInstance().getByContains("");
                    } else {
                        values = com.lusfold.androidkeyvaluestore.KVStore.getInstance().getByPrefix(prefix);
                    }
                    for (java.util.Map.Entry<java.lang.String, java.lang.String> entry : values.entrySet()) {
                        java.lang.Object done;
                        if (entry.getValue().equals("true") || entry.getValue().equals("false")) {
                            done = java.lang.Boolean.valueOf(entry.getValue());
                        } else {
                            done = java.lang.Long.valueOf(entry.getValue());
                        }
                        if (prefix.isEmpty()) {
                            if (!entry.getKey().contains("readLater")) {
                                if ((entry.getKey().length() == 6) && (done instanceof java.lang.Boolean)) {
                                    ids.add("t3_" + entry.getKey());
                                } else if (done instanceof java.lang.Long) {
                                    if (entry.getKey().contains("_")) {
                                        idsSorted.put(((java.lang.Long) (done)), entry.getKey());
                                    } else {
                                        idsSorted.put(((java.lang.Long) (done)), "t3_" + entry.getKey());
                                    }
                                }
                            }
                        } else {
                            java.lang.String key = entry.getKey();
                            if (!key.contains("_")) {
                                key = "t3_" + key;
                            }
                            idsSorted.put(((java.lang.Long) (done)), key.replace(prefix, ""));
                        }
                    }
                    if (!idsSorted.isEmpty()) {
                        java.util.TreeMap<java.lang.Long, java.lang.String> result2 = new java.util.TreeMap<>(java.util.Collections.reverseOrder());
                        result2.putAll(idsSorted);
                        ids.addAll(0, result2.values());
                    }
                    paginator = new net.dean.jraw.paginators.FullnamesPaginator(me.ccrama.redditslide.Authentication.reddit, ids.toArray(new java.lang.String[ids.size() - 1]));
                }
                if (!paginator.hasNext()) {
                    nomore = true;
                    return new java.util.ArrayList<>();
                }
                for (net.dean.jraw.models.Thing c : paginator.next()) {
                    if (c instanceof net.dean.jraw.models.Contribution) {
                        newData.add(((net.dean.jraw.models.Contribution) (c)));
                    }
                }
                return newData;
            } catch (java.lang.Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}