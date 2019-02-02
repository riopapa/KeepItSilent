package com.urrecliner.andriod.keepitdown;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    Context context;
    public void Utils(Context context) {
        this.context = context;
    }

    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
    final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss sss", Locale.KOREA);

    public void append2file(String textLine) {

        File directory = getDirectory();
        BufferedWriter bw = null;
        FileWriter fw = null;
        String fullName = directory.toString() + "/" + "KeepItDown_" + dateFormat.format(new Date())+".txt";
        try {
            File file = new File(fullName);
            // if file doesnt exists, then create it
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    logE("createFile", " Error");
                }
            }
            String outText = "\n"+textLine;
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.write(outText);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) bw.close();
                if (fw != null) fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private File getDirectory() {
        File directory = new File(Environment.getExternalStorageDirectory(), "KeepItDown");
        try {
            if (!directory.exists()) {
                directory.mkdirs();
            }
        } catch (Exception e) {
            Log.e("creating Directory error", directory.toString() + "_" + e.toString());
        }
        return directory;
    }

    public void log(String tag, String text) {
        StackTraceElement[] traces;
        traces = Thread.currentThread().getStackTrace();
        String log = " " + traces[5].getMethodName() + " > " + traces[4].getMethodName() + " > " + traces[3].getMethodName() + " #" + traces[3].getLineNumber() + " "+text;
        Log.w(tag , log);
        append2file(timeFormat.format(new Date())+" : " +log);
    }

    public void logE(String tag, String text) {
        StackTraceElement[] traces;
        traces = Thread.currentThread().getStackTrace();
        String where = " " + traces[5].getMethodName() + " > " + traces[4].getMethodName() + " > " + traces[3].getMethodName() + " #" + traces[3].getLineNumber();
        Log.e("<" + tag + ">" , where + " " + text);
    }

}
