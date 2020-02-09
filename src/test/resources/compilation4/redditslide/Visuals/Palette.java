package me.ccrama.redditslide.Visuals;
import java.util.Locale;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.ColorPreferences;
public class Palette {
    private int fontColor;

    public int backgroundColor;

    public static int getDefaultColor() {
        if (me.ccrama.redditslide.Reddit.colors.contains("DEFAULTCOLOR")) {
            return me.ccrama.redditslide.Reddit.colors.getInt("DEFAULTCOLOR", android.graphics.Color.parseColor("#e64a19"));
        } else {
            return android.graphics.Color.parseColor("#e64a19");
        }
    }

    /**
     * Gets the status bar color for the activity.
     *
     * @return Color-int for the status bar
     */
    public static int getStatusBarColor() {
        return me.ccrama.redditslide.Visuals.Palette.getDarkerColor(me.ccrama.redditslide.Visuals.Palette.getDefaultColor());
    }

    /**
     * Gets the status bar color for the activity based on the specified username.
     *
     * @param username
     * 		The username to base the theme on
     * @return Color-int for the status bar
     */
    public static int getUserStatusBarColor(java.lang.String username) {
        return me.ccrama.redditslide.Visuals.Palette.getDarkerColor(me.ccrama.redditslide.Visuals.Palette.getColorUser(username));
    }

    /**
     * Gets the status bar color for the activity based on the specified subreddit.
     *
     * @param subreddit
     * 		The subreddit to base the theme on
     * @return Color-int for the status bar
     */
    public static int getSubredditStatusBarColor(java.lang.String subreddit) {
        return me.ccrama.redditslide.Visuals.Palette.getDarkerColor(me.ccrama.redditslide.Visuals.Palette.getColor(subreddit));
    }

    public static int getDefaultAccent() {
        if (me.ccrama.redditslide.Reddit.colors.contains("ACCENTCOLOR")) {
            return me.ccrama.redditslide.Reddit.colors.getInt("ACCENTCOLOR", android.graphics.Color.parseColor("#ff6e40"));
        } else {
            return android.graphics.Color.parseColor("#ff6e40");
        }
    }

    private int mainColor;

    private int accentColor;

    private static int getColorAccent(final java.lang.String subreddit) {
        if (me.ccrama.redditslide.Reddit.colors.contains("ACCENT" + subreddit.toLowerCase(java.util.Locale.ENGLISH))) {
            return me.ccrama.redditslide.Reddit.colors.getInt("ACCENT" + subreddit.toLowerCase(java.util.Locale.ENGLISH), me.ccrama.redditslide.Visuals.Palette.getDefaultColor());
        } else {
            return me.ccrama.redditslide.Visuals.Palette.getDefaultColor();
        }
    }

    public static int getFontColorUser(final java.lang.String subreddit) {
        if (me.ccrama.redditslide.Reddit.colors.contains("USER" + subreddit.toLowerCase(java.util.Locale.ENGLISH))) {
            final int color = me.ccrama.redditslide.Reddit.colors.getInt("USER" + subreddit.toLowerCase(java.util.Locale.ENGLISH), me.ccrama.redditslide.Visuals.Palette.getDefaultColor());
            if (color == me.ccrama.redditslide.Visuals.Palette.getDefaultColor()) {
                return 0;
            } else {
                return color;
            }
        } else {
            return 0;
        }
    }

    public static int[] getColors(java.lang.String subreddit, android.content.Context context) {
        int[] ints = new int[2];
        ints[0] = me.ccrama.redditslide.Visuals.Palette.getColor(subreddit);
        ints[1] = new me.ccrama.redditslide.ColorPreferences(context).getColor(subreddit);
        return ints;
    }

    public static int getColor(final java.lang.String subreddit) {
        if ((subreddit != null) && me.ccrama.redditslide.Reddit.colors.contains(subreddit.toLowerCase(java.util.Locale.ENGLISH))) {
            return me.ccrama.redditslide.Reddit.colors.getInt(subreddit.toLowerCase(java.util.Locale.ENGLISH), me.ccrama.redditslide.Visuals.Palette.getDefaultColor());
        }
        return me.ccrama.redditslide.Visuals.Palette.getDefaultColor();
    }

    public static void setColor(final java.lang.String subreddit, int color) {
        me.ccrama.redditslide.Reddit.colors.edit().putInt(subreddit.toLowerCase(java.util.Locale.ENGLISH), color).apply();
    }

    public static void removeColor(final java.lang.String subreddit) {
        me.ccrama.redditslide.Reddit.colors.edit().remove(subreddit.toLowerCase(java.util.Locale.ENGLISH)).apply();
    }

    public static int getColorUser(final java.lang.String username) {
        if (me.ccrama.redditslide.Reddit.colors.contains("USER" + username.toLowerCase(java.util.Locale.ENGLISH))) {
            return me.ccrama.redditslide.Reddit.colors.getInt("USER" + username.toLowerCase(java.util.Locale.ENGLISH), me.ccrama.redditslide.Visuals.Palette.getDefaultColor());
        } else {
            return me.ccrama.redditslide.Visuals.Palette.getDefaultColor();
        }
    }

