package com.moviestan.app.util;

import java.net.URLEncoder;

public class URLFormatter {

    public static String urlFormatter(String keyword){
        String returnVal = keyword;
        try {
            returnVal = URLEncoder.encode(keyword, "utf-8");
        }catch(Exception e){

        }
        return returnVal;
    }
}
