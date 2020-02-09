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
package me.ccrama.redditslide.Activities;
import java.util.regex.Pattern;
import me.ccrama.redditslide.util.LogUtil;
import java.util.Locale;
import me.ccrama.redditslide.R;
import java.util.regex.Matcher;
import me.ccrama.redditslide.Visuals.Palette;
import java.util.ArrayList;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.UserSubscriptions;
import java.util.List;
/**
 * This class handles creation of Multireddits.
 */
public class CreateMulti extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    private java.util.ArrayList<java.lang.String> subs;

    private boolean delete = false;

    private me.ccrama.redditslide.Activities.CreateMulti.CustomAdapter adapter;

    private android.widget.EditText title;

    private android.support.v7.widget.RecyclerView recyclerView;

    private java.lang.String input;

    private java.lang.String old;

    public static final java.lang.String EXTRA_MULTI = "multi";

    // Shows a dialog with all Subscribed subreddits and allows the user to select which ones to include in the Multireddit
    private java.lang.String[] all;

    @java.lang.Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        overrideRedditSwipeAnywhere();
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_createmulti);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, "", true, true);
        findViewById(me.ccrama.redditslide.R.id.add).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                showSelectDialog();
            }
        });
        title = ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.name)));
        subs = new java.util.ArrayList<>();
        if (getIntent().hasExtra(me.ccrama.redditslide.Activities.CreateMulti.EXTRA_MULTI)) {
            final java.lang.String multi = getIntent().getExtras().getString(me.ccrama.redditslide.Activities.CreateMulti.EXTRA_MULTI);
            old = multi;
            title.setText(multi.replace("%20", " "));
            me.ccrama.redditslide.UserSubscriptions.getMultireddits(new me.ccrama.redditslide.UserSubscriptions.MultiCallback() {
                @java.lang.Override
                public void onComplete(java.util.List<net.dean.jraw.models.MultiReddit> multis) {
                    for (net.dean.jraw.models.MultiReddit multiReddit : multis) {
                        if (multiReddit.getDisplayName().equals(multi)) {
                            for (net.dean.jraw.models.MultiSubreddit sub : multiReddit.getSubreddits()) {
                                subs.add(sub.getDisplayName().toLowerCase(java.util.Locale.ENGLISH));
                            }
                        }
                    }
                }
            });
        }
        recyclerView = ((android.support.v7.widget.RecyclerView) (findViewById(me.ccrama.redditslide.R.id.subslist)));
        adapter = new me.ccrama.redditslide.Activities.CreateMulti.CustomAdapter(subs);
        // adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new android.support.v7.widget.LinearLayoutManager(this));
    }

    public void showSelectDialog() {
        // List of all subreddits of the multi
        java.util.List<java.lang.String> sorted = new java.util.ArrayList<>();
        java.util.List<java.lang.String> multiSubs = new java.util.ArrayList<>();
        multiSubs.addAll(subs);
        sorted.addAll(subs);
        // Add all user subs that aren't already on the list
        for (java.lang.String s : me.ccrama.redditslide.UserSubscriptions.sort(me.ccrama.redditslide.UserSubscriptions.getSubscriptions(this))) {
            if (!sorted.contains(s))
                sorted.add(s);

        }
        // Array of all subs
        all = new java.lang.String[sorted.size()];
        // Contains which subreddits are checked
        boolean[] checked = new boolean[all.length];
        // Remove special subreddits from list and store it in "all"
        int i = 0;
        for (java.lang.String s : sorted) {
            if (((((!s.equals("all")) && (!s.equals("frontpage"))) && (!s.contains("+"))) && (!s.contains("."))) && (!s.contains("/m/"))) {
                all[i] = s;
                i++;
            }
        }
        // Remove empty entries & store which subreddits are checked
        java.util.List<java.lang.String> list = new java.util.ArrayList<>();
        i = 0;
        for (java.lang.String s : all) {
            if ((s != null) && (!s.isEmpty())) {
                list.add(s);
                if (multiSubs.contains(s)) {
                    checked[i] = true;
                }
                i++;
            }
        }
        // Convert List back to Array
        all = list.toArray(new java.lang.String[list.size()]);
        final java.util.ArrayList<java.lang.String> toCheck = new java.util.ArrayList<>();
        toCheck.addAll(subs);
        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setMultiChoiceItems(all, checked, new android.content.DialogInterface.OnMultiChoiceClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialog, int which, boolean isChecked) {
                if (!isChecked) {
                    toCheck.remove(all[which]);
                } else {
                    toCheck.add(all[which]);
                }
                android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "Done with " + all[which]);
            }
        }).setTitle(me.ccrama.redditslide.R.string.multireddit_selector).setPositiveButton(getString(me.ccrama.redditslide.R.string.btn_add).toUpperCase(), new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                subs = toCheck;
                adapter = new me.ccrama.redditslide.Activities.CreateMulti.CustomAdapter(subs);
                recyclerView.setAdapter(adapter);
            }
        }).setNegativeButton(me.ccrama.redditslide.R.string.reorder_add_subreddit, new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.CreateMulti.this).title(me.ccrama.redditslide.R.string.reorder_add_subreddit).inputRangeRes(2, 21, me.ccrama.redditslide.R.color.md_red_500).alwaysCallInputCallback().input(getString(me.ccrama.redditslide.R.string.reorder_subreddit_name), null, false, new com.afollestad.materialdialogs.MaterialDialog.InputCallback() {
                    @java.lang.Override
                    public void onInput(com.afollestad.materialdialogs.MaterialDialog dialog, java.lang.CharSequence raw) {
                        input = raw.toString().replaceAll("\\s", "");// remove whitespace from input

                    }
                }).positiveText(me.ccrama.redditslide.R.string.btn_add).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                    @java.lang.Override
                    public void onClick(com.afollestad.materialdialogs.MaterialDialog dialog, com.afollestad.materialdialogs.DialogAction which) {
                        new me.ccrama.redditslide.Activities.CreateMulti.AsyncGetSubreddit().execute(input);
                    }
                }).negativeText(me.ccrama.redditslide.R.string.btn_cancel).onNegative(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                    @java.lang.Override
                    public void onClick(com.afollestad.materialdialogs.MaterialDialog dialog, com.afollestad.materialdialogs.DialogAction which) {
                    }
                }).show();
            }
        }).show();
    }

    private class AsyncGetSubreddit extends android.os.AsyncTask<java.lang.String, java.lang.Void, net.dean.jraw.models.Subreddit> {
        @java.lang.Override
        public void onPostExecute(net.dean.jraw.models.Subreddit subreddit) {
            if (((subreddit != null) || input.equalsIgnoreCase("friends")) || input.equalsIgnoreCase("mod")) {
                subs.add(input);
                adapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(subs.size());
            }
        }

        @java.lang.Override
        protected net.dean.jraw.models.Subreddit doInBackground(final java.lang.String... params) {
            try {
                if (subs.contains(params[0]))
                    return null;

                return me.ccrama.redditslide.Authentication.reddit.getSubreddit(params[0]);
            } catch (java.lang.Exception e) {
                runOnUiThread(new java.lang.Runnable() {
                    @java.lang.Override
                    public void run() {
                        try {
                            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.CreateMulti.this).setTitle(me.ccrama.redditslide.R.string.subreddit_err).setMessage(getString(me.ccrama.redditslide.R.string.subreddit_err_msg, params[0])).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
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
                return null;
            }
        }
    }

    /**
     * Responsible for showing a list of subreddits which are added to this Multireddit
     */
    public class CustomAdapter extends android.support.v7.widget.RecyclerView.Adapter<me.ccrama.redditslide.Activities.CreateMulti.CustomAdapter.ViewHolder> {
        private final java.util.ArrayList<java.lang.String> items;

        public CustomAdapter(java.util.ArrayList<java.lang.String> items) {
            this.items = items;
        }

        @java.lang.Override
        public me.ccrama.redditslide.Activities.CreateMulti.CustomAdapter.ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View v = android.view.LayoutInflater.from(parent.getContext()).inflate(me.ccrama.redditslide.R.layout.subforsublist, parent, false);
            return new me.ccrama.redditslide.Activities.CreateMulti.CustomAdapter.ViewHolder(v);
        }

        @java.lang.Override
        public void onBindViewHolder(final me.ccrama.redditslide.Activities.CreateMulti.CustomAdapter.ViewHolder holder, int position) {
            final java.lang.String origPos = items.get(position);
            holder.text.setText(origPos);
            holder.itemView.findViewById(me.ccrama.redditslide.R.id.color).setBackgroundResource(me.ccrama.redditslide.R.drawable.circle);
            holder.itemView.findViewById(me.ccrama.redditslide.R.id.color).getBackground().setColorFilter(me.ccrama.redditslide.Visuals.Palette.getColor(origPos), android.graphics.PorterDuff.Mode.MULTIPLY);
            holder.itemView.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.CreateMulti.this).setTitle(me.ccrama.redditslide.R.string.really_remove_subreddit_title).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            subs.remove(origPos);
                            adapter = new me.ccrama.redditslide.Activities.CreateMulti.CustomAdapter(subs);
                            recyclerView.setAdapter(adapter);
                        }
                    }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, null).show();
                }
            });
        }

        @java.lang.Override
        public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
            final android.widget.TextView text;

            public ViewHolder(android.view.View itemView) {
                super(itemView);
                text = ((android.widget.TextView) (itemView.findViewById(me.ccrama.redditslide.R.id.name)));
            }
        }
    }

    /**
     * Saves a Multireddit with applicable data in an async task
     */
    public class SaveMulti extends android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void> {
        @java.lang.Override
        protected java.lang.Void doInBackground(java.lang.Void... params) {
            try {
                java.lang.String multiName = title.getText().toString().replace(" ", "").replace("-", "_");
                java.util.regex.Pattern validName = java.util.regex.Pattern.compile("^[A-Za-z0-9][A-Za-z0-9_]{2,20}$");
                java.util.regex.Matcher m = validName.matcher(multiName);
                if (!m.matches()) {
                    android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "Invalid multi name");
                    throw new java.lang.IllegalArgumentException(multiName);
                }
                if (delete) {
                    android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "Deleting");
                    new net.dean.jraw.managers.MultiRedditManager(me.ccrama.redditslide.Authentication.reddit).delete(old);
                } else {
                    if (((old != null) && (!old.isEmpty())) && (!old.replace(" ", "").equals(multiName))) {
                        android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "Renaming");
                        new net.dean.jraw.managers.MultiRedditManager(me.ccrama.redditslide.Authentication.reddit).rename(old, multiName);
                    }
                    android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "Create or Update, Name: " + multiName);
                    new net.dean.jraw.managers.MultiRedditManager(me.ccrama.redditslide.Authentication.reddit).createOrUpdate(new net.dean.jraw.http.MultiRedditUpdateRequest.Builder(me.ccrama.redditslide.Authentication.name, multiName).subreddits(subs).build());
                    runOnUiThread(new java.lang.Runnable() {
                        @java.lang.Override
                        public void run() {
                            android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "Update Subreddits");
                            new me.ccrama.redditslide.UserSubscriptions.SyncMultireddits(me.ccrama.redditslide.Activities.CreateMulti.this).execute();
                        }
                    });
                }
                runOnUiThread(new java.lang.Runnable() {
                    @java.lang.Override
                    public void run() {
                        android.content.Context context = getApplicationContext();
                        java.lang.CharSequence text = getString(me.ccrama.redditslide.R.string.multi_saved_successfully);
                        int duration = android.widget.Toast.LENGTH_SHORT;
                        android.widget.Toast toast = android.widget.Toast.makeText(context, text, duration);
                        toast.show();
                    }
                });
            } catch (net.dean.jraw.http.NetworkException | net.dean.jraw.ApiException e) {
                runOnUiThread(new java.lang.Runnable() {
                    @java.lang.Override
                    public void run() {
                        java.lang.String errorMsg = getString(me.ccrama.redditslide.R.string.misc_err);
                        // Creating correct error message if the multireddit has more than 100 subs or its name already exists
                        if (e instanceof net.dean.jraw.ApiException) {
                            errorMsg = (((getString(me.ccrama.redditslide.R.string.misc_err) + ": ") + ((net.dean.jraw.ApiException) (e)).getExplanation()) + "\n") + getString(me.ccrama.redditslide.R.string.misc_retry);
                        } else if (((net.dean.jraw.http.NetworkException) (e)).getResponse().getStatusCode() == 409) {
                            // The HTTP status code returned when the name of the multireddit already exists or
                            // has more than 100 subs is 409
                            errorMsg = getString(me.ccrama.redditslide.R.string.multireddit_save_err);
                        }
                        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.CreateMulti.this).setTitle(me.ccrama.redditslide.R.string.err_title).setMessage(errorMsg).setNeutralButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).create().show();
                    }
                });
                e.printStackTrace();
            } catch (java.lang.IllegalArgumentException e) {
                runOnUiThread(new java.lang.Runnable() {
                    @java.lang.Override
                    public void run() {
                        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.CreateMulti.this).setTitle(me.ccrama.redditslide.R.string.multireddit_invalid_name).setMessage(me.ccrama.redditslide.R.string.multireddit_invalid_name_msg).setNeutralButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).create().show();
                    }
                });
            }
            return null;
        }
    }

    @java.lang.Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        android.view.MenuInflater inflater = getMenuInflater();
        inflater.inflate(me.ccrama.redditslide.R.menu.menu_create_multi, menu);
        return true;
    }

    @java.lang.Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case me.ccrama.redditslide.R.id.delete :
                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setTitle(getString(me.ccrama.redditslide.R.string.delete_multireddit_title, title.getText().toString())).setMessage(me.ccrama.redditslide.R.string.cannot_be_undone).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.CreateMulti.this).title(me.ccrama.redditslide.R.string.deleting).progress(true, 100).content(me.ccrama.redditslide.R.string.misc_please_wait).cancelable(false).show();
                        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
                            @java.lang.Override
                            protected java.lang.Void doInBackground(java.lang.Void... params) {
                                try {
                                    new net.dean.jraw.managers.MultiRedditManager(me.ccrama.redditslide.Authentication.reddit).delete(old);
                                    runOnUiThread(new java.lang.Runnable() {
                                        @java.lang.Override
                                        public void run() {
                                            new me.ccrama.redditslide.UserSubscriptions.SyncMultireddits(me.ccrama.redditslide.Activities.CreateMulti.this).execute();
                                        }
                                    });
                                } catch (final java.lang.Exception e) {
                                    runOnUiThread(new java.lang.Runnable() {
                                        @java.lang.Override
                                        public void run() {
                                            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.CreateMulti.this).setTitle(me.ccrama.redditslide.R.string.err_title).setMessage(e instanceof net.dean.jraw.ApiException ? (((getString(me.ccrama.redditslide.R.string.misc_err) + ": ") + ((net.dean.jraw.ApiException) (e)).getExplanation()) + "\n") + getString(me.ccrama.redditslide.R.string.misc_retry) : getString(me.ccrama.redditslide.R.string.misc_err)).setNeutralButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                                                @java.lang.Override
                                                public void onClick(android.content.DialogInterface dialogInterface, int i) {
                                                    finish();
                                                }
                                            }).create().show();
                                        }
                                    });
                                    e.printStackTrace();
                                }
                                return null;
                            }
                        }.execute();
                    }
                }).setNegativeButton(me.ccrama.redditslide.R.string.btn_cancel, null).show();
                return true;
            case me.ccrama.redditslide.R.id.save :
                if (title.getText().toString().isEmpty()) {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setTitle(me.ccrama.redditslide.R.string.multireddit_title_empty).setMessage(me.ccrama.redditslide.R.string.multireddit_title_empty_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            dialog.dismiss();
                            title.requestFocus();
                        }
                    }).show();
                } else if (subs.isEmpty()) {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setTitle(me.ccrama.redditslide.R.string.multireddit_no_subs).setMessage(me.ccrama.redditslide.R.string.multireddit_no_subs_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                } else {
                    new me.ccrama.redditslide.Activities.CreateMulti.SaveMulti().execute();
                }
                return true;
            case android.R.id.home :
                onBackPressed();
                return true;
            default :
                return false;
        }
    }
}