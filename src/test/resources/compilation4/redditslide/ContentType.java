package me.ccrama.redditslide;
import java.util.Locale;
import java.util.HashMap;
import java.net.URISyntaxException;
import me.ccrama.redditslide.*;
import java.net.URI;
/**
 * Created by ccrama on 5/26/2015.
 */
public class ContentType {
    /**
     * Checks if {@code host} is contains by any of the provided {@code bases}
     * <p/>
     * For example "www.youtube.com" contains "youtube.com" but not "notyoutube.com" or
     * "youtube.co.uk"
     *
     * @param host
     * 		A hostname from e.g. {@link URI#getHost()}
     * @param bases
     * 		Any number of hostnames to compare against {@code host}
     * @return If {@code host} contains any of {@code bases}
     */
    public static boolean hostContains(java.lang.String host, java.lang.String... bases) {
        if ((host == null) || host.isEmpty())
            return false;

        for (java.lang.String base : bases) {
            if ((base == null) || base.isEmpty())
                continue;

            final int index = host.lastIndexOf(base);
            if ((index < 0) || ((index + base.length()) != host.length()))
                continue;

            if ((base.length() == host.length()) || (host.charAt(index - 1) == '.'))
                return true;

        }
        return false;
    }

    public static boolean isGif(java.net.URI uri) {
        try {
            final java.lang.String host = uri.getHost().toLowerCase(java.util.Locale.ENGLISH);
            final java.lang.String path = uri.getPath().toLowerCase(java.util.Locale.ENGLISH);
            return ((((me.ccrama.redditslide.ContentType.hostContains(host, "gfycat.com") || me.ccrama.redditslide.ContentType.hostContains(host, "v.redd.it")) || path.endsWith(".gif")) || path.endsWith(".gifv")) || path.endsWith(".webm")) || path.endsWith(".mp4");
        } catch (java.lang.NullPointerException e) {
            return false;
        }
    }

    public static boolean isGifLoadInstantly(java.net.URI uri) {
        try {
            final java.lang.String host = uri.getHost().toLowerCase(java.util.Locale.ENGLISH);
            final java.lang.String path = uri.getPath().toLowerCase(java.util.Locale.ENGLISH);
            return ((me.ccrama.redditslide.ContentType.hostContains(host, "gfycat.com") || me.ccrama.redditslide.ContentType.hostContains(host, "v.redd.it")) || (me.ccrama.redditslide.ContentType.hostContains(host, "imgur.com") && ((path.endsWith(".gif") || path.endsWith(".gifv")) || path.endsWith(".webm")))) || path.endsWith(".mp4");
        } catch (java.lang.NullPointerException e) {
            return false;
        }
    }

    public static boolean isImage(java.net.URI uri) {
        try {
            final java.lang.String host = uri.getHost().toLowerCase(java.util.Locale.ENGLISH);
            final java.lang.String path = uri.getPath().toLowerCase(java.util.Locale.ENGLISH);
            return ((host.equals("i.reddituploads.com") || path.endsWith(".png")) || path.endsWith(".jpg")) || path.endsWith(".jpeg");
        } catch (java.lang.NullPointerException e) {
            return false;
        }
    }

    public static boolean isAlbum(java.net.URI uri) {
        try {
            final java.lang.String host = uri.getHost().toLowerCase(java.util.Locale.ENGLISH);
            final java.lang.String path = uri.getPath().toLowerCase(java.util.Locale.ENGLISH);
            return me.ccrama.redditslide.ContentType.hostContains(host, "imgur.com", "bildgur.de") && (((path.startsWith("/a/") || path.startsWith("/gallery/")) || path.startsWith("/g/")) || path.contains(","));
        } catch (java.lang.NullPointerException e) {
            return false;
        }
    }

    public static boolean isVideo(java.net.URI uri) {
        try {
            final java.lang.String host = uri.getHost().toLowerCase(java.util.Locale.ENGLISH);
            final java.lang.String path = uri.getPath().toLowerCase(java.util.Locale.ENGLISH);
            return ((me.ccrama.redditslide.Reddit.videoPlugin && me.ccrama.redditslide.ContentType.hostContains(host, "youtu.be", "youtube.com", "youtube.co.uk")) && (!path.contains("/user/"))) && (!path.contains("/channel/"));
        } catch (java.lang.NullPointerException e) {
            return false;
        }
    }

    public static boolean isImgurLink(java.lang.String url) {
        try {
            final java.net.URI uri = new java.net.URI(url);
            final java.lang.String host = uri.getHost().toLowerCase(java.util.Locale.ENGLISH);
            return ((me.ccrama.redditslide.ContentType.hostContains(host, "imgur.com", "bildgur.de") && (!me.ccrama.redditslide.ContentType.isAlbum(uri))) && (!me.ccrama.redditslide.ContentType.isGif(uri))) && (!me.ccrama.redditslide.ContentType.isImage(uri));
        } catch (java.net.URISyntaxException | java.lang.NullPointerException e) {
            return false;
        }
    }

