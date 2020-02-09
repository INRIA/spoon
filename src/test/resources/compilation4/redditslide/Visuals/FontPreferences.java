package me.ccrama.redditslide.Visuals;
import me.ccrama.redditslide.R;
/**
 * Created by ccrama on 7/9/2015.
 */
public class FontPreferences {
    private static final java.lang.String FONT_STYLE_POST = "FONT_STYLE_POST";

    private static final java.lang.String FONT_STYLE_COMMENT = "FONT_STYLE_COMMENT";

    private static final java.lang.String FONT_COMMENT = "FONT_COMMENT";

    private static final java.lang.String FONT_TITLE = "FONT_TITLE";

    private final android.content.Context context;

    public FontPreferences(android.content.Context context) {
        this.context = context;
    }

    private android.content.SharedPreferences open() {
        return context.getSharedPreferences("prefs", android.content.Context.MODE_PRIVATE);
    }

    private android.content.SharedPreferences.Editor edit() {
        return open().edit();
    }

    public me.ccrama.redditslide.Visuals.FontPreferences.FontStyle getPostFontStyle() {
        return me.ccrama.redditslide.Visuals.FontPreferences.FontStyle.valueOf(open().getString(me.ccrama.redditslide.Visuals.FontPreferences.FONT_STYLE_POST, me.ccrama.redditslide.Visuals.FontPreferences.FontStyle.Medium.name()));
    }

    public me.ccrama.redditslide.Visuals.FontPreferences.FontStyleComment getCommentFontStyle() {
        return me.ccrama.redditslide.Visuals.FontPreferences.FontStyleComment.valueOf(open().getString(me.ccrama.redditslide.Visuals.FontPreferences.FONT_STYLE_COMMENT, me.ccrama.redditslide.Visuals.FontPreferences.FontStyleComment.Medium.name()));
    }

    public me.ccrama.redditslide.Visuals.FontPreferences.FontTypeComment getFontTypeComment() {
        return me.ccrama.redditslide.Visuals.FontPreferences.FontTypeComment.valueOf(open().getString(me.ccrama.redditslide.Visuals.FontPreferences.FONT_COMMENT, me.ccrama.redditslide.Visuals.FontPreferences.FontTypeComment.Regular.name()));
    }

    public me.ccrama.redditslide.Visuals.FontPreferences.FontTypeTitle getFontTypeTitle() {
        return me.ccrama.redditslide.Visuals.FontPreferences.FontTypeTitle.valueOf(open().getString(me.ccrama.redditslide.Visuals.FontPreferences.FONT_TITLE, me.ccrama.redditslide.Visuals.FontPreferences.FontTypeTitle.Regular.name()));
    }

    public void setPostFontStyle(me.ccrama.redditslide.Visuals.FontPreferences.FontStyle style) {
        edit().putString(me.ccrama.redditslide.Visuals.FontPreferences.FONT_STYLE_POST, style.name()).commit();
    }

    public void setCommentFontStyle(me.ccrama.redditslide.Visuals.FontPreferences.FontStyleComment style) {
        edit().putString(me.ccrama.redditslide.Visuals.FontPreferences.FONT_STYLE_COMMENT, style.name()).commit();
    }

    public void setCommentFont(me.ccrama.redditslide.Visuals.FontPreferences.FontTypeComment style) {
        edit().putString(me.ccrama.redditslide.Visuals.FontPreferences.FONT_COMMENT, style.name()).commit();
    }

    public void setTitleFont(me.ccrama.redditslide.Visuals.FontPreferences.FontTypeTitle style) {
        edit().putString(me.ccrama.redditslide.Visuals.FontPreferences.FONT_TITLE, style.name()).commit();
    }

    public enum FontStyle {

        Tiny(me.ccrama.redditslide.R.style.FontStyle_TinyPost, me.ccrama.redditslide.R.string.font_size_tiny),
        Smaller(me.ccrama.redditslide.R.style.FontStyle_SmallerPost, me.ccrama.redditslide.R.string.font_size_smaller),
        Small(me.ccrama.redditslide.R.style.FontStyle_SmallPost, me.ccrama.redditslide.R.string.font_size_small),
        Medium(me.ccrama.redditslide.R.style.FontStyle_MediumPost, me.ccrama.redditslide.R.string.font_size_medium),
        Large(me.ccrama.redditslide.R.style.FontStyle_LargePost, me.ccrama.redditslide.R.string.font_size_large),
        Larger(me.ccrama.redditslide.R.style.FontStyle_LargerPost, me.ccrama.redditslide.R.string.font_size_larger),
        Huge(me.ccrama.redditslide.R.style.FontStyle_HugePost, me.ccrama.redditslide.R.string.font_size_huge);
        private final int resId;

