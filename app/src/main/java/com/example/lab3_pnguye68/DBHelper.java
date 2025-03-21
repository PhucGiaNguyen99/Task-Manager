package com.example.lab3_pnguye68;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "todo.db";
    public static final String TABLE_NAME = "todo_items";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ITEM = "item";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ITEM + " TEXT)");
        seedData(db);
    }

    private void seedData(SQLiteDatabase db) {
        db.execSQL("INSERT INTO " + TABLE_NAME + " (item)  VALUES ('Buy groceries'), ('Finish homework'), ('Workout')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Cursor getAllItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT item FROM " + TABLE_NAME, null);
    }

    public void insertItem(String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM, item);
        db.insert(TABLE_NAME, null, values);
    }

    public void deleteItem(String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ITEM + " = ?", new String[]{item});
    }

    public void updateItem(String oldItem, String newItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM, newItem);
        db.update(TABLE_NAME, values, COLUMN_ITEM + " = ?", new String[]{oldItem});
    }

    public void deleteDoneItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ITEM + " LIKE ? ", new String[]{"Done: %"});
    }
}
