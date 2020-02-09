package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Visuals.FontPreferences;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.ColorPreferences;
/**
 * Created by ccrama on 3/5/2015.
 */
public class Tutorial extends android.support.v7.app.AppCompatActivity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 2;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private android.support.v4.view.ViewPager mPager;

    @java.lang.Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(new me.ccrama.redditslide.Visuals.FontPreferences(this).getCommentFontStyle().getResId(), true);
        getTheme().applyStyle(new me.ccrama.redditslide.Visuals.FontPreferences(this).getPostFontStyle().getResId(), true);
        getTheme().applyStyle(new me.ccrama.redditslide.ColorPreferences(this).getFontStyle().getBaseId(), true);
        setContentView(me.ccrama.redditslide.R.layout.activity_tutorial);
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = findViewById(me.ccrama.redditslide.R.id.vp);
        /* The pager adapter, which provides the pages to the view pager widget. */
        android.support.v4.view.PagerAdapter mPagerAdapter = new me.ccrama.redditslide.Activities.Tutorial.ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        if (getIntent().hasExtra("page")) {
            mPager.setCurrentItem(1);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            android.view.Window window = this.getWindow();
            window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(android.graphics.Color.parseColor("#FF5252")));
        }
    }

    @java.lang.Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    public static class Welcome extends android.support.v4.app.Fragment {
        @java.lang.Override
        public void onResume() {
            super.onResume();
        }

        @java.lang.Override
        public android.view.View onCreateView(android.view.LayoutInflater inflater, final android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
            android.view.View v = inflater.inflate(me.ccrama.redditslide.R.layout.fragment_welcome, container, false);
            v.findViewById(me.ccrama.redditslide.R.id.next).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    ((me.ccrama.redditslide.Activities.Tutorial) (getActivity())).mPager.setCurrentItem(1);
                }
            });
            return v;
        }
    }

    int back;

    public static class Personalize extends android.support.v4.app.Fragment {
        @java.lang.Override
        public void onResume() {
            super.onResume();
        }

        @java.lang.Override
        public android.view.View onCreateView(android.view.LayoutInflater inflater, final android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
            ((me.ccrama.redditslide.Activities.Tutorial) (getActivity())).back = new me.ccrama.redditslide.ColorPreferences(getContext()).getFontStyle().getThemeType();
            android.view.View v = inflater.inflate(me.ccrama.redditslide.R.layout.fragment_basicinfo, container, false);
            final android.view.View header = v.findViewById(me.ccrama.redditslide.R.id.header);
            ((android.widget.ImageView) (v.findViewById(me.ccrama.redditslide.R.id.tint_accent))).setColorFilter(getActivity().getResources().getColor(new me.ccrama.redditslide.ColorPreferences(getContext()).getFontStyle().getColor()));
            ((android.widget.ImageView) (v.findViewById(me.ccrama.redditslide.R.id.tint_primary))).setColorFilter(me.ccrama.redditslide.Visuals.Palette.getDefaultColor());
            header.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getDefaultColor());
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                android.view.Window window = getActivity().getWindow();
                window.setStatusBarColor(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(me.ccrama.redditslide.Visuals.Palette.getDefaultColor()));
            }
            v.findViewById(me.ccrama.redditslide.R.id.primary).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    android.view.LayoutInflater inflater = getActivity().getLayoutInflater();
                    final android.view.View dialoglayout = inflater.inflate(me.ccrama.redditslide.R.layout.choosemain, null);
                    com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(getContext());
                    final android.widget.TextView title = dialoglayout.findViewById(me.ccrama.redditslide.R.id.title);
                    title.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getDefaultColor());
                    uz.shift.colorpicker.LineColorPicker colorPicker = dialoglayout.findViewById(me.ccrama.redditslide.R.id.picker);
                    final uz.shift.colorpicker.LineColorPicker colorPicker2 = dialoglayout.findViewById(me.ccrama.redditslide.R.id.picker2);
                    colorPicker.setColors(me.ccrama.redditslide.ColorPreferences.getBaseColors(getContext()));
                    int currentColor = me.ccrama.redditslide.Visuals.Palette.getDefaultColor();
                    for (int i : colorPicker.getColors()) {
                        for (int i2 : me.ccrama.redditslide.ColorPreferences.getColors(getContext(), i)) {
                            if (i2 == currentColor) {
                                colorPicker.setSelectedColor(i);
                                colorPicker2.setColors(me.ccrama.redditslide.ColorPreferences.getColors(getContext(), i));
                                colorPicker2.setSelectedColor(i2);
                                break;
                            }
                        }
                    }
                    colorPicker.setOnColorChangedListener(new uz.shift.colorpicker.OnColorChangedListener() {
                        @java.lang.Override
                        public void onColorChanged(int c) {
                            colorPicker2.setColors(me.ccrama.redditslide.ColorPreferences.getColors(getContext(), c));
                            colorPicker2.setSelectedColor(c);
                        }
                    });
                    colorPicker2.setOnColorChangedListener(new uz.shift.colorpicker.OnColorChangedListener() {
                        @java.lang.Override
                        public void onColorChanged(int i) {
                            title.setBackgroundColor(colorPicker2.getColor());
                            header.setBackgroundColor(colorPicker2.getColor());
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                android.view.Window window = getActivity().getWindow();
                                window.setStatusBarColor(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(colorPicker2.getColor()));
                            }
                        }
                    });
                    dialoglayout.findViewById(me.ccrama.redditslide.R.id.ok).setOnClickListener(new android.view.View.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.view.View v) {
                            me.ccrama.redditslide.Reddit.colors.edit().putInt("DEFAULTCOLOR", colorPicker2.getColor()).apply();
                            android.content.Intent i = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.Tutorial.class);
                            i.putExtra("page", 1);
                            i.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(i);
                            getActivity().overridePendingTransition(0, 0);
                            getActivity().finish();
                            getActivity().overridePendingTransition(0, 0);
                        }
                    });
                    builder.setView(dialoglayout);
                    builder.show();
                }
            });
            v.findViewById(me.ccrama.redditslide.R.id.secondary).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    android.view.LayoutInflater inflater = getActivity().getLayoutInflater();
                    final android.view.View dialoglayout = inflater.inflate(me.ccrama.redditslide.R.layout.chooseaccent, null);
                    com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(getActivity());
                    final android.widget.TextView title = dialoglayout.findViewById(me.ccrama.redditslide.R.id.title);
                    title.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getDefaultColor());
                    final uz.shift.colorpicker.LineColorPicker colorPicker = dialoglayout.findViewById(me.ccrama.redditslide.R.id.picker3);
                    int[] arrs = new int[me.ccrama.redditslide.ColorPreferences.getNumColorsFromThemeType(0)];
                    int i = 0;
                    for (me.ccrama.redditslide.ColorPreferences.Theme type : me.ccrama.redditslide.ColorPreferences.Theme.values()) {
                        if (type.getThemeType() == me.ccrama.redditslide.ColorPreferences.ColorThemeOptions.Dark.getValue()) {
                            arrs[i] = android.support.v4.content.ContextCompat.getColor(getActivity(), type.getColor());
                            i++;
                        }
                    }
                    colorPicker.setColors(arrs);
                    colorPicker.setSelectedColor(new me.ccrama.redditslide.ColorPreferences(getActivity()).getColor(""));
                    dialoglayout.findViewById(me.ccrama.redditslide.R.id.ok).setOnClickListener(new android.view.View.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.view.View v) {
                            int color = colorPicker.getColor();
                            me.ccrama.redditslide.ColorPreferences.Theme t = null;
                            for (me.ccrama.redditslide.ColorPreferences.Theme type : me.ccrama.redditslide.ColorPreferences.Theme.values()) {
                                if ((android.support.v4.content.ContextCompat.getColor(getActivity(), type.getColor()) == color) && (((me.ccrama.redditslide.Activities.Tutorial) (getActivity())).back == type.getThemeType())) {
                                    t = type;
                                    break;
                                }
                            }
                            new me.ccrama.redditslide.ColorPreferences(getActivity()).setFontStyle(t);
                            android.content.Intent i = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.Tutorial.class);
                            i.putExtra("page", 1);
                            i.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(i);
                            getActivity().overridePendingTransition(0, 0);
                            getActivity().finish();
                            getActivity().overridePendingTransition(0, 0);
                        }
                    });
                    builder.setView(dialoglayout);
                    builder.show();
                }
            });
            v.findViewById(me.ccrama.redditslide.R.id.base).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    android.view.LayoutInflater inflater = getActivity().getLayoutInflater();
                    final android.view.View dialoglayout = inflater.inflate(me.ccrama.redditslide.R.layout.choosethemesmall, null);
                    com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(getActivity());
                    final android.widget.TextView title = dialoglayout.findViewById(me.ccrama.redditslide.R.id.title);
                    title.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getDefaultColor());
                    for (final android.util.Pair<java.lang.Integer, java.lang.Integer> pair : me.ccrama.redditslide.ColorPreferences.themePairList) {
                        dialoglayout.findViewById(pair.first).setOnClickListener(new android.view.View.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.view.View v) {
                                java.lang.String[] names = new me.ccrama.redditslide.ColorPreferences(getActivity()).getFontStyle().getTitle().split("_");
                                java.lang.String name = names[names.length - 1];
                                final java.lang.String newName = name.replace("(", "");
                                for (me.ccrama.redditslide.ColorPreferences.Theme theme : me.ccrama.redditslide.ColorPreferences.Theme.values()) {
                                    if (theme.toString().contains(newName) && (theme.getThemeType() == pair.second)) {
                                        ((me.ccrama.redditslide.Activities.Tutorial) (getActivity())).back = theme.getThemeType();
                                        new me.ccrama.redditslide.ColorPreferences(getActivity()).setFontStyle(theme);
                                        android.content.Intent i = new android.content.Intent(getActivity(), me.ccrama.redditslide.Activities.Tutorial.class);
                                        i.putExtra("page", 1);
                                        i.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(i);
                                        getActivity().overridePendingTransition(0, 0);
                                        getActivity().finish();
                                        getActivity().overridePendingTransition(0, 0);
                                        break;
                                    }
                                }
                            }
                        });
                    }
                    builder.setView(dialoglayout);
                    builder.show();
                }
            });
            v.findViewById(me.ccrama.redditslide.R.id.next).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    me.ccrama.redditslide.Reddit.colors.edit().putString("Tutorial", "S").commit();
                    me.ccrama.redditslide.Reddit.appRestart.edit().putString("startScreen", "a").apply();
                    me.ccrama.redditslide.Reddit.forceRestart(getActivity(), false);
                }
            });
            return v;
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @java.lang.Override
        public android.support.v4.app.Fragment getItem(int position) {
            if (position == 0) {
                return new me.ccrama.redditslide.Activities.Tutorial.Welcome();
            } else {
                return new me.ccrama.redditslide.Activities.Tutorial.Personalize();
            }
        }

        @java.lang.Override
        public int getCount() {
            return me.ccrama.redditslide.Activities.Tutorial.NUM_PAGES;
        }
    }
}