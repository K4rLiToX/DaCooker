package es.android.dacooker.services;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BBDD_Helper extends SQLiteOpenHelper {

    // Version y nombre de la BD usada por SQLite para la aplicacion
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "dacooker.db";

    //Constructor del BD Helper que nos permite ejecutar las diferentes operaciones dentro de la bd
    public BBDD_Helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Creacion de la BD usada cuando no existe en el sistema
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Struct_BD.SQL_CREATE_RECIPE);
        db.execSQL(Struct_BD.SQL_CREATE_STEP);
        db.execSQL(Struct_BD.SQL_CREATE_INGREDIENT);
    }

    //Si incrementamos la version de la BD, se borraran las tablas y se volveran a crear vacias
    //Posibilidad de hacerse manualmente, aunque asi es mas comodo
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(Struct_BD.SQL_DELETE_RECIPE);
        db.execSQL(Struct_BD.SQL_DELETE_STEP);
        db.execSQL(Struct_BD.SQL_DELETE_INGREDIENT);
        onCreate(db);
    }

    //Posibilidad de retroceso en la BD
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


}