        private final int title;

        public int getResId() {
            return resId;
        }

        public int getTitle() {
            return title;
        }

        FontStyle(int resId, int title) {
            this.resId = resId;
            this.title = title;
        }
    }

    public enum FontStyleComment {

        Smaller(me.ccrama.redditslide.R.style.FontStyle_SmallerComment, me.ccrama.redditslide.R.string.font_size_smaller),
        Small(me.ccrama.redditslide.R.style.FontStyle_SmallComment, me.ccrama.redditslide.R.string.font_size_small),
        Medium(me.ccrama.redditslide.R.style.FontStyle_MediumComment, me.ccrama.redditslide.R.string.font_size_medium),
        Large(me.ccrama.redditslide.R.style.FontStyle_LargeComment, me.ccrama.redditslide.R.string.font_size_large),
        Larger(me.ccrama.redditslide.R.style.FontStyle_LargerComment, me.ccrama.redditslide.R.string.font_size_larger),
        Huge(me.ccrama.redditslide.R.style.FontStyle_HugeComment, me.ccrama.redditslide.R.string.font_size_huge);
        private final int resId;

        private final int title;

        public int getResId() {
            return resId;
        }

        public int getTitle() {
            return title;
        }

        FontStyleComment(int resId, int title) {
            this.resId = resId;
            this.title = title;
        }
    }

    public enum FontTypeComment {

        Slab(com.devspark.robototextview.RobotoTypefaces.TYPEFACE_ROBOTO_SLAB_REGULAR, "Slab"),
        Condensed(com.devspark.robototextview.RobotoTypefaces.TYPEFACE_ROBOTO_CONDENSED_REGULAR, "Condensed"),
        Light(com.devspark.robototextview.RobotoTypefaces.TYPEFACE_ROBOTO_LIGHT, "Light"),
        Regular(com.devspark.robototextview.RobotoTypefaces.TYPEFACE_ROBOTO_REGULAR, "Regular"),
        System(-1, "System");
        private final int typeface;

        private final java.lang.String title;

        public int getTypeface() {
            return typeface;
        }

        public java.lang.String getTitle() {
            return title;
        }

        FontTypeComment(int resId, java.lang.String title) {
            this.typeface = resId;
            this.title = title;
        }
    }

    public enum FontTypeTitle {

        Slab(com.devspark.robototextview.RobotoTypefaces.TYPEFACE_ROBOTO_SLAB_LIGHT, "Slab Light"),
        SlabReg(com.devspark.robototextview.RobotoTypefaces.TYPEFACE_ROBOTO_SLAB_REGULAR, "Slab Regular"),
        Condensed(com.devspark.robototextview.RobotoTypefaces.TYPEFACE_ROBOTO_CONDENSED_LIGHT, "Condensed Light"),
        CondensedReg(com.devspark.robototextview.RobotoTypefaces.TYPEFACE_ROBOTO_CONDENSED_REGULAR, "Condensed Regular"),
        Light(com.devspark.robototextview.RobotoTypefaces.TYPEFACE_ROBOTO_LIGHT, "Light"),
        Regular(com.devspark.robototextview.RobotoTypefaces.TYPEFACE_ROBOTO_REGULAR, "Regular"),
        Bold(com.devspark.robototextview.RobotoTypefaces.TYPEFACE_ROBOTO_BOLD, "Bold"),
        Medium(com.devspark.robototextview.RobotoTypefaces.TYPEFACE_ROBOTO_MEDIUM, "Medium"),
        CondensedBold(com.devspark.robototextview.RobotoTypefaces.TYPEFACE_ROBOTO_CONDENSED_BOLD, "Condensed Bold"),
        System(-1, "System");
        private final int typeface;

        private final java.lang.String title;

        public int getTypeface() {
            return typeface;
        }

        public java.lang.String getTitle() {
            return title;
        }

        FontTypeTitle(int resId, java.lang.String title) {
            this.typeface = resId;
            this.title = title;
        }
    }
}