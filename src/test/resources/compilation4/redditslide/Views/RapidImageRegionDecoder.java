package me.ccrama.redditslide.Views;
/**
 * A very simple implementation of {@link com.davemorrissey.labs.subscaleview.decoder.ImageRegionDecoder}
 * using the RapidDecoder library (https://github.com/suckgamony/RapidDecoder). For PNGs, this can
 * give more reliable decoding and better performance. For JPGs, it is slower and can run out of
 * memory with large images, but has better support for grayscale and CMYK images.
 *
 * This is an incomplete and untested implementation provided as an example only.
 */
public class RapidImageRegionDecoder implements com.davemorrissey.labs.subscaleview.decoder.ImageRegionDecoder {
    private rapid.decoder.BitmapDecoder decoder;

    @java.lang.Override
    public android.graphics.Point init(android.content.Context context, android.net.Uri uri) throws java.lang.Exception {
        decoder = rapid.decoder.BitmapDecoder.from(context, uri);
        decoder.useBuiltInDecoder(true);
        return new android.graphics.Point(decoder.sourceWidth(), decoder.sourceHeight());
    }

    @java.lang.Override
    public synchronized android.graphics.Bitmap decodeRegion(android.graphics.Rect sRect, int sampleSize) {
        try {
            return decoder.reset().region(sRect).scale(sRect.width() / sampleSize, sRect.height() / sampleSize).decode();
        } catch (java.lang.Exception e) {
            return null;
        }
    }

    @java.lang.Override
    public boolean isReady() {
        return decoder != null;
    }

    @java.lang.Override
    public void recycle() {
        rapid.decoder.BitmapDecoder.destroyMemoryCache();
        rapid.decoder.BitmapDecoder.destroyDiskCache();
        decoder.reset();
        decoder = null;
    }
}