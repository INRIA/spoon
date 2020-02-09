package me.ccrama.redditslide.Adapters;
import java.util.Locale;
import me.ccrama.redditslide.Visuals.Palette;
import java.util.ArrayList;
import me.ccrama.redditslide.Activities.MainActivity;
import me.ccrama.redditslide.CaseInsensitiveArrayList;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Activities.SubredditView;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Constants;
import org.apache.commons.lang3.StringUtils;
import me.ccrama.redditslide.SantitizeField;
import me.ccrama.redditslide.UserSubscriptions;
import java.util.List;
import java.util.Map;
/**
 * Created by ccrama on 8/17/2015.
 */
public class SideArrayAdapter extends android.widget.ArrayAdapter<java.lang.String> {
    private final java.util.List<java.lang.String> objects;

    private android.widget.Filter filter;

    public me.ccrama.redditslide.CaseInsensitiveArrayList baseItems;

    public me.ccrama.redditslide.CaseInsensitiveArrayList fitems;

    public android.widget.ListView parentL;

    public boolean openInSubView = true;

    public SideArrayAdapter(android.content.Context context, java.util.ArrayList<java.lang.String> objects, java.util.ArrayList<java.lang.String> allSubreddits, android.widget.ListView view) {
        super(context, 0, objects);
        this.objects = new java.util.ArrayList<>(allSubreddits);
        filter = new me.ccrama.redditslide.Adapters.SideArrayAdapter.SubFilter();
        fitems = new me.ccrama.redditslide.CaseInsensitiveArrayList(objects);
        baseItems = new me.ccrama.redditslide.CaseInsensitiveArrayList(objects);
        parentL = view;
        multiToMatch = me.ccrama.redditslide.UserSubscriptions.getMultiNameToSubs(true);
    }

    @java.lang.Override
    public boolean isEnabled(int position) {
        return false;
    }

    @java.lang.Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @java.lang.Override
    public android.widget.Filter getFilter() {
        if (filter == null) {
            filter = new me.ccrama.redditslide.Adapters.SideArrayAdapter.SubFilter();
        }
        return filter;
    }

    int height;

    java.util.Map<java.lang.String, java.lang.String> multiToMatch;

