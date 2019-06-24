package com.urrecliner.andriod.keepitsilent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Objects;

import static com.urrecliner.andriod.keepitsilent.Vars.STATE_ALARM;
import static com.urrecliner.andriod.keepitsilent.Vars.databaseIO;
import static com.urrecliner.andriod.keepitsilent.Vars.mainActivity;
import static com.urrecliner.andriod.keepitsilent.Vars.reminder;
import static com.urrecliner.andriod.keepitsilent.Vars.stateCode;
import static com.urrecliner.andriod.keepitsilent.Vars.utils;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String subject;
        String logID = "ALARM RCV";
        if (utils == null)
            utils = new Utils();
        utils.log(logID, "action: " + intent.getAction()+" stateCode: "+ stateCode);

        Bundle args = intent.getBundleExtra("DATA");
        assert args != null;
        reminder = (Reminder) args.getSerializable("reminder");
        assert reminder != null;
        subject = reminder.getSubject();
        String caseSFO = Objects.requireNonNull(intent.getExtras()).getString("case");
        utils.log(logID,"case:"+ caseSFO + " subject: "+subject);
        assert caseSFO != null;
        Intent i = new Intent(context, MainActivity.class);
        switch (caseSFO) {
            case "L":   // looping
                break;
            case "S":   // start
                MannerMode.turnOn(context, subject, reminder.getVibrate());
                String text = subject+"\nGo into Mute till " + utils.hourMin(reminder.getFinishHour(),reminder.getFinishMin());
                Toast.makeText(mainActivity, text, Toast.LENGTH_LONG).show();
                break;
            case "F":   // finish
                MannerMode.turnOff(context, subject);
                text = subject+"\nFinished ";
                Toast.makeText(mainActivity, text, Toast.LENGTH_LONG).show();
                break;
            case "O":   // onetime
                MannerMode.turnOff(context, subject);
                reminder.setActive(false);
                databaseIO.update(reminder.getId(), reminder);
                break;
            default:
                utils.log(logID,"Case Error " + caseSFO);
        }
        if (caseSFO.equals("L"))
            stateCode = "Loop";
        else
            stateCode = STATE_ALARM;
        i.putExtra("stateCode",stateCode);
        i.putExtra("DATA",args);
        context.startActivity(i);
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
