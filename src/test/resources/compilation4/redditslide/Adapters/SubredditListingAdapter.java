package me.ccrama.redditslide.Adapters;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Visuals.Palette;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by ccrama on 8/17/2015.
 */
public class SubredditListingAdapter extends android.widget.ArrayAdapter<java.lang.String> {
    private final java.util.ArrayList<java.lang.String> fitems;

    public SubredditListingAdapter(android.content.Context context, java.util.ArrayList<java.lang.String> objects) {
        super(context, 0, objects);
        java.util.List<java.lang.String> objects1 = new java.util.ArrayList<>(objects);
        fitems = new java.util.ArrayList<>(objects);
    }

    @java.lang.Override
    public android.view.View getView(final int position, android.view.View convertView, android.view.ViewGroup parent) {
        if (convertView == null) {
            convertView = android.view.LayoutInflater.from(getContext()).inflate(me.ccrama.redditslide.R.layout.subforsublist, parent, false);
        }
        final android.widget.TextView t = ((android.widget.TextView) (convertView.findViewById(me.ccrama.redditslide.R.id.name)));
        t.setText(fitems.get(position));
        convertView.findViewById(me.ccrama.redditslide.R.id.color).setBackgroundResource(me.ccrama.redditslide.R.drawable.circle);
        convertView.findViewById(me.ccrama.redditslide.R.id.color).getBackground().setColorFilter(me.ccrama.redditslide.Visuals.Palette.getColor(fitems.get(position)), android.graphics.PorterDuff.Mode.MULTIPLY);
        return convertView;
    }
}