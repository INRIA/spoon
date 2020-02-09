package me.ccrama.redditslide.Adapters;
import me.ccrama.redditslide.HasSeen;
import me.ccrama.redditslide.Reddit;
import java.util.ArrayList;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.PostMatch;
import me.ccrama.redditslide.SettingValues;
/**
 * Created by ccrama on 9/17/2015.
 */
public class ContributionPostsSaved extends me.ccrama.redditslide.Adapters.ContributionPosts {
    private final java.lang.String category;

    public ContributionPostsSaved(java.lang.String subreddit, java.lang.String where, java.lang.String category) {
        super(subreddit, where);
        this.category = category;
    }

    net.dean.jraw.paginators.UserSavedPaginator paginator;

    @java.lang.Override
    public void loadMore(me.ccrama.redditslide.Adapters.ContributionAdapter adapter, java.lang.String subreddit, boolean reset) {
        new me.ccrama.redditslide.Adapters.ContributionPostsSaved.LoadData(reset).execute(subreddit);
    }

    public class LoadData extends me.ccrama.redditslide.Adapters.ContributionPosts.LoadData {
        public LoadData(boolean reset) {
            super(reset);
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
                if (reset || (posts == null)) {
                    posts = submissions;
                    start = -1;
                } else {
                    posts.addAll(submissions);
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
            } else if (!nomore) {
                // error
                adapter.setError(true);
            }
            refreshLayout.setRefreshing(false);
        }

        @java.lang.Override
        protected java.util.ArrayList<net.dean.jraw.models.Contribution> doInBackground(java.lang.String... subredditPaginators) {
            java.util.ArrayList<net.dean.jraw.models.Contribution> newData = new java.util.ArrayList<>();
            try {
                if (reset || (paginator == null)) {
                    paginator = new net.dean.jraw.paginators.UserSavedPaginator(me.ccrama.redditslide.Authentication.reddit, where, subreddit);
                    paginator.setSorting(me.ccrama.redditslide.SettingValues.getSubmissionSort(subreddit));
                    paginator.setTimePeriod(me.ccrama.redditslide.SettingValues.getSubmissionTimePeriod(subreddit));
                    if (category != null)
                        paginator.setCategory(category);

                }
                if (!paginator.hasNext()) {
                    nomore = true;
                    return new java.util.ArrayList<>();
                }
                for (net.dean.jraw.models.Contribution c : paginator.next()) {
                    if (c instanceof net.dean.jraw.models.Submission) {
                        net.dean.jraw.models.Submission s = ((net.dean.jraw.models.Submission) (c));
                        if (!me.ccrama.redditslide.PostMatch.doesMatch(s)) {
                            newData.add(s);
                        }
                    } else {
                        newData.add(c);
                    }
                }
                me.ccrama.redditslide.HasSeen.setHasSeenContrib(newData);
                return newData;
            } catch (java.lang.Exception e) {
                return null;
            }
        }
    }
}