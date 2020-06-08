package com.urrecliner.keepitsilent;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.urrecliner.keepitsilent.databinding.ActivityAddBinding;

import static com.urrecliner.keepitsilent.Vars.STATE_ADD_UPDATE;
import static com.urrecliner.keepitsilent.Vars.addNewSilent;
import static com.urrecliner.keepitsilent.Vars.colorOff;
import static com.urrecliner.keepitsilent.Vars.colorOffBack;
import static com.urrecliner.keepitsilent.Vars.colorOn;
import static com.urrecliner.keepitsilent.Vars.colorOnBack;
import static com.urrecliner.keepitsilent.Vars.mainActivity;
import static com.urrecliner.keepitsilent.Vars.recycleViewAdapter;
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
    private ActivityAddBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (addNewSilent)
            silentInfo = mainActivity.getDefaultSilent();
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle((addNewSilent) ? R.string.add_table :R.string.update_table);

        weekView[0] = binding.avWeek0; weekView[1] = binding.avWeek1; weekView[2] = binding.avWeek2; weekView[3] = binding.avWeek3;
        weekView[4] = binding.avWeek4; weekView[5] = binding.avWeek5; weekView[6] = binding.avWeek6;
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
        binding.timePickerStart.setIs24HourView(true);
        binding.timePickerStart.setHour(startHour); binding.timePickerStart.setMinute(startMin);
        binding.timePickerFinish.setIs24HourView(true);
        binding.timePickerFinish.setHour(finishHour); binding.timePickerFinish.setMinute(finishMin);

        if (subject == null)
            subject = getString(R.string.no_subject);
        binding.etSubject.setText(subject);
        for (int i=0; i < 7; i++) {
            weekView[i].setId(i);
            weekView[i].setWidth(xSize);
            weekView[i].setGravity(Gravity.CENTER);
            weekView[i].setTextColor((week[i]) ? colorOn:colorOff);
            weekView[i].setBackgroundColor((week[i]) ? colorOnBack:colorOffBack);
            weekView[i].setTypeface(null, (week[i]) ? Typeface.BOLD:Typeface.NORMAL);
            weekView[i].setText(weekName[i]);
        }

        binding.avVibrate.setImageResource((vibrate)? R.mipmap.ic_phone_vibrate:R.mipmap.ic_phone_silent);
        binding.avVibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate ^= true;
                binding.avVibrate.setImageResource((vibrate)? R.mipmap.ic_phone_vibrate:R.mipmap.ic_phone_silent);
                v.invalidate();
            }
        });

        binding.cbActive.setChecked(active);
        binding.cbActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                active ^= true;
                binding.cbActive.setChecked(active);
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

        subject = binding.etSubject.getText().toString();
        if (subject.length() == 0)
            subject = "No Subject";
        startHour = binding.timePickerStart.getHour(); startMin = binding.timePickerStart.getMinute();
        finishHour = binding.timePickerFinish.getHour(); finishMin = binding.timePickerFinish.getMinute();
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
        recycleViewAdapter.notifyItemChanged(silentIdx, silentInfo);

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
        inputMethodManager.hideSoftInputFromWindow(binding.etSubject.getWindowToken(), 0);
        super.onPause();
    }
}
