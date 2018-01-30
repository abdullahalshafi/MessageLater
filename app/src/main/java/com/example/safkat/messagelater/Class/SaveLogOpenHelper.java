package com.example.safkat.messagelater.Class;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Safkat on 7/18/2017.
 */

public class SaveLogOpenHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SaveLog.db";

    public SaveLogOpenHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SaveLog.DefineTable.TABLE_NAME + " (" +
                    SaveLog.DefineTable.COLUMN_NAME_PNO + " TEXT," +
                    SaveLog.DefineTable.COLUMN_NAME_TIME + " TEXT,"+
                    SaveLog.DefineTable.COLUMN_NAME_UID + " TEXT)";
}
