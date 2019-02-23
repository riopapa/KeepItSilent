package com.urrecliner.andriod.keepitdown;

import java.util.Calendar;

public class NextEventTime {

    static long calc(int hour, int min, boolean week[]) {
        Calendar today = Calendar.getInstance();
        int DD = today.get(Calendar.DATE);
        int WK = today.get(Calendar.DAY_OF_WEEK) - 1; // 1 for sunday

        long todayEvent = today.getTimeInMillis();
        today.set(Calendar.SECOND, 0);
        long nextEvent;
        today.set(Calendar.HOUR_OF_DAY, hour);
        today.set(Calendar.MINUTE, min);
        for (int i = WK; ; ) {
            if (week[i]) {
                nextEvent = today.getTimeInMillis();
                if (nextEvent > todayEvent)
                    break;
            }
            today.set(Calendar.DATE, ++DD);
            DD = today.get(Calendar.DATE);
            i++;
            if (i == 7)
                i = 0;
        }
        return nextEvent;
    }

}
