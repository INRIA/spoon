/**
 * Created by ccrama on 10/30/2015.
 */
package me.ccrama.redditslide.Adapters;
import me.ccrama.redditslide.R;
public class ErrorAdapter extends android.support.v7.widget.RecyclerView.Adapter<me.ccrama.redditslide.Adapters.ErrorAdapter.ViewHolder> {
    @java.lang.Override
    public me.ccrama.redditslide.Adapters.ErrorAdapter.ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        android.view.View v = android.view.LayoutInflater.from(parent.getContext()).inflate(me.ccrama.redditslide.R.layout.nointernet, parent, false);
        return new me.ccrama.redditslide.Adapters.ErrorAdapter.ViewHolder(v);
    }

    @java.lang.Override
    public void onBindViewHolder(me.ccrama.redditslide.Adapters.ErrorAdapter.ViewHolder holder, int position) {
    }

    @java.lang.Override
    public int getItemCount() {
        return 1;
    }

    public static class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public ViewHolder(android.view.View itemView) {
            super(itemView);
        }
    }
}