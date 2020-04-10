package com.urrecliner.andriod.keepitsilent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import static com.urrecliner.andriod.keepitsilent.Vars.colorActive;
import static com.urrecliner.andriod.keepitsilent.Vars.colorInactiveBack;
import static com.urrecliner.andriod.keepitsilent.Vars.colorOff;
import static com.urrecliner.andriod.keepitsilent.Vars.colorOffBack;
import static com.urrecliner.andriod.keepitsilent.Vars.colorOn;
import static com.urrecliner.andriod.keepitsilent.Vars.colorOnBack;
import static com.urrecliner.andriod.keepitsilent.Vars.listViewWeek;
import static com.urrecliner.andriod.keepitsilent.Vars.silentIdx;
import static com.urrecliner.andriod.keepitsilent.Vars.silentInfo;
import static com.urrecliner.andriod.keepitsilent.Vars.silentInfos;
import static com.urrecliner.andriod.keepitsilent.Vars.utils;

public class ListViewAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    ListViewAdapter(Context context) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return silentInfos.size();
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

        silentIdx = position;
        silentInfo = silentInfos.get(silentIdx);
        View listItem = convertView;
        if (listItem == null)
             listItem = layoutInflater.inflate(R.layout.reminder_info, null);

        boolean active = silentInfo.getActive();
        boolean vibrate = silentInfo.getVibrate();

        ImageView lvVibrate = listItem.findViewById(R.id.lv_vibrate);
        int resource;
        if (vibrate)
            resource = (active) ? R.mipmap.ic_phone_vibrate :R.mipmap.ic_phone_vibrate_notactive;
        else
            resource = (active) ? R.mipmap.ic_phone_silent : R.mipmap.ic_phone_silent_notactive;
        lvVibrate.setImageResource(resource);

        TextView tv = listItem.findViewById(R.id.tv_subject);
        tv.setText(silentInfo.getSubject());
        tv.setTextColor((active) ? colorOn:colorOff);

        if (silentIdx == 0) {
            for (int i = 0; i < 7; i++) {
                TextView tVWeek = listItem.findViewById(listViewWeek[i]);
                tVWeek.setTextColor(colorOffBack);  // transparent
            }
            String txt = "-";
            tv = listItem.findViewById(R.id.tv_StartTime); tv.setText(txt);
            tv.setTextColor((active) ? colorOn:colorOff);
            txt = utils.hourMin(silentInfo.getFinishHour(), silentInfo.getFinishMin());
            tv = listItem.findViewById(R.id.tv_FinishTime); tv.setText(txt);
            tv.setTextColor((active) ? colorOn:colorOff);
        }
        else{
            boolean[] week = silentInfo.getWeek();
            for (int i = 0; i < 7; i++) {
                TextView tV = listItem.findViewById(listViewWeek[i]);
                tV.setTextColor(week[i] ? colorActive : colorOff);
                if (active)
                    tV.setBackgroundColor(week[i] ? colorOnBack : colorOffBack);
                else
                    tV.setBackgroundColor(week[i] ? colorInactiveBack : colorOffBack);
            }
            String txt = utils.hourMin (silentInfo.getStartHour(), silentInfo.getStartMin());
            tv = listItem.findViewById(R.id.tv_StartTime); tv.setText(txt);
            tv.setTextColor((active) ? colorOn:colorOff);
            txt = utils.hourMin (silentInfo.getFinishHour(), silentInfo.getFinishMin());
            tv = listItem.findViewById(R.id.tv_FinishTime); tv.setText(txt);
            tv.setTextColor((active) ? colorOn:colorOff);
        }
        return listItem;
    }
}
