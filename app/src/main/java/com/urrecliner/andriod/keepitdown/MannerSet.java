package com.urrecliner.andriod.keepitdown;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.Toast;

import static com.urrecliner.andriod.keepitdown.Vars.beepManner;
import static com.urrecliner.andriod.keepitdown.Vars.utils;

class MannerSet {

    static void on (Context context, String subject, boolean vibrate) {
        final String text = subject + "\nGo into Silent";
        utils.log("MannerOn",text);
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        assert am != null;
        if (vibrate)
            am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        else
            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        if (beepManner) {
            final MediaPlayer mp = MediaPlayer.create(context, R.raw.manner_starting);
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) { mediaPlayer.start(); }
            });
        }
        Toast.makeText(context,text,Toast.LENGTH_LONG).show();
    }
    static void off (Context context, String subject) {
        final  String text = subject + "\nReturn to normal";
        utils.log("MannerOff", text);
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        assert am != null;
        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        if (beepManner) {
            final MediaPlayer mp = MediaPlayer.create(context, R.raw.manner_ending_call);
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
        }
        Toast.makeText(context, text,Toast.LENGTH_LONG).show();
    }

}
