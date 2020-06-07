package com.urrecliner.keepitsilent;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import static com.urrecliner.keepitsilent.Vars.STATE_BOOT;
import static com.urrecliner.keepitsilent.Vars.mainContext;
import static com.urrecliner.keepitsilent.Vars.stateCode;
import static com.urrecliner.keepitsilent.Vars.utils;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        if (utils == null) {
//            utils = new Utils();
////            mainContext = context;
//        }
        String logID = STATE_BOOT;
//        utils.log(logID, "Activated  ------------- " + intent.getAction());
//        Log.e(logID,"ACTIVATED");
        stateCode = STATE_BOOT;
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("stateCode", stateCode);
        context.startActivity(i);
    }
}