    /**
     * Attempt to determine the content type of a link from the URL
     *
     * @param url
     * 		URL to get ContentType from
     * @return ContentType of the URL
     */
    public static me.ccrama.redditslide.ContentType.Type getContentType(java.lang.String url) {
        if ((!url.startsWith("//")) && ((((((((url.startsWith("/") && (url.length() < 4)) || url.startsWith("#spoiler")) || url.startsWith("/spoiler")) || url.startsWith("#s-")) || url.equals("#s")) || url.equals("#ln")) || url.equals("#b")) || url.equals("#sp"))) {
            return me.ccrama.redditslide.ContentType.Type.SPOILER;
        }
        if (url.startsWith("mailto:")) {
            return me.ccrama.redditslide.ContentType.Type.EXTERNAL;
        }
        if (url.startsWith("//"))
            url = "https:" + url;

        if (url.startsWith("/"))
            url = "reddit.com" + url;

        if (!url.contains("://"))
            url = "http://" + url;

        try {
            final java.net.URI uri = new java.net.URI(url);
            final java.lang.String host = uri.getHost().toLowerCase(java.util.Locale.ENGLISH);
            final java.lang.String scheme = uri.getScheme().toLowerCase(java.util.Locale.ENGLISH);
            if (me.ccrama.redditslide.ContentType.hostContains(host, "v.redd.it") || (host.equals("reddit.com") && url.contains("reddit.com/video/"))) {
                if (url.contains("DASH_")) {
                    return me.ccrama.redditslide.ContentType.Type.VREDDIT_DIRECT;
                } else {
                    return me.ccrama.redditslide.ContentType.Type.VREDDIT_REDIRECT;
                }
            }
            if ((!scheme.equals("http")) && (!scheme.equals("https"))) {
                return me.ccrama.redditslide.ContentType.Type.EXTERNAL;
            }
            if (me.ccrama.redditslide.ContentType.isVideo(uri)) {
                return me.ccrama.redditslide.ContentType.Type.VIDEO;
            }
            if (me.ccrama.redditslide.PostMatch.openExternal(url)) {
                return me.ccrama.redditslide.ContentType.Type.EXTERNAL;
            }
            if (me.ccrama.redditslide.ContentType.isGif(uri)) {
                return me.ccrama.redditslide.ContentType.Type.GIF;
            }
            if (me.ccrama.redditslide.ContentType.isImage(uri)) {
                return me.ccrama.redditslide.ContentType.Type.IMAGE;
            }
            if (me.ccrama.redditslide.ContentType.isAlbum(uri)) {
                return me.ccrama.redditslide.ContentType.Type.ALBUM;
            }
            if (me.ccrama.redditslide.ContentType.hostContains(host, "imgur.com", "bildgur.de")) {
                return me.ccrama.redditslide.ContentType.Type.IMGUR;
            }
            if ((me.ccrama.redditslide.ContentType.hostContains(host, "xkcd.com") && (!me.ccrama.redditslide.ContentType.hostContains("imgs.xkcd.com"))) && (!me.ccrama.redditslide.ContentType.hostContains("what-if.xkcd.com"))) {
                return me.ccrama.redditslide.ContentType.Type.XKCD;
            }
            if (me.ccrama.redditslide.ContentType.hostContains(host, "tumblr.com") && uri.getPath().contains("post")) {
                return me.ccrama.redditslide.ContentType.Type.TUMBLR;
            }
            if (me.ccrama.redditslide.ContentType.hostContains(host, "reddit.com", "redd.it")) {
                return me.ccrama.redditslide.ContentType.Type.REDDIT;
            }
            if (me.ccrama.redditslide.ContentType.hostContains(host, "vid.me")) {
                return me.ccrama.redditslide.ContentType.Type.VID_ME;
            }
            if (me.ccrama.redditslide.ContentType.hostContains(host, "deviantart.com")) {
                return me.ccrama.redditslide.ContentType.Type.DEVIANTART;
            }
            if (me.ccrama.redditslide.ContentType.hostContains(host, "streamable.com")) {
                return me.ccrama.redditslide.ContentType.Type.STREAMABLE;
            }
            return me.ccrama.redditslide.ContentType.Type.LINK;
        } catch (java.net.URISyntaxException | java.lang.NullPointerException e) {
            // a valid link but something un-encoded in the URL
            if ((e.getMessage() != null) && ((e.getMessage().contains("Illegal character in fragment") || e.getMessage().contains("Illegal character in query")) || e.getMessage().contains("Illegal character in path"))) {
                return me.ccrama.redditslide.ContentType.Type.LINK;
            }
            e.printStackTrace();
            return me.ccrama.redditslide.ContentType.Type.NONE;
        }
    }

