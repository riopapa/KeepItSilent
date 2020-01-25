package com.urrecliner.andriod.keepitsilent;

import java.io.Serializable;

public class SilentInfo implements Serializable {
    private String subject;
    private int startHour, startMin, finishHour, finishMin;
    private boolean active;
    private boolean[] week = {true, true, true, true, true, true, true};
    private boolean vibrate;

    SilentInfo() { }

    SilentInfo(String subject, int startHour, int startMin, int finishHour, int finishMin,
               boolean[] week, boolean active, boolean vibrate) {
        this.subject = subject;
        this.startHour = startHour;
        this.startMin = startMin;
        this.finishHour = finishHour;
        this.finishMin = finishMin;
        System.arraycopy(week, 0, this.week, 0, 7);
        this.active = active;
        this.vibrate = vibrate;
    }

    String getSubject() {
        return subject;
    }
    void setSubject(String subject) { this.subject = subject; }

    int getStartHour() { return startHour; }

    int getStartMin() { return startMin; }

    int getFinishHour() { return finishHour; }

    int getFinishMin() { return finishMin; }

    boolean getActive() { return active; }
    void setActive(boolean TorF) { this.active = TorF; }

    boolean[] getWeek() { return week; }

    boolean getVibrate() { return vibrate; }

}
