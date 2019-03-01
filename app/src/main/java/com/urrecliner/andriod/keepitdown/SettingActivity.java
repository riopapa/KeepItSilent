package com.urrecliner.andriod.keepitdown;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import static com.urrecliner.andriod.keepitdown.Vars.ReceiverCase;
import static com.urrecliner.andriod.keepitdown.Vars.beepManner;
import static com.urrecliner.andriod.keepitdown.Vars.default_Duration;
import static com.urrecliner.andriod.keepitdown.Vars.editor;
import static com.urrecliner.andriod.keepitdown.Vars.interval_Long;
import static com.urrecliner.andriod.keepitdown.Vars.interval_Short;
import static com.urrecliner.andriod.keepitdown.Vars.mSettings;
import static com.urrecliner.andriod.keepitdown.Vars.settingActivity;
import static com.urrecliner.andriod.keepitdown.Vars.utils;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.w("Setting","OnCreate");
        super.onCreate(savedInstanceState);
        settingActivity = this;
        setContentView(R.layout.activity_setting);
        showSoundSetting();
    }

    public void adjustSetting(View v) {
        editor = mSettings.edit();
        switch (v.getId()) {
            case R.id.set_interval_short_Minus:
                if (interval_Short > 1) {
                    interval_Short -= 1;
                    editor.putInt("interval_Short", interval_Short).apply();
                }
                break;
            case R.id.set_interval_short_Plus:
                interval_Short += 1;
                editor.putInt("interval_Short", interval_Short).apply();
                break;
            case R.id.set_interval_long_Minus:
                if (interval_Long > 20) {
                    interval_Long -= 10;
                    editor.putInt("interval_Long", interval_Long).apply();
                }
                break;
            case R.id.set_interval_long_Plus:
                interval_Long += 10;
                editor.putInt("interval_Long", interval_Long).apply();
                break;
            case R.id.set_default_Duration_Minus:
                if (default_Duration > 20) {
                    default_Duration -= 10;
                    editor.putInt("default_Duration", default_Duration).apply();
                }
                break;
            case R.id.set_default_Duration_Plus:
                default_Duration += 10;
                editor.putInt("default_Duration", default_Duration).apply();
                break;
            case R.id.set_sound:
                beepManner ^= true;
                editor.putBoolean("beepManner", beepManner).apply();
                break;
            case R.id.set_re_run:
                ReceiverCase = "ReRun";
                finish();
                break;
            default:
                utils.log("clicked","ID : "+v.getId());
        }
        showSoundSetting();
    }
    private void showSoundSetting() {
        final TextView tvSound = findViewById(R.id.set_sound);
        final TextView tvSoundText = findViewById(R.id.set_sound_text);
        String text = (beepManner) ? "끔" : "켬";
        tvSound.setText(text);
        text = getString(R.string.sound_when_manner_changed);
        text += (beepManner) ? "남" : "안 남";
        tvSoundText.setText(text);
        final TextView tvShort = findViewById(R.id.set_interval_short);
        final TextView tvLong = findViewById(R.id.set_interval_long);
        final TextView tvDuration = findViewById(R.id.set_default_Duration);
        text = ""+interval_Short; tvShort.setText(text);
        text = ""+interval_Long; tvLong.setText(text);
        text = ""+default_Duration; tvDuration.setText(text);
    }
}
