package com.moviestan.app.data;


import com.moviestan.app.util.JSONGetter;
import com.moviestan.app.util.LogFactory;

import org.json.JSONObject;

import java.io.Serializable;


public class AngelSerializer implements Serializable{
    public String username;
    public String name;
    public String email;
    public String line_id;
    public String phone_number;

    public static AngelSerializer fromJSON(String json) {
        AngelSerializer data = null;
        try {
            JSONObject userObject = new JSONObject(json);
            data = new AngelSerializer();
            data.username 		= JSONGetter.get(userObject, "AngelServiceNo");
            data.name 			= JSONGetter.get(userObject, "Name");
            data.email 			= JSONGetter.get(userObject, "Email");
            data.line_id 		= JSONGetter.get(userObject, "LineID");
            data.phone_number 	= JSONGetter.get(userObject, "PhoneNumber");

        } catch (Exception e) {
            LogFactory.set("AngelSerializer Error", e);
        }
        return data;
    }
}
