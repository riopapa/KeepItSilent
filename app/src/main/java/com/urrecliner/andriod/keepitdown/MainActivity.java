package com.urrecliner.andriod.keepitdown;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.urrecliner.andriod.keepitdown.Vars.addViewWeek;
import static com.urrecliner.andriod.keepitdown.Vars.listViewWeek;
import static com.urrecliner.andriod.keepitdown.Vars.mainActivity;
import static com.urrecliner.andriod.keepitdown.Vars.mainContext;
import static com.urrecliner.andriod.keepitdown.Vars.nowPosition;
import static com.urrecliner.andriod.keepitdown.Vars.nowUniqueId;
import static com.urrecliner.andriod.keepitdown.Vars.oneTimeId;
import static com.urrecliner.andriod.keepitdown.Vars.utils;
import static com.urrecliner.andriod.keepitdown.Vars.weekName;

public class MainActivity extends AppCompatActivity {

    private ListView lVReminder;
    private DatabaseIO databaseIO;
    private ArrayList<Reminder> myReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.w("button","fab clicked");
//                Intent intent = new Intent(MainActivity.this, AddActivity.class);
//                startActivity(intent);
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//            }
//        });
        utils = new Utils();
        setVariables();
        preparePermission(getApplicationContext());

        databaseIO = new DatabaseIO(getApplicationContext());
        Cursor cursor = databaseIO.getAll();
        Vars.dbCount = databaseIO.getCount(cursor);
        Log.w("Initial", "dbCount " + Vars.dbCount);
        if (Vars.dbCount == 0)
            databaseIO.clearDatabase(getApplicationContext());
    }


    private void setVariables() {
        Vars.mainActivity = this;
        Vars.mainContext = getApplicationContext();
        Vars.colorOn = ContextCompat.getColor(getBaseContext(), R.color.Navy);
        Vars.colorInactiveBack = ContextCompat.getColor(getBaseContext(), R.color.gray);
        Vars.colorOnBack = ContextCompat.getColor(getBaseContext(), R.color.JeansBlue);
        Vars.colorOff = ContextCompat.getColor(getBaseContext(), R.color.BlueGray);
        Vars.colorActive = ContextCompat.getColor(getBaseContext(), R.color.EarthBlue);
        Vars.colorActiveBack = ContextCompat.getColor(getBaseContext(), R.color.JeansBlue);
        Vars.colorOffBack = ContextCompat.getColor(getBaseContext(), R.color.transparent);

        weekName[0] = getResources().getString(R.string.week_0);
        weekName[1] = getResources().getString(R.string.week_1);
        weekName[2] = getResources().getString(R.string.week_2);
        weekName[3] = getResources().getString(R.string.week_3);
        weekName[4] = getResources().getString(R.string.week_4);
        weekName[5] = getResources().getString(R.string.week_5);
        weekName[6] = getResources().getString(R.string.week_6);

        listViewWeek[0] = R.id.lt_week0;
        listViewWeek[1] = R.id.lt_week1;
        listViewWeek[2] = R.id.lt_week2;
        listViewWeek[3] = R.id.lt_week3;
        listViewWeek[4] = R.id.lt_week4;
        listViewWeek[5] = R.id.lt_week5;
        listViewWeek[6] = R.id.lt_week6;

        addViewWeek[0] = R.id.av_week0;
        addViewWeek[1] = R.id.av_week1;
        addViewWeek[2] = R.id.av_week2;
        addViewWeek[3] = R.id.av_week3;
        addViewWeek[4] = R.id.av_week4;
        addViewWeek[5] = R.id.av_week5;
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
//        if (id == R.id.action_timer) {
//            Intent intent = new Intent(MainActivity.this, TimerActivity.class);
//            startActivity(intent);
//            return true;
//        }
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

    private void showArrayLists() {
        lVReminder = findViewById(R.id.lv_reminder);
        myReminder = getAllDatabase();
        ListViewAdapter listViewAdapter = new ListViewAdapter(this, myReminder);
        lVReminder.setAdapter(listViewAdapter);
        lVReminder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                nowPosition = position;
                nowUniqueId = myReminder.get(position).getUniqueId();
                new callReminder().execute(myReminder.get(position));
//                dialog(myReminder.get(position));
                //new callReminder().execute(myReminder.get(position));
            }
        });
        lVReminder.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //dialog(myReminder.get(position));
                return false;
            }
        });
    }

    public ArrayList getAllDatabase() {
        ArrayList<Reminder> dbList;
        databaseIO = new DatabaseIO(this);
        Cursor cursor = databaseIO.getAll();
        dbList = databaseIO.showAll(cursor);
        databaseIO.close();
        return dbList;
    }

    private void dialog(final Reminder reminder) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(reminder.getSubject());
        dialog.setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (databaseIO.delete(reminder.getId()) != -1) {
                    myReminder.remove(reminder);
                    ListViewAdapter listViewAdapter = new ListViewAdapter(MainActivity.this, myReminder);
                    lVReminder.setAdapter(listViewAdapter);
                }
            }
        });
        dialog.setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.w("edit", "clicked");
                new callReminder().execute(reminder);
            }
        });
        dialog.show();
    }

    private class callReminder extends AsyncTask<Reminder, Void, Void> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
//            Log.w("onPreExecute","onPreExecute");
//            dialog = ProgressDialog.show(MainActivity.this, "", "Đang tải... ");
//            dialog.setCancelable(true);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Reminder... params) {
            Log.w("doInBackground", "doInBackground");
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
            Log.w("onPostExecute", "onPostExecute");
            super.onPostExecute(aVoid);
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        showArrayLists();
        TextView tv = findViewById(R.id.bottom_message);
        tv.startAnimation(AnimationUtils.loadAnimation(mainActivity, R.anim.move));
    }

    @Override
    public void onBackPressed() {   // ignore back key
//        super.onBackPressed();
    }

    void preparePermission(Context context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (!nm.isNotificationPolicyAccessGranted()) {
            Intent intent = new
                    Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }
    }
}
