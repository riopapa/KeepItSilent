package com.urrecliner.andriod.keepitsilent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.urrecliner.andriod.keepitsilent.Vars.stateCode;
import static com.urrecliner.andriod.keepitsilent.Vars.utils;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (utils == null)
            utils = new Utils();
        String logID = "booted";
        utils.log(logID, "Activated " + intent.getAction());
        stateCode = "Boot";
        Intent i = new Intent(context, MainActivity.class);
        i.putExtra("stateCode", stateCode);
        context.startActivity(i);
    }
}