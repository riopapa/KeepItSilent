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

import static com.urrecliner.andriod.keepitdown.Vars.Receiver;
import static com.urrecliner.andriod.keepitdown.Vars.addActivity;
import static com.urrecliner.andriod.keepitdown.Vars.addViewWeek;
import static com.urrecliner.andriod.keepitdown.Vars.colorOff;
import static com.urrecliner.andriod.keepitdown.Vars.colorOffBack;
import static com.urrecliner.andriod.keepitdown.Vars.colorOn;
import static com.urrecliner.andriod.keepitdown.Vars.colorOnBack;
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
        addActivity = this;
        setContentView(R.layout.activity_add);

        Bundle data = getIntent().getExtras();
        assert data != null;
        try {
            reminder = (Reminder) data.getSerializable("reminder");
        }
        catch (Exception e) {
            utils.logE("Reminder ","is NULL when GET REMINDER");
        }

        for (int i=0; i < 7; i++)
            weekView[i] = findViewById(addViewWeek[i]);

        ActionBar actionBar;
        actionBar = getSupportActionBar();
        assert actionBar != null;
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
            databaseIO.insert(reminder);
//            id = databaseIO.insert(reminder);
//            reminder.setId(id);
        } else {
            databaseIO.update(reminder.getId(), reminder);
        }
        databaseIO.close();
        Receiver = "AddUpdate";
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        Bundle args = new Bundle();
        args.putSerializable("reminder", reminder);
        i.putExtra("DATA", args);
        getApplicationContext().startActivity(i);
        finish();
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
            myReminder = databaseIO.retrieveAllReminders(cursor);
            cursor.close();
            databaseIO.delete(myReminder.get(Vars.nowPosition).getId());
//            if (databaseIO.delete(myReminder.get(Vars.nowPosition).getId()) != -1) {
//                myReminder.remove(myReminder.get(Vars.nowPosition));
//                ListViewAdapter listViewAdapter = new ListViewAdapter(getBaseContext(),MainActivity.this, myReminder);
//                lVReminder.setAdapter(listViewAdapter);
//            }
            databaseIO.close();
            cancelReminder();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cancelReminder() {

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        assert alarmManager != null;
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AddActivity.this, reminder.getUniqueId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
        pendingIntent = PendingIntent.getBroadcast(AddActivity.this, reminder.getUniqueId() + 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
        utils.log("reminder","Deleted");
    }

    @Override
    protected void onPause() {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        EditText et = findViewById(R.id.et_subject);
        inputMethodManager.hideSoftInputFromWindow(et.getWindowToken(), 0);

        super.onPause();
    }
}