    /**
     * Attempts to determine the content of a submission, mostly based on the URL
     *
     * @param submission
     * 		Submission to get the content type from
     * @return Content type of the Submission
     * @see #getContentType(String)
     */
    public static me.ccrama.redditslide.ContentType.Type getContentType(net.dean.jraw.models.Submission submission) {
        if (submission == null) {
            return me.ccrama.redditslide.ContentType.Type.SELF;// hopefully shouldn't be null, but catch it in case

        }
        if (submission.isSelfPost()) {
            return me.ccrama.redditslide.ContentType.Type.SELF;
        }
        final java.lang.String url = submission.getUrl();
        final me.ccrama.redditslide.ContentType.Type basicType = me.ccrama.redditslide.ContentType.getContentType(url);
        // TODO: Decide whether internal youtube links should be EMBEDDED or LINK
        /* Disable this for nowif (basicType.equals(Type.LINK) && submission.getDataNode().has("media_embed") && submission
        .getDataNode()
        .get("media_embed")
        .has("content")) {
        return Type.EMBEDDED;
        }
         */
        return basicType;
    }

    public static boolean displayImage(me.ccrama.redditslide.ContentType.Type t) {
        switch (t) {
            case ALBUM :
            case DEVIANTART :
            case IMAGE :
            case XKCD :
            case TUMBLR :
            case IMGUR :
            case SELF :
                return true;
            default :
                return false;
        }
    }

    public static boolean fullImage(me.ccrama.redditslide.ContentType.Type t) {
        switch (t) {
            case ALBUM :
            case DEVIANTART :
            case GIF :
            case IMAGE :
            case IMGUR :
            case STREAMABLE :
            case TUMBLR :
            case XKCD :
            case VIDEO :
            case SELF :
            case VREDDIT_DIRECT :
            case VREDDIT_REDIRECT :
            case VID_ME :
                return true;
            case EMBEDDED :
            case EXTERNAL :
            case LINK :
            case NONE :
            case REDDIT :
            case SPOILER :
            default :
                return false;
        }
    }

    public static boolean mediaType(me.ccrama.redditslide.ContentType.Type t) {
        switch (t) {
            case ALBUM :
            case DEVIANTART :
            case GIF :
            case IMAGE :
            case TUMBLR :
            case XKCD :
            case IMGUR :
            case VREDDIT_DIRECT :
            case VREDDIT_REDIRECT :
            case STREAMABLE :
            case VID_ME :
                return true;
            default :
                return false;
        }
    }

    /**
     * Returns a string identifier for a submission e.g. Link, GIF, NSFW Image
     *
     * @param submission
     * 		Submission to get the description for
     * @return the String identifier
     */
    private static int getContentID(net.dean.jraw.models.Submission submission) {
        return me.ccrama.redditslide.ContentType.getContentID(me.ccrama.redditslide.ContentType.getContentType(submission), submission.isNsfw());
    }

    public static int getContentID(me.ccrama.redditslide.ContentType.Type contentType, boolean nsfw) {
        if (nsfw) {
            switch (contentType) {
                case ALBUM :
                    return me.ccrama.redditslide.R.string.type_nsfw_album;
                case EMBEDDED :
                    return me.ccrama.redditslide.R.string.type_nsfw_emb;
                case EXTERNAL :
                    return me.ccrama.redditslide.R.string.type_nsfw_link;
                case GIF :
                    return me.ccrama.redditslide.R.string.type_nsfw_gif;
                case IMAGE :
                    return me.ccrama.redditslide.R.string.type_nsfw_img;
                case TUMBLR :
                    return me.ccrama.redditslide.R.string.type_nsfw_tumblr;
                case IMGUR :
                    return me.ccrama.redditslide.R.string.type_nsfw_imgur;
                case LINK :
                    return me.ccrama.redditslide.R.string.type_nsfw_link;
                case VIDEO :
                case VREDDIT_DIRECT :
                case VREDDIT_REDIRECT :
                case VID_ME :
                    return me.ccrama.redditslide.R.string.type_nsfw_video;
            }
        } else {
            switch (contentType) {
                case ALBUM :
                    return me.ccrama.redditslide.R.string.type_album;
                case XKCD :
                    return me.ccrama.redditslide.R.string.type_xkcd;
                case DEVIANTART :
                    return me.ccrama.redditslide.R.string.type_deviantart;
                case EMBEDDED :
                    return me.ccrama.redditslide.R.string.type_emb;
                case EXTERNAL :
                    return me.ccrama.redditslide.R.string.type_external;
                case GIF :
                    return me.ccrama.redditslide.R.string.type_gif;
                case IMAGE :
                    return me.ccrama.redditslide.R.string.type_img;
                case IMGUR :
                    return me.ccrama.redditslide.R.string.type_imgur;
                case LINK :
                    return me.ccrama.redditslide.R.string.type_link;
                case TUMBLR :
                    return me.ccrama.redditslide.R.string.type_tumblr;
                case NONE :
                    return me.ccrama.redditslide.R.string.type_title_only;
                case REDDIT :
                    return me.ccrama.redditslide.R.string.type_reddit;
                case SELF :
                    return me.ccrama.redditslide.R.string.type_selftext;
                case STREAMABLE :
                    return me.ccrama.redditslide.R.string.type_streamable;
                case VIDEO :
                    return me.ccrama.redditslide.R.string.type_youtube;
                case VID_ME :
                    return me.ccrama.redditslide.R.string.type_vidme;
                case VREDDIT_REDIRECT :
                case VREDDIT_DIRECT :
                    return me.ccrama.redditslide.R.string.type_vreddit;
            }
        }
        return me.ccrama.redditslide.R.string.type_link;
    }

