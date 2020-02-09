package me.ccrama.redditslide.Tumblr;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;
@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({ "blog_name", "id", "post_url", "slug", "type", "date", "timestamp", "state", "format", "reblog_key", "tags", "short_url", "summary", "recommended_source", "recommended_color", "highlighted", "note_count", "caption", "reblog", "trail", "photoset_layout", "photos", "can_send_in_message", "can_like", "can_reblog", "display_avatar" })
public class Post {
    @com.fasterxml.jackson.annotation.JsonProperty("blog_name")
    private java.lang.String blogName;

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    private java.lang.Double id;

    @com.fasterxml.jackson.annotation.JsonProperty("post_url")
    private java.lang.String postUrl;

    @com.fasterxml.jackson.annotation.JsonProperty("slug")
    private java.lang.String slug;

    @com.fasterxml.jackson.annotation.JsonProperty("type")
    private java.lang.String type;

    @com.fasterxml.jackson.annotation.JsonProperty("date")
    private java.lang.String date;

    @com.fasterxml.jackson.annotation.JsonProperty("timestamp")
    private java.lang.Double timestamp;

    @com.fasterxml.jackson.annotation.JsonProperty("state")
    private java.lang.String state;

    @com.fasterxml.jackson.annotation.JsonProperty("format")
    private java.lang.String format;

    @com.fasterxml.jackson.annotation.JsonProperty("reblog_key")
    private java.lang.String reblogKey;

    @com.fasterxml.jackson.annotation.JsonProperty("tags")
    private java.util.List<java.lang.String> tags = new java.util.ArrayList<java.lang.String>();

    @com.fasterxml.jackson.annotation.JsonProperty("short_url")
    private java.lang.String shortUrl;

    @com.fasterxml.jackson.annotation.JsonProperty("summary")
    private java.lang.String summary;

    @com.fasterxml.jackson.annotation.JsonProperty("recommended_source")
    private java.lang.Object recommendedSource;

    @com.fasterxml.jackson.annotation.JsonProperty("recommended_color")
    private java.lang.Object recommendedColor;

    @com.fasterxml.jackson.annotation.JsonProperty("highlighted")
    private java.util.List<java.lang.Object> highlighted = new java.util.ArrayList<java.lang.Object>();

    @com.fasterxml.jackson.annotation.JsonProperty("note_count")
    private java.lang.Integer noteCount;

    @com.fasterxml.jackson.annotation.JsonProperty("caption")
    private java.lang.String caption;

    @com.fasterxml.jackson.annotation.JsonProperty("reblog")
    private me.ccrama.redditslide.Tumblr.Reblog reblog;

    @com.fasterxml.jackson.annotation.JsonProperty("trail")
    private java.util.List<me.ccrama.redditslide.Tumblr.Trail> trail = new java.util.ArrayList<me.ccrama.redditslide.Tumblr.Trail>();

    @com.fasterxml.jackson.annotation.JsonProperty("photoset_layout")
    private java.lang.String photosetLayout;

    @com.fasterxml.jackson.annotation.JsonProperty("photos")
    private java.util.List<me.ccrama.redditslide.Tumblr.Photo> photos = new java.util.ArrayList<me.ccrama.redditslide.Tumblr.Photo>();

    @com.fasterxml.jackson.annotation.JsonProperty("can_send_in_message")
    private java.lang.Boolean canSendInMessage;

    @com.fasterxml.jackson.annotation.JsonProperty("can_like")
    private java.lang.Boolean canLike;

    @com.fasterxml.jackson.annotation.JsonProperty("can_reblog")
    private java.lang.Boolean canReblog;

    @com.fasterxml.jackson.annotation.JsonProperty("display_avatar")
    private java.lang.Boolean displayAvatar;

    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.Map<java.lang.String, java.lang.Object> additionalProperties = new java.util.HashMap<java.lang.String, java.lang.Object>();

    /**
     *
     *
     * @return The blogName
     */
    @com.fasterxml.jackson.annotation.JsonProperty("blog_name")
    public java.lang.String getBlogName() {
        return blogName;
    }

