package com.hfad.treasure;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.*;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String  DB_NAME = "local_storage.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_LOCATION = "current_location";
    private static final String TABLE_CONTENT = "available_content";
    private static final String L_CREATE_STR = "ll_id INTEGER PRIMARY KEY AUTOINCREMENT, accauntName TEXT, userName TEXT, latitude TEXT, longitude TEXT, dt TEXT, isSent INTEGER";
    private static final String C_CREATE_STR = "ac_id INTEGER PRIMARY KEY AUTOINCREMENT, accauntName TEXT, userName TEXT, contentType TEXT, itemName TEXT, dt TEXT, isDownloaded INTEGER";

    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        CreateTable(sqLiteDatabase, TABLE_LOCATION, L_CREATE_STR);
        CreateTable(sqLiteDatabase, TABLE_CONTENT, C_CREATE_STR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTENT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        onCreate(sqLiteDatabase);
    }

    private void CreateTable(SQLiteDatabase sqLiteDatabase, String tableName, String fieldsString){
        String createTable = "CREATE TABLE IF NOT EXISTS " + tableName + "(" + fieldsString + ")";
        sqLiteDatabase.execSQL(createTable);
    }


    public boolean addData(String tableName, ContentValues contentValues, String idName) throws SQLiteException {
        Log.d(TAG + " addData." + tableName , "Adding " + contentValues.toString() + " to " + tableName);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insertOrThrow(tableName, null, contentValues);
/*
        if (result == -1) {
            return false;
        } else {
            int id = 0;
            Cursor insertedId = getLastRecord(tableName, "1", "1", idName, false);
            while (insertedId.moveToNext()) {
                id = insertedId.getInt(0);
            }
            Log.d(TAG + " addData." + tableName , "insertedId " + id + " to " + tableName);
            return true;
        }

 */
        return true;
    }

    public void addMultipleData(String tableName, ArrayList<ContentValues> contentValuesArrayList, String idName){
        int i = 0;
        for (ContentValues cv: contentValuesArrayList){
            try {
                addData(tableName, cv, idName);
            }catch (SQLiteException e){
                Log.d(TAG + " addMultipleData." , e.getMessage());
            }
            i = i + 1;
        }
        Log.d(TAG + " addMultipleData: " , "inserted: " + i + " records into " + tableName);
    }

    public boolean updData(String tableName, ContentValues contentValues, String idName, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG + " updData." + tableName, "updData: Updating " + contentValues.toString() + " to " + tableName);

        long result = db.update(tableName, contentValues, idName + " == ?", new String[]{String.valueOf(id)});
        if(result == 0){
            return false;
        } else {
            return  true;
        }
    }

    public Cursor getData(String tableName, String idName, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + tableName + " WHERE " + idName + " in (?) ";
        Cursor data = db.rawQuery(query, new String[]{String.valueOf(id)});

        Log.d(TAG + " getData." + tableName, "Query: " + query);

        return data;
    }

    public Cursor getLastRecord(String tableName, String whereParam, String whereValue, String idName, boolean sortDesc){
        String sortDirection = sortDesc ? "DESC" : "";
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + tableName + " WHERE " + whereParam + " in (?)" + " ORDER BY " + idName + " " + sortDirection + " limit 1";
        Cursor data = db.rawQuery(query, new String[]{whereValue});

        Log.d(TAG + " getLastRecord." + tableName, "Query: " + query);

        return data;
    }
}
