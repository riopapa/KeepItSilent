package com.urrecliner.keepitsilent;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.urrecliner.keepitsilent.databinding.ReminderInfoBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.urrecliner.keepitsilent.Vars.addNewSilent;
import static com.urrecliner.keepitsilent.Vars.colorActive;
import static com.urrecliner.keepitsilent.Vars.colorInactiveBack;
import static com.urrecliner.keepitsilent.Vars.colorOff;
import static com.urrecliner.keepitsilent.Vars.colorOffBack;
import static com.urrecliner.keepitsilent.Vars.colorOn;
import static com.urrecliner.keepitsilent.Vars.colorOnBack;
import static com.urrecliner.keepitsilent.Vars.listViewWeek;
import static com.urrecliner.keepitsilent.Vars.mainContext;
import static com.urrecliner.keepitsilent.Vars.silentIdx;
import static com.urrecliner.keepitsilent.Vars.silentInfo;
import static com.urrecliner.keepitsilent.Vars.silentInfos;
import static com.urrecliner.keepitsilent.Vars.utils;
import static com.urrecliner.keepitsilent.databinding.ReminderInfoBinding.*;

public class RecycleViewAdapter  extends RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder> {

    public RecycleViewAdapter() {
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ReminderInfoBinding binding;
        public MyViewHolder(ReminderInfoBinding b){
            super(b.getRoot());
            binding = b;
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        return new MyViewHolder(ReminderInfoBinding.inflate(LayoutInflater.from(parent.getContext())));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){

        silentIdx = position;
        silentInfo = silentInfos.get(silentIdx);
        boolean active = silentInfo.getActive();
        boolean vibrate = silentInfo.getVibrate();
        if (vibrate)
            holder.binding.lvVibrate.setImageResource((active) ? R.mipmap.ic_phone_vibrate :R.mipmap.ic_phone_vibrate_notactive);
        else
            holder.binding.lvVibrate.setImageResource((active) ? R.mipmap.ic_phone_silent : R.mipmap.ic_phone_silent_notactive);

        holder.binding.rmdSubject.setText(silentInfo.getSubject());
        holder.binding.rmdSubject.setTextColor((active) ? colorOn:colorOff);

        TextView [] tViewWeek = new TextView[7];
        tViewWeek[0] = holder.binding.ltWeek0; tViewWeek[1] = holder.binding.ltWeek1; tViewWeek[2] = holder.binding.ltWeek2;
        tViewWeek[3] = holder.binding.ltWeek3; tViewWeek[4] = holder.binding.ltWeek4; tViewWeek[5] = holder.binding.ltWeek5;
        tViewWeek[6] = holder.binding.ltWeek6;
        if (silentIdx == 0) {
            for (int i = 0; i < 7; i++) {
                tViewWeek[i].setTextColor(colorOffBack);  // transparent
            }
            String txt = "-";
            holder.binding.rmdStartTime.setText(txt);
            holder.binding.rmdStartTime.setTextColor((active) ? colorOn:colorOff);
            txt = utils.hourMin(silentInfo.getFinishHour(), silentInfo.getFinishMin());
            holder.binding.rmdFinishTime.setText(txt);
        }
        else{
            boolean[] week = silentInfo.getWeek();
            for (int i = 0; i < 7; i++) {
                tViewWeek[i].setTextColor(week[i] ? colorActive : colorOff);
                if (active)
                    tViewWeek[i].setBackgroundColor(week[i] ? colorOnBack : colorOffBack);
                else
                    tViewWeek[i].setBackgroundColor(week[i] ? colorInactiveBack : colorOffBack);
            }
            String txt = utils.hourMin (silentInfo.getStartHour(), silentInfo.getStartMin());
            holder.binding.rmdStartTime.setText(txt);
            holder.binding.rmdStartTime.setTextColor((active) ? colorOn:colorOff);
            txt = utils.hourMin (silentInfo.getFinishHour(), silentInfo.getFinishMin());
            holder.binding.rmdFinishTime.setText(txt);
        }
        holder.binding.rmdFinishTime.setTextColor((active) ? colorOn:colorOff);
        holder.binding.getRoot().setTag(""+silentIdx);
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                silentIdx = Integer.parseInt(v.getTag().toString());
                silentInfo = silentInfos.get(silentIdx);
                Intent intent;
                if (silentIdx != 0) {
                    addNewSilent = false;
                    intent = new Intent(mainContext, AddUpdateActivity.class);
                } else {
                    intent = new Intent(mainContext, OneTimeActivity.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mainContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount(){
        return silentInfos.size();
    }
}
