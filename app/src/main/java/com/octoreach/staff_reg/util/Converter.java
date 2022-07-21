package com.octoreach.poct.ascensia.cpo.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;
import static java.lang.Math.abs;

public class Converter {

    /**過濾用的byte array to hex string 0714**/
    /**
     * 過濾model number、serial number的"00" 因資料會多給"00"
     **/
    public static String byteArrayToHexStr(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }

        StringBuilder hex = new StringBuilder(byteArray.length * 2);
        for (byte aData : byteArray) {
            if (!String.format("%02X", aData).equals("00"))
                hex.append(String.format("%02X", aData));
        }
        String gethex = hex.toString();
        return gethex;
    }

    /**
     * Little Endian with length byte array to hex string
     **/
    //len = (len-1)~0
    public static String byteArrayToHexStrLEWithLen(byte[] byteArray, int len) {
        if (byteArray == null) {
            return null;
        }

        StringBuilder hex = new StringBuilder(len * 2);
        for (int i = len - 1; i >= 0; i--) {
            hex.append(String.format("%02X", byteArray[i]));
        }
        return hex.toString();
    }

    /**
     * Little Endian with length byte array to hex string
     **/
    public static String byteArrayToHexStrLEWithLen(byte[] byteArray, int beginIdx, int len) {
        if (byteArray == null) {
            return null;
        }

        StringBuilder hex = new StringBuilder(len * 2);
        for (int i = beginIdx; i > beginIdx - len; i--) {
            hex.append(String.format("%02X", byteArray[i]));
        }
        String gethex = hex.toString();
        return gethex;
    }


    /**
     * 正常版的byte array to hex string 0714
     **/
    public static String byteArrayToHexStr2(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }

        StringBuilder hex = new StringBuilder(byteArray.length * 2);
        for (byte aData : byteArray) {
            hex.append(String.format("%02X", aData));
        }
        String gethex = hex.toString();
        return gethex;
    }

    public byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }


    public static long getTimeSecondDiff(long currentTime, long time) {
//        Date date = new Date();
//        long secNow = date.getTime() / 1000;

        Log.d(TAG, "currentTime :" + getDateFromSeconds(currentTime));
        Log.d(TAG, "time :" + getDateFromSeconds(time));


        return abs(currentTime - time);
    }

    public static long getErrorRecodeTimeSec(byte[] getByteData) {
        String timeStamp = byteArrayToHexStrLEWithLen(getByteData, 4);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = null;

        //Error code time stamp is total seconds since 2000/1/1
        try {
            date = sdf.parse("2000-01-01T00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long timeSec = date.getTime() / 1000;
        timeSec += Long.parseLong(timeStamp, 16);

//        Log.d(TAG, "ErrorRecodeTime:" + getDateFromSeconds(timeSec));
        Log.d(TAG, "ErrorRecodeTimeSec :" + timeSec);

        return timeSec;
    }

    /**
     * 秒數轉化為日期
     */
    public static String getDateFromSeconds(long seconds) {
        if (seconds == 0)
            return " ";
        else {
            Date date = new Date();
            try {
                date.setTime(seconds * 1000);
            } catch (NumberFormatException nfe) {
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            return sdf.format(date);
        }
    }

    public static long getDateStringToSeconds(String dateString) {
        if (dateString == null)
            return 0;
        else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = null;
            try {
                date = sdf.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return date.getTime() / 1000;
        }
    }

    public static long getDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

}
