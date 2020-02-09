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
@com.fasterxml.jackson.annotation.JsonPropertyOrder({ "title", "name", "total_posts", "posts", "url", "updated", "description", "is_nsfw", "ask", "ask_page_title", "ask_anon", "share_likes", "likes" })
public class Blog {
    @com.fasterxml.jackson.annotation.JsonProperty("title")
    private java.lang.String title;

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    private java.lang.String name;

    @com.fasterxml.jackson.annotation.JsonProperty("total_posts")
    private java.lang.Integer totalPosts;

    @com.fasterxml.jackson.annotation.JsonProperty("posts")
    private java.lang.Integer posts;

    @com.fasterxml.jackson.annotation.JsonProperty("url")
    private java.lang.String url;

    @com.fasterxml.jackson.annotation.JsonProperty("updated")
    private java.lang.Double updated;

    @com.fasterxml.jackson.annotation.JsonProperty("description")
    private java.lang.String description;

    @com.fasterxml.jackson.annotation.JsonProperty("is_nsfw")
    private java.lang.Boolean isNsfw;

    @com.fasterxml.jackson.annotation.JsonProperty("ask")
    private java.lang.Boolean ask;

    @com.fasterxml.jackson.annotation.JsonProperty("ask_page_title")
    private java.lang.String askPageTitle;

    @com.fasterxml.jackson.annotation.JsonProperty("ask_anon")
    private java.lang.Boolean askAnon;

    @com.fasterxml.jackson.annotation.JsonProperty("share_likes")
    private java.lang.Boolean shareLikes;

    @com.fasterxml.jackson.annotation.JsonProperty("likes")
    private java.lang.Integer likes;

    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.Map<java.lang.String, java.lang.Object> additionalProperties = new java.util.HashMap<java.lang.String, java.lang.Object>();

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
     * @return The totalPosts
     */
    @com.fasterxml.jackson.annotation.JsonProperty("total_posts")
    public java.lang.Integer getTotalPosts() {
        return totalPosts;
    }

    /**
     *
     *
     * @param totalPosts
     * 		The total_posts
     */
    @com.fasterxml.jackson.annotation.JsonProperty("total_posts")
    public void setTotalPosts(java.lang.Integer totalPosts) {
        this.totalPosts = totalPosts;
    }

    /**
     *
     *
     * @return The posts
     */
    @com.fasterxml.jackson.annotation.JsonProperty("posts")
    public java.lang.Integer getPosts() {
        return posts;
    }

    /**
     *
     *
     * @param posts
     * 		The posts
     */
    @com.fasterxml.jackson.annotation.JsonProperty("posts")
    public void setPosts(java.lang.Integer posts) {
        this.posts = posts;
    }

    /**
     *
     *
     * @return The url
     */
    @com.fasterxml.jackson.annotation.JsonProperty("url")
    public java.lang.String getUrl() {
        return url;
    }

    /**
     *
     *
     * @param url
     * 		The url
     */
    @com.fasterxml.jackson.annotation.JsonProperty("url")
    public void setUrl(java.lang.String url) {
        this.url = url;
    }

    /**
     *
     *
     * @return The updated
     */
    @com.fasterxml.jackson.annotation.JsonProperty("updated")
    public java.lang.Double getUpdated() {
        return updated;
    }

    /**
     *
     *
     * @param updated
     * 		The updated
     */
    @com.fasterxml.jackson.annotation.JsonProperty("updated")
    public void setUpdated(java.lang.Double updated) {
        this.updated = updated;
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
     * @return The isNsfw
     */
    @com.fasterxml.jackson.annotation.JsonProperty("is_nsfw")
    public java.lang.Boolean getIsNsfw() {
        return isNsfw;
    }

    /**
     *
     *
     * @param isNsfw
     * 		The is_nsfw
     */
    @com.fasterxml.jackson.annotation.JsonProperty("is_nsfw")
    public void setIsNsfw(java.lang.Boolean isNsfw) {
        this.isNsfw = isNsfw;
    }

    /**
     *
     *
     * @return The ask
     */
    @com.fasterxml.jackson.annotation.JsonProperty("ask")
    public java.lang.Boolean getAsk() {
        return ask;
    }

    /**
     *
     *
     * @param ask
     * 		The ask
     */
    @com.fasterxml.jackson.annotation.JsonProperty("ask")
    public void setAsk(java.lang.Boolean ask) {
        this.ask = ask;
    }

    /**
     *
     *
     * @return The askPageTitle
     */
    @com.fasterxml.jackson.annotation.JsonProperty("ask_page_title")
    public java.lang.String getAskPageTitle() {
        return askPageTitle;
    }

    /**
     *
     *
     * @param askPageTitle
     * 		The ask_page_title
     */
    @com.fasterxml.jackson.annotation.JsonProperty("ask_page_title")
    public void setAskPageTitle(java.lang.String askPageTitle) {
        this.askPageTitle = askPageTitle;
    }

    /**
     *
     *
     * @return The askAnon
     */
    @com.fasterxml.jackson.annotation.JsonProperty("ask_anon")
    public java.lang.Boolean getAskAnon() {
        return askAnon;
    }

    /**
     *
     *
     * @param askAnon
     * 		The ask_anon
     */
    @com.fasterxml.jackson.annotation.JsonProperty("ask_anon")
    public void setAskAnon(java.lang.Boolean askAnon) {
        this.askAnon = askAnon;
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
     * @return The likes
     */
    @com.fasterxml.jackson.annotation.JsonProperty("likes")
    public java.lang.Integer getLikes() {
        return likes;
    }

    /**
     *
     *
     * @param likes
     * 		The likes
     */
    @com.fasterxml.jackson.annotation.JsonProperty("likes")
    public void setLikes(java.lang.Integer likes) {
        this.likes = likes;
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