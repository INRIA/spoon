package me.ccrama.redditslide.Views;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.io.File;
/**
 * Helper class used to set the source and additional attributes from a variety of sources. Supports
 * use of a bitmap, asset, resource, external file or any other URI.
 *
 * When you are using a preview image, you must set the dimensions of the full size image on the
 * ImageSource object for the full size image using the {@link #dimensions(int, int)} method.
 */
public final class ImageSource {
    static final java.lang.String FILE_SCHEME = "file:///";

    static final java.lang.String ASSET_SCHEME = "file:///android_asset/";

    private final android.net.Uri uri;

    private final android.graphics.Bitmap bitmap;

    private final java.lang.Integer resource;

    private boolean tile;

    private int sWidth;

    private int sHeight;

    private android.graphics.Rect sRegion;

    private boolean cached;

    private ImageSource(android.graphics.Bitmap bitmap, boolean cached) {
        this.bitmap = bitmap;
        this.uri = null;
        this.resource = null;
        this.tile = true;
        this.sWidth = bitmap.getWidth();
        this.sHeight = bitmap.getHeight();
        this.cached = cached;
    }

    private ImageSource(android.net.Uri uri) {
        // #114 If file doesn't exist, attempt to url decode the URI and try again
        java.lang.String uriString = uri.toString();
        if (uriString.startsWith(me.ccrama.redditslide.Views.ImageSource.FILE_SCHEME)) {
            java.io.File uriFile = new java.io.File(uriString.substring(me.ccrama.redditslide.Views.ImageSource.FILE_SCHEME.length() - 1));
            if (!uriFile.exists()) {
                try {
                    uri = android.net.Uri.parse(java.net.URLDecoder.decode(uriString, "UTF-8"));
                } catch (java.io.UnsupportedEncodingException e) {
                    // Fallback to encoded URI. This exception is not expected.
                }
            }
        }
        this.bitmap = null;
        this.uri = uri;
        this.resource = null;
        this.tile = true;
    }

    private ImageSource(int resource) {
        this.bitmap = null;
        this.uri = null;
        this.resource = resource;
        this.tile = true;
    }

    /**
     * Create an instance from a resource. The correct resource for the device screen resolution will be used.
     *
     * @param resId
     * 		resource ID.
     */
    public static me.ccrama.redditslide.Views.ImageSource resource(int resId) {
        return new me.ccrama.redditslide.Views.ImageSource(resId);
    }

    /**
     * Create an instance from an asset name.
     *
     * @param assetName
     * 		asset name.
     */
    public static me.ccrama.redditslide.Views.ImageSource asset(java.lang.String assetName) {
        if (assetName == null) {
            throw new java.lang.NullPointerException("Asset name must not be null");
        }
        return me.ccrama.redditslide.Views.ImageSource.uri(me.ccrama.redditslide.Views.ImageSource.ASSET_SCHEME + assetName);
    }

    /**
     * Create an instance from a URI. If the URI does not start with a scheme, it's assumed to be the URI
     * of a file.
     *
     * @param uri
     * 		image URI.
     */
    public static me.ccrama.redditslide.Views.ImageSource uri(java.lang.String uri) {
        if (uri == null) {
            throw new java.lang.NullPointerException("Uri must not be null");
        }
        if (!uri.contains("://")) {
            if (uri.startsWith("/")) {
                uri = uri.substring(1);
            }
            uri = me.ccrama.redditslide.Views.ImageSource.FILE_SCHEME + uri;
        }
        return new me.ccrama.redditslide.Views.ImageSource(android.net.Uri.parse(uri));
    }

    /**
     * Create an instance from a URI.
     *
     * @param uri
     * 		image URI.
     */
    public static me.ccrama.redditslide.Views.ImageSource uri(android.net.Uri uri) {
        if (uri == null) {
            throw new java.lang.NullPointerException("Uri must not be null");
        }
        return new me.ccrama.redditslide.Views.ImageSource(uri);
    }

    /**
     * Provide a loaded bitmap for display.
     *
     * @param bitmap
     * 		bitmap to be displayed.
     */
    public static me.ccrama.redditslide.Views.ImageSource bitmap(android.graphics.Bitmap bitmap) {
        if (bitmap == null) {
            throw new java.lang.NullPointerException("Bitmap must not be null");
        }
        return new me.ccrama.redditslide.Views.ImageSource(bitmap, false);
    }

    /**
     * Provide a loaded and cached bitmap for display. This bitmap will not be recycled when it is no
     * longer needed. Use this method if you loaded the bitmap with an image loader such as Picasso
     * or Volley.
     *
     * @param bitmap
     * 		bitmap to be displayed.
     */
    public static me.ccrama.redditslide.Views.ImageSource cachedBitmap(android.graphics.Bitmap bitmap) {
        if (bitmap == null) {
            throw new java.lang.NullPointerException("Bitmap must not be null");
        }
        return new me.ccrama.redditslide.Views.ImageSource(bitmap, true);
    }

    /**
     * Enable tiling of the image. This does not apply to preview images which are always loaded as a single bitmap.,
     * and tiling cannot be disabled when displaying a region of the source image.
     *
     * @return this instance for chaining.
     */
    public me.ccrama.redditslide.Views.ImageSource tilingEnabled() {
        return tiling(true);
    }

    /**
     * Disable tiling of the image. This does not apply to preview images which are always loaded as a single bitmap,
     * and tiling cannot be disabled when displaying a region of the source image.
     *
     * @return this instance for chaining.
     */
    public me.ccrama.redditslide.Views.ImageSource tilingDisabled() {
        return tiling(false);
    }

    /**
     * Enable or disable tiling of the image. This does not apply to preview images which are always loaded as a single bitmap,
     * and tiling cannot be disabled when displaying a region of the source image.
     *
     * @return this instance for chaining.
     */
    public me.ccrama.redditslide.Views.ImageSource tiling(boolean tile) {
        this.tile = tile;
        return this;
    }

    /**
     * Use a region of the source image. Region must be set independently for the full size image and the preview if
     * you are using one.
     *
     * @return this instance for chaining.
     */
    public me.ccrama.redditslide.Views.ImageSource region(android.graphics.Rect sRegion) {
        this.sRegion = sRegion;
        setInvariants();
        return this;
    }

    /**
     * Declare the dimensions of the image. This is only required for a full size image, when you are specifying a URI
     * and also a preview image. When displaying a bitmap object, or not using a preview, you do not need to declare
     * the image dimensions. Note if the declared dimensions are found to be incorrect, the view will reset.
     *
     * @return this instance for chaining.
     */
    public me.ccrama.redditslide.Views.ImageSource dimensions(int sWidth, int sHeight) {
        if (bitmap == null) {
            this.sWidth = sWidth;
            this.sHeight = sHeight;
        }
        setInvariants();
        return this;
    }

    private void setInvariants() {
        if (this.sRegion != null) {
            this.tile = true;
            this.sWidth = this.sRegion.width();
            this.sHeight = this.sRegion.height();
        }
    }

    protected final android.net.Uri getUri() {
        return uri;
    }

    protected final android.graphics.Bitmap getBitmap() {
        return bitmap;
    }

    protected final java.lang.Integer getResource() {
        return resource;
    }

    protected final boolean getTile() {
        return tile;
    }

    protected final int getSWidth() {
        return sWidth;
    }

    protected final int getSHeight() {
        return sHeight;
    }

    protected final android.graphics.Rect getSRegion() {
        return sRegion;
    }

    protected final boolean isCached() {
        return cached;
    }
}