    /**
     *
     *
     * @param blogName
     * 		The blog_name
     */
    @com.fasterxml.jackson.annotation.JsonProperty("blog_name")
    public void setBlogName(java.lang.String blogName) {
        this.blogName = blogName;
    }

    /**
     *
     *
     * @return The id
     */
    @com.fasterxml.jackson.annotation.JsonProperty("id")
    public java.lang.Double getId() {
        return id;
    }

    /**
     *
     *
     * @param id
     * 		The id
     */
    @com.fasterxml.jackson.annotation.JsonProperty("id")
    public void setId(java.lang.Double id) {
        this.id = id;
    }

    /**
     *
     *
     * @return The postUrl
     */
    @com.fasterxml.jackson.annotation.JsonProperty("post_url")
    public java.lang.String getPostUrl() {
        return postUrl;
    }

    /**
     *
     *
     * @param postUrl
     * 		The post_url
     */
    @com.fasterxml.jackson.annotation.JsonProperty("post_url")
    public void setPostUrl(java.lang.String postUrl) {
        this.postUrl = postUrl;
    }

    /**
     *
     *
     * @return The slug
     */
    @com.fasterxml.jackson.annotation.JsonProperty("slug")
    public java.lang.String getSlug() {
        return slug;
    }

    /**
     *
     *
     * @param slug
     * 		The slug
     */
    @com.fasterxml.jackson.annotation.JsonProperty("slug")
    public void setSlug(java.lang.String slug) {
        this.slug = slug;
    }

    /**
     *
     *
     * @return The type
     */
    @com.fasterxml.jackson.annotation.JsonProperty("type")
    public java.lang.String getType() {
        return type;
    }

    /**
     *
     *
     * @param type
     * 		The type
     */
    @com.fasterxml.jackson.annotation.JsonProperty("type")
    public void setType(java.lang.String type) {
        this.type = type;
    }

    /**
     *
     *
     * @return The date
     */
    @com.fasterxml.jackson.annotation.JsonProperty("date")
    public java.lang.String getDate() {
        return date;
    }

    /**
     *
     *
     * @param date
     * 		The date
     */
    @com.fasterxml.jackson.annotation.JsonProperty("date")
    public void setDate(java.lang.String date) {
        this.date = date;
    }

    /**
     *
     *
     * @return The timestamp
     */
    @com.fasterxml.jackson.annotation.JsonProperty("timestamp")
    public java.lang.Double getTimestamp() {
        return timestamp;
    }

    /**
     *
     *
     * @param timestamp
     * 		The timestamp
     */
    @com.fasterxml.jackson.annotation.JsonProperty("timestamp")
    public void setTimestamp(java.lang.Double timestamp) {
        this.timestamp = timestamp;
    }

    /**
     *
     *
     * @return The state
     */
    @com.fasterxml.jackson.annotation.JsonProperty("state")
    public java.lang.String getState() {
        return state;
    }

    /**
     *
     *
     * @param state
     * 		The state
     */
    @com.fasterxml.jackson.annotation.JsonProperty("state")
    public void setState(java.lang.String state) {
        this.state = state;
    }

    /**
     *
     *
     * @return The format
     */
    @com.fasterxml.jackson.annotation.JsonProperty("format")
    public java.lang.String getFormat() {
        return format;
    }

    /**
     *
     *
     * @param format
     * 		The format
     */
    @com.fasterxml.jackson.annotation.JsonProperty("format")
    public void setFormat(java.lang.String format) {
        this.format = format;
    }

    /**
     *
     *
     * @return The reblogKey
     */
    @com.fasterxml.jackson.annotation.JsonProperty("reblog_key")
    public java.lang.String getReblogKey() {
        return reblogKey;
    }

    /**
     *
     *
     * @param reblogKey
     * 		The reblog_key
     */
    @com.fasterxml.jackson.annotation.JsonProperty("reblog_key")
    public void setReblogKey(java.lang.String reblogKey) {
        this.reblogKey = reblogKey;
    }

