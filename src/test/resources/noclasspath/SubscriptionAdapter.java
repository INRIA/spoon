package org.bottiger.podcast.adapters;

import android.graphics.Bitmap;
import android.widget.ImageView;
import com.bumptech.glide.g.a.c;
import org.bottiger.podcast.adapters.viewholders.subscription.SubscriptionViewHolder;

class SubscriptionAdapter$3 extends c {
    final /* synthetic */ SubscriptionAdapter this$0;
    final /* synthetic */ SubscriptionViewHolder val$argHolder;

    SubscriptionAdapter$3(SubscriptionAdapter subscriptionAdapter, ImageView imageView, SubscriptionViewHolder subscriptionViewHolder) {
        this.this$0 = subscriptionAdapter;
        this.val$argHolder = subscriptionViewHolder;
        super(imageView);
    }

    protected void setResource(Bitmap bitmap) {
        this.val$argHolder.image.setImageBitmap(bitmap);
    }
}
