package com.urrecliner.keepitsilent;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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
import java.util.Timer;
import java.util.TimerTask;

import static com.urrecliner.keepitsilent.Vars.STATE_ADD_UPDATE;
import static com.urrecliner.keepitsilent.Vars.STATE_ALARM;
import static com.urrecliner.keepitsilent.Vars.STATE_BOOT;
import static com.urrecliner.keepitsilent.Vars.STATE_ONETIME;
import static com.urrecliner.keepitsilent.Vars.actionHander;
import static com.urrecliner.keepitsilent.Vars.addNewSilent;
import static com.urrecliner.keepitsilent.Vars.addViewWeek;
import static com.urrecliner.keepitsilent.Vars.beepManner;
import static com.urrecliner.keepitsilent.Vars.colorActive;
import static com.urrecliner.keepitsilent.Vars.colorInactiveBack;
import static com.urrecliner.keepitsilent.Vars.colorOff;
import static com.urrecliner.keepitsilent.Vars.colorOffBack;
import static com.urrecliner.keepitsilent.Vars.colorOn;
import static com.urrecliner.keepitsilent.Vars.colorOnBack;
import static com.urrecliner.keepitsilent.Vars.default_Duration;
import static com.urrecliner.keepitsilent.Vars.interval_Long;
import static com.urrecliner.keepitsilent.Vars.interval_Short;
import static com.urrecliner.keepitsilent.Vars.listViewWeek;
import static com.urrecliner.keepitsilent.Vars.mainActivity;
import static com.urrecliner.keepitsilent.Vars.mainContext;
import static com.urrecliner.keepitsilent.Vars.sdfDateTime;
import static com.urrecliner.keepitsilent.Vars.sdfTime;
import static com.urrecliner.keepitsilent.Vars.sharedPreferences;
import static com.urrecliner.keepitsilent.Vars.silentIdx;
import static com.urrecliner.keepitsilent.Vars.silentInfo;
import static com.urrecliner.keepitsilent.Vars.silentInfos;
import static com.urrecliner.keepitsilent.Vars.stateCode;
import static com.urrecliner.keepitsilent.Vars.utils;
import static com.urrecliner.keepitsilent.Vars.weekName;
import static com.urrecliner.keepitsilent.Vars.xSize;

public class MainActivity extends AppCompatActivity {

    ListView lVReminder;
    com.urrecliner.keepitsilent.ListViewAdapter listViewAdapter;
    private static String logID = "Main";
    private static String blank = "BLANK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = this;
        mainContext = this.getApplicationContext();
        if (utils == null)
            utils = new Utils();
        utils.log(logID, "Main start ");
        askPermission();

