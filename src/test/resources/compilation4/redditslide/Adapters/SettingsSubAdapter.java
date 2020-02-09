package me.ccrama.redditslide.Adapters;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Visuals.Palette;
import java.util.ArrayList;
import me.ccrama.redditslide.ColorPreferences;
import me.ccrama.redditslide.Activities.MainActivity;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Activities.SubredditView;
import me.ccrama.redditslide.Activities.SettingsSubreddit;
/**
 * Created by ccrama on 8/17/2015.
 */
public class SettingsSubAdapter extends android.support.v7.widget.RecyclerView.Adapter<me.ccrama.redditslide.Adapters.SettingsSubAdapter.ViewHolder> {
    private final java.util.ArrayList<java.lang.String> objects;

    private android.app.Activity context;

    public SettingsSubAdapter(android.app.Activity context, java.util.ArrayList<java.lang.String> objects) {
        this.objects = objects;
        this.context = context;
    }

    @java.lang.Override
    public me.ccrama.redditslide.Adapters.SettingsSubAdapter.ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        android.view.View v = android.view.LayoutInflater.from(parent.getContext()).inflate(me.ccrama.redditslide.R.layout.subforsublisteditor, parent, false);
        return new me.ccrama.redditslide.Adapters.SettingsSubAdapter.ViewHolder(v);
    }

    @java.lang.Override
    public void onBindViewHolder(me.ccrama.redditslide.Adapters.SettingsSubAdapter.ViewHolder holder, int position) {
        android.view.View convertView = holder.itemView;
        final android.widget.TextView t = convertView.findViewById(me.ccrama.redditslide.R.id.name);
        t.setText(objects.get(position));
        final java.lang.String subreddit = objects.get(position);
        convertView.findViewById(me.ccrama.redditslide.R.id.color).setBackgroundResource(me.ccrama.redditslide.R.drawable.circle);
        convertView.findViewById(me.ccrama.redditslide.R.id.color).getBackground().setColorFilter(me.ccrama.redditslide.Visuals.Palette.getColor(subreddit), android.graphics.PorterDuff.Mode.MULTIPLY);
        final java.lang.String DELETE_SUB_SETTINGS_TITLE = (subreddit.contains("/m/")) ? subreddit : "/r/" + subreddit;
        convertView.findViewById(me.ccrama.redditslide.R.id.remove).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(context).setTitle(context.getString(me.ccrama.redditslide.R.string.settings_delete_sub_settings, DELETE_SUB_SETTINGS_TITLE)).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        me.ccrama.redditslide.Visuals.Palette.removeColor(subreddit);
                        // Remove layout settings
                        me.ccrama.redditslide.SettingValues.prefs.edit().remove(me.ccrama.redditslide.Reddit.PREF_LAYOUT + subreddit).apply();
                        // Remove accent / font color settings
                        new me.ccrama.redditslide.ColorPreferences(context).removeFontStyle(subreddit);
                        me.ccrama.redditslide.SettingValues.resetPicsEnabled(subreddit);
                        me.ccrama.redditslide.SettingValues.resetSelftextEnabled(subreddit);
                        dialog.dismiss();
                        objects.remove(subreddit);
                        notifyDataSetChanged();
                    }
                }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        dialog.dismiss();
                        dialog.dismiss();
                    }
                }).show();
            }
        });
        convertView.findViewById(me.ccrama.redditslide.R.id.edit).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                prepareAndShowSubEditor(subreddit);
            }
        });
    }

    @java.lang.Override
    public int getItemCount() {
        return objects.size();
    }

    public class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public ViewHolder(android.view.View itemView) {
            super(itemView);
        }
    }

    /**
     * Displays the subreddit color chooser
     * It is possible to color multiple subreddits at the same time
     *
     * @param subreddits
     * 		Subreddits as an array
     * @param context
     * 		Context for getting colors
     * @param dialoglayout
     * 		The subchooser layout (R.layout.colorsub)
     */
    public static void showSubThemeEditor(final java.util.ArrayList<java.lang.String> subreddits, final android.app.Activity context, android.view.View dialoglayout) {
        if (subreddits.isEmpty()) {
            return;
        }
        final boolean multipleSubs = subreddits.size() > 1;
        boolean isAlternateLayout;
        int currentColor;
        int currentAccentColor;
        final me.ccrama.redditslide.ColorPreferences colorPrefs = new me.ccrama.redditslide.ColorPreferences(context);
        final java.lang.String subreddit = (multipleSubs) ? null : subreddits.get(0);
        final android.support.v7.widget.SwitchCompat bigPics = dialoglayout.findViewById(me.ccrama.redditslide.R.id.bigpics);
        final android.support.v7.widget.SwitchCompat selftext = dialoglayout.findViewById(me.ccrama.redditslide.R.id.selftext);
        // Selected multiple subreddits
        if (multipleSubs) {
            // Check if all selected subs have the same settings
            int previousSubColor = 0;
            int previousSubAccent = 0;
            bigPics.setChecked(me.ccrama.redditslide.SettingValues.bigPicEnabled);
            selftext.setChecked(me.ccrama.redditslide.SettingValues.cardText);
            boolean sameMainColor = true;
            boolean sameAccentColor = true;
            for (java.lang.String sub : subreddits) {
                int currentSubColor = me.ccrama.redditslide.Visuals.Palette.getColor(sub);
                int currentSubAccent = colorPrefs.getColor("");
                if ((previousSubColor != 0) && (previousSubAccent != 0)) {
                    if (currentSubColor != previousSubColor) {
                        sameMainColor = false;
                    } else if (currentSubAccent != previousSubAccent) {
                        sameAccentColor = false;
                    }
                }
                if ((!sameMainColor) && (!sameAccentColor)) {
                    break;
                }
                previousSubAccent = currentSubAccent;
                previousSubColor = currentSubColor;
            }
            currentColor = me.ccrama.redditslide.Visuals.Palette.getDefaultColor();
            currentAccentColor = colorPrefs.getColor("");
            isAlternateLayout = false;
            // If all selected subs have the same settings, display them
            if (sameMainColor) {
                currentColor = previousSubColor;
            }
            if (sameAccentColor) {
                currentAccentColor = previousSubAccent;
            }
        } else {
            // Is only one selected sub
            currentColor = me.ccrama.redditslide.Visuals.Palette.getColor(subreddit);
            isAlternateLayout = me.ccrama.redditslide.SettingValues.prefs.contains(me.ccrama.redditslide.Reddit.PREF_LAYOUT + subreddit);
            currentAccentColor = colorPrefs.getColor(subreddit);
            bigPics.setChecked(me.ccrama.redditslide.SettingValues.isPicsEnabled(subreddit));
            selftext.setChecked(me.ccrama.redditslide.SettingValues.isSelftextEnabled(subreddit));
        }
        com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(context);
        final android.widget.TextView title = dialoglayout.findViewById(me.ccrama.redditslide.R.id.title);
        title.setBackgroundColor(currentColor);
        if (multipleSubs) {
            java.lang.String titleString = "";
            for (java.lang.String sub : subreddits) {
                // if the subreddit is the frontpage, don't put "/r/" in front of it
                if (sub.equals("frontpage")) {
                    titleString += sub + ", ";
                } else if (sub.contains("/m/")) {
                    titleString += sub + ", ";
                } else {
                    titleString += ("/r/" + sub) + ", ";
                }
            }
            titleString = titleString.substring(0, titleString.length() - 2);
            title.setMaxLines(3);
            title.setText(titleString);
        } else if (subreddit.contains("/m/")) {
            title.setText(subreddit);
        } else {
            // if the subreddit is the frontpage, don't put "/r/" in front of it
            title.setText(subreddit.equals("frontpage") ? "frontpage" : "/r/" + subreddit);
        }
        {
            // Primary color pickers
            final uz.shift.colorpicker.LineColorPicker colorPickerPrimary = dialoglayout.findViewById(me.ccrama.redditslide.R.id.picker);
            // shades of primary colors
            final uz.shift.colorpicker.LineColorPicker colorPickerPrimaryShades = dialoglayout.findViewById(me.ccrama.redditslide.R.id.picker2);
            colorPickerPrimary.setColors(me.ccrama.redditslide.ColorPreferences.getBaseColors(context));
            // Iterate through all colors and check if it matches the current color of the sub, then select it
            for (int i : colorPickerPrimary.getColors()) {
                for (int i2 : me.ccrama.redditslide.ColorPreferences.getColors(context, i)) {
                    if (i2 == currentColor) {
                        colorPickerPrimary.setSelectedColor(i);
                        colorPickerPrimaryShades.setColors(me.ccrama.redditslide.ColorPreferences.getColors(context, i));
                        colorPickerPrimaryShades.setSelectedColor(i2);
                        break;
                    }
                }
            }
            // Base color changed
            colorPickerPrimary.setOnColorChangedListener(new uz.shift.colorpicker.OnColorChangedListener() {
                @java.lang.Override
                public void onColorChanged(int c) {
                    // Show variations of the base color
                    colorPickerPrimaryShades.setColors(me.ccrama.redditslide.ColorPreferences.getColors(context, c));
                    colorPickerPrimaryShades.setSelectedColor(c);
                }
            });
            colorPickerPrimaryShades.setOnColorChangedListener(new uz.shift.colorpicker.OnColorChangedListener() {
                @java.lang.Override
                public void onColorChanged(int i) {
                    if (context instanceof me.ccrama.redditslide.Activities.MainActivity) {
                        ((me.ccrama.redditslide.Activities.MainActivity) (context)).updateColor(colorPickerPrimaryShades.getColor(), subreddit);
                    }
                    title.setBackgroundColor(colorPickerPrimaryShades.getColor());
                }
            });
            {
                /* TODO   TextView dialogButton = (TextView) dialoglayout.findViewById(R.id.reset);

                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Palette.removeColor(subreddit);
                hea.setBackgroundColor(Palette.getDefaultColor());
                findViewById(R.id.header).setBackgroundColor(Palette.getDefaultColor());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.setStatusBarColor(Palette.getDarkerColor(Palette.getDefaultColor()));
                context.setTaskDescription(new ActivityManager.TaskDescription(subreddit, ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_launcher)).getBitmap(), colorPicker2.getColor()));

                }
                title.setBackgroundColor(Palette.getDefaultColor());


                int cx = center.getWidth() / 2;
                int cy = center.getHeight() / 2;

                int initialRadius = body.getWidth();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                Animator anim =
                ViewAnimationUtils.createCircularReveal(body, cx, cy, initialRadius, 0);

                anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                body.setVisibility(View.GONE);
                }
                });
                anim.start();

                } else {
                body.setVisibility(View.GONE);

                }

                }
                });
                 */
            }
            // Accent color picker
            final uz.shift.colorpicker.LineColorPicker colorPickerAcc = dialoglayout.findViewById(me.ccrama.redditslide.R.id.picker3);
            {
                // Get all possible accent colors (for day theme)
                int[] arrs = new int[me.ccrama.redditslide.ColorPreferences.getNumColorsFromThemeType(0)];
                int i = 0;
                for (me.ccrama.redditslide.ColorPreferences.Theme type : me.ccrama.redditslide.ColorPreferences.Theme.values()) {
                    if (type.getThemeType() == me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Dark.getValue()) {
                        arrs[i] = android.support.v4.content.ContextCompat.getColor(context, type.getColor());
                        i++;
                    }
                    colorPickerAcc.setColors(arrs);
                    colorPickerAcc.setSelectedColor(currentAccentColor);
                }
            }
            builder.setView(dialoglayout);
            builder.setCancelable(false);
            builder.setNegativeButton(me.ccrama.redditslide.R.string.btn_cancel, new android.content.DialogInterface.OnClickListener() {
                @java.lang.Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    if (context instanceof me.ccrama.redditslide.Activities.MainActivity) {
                        ((me.ccrama.redditslide.Activities.MainActivity) (context)).updateColor(me.ccrama.redditslide.Visuals.Palette.getColor(subreddit), subreddit);
                    }
                }
            }).setNeutralButton(me.ccrama.redditslide.R.string.btn_reset, new android.content.DialogInterface.OnClickListener() {
                @java.lang.Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    java.lang.String subTitles = "";
                    if (multipleSubs) {
                        for (java.lang.String sub : subreddits) {
                            // if the subreddit is the frontpage, don't put "/r/" in front of it
                            if (sub.equals("frontpage")) {
                                subTitles += sub + ", ";
                            } else {
                                subTitles += ("/r/" + sub) + ", ";
                            }
                        }
                        subTitles = subTitles.substring(0, subTitles.length() - 2);
                    } else {
                        // if the subreddit is the frontpage, don't put "/r/" in front of it
                        subTitles = (subreddit.equals("frontpage")) ? "frontpage" : "/r/" + subreddit;
                    }
                    java.lang.String titleStart = context.getString(me.ccrama.redditslide.R.string.settings_delete_sub_settings, subTitles);
                    titleStart = titleStart.replace("/r//r/", "/r/");
                    if (titleStart.contains("/r/frontpage")) {
                        titleStart = titleStart.replace("/r/frontpage", "frontpage");
                    }
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(context).setTitle(titleStart).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            for (java.lang.String sub : subreddits) {
                                me.ccrama.redditslide.Visuals.Palette.removeColor(sub);
                                // Remove layout settings
                                me.ccrama.redditslide.SettingValues.prefs.edit().remove(me.ccrama.redditslide.Reddit.PREF_LAYOUT + sub).apply();
                                // Remove accent / font color settings
                                new me.ccrama.redditslide.ColorPreferences(context).removeFontStyle(sub);
                                me.ccrama.redditslide.SettingValues.resetPicsEnabled(sub);
                                me.ccrama.redditslide.SettingValues.resetSelftextEnabled(sub);
                            }
                            if (context instanceof me.ccrama.redditslide.Activities.MainActivity) {
                                ((me.ccrama.redditslide.Activities.MainActivity) (context)).reloadSubs();
                            } else if (context instanceof me.ccrama.redditslide.Activities.SettingsSubreddit) {
                                ((me.ccrama.redditslide.Activities.SettingsSubreddit) (context)).reloadSubList();
                            } else if (context instanceof me.ccrama.redditslide.Activities.SubredditView) {
                                ((me.ccrama.redditslide.Activities.SubredditView) (context)).restartTheme();
                            }
                        }
                    }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, null).show();
                }
            }).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                @java.lang.Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    final int newPrimaryColor = colorPickerPrimaryShades.getColor();
                    final int newAccentColor = colorPickerAcc.getColor();
                    for (java.lang.String sub : subreddits) {
                        // Set main color
                        if (bigPics.isChecked() != me.ccrama.redditslide.SettingValues.isPicsEnabled(sub)) {
                            me.ccrama.redditslide.SettingValues.setPicsEnabled(sub, bigPics.isChecked());
                        }
                        if (selftext.isChecked() != me.ccrama.redditslide.SettingValues.isSelftextEnabled(sub)) {
                            me.ccrama.redditslide.SettingValues.setSelftextEnabled(sub, selftext.isChecked());
                        }
                        // Only do set colors if either subreddit theme color has changed
                        if ((me.ccrama.redditslide.Visuals.Palette.getColor(sub) != newPrimaryColor) || (me.ccrama.redditslide.Visuals.Palette.getDarkerColor(sub) != newAccentColor)) {
                            if (newPrimaryColor != me.ccrama.redditslide.Visuals.Palette.getDefaultColor()) {
                                me.ccrama.redditslide.Visuals.Palette.setColor(sub, newPrimaryColor);
                            } else {
                                me.ccrama.redditslide.Visuals.Palette.removeColor(sub);
                            }
                            // Set accent color
                            me.ccrama.redditslide.ColorPreferences.Theme t = null;
                            // Do not save accent color if it matches the default accent color
                            if ((newAccentColor != android.support.v4.content.ContextCompat.getColor(context, colorPrefs.getFontStyle().getColor())) || (newAccentColor != android.support.v4.content.ContextCompat.getColor(context, colorPrefs.getFontStyleSubreddit(sub).getColor()))) {
                                me.ccrama.redditslide.util.LogUtil.v("Accent colors not equal");
                                int back = new me.ccrama.redditslide.ColorPreferences(context).getFontStyle().getThemeType();
                                for (me.ccrama.redditslide.ColorPreferences.Theme type : me.ccrama.redditslide.ColorPreferences.Theme.values()) {
                                    if ((android.support.v4.content.ContextCompat.getColor(context, type.getColor()) == newAccentColor) && (back == type.getThemeType())) {
                                        t = type;
                                        me.ccrama.redditslide.util.LogUtil.v("Setting accent color to " + t.getTitle());
                                        break;
                                    }
                                }
                            } else {
                                new me.ccrama.redditslide.ColorPreferences(context).removeFontStyle(sub);
                            }
                            if (t != null) {
                                colorPrefs.setFontStyle(t, sub);
                            }
                        }
                        // Set layout
                        me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.Reddit.PREF_LAYOUT + sub, true).apply();
                    }
                    // Only refresh stuff if the user changed something
                    if ((me.ccrama.redditslide.Visuals.Palette.getColor(subreddit) != newPrimaryColor) || (me.ccrama.redditslide.Visuals.Palette.getDarkerColor(subreddit) != newAccentColor)) {
                        if (context instanceof me.ccrama.redditslide.Activities.MainActivity) {
                            ((me.ccrama.redditslide.Activities.MainActivity) (context)).reloadSubs();
                        } else if (context instanceof me.ccrama.redditslide.Activities.SettingsSubreddit) {
                            ((me.ccrama.redditslide.Activities.SettingsSubreddit) (context)).reloadSubList();
                        } else if (context instanceof me.ccrama.redditslide.Activities.SubredditView) {
                            ((me.ccrama.redditslide.Activities.SubredditView) (context)).restartTheme();
                        }
                    }
                }
            }).show();
        }
    }

    public void prepareAndShowSubEditor(java.util.ArrayList<java.lang.String> subreddits) {
        if (subreddits.size() == 1)
            prepareAndShowSubEditor(subreddits.get(0));
        else if (subreddits.size() > 1) {
            android.view.LayoutInflater localInflater = context.getLayoutInflater();
            final android.view.View dialoglayout = localInflater.inflate(me.ccrama.redditslide.R.layout.colorsub, null);
            me.ccrama.redditslide.Adapters.SettingsSubAdapter.showSubThemeEditor(subreddits, context, dialoglayout);
        }
    }

    private void prepareAndShowSubEditor(java.lang.String subreddit) {
        int style = new me.ccrama.redditslide.ColorPreferences(context).getThemeSubreddit(subreddit);
        final android.content.Context contextThemeWrapper = new android.support.v7.view.ContextThemeWrapper(context, style);
        android.view.LayoutInflater localInflater = context.getLayoutInflater().cloneInContext(contextThemeWrapper);
        final android.view.View dialoglayout = localInflater.inflate(me.ccrama.redditslide.R.layout.colorsub, null);
        java.util.ArrayList<java.lang.String> arrayList = new java.util.ArrayList<>();
        arrayList.add(subreddit);
        me.ccrama.redditslide.Adapters.SettingsSubAdapter.showSubThemeEditor(arrayList, context, dialoglayout);
    }
}