package com.arcaneconstruct.triviador;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.GameHelper;
import com.arcaneconstruct.triviador.data.ConfigData;
import com.arcaneconstruct.triviador.data.JsonProcessor;
import com.arcaneconstruct.triviador.data.LevelData;

public class ResultActivity extends AppCompatActivity implements AppConstants {

    private static final String TAG ="LevelActivity" ;
    private TextView title;
    private TextView body;
    ConfigData config;
    SharedPreferences settings;
    int crtLevel;
    LevelData ld;
    Button next;
    Button back;
    InterstitialAd mInterstitialAd;
    GameHelper mHelper;
    String leaderboardId;
    Button leaderboard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        settings = getSharedPreferences(PREFS_NAME, 0);
        config= JsonProcessor.readConfig(this, "levels");
        next = (Button) findViewById(R.id.result_next);
        title = (TextView) findViewById(R.id.result_title);
        back = (Button) findViewById(R.id.result_back);
        body = (TextView) findViewById(R.id.result_body);
        View.OnClickListener listenerBack = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        back.setOnClickListener(listenerBack);
        View.OnClickListener listenerNext = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LevelActivity.class);
                startActivity(intent);
                finish();
            }
        };
        next.setOnClickListener(listenerNext);
        int crtLevel=getLevel();
        boolean success = getIntent().getExtras().getBoolean(SUCCESS);
        if(success) {
            if(crtLevel>=config.getNoLevels()){
                //resetam
                setLevel(1);
                body.setText("Ai gasit raspunsul si ai terminat toate intrebarile!");
                next.setText("Reincepe jocul");
            }else{
                setLevel(getLevel()+1);
                body.setText("Ai gasit raspunsul!");
                next.setText("Urmatorul nivel");
            }
            title.setText("Corect!");
        }else{
            title.setText("Gresit!");
            body.setText("Ai gresit raspunsul");
            next.setText("Reincearca");
        }
        back.setText("Inapoi");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
            }
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mInterstitialAd.show();
            }
        });
        if(!getNoAds()) requestNewInterstitial();
        leaderboard=(Button)findViewById(R.id.result_leaderboard);
        leaderboard.setEnabled(false);
        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(
                                mHelper.getApiClient(), getString(R.string.leaderboard_id)),
                        50);
            }
        });
        mHelper = new GameHelper(this, GameHelper.CLIENT_ALL);
        GameHelper.GameHelperListener listener = new GameHelper.GameHelperListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onSignInSucceeded() {
                leaderboardId=getResources().getString(R.string.leaderboard_id);
                leaderboard.setEnabled(true);
                Games.Leaderboards.submitScoreImmediate (mHelper.getApiClient(),leaderboardId, getLevel()).setResultCallback(resultCallback);
            }
            @Override
            public void onSignInFailed() {
                leaderboard.setEnabled(false);
                return;
            }
        };
        mHelper.setPlusApiOptions(new Plus.PlusOptions.Builder().build());
        mHelper.setup(listener);
    }
    private ResultCallback resultCallback = new ResultCallback () {
        @Override
        public void onResult(Result result) {
            if (result.getStatus().isSuccess()){
                // Toast.makeText(getContext(), "SCORE SUBMITTED.NO NEED TO UPDATE", Toast.LENGTH_LONG).show();
            }
            else{
                // Toast.makeText(getContext(), "Error NETWORK >UPDATE LATER", Toast.LENGTH_LONG).show();
            }
        }
    };
    @Override
    protected void onStart() {
        super.onStart();
        mHelper.onStart(this);
    }
    @Override
    protected void onStop() {
        super.onStop();
        mHelper.onStop();
    }
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("E00383F5CF06D921F45BC5388B3042F8")
                .build();
        mInterstitialAd.loadAd(adRequest);
    }
    private ResultActivity getContext() {
        return this;
    }

    public boolean getNoAds() {
        boolean noAds = settings.getBoolean(NOADS, false);
        return noAds;
    }
    @Override
    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        mHelper.onActivityResult(request, response, data);
    }

    /**
     * Get level from shared settings
     * @return current level
     */
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
    public void setLevel(int level) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(LEVEL, level);
        editor.commit();
    }

}
