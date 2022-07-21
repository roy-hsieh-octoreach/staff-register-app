package com.octoreach.poct.ascensia.cpo.util;

import android.os.Environment;

import java.io.File;

public class DebugLog {
    public static void logFileDelete() {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

        File logFile = new File(path + "/log.txt");
        if (logFile.exists())
            logFile.delete();
    }

    public static void logWrite(String log, boolean append) {

        /*
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        try {
            FileWriter fw = new FileWriter(path + "/log.txt", append);
            BufferedWriter bw = new BufferedWriter(fw); //將BufferedWeiter與FileWrite物件做保持
            bw.write(df.format(new Date()) + ":" + log);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

         */
    }
}
