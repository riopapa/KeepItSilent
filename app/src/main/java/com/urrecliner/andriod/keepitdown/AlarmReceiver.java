package com.urrecliner.andriod.keepitdown;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;

import java.util.Objects;

import static com.urrecliner.andriod.keepitdown.Vars.addActivity;
import static com.urrecliner.andriod.keepitdown.Vars.mainContext;
import static com.urrecliner.andriod.keepitdown.Vars.utils;

public class AlarmReceiver extends BroadcastReceiver {
//    ArrayList<Reminder> myReminder;
    Reminder reminder;
    String subject;
    boolean vibrate;

    @Override
    public void onReceive(Context context, Intent intent) {
        utils = new Utils();
        utils.log("alarmReceiver","Activated --");

        Bundle args = intent.getBundleExtra("DATA");
        reminder = (Reminder) args.getSerializable("reminder");
        if (reminder == null) {
            utils.log("receiver", "IS NULL");
        }
        subject = reminder.getSubject();
        String caseSFO = Objects.requireNonNull(intent.getExtras()).getString("case");
        utils.log("reminder","subject: "+subject+" case: "+ caseSFO);
        assert caseSFO != null;
        switch (caseSFO) {
            case "S":   // start
                vibrate = reminder.getVibrate();
                setMannerOn(vibrate);
                break;
            case "F":   // finish
                setMannerOff();
                addActivity.requestBroadCasting(reminder);
                break;
            case "O":   // onetime
                setMannerOff();
                reminder.setActive(false);
                DatabaseIO databaseIO = new DatabaseIO(mainContext);
                databaseIO.update(reminder.getId(), reminder);
                break;
            default:
                utils.logE("receive","Case Error " + caseSFO);
        }
    }

    public void setMannerOn (boolean vibrate) {
        utils.log("MannerOn","setting");
        AudioManager am = (AudioManager) mainContext.getSystemService(Context.AUDIO_SERVICE);
        assert am != null;
        if (vibrate)
            am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        else
            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }
    public void setMannerOff () {
        utils.log("goback","to normal");
        AudioManager am = (AudioManager) mainContext.getSystemService(Context.AUDIO_SERVICE);
        assert am != null;
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

//    private static void dumpIntent(Intent i){
//        String LOG_TAG = "dump";
//        Bundle bundle = i.getExtras();
//        if (bundle != null) {
//            Log.e(LOG_TAG,"-- Dumping Intent start");
//            Log.e(LOG_TAG,bundle.toString());
//            Set<String> keys = bundle.keySet();
//            for (String key : keys) {
//                Log.e(LOG_TAG, "[" + key + "=" + bundle.get(key) + "]");
//            }
//            Log.e(LOG_TAG,"-- Dumping Intent end");
//        }
//    }
}
