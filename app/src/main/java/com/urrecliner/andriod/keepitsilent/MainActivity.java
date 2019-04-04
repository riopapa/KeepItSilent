package com.urrecliner.andriod.keepitsilent;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.urrecliner.andriod.keepitsilent.Vars.addViewWeek;
import static com.urrecliner.andriod.keepitsilent.Vars.beepManner;
import static com.urrecliner.andriod.keepitsilent.Vars.databaseIO;
import static com.urrecliner.andriod.keepitsilent.Vars.default_Duration;
import static com.urrecliner.andriod.keepitsilent.Vars.interval_Long;
import static com.urrecliner.andriod.keepitsilent.Vars.interval_Short;
import static com.urrecliner.andriod.keepitsilent.Vars.listViewWeek;
import static com.urrecliner.andriod.keepitsilent.Vars.mSettings;
import static com.urrecliner.andriod.keepitsilent.Vars.nowPosition;
import static com.urrecliner.andriod.keepitsilent.Vars.nowUniqueId;
import static com.urrecliner.andriod.keepitsilent.Vars.oneTimeId;
import static com.urrecliner.andriod.keepitsilent.Vars.reminder;
import static com.urrecliner.andriod.keepitsilent.Vars.sdfDateTime;
import static com.urrecliner.andriod.keepitsilent.Vars.stateCode;
import static com.urrecliner.andriod.keepitsilent.Vars.utils;
import static com.urrecliner.andriod.keepitsilent.Vars.weekName;

public class MainActivity extends AppCompatActivity {

    ListView lVReminder;
    com.urrecliner.andriod.keepitsilent.ListViewAdapter listViewAdapter;
    private ArrayList<com.urrecliner.andriod.keepitsilent.Reminder> reminders;
    private static String logID = "Main";
    private static String blank = "BLANK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.urrecliner.andriod.keepitsilent.Vars.mainActivity = this;
        com.urrecliner.andriod.keepitsilent.Vars.mainContext = this.getApplicationContext();
        utils = new com.urrecliner.andriod.keepitsilent.Utils();

