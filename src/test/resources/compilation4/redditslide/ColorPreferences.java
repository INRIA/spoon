package me.ccrama.redditslide;
import java.util.Locale;
import me.ccrama.redditslide.Activities.Slide;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
/**
 * Created by ccrama on 7/9/2015.
 */
public class ColorPreferences {
    private static final java.lang.String USER_THEME_DELIMITER = "$USER$";

    public static final java.lang.String FONT_STYLE = "THEME";

    private final android.content.Context context;

    public ColorPreferences(android.content.Context context) {
        this.context = context;
    }

    public static int[] getColors(android.content.Context context, int c) {
        if (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_red_500)) {
            return new int[]{ // ContextCompat.getColor(context, R.color.md_red_100),
            android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_red_200), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_red_300), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_red_400), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_red_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_red_600), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_red_700), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_red_800), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_red_900) };
        } else if (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_pink_500)) {
            return new int[]{ // ContextCompat.getColor(context, R.color.md_pink_100),
            android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_pink_200), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_pink_300), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_pink_400), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_pink_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_pink_600), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_pink_700), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_pink_800), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_pink_900) };
        } else if (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_purple_500)) {
            return new int[]{ // ContextCompat.getColor(context, R.color.md_purple_100),
            android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_purple_200), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_purple_300), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_purple_400), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_purple_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_purple_600), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_purple_700), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_purple_800), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_purple_900) };
        } else if (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_purple_500)) {
            return new int[]{ // ContextCompat.getColor(context, R.color.md_deep_purple_100),
            android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_purple_200), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_purple_300), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_purple_400), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_purple_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_purple_600), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_purple_700), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_purple_800), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_purple_900) };
        } else if (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_indigo_500)) {
            return new int[]{ // ContextCompat.getColor(context, R.color.md_indigo_100),
            android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_indigo_200), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_indigo_300), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_indigo_400), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_indigo_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_indigo_600), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_indigo_700), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_indigo_800), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_indigo_900) };
        } else if (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_500)) {
            return new int[]{ // ContextCompat.getColor(context, R.color.md_blue_100),
            android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_200), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_300), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_400), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_600), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_700), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_800), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_900) };
        } else if (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_blue_500)) {
            return new int[]{ // ContextCompat.getColor(context, R.color.md_light_blue_100),
            android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_blue_200), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_blue_300), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_blue_400), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_blue_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_blue_600), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_blue_700), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_blue_800), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_blue_900) };
        } else if (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_cyan_500)) {
            return new int[]{ // ContextCompat.getColor(context, R.color.md_cyan_100),
            android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_cyan_200), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_cyan_300), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_cyan_400), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_cyan_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_cyan_600), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_cyan_700), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_cyan_800), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_cyan_900) };
        } else if (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_teal_500)) {
            return new int[]{ // ContextCompat.getColor(context, R.color.md_teal_100),
            android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_teal_200), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_teal_300), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_teal_400), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_teal_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_teal_600), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_teal_700), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_teal_800), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_teal_900) };
        } else if (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_green_500)) {
            return new int[]{ // ContextCompat.getColor(context, R.color.md_green_100),
            android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_green_200), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_green_300), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_green_400), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_green_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_green_600), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_green_700), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_green_800), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_green_900) };
        } else if (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_green_500)) {
            return new int[]{ // ContextCompat.getColor(context, R.color.md_light_green_100),
            // ContextCompat.getColor(context, R.color.md_light_green_200),
            android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_green_300), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_green_400), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_green_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_green_600), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_green_700), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_green_800), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_green_900) };
        } else if (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_lime_500)) {
            return new int[]{ // ContextCompat.getColor(context, R.color.md_lime_100),
            // ContextCompat.getColor(context, R.color.md_lime_200),
            android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_lime_300), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_lime_400), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_lime_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_lime_600), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_lime_700), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_lime_800), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_lime_900) };
        } else if (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_yellow_500)) {
            return new int[]{ // ContextCompat.getColor(context, R.color.md_yellow_100),
            // ContextCompat.getColor(context, R.color.md_yellow_200),
            // ContextCompat.getColor(context, R.color.md_yellow_300),
            android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_yellow_400), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_yellow_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_yellow_600), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_yellow_700), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_yellow_800), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_yellow_900) };
        } else if (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_amber_500)) {
            return new int[]{ // ContextCompat.getColor(context, R.color.md_amber_100),
            // ContextCompat.getColor(context, R.color.md_amber_200),
            android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_amber_300), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_amber_400), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_amber_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_amber_600), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_amber_700), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_amber_800), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_amber_900) };
        } else if (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_orange_500)) {
            return new int[]{ // ContextCompat.getColor(context, R.color.md_orange_100),
            // ContextCompat.getColor(context, R.color.md_orange_200),
            android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_orange_300), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_orange_400), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_orange_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_orange_600), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_orange_700), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_orange_800), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_orange_900) };
        } else if (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_orange_500)) {
            return new int[]{ // ContextCompat.getColor(context, R.color.md_deep_orange_100),
            android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_orange_200), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_orange_300), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_orange_400), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_orange_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_orange_600), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_orange_700), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_orange_800), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_orange_900) };
        } else if (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_brown_500)) {
            return new int[]{ // ContextCompat.getColor(context, R.color.md_brown_100),
            android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_brown_200), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_brown_300), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_brown_400), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_brown_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_brown_600), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_brown_700), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_brown_800), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_brown_900) };
        } else if (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_grey_500)) {
            return new int[]{ // ContextCompat.getColor(context, R.color.md_grey_100),
            // ContextCompat.getColor(context, R.color.md_grey_200),
            // ContextCompat.getColor(context, R.color.md_grey_300),
            android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_grey_400), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_grey_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_grey_600), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_grey_700), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_grey_800), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_grey_900), android.graphics.Color.parseColor("#000000") };
        } else {
            return new int[]{ // ContextCompat.getColor(context, R.color.md_blue_grey_100),
            // ContextCompat.getColor(context, R.color.md_blue_grey_200),
            android.graphics.Color.parseColor("#5C94C8"), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_grey_300), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_grey_400), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_grey_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_grey_600), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_grey_700), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_grey_800), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_grey_900) };
        }
    }

    public static java.lang.String getIconName(android.content.Context context, int c) {
        java.lang.String result = me.ccrama.redditslide.Activities.Slide.class.getPackage().getName() + ".Slide";
        if ((((((((c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_red_200)) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_red_300))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_red_400))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_red_500))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_red_600))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_red_700))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_red_800))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_red_900))) {
            result += "Red";
        } else if ((((((((c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_pink_200)) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_pink_300))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_pink_400))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_pink_500))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_pink_600))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_pink_700))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_pink_800))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_pink_900))) {
            result += "Pink";
        } else if ((((((((c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_purple_200)) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_purple_300))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_purple_400))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_purple_500))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_purple_600))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_purple_700))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_purple_800))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_purple_900))) {
            result += "Purple";
        } else if ((((((((c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_purple_200)) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_purple_300))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_purple_400))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_purple_500))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_purple_600))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_purple_700))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_purple_800))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_purple_900))) {
            result += "DeepPurple";
        } else if ((((((((c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_indigo_200)) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_indigo_300))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_indigo_400))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_indigo_500))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_indigo_600))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_indigo_700))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_indigo_800))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_indigo_900))) {
            result += "Indigo";
        } else if ((((((((c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_200)) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_300))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_400))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_500))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_600))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_700))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_800))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_900))) {
            result += "Blue";
        } else if ((((((((c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_blue_200)) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_blue_300))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_blue_400))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_blue_500))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_blue_600))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_blue_700))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_blue_800))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_blue_900))) {
            result += "LightBlue";
        } else if ((((((((c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_cyan_200)) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_cyan_300))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_cyan_400))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_cyan_500))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_cyan_600))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_cyan_700))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_cyan_800))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_cyan_900))) {
            result += "Cyan";
        } else if ((((((((c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_teal_200)) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_teal_300))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_teal_400))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_teal_500))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_teal_600))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_teal_700))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_teal_800))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_teal_900))) {
            result += "Teal";
        } else if ((((((((c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_green_200)) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_green_300))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_green_400))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_green_500))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_green_600))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_green_700))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_green_800))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_green_900))) {
            result += "Green";
        } else if ((((((((c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_green_200)) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_green_300))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_green_400))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_green_500))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_green_600))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_green_700))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_green_800))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_green_900))) {
            result += "LightGreen";
        } else if ((((((((c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_lime_200)) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_lime_300))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_lime_400))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_lime_500))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_lime_600))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_lime_700))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_lime_800))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_lime_900))) {
            result += "Lime";
        } else if ((((((c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_yellow_400)) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_yellow_500))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_yellow_600))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_yellow_700))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_yellow_800))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_yellow_900))) {
            result += "Yellow";
        } else if ((((((((c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_amber_200)) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_amber_300))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_amber_400))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_amber_500))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_amber_600))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_amber_700))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_amber_800))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_amber_900))) {
            result += "Amber";
        } else if ((((((((c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_orange_200)) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_orange_300))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_orange_400))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_orange_500))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_orange_600))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_orange_700))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_orange_800))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_orange_900))) {
            result += "Orange";
        } else if ((((((((c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_orange_200)) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_orange_300))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_orange_400))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_orange_500))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_orange_600))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_orange_700))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_orange_800))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_orange_900))) {
            result += "DeepOrange";
        } else if ((((((((c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_brown_200)) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_brown_300))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_brown_400))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_brown_500))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_brown_600))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_brown_700))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_brown_800))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_brown_900))) {
            result += "Brown";
        } else if (((((((c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_grey_300)) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_grey_400))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_grey_500))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_grey_600))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_grey_700))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_grey_800))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_grey_900))) {
            result += "Grey";
        } else if ((((((((c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_grey_200)) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_grey_300))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_grey_400))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_grey_500))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_grey_600))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_grey_700))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_grey_800))) || (c == android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_grey_900))) {
            result += "BlueGrey";
        } else {
            result += "Default";
        }
        return result;
    }

    public static int[] getBaseColors(android.content.Context context) {
        return new int[]{ android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_red_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_pink_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_purple_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_purple_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_indigo_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_blue_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_cyan_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_teal_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_green_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_light_green_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_lime_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_yellow_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_amber_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_orange_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_deep_orange_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_brown_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_blue_grey_500), android.support.v4.content.ContextCompat.getColor(context, me.ccrama.redditslide.R.color.md_grey_500) };
    }

    public static int getNumColorsFromThemeType(int themeType) {
        int num = 0;
        for (me.ccrama.redditslide.ColorPreferences.Theme theme : me.ccrama.redditslide.ColorPreferences.Theme.values()) {
            if (themeType == theme.getThemeType()) {
                num++;
            }
        }
        return num;
    }

    protected android.content.SharedPreferences open() {
        return context.getSharedPreferences("prefs", android.content.Context.MODE_PRIVATE);
    }

    protected android.content.SharedPreferences.Editor edit() {
        return open().edit();
    }

    private java.lang.String getUserThemeName(java.lang.String themeName, java.lang.String defaultValue) {
        java.lang.String userTheme = open().getString(themeName.concat(me.ccrama.redditslide.ColorPreferences.USER_THEME_DELIMITER + me.ccrama.redditslide.Authentication.name), null);
        if (userTheme != null) {
            return userTheme.split(org.apache.commons.text.StringEscapeUtils.escapeJava(me.ccrama.redditslide.ColorPreferences.USER_THEME_DELIMITER))[0];
        } else {
            return open().getString(themeName, defaultValue);
        }
    }

    private void setUserThemeName(java.lang.String themeName, java.lang.String defaultValue) {
        edit().putString(themeName.concat(me.ccrama.redditslide.ColorPreferences.USER_THEME_DELIMITER + me.ccrama.redditslide.Authentication.name), defaultValue).commit();
    }

    public me.ccrama.redditslide.ColorPreferences.Theme getFontStyle() {
        try {
            if (me.ccrama.redditslide.SettingValues.isNight()) {
                return getColoredTheme(me.ccrama.redditslide.SettingValues.nightTheme, getUserThemeName(me.ccrama.redditslide.ColorPreferences.FONT_STYLE, me.ccrama.redditslide.ColorPreferences.Theme.dark_amber.name()), me.ccrama.redditslide.ColorPreferences.Theme.valueOf(open().getString(me.ccrama.redditslide.ColorPreferences.FONT_STYLE, me.ccrama.redditslide.ColorPreferences.Theme.dark_amber.name())));
            }
            return me.ccrama.redditslide.ColorPreferences.Theme.valueOf(getUserThemeName(me.ccrama.redditslide.ColorPreferences.FONT_STYLE, me.ccrama.redditslide.ColorPreferences.Theme.dark_amber.name()));
        } catch (java.lang.Exception e) {
            return me.ccrama.redditslide.ColorPreferences.Theme.dark_amber;
        }
    }

    public me.ccrama.redditslide.ColorPreferences.Theme getFontStyleSubreddit(java.lang.String s) {
        try {
            return me.ccrama.redditslide.ColorPreferences.Theme.valueOf(getUserThemeName(s, getFontStyle().name()));
        } catch (java.lang.Exception e) {
            return me.ccrama.redditslide.ColorPreferences.Theme.dark_amber;
        }
    }

    public void setFontStyle(me.ccrama.redditslide.ColorPreferences.Theme style) {
        setUserThemeName(me.ccrama.redditslide.ColorPreferences.FONT_STYLE, style.name());
    }

    public int getThemeSubreddit(java.lang.String s) {
        int back = getFontStyle().getThemeType();
        java.lang.String str = getUserThemeName(s.toLowerCase(java.util.Locale.ENGLISH), getFontStyle().getTitle());
        if (me.ccrama.redditslide.ColorPreferences.Theme.valueOf(str).getThemeType() != back) {
            java.lang.String[] names = str.split("_");
            java.lang.String name = names[names.length - 1];
            for (me.ccrama.redditslide.ColorPreferences.Theme theme : me.ccrama.redditslide.ColorPreferences.Theme.values()) {
                if (theme.toString().contains(name) && (theme.getThemeType() == back)) {
                    setFontStyle(theme, s);
                    return theme.baseId;
                }
            }
        } else {
            return me.ccrama.redditslide.ColorPreferences.Theme.valueOf(str).baseId;
        }
        return getFontStyle().baseId;
    }

    public me.ccrama.redditslide.ColorPreferences.Theme getThemeSubreddit(java.lang.String s, boolean b) {
        if (s == null) {
            s = "Promoted";
        }
        int back = getFontStyle().getThemeType();
        java.lang.String str = getUserThemeName(s.toLowerCase(java.util.Locale.ENGLISH), getFontStyle().getTitle());
        try {
            if (me.ccrama.redditslide.ColorPreferences.Theme.valueOf(str).getThemeType() != back) {
                java.lang.String[] names = str.split("_");
                java.lang.String name = names[names.length - 1];
                for (me.ccrama.redditslide.ColorPreferences.Theme theme : me.ccrama.redditslide.ColorPreferences.Theme.values()) {
                    if (theme.toString().contains(name) && (theme.getThemeType() == back)) {
                        setFontStyle(theme, s);
                        return theme;
                    }
                }
            } else {
                return me.ccrama.redditslide.ColorPreferences.Theme.valueOf(str);
            }
        } catch (java.lang.Exception ignored) {
        }
        return getFontStyle();
    }

    public int getDarkThemeSubreddit(java.lang.String s) {
        return getColoredTheme(4, getUserThemeName(s.toLowerCase(java.util.Locale.ENGLISH), getFontStyle().getTitle()), getFontStyle()).baseId;
    }

    private me.ccrama.redditslide.ColorPreferences.Theme getColoredTheme(int i, java.lang.String base, me.ccrama.redditslide.ColorPreferences.Theme defaultTheme) {
        try {
            if (me.ccrama.redditslide.ColorPreferences.Theme.valueOf(base).getThemeType() != i) {
                java.lang.String[] names = base.split("_");
                java.lang.String name = names[names.length - 1];
                for (me.ccrama.redditslide.ColorPreferences.Theme theme : me.ccrama.redditslide.ColorPreferences.Theme.values()) {
                    if (theme.toString().contains(name) && (theme.getThemeType() == i)) {
                        return theme;
                    }
                }
            } else {
                return me.ccrama.redditslide.ColorPreferences.Theme.valueOf(base);
            }
        } catch (java.lang.Exception ignored) {
        }
        return defaultTheme;
    }

    public void setFontStyle(me.ccrama.redditslide.ColorPreferences.Theme style, java.lang.String s) {
        setUserThemeName(s.toLowerCase(java.util.Locale.ENGLISH), style.name());
    }

    public void removeFontStyle(java.lang.String subreddit) {
        edit().remove(subreddit.concat(me.ccrama.redditslide.ColorPreferences.USER_THEME_DELIMITER + me.ccrama.redditslide.Authentication.name)).commit();
    }

    public int getColor(java.lang.String s) {
        return android.support.v4.content.ContextCompat.getColor(context, getThemeSubreddit(s, true).getColor());
    }

    public enum Theme {

        dark_white(me.ccrama.redditslide.R.style.white_dark, "dark_white", me.ccrama.redditslide.R.color.md_blue_grey_200, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Dark.getValue()),
        light_white(me.ccrama.redditslide.R.style.white_light, "light_white", me.ccrama.redditslide.R.color.md_blue_grey_200, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Light.getValue()),
        amoled_white(me.ccrama.redditslide.R.style.white_amoled, "amoled_white", me.ccrama.redditslide.R.color.md_blue_grey_200, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.AMOLED.getValue()),
        blue_white(me.ccrama.redditslide.R.style.white_blue, "blue_white", me.ccrama.redditslide.R.color.md_blue_grey_200, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.DarkBlue.getValue()),
        amoled_light_white(me.ccrama.redditslide.R.style.white_AMOLED_lighter, "amoled_light_white", me.ccrama.redditslide.R.color.md_blue_grey_200, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.AMOLEDContrast.getValue()),
        dark_pink(me.ccrama.redditslide.R.style.pink_dark, "dark_pink", me.ccrama.redditslide.R.color.md_pink_A200, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Dark.getValue()),
        light_pink(me.ccrama.redditslide.R.style.pink_light, "light_pink", me.ccrama.redditslide.R.color.md_pink_A200, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Light.getValue()),
        amoled_pink(me.ccrama.redditslide.R.style.pink_amoled, "amoled_pink", me.ccrama.redditslide.R.color.md_pink_A200, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.AMOLED.getValue()),
        blue_pink(me.ccrama.redditslide.R.style.pink_blue, "blue_pink", me.ccrama.redditslide.R.color.md_pink_A200, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.DarkBlue.getValue()),
        amoled_light_pink(me.ccrama.redditslide.R.style.pink_AMOLED_lighter, "amoled_light_pink", me.ccrama.redditslide.R.color.md_pink_A200, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.AMOLEDContrast.getValue()),
        dark_deeporange(me.ccrama.redditslide.R.style.deeporange_dark, "dark_deeporange", me.ccrama.redditslide.R.color.md_deep_orange_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Dark.getValue()),
        light_deeporange(me.ccrama.redditslide.R.style.deeporange_LIGHT, "light_deeporange", me.ccrama.redditslide.R.color.md_deep_orange_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Light.getValue()),
        amoled_deeporange(me.ccrama.redditslide.R.style.deeporange_AMOLED, "amoled_deeporange", me.ccrama.redditslide.R.color.md_deep_orange_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.AMOLED.getValue()),
        blue_deeporange(me.ccrama.redditslide.R.style.deeporange_blue, "blue_deeporange", me.ccrama.redditslide.R.color.md_deep_orange_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.DarkBlue.getValue()),
        amoled_light_deeporange(me.ccrama.redditslide.R.style.deeporange_AMOLED_lighter, "amoled_light_deeporange", me.ccrama.redditslide.R.color.md_deep_orange_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.AMOLEDContrast.getValue()),
        dark_amber(me.ccrama.redditslide.R.style.amber_dark, "dark_amber", me.ccrama.redditslide.R.color.md_amber_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Dark.getValue()),
        light_amber(me.ccrama.redditslide.R.style.amber_LIGHT, "light_amber", me.ccrama.redditslide.R.color.md_amber_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Light.getValue()),
        amoled_amber(me.ccrama.redditslide.R.style.amber_AMOLED, "amoled_amber", me.ccrama.redditslide.R.color.md_amber_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.AMOLED.getValue()),
        blue_amber(me.ccrama.redditslide.R.style.amber_blue, "blue_amber", me.ccrama.redditslide.R.color.md_amber_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.DarkBlue.getValue()),
        amoled_light_amber(me.ccrama.redditslide.R.style.amber_AMOLED_lighter, "amoled_light_amber", me.ccrama.redditslide.R.color.md_amber_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.AMOLEDContrast.getValue()),
        dark_yellow(me.ccrama.redditslide.R.style.yellow_dark, "dark_yellow", me.ccrama.redditslide.R.color.md_yellow_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Dark.getValue()),
        light_yellow(me.ccrama.redditslide.R.style.yellow_LIGHT, "light_yellow", me.ccrama.redditslide.R.color.md_yellow_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Light.getValue()),
        amoled_yellow(me.ccrama.redditslide.R.style.yellow_AMOLED, "amoled_yellow", me.ccrama.redditslide.R.color.md_yellow_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.AMOLED.getValue()),
        blue_yellow(me.ccrama.redditslide.R.style.yellow_blue, "blue_yellow", me.ccrama.redditslide.R.color.md_yellow_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.DarkBlue.getValue()),
        amoled_light_yellow(me.ccrama.redditslide.R.style.yellow_AMOLED_lighter, "amoled_light_yellow", me.ccrama.redditslide.R.color.md_yellow_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.AMOLEDContrast.getValue()),
        dark_lime(me.ccrama.redditslide.R.style.lime_dark, "dark_lime", me.ccrama.redditslide.R.color.md_lime_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Dark.getValue()),
        light_lime(me.ccrama.redditslide.R.style.lime_LIGHT, "light_lime", me.ccrama.redditslide.R.color.md_lime_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Light.getValue()),
        amoled_lime(me.ccrama.redditslide.R.style.lime_AMOLED, "amoled_lime", me.ccrama.redditslide.R.color.md_lime_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.AMOLED.getValue()),
        blue_lime(me.ccrama.redditslide.R.style.lime_blue, "blue_lime", me.ccrama.redditslide.R.color.md_lime_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.DarkBlue.getValue()),
        amoled_light_lime(me.ccrama.redditslide.R.style.lime_AMOLED_lighter, "amoled_light_lime", me.ccrama.redditslide.R.color.md_lime_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.AMOLEDContrast.getValue()),
        dark_green(me.ccrama.redditslide.R.style.green_dark, "dark_green", me.ccrama.redditslide.R.color.md_green_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Dark.getValue()),
        light_green(me.ccrama.redditslide.R.style.green_LIGHT, "light_green", me.ccrama.redditslide.R.color.md_green_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Light.getValue()),
        amoled_green(me.ccrama.redditslide.R.style.green_AMOLED, "amoled_green", me.ccrama.redditslide.R.color.md_green_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.AMOLED.getValue()),
        blue_green(me.ccrama.redditslide.R.style.green_blue, "blue_green", me.ccrama.redditslide.R.color.md_green_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.DarkBlue.getValue()),
        amoled_light_green(me.ccrama.redditslide.R.style.green_AMOLED_lighter, "amoled_light_green", me.ccrama.redditslide.R.color.md_green_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.AMOLEDContrast.getValue()),
        dark_teal(me.ccrama.redditslide.R.style.teal_dark, "dark_teal", me.ccrama.redditslide.R.color.md_teal_A700, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Dark.getValue()),
        light_teal(me.ccrama.redditslide.R.style.teal_light, "light_teal", me.ccrama.redditslide.R.color.md_teal_A700, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Light.getValue()),
        amoled_teal(me.ccrama.redditslide.R.style.teal_amoled, "amoled_teal", me.ccrama.redditslide.R.color.md_teal_A700, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.AMOLED.getValue()),
        blue_teal(me.ccrama.redditslide.R.style.teal_blue, "blue_teal", me.ccrama.redditslide.R.color.md_teal_A700, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.DarkBlue.getValue()),
        amoled_light_teal(me.ccrama.redditslide.R.style.teal_AMOLED_lighter, "amoled_light_teal", me.ccrama.redditslide.R.color.md_teal_A700, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.AMOLEDContrast.getValue()),
        dark_cyan(me.ccrama.redditslide.R.style.cyan_dark, "dark_cyan", me.ccrama.redditslide.R.color.md_cyan_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Dark.getValue()),
        light_cyan(me.ccrama.redditslide.R.style.cyan_LIGHT, "light_cyan", me.ccrama.redditslide.R.color.md_cyan_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Light.getValue()),
        amoled_cyan(me.ccrama.redditslide.R.style.cyan_AMOLED, "amoled_cyan", me.ccrama.redditslide.R.color.md_cyan_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.AMOLED.getValue()),
        blue_cyan(me.ccrama.redditslide.R.style.cyan_blue, "blue_cyan", me.ccrama.redditslide.R.color.md_cyan_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.DarkBlue.getValue()),
        amoled_light_cyan(me.ccrama.redditslide.R.style.cyan_AMOLED_lighter, "amoled_light_cyan", me.ccrama.redditslide.R.color.md_cyan_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.AMOLEDContrast.getValue()),
        dark_lightblue(me.ccrama.redditslide.R.style.lightblue_dark, "dark_lightblue", me.ccrama.redditslide.R.color.md_light_blue_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Dark.getValue()),
        light_lightblue(me.ccrama.redditslide.R.style.lightblue_LIGHT, "light_lightblue", me.ccrama.redditslide.R.color.md_light_blue_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Light.getValue()),
        amoled_lightblue(me.ccrama.redditslide.R.style.lightblue_AMOLED, "amoled_lightblue", me.ccrama.redditslide.R.color.md_light_blue_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.AMOLED.getValue()),
        blue_lightblue(me.ccrama.redditslide.R.style.lightblue_blue, "blue_lightblue", me.ccrama.redditslide.R.color.md_light_blue_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.DarkBlue.getValue()),
        amoled_light_lightblue(me.ccrama.redditslide.R.style.lightblue_AMOLED_lighter, "amoled_light_lightblue", me.ccrama.redditslide.R.color.md_light_blue_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.AMOLEDContrast.getValue()),
        dark_blue(me.ccrama.redditslide.R.style.blue_dark, "dark_blue", me.ccrama.redditslide.R.color.md_blue_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Dark.getValue()),
        light_blue(me.ccrama.redditslide.R.style.blue_LIGHT, "light_blue", me.ccrama.redditslide.R.color.md_blue_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Light.getValue()),
        amoled_blue(me.ccrama.redditslide.R.style.blue_AMOLED, "amoled_blue", me.ccrama.redditslide.R.color.md_blue_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.AMOLED.getValue()),
        blue_blue(me.ccrama.redditslide.R.style.blue_blue, "blue_blue", me.ccrama.redditslide.R.color.md_blue_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.DarkBlue.getValue()),
        amoled_light_blue(me.ccrama.redditslide.R.style.blue_AMOLED_lighter, "amoled_light_blue", me.ccrama.redditslide.R.color.md_blue_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.AMOLEDContrast.getValue()),
        dark_indigo(me.ccrama.redditslide.R.style.indigo_dark, "dark_indigo", me.ccrama.redditslide.R.color.md_indigo_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Dark.getValue()),
        light_indigo(me.ccrama.redditslide.R.style.indigo_LIGHT, "light_indigo", me.ccrama.redditslide.R.color.md_indigo_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Light.getValue()),
        amoled_indigo(me.ccrama.redditslide.R.style.indigo_AMOLED, "amoled_indigo", me.ccrama.redditslide.R.color.md_indigo_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.AMOLED.getValue()),
        blue_indigo(me.ccrama.redditslide.R.style.indigo_blue, "blue_indigo", me.ccrama.redditslide.R.color.md_indigo_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.DarkBlue.getValue()),
        amoled_light_indigo(me.ccrama.redditslide.R.style.indigo_AMOLED_lighter, "amoled_light_indigo", me.ccrama.redditslide.R.color.md_indigo_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.AMOLEDContrast.getValue()),
        sepia_white(me.ccrama.redditslide.R.style.white_sepia, "sepia_white", me.ccrama.redditslide.R.color.md_blue_grey_200, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Sepia.getValue()),
        sepia_pink(me.ccrama.redditslide.R.style.pink_sepia, "sepia_pink", me.ccrama.redditslide.R.color.md_pink_A200, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Sepia.getValue()),
        sepia_deeporange(me.ccrama.redditslide.R.style.deeporange_sepia, "sepia_deeporange", me.ccrama.redditslide.R.color.md_deep_orange_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Sepia.getValue()),
        sepia_amber(me.ccrama.redditslide.R.style.amber_sepia, "sepia_amber", me.ccrama.redditslide.R.color.md_amber_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Sepia.getValue()),
        sepia_yellow(me.ccrama.redditslide.R.style.yellow_sepia, "sepia_yellow", me.ccrama.redditslide.R.color.md_yellow_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Sepia.getValue()),
        sepia_lime(me.ccrama.redditslide.R.style.lime_sepia, "sepia_lime", me.ccrama.redditslide.R.color.md_lime_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Sepia.getValue()),
        sepia_green(me.ccrama.redditslide.R.style.green_sepia, "sepia_green", me.ccrama.redditslide.R.color.md_green_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Sepia.getValue()),
        sepia_teal(me.ccrama.redditslide.R.style.teal_sepia, "sepia_teal", me.ccrama.redditslide.R.color.md_teal_A700, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Sepia.getValue()),
        sepia_cyan(me.ccrama.redditslide.R.style.cyan_sepia, "sepia_cyan", me.ccrama.redditslide.R.color.md_cyan_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Sepia.getValue()),
        sepia_lightblue(me.ccrama.redditslide.R.style.lightblue_sepia, "sepia_lightblue", me.ccrama.redditslide.R.color.md_light_blue_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Sepia.getValue()),
        sepia_blue(me.ccrama.redditslide.R.style.blue_sepia, "sepia_blue", me.ccrama.redditslide.R.color.md_blue_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Sepia.getValue()),
        sepia_indigo(me.ccrama.redditslide.R.style.indigo_sepia, "sepia_indigo", me.ccrama.redditslide.R.color.md_indigo_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Sepia.getValue()),
        night_red_white(me.ccrama.redditslide.R.style.white_night_red, "night_red_white", me.ccrama.redditslide.R.color.md_blue_grey_200, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.RedShift.getValue()),
        night_red_pink(me.ccrama.redditslide.R.style.pink_night_red, "night_red_pink", me.ccrama.redditslide.R.color.md_pink_A200, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.RedShift.getValue()),
        night_red_deeporange(me.ccrama.redditslide.R.style.deeporange_night_red, "night_red_deeporange", me.ccrama.redditslide.R.color.md_deep_orange_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.RedShift.getValue()),
        night_red_amber(me.ccrama.redditslide.R.style.amber_night_red, "night_red_amber", me.ccrama.redditslide.R.color.md_amber_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.RedShift.getValue()),
        night_red_yellow(me.ccrama.redditslide.R.style.yellow_night_red, "night_red_yellow", me.ccrama.redditslide.R.color.md_yellow_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.RedShift.getValue()),
        night_red_lime(me.ccrama.redditslide.R.style.lime_night_red, "night_red_lime", me.ccrama.redditslide.R.color.md_lime_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.RedShift.getValue()),
        night_red_green(me.ccrama.redditslide.R.style.green_night_red, "night_red_green", me.ccrama.redditslide.R.color.md_green_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.RedShift.getValue()),
        night_red_teal(me.ccrama.redditslide.R.style.teal_night_red, "night_red_teal", me.ccrama.redditslide.R.color.md_teal_A700, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.RedShift.getValue()),
        night_red_cyan(me.ccrama.redditslide.R.style.cyan_night_red, "night_red_cyan", me.ccrama.redditslide.R.color.md_cyan_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.RedShift.getValue()),
        night_red_lightblue(me.ccrama.redditslide.R.style.lightblue_night_red, "night_red_lightblue", me.ccrama.redditslide.R.color.md_light_blue_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.RedShift.getValue()),
        night_red_blue(me.ccrama.redditslide.R.style.blue_night_red, "night_red_blue", me.ccrama.redditslide.R.color.md_blue_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.RedShift.getValue()),
        night_red_indigo(me.ccrama.redditslide.R.style.indigo_night_red, "night_red_indigo", me.ccrama.redditslide.R.color.md_indigo_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.RedShift.getValue()),
        pixel_white(me.ccrama.redditslide.R.style.white_pixel, "pixel_white", me.ccrama.redditslide.R.color.md_blue_grey_200, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Pixel.getValue()),
        pixel_pink(me.ccrama.redditslide.R.style.pink_pixel, "pixel_pink", me.ccrama.redditslide.R.color.md_pink_A200, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Pixel.getValue()),
        pixel_deeporange(me.ccrama.redditslide.R.style.deeporange_pixel, "pixel_deeporange", me.ccrama.redditslide.R.color.md_deep_orange_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Pixel.getValue()),
        pixel_amber(me.ccrama.redditslide.R.style.amber_pixel, "pixel_amber", me.ccrama.redditslide.R.color.md_amber_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Pixel.getValue()),
        pixel_yellow(me.ccrama.redditslide.R.style.yellow_pixel, "pixel_yellow", me.ccrama.redditslide.R.color.md_yellow_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Pixel.getValue()),
        pixel_lime(me.ccrama.redditslide.R.style.lime_pixel, "pixel_lime", me.ccrama.redditslide.R.color.md_lime_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Pixel.getValue()),
        pixel_green(me.ccrama.redditslide.R.style.green_pixel, "pixel_green", me.ccrama.redditslide.R.color.md_green_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Pixel.getValue()),
        pixel_teal(me.ccrama.redditslide.R.style.teal_pixel, "pixel_teal", me.ccrama.redditslide.R.color.md_teal_A700, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Pixel.getValue()),
        pixel_cyan(me.ccrama.redditslide.R.style.cyan_pixel, "pixel_cyan", me.ccrama.redditslide.R.color.md_cyan_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Pixel.getValue()),
        pixel_lightblue(me.ccrama.redditslide.R.style.lightblue_pixel, "pixel_lightblue", me.ccrama.redditslide.R.color.md_light_blue_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Pixel.getValue()),
        pixel_blue(me.ccrama.redditslide.R.style.blue_pixel, "pixel_blue", me.ccrama.redditslide.R.color.md_blue_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Pixel.getValue()),
        pixel_indigo(me.ccrama.redditslide.R.style.indigo_pixel, "pixel_indigo", me.ccrama.redditslide.R.color.md_indigo_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Pixel.getValue()),
        deep_white(me.ccrama.redditslide.R.style.white_deep, "deep_white", me.ccrama.redditslide.R.color.md_blue_grey_200, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Deep.getValue()),
        deep_pink(me.ccrama.redditslide.R.style.pink_deep, "deep_pink", me.ccrama.redditslide.R.color.md_pink_A200, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Deep.getValue()),
        deep_deeporange(me.ccrama.redditslide.R.style.deeporange_deep, "deep_deeporange", me.ccrama.redditslide.R.color.md_deep_orange_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Deep.getValue()),
        deep_amber(me.ccrama.redditslide.R.style.amber_deep, "deep_amber", me.ccrama.redditslide.R.color.md_amber_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Deep.getValue()),
        deep_yellow(me.ccrama.redditslide.R.style.yellow_deep, "deep_yellow", me.ccrama.redditslide.R.color.md_yellow_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Deep.getValue()),
        deep_lime(me.ccrama.redditslide.R.style.lime_deep, "deep_lime", me.ccrama.redditslide.R.color.md_lime_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Deep.getValue()),
        deep_green(me.ccrama.redditslide.R.style.green_deep, "deep_green", me.ccrama.redditslide.R.color.md_green_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Deep.getValue()),
        deep_teal(me.ccrama.redditslide.R.style.teal_deep, "deep_teal", me.ccrama.redditslide.R.color.md_teal_A700, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Deep.getValue()),
        deep_cyan(me.ccrama.redditslide.R.style.cyan_deep, "deep_cyan", me.ccrama.redditslide.R.color.md_cyan_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Deep.getValue()),
        deep_lightblue(me.ccrama.redditslide.R.style.lightblue_deep, "deep_lightblue", me.ccrama.redditslide.R.color.md_light_blue_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Deep.getValue()),
        deep_blue(me.ccrama.redditslide.R.style.blue_deep, "deep_blue", me.ccrama.redditslide.R.color.md_blue_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Deep.getValue()),
        deep_indigo(me.ccrama.redditslide.R.style.indigo_deep, "deep_indigo", me.ccrama.redditslide.R.color.md_indigo_A400, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Deep.getValue());
        private int baseId;

        private java.lang.String title;

        private int themeType;

        private int color;

        Theme(int baseId, java.lang.String title, int color, int themetype) {
            this.baseId = baseId;
            this.color = color;
            this.themeType = themetype;
            this.title = title;
        }

        public int getBaseId() {
            return baseId;
        }

        public int getThemeType() {
            return themeType;
        }

        public java.lang.String getTitle() {
            return title;
        }

        public int getColor() {
            return color;
        }
    }

    public static java.util.List<android.util.Pair<java.lang.Integer, java.lang.Integer>> themePairList = new java.util.ArrayList<>(java.util.Arrays.asList(new android.util.Pair<>(me.ccrama.redditslide.R.id.dark, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Dark.getValue()), new android.util.Pair<>(me.ccrama.redditslide.R.id.light, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Light.getValue()), new android.util.Pair<>(me.ccrama.redditslide.R.id.amoled, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.AMOLED.getValue()), new android.util.Pair<>(me.ccrama.redditslide.R.id.blue, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.DarkBlue.getValue()), new android.util.Pair<>(me.ccrama.redditslide.R.id.amoled_contrast, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.AMOLEDContrast.getValue()), new android.util.Pair<>(me.ccrama.redditslide.R.id.sepia, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Sepia.getValue()), new android.util.Pair<>(me.ccrama.redditslide.R.id.red, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.RedShift.getValue()), new android.util.Pair<>(me.ccrama.redditslide.R.id.pixel, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Pixel.getValue()), new android.util.Pair<>(me.ccrama.redditslide.R.id.deep, me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Deep.getValue())));

    public enum ColorThemeOptions {

        Dark(0),
        Light(1),
        AMOLED(2),
        DarkBlue(3),
        AMOLEDContrast(4),
        Sepia(5),
        RedShift(6),
        Pixel(7),
        Deep(8);
        private final int mValue;

        ColorThemeOptions(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }
    }
}