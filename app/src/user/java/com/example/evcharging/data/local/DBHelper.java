package com.example.evcharging.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Centralised SQLite helper for caching API responses and queuing
 * pending API requests while the device is offline.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "evcharging.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_CACHED_RESPONSES = "cached_responses";
    private static final String TABLE_PENDING_REQUESTS = "pending_requests";

    private static volatile DBHelper instance;

    public static class PendingRequest {
        public final long id;
        public final String method;
        public final String endpoint;
        public final String body;
        public final long createdAt;

        public PendingRequest(long id, String method, String endpoint, String body, long createdAt) {
            this.id = id;
            this.method = method;
            this.endpoint = endpoint;
            this.body = body;
            this.createdAt = createdAt;
        }
    }

    private DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DBHelper.class) {
                if (instance == null) {
                    instance = new DBHelper(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CACHED_RESPONSES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "endpoint TEXT NOT NULL," +
                "request_key TEXT NOT NULL DEFAULT ''," +
                "response TEXT NOT NULL," +
                "last_updated INTEGER NOT NULL," +
                "UNIQUE(endpoint, request_key) ON CONFLICT REPLACE" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_PENDING_REQUESTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "method TEXT NOT NULL," +
                "endpoint TEXT NOT NULL," +
                "body TEXT," +
                "created_at INTEGER NOT NULL" +
                ")");

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_cached_endpoint_key " +
                "ON " + TABLE_CACHED_RESPONSES + " (endpoint, request_key)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No-op for initial release. Bump DB_VERSION when schema changes.
    }

    private String normaliseKey(@Nullable String requestKey) {
        return requestKey == null ? "" : requestKey;
    }

    public void saveCachedResponse(String endpoint, @Nullable String requestKey, String responseJson) {
        SQLiteDatabase db = getWritableDatabase();
        String key = normaliseKey(requestKey);
        ContentValues values = new ContentValues();
        values.put("endpoint", endpoint);
        values.put("request_key", key);
        values.put("response", responseJson);
        values.put("last_updated", System.currentTimeMillis());
        db.insertWithOnConflict(TABLE_CACHED_RESPONSES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    @Nullable
    public String getCachedResponse(String endpoint, @Nullable String requestKey) {
        SQLiteDatabase db = getReadableDatabase();
        String key = normaliseKey(requestKey);
        try (Cursor cursor = db.query(
                TABLE_CACHED_RESPONSES,
                new String[]{"response"},
                "endpoint = ? AND request_key = ?",
                new String[]{endpoint, key},
                null,
                null,
                "last_updated DESC",
                "1"
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        }
        return null;
    }

    public void clearCachedResponse(String endpoint, @Nullable String requestKey) {
        SQLiteDatabase db = getWritableDatabase();
        String key = normaliseKey(requestKey);
        db.delete(
                TABLE_CACHED_RESPONSES,
                "endpoint = ? AND request_key = ?",
                new String[]{endpoint, key}
        );
    }

    public long insertPendingRequest(String method, String endpoint, @Nullable String body) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("method", method);
        values.put("endpoint", endpoint);
        values.put("body", body);
        values.put("created_at", System.currentTimeMillis());
        return db.insert(TABLE_PENDING_REQUESTS, null, values);
    }

    public List<PendingRequest> getPendingRequests() {
        SQLiteDatabase db = getReadableDatabase();
        List<PendingRequest> requests = new ArrayList<>();
        try (Cursor cursor = db.query(
                TABLE_PENDING_REQUESTS,
                new String[]{"id", "method", "endpoint", "body", "created_at"},
                null,
                null,
                null,
                null,
                "created_at ASC"
        )) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    requests.add(new PendingRequest(
                            cursor.getLong(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getLong(4)
                    ));
                }
            }
        }
        return requests;
    }

    public void deletePendingRequest(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_PENDING_REQUESTS, "id = ?", new String[]{String.valueOf(id)});
    }
}
