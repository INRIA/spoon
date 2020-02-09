package me.ccrama.redditslide.Tumblr;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({ "name", "active", "theme", "share_likes", "share_following" })
public class Blog_ {
    @com.fasterxml.jackson.annotation.JsonProperty("name")
    private java.lang.String name;

    @com.fasterxml.jackson.annotation.JsonProperty("active")
    private java.lang.Boolean active;

    @com.fasterxml.jackson.annotation.JsonProperty("theme")
    private me.ccrama.redditslide.Tumblr.Theme theme;

    @com.fasterxml.jackson.annotation.JsonProperty("share_likes")
    private java.lang.Boolean shareLikes;

    @com.fasterxml.jackson.annotation.JsonProperty("share_following")
    private java.lang.Boolean shareFollowing;

    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.Map<java.lang.String, java.lang.Object> additionalProperties = new java.util.HashMap<java.lang.String, java.lang.Object>();

    /**
     *
     *
     * @return The name
     */
    @com.fasterxml.jackson.annotation.JsonProperty("name")
    public java.lang.String getName() {
        return name;
    }

    /**
     *
     *
     * @param name
     * 		The name
     */
    @com.fasterxml.jackson.annotation.JsonProperty("name")
    public void setName(java.lang.String name) {
        this.name = name;
    }

    /**
     *
     *
     * @return The active
     */
    @com.fasterxml.jackson.annotation.JsonProperty("active")
    public java.lang.Boolean getActive() {
        return active;
    }

    /**
     *
     *
     * @param active
     * 		The active
     */
    @com.fasterxml.jackson.annotation.JsonProperty("active")
    public void setActive(java.lang.Boolean active) {
        this.active = active;
    }

    /**
     *
     *
     * @return The theme
     */
    @com.fasterxml.jackson.annotation.JsonProperty("theme")
    public me.ccrama.redditslide.Tumblr.Theme getTheme() {
        return theme;
    }

    /**
     *
     *
     * @param theme
     * 		The theme
     */
    @com.fasterxml.jackson.annotation.JsonProperty("theme")
    public void setTheme(me.ccrama.redditslide.Tumblr.Theme theme) {
        this.theme = theme;
    }

    /**
     *
     *
     * @return The shareLikes
     */
    @com.fasterxml.jackson.annotation.JsonProperty("share_likes")
    public java.lang.Boolean getShareLikes() {
        return shareLikes;
    }

    /**
     *
     *
     * @param shareLikes
     * 		The share_likes
     */
    @com.fasterxml.jackson.annotation.JsonProperty("share_likes")
    public void setShareLikes(java.lang.Boolean shareLikes) {
        this.shareLikes = shareLikes;
    }

    /**
     *
     *
     * @return The shareFollowing
     */
    @com.fasterxml.jackson.annotation.JsonProperty("share_following")
    public java.lang.Boolean getShareFollowing() {
        return shareFollowing;
    }

    /**
     *
     *
     * @param shareFollowing
     * 		The share_following
     */
    @com.fasterxml.jackson.annotation.JsonProperty("share_following")
    public void setShareFollowing(java.lang.Boolean shareFollowing) {
        this.shareFollowing = shareFollowing;
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