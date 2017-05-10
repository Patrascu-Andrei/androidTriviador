package com.arcaneconstruct.triviador;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.arcaneconstruct.triviador.data.ConfigData;
import com.arcaneconstruct.triviador.data.JsonProcessor;
import com.arcaneconstruct.triviador.data.LevelData;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class LevelActivity extends AppCompatActivity implements AppConstants {
    private static final String TAG = "LevelActivity";
    private TextView title;
    private TextView question;
    ConfigData config;
    SharedPreferences settings;
    int crtLevel;
    LevelData ld;
    RadioGroup rg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        config = JsonProcessor.readConfig(this, "levels");
        settings = getSharedPreferences(PREFS_NAME, 0);

        crtLevel = getLevel();
        title = (TextView) findViewById(R.id.level_title);
        title.setText("Level " + crtLevel);

        ld = config.getLevels().get(crtLevel - 1);

        question = (TextView) findViewById(R.id.level_question);
        question.setText(ld.getQuestion());
        Animation animScale = AnimationUtils.loadAnimation(this, R.anim.scale);
        question.startAnimation(animScale);

        rg = (RadioGroup) findViewById(R.id.options_group);
        RadioButton rb1 = (RadioButton) rg.findViewById(R.id.option1);
        rb1.setText(ld.getOptions().get(0));
        RadioButton rb2 = (RadioButton) rg.findViewById(R.id.option2);
        rb2.setText(ld.getOptions().get(1));
        RadioButton rb3 = (RadioButton) rg.findViewById(R.id.option3);
        rb3.setText(ld.getOptions().get(2));
        RadioButton rb4 = (RadioButton) rg.findViewById(R.id.option4);
        rb4.setText(ld.getOptions().get(3));

        if (!getNoAds()){
            AdView mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().addTestDevice("E00383F5CF06D921F45BC5388B3042F8").build(
            );
            mAdView.loadAd(adRequest);

        }

    }
    public boolean getNoAds() {
        boolean noAds = settings.getBoolean(NOADS, false);
        return noAds;
    }



    public void onRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.option1:
                if (checked)
                    if (ld.getSolution() == 1) {
                        Log.d(TAG, "OK");
                        startResult(true);
                    } else {
                        Log.d(TAG, "NOT OK");
                        startResult(false);
                    }
                break;
            case R.id.option2:
                if (checked)
                    if (ld.getSolution() == 2) {
                        Log.d(TAG, "OK");
                        startResult(true);
                    } else {
                        Log.d(TAG, "NOT OK");
                        startResult(false);
                    }
                break;
            case R.id.option3:
                if (checked)
                    if (ld.getSolution() == 3) {
                        Log.d(TAG, "OK");
                        startResult(true);
                    } else {
                        Log.d(TAG, "NOT OK");
                        startResult(false);
                    }
                break;
            case R.id.option4:
                if (checked)
                    if (ld.getSolution() == 4) {
                        Log.d(TAG, "OK");
                        startResult(true);
                    } else {
                        Log.d(TAG, "NOT OK");
                        startResult(false);
                    }
                break;
        }
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
    private LevelActivity getContext() {
        return this;
    }
    private void startResult(boolean success) {
        Intent intent = new Intent(getContext(), ResultActivity.class);
        if (success && ld.getId() <= config.getNoLevels()) {
            setLevel(ld.getId());
        }
        intent.putExtra(SUCCESS, success);
        startActivity(intent);
        finish();
    }
    public void setLevel(int level) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(LEVEL, level);
        editor.commit();
    }
}

