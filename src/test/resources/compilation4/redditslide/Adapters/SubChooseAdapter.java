package me.ccrama.redditslide.Adapters;
import me.ccrama.redditslide.util.LogUtil;
import java.util.Locale;
import me.ccrama.redditslide.Activities.SetupWidget;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Activities.OpenContent;
import me.ccrama.redditslide.util.ImageUtil;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.Widget.SubredditWidgetProvider;
import java.util.ArrayList;
import me.ccrama.redditslide.Activities.Shortcut;
import java.util.List;
/**
 * Created by ccrama on 8/17/2015.
 */
public class SubChooseAdapter extends android.widget.ArrayAdapter<java.lang.String> {
    private final java.util.List<java.lang.String> objects;

    private android.widget.Filter filter;

    public java.util.ArrayList<java.lang.String> baseItems;

    public java.util.ArrayList<java.lang.String> fitems;

    public boolean openInSubView = true;

    public SubChooseAdapter(android.content.Context context, java.util.ArrayList<java.lang.String> objects, java.util.ArrayList<java.lang.String> allSubreddits) {
        super(context, 0, objects);
        this.objects = new java.util.ArrayList<>(allSubreddits);
        filter = new me.ccrama.redditslide.Adapters.SubChooseAdapter.SubFilter();
        fitems = new java.util.ArrayList<>(objects);
        baseItems = new java.util.ArrayList<>(objects);
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
            filter = new me.ccrama.redditslide.Adapters.SubChooseAdapter.SubFilter();
        }
        return filter;
    }

    private static class ViewHolderItem {
        private android.widget.TextView t;

        ViewHolderItem(android.widget.TextView t) {
            this.t = t;
        }
    }

    @java.lang.Override
    public android.view.View getView(final int position, android.view.View convertView, android.view.ViewGroup parent) {
        me.ccrama.redditslide.Adapters.SubChooseAdapter.ViewHolderItem viewHolderItem;
        if (convertView == null) {
            convertView = android.view.LayoutInflater.from(getContext()).inflate(me.ccrama.redditslide.R.layout.subforsublist, parent, false);
            viewHolderItem = new me.ccrama.redditslide.Adapters.SubChooseAdapter.ViewHolderItem(((android.widget.TextView) (convertView.findViewById(me.ccrama.redditslide.R.id.name))));
            convertView.setTag(viewHolderItem);
        } else {
            viewHolderItem = ((me.ccrama.redditslide.Adapters.SubChooseAdapter.ViewHolderItem) (convertView.getTag()));
        }
        final android.widget.TextView t = viewHolderItem.t;
        t.setText(fitems.get(position));
        final java.lang.String subreddit = fitems.get(position);
        convertView.findViewById(me.ccrama.redditslide.R.id.color).setBackgroundResource(me.ccrama.redditslide.R.drawable.circle);
        convertView.findViewById(me.ccrama.redditslide.R.id.color).getBackground().setColorFilter(me.ccrama.redditslide.Visuals.Palette.getColor(subreddit), android.graphics.PorterDuff.Mode.MULTIPLY);
        if (getContext() instanceof me.ccrama.redditslide.Activities.SetupWidget) {
            convertView.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    ((me.ccrama.redditslide.Activities.SetupWidget) (getContext())).name = subreddit;
                    me.ccrama.redditslide.Widget.SubredditWidgetProvider.lastDone = subreddit;
                    ((me.ccrama.redditslide.Activities.SetupWidget) (getContext())).startWidget();
                }
            });
        } else if (getContext() instanceof me.ccrama.redditslide.Activities.Shortcut) {
            convertView.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    final android.graphics.Bitmap src;
                    final android.graphics.Bitmap bm2;
                    android.content.Intent shortcutIntent = new android.content.Intent(getContext(), me.ccrama.redditslide.Activities.OpenContent.class);
                    if (subreddit.toLowerCase(java.util.Locale.ENGLISH).equals("androidcirclejerk")) {
                        bm2 = me.ccrama.redditslide.Activities.Shortcut.drawableToBitmap(android.support.v4.content.ContextCompat.getDrawable(getContext(), me.ccrama.redditslide.R.drawable.matiasduarte));
                        android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "NULL IS " + (bm2 == null));
                    } else {
                        src = me.ccrama.redditslide.Activities.Shortcut.drawableToBitmap(android.support.v4.content.ContextCompat.getDrawable(getContext(), me.ccrama.redditslide.R.drawable.blackandwhite));
                        final int overlayColor = me.ccrama.redditslide.Visuals.Palette.getColor(subreddit);
                        final android.graphics.Paint paint = new android.graphics.Paint();
                        android.graphics.Canvas c;
                        final android.graphics.Bitmap bm1 = android.graphics.Bitmap.createBitmap(src.getWidth(), src.getHeight(), android.graphics.Bitmap.Config.ARGB_8888);
                        c = new android.graphics.Canvas(bm1);
                        paint.setColorFilter(new android.graphics.PorterDuffColorFilter(overlayColor, android.graphics.PorterDuff.Mode.OVERLAY));
                        c.drawBitmap(src, 0, 0, paint);
                        bm2 = android.graphics.Bitmap.createBitmap(src.getWidth(), src.getHeight(), android.graphics.Bitmap.Config.ARGB_8888);
                        c = new android.graphics.Canvas(bm2);
                        paint.setColorFilter(new android.graphics.PorterDuffColorFilter(overlayColor, android.graphics.PorterDuff.Mode.SRC_ATOP));
                        c.drawBitmap(src, 0, 0, paint);
                        // paint.setColorFilter(null);
                        // paint.setXfermode(new AvoidXfermode(overlayColor, 0, AvoidXfermode.Mode.TARGET));
                        // c.drawBitmap(bm1, 0, 0, paint);
                        me.ccrama.redditslide.util.ImageUtil.drawWithTargetColor(bm2, bm1, overlayColor, 0);
                    }
                    final float scale = ((me.ccrama.redditslide.Activities.Shortcut) (getContext())).getResources().getDisplayMetrics().density;
                    int p = ((int) ((50 * scale) + 0.5F));
                    shortcutIntent.putExtra(me.ccrama.redditslide.Activities.OpenContent.EXTRA_URL, "reddit.com/r/" + subreddit);
                    android.content.Intent intent = new android.content.Intent();
                    intent.putExtra(android.content.Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
                    intent.putExtra(android.content.Intent.EXTRA_SHORTCUT_NAME, "/r/" + subreddit);
                    intent.putExtra(android.content.Intent.EXTRA_SHORTCUT_ICON, android.graphics.Bitmap.createScaledBitmap(bm2, p, p, false));
                    ((me.ccrama.redditslide.Activities.Shortcut) (getContext())).setResult(android.app.Activity.RESULT_OK, intent);
                    ((me.ccrama.redditslide.Activities.Shortcut) (getContext())).finish();
                }
            });
        }
        return convertView;
    }

    @java.lang.Override
    public int getCount() {
        return fitems.size();
    }

    private class SubFilter extends android.widget.Filter {
        @java.lang.Override
        protected android.widget.Filter.FilterResults performFiltering(java.lang.CharSequence constraint) {
            android.widget.Filter.FilterResults results = new android.widget.Filter.FilterResults();
            java.lang.String prefix = constraint.toString().toLowerCase(java.util.Locale.ENGLISH);
            if ((prefix == null) || (prefix.length() == 0)) {
                java.util.ArrayList<java.lang.String> list = new java.util.ArrayList<>(baseItems);
                results.values = list;
                results.count = list.size();
            } else {
                openInSubView = true;
                final java.util.ArrayList<java.lang.String> list = new java.util.ArrayList<>(objects);
                final java.util.ArrayList<java.lang.String> nlist = new java.util.ArrayList<>();
                for (java.lang.String sub : list) {
                    if (sub.contains(prefix))
                        nlist.add(sub);

                    if (sub.equals(prefix))
                        openInSubView = false;

                }
                if (openInSubView) {
                    nlist.add(prefix);
                }
                results.values = nlist;
                results.count = nlist.size();
            }
            return results;
        }

        @java.lang.SuppressWarnings("unchecked")
        @java.lang.Override
        protected void publishResults(java.lang.CharSequence constraint, android.widget.Filter.FilterResults results) {
            fitems = ((java.util.ArrayList<java.lang.String>) (results.values));
            clear();
            if (fitems != null) {
                addAll(fitems);
                notifyDataSetChanged();
            }
        }
    }
}