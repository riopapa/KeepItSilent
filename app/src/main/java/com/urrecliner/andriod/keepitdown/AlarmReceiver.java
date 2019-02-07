package com.urrecliner.andriod.keepitdown;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;

import java.util.Objects;

import static com.urrecliner.andriod.keepitdown.Vars.Receiver;
import static com.urrecliner.andriod.keepitdown.Vars.mainContext;
import static com.urrecliner.andriod.keepitdown.Vars.utils;

public class AlarmReceiver extends BroadcastReceiver {

    Reminder reminder;
    String subject;
    boolean vibrate;

    @Override
    public void onReceive(Context context, Intent intent) {
        utils = new Utils();

        Bundle args = intent.getBundleExtra("DATA");
        reminder = (Reminder) args.getSerializable("reminder");
        subject = reminder.getSubject();
        int uniqueId = reminder.getUniqueId();
        String caseSFO = Objects.requireNonNull(intent.getExtras()).getString("case");
        utils.log("reminder"," case: "+ caseSFO + " uniqueId "+uniqueId+" subject: "+subject);
        assert caseSFO != null;
        switch (caseSFO) {
            case "S":   // start
                vibrate = reminder.getVibrate();
                setMannerOn(vibrate, context);
                break;
            case "F":   // finish
                setMannerOff(context);
//                Bundle args = intent.getBundleExtra("DATA");
                Receiver = "Alarm";
                Intent i = new Intent(context, MainActivity.class);
                i.putExtra("Receiver","Alarm");
//                args.putSerializable("reminder", reminder);
                i.putExtra("DATA",args);
                context.startActivity(i);
                break;
            case "O":   // onetime
                setMannerOff(context);
                reminder.setActive(false);
                DatabaseIO databaseIO = new DatabaseIO(mainContext);
                databaseIO.update(reminder.getId(), reminder);
                break;
            default:
                utils.logE("receive","Case Error " + caseSFO);
        }
    }

    public void setMannerOn (boolean vibrate, Context context) {
        utils.log("MannerOn","Go into Silent");
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        assert am != null;
        if (vibrate)
            am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        else
            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }
    public void setMannerOff (Context context) {
        utils.log("MannerOff","Return to normal");
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        assert am != null;
        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
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