        Intent intent = getIntent();
        if (intent == null) {
            stateCode = "NULL";
        } else {
            stateCode = intent.getStringExtra("stateCode");
            if (stateCode == null)
                stateCode = blank;
        }
        utils.log(logID, stateCode);
        utils.deleteOldLogFiles();
        if (!stateCode.equals(blank))
            return;
        setContentView(R.layout.activity_main);
        setVariables();
        actOnStateCode();
        new Timer().schedule(new TimerTask() {
            public void run () {
                updateNotificationBar("xx:xx","not activated yet","S");
            }
        }, 100);
        actionHander = new Handler() { public void handleMessage(Message msg) { actOnStateCode(); }};

    }

    void updateNotificationBar(String dateTime, String subject, String startFinish) {
        Intent updateIntent = new Intent(MainActivity.this, NotificationService.class);
        updateIntent.putExtra("isUpdate", true);
        updateIntent.putExtra("dateTime", dateTime);
        updateIntent.putExtra("subject", subject);
        updateIntent.putExtra("startFinish", startFinish);
        startService(updateIntent);
    }

    void setVariables() {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        beepManner = sharedPreferences.getBoolean("beepManner", true);
        interval_Short = sharedPreferences.getInt("interval_Short", 5);
        interval_Long = sharedPreferences.getInt("interval_Long", 30);
        default_Duration = sharedPreferences.getInt("default_Duration", 60);
        colorOn = ContextCompat.getColor(getBaseContext(), R.color.colorOn);
        colorInactiveBack = ContextCompat.getColor(getBaseContext(), R.color.colorInactiveBack);
        colorOnBack = ContextCompat.getColor(getBaseContext(), R.color.colorOnBack);
        colorOff = ContextCompat.getColor(getBaseContext(), R.color.colorOff);
        colorActive = ContextCompat.getColor(getBaseContext(), R.color.colorActive);
        colorOffBack = ContextCompat.getColor(getBaseContext(), R.color.colorOffBack);

        silentInfos = utils.readSharedPrefTables();
        if (silentInfos.size() == 0)
            initiateSilentInfos();

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

        // get permission for silent mode
        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (!notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }
    }

    void actOnStateCode() {

        if (!stateCode.equals(blank))
            utils.log(logID, "State=" + stateCode);
        switch (stateCode) {
            case STATE_ONETIME:
                stateCode = "@" + stateCode;
                finish();
                break;

            case STATE_ALARM:
                stateCode = "@" + stateCode;
                scheduleNextTask("Next Alarm Settled ");
                finish();
                break;

            case STATE_BOOT:  // it means from receiver
                stateCode = "@" + stateCode;
                scheduleNextTask("Boot triggered new Alarm ");
                finish();
                break;

            case STATE_ADD_UPDATE:
                stateCode = "@" + stateCode;
                break;

            default:
                break;
        }
        setContentView(R.layout.activity_main);
        showArrayLists();
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
                addNewSilent = true;
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
                                initiateSilentInfos();
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
        listViewAdapter = new ListViewAdapter(this);
        lVReminder.setAdapter(listViewAdapter);
        lVReminder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                silentIdx = position;
                silentInfo = silentInfos.get(position);
                Intent intent;
                if (silentIdx != 0) {
                    addNewSilent = false;
                    intent = new Intent(MainActivity.this, AddUpdateActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(MainActivity.this, OneTimeActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    void scheduleNextTask(String headInfo) {
        long nextTime = System.currentTimeMillis() + (long)240*60*60*1000;
        int saveIdx = 0;
        String StartFinish = "S";
        boolean[] week;
        for (int idx = 0; idx < silentInfos.size(); idx++) {
            SilentInfo silentTemp = silentInfos.get(idx);
            if (silentTemp.getActive()) {
                week = silentTemp.getWeek();
                long nextStart = CalculateNext.calc(false, silentTemp.getStartHour(), silentTemp.getStartMin(), week, 0);
                if (nextStart < nextTime) {
                    nextTime = nextStart;
                    saveIdx = idx;
                    StartFinish = "S";
                }

                long nextFinish = CalculateNext.calc(true, silentTemp.getFinishHour(), silentTemp.getFinishMin(), week, (silentTemp.getStartHour()> silentTemp.getFinishHour()) ? (long)24*60*60*1000 : 0);
                if (nextFinish < nextTime) {
                    nextTime = nextFinish;
                    saveIdx = idx;
                    StartFinish = (idx == 0) ? "O":"F";
                }
            }
        }
        silentInfo = silentInfos.get(saveIdx);
        NextAlarm.request(silentInfo, nextTime, StartFinish, getApplicationContext());
        String msg = headInfo + "\n" + silentInfo.getSubject() + "\n" + sdfDateTime.format(nextTime) + " " + StartFinish;
//        utils.log(logID, msg);
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        utils.log(logID,sdfDateTime.format(nextTime) + " " + StartFinish + " " + silentInfo.getSubject());
        updateNotificationBar (sdfTime.format(nextTime), silentInfo.getSubject(), StartFinish);
        if (stateCode.equals("@back") && StartFinish.equals("F")) {
            MannerMode.turnOn(getApplicationContext(), silentInfo.getSubject(), silentInfo.getVibrate());
        }
    }

    private void initiateSilentInfos() {
        silentInfos.clear();
        silentInfo = getSilentOneTime();
        silentInfos.add(silentInfo);
        silentInfo = getDefaultSilent();
        silentInfos.add(silentInfo);
        boolean [] week = new boolean[]{true, false, false, false, false, false, false};
        silentInfo = new SilentInfo(getString(R.string.Sunday_Church), 9, 30, 16, 30, week, true, true);
        silentInfos.add(silentInfo);
        week = new boolean[]{true, false, false, false, false, false, true};
        silentInfo = new SilentInfo(getString(R.string.WeekEnd_Night), 22, 30, 10, 30, week, true, false);
        silentInfos.add(silentInfo);
        utils.saveSharedPrefTables();
    }

    SilentInfo getDefaultSilent() {

        boolean [] week = new boolean[]{false, true, true, true, true, true, false};
        return new SilentInfo(getString(R.string.WeekDay_Night), 23, 30, 6, 30, week, true, false);
    }

    SilentInfo getSilentOneTime() {

        boolean [] week = new boolean[]{false, false, false, false, false, false, false};
        return new SilentInfo(getString(R.string.silent_Once), 1,2,3,4,
                week, false, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        utils.log(logID, "RESUME " + stateCode);
        setVariables();
        actOnStateCode();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        stateCode = "@back";
        scheduleNextTask("Activate Silent Time ");
        super.onBackPressed();
    }

    // ↓ ↓ ↓ P E R M I S S I O N    RELATED /////// ↓ ↓ ↓ ↓
    ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();

    private void askPermission() {
        permissions.add(Manifest.permission.READ_PHONE_STATE);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.VIBRATE);
        permissions.add(Manifest.permission.ACCESS_NOTIFICATION_POLICY);
        permissions.add(Manifest.permission.RECEIVE_BOOT_COMPLETED);
        permissionsToRequest = findUnAskedPermissions(permissions);
        if (permissionsToRequest.size() != 0) {
            requestPermissions(permissionsToRequest.toArray(new String[0]),
//            requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                    ALL_PERMISSIONS_RESULT);
        }
        // get permission for silent mode
        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }
    }

    private ArrayList findUnAskedPermissions(@NonNull ArrayList<String> wanted) {
        ArrayList <String> result = new ArrayList<String>();
        for (String perm : wanted) if (hasPermission(perm)) result.add(perm);
        return result;
    }
    private boolean hasPermission(@NonNull String permission) {
        return (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ALL_PERMISSIONS_RESULT) {
            for (String perms : permissionsToRequest) {
                if (hasPermission(perms)) {
                    permissionsRejected.add(perms);
                }
            }
            if (permissionsRejected.size() > 0) {
                if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                    String msg = "These permissions are mandatory for the application. Please allow access.";
                    showDialog(msg);
                }
            }
            else
                Toast.makeText(mainContext, "Permissions not granted.", Toast.LENGTH_LONG).show();
        }
    }
    private void showDialog(String msg) {
        showMessageOKCancel(msg,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(permissionsRejected.toArray(
                                new String[0]), ALL_PERMISSIONS_RESULT);
                    }
                });
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(mainActivity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


// ↑ ↑ ↑ ↑ P E R M I S S I O N    RELATED /////// ↑ ↑ ↑

}
