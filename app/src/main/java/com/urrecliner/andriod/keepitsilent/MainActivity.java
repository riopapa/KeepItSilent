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

import static com.urrecliner.andriod.keepitsilent.Vars.NORMAL_ID;
import static com.urrecliner.andriod.keepitsilent.Vars.ONETIME_ID;
import static com.urrecliner.andriod.keepitsilent.Vars.addViewWeek;
import static com.urrecliner.andriod.keepitsilent.Vars.beepManner;
import static com.urrecliner.andriod.keepitsilent.Vars.colorActive;
import static com.urrecliner.andriod.keepitsilent.Vars.colorInactiveBack;
import static com.urrecliner.andriod.keepitsilent.Vars.colorOff;
import static com.urrecliner.andriod.keepitsilent.Vars.colorOffBack;
import static com.urrecliner.andriod.keepitsilent.Vars.colorOn;
import static com.urrecliner.andriod.keepitsilent.Vars.colorOnBack;
import static com.urrecliner.andriod.keepitsilent.Vars.databaseIO;
import static com.urrecliner.andriod.keepitsilent.Vars.default_Duration;
import static com.urrecliner.andriod.keepitsilent.Vars.interval_Long;
import static com.urrecliner.andriod.keepitsilent.Vars.interval_Short;
import static com.urrecliner.andriod.keepitsilent.Vars.listViewWeek;
import static com.urrecliner.andriod.keepitsilent.Vars.mSettings;
import static com.urrecliner.andriod.keepitsilent.Vars.reminder;
import static com.urrecliner.andriod.keepitsilent.Vars.sdfDateTime;
import static com.urrecliner.andriod.keepitsilent.Vars.stateCode;
import static com.urrecliner.andriod.keepitsilent.Vars.utils;
import static com.urrecliner.andriod.keepitsilent.Vars.weekName;
import static com.urrecliner.andriod.keepitsilent.Vars.xSize;

public class MainActivity extends AppCompatActivity {

    ListView lVReminder;
    ListViewAdapter listViewAdapter;
    private ArrayList<Reminder> reminders;
    private static String logID = "Main";
    private static String blank = "BLANK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Vars.mainActivity = this;
        Vars.mainContext = this.getApplicationContext();
        if (utils == null)
            utils = new Utils();

        Intent intent = getIntent();
        if (intent == null) {
            stateCode = "NULL";
        } else {
            stateCode = intent.getStringExtra("stateCode");
            if (stateCode == null)
                stateCode = blank;
        }
        utils.log(logID, stateCode);
        preparePermission(getApplicationContext());
        PermissionCheck permissionCheck = new PermissionCheck();
        if (!permissionCheck.isAllPermitted(this))
            return;
        utils.deleteOldLogFiles();
        if (!stateCode.equals(blank))
            return;
        setContentView(R.layout.activity_main);
        setVariables();
        onStateCode();
    }

