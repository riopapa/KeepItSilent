package com.urrecliner.andriod.keepitdown;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseIO extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "KeepIt_Down.db";
    private static final String TABLE_NAME = "KeepITDown";
    private static final int SCHEMA_VERSION = 1;

    public DatabaseIO(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        create_Table(db);
    }

    private void create_Table(SQLiteDatabase db) {
        String sqlCommand = "CREATE TABLE if not exists " + TABLE_NAME + " " +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, uniq INTEGER, " +
                "subject TEXT, timeStart INTEGER, timeFinish  INTEGER, weekTbl TEXT, active INTEGER, vibrate INTEGER) ;";
        db.execSQL(sqlCommand);
        Log.w("my log on create ", "onCreate, sqlCommand: " + sqlCommand);
    }

    public long insert(Reminder reminder) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        long uniq = System.currentTimeMillis();
        String subject = reminder.getSubject();
        int timeStart = reminder.getStartHour() * 1000 + reminder.getStartMin();
        int timeFinish = reminder.getFinishHour() * 1000 + reminder.getFinishMin();
        boolean [] week = reminder.getWeek();
        String weekTbl = "";
        for (int i=0; i<7;i++) {
            String tf = (week[i])? "1":"0";
            weekTbl += tf;
        }
        int active = (reminder.getActive()) ? 1:0;
        int vibrate = (reminder.getVibrate()) ? 1:0;
//        cv.put("_id", _id);
        cv.put("uniq", uniq);
        cv.put("subject", subject);
        cv.put("timeStart", timeStart);
        cv.put("timeFinish", timeFinish);
        cv.put("weekTbl", weekTbl);
        cv.put("active",active);
        cv.put("vibrate",vibrate);

        long result = db.insert(TABLE_NAME, null, cv);
        db.close();
        Log.w("mylog insert", "Insert DB: id = " + result);
        return result;
    }

    public long update(long id, Reminder reminder) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        Log.w("for update","id is "+id);
        long uniq = reminder.getUniq();
        String subject = reminder.getSubject();
        int timeStart = reminder.getStartHour() * 1000 + reminder.getStartMin();
        int timeFinish = reminder.getFinishHour() * 1000 + reminder.getFinishMin();
        boolean [] week = reminder.getWeek();
        String weekTbl = "";
        for (int i=0; i<7;i++) {
            String tf = (week[i])? "1":"0";
            weekTbl += tf;
        }
        int active = (reminder.getActive()) ? 1:0;
        int vibrate = (reminder.getVibrate()) ? 1:0;

        cv.put("uniq", uniq);
        cv.put("subject", subject);
        cv.put("timeStart", timeStart);
        cv.put("timeFinish", timeFinish);
        cv.put("weekTbl", weekTbl);
        cv.put("active",active);
        cv.put("vibrate",vibrate);
        String[] args = {String.valueOf(id)};
        long nb = db.update(TABLE_NAME, cv, "_id=?", args);
        db.close();
        Log.w("myLog update","Update: " + nb);
        return  nb;
    }

    public long delete(long id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] args = {String.valueOf(id)};
        long result = db.delete(TABLE_NAME, "_id=?", args);
        db.close();
        Log.w("mylog delete", "Delete: id = " + result);
        return result;
    }

    public Cursor getAll() {
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

    public ArrayList<Reminder> showAll(Cursor result) {
        ArrayList<Reminder> list = new ArrayList<Reminder>();
        result.moveToFirst();
        while (!result.isAfterLast()) {
            long id = result.getInt(0);
            long uniq = result.getLong(1);
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
            Reminder reminder = new Reminder(id, uniq, subject, startHour, startMin, finishHour, finishMin, week, active, vibrate);
            list.add(reminder);
            Log.w("myLog showAll", ""+id + ", "+uniq+" , " + subject + " , " + startHour + ":" + startMin + " ~ " + finishHour+":"+finishMin+" weekTbl "+ weekTbl + " active "+ active + " vib "+vibrate);
            result.moveToNext();
        }
        return list;
    }

    public void close() {
        SQLiteDatabase db = getWritableDatabase();
        db.close();
    }

    public long getCount(Cursor result) {
        return result.getCount();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
