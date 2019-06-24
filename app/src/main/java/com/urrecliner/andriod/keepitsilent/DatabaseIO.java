package com.urrecliner.andriod.keepitsilent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import static com.urrecliner.andriod.keepitsilent.Vars.databaseIO;
import static com.urrecliner.andriod.keepitsilent.Vars.mainActivity;
import static com.urrecliner.andriod.keepitsilent.Vars.mainContext;
import static com.urrecliner.andriod.keepitsilent.Vars.NORMAL_ID;
import static com.urrecliner.andriod.keepitsilent.Vars.ONETIME_ID;
import static com.urrecliner.andriod.keepitsilent.Vars.utils;

public class DatabaseIO extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "keepitsilent.db";
    private static final String TABLE_NAME = "keepitsilent";
    private static final int SCHEMA_VERSION = 1;
    private static String logID = "dbIO";

    DatabaseIO() {
        super(mainContext, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        create_Table(db);
    }

    private void create_Table(SQLiteDatabase db) {
        String sqlCommand = "CREATE TABLE if not exists " + TABLE_NAME + " " +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, uniqueId INTEGER, " +
                "subject TEXT, timeStart INTEGER, timeFinish  INTEGER, weekTbl TEXT, active INTEGER, vibrate INTEGER) ;";
        db.execSQL(sqlCommand);
        Log.w(logID, "onCreate, sqlCommand: " + sqlCommand);
    }

    void insert(Reminder reminder) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        int uniqueId = reminder.getUniqueId();
        String subject = reminder.getSubject();
        int timeStart = reminder.getStartHour() * 1000 + reminder.getStartMin();
        int timeFinish = reminder.getFinishHour() * 1000 + reminder.getFinishMin();
        String weekTbl = buildWeekTbl(reminder);
        int active = (reminder.getActive()) ? 1:0;
        int vibrate = (reminder.getVibrate()) ? 1:0;

        cv.put("uniqueId", uniqueId);
        cv.put("subject", subject);
        cv.put("timeStart", timeStart);
        cv.put("timeFinish", timeFinish);
        cv.put("weekTbl", weekTbl);
        cv.put("active",active);
        cv.put("vibrate",vibrate);

        long result = db.insert(TABLE_NAME, null, cv);
        db.close();
        Log.w(logID, "Insert DB: id = " + result+ "uid = "+uniqueId);
    }

    private String buildWeekTbl(Reminder reminder) {
        boolean[] week = reminder.getWeek();
        StringBuilder weekTbl = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            String tf = (week[i]) ? "1" : "0";
            weekTbl.append(tf);
        }
        return weekTbl.toString();
    }

    void update(long id, Reminder reminder) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        int uniqueId = reminder.getUniqueId();

        String subject = reminder.getSubject();
        int timeStart = reminder.getStartHour() * 1000 + reminder.getStartMin();
        int timeFinish = reminder.getFinishHour() * 1000 + reminder.getFinishMin();
        String weekTbl = buildWeekTbl(reminder);
        int active = (reminder.getActive()) ? 1:0;
        int vibrate = (reminder.getVibrate()) ? 1:0;

//        cv.put("_id", id);
        cv.put("uniqueId", uniqueId);
        cv.put("subject", subject);
        cv.put("timeStart", timeStart);
        cv.put("timeFinish", timeFinish);
        cv.put("weekTbl", weekTbl);
        cv.put("active",active);
        cv.put("vibrate",vibrate);
        String[] args = {String.valueOf(id)};
        db.update(TABLE_NAME, cv, "_id=?", args);
        db.close();
    }

    Reminder getOneTime() {

        databaseIO = new DatabaseIO();
        Cursor cursor = databaseIO.getAll();
        ArrayList<Reminder> reminders;
        reminders = retrieveAllReminders(cursor);
        cursor.close();
        for (Reminder rm : reminders) {
            if (rm.getUniqueId() == ONETIME_ID) {
                return rm;
            }
        }
        return null;
    }

    void delete(long id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] args = {String.valueOf(id)};
        long result = db.delete(TABLE_NAME, "_id=?", args);
        db.close();
        Log.w(logID, "Delete id = " + result);
    }

    Cursor getAll() {
        SQLiteDatabase db = getReadableDatabase();
        String sqlCommand = "SELECT * FROM " + TABLE_NAME;
        Cursor result;
        try {
            result = db.rawQuery(sqlCommand, null);
        } catch (Exception e) {
            create_Table(db);
            result = db.rawQuery(sqlCommand, null);
        }
        return result;
    }

    ArrayList<Reminder> retrieveAllReminders(Cursor result) {
        ArrayList<Reminder> list = new ArrayList<>();
        result.moveToFirst();
        while (!result.isAfterLast()) {
            long id = result.getInt(0);
            int uniqueId = result.getInt(1);
            String subject = result.getString(2);
            int t = result.getInt(3);
            int startHour = t / 1000, startMin = t % 1000;
            t = result.getInt(4);
            int finishHour = t/ 1000, finishMin = t % 1000;
            String weekTbl = result.getString(5);
            boolean [] week = new boolean[7];
            for (int i = 0; i < 7; i++)
                week[i] = weekTbl.substring(i, i + 1).equals("1");
            boolean active = result.getInt(6)==1;
            boolean vibrate = result.getInt(7)==1;
            Reminder reminder = new Reminder(id, uniqueId, subject, startHour, startMin, finishHour, finishMin, week, active, vibrate);
            list.add(reminder);
            result.moveToNext();
        }
        return list;
    }

    void clearDatabase(Context context) {

        Cursor cursor = databaseIO.getAll();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long id = cursor.getInt(0);
            databaseIO.delete(id);
//            Log.w(logID, "clear id "+id);
            cursor.moveToNext();
        }
        cursor.close();
        databaseIO.close();
        Toast.makeText(context,"Initiating time table",Toast.LENGTH_LONG).show();
        utils.log(logID, "create new db");
        Reminder reminder = new Reminder();
        reminder = reminder.getDefaultReminder();
        String txt = reminder.getSubject();

        reminder.setUniqueId(ONETIME_ID);
        reminder.setActive(false);
        reminder.setSubject(mainActivity.getResources().getString(R.string.action_timer));
        insert(reminder);

        reminder.setUniqueId(NORMAL_ID);
        reminder.setSubject(txt);
        reminder.setActive(true);
        insert(reminder);
//        Vars.dbCount = 2;
    }
    public void close() {
        SQLiteDatabase db = getWritableDatabase();
        db.close();
    }

    long getCount(Cursor result) {
        return result.getCount();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    @Override protected void finalize() throws Throwable { this.close(); super.finalize(); }
}
