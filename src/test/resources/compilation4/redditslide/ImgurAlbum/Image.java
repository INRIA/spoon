package me.ccrama.redditslide.ImgurAlbum;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({ "hash", "title", "description", "width", "height", "size", "ext", "animated", "prefer_video", "looping", "datetime" })
public class Image {
    @com.fasterxml.jackson.annotation.JsonProperty("hash")
    private java.lang.String hash;

    @com.fasterxml.jackson.annotation.JsonProperty("title")
    private java.lang.String title;

    @com.fasterxml.jackson.annotation.JsonProperty("description")
    private java.lang.String description;

    @com.fasterxml.jackson.annotation.JsonProperty("width")
    private java.lang.Integer width;

    @com.fasterxml.jackson.annotation.JsonProperty("height")
    private java.lang.Integer height;

    @com.fasterxml.jackson.annotation.JsonProperty("size")
    private java.lang.Integer size;

    @com.fasterxml.jackson.annotation.JsonProperty("ext")
    private java.lang.String ext;

    @com.fasterxml.jackson.annotation.JsonProperty("animated")
    private java.lang.Boolean animated;

    @com.fasterxml.jackson.annotation.JsonProperty("prefer_video")
    private java.lang.Boolean preferVideo;

    @com.fasterxml.jackson.annotation.JsonProperty("looping")
    private java.lang.Boolean looping;

    @com.fasterxml.jackson.annotation.JsonProperty("datetime")
    private java.lang.String datetime;

    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.Map<java.lang.String, java.lang.Object> additionalProperties = new java.util.HashMap<>();

    /**
     *
     *
     * @return The hash
     */
    @com.fasterxml.jackson.annotation.JsonProperty("hash")
    public java.lang.String getHash() {
        return hash;
    }

    /**
     *
     *
     * @param hash
     * 		The hash
     */
    @com.fasterxml.jackson.annotation.JsonProperty("hash")
    public void setHash(java.lang.String hash) {
        this.hash = hash;
    }

    /**
     *
     *
     * @return The title
     */
    @com.fasterxml.jackson.annotation.JsonProperty("title")
    public java.lang.String getTitle() {
        return title;
    }

    /**
     *
     *
     * @param title
     * 		The title
     */
    @com.fasterxml.jackson.annotation.JsonProperty("title")
    public void setTitle(java.lang.String title) {
        this.title = title;
    }

    /**
     *
     *
     * @return The description
     */
    @com.fasterxml.jackson.annotation.JsonProperty("description")
    public java.lang.String getDescription() {
        return description;
    }

    /**
     *
     *
     * @param description
     * 		The description
     */
    @com.fasterxml.jackson.annotation.JsonProperty("description")
    public void setDescription(java.lang.String description) {
        this.description = description;
    }

    /**
     *
     *
     * @return The width
     */
    @com.fasterxml.jackson.annotation.JsonProperty("width")
    public java.lang.Integer getWidth() {
        return width;
    }

    /**
     *
     *
     * @param width
     * 		The width
     */
    @com.fasterxml.jackson.annotation.JsonProperty("width")
    public void setWidth(java.lang.Integer width) {
        this.width = width;
    }

    /**
     *
     *
     * @return The height
     */
    @com.fasterxml.jackson.annotation.JsonProperty("height")
    public java.lang.Integer getHeight() {
        return height;
    }

    /**
     *
     *
     * @param height
     * 		The height
     */
    @com.fasterxml.jackson.annotation.JsonProperty("height")
    public void setHeight(java.lang.Integer height) {
        this.height = height;
    }

    /**
     *
     *
     * @return The size
     */
    @com.fasterxml.jackson.annotation.JsonProperty("size")
    public java.lang.Integer getSize() {
        return size;
    }

    /**
     *
     *
     * @param size
     * 		The size
     */
    @com.fasterxml.jackson.annotation.JsonProperty("size")
    public void setSize(java.lang.Integer size) {
        this.size = size;
    }

    /**
     *
     *
     * @return The ext
     */
    @com.fasterxml.jackson.annotation.JsonProperty("ext")
    public java.lang.String getExt() {
        return ext;
    }

    /**
     *
     *
     * @param ext
     * 		The ext
     */
    @com.fasterxml.jackson.annotation.JsonProperty("ext")
    public void setExt(java.lang.String ext) {
        this.ext = ext;
    }

    /**
     *
     *
     * @return The animated
     */
    @com.fasterxml.jackson.annotation.JsonProperty("animated")
    public java.lang.Boolean isAnimated() {
        return animated;
    }

    /**
     *
     *
     * @param animated
     * 		The animated
     */
    @com.fasterxml.jackson.annotation.JsonProperty("animated")
    public void setAnimated(java.lang.Boolean animated) {
        this.animated = animated;
    }

    /**
     *
     *
     * @return The preferVideo
     */
    @com.fasterxml.jackson.annotation.JsonProperty("prefer_video")
    public java.lang.Boolean getPreferVideo() {
        return preferVideo;
    }

    /**
     *
     *
     * @param preferVideo
     * 		The prefer_video
     */
    @com.fasterxml.jackson.annotation.JsonProperty("prefer_video")
    public void setPreferVideo(java.lang.Boolean preferVideo) {
        this.preferVideo = preferVideo;
    }

    /**
     *
     *
     * @return The looping
     */
    @com.fasterxml.jackson.annotation.JsonProperty("looping")
    public java.lang.Boolean getLooping() {
        return looping;
    }

    /**
     *
     *
     * @param looping
     * 		The looping
     */
    @com.fasterxml.jackson.annotation.JsonProperty("looping")
    public void setLooping(java.lang.Boolean looping) {
        this.looping = looping;
    }

    /**
     *
     *
     * @return The datetime
     */
    @com.fasterxml.jackson.annotation.JsonProperty("datetime")
    public java.lang.String getDatetime() {
        return datetime;
    }

    /**
     *
     *
     * @param datetime
     * 		The datetime
     */
    @com.fasterxml.jackson.annotation.JsonProperty("datetime")
    public void setDatetime(java.lang.String datetime) {
        this.datetime = datetime;
    }

    @com.fasterxml.jackson.annotation.JsonAnyGetter
    public java.util.Map<java.lang.String, java.lang.Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @com.fasterxml.jackson.annotation.JsonAnySetter
    public void setAdditionalProperty(java.lang.String name, java.lang.Object value) {
        this.additionalProperties.put(name, value);
    }

    public java.lang.String getImageUrl() {
        return ("https://i.imgur.com/" + getHash()) + getExt();
    }

    public java.lang.String getThumbnailUrl() {
        return (("https://i.imgur.com/" + getHash()) + "s") + getExt();
    }
}