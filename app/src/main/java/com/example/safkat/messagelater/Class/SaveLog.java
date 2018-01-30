package com.example.safkat.messagelater.Class;

import android.provider.BaseColumns;

/**
 * Created by Safkat on 7/18/2017.
 */

public class SaveLog {

    private SaveLog() {}

    public static class DefineTable implements BaseColumns{

        public static final String TABLE_NAME = "log";
        public static final String COLUMN_NAME_PNO = "p_no";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_UID = "u_id";

    }





}