    /**
     *
     *
     * @return The tags
     */
    @com.fasterxml.jackson.annotation.JsonProperty("tags")
    public java.util.List<java.lang.String> getTags() {
        return tags;
    }

    /**
     *
     *
     * @param tags
     * 		The tags
     */
    @com.fasterxml.jackson.annotation.JsonProperty("tags")
    public void setTags(java.util.List<java.lang.String> tags) {
        this.tags = tags;
    }

    /**
     *
     *
     * @return The shortUrl
     */
    @com.fasterxml.jackson.annotation.JsonProperty("short_url")
    public java.lang.String getShortUrl() {
        return shortUrl;
    }

    /**
     *
     *
     * @param shortUrl
     * 		The short_url
     */
    @com.fasterxml.jackson.annotation.JsonProperty("short_url")
    public void setShortUrl(java.lang.String shortUrl) {
        this.shortUrl = shortUrl;
    }

    /**
     *
     *
     * @return The summary
     */
    @com.fasterxml.jackson.annotation.JsonProperty("summary")
    public java.lang.String getSummary() {
        return summary;
    }

    /**
     *
     *
     * @param summary
     * 		The summary
     */
    @com.fasterxml.jackson.annotation.JsonProperty("summary")
    public void setSummary(java.lang.String summary) {
        this.summary = summary;
    }

    /**
     *
     *
     * @return The recommendedSource
     */
    @com.fasterxml.jackson.annotation.JsonProperty("recommended_source")
    public java.lang.Object getRecommendedSource() {
        return recommendedSource;
    }

    /**
     *
     *
     * @param recommendedSource
     * 		The recommended_source
     */
    @com.fasterxml.jackson.annotation.JsonProperty("recommended_source")
    public void setRecommendedSource(java.lang.Object recommendedSource) {
        this.recommendedSource = recommendedSource;
    }

    /**
     *
     *
     * @return The recommendedColor
     */
    @com.fasterxml.jackson.annotation.JsonProperty("recommended_color")
    public java.lang.Object getRecommendedColor() {
        return recommendedColor;
    }

    /**
     *
     *
     * @param recommendedColor
     * 		The recommended_color
     */
    @com.fasterxml.jackson.annotation.JsonProperty("recommended_color")
    public void setRecommendedColor(java.lang.Object recommendedColor) {
        this.recommendedColor = recommendedColor;
    }

    /**
     *
     *
     * @return The highlighted
     */
    @com.fasterxml.jackson.annotation.JsonProperty("highlighted")
    public java.util.List<java.lang.Object> getHighlighted() {
        return highlighted;
    }

    /**
     *
     *
     * @param highlighted
     * 		The highlighted
     */
    @com.fasterxml.jackson.annotation.JsonProperty("highlighted")
    public void setHighlighted(java.util.List<java.lang.Object> highlighted) {
        this.highlighted = highlighted;
    }

    /**
     *
     *
     * @return The noteCount
     */
    @com.fasterxml.jackson.annotation.JsonProperty("note_count")
    public java.lang.Integer getNoteCount() {
        return noteCount;
    }

    /**
     *
     *
     * @param noteCount
     * 		The note_count
     */
    @com.fasterxml.jackson.annotation.JsonProperty("note_count")
    public void setNoteCount(java.lang.Integer noteCount) {
        this.noteCount = noteCount;
    }

    /**
     *
     *
     * @return The caption
     */
    @com.fasterxml.jackson.annotation.JsonProperty("caption")
    public java.lang.String getCaption() {
        return caption;
    }

    /**
     *
     *
     * @param caption
     * 		The caption
     */
    @com.fasterxml.jackson.annotation.JsonProperty("caption")
    public void setCaption(java.lang.String caption) {
        this.caption = caption;
    }

    /**
     *
     *
     * @return The reblog
     */
    @com.fasterxml.jackson.annotation.JsonProperty("reblog")
    public me.ccrama.redditslide.Tumblr.Reblog getReblog() {
        return reblog;
    }

    /**
     *
     *
     * @param reblog
     * 		The reblog
     */
    @com.fasterxml.jackson.annotation.JsonProperty("reblog")
    public void setReblog(me.ccrama.redditslide.Tumblr.Reblog reblog) {
        this.reblog = reblog;
    }

