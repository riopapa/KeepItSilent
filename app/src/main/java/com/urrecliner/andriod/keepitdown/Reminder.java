package com.urrecliner.andriod.keepitdown;

import java.io.Serializable;

public class Reminder implements Serializable {
    private long id;
    private int uniqueId;
    private String subject;
    private int startHour, startMin, finishHour, finishMin;
    private boolean active;
    private boolean[] week = {true, true, true, true, true, true, true};
    private boolean vibrate;

    public Reminder() { }

    public Reminder(long id, int uniqueId, String subject, int startHour, int startMin, int finishHour, int finishMin,
                    boolean week[], boolean active, boolean vibrate) {
        this.id = id;
        this.uniqueId = uniqueId;
        this.subject = subject;
        this.startHour = startHour;
        this.startMin = startMin;
        this.finishHour = finishHour;
        this.finishMin = finishMin;
        for (int i = 0; i < 7; i++) this.week[i] = week[i];
        this.active = active;
        this.vibrate = vibrate;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public int getUniqueId() { return uniqueId; }
    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) { this.subject = subject; }

    public int getStartHour() { return startHour; }

    public int getStartMin() { return startMin; }

    public int getFinishHour() { return finishHour; }

    public int getFinishMin() { return finishMin; }

    public boolean getActive() { return active; }
    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean[] getWeek() {
        boolean week[] = new boolean[7];
        for (int i = 0; i < 7; i++) week[i] = this.week[i];
        return week;
    }

    public boolean getVibrate() { return vibrate; }

    public Reminder getDefaultReminder() {
        id = 1;
        uniqueId = (int) (System.currentTimeMillis() % 1000000000L);
        subject = "Weekday Silent";
        startHour = 23; startMin = 30; finishHour= 7; finishMin = 30;
        week = new boolean[]{false, true, true, true, true, true, false};
        return new Reminder(id, uniqueId, subject, startHour, startMin, finishHour, finishMin,
                week, true, true);
    }

}
