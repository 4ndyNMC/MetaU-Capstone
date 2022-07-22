package com.example.metaucapstone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.metaucapstone.models.Recipe;
import com.example.metaucapstone.models.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = "DatabaseHelper";
    public static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, "data.db", null, DATABASE_VERSION);
        initCache(this.getWritableDatabase());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        initCache(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        clearCache(db);
    }

    private void initCache(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS friends (uid TEXT PRIMARY KEY, profilePicUrl TEXT, object BLOB)");
        db.execSQL("CREATE TABLE IF NOT EXISTS usernames (uid TEXT PRIMARY KEY, username TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS recipes (id TEXT PRIMARY KEY, object BLOB)");
        db.execSQL("CREATE TABLE IF NOT EXISTS images (id TEXT PRIMARY KEY, object BLOB)");
        db.execSQL("CREATE TABLE IF NOT EXISTS userData (stat TEXT PRIMARY KEY, value TEXT)");
    }

    public void clearCache(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS friends");
        db.execSQL("DROP TABLE IF EXISTS usernames");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS images");
        db.execSQL("DROP TABLE IF EXISTS userData");
    }

    public boolean pfpStored() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM images WHERE id = ?", new String[] {"default"});
        boolean hasValue = cursor.getCount() > 0;
        cursor.close();
        return hasValue;
    }

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
            contentValues.put("object", serializedBitmap);

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

    public boolean hasUid(String uid) {
        return tableContains("usernames", "uid", uid);
    }

    public boolean insertUsername(String uid, String username) {
        if (hasUid(uid)) return updateUsername(uid, username);
        return tableInsert("usernames", new HashMap<String, Object>() {{
            put("uid", uid);
            put("username", username);
        }});
    }

    public boolean updateUsername(String uid, String username) {
        return tableUpdate("usernames", "uid = ?", uid, new HashMap<String, Object>() {{
            put("uid", uid);
            put("username", username);
        }});
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

    public boolean insertFriend(String uid, String profilePicUrl, User obj) throws IOException {
        if (hasFriend(uid)) return updateFriend(uid, profilePicUrl, obj);
        return tableInsert("friends", new HashMap<String, Object>() {{
            put("uid", uid);
            put("profilePicUrl", profilePicUrl);
            put("object", serializeObject(obj));
        }});
    }

    public boolean updateFriend(String uid, String profilePicUrl, User obj) throws IOException {
        return tableUpdate("friends", "uid = ?", uid, new HashMap<String, Object>() {{
            put("uid", uid);
            put("profilePicUrl", profilePicUrl);
            put("object", serializeObject(obj));
        }});
    }

    public boolean deleteFriend(String uid) {
        return tableDelete("friends", "uid", uid);
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

    public boolean hasRecipe(String id) {
        return tableContains("recipes", "id", id);
    }

    public boolean insertRecipe(String id, Recipe obj) throws IOException {
        if (hasRecipe(id)) return updateRecipe(id, obj);
        return tableInsert("recipes", new HashMap<String, Object>() {{
            put("id", id);
            put("object", serializeObject(obj));
        }});
    }

    public boolean updateRecipe(String id, Recipe obj) throws IOException {
        return tableUpdate("recipes", "id = ?", id, new HashMap<String ,Object>() {{
            put("id", id);
            put("object", serializeObject(obj));
        }});
    }

    public boolean deleteRecipe(String id) {
        return tableDelete("recipes", "id", id);
    }

    public Cursor getRecipeData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM recipes", null);
    }

    public boolean hasUserData(String stat) {
        return tableContains("userData", "stat", stat);
    }

    public boolean setUserData(String stat, String value) {
        if (hasUserData(stat)) return updateUserData(stat, value);
        return tableInsert("userData", new HashMap<String, Object>() {{
            put("stat", stat);
            put("value", value);
        }});
    }

    public boolean updateUserData(String stat, String value) {
        return tableUpdate("userData", "stat = ?", stat, new HashMap<String, Object>() {{
            put("stat", stat);
            put("value", value);
        }});
    }

    private byte[] serializeObject(Object obj) {
        byte[] serializedObject = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();
            serializedObject = bos.toByteArray();
            bos.close();
        } catch (IOException e) {
            Log.e(TAG, "serialization error: " + e);
        }
        return serializedObject;
    }

    private boolean tableInsert(String table, HashMap<String, Object> values) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for (String key : values.keySet()) {
            if (key.equals("object")) contentValues.put(key, (byte[]) values.get(key));
            else contentValues.put(key, (String) values.get(key));
        }
        long result = db.insert(table, null, contentValues);
        return result != -1;
    }

    private boolean tableUpdate(String table, String whereClause, String column, HashMap<String, Object> values) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for (String key : values.keySet()) {
            if (key.equals("object")) contentValues.put(key, (byte[]) values.get(key));
            else contentValues.put(key, (String) values.get(key));
        }
        long result = db.update(table, contentValues, whereClause, new String[] {column});
        return result != -1;
    }

    private boolean tableDelete(String table, String column, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (tableContains(table, column, value)) {
            long result = db.delete(table, column + " = ?", new String[] {value});
            return result != -1;
        }
        return false;
    }

    private boolean tableContains(String table, String column, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table + " WHERE " + column + " = ?", new String[]{value});
        boolean hasValue = cursor.getCount() > 0;
        cursor.close();
        return hasValue;
    }
}
