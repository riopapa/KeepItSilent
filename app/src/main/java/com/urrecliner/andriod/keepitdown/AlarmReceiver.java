package com.urrecliner.andriod.keepitdown;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Objects;

import static com.urrecliner.andriod.keepitdown.Vars.ReceiverCase;
import static com.urrecliner.andriod.keepitdown.Vars.databaseIO;
import static com.urrecliner.andriod.keepitdown.Vars.mainActivity;
import static com.urrecliner.andriod.keepitdown.Vars.reminder;
import static com.urrecliner.andriod.keepitdown.Vars.utils;

public class AlarmReceiver extends BroadcastReceiver {

    String subject;
    boolean vibrate;

    @Override
    public void onReceive(Context context, Intent intent) {
        utils = new Utils();
        utils.log("AlarmReceiver", "action: " + intent.getAction()+" ReceiverCase: "+ReceiverCase);

        Bundle args = intent.getBundleExtra("DATA");
        assert args != null;
        reminder = (Reminder) args.getSerializable("reminder");
        assert reminder != null;
        subject = reminder.getSubject();
        int uniqueId = reminder.getUniqueId();
        String caseSFO = Objects.requireNonNull(intent.getExtras()).getString("case");
        utils.log("reminder"," case: "+ caseSFO + " uniqueId "+uniqueId+" subject: "+subject);
        assert caseSFO != null;
        switch (caseSFO) {
            case "S":   // start
                vibrate = reminder.getVibrate();
                MannerMode.turnOn(context, subject, vibrate);
                String text = "Go into Mute till " + utils.hourMin(reminder.getFinishHour(),reminder.getFinishMin());
                Toast.makeText(mainActivity, text, Toast.LENGTH_LONG).show();
                break;
            case "F":   // finish
                MannerMode.turnOff(context, subject);
                ReceiverCase = "Alarm";
                Intent i = new Intent(context, MainActivity.class);
                i.putExtra("ReceiverCase","Alarm");
                i.putExtra("DATA",args);
                context.startActivity(i);
                break;
            case "O":   // onetime
                MannerMode.turnOff(context, subject);
                reminder.setActiveFalse();
                databaseIO.update(reminder.getId(), reminder);
                break;
            default:
                utils.logE("receive","Case Error " + caseSFO);
        }
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
