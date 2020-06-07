package com.urrecliner.keepitsilent;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import static com.urrecliner.keepitsilent.Vars.sdfDateTime;
import static com.urrecliner.keepitsilent.Vars.utils;

class NextAlarm {
    static void request(SilentInfo silentInfo, long nextTime, String StartFinish, Context context) {
        String logID = "NextAlarm";
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        Intent intent = new Intent(context, AlarmReceiver.class);
        Bundle args = new Bundle();
        args.putSerializable("silentInfo", silentInfo);
        utils.log(logID,"time:"+sdfDateTime.format(nextTime)+" "+StartFinish+" "+ silentInfo.getSubject());

        intent.putExtra("DATA",args);
        intent.putExtra("case",StartFinish);   // "S" : Start, "F" : Finish, "O" : One time
        int uniqueId = 123456; // (int) System.currentTimeMillis() & 0xffff;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, uniqueId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (!silentInfo.getActive()) {
            alarmManager.cancel(pendingIntent);
            utils.log(logID,StartFinish+" TASK Canceled : "+ silentInfo.getSubject());
        }
        else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, nextTime, pendingIntent);
        }
    }
}
