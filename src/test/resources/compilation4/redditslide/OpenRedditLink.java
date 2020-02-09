package me.ccrama.redditslide;
import me.ccrama.redditslide.Activities.MultiredditOverview;
import me.ccrama.redditslide.Activities.SendMessage;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.Activities.MainActivity;
import me.ccrama.redditslide.Activities.Profile;
import me.ccrama.redditslide.Activities.SubredditView;
import me.ccrama.redditslide.Activities.Website;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.Activities.CommentsScreenSingle;
import me.ccrama.redditslide.Activities.LiveThread;
import me.ccrama.redditslide.Activities.OpenContent;
import me.ccrama.redditslide.util.LinkUtil;
import me.ccrama.redditslide.Activities.Search;
import me.ccrama.redditslide.Activities.Submit;
import me.ccrama.redditslide.Activities.Wiki;
import java.util.Objects;
import java.util.List;
public class OpenRedditLink {
    public OpenRedditLink(android.content.Context context, java.lang.String url) {
        me.ccrama.redditslide.OpenRedditLink.openUrl(context, url, true);
    }

    public OpenRedditLink(android.content.Context context, java.lang.String url, boolean openIfOther) {
        me.ccrama.redditslide.OpenRedditLink.openUrl(context, url, openIfOther);
    }

    /**
     * If the Uri has a query parameter called key, call intent.putExtra with the corresponding
     * query paramter value as a String.
     *
     * @param intent
     * 		The intent to put the data into
     * @param uri
     * 		The uri to look up the query parameter from
     * @param name
     * 		The name of the data to add to the intent. e.g. {@link Intent#EXTRA_SUBJECT}
     * @param key
     * 		The query parameter key, e.g. selftext in ?selftext=true
     * @see Intent#putExtra(String, String)
     */
    private static void putExtraIfParamExists(android.content.Intent intent, android.net.Uri uri, java.lang.String name, java.lang.String key) {
        java.lang.String param = uri.getQueryParameter(key);
        if (param != null) {
            intent.putExtra(name, param);
        }
    }

    /**
     * If the Uri has a query parameter called key, and it equals toCompare, call intent.putExtra
     * with true.
     *
     * @param intent
     * 		The intent to put the data into
     * @param uri
     * 		The uri to look up the query parameter from
     * @param name
     * 		The name of the data to add to the intent. e.g. {@link Intent#EXTRA_SUBJECT}
     * @param key
     * 		The query parameter key, e.g. selftext in ?selftext=true
     * @param toCompare
     * 		The value to compare the query parameter value to
     * @see Intent#putExtra(String, boolean)
     */
    private static void putExtraIfParamEquals(android.content.Intent intent, android.net.Uri uri, java.lang.String name, java.lang.String key, java.lang.String toCompare) {
        java.lang.String param = uri.getQueryParameter(key);
        if ((param != null) && param.equals(toCompare)) {
            intent.putExtra(name, true);
        }
    }

