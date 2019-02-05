package com.urrecliner.andriod.keepitdown;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import static com.urrecliner.andriod.keepitdown.Vars.sdfDateTime;
import static com.urrecliner.andriod.keepitdown.Vars.timerActivity;
import static com.urrecliner.andriod.keepitdown.Vars.utils;

public class TimerActivity extends AppCompatActivity {

    Reminder reminder;
    private long id;
    private int uniqueId;
    private String subject;
    private int startHour, startMin;
    private int finishHour, finishMin;
    private boolean vibrate, active;
    private int startTime, durationMin = 0;       // in minutes
    Calendar calendar;
    private TextView tVDuration;
    TimePicker tp;
    boolean timeUpDown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.action_timer));

        Bundle data = getIntent().getExtras();
        try {
            reminder = (Reminder) data.getSerializable("reminder");
        }
        catch (Exception e) {
            Log.w("reminder","is null");
        }
        id = reminder.getId();
        uniqueId = reminder.getUniqueId();
        subject = reminder.getSubject();
        vibrate = reminder.getVibrate();
        active = true;

        timerActivity = this;
        calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND,0);
        subject = getResources().getString(R.string.action_timer);
        startHour = calendar.get(Calendar.HOUR_OF_DAY);
        startMin = calendar.get(Calendar.MINUTE);
        startTime = startHour * 60 + startMin;
        finishHour = startHour + 1;     // might be error if over 24 hour
        finishMin = startMin;
        durationMin = 60;
        tVDuration = findViewById(R.id.tm_duration);
        tp = findViewById(R.id.timePickerTimer);
        tp.setIs24HourView(true);
        tp.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int min) {
                if (timeUpDown)
                    return;
                if (hour * 60 + min <= startTime) {
                    warningTime();
                    return;
                }
                finishHour = hour; finishMin = min;
                durationMin = (finishHour - startHour) * 60 + finishMin - startMin;
                String text = (""+(100 + durationMin / 60)).substring(1) + " : " + (""+(100 + durationMin % 60)).substring(1)+  " 후";
                tVDuration.setText(text);
            }
        });
        buttonSetting();
        adjustTimePicker();
    }

    void buttonSetting() {
        final ImageView ib = findViewById(R.id.tm_vibrate);
        ib.setImageResource((vibrate)? R.mipmap.ic_phone_vibrate:R.mipmap.ic_phone_silent);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate ^= true;
                ib.setImageResource((vibrate)? R.mipmap.ic_phone_vibrate:R.mipmap.ic_phone_silent);
                v.invalidate();
            }
        });
        final TextView time10minus = findViewById(R.id.minus10Min);
        time10minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("onclick","minus 10 ");
                if (durationMin > 10) {
                    durationMin -= 10;
                    adjustTimePicker();
                }
                else
                    warningTime();
            }
        });

        final TextView time10plus = findViewById(R.id.plus10Min);
        time10plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("onclick","plus 10 ");
                durationMin += 10;
                adjustTimePicker();
            }
        });

        final TextView time30minus = findViewById(R.id.minus30Min);
        time30minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("onclick","minus 30 ");
                if (durationMin > 30) {
                    durationMin -= 30;
                    adjustTimePicker();
                }
                else
                    warningTime();
            }
        });

        final TextView time30plus = findViewById(R.id.plus30Min);
        time30plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("onclick","plus 30 ");
                durationMin += 30;
                adjustTimePicker();
            }
        });
    }

    private void warningTime() {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_allowed),Toast.LENGTH_LONG).show();
        Log.w("TIME BELOW","start ---");
        finishHour = startHour + 1;
        finishMin = startMin;
        durationMin = 60;
        adjustTimePicker();
    }

    void adjustTimePicker() {
        int time = startHour * 60 + startMin + durationMin;
        finishHour = time / 60; finishMin = time % 60;
        timeUpDown = true;  // to prevent double TimeChanged action
        tp.setHour(finishHour);
        tp.setMinute(finishMin);
        String text = (""+(100 + durationMin / 60)).substring(1) + " : " + (""+(100 + durationMin % 60)).substring(1) + " 후";
        tVDuration.setText(text);
        timeUpDown = false;
    }

    private void onSave() {

        boolean week[] = new boolean[7];
        Reminder reminder = new Reminder(id, uniqueId, subject, startHour, startMin, finishHour, finishMin,
                week, active, vibrate);
        DatabaseIO databaseIO = new DatabaseIO(this);
        databaseIO.update(reminder.getId(), reminder);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intentS = new Intent(this, AlarmReceiver.class);
        Bundle args = new Bundle();
        args.putSerializable("reminder", reminder);
        intentS.putExtra("DATA",args);
        intentS.putExtra("case","O");
        intentS.putExtra("uniqueId",reminder.getUniqueId());

        calendar.set(Calendar.HOUR_OF_DAY, finishHour);
        calendar.set(Calendar.MINUTE, finishMin);
        calendar.set(Calendar.SECOND,0);
        long nextStart = calendar.getTimeInMillis();

        PendingIntent pendingIntentS = PendingIntent.getBroadcast(TimerActivity.this, reminder.getUniqueId(), intentS, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, nextStart, pendingIntentS);
        utils.log("OneTime",subject + "  Activated " + sdfDateTime.format(nextStart));
        AlarmReceiver alarmReceiver = new AlarmReceiver();
        alarmReceiver.setMannerOn(vibrate);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_save) {
            onSave();
            return true;
        }

        if (id == R.id.action_cancel) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {

//        // hide the keyboard in order to avoid getTextBeforeCursor on inactive InputConnection
//        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        EditText et = findViewById(R.id.et_subject);
//        inputMethodManager.hideSoftInputFromWindow(et.getWindowToken(), 0);
//
        super.onPause();
    }
}
