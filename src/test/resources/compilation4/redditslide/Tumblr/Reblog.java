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
@com.fasterxml.jackson.annotation.JsonPropertyOrder({ "tree_html", "comment" })
public class Reblog {
    @com.fasterxml.jackson.annotation.JsonProperty("tree_html")
    private java.lang.String treeHtml;

    @com.fasterxml.jackson.annotation.JsonProperty("comment")
    private java.lang.String comment;

    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.Map<java.lang.String, java.lang.Object> additionalProperties = new java.util.HashMap<java.lang.String, java.lang.Object>();

    /**
     *
     *
     * @return The treeHtml
     */
    @com.fasterxml.jackson.annotation.JsonProperty("tree_html")
    public java.lang.String getTreeHtml() {
        return treeHtml;
    }

    /**
     *
     *
     * @param treeHtml
     * 		The tree_html
     */
    @com.fasterxml.jackson.annotation.JsonProperty("tree_html")
    public void setTreeHtml(java.lang.String treeHtml) {
        this.treeHtml = treeHtml;
    }

    /**
     *
     *
     * @return The comment
     */
    @com.fasterxml.jackson.annotation.JsonProperty("comment")
    public java.lang.String getComment() {
        return comment;
    }

    /**
     *
     *
     * @param comment
     * 		The comment
     */
    @com.fasterxml.jackson.annotation.JsonProperty("comment")
    public void setComment(java.lang.String comment) {
        this.comment = comment;
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