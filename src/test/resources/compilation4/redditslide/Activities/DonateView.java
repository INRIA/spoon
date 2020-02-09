package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.util.Purchase;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.util.IabResult;
import me.ccrama.redditslide.util.SkuDetails;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.util.IabHelper;
import me.ccrama.redditslide.util.Inventory;
/**
 * Created by carlo_000 on 5/26/2015.
 * Allows a user to donate to Slide using Google Play's IabHelper
 */
public class DonateView extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    private final me.ccrama.redditslide.util.IabHelper.OnConsumeFinishedListener mPurchaseFinishedListener = new me.ccrama.redditslide.util.IabHelper.OnConsumeFinishedListener() {
        @java.lang.Override
        public void onConsumeFinished(me.ccrama.redditslide.util.Purchase purchase, me.ccrama.redditslide.util.IabResult result) {
            if (result.isFailure()) {
                android.util.Log.d(me.ccrama.redditslide.util.LogUtil.getTag(), "Error purchasing: " + result);
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(me.ccrama.redditslide.Activities.DonateView.this);
                builder.setTitle(me.ccrama.redditslide.R.string.donate_err_title);
                builder.setMessage(me.ccrama.redditslide.R.string.donate_err_msg);
                builder.setNeutralButton(me.ccrama.redditslide.R.string.btn_ok, null);
                builder.show();
            } else if (purchase.getSku().contains("donation")) {
                runOnUiThread(new java.lang.Runnable() {
                    @java.lang.Override
                    public void run() {
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(me.ccrama.redditslide.Activities.DonateView.this);
                        builder.setTitle(me.ccrama.redditslide.R.string.donate_success_title);
                        builder.setMessage(me.ccrama.redditslide.R.string.donate_success_msg);
                        builder.setPositiveButton(me.ccrama.redditslide.R.string.donate_success_btn, new android.content.DialogInterface.OnClickListener() {
                            public void onClick(android.content.DialogInterface dialog, int id) {
                                me.ccrama.redditslide.Activities.DonateView.this.finish();
                            }
                        });
                        builder.show();
                    }
                });
            }
        }
    };

    @java.lang.Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        disableSwipeBackLayout();
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_donate);
        android.support.v7.widget.Toolbar t = ((android.support.v7.widget.Toolbar) (findViewById(me.ccrama.redditslide.R.id.toolbar)));
        t.setTitle(me.ccrama.redditslide.R.string.settings_title_support);
        setRecentBar(getString(me.ccrama.redditslide.R.string.settings_title_support), me.ccrama.redditslide.Visuals.Palette.getDarkerColor(android.support.v4.content.ContextCompat.getColor(this, me.ccrama.redditslide.R.color.md_light_green_500)));
        setSupportActionBar(t);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            android.view.Window window = this.getWindow();
            window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(android.support.v4.content.ContextCompat.getColor(this, me.ccrama.redditslide.R.color.md_light_green_500)));
            if (me.ccrama.redditslide.SettingValues.colorNavBar) {
                window.setNavigationBarColor(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(android.support.v4.content.ContextCompat.getColor(this, me.ccrama.redditslide.R.color.md_light_green_500)));
            }
        }
        final com.rey.material.widget.Slider slider = ((com.rey.material.widget.Slider) (findViewById(me.ccrama.redditslide.R.id.slider_sl_discrete)));
        slider.setValue(4, false);
        final android.widget.TextView ads = ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.ads)));
        final android.widget.TextView hours = ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.hours)));
        final android.widget.TextView money = ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.money)));
        slider.setOnPositionChangeListener(new com.rey.material.widget.Slider.OnPositionChangeListener() {
            @java.lang.Override
            public void onPositionChanged(com.rey.material.widget.Slider view, boolean fromUser, float oldPos, float newPos, int oldValue, int newValue) {
                ads.setText((" " + (newValue * 330)) + " ");
                hours.setText((" " + java.lang.String.valueOf(((double) (newValue)) / 10)) + " ");
                money.setText("$" + newValue);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ads.setText((" " + (4 * 330)) + " ");
        hours.setText((" " + java.lang.String.valueOf(((double) (4)) / 10)) + " ");
        money.setText("$" + 4);
        findViewById(me.ccrama.redditslide.R.id.donate).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View view) {
                java.lang.String name = "";
                if (me.ccrama.redditslide.Authentication.isLoggedIn) {
                    name = me.ccrama.redditslide.Authentication.name;
                }
                if (me.ccrama.redditslide.Reddit.mHelper != null) {
                    me.ccrama.redditslide.Reddit.mHelper.flagEndAsync();
                }
                me.ccrama.redditslide.Reddit.mHelper.queryInventoryAsync(new me.ccrama.redditslide.util.IabHelper.QueryInventoryFinishedListener() {
                    @java.lang.Override
                    public void onQueryInventoryFinished(me.ccrama.redditslide.util.IabResult result, me.ccrama.redditslide.util.Inventory inv) {
                        if (inv != null) {
                            me.ccrama.redditslide.util.SkuDetails donation = inv.getSkuDetails("donation_" + slider.getValue());
                            me.ccrama.redditslide.util.LogUtil.v("Trying to get donation_" + slider.getValue());
                            if (donation != null) {
                                me.ccrama.redditslide.util.LogUtil.v("Not null");
                                me.ccrama.redditslide.Reddit.mHelper.launchPurchaseFlow(me.ccrama.redditslide.Activities.DonateView.this, donation.getSku(), 4000, new me.ccrama.redditslide.util.IabHelper.OnIabPurchaseFinishedListener() {
                                    @java.lang.Override
                                    public void onIabPurchaseFinished(me.ccrama.redditslide.util.IabResult result, me.ccrama.redditslide.util.Purchase info) {
                                        if (result.isSuccess()) {
                                            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.DonateView.this).setTitle("Thank you!").setMessage("Thank you very much for your support :)").setPositiveButton(me.ccrama.redditslide.R.string.btn_done, null).show();
                                        } else {
                                            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.DonateView.this).setTitle("Uh oh, something went wrong.").setMessage("Please try again soon! Sorry for the inconvenience.").setPositiveButton("Ok", null).show();
                                        }
                                    }
                                });
                            } else {
                                me.ccrama.redditslide.util.LogUtil.v("Null");
                            }
                        } else {
                            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.DonateView.this).setTitle("Uh oh, something went wrong.").setMessage("Please try again soon! Sorry for the inconvenience.").setPositiveButton("Ok", null).show();
                        }
                    }
                });
            }
        });
    }
}