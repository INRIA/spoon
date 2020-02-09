package me.ccrama.redditslide.Fragments;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
/**
 * Created by ccrama on 6/2/2015.
 */
public class Image extends android.support.v4.app.Fragment {
    java.lang.String url;

    @java.lang.Override
    public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
        android.view.ViewGroup rootView = ((android.view.ViewGroup) (inflater.inflate(me.ccrama.redditslide.R.layout.submission_imagecard, container, false)));
        final com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView image = ((com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView) (rootView.findViewById(me.ccrama.redditslide.R.id.image)));
        android.widget.TextView title = ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.title)));
        android.widget.TextView desc = ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.desc)));
        title.setVisibility(android.view.View.GONE);
        desc.setVisibility(android.view.View.GONE);
        ((me.ccrama.redditslide.Reddit) (getContext().getApplicationContext())).getImageLoader().loadImage(url, new com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener() {
            @java.lang.Override
            public void onLoadingComplete(java.lang.String imageUri, android.view.View view, android.graphics.Bitmap loadedImage) {
                image.setImage(com.davemorrissey.labs.subscaleview.ImageSource.bitmap(loadedImage));
            }
        });
        return rootView;
    }

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.os.Bundle bundle = this.getArguments();
        url = bundle.getString("url");
    }
}