package com.urrecliner.andriod.keepitdown;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Iterator;
import java.util.Set;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle args = intent.getBundleExtra("DATA");
        Reminder reminder = (Reminder) args.getSerializable("reminder");
        try {
            long id = reminder.getId();
            long time = reminder.getTime();
            String subject = reminder.getSubject();
            String content = reminder.getContent();
            String txt = "time="+time+", subject="+subject+" id="+id + " content="+content;
            Log.w("received", txt);
        } catch (Exception e) {
            Log.w("receiver", "Error Receiver: " + e.toString());
        }

       /* Intent intent1 = new Intent(context, AlarmClock.ACTION_SHOW_ALARMS);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent i = new Intent(MainActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);*/
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
