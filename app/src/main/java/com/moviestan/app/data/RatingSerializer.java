package com.moviestan.app.data;


import com.moviestan.app.util.JSONGetter;
import com.moviestan.app.util.LogFactory;

import org.json.JSONObject;

import java.io.Serializable;


public class RatingSerializer implements Serializable{
    public String fb_id;
    public String name;
    public String score;
    public String comments;
    public String date;
    public static RatingSerializer fromJSON(String json) {
        RatingSerializer data = null;
        try {
            JSONObject userObject = new JSONObject(json);
            data = new RatingSerializer();
            data.fb_id 		    = JSONGetter.get(userObject, "UserFB");
            data.name 		    = JSONGetter.get(userObject, "UserName");
            data.score 		    = JSONGetter.get(userObject, "Score");
            data.comments 		= JSONGetter.get(userObject, "Comments");
            data.date 		    = JSONGetter.get(userObject, "Date");

        } catch (Exception e) {
            LogFactory.set("RatingSerializer Error", e);
        }
        return data;
    }
}
