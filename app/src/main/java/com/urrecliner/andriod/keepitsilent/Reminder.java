package com.urrecliner.andriod.keepitsilent;

import java.io.Serializable;

public class Reminder implements Serializable {
    private long id;
    private int uniqueId;
    private String subject;
    private int startHour, startMin, finishHour, finishMin;
    private boolean active;
    private boolean[] week = {true, true, true, true, true, true, true};
    private boolean vibrate;

    Reminder() { }

    Reminder(long id, int uniqueId, String subject, int startHour, int startMin, int finishHour, int finishMin,
             boolean[] week, boolean active, boolean vibrate) {
        this.id = id;
        this.uniqueId = uniqueId;
        this.subject = subject;
        this.startHour = startHour;
        this.startMin = startMin;
        this.finishHour = finishHour;
        this.finishMin = finishMin;
        System.arraycopy(week, 0, this.week, 0, 7);
        this.active = active;
        this.vibrate = vibrate;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    int getUniqueId() { return uniqueId; }
    void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
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

    Reminder getDefaultReminder() {
        id = 1;
        uniqueId = (int) (System.currentTimeMillis() % 1000000L);
        subject = "WeekDay Night";
        startHour = 23; startMin = 30; finishHour= 7; finishMin = 30;
        week = new boolean[]{false, true, true, true, true, true, false};
        return new Reminder(id, uniqueId, subject, startHour, startMin, finishHour, finishMin,
                week, true, true);
    }

}