    // Returns true if link was in fact handled by this method. If false, further action should be taken
    public static boolean openUrl(android.content.Context context, java.lang.String url, boolean openIfOther) {
        boolean np = false;
        me.ccrama.redditslide.util.LogUtil.v("Link is " + url);
        android.net.Uri uri = me.ccrama.redditslide.OpenRedditLink.formatRedditUrl(url);
        if (uri == null) {
            me.ccrama.redditslide.util.LinkUtil.openExternally(url);
            return false;
        }
        if (uri.getHost().startsWith("np")) {
            np = true;
            uri = uri.buildUpon().authority("reddit.com").build();
        }
        me.ccrama.redditslide.OpenRedditLink.RedditLinkType type = me.ccrama.redditslide.OpenRedditLink.getRedditLinkType(uri);
        java.util.List<java.lang.String> parts = uri.getPathSegments();
        android.content.Intent i = null;
        switch (type) {
            case SHORTENED :
                {
                    i = new android.content.Intent(context, me.ccrama.redditslide.Activities.CommentsScreenSingle.class);
                    i.putExtra(me.ccrama.redditslide.Activities.CommentsScreenSingle.EXTRA_SUBREDDIT, me.ccrama.redditslide.Reddit.EMPTY_STRING);
                    i.putExtra(me.ccrama.redditslide.Activities.CommentsScreenSingle.EXTRA_CONTEXT, me.ccrama.redditslide.Reddit.EMPTY_STRING);
                    i.putExtra(me.ccrama.redditslide.Activities.CommentsScreenSingle.EXTRA_NP, np);
                    i.putExtra(me.ccrama.redditslide.Activities.CommentsScreenSingle.EXTRA_SUBMISSION, parts.get(0));
                    break;
                }
            case LIVE :
                {
                    i = new android.content.Intent(context, me.ccrama.redditslide.Activities.LiveThread.class);
                    i.putExtra(me.ccrama.redditslide.Activities.LiveThread.EXTRA_LIVEURL, parts.get(1));
                    break;
                }
            case WIKI :
                {
                    i = new android.content.Intent(context, me.ccrama.redditslide.Activities.Wiki.class);
                    i.putExtra(me.ccrama.redditslide.Activities.Wiki.EXTRA_SUBREDDIT, parts.get(1));
                    if (parts.size() >= 4) {
                        i.putExtra(me.ccrama.redditslide.Activities.Wiki.EXTRA_PAGE, parts.get(3));
                    }
                    break;
                }
            case SEARCH :
                {
                    i = new android.content.Intent(context, me.ccrama.redditslide.Activities.Search.class);
                    java.lang.String restrictSub = uri.getQueryParameter("restrict_sr");
                    if ((restrictSub != null) && restrictSub.equals("on")) {
                        i.putExtra(me.ccrama.redditslide.Activities.Search.EXTRA_SUBREDDIT, parts.get(1));
                    } else {
                        i.putExtra(me.ccrama.redditslide.Activities.Search.EXTRA_SUBREDDIT, "all");
                    }
                    me.ccrama.redditslide.OpenRedditLink.putExtraIfParamExists(i, uri, me.ccrama.redditslide.Activities.Search.EXTRA_TERM, "q");
                    me.ccrama.redditslide.OpenRedditLink.putExtraIfParamExists(i, uri, me.ccrama.redditslide.Activities.Search.EXTRA_AUTHOR, "author");
                    me.ccrama.redditslide.OpenRedditLink.putExtraIfParamExists(i, uri, me.ccrama.redditslide.Activities.Search.EXTRA_URL, "url");
                    me.ccrama.redditslide.OpenRedditLink.putExtraIfParamExists(i, uri, me.ccrama.redditslide.Activities.Search.EXTRA_SITE, "site");
                    me.ccrama.redditslide.OpenRedditLink.putExtraIfParamEquals(i, uri, me.ccrama.redditslide.Activities.Search.EXTRA_NSFW, "nsfw", "yes");
                    me.ccrama.redditslide.OpenRedditLink.putExtraIfParamEquals(i, uri, me.ccrama.redditslide.Activities.Search.EXTRA_SELF, "self", "yes");
                    me.ccrama.redditslide.OpenRedditLink.putExtraIfParamEquals(i, uri, me.ccrama.redditslide.Activities.Search.EXTRA_SELF, "selftext", "yes");
                    break;
                }
            case SUBMIT :
                {
                    i = new android.content.Intent(context, me.ccrama.redditslide.Activities.Submit.class);
                    i.putExtra(me.ccrama.redditslide.Activities.Submit.EXTRA_SUBREDDIT, parts.get(1));
                    // Submit already uses EXTRA_SUBJECT for title and EXTRA_TEXT for URL for sharing so we use those
                    me.ccrama.redditslide.OpenRedditLink.putExtraIfParamExists(i, uri, android.content.Intent.EXTRA_SUBJECT, "title");
                    // Reddit behavior: If selftext is true or if selftext doesn't exist and text does exist then page
                    // defaults to showing self post page. If selftext is false, or doesn't exist and no text then the page
                    // defaults to showing the link post page.
                    // We say isSelfText=true for the "no selftext, no text, no url" condition because that's slide's
                    // default behavior for the submit page, whereas reddit's behavior would say isSelfText=false.
                    boolean isSelfText = (uri.getBooleanQueryParameter("selftext", false) || (uri.getQueryParameter("text") != null)) || (!uri.getBooleanQueryParameter("url", false));
                    i.putExtra(me.ccrama.redditslide.Activities.Submit.EXTRA_IS_SELF, isSelfText);
                    me.ccrama.redditslide.OpenRedditLink.putExtraIfParamExists(i, uri, me.ccrama.redditslide.Activities.Submit.EXTRA_BODY, "text");
                    me.ccrama.redditslide.OpenRedditLink.putExtraIfParamExists(i, uri, android.content.Intent.EXTRA_TEXT, "url");
                    break;
                }
            case COMMENT_PERMALINK :
                {
                    i = new android.content.Intent(context, me.ccrama.redditslide.Activities.CommentsScreenSingle.class);
                    if (parts.get(0).equalsIgnoreCase("u") || parts.get(0).equalsIgnoreCase("user")) {
                        // Prepend u_ because user profile posts are made to /r/u_username
                        i.putExtra(me.ccrama.redditslide.Activities.CommentsScreenSingle.EXTRA_SUBREDDIT, "u_" + parts.get(2));
                    } else {
                        i.putExtra(me.ccrama.redditslide.Activities.CommentsScreenSingle.EXTRA_SUBREDDIT, parts.get(1));
                    }
                    i.putExtra(me.ccrama.redditslide.Activities.CommentsScreenSingle.EXTRA_SUBMISSION, parts.get(3));
                    i.putExtra(me.ccrama.redditslide.Activities.CommentsScreenSingle.EXTRA_NP, np);
                    if (parts.size() >= 6) {
                        i.putExtra(me.ccrama.redditslide.Activities.CommentsScreenSingle.EXTRA_LOADMORE, true);
                        java.lang.String end = parts.get(5);
                        if (end.length() >= 3)
                            i.putExtra(me.ccrama.redditslide.Activities.CommentsScreenSingle.EXTRA_CONTEXT, end);

                        me.ccrama.redditslide.OpenRedditLink.putExtraIfParamExists(i, uri, me.ccrama.redditslide.Activities.CommentsScreenSingle.EXTRA_CONTEXT_NUMBER, "context");
                        try {
                            java.lang.String contextNumber = uri.getQueryParameter("context");
                            if (contextNumber != null) {
                                i.putExtra(me.ccrama.redditslide.Activities.CommentsScreenSingle.EXTRA_CONTEXT_NUMBER, java.lang.Integer.parseInt(contextNumber));
                            }
                        } catch (java.lang.NumberFormatException ignored) {
                        }
                    }
                    break;
                }
            case SUBMISSION :
                {
                    i = new android.content.Intent(context, me.ccrama.redditslide.Activities.CommentsScreenSingle.class);
                    if (parts.get(0).equalsIgnoreCase("u") || parts.get(0).equalsIgnoreCase("user")) {
                        // Prepend u_ because user profile posts are made to /r/u_username
                        i.putExtra(me.ccrama.redditslide.Activities.CommentsScreenSingle.EXTRA_SUBREDDIT, "u_" + parts.get(1));
                    } else {
                        i.putExtra(me.ccrama.redditslide.Activities.CommentsScreenSingle.EXTRA_SUBREDDIT, parts.get(1));
                    }
                    i.putExtra(me.ccrama.redditslide.Activities.CommentsScreenSingle.EXTRA_CONTEXT, me.ccrama.redditslide.Reddit.EMPTY_STRING);
                    i.putExtra(me.ccrama.redditslide.Activities.CommentsScreenSingle.EXTRA_NP, np);
                    i.putExtra(me.ccrama.redditslide.Activities.CommentsScreenSingle.EXTRA_SUBMISSION, parts.get(3));
                    break;
                }
            case SUBMISSION_WITHOUT_SUB :
                {
                    i = new android.content.Intent(context, me.ccrama.redditslide.Activities.CommentsScreenSingle.class);
                    i.putExtra(me.ccrama.redditslide.Activities.CommentsScreenSingle.EXTRA_SUBREDDIT, me.ccrama.redditslide.Reddit.EMPTY_STRING);
                    i.putExtra(me.ccrama.redditslide.Activities.CommentsScreenSingle.EXTRA_CONTEXT, me.ccrama.redditslide.Reddit.EMPTY_STRING);
                    i.putExtra(me.ccrama.redditslide.Activities.CommentsScreenSingle.EXTRA_NP, np);
                    i.putExtra(me.ccrama.redditslide.Activities.CommentsScreenSingle.EXTRA_SUBMISSION, parts.get(1));
                    break;
                }
            case SUBREDDIT :
                {
                    i = new android.content.Intent(context, me.ccrama.redditslide.Activities.SubredditView.class);
                    i.putExtra(me.ccrama.redditslide.Activities.SubredditView.EXTRA_SUBREDDIT, parts.get(1));
                    break;
                }
            case MULTIREDDIT :
                {
                    i = new android.content.Intent(context, me.ccrama.redditslide.Activities.MultiredditOverview.class);
                    i.putExtra(me.ccrama.redditslide.Activities.MultiredditOverview.EXTRA_PROFILE, parts.get(1));
                    i.putExtra(me.ccrama.redditslide.Activities.MultiredditOverview.EXTRA_MULTI, parts.get(3));
                    break;
                }
            case MESSAGE :
                {
                    i = new android.content.Intent(context, me.ccrama.redditslide.Activities.SendMessage.class);
                    me.ccrama.redditslide.OpenRedditLink.putExtraIfParamExists(i, uri, me.ccrama.redditslide.Activities.SendMessage.EXTRA_NAME, "to");
                    me.ccrama.redditslide.OpenRedditLink.putExtraIfParamExists(i, uri, me.ccrama.redditslide.Activities.SendMessage.EXTRA_SUBJECT, "subject");
                    me.ccrama.redditslide.OpenRedditLink.putExtraIfParamExists(i, uri, me.ccrama.redditslide.Activities.SendMessage.EXTRA_MESSAGE, "message");
                    break;
                }
            case USER :
                {
                    java.lang.String name = parts.get(1);
                    if (name.equals("me") && me.ccrama.redditslide.Authentication.isLoggedIn)
                        name = me.ccrama.redditslide.Authentication.name;

                    i = new android.content.Intent(context, me.ccrama.redditslide.Activities.Profile.class);
                    i.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_PROFILE, name);
                    break;
                }
            case HOME :
                {
                    i = new android.content.Intent(context, me.ccrama.redditslide.Activities.MainActivity.class);
                    break;
                }
            case OTHER :
                {
                    if (openIfOther) {
                        if (context instanceof android.app.Activity) {
                            me.ccrama.redditslide.util.LinkUtil.openUrl(url, me.ccrama.redditslide.Visuals.Palette.getStatusBarColor(), ((android.app.Activity) (context)));
                        } else {
                            i = new android.content.Intent(context, me.ccrama.redditslide.Activities.Website.class);
                            i.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_URL, url);
                        }
                    } else {
                        return false;
                    }
                    break;
                }
        }
        if (i != null) {
            if (context instanceof me.ccrama.redditslide.Activities.OpenContent) {
                // i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                // i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                i.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(i);
        }
        return true;
    }

    public OpenRedditLink(android.content.Context c, java.lang.String submission, java.lang.String subreddit, java.lang.String id) {
        android.content.Intent i = new android.content.Intent(c, me.ccrama.redditslide.Activities.CommentsScreenSingle.class);
        i.putExtra(me.ccrama.redditslide.Activities.CommentsScreenSingle.EXTRA_SUBREDDIT, subreddit);
        i.putExtra(me.ccrama.redditslide.Activities.CommentsScreenSingle.EXTRA_CONTEXT, id);
        i.putExtra(me.ccrama.redditslide.Activities.CommentsScreenSingle.EXTRA_SUBMISSION, submission);
        c.startActivity(i);
    }

    /**
     * Append every path segment in segments to a Uri.Builder
     *
     * @param builder
     * 		Uri builder to append the path segments to
     * @param segments
     * 		A list of path segments to append to the builder
     * @see Uri#getPathSegments()
     */
    private static void appendPathSegments(android.net.Uri.Builder builder, java.util.List<java.lang.String> segments) {
        for (java.lang.String segment : segments) {
            builder.appendPath(segment);
        }
    }

    /**
     * Takes a reddit.com url and formats it for easier use
     *
     * @param url
     * 		The url to format
     * @return Formatted Uri without subdomains, language tags & other unused prefixes
     */
    @android.support.annotation.Nullable
    public static android.net.Uri formatRedditUrl(java.lang.String url) {
        if (url == null) {
            return null;
        }
        android.net.Uri uri = me.ccrama.redditslide.util.LinkUtil.formatURL(url);
        if (uri.getHost().equals("www.google.com")) {
            java.lang.String ampURL = uri.getQueryParameter("url");
            if (ampURL != null) {
                android.net.Uri ampURI = android.net.Uri.parse(ampURL);
                java.lang.String host = ampURI.getHost();
                if ((host != null) && host.equals("amp.reddit.com")) {
                    uri = ampURI;
                }
            }
            if (uri.getPath().startsWith("/amp/s/amp.reddit.com")) {
                java.util.List<java.lang.String> segments = uri.getPathSegments();
                android.net.Uri.Builder builder = uri.buildUpon().authority("reddit.com").path(null);
                me.ccrama.redditslide.OpenRedditLink.appendPathSegments(builder, segments.subList(3, segments.size()));
                uri = builder.build();
            }
        }
        if (uri.getHost().matches("(?i).+\\.reddit\\.com")) {
            // tests for subdomain
            android.net.Uri.Builder builder = uri.buildUpon();
            java.lang.String host = uri.getHost();
            java.lang.String subdomain = host.split("\\.", 2)[0];
            if (subdomain.equalsIgnoreCase("np")) {
                // no participation link: https://www.reddit.com/r/NoParticipation/wiki/index
                host = "npreddit.com";
            } else if (subdomain.matches("www|ssl|pay|amp|old|new|") || // country codes (e.g. en-GB, us)
            subdomain.matches("(?i)([_a-z0-9]{2}-)?[_a-z0-9]{1,2}")) {
                // Subdomains that don't require special handling
                host = "reddit.com";
            } else if (subdomain.matches("beta|blog|code|mod|out|store")) {
                return null;
            } else {
                // subdomain is a subreddit, change subreddit.reddit.com to reddit.com/r/subreddit
                host = "reddit.com";
                builder.path("r").appendPath(subdomain);
                me.ccrama.redditslide.OpenRedditLink.appendPathSegments(builder, uri.getPathSegments());
            }
            uri = builder.authority(host).build();
        }
        java.util.List<java.lang.String> segments = uri.getPathSegments();
        // Converts links such as reddit.com/help to reddit.com/r/reddit.com/wiki
        if ((!segments.isEmpty()) && segments.get(0).matches("w|wiki|help")) {
            android.net.Uri.Builder builder = uri.buildUpon().path("/r/reddit.com/wiki");
            me.ccrama.redditslide.OpenRedditLink.appendPathSegments(builder, segments.subList(1, segments.size()));
            uri = builder.build();
        }
        return uri;
    }

    /**
     * Determines the reddit link type
     *
     * @param uri
     * 		Reddit.com link
     * @return LinkType
     */
    public static me.ccrama.redditslide.OpenRedditLink.RedditLinkType getRedditLinkType(@android.support.annotation.NonNull
    android.net.Uri uri) {
        java.lang.String host = java.util.Objects.requireNonNull(uri.getHost());
        if (host.equals("redd.it")) {
            return me.ccrama.redditslide.OpenRedditLink.RedditLinkType.SHORTENED;
        }
        java.lang.String path = java.util.Objects.requireNonNull(uri.getPath());
        if (path.matches("(?i)/live/[^/]*")) {
            return me.ccrama.redditslide.OpenRedditLink.RedditLinkType.LIVE;
        } else if (path.matches("(?i)/message/compose.*")) {
            return me.ccrama.redditslide.OpenRedditLink.RedditLinkType.MESSAGE;
        } else if (path.matches("(?i)(?:/r/[a-z0-9-_.]+)?/(?:w|wiki|help).*")) {
            // Wiki link. Format: reddit.com/r/$subreddit/w[iki]/$page [optional]
            return me.ccrama.redditslide.OpenRedditLink.RedditLinkType.WIKI;
        } else if (path.matches("(?i)/r/[a-z0-9-_.]+/about.*")) {
            // Unhandled link. Format: reddit.com/r/$subreddit/about/$page [optional]
            return me.ccrama.redditslide.OpenRedditLink.RedditLinkType.OTHER;
        } else if (path.matches("(?i)/r/[a-z0-9-_.]+/search.*")) {
            // Wiki link. Format: reddit.com/r/$subreddit/search?q= [optional]
            return me.ccrama.redditslide.OpenRedditLink.RedditLinkType.SEARCH;
        } else if (path.matches("(?i)/r/[a-z0-9-_.]+/submit.*")) {
            // Submit post link. Format: reddit.com/r/$subreddit/submit
            return me.ccrama.redditslide.OpenRedditLink.RedditLinkType.SUBMIT;
        } else if (path.matches("(?i)/(?:r|u(?:ser)?)/[a-z0-9-_.]+/comments/\\w+/[\\w-]*/.+")) {
            // Permalink to comments. Format: reddit.com/r [or u or user]/$subreddit/comments/$post_id/$post_title [can be empty]/$comment_id
            return me.ccrama.redditslide.OpenRedditLink.RedditLinkType.COMMENT_PERMALINK;
        } else if (path.matches("(?i)/(?:r|u(?:ser)?)/[a-z0-9-_.]+/comments/\\w+.*")) {
            // Submission. Format: reddit.com/r [or u or user]/$subreddit/comments/$post_id/$post_title [optional]
            return me.ccrama.redditslide.OpenRedditLink.RedditLinkType.SUBMISSION;
        } else if (path.matches("(?i)/comments/\\w+.*")) {
            // Submission without a given subreddit. Format: reddit.com/comments/$post_id/$post_title [optional]
            return me.ccrama.redditslide.OpenRedditLink.RedditLinkType.SUBMISSION_WITHOUT_SUB;
        } else if (path.matches("(?i)/r/[a-z0-9-_.]+.*")) {
            // Subreddit. Format: reddit.com/r/$subreddit/$sort [optional]
            return me.ccrama.redditslide.OpenRedditLink.RedditLinkType.SUBREDDIT;
        } else if (path.matches("(?i)/u(?:ser)?/[a-z0-9-_]+.*/m/[a-z0-9_]+.*")) {
            // Multireddit. Format: reddit.com/u [or user]/$username/m/$multireddit/$sort [optional]
            return me.ccrama.redditslide.OpenRedditLink.RedditLinkType.MULTIREDDIT;
        } else if (path.matches("(?i)/u(?:ser)?/[a-z0-9-_]+.*")) {
            // User. Format: reddit.com/u [or user]/$username/$page [optional]
            return me.ccrama.redditslide.OpenRedditLink.RedditLinkType.USER;
        } else if (path.matches("^/?$")) {
            // Reddit home link
            return me.ccrama.redditslide.OpenRedditLink.RedditLinkType.HOME;
        } else {
            // Open all links that we can't open in another app
            return me.ccrama.redditslide.OpenRedditLink.RedditLinkType.OTHER;
        }
    }

    public enum RedditLinkType {

        SHORTENED,
        WIKI,
        COMMENT_PERMALINK,
        SUBMISSION,
        SUBMISSION_WITHOUT_SUB,
        SUBREDDIT,
        USER,
        SEARCH,
        MESSAGE,
        MULTIREDDIT,
        LIVE,
        SUBMIT,
        HOME,
        OTHER;}
}