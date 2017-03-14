package com.moviestan.app.util;

import org.json.JSONObject;

public class JSONGetter {

    public static String get(JSONObject obj, String item){
        String returnString = "";

        try{
            returnString = obj.getString(item);
        }catch (Exception e){

        }

        return returnString;
    }
}