        Intent intent = getIntent();
        if (intent == null) {
            utils.log(logID, "intent NULL");
            stateCode = "NULL";
        }
        else {
            stateCode = intent.getStringExtra("stateCode");
            if (stateCode == null)
                stateCode = blank;
        }
        utils.log(logID, stateCode);
        preparePermission(getApplicationContext());
        PermissionCheck permissionCheck = new PermissionCheck();
        if (!permissionCheck.isAllPermitted(this))
                return;
        setVariables();
        onStateCode();
    }

    void setVariables() {
        utils = new com.urrecliner.andriod.keepitsilent.Utils();
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        beepManner = mSettings.getBoolean("beepManner", true);
        interval_Short = mSettings.getInt("interval_Short", 5);
        interval_Long = mSettings.getInt("interval_Long", 30);
        default_Duration = mSettings.getInt("default_Duration", 60);

        com.urrecliner.andriod.keepitsilent.Vars.colorOn = ContextCompat.getColor(getBaseContext(), R.color.Navy);
        com.urrecliner.andriod.keepitsilent.Vars.colorInactiveBack = ContextCompat.getColor(getBaseContext(), R.color.gray);
        com.urrecliner.andriod.keepitsilent.Vars.colorOnBack = ContextCompat.getColor(getBaseContext(), R.color.colorPrimary);
        com.urrecliner.andriod.keepitsilent.Vars.colorOff = ContextCompat.getColor(getBaseContext(), R.color.BlueGray);
        com.urrecliner.andriod.keepitsilent.Vars.colorActive = ContextCompat.getColor(getBaseContext(), R.color.EarthBlue);
        com.urrecliner.andriod.keepitsilent.Vars.colorOffBack = ContextCompat.getColor(getBaseContext(), R.color.transparent);

        databaseIO = new com.urrecliner.andriod.keepitsilent.DatabaseIO();
        Cursor cursor = databaseIO.getAll();
        com.urrecliner.andriod.keepitsilent.Vars.dbCount = databaseIO.getCount(cursor);
        cursor.close();
//        utils.log(logID, "dbCount " + Vars.dbCount);
        if (com.urrecliner.andriod.keepitsilent.Vars.dbCount == 0)
            databaseIO.clearDatabase(getApplicationContext());

        weekName[0] = getResources().getString(R.string.week_0); weekName[1] = getResources().getString(R.string.week_1); weekName[2] = getResources().getString(R.string.week_2); weekName[3] = getResources().getString(R.string.week_3);
        weekName[4] = getResources().getString(R.string.week_4); weekName[5] = getResources().getString(R.string.week_5); weekName[6] = getResources().getString(R.string.week_6);

        listViewWeek[0] = R.id.lt_week0; listViewWeek[1] = R.id.lt_week1; listViewWeek[2] = R.id.lt_week2;
        listViewWeek[3] = R.id.lt_week3; listViewWeek[4] = R.id.lt_week4; listViewWeek[5] = R.id.lt_week5;
        listViewWeek[6] = R.id.lt_week6;

        addViewWeek[0] = R.id.av_week0; addViewWeek[1] = R.id.av_week1; addViewWeek[2] = R.id.av_week2;
        addViewWeek[3] = R.id.av_week3; addViewWeek[4] = R.id.av_week4; addViewWeek[5] = R.id.av_week5;
        addViewWeek[6] = R.id.av_week6;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        com.urrecliner.andriod.keepitsilent.Vars.xSize = size.x / 9;    // width / (7 week + 2)
    }

    void onStateCode() {
        String text;
        if (!stateCode.equals(blank))
            utils.log(logID, "switch="+ stateCode);
        switch (stateCode) {
            case "Timer":
                stateCode = "Tim@r";
                showArrayLists();
                break;
            case "Alarm":
                Bundle args = getIntent().getBundleExtra("DATA");
                reminder = (com.urrecliner.andriod.keepitsilent.Reminder) args.getSerializable("reminder");
                stateCode = "Al@rm";
                text = "New Alarm " + reminder.getSubject()+" Settled " + scheduleOneTask(reminder);
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                utils.log(logID, text);
                finish();
                break;
            case "ReRun":  // it means from receiver
                text = scheduleAllTasks();
                Toast.makeText(getApplicationContext(),text + "\n모두 재 설정되었습니다" ,Toast.LENGTH_LONG).show();
                stateCode = "R@n";
//                finish();
                break;
            case "Boot":  // it means from receiver
                scheduleAllTasks();
                stateCode = "B@@t";
                finish();
                break;
            case "AddUpdate":
//                Bundle arg = getIntent().getBundleExtra("DATA");
//                reminder = (Reminder) arg.getSerializable("reminder");
                text =  (reminder.getActive()) ? "Alarm Updated ": "Alarm Canceled ";
                text += scheduleOneTask(reminder) + " " + reminder.getSubject();
                utils.log(logID, text);
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                stateCode = "@ddUp";
                setContentView(R.layout.activity_main);
//                setVariables();
                showArrayLists();
                break;
            default:
                setContentView(R.layout.activity_main);
//                setVariables();
                showArrayLists();
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_add:
                intent = new Intent(MainActivity.this, com.urrecliner.andriod.keepitsilent.AddUpdateActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_setting:
                intent = new Intent(MainActivity.this, com.urrecliner.andriod.keepitsilent.SettingActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_reset:
                new AlertDialog.Builder(this)
                        .setTitle("데이터 초기화")
                        .setMessage("이미 설정되어 있는 테이블을 다 삭제합니다")
                        .setIcon(R.mipmap.icon_alert)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                databaseIO.clearDatabase(getApplicationContext());
                                showArrayLists();
                            }})
                        .setNegativeButton(android.R.string.no, null)
                        .show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showArrayLists() {
        lVReminder = findViewById(R.id.lv_reminder);
//        reminders = getAllDatabase();
        Cursor cursor = databaseIO.getAll();
        reminders = databaseIO.retrieveAllReminders(cursor);
        cursor.close();

        listViewAdapter = new com.urrecliner.andriod.keepitsilent.ListViewAdapter(this, reminders);
        lVReminder.setAdapter(listViewAdapter);
        lVReminder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                nowPosition = position;
                reminder = reminders.get(position);
                nowUniqueId = reminder.getUniqueId();
//                new callReminder().execute(reminders.get(position));
                Intent intent;
                if (nowUniqueId != oneTimeId)
                    intent = new Intent(MainActivity.this, com.urrecliner.andriod.keepitsilent.AddUpdateActivity.class);
                else
                    intent = new Intent(MainActivity.this, com.urrecliner.andriod.keepitsilent.OneTimeActivity.class);
                intent.putExtra("reminder", reminders.get(position));
                startActivity(intent);
            }
        });
    }

    String scheduleAllTasks() {

        databaseIO = new com.urrecliner.andriod.keepitsilent.DatabaseIO();
        Cursor cursor = databaseIO.getAll();
        reminders = databaseIO.retrieveAllReminders(cursor);
        cursor.close();
        StringBuilder text = new StringBuilder();
        for (com.urrecliner.andriod.keepitsilent.Reminder rm : reminders) {
            utils.log(logID, stateCode +" "+rm.getUniqueId()+":"+rm.getSubject());
            if (rm.getUniqueId() != oneTimeId && rm.getActive()) {
                long nextStart = com.urrecliner.andriod.keepitsilent.NextEventTime.calc(false, rm.getStartHour(), rm.getStartMin(), rm.getWeek());
                com.urrecliner.andriod.keepitsilent.NextAlarm.request(rm, nextStart,"S", getApplicationContext());
                long nextFinish = com.urrecliner.andriod.keepitsilent.NextEventTime.calc(true, rm.getFinishHour(), rm.getFinishMin(), rm.getWeek());
                com.urrecliner.andriod.keepitsilent.NextAlarm.request(rm, nextFinish,"F", getApplicationContext());
                text.append("\n").append(rm.getSubject()).append("\n").append(sdfDateTime.format(nextStart)).append(" ~ ").append(sdfDateTime.format(nextFinish));
                utils.log(logID, rm.getSubject() + " : " + sdfDateTime.format(nextStart) + ", " + sdfDateTime.format(nextFinish));
            }
        }
        return text.toString();
    }

    String scheduleOneTask(com.urrecliner.andriod.keepitsilent.Reminder reminder) {

        long nextStart = com.urrecliner.andriod.keepitsilent.NextEventTime.calc(false, reminder.getStartHour(), reminder.getStartMin(),reminder.getWeek());
        com.urrecliner.andriod.keepitsilent.NextAlarm.request(reminder, nextStart, "S", getApplicationContext());
        long nextFinish = com.urrecliner.andriod.keepitsilent.NextEventTime.calc(true, reminder.getFinishHour(), reminder.getFinishMin(),reminder.getWeek());
        com.urrecliner.andriod.keepitsilent.NextAlarm.request(reminder, nextFinish, "F", getApplicationContext());
        String text = sdfDateTime.format(nextStart) + ", " + sdfDateTime.format(nextFinish);
        utils.log(logID, text+" : "+reminder.getSubject());

        if (stateCode.equals("refresh")) {
            stateCode = "r@fresh";
            finish();
        }
        utils.deleteOldLogFiles();
        return text;
    }

    @Override
    protected void onResume() {
        super.onResume();
        utils.log(logID, "RESUME "+ stateCode);
        setVariables();
        onStateCode();
    }

//    @Override
//    public void onBackPressed() {  }

    void preparePermission(Context context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert nm != null;
        if (!nm.isNotificationPolicyAccessGranted()) {
            Intent intent = new
                    Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }
    }
}
