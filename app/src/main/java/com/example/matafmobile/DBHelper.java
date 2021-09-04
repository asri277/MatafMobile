package com.example.matafmobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "collectionRecord.db";
    public static final String TABLE_NAME = "cRecord_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "staffID";
    public static final String COL_3 = "staffName";
    public static final String COL_4 = "location"; //from QR
    public static final String COL_5 = "date";
    public static final String COL_6 = "time";


    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ TABLE_NAME +"("+COL_1+" INTEGER PRIMARY KEY AUTOINCREMENT ,"+COL_2+" TEXT ,"+COL_3+" TEXT ,"+COL_4+" TEXT ,"+COL_5+" TEXT , "+COL_6+" TEXT )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String staffID, String staffName, String location, String date, String time){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,staffID);
        contentValues.put(COL_3,staffName);
        contentValues.put(COL_4,location);
        contentValues.put(COL_5,date);
        contentValues.put(COL_6,time);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME, null ); //res = result
        return res;
    }


}
