package com.urrecliner.andriod.keepitsilent;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.widget.Toast;

import static android.content.Context.VIBRATOR_SERVICE;
import static com.urrecliner.andriod.keepitsilent.Vars.beepManner;
import static com.urrecliner.andriod.keepitsilent.Vars.utils;

class MannerMode {

    private static MediaPlayer mp;
    static private String logID = "MannerMode";
    static void turnOn(Context context, String subject, boolean vibrate) {
        final String text = subject + ", Go into Silent";
        utils.log(logID,text);
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        assert am != null;
        if (beepManner) {
            if (mp == null)
                mp = MediaPlayer.create(context, R.raw.manner_starting);
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) { mediaPlayer.start(); }
            });
        }
        vibratePhone(context);
        Toast.makeText(context,text,Toast.LENGTH_LONG).show();
        if (vibrate)
            am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        else
            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }

    static void turnOff(Context context, String subject) {
        final  String text = subject + ", Return to normal";
        utils.log(logID, text);
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        assert am != null;
        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        Toast.makeText(context, text,Toast.LENGTH_LONG).show();
        vibratePhone(context);
        if (beepManner) {
            mp = MediaPlayer.create(context, R.raw.manner_return2normal);
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
        }
    }

    private static void vibratePhone(Context context) {
        long[] pattern = {0, 100, 1000, 300, 200, 100, 500, 200, 100};
        Vibrator v = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
//        if (Build.VERSION.SDK_INT >= 26) {
//            v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
//        }
//        else {
            v.vibrate(pattern, -1);
//        }
    }
}
