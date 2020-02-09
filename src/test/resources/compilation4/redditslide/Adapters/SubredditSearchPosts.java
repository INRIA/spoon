package me.ccrama.redditslide.Adapters;
import me.ccrama.redditslide.Activities.MultiredditOverview;
import me.ccrama.redditslide.util.SortingUtil;
import me.ccrama.redditslide.R;
import java.net.UnknownHostException;
import java.util.ArrayList;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.PostMatch;
/**
 * Created by ccrama on 9/17/2015.
 */
public class SubredditSearchPosts extends me.ccrama.redditslide.Adapters.GeneralPosts {
    private java.lang.String term;

    private java.lang.String subreddit = "";

    public boolean loading;

    private net.dean.jraw.paginators.Paginator<net.dean.jraw.models.Submission> paginator;

    public android.support.v4.widget.SwipeRefreshLayout refreshLayout;

    private me.ccrama.redditslide.Adapters.ContributionAdapter adapter;

    public android.app.Activity parent;

    public SubredditSearchPosts(java.lang.String subreddit, java.lang.String term, android.app.Activity parent, boolean multireddit) {
        if (subreddit != null) {
            this.subreddit = subreddit;
        }
        this.parent = parent;
        this.term = term;
        this.multireddit = multireddit;
    }

    public void bindAdapter(me.ccrama.redditslide.Adapters.ContributionAdapter a, android.support.v4.widget.SwipeRefreshLayout layout) {
        this.adapter = a;
        this.refreshLayout = layout;
        loadMore(a, subreddit, term, true);
    }

    public void loadMore(me.ccrama.redditslide.Adapters.ContributionAdapter a, java.lang.String subreddit, java.lang.String where, boolean reset) {
        this.adapter = a;
        this.subreddit = subreddit;
        this.term = where;
        new me.ccrama.redditslide.Adapters.SubredditSearchPosts.LoadData(reset).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void loadMore(me.ccrama.redditslide.Adapters.ContributionAdapter a, java.lang.String subreddit, java.lang.String where, boolean reset, boolean multi, net.dean.jraw.paginators.TimePeriod time) {
        this.adapter = a;
        this.subreddit = subreddit;
        this.term = where;
        this.multireddit = multi;
        this.time = time;
        new me.ccrama.redditslide.Adapters.SubredditSearchPosts.LoadData(reset).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    boolean multireddit;

    net.dean.jraw.paginators.TimePeriod time = net.dean.jraw.paginators.TimePeriod.ALL;

    public void reset(net.dean.jraw.paginators.TimePeriod time) {
        this.time = time;
        new me.ccrama.redditslide.Adapters.SubredditSearchPosts.LoadData(true).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public class LoadData extends android.os.AsyncTask<java.lang.String, java.lang.Void, java.util.ArrayList<net.dean.jraw.models.Contribution>> {
        final boolean reset;

        public LoadData(boolean reset) {
            this.reset = reset;
        }

        @java.lang.Override
        public void onPostExecute(java.util.ArrayList<net.dean.jraw.models.Contribution> submissions) {
            loading = false;
            if (error != null) {
                if (error instanceof net.dean.jraw.http.NetworkException) {
                    net.dean.jraw.http.NetworkException e = ((net.dean.jraw.http.NetworkException) (error));
                    android.widget.Toast.makeText(adapter.mContext, (("Loading failed, " + e.getResponse().getStatusCode()) + ": ") + ((net.dean.jraw.http.NetworkException) (error)).getResponse().getStatusMessage(), android.widget.Toast.LENGTH_LONG).show();
                }
                if (error.getCause() instanceof java.net.UnknownHostException) {
                    android.widget.Toast.makeText(adapter.mContext, "Loading failed, please check your internet connection", android.widget.Toast.LENGTH_LONG).show();
                }
            }
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
            } else if (submissions != null) {
                // end of submissions
                nomore = true;
                adapter.notifyDataSetChanged();
                if (reset) {
                    android.widget.Toast.makeText(adapter.mContext, me.ccrama.redditslide.R.string.no_posts_found, android.widget.Toast.LENGTH_LONG).show();
                }
            } else if (!nomore) {
                // error
                adapter.setError(true);
            }
            refreshLayout.setRefreshing(false);
        }

        @java.lang.Override
        protected java.util.ArrayList<net.dean.jraw.models.Contribution> doInBackground(java.lang.String... subredditPaginators) {
            java.util.ArrayList<net.dean.jraw.models.Contribution> newSubmissions = new java.util.ArrayList<>();
            try {
                if (reset || (paginator == null)) {
                    if (multireddit) {
                        paginator = new net.dean.jraw.paginators.SubmissionSearchPaginatorMultireddit(me.ccrama.redditslide.Authentication.reddit, term);
                        ((net.dean.jraw.paginators.SubmissionSearchPaginatorMultireddit) (paginator)).setMultiReddit(me.ccrama.redditslide.Activities.MultiredditOverview.searchMulti);
                        ((net.dean.jraw.paginators.SubmissionSearchPaginatorMultireddit) (paginator)).setSearchSorting(net.dean.jraw.paginators.SubmissionSearchPaginatorMultireddit.SearchSort.valueOf(me.ccrama.redditslide.util.SortingUtil.search.toString()));
                        ((net.dean.jraw.paginators.SubmissionSearchPaginatorMultireddit) (paginator)).setSyntax(net.dean.jraw.paginators.SubmissionSearchPaginatorMultireddit.SearchSyntax.LUCENE);
                    } else {
                        paginator = new net.dean.jraw.paginators.SubmissionSearchPaginator(me.ccrama.redditslide.Authentication.reddit, term);
                        if (!subreddit.isEmpty()) {
                            ((net.dean.jraw.paginators.SubmissionSearchPaginator) (paginator)).setSubreddit(subreddit);
                        }
                        ((net.dean.jraw.paginators.SubmissionSearchPaginator) (paginator)).setSearchSorting(me.ccrama.redditslide.util.SortingUtil.search);
                        ((net.dean.jraw.paginators.SubmissionSearchPaginator) (paginator)).setSyntax(net.dean.jraw.paginators.SubmissionSearchPaginator.SearchSyntax.LUCENE);
                    }
                    paginator.setTimePeriod(time);
                }
                if (!paginator.hasNext()) {
                    nomore = true;
                    return newSubmissions;
                }
                for (net.dean.jraw.models.Submission s : paginator.next()) {
                    newSubmissions.add(s);
                }
                return newSubmissions;
            } catch (java.lang.Exception e) {
                error = e;
                e.printStackTrace();
                return null;
            }
        }

        java.lang.Exception error;
    }
}