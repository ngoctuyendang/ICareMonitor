package com.example.icaremonitor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

public class Database extends SQLiteOpenHelper {
    public static final String TAG = "Database";
    public static final String TABLE_NAME2 = "history";
    public static final String COL1 = "id";
    public static final String COL2 = "Heart_Rate";
    public static final String COL3 = "Time";
    public static final String COL4 = "Note";

    public Database(Context context) {
        super(context, TABLE_NAME2, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable= ("CREATE TABLE " + TABLE_NAME2 + "("
                + COL1 +" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL2 + " TEXT, "
                + COL3 + " TEXT, "
                + COL4 + " TEXT);");

        db.execSQL(createTable);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP  TABLE IF EXISTS " + TABLE_NAME2);
        onCreate(db);
    }
    public boolean INSERT_Data(String item, String time, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, item);
        contentValues.put(COL3, time);
        contentValues.put(COL4,note);
        long result = db.insert(TABLE_NAME2, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT *FROM " + TABLE_NAME2;
        Cursor data = db.rawQuery(query, null);
        return data;
    }
    public void  deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME2, null, null);
        db.close();
    }
    public void DELETE_Data(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String a = "DELETE FROM "+Database.TABLE_NAME2+" WHERE id = "+id+"";
        db.execSQL(a);
    }
    public void updateName(String newNote, int id, String oldName){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME2 + " SET " + COL4 +
                " = '" + newNote + "' WHERE " + COL1 + " = '" + id + "'" +
                " AND " + COL4 + " = '" + oldName + "'";
        db.execSQL(query);
    }


}
