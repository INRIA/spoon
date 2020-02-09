package me.ccrama.redditslide.util;
/**
 * Created by adrian on 8/24/16.
 */
public class ImageUtil {
    /* TODO: Implement tolerance */
    public static void drawWithTargetColor(android.graphics.Bitmap bm, android.graphics.Bitmap src, int targetcolor, int tolerance) {
        /* int r = (targetcolor >> 16) & 0xFF;
        int g = (targetcolor >>  8) & 0xFF;
        int b = (targetcolor >>  0) & 0xFF;
         */
        int width = java.lang.Math.max(bm.getWidth(), src.getWidth());
        int height = java.lang.Math.max(bm.getHeight(), src.getHeight());
        int[] bmpixels = new int[width * height];
        bm.getPixels(bmpixels, 0, bm.getWidth(), 0, 0, width, height);
        int[] srcpixels = new int[width * height];
        src.getPixels(srcpixels, 0, src.getWidth(), 0, 0, width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (bmpixels[x + (width * y)] == targetcolor) {
                    bmpixels[x + (width * y)] = srcpixels[x + (width * y)];
                }
            }
        }
        bm.setPixels(bmpixels, 0, bm.getWidth(), 0, 0, width, height);
    }
}