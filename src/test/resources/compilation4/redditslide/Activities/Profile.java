package me.ccrama.redditslide.Activities;
import java.util.Locale;
import java.util.HashMap;
import me.ccrama.redditslide.Visuals.Palette;
import java.util.ArrayList;
import me.ccrama.redditslide.ColorPreferences;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.TimeUtils;
import me.ccrama.redditslide.util.SortingUtil;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.util.LinkUtil;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.Fragments.ContributionsView;
import java.util.List;
import java.util.Map;
import me.ccrama.redditslide.UserTags;
import me.ccrama.redditslide.Fragments.HistoryView;
/**
 * Created by ccrama on 9/17/2015.
 */
public class Profile extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    public static final java.lang.String EXTRA_PROFILE = "profile";

    public static final java.lang.String EXTRA_SAVED = "saved";

    public static final java.lang.String EXTRA_COMMENT = "comment";

    public static final java.lang.String EXTRA_SUBMIT = "submitted";

    public static final java.lang.String EXTRA_UPVOTE = "upvoted";

    public static final java.lang.String EXTRA_HISTORY = "history";

    private java.lang.String name;

    private net.dean.jraw.models.Account account;

    private java.util.List<net.dean.jraw.models.Trophy> trophyCase;

    private android.support.v4.view.ViewPager pager;

    private android.support.design.widget.TabLayout tabs;

    private java.lang.String[] usedArray;

    public boolean isSavedView;

    private void scrollToTabAfterLayout(final int tabIndex) {
        // from http://stackoverflow.com/a/34780589/3697225
        if (tabs != null) {
            final android.view.ViewTreeObserver observer = tabs.getViewTreeObserver();
            if (observer.isAlive()) {
                observer.dispatchOnGlobalLayout();// In case a previous call is waiting when this call is made

                observer.addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {
                    @java.lang.Override
                    public void onGlobalLayout() {
                        tabs.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        tabs.getTabAt(tabIndex).select();
                    }
                });
            }
        }
    }

    public static boolean isValidUsername(java.lang.String user) {
        /* https://github.com/reddit/reddit/blob/master/r2/r2/lib/validator/validator.py#L261 */
        return user.matches("^[a-zA-Z0-9_-]{3,20}$");
    }

    private boolean friend;

    private android.view.MenuItem sortItem;

    private android.view.MenuItem categoryItem;

    public static net.dean.jraw.paginators.Sorting profSort;

    public static net.dean.jraw.paginators.TimePeriod profTime;

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstance) {
        overrideSwipeFromAnywhere();
        super.onCreate(savedInstance);
        name = getIntent().getExtras().getString(me.ccrama.redditslide.Activities.Profile.EXTRA_PROFILE, "");
        setShareUrl("https://reddit.com/u/" + name);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_profile);
        setupUserAppBar(me.ccrama.redditslide.R.id.toolbar, name, true, name);
        mToolbar.setPopupTheme(new me.ccrama.redditslide.ColorPreferences(this).getFontStyle().getBaseId());
        me.ccrama.redditslide.Activities.Profile.profSort = net.dean.jraw.paginators.Sorting.HOT;
        me.ccrama.redditslide.Activities.Profile.profTime = net.dean.jraw.paginators.TimePeriod.ALL;
        findViewById(me.ccrama.redditslide.R.id.header).setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getColorUser(name));
        tabs = ((android.support.design.widget.TabLayout) (findViewById(me.ccrama.redditslide.R.id.sliding_tabs)));
        tabs.setTabMode(android.support.design.widget.TabLayout.MODE_SCROLLABLE);
        tabs.setSelectedTabIndicatorColor(new me.ccrama.redditslide.ColorPreferences(this).getColor("no sub"));
        pager = ((android.support.v4.view.ViewPager) (findViewById(me.ccrama.redditslide.R.id.content_view)));
        if (name.equals(me.ccrama.redditslide.Authentication.name))
            setDataSet(new java.lang.String[]{ getString(me.ccrama.redditslide.R.string.profile_overview), getString(me.ccrama.redditslide.R.string.profile_comments), getString(me.ccrama.redditslide.R.string.profile_submitted), getString(me.ccrama.redditslide.R.string.profile_gilded), getString(me.ccrama.redditslide.R.string.profile_upvoted), getString(me.ccrama.redditslide.R.string.profile_downvoted), getString(me.ccrama.redditslide.R.string.profile_saved), getString(me.ccrama.redditslide.R.string.profile_hidden), getString(me.ccrama.redditslide.R.string.profile_history) });
        else
            setDataSet(new java.lang.String[]{ getString(me.ccrama.redditslide.R.string.profile_overview), getString(me.ccrama.redditslide.R.string.profile_comments), getString(me.ccrama.redditslide.R.string.profile_submitted), getString(me.ccrama.redditslide.R.string.profile_gilded) });

        new me.ccrama.redditslide.Activities.Profile.getProfile().execute(name);
        pager.setOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {
            @java.lang.Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @java.lang.Override
            public void onPageSelected(int position) {
                isSavedView = position == 6;
                findViewById(me.ccrama.redditslide.R.id.header).animate().translationY(0).setInterpolator(new android.view.animation.LinearInterpolator()).setDuration(180);
                if (sortItem != null) {
                    if (position < 3) {
                        sortItem.setVisible(true);
                    } else {
                        sortItem.setVisible(false);
                    }
                }
                if (((categoryItem != null) && (me.ccrama.redditslide.Authentication.me != null)) && me.ccrama.redditslide.Authentication.me.hasGold()) {
                    if (position == 6) {
                        categoryItem.setVisible(true);
                    } else {
                        categoryItem.setVisible(false);
                    }
                }
            }

            @java.lang.Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        if (getIntent().hasExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_SAVED) && name.equals(me.ccrama.redditslide.Authentication.name)) {
            pager.setCurrentItem(6);
        }
        if (getIntent().hasExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_COMMENT) && name.equals(me.ccrama.redditslide.Authentication.name)) {
            pager.setCurrentItem(1);
        }
        if (getIntent().hasExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_SUBMIT) && name.equals(me.ccrama.redditslide.Authentication.name)) {
            pager.setCurrentItem(2);
        }
        if (getIntent().hasExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_HISTORY) && name.equals(me.ccrama.redditslide.Authentication.name)) {
            pager.setCurrentItem(8);
        }
        if (getIntent().hasExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_UPVOTE) && name.equals(me.ccrama.redditslide.Authentication.name)) {
            pager.setCurrentItem(4);
        }
        isSavedView = pager.getCurrentItem() == 6;
        if (pager.getCurrentItem() != 0) {
            scrollToTabAfterLayout(pager.getCurrentItem());
        }
    }

    private void doClick() {
        if (account == null) {
            try {
                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setTitle(me.ccrama.redditslide.R.string.profile_err_title).setCancelable(false).setMessage(me.ccrama.redditslide.R.string.profile_err_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                    public void onClick(android.content.DialogInterface dialog, int whichButton) {
                    }
                }).setCancelable(false).setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {
                    @java.lang.Override
                    public void onDismiss(android.content.DialogInterface dialog) {
                        onBackPressed();
                    }
                }).show();
            } catch (com.afollestad.materialdialogs.MaterialDialog.DialogException e) {
                android.util.Log.w(me.ccrama.redditslide.util.LogUtil.getTag(), "Activity already in background, dialog not shown " + e);
            }
            return;
        }
        if ((account.getDataNode().has("is_suspended") && account.getDataNode().get("is_suspended").asBoolean()) && (!name.equalsIgnoreCase(me.ccrama.redditslide.Authentication.name))) {
            try {
                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setTitle(me.ccrama.redditslide.R.string.account_suspended).setCancelable(false).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                    public void onClick(android.content.DialogInterface dialog, int whichButton) {
                        finish();
                    }
                }).setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {
                    @java.lang.Override
                    public void onDismiss(android.content.DialogInterface dialog) {
                        finish();
                    }
                }).show();
            } catch (com.afollestad.materialdialogs.MaterialDialog.DialogException e) {
                android.util.Log.w(me.ccrama.redditslide.util.LogUtil.getTag(), "Activity already in background, dialog not shown " + e);
            }
        }
    }

    private void setDataSet(java.lang.String[] data) {
        usedArray = data;
        me.ccrama.redditslide.Activities.Profile.ProfilePagerAdapter adapter = new me.ccrama.redditslide.Activities.Profile.ProfilePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(1);
        tabs.setupWithViewPager(pager);
    }

    private class getProfile extends android.os.AsyncTask<java.lang.String, java.lang.Void, java.lang.Void> {
        @java.lang.Override
        protected java.lang.Void doInBackground(java.lang.String... params) {
            try {
                if (!me.ccrama.redditslide.Activities.Profile.isValidUsername(params[0])) {
                    account = null;
                    return null;
                }
                account = me.ccrama.redditslide.Authentication.reddit.getUser(params[0]);
                trophyCase = new net.dean.jraw.fluent.FluentRedditClient(me.ccrama.redditslide.Authentication.reddit).user(params[0]).trophyCase();
            } catch (java.lang.RuntimeException ignored) {
            }
            return null;
        }

        @java.lang.Override
        public void onPostExecute(java.lang.Void voidd) {
            doClick();
        }
    }

    public class ProfilePagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
        public ProfilePagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @java.lang.Override
        public android.support.v4.app.Fragment getItem(int i) {
            if (i < 8) {
                android.support.v4.app.Fragment f = new me.ccrama.redditslide.Fragments.ContributionsView();
                android.os.Bundle args = new android.os.Bundle();
                args.putString("id", name);
                java.lang.String place;
                switch (i) {
                    case 0 :
                        place = "overview";
                        break;
                    case 1 :
                        place = "comments";
                        break;
                    case 2 :
                        place = "submitted";
                        break;
                    case 3 :
                        place = "gilded";
                        break;
                    case 4 :
                        place = "liked";
                        break;
                    case 5 :
                        place = "disliked";
                        break;
                    case 6 :
                        place = "saved";
                        break;
                    case 7 :
                        place = "hidden";
                        break;
                    default :
                        place = "overview";
                }
                args.putString("where", place);
                f.setArguments(args);
                return f;
            } else {
                android.support.v4.app.Fragment f = new me.ccrama.redditslide.Fragments.HistoryView();
                return f;
            }
        }

        @java.lang.Override
        public int getCount() {
            if (usedArray == null) {
                return 1;
            } else {
                return usedArray.length;
            }
        }

        @java.lang.Override
        public java.lang.CharSequence getPageTitle(int position) {
            return usedArray[position];
        }
    }

    public void openPopup() {
        android.support.v7.widget.PopupMenu popup = new android.support.v7.widget.PopupMenu(this, findViewById(me.ccrama.redditslide.R.id.anchor), android.view.Gravity.RIGHT);
        final android.text.Spannable[] base = me.ccrama.redditslide.util.SortingUtil.getSortingSpannables(me.ccrama.redditslide.Activities.Profile.profSort);
        for (android.text.Spannable s : base) {
            android.view.MenuItem m = popup.getMenu().add(s);
        }
        popup.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(android.view.MenuItem item) {
                me.ccrama.redditslide.util.LogUtil.v("Chosen is " + item.getOrder());
                int i = 0;
                for (android.text.Spannable s : base) {
                    if (s.equals(item.getTitle())) {
                        break;
                    }
                    i++;
                }
                switch (i) {
                    case 0 :
                        me.ccrama.redditslide.Activities.Profile.profSort = net.dean.jraw.paginators.Sorting.HOT;
                        break;
                    case 1 :
                        me.ccrama.redditslide.Activities.Profile.profSort = net.dean.jraw.paginators.Sorting.NEW;
                        break;
                    case 2 :
                        me.ccrama.redditslide.Activities.Profile.profSort = net.dean.jraw.paginators.Sorting.RISING;
                        break;
                    case 3 :
                        me.ccrama.redditslide.Activities.Profile.profSort = net.dean.jraw.paginators.Sorting.TOP;
                        openPopupTime();
                        return true;
                    case 4 :
                        me.ccrama.redditslide.Activities.Profile.profSort = net.dean.jraw.paginators.Sorting.CONTROVERSIAL;
                        openPopupTime();
                        return true;
                }
                me.ccrama.redditslide.util.SortingUtil.sorting.put(name.toLowerCase(java.util.Locale.ENGLISH), me.ccrama.redditslide.Activities.Profile.profSort);
                int current = pager.getCurrentItem();
                me.ccrama.redditslide.Activities.Profile.ProfilePagerAdapter adapter = new me.ccrama.redditslide.Activities.Profile.ProfilePagerAdapter(getSupportFragmentManager());
                pager.setAdapter(adapter);
                pager.setOffscreenPageLimit(1);
                tabs.setupWithViewPager(pager);
                pager.setCurrentItem(current);
                return true;
            }
        });
        popup.show();
    }

    public void openPopupTime() {
        android.support.v7.widget.PopupMenu popup = new android.support.v7.widget.PopupMenu(this, findViewById(me.ccrama.redditslide.R.id.anchor), android.view.Gravity.RIGHT);
        final android.text.Spannable[] base = me.ccrama.redditslide.util.SortingUtil.getSortingTimesSpannables(me.ccrama.redditslide.Activities.Profile.profTime);
        for (android.text.Spannable s : base) {
            android.view.MenuItem m = popup.getMenu().add(s);
        }
        popup.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(android.view.MenuItem item) {
                me.ccrama.redditslide.util.LogUtil.v("Chosen is " + item.getOrder());
                int i = 0;
                for (android.text.Spannable s : base) {
                    if (s.equals(item.getTitle())) {
                        break;
                    }
                    i++;
                }
                switch (i) {
                    case 0 :
                        me.ccrama.redditslide.Activities.Profile.profTime = net.dean.jraw.paginators.TimePeriod.HOUR;
                        break;
                    case 1 :
                        me.ccrama.redditslide.Activities.Profile.profTime = net.dean.jraw.paginators.TimePeriod.DAY;
                        break;
                    case 2 :
                        me.ccrama.redditslide.Activities.Profile.profTime = net.dean.jraw.paginators.TimePeriod.WEEK;
                        break;
                    case 3 :
                        me.ccrama.redditslide.Activities.Profile.profTime = net.dean.jraw.paginators.TimePeriod.MONTH;
                        break;
                    case 4 :
                        me.ccrama.redditslide.Activities.Profile.profTime = net.dean.jraw.paginators.TimePeriod.YEAR;
                        break;
                    case 5 :
                        me.ccrama.redditslide.Activities.Profile.profTime = net.dean.jraw.paginators.TimePeriod.ALL;
                        break;
                }
                me.ccrama.redditslide.util.SortingUtil.sorting.put(name.toLowerCase(java.util.Locale.ENGLISH), me.ccrama.redditslide.Activities.Profile.profSort);
                me.ccrama.redditslide.util.SortingUtil.times.put(name.toLowerCase(java.util.Locale.ENGLISH), me.ccrama.redditslide.Activities.Profile.profTime);
                int current = pager.getCurrentItem();
                me.ccrama.redditslide.Activities.Profile.ProfilePagerAdapter adapter = new me.ccrama.redditslide.Activities.Profile.ProfilePagerAdapter(getSupportFragmentManager());
                pager.setAdapter(adapter);
                pager.setOffscreenPageLimit(1);
                tabs.setupWithViewPager(pager);
                pager.setCurrentItem(current);
                return true;
            }
        });
        popup.show();
    }

    public java.lang.String category;

    public java.lang.String subreddit;

    @java.lang.Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        android.view.MenuInflater inflater = getMenuInflater();
        inflater.inflate(me.ccrama.redditslide.R.menu.menu_profile, menu);
        // used to hide the sort item on certain Profile tabs
        sortItem = menu.findItem(me.ccrama.redditslide.R.id.sort);
        categoryItem = menu.findItem(me.ccrama.redditslide.R.id.category);
        categoryItem.setVisible(false);
        sortItem.setVisible(false);
        int position = (pager == null) ? 0 : pager.getCurrentItem();
        if (sortItem != null) {
            if (position < 3) {
                sortItem.setVisible(true);
            } else {
                sortItem.setVisible(false);
            }
        }
        if (((categoryItem != null) && (me.ccrama.redditslide.Authentication.me != null)) && me.ccrama.redditslide.Authentication.me.hasGold()) {
            if (position == 6) {
                categoryItem.setVisible(true);
            } else {
                categoryItem.setVisible(false);
            }
        }
        return true;
    }

    @java.lang.Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                onBackPressed();
                break;
            case me.ccrama.redditslide.R.id.category :
                new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.util.List<java.lang.String>>() {
                    android.app.Dialog d;

                    @java.lang.Override
                    public void onPreExecute() {
                        d = new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.Profile.this).progress(true, 100).content(me.ccrama.redditslide.R.string.misc_please_wait).title(me.ccrama.redditslide.R.string.profile_category_loading).show();
                    }

                    @java.lang.Override
                    protected java.util.List<java.lang.String> doInBackground(java.lang.Void... params) {
                        try {
                            java.util.List<java.lang.String> categories = new java.util.ArrayList<>(new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).getSavedCategories());
                            categories.add(0, "No category");
                            return categories;
                        } catch (java.lang.Exception e) {
                            e.printStackTrace();
                            // probably has no categories?
                            return new java.util.ArrayList<java.lang.String>() {
                                {
                                    add(0, "No category");
                                }
                            };
                        }
                    }

                    @java.lang.Override
                    public void onPostExecute(final java.util.List<java.lang.String> data) {
                        try {
                            new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.Profile.this).items(data).title(me.ccrama.redditslide.R.string.profile_category_select).itemsCallback(new com.afollestad.materialdialogs.MaterialDialog.ListCallback() {
                                @java.lang.Override
                                public void onSelection(com.afollestad.materialdialogs.MaterialDialog dialog, final android.view.View itemView, int which, java.lang.CharSequence text) {
                                    final java.lang.String t = data.get(which);
                                    if (which == 0)
                                        category = null;
                                    else
                                        category = t;

                                    int current = pager.getCurrentItem();
                                    me.ccrama.redditslide.Activities.Profile.ProfilePagerAdapter adapter = new me.ccrama.redditslide.Activities.Profile.ProfilePagerAdapter(getSupportFragmentManager());
                                    pager.setAdapter(adapter);
                                    pager.setOffscreenPageLimit(1);
                                    tabs.setupWithViewPager(pager);
                                    pager.setCurrentItem(current);
                                }
                            }).show();
                            if (d != null) {
                                d.dismiss();
                            }
                        } catch (java.lang.Exception ignored) {
                        }
                    }
                }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                break;
            case me.ccrama.redditslide.R.id.info :
                if ((account != null) && (trophyCase != null)) {
                    android.view.LayoutInflater inflater = getLayoutInflater();
                    final android.view.View dialoglayout = inflater.inflate(me.ccrama.redditslide.R.layout.colorprofile, null);
                    com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this);
                    final android.widget.TextView title = dialoglayout.findViewById(me.ccrama.redditslide.R.id.title);
                    title.setText(name);
                    if (account.getDataNode().has("is_employee") && account.getDataNode().get("is_employee").asBoolean()) {
                        android.text.SpannableStringBuilder admin = new android.text.SpannableStringBuilder("[A]");
                        admin.setSpan(new android.text.style.RelativeSizeSpan(0.67F), 0, admin.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        title.append(" ");
                        title.append(admin);
                    }
                    dialoglayout.findViewById(me.ccrama.redditslide.R.id.share).setOnClickListener(new android.view.View.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.view.View v) {
                            me.ccrama.redditslide.Reddit.defaultShareText(getString(me.ccrama.redditslide.R.string.profile_share, name), "https://www.reddit.com/u/" + name, me.ccrama.redditslide.Activities.Profile.this);
                        }
                    });
                    final int currentColor = me.ccrama.redditslide.Visuals.Palette.getColorUser(name);
                    title.setBackgroundColor(currentColor);
                    java.lang.String info = getString(me.ccrama.redditslide.R.string.profile_age, me.ccrama.redditslide.TimeUtils.getTimeSince(account.getCreated().getTime(), this));
                    /* todo better if (account.hasGold() &&account.getDataNode().has("gold_expiration") ) {
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(account.getDataNode().get("gold_expiration").asLong());
                    info.append("Gold expires on " + new SimpleDateFormat("dd/MM/yy").format(c.getTime()));
                    }
                     */
                    ((android.widget.TextView) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.moreinfo))).setText(info);
                    java.lang.String tag = me.ccrama.redditslide.UserTags.getUserTag(name);
                    if (tag.isEmpty()) {
                        tag = getString(me.ccrama.redditslide.R.string.profile_tag_user);
                    } else {
                        tag = getString(me.ccrama.redditslide.R.string.profile_tag_user_existing, tag);
                    }
                    ((android.widget.TextView) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.tagged))).setText(tag);
                    android.widget.LinearLayout l = dialoglayout.findViewById(me.ccrama.redditslide.R.id.trophies_inner);
                    dialoglayout.findViewById(me.ccrama.redditslide.R.id.tag).setOnClickListener(new android.view.View.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.view.View v) {
                            com.afollestad.materialdialogs.MaterialDialog.Builder b = new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.Profile.this).title(getString(me.ccrama.redditslide.R.string.profile_tag_set, name)).input(getString(me.ccrama.redditslide.R.string.profile_tag), me.ccrama.redditslide.UserTags.getUserTag(name), false, new com.afollestad.materialdialogs.MaterialDialog.InputCallback() {
                                @java.lang.Override
                                public void onInput(com.afollestad.materialdialogs.MaterialDialog dialog, java.lang.CharSequence input) {
                                }
                            }).positiveText(me.ccrama.redditslide.R.string.profile_btn_tag).neutralText(me.ccrama.redditslide.R.string.btn_cancel);
                            if (me.ccrama.redditslide.UserTags.isUserTagged(name)) {
                                b.negativeText(me.ccrama.redditslide.R.string.profile_btn_untag);
                            }
                            b.onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                                @java.lang.Override
                                public void onClick(com.afollestad.materialdialogs.MaterialDialog dialog, com.afollestad.materialdialogs.DialogAction which) {
                                    me.ccrama.redditslide.UserTags.setUserTag(name, dialog.getInputEditText().getText().toString());
                                    java.lang.String tag = me.ccrama.redditslide.UserTags.getUserTag(name);
                                    if (tag.isEmpty()) {
                                        tag = getString(me.ccrama.redditslide.R.string.profile_tag_user);
                                    } else {
                                        tag = getString(me.ccrama.redditslide.R.string.profile_tag_user_existing, tag);
                                    }
                                    ((android.widget.TextView) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.tagged))).setText(tag);
                                }
                            }).onNeutral(null).onNegative(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                                @java.lang.Override
                                public void onClick(com.afollestad.materialdialogs.MaterialDialog dialog, com.afollestad.materialdialogs.DialogAction which) {
                                    me.ccrama.redditslide.UserTags.removeUserTag(name);
                                    java.lang.String tag = me.ccrama.redditslide.UserTags.getUserTag(name);
                                    if (tag.isEmpty()) {
                                        tag = getString(me.ccrama.redditslide.R.string.profile_tag_user);
                                    } else {
                                        tag = getString(me.ccrama.redditslide.R.string.profile_tag_user_existing, tag);
                                    }
                                    ((android.widget.TextView) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.tagged))).setText(tag);
                                }
                            }).show();
                        }
                    });
                    if (trophyCase.isEmpty()) {
                        dialoglayout.findViewById(me.ccrama.redditslide.R.id.trophies).setVisibility(android.view.View.GONE);
                    } else {
                        for (final net.dean.jraw.models.Trophy t : trophyCase) {
                            android.view.View view = getLayoutInflater().inflate(me.ccrama.redditslide.R.layout.trophy, null);
                            ((me.ccrama.redditslide.Reddit) (getApplicationContext())).getImageLoader().displayImage(t.getIcon(), ((android.widget.ImageView) (view.findViewById(me.ccrama.redditslide.R.id.image))));
                            ((android.widget.TextView) (view.findViewById(me.ccrama.redditslide.R.id.trophyTitle))).setText(t.getFullName());
                            if ((t.getAboutUrl() != null) && (!t.getAboutUrl().equalsIgnoreCase("null"))) {
                                view.setOnClickListener(new android.view.View.OnClickListener() {
                                    @java.lang.Override
                                    public void onClick(android.view.View v) {
                                        me.ccrama.redditslide.util.LinkUtil.openUrl(me.ccrama.redditslide.util.LinkUtil.formatURL(t.getAboutUrl()).toString(), me.ccrama.redditslide.Visuals.Palette.getColorUser(account.getFullName()), me.ccrama.redditslide.Activities.Profile.this);
                                    }
                                });
                            }
                            l.addView(view);
                        }
                    }
                    if (me.ccrama.redditslide.Authentication.isLoggedIn) {
                        dialoglayout.findViewById(me.ccrama.redditslide.R.id.pm).setOnClickListener(new android.view.View.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.view.View v) {
                                android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.Profile.this, me.ccrama.redditslide.Activities.SendMessage.class);
                                i.putExtra(me.ccrama.redditslide.Activities.SendMessage.EXTRA_NAME, name);
                                startActivity(i);
                            }
                        });
                        friend = account.isFriend();
                        if (friend) {
                            ((android.widget.TextView) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.friend))).setText(me.ccrama.redditslide.R.string.profile_remove_friend);
                        } else {
                            ((android.widget.TextView) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.friend))).setText(me.ccrama.redditslide.R.string.profile_add_friend);
                        }
                        dialoglayout.findViewById(me.ccrama.redditslide.R.id.friend_body).setOnClickListener(new android.view.View.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.view.View v) {
                                new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
                                    @java.lang.Override
                                    protected java.lang.Void doInBackground(java.lang.Void... params) {
                                        if (friend) {
                                            try {
                                                new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).deleteFriend(name);
                                            } catch (java.lang.Exception ignored) {
                                                // Will throw java.lang.IllegalStateException: No Content-Type header was found, but it still works.
                                            }
                                            friend = false;
                                        } else {
                                            new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).updateFriend(name);
                                            friend = true;
                                        }
                                        return null;
                                    }

                                    @java.lang.Override
                                    public void onPostExecute(java.lang.Void voids) {
                                        if (friend) {
                                            ((android.widget.TextView) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.friend))).setText(me.ccrama.redditslide.R.string.profile_remove_friend);
                                        } else {
                                            ((android.widget.TextView) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.friend))).setText(me.ccrama.redditslide.R.string.profile_add_friend);
                                        }
                                    }
                                }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        });
                        dialoglayout.findViewById(me.ccrama.redditslide.R.id.block_body).setOnClickListener(new android.view.View.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.view.View v) {
                                new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
                                    @java.lang.Override
                                    protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                                        java.util.Map<java.lang.String, java.lang.String> map = new java.util.HashMap();
                                        map.put("account_id", "t2_" + account.getId());
                                        try {
                                            me.ccrama.redditslide.Authentication.reddit.execute(me.ccrama.redditslide.Authentication.reddit.request().post(map).path(java.lang.String.format("/api/block_user")).build());
                                        } catch (java.lang.Exception ex) {
                                            return false;
                                        }
                                        return true;
                                    }

                                    @java.lang.Override
                                    public void onPostExecute(java.lang.Boolean blocked) {
                                        if (!blocked) {
                                            android.widget.Toast.makeText(getBaseContext(), getString(me.ccrama.redditslide.R.string.err_block_user), android.widget.Toast.LENGTH_LONG).show();
                                        } else {
                                            android.widget.Toast.makeText(getBaseContext(), getString(me.ccrama.redditslide.R.string.success_block_user), android.widget.Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        });
                    } else {
                        dialoglayout.findViewById(me.ccrama.redditslide.R.id.pm).setVisibility(android.view.View.GONE);
                    }
                    dialoglayout.findViewById(me.ccrama.redditslide.R.id.multi_body).setOnClickListener(new android.view.View.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.view.View v) {
                            android.content.Intent inte = new android.content.Intent(me.ccrama.redditslide.Activities.Profile.this, me.ccrama.redditslide.Activities.MultiredditOverview.class);
                            inte.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_PROFILE, name);
                            me.ccrama.redditslide.Activities.Profile.this.startActivity(inte);
                        }
                    });
                    final android.view.View body = dialoglayout.findViewById(me.ccrama.redditslide.R.id.body2);
                    body.setVisibility(android.view.View.INVISIBLE);
                    final android.view.View center = dialoglayout.findViewById(me.ccrama.redditslide.R.id.colorExpandFrom);
                    dialoglayout.findViewById(me.ccrama.redditslide.R.id.color).setOnClickListener(new android.view.View.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.view.View v) {
                            int cx = center.getWidth() / 2;
                            int cy = center.getHeight() / 2;
                            int finalRadius = java.lang.Math.max(body.getWidth(), body.getHeight());
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                android.animation.Animator anim = android.view.ViewAnimationUtils.createCircularReveal(body, cx, cy, 0, finalRadius);
                                body.setVisibility(android.view.View.VISIBLE);
                                anim.start();
                            } else {
                                body.setVisibility(android.view.View.VISIBLE);
                            }
                        }
                    });
                    uz.shift.colorpicker.LineColorPicker colorPicker = dialoglayout.findViewById(me.ccrama.redditslide.R.id.picker);
                    final uz.shift.colorpicker.LineColorPicker colorPicker2 = dialoglayout.findViewById(me.ccrama.redditslide.R.id.picker2);
                    colorPicker.setColors(me.ccrama.redditslide.ColorPreferences.getBaseColors(this));
                    colorPicker.setOnColorChangedListener(new uz.shift.colorpicker.OnColorChangedListener() {
                        @java.lang.Override
                        public void onColorChanged(int c) {
                            colorPicker2.setColors(me.ccrama.redditslide.ColorPreferences.getColors(getBaseContext(), c));
                            colorPicker2.setSelectedColor(c);
                        }
                    });
                    for (int i : colorPicker.getColors()) {
                        for (int i2 : me.ccrama.redditslide.ColorPreferences.getColors(getBaseContext(), i)) {
                            if (i2 == currentColor) {
                                colorPicker.setSelectedColor(i);
                                colorPicker2.setColors(me.ccrama.redditslide.ColorPreferences.getColors(getBaseContext(), i));
                                colorPicker2.setSelectedColor(i2);
                                break;
                            }
                        }
                    }
                    colorPicker2.setOnColorChangedListener(new uz.shift.colorpicker.OnColorChangedListener() {
                        @java.lang.Override
                        public void onColorChanged(int i) {
                            findViewById(me.ccrama.redditslide.R.id.header).setBackgroundColor(colorPicker2.getColor());
                            if (mToolbar != null)
                                mToolbar.setBackgroundColor(colorPicker2.getColor());

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                android.view.Window window = getWindow();
                                window.setStatusBarColor(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(colorPicker2.getColor()));
                            }
                            title.setBackgroundColor(colorPicker2.getColor());
                        }
                    });
                    {
                        android.widget.TextView dialogButton = dialoglayout.findViewById(me.ccrama.redditslide.R.id.ok);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new android.view.View.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.view.View v) {
                                me.ccrama.redditslide.Visuals.Palette.setColorUser(name, colorPicker2.getColor());
                                int cx = center.getWidth() / 2;
                                int cy = center.getHeight() / 2;
                                int initialRadius = body.getWidth();
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                    android.animation.Animator anim = android.view.ViewAnimationUtils.createCircularReveal(body, cx, cy, initialRadius, 0);
                                    anim.addListener(new android.animation.AnimatorListenerAdapter() {
                                        @java.lang.Override
                                        public void onAnimationEnd(android.animation.Animator animation) {
                                            super.onAnimationEnd(animation);
                                            body.setVisibility(android.view.View.GONE);
                                        }
                                    });
                                    anim.start();
                                } else {
                                    body.setVisibility(android.view.View.GONE);
                                }
                            }
                        });
                    }
                    {
                        final android.widget.TextView dialogButton = dialoglayout.findViewById(me.ccrama.redditslide.R.id.reset);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new android.view.View.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.view.View v) {
                                me.ccrama.redditslide.Visuals.Palette.removeUserColor(name);
                                android.support.design.widget.Snackbar.make(dialogButton, "User color removed", android.support.design.widget.Snackbar.LENGTH_SHORT).show();
                                int cx = center.getWidth() / 2;
                                int cy = center.getHeight() / 2;
                                int initialRadius = body.getWidth();
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                    android.animation.Animator anim = android.view.ViewAnimationUtils.createCircularReveal(body, cx, cy, initialRadius, 0);
                                    anim.addListener(new android.animation.AnimatorListenerAdapter() {
                                        @java.lang.Override
                                        public void onAnimationEnd(android.animation.Animator animation) {
                                            super.onAnimationEnd(animation);
                                            body.setVisibility(android.view.View.GONE);
                                        }
                                    });
                                    anim.start();
                                } else {
                                    body.setVisibility(android.view.View.GONE);
                                }
                            }
                        });
                    }
                    ((android.widget.TextView) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.commentkarma))).setText(java.lang.String.format(java.util.Locale.getDefault(), "%d", account.getCommentKarma()));
                    ((android.widget.TextView) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.linkkarma))).setText(java.lang.String.format(java.util.Locale.getDefault(), "%d", account.getLinkKarma()));
                    builder.setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {
                        @java.lang.Override
                        public void onDismiss(android.content.DialogInterface dialogInterface) {
                            findViewById(me.ccrama.redditslide.R.id.header).setBackgroundColor(currentColor);
                            if (mToolbar != null)
                                mToolbar.setBackgroundColor(currentColor);

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                android.view.Window window = getWindow();
                                window.setStatusBarColor(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(currentColor));
                            }
                        }
                    });
                    builder.setView(dialoglayout);
                    builder.show();
                }
                return true;
            case me.ccrama.redditslide.R.id.sort :
                openPopup();
                return true;
        }
        return false;
    }
}