    @java.lang.Override
    public android.view.View getView(final int position, android.view.View convertView, android.view.ViewGroup parent) {
        if (position < fitems.size()) {
            convertView = android.view.LayoutInflater.from(getContext()).inflate(me.ccrama.redditslide.R.layout.subforsublist, parent, false);
            final java.lang.String sub;
            final java.lang.String base = fitems.get(position);
            if (multiToMatch.containsKey(fitems.get(position)) && (!fitems.get(position).contains("/m/"))) {
                sub = multiToMatch.get(fitems.get(position));
            } else {
                sub = fitems.get(position);
            }
            final android.widget.TextView t = ((android.widget.TextView) (convertView.findViewById(me.ccrama.redditslide.R.id.name)));
            t.setText(sub);
            if (height == 0) {
                final android.view.View finalConvertView = convertView;
                convertView.getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {
                    @java.lang.Override
                    public void onGlobalLayout() {
                        height = finalConvertView.getHeight();
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            finalConvertView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            finalConvertView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                    }
                });
            }
            final java.lang.String subreddit = (sub.contains("+") || sub.contains("/m/")) ? sub : me.ccrama.redditslide.SantitizeField.sanitizeString(sub.replace(getContext().getString(me.ccrama.redditslide.R.string.search_goto) + " ", ""));
            convertView.findViewById(me.ccrama.redditslide.R.id.color).setBackgroundResource(me.ccrama.redditslide.R.drawable.circle);
            convertView.findViewById(me.ccrama.redditslide.R.id.color).getBackground().setColorFilter(me.ccrama.redditslide.Visuals.Palette.getColor(subreddit), android.graphics.PorterDuff.Mode.MULTIPLY);
            convertView.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View view) {
                    if (base.startsWith(getContext().getString(me.ccrama.redditslide.R.string.search_goto) + " ") || (!((me.ccrama.redditslide.Activities.MainActivity) (getContext())).usedArray.contains(base))) {
                        try {
                            // Hide the toolbar search UI without an animation because we're starting a new activity
                            if (((me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_TOOLBAR) || (me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_BOTH)) && (((me.ccrama.redditslide.Activities.MainActivity) (getContext())).findViewById(me.ccrama.redditslide.R.id.toolbar_search).getVisibility() == android.view.View.VISIBLE)) {
                                ((me.ccrama.redditslide.Activities.MainActivity) (getContext())).findViewById(me.ccrama.redditslide.R.id.toolbar_search_suggestions).setVisibility(android.view.View.GONE);
                                ((me.ccrama.redditslide.Activities.MainActivity) (getContext())).findViewById(me.ccrama.redditslide.R.id.toolbar_search).setVisibility(android.view.View.GONE);
                                ((me.ccrama.redditslide.Activities.MainActivity) (getContext())).findViewById(me.ccrama.redditslide.R.id.close_search_toolbar).setVisibility(android.view.View.GONE);
                                // Play the exit animations of the search toolbar UI to avoid the animations failing to animate upon the next time
                                // the search toolbar UI is called. Set animation to 0 because the UI is already hidden.
                                ((me.ccrama.redditslide.Activities.MainActivity) (getContext())).exitAnimationsForToolbarSearch(0, ((android.support.v7.widget.CardView) (((me.ccrama.redditslide.Activities.MainActivity) (getContext())).findViewById(me.ccrama.redditslide.R.id.toolbar_search_suggestions))), ((android.widget.AutoCompleteTextView) (((me.ccrama.redditslide.Activities.MainActivity) (getContext())).findViewById(me.ccrama.redditslide.R.id.toolbar_search))), ((android.widget.ImageView) (((me.ccrama.redditslide.Activities.MainActivity) (getContext())).findViewById(me.ccrama.redditslide.R.id.close_search_toolbar))));
                                if (me.ccrama.redditslide.SettingValues.single) {
                                    ((me.ccrama.redditslide.Activities.MainActivity) (getContext())).getSupportActionBar().setTitle(((me.ccrama.redditslide.Activities.MainActivity) (getContext())).selectedSub);
                                } else {
                                    ((me.ccrama.redditslide.Activities.MainActivity) (getContext())).getSupportActionBar().setTitle(((me.ccrama.redditslide.Activities.MainActivity) (getContext())).tabViewModeTitle);
                                }
                            }
                        } catch (java.lang.NullPointerException npe) {
                            android.util.Log.e(getClass().getName(), npe.getMessage());
                        }
                        android.content.Intent inte = new android.content.Intent(getContext(), me.ccrama.redditslide.Activities.SubredditView.class);
                        inte.putExtra(me.ccrama.redditslide.Activities.SubredditView.EXTRA_SUBREDDIT, subreddit);
                        ((android.app.Activity) (getContext())).startActivityForResult(inte, 2001);
                    } else {
                        if (((me.ccrama.redditslide.Activities.MainActivity) (getContext())).commentPager && (((me.ccrama.redditslide.Activities.MainActivity) (getContext())).adapter instanceof me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapterComment)) {
                            ((me.ccrama.redditslide.Activities.MainActivity) (getContext())).openingComments = null;
                            ((me.ccrama.redditslide.Activities.MainActivity) (getContext())).toOpenComments = -1;
                            ((me.ccrama.redditslide.Activities.MainActivity.OverviewPagerAdapterComment) (((me.ccrama.redditslide.Activities.MainActivity) (getContext())).adapter)).size = ((me.ccrama.redditslide.Activities.MainActivity) (getContext())).usedArray.size() + 1;
                            ((me.ccrama.redditslide.Activities.MainActivity) (getContext())).reloadItemNumber = ((me.ccrama.redditslide.Activities.MainActivity) (getContext())).usedArray.indexOf(base);
                            ((me.ccrama.redditslide.Activities.MainActivity) (getContext())).adapter.notifyDataSetChanged();
                            ((me.ccrama.redditslide.Activities.MainActivity) (getContext())).doPageSelectedComments(((me.ccrama.redditslide.Activities.MainActivity) (getContext())).usedArray.indexOf(base));
                            ((me.ccrama.redditslide.Activities.MainActivity) (getContext())).reloadItemNumber = -2;
                        }
                        try {
                            // Hide the toolbar search UI with an animation because we're just changing tabs
                            if (((me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_TOOLBAR) || (me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_BOTH)) && (((me.ccrama.redditslide.Activities.MainActivity) (getContext())).findViewById(me.ccrama.redditslide.R.id.toolbar_search).getVisibility() == android.view.View.VISIBLE)) {
                                ((me.ccrama.redditslide.Activities.MainActivity) (getContext())).findViewById(me.ccrama.redditslide.R.id.close_search_toolbar).performClick();
                            }
                        } catch (java.lang.NullPointerException npe) {
                            android.util.Log.e(getClass().getName(), npe.getMessage());
                        }
                        ((me.ccrama.redditslide.Activities.MainActivity) (getContext())).pager.setCurrentItem(((me.ccrama.redditslide.Activities.MainActivity) (getContext())).usedArray.indexOf(base));
                        ((me.ccrama.redditslide.Activities.MainActivity) (getContext())).drawerLayout.closeDrawers();
                        if (((me.ccrama.redditslide.Activities.MainActivity) (getContext())).drawerSearch != null) {
                            ((me.ccrama.redditslide.Activities.MainActivity) (getContext())).drawerSearch.setText("");
                        }
                    }
                    android.view.inputmethod.InputMethodManager imm = ((android.view.inputmethod.InputMethodManager) (getContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE)));
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            });
            convertView.setOnLongClickListener(new android.view.View.OnLongClickListener() {
                @java.lang.Override
                public boolean onLongClick(android.view.View view) {
                    try {
                        // Hide the toolbar search UI without an animation because we're starting a new activity
                        if (((me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_TOOLBAR) || (me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_BOTH)) && (((me.ccrama.redditslide.Activities.MainActivity) (getContext())).findViewById(me.ccrama.redditslide.R.id.toolbar_search).getVisibility() == android.view.View.VISIBLE)) {
                            ((me.ccrama.redditslide.Activities.MainActivity) (getContext())).findViewById(me.ccrama.redditslide.R.id.toolbar_search_suggestions).setVisibility(android.view.View.GONE);
                            ((me.ccrama.redditslide.Activities.MainActivity) (getContext())).findViewById(me.ccrama.redditslide.R.id.toolbar_search).setVisibility(android.view.View.GONE);
                            ((me.ccrama.redditslide.Activities.MainActivity) (getContext())).findViewById(me.ccrama.redditslide.R.id.close_search_toolbar).setVisibility(android.view.View.GONE);
                            // Play the exit animations of the search toolbar UI to avoid the animations failing to animate upon the next time
                            // the search toolbar UI is called. Set animation to 0 because the UI is already hidden.
                            ((me.ccrama.redditslide.Activities.MainActivity) (getContext())).exitAnimationsForToolbarSearch(0, ((android.support.v7.widget.CardView) (((me.ccrama.redditslide.Activities.MainActivity) (getContext())).findViewById(me.ccrama.redditslide.R.id.toolbar_search_suggestions))), ((android.widget.AutoCompleteTextView) (((me.ccrama.redditslide.Activities.MainActivity) (getContext())).findViewById(me.ccrama.redditslide.R.id.toolbar_search))), ((android.widget.ImageView) (((me.ccrama.redditslide.Activities.MainActivity) (getContext())).findViewById(me.ccrama.redditslide.R.id.close_search_toolbar))));
                            if (me.ccrama.redditslide.SettingValues.single) {
                                ((me.ccrama.redditslide.Activities.MainActivity) (getContext())).getSupportActionBar().setTitle(((me.ccrama.redditslide.Activities.MainActivity) (getContext())).selectedSub);
                            } else {
                                ((me.ccrama.redditslide.Activities.MainActivity) (getContext())).getSupportActionBar().setTitle(((me.ccrama.redditslide.Activities.MainActivity) (getContext())).tabViewModeTitle);
                            }
                        }
                    } catch (java.lang.NullPointerException npe) {
                        android.util.Log.e(getClass().getName(), npe.getMessage());
                    }
                    android.content.Intent inte = new android.content.Intent(getContext(), me.ccrama.redditslide.Activities.SubredditView.class);
                    inte.putExtra(me.ccrama.redditslide.Activities.SubredditView.EXTRA_SUBREDDIT, subreddit);
                    ((android.app.Activity) (getContext())).startActivityForResult(inte, 2001);
                    android.view.inputmethod.InputMethodManager imm = ((android.view.inputmethod.InputMethodManager) (getContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE)));
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    return true;
                }
            });
        } else if (((fitems.size() * height) < parentL.getHeight()) && ((me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_DRAWER) || (me.ccrama.redditslide.SettingValues.subredditSearchMethod == me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_BOTH))) {
            convertView = android.view.LayoutInflater.from(getContext()).inflate(me.ccrama.redditslide.R.layout.spacer, parent, false);
            android.view.ViewGroup.LayoutParams params = convertView.findViewById(me.ccrama.redditslide.R.id.height).getLayoutParams();
            params.height = parentL.getHeight() - ((getCount() - 1) * height);
            convertView.setLayoutParams(params);
        } else {
            convertView = android.view.LayoutInflater.from(getContext()).inflate(me.ccrama.redditslide.R.layout.spacer, parent, false);
            android.view.ViewGroup.LayoutParams params = convertView.findViewById(me.ccrama.redditslide.R.id.height).getLayoutParams();
            params.height = 0;
            convertView.setLayoutParams(params);
        }
        return convertView;
    }

    @java.lang.Override
    public int getCount() {
        return fitems.size() + 1;
    }

    public void updateHistory(java.util.ArrayList<java.lang.String> history) {
        for (java.lang.String s : history) {
            if (!objects.contains(s)) {
                objects.add(s);
            }
        }
        notifyDataSetChanged();
    }

    private class SubFilter extends android.widget.Filter {
        @java.lang.Override
        protected android.widget.Filter.FilterResults performFiltering(java.lang.CharSequence constraint) {
            android.widget.Filter.FilterResults results = new android.widget.Filter.FilterResults();
            java.lang.String prefix = constraint.toString().toLowerCase(java.util.Locale.ENGLISH);
            if ((prefix == null) || prefix.isEmpty()) {
                me.ccrama.redditslide.CaseInsensitiveArrayList list = new me.ccrama.redditslide.CaseInsensitiveArrayList(baseItems);
                results.values = list;
                results.count = list.size();
            } else {
                openInSubView = true;
                final me.ccrama.redditslide.CaseInsensitiveArrayList list = new me.ccrama.redditslide.CaseInsensitiveArrayList(objects);
                final me.ccrama.redditslide.CaseInsensitiveArrayList nlist = new me.ccrama.redditslide.CaseInsensitiveArrayList();
                for (java.lang.String sub : list) {
                    if (org.apache.commons.lang3.StringUtils.containsIgnoreCase(sub, prefix))
                        nlist.add(sub);

                    if (sub.equals(prefix))
                        openInSubView = false;

                }
                if (openInSubView) {
                    nlist.add((getContext().getString(me.ccrama.redditslide.R.string.search_goto) + " ") + prefix);
                }
                results.values = nlist;
                results.count = nlist.size();
            }
            return results;
        }

        @java.lang.SuppressWarnings("unchecked")
        @java.lang.Override
        protected void publishResults(java.lang.CharSequence constraint, android.widget.Filter.FilterResults results) {
            fitems = ((me.ccrama.redditslide.CaseInsensitiveArrayList) (results.values));
            clear();
            if (fitems != null) {
                addAll(fitems);
                notifyDataSetChanged();
            }
        }
    }
}