    public static void setColorUser(final java.lang.String username, int color) {
        me.ccrama.redditslide.Reddit.colors.edit().putInt("USER" + username.toLowerCase(java.util.Locale.ENGLISH), color).apply();
    }

    public static void removeUserColor(final java.lang.String username) {
        me.ccrama.redditslide.Reddit.colors.edit().remove("USER" + username.toLowerCase(java.util.Locale.ENGLISH)).apply();
    }

    public static me.ccrama.redditslide.Visuals.Palette getSubredditPallete(java.lang.String subredditname) {
        me.ccrama.redditslide.Visuals.Palette p = new me.ccrama.redditslide.Visuals.Palette();
        p.theme = me.ccrama.redditslide.Visuals.Palette.ThemeEnum.valueOf(me.ccrama.redditslide.Reddit.colors.getString("ThemeDefault", "DARK"));
        p.fontColor = p.theme.getFontColor();
        p.backgroundColor = p.theme.getBackgroundColor();
        p.mainColor = me.ccrama.redditslide.Visuals.Palette.getColor(subredditname);
        p.accentColor = me.ccrama.redditslide.Visuals.Palette.getColorAccent(subredditname);
        return p;
    }

    public static me.ccrama.redditslide.Visuals.Palette getDefaultPallete() {
        me.ccrama.redditslide.Visuals.Palette p = new me.ccrama.redditslide.Visuals.Palette();
        p.theme = me.ccrama.redditslide.Visuals.Palette.ThemeEnum.valueOf(me.ccrama.redditslide.Reddit.colors.getString("ThemeDefault", "DARK"));
        p.fontColor = p.theme.getFontColor();
        p.backgroundColor = p.theme.getBackgroundColor();
        return p;
    }

    public static int getDarkerColor(java.lang.String s) {
        return me.ccrama.redditslide.Visuals.Palette.getDarkerColor(me.ccrama.redditslide.Visuals.Palette.getColor(s));
    }

    public static int getDarkerColor(int color) {
        float[] hsv = new float[3];
        android.graphics.Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8F;
        color = android.graphics.Color.HSVToColor(hsv);
        return color;
    }

    public me.ccrama.redditslide.Visuals.Palette.ThemeEnum theme;

    public enum ThemeEnum {

        DARK("Dark", android.graphics.Color.parseColor("#303030"), android.graphics.Color.parseColor("#424242"), android.graphics.Color.parseColor("#ffffff"), android.graphics.Color.parseColor("#B3FFFFFF")),
        LIGHT("Light", android.graphics.Color.parseColor("#e5e5e5"), android.graphics.Color.parseColor("#ffffff"), android.graphics.Color.parseColor("#de000000"), android.graphics.Color.parseColor("#8A000000")),
        AMOLEDBLACK("Black", android.graphics.Color.parseColor("#000000"), android.graphics.Color.parseColor("#212121"), android.graphics.Color.parseColor("#ffffff"), android.graphics.Color.parseColor("#B3FFFFFF")),
        SEPIA("Sepia", android.graphics.Color.parseColor("#cac5ad"), android.graphics.Color.parseColor("#e2dfd7"), android.graphics.Color.parseColor("#DE3e3d36"), android.graphics.Color.parseColor("#8A3e3d36")),
        BLUE("Dark Blue", android.graphics.Color.parseColor("#2F3D44"), android.graphics.Color.parseColor("#37474F"), android.graphics.Color.parseColor("#ffffff"), android.graphics.Color.parseColor("#B3FFFFFF")),
        PIXEL("Pixel", android.graphics.Color.parseColor("#3e3e3e"), android.graphics.Color.parseColor("#2d2d2d"), android.graphics.Color.parseColor("#ffffff"), android.graphics.Color.parseColor("#B3FFFFFF")),
        DEEP("Deep", android.graphics.Color.parseColor("#16161C"), android.graphics.Color.parseColor("#212026"), android.graphics.Color.parseColor("#ffffff"), android.graphics.Color.parseColor("#1C1B21"));
        public java.lang.String getDisplayName() {
            return displayName;
        }

        public int getBackgroundColor() {
            return backgroundColor;
        }

        public int getCardBackgroundColor() {
            return cardBackgroundColor;
        }

        public int getFontColor() {
            return fontColor;
        }

        public int getTint() {
            return tint;
        }

        final java.lang.String displayName;

        final int backgroundColor;

        final int cardBackgroundColor;

        final int tint;

        final int fontColor;

        ThemeEnum(java.lang.String s, int backgroundColor, int cardBackgroundColor, int fontColor, int tint) {
            this.displayName = s;
            this.backgroundColor = backgroundColor;
            this.cardBackgroundColor = cardBackgroundColor;
            this.fontColor = fontColor;
            this.tint = tint;
        }
    }
}