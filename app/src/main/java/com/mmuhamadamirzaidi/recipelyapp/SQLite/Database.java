package com.mmuhamadamirzaidi.recipelyapp.SQLite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class Database extends SQLiteAssetHelper {

    public static final String DB_NAME = "RecipelyAppDatabase.db";
    public static final int DB_VERSION = 1;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void addToBookmark(String recipeId){

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO Bookmark(RecipeId) VALUES ('%s');", recipeId);

        db.execSQL(query);
    }

    public void clearBookmark(String recipeId){

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM Bookmark WHERE recipeId = '%s';", recipeId);

        db.execSQL(query);
    }

    public boolean currentBookmark(String recipeId){

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT * FROM Bookmark WHERE recipeId = '%s';", recipeId);

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}
