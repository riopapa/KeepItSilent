package com.urrecliner.andriod.keepitdown;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import static com.urrecliner.andriod.keepitdown.Vars.Receiver;
import static com.urrecliner.andriod.keepitdown.Vars.addActivity;
import static com.urrecliner.andriod.keepitdown.Vars.addViewWeek;
import static com.urrecliner.andriod.keepitdown.Vars.listViewWeek;
import static com.urrecliner.andriod.keepitdown.Vars.mainActivity;
import static com.urrecliner.andriod.keepitdown.Vars.mainContext;
import static com.urrecliner.andriod.keepitdown.Vars.nowPosition;
import static com.urrecliner.andriod.keepitdown.Vars.nowUniqueId;
import static com.urrecliner.andriod.keepitdown.Vars.oneTimeId;
import static com.urrecliner.andriod.keepitdown.Vars.sdfDateTime;
import static com.urrecliner.andriod.keepitdown.Vars.utils;
import static com.urrecliner.andriod.keepitdown.Vars.weekName;

public class MainActivity extends AppCompatActivity {

    ListView lVReminder;
    private DatabaseIO databaseIO;
    private ArrayList<Reminder> myReminder;
    Reminder reminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        utils = new Utils();
        utils.log("MainActivity","onCreate");
        preparePermission(getApplicationContext());
//        Receiver = "mainOnCreate";
//        try {
//            Intent intent = getIntent();
//            if (intent != null) {
//                utils.log("mainOnCreate"," On create has intent \n"+intent.toString());
//                Bundle extras = intent.getExtras();
//                if (extras == null) {
//                    utils.log("onCreate","extra is null");
//                }
//                else
//                    Receiver = extras.getString("receiver");
//                Bundle args = getIntent().getBundleExtra("DATA");
//                if (args == null) {
//                    utils.log("onCreate"," args is null");
//                }
//                else {
//                    reminder = (Reminder) args.getSerializable("reminder");
//                    utils.log("onCreate","reminder ----- uniq "+reminder.getUniqueId());
//                }
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Receiver = "oCC";
//        }
        utils.log("Receiver ","onCreate is "+Receiver);
        if (Receiver.equals("Alarm")) { // it means from receiver
            utils.log("From","ALARM");
            Bundle args = getIntent().getBundleExtra("DATA");
            reminder = (Reminder) args.getSerializable("reminder");
            requestBroadCasting(reminder);
            Receiver = "AlarmEnd";
            finish();
        }
        else if (Receiver.equals("Boot")) { // it means from receiver
            utils.log("From","BOOT");
            requestBroadCastingAll();
            Receiver = "BootEnd";
            finish();
        }
        else {
            setContentView(R.layout.activity_main);
            setVariables();
            showArrayLists();
        }
        utils.log("END of","ONCREATE");
        super.onCreate(savedInstanceState);
    }

    void setVariables() {
        utils = new Utils();
        Vars.mainActivity = this;
        if (mainContext == null)
            mainContext = getApplicationContext();
        Vars.colorOn = ContextCompat.getColor(getBaseContext(), R.color.Navy);
        Vars.colorInactiveBack = ContextCompat.getColor(getBaseContext(), R.color.gray);
        Vars.colorOnBack = ContextCompat.getColor(getBaseContext(), R.color.JeansBlue);
        Vars.colorOff = ContextCompat.getColor(getBaseContext(), R.color.BlueGray);
        Vars.colorActive = ContextCompat.getColor(getBaseContext(), R.color.EarthBlue);
        Vars.colorActiveBack = ContextCompat.getColor(getBaseContext(), R.color.JeansBlue);
        Vars.colorOffBack = ContextCompat.getColor(getBaseContext(), R.color.transparent);

        databaseIO = new DatabaseIO(mainContext);
        Cursor cursor = databaseIO.getAll();
        Vars.dbCount = databaseIO.getCount(cursor);
        cursor.close();
        utils.log("Initial", "dbCount " + Vars.dbCount);
        if (Vars.dbCount == 0)
            databaseIO.clearDatabase(mainContext);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add) {
            Intent intent = new Intent(MainActivity.this, AddActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_reset) {
            new AlertDialog.Builder(this)
                    .setTitle("데이터 초기화")
                    .setMessage("이미 설정되어 있는 것들을 다 삭제합니다")
                    .setIcon(R.mipmap.icon_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            DatabaseIO databaseIO = new DatabaseIO(mainContext);
                            databaseIO.clearDatabase(mainContext);
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
//        myReminder = getAllDatabase();
        databaseIO = new DatabaseIO(this);
        Cursor cursor = databaseIO.getAll();
        myReminder = databaseIO.retrieveAllReminders(cursor);
        cursor.close();

        ListViewAdapter listViewAdapter = new ListViewAdapter(this, myReminder);
        utils.log("listViewAdapter","BUILD");
        lVReminder.setAdapter(listViewAdapter);
        lVReminder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                nowPosition = position;
                nowUniqueId = myReminder.get(position).getUniqueId();
                new callReminder().execute(myReminder.get(position));
            }
        });
        lVReminder.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //dialog(myReminder.get(position));
                return false;
            }
        });
        TextView tv = findViewById(R.id.bottom_message);
        tv.startAnimation(AnimationUtils.loadAnimation(mainActivity, R.anim.move));
    }

    public ArrayList getAllDatabase() {
        ArrayList<Reminder> dbList;
        databaseIO = new DatabaseIO(this);
        Cursor cursor = databaseIO.getAll();
        dbList = databaseIO.retrieveAllReminders(cursor);
        cursor.close();
        return dbList;
    }

    private class callReminder extends AsyncTask<Reminder, Void, Void> {
//        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
//            utils.log("onPreExecute","onPreExecute");
//            dialog = ProgressDialog.show(MainActivity.this, "", "Đang tải... ");
//            dialog.setCancelable(true);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Reminder... params) {
//            utils.log("doInBackground", "doInBackground");
            Intent intent;
            if (nowUniqueId != oneTimeId)
                intent = new Intent(MainActivity.this, AddActivity.class);
            else
                intent = new Intent(MainActivity.this, TimerActivity.class);
            intent.putExtra("reminder", params[0]);
            startActivity(intent);
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            utils.log("onPostExecute", "onPostExecute");
            super.onPostExecute(aVoid);
//            if (dialog != null) {
//                dialog.dismiss();
//            }
        }
    }

