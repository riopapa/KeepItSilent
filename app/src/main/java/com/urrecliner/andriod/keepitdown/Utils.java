package com.urrecliner.andriod.keepitdown;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Collator;
import java.util.Date;

import static com.urrecliner.andriod.keepitdown.Vars.sdfDate;
import static com.urrecliner.andriod.keepitdown.Vars.sdfDateTimeLog;

class Utils {
//    private Context context;
//    void Utils(Context context) {
//        this.context = context;
//    }

    String hourMin (int hour, int min) { return int2NN(hour)+":"+int2NN(min); }
    private String int2NN (int nbr) {
        return (""+(100 + nbr)).substring(1);
    }

    private void append2file(String textLine) {

        File directory = getDirectory();
        BufferedWriter bw = null;
        FileWriter fw = null;
        String fullName = directory.toString() + "/" + "log_" + sdfDate.format(new Date())+".txt";
        try {
            File file = new File(fullName);
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    logE("createFile", " Error");
                }
            }
            String outText = "\n"+textLine+"\n";
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
        File directory = new File(Environment.getExternalStorageDirectory(), "Keep It Down");
        try {
            if (!directory.exists()) {
                if(directory.mkdirs()) {
                    Log.e("mkdirs","Failed "+directory);
                }
            }
        } catch (Exception e) {
            Log.e("creating Directory error", directory.toString() + "_" + e.toString());
        }
        return directory;
    }

    void log(String tag, String text) {
        int pid = android.os.Process.myPid();
        StackTraceElement[] traces;
        traces = Thread.currentThread().getStackTrace();
        String log = pid+ " " + traces[5].getMethodName() + " > " + traces[4].getMethodName() + " > " + traces[3].getMethodName() + " #" + traces[3].getLineNumber() + " {"+ tag + "} " + text;
        Log.w(tag , log);
        append2file(sdfDateTimeLog.format(new Date())+" : " +log);
    }

    void logE(String tag, String text) {
        StackTraceElement[] traces;
        traces = Thread.currentThread().getStackTrace();
        String log = " " + traces[5].getMethodName() + " > " + traces[4].getMethodName() + " > " + traces[3].getMethodName() + " #" + traces[3].getLineNumber() + " {"+ tag + "} " + text;
        Log.e("<" + tag + ">" , log);
        append2file(sdfDateTimeLog.format(new Date())+" : " +log);
    }

    void deleteOldFiles() {     // remove older than 5 days

        String oldDate = sdfDate.format(System.currentTimeMillis() - 3*24*60*60*1000L);
        File packageDirectory = getDirectory();
        File[] files = getDirectoryList(packageDirectory);
        Collator myCollator = Collator.getInstance();
        for (File file : files) {
            String shortFileName = file.getName();
            if (myCollator.compare(shortFileName, oldDate) < 0) {
                if (file.delete())
                    Log.e("file","Delete Error "+file);
            }
        }
    }

    private File[] getDirectoryList(File fullPath) {
        return fullPath.listFiles();
    }

}
