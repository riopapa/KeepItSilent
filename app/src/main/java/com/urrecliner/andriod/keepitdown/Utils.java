package com.urrecliner.andriod.keepitdown;

import android.content.Context;
import android.media.AudioManager;
import android.os.Vibrator;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Utils {
    public void Utils() {
    }
    public String getFullDateTimeFormat(long t) {
        String strDateFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat, Locale.US);
        return sdf.format(t);
    }
    public String getDateFormat(long t) {
        String strDateFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat, Locale.US);
        return sdf.format(t);
    }
    public String getTimeFormat(long t) {
        String strDateFormat = "HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat, Locale.US);
        return sdf.format(t);
    }
    public String geMinuteFormat(long t) {
        String strDateFormat = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat, Locale.US);
        return sdf.format(t);
    }

    public void letPhoneVibrate(Context context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(400);
    }

    public void setMannerOn (Context context, boolean vibrate) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
        am.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
        am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);
        am.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, 0);
        if (vibrate)
            am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        else
            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }

    public void setMannerOff (Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);
        am.setStreamVolume(AudioManager.STREAM_RING, am.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
        am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, am.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION), 0);
        am.setStreamVolume(AudioManager.STREAM_SYSTEM, am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM), 0);
    }
}
