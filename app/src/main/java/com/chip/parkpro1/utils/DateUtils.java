package com.chip.parkpro1.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtils {

    public static String getSpecificFormat(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
        return sdf.format(System.currentTimeMillis());
    }

    public static String getPostTime(String pattern) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, 1);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
        return sdf.format(cal.getTime());
    }

    public static long getEpochFromStringDate(String date) {
        java.text.DateFormat format = new SimpleDateFormat(DateUtils.DateFormat.DATE_FORMAT + " ",Locale.ENGLISH);
        try {
            Date date1 = format.parse(date);
            return date1.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            //noinspection deprecation
            return new Date(date).getTime();
        }

    }

    public static String getStringDateFromEpoch(long date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
        return sdf.format(new Date(date));
    }

    public static Boolean isSameDay(long day1, long day2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(day1);
        cal2.setTimeInMillis(day2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static String changePattern(String date, String fromPattern, String toPattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(fromPattern, Locale.ENGLISH);
        Date testDate = null;
        try {
            testDate = sdf.parse(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat(toPattern, Locale.ENGLISH);
        return formatter.format(testDate);
    }

    public enum DateFormat {
        DATE_FORMAT ("EEEE MMMM d, yyy"),
        HOURS_FORMAT("h:mm aa");

        private String txt;
        DateFormat(String t){
            this.txt = t;
        }

        @Override
        public String toString() {
            return txt;
        }
    }

}