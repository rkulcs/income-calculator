package com.incomecalculator.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * A collection of methods for initializing or updating a database.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ApplicationDatabase.db";

    public DatabaseHelper(@Nullable Context context, @Nullable String name,
                          @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context) {
        this(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(Contract.CurrencyInformation.SQL_CREATE_TABLE);
        db.execSQL(Contract.WageInformation.SQL_CREATE_TABLE);
        db.execSQL(Contract.ShiftInformation.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int previousVersion, int nextVersion) {

    }

    /**
     * Gets an instance of a writeable database to be used by the application.
     */
    public static SQLiteDatabase getDatabase(Context context) {

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        return dbHelper.getWritableDatabase();
    }
}