    static java.util.HashMap<java.lang.String, java.lang.String> contentDescriptions = new java.util.HashMap<>();

    /**
     * Returns a description of the submission, for example "Link", "NSFW link", if the link is set
     * to open externally it returns the package name of the app that opens it, or "External"
     *
     * @param submission
     * 		The submission to describe
     * @param context
     * 		Current context
     * @return The content description
     */
    public static java.lang.String getContentDescription(net.dean.jraw.models.Submission submission, android.content.Context context) {
        final int generic = me.ccrama.redditslide.ContentType.getContentID(submission);
        final android.content.res.Resources res = context.getResources();
        final java.lang.String domain = submission.getDomain();
        if (generic != me.ccrama.redditslide.R.string.type_external) {
            return res.getString(generic);
        }
        if (me.ccrama.redditslide.ContentType.contentDescriptions.containsKey(domain)) {
            return me.ccrama.redditslide.ContentType.contentDescriptions.get(domain);
        }
        try {
            final android.content.pm.PackageManager pm = context.getPackageManager();
            final android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(submission.getUrl()));
            final java.lang.String packageName = pm.resolveActivity(intent, 0).activityInfo.packageName;
            java.lang.String description;
            if (!packageName.equals("android")) {
                description = pm.getApplicationLabel(pm.getApplicationInfo(packageName, android.content.pm.PackageManager.GET_META_DATA)).toString();
            } else {
                description = res.getString(generic);
            }
            // Looking up a package name takes a long time (3~10ms), memoize it
            me.ccrama.redditslide.ContentType.contentDescriptions.put(domain, description);
            return description;
        } catch (android.content.pm.PackageManager.NameNotFoundException | java.lang.NullPointerException e) {
            me.ccrama.redditslide.ContentType.contentDescriptions.put(domain, res.getString(generic));
            return res.getString(generic);
        }
    }

    public static boolean isImgurImage(java.lang.String lqUrl) {
        try {
            final java.net.URI uri = new java.net.URI(lqUrl);
            final java.lang.String host = uri.getHost().toLowerCase(java.util.Locale.ENGLISH);
            final java.lang.String path = uri.getPath().toLowerCase(java.util.Locale.ENGLISH);
            return (host.contains("imgur.com") || host.contains("bildgur.de")) && ((path.endsWith(".png") || path.endsWith(".jpg")) || path.endsWith(".jpeg"));
        } catch (java.lang.Exception e) {
            return false;
        }
    }

    public static boolean isImgurHash(java.lang.String lqUrl) {
        try {
            final java.net.URI uri = new java.net.URI(lqUrl);
            final java.lang.String host = uri.getHost().toLowerCase(java.util.Locale.ENGLISH);
            final java.lang.String path = uri.getPath().toLowerCase(java.util.Locale.ENGLISH);
            return host.contains("imgur.com") && (!((path.endsWith(".png") && (!path.endsWith(".jpg"))) && (!path.endsWith(".jpeg"))));
        } catch (java.lang.Exception e) {
            return false;
        }
    }

    public enum Type {

        ALBUM,
        DEVIANTART,
        EMBEDDED,
        EXTERNAL,
        GIF,
        VREDDIT_DIRECT,
        VREDDIT_REDIRECT,
        IMAGE,
        IMGUR,
        LINK,
        NONE,
        REDDIT,
        SELF,
        SPOILER,
        STREAMABLE,
        VIDEO,
        XKCD,
        TUMBLR,
        VID_ME;}
}