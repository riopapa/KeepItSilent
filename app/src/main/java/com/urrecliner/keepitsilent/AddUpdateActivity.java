package com.urrecliner.keepitsilent;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

import static com.urrecliner.keepitsilent.Vars.STATE_ADD_UPDATE;
import static com.urrecliner.keepitsilent.Vars.addNewSilent;
import static com.urrecliner.keepitsilent.Vars.addViewWeek;
import static com.urrecliner.keepitsilent.Vars.colorOff;
import static com.urrecliner.keepitsilent.Vars.colorOffBack;
import static com.urrecliner.keepitsilent.Vars.colorOn;
import static com.urrecliner.keepitsilent.Vars.colorOnBack;
import static com.urrecliner.keepitsilent.Vars.mainActivity;
import static com.urrecliner.keepitsilent.Vars.silentIdx;
import static com.urrecliner.keepitsilent.Vars.silentInfo;
import static com.urrecliner.keepitsilent.Vars.silentInfos;
import static com.urrecliner.keepitsilent.Vars.silentUniq;
import static com.urrecliner.keepitsilent.Vars.stateCode;
import static com.urrecliner.keepitsilent.Vars.utils;
import static com.urrecliner.keepitsilent.Vars.weekName;
import static com.urrecliner.keepitsilent.Vars.xSize;

public class AddUpdateActivity extends AppCompatActivity {

    private String subject;
    private int startHour, startMin, finishHour, finishMin;
    private boolean active;
    private boolean[] week = new boolean[7];
    private TextView [] weekView = new TextView[7];
    private boolean vibrate;
    private String logID = "Add,Update";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        if (addNewSilent)
            silentInfo = mainActivity.getDefaultSilent();
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle((addNewSilent) ? R.string.add_table :R.string.update_table);
        for (int i=0; i < 7; i++)
            weekView[i] = findViewById(addViewWeek[i]);
        build_SilentInfo();
    }

    void build_SilentInfo() {

        subject = silentInfo.getSubject();
        startHour = silentInfo.getStartHour();
        startMin = silentInfo.getStartMin();
        finishHour = silentInfo.getFinishHour();
        finishMin = silentInfo.getFinishMin();
        active = silentInfo.getActive();
        week = silentInfo.getWeek();
        vibrate = silentInfo.getVibrate();
        TimePicker tp = findViewById(R.id.timePickerStart);
        tp.setIs24HourView(true);
        tp.setHour(startHour); tp.setMinute(startMin);
        tp = findViewById(R.id.timePickerFinish);
        tp.setIs24HourView(true);
        tp.setHour(finishHour); tp.setMinute(finishMin);

        EditText et = findViewById(R.id.et_subject);
        if (subject == null)
            subject = getString(R.string.no_subject);
        et.setText(subject);
        for (int i=0; i < 7; i++) {
            weekView[i].setId(i);
            weekView[i].setWidth(xSize);
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

    private void saveSilentInfo() {

        boolean any = false;
        for (int i = 0; i < 7; i++) { any |= week[i]; }
        if (!any) {
            Toast.makeText(getBaseContext(), R.string.at_least_one_day_selected,Toast.LENGTH_LONG).show();
            return;
        }
        EditText et = findViewById(R.id.et_subject);
        subject = et.getText().toString();
        if (subject.length() == 0)
            subject = "No Subject";
        TimePicker tp = findViewById(R.id.timePickerStart);
        startHour = tp.getHour(); startMin = tp.getMinute();
        tp = findViewById(R.id.timePickerFinish);
        finishHour = tp.getHour(); finishMin = tp.getMinute();
        silentInfo = new SilentInfo(subject, startHour, startMin, finishHour, finishMin,
            week, active, vibrate);
        if (addNewSilent)
            silentInfos.add(silentInfo);
        else
            silentInfos.set(silentIdx, silentInfo);
        utils.saveSharedPrefTables();

        stateCode = STATE_ADD_UPDATE;
        utils.log(logID, stateCode + " "+utils.hourMin(startHour,startMin));
//        Intent i = new Intent(getApplicationContext(), MainActivity.class);
//        Bundle args = new Bundle();
//        args.putSerializable("silentInfo", silentInfo);
//        i.putExtra("DATA", args);
//        getApplicationContext().startActivity(i);
//        utils.log("AddUpdateActivity","start MainActivity");
        finish();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_add, menu);
        if (addNewSilent) {
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

        int id = item.getItemId();
        switch (id) {
            case R.id.action_save:
                saveSilentInfo();
                break;
            case R.id.action_cancel:
                break;
            case R.id.action_delete:
                silentInfos.remove(silentIdx);
                utils.saveSharedPrefTables();
                cancelSilentInfo();
                break;
        }
        finish();
//        Intent intent = new Intent(mainContext,MainActivity.class);
//        mainContext.startActivity(intent);
        return false;
//        return super.onOptionsItemSelected(item);
    }

    private void cancelSilentInfo() {

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        assert alarmManager != null;
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AddUpdateActivity.this, silentUniq, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
        pendingIntent = PendingIntent.getBroadcast(AddUpdateActivity.this, silentUniq+1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
        utils.log(logID, "silentInfo Deleted");
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
