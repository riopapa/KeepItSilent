package com.urrecliner.andriod.keepitdown;

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

import static com.urrecliner.andriod.keepitdown.Vars.ReceiverCase;
import static com.urrecliner.andriod.keepitdown.Vars.addViewWeek;
import static com.urrecliner.andriod.keepitdown.Vars.beepManner;
import static com.urrecliner.andriod.keepitdown.Vars.databaseIO;
import static com.urrecliner.andriod.keepitdown.Vars.default_Duration;
import static com.urrecliner.andriod.keepitdown.Vars.interval_Long;
import static com.urrecliner.andriod.keepitdown.Vars.interval_Short;
import static com.urrecliner.andriod.keepitdown.Vars.listViewWeek;
import static com.urrecliner.andriod.keepitdown.Vars.mSettings;
import static com.urrecliner.andriod.keepitdown.Vars.nowPosition;
import static com.urrecliner.andriod.keepitdown.Vars.nowUniqueId;
import static com.urrecliner.andriod.keepitdown.Vars.oneTimeId;
import static com.urrecliner.andriod.keepitdown.Vars.reminder;
import static com.urrecliner.andriod.keepitdown.Vars.sdfDateTime;
import static com.urrecliner.andriod.keepitdown.Vars.utils;
import static com.urrecliner.andriod.keepitdown.Vars.weekName;

public class MainActivity extends AppCompatActivity {

    ListView lVReminder;
    ListViewAdapter listViewAdapter;
    private ArrayList<Reminder> reminders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Vars.mainActivity = this;
        Vars.mainContext = this;
        utils = new Utils();