//    void setAwakenAlways(Context context) {
//        ComponentName receiver = new ComponentName(context, MainActivity.class);
//        PackageManager pm = context.getPackageManager();
//        pm.setComponentEnabledSetting(receiver,
//                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                PackageManager.DONT_KILL_APP);
//    }

    void setVariables() {

        if (utils == null)
            utils = new Utils();
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        beepManner = mSettings.getBoolean("beepManner", true);
        interval_Short = mSettings.getInt("interval_Short", 5);
        interval_Long = mSettings.getInt("interval_Long", 30);
        default_Duration = mSettings.getInt("default_Duration", 60);
        colorOn = ContextCompat.getColor(getBaseContext(), R.color.Navy);
        colorInactiveBack = ContextCompat.getColor(getBaseContext(), R.color.gray);
        colorOnBack = ContextCompat.getColor(getBaseContext(), R.color.colorPrimary);
        colorOff = ContextCompat.getColor(getBaseContext(), R.color.BlueGray);
        colorActive = ContextCompat.getColor(getBaseContext(), R.color.EarthBlue);
        colorOffBack = ContextCompat.getColor(getBaseContext(), R.color.transparent);

        databaseIO = new DatabaseIO();
        Cursor cursor = databaseIO.getAll();
        long dbCount = databaseIO.getCount(cursor);
        cursor.close();
        if (dbCount == 0)
            databaseIO.clearDatabase(getApplicationContext());

        weekName[0] = getResources().getString(R.string.week_0);    weekName[1] = getResources().getString(R.string.week_1);    weekName[2] = getResources().getString(R.string.week_2);    weekName[3] = getResources().getString(R.string.week_3);
        weekName[4] = getResources().getString(R.string.week_4);    weekName[5] = getResources().getString(R.string.week_5);    weekName[6] = getResources().getString(R.string.week_6);

        listViewWeek[0] = R.id.lt_week0;    listViewWeek[1] = R.id.lt_week1;    listViewWeek[2] = R.id.lt_week2;
        listViewWeek[3] = R.id.lt_week3;    listViewWeek[4] = R.id.lt_week4;    listViewWeek[5] = R.id.lt_week5;
        listViewWeek[6] = R.id.lt_week6;

        addViewWeek[0] = R.id.av_week0; addViewWeek[1] = R.id.av_week1; addViewWeek[2] = R.id.av_week2;
        addViewWeek[3] = R.id.av_week3; addViewWeek[4] = R.id.av_week4; addViewWeek[5] = R.id.av_week5;
        addViewWeek[6] = R.id.av_week6;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        xSize = size.x / 9;    // width / (7 week + 2)
    }

    void onStateCode() {

        if (!stateCode.equals(blank))
            utils.log(logID, "State=" + stateCode);
        switch (stateCode) {
            case "Timer":
                stateCode = "@" + stateCode;
                finishAffinity();
//                showArrayLists();
                break;

            case "Alarm":
//                Bundle args = getIntent().getBundleExtra("DATA");
//                try {
//                    Reminder reminder1 = (Reminder) args.getSerializable("reminder");
//                    if (reminder1 != null)
//                        reminder = reminder1;
//                } catch (NullPointerException ne) {
//                    utils.log(stateCode, "reminder1 is null");
//                }
//                if (reminder != null) {
//                    stateCode = "@" + stateCode;
//                    text = "New Alarm " + reminder.getSubject() + " Settled " + scheduleOneTask(reminder);
//                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
//                    utils.log(logID, text);
//                    finish();
//                } else
//                    scheduleAllTasks();
                stateCode = "@" + stateCode;
                scheduleNextTask("Next Alarm Settled ");
                finishAffinity();
//                finishAffinity();
                break;

            case "Loop":  // it means from receiver
                stateCode = "@" + stateCode;
                scheduleLooping();
                finishAffinity();
                break;

            case "ReRun":  // it means from receiver
                stateCode = "@" + stateCode;
                scheduleNextTask("ReRun Activated ");
                finishAffinity();
                break;

            case "Boot":  // it means from receiver
                stateCode = "@" + stateCode;
                scheduleNextTask("Boot triggered new Alarm ");
                finishAffinity();
                break;

            case "AddUpdate":
                stateCode = "@" + stateCode;
                scheduleNextTask("Alarm Updated ");
                finishAffinity();
                break;

            default:
                setContentView(R.layout.activity_main);
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
                intent = new Intent(MainActivity.this, AddUpdateActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_setting:
                intent = new Intent(MainActivity.this, SettingActivity.class);
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
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showArrayLists() {

        lVReminder = findViewById(R.id.lv_reminder);
        Cursor cursor = databaseIO.getAll();
        reminders = databaseIO.retrieveAllReminders(cursor);
        cursor.close();

        listViewAdapter = new ListViewAdapter(this, reminders);
        lVReminder.setAdapter(listViewAdapter);
        lVReminder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Vars.nowPosition = position;
                reminder = reminders.get(position);
                int uniqueId = reminder.getUniqueId();
//                new callReminder().execute(reminders.get(position));
                Intent intent;
                if (uniqueId != ONETIME_ID)
                    intent = new Intent(MainActivity.this, AddUpdateActivity.class);
                else
                    intent = new Intent(MainActivity.this, OneTimeActivity.class);
                intent.putExtra("reminder", reminders.get(position));
                startActivity(intent);
            }
        });
    }

    void scheduleNextTask(String headInfo) {
        long nextTime = System.currentTimeMillis() + (long)2400*60*60*1000;
        Reminder rmNext = reminder;
        String StartFinish = "S";
        databaseIO = new DatabaseIO();
        Cursor cursor = databaseIO.getAll();
        reminders = databaseIO.retrieveAllReminders(cursor);
        cursor.close();
        boolean[] week;
        for (Reminder rm : reminders) {
            if (rm.getActive()) {
                if (rm.getUniqueId() == ONETIME_ID)
                    week = new boolean[]{true, true, true, true, true, true, true};
                else
                    week = rm.getWeek();
                long nextStart = CalculateNext.calc(false, rm.getStartHour(), rm.getStartMin(), week);
                if (nextStart < nextTime) {
                    nextTime = nextStart;
                    rmNext = rm;
                    StartFinish = "S";
                }
                long nextFinish = CalculateNext.calc(true, rm.getFinishHour(), rm.getFinishMin(), week);
                if (nextFinish < nextTime) {
                    nextTime = nextFinish;
                    rmNext = rm;
                    StartFinish = (rm.getUniqueId() == ONETIME_ID) ? "O":"F";
                }
            }
        }
//        rmNext.setUniqueId((int) (System.currentTimeMillis() % 10000000L));
//        rmNext.setUniqueId(NORMAL_ID + (int) (Character) StartFinish.charAt(0));
        NextAlarm.request(rmNext, nextTime, StartFinish, getApplicationContext());
        String msg = headInfo + "\n" + rmNext.getSubject() + "\n" + sdfDateTime.format(nextTime) + " " + StartFinish;
        utils.log(logID, msg);
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        utils.logE(logID,sdfDateTime.format(nextTime) + " " + StartFinish + " " + rmNext.getSubject());
    }

//        String scheduleLoopTask() {
//
//        long nextStart = System.currentTimeMillis() + 30*60*1000;
//        reminder.setActiveTrue();
//        NextAlarm.request(reminder, nextStart, "L", getApplicationContext());
//        String text = sdfDateTime.format(nextStart);
//        utils.log(logID, text + " : Loop");
//        return text;
//    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!stateCode.equals("Loop"))
            utils.log(logID, "RESUME " + stateCode);
        if (!stateCode.equals(blank)) {
            setVariables();
            onStateCode();
        }
        else
            scheduleLooping();
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    void scheduleLooping() {
        reminder.setUniqueId(NORMAL_ID - 11);
        reminder.setActive(true);
        reminder.setSubject("Looping");
        long nextTime = System.currentTimeMillis() + 58*60000;
        NextAlarm.request(reminder, nextTime, "L", getApplicationContext());
//        utils.logE(logID,reminder.getSubject() + " " + sdfDateTime.format(nextTime));
        scheduleNextTask("Looping");
    }
}
