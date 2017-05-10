package com.arcaneconstruct.triviador;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.arcaneconstruct.util.util.IabHelper;
import com.arcaneconstruct.util.util.IabResult;
import com.arcaneconstruct.util.util.Inventory;
import com.arcaneconstruct.util.util.Purchase;

public class MainActivity extends AppCompatActivity implements AppConstants {
    private static final String TAG = "MainActivity";
    TextView mainLevel;
    Button start;
    SharedPreferences settings;
    Button noAds;
    IabHelper mHelper;
    static final String SKU_NOADS = "com.tedrasoft.triviador.noads";
    static final int RC_REQUEST = 10001;
    String base64EncodedPublicKey =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnv4phsuEj5jTVOI6Zrj/Y1OfKKiOi3SgoQgPlD0UWq4tBQQqPUknNCbn+q6/a/2NBof52axlfzN2wmmhn65I7gkAozCZhK71z9nBAexWZ8hG15jhlfdJ8dEme9bHDZBPyeXrknwmMY6Oz+yO3Z/53A9RTvxuRhxvZd8MYvFXd7DyLggNvqpfQrug+wRteHG4ts9sqEIWGlBXlnwbpx2194dPYzx470xd9S+iXE3+rtb40nHLrpziNseRrdaACAcYP5h4lWlibmT9Lh1dtlDFXa7uMATA7/FvDSS5oD5WEbloHHWp44LjPLzs2IyYmo8+TH+X0I9Eq2iyGq10eAsEtQIDAQAB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = getSharedPreferences(PREFS_NAME, 0);
        start = (Button) findViewById(R.id.main_play);
        noAds = (Button) findViewById(R.id.main_no_ads);
        View.OnClickListener noAdsListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String payload = "";
                mHelper.launchPurchaseFlow(getContext(), SKU_NOADS, RC_REQUEST,
                        mPurchaseFinishedListener, payload);
            }
        };
        if(!getNoAds()){
            noAds.setOnClickListener(noAdsListener);
        }else{
            noAds.setEnabled(false);
        }
        mainLevel = (TextView) findViewById(R.id.main_level);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LevelActivity.class);
                startActivity(intent);
                finish();
            }
        };
        start.setOnClickListener(listener);
        int crtLevel=getLevel();
        mainLevel.setText("Level " + crtLevel);
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);
// Start setup. This is asynchronous and the specified listener
// will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        if(!getNoAds()) {
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    Log.d(TAG, "Setup finished.");
                    if (!result.isSuccess()) {
// Oh noes, there was a problem.
                        Log.d(TAG, "Problem setting up in-app billing: " + result);
                        return;
                    }
// Have we been disposed of in the meantime? If so, quit.
                    if (mHelper == null) return;
// IAB is fully set up. Now, let's get an inventory of stuff we own.
                    Log.d(TAG, "Setup successful. Querying inventory.");
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                }
            });
        }
    }
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
                public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                    Log.d(TAG, "Query inventory finished.");
// Have we been disposed of in the meantime? If so, quit.
                    if (mHelper == null) return;
// Is it a failure?
                    if (result.isFailure()) {
                        Log.d(TAG,"Failed to query inventory: " + result);
                        return;
                    }
                    Log.d(TAG, "Query inventory was successful.");
// Verificam daca am un no ads cumparat
                    Purchase gasPurchase = inventory.getPurchase(SKU_NOADS);
                    if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
                        Log.d(TAG, "We have no ads. do not consume it to make it available to other devices as well.");
                                setNoAds();
                        return;
                    }
                    updateUi();
                    setWaitScreen(false);
                    Log.d(TAG, "Initial inventory query finished; enabling main UI.");
                }
            };
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return true;
    }
    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new
            IabHelper.OnIabPurchaseFinishedListener() {
                public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                    Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
// if we were disposed of in the meantime, quit.
                    if (mHelper == null) return;
                    if (result.isFailure()) {
                        Log.d(TAG,"Error purchasing: " + result);
                        setWaitScreen(false);
                        return;
                    }
                    if (!verifyDeveloperPayload(purchase)) {
                        Log.d(TAG,"Error purchasing. Authenticity verification failed.");
                        setWaitScreen(false);
                        return;
                    }
                    Log.d(TAG, "Purchase successful.");
                    if (purchase.getSku().equals(SKU_NOADS)) {
// mHelper.consumeAsync(purchase, mConsumeFinishedListener);
//salvam in shared prefs nu consumam
                        setNoAds();
                    }
                }
            };
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new
            IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase, IabResult result) {
                    Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " +
                            result);
// if we were disposed of in the meantime, quit.
                    if (mHelper == null) return;
                    if (result.isSuccess()) {

                        Log.d(TAG, "Consumption successful. .");
                    }
                    else {
                        Log.d(TAG, "Error while consuming: " + result);
                    }
                    updateUi();
                    setWaitScreen(false);
                    Log.d(TAG, "End consumption flow.");
                }
            };
    // Enables or disables the "please wait" screen.
    void setWaitScreen(boolean set) {
        Log.d(TAG, "daca avem nevoie sa asteptam pana facem sincronizarea cu IAP");
    }
    public void updateUi() {
        if(getNoAds()) noAds.setEnabled(false);
    }

    private MainActivity getContext() {
        return this;
    }
    public int getLevel() {
        int level = settings.getInt(LEVEL, 1);
        int maxLevel = settings.getInt(NO_LEVELS, -1);
        if (maxLevel < 0) {
            return level;
        } else {
            if (level > maxLevel) {
                level = 1;
            }
        }
        return level;
    }
    public boolean getNoAds() {
        boolean noAds = settings.getBoolean(NOADS, false);
        return noAds;
    }
    public void setNoAds() {
// SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(NOADS, true);
        editor.commit();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;
// Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
// not handled, so handle it ourselves (here's where you'd
// perform any handling of activity results not related to in-app
// billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }
}