//    @Override
//    protected void onPostResume() {
//        utils.log("onPostResume","  ---------- start "+Receiver);
//        super.onPostResume();
//        try {
//            Intent intent = getIntent();
//            if (intent == null) {
//                utils.log("onPostResume","intent is NULL");
//            }
//            else {
//                utils.log("onPostResume"," On create has intent");
//                Bundle extras = intent.getExtras();
//                Bundle args = getIntent().getBundleExtra("DATA");
//                if (extras == null) {
//                    utils.log("onPostResume","extra is null");
//                }
//                else
//                    Receiver = extras.getString("Receiver");
//                if (args == null) {
//                    utils.log("onPostResume"," args is null");
//                }
//                else {
//                    reminder = (Reminder) args.getSerializable("reminder");
//                    utils.log("onPostResume","reminder ----- uniq "+reminder.getUniqueId());
//                }
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Receiver = "oCC";
//        }
//
//        utils.log("RECEICER","onPostResume is "+Receiver);
//
//        if (Receiver.equals("Alarm")) { // it means from receiver
//            utils.log("From","Alarm");
//            Bundle args = getIntent().getBundleExtra("DATA");
//            reminder = (Reminder) args.getSerializable("reminder");
//            requestBroadCasting(reminder);
//            Receiver = "AlarmEnd";
//            finish();
//        }
//        else if (Receiver.equals("Boot")) { // it means from receiver
//            utils.log("From","BOOT");
//            requestBroadCastingAll();
//            Receiver = "BootEnd";
//            finish();
//        }
//        else
//            showArrayLists();
//    }

    void requestBroadCastingAll() {

        if (mainActivity == null)
            mainActivity = new MainActivity();
        if (addActivity == null)
            addActivity = new AddActivity();
        AlarmManager alarmManager = (AlarmManager) mainActivity.getSystemService(ALARM_SERVICE);
        assert alarmManager != null;
        Intent intentS = new Intent(this, AlarmReceiver.class);
//        myReminder = getAllDatabase();

        databaseIO = new DatabaseIO(this);
        Cursor cursor = databaseIO.getAll();
        myReminder = databaseIO.retrieveAllReminders(cursor);
        cursor.close();

        for (Reminder rm1 : myReminder) {
            if (rm1.getUniqueId() != oneTimeId && rm1.getActive()) {
                Bundle args = new Bundle();
                args.putSerializable("reminder", rm1);
                intentS.putExtra("DATA", args);
                intentS.putExtra("case", "S");   // "S" : Start, "F" : Finish, "O" : One time
                intentS.putExtra("uniqueId", rm1.getUniqueId());
                long nextStart = calcNextEvent(rm1.getStartHour(), rm1.getStartMin(), rm1.getWeek());
                PendingIntent pendingIntentS = PendingIntent.getBroadcast(mainActivity, rm1.getUniqueId(),
                        intentS, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.RTC_WAKEUP, nextStart, pendingIntentS);
                utils.log("Boot",  "_START : " + sdfDateTime.format(nextStart) + " uniqueId: " + rm1.getUniqueId() + " "+rm1.getSubject());
                long timeDiff = ((rm1.getFinishHour() - rm1.getStartHour()) * 60 + rm1.getFinishMin() - rm1.getStartMin()) * 60 * 1000;
                if (timeDiff < 0)
                    timeDiff += 24 * 60 * 60 * 1000;
                nextStart += timeDiff;
                Intent intentF = new Intent(this, AlarmReceiver.class);
                Bundle argsF = new Bundle();
                argsF.putSerializable("reminder", rm1);
                intentF.putExtra("DATA", argsF);
                intentF.putExtra("case", "F");
                intentF.putExtra("uniqueId", rm1.getUniqueId());
                PendingIntent pendingIntentF = PendingIntent.getBroadcast(mainActivity, rm1.getUniqueId() + 100,
                        intentF, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.RTC_WAKEUP, nextStart, pendingIntentF);
                utils.log("Boot", "FINISH : " + sdfDateTime.format(nextStart) + " uniqueId: " + (rm1.getUniqueId() + 100)+" "+rm1.getSubject());
            }
        }
    }

    void requestBroadCasting(Reminder reminder) {

        AlarmManager alarmManager = (AlarmManager) mainContext.getSystemService(ALARM_SERVICE);
        assert alarmManager != null;
        Intent intentS = new Intent(mainContext, AlarmReceiver.class);
        Bundle args = new Bundle();
        args.putSerializable("reminder", reminder);
        intentS.putExtra("DATA",args);
        intentS.putExtra("case","S");   // "S" : Start, "F" : Finish, "O" : One time
        intentS.putExtra("uniqueId",reminder.getUniqueId());
        long nextStart= calcNextEvent(reminder.getStartHour(), reminder.getStartMin(),reminder.getWeek());

        PendingIntent pendingIntentS = PendingIntent.getBroadcast(mainContext, reminder.getUniqueId(), intentS, PendingIntent.FLAG_UPDATE_CURRENT);

        if (!reminder.getActive()) {
            alarmManager.cancel(pendingIntentS);
            utils.log("reminder","CANCELED uniqueId: "+reminder.getUniqueId());
        }
        else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, nextStart, pendingIntentS);
            utils.log("_START",  sdfDateTime.format(nextStart) + " uniqueId: " + reminder.getUniqueId() + " "+reminder.getSubject());
        }

        long timeDiff = ((reminder.getFinishHour() - reminder.getStartHour()) * 60 + reminder.getFinishMin() - reminder.getStartMin()) * 60 * 1000;
        if (timeDiff < 0)
            timeDiff += 24 * 60 * 60 * 1000;
        nextStart += timeDiff;
        Intent intentF = new Intent(mainContext, AlarmReceiver.class);
        Bundle argsF = new Bundle();
        argsF.putSerializable("reminder", reminder);
        intentF.putExtra("DATA",argsF);
        intentF.putExtra("case","F");
        intentF.putExtra("uniqueId",reminder.getUniqueId());

        PendingIntent pendingIntentF = PendingIntent.getBroadcast(mainActivity,  reminder.getUniqueId() + 100, intentF, PendingIntent.FLAG_UPDATE_CURRENT);
        if (!reminder.getActive()) {
            alarmManager.cancel(pendingIntentF);
        }
        else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, nextStart, pendingIntentF);
            utils.log("FINISH", sdfDateTime.format(nextStart) + " uniqueId: " + (reminder.getUniqueId() + 100)+" "+reminder.getSubject());
        }
        if (Receiver.equals("refresh")) {
            Receiver = "refreshEnd";
        }
    }

    static long calcNextEvent(int nextHour, int nextMin, boolean week[]) {
        Calendar today = Calendar.getInstance();
        int DD = today.get(Calendar.DATE);
        int WK = today.get(Calendar.DAY_OF_WEEK) - 1; // 1 for sunday

        long todayEvent = today.getTimeInMillis();
        today.set(Calendar.SECOND, 0);
        long nextEvent;
        today.set(Calendar.HOUR_OF_DAY, nextHour);
        today.set(Calendar.MINUTE, nextMin);
        for (int i = WK; ; ) {
            if (week[i]) {
                nextEvent = today.getTimeInMillis();
                if (nextEvent > todayEvent)
                    break;
            }
            today.set(Calendar.DATE, ++DD);
            DD = today.get(Calendar.DATE);
            i++;
            if (i == 7)
                i = 0;
        }
        return nextEvent;
    }

    @Override
    public void onBackPressed() {   // ignore back key
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
