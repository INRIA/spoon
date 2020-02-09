package me.ccrama.redditslide.Activities;
import java.util.Locale;
import me.ccrama.redditslide.ContentType;
import com.google.gson.JsonObject;
import me.ccrama.redditslide.Visuals.Palette;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import java.net.URI;
import com.google.gson.Gson;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.TimeUtils;
import me.ccrama.redditslide.SpoilerRobotoTextView;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.util.TwitterObject;
import me.ccrama.redditslide.Views.CommentOverflow;
import com.fasterxml.jackson.databind.ObjectReader;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.ccrama.redditslide.util.HttpUtil;
import me.ccrama.redditslide.util.SubmissionParser;
import java.io.IOException;
import me.ccrama.redditslide.Views.SidebarLayout;
import me.ccrama.redditslide.util.LinkUtil;
import me.ccrama.redditslide.Authentication;
import java.util.Map;
public class LiveThread extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    public static final java.lang.String EXTRA_LIVEURL = "liveurl";

    public net.dean.jraw.models.LiveThread thread;

    @java.lang.Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                onBackPressed();
                return true;
            case me.ccrama.redditslide.R.id.info :
                ((android.support.v4.widget.DrawerLayout) (findViewById(me.ccrama.redditslide.R.id.drawer_layout))).openDrawer(android.view.Gravity.RIGHT);
                return true;
            default :
                return false;
        }
    }

    @java.lang.Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        android.view.MenuInflater inflater = getMenuInflater();
        inflater.inflate(me.ccrama.redditslide.R.menu.settings_info, menu);
        return true;
    }

    public android.support.v7.widget.RecyclerView baseRecycler;

    public java.lang.String term;

    @java.lang.Override
    public void onDestroy() {
        super.onDestroy();
        // todo finish
    }

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        overrideSwipeFromAnywhere();
        getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().getDecorView().setBackgroundDrawable(null);
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_livethread);
        baseRecycler = ((android.support.v7.widget.RecyclerView) (findViewById(me.ccrama.redditslide.R.id.content_view)));
        baseRecycler.setLayoutManager(new android.support.v7.widget.LinearLayoutManager(this));
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
            com.afollestad.materialdialogs.MaterialDialog d;

            @java.lang.Override
            public void onPreExecute() {
                d = new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.LiveThread.this).title(me.ccrama.redditslide.R.string.livethread_loading_title).content(me.ccrama.redditslide.R.string.misc_please_wait).progress(true, 100).cancelable(false).show();
            }

            @java.lang.Override
            protected java.lang.Void doInBackground(java.lang.Void... params) {
                try {
                    thread = new net.dean.jraw.managers.LiveThreadManager(me.ccrama.redditslide.Authentication.reddit).get(getIntent().getStringExtra(me.ccrama.redditslide.Activities.LiveThread.EXTRA_LIVEURL));
                } catch (java.lang.Exception e) {
                }
                return null;
            }

            @java.lang.Override
            public void onPostExecute(java.lang.Void aVoid) {
                if (thread == null) {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.LiveThread.this).setTitle(me.ccrama.redditslide.R.string.livethread_not_found).setMessage(me.ccrama.redditslide.R.string.misc_please_try_again_soon).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            finish();
                        }
                    }).setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {
                        @java.lang.Override
                        public void onDismiss(android.content.DialogInterface dialog) {
                            finish();
                        }
                    }).setCancelable(false).show();
                } else {
                    d.dismiss();
                    setupAppBar(me.ccrama.redditslide.R.id.toolbar, thread.getTitle(), true, false);
                    findViewById(me.ccrama.redditslide.R.id.toolbar).setBackgroundResource(me.ccrama.redditslide.R.color.md_red_300);
                    findViewById(me.ccrama.redditslide.R.id.header_sub).setBackgroundResource(me.ccrama.redditslide.R.color.md_red_300);
                    themeSystemBars(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(getResources().getColor(me.ccrama.redditslide.R.color.md_red_300)));
                    setRecentBar(getString(me.ccrama.redditslide.R.string.livethread_recents_title, thread.getTitle()), getResources().getColor(me.ccrama.redditslide.R.color.md_red_300));
                    doPaginator();
                }
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    java.util.ArrayList<net.dean.jraw.models.LiveUpdate> updates;

    net.dean.jraw.paginators.LiveThreadPaginator paginator;

    public void doPaginator() {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
            @java.lang.Override
            protected java.lang.Void doInBackground(java.lang.Void... params) {
                paginator = new net.dean.jraw.managers.LiveThreadManager(me.ccrama.redditslide.Authentication.reddit).stream(thread);
                updates = new java.util.ArrayList<>(paginator.accumulateMerged(5));
                return null;
            }

            @java.lang.Override
            public void onPostExecute(java.lang.Void aVoid) {
                doLiveThreadUpdates();
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void doLiveThreadUpdates() {
        final me.ccrama.redditslide.Activities.LiveThread.PaginatorAdapter adapter = new me.ccrama.redditslide.Activities.LiveThread.PaginatorAdapter(this);
        baseRecycler.setAdapter(adapter);
        doLiveSidebar();
        if ((thread.getWebsocketUrl() != null) && (!thread.getWebsocketUrl().isEmpty())) {
            new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
                @java.lang.Override
                protected java.lang.Void doInBackground(java.lang.Void... params) {
                    final com.fasterxml.jackson.databind.ObjectReader o = new com.fasterxml.jackson.databind.ObjectMapper().reader();
                    try {
                        com.neovisionaries.ws.client.WebSocket ws = new com.neovisionaries.ws.client.WebSocketFactory().createSocket(thread.getWebsocketUrl());
                        ws.addListener(new com.neovisionaries.ws.client.WebSocketListener() {
                            @java.lang.Override
                            public void onStateChanged(com.neovisionaries.ws.client.WebSocket websocket, com.neovisionaries.ws.client.WebSocketState newState) {
                            }

                            @java.lang.Override
                            public void onConnected(com.neovisionaries.ws.client.WebSocket websocket, java.util.Map<java.lang.String, java.util.List<java.lang.String>> headers) {
                            }

                            @java.lang.Override
                            public void onConnectError(com.neovisionaries.ws.client.WebSocket websocket, com.neovisionaries.ws.client.WebSocketException cause) {
                            }

                            @java.lang.Override
                            public void onDisconnected(com.neovisionaries.ws.client.WebSocket websocket, com.neovisionaries.ws.client.WebSocketFrame serverCloseFrame, com.neovisionaries.ws.client.WebSocketFrame clientCloseFrame, boolean closedByServer) {
                            }

                            @java.lang.Override
                            public void onFrame(com.neovisionaries.ws.client.WebSocket websocket, com.neovisionaries.ws.client.WebSocketFrame frame) {
                            }

                            @java.lang.Override
                            public void onContinuationFrame(com.neovisionaries.ws.client.WebSocket websocket, com.neovisionaries.ws.client.WebSocketFrame frame) {
                            }

                            @java.lang.Override
                            public void onTextFrame(com.neovisionaries.ws.client.WebSocket websocket, com.neovisionaries.ws.client.WebSocketFrame frame) {
                            }

                            @java.lang.Override
                            public void onBinaryFrame(com.neovisionaries.ws.client.WebSocket websocket, com.neovisionaries.ws.client.WebSocketFrame frame) {
                            }

                            @java.lang.Override
                            public void onCloseFrame(com.neovisionaries.ws.client.WebSocket websocket, com.neovisionaries.ws.client.WebSocketFrame frame) {
                            }

                            @java.lang.Override
                            public void onPingFrame(com.neovisionaries.ws.client.WebSocket websocket, com.neovisionaries.ws.client.WebSocketFrame frame) {
                            }

                            @java.lang.Override
                            public void onPongFrame(com.neovisionaries.ws.client.WebSocket websocket, com.neovisionaries.ws.client.WebSocketFrame frame) {
                            }

                            @java.lang.Override
                            public void onTextMessage(com.neovisionaries.ws.client.WebSocket websocket, java.lang.String s) {
                                me.ccrama.redditslide.util.LogUtil.v("Recieved" + s);
                                if (s.contains("\"type\": \"update\"")) {
                                    try {
                                        net.dean.jraw.models.LiveUpdate u = new net.dean.jraw.models.LiveUpdate(o.readTree(s).get("payload").get("data"));
                                        updates.add(0, u);
                                        runOnUiThread(new java.lang.Runnable() {
                                            @java.lang.Override
                                            public void run() {
                                                adapter.notifyItemInserted(0);
                                                baseRecycler.smoothScrollToPosition(0);
                                            }
                                        });
                                    } catch (java.io.IOException e) {
                                        e.printStackTrace();
                                    }
                                } else if (s.contains("embeds_ready")) {
                                    java.lang.String node = updates.get(0).getDataNode().toString();
                                    me.ccrama.redditslide.util.LogUtil.v("Getting");
                                    try {
                                        node = node.replace("\"embeds\":[]", "\"embeds\":" + o.readTree(s).get("payload").get("media_embeds").toString());
                                        net.dean.jraw.models.LiveUpdate u = new net.dean.jraw.models.LiveUpdate(o.readTree(node));
                                        updates.set(0, u);
                                        runOnUiThread(new java.lang.Runnable() {
                                            @java.lang.Override
                                            public void run() {
                                                adapter.notifyItemChanged(0);
                                            }
                                        });
                                    } catch (java.lang.Exception e) {
                                        e.printStackTrace();
                                    }
                                }/* todoelse if(s.contains("delete")){
                                updates.remove(0);
                                adapter.notifyItemRemoved(0);
                                }
                                 */

                            }

                            @java.lang.Override
                            public void onBinaryMessage(com.neovisionaries.ws.client.WebSocket websocket, byte[] binary) {
                            }

                            @java.lang.Override
                            public void onSendingFrame(com.neovisionaries.ws.client.WebSocket websocket, com.neovisionaries.ws.client.WebSocketFrame frame) {
                            }

                            @java.lang.Override
                            public void onFrameSent(com.neovisionaries.ws.client.WebSocket websocket, com.neovisionaries.ws.client.WebSocketFrame frame) {
                            }

                            @java.lang.Override
                            public void onFrameUnsent(com.neovisionaries.ws.client.WebSocket websocket, com.neovisionaries.ws.client.WebSocketFrame frame) {
                            }

                            @java.lang.Override
                            public void onError(com.neovisionaries.ws.client.WebSocket websocket, com.neovisionaries.ws.client.WebSocketException cause) {
                            }

                            @java.lang.Override
                            public void onFrameError(com.neovisionaries.ws.client.WebSocket websocket, com.neovisionaries.ws.client.WebSocketException cause, com.neovisionaries.ws.client.WebSocketFrame frame) {
                            }

                            @java.lang.Override
                            public void onMessageError(com.neovisionaries.ws.client.WebSocket websocket, com.neovisionaries.ws.client.WebSocketException cause, java.util.List<com.neovisionaries.ws.client.WebSocketFrame> frames) {
                            }

                            @java.lang.Override
                            public void onMessageDecompressionError(com.neovisionaries.ws.client.WebSocket websocket, com.neovisionaries.ws.client.WebSocketException cause, byte[] compressed) {
                            }

                            @java.lang.Override
                            public void onTextMessageError(com.neovisionaries.ws.client.WebSocket websocket, com.neovisionaries.ws.client.WebSocketException cause, byte[] data) {
                            }

                            @java.lang.Override
                            public void onSendError(com.neovisionaries.ws.client.WebSocket websocket, com.neovisionaries.ws.client.WebSocketException cause, com.neovisionaries.ws.client.WebSocketFrame frame) {
                            }

                            @java.lang.Override
                            public void onUnexpectedError(com.neovisionaries.ws.client.WebSocket websocket, com.neovisionaries.ws.client.WebSocketException cause) {
                            }

                            @java.lang.Override
                            public void handleCallbackError(com.neovisionaries.ws.client.WebSocket websocket, java.lang.Throwable cause) {
                            }

                            @java.lang.Override
                            public void onSendingHandshake(com.neovisionaries.ws.client.WebSocket websocket, java.lang.String requestLine, java.util.List<java.lang.String[]> headers) {
                            }
                        });
                        ws.connect();
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    } catch (com.neovisionaries.ws.client.WebSocketException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public class PaginatorAdapter extends android.support.v7.widget.RecyclerView.Adapter<me.ccrama.redditslide.Activities.LiveThread.PaginatorAdapter.ItemHolder> {
        private android.view.LayoutInflater layoutInflater;

        public PaginatorAdapter(android.content.Context context) {
            layoutInflater = android.view.LayoutInflater.from(context);
        }

        @java.lang.Override
        public me.ccrama.redditslide.Activities.LiveThread.PaginatorAdapter.ItemHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View itemView = layoutInflater.inflate(me.ccrama.redditslide.R.layout.live_list_item, parent, false);
            return new me.ccrama.redditslide.Activities.LiveThread.PaginatorAdapter.ItemHolder(itemView);
        }

        @java.lang.Override
        public void onBindViewHolder(final me.ccrama.redditslide.Activities.LiveThread.PaginatorAdapter.ItemHolder holder, int position) {
            final net.dean.jraw.models.LiveUpdate u = updates.get(position);
            holder.title.setText((("/u/" + u.getAuthor()) + " ") + me.ccrama.redditslide.TimeUtils.getTimeAgo(u.getCreated().getTime(), me.ccrama.redditslide.Activities.LiveThread.this));
            if (u.getBody().isEmpty()) {
                holder.info.setVisibility(android.view.View.GONE);
            } else {
                holder.info.setVisibility(android.view.View.VISIBLE);
                holder.info.setTextHtml(android.text.Html.fromHtml(u.getDataNode().get("body_html").asText()), "NO SUBREDDIT");
            }
            holder.title.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.LiveThread.this, me.ccrama.redditslide.Activities.Profile.class);
                    i.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_PROFILE, u.getAuthor());
                    startActivity(i);
                }
            });
            holder.imageArea.setVisibility(android.view.View.GONE);
            holder.twitterArea.setVisibility(android.view.View.GONE);
            holder.twitterArea.stopLoading();
            if (u.getEmbeds().size() == 0) {
                holder.go.setVisibility(android.view.View.GONE);
            } else {
                final java.lang.String url = u.getEmbeds().get(0).getUrl();
                holder.go.setVisibility(android.view.View.VISIBLE);
                holder.go.setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.LiveThread.this, me.ccrama.redditslide.Activities.Website.class);
                        i.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_URL, url);
                        startActivity(i);
                    }
                });
                final java.lang.String host = java.net.URI.create(url).getHost().toLowerCase(java.util.Locale.ENGLISH);
                if (me.ccrama.redditslide.ContentType.hostContains(host, "imgur.com")) {
                    me.ccrama.redditslide.util.LogUtil.v("Imgur");
                    holder.imageArea.setVisibility(android.view.View.VISIBLE);
                    holder.imageArea.setOnClickListener(new android.view.View.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.view.View v) {
                            holder.go.callOnClick();
                        }
                    });
                    ((me.ccrama.redditslide.Reddit) (getApplicationContext())).getImageLoader().displayImage(url, holder.imageArea);
                } else if (me.ccrama.redditslide.ContentType.hostContains(host, "twitter.com")) {
                    me.ccrama.redditslide.util.LogUtil.v("Twitter");
                    holder.twitterArea.setVisibility(android.view.View.VISIBLE);
                    new me.ccrama.redditslide.Activities.LiveThread.PaginatorAdapter.LoadTwitter(holder.twitterArea, url).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        }

        public class LoadTwitter extends android.os.AsyncTask<java.lang.String, java.lang.Void, java.lang.Void> {
            private okhttp3.OkHttpClient client;

            private com.google.gson.Gson gson;

            java.lang.String url;

            private android.webkit.WebView view;

            me.ccrama.redditslide.util.TwitterObject twitter;

            public LoadTwitter(@org.jetbrains.annotations.NotNull
            android.webkit.WebView view, @org.jetbrains.annotations.NotNull
            java.lang.String url) {
                this.view = view;
                this.url = url;
                client = me.ccrama.redditslide.Reddit.client;
                gson = new com.google.gson.Gson();
            }

            public void parseJson() {
                try {
                    com.google.gson.JsonObject result = me.ccrama.redditslide.util.HttpUtil.getJsonObject(client, gson, "https://publish.twitter.com/oembed?url=" + url, null);
                    me.ccrama.redditslide.util.LogUtil.v("Got " + android.text.Html.fromHtml(result.toString()));
                    twitter = new com.fasterxml.jackson.databind.ObjectMapper().readValue(result.toString(), me.ccrama.redditslide.util.TwitterObject.class);
                } catch (java.lang.Exception e) {
                    e.printStackTrace();
                }
            }

            @java.lang.Override
            protected java.lang.Void doInBackground(final java.lang.String... sub) {
                parseJson();
                return null;
            }

            @java.lang.Override
            public void onPostExecute(java.lang.Void aVoid) {
                if ((twitter != null) && (twitter.getHtml() != null)) {
                    view.loadData(twitter.getHtml().replace("//platform.twitter", "https://platform.twitter"), "text/html", "UTF-8");
                }
            }
        }

        @java.lang.Override
        public int getItemCount() {
            return updates.size();
        }

        public class ItemHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
            android.widget.TextView title;

            me.ccrama.redditslide.SpoilerRobotoTextView info;

            android.widget.ImageView imageArea;

            android.webkit.WebView twitterArea;

            android.view.View go;

            public ItemHolder(android.view.View itemView) {
                super(itemView);
                title = itemView.findViewById(me.ccrama.redditslide.R.id.title);
                info = itemView.findViewById(me.ccrama.redditslide.R.id.body);
                go = itemView.findViewById(me.ccrama.redditslide.R.id.go);
                imageArea = itemView.findViewById(me.ccrama.redditslide.R.id.image_area);
                twitterArea = itemView.findViewById(me.ccrama.redditslide.R.id.twitter_area);
                twitterArea.setWebChromeClient(new android.webkit.WebChromeClient());
                twitterArea.getSettings().setJavaScriptEnabled(true);
                twitterArea.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                twitterArea.setLayerType(android.webkit.WebView.LAYER_TYPE_SOFTWARE, null);
            }
        }
    }

    public void doLiveSidebar() {
        findViewById(me.ccrama.redditslide.R.id.loader).setVisibility(android.view.View.GONE);
        final android.view.View dialoglayout = findViewById(me.ccrama.redditslide.R.id.sidebarsub);
        dialoglayout.findViewById(me.ccrama.redditslide.R.id.sub_stuff).setVisibility(android.view.View.GONE);
        ((android.widget.TextView) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.sub_infotitle))).setText((thread.getState() ? "LIVE: " : "") + thread.getTitle());
        ((android.widget.TextView) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.active_users))).setText(thread.getLocalizedViewerCount() + " viewing");
        ((android.widget.TextView) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.active_users))).setText(thread.getLocalizedViewerCount());
        {
            final java.lang.String text = thread.getDataNode().get("resources_html").asText();
            final me.ccrama.redditslide.SpoilerRobotoTextView body = ((me.ccrama.redditslide.SpoilerRobotoTextView) (findViewById(me.ccrama.redditslide.R.id.sidebar_text)));
            me.ccrama.redditslide.Views.CommentOverflow overflow = ((me.ccrama.redditslide.Views.CommentOverflow) (findViewById(me.ccrama.redditslide.R.id.commentOverflow)));
            setViews(text, "none", body, overflow);
        }
        {
            final java.lang.String text = thread.getDataNode().get("description_html").asText();
            final me.ccrama.redditslide.SpoilerRobotoTextView body = ((me.ccrama.redditslide.SpoilerRobotoTextView) (findViewById(me.ccrama.redditslide.R.id.sub_title)));
            me.ccrama.redditslide.Views.CommentOverflow overflow = ((me.ccrama.redditslide.Views.CommentOverflow) (findViewById(me.ccrama.redditslide.R.id.sub_title_overflow)));
            setViews(text, "none", body, overflow);
        }
    }

    private void setViews(java.lang.String rawHTML, java.lang.String subreddit, me.ccrama.redditslide.SpoilerRobotoTextView firstTextView, me.ccrama.redditslide.Views.CommentOverflow commentOverflow) {
        if (rawHTML.isEmpty()) {
            return;
        }
        java.util.List<java.lang.String> blocks = me.ccrama.redditslide.util.SubmissionParser.getBlocks(rawHTML);
        int startIndex = 0;
        // the <div class="md"> case is when the body contains a table or code block first
        if (!blocks.get(0).equals("<div class=\"md\">")) {
            firstTextView.setVisibility(android.view.View.VISIBLE);
            firstTextView.setTextHtml(blocks.get(0), subreddit);
            startIndex = 1;
        } else {
            firstTextView.setText("");
            firstTextView.setVisibility(android.view.View.GONE);
        }
        if (blocks.size() > 1) {
            if (startIndex == 0) {
                commentOverflow.setViews(blocks, subreddit);
            } else {
                commentOverflow.setViews(blocks.subList(startIndex, blocks.size()), subreddit);
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