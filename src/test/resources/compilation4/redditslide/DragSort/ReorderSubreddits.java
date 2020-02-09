/* Copyright (C) 2013 The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package me.ccrama.redditslide.DragSort;
import java.util.Locale;
import java.util.HashMap;
import me.ccrama.redditslide.Visuals.Palette;
import java.util.ArrayList;
import me.ccrama.redditslide.ColorPreferences;
import me.ccrama.redditslide.CaseInsensitiveArrayList;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.UserSubscriptions;
import java.util.List;
import me.ccrama.redditslide.Activities.BaseActivityAnim;
import me.ccrama.redditslide.Activities.SettingsTheme;
import static me.ccrama.redditslide.UserSubscriptions.setPinned;
import me.ccrama.redditslide.Fragments.SettingsThemeFragment;
public class ReorderSubreddits extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    private me.ccrama.redditslide.CaseInsensitiveArrayList subs;

    private me.ccrama.redditslide.DragSort.ReorderSubreddits.CustomAdapter adapter;

    private android.support.v7.widget.RecyclerView recyclerView;

    private java.lang.String input;

    public static final java.lang.String MULTI_REDDIT = "/m/";

    android.view.MenuItem subscribe;

    @java.lang.Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        android.view.MenuInflater inflater = getMenuInflater();
        inflater.inflate(me.ccrama.redditslide.R.menu.reorder_subs, menu);
        subscribe = menu.findItem(me.ccrama.redditslide.R.id.alphabetize_subscribe);
        subscribe.setChecked(me.ccrama.redditslide.SettingValues.alphabetizeOnSubscribe);
        return true;
    }

    @java.lang.Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                return true;
            case me.ccrama.redditslide.R.id.refresh :
                done = 0;
                final android.app.Dialog d = new com.afollestad.materialdialogs.MaterialDialog.Builder(this).title(me.ccrama.redditslide.R.string.general_sub_sync).content(me.ccrama.redditslide.R.string.misc_please_wait).progress(true, 100).cancelable(false).show();
                new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.util.ArrayList<java.lang.String>>() {
                    @java.lang.Override
                    protected java.util.ArrayList<java.lang.String> doInBackground(java.lang.Void... params) {
                        java.util.ArrayList<java.lang.String> newSubs = new java.util.ArrayList<>(me.ccrama.redditslide.UserSubscriptions.syncSubreddits(me.ccrama.redditslide.DragSort.ReorderSubreddits.this));
                        me.ccrama.redditslide.UserSubscriptions.syncMultiReddits(me.ccrama.redditslide.DragSort.ReorderSubreddits.this);
                        return newSubs;
                    }

                    @java.lang.Override
                    protected void onPostExecute(java.util.ArrayList<java.lang.String> newSubs) {
                        d.dismiss();
                        // Determine if we should insert subreddits at the end of the list or sorted
                        boolean sorted = subs.equals(me.ccrama.redditslide.UserSubscriptions.sortNoExtras(subs));
                        android.content.res.Resources res = getResources();
                        for (java.lang.String s : newSubs) {
                            if (!subs.contains(s)) {
                                done++;
                                subs.add(s);
                            }
                        }
                        if (sorted && (done > 0)) {
                            subs = me.ccrama.redditslide.UserSubscriptions.sortNoExtras(subs);
                            adapter = new me.ccrama.redditslide.DragSort.ReorderSubreddits.CustomAdapter(subs);
                            recyclerView.setAdapter(adapter);
                        } else if (done > 0) {
                            adapter.notifyDataSetChanged();
                            recyclerView.smoothScrollToPosition(subs.size());
                        }
                        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.DragSort.ReorderSubreddits.this).setTitle(me.ccrama.redditslide.R.string.reorder_sync_complete).setMessage(res.getQuantityString(me.ccrama.redditslide.R.plurals.reorder_subs_added, done, done)).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, null).show();
                    }
                }.execute();
                return true;
            case me.ccrama.redditslide.R.id.alphabetize :
                subs = me.ccrama.redditslide.UserSubscriptions.sortNoExtras(subs);
                adapter = new me.ccrama.redditslide.DragSort.ReorderSubreddits.CustomAdapter(subs);
                // adapter.setHasStableIds(true);
                recyclerView.setAdapter(adapter);
                return true;
            case me.ccrama.redditslide.R.id.alphabetize_subscribe :
                me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_ALPHABETIZE_SUBSCRIBE, !me.ccrama.redditslide.SettingValues.alphabetizeOnSubscribe).apply();
                me.ccrama.redditslide.SettingValues.alphabetizeOnSubscribe = !me.ccrama.redditslide.SettingValues.alphabetizeOnSubscribe;
                if (subscribe != null)
                    subscribe.setChecked(me.ccrama.redditslide.SettingValues.alphabetizeOnSubscribe);

                return true;
            case me.ccrama.redditslide.R.id.info :
                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setTitle(me.ccrama.redditslide.R.string.reorder_subs_FAQ).setMessage(me.ccrama.redditslide.R.string.sorting_faq).show();
                return true;
        }
        return false;
    }

    @java.lang.Override
    public void onPause() {
        try {
            me.ccrama.redditslide.UserSubscriptions.setSubscriptions(new me.ccrama.redditslide.CaseInsensitiveArrayList(subs));
            me.ccrama.redditslide.Fragments.SettingsThemeFragment.changed = true;
        } catch (java.lang.Exception e) {
        }
        super.onPause();
    }

    @java.lang.Override
    public void onBackPressed() {
        if (isMultiple) {
            chosen = new java.util.ArrayList<>();
            doOldToolbar();
            adapter.notifyDataSetChanged();
            isMultiple = false;
        } else {
            super.onBackPressed();
        }
    }

    private java.util.ArrayList<java.lang.String> chosen = new java.util.ArrayList<>();

    java.util.HashMap<java.lang.String, java.lang.Boolean> isSubscribed;

    private boolean isMultiple;

    private int done = 0;

    @java.lang.Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        disableSwipeBackLayout();
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_sort);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.settings_manage_subscriptions, false, true);
        mToolbar.setPopupTheme(new me.ccrama.redditslide.ColorPreferences(this).getFontStyle().getBaseId());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        isSubscribed = new java.util.HashMap<>();
        if (me.ccrama.redditslide.Authentication.isLoggedIn) {
            new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
                boolean success = true;

                @java.lang.Override
                protected java.lang.Void doInBackground(java.lang.Void... params) {
                    java.util.ArrayList<net.dean.jraw.models.Subreddit> subs = new java.util.ArrayList<>();
                    net.dean.jraw.paginators.UserSubredditsPaginator p = new net.dean.jraw.paginators.UserSubredditsPaginator(me.ccrama.redditslide.Authentication.reddit, "subscriber");
                    try {
                        while (p.hasNext()) {
                            subs.addAll(p.next());
                        } 
                    } catch (java.lang.Exception e) {
                        success = false;
                        return null;
                    }
                    for (net.dean.jraw.models.Subreddit s : subs) {
                        isSubscribed.put(s.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), true);
                    }
                    if (me.ccrama.redditslide.UserSubscriptions.multireddits == null) {
                        me.ccrama.redditslide.UserSubscriptions.loadMultireddits();
                    }
                    return null;
                }

                @java.lang.Override
                protected void onPostExecute(java.lang.Void aVoid) {
                    if (success) {
                        d.dismiss();
                        doShowSubs();
                    } else {
                        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.DragSort.ReorderSubreddits.this).setTitle(me.ccrama.redditslide.R.string.err_title).setMessage(me.ccrama.redditslide.R.string.misc_please_try_again_soon).setCancelable(false).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
                    }
                }

                android.app.Dialog d;

                @java.lang.Override
                protected void onPreExecute() {
                    d = new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.DragSort.ReorderSubreddits.this).progress(true, 100).content(me.ccrama.redditslide.R.string.misc_please_wait).title(me.ccrama.redditslide.R.string.reorder_loading_title).cancelable(false).show();
                }
            }.execute();
        } else {
            doShowSubs();
        }
    }

    public void doShowSubs() {
        subs = new me.ccrama.redditslide.CaseInsensitiveArrayList(me.ccrama.redditslide.UserSubscriptions.getSubscriptions(this));
        recyclerView = ((android.support.v7.widget.RecyclerView) (findViewById(me.ccrama.redditslide.R.id.subslist)));
        recyclerView.setLayoutManager(new android.support.v7.widget.LinearLayoutManager(this));
        recyclerView.setItemAnimator(null);
        me.ccrama.redditslide.DragSort.DragSortRecycler dragSortRecycler = new me.ccrama.redditslide.DragSort.DragSortRecycler();
        dragSortRecycler.setViewHandleId();
        dragSortRecycler.setFloatingAlpha();
        dragSortRecycler.setAutoScrollSpeed();
        dragSortRecycler.setAutoScrollWindow();
        dragSortRecycler.setOnItemMovedListener(new me.ccrama.redditslide.DragSort.DragSortRecycler.OnItemMovedListener() {
            @java.lang.Override
            public void onItemMoved(int from, int to) {
                if (to == subs.size()) {
                    to -= 1;
                }
                java.lang.String item = subs.remove(from);
                subs.add(to, item);
                adapter.notifyDataSetChanged();
                me.ccrama.redditslide.CaseInsensitiveArrayList pinned = me.ccrama.redditslide.UserSubscriptions.getPinned();
                if (pinned.contains(item) && (pinned.size() != 1)) {
                    pinned.remove(item);
                    if (to > pinned.size()) {
                        to = pinned.size();
                    }
                    pinned.add(to, item);
                    me.ccrama.redditslide.UserSubscriptions.setPinned(pinned);
                }
            }
        });
        dragSortRecycler.setOnDragStateChangedListener(new me.ccrama.redditslide.DragSort.DragSortRecycler.OnDragStateChangedListener() {
            @java.lang.Override
            public void onDragStart() {
            }

            @java.lang.Override
            public void onDragStop() {
            }
        });
        final com.getbase.floatingactionbutton.FloatingActionsMenu fab = ((com.getbase.floatingactionbutton.FloatingActionsMenu) (findViewById(me.ccrama.redditslide.R.id.add)));
        {
            com.getbase.floatingactionbutton.FloatingActionButton collection = ((com.getbase.floatingactionbutton.FloatingActionButton) (findViewById(me.ccrama.redditslide.R.id.collection)));
            android.graphics.drawable.Drawable icon = android.support.v4.content.res.ResourcesCompat.getDrawable(getResources(), me.ccrama.redditslide.R.drawable.collection, null);
            collection.setIconDrawable(icon);
            collection.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    fab.collapse();
                    if ((me.ccrama.redditslide.UserSubscriptions.multireddits != null) && (!me.ccrama.redditslide.UserSubscriptions.multireddits.isEmpty())) {
                        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.DragSort.ReorderSubreddits.this).setTitle(me.ccrama.redditslide.R.string.create_or_import_multi).setPositiveButton(me.ccrama.redditslide.R.string.btn_new, new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                doCollection();
                            }
                        }).setNegativeButton(me.ccrama.redditslide.R.string.btn_import_multi, new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                final java.lang.String[] multis = new java.lang.String[me.ccrama.redditslide.UserSubscriptions.multireddits.size()];
                                int i = 0;
                                for (net.dean.jraw.models.MultiReddit m : me.ccrama.redditslide.UserSubscriptions.multireddits) {
                                    multis[i] = m.getDisplayName();
                                    i++;
                                }
                                com.afollestad.materialdialogs.MaterialDialog.Builder builder = new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.DragSort.ReorderSubreddits.this);
                                builder.title(me.ccrama.redditslide.R.string.reorder_subreddits_title).items(multis).itemsCallbackSingleChoice(-1, new com.afollestad.materialdialogs.MaterialDialog.ListCallbackSingleChoice() {
                                    @java.lang.Override
                                    public boolean onSelection(com.afollestad.materialdialogs.MaterialDialog dialog, android.view.View itemView, int which, java.lang.CharSequence text) {
                                        java.lang.String name = multis[which];
                                        net.dean.jraw.models.MultiReddit r = me.ccrama.redditslide.UserSubscriptions.getMultiredditByDisplayName(name);
                                        java.lang.StringBuilder b = new java.lang.StringBuilder();
                                        for (net.dean.jraw.models.MultiSubreddit s : r.getSubreddits()) {
                                            b.append(s.getDisplayName());
                                            b.append("+");
                                        }
                                        int pos = addSubAlphabetically(me.ccrama.redditslide.DragSort.ReorderSubreddits.MULTI_REDDIT + r.getDisplayName());
                                        me.ccrama.redditslide.UserSubscriptions.setSubNameToProperties(me.ccrama.redditslide.DragSort.ReorderSubreddits.MULTI_REDDIT + r.getDisplayName(), b.toString());
                                        adapter.notifyDataSetChanged();
                                        recyclerView.smoothScrollToPosition(pos);
                                        return false;
                                    }
                                }).show();
                            }
                        }).show();
                    } else {
                        doCollection();
                    }
                }
            });
        }
        {
            com.getbase.floatingactionbutton.FloatingActionButton collection = ((com.getbase.floatingactionbutton.FloatingActionButton) (findViewById(me.ccrama.redditslide.R.id.sub)));
            android.graphics.drawable.Drawable icon = android.support.v4.content.res.ResourcesCompat.getDrawable(getResources(), me.ccrama.redditslide.R.drawable.sub, null);
            collection.setIconDrawable(icon);
            collection.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    fab.collapse();
                    com.afollestad.materialdialogs.MaterialDialog.Builder b = new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.DragSort.ReorderSubreddits.this).title(me.ccrama.redditslide.R.string.reorder_add_or_search_subreddit).alwaysCallInputCallback().input(getString(me.ccrama.redditslide.R.string.reorder_subreddit_name), null, false, new com.afollestad.materialdialogs.MaterialDialog.InputCallback() {
                        @java.lang.Override
                        public void onInput(com.afollestad.materialdialogs.MaterialDialog dialog, java.lang.CharSequence raw) {
                            input = raw.toString();
                        }
                    }).positiveText(me.ccrama.redditslide.R.string.btn_add).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                        @java.lang.Override
                        public void onClick(com.afollestad.materialdialogs.MaterialDialog dialog, com.afollestad.materialdialogs.DialogAction which) {
                            new me.ccrama.redditslide.DragSort.ReorderSubreddits.AsyncGetSubreddit().execute(input);
                        }
                    }).negativeText(me.ccrama.redditslide.R.string.btn_cancel).onNegative(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                        @java.lang.Override
                        public void onClick(com.afollestad.materialdialogs.MaterialDialog dialog, com.afollestad.materialdialogs.DialogAction which) {
                        }
                    });
                    b.show();
                }
            });
        }
        {
            com.getbase.floatingactionbutton.FloatingActionButton collection = ((com.getbase.floatingactionbutton.FloatingActionButton) (findViewById(me.ccrama.redditslide.R.id.domain)));
            android.graphics.drawable.Drawable icon = android.support.v4.content.res.ResourcesCompat.getDrawable(getResources(), me.ccrama.redditslide.R.drawable.link, null);
            collection.setIconDrawable(icon);
            collection.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    fab.collapse();
                    new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.DragSort.ReorderSubreddits.this).title(me.ccrama.redditslide.R.string.reorder_add_domain).alwaysCallInputCallback().input("example.com" + getString(me.ccrama.redditslide.R.string.reorder_domain_placeholder), null, false, new com.afollestad.materialdialogs.MaterialDialog.InputCallback() {
                        @java.lang.Override
                        public void onInput(com.afollestad.materialdialogs.MaterialDialog dialog, java.lang.CharSequence raw) {
                            input = raw.toString().replaceAll("\\s", "");// remove whitespace from input

                            if (input.contains(".")) {
                                dialog.getActionButton(com.afollestad.materialdialogs.DialogAction.POSITIVE).setEnabled(true);
                            } else {
                                dialog.getActionButton(com.afollestad.materialdialogs.DialogAction.POSITIVE).setEnabled(false);
                            }
                        }
                    }).positiveText(me.ccrama.redditslide.R.string.btn_add).inputRange(1, 35).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                        @java.lang.Override
                        public void onClick(com.afollestad.materialdialogs.MaterialDialog dialog, com.afollestad.materialdialogs.DialogAction which) {
                            try {
                                java.lang.String url = input;
                                java.util.List<java.lang.String> sortedSubs = me.ccrama.redditslide.UserSubscriptions.sortNoExtras(subs);
                                if (sortedSubs.equals(subs)) {
                                    subs.add(url);
                                    subs = me.ccrama.redditslide.UserSubscriptions.sortNoExtras(subs);
                                    adapter = new me.ccrama.redditslide.DragSort.ReorderSubreddits.CustomAdapter(subs);
                                    recyclerView.setAdapter(adapter);
                                } else {
                                    int pos = addSubAlphabetically(url);
                                    adapter.notifyDataSetChanged();
                                    recyclerView.smoothScrollToPosition(pos);
                                }
                            } catch (java.lang.Exception e) {
                                e.printStackTrace();
                                // todo make this better
                                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.DragSort.ReorderSubreddits.this).setTitle(me.ccrama.redditslide.R.string.reorder_url_err).setMessage(me.ccrama.redditslide.R.string.misc_please_try_again).show();
                            }
                        }
                    }).negativeText(me.ccrama.redditslide.R.string.btn_cancel).onNegative(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                        @java.lang.Override
                        public void onClick(com.afollestad.materialdialogs.MaterialDialog dialog, com.afollestad.materialdialogs.DialogAction which) {
                        }
                    }).show();
                }
            });
        }
        recyclerView.addItemDecoration(dragSortRecycler);
        recyclerView.addOnItemTouchListener(dragSortRecycler);
        recyclerView.addOnScrollListener(dragSortRecycler.getScrollListener());
        dragSortRecycler.setViewHandleId();
        if ((subs != null) && (!subs.isEmpty())) {
            adapter = new me.ccrama.redditslide.DragSort.ReorderSubreddits.CustomAdapter(subs);
            // adapter.setHasStableIds(true);
            recyclerView.setAdapter(adapter);
        } else {
            subs = new me.ccrama.redditslide.CaseInsensitiveArrayList();
        }
        recyclerView.addOnScrollListener(new android.support.v7.widget.RecyclerView.OnScrollListener() {
            @java.lang.Override
            public void onScrolled(android.support.v7.widget.RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getScrollState() == android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING) {
                    diff += dy;
                } else {
                    diff = 0;
                }
                if ((dy <= 0) && (fab.getId() != 0)) {
                } else {
                    fab.collapse();
                }
            }
        });
    }

    public int diff;

    public void doCollection() {
        final java.util.ArrayList<java.lang.String> subs2 = me.ccrama.redditslide.UserSubscriptions.sort(me.ccrama.redditslide.UserSubscriptions.getSubscriptions(this));
        subs2.remove("frontpage");
        subs2.remove("all");
        java.util.ArrayList<java.lang.String> toRemove = new java.util.ArrayList<>();
        for (java.lang.String s : subs2) {
            if (s.contains(".") || s.contains(me.ccrama.redditslide.DragSort.ReorderSubreddits.MULTI_REDDIT)) {
                toRemove.add(s);
            }
        }
        subs2.removeAll(toRemove);
        final java.lang.CharSequence[] subsAsChar = subs2.toArray(new java.lang.CharSequence[subs2.size()]);
        com.afollestad.materialdialogs.MaterialDialog.Builder builder = new com.afollestad.materialdialogs.MaterialDialog.Builder(this);
        builder.title(me.ccrama.redditslide.R.string.reorder_subreddits_title).items(subsAsChar).itemsCallbackMultiChoice(null, new com.afollestad.materialdialogs.MaterialDialog.ListCallbackMultiChoice() {
            @java.lang.Override
            public boolean onSelection(com.afollestad.materialdialogs.MaterialDialog dialog, java.lang.Integer[] which, java.lang.CharSequence[] text) {
                java.util.ArrayList<java.lang.String> selectedSubs = new java.util.ArrayList<>();
                for (int i : which) {
                    selectedSubs.add(subsAsChar[i].toString());
                }
                java.lang.StringBuilder b = new java.lang.StringBuilder();
                for (java.lang.String s : selectedSubs) {
                    b.append(s);
                    b.append("+");
                }
                java.lang.String finalS = b.toString().substring(0, b.length() - 1);
                android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), finalS);
                int pos = addSubAlphabetically(finalS);
                adapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(pos);
                return true;
            }
        }).positiveText(me.ccrama.redditslide.R.string.btn_add).negativeText(me.ccrama.redditslide.R.string.btn_cancel).show();
    }

    public void doAddSub(java.lang.String subreddit) {
        subreddit = subreddit.toLowerCase(java.util.Locale.ENGLISH);
        java.util.List<java.lang.String> sortedSubs = me.ccrama.redditslide.UserSubscriptions.sortNoExtras(subs);
        if (sortedSubs.equals(subs)) {
            subs.add(subreddit);
            subs = me.ccrama.redditslide.UserSubscriptions.sortNoExtras(subs);
            adapter = new me.ccrama.redditslide.DragSort.ReorderSubreddits.CustomAdapter(subs);
            recyclerView.setAdapter(adapter);
        } else {
            int pos = addSubAlphabetically(subreddit);
            adapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(pos);
        }
    }

    private int addSubAlphabetically(java.lang.String finalS) {
        int i = subs.size() - 1;
        while ((i >= 0) && (finalS.compareTo(subs.get(i)) < 0)) {
            i--;
        } 
        i += 1;
        subs.add(i, finalS);
        return i;
    }

    private class AsyncGetSubreddit extends android.os.AsyncTask<java.lang.String, java.lang.Void, net.dean.jraw.models.Subreddit> {
        @java.lang.Override
        public void onPostExecute(net.dean.jraw.models.Subreddit subreddit) {
            if (subreddit != null) {
                doAddSub(subreddit.getDisplayName());
            } else if (isSpecial(sub)) {
                doAddSub(sub);
            }
        }

        java.util.ArrayList<net.dean.jraw.models.Subreddit> otherSubs;

        java.lang.String sub;

        @java.lang.Override
        protected net.dean.jraw.models.Subreddit doInBackground(final java.lang.String... params) {
            sub = params[0];
            if (isSpecial(sub))
                return null;

            try {
                return subs.contains(params[0]) ? null : me.ccrama.redditslide.Authentication.reddit.getSubreddit(params[0]);
            } catch (java.lang.Exception e) {
                otherSubs = new java.util.ArrayList<>();
                net.dean.jraw.paginators.SubredditSearchPaginator p = new net.dean.jraw.paginators.SubredditSearchPaginator(me.ccrama.redditslide.Authentication.reddit, sub);
                while (p.hasNext()) {
                    otherSubs.addAll(p.next());
                } 
                if (otherSubs.isEmpty()) {
                    runOnUiThread(new java.lang.Runnable() {
                        @java.lang.Override
                        public void run() {
                            try {
                                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.DragSort.ReorderSubreddits.this).setTitle(me.ccrama.redditslide.R.string.subreddit_err).setMessage(getString(me.ccrama.redditslide.R.string.subreddit_err_msg, params[0])).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                                    @java.lang.Override
                                    public void onClick(android.content.DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {
                                    @java.lang.Override
                                    public void onDismiss(android.content.DialogInterface dialog) {
                                    }
                                }).show();
                            } catch (java.lang.Exception ignored) {
                            }
                        }
                    });
                } else {
                    runOnUiThread(new java.lang.Runnable() {
                        @java.lang.Override
                        public void run() {
                            try {
                                final java.util.ArrayList<java.lang.String> subs = new java.util.ArrayList<>();
                                for (net.dean.jraw.models.Subreddit s : otherSubs) {
                                    subs.add(s.getDisplayName());
                                }
                                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.DragSort.ReorderSubreddits.this).setTitle(me.ccrama.redditslide.R.string.reorder_not_found_err).setItems(subs.toArray(new java.lang.String[subs.size()]), new android.content.DialogInterface.OnClickListener() {
                                    @java.lang.Override
                                    public void onClick(android.content.DialogInterface dialog, int which) {
                                        doAddSub(subs.get(which));
                                    }
                                }).setPositiveButton(me.ccrama.redditslide.R.string.btn_cancel, null).show();
                            } catch (java.lang.Exception ignored) {
                            }
                        }
                    });
                }
            }
            return null;
        }
    }

    public void doOldToolbar() {
        mToolbar = ((android.support.v7.widget.Toolbar) (findViewById(me.ccrama.redditslide.R.id.toolbar)));
        mToolbar.setVisibility(android.view.View.VISIBLE);
    }

    public class CustomAdapter extends android.support.v7.widget.RecyclerView.Adapter<android.support.v7.widget.RecyclerView.ViewHolder> {
        private final java.util.ArrayList<java.lang.String> items;

        public CustomAdapter(java.util.ArrayList<java.lang.String> items) {
            this.items = items;
        }

        @java.lang.Override
        public android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            if (viewType == 2) {
                android.view.View v = android.view.LayoutInflater.from(parent.getContext()).inflate(me.ccrama.redditslide.R.layout.spacer, parent, false);
                return new me.ccrama.redditslide.DragSort.ReorderSubreddits.CustomAdapter.SpacerViewHolder(v);
            }
            android.view.View v = android.view.LayoutInflater.from(parent.getContext()).inflate(me.ccrama.redditslide.R.layout.subforsublistdrag, parent, false);
            return new me.ccrama.redditslide.DragSort.ReorderSubreddits.CustomAdapter.ViewHolder(v);
        }

        @java.lang.Override
        public int getItemViewType(int position) {
            if (position == items.size()) {
                return 2;
            }
            return 1;
        }

        public void doNewToolbar() {
            mToolbar.setVisibility(android.view.View.GONE);
            mToolbar = ((android.support.v7.widget.Toolbar) (findViewById(me.ccrama.redditslide.R.id.toolbar2)));
            mToolbar.setTitle(getResources().getQuantityString(me.ccrama.redditslide.R.plurals.reorder_selected, chosen.size(), chosen.size()));
            mToolbar.findViewById(me.ccrama.redditslide.R.id.delete).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    com.afollestad.materialdialogs.AlertDialogWrapper.Builder b = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.DragSort.ReorderSubreddits.this).setTitle(me.ccrama.redditslide.R.string.reorder_remove_title).setPositiveButton(me.ccrama.redditslide.R.string.btn_remove, new android.content.DialogInterface.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            for (java.lang.String s : chosen) {
                                int index = subs.indexOf(s);
                                subs.remove(index);
                                adapter.notifyItemRemoved(index);
                            }
                            isMultiple = false;
                            chosen = new java.util.ArrayList<>();
                            doOldToolbar();
                        }
                    });
                    if ((me.ccrama.redditslide.Authentication.isLoggedIn && me.ccrama.redditslide.Authentication.didOnline) && isSingle(chosen)) {
                        b.setNeutralButton(me.ccrama.redditslide.R.string.reorder_remove_unsubsribe, new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                for (java.lang.String s : chosen) {
                                    int index = subs.indexOf(s);
                                    subs.remove(index);
                                    adapter.notifyItemRemoved(index);
                                }
                                new me.ccrama.redditslide.UserSubscriptions.UnsubscribeTask().execute(chosen.toArray(new java.lang.String[chosen.size()]));
                                for (java.lang.String s : chosen) {
                                    isSubscribed.put(s.toLowerCase(java.util.Locale.ENGLISH), false);
                                }
                                isMultiple = false;
                                chosen = new java.util.ArrayList<>();
                                doOldToolbar();
                            }
                        });
                    }
                    b.setNegativeButton(me.ccrama.redditslide.R.string.btn_cancel, new android.content.DialogInterface.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                        }
                    }).show();
                }
            });
            mToolbar.findViewById(me.ccrama.redditslide.R.id.top).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    for (java.lang.String s : chosen) {
                        int index = subs.indexOf(s);
                        subs.remove(index);
                        subs.add(0, s);
                    }
                    isMultiple = false;
                    doOldToolbar();
                    chosen = new java.util.ArrayList<>();
                    notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(0);
                }
            });
            mToolbar.findViewById(me.ccrama.redditslide.R.id.pin).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    java.util.List<java.lang.String> pinned = me.ccrama.redditslide.UserSubscriptions.getPinned();
                    boolean contained = pinned.containsAll(chosen);
                    for (java.lang.String s : chosen) {
                        if (contained) {
                            me.ccrama.redditslide.UserSubscriptions.removePinned(s, me.ccrama.redditslide.DragSort.ReorderSubreddits.this);
                        } else {
                            me.ccrama.redditslide.UserSubscriptions.addPinned(s, me.ccrama.redditslide.DragSort.ReorderSubreddits.this);
                            int index = subs.indexOf(s);
                            subs.remove(index);
                            subs.add(0, s);
                        }
                    }
                    isMultiple = false;
                    doOldToolbar();
                    chosen = new java.util.ArrayList<>();
                    notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(0);
                }
            });
        }

        int[] textColorAttr = new int[]{ me.ccrama.redditslide.R.attr.fontColor };

        android.content.res.TypedArray ta = obtainStyledAttributes(textColorAttr);

        int textColor = ta.getColor(0, android.graphics.Color.BLACK);

        public void updateToolbar() {
            mToolbar.setTitle(getResources().getQuantityString(me.ccrama.redditslide.R.plurals.reorder_selected, chosen.size(), chosen.size()));
        }

        @java.lang.Override
        public void onBindViewHolder(final android.support.v7.widget.RecyclerView.ViewHolder holderB, final int position) {
            if (holderB instanceof me.ccrama.redditslide.DragSort.ReorderSubreddits.CustomAdapter.ViewHolder) {
                final me.ccrama.redditslide.DragSort.ReorderSubreddits.CustomAdapter.ViewHolder holder = ((me.ccrama.redditslide.DragSort.ReorderSubreddits.CustomAdapter.ViewHolder) (holderB));
                final java.lang.String origPos = items.get(position);
                holder.text.setText(origPos);
                if (chosen.contains(origPos)) {
                    holder.itemView.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(holder.text.getCurrentTextColor()));
                    holder.text.setTextColor(android.graphics.Color.WHITE);
                } else {
                    holder.itemView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                    holder.text.setTextColor(textColor);
                }
                if ((!isSingle(origPos)) || (!me.ccrama.redditslide.Authentication.isLoggedIn)) {
                    holder.check.setVisibility(android.view.View.GONE);
                } else {
                    holder.check.setVisibility(android.view.View.VISIBLE);
                }
                holder.check.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                    @java.lang.Override
                    public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                        // do nothing
                    }
                });
                holder.check.setChecked(isSubscribed.containsKey(origPos.toLowerCase(java.util.Locale.ENGLISH)) && isSubscribed.get(origPos.toLowerCase(java.util.Locale.ENGLISH)));
                holder.check.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                    @java.lang.Override
                    public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                        java.lang.String sub = origPos;
                        if (!isChecked) {
                            new me.ccrama.redditslide.UserSubscriptions.UnsubscribeTask().execute(sub);
                            android.support.design.widget.Snackbar.make(mToolbar, getString(me.ccrama.redditslide.R.string.reorder_unsubscribed_toast, origPos), android.support.design.widget.Snackbar.LENGTH_SHORT).show();
                        } else {
                            new me.ccrama.redditslide.UserSubscriptions.SubscribeTask(me.ccrama.redditslide.DragSort.ReorderSubreddits.this).execute(sub);
                            android.support.design.widget.Snackbar.make(mToolbar, getString(me.ccrama.redditslide.R.string.reorder_subscribed_toast, origPos), android.support.design.widget.Snackbar.LENGTH_SHORT).show();
                        }
                        isSubscribed.put(origPos.toLowerCase(java.util.Locale.ENGLISH), isChecked);
                    }
                });
                holder.itemView.findViewById(me.ccrama.redditslide.R.id.color).setBackgroundResource(me.ccrama.redditslide.R.drawable.circle);
                holder.itemView.findViewById(me.ccrama.redditslide.R.id.color).getBackground().setColorFilter(me.ccrama.redditslide.Visuals.Palette.getColor(origPos), android.graphics.PorterDuff.Mode.MULTIPLY);
                if (me.ccrama.redditslide.UserSubscriptions.getPinned().contains(origPos)) {
                    holder.itemView.findViewById(me.ccrama.redditslide.R.id.pinned).setVisibility(android.view.View.VISIBLE);
                } else {
                    holder.itemView.findViewById(me.ccrama.redditslide.R.id.pinned).setVisibility(android.view.View.GONE);
                }
                holder.itemView.setOnLongClickListener(new android.view.View.OnLongClickListener() {
                    @java.lang.Override
                    public boolean onLongClick(android.view.View v) {
                        if (!isMultiple) {
                            isMultiple = true;
                            chosen = new java.util.ArrayList<>();
                            chosen.add(origPos);
                            doNewToolbar();
                            holder.itemView.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(me.ccrama.redditslide.Visuals.Palette.getDefaultAccent()));
                            holder.text.setTextColor(android.graphics.Color.WHITE);
                        } else if (chosen.contains(origPos)) {
                            holder.itemView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                            // set the color of the text back to what it should be
                            holder.text.setTextColor(textColor);
                            chosen.remove(origPos);
                            if (chosen.isEmpty()) {
                                isMultiple = false;
                                doOldToolbar();
                            }
                        } else {
                            chosen.add(origPos);
                            holder.itemView.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(me.ccrama.redditslide.Visuals.Palette.getDefaultAccent()));
                            holder.text.setTextColor(textColor);
                            updateToolbar();
                        }
                        return true;
                    }
                });
                holder.itemView.setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        if (!isMultiple) {
                            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.DragSort.ReorderSubreddits.this).setItems(new java.lang.CharSequence[]{ getString(me.ccrama.redditslide.R.string.reorder_move), me.ccrama.redditslide.UserSubscriptions.getPinned().contains(origPos) ? "Unpin" : "Pin", getString(me.ccrama.redditslide.R.string.btn_delete) }, new android.content.DialogInterface.OnClickListener() {
                                @java.lang.Override
                                public void onClick(android.content.DialogInterface dialog, int which) {
                                    if (which == 2) {
                                        com.afollestad.materialdialogs.AlertDialogWrapper.Builder b = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.DragSort.ReorderSubreddits.this).setTitle(me.ccrama.redditslide.R.string.reorder_remove_title).setPositiveButton(me.ccrama.redditslide.R.string.btn_remove, new android.content.DialogInterface.OnClickListener() {
                                            @java.lang.Override
                                            public void onClick(android.content.DialogInterface dialog, int which) {
                                                subs.remove(items.get(position));
                                                adapter.notifyItemRemoved(position);
                                            }
                                        }).setNegativeButton(me.ccrama.redditslide.R.string.btn_cancel, new android.content.DialogInterface.OnClickListener() {
                                            @java.lang.Override
                                            public void onClick(android.content.DialogInterface dialog, int which) {
                                            }
                                        });
                                        if ((me.ccrama.redditslide.Authentication.isLoggedIn && me.ccrama.redditslide.Authentication.didOnline) && isSingle(origPos)) {
                                            b.setNeutralButton(me.ccrama.redditslide.R.string.reorder_remove_unsubsribe, new android.content.DialogInterface.OnClickListener() {
                                                @java.lang.Override
                                                public void onClick(android.content.DialogInterface dialog, int which) {
                                                    final java.lang.String sub = items.get(position);
                                                    subs.remove(sub);
                                                    adapter.notifyItemRemoved(position);
                                                    new me.ccrama.redditslide.UserSubscriptions.UnsubscribeTask().execute(sub);
                                                    isSubscribed.put(sub.toLowerCase(java.util.Locale.ENGLISH), false);
                                                }
                                            });
                                        }
                                        b.show();
                                    } else if (which == 0) {
                                        java.lang.String s = items.get(holder.getAdapterPosition());
                                        int index = subs.indexOf(s);
                                        subs.remove(index);
                                        subs.add(0, s);
                                        notifyItemMoved(holder.getAdapterPosition(), 0);
                                        recyclerView.smoothScrollToPosition(0);
                                    } else if (which == 1) {
                                        java.lang.String s = items.get(holder.getAdapterPosition());
                                        if (!me.ccrama.redditslide.UserSubscriptions.getPinned().contains(s)) {
                                            int index = subs.indexOf(s);
                                            me.ccrama.redditslide.UserSubscriptions.addPinned(s, me.ccrama.redditslide.DragSort.ReorderSubreddits.this);
                                            subs.remove(index);
                                            subs.add(0, s);
                                            notifyItemMoved(holder.getAdapterPosition(), 0);
                                            recyclerView.smoothScrollToPosition(0);
                                        } else {
                                            me.ccrama.redditslide.UserSubscriptions.removePinned(s, me.ccrama.redditslide.DragSort.ReorderSubreddits.this);
                                            adapter.notifyItemChanged(holder.getAdapterPosition());
                                        }
                                    }
                                }
                            }).show();
                        } else if (chosen.contains(origPos)) {
                            holder.itemView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                            // set the color of the text back to what it should be
                            int[] textColorAttr = new int[]{ me.ccrama.redditslide.R.attr.fontColor };
                            android.content.res.TypedArray ta = obtainStyledAttributes(textColorAttr);
                            holder.text.setTextColor(ta.getColor(0, android.graphics.Color.BLACK));
                            ta.recycle();
                            chosen.remove(origPos);
                            updateToolbar();
                            if (chosen.isEmpty()) {
                                isMultiple = false;
                                doOldToolbar();
                            }
                        } else {
                            chosen.add(origPos);
                            holder.itemView.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(me.ccrama.redditslide.Visuals.Palette.getDefaultAccent()));
                            holder.text.setTextColor(android.graphics.Color.WHITE);
                            updateToolbar();
                        }
                    }
                });
            }
        }

        @java.lang.Override
        public int getItemCount() {
            return items.size() + 1;
        }

        public class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
            final android.widget.TextView text;

            final android.support.v7.widget.AppCompatCheckBox check;

            public ViewHolder(android.view.View itemView) {
                super(itemView);
                text = ((android.widget.TextView) (itemView.findViewById(me.ccrama.redditslide.R.id.name)));
                check = ((android.support.v7.widget.AppCompatCheckBox) (itemView.findViewById(me.ccrama.redditslide.R.id.isSubscribed)));
            }
        }

        public class SpacerViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
            public SpacerViewHolder(android.view.View itemView) {
                super(itemView);
                itemView.findViewById(me.ccrama.redditslide.R.id.height).setLayoutParams(new android.widget.LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, me.ccrama.redditslide.Reddit.dpToPxVertical(88)));
            }
        }
    }

    /**
     * Check if all of the subreddits are single
     *
     * @param subreddits
     * 		list of subreddits to check
     * @return if all of the subreddits are single
     * @see #isSingle(java.lang.String)
     */
    private boolean isSingle(java.util.List<java.lang.String> subreddits) {
        for (java.lang.String subreddit : subreddits) {
            if (!isSingle(subreddit))
                return false;

        }
        return true;
    }

    /**
     * If the subreddit isn't special, combined, or a multireddit - can attempt to be subscribed to
     *
     * @param subreddit
     * 		name of a subreddit
     * @return if the subreddit is single
     */
    private boolean isSingle(java.lang.String subreddit) {
        return !(((isSpecial(subreddit) || subreddit.contains("+")) || subreddit.contains(".")) || subreddit.contains(me.ccrama.redditslide.DragSort.ReorderSubreddits.MULTI_REDDIT));
    }

    /**
     * Subreddits with important behaviour - frontpage, all, random, etc.
     *
     * @param subreddit
     * 		name of a subreddit
     * @return if the subreddit is special
     */
    private boolean isSpecial(java.lang.String subreddit) {
        for (java.lang.String specialSubreddit : me.ccrama.redditslide.UserSubscriptions.specialSubreddits) {
            if (subreddit.equalsIgnoreCase(specialSubreddit))
                return true;

        }
        return false;
    }
}