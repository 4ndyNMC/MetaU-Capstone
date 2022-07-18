package com.example.metaucapstone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.metaucapstone.models.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = "DatabaseHelper";
    public static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, "data.db", null, DATABASE_VERSION);
        initCache(this.getWritableDatabase());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "DATABASE CREATED");
        initCache(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        clearCache(db);
    }

    private void initCache(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS friends (uid TEXT PRIMARY KEY, profilePicUrl TEXT, object BLOB)");
        db.execSQL("CREATE TABLE IF NOT EXISTS usernames (uid TEXT PRIMARY KEY, username TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS images (id TEXT PRIMARY KEY, image BLOB)");
    }

    public void clearCache(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS usernames");
        db.execSQL("DROP TABLE IF EXISTS friends");
        db.execSQL("DROP TABLE IF EXISTS images");
    }

    public boolean pfpStored() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM images WHERE id = ?", new String[] {"default"});
        boolean hasValue = cursor.getCount() > 0;
        cursor.close();
        return hasValue;
    }

    // TODO: abstract serialization
    public boolean storePfp(Bitmap img) throws IOException {
        if (pfpStored()) return true;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(bos);
            CachedBitmap cachedBitmap = new CachedBitmap(img);
            cachedBitmap.writeObject(out);
            byte[] serializedBitmap = bos.toByteArray();
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", "default");
            contentValues.put("image", serializedBitmap);

            long result = db.insert("images", null, contentValues);
            return result != -1;
        } catch (IOException e) {
            Log.e(TAG, "serialization error: " + e);
            bos.close();
        }
        return false;
    }

    public Bitmap getDefaultPfp() throws IOException, ClassNotFoundException {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM images WHERE id = ?", new String[] {"default"});
        cursor.moveToNext();
        byte[] serializedImage = cursor.getBlob(1);
        ByteArrayInputStream bis = new ByteArrayInputStream(serializedImage);
        ObjectInputStream in = new ObjectInputStream(bis);
        CachedBitmap cachedBitmap = new CachedBitmap(null);
        cachedBitmap.readObject(in);
        return cachedBitmap.bitmap;
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
        ObjectOutputStream out;
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

    public Cursor getFriendsData(String uid) {
        if (!hasFriend(uid)) return null;
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM friends WHERE uid = ?", new String[] {uid});
    }
}
