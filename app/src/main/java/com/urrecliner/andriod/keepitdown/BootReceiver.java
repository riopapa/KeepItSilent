package com.urrecliner.andriod.keepitdown;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.urrecliner.andriod.keepitdown.Vars.Receiver;
import static com.urrecliner.andriod.keepitdown.Vars.utils;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        utils = new Utils();
        utils.log("BootReceiver", "Activated -- action is " + intent.getAction());
        Receiver = "Boot";
        Intent i = new Intent(context, MainActivity.class);
        i.putExtra("Receiver", "Boot");
        context.startActivity(i);
    }
}
