package com.urrecliner.andriod.keepitdown;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.Serializable;
import java.util.Calendar;

public class AddActivity extends AppCompatActivity {

    private EditText et_subject;
    private TextView tv_set_time;
    private Button btn_set_time;

    private String subject, content;
    private Calendar calendar;
    private long id;
    private long timeStart, timeFinish;
    private boolean [] week;

    private boolean isNew = true;
    private Reminder reminder;
    Utils utils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
//        actionBar.setTitle(getResources().getString(R.string.action_add));
        actionBar.setTitle("추가/수정");
        utils = new Utils();
        prevtime = timeStart;
        initUI();

        getDefaultInfo();

        try {
            Bundle data = getIntent().getExtras();
            reminder = (Reminder) data.getSerializable("reminder");
            if (reminder != null) {
                isNew = false;
                et_subject.setText(reminder.getSubject());
                et_content.setText(reminder.getContent());
                tv_set_time.setText(reminder.getDateTime());
            }
        } catch (Exception e) {
            Log.d("huyhungdinh", "Error" + e.toString());
        }
    }

    private void initUI() {
        et_subject = (EditText) findViewById(R.id.et_subject);
        et_content = (EditText) findViewById(R.id.et_content);

        tv_set_time = (TextView) findViewById(R.id.tv_set_time);

        btn_set_time = (Button) findViewById(R.id.btn_set_time);
        btn_set_time.setOnClickListener(buttonClick);
    }

    public void getDefaultInfo() {
        timeStart = System.currentTimeMillis();
        tv_set_time.setText(utils.getTimeFormat(timeStart));
    }

    View.OnClickListener buttonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_set_time:
                    dateTimeDialog();
                    break;
            }
        }
    };

    private void dateTimeDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_datetimepicker_layout, null);
        final DatePicker dp = (DatePicker) dialogView.findViewById(R.id.date_picker);
        final TimePicker tp = (TimePicker) dialogView.findViewById(R.id.time_picker);
        tp.setIs24HourView(true);

        dialog.setView(dialogView);
        dialog.setTitle(R.string.setTime);
        dialog.setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, dp.getYear());
                calendar.set(Calendar.MONTH, dp.getMonth());
                calendar.set(Calendar.DAY_OF_MONTH, dp.getDayOfMonth());
                calendar.set(Calendar.HOUR_OF_DAY, tp.getHour());
                calendar.set(Calendar.MINUTE, tp.getMinute());
                timeStart = calendar.getTimeInMillis();

                String strTime = (utils.getFullDateTimeFormat(timeStart));
                tv_set_time.setText(strTime);
            }
        });
        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.create();
        dialog.show();
    }

        private void onSave() {
        subject = et_subject.getText().toString();
        content = et_content.getText().toString();
        Reminder Re_minder = new Reminder(id, subject, content, timeStart, timeFinish, 0, week);
        DatabaseIO databaseIO = new DatabaseIO(this);
        if (isNew) {
            id = databaseIO.insert(Re_minder);
            Re_minder.setId(id);
        } else {
            databaseIO.update(reminder.getId(), Re_minder);
        }
        databaseIO.close();
//        Log.w("onsave reminder", Re_minder.toString());
        Intent intent = new Intent(getApplicationContext(), Reminder.class);
        PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);

        Intent myIntent = new Intent(AddActivity.this, AlarmReceiver.class);
//
//        boolean alarmExists =
//                (PendingIntent.getBroadcast(this, 0,
//                        myIntent,
//                        PendingIntent.FLAG_NO_CREATE) != null);
//
//        if (alarmExists) {
//            Log.e("alarm","Cancel Old");
//            alarmManager.cancel((AlarmManager.OnAlarmListener) myIntent);
//        }
        Bundle args = new Bundle();
        args.putSerializable("reminder",(Serializable)Re_minder);
        myIntent.putExtra("DATA",args);
        final int _id = (int) System.currentTimeMillis();
        PendingIntent appIntent = PendingIntent.getBroadcast(AddActivity.this, _id, myIntent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), appIntent);

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
}
