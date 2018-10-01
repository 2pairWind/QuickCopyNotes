package com.example.artur.mynotesapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.SimpleCursorAdapter;

public class DbOpenHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "notes.db";
    private static final String DB_TABLE = "notes";
    private static final String PRIMARY_KEY = "_id";
    private static final String COLUMN_NOTE = "note";
    private static final String COLUMN_TITLE = "title";
    private static final String DB_CREATE = "CREATE TABLE " +
        DB_TABLE + " (" + PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NOTE + " TEXT NOT NULL, " + COLUMN_TITLE + " TEXT NOT NULL);";
    private static final String[] FIELDS = {PRIMARY_KEY, COLUMN_TITLE, COLUMN_NOTE};
    private static final String[] FROM = {COLUMN_TITLE, COLUMN_NOTE};
    private static final int[] TO = {R.id.noteTitle, R.id.noteText};
    static final String EXTRA_NOTE_ID = "EXTRA_NOTE_ID";

    private Context mContext;
    private SQLiteDatabase mDb;

    DbOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
        mDb = (mDb == null) ? getWritableDatabase() : mDb;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public int saveNote(String title, String text, int id) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_NOTE, text);
        mDb = (mDb == null) ? getWritableDatabase() : mDb;

        if (id > -1) {
            return mDb.update(DB_TABLE, values,PRIMARY_KEY + "=" + id, null);
        } else {
            return (int) mDb.insert(DB_TABLE, null, values);
        }
    }

    public void deleteNote(long id) {
        mDb = (mDb == null) ? getWritableDatabase() : mDb;
        mDb.delete(DB_TABLE, PRIMARY_KEY + "=" + id, null);
    }

    public String[] getNoteById(long id) {
        mDb = (mDb == null) ? getWritableDatabase() : mDb;
        Cursor c = mDb.query(DB_TABLE, FIELDS, PRIMARY_KEY + "=" + id,
                null, null, null, null);

        String[] note = new String[2];
        if (c != null) {
            c.moveToFirst();
            note[0] = c.getString(c.getColumnIndexOrThrow(COLUMN_TITLE));
            note[1] = c.getString(c.getColumnIndexOrThrow(COLUMN_NOTE));
            c.close();
        }

        return note;
    }

    public SimpleCursorAdapter getAdapter() {
        return new SimpleCursorAdapter(mContext, R.layout.list_item, null, FROM, TO, 0);
    }

    public Cursor getCursor() {
        mDb = (mDb == null) ? getWritableDatabase() : mDb;
        return mDb.query(DB_TABLE, FIELDS,null, null, null, null, null);
    }
}
