package com.urrecliner.andriod.keepitdown;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.urrecliner.andriod.keepitdown.Vars.colorActive;
import static com.urrecliner.andriod.keepitdown.Vars.colorActiveBack;
import static com.urrecliner.andriod.keepitdown.Vars.colorInactiveBack;
import static com.urrecliner.andriod.keepitdown.Vars.colorOff;
import static com.urrecliner.andriod.keepitdown.Vars.colorOffBack;
import static com.urrecliner.andriod.keepitdown.Vars.colorOn;
import static com.urrecliner.andriod.keepitdown.Vars.listViewWeek;
import static com.urrecliner.andriod.keepitdown.Vars.oneTimeId;

public class ListViewAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private ArrayList<Reminder> myReminder;
    public ListViewAdapter(Context context, ArrayList<Reminder> myReminder) {
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

        ImageView iv_icon = listItem.findViewById(R.id.iv_icon);
        int resource;
        if (vibrate)
            resource = (active) ? R.mipmap.ic_phone_vibrate :R.mipmap.ic_phone_vibrate_notactive;
        else
            resource = (active) ? R.mipmap.ic_phone_silent : R.mipmap.ic_phone_silent_notactive;
        iv_icon.setImageResource(resource);

        TextView tv = listItem.findViewById(R.id.tv_subject);
        tv.setText(myReminder.get(position).getSubject());
        tv.setTextColor((active) ? colorOn:colorOff);

        int uniqueId = myReminder.get(position).getUniqueId();
        if (uniqueId == oneTimeId) {
            for (int i = 0; i < 7; i++) {
                TextView tV = listItem.findViewById(listViewWeek[i]);
                tV.setTextColor(colorOffBack);  // transparent
            }
            String txt = "-";
            tv = listItem.findViewById(R.id.tv_StartTime); tv.setText(txt);
            tv.setTextColor((active) ? colorOn:colorOff);
            txt = (""+(myReminder.get(position).getFinishHour()+100)).substring(1) + ":" + (""+(myReminder.get(position).getFinishMin()+100)).substring(1);
            tv = listItem.findViewById(R.id.tv_FinishTime); tv.setText(txt);
            tv.setTextColor((active) ? colorOn:colorOff);
        }
        else{
            boolean week[] = myReminder.get(position).getWeek();
            for (int i = 0; i < 7; i++) {
                TextView tV = listItem.findViewById(listViewWeek[i]);
                tV.setTextColor(week[i] ? colorActive : colorOff);
                if (active)
                    tV.setBackgroundColor(week[i] ? colorActiveBack : colorOffBack);
                else
                    tV.setBackgroundColor(week[i] ? colorInactiveBack : colorOffBack);
            }
            String txt = (""+(myReminder.get(position).getStartHour()+100)).substring(1) + ":" + (""+(myReminder.get(position).getStartMin()+100)).substring(1);
            tv = listItem.findViewById(R.id.tv_StartTime); tv.setText(txt);
            tv.setTextColor((active) ? colorOn:colorOff);
            txt = (""+(myReminder.get(position).getFinishHour()+100)).substring(1) + ":" + (""+(myReminder.get(position).getFinishMin()+100)).substring(1);
            tv = listItem.findViewById(R.id.tv_FinishTime); tv.setText(txt);
            tv.setTextColor((active) ? colorOn:colorOff);
        }

        return listItem;
    }
}
