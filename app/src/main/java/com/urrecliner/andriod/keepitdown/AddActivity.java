package com.urrecliner.andriod.keepitdown;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import static com.urrecliner.andriod.keepitdown.Vars.addActivity;
import static com.urrecliner.andriod.keepitdown.Vars.addViewWeek;
import static com.urrecliner.andriod.keepitdown.Vars.colorOff;
import static com.urrecliner.andriod.keepitdown.Vars.colorOffBack;
import static com.urrecliner.andriod.keepitdown.Vars.colorOn;
import static com.urrecliner.andriod.keepitdown.Vars.colorOnBack;
import static com.urrecliner.andriod.keepitdown.Vars.sdfDateTime;
import static com.urrecliner.andriod.keepitdown.Vars.utils;
import static com.urrecliner.andriod.keepitdown.Vars.weekName;

public class AddActivity extends AppCompatActivity {

    private Reminder reminder;
    private long id;
    private int uniqueId;
    private String subject;
    private int startHour, startMin, finishHour, finishMin;
    private boolean active;
    private boolean[] week = new boolean[7];
    private TextView [] weekView = new TextView[7];
    private boolean vibrate;
    private boolean isNew = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        addActivity = this;

        for (int i=0; i < 7; i++)
            weekView[i] = findViewById(addViewWeek[i]);

