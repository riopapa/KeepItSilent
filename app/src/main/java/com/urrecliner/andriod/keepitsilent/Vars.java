package com.urrecliner.andriod.keepitsilent;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Locale;

class Vars {
    static int colorOn, colorOnBack, colorInactiveBack, colorOff, colorOffBack, colorActive;
    static int nowPosition;
    static int ONETIME_ID = 1111111;
    static int NORMAL_ID = 1234567;
    static int xSize; // width for each week in AddUpdateActivity
    static int [] addViewWeek = new int[7];
    static int [] listViewWeek = new int[7];
    static String [] weekName = new String[7];
    static Utils utils = null;

    static MainActivity mainActivity;
    static Context mainContext;

    static String stateCode;

    static SharedPreferences mSettings;
    static SharedPreferences.Editor editor;
    static boolean beepManner = true;

    static int interval_Short = 10;
    static int interval_Long = 30;
    static int default_Duration = 60;

    static final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    static final SimpleDateFormat sdfDateTime = new SimpleDateFormat("MM-dd HH:mm", Locale.US);
    static final SimpleDateFormat sdfLogTime = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US);
    static Reminder reminder = new Reminder();
    static DatabaseIO databaseIO;
}
