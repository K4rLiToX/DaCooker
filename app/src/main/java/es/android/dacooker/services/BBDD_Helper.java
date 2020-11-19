package es.android.dacooker.services;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BBDD_Helper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "dacooker.db";

    public BBDD_Helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Struct_BD.SQL_CREATE_RECIPE);
        db.execSQL(Struct_BD.SQL_CREATE_STEP);
        db.execSQL(Struct_BD.SQL_CREATE_INGREDIENT);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(Struct_BD.SQL_DELETE_RECIPE);
        db.execSQL(Struct_BD.SQL_DELETE_STEP);
        db.execSQL(Struct_BD.SQL_DELETE_INGREDIENT);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


}
