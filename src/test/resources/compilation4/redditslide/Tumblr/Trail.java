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
@com.fasterxml.jackson.annotation.JsonPropertyOrder({ "blog", "post", "content_raw", "content", "is_current_item", "is_root_item" })
public class Trail {
    @com.fasterxml.jackson.annotation.JsonProperty("blog")
    private me.ccrama.redditslide.Tumblr.Blog_ blog;

    @com.fasterxml.jackson.annotation.JsonProperty("post")
    private me.ccrama.redditslide.Tumblr.Post_ post;

    @com.fasterxml.jackson.annotation.JsonProperty("content_raw")
    private java.lang.String contentRaw;

    @com.fasterxml.jackson.annotation.JsonProperty("content")
    private java.lang.String content;

    @com.fasterxml.jackson.annotation.JsonProperty("is_current_item")
    private java.lang.Boolean isCurrentItem;

    @com.fasterxml.jackson.annotation.JsonProperty("is_root_item")
    private java.lang.Boolean isRootItem;

    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.Map<java.lang.String, java.lang.Object> additionalProperties = new java.util.HashMap<java.lang.String, java.lang.Object>();

    /**
     *
     *
     * @return The blog
     */
    @com.fasterxml.jackson.annotation.JsonProperty("blog")
    public me.ccrama.redditslide.Tumblr.Blog_ getBlog() {
        return blog;
    }

    /**
     *
     *
     * @param blog
     * 		The blog
     */
    @com.fasterxml.jackson.annotation.JsonProperty("blog")
    public void setBlog(me.ccrama.redditslide.Tumblr.Blog_ blog) {
        this.blog = blog;
    }

    /**
     *
     *
     * @return The post
     */
    @com.fasterxml.jackson.annotation.JsonProperty("post")
    public me.ccrama.redditslide.Tumblr.Post_ getPost() {
        return post;
    }

    /**
     *
     *
     * @param post
     * 		The post
     */
    @com.fasterxml.jackson.annotation.JsonProperty("post")
    public void setPost(me.ccrama.redditslide.Tumblr.Post_ post) {
        this.post = post;
    }

    /**
     *
     *
     * @return The contentRaw
     */
    @com.fasterxml.jackson.annotation.JsonProperty("content_raw")
    public java.lang.String getContentRaw() {
        return contentRaw;
    }

    /**
     *
     *
     * @param contentRaw
     * 		The content_raw
     */
    @com.fasterxml.jackson.annotation.JsonProperty("content_raw")
    public void setContentRaw(java.lang.String contentRaw) {
        this.contentRaw = contentRaw;
    }

    /**
     *
     *
     * @return The content
     */
    @com.fasterxml.jackson.annotation.JsonProperty("content")
    public java.lang.String getContent() {
        return content;
    }

    /**
     *
     *
     * @param content
     * 		The content
     */
    @com.fasterxml.jackson.annotation.JsonProperty("content")
    public void setContent(java.lang.String content) {
        this.content = content;
    }

    /**
     *
     *
     * @return The isCurrentItem
     */
    @com.fasterxml.jackson.annotation.JsonProperty("is_current_item")
    public java.lang.Boolean getIsCurrentItem() {
        return isCurrentItem;
    }

    /**
     *
     *
     * @param isCurrentItem
     * 		The is_current_item
     */
    @com.fasterxml.jackson.annotation.JsonProperty("is_current_item")
    public void setIsCurrentItem(java.lang.Boolean isCurrentItem) {
        this.isCurrentItem = isCurrentItem;
    }

    /**
     *
     *
     * @return The isRootItem
     */
    @com.fasterxml.jackson.annotation.JsonProperty("is_root_item")
    public java.lang.Boolean getIsRootItem() {
        return isRootItem;
    }

    /**
     *
     *
     * @param isRootItem
     * 		The is_root_item
     */
    @com.fasterxml.jackson.annotation.JsonProperty("is_root_item")
    public void setIsRootItem(java.lang.Boolean isRootItem) {
        this.isRootItem = isRootItem;
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