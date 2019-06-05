package com.urrecliner.andriod.keepitsilent;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import static com.urrecliner.andriod.keepitsilent.Vars.sdfDateTime;
import static com.urrecliner.andriod.keepitsilent.Vars.utils;

class NextAlarm {
    static void request(Reminder reminder, long nextTime, String StartFinish, Context context) {
        String logID = "NextAlarm";
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        Intent intent = new Intent(context, AlarmReceiver.class);
        Bundle args = new Bundle();
        args.putSerializable("reminder", reminder);
        utils.log(logID,"time:"+sdfDateTime.format(nextTime)+" "+StartFinish+" "+reminder.getSubject());

        intent.putExtra("DATA",args);
        intent.putExtra("case",StartFinish);   // "S" : Start, "F" : Finish, "O" : One time
//        int uniqueId = (StartFinish.equals("S")) ? reminder.getUniqueId() : reminder.getUniqueId() + 1;
        int uniqueId = reminder.getUniqueId();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, uniqueId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (!reminder.getActive()) {
            alarmManager.cancel(pendingIntent);
            utils.log(logID,StartFinish+" TASK Canceled : "+reminder.getSubject());
        }
        else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, nextTime, pendingIntent);
        }
    }
}
