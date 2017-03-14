package com.moviestan.app.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final String DATABASE = "database.db";

    private static final int DATABASEVERSION = 1;

    private SQLiteDatabase db;

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE, null, DATABASEVERSION);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String DATABASE_CREATE_TABLE = "CREATE TABLE my_diary (_ID INTEGER PRIMARY KEY, " +
                " diary_date DATETIME, " +
                " today_weight TEXT, " +
                " cal_breakfast_total TEXT, " +
                " cal_breakfast_list TEXT, " +
                " cal_breakfast_cal_list TEXT, " +
                " cal_lunch_total TEXT, " +
                " cal_lunch_list TEXT, " +
                " cal_lunch_cal_list TEXT, " +
                " cal_dinner_total TEXT, " +
                " cal_dinner_list TEXT, " +
                " cal_dinner_cal_list TEXT, " +
                " cal_teatime_total TEXT, " +
                " cal_teatime_list TEXT, " +
                " cal_teatime_cal_list TEXT, " +
                " cal_nightdinner_total TEXT, " +
                " cal_nightdinner_list TEXT, " +
                " cal_nightdinner_cal_list TEXT, " +
                " cal_sport_total TEXT, " +
                " cal_sport_list TEXT, " +
                " cal_sport_cal_list TEXT, " +
                " cal_drink TEXT, " +
                " toilet_status TEXT, " +
                " mc_status TEXT, " +
                " coin_status TEXT, " +
                " reduce_weight TEXT, " +
                " achieve_percentage TEXT)";

        db.execSQL(DATABASE_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS my_diary");
        onCreate(db);
    }

    public Cursor getAll(String tableName, String whereStr)
    {
        LogFactory.set("DB Query", "SELECT * FROM " + tableName + whereStr);
        return db.rawQuery("SELECT * FROM " + tableName + whereStr, null);
    }

    public Cursor sql(String sqlString)
    {
//        Log.d("DB Query", "SELECT * FROM " + tableName + whereStr);
        return db.rawQuery(sqlString, null);
    }

    public long insert(String tableName, String[] column, String[] value)
    {
        ContentValues args = new ContentValues();
        for(int i = 0; i < column.length; i++)
        {
            args.put(column[i], value[i]);
            LogFactory.set("DB INSERT - " + column[i], value[i]);
        }

        return db.insert(tableName, null, args);
    }

    public int delete(String tableName, long rowId)
    {
        return db.delete(tableName,
                "_ID = " + rowId,
                null
        );
    }

    public int deleteWhere(String tableName, String where)
    {
        return db.delete(tableName,
                where,
                null
        );
    }

    public int update(String tableName, long rowId, String[] column, String[] value)
    {
        ContentValues args = new ContentValues();
        for(int i = 0; i < column.length; i++)
        {
            args.put(column[i], value[i]);
            LogFactory.set("DB UPDATE - " + column[i], value[i]);
        }
        return db.update(tableName,
                args,
                "_ID=" + rowId,
                null
        );
    }


}