    /**
     *
     *
     * @return The trail
     */
    @com.fasterxml.jackson.annotation.JsonProperty("trail")
    public java.util.List<me.ccrama.redditslide.Tumblr.Trail> getTrail() {
        return trail;
    }

    /**
     *
     *
     * @param trail
     * 		The trail
     */
    @com.fasterxml.jackson.annotation.JsonProperty("trail")
    public void setTrail(java.util.List<me.ccrama.redditslide.Tumblr.Trail> trail) {
        this.trail = trail;
    }

    /**
     *
     *
     * @return The photosetLayout
     */
    @com.fasterxml.jackson.annotation.JsonProperty("photoset_layout")
    public java.lang.String getPhotosetLayout() {
        return photosetLayout;
    }

    /**
     *
     *
     * @param photosetLayout
     * 		The photoset_layout
     */
    @com.fasterxml.jackson.annotation.JsonProperty("photoset_layout")
    public void setPhotosetLayout(java.lang.String photosetLayout) {
        this.photosetLayout = photosetLayout;
    }

    /**
     *
     *
     * @return The photos
     */
    @com.fasterxml.jackson.annotation.JsonProperty("photos")
    public java.util.List<me.ccrama.redditslide.Tumblr.Photo> getPhotos() {
        return photos;
    }

    /**
     *
     *
     * @param photos
     * 		The photos
     */
    @com.fasterxml.jackson.annotation.JsonProperty("photos")
    public void setPhotos(java.util.List<me.ccrama.redditslide.Tumblr.Photo> photos) {
        this.photos = photos;
    }

    /**
     *
     *
     * @return The canSendInMessage
     */
    @com.fasterxml.jackson.annotation.JsonProperty("can_send_in_message")
    public java.lang.Boolean getCanSendInMessage() {
        return canSendInMessage;
    }

    /**
     *
     *
     * @param canSendInMessage
     * 		The can_send_in_message
     */
    @com.fasterxml.jackson.annotation.JsonProperty("can_send_in_message")
    public void setCanSendInMessage(java.lang.Boolean canSendInMessage) {
        this.canSendInMessage = canSendInMessage;
    }

    /**
     *
     *
     * @return The canLike
     */
    @com.fasterxml.jackson.annotation.JsonProperty("can_like")
    public java.lang.Boolean getCanLike() {
        return canLike;
    }

    /**
     *
     *
     * @param canLike
     * 		The can_like
     */
    @com.fasterxml.jackson.annotation.JsonProperty("can_like")
    public void setCanLike(java.lang.Boolean canLike) {
        this.canLike = canLike;
    }

    /**
     *
     *
     * @return The canReblog
     */
    @com.fasterxml.jackson.annotation.JsonProperty("can_reblog")
    public java.lang.Boolean getCanReblog() {
        return canReblog;
    }

    /**
     *
     *
     * @param canReblog
     * 		The can_reblog
     */
    @com.fasterxml.jackson.annotation.JsonProperty("can_reblog")
    public void setCanReblog(java.lang.Boolean canReblog) {
        this.canReblog = canReblog;
    }

    /**
     *
     *
     * @return The displayAvatar
     */
    @com.fasterxml.jackson.annotation.JsonProperty("display_avatar")
    public java.lang.Boolean getDisplayAvatar() {
        return displayAvatar;
    }

    /**
     *
     *
     * @param displayAvatar
     * 		The display_avatar
     */
    @com.fasterxml.jackson.annotation.JsonProperty("display_avatar")
    public void setDisplayAvatar(java.lang.Boolean displayAvatar) {
        this.displayAvatar = displayAvatar;
    }

    @com.fasterxml.jackson.annotation.JsonAnyGetter
    public java.util.Map<java.lang.String, java.lang.Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @com.fasterxml.jackson.annotation.JsonAnySetter
    public void setAdditionalProperty(java.lang.String name, java.lang.Object value) {
        this.additionalProperties.put(name, value);
    }
}