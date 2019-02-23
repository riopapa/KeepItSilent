package com.urrecliner.andriod.keepitdown;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Locale;

class Vars {
    static Context mainContext;
    static int colorOn, colorOnBack, colorInactiveBack, colorOff, colorOffBack, colorActive, colorActiveBack;
    static long dbCount;
    static int nowPosition, nowUniqueId;
    static int oneTimeId = -12345;
    static int xSize; // width for each week in AddActivity
    static int addViewWeek[] = new int[7];
    static int listViewWeek[] = new int[7];
    static String weekName[] = new String[7];
    static Utils utils = null;
    static MainActivity mainActivity;
    static AddActivity addActivity;
    static TimerActivity timerActivity;
    static SettingActivity settingActivity;
    static String ReceiverCase = "init";
    static int finishHour, finishMin;

    static SharedPreferences mSettings;
    static SharedPreferences.Editor editor;
    static boolean beepManner = true;

    static int interval_Short = 5;
    static int interval_Long = 30;
    static int default_Duration = 60;

    static final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    static final SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    static final SimpleDateFormat sdfLog = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss sss", Locale.US);
    static Reminder reminder = new Reminder();
    static DatabaseIO databaseIO;
}
