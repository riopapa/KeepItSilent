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
import static com.urrecliner.andriod.keepitdown.Vars.sdfLog;

class Utils {
//    private Context context;
//    void Utils(Context context) {
//        this.context = context;
//    }

    void append2file(String textLine) {

        File directory = getDirectory();
        BufferedWriter bw = null;
        FileWriter fw = null;
        String fullName = directory.toString() + "/" + "KeepItDown_" + sdfDate.format(new Date())+".txt";
        try {
            File file = new File(fullName);
            // if file doesnt exists, then create it
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

    void log(String tag, String text) {
        StackTraceElement[] traces;
        traces = Thread.currentThread().getStackTrace();
        String log = " " + traces[5].getMethodName() + " > " + traces[4].getMethodName() + " > " + traces[3].getMethodName() + " #" + traces[3].getLineNumber() + " "+text;
        Log.w(tag , log);
        append2file(sdfLog.format(new Date())+" : " +log);
    }

    void logE(String tag, String text) {
        StackTraceElement[] traces;
        traces = Thread.currentThread().getStackTrace();
        String log = " " + traces[5].getMethodName() + " > " + traces[4].getMethodName() + " > " + traces[3].getMethodName() + " #" + traces[3].getLineNumber() + " " + text;
        Log.e("<" + tag + ">" , log);
        append2file(sdfLog.format(new Date())+" : " +log);
    }

    void deleteOldFiles() {     // remove older than 5 days

        String weekAgo = sdfDate.format(System.currentTimeMillis() - 5*24*60*60*1000L);
        File packageDirectory = getDirectory();
        File[] files = getDirectoryList(packageDirectory);
        Collator myCollator = Collator.getInstance();
        for (File file : files) {
            String shortFileName = file.getName();
            if (myCollator.compare(shortFileName, weekAgo) < 0) {
                file.delete();
            }
        }
    }

    File[] getDirectoryList(File fullPath) {
        File[] files = fullPath.listFiles();
//        log("# of files", "in dir : " + files.length);
        return files;
    }

}
