package me.ccrama.redditslide.Activities;
import java.util.Locale;
import java.util.Set;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.SettingValues;
/**
 * Created by l3d00m on 11/13/2015.
 */
public class SettingsFilter extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    android.widget.EditText title;

    android.widget.EditText text;

    android.widget.EditText domain;

    android.widget.EditText subreddit;

    android.widget.EditText flair;

    android.widget.EditText user;

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_settings_filters);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.settings_title_filter, true, true);
        title = ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.title)));
        text = ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.text)));
        domain = ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.domain)));
        subreddit = ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.subreddit)));
        flair = ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.flair)));
        user = ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.user)));
        title.setOnEditorActionListener(makeOnEditorActionListener(me.ccrama.redditslide.SettingValues.titleFilters::add));
        text.setOnEditorActionListener(makeOnEditorActionListener(me.ccrama.redditslide.SettingValues.textFilters::add));
        domain.setOnEditorActionListener(makeOnEditorActionListener(me.ccrama.redditslide.SettingValues.domainFilters::add));
        subreddit.setOnEditorActionListener(makeOnEditorActionListener(me.ccrama.redditslide.SettingValues.subredditFilters::add));
        user.setOnEditorActionListener(makeOnEditorActionListener(me.ccrama.redditslide.SettingValues.userFilters::add));
        flair.setOnEditorActionListener((android.widget.TextView v,int actionId,android.view.KeyEvent event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                java.lang.String text = v.getText().toString().toLowerCase(java.util.Locale.ENGLISH).trim();
                if (text.matches(".+:.+")) {
                    me.ccrama.redditslide.SettingValues.flairFilters.add(text);
                    v.setText("");
                    updateFilters();
                }
            }
            return false;
        });
        updateFilters();
    }

    /**
     * Makes an OnEditorActionListener that calls filtersAdd when done is pressed
     *
     * @param filtersAdd
     * 		called when done is pressed
     * @return The new OnEditorActionListener
     */
    private android.widget.TextView.OnEditorActionListener makeOnEditorActionListener(android.support.v4.util.Consumer<java.lang.String> filtersAdd) {
        return new android.widget.TextView.OnEditorActionListener() {
            @java.lang.Override
            public boolean onEditorAction(android.widget.TextView v, int actionId, android.view.KeyEvent event) {
                if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                    java.lang.String text = v.getText().toString().toLowerCase(java.util.Locale.ENGLISH).trim();
                    if (!text.isEmpty()) {
                        filtersAdd.accept(text);
                        v.setText("");
                        updateFilters();
                    }
                }
                return false;
            }
        };
    }

    /**
     * Iterate through filters and add an item for each to the layout with id, with a remove button calling filtersRemoved
     *
     * @param id
     * 		ID of linearlayout containing items
     * @param filters
     * 		Set of filters to iterate through
     * @param filtersRemove
     * 		Method to call on remove button press
     */
    private void updateList(int id, java.util.Set<java.lang.String> filters, android.support.v4.util.Consumer<java.lang.String> filtersRemove) {
        ((android.widget.LinearLayout) (findViewById(id))).removeAllViews();
        for (java.lang.String s : filters) {
            final android.view.View t = getLayoutInflater().inflate(me.ccrama.redditslide.R.layout.account_textview, ((android.widget.LinearLayout) (findViewById(id))), false);
            ((android.widget.TextView) (t.findViewById(me.ccrama.redditslide.R.id.name))).setText(s);
            t.findViewById(me.ccrama.redditslide.R.id.remove).setOnClickListener((android.view.View v) -> {
                filtersRemove.accept(s);
                updateFilters();
            });
            ((android.widget.LinearLayout) (findViewById(id))).addView(t);
        }
    }

    /**
     * Updates the filters shown in the UI
     */
    public void updateFilters() {
        updateList(me.ccrama.redditslide.R.id.domainlist, me.ccrama.redditslide.SettingValues.domainFilters, me.ccrama.redditslide.SettingValues.domainFilters::remove);
        updateList(me.ccrama.redditslide.R.id.subredditlist, me.ccrama.redditslide.SettingValues.subredditFilters, me.ccrama.redditslide.SettingValues.subredditFilters::remove);
        updateList(me.ccrama.redditslide.R.id.userlist, me.ccrama.redditslide.SettingValues.userFilters, me.ccrama.redditslide.SettingValues.userFilters::remove);
        updateList(me.ccrama.redditslide.R.id.selftextlist, me.ccrama.redditslide.SettingValues.textFilters, me.ccrama.redditslide.SettingValues.textFilters::remove);
        updateList(me.ccrama.redditslide.R.id.titlelist, me.ccrama.redditslide.SettingValues.titleFilters, me.ccrama.redditslide.SettingValues.titleFilters::remove);
        ((android.widget.LinearLayout) (findViewById(me.ccrama.redditslide.R.id.flairlist))).removeAllViews();
        for (java.lang.String s : me.ccrama.redditslide.SettingValues.flairFilters) {
            final android.view.View t = getLayoutInflater().inflate(me.ccrama.redditslide.R.layout.account_textview, ((android.widget.LinearLayout) (findViewById(me.ccrama.redditslide.R.id.domainlist))), false);
            android.text.SpannableStringBuilder b = new android.text.SpannableStringBuilder();
            java.lang.String subname = s.split(":")[0];
            android.text.SpannableStringBuilder subreddit = new android.text.SpannableStringBuilder((" /r/" + subname) + " ");
            if (me.ccrama.redditslide.SettingValues.colorSubName && (me.ccrama.redditslide.Visuals.Palette.getColor(subname) != me.ccrama.redditslide.Visuals.Palette.getDefaultColor())) {
                subreddit.setSpan(new android.text.style.ForegroundColorSpan(me.ccrama.redditslide.Visuals.Palette.getColor(subname)), 0, subreddit.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                subreddit.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, subreddit.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            b.append(subreddit).append(s.split(":")[1]);
            ((android.widget.TextView) (t.findViewById(me.ccrama.redditslide.R.id.name))).setText(b);
            t.findViewById(me.ccrama.redditslide.R.id.remove).setOnClickListener((android.view.View v) -> {
                me.ccrama.redditslide.SettingValues.flairFilters.remove(s);
                updateFilters();
            });
            ((android.widget.LinearLayout) (findViewById(me.ccrama.redditslide.R.id.flairlist))).addView(t);
        }
    }

    @java.lang.Override
    public void onPause() {
        super.onPause();
        android.content.SharedPreferences.Editor e = me.ccrama.redditslide.SettingValues.prefs.edit();
        e.putStringSet(me.ccrama.redditslide.SettingValues.PREF_TITLE_FILTERS, me.ccrama.redditslide.SettingValues.titleFilters);
        e.putStringSet(me.ccrama.redditslide.SettingValues.PREF_DOMAIN_FILTERS, me.ccrama.redditslide.SettingValues.domainFilters);
        e.putStringSet(me.ccrama.redditslide.SettingValues.PREF_TEXT_FILTERS, me.ccrama.redditslide.SettingValues.textFilters);
        e.putStringSet(me.ccrama.redditslide.SettingValues.PREF_SUBREDDIT_FILTERS, me.ccrama.redditslide.SettingValues.subredditFilters);
        e.putStringSet(me.ccrama.redditslide.SettingValues.PREF_FLAIR_FILTERS, me.ccrama.redditslide.SettingValues.flairFilters);
        e.putStringSet(me.ccrama.redditslide.SettingValues.PREF_USER_FILTERS, me.ccrama.redditslide.SettingValues.userFilters);
        e.apply();
    }
}