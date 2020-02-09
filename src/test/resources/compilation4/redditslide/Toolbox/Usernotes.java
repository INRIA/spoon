package me.ccrama.redditslide.Toolbox;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.IOException;
import com.google.gson.JsonSerializer;
import java.io.ByteArrayOutputStream;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import java.util.TreeMap;
import com.google.gson.JsonParser;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import java.io.ByteArrayInputStream;
import com.google.gson.JsonElement;
import org.apache.commons.lang3.StringUtils;
import com.google.gson.JsonSerializationContext;
import com.google.gson.annotations.SerializedName;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
/**
 * A group of usernotes for a subreddit
 */
public class Usernotes {
    @com.google.gson.annotations.SerializedName("ver")
    private int schema;

    private me.ccrama.redditslide.Toolbox.Usernotes.UsernotesConstants constants;

    @com.google.gson.annotations.SerializedName("blob")
    private java.util.Map<java.lang.String, java.util.List<me.ccrama.redditslide.Toolbox.Usernote>> notes;

    private transient java.lang.String subreddit;

    public Usernotes() {
        // for GSON
    }

    public Usernotes(int schema, me.ccrama.redditslide.Toolbox.Usernotes.UsernotesConstants constants, java.util.Map<java.lang.String, java.util.List<me.ccrama.redditslide.Toolbox.Usernote>> notes, java.lang.String subreddit) {
        this.schema = schema;
        this.constants = constants;
        this.notes = notes;
        this.subreddit = subreddit;
    }

    /**
     * Add a usernote to this usernotes object
     *
     * Make sure to persist back to the wiki after doing this!
     *
     * @param user
     * 		User to add note for
     * @param noteText
     * 		Note text
     * @param link
     * 		Toolbox link formatted link
     * @param time
     * 		Time in ms
     * @param mod
     * 		Mod making the note
     * @param type
     * 		optional warning type
     */
    public void createNote(java.lang.String user, java.lang.String noteText, java.lang.String link, long time, java.lang.String mod, java.lang.String type) {
        boolean modExists = false;
        int modIndex = -1;
        boolean typeExists = false;
        int typeIndex = -1;
        for (int i = 0; i < constants.getMods().length; i++) {
            if (constants.getMods()[i].equals(mod)) {
                modExists = true;
                modIndex = i;
                break;
            }
        }
        for (int i = 0; i < constants.getTypes().length; i++) {
            if (((constants.getTypes()[i] == null) && (type == null)) || ((constants.getTypes()[i] != null) && constants.getTypes()[i].equals(type))) {
                typeExists = true;
                typeIndex = i;
                break;
            }
        }
        if (!modExists) {
            modIndex = constants.addMod(mod);
        }
        if (!typeExists) {
            typeIndex = constants.addType(type);
        }
        me.ccrama.redditslide.Toolbox.Usernote note = new me.ccrama.redditslide.Toolbox.Usernote(noteText, link, time / 1000, modIndex, typeIndex);
        if (notes.containsKey(user)) {
            notes.get(user).add(0, note);
        } else {
            java.util.List<me.ccrama.redditslide.Toolbox.Usernote> newList = new java.util.ArrayList<>();
            newList.add(note);
            notes.put(user, newList);
        }
    }

    /**
     * Remove a usernote for a user
     *
     * Make sure to persist back to the wiki after doing this!
     *
     * @param user
     * 		User to remove note from
     * @param note
     * 		Note to remove
     */
    public void removeNote(java.lang.String user, me.ccrama.redditslide.Toolbox.Usernote note) {
        if (notes.get(user) != null) {
            notes.get(user).remove(note);
            if (notes.get(user).size() == 0) {
                // if we just removed the last note, remove the user too
                notes.remove(user);
            }
        }
    }

    public int getSchema() {
        return schema;
    }

    public me.ccrama.redditslide.Toolbox.Usernotes.UsernotesConstants getConstants() {
        return constants;
    }

    public java.util.Map<java.lang.String, java.util.List<me.ccrama.redditslide.Toolbox.Usernote>> getNotes() {
        return notes;
    }

    /**
     * Get the list of usernotes for a user
     *
     * @param user
     * 		User to get notes for
     * @return List of usernotes
     */
    public java.util.List<me.ccrama.redditslide.Toolbox.Usernote> getNotesForUser(java.lang.String user) {
        return notes.get(user);
    }

    /**
     * Gets the display text for a user using same logic as toolbox
     *
     * @param user
     * 		User
     * @return (Shortened) usernote text (plus count if additional notes)
     */
    public java.lang.String getDisplayNoteForUser(java.lang.String user) {
        int count = getNotesForUser(user).size();
        if (count == 0) {
            return "";
        }
        java.lang.String noteText = org.apache.commons.lang3.StringUtils.abbreviate(getNotesForUser(user).get(0).getNoteText(), "…", 20);
        if (count > 1) {
            noteText += (" (+" + (count - 1)) + ")";
        }
        return noteText;
    }

