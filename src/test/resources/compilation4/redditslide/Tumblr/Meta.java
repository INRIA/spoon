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
@com.fasterxml.jackson.annotation.JsonPropertyOrder({ "status", "msg" })
public class Meta {
    @com.fasterxml.jackson.annotation.JsonProperty("status")
    private java.lang.Integer status;

    @com.fasterxml.jackson.annotation.JsonProperty("msg")
    private java.lang.String msg;

    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.Map<java.lang.String, java.lang.Object> additionalProperties = new java.util.HashMap<java.lang.String, java.lang.Object>();

    /**
     *
     *
     * @return The status
     */
    @com.fasterxml.jackson.annotation.JsonProperty("status")
    public java.lang.Integer getStatus() {
        return status;
    }

    /**
     *
     *
     * @param status
     * 		The status
     */
    @com.fasterxml.jackson.annotation.JsonProperty("status")
    public void setStatus(java.lang.Integer status) {
        this.status = status;
    }

    /**
     *
     *
     * @return The msg
     */
    @com.fasterxml.jackson.annotation.JsonProperty("msg")
    public java.lang.String getMsg() {
        return msg;
    }

    /**
     *
     *
     * @param msg
     * 		The msg
     */
    @com.fasterxml.jackson.annotation.JsonProperty("msg")
    public void setMsg(java.lang.String msg) {
        this.msg = msg;
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