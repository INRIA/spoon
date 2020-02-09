package me.ccrama.redditslide.Adapters;
import java.util.Locale;
import java.util.HashMap;
import me.ccrama.redditslide.Fragments.CommentPage;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.TreeMap;
import me.ccrama.redditslide.util.NetworkUtil;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.Authentication;
import java.util.List;
import java.util.Map;
import me.ccrama.redditslide.LastComments;
import java.util.Collections;
/**
 * Created by ccrama on 9/17/2015.
 */
public class SubmissionComments {
    public boolean single;

    public final android.support.v4.widget.SwipeRefreshLayout refreshLayout;

    private final java.lang.String fullName;

    private final me.ccrama.redditslide.Fragments.CommentPage page;

    public java.util.ArrayList<me.ccrama.redditslide.Adapters.CommentObject> comments;

    public java.util.HashMap<java.lang.String, java.lang.String> commentOPs = new java.util.HashMap<>();

    public net.dean.jraw.models.Submission submission;

    private java.lang.String context;

    private net.dean.jraw.models.CommentSort defaultSorting = net.dean.jraw.models.CommentSort.CONFIDENCE;

    private me.ccrama.redditslide.Adapters.CommentAdapter adapter;

    public me.ccrama.redditslide.Adapters.SubmissionComments.LoadData mLoadData;

    public boolean online = true;

    int contextNumber = 5;

