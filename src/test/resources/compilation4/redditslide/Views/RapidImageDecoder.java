package me.ccrama.redditslide.Views;
/**
 * A very simple implementation of {@link com.davemorrissey.labs.subscaleview.decoder.ImageRegionDecoder}
 * using the RapidDecoder library (https://github.com/suckgamony/RapidDecoder). For PNGs, this can
 * give more reliable decoding and better performance. For JPGs, it is slower and can run out of
 * memory with large images, but has better support for grayscale and CMYK images.
 *
 * This is an incomplete and untested implementation provided as an example only.
 */
public class RapidImageDecoder implements me.ccrama.redditslide.Views.ImageDecoder {
    @java.lang.Override
    public android.graphics.Bitmap decode(android.content.Context context, android.net.Uri uri) {
        return rapid.decoder.BitmapDecoder.from(context, uri).useBuiltInDecoder(true).config(android.graphics.Bitmap.Config.RGB_565).decode();
    }
}