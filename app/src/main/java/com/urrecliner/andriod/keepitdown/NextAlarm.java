package com.urrecliner.andriod.keepitdown;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import static com.urrecliner.andriod.keepitdown.Vars.mainContext;
import static com.urrecliner.andriod.keepitdown.Vars.sdfDateTime;
import static com.urrecliner.andriod.keepitdown.Vars.utils;

class NextAlarm {

    static void request(Reminder reminder, long nextTime, String S_F) {
        AlarmManager alarmManager = (AlarmManager) mainContext.getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        Intent intent = new Intent(mainContext, AlarmReceiver.class);
        Bundle args = new Bundle();
        args.putSerializable("reminder", reminder);
        intent.putExtra("DATA",args);
        intent.putExtra("case",S_F);   // "S" : Start, "F" : Finish, "O" : One time
        int uniqueId = (S_F.equals("S")) ? reminder.getUniqueId() : reminder.getUniqueId() + 1;

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mainContext, uniqueId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (!reminder.getActive()) {
            alarmManager.cancel(pendingIntent);
            utils.log(S_F,"CANCELED uniqueId: "+reminder.getSubject());
        }
        else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, nextTime, pendingIntent);
            utils.log(S_F,  sdfDateTime.format(nextTime) + " uniqueId: " + uniqueId + " "+reminder.getSubject());
        }
    }
}
