package com.urrecliner.andriod.keepitdown;

import java.io.Serializable;

public class Reminder implements Serializable {
    private long id;
    private String subject;
    private int startHour, startMin, finishHour, finishMin;
    private boolean active;
    private boolean[] week = {true, true, true, true, true, true, true};
    private boolean vibrate;

    public Reminder() {

    }

    public Reminder(long id, String subject, int startHour, int startMin, int finishHour, int finishMin,
                    boolean week[], boolean vibrate) {
        this.id = id;
        this.subject = subject;
        this.startHour = startHour;
        this.startMin = startMin;
        this.finishHour = finishHour;
        this.finishMin = finishMin;
        for (int i = 0; i < 7; i++) this.week[i] = week[i];
        this.vibrate = vibrate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public int getStartHour() { return startHour; }

    public int getStartMin() { return startMin; }

    public int getFinishHour() { return finishHour; }

    public int getFinishMin() { return finishMin; }

    public boolean getActive() { return active; }

    public boolean[] getWeek() {
        boolean week[] = new boolean[7];
        for (int i = 0; i < 7; i++) week[i] = this.week[i];
        return week;
    }

    public boolean getVibrate() { return vibrate; }
}
