package com.moviestan.app.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFormatter {

    private static String dayString 	= "days";
    private static String monthString 	= "months";
    private static String yearString  	= "years";

    public static String cal(String dateString){
        String returnString = "";

        // set date
        String dateUnformat = dateString; //comment.date;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            Date getFormattedDate = format.parse(dateUnformat);
            Calendar getCalendar = Calendar.getInstance();
            getCalendar.setTime(getFormattedDate);
//
//            Calendar getCurrentCalendar = Calendar.getInstance();
//
//            long dataDateMi    = getCalendar.getTimeInMillis();
//            long currentDateMi = getCurrentCalendar.getTimeInMillis();
//
//            long diffDateMi = currentDateMi - dataDateMi;
//            Calendar finalCalendar = Calendar.getInstance();
//            finalCalendar.setTimeInMillis(Math.abs(diffDateMi));
//
//            int getDays = finalCalendar.get(Calendar.DAY_OF_YEAR);
//
//            // less then 30 days
//            if(getDays <= 30){
//                returnString = getDays-1 + " " + dayString;
//            }else if(getDays <= 365){
//                returnString = (getDays/30)-1 + " " + monthString;
//            }else{
//                returnString = (getDays/365)-1 + " " + yearString;
//            }

            returnString = getCalendar.get(Calendar.YEAR) + "." + (getCalendar.get(Calendar.MONTH)+1) + "." + getCalendar.get(Calendar.DAY_OF_MONTH);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return returnString;
    }
}
