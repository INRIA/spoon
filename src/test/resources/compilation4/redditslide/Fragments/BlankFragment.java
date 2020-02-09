package me.ccrama.redditslide.Fragments;
import me.ccrama.redditslide.R;
public class BlankFragment extends android.support.v4.app.Fragment {
    public android.view.View v2;

    public android.view.View realBack;

    @java.lang.Override
    public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
        android.view.View v = inflater.inflate(me.ccrama.redditslide.R.layout.blank_fragment, container, false);
        v2 = v.findViewById(me.ccrama.redditslide.R.id.back);
        realBack = v;
        return v;
    }

    public void doOffset(float percent) {
        android.widget.RelativeLayout.LayoutParams params = ((android.widget.RelativeLayout.LayoutParams) (v2.getLayoutParams()));
        params.setMargins(0, 0, ((int) ((-v2.getWidth()) * ((1.0F - percent) * 1.25))), 0);
        v2.setLayoutParams(params);
    }
}