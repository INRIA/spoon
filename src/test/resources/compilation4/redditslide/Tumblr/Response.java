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
@com.fasterxml.jackson.annotation.JsonPropertyOrder({ "blog", "posts", "total_posts" })
public class Response {
    @com.fasterxml.jackson.annotation.JsonProperty("blog")
    private me.ccrama.redditslide.Tumblr.Blog blog;

    @com.fasterxml.jackson.annotation.JsonProperty("posts")
    private java.util.List<me.ccrama.redditslide.Tumblr.Post> posts = new java.util.ArrayList<me.ccrama.redditslide.Tumblr.Post>();

    @com.fasterxml.jackson.annotation.JsonProperty("total_posts")
    private java.lang.Integer totalPosts;

    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.Map<java.lang.String, java.lang.Object> additionalProperties = new java.util.HashMap<java.lang.String, java.lang.Object>();

    /**
     *
     *
     * @return The blog
     */
    @com.fasterxml.jackson.annotation.JsonProperty("blog")
    public me.ccrama.redditslide.Tumblr.Blog getBlog() {
        return blog;
    }

    /**
     *
     *
     * @param blog
     * 		The blog
     */
    @com.fasterxml.jackson.annotation.JsonProperty("blog")
    public void setBlog(me.ccrama.redditslide.Tumblr.Blog blog) {
        this.blog = blog;
    }

    /**
     *
     *
     * @return The posts
     */
    @com.fasterxml.jackson.annotation.JsonProperty("posts")
    public java.util.List<me.ccrama.redditslide.Tumblr.Post> getPosts() {
        return posts;
    }

    /**
     *
     *
     * @param posts
     * 		The posts
     */
    @com.fasterxml.jackson.annotation.JsonProperty("posts")
    public void setPosts(java.util.List<me.ccrama.redditslide.Tumblr.Post> posts) {
        this.posts = posts;
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

    @com.fasterxml.jackson.annotation.JsonAnyGetter
    public java.util.Map<java.lang.String, java.lang.Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @com.fasterxml.jackson.annotation.JsonAnySetter
    public void setAdditionalProperty(java.lang.String name, java.lang.Object value) {
        this.additionalProperties.put(name, value);
    }
}