package me.ccrama.redditslide.util;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({ "url", "author_name", "author_url", "html", "width", "height", "type", "cache_age", "provider_name", "provider_url", "version" })
public class TwitterObject {
    @com.fasterxml.jackson.annotation.JsonProperty("url")
    private java.lang.String url;

    @com.fasterxml.jackson.annotation.JsonProperty("author_name")
    private java.lang.String authorName;

    @com.fasterxml.jackson.annotation.JsonProperty("author_url")
    private java.lang.String authorUrl;

    @com.fasterxml.jackson.annotation.JsonProperty("html")
    private java.lang.String html;

    @com.fasterxml.jackson.annotation.JsonProperty("width")
    private java.lang.Integer width;

    @com.fasterxml.jackson.annotation.JsonProperty("height")
    private java.lang.Integer height;

    @com.fasterxml.jackson.annotation.JsonProperty("type")
    private java.lang.String type;

    @com.fasterxml.jackson.annotation.JsonProperty("cache_age")
    private java.lang.String cacheAge;

    @com.fasterxml.jackson.annotation.JsonProperty("provider_name")
    private java.lang.String providerName;

    @com.fasterxml.jackson.annotation.JsonProperty("provider_url")
    private java.lang.String providerUrl;

    @com.fasterxml.jackson.annotation.JsonProperty("version")
    private java.lang.String version;

    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.Map<java.lang.String, java.lang.Object> additionalProperties = new java.util.HashMap<java.lang.String, java.lang.Object>();

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
     * @return The authorName
     */
    @com.fasterxml.jackson.annotation.JsonProperty("author_name")
    public java.lang.String getAuthorName() {
        return authorName;
    }

    /**
     *
     *
     * @param authorName
     * 		The author_name
     */
    @com.fasterxml.jackson.annotation.JsonProperty("author_name")
    public void setAuthorName(java.lang.String authorName) {
        this.authorName = authorName;
    }

    /**
     *
     *
     * @return The authorUrl
     */
    @com.fasterxml.jackson.annotation.JsonProperty("author_url")
    public java.lang.String getAuthorUrl() {
        return authorUrl;
    }

    /**
     *
     *
     * @param authorUrl
     * 		The author_url
     */
    @com.fasterxml.jackson.annotation.JsonProperty("author_url")
    public void setAuthorUrl(java.lang.String authorUrl) {
        this.authorUrl = authorUrl;
    }

    /**
     *
     *
     * @return The html
     */
    @com.fasterxml.jackson.annotation.JsonProperty("html")
    public java.lang.String getHtml() {
        return html;
    }

    /**
     *
     *
     * @param html
     * 		The html
     */
    @com.fasterxml.jackson.annotation.JsonProperty("html")
    public void setHtml(java.lang.String html) {
        this.html = html;
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
     * @return The cacheAge
     */
    @com.fasterxml.jackson.annotation.JsonProperty("cache_age")
    public java.lang.String getCacheAge() {
        return cacheAge;
    }

    /**
     *
     *
     * @param cacheAge
     * 		The cache_age
     */
    @com.fasterxml.jackson.annotation.JsonProperty("cache_age")
    public void setCacheAge(java.lang.String cacheAge) {
        this.cacheAge = cacheAge;
    }

    /**
     *
     *
     * @return The providerName
     */
    @com.fasterxml.jackson.annotation.JsonProperty("provider_name")
    public java.lang.String getProviderName() {
        return providerName;
    }

    /**
     *
     *
     * @param providerName
     * 		The provider_name
     */
    @com.fasterxml.jackson.annotation.JsonProperty("provider_name")
    public void setProviderName(java.lang.String providerName) {
        this.providerName = providerName;
    }

    /**
     *
     *
     * @return The providerUrl
     */
    @com.fasterxml.jackson.annotation.JsonProperty("provider_url")
    public java.lang.String getProviderUrl() {
        return providerUrl;
    }

    /**
     *
     *
     * @param providerUrl
     * 		The provider_url
     */
    @com.fasterxml.jackson.annotation.JsonProperty("provider_url")
    public void setProviderUrl(java.lang.String providerUrl) {
        this.providerUrl = providerUrl;
    }

    /**
     *
     *
     * @return The version
     */
    @com.fasterxml.jackson.annotation.JsonProperty("version")
    public java.lang.String getVersion() {
        return version;
    }

    /**
     *
     *
     * @param version
     * 		The version
     */
    @com.fasterxml.jackson.annotation.JsonProperty("version")
    public void setVersion(java.lang.String version) {
        this.version = version;
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