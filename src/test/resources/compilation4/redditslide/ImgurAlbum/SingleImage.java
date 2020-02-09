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
@com.fasterxml.jackson.annotation.JsonPropertyOrder({ "id", "title", "description", "datetime", "type", "animated", "width", "height", "size", "views", "bandwidth", "vote", "favorite", "nsfw", "section", "account_url", "account_id", "in_gallery", "link" })
public class SingleImage {
    @com.fasterxml.jackson.annotation.JsonProperty("id")
    private java.lang.String id;

    @com.fasterxml.jackson.annotation.JsonProperty("title")
    private java.lang.String title;

    @com.fasterxml.jackson.annotation.JsonProperty("description")
    private java.lang.String description;

    @com.fasterxml.jackson.annotation.JsonProperty("datetime")
    private java.lang.Double datetime;

    @com.fasterxml.jackson.annotation.JsonProperty("type")
    private java.lang.String type;

    @com.fasterxml.jackson.annotation.JsonProperty("animated")
    private java.lang.Boolean animated;

    @com.fasterxml.jackson.annotation.JsonProperty("width")
    private java.lang.Integer width;

    @com.fasterxml.jackson.annotation.JsonProperty("height")
    private java.lang.Integer height;

    @com.fasterxml.jackson.annotation.JsonProperty("size")
    private java.lang.Double size;

    @com.fasterxml.jackson.annotation.JsonProperty("views")
    private java.lang.Double views;

    @com.fasterxml.jackson.annotation.JsonProperty("bandwidth")
    private java.lang.Double bandwidth;

    @com.fasterxml.jackson.annotation.JsonProperty("vote")
    private java.lang.Object vote;

    @com.fasterxml.jackson.annotation.JsonProperty("favorite")
    private java.lang.Boolean favorite;

    @com.fasterxml.jackson.annotation.JsonProperty("nsfw")
    private java.lang.Boolean nsfw;

    @com.fasterxml.jackson.annotation.JsonProperty("section")
    private java.lang.String section;

    @com.fasterxml.jackson.annotation.JsonProperty("account_url")
    private java.lang.Object accountUrl;

    @com.fasterxml.jackson.annotation.JsonProperty("account_id")
    private java.lang.Object accountId;

    @com.fasterxml.jackson.annotation.JsonProperty("in_gallery")
    private java.lang.Boolean inGallery;

    @com.fasterxml.jackson.annotation.JsonProperty("link")
    private java.lang.String link;

    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.Map<java.lang.String, java.lang.Object> additionalProperties = new java.util.HashMap<>();

    /**
     *
     *
     * @return The id
     */
    @com.fasterxml.jackson.annotation.JsonProperty("id")
    public java.lang.String getId() {
        return id;
    }

