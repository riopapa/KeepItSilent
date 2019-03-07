package com.urrecliner.andriod.keepitdown;

import android.app.AlarmManager;
import android.app.PendingIntent;
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

import static com.urrecliner.andriod.keepitdown.Vars.ReceiverCase;
import static com.urrecliner.andriod.keepitdown.Vars.databaseIO;
import static com.urrecliner.andriod.keepitdown.Vars.default_Duration;
import static com.urrecliner.andriod.keepitdown.Vars.finishHour;
import static com.urrecliner.andriod.keepitdown.Vars.finishMin;
import static com.urrecliner.andriod.keepitdown.Vars.interval_Long;
import static com.urrecliner.andriod.keepitdown.Vars.interval_Short;
import static com.urrecliner.andriod.keepitdown.Vars.sdfDateTime;
import static com.urrecliner.andriod.keepitdown.Vars.timerActivity;
import static com.urrecliner.andriod.keepitdown.Vars.utils;

public class TimerActivity extends AppCompatActivity {

    Reminder reminder;
    private long id;
    private int uniqueId;
    private String subject;
    private int startHour, startMin;
    private boolean vibrate;
    private int durationMin = 0;       // in minutes
    Calendar calendar;
    private TextView tVDuration;
    TimePicker tp;
    boolean timePicker_UpDown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        timerActivity = this;
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(getResources().getString(R.string.action_timer));

        Bundle data = getIntent().getExtras();
        assert data != null;
        reminder = (Reminder) data.getSerializable("reminder");
        assert reminder != null;
        id = reminder.getId();
        uniqueId = reminder.getUniqueId();
        subject = reminder.getSubject();
        vibrate = reminder.getVibrate();
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

    private void onSave() {

        boolean week[] = new boolean[7];
        Reminder reminder = new Reminder(id, uniqueId, subject, startHour, startMin, finishHour, finishMin,
                week, true, vibrate);
        databaseIO.update(reminder.getId(), reminder);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        assert alarmManager != null;
        Intent intentS = new Intent(this, AlarmReceiver.class);
        Bundle args = new Bundle();
        args.putSerializable("reminder", reminder);
        intentS.putExtra("DATA",args);
        intentS.putExtra("case","O");

        calendar.set(Calendar.HOUR_OF_DAY, finishHour);
        calendar.set(Calendar.MINUTE, finishMin);
        calendar.set(Calendar.SECOND,0);
        long nextStart = calendar.getTimeInMillis();
        if (nextStart < System.currentTimeMillis())     // in case next day
            nextStart += 24 * 60 * 60000;
        PendingIntent pendingIntentS = PendingIntent.getBroadcast(TimerActivity.this, reminder.getUniqueId(),
                intentS, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, nextStart, pendingIntentS);
        utils.log("OneTime",subject + "  Activated " + sdfDateTime.format(nextStart));
        MannerMode.on(getApplicationContext(), subject, vibrate);
        ReceiverCase = "Timer";
        finish();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            onSave();
            return true;
        }

        if (id == R.id.action_cancel) {
            databaseIO.update(reminder.getId(), reminder);
            databaseIO.close();
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
