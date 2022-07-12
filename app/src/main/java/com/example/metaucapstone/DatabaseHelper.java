package com.example.metaucapstone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, "data.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE usernames (uid TEXT PRIMARY KEY, username TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usernames");
    }

    public boolean hasUsername(String username) {
        return hasValue("username", username);
    }

    public boolean hasUid(String uid) {
        return hasValue("uid", uid);
    }

    public boolean hasValue(String col, String val) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usernames WHERE " + col + " = ?", new String[]{val});
        boolean hasValue = cursor.getCount() > 0;
        cursor.close();
        return hasValue;
    }

    public boolean insertUsername(String uid, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("uid", uid);
        contentValues.put("username", username);
        if (hasUid(uid)) return updateUsername(uid, username);

        long result = db.insert("usernames", null, contentValues);
        return result != -1;
    }

    public boolean updateUsername(String uid, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("uid", uid);
        contentValues.put("username", username);
        if (hasUid(uid)) {
            long result = db.update("usernames", contentValues, "uid=?", new String[] {uid});
            return result != -1;
        }
        return false;
    }

    public boolean deleteUsername(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (hasUsername(username)) {
            long result = db.delete("usernames", "username=?", new String[] {username});
            return result != -1;
        }
        return false;
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM usernames", null);
    }
}
