package com.urrecliner.andriod.keepitdown;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import static com.urrecliner.andriod.keepitdown.Vars.mainActivity;
import static com.urrecliner.andriod.keepitdown.Vars.oneTimeId;
import static com.urrecliner.andriod.keepitdown.Vars.utils;

public class DatabaseIO extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "KeepITDowN.db";
    private static final String TABLE_NAME = "KeepITDown";
    private static final int SCHEMA_VERSION = 1;

    DatabaseIO(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
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
        Log.w("my log on create ", "onCreate, sqlCommand: " + sqlCommand);
    }

    void insert(Reminder reminder) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        int uniqueId = reminder.getUniqueId();
        Log.w("insert","uid "+uniqueId);
        String subject = reminder.getSubject();
        int timeStart = reminder.getStartHour() * 1000 + reminder.getStartMin();
        int timeFinish = reminder.getFinishHour() * 1000 + reminder.getFinishMin();
        boolean [] week = reminder.getWeek();
        StringBuilder weekTbl = new StringBuilder();
        for (int i=0; i<7;i++) {
            String tf = (week[i])? "1":"0";
            weekTbl.append(tf);
        }
        int active = (reminder.getActive()) ? 1:0;
        int vibrate = (reminder.getVibrate()) ? 1:0;

        cv.put("uniqueId", uniqueId);
        cv.put("subject", subject);
        cv.put("timeStart", timeStart);
        cv.put("timeFinish", timeFinish);
        cv.put("weekTbl", weekTbl.toString());
        cv.put("active",active);
        cv.put("vibrate",vibrate);

        long result = db.insert(TABLE_NAME, null, cv);
        db.close();
        Log.w("mylog insert", "Insert DB: id = " + result);
//        return result;
    }

    void update(long id, Reminder reminder) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        int uniqueId = reminder.getUniqueId();

        String subject = reminder.getSubject();
        int timeStart = reminder.getStartHour() * 1000 + reminder.getStartMin();
        int timeFinish = reminder.getFinishHour() * 1000 + reminder.getFinishMin();
        boolean [] week = reminder.getWeek();
        StringBuilder weekTbl = new StringBuilder();
        for (int i=0; i<7;i++) {
            String tf = (week[i])? "1":"0";
            weekTbl.append(tf);
        }
        int active = (reminder.getActive()) ? 1:0;
        int vibrate = (reminder.getVibrate()) ? 1:0;

//        cv.put("_id", id);
        cv.put("uniqueId", uniqueId);
        cv.put("subject", subject);
        cv.put("timeStart", timeStart);
        cv.put("timeFinish", timeFinish);
        cv.put("weekTbl", weekTbl.toString());
        cv.put("active",active);
        cv.put("vibrate",vibrate);
        String[] args = {String.valueOf(id)};
        db.update(TABLE_NAME, cv, "_id=?", args);
        db.close();
    }

    void delete(long id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] args = {String.valueOf(id)};
        long result = db.delete(TABLE_NAME, "_id=?", args);
        db.close();
        Log.w("mylog delete", "Delete: id = " + result);
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
//            Log.w("db", ""+id + ","+uniqueId+"," + startHour + ":" + startMin + "~" + finishHour+":"+finishMin+" weekTbl "+ weekTbl + " active="+ active + " vib="+vibrate + " , " +subject );
            result.moveToNext();
        }
        return list;
    }

    void clearDatabase(Context context) {

        DatabaseIO databaseIO = new DatabaseIO(context);
        Cursor cursor = databaseIO.getAll();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long id = cursor.getInt(0);
            databaseIO.delete(id);
            Log.w("db delete","id "+id);
            cursor.moveToNext();
        }
        cursor.close();
        databaseIO.close();
        Toast.makeText(context,"Initiating time table",Toast.LENGTH_LONG).show();
        utils.log("create db", "new");
        Reminder reminder = new Reminder();
        reminder = reminder.getDefaultReminder();
        int uId = reminder.getUniqueId();
        String txt = reminder.getSubject();
        reminder.setUniqueId(oneTimeId);
        reminder.setSubject(mainActivity.getResources().getString(R.string.action_timer));
        insert(reminder);
        reminder.setUniqueId(uId);
        reminder.setSubject(txt);
        insert(reminder);
        Vars.dbCount = 2;
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