    /**
     * Get the color for the primary displayed usernote of a user
     *
     * @param user
     * 		User
     * @return A color int
     */
    @android.support.annotation.ColorInt
    public int getDisplayColorForUser(java.lang.String user) {
        if (getNotesForUser(user).size() > 0) {
            return getColorFromWarningIndex(getNotesForUser(user).get(0).getWarning());
        } else {
            return 0xff808080;
        }
    }

    /**
     * Get a color from a warning index
     *
     * @param index
     * 		Index
     * @return A color int
     */
    @android.support.annotation.ColorInt
    public int getColorFromWarningIndex(int index) {
        java.lang.String color = "#808080";
        me.ccrama.redditslide.Toolbox.ToolboxConfig config = me.ccrama.redditslide.Toolbox.Toolbox.getConfig(subreddit);
        if (config != null) {
            // Subs can have usernotes without a toolbox config
            color = config.getUsernoteColor(constants.getTypeName(index));
        } else {
            java.util.Map<java.lang.String, java.lang.String> defaults = me.ccrama.redditslide.Toolbox.Toolbox.DEFAULT_USERNOTE_TYPES.get(constants.getTypeName(index));
            if (defaults != null) {
                java.lang.String defaultColor = defaults.get("color");
                if (defaultColor != null) {
                    color = defaultColor;
                }
            }
        }
        try {
            return com.google.android.exoplayer2.util.ColorParser.parseCssColor(color);
        } catch (java.lang.IllegalArgumentException e) {
            return 0xff808080;
        }
    }

    /**
     * Get the warning text for a usernote from the index in the warnings array
     *
     * @param index
     * 		Index in warnings array
     * @param bracket
     * 		Whether to wrap the returned result in brackets
     * @return Warning text
     */
    public java.lang.String getWarningTextFromWarningIndex(int index, boolean bracket) {
        java.lang.StringBuilder result = new java.lang.StringBuilder(bracket ? "[" : "");
        if (me.ccrama.redditslide.Toolbox.Toolbox.getConfig(subreddit) != null) {
            if (constants.getTypeName(index) != null) {
                java.lang.String text = me.ccrama.redditslide.Toolbox.Toolbox.getConfig(subreddit).getUsernoteText(constants.getTypeName(index));
                if (!text.isEmpty()) {
                    result.append(text);
                } else {
                    return "";
                }
            } else {
                return "";
            }
        } else if (constants.getTypeName(index) != null) {
            java.lang.String def = me.ccrama.redditslide.Toolbox.Toolbox.DEFAULT_USERNOTE_TYPES.get(constants.getTypeName(index)).get("text");
            if (def != null) {
                result.append(def);
            } else {
                return "";
            }
        } else {
            return "";
        }
        result.append(bracket ? "]" : "");
        return result.toString();
    }

    public java.lang.String getModNameFromModIndex(int index) {
        return constants.getModName(index);
    }

    /**
     * Sets the Usernotes object's subreddit
     *
     * @param subreddit
     * 		
     */
    public void setSubreddit(java.lang.String subreddit) {
        this.subreddit = subreddit;
    }

    /**
     * Allows GSON to deserialize the "blob" into an object
     */
    public static class BlobDeserializer implements com.google.gson.JsonDeserializer<java.util.Map<java.lang.String, java.util.List<me.ccrama.redditslide.Toolbox.Usernote>>> {
        @java.lang.Override
        public java.util.Map<java.lang.String, java.util.List<me.ccrama.redditslide.Toolbox.Usernote>> deserialize(com.google.gson.JsonElement json, java.lang.reflect.Type typeOfT, com.google.gson.JsonDeserializationContext context) throws com.google.gson.JsonParseException {
            java.lang.String decodedBlob = me.ccrama.redditslide.Toolbox.Usernotes.BlobDeserializer.blobToJson(json.getAsString());
            if (decodedBlob == null) {
                return null;
            }
            com.google.gson.JsonElement jsonBlob = new com.google.gson.JsonParser().parse(decodedBlob);
            java.util.Map<java.lang.String, java.util.List<me.ccrama.redditslide.Toolbox.Usernote>> result = new java.util.TreeMap<>(java.lang.String.CASE_INSENSITIVE_ORDER);
            for (java.util.Map.Entry<java.lang.String, com.google.gson.JsonElement> userAndNotes : jsonBlob.getAsJsonObject().entrySet()) {
                java.util.List<me.ccrama.redditslide.Toolbox.Usernote> notesList = new java.util.ArrayList<>();
                for (com.google.gson.JsonElement notesArray : userAndNotes.getValue().getAsJsonObject().get("ns").getAsJsonArray()) {
                    notesList.add(context.deserialize(notesArray, me.ccrama.redditslide.Toolbox.Usernote.class));
                }
                result.put(userAndNotes.getKey().toLowerCase(), notesList);
            }
            return result;
        }