        Bundle data = getIntent().getExtras();
        try {
            reminder = (Reminder) data.getSerializable("reminder");
        }
        catch (Exception e) {
            Log.w("reminder","is null");
        }
        if (reminder != null) {
            isNew = false;
            actionBar.setTitle("수정");
        }
        else {
            isNew = true;
            reminder = new Reminder();
            reminder = reminder.getDefaultReminder();
            actionBar.setTitle("추가");
        }
        id = reminder.getId();
        uniqueId = reminder.getUniqueId();
        subject = reminder.getSubject();
        startHour = reminder.getStartHour();
        startMin = reminder.getStartMin();
        finishHour = reminder.getFinishHour();
        finishMin = reminder.getFinishMin();
        active = reminder.getActive();
        week = reminder.getWeek();
        vibrate = reminder.getVibrate();
        build_addActivity();
    }

    void build_addActivity() {

        Log.w("building ","AddActivity");
        TimePicker tp = findViewById(R.id.timePickerStart);
        tp.setIs24HourView(true);
        tp.setHour(startHour); tp.setMinute(startMin);
        tp = findViewById(R.id.timePickerFinish);
        tp.setIs24HourView(true);
        tp.setHour(finishHour); tp.setMinute(finishMin);

        EditText et = findViewById(R.id.et_subject);
        if (subject == null)
            subject = "제목 없음";
        et.setText(subject);
        for (int i=0; i < 7; i++) {
            weekView[i].setId(i);
            weekView[i].setWidth(Vars.xSize);
            weekView[i].setGravity(Gravity.CENTER);
            weekView[i].setTextColor((week[i]) ? colorOn:colorOff);
            weekView[i].setBackgroundColor((week[i]) ? colorOnBack:colorOffBack);
            weekView[i].setTypeface(null, (week[i]) ? Typeface.BOLD:Typeface.NORMAL);
            weekView[i].setText(weekName[i]);
        }

        final ImageView ib = findViewById(R.id.av_vibrate);
        ib.setImageResource((vibrate)? R.mipmap.ic_phone_vibrate:R.mipmap.ic_phone_silent);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("onclick","vibrate toggle");
                vibrate ^= true;
                ib.setImageResource((vibrate)? R.mipmap.ic_phone_vibrate:R.mipmap.ic_phone_silent);
                v.invalidate();
            }
        });

        final CheckBox cb = findViewById(R.id.cb_active);
        cb.setChecked(active);
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("onclick","Checkbox toggle");
                active ^= true;
                cb.setChecked(active);
                v.invalidate();
            }
        });
    }

   public void toggleWeek(View v) {
        int i = v.getId();
        week[i] ^= true;
        weekView[i].setTextColor((week[i]) ? colorOn:colorOff);
        weekView[i].setBackgroundColor((week[i]) ? colorOnBack:colorOffBack);
        weekView[i].setTypeface(null, (week[i]) ? Typeface.BOLD:Typeface.NORMAL);
        v.invalidate();
    }

    private void onSave() {

        boolean any = false;
        for (int i = 0; i < 7; i++) { any |= week[i]; }
        if (!any) {
            Toast.makeText(getBaseContext(), "적어도 하루는 선택해야 하지 않을까요?",Toast.LENGTH_LONG).show();
            return;
        }
        EditText et = findViewById(R.id.et_subject);
        subject = et.getText().toString();
        if (subject.length() == 0)
            subject = "제목 없음";
        TimePicker tp = findViewById(R.id.timePickerStart);
        startHour = tp.getHour(); startMin = tp.getMinute();
        tp = findViewById(R.id.timePickerFinish);
        finishHour = tp.getHour(); finishMin = tp.getMinute();
        Reminder reminder = new Reminder(id, uniqueId, subject, startHour, startMin, finishHour, finishMin,
            week, active, vibrate);
        DatabaseIO databaseIO = new DatabaseIO(this);
        if (isNew) {
            id = databaseIO.insert(reminder);
            reminder.setId(id);
        } else {
            databaseIO.update(reminder.getId(), reminder);
        }
        databaseIO.close();
        requestBroadCasting(reminder);
        finish();
    }

    public void requestBroadCasting(Reminder reminder) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intentS = new Intent(this, AlarmReceiver.class);
        Bundle args = new Bundle();
        args.putSerializable("reminder", reminder);
        intentS.putExtra("DATA",args);
        intentS.putExtra("case","S");   // "S" : Start, "F" : Finish, "O" : One time
        intentS.putExtra("uniqueId",reminder.getUniqueId());
        long nextStart= calcNextEvent(startHour, startMin,week);

        PendingIntent pendingIntentS = PendingIntent.getBroadcast(AddActivity.this, reminder.getUniqueId(), intentS, PendingIntent.FLAG_UPDATE_CURRENT);

        if (!reminder.getActive()) {
            alarmManager.cancel(pendingIntentS);
            utils.log("reminder","CANCELED");
        }
        else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, nextStart, pendingIntentS);
            utils.log("reminder",subject + "  Activated START : " + sdfDateTime.format(nextStart));
        }

        long timeDiff = (finishHour * 60 + finishMin - startHour * 60 - startMin) * 60 * 1000;
        if (timeDiff < 0)
            timeDiff += 24 * 60 * 60 * 1000;
        nextStart += timeDiff;
        Intent intentF = new Intent(this, AlarmReceiver.class);
        Bundle argsF = new Bundle();
        argsF.putSerializable("reminder", reminder);
        intentF.putExtra("DATA",argsF);
        intentF.putExtra("case","F");
        intentF.putExtra("uniqueId",reminder.getUniqueId());

        PendingIntent pendingIntentF = PendingIntent.getBroadcast(AddActivity.this,  reminder.getUniqueId() + 100, intentF, PendingIntent.FLAG_UPDATE_CURRENT);
        if (!reminder.getActive()) {
            alarmManager.cancel(pendingIntentF);
        }
        else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, nextStart, pendingIntentF);
            utils.log("reminder",subject + "  Activated FINISH : " + sdfDateTime.format(nextStart));
        }
    }

    private long calcNextEvent(int nextHour, int nextMin, boolean week[]) {
        Calendar today = Calendar.getInstance();
        int DD = today.get(Calendar.DATE);
        int WK = today.get(Calendar.DAY_OF_WEEK) - 1; // 1 for sunday

        long todayEvent = today.getTimeInMillis();
        today.set(Calendar.SECOND, 0);
        long nextEvent = 0;
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        if (isNew) {
            menu.findItem(R.id.action_delete).setEnabled(false);
            menu.findItem(R.id.action_delete).getIcon().setAlpha(80);
        }
        else {
            menu.findItem(R.id.action_delete).setEnabled(true);
            menu.findItem(R.id.action_delete).getIcon().setAlpha(255);
        }
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

        if (id == R.id.action_delete) {
            DatabaseIO databaseIO = new DatabaseIO(this);
            Cursor cursor = databaseIO.getAll();
            ArrayList<Reminder> myReminder;
            myReminder = databaseIO.showAll(cursor);
            if (databaseIO.delete(myReminder.get(Vars.nowPosition).getId()) != -1) {
//                myReminder.remove(myReminder.get(Vars.nowPosition));
//                ListViewAdapter listViewAdapter = new ListViewAdapter(getBaseContext(),MainActivity.this, myReminder);
//                lVReminder.setAdapter(listViewAdapter);
            }
            databaseIO.close();
            cancelReminder();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cancelReminder() {

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AddActivity.this, reminder.getUniqueId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
        pendingIntent = PendingIntent.getBroadcast(AddActivity.this, reminder.getUniqueId() + 100, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
        utils.log("reminder","Deleted");
    }

    @Override
    protected void onPause() {

        // hide the keyboard in order to avoid getTextBeforeCursor on inactive InputConnection
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        EditText et = findViewById(R.id.et_subject);
        inputMethodManager.hideSoftInputFromWindow(et.getWindowToken(), 0);

        super.onPause();
    }
//    public static void cancelReminder(Context context,Class<?> cls)
//    {
//        // Disable a receiver
//        ComponentName receiver = new ComponentName(context, cls);
//        PackageManager pm = context.getPackageManager();
//        pm.setComponentEnabledSetting(receiver,
//                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                PackageManager.DONT_KILL_APP);
//
//        Intent intent1 = new Intent(context, cls);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
//                0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
//        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
//        am.cancel(pendingIntent);
//        pendingIntent.cancel();
//    }
}
