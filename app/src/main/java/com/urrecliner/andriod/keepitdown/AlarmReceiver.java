package com.urrecliner.andriod.keepitdown;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Iterator;
import java.util.Set;

import static com.urrecliner.andriod.keepitdown.Vars.addActivity;
import static com.urrecliner.andriod.keepitdown.Vars.mainContext;
import static com.urrecliner.andriod.keepitdown.Vars.utils;

public class AlarmReceiver extends BroadcastReceiver {
//    ArrayList<Reminder> myReminder;
    Reminder reminder;
    long uniq;
    String subject;
    boolean vibrate;

    @Override
    public void onReceive(Context context, Intent intent) {
        utils = new Utils();
        utils.logE("alarmReceiver","Activated --");

        Bundle args = intent.getBundleExtra("DATA");
        reminder = (Reminder) args.getSerializable("reminder");
        if (reminder == null) {
            Log.w("receiver", "IS NULL");
        }
        uniq = reminder.getUniq();
        subject = reminder.getSubject();
        boolean start = intent.getExtras().getBoolean("start");
        utils.log("reminder","RECEIVED");
        utils.log("reminder","id: "+uniq+", subject: "+subject+" start: "+ start);
        if (start) {
            vibrate = reminder.getVibrate();
            setMannerOn(vibrate);
        }
        else {
            setMannerOff();
            addActivity.requestBroadCasting(reminder);
        }
    }

    public void setMannerOn (boolean vibrate) {
        Log.w("MannerOn","setting");
        AudioManager am = (AudioManager) mainContext.getSystemService(Context.AUDIO_SERVICE);

        am.setStreamVolume(AudioManager.STREAM_ALARM, 0,0);
        am.setStreamVolume(AudioManager.STREAM_DTMF, 0,0);
        am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
        am.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
        am.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, 0);
        if (vibrate)
            am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        else
            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }
    public void setMannerOff () {
        Log.w("goback","to normal");
        AudioManager am = (AudioManager) mainContext.getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_ALARM, am.getStreamMaxVolume(AudioManager.STREAM_ALARM),0);
        am.setStreamVolume(AudioManager.STREAM_DTMF, am.getStreamMaxVolume(AudioManager.STREAM_DTMF),0);
        am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, am.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION), 0);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);
        am.setStreamVolume(AudioManager.STREAM_RING, am.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
        am.setStreamVolume(AudioManager.STREAM_SYSTEM, am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM), 0);
//        am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, am.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

//        // RingtonePlayingService 서비스 intent 생성
//        Intent service_intent = new Intent(mainContext, RingtonePlayingService.class);
//
//        // RingtonePlayinService로 extra string값 보내기
//        service_intent.putExtra("state", get_yout_string);
//        // start the ringtone service
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
//            this.context.startForegroundService(service_intent);
//        }else{
//            this.context.startService(service_intent);
//        }
//
    }

    private static void dumpIntent(Intent i){
        String LOG_TAG = "dump";
        Bundle bundle = i.getExtras();
        if (bundle != null) {
            Log.e(LOG_TAG,"-- Dumping Intent start");
            Log.e(LOG_TAG,bundle.toString());
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();
            while (it.hasNext()) {
                String key = it.next();
                Log.e(LOG_TAG,"[" + key + "=" + bundle.get(key)+"]");
            }
            Log.e(LOG_TAG,"-- Dumping Intent end");
        }
    }
}