        Intent intent = getIntent();
        if (intent == null) {
            utils.log("intent","NULL");
            ReceiverCase = "NULL";
        }
        else {
            ReceiverCase = intent.getStringExtra("ReceiverCase");
            if (ReceiverCase == null)
                ReceiverCase = "BLANK";
        }
        utils.log("MainActivity","- onCreate - ReceiverCase: "+ReceiverCase);
        preparePermission(getApplicationContext());
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        beepManner = mSettings.getBoolean("beepManner", true);
        interval_Short = mSettings.getInt("interval_Short", 5);
        interval_Long = mSettings.getInt("interval_Long", 30);
        default_Duration = mSettings.getInt("default_Duration", 60);
        setVariables();
        act_OnReceiverCase();
    }

    void setVariables() {
        utils = new Utils();
        Vars.colorOn = ContextCompat.getColor(getBaseContext(), R.color.Navy);
        Vars.colorInactiveBack = ContextCompat.getColor(getBaseContext(), R.color.gray);
        Vars.colorOnBack = ContextCompat.getColor(getBaseContext(), R.color.JeansBlue);
        Vars.colorOff = ContextCompat.getColor(getBaseContext(), R.color.BlueGray);
        Vars.colorActive = ContextCompat.getColor(getBaseContext(), R.color.EarthBlue);
        Vars.colorActiveBack = ContextCompat.getColor(getBaseContext(), R.color.JeansBlue);
        Vars.colorOffBack = ContextCompat.getColor(getBaseContext(), R.color.transparent);

        databaseIO = new DatabaseIO();
        Cursor cursor = databaseIO.getAll();
        Vars.dbCount = databaseIO.getCount(cursor);
        cursor.close();
//        utils.log("Initial", "dbCount " + Vars.dbCount);
        if (Vars.dbCount == 0)
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
        Vars.xSize = size.x / 9;
    }

    void act_OnReceiverCase() {
        String text;
        if (!ReceiverCase.equals("BLANK"))
            utils.log("switch",ReceiverCase);
        switch (ReceiverCase) {
            case "Timer":
                ReceiverCase = "TimerEnd";
                showArrayLists();
                break;
            case "Alarm":
//                Bundle args = getIntent().getBundleExtra("DATA");
//                reminder = (Reminder) args.getSerializable("reminder");
                requestBroadCastOne(reminder);
                ReceiverCase = "AlarmEnd";
                text = "New Alarm Settled " + reminder.getSubject()+" " + utils.hourMin(reminder.getStartHour(),reminder.getStartMin()) + " ~ " +utils.hourMin(reminder.getFinishHour(),reminder.getFinishMin());
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                utils.log(ReceiverCase,text);
                finish();
                break;
            case "ReRun":  // it means from receiver
                text = requestBroadCastAll();
                Toast.makeText(getApplicationContext(),text + "\n모두 재 설정되었습니다" ,Toast.LENGTH_LONG).show();
                ReceiverCase = "RunEnd";
//                finish();
                break;
            case "Boot":  // it means from receiver
                requestBroadCastAll();
                ReceiverCase = "BootEnd";
                finish();
                break;
            case "AddUpdate":
//                Bundle arg = getIntent().getBundleExtra("DATA");
//                reminder = (Reminder) arg.getSerializable("reminder");
                requestBroadCastOne(reminder);
                ReceiverCase = "AddEnd";
                text =  (reminder.getActive()) ? "Alarm Updated ": "Alarm Canceled ";
                text += reminder.getSubject()+" " + utils.hourMin(reminder.getStartHour(),reminder.getStartMin()) + " ~ " +utils.hourMin(reminder.getFinishHour(),reminder.getFinishMin());
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                setContentView(R.layout.activity_main);
//                setVariables();
                showArrayLists();
                break;
            default:
//                if (ReceiverCase.equals("AddUpdate")) {
//                    utils.log("From", "AddUpdate");
//                    Bundle arg = getIntent().getBundleExtra("DATA");
//                    reminder = (Reminder) arg.getSerializable("reminder");
//                    requestBroadCastOne(reminder);
//                    ReceiverCase = "AddEnd";
//                }
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

        listViewAdapter = new ListViewAdapter(this, reminders);
//        utils.log("listViewAdapter","BUILD");
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
                    intent = new Intent(MainActivity.this, AddUpdateActivity.class);
                else
                    intent = new Intent(MainActivity.this, TimerActivity.class);
                intent.putExtra("reminder", reminders.get(position));
                startActivity(intent);
            }
        });
    }

    String requestBroadCastAll() {

        databaseIO = new DatabaseIO();
        Cursor cursor = databaseIO.getAll();
        reminders = databaseIO.retrieveAllReminders(cursor);
        cursor.close();
        StringBuilder text = new StringBuilder();
        for (Reminder rm1 : reminders) {
            utils.log(ReceiverCase, "uid "+rm1.getSubject());
            if (rm1.getUniqueId() != oneTimeId && rm1.getActive()) {
                long nextStart = NextEventTime.calc(false, rm1.getStartHour(), rm1.getStartMin(), rm1.getWeek());
                NextAlarm.request(rm1, nextStart,"S");
                long nextFinish = NextEventTime.calc(true, rm1.getFinishHour(), rm1.getFinishMin(), rm1.getWeek());
                NextAlarm.request(rm1, nextFinish,"F");
                text.append("\n").append(rm1.getSubject()).append("\n").append(sdfDateTime.format(nextStart)).append(" ~ ").append(sdfDateTime.format(nextFinish));
                utils.log(ReceiverCase,rm1.getSubject() + " : " + sdfDateTime.format(nextStart) + " ~ " + sdfDateTime.format(nextFinish));
            }
        }
        return text.toString();
    }

    void requestBroadCastOne(Reminder reminder) {

        long nextStart = NextEventTime.calc(false, reminder.getStartHour(), reminder.getStartMin(),reminder.getWeek());
        NextAlarm.request(reminder, nextStart, "S");
        long nextFinish = NextEventTime.calc(true, reminder.getFinishHour(), reminder.getFinishMin(),reminder.getWeek());
        NextAlarm.request(reminder, nextFinish, "F");
        utils.log(ReceiverCase,reminder.getSubject() + " : " + sdfDateTime.format(nextStart) + " ~ " + sdfDateTime.format(nextFinish));

        if (ReceiverCase.equals("refresh")) {
            ReceiverCase = "refreshEnd";
            finish();
        }
        utils.deleteOldFiles();
    }

    @Override
    protected void onResume() {
        super.onResume();
        utils.log("RESUME","-- ReceiverCase : "+ReceiverCase);
        setVariables();
        act_OnReceiverCase();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

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
