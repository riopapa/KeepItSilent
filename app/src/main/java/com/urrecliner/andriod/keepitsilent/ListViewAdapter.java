package com.urrecliner.andriod.keepitsilent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.urrecliner.andriod.keepitsilent.Vars.colorActive;
import static com.urrecliner.andriod.keepitsilent.Vars.colorOnBack;
import static com.urrecliner.andriod.keepitsilent.Vars.colorInactiveBack;
import static com.urrecliner.andriod.keepitsilent.Vars.colorOff;
import static com.urrecliner.andriod.keepitsilent.Vars.colorOffBack;
import static com.urrecliner.andriod.keepitsilent.Vars.colorOn;
import static com.urrecliner.andriod.keepitsilent.Vars.listViewWeek;
import static com.urrecliner.andriod.keepitsilent.Vars.ONETIME_ID;
import static com.urrecliner.andriod.keepitsilent.Vars.utils;

public class ListViewAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private ArrayList<Reminder> myReminder;
    ListViewAdapter(Context context, ArrayList<Reminder> myReminder) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.myReminder = myReminder;
    }

    @Override
    public int getCount() {
        return myReminder.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItem = convertView;
        if (listItem == null) {
             listItem = layoutInflater.inflate(R.layout.list_reminder_layout, null);
        }
        boolean active = myReminder.get(position).getActive();
        boolean vibrate = myReminder.get(position).getVibrate();

        ImageView lvVibrate = listItem.findViewById(R.id.lv_vibrate);
        int resource;
        if (vibrate)
            resource = (active) ? R.mipmap.ic_phone_vibrate :R.mipmap.ic_phone_vibrate_notactive;
        else
            resource = (active) ? R.mipmap.ic_phone_silent : R.mipmap.ic_phone_silent_notactive;
        lvVibrate.setImageResource(resource);

        TextView tv = listItem.findViewById(R.id.tv_subject);
        tv.setText(myReminder.get(position).getSubject());
        tv.setTextColor((active) ? colorOn:colorOff);

        int uniqueId = myReminder.get(position).getUniqueId();
        if (uniqueId == ONETIME_ID) {
            for (int i = 0; i < 7; i++) {
                TextView tVWeek = listItem.findViewById(listViewWeek[i]);
                tVWeek.setTextColor(colorOffBack);  // transparent
            }
            String txt = "-";
            tv = listItem.findViewById(R.id.tv_StartTime); tv.setText(txt);
            tv.setTextColor((active) ? colorOn:colorOff);
            txt = utils.hourMin(myReminder.get(position).getFinishHour(),myReminder.get(position).getFinishMin());
            tv = listItem.findViewById(R.id.tv_FinishTime); tv.setText(txt);
            tv.setTextColor((active) ? colorOn:colorOff);
        }
        else{
            boolean[] week = myReminder.get(position).getWeek();
            for (int i = 0; i < 7; i++) {
                TextView tV = listItem.findViewById(listViewWeek[i]);
                tV.setTextColor(week[i] ? colorActive : colorOff);
                if (active)
                    tV.setBackgroundColor(week[i] ? colorOnBack : colorOffBack);
                else
                    tV.setBackgroundColor(week[i] ? colorInactiveBack : colorOffBack);
            }
            String txt = utils.hourMin (myReminder.get(position).getStartHour(),myReminder.get(position).getStartMin());
            tv = listItem.findViewById(R.id.tv_StartTime); tv.setText(txt);
            tv.setTextColor((active) ? colorOn:colorOff);
            txt = utils.hourMin (myReminder.get(position).getFinishHour(),myReminder.get(position).getFinishMin());
            tv = listItem.findViewById(R.id.tv_FinishTime); tv.setText(txt);
            tv.setTextColor((active) ? colorOn:colorOff);
        }
        return listItem;
    }
}
