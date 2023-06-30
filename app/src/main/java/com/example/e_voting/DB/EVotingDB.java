package com.example.e_voting.DB;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EVotingDB extends SQLiteOpenHelper {
    public static final String DBNAME= "votes.db";
    public static final String TABLE_NAME_CATEGORY = "category";
    public static final int VER = 1;

    public EVotingDB(@Nullable Context context) {
        super(context, DBNAME,null, VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String categoryQuery = "CREATE TABLE "+TABLE_NAME_CATEGORY+" (id INTEGER PRIMARY KEY, title TEXT)";
        db.execSQL(categoryQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String categoryQuery = "DROP TABLE IF EXISTS "+TABLE_NAME_CATEGORY;
        db.execSQL(categoryQuery);


        onCreate(db);
    }
    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT title FROM category", null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("title"));
                categories.add(category);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        // If no categories were found, add a default category
        if (categories.isEmpty()) {
            categories.add("Other");
        }

        return categories;
    }


}