    public SubmissionComments(java.lang.String fullName, me.ccrama.redditslide.Fragments.CommentPage commentPage, android.support.v4.widget.SwipeRefreshLayout layout, net.dean.jraw.models.Submission s) {
        this.fullName = fullName;
        this.page = commentPage;
        online = me.ccrama.redditslide.util.NetworkUtil.isConnected(page.getActivity());
        this.refreshLayout = layout;
        if (s.getComments() != null) {
            submission = s;
            net.dean.jraw.models.CommentNode baseComment = s.getComments();
            comments = new java.util.ArrayList<>();
            java.util.Map<java.lang.Integer, me.ccrama.redditslide.Adapters.MoreChildItem> waiting = new java.util.HashMap<>();
            for (net.dean.jraw.models.CommentNode n : baseComment.walkTree()) {
                me.ccrama.redditslide.Adapters.CommentObject obj = new me.ccrama.redditslide.Adapters.CommentItem(n);
                java.util.List<java.lang.Integer> removed = new java.util.ArrayList<>();
                java.util.Map<java.lang.Integer, me.ccrama.redditslide.Adapters.MoreChildItem> map = new java.util.TreeMap<>(java.util.Collections.reverseOrder());
                map.putAll(waiting);
                for (java.lang.Integer i : map.keySet()) {
                    if (i >= n.getDepth()) {
                        comments.add(waiting.get(i));
                        removed.add(i);
                        waiting.remove(i);
                    }
                }
                comments.add(obj);
                if (n.hasMoreComments() && online) {
                    waiting.put(n.getDepth(), new me.ccrama.redditslide.Adapters.MoreChildItem(n, n.getMoreChildren()));
                }
            }
            java.util.Map<java.lang.Integer, me.ccrama.redditslide.Adapters.MoreChildItem> map = new java.util.TreeMap<>(java.util.Collections.reverseOrder());
            map.putAll(waiting);
            for (java.lang.Integer i : map.keySet()) {
                comments.add(waiting.get(i));
                waiting.remove(i);
            }
            if (baseComment.hasMoreComments() && online) {
                comments.add(new me.ccrama.redditslide.Adapters.MoreChildItem(baseComment, baseComment.getMoreChildren()));
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            refreshLayout.setRefreshing(false);
            refreshLayout.setEnabled(false);
        }
    }

    public SubmissionComments(java.lang.String fullName, me.ccrama.redditslide.Fragments.CommentPage commentPage, android.support.v4.widget.SwipeRefreshLayout layout) {
        this.fullName = fullName;
        this.page = commentPage;
        this.refreshLayout = layout;
    }

    public SubmissionComments(java.lang.String fullName, me.ccrama.redditslide.Fragments.CommentPage commentPage, android.support.v4.widget.SwipeRefreshLayout layout, java.lang.String context) {
        this.fullName = fullName;
        this.page = commentPage;
        this.context = context;
        this.refreshLayout = layout;
    }

    public SubmissionComments(java.lang.String fullName, me.ccrama.redditslide.Fragments.CommentPage commentPage, android.support.v4.widget.SwipeRefreshLayout layout, java.lang.String context, int contextNumber) {
        this.fullName = fullName;
        this.page = commentPage;
        this.context = context;
        this.refreshLayout = layout;
        this.contextNumber = contextNumber;
    }

    public void cancelLoad() {
        if (mLoadData != null) {
            mLoadData.cancel(true);
        }
    }

    public void setSorting(net.dean.jraw.models.CommentSort sort) {
        defaultSorting = sort;
        mLoadData = new me.ccrama.redditslide.Adapters.SubmissionComments.LoadData(true);
        mLoadData.execute(fullName);
    }

    public void loadMore(me.ccrama.redditslide.Adapters.CommentAdapter adapter, java.lang.String subreddit) {
        this.adapter = adapter;
        mLoadData = new me.ccrama.redditslide.Adapters.SubmissionComments.LoadData(true);
        mLoadData.execute(fullName);
    }

    public void loadMoreReply(me.ccrama.redditslide.Adapters.CommentAdapter adapter) {
        this.adapter = adapter;
        adapter.currentSelectedItem = context;
        mLoadData = new me.ccrama.redditslide.Adapters.SubmissionComments.LoadData(false);
        mLoadData.execute(fullName);
    }

    public void loadMoreReplyTop(me.ccrama.redditslide.Adapters.CommentAdapter adapter, java.lang.String context) {
        this.adapter = adapter;
        adapter.currentSelectedItem = context;
        mLoadData = new me.ccrama.redditslide.Adapters.SubmissionComments.LoadData(true);
        mLoadData.execute(fullName);
        if ((context == null) || context.isEmpty()) {
            android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(page.rv, "Comment submitted", android.support.design.widget.Snackbar.LENGTH_SHORT);
            android.view.View view = s.getView();
            android.widget.TextView tv = ((android.widget.TextView) (view.findViewById(android.support.design.R.id.snackbar_text)));
            tv.setTextColor(android.graphics.Color.WHITE);
            s.show();
        }
    }

    public void loadMore(me.ccrama.redditslide.Adapters.CommentAdapter adapter, java.lang.String subreddit, boolean forgetPlace) {
        adapter.currentSelectedItem = "";
        this.adapter = adapter;
        mLoadData = new me.ccrama.redditslide.Adapters.SubmissionComments.LoadData(true);
        mLoadData.execute(fullName);
    }

    public com.fasterxml.jackson.databind.JsonNode getSubmissionNode(net.dean.jraw.http.SubmissionRequest request) {
        java.util.Map<java.lang.String, java.lang.String> args = new java.util.HashMap<>();
        if (request.getDepth() != null)
            args.put("depth", java.lang.Integer.toString(request.getDepth()));

        if (request.getContext() != null)
            args.put("context", java.lang.Integer.toString(request.getContext()));

        if (request.getLimit() != null)
            args.put("limit", java.lang.Integer.toString(request.getLimit()));

        if (((request.getFocus() != null) && (request.getFocus().length() >= 3)) && (!net.dean.jraw.util.JrawUtils.isFullname(request.getFocus())))
            args.put("comment", request.getFocus());

        args.put("feature", "link_preview");
        args.put("sr_detail", "true");
        args.put("expand_srs", "true");
        args.put("from_detail", "true");
        args.put("always_show_media", "1");
        net.dean.jraw.models.CommentSort sort = request.getSort();
        // Reddit sorts by confidence by default
        if (sort == null)
            sort = net.dean.jraw.models.CommentSort.CONFIDENCE;

        args.put("sort", sort.name().toLowerCase(java.util.Locale.ENGLISH));
        net.dean.jraw.http.RestResponse response = me.ccrama.redditslide.Authentication.reddit.execute(me.ccrama.redditslide.Authentication.reddit.request().path(java.lang.String.format("/comments/%s", request.getId())).query(args).build());
        return response.getJson();
    }

    public void reloadSubmission(me.ccrama.redditslide.Adapters.CommentAdapter commentAdapter) {
        commentAdapter.submission = me.ccrama.redditslide.Authentication.reddit.getSubmission(submission.getFullName().substring(3, submission.getFullName().length()));
    }

    public boolean forceSorting = false;

    public class LoadData extends android.os.AsyncTask<java.lang.String, java.lang.Void, java.util.ArrayList<me.ccrama.redditslide.Adapters.CommentObject>> {
        final boolean reset;

        public LoadData(boolean reset) {
            this.reset = reset;
        }

        @java.lang.Override
        public void onPostExecute(java.util.ArrayList<me.ccrama.redditslide.Adapters.CommentObject> subs) {
            if (page.isVisible() && (submission != null)) {
                refreshLayout.setRefreshing(false);
                page.doRefresh(false);
                if (((submission.isArchived() && (!page.archived)) || (submission.isLocked() && (!page.locked))) || (submission.getDataNode().get("contest_mode").asBoolean() && (!page.contest)))
                    page.doTopBarNotify(submission, adapter);

                page.doData(reset);
                me.ccrama.redditslide.LastComments.setComments(submission);
            }
        }

        @java.lang.Override
        protected java.util.ArrayList<me.ccrama.redditslide.Adapters.CommentObject> doInBackground(java.lang.String... subredditPaginators) {
            net.dean.jraw.http.SubmissionRequest.Builder builder;
            if (context == null) {
                single = false;
                builder = new net.dean.jraw.http.SubmissionRequest.Builder(fullName).sort(defaultSorting);
            } else {
                single = true;
                builder = new net.dean.jraw.http.SubmissionRequest.Builder(fullName).sort(defaultSorting).focus(context).context(contextNumber);
            }
            try {
                com.fasterxml.jackson.databind.JsonNode node = getSubmissionNode(builder.build());
                submission = net.dean.jraw.models.meta.SubmissionSerializer.withComments(node, defaultSorting);
                net.dean.jraw.models.CommentNode baseComment = submission.getComments();
                /* if (page.o != null)
                page.o.setCommentAndWrite(submission.getFullName(), node, submission).writeToMemory();
                 */
                comments = new java.util.ArrayList<>();
                java.util.Map<java.lang.Integer, me.ccrama.redditslide.Adapters.MoreChildItem> waiting = new java.util.HashMap<>();
                commentOPs = new java.util.HashMap<>();
                java.lang.String currentOP = "";
                for (net.dean.jraw.models.CommentNode n : baseComment.walkTree()) {
                    if (n.getDepth() == 1) {
                        currentOP = n.getComment().getAuthor();
                    }
                    commentOPs.put(n.getComment().getId(), currentOP);
                    me.ccrama.redditslide.Adapters.CommentObject obj = new me.ccrama.redditslide.Adapters.CommentItem(n);
                    java.util.List<java.lang.Integer> removed = new java.util.ArrayList<>();
                    java.util.Map<java.lang.Integer, me.ccrama.redditslide.Adapters.MoreChildItem> map = new java.util.TreeMap<>(java.util.Collections.reverseOrder());
                    map.putAll(waiting);
                    for (java.lang.Integer i : map.keySet()) {
                        if (i >= n.getDepth()) {
                            comments.add(waiting.get(i));
                            removed.add(i);
                            waiting.remove(i);
                        }
                    }
                    comments.add(obj);
                    if (n.hasMoreComments()) {
                        waiting.put(n.getDepth(), new me.ccrama.redditslide.Adapters.MoreChildItem(n, n.getMoreChildren()));
                    }
                }
                java.util.Map<java.lang.Integer, me.ccrama.redditslide.Adapters.MoreChildItem> map = new java.util.TreeMap<>(java.util.Collections.reverseOrder());
                map.putAll(waiting);
                for (java.lang.Integer i : map.keySet()) {
                    comments.add(waiting.get(i));
                    waiting.remove(i);
                }
                if (baseComment.hasMoreComments()) {
                    comments.add(new me.ccrama.redditslide.Adapters.MoreChildItem(baseComment, baseComment.getMoreChildren()));
                }
                return comments;
            } catch (java.lang.Exception e) {
                // Todo reauthenticate
            }
            return null;
        }
    }
}