package me.ccrama.redditslide.Toolbox;
import java.lang.reflect.Type;
import com.google.gson.annotations.JsonAdapter;
import java.util.HashMap;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;
/**
 * Class defining a toolbox config. Contains removal reasons, mod macros, usernote colors, domain tags, etc.
 */
public class ToolboxConfig {
    @com.google.gson.annotations.SerializedName("ver")
    private int schema;

    @com.google.gson.annotations.JsonAdapter(me.ccrama.redditslide.Toolbox.ToolboxConfig.EmptyStringAsNullTypeAdapter.class)
    private java.util.List<java.util.Map<java.lang.String, java.lang.String>> domainTags;

    @com.google.gson.annotations.JsonAdapter(me.ccrama.redditslide.Toolbox.ToolboxConfig.EmptyStringAsNullTypeAdapter.class)
    private me.ccrama.redditslide.Toolbox.RemovalReasons removalReasons;

    @com.google.gson.annotations.JsonAdapter(me.ccrama.redditslide.Toolbox.ToolboxConfig.EmptyStringAsNullTypeAdapter.class)
    private java.util.List<java.util.Map<java.lang.String, java.lang.String>> macros;

    @com.google.gson.annotations.SerializedName("usernoteColors")
    @com.google.gson.annotations.JsonAdapter(me.ccrama.redditslide.Toolbox.ToolboxConfig.UsernoteTypeDeserializer.class)
    private java.util.Map<java.lang.String, java.util.Map<java.lang.String, java.lang.String>> usernoteTypes;

    @com.google.gson.annotations.JsonAdapter(me.ccrama.redditslide.Toolbox.ToolboxConfig.EmptyStringAsNullTypeAdapter.class)
    private java.util.Map<java.lang.String, java.lang.String> banMacros;

    public ToolboxConfig() {
    }

    public int getSchema() {
        return schema;
    }

    public java.util.List<java.util.Map<java.lang.String, java.lang.String>> getDomainTags() {
        return domainTags;
    }

    public me.ccrama.redditslide.Toolbox.RemovalReasons getRemovalReasons() {
        return removalReasons;
    }

    public java.util.Map<java.lang.String, java.util.Map<java.lang.String, java.lang.String>> getUsernoteTypes() {
        return usernoteTypes;
    }

    public java.lang.String getUsernoteColor(java.lang.String type) {
        if (((usernoteTypes != null) && (usernoteTypes.get(type) != null)) && (usernoteTypes.get(type).get("color") != null)) {
            return usernoteTypes.get(type).get("color");
        } else if (me.ccrama.redditslide.Toolbox.Toolbox.DEFAULT_USERNOTE_TYPES.get(type) != null) {
            return me.ccrama.redditslide.Toolbox.Toolbox.DEFAULT_USERNOTE_TYPES.get(type).get("color");
        } else {
            return "#808080";// gray for non-typed or unknown type notes, same as Toolbox

        }
    }

    public java.lang.String getUsernoteText(java.lang.String type) {
        if (((usernoteTypes != null) && (usernoteTypes.get(type) != null)) && (usernoteTypes.get(type).get("text") != null)) {
            return usernoteTypes.get(type).get("text");
        } else if (me.ccrama.redditslide.Toolbox.Toolbox.DEFAULT_USERNOTE_TYPES.get(type) != null) {
            return me.ccrama.redditslide.Toolbox.Toolbox.DEFAULT_USERNOTE_TYPES.get(type).get("text");
        } else {
            return "";
        }
    }

    public static class UsernoteTypeDeserializer implements com.google.gson.JsonDeserializer<java.util.Map<java.lang.String, java.util.Map<java.lang.String, java.lang.String>>> {
        @java.lang.Override
        public java.util.Map<java.lang.String, java.util.Map<java.lang.String, java.lang.String>> deserialize(com.google.gson.JsonElement json, java.lang.reflect.Type typeOfT, com.google.gson.JsonDeserializationContext context) throws com.google.gson.JsonParseException {
            if (json.isJsonPrimitive()) {
                // isn't an array
                return null;
            }
            java.util.Map<java.lang.String, java.util.Map<java.lang.String, java.lang.String>> result = new java.util.HashMap<>();
            for (com.google.gson.JsonElement noteType : json.getAsJsonArray()) {
                java.util.Map<java.lang.String, java.lang.String> details = new java.util.HashMap<>();
                details.put("color", noteType.getAsJsonObject().get("color").getAsString());
                details.put("text", noteType.getAsJsonObject().get("text").getAsString());
                result.put(noteType.getAsJsonObject().get("key").getAsString(), details);
            }
            return result;
        }
    }

    // from https://stackoverflow.com/a/48806970, because toolbox uses empty strings to mean null in some instances
    public final class EmptyStringAsNullTypeAdapter<T> implements com.google.gson.JsonDeserializer<T> {
        @java.lang.Override
        public T deserialize(final com.google.gson.JsonElement jsonElement, final java.lang.reflect.Type type, final com.google.gson.JsonDeserializationContext context) throws com.google.gson.JsonParseException {
            if (jsonElement.isJsonPrimitive()) {
                final com.google.gson.JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
                if (jsonPrimitive.isString() && jsonPrimitive.getAsString().isEmpty()) {
                    return null;
                }
            }
            return context.deserialize(jsonElement, type);
        }
    }
}