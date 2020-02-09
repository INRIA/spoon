package me.ccrama.redditslide.Adapters;
import java.util.Locale;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import me.ccrama.redditslide.*;
import java.util.List;
import me.ccrama.redditslide.Fragments.SubredditListView;
/**
 * This class is reponsible for loading a list of subreddits from an endpoint
 * {@link loadMore(Context, SubmissionDisplay, boolean, String)} is implemented
 * asynchronously.
 * <p/>
 * Created by ccrama on 3/21/2016.
 */
public class SubredditNames {
    public java.util.List<net.dean.jraw.models.Subreddit> posts;

    public java.lang.String where;

    public boolean nomore = false;

    public boolean stillShow;

    public boolean loading;

    public me.ccrama.redditslide.Fragments.SubredditListView parent;

    private net.dean.jraw.paginators.Paginator<net.dean.jraw.models.Subreddit> paginator;

    android.content.Context c;

    public SubredditNames(java.lang.String where, android.content.Context c, me.ccrama.redditslide.Fragments.SubredditListView parent) {
        posts = new java.util.ArrayList<>();
        this.parent = parent;
        this.where = where;
        this.c = c;
    }

    public void loadMore(android.content.Context context, boolean reset) {
        new me.ccrama.redditslide.Adapters.SubredditNames.LoadData(context, reset).execute(where);
    }

    public void loadMore(android.content.Context context, boolean reset, java.lang.String where) {
        this.where = where;
        loadMore(context, reset);
    }

    public java.util.List<net.dean.jraw.models.Subreddit> getPosts() {
        return posts;
    }

    /**
     * Asynchronous task for loading data
     */
    private class LoadData extends android.os.AsyncTask<java.lang.String, java.lang.Void, java.util.List<net.dean.jraw.models.Subreddit>> {
        final boolean reset;

        android.content.Context context;

        public LoadData(android.content.Context context, boolean reset) {
            this.context = context;
            this.reset = reset;
        }

        @java.lang.Override
        public void onPostExecute(java.util.List<net.dean.jraw.models.Subreddit> submissions) {
            loading = false;
            context = null;
            if ((submissions != null) && (!submissions.isEmpty())) {
                java.util.ArrayList<net.dean.jraw.models.Subreddit> toRemove = new java.util.ArrayList<>();
                for (net.dean.jraw.models.Subreddit s : submissions) {
                    if (me.ccrama.redditslide.PostMatch.contains(s.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), me.ccrama.redditslide.SettingValues.subredditFilters, true))
                        toRemove.add(s);

                }
                submissions.removeAll(toRemove);
                // new submissions found
                int start = 0;
                if (posts != null) {
                    start = posts.size() + 1;
                }
                if (reset || (posts == null)) {
                    posts = new java.util.ArrayList(new java.util.LinkedHashSet(submissions));
                    start = -1;
                } else {
                    posts.addAll(submissions);
                    posts = new java.util.ArrayList(new java.util.LinkedHashSet(posts));
                }
                final int finalStart = start;
                // update online
                parent.updateSuccess(posts, finalStart);
            } else if (!nomore) {
                parent.updateError();
            }
        }

        @java.lang.Override
        protected java.util.List<net.dean.jraw.models.Subreddit> doInBackground(java.lang.String... subredditPaginators) {
            java.util.List<net.dean.jraw.models.Subreddit> things = new java.util.ArrayList<>();
            try {
                if (subredditPaginators[0].equalsIgnoreCase("trending")) {
                    java.util.List<java.lang.String> trending = me.ccrama.redditslide.Authentication.reddit.getTrendingSubreddits();
                    for (java.lang.String s : trending) {
                        things.add(me.ccrama.redditslide.Authentication.reddit.getSubreddit(s));
                    }
                    nomore = true;
                } else if (subredditPaginators[0].equalsIgnoreCase("popular")) {
                    stillShow = true;
                    if (reset || (paginator == null)) {
                        paginator = new net.dean.jraw.paginators.SubredditStream(me.ccrama.redditslide.Authentication.reddit, subredditPaginators[0]);
                        paginator.setSorting(me.ccrama.redditslide.SettingValues.getSubmissionSort(where));
                        paginator.setTimePeriod(me.ccrama.redditslide.SettingValues.getSubmissionTimePeriod(where));
                        paginator.setLimit(me.ccrama.redditslide.Constants.PAGINATOR_POST_LIMIT);
                    }
                    try {
                        if ((paginator != null) && paginator.hasNext()) {
                            things.addAll(paginator.next());
                        } else {
                            nomore = true;
                        }
                    } catch (java.lang.Exception e) {
                        e.printStackTrace();
                        if (e.getMessage().contains("Forbidden")) {
                            me.ccrama.redditslide.Reddit.authentication.updateToken(context);
                        }
                    }
                } else {
                    stillShow = true;
                    if (reset || (paginator == null)) {
                        paginator = new net.dean.jraw.paginators.SubredditSearchPaginator(me.ccrama.redditslide.Authentication.reddit, subredditPaginators[0]);
                        paginator.setSorting(me.ccrama.redditslide.SettingValues.getSubmissionSort(where));
                        paginator.setTimePeriod(me.ccrama.redditslide.SettingValues.getSubmissionTimePeriod(where));
                        paginator.setLimit(me.ccrama.redditslide.Constants.PAGINATOR_POST_LIMIT);
                    }
                    try {
                        if ((paginator != null) && paginator.hasNext()) {
                            things.addAll(paginator.next());
                        } else {
                            nomore = true;
                        }
                    } catch (java.lang.Exception e) {
                        e.printStackTrace();
                        if (e.getMessage().contains("Forbidden")) {
                            me.ccrama.redditslide.Reddit.authentication.updateToken(context);
                        }
                    }
                }
            } catch (java.lang.Exception e) {
                return null;
            }
            return things;
        }
    }
}