package com.urrecliner.andriod.keepitdown;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView lVReminder;
    private DatabaseIO databaseIO;
    private ArrayList<Reminder> myReminder;
    private String [] weekName;
    Utils utils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w("button","fab clicked");
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
        utils = new Utils();
        weekName = new String[7];
        weekName[0] = getResources().getString(R.string.week_0);
        weekName[1] = getResources().getString(R.string.week_1);
        weekName[2] = getResources().getString(R.string.week_2);
        weekName[3] = getResources().getString(R.string.week_3);
        weekName[4] = getResources().getString(R.string.week_4);
        weekName[5] = getResources().getString(R.string.week_5);
        weekName[6] = getResources().getString(R.string.week_6);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            Intent intent = new Intent(MainActivity.this, AddActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initUI() {
        lVReminder = (ListView) findViewById(R.id.lv_reminder);
        databaseIO = new DatabaseIO(this);
        Cursor cursor = databaseIO.getAll();
        myReminder = databaseIO.showAll(cursor);
        ListViewAdapter listViewAdapter = new ListViewAdapter(this, myReminder);
        lVReminder.setAdapter(listViewAdapter);
        lVReminder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog(myReminder.get(position));
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
                Log.w("edit","clicked");
                new callReminder().execute(reminder);
            }
        });
        dialog.show();
    }

    private class callReminder extends AsyncTask<Reminder, Void, Void> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            Log.w("onPreExecute","onPreExecute");
            dialog = ProgressDialog.show(MainActivity.this, "", "Đang tải... ");
            dialog.setCancelable(true);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Reminder... params) {
            Log.w("doInBackground","doInBackground");
            Intent intent = new Intent(MainActivity.this, AddActivity.class);
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
            Log.w("onPostExecute","onPostExecute");
            super.onPostExecute(aVoid);
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        initUI();
    }

}
