package com.urrecliner.keepitsilent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import static com.urrecliner.keepitsilent.Vars.STATE_ONETIME;
import static com.urrecliner.keepitsilent.Vars.default_Duration;
import static com.urrecliner.keepitsilent.Vars.interval_Long;
import static com.urrecliner.keepitsilent.Vars.interval_Short;
import static com.urrecliner.keepitsilent.Vars.mainActivity;
import static com.urrecliner.keepitsilent.Vars.silentIdx;
import static com.urrecliner.keepitsilent.Vars.silentInfos;
import static com.urrecliner.keepitsilent.Vars.stateCode;
import static com.urrecliner.keepitsilent.Vars.utils;

public class OneTimeActivity extends AppCompatActivity {

    SilentInfo silentInfo;
    private String subject;
    private int startHour, startMin, finishHour, finishMin;
    private boolean vibrate;
    private int durationMin = 0;       // in minutes
    Calendar calendar;
    private TextView tVDuration;
    TimePicker tp;
    boolean timePicker_UpDown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent closeIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        getApplicationContext().sendBroadcast(closeIntent);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(getResources().getString(R.string.silent_Once));

//        Bundle data = getIntent().getExtras();
//        assert data != null;
//        silentInfo = (SilentInfo) data.getSerializable("silentInfo");
        silentIdx = 0;
        silentInfo = silentInfos.get(silentIdx);
        subject = silentInfo.getSubject();
        vibrate = silentInfo.getVibrate();
        calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND,0);
        startHour = calendar.get(Calendar.HOUR_OF_DAY);
        startMin = calendar.get(Calendar.MINUTE);
        finishHour = startHour + 1;     // default is 60 min.
        finishMin = startMin;
        durationMin = default_Duration;
        tVDuration = findViewById(R.id.tm_duration);
        tp = findViewById(R.id.timePickerTimer);
        tp.setIs24HourView(true);
        tp.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int min) {
                if (timePicker_UpDown)
                    return;
                finishHour = hour; finishMin = min;
                durationMin = (finishHour - startHour) * 60 + finishMin - startMin;
                String text;
                if (durationMin > 1)
                    text = (""+(100 + durationMin / 60)).substring(1) + " : " + (""+(100 + durationMin % 60)).substring(1)+  " 후";
                else
                    text = getString(R.string.already_passed_time);
                tVDuration.setText(text);
            }
        });
        buildScreen();
        buttonSetting();
        adjustTimePicker();
    }

    void buildScreen() {
        String text;
        TextView tv;
        tv = findViewById(R.id.minus10Min); text = interval_Short+"분↓"; tv.setText(text);
        tv = findViewById(R.id.plus10Min); text = interval_Short+"분↑"; tv.setText(text);
        tv = findViewById(R.id.minus30Min); text = interval_Long+"분↓"; tv.setText(text);
        tv = findViewById(R.id.plus30Min); text = interval_Long+"분↑"; tv.setText(text);
    }
    void buttonSetting() {
        final ImageView iVVibrate = findViewById(R.id.tm_vibrate);
        iVVibrate.setImageResource((vibrate)? R.mipmap.ic_phone_vibrate:R.mipmap.ic_phone_silent);
        iVVibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate ^= true;
                iVVibrate.setImageResource((vibrate)? R.mipmap.ic_phone_vibrate:R.mipmap.ic_phone_silent);
                v.invalidate();
            }
        });
        final TextView time10minus = findViewById(R.id.minus10Min);
        time10minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (durationMin > interval_Short) {
                    durationMin -= interval_Short;
                    adjustTimePicker();
                }
            }
        });

        final TextView time10plus = findViewById(R.id.plus10Min);
        time10plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                durationMin += interval_Short;
                adjustTimePicker();
            }
        });

        final TextView time30minus = findViewById(R.id.minus30Min);
        time30minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (durationMin > interval_Long) {
                    durationMin -= interval_Long;
                    adjustTimePicker();
                }
            }
        });

        final TextView time30plus = findViewById(R.id.plus30Min);
        time30plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                durationMin += interval_Long;
                adjustTimePicker();
            }
        });
    }

    void adjustTimePicker() {
        int time = startHour * 60 + startMin + durationMin;
        finishHour = time / 60; finishMin = time % 60;
        if (finishHour >= 24)
            finishHour -= 24;
        timePicker_UpDown = true;  // to prevent double TimeChanged action
        tp.setHour(finishHour);
        tp.setMinute(finishMin);
        String text = (""+(100 + durationMin / 60)).substring(1) + " : " + (""+(100 + durationMin % 60)).substring(1) + " 후";
        tVDuration.setText(text);
        timePicker_UpDown = false;
        tp.invalidate();
    }

    private void saveOneTime() {

        boolean [] week = new boolean[]{true, true, true, true, true, true, true};
        silentInfo = new SilentInfo(subject, startHour, startMin, finishHour, finishMin, week, true, vibrate);
        silentInfos.set(0, silentInfo);
        utils.saveSharedPrefTables();
        MannerMode.turnOn(getApplicationContext(), subject, vibrate);
        stateCode = STATE_ONETIME;
        if (mainActivity == null)
            mainActivity = new MainActivity();
        mainActivity.scheduleNextTask("One Time");
        finish();
//
//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        assert alarmManager != null;
//        Intent intentS = new Intent(this, AlarmReceiver.class);
//        Bundle args = new Bundle();
//        args.putSerializable("silentInfo", silentInfo);
//        intentS.putExtra("DATA",args);
//        intentS.putExtra("case","O");
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            saveOneTime();
            return true;
        }

        if (id == R.id.action_cancel) {
            silentInfos.set(silentIdx, silentInfo);
            utils.saveSharedPrefTables();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}