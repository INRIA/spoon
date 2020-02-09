package me.ccrama.redditslide.util;
import me.ccrama.redditslide.R;
import java.io.FileOutputStream;
import me.ccrama.redditslide.Reddit;
import java.io.IOException;
import java.io.File;
public class ShareUtil {
    private ShareUtil() {
    }

    public static void shareImage(final java.lang.String finalUrl, final android.content.Context context) {
        ((me.ccrama.redditslide.Reddit) (context.getApplicationContext())).getImageLoader().loadImage(finalUrl, new com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener() {
            @java.lang.Override
            public void onLoadingComplete(java.lang.String imageUri, android.view.View view, android.graphics.Bitmap loadedImage) {
                me.ccrama.redditslide.util.ShareUtil.shareImage(loadedImage, context);
            }
        });
    }

    /**
     * Converts an image to a PNG, stores it to the cache, then shares it. Saves the image to
     * /cache/shared_image for easy deletion. If the /cache/shared_image folder already exists, we
     * clear it's contents as to avoid increasing the cache size unnecessarily.
     *
     * @param bitmap
     * 		image to share
     */
    public static void shareImage(final android.graphics.Bitmap bitmap, android.content.Context context) {
        java.io.File image;// image to share

        // check to see if the cache/shared_images directory is present
        final java.io.File imagesDir = new java.io.File((context.getCacheDir().toString() + java.io.File.separator) + "shared_image");
        if (!imagesDir.exists()) {
            imagesDir.mkdir();// create the folder if it doesn't exist

        } else {
            me.ccrama.redditslide.util.FileUtil.deleteFilesInDir(imagesDir);
        }
        try {
            // creates a file in the cache; filename will be prefixed with "img" and end with ".png"
            image = java.io.File.createTempFile("img", ".png", imagesDir);
            java.io.FileOutputStream out = null;
            try {
                // convert image to png
                out = new java.io.FileOutputStream(image);
                bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out);
            } finally {
                if (out != null) {
                    out.close();
                    final android.net.Uri contentUri = me.ccrama.redditslide.util.FileUtil.getFileUri(image, context);
                    if (contentUri != null) {
                        final android.content.Intent shareImageIntent = me.ccrama.redditslide.util.FileUtil.getFileIntent(image, new android.content.Intent(android.content.Intent.ACTION_SEND), context);
                        shareImageIntent.putExtra(android.content.Intent.EXTRA_STREAM, contentUri);
                        // Select a share option
                        context.startActivity(android.content.Intent.createChooser(shareImageIntent, context.getString(me.ccrama.redditslide.R.string.misc_img_share)));
                    } else {
                        android.widget.Toast.makeText(context, context.getString(me.ccrama.redditslide.R.string.err_share_image), android.widget.Toast.LENGTH_LONG).show();
                    }
                }
            }
        } catch (java.io.IOException | java.lang.NullPointerException e) {
            e.printStackTrace();
            android.widget.Toast.makeText(context, context.getString(me.ccrama.redditslide.R.string.err_share_image), android.widget.Toast.LENGTH_LONG).show();
        }
    }
}