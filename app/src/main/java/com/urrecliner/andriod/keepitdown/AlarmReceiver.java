package com.urrecliner.andriod.keepitdown;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Objects;

import static com.urrecliner.andriod.keepitdown.Vars.ReceiverCase;
import static com.urrecliner.andriod.keepitdown.Vars.beepManner;
import static com.urrecliner.andriod.keepitdown.Vars.reminder;
import static com.urrecliner.andriod.keepitdown.Vars.utils;

public class AlarmReceiver extends BroadcastReceiver {

    String subject;
    boolean vibrate;

    @Override
    public void onReceive(Context context, Intent intent) {
        utils = new Utils();
//        utils.log("AlarmReceiver", "Activated -- action is " + intent.getAction()+" ReceiverCase: "+ReceiverCase);

        Bundle args = intent.getBundleExtra("DATA");
        assert args != null;
        reminder = (Reminder) args.getSerializable("reminder");
        assert reminder != null;
        subject = reminder.getSubject();
        int uniqueId = reminder.getUniqueId();
        String caseSFO = Objects.requireNonNull(intent.getExtras()).getString("case");
        utils.log("reminder"," case: "+ caseSFO + " uniqueId "+uniqueId+" subject: "+subject);
        assert caseSFO != null;
        switch (caseSFO) {
            case "S":   // start
                vibrate = reminder.getVibrate();
                setMannerOn(context, subject, vibrate);
                break;
            case "F":   // finish
                setMannerOff(context, subject);
                ReceiverCase = "Alarm";
                Intent i = new Intent(context, MainActivity.class);
                i.putExtra("ReceiverCase","Alarm");
                i.putExtra("DATA",args);
                context.startActivity(i);
                break;
            case "O":   // onetime
                setMannerOff(context, subject);
                reminder.setActive(false);
                DatabaseIO dbIO = new DatabaseIO(context);
                dbIO.update(reminder.getId(), reminder);
                break;
            default:
                utils.logE("receive","Case Error " + caseSFO);
        }
    }

    public void setMannerOn (Context context, String subject, boolean vibrate) {
        final String text = subject + "\nGo into Silent";
        utils.log("MannerOn",text);
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        assert am != null;
        if (vibrate)
            am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        else
            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        if (beepManner) {
            final MediaPlayer mp = MediaPlayer.create(context, R.raw.manner_go_into_silent);
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.setVolume(0.5f, 0.5f);
                    mediaPlayer.start();
                }
            });
        }
        Toast.makeText(context,text,Toast.LENGTH_LONG).show();
    }
    public void setMannerOff (Context context, String subject) {
        final  String text = subject + "\nReturn to normal";
        utils.log("MannerOff",text);
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        assert am != null;
        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        if (beepManner) {
            final MediaPlayer mp = MediaPlayer.create(context, R.raw.manner_return_to_normal);
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
        }
        Toast.makeText(context, text,Toast.LENGTH_LONG).show();
    }

//    private static void dumpIntent(Intent i){
//        String LOG_TAG = "dump";
//        Bundle bundle = i.getExtras();
//        if (bundle != null) {
//            Log.e(LOG_TAG,"-- Dumping Intent start");
//            Log.e(LOG_TAG,bundle.toString());
//            Set<String> keys = bundle.keySet();
//            for (String key : keys) {
//                Log.e(LOG_TAG, "[" + key + "=" + bundle.get(key) + "]");
//            }
//            Log.e(LOG_TAG,"-- Dumping Intent end");
//        }
//    }
}
