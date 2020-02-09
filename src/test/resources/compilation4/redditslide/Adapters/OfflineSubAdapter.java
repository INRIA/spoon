package me.ccrama.redditslide.Adapters;
/**
 * Created by carlo_000 on 4/18/2016.
 */
public class OfflineSubAdapter extends android.widget.ArrayAdapter<java.lang.String> {
    private android.content.Context mContext;

    public OfflineSubAdapter(android.content.Context context, int textViewResourceId, java.lang.String[] objects) {
        super(context, textViewResourceId, objects);
        this.titles = objects;
        mContext = context;
    }

    java.lang.String[] titles;

    @java.lang.Override
    public android.view.View getDropDownView(int position, android.view.View convertView, android.view.ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @java.lang.Override
    public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public android.view.View getCustomView(int position, android.view.View convertView, android.view.ViewGroup parent) {
        android.view.LayoutInflater inflater = ((android.view.LayoutInflater) (mContext.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE)));
        me.ccrama.redditslide.Adapters.OfflineSubAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, null);
            holder = new me.ccrama.redditslide.Adapters.OfflineSubAdapter.ViewHolder();
            holder.txt01 = ((android.widget.TextView) (convertView.findViewById(android.R.id.text1)));
            holder.txt01.setTextColor(android.graphics.Color.WHITE);
            convertView.setTag(holder);
        } else {
            holder = ((me.ccrama.redditslide.Adapters.OfflineSubAdapter.ViewHolder) (convertView.getTag()));
        }
        holder.txt01.setText(titles[position]);
        return convertView;
    }

    class ViewHolder {
        android.widget.TextView txt01;
    }
}