        /**
         * Converts a base64 encoded and zlib compressed blob into a String.
         *
         * @param blob
         * 		Blob to convert to string
         * @return Decoded blob
         */
        public static java.lang.String blobToJson(java.lang.String blob) {
            final byte[] decoded = android.util.Base64.decode(blob, android.util.Base64.DEFAULT);
            // Adapted from https://stackoverflow.com/a/33022277
            try {
                java.io.ByteArrayInputStream input = new java.io.ByteArrayInputStream(decoded);
                java.util.zip.InflaterInputStream inflater = new java.util.zip.InflaterInputStream(input);
                java.lang.StringBuilder result = new java.lang.StringBuilder();
                byte[] buf = new byte[5];
                int rlen;
                while ((rlen = inflater.read(buf)) != (-1)) {
                    result.append(new java.lang.String(java.util.Arrays.copyOf(buf, rlen)));
                } 
                return result.toString();
            } catch (java.io.IOException e) {
                return null;
            }
        }
    }

    /**
     * Allows GSON to serialize the usernotes map into a blob
     */
    public static class BlobSerializer implements com.google.gson.JsonSerializer<java.util.Map<java.lang.String, java.util.List<me.ccrama.redditslide.Toolbox.Usernote>>> {
        @java.lang.Override
        public com.google.gson.JsonElement serialize(java.util.Map<java.lang.String, java.util.List<me.ccrama.redditslide.Toolbox.Usernote>> src, java.lang.reflect.Type srcType, com.google.gson.JsonSerializationContext context) {
            java.util.Map<java.lang.String, java.util.Map<java.lang.String, java.util.List<me.ccrama.redditslide.Toolbox.Usernote>>> notes = new java.util.HashMap<>();
            for (java.lang.String user : src.keySet()) {
                java.util.Map<java.lang.String, java.util.List<me.ccrama.redditslide.Toolbox.Usernote>> newNotes = new java.util.HashMap<>();
                newNotes.put("ns", src.get(user));
                notes.put(user, newNotes);
            }
            java.lang.String encodedBlob = me.ccrama.redditslide.Toolbox.Usernotes.BlobSerializer.jsonToBlob(context.serialize(notes).toString());
            return context.serialize(encodedBlob);
        }

        /**
         * Converts a JSON string into a zlib compressed and base64 encoded blog
         *
         * @param json
         * 		JSON to turn into blob
         * @return Blob
         */
        public static java.lang.String jsonToBlob(java.lang.String json) {
            // Adapted from https://stackoverflow.com/a/33022277
            try {
                java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
                java.util.zip.DeflaterOutputStream deflater = new java.util.zip.DeflaterOutputStream(output);
                deflater.write(json.getBytes());
                deflater.flush();
                deflater.close();
                return android.util.Base64.encodeToString(output.toByteArray(), android.util.Base64.NO_WRAP);
            } catch (java.io.IOException e) {
                return null;
            }
        }
    }

    /**
     * Class describing the "constants" field of a usernotes config
     */
    public static class UsernotesConstants {
        @com.google.gson.annotations.SerializedName("users")
        private java.lang.String[] mods;// String array of mods. Usernote mod is index in this


        @com.google.gson.annotations.SerializedName("warnings")
        private java.lang.String[] types;// String array of used type names corresponding to types in the config/defaults. Usernote warning is index in this


        public UsernotesConstants() {
            // for GSON
        }

        public UsernotesConstants(java.lang.String[] mods, java.lang.String[] types) {
            this.mods = mods;
            this.types = types;
        }

        public java.lang.String[] getMods() {
            return mods;
        }

        /**
         * Add a new user to the mods array
         *
         * Does not check for duplicates!
         *
         * @param user
         * 		User to add
         * @return Index of added mod
         */
        public int addMod(java.lang.String user) {
            java.lang.String[] newMods = new java.lang.String[mods.length + 1];
            java.lang.System.arraycopy(mods, 0, newMods, 0, mods.length);
            newMods[newMods.length - 1] = user;
            mods = newMods;
            return newMods.length - 1;
        }

        public java.lang.String[] getTypes() {
            return types;
        }

        /**
         * Adds a type to the warnings array
         *
         * Does not check for duplicates!
         *
         * @param type
         * 		Type to add
         * @return Index of added type
         */
        public int addType(java.lang.String type) {
            java.lang.String[] newTypes = new java.lang.String[types.length + 1];
            java.lang.System.arraycopy(types, 0, newTypes, 0, types.length);
            newTypes[newTypes.length - 1] = type;
            types = newTypes;
            return newTypes.length - 1;
        }

        public java.lang.String getTypeName(int index) {
            return types[index];
        }

        public java.lang.String getModName(int index) {
            return mods[index];
        }
    }
}