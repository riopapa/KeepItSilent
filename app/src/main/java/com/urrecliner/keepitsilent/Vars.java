package com.urrecliner.keepitsilent;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

class Vars {
    static int colorOn, colorOnBack, colorInactiveBack, colorOff, colorOffBack, colorActive;
    static int xSize; // width for each week in AddUpdateActivity
    static int [] listViewWeek = new int[7];
    static String [] weekName = new String[7];
    static Utils utils = null;

    static boolean addNewSilent = false;
    static MainActivity mainActivity;
    static Context mainContext;
    static RecycleViewAdapter recycleViewAdapter;

    static String stateCode;

    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;
    static boolean beepManner = true;

    static int interval_Short = 10;
    static int interval_Long = 30;
    static int default_Duration = 60;

    static final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    static final SimpleDateFormat sdfDateTime = new SimpleDateFormat("MM-dd HH:mm", Locale.US);
    static final SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.US);
    static final SimpleDateFormat sdfLogTime = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US);
    static SilentInfo silentInfo = new SilentInfo();
    static ArrayList<SilentInfo> silentInfos;
    static int silentIdx;
    static int silentUniq = 123456;

    static final String STATE_BLANK = "BLANK";
    static final String STATE_ALARM = "Alarm";
    static final String STATE_ONETIME = "OneTime";
    static final String STATE_BOOT = "Boot";
    static final String STATE_ADD_UPDATE = "AddUpdate";
    static Handler actionHander;
}
