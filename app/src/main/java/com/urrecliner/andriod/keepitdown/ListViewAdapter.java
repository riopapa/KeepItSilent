package com.urrecliner.andriod.keepitdown;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private ArrayList<Reminder> myReminder;
    private Context mContext;
    public ListViewAdapter(Context context, ArrayList<Reminder> myReminder) {
        mContext = context;
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
        boolean active, vibrate;
        View listItem = convertView;
        if (listItem == null) {
            listItem = layoutInflater.inflate(R.layout.list_reminder_layout, parent);
        }
        active = myReminder.get(position).getActive();
        vibrate = myReminder.get(position).getVibrate();
        int colorOn = ContextCompat.getColor(mContext,R.color.black);
        int colorOff = ContextCompat.getColor(mContext,R.color.gray);

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

        boolean week[] = myReminder.get(position).getWeek();

        tv = listItem.findViewById(R.id.lt_week0); tv.setTextColor(week[0] ? colorOn:colorOff);
        tv = listItem.findViewById(R.id.lt_week1); tv.setTextColor(week[1] ? colorOn:colorOff);
        tv = listItem.findViewById(R.id.lt_week2); tv.setTextColor(week[2] ? colorOn:colorOff);
        tv = listItem.findViewById(R.id.lt_week3); tv.setTextColor(week[3] ? colorOn:colorOff);
        tv = listItem.findViewById(R.id.lt_week4); tv.setTextColor(week[4] ? colorOn:colorOff);
        tv = listItem.findViewById(R.id.lt_week5); tv.setTextColor(week[5] ? colorOn:colorOff);
        tv = listItem.findViewById(R.id.lt_week6); tv.setTextColor(week[6] ? colorOn:colorOff);

        String txt = (""+(myReminder.get(position).getStartHour()+100)).substring(1) + ":" + (""+(myReminder.get(position).getStartMin()+100)).substring(1);
        tv = listItem.findViewById(R.id.tv_StartTime); tv.setText(txt);
        tv.setTextColor((active) ? colorOn:colorOff);
        txt = (""+(myReminder.get(position).getFinishHour()+100)).substring(1) + ":" + (""+(myReminder.get(position).getFinishMin()+100)).substring(1);
        tv = listItem.findViewById(R.id.tv_FinishTime); tv.setText(txt);
        tv.setTextColor((active) ? colorOn:colorOff);
        return listItem;
    }
}
