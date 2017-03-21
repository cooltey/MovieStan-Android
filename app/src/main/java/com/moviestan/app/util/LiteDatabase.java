package com.moviestan.app.util;

import android.content.Context;
import android.content.SharedPreferences;

public class LiteDatabase {


    // column
    public String FACEBOOK_ID       = "FACEBOOK_ID";
    public String FACEBOOK_EMAIL    = "FACEBOOK_EMAIL";
    public String FACEBOOK_NAME     = "FACEBOOK_NAME";
    public String APP_USER_ID       = "APP_USER_ID";
    public String APP_USER_TOKEN    = "APP_USER_TOKEN";


    private String mCoreDatabase = "Lite_Database";
    private SharedPreferences mLiteDB;
    private Context mContext;

    public LiteDatabase(Context context){
        mContext     = context;
        mLiteDB = mContext.getSharedPreferences(mCoreDatabase, 0);
    }

    public void save(String set_column, String set_value){
        mLiteDB.edit().putString(set_column, set_value).commit();
    }

    public void save(String set_column, int set_value){
        mLiteDB.edit().putString(set_column, set_value + "").commit();
    }

    public String get(String set_column){
        return mLiteDB.getString(set_column, "0");
    }

    public String getEmpty(String set_column){
        return mLiteDB.getString(set_column, "");
    }

    public void clear(){
        mLiteDB.edit().clear().commit();
    }
}
