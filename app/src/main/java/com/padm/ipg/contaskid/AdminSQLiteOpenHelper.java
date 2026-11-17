package com.padm.ipg.contaskid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** Stores one local high-score record, preserving the original database schema. */
public final class AdminSQLiteOpenHelper extends SQLiteOpenHelper {
    public static final class Record {
        public final String name;
        public final int score;

        Record(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }

    public AdminSQLiteOpenHelper(Context context) {
        super(context, "BD", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pontos(nome TEXT NOT NULL, score INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No migration required: this version keeps the original schema.
    }

    public Record getBestScore() {
        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT nome, score FROM pontos ORDER BY score DESC LIMIT 1", null)) {
            if (cursor.moveToFirst()) {
                return new Record(cursor.getString(0), cursor.getInt(1));
            }
            return null;
        }
    }

    public void saveIfRecord(String name, int score) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            int previous = -1;
            try (Cursor cursor = db.rawQuery("SELECT MAX(score) FROM pontos", null)) {
                if (cursor.moveToFirst() && !cursor.isNull(0)) {
                    previous = cursor.getInt(0);
                }
            }
            if (score > previous) {
                ContentValues values = new ContentValues();
                values.put("nome", name);
                values.put("score", score);
                db.delete("pontos", null, null);
                db.insertOrThrow("pontos", null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
