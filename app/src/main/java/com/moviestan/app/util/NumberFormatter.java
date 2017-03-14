package com.moviestan.app.util;

import java.text.DecimalFormat;

public class NumberFormatter {

    public static int parseInt(String str){

        int returnVal = 0;

        try {
            if (str != null) {
                if (str.length() > 0) {
                    returnVal = Integer.parseInt(str.trim());
                }
            }
        }catch (Exception e){

        }

        return returnVal;
    }

    public static double parseDouble(String str){

        double returnVal = 0;

        try {
            if(str != null){
                if(str.length() > 0){
                    returnVal = Double.parseDouble(str.trim());
                }
            }
        }catch (Exception e){

        }

        return returnVal;
    }

    public static int parseInt(String str, String removeChar){

        int returnVal = 0;
        try {
            str = str.replace(removeChar, "");

            if (str != null) {
                if (str.length() > 0) {
                    returnVal = Integer.parseInt(str);
                }
            }
        }catch(Exception e){

        }
        return returnVal;
    }

    public static double parseDouble(String str, String removeChar){

        double returnVal = 0;
        try {
            str = str.replace(removeChar, "");

            if (str != null) {
                if (str.length() > 0) {
                    returnVal = Double.parseDouble(str.trim());
                }
            }
        }catch(Exception e){

        }
        return returnVal;
    }

    public static String dateFormat(int number){
        String returnVal = number + "";

        if(number < 10){
            returnVal = "0" + number;
        }

        return returnVal;
    }

    public static double weightFormat(double get_result){

        double returnVal = 0;
        try {
            DecimalFormat df = new DecimalFormat("##.0");
            returnVal = Double.parseDouble(df.format(get_result));
        }catch(Exception e){

        }
        return returnVal;
    }
}