    /**
     *
     *
     * @param id
     * 		The id
     */
    @com.fasterxml.jackson.annotation.JsonProperty("id")
    public void setId(java.lang.String id) {
        this.id = id;
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
     * @return The datetime
     */
    @com.fasterxml.jackson.annotation.JsonProperty("datetime")
    public java.lang.Double getDatetime() {
        return datetime;
    }

    /**
     *
     *
     * @param datetime
     * 		The datetime
     */
    @com.fasterxml.jackson.annotation.JsonProperty("datetime")
    public void setDatetime(java.lang.Double datetime) {
        this.datetime = datetime;
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
     * @return The animated
     */
    @com.fasterxml.jackson.annotation.JsonProperty("animated")
    public java.lang.Boolean getAnimated() {
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
    public java.lang.Double getSize() {
        return size;
    }

    /**
     *
     *
     * @param size
     * 		The size
     */
    @com.fasterxml.jackson.annotation.JsonProperty("size")
    public void setSize(java.lang.Double size) {
        this.size = size;
    }

    /**
     *
     *
     * @return The views
     */
    @com.fasterxml.jackson.annotation.JsonProperty("views")
    public java.lang.Double getViews() {
        return views;
    }

    /**
     *
     *
     * @param views
     * 		The views
     */
    @com.fasterxml.jackson.annotation.JsonProperty("views")
    public void setViews(java.lang.Double views) {
        this.views = views;
    }

    /**
     *
     *
     * @return The bandwidth
     */
    @com.fasterxml.jackson.annotation.JsonProperty("bandwidth")
    public java.lang.Double getBandwidth() {
        return bandwidth;
    }

    /**
     *
     *
     * @param bandwidth
     * 		The bandwidth
     */
    @com.fasterxml.jackson.annotation.JsonProperty("bandwidth")
    public void setBandwidth(java.lang.Double bandwidth) {
        this.bandwidth = bandwidth;
    }

    /**
     *
     *
     * @return The vote
     */
    @com.fasterxml.jackson.annotation.JsonProperty("vote")
    public java.lang.Object getVote() {
        return vote;
    }

    /**
     *
     *
     * @param vote
     * 		The vote
     */
    @com.fasterxml.jackson.annotation.JsonProperty("vote")
    public void setVote(java.lang.Object vote) {
        this.vote = vote;
    }

    /**
     *
     *
     * @return The favorite
     */
    @com.fasterxml.jackson.annotation.JsonProperty("favorite")
    public java.lang.Boolean getFavorite() {
        return favorite;
    }

    /**
     *
     *
     * @param favorite
     * 		The favorite
     */
    @com.fasterxml.jackson.annotation.JsonProperty("favorite")
    public void setFavorite(java.lang.Boolean favorite) {
        this.favorite = favorite;
    }

    /**
     *
     *
     * @return The nsfw
     */
    @com.fasterxml.jackson.annotation.JsonProperty("nsfw")
    public java.lang.Boolean getNsfw() {
        return nsfw;
    }

    /**
     *
     *
     * @param nsfw
     * 		The nsfw
     */
    @com.fasterxml.jackson.annotation.JsonProperty("nsfw")
    public void setNsfw(java.lang.Boolean nsfw) {
        this.nsfw = nsfw;
    }

    /**
     *
     *
     * @return The section
     */
    @com.fasterxml.jackson.annotation.JsonProperty("section")
    public java.lang.String getSection() {
        return section;
    }

    /**
     *
     *
     * @param section
     * 		The section
     */
    @com.fasterxml.jackson.annotation.JsonProperty("section")
    public void setSection(java.lang.String section) {
        this.section = section;
    }

    /**
     *
     *
     * @return The accountUrl
     */
    @com.fasterxml.jackson.annotation.JsonProperty("account_url")
    public java.lang.Object getAccountUrl() {
        return accountUrl;
    }

    /**
     *
     *
     * @param accountUrl
     * 		The account_url
     */
    @com.fasterxml.jackson.annotation.JsonProperty("account_url")
    public void setAccountUrl(java.lang.Object accountUrl) {
        this.accountUrl = accountUrl;
    }

    /**
     *
     *
     * @return The accountId
     */
    @com.fasterxml.jackson.annotation.JsonProperty("account_id")
    public java.lang.Object getAccountId() {
        return accountId;
    }

    /**
     *
     *
     * @param accountId
     * 		The account_id
     */
    @com.fasterxml.jackson.annotation.JsonProperty("account_id")
    public void setAccountId(java.lang.Object accountId) {
        this.accountId = accountId;
    }

    /**
     *
     *
     * @return The inGallery
     */
    @com.fasterxml.jackson.annotation.JsonProperty("in_gallery")
    public java.lang.Boolean getInGallery() {
        return inGallery;
    }

    /**
     *
     *
     * @param inGallery
     * 		The in_gallery
     */
    @com.fasterxml.jackson.annotation.JsonProperty("in_gallery")
    public void setInGallery(java.lang.Boolean inGallery) {
        this.inGallery = inGallery;
    }

    /**
     *
     *
     * @return The link
     */
    @com.fasterxml.jackson.annotation.JsonProperty("link")
    public java.lang.String getLink() {
        return link;
    }

    /**
     *
     *
     * @param link
     * 		The link
     */
    @com.fasterxml.jackson.annotation.JsonProperty("link")
    public void setLink(java.lang.String link) {
        this.link = link;
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