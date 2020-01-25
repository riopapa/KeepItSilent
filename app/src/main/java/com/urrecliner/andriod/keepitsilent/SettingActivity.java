package com.urrecliner.andriod.keepitsilent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import static com.urrecliner.andriod.keepitsilent.Vars.beepManner;
import static com.urrecliner.andriod.keepitsilent.Vars.default_Duration;
import static com.urrecliner.andriod.keepitsilent.Vars.editor;
import static com.urrecliner.andriod.keepitsilent.Vars.interval_Long;
import static com.urrecliner.andriod.keepitsilent.Vars.interval_Short;
import static com.urrecliner.andriod.keepitsilent.Vars.sharedPreferences;
import static com.urrecliner.andriod.keepitsilent.Vars.utils;

public class SettingActivity extends AppCompatActivity {

    static private String logID = "Setting";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        showSoundSetting();
    }

    public void adjustSetting(View v) {
        editor = sharedPreferences.edit();
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
            default:
                utils.log(logID,"click ID : "+v.getId());
        }
        showSoundSetting();
    }
    private void showSoundSetting() {
        TextView tv;
        String text;
        tv = findViewById(R.id.set_sound); text = (beepManner) ? "끔" : "켬"; tv.setText(text);
        tv = findViewById(R.id.set_sound_text); text = getString(R.string.sound_when_manner_changed) + ((beepManner) ? "남" : "안 남");
        tv.setText(text);
        tv = findViewById(R.id.set_interval_short); text = ""+interval_Short; tv.setText(text);
        tv = findViewById(R.id.set_interval_long); text = ""+interval_Long; tv.setText(text);
        tv = findViewById(R.id.set_default_Duration); text = ""+default_Duration; tv.setText(text);
    }
}
