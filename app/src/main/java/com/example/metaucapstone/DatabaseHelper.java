package com.example.metaucapstone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.metaucapstone.models.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = "DatabaseHelper";
    public static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, "data.db", null, DATABASE_VERSION);
        initDb(this.getWritableDatabase());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "DATABASE CREATED");
        initDb(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usernames");
        db.execSQL("DROP TABLE IF EXISTS friends");
    }

    private void initDb(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS friends (uid TEXT PRIMARY KEY, profilePicUrl TEXT, object BLOB)");
        db.execSQL("CREATE TABLE IF NOT EXISTS usernames (uid TEXT PRIMARY KEY, username TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS images (image BLOB)");
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
        if (hasUid(uid)) return updateUsername(uid, username);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("uid", uid);
        contentValues.put("username", username);
        Log.i(TAG, username + " username");

        long result = db.insert("usernames", null, contentValues);
        db.close();
        return result != -1;
    }

    public boolean updateUsername(String uid, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("uid", uid);
        contentValues.put("username", username);
        if (hasUid(uid)) {
            long result = db.update("usernames", contentValues, "uid=?", new String[] {uid});
            db.close();
            return result != -1;
        }
        db.close();
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

    public Cursor getUsernameData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM usernames", null);
    }

    public boolean hasFriend(String uid) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM friends WHERE uid = ?", new String[] {uid});
        boolean hasValue = cursor.getCount() > 0;
        cursor.close();
        return hasValue;
    }

    // TODO: abstract populating the ContentValues object in insertFriend & updateFriend
    //       into helper method
    public boolean insertFriend(String uid, String profilePicUrl, User obj) throws IOException {
        if (hasFriend(uid)) return updateFriend(uid, profilePicUrl, obj);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();
            byte[] serializedUser = bos.toByteArray();
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("uid", uid);
            contentValues.put("profilePicUrl", profilePicUrl);
            contentValues.put("object", serializedUser);

            long result = db.insert("friends", null, contentValues);
            return result != -1;
        } finally {
            bos.close();
        }
    }

    public boolean updateFriend(String uid, String profilePicUrl, User obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();
            byte[] serializedUser = bos.toByteArray();
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("uid", uid);
            contentValues.put("profilePicUrl", profilePicUrl);
            contentValues.put("object", serializedUser);

            long result = db.update("friends", contentValues, "uid = ?", new String[] {uid});
            return result != -1;
        } finally {
            bos.close();
        }
    }

    public Cursor getFriendsData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM friends", null);
    }
}
