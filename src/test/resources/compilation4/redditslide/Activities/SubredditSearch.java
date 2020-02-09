package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Fragments.SubredditListView;
/**
 * Created by ccrama on 9/17/2015.
 */
public class SubredditSearch extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    @java.lang.Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        android.view.MenuInflater inflater = getMenuInflater();
        inflater.inflate(me.ccrama.redditslide.R.menu.menu_edit, menu);
        return true;
    }

    @java.lang.Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                onBackPressed();
                return true;
            case me.ccrama.redditslide.R.id.edit :
                {
                    new com.afollestad.materialdialogs.MaterialDialog.Builder(this).alwaysCallInputCallback().inputType(android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS).inputRange(3, 100).input(getString(me.ccrama.redditslide.R.string.discover_search), term, new com.afollestad.materialdialogs.MaterialDialog.InputCallback() {
                        @java.lang.Override
                        public void onInput(com.afollestad.materialdialogs.MaterialDialog dialog, java.lang.CharSequence input) {
                            if (input.length() >= 3) {
                                dialog.getActionButton(com.afollestad.materialdialogs.DialogAction.POSITIVE).setEnabled(true);
                            } else {
                                dialog.getActionButton(com.afollestad.materialdialogs.DialogAction.POSITIVE).setEnabled(false);
                            }
                        }
                    }).positiveText(me.ccrama.redditslide.R.string.search_all).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                        @java.lang.Override
                        public void onClick(@android.support.annotation.NonNull
                        com.afollestad.materialdialogs.MaterialDialog dialog, @android.support.annotation.NonNull
                        com.afollestad.materialdialogs.DialogAction which) {
                            android.content.Intent inte = new android.content.Intent(me.ccrama.redditslide.Activities.SubredditSearch.this, me.ccrama.redditslide.Activities.SubredditSearch.class);
                            inte.putExtra("term", dialog.getInputEditText().getText().toString());
                            me.ccrama.redditslide.Activities.SubredditSearch.this.startActivity(inte);
                            finish();
                        }
                    }).negativeText(me.ccrama.redditslide.R.string.btn_cancel).show();
                }
                return true;
            default :
                return false;
        }
    }

    java.lang.String term;

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstance) {
        super.onCreate(savedInstance);
        term = getIntent().getExtras().getString("term");
        applyColorTheme("");
        setContentView(me.ccrama.redditslide.R.layout.activity_fragmentinner);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, term, true, true);
        android.support.v4.app.Fragment f = new me.ccrama.redditslide.Fragments.SubredditListView();
        android.os.Bundle args = new android.os.Bundle();
        args.putString("id", term);
        f.setArguments(args);
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(me.ccrama.redditslide.R.id.fragmentcontent, f);
        fragmentTransaction.commit();
    }
}