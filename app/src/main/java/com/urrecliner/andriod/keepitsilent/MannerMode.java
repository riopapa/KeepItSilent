package com.urrecliner.andriod.keepitsilent;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.VibrationEffect;
import android.os.Vibrator;

import static android.content.Context.VIBRATOR_SERVICE;
import static com.urrecliner.andriod.keepitsilent.Vars.beepManner;
import static com.urrecliner.andriod.keepitsilent.Vars.mainContext;
import static com.urrecliner.andriod.keepitsilent.Vars.utils;

class MannerMode {

    static private String logID = "MannerMode";
    private static MediaPlayer mpStart, mpFinish;

    static void turnOn(Context context, String subject, boolean vibrate) {
//        final String text = subject + ", Go into Silent";
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        assert am != null;
        beepStart();
        vibratePhoneByPattern(context);
//        Toast.makeText(context,text,Toast.LENGTH_LONG).show();
        if (vibrate)
            am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        else {
            if(am.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }
    }

    static void turnOff(Context context, String subject) {
//        final  String text = subject + ", Return to normal";
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        assert am != null;
        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        vibratePhoneByPattern(context);
        beepFinish();
    }

    private static void vibratePhoneByPattern(Context context) {
        long[] pattern = {0, 100, 1000, 300, 200, 100, 500, 200, 100};
        Vibrator v = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        assert v != null;
        v.vibrate(VibrationEffect.createWaveform(pattern, -1));
    }

    private static void beepStart() {

        if (beepManner) {
            if (mpStart == null) {
                utils.log(logID, "creating beep");
                mpStart = MediaPlayer.create(mainContext, R.raw.manner_starting);
                mpStart.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                    }
                });
            }
            else
                mpStart.start();
        }
    }

    private static void beepFinish() {

        if (beepManner) {
            if (mpFinish == null) {
                utils.log(logID, "creating beep");
                mpFinish = MediaPlayer.create(mainContext, R.raw.manner_return2normal);
                mpFinish.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                    }
                });
            }
            else
                mpFinish.start();
        }
    }

}
