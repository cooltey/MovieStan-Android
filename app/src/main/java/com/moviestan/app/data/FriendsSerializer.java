package com.moviestan.app.data;


import com.moviestan.app.util.JSONGetter;
import com.moviestan.app.util.LogFactory;

import org.json.JSONObject;

import java.io.Serializable;


public class FriendsSerializer implements Serializable{
    public String user_id;
    public String fb_id;
    public String name;
    public String date;
    public String accpetance;
    public static FriendsSerializer fromJSON(String json) {
        FriendsSerializer data = null;
        try {
            JSONObject userObject = new JSONObject(json);
            data = new FriendsSerializer();
            data.user_id 		= JSONGetter.get(userObject, "UserId");
            data.fb_id 		    = JSONGetter.get(userObject, "UserFB");
            data.name 		    = JSONGetter.get(userObject, "UserName");
            data.date 		    = JSONGetter.get(userObject, "Date");
            data.accpetance     = JSONGetter.get(userObject, "Acceptance");

        } catch (Exception e) {
            LogFactory.set("FriendsSerializer Error", e);
        }
        return data;
    }
}
