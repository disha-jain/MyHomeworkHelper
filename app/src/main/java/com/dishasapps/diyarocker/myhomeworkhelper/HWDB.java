package com.dishasapps.diyarocker.myhomeworkhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by diyarocker on 12/2/14.
 */
public class HWDB {


/**
 * This class manages a connection to the database, providing
 * convenience methods to create/update/delete, and centralizing the
 * constants used in the database.
 *
 * It should be possible to adapt this class for common android/db applications
 * by changing the constants and a few methods.
 *
 * This class is released into the public domain, free for any purpose.
 * Nick Parlante 2011
 *
 */
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "hwdb";
    public static final String DATABASE_TABLE = "hw";

    // Field names -- use the KEY_XXX constants here and in
    // client code, so it's all consistent and checked at compile-time.

    public static final String KEY_ROWID = "_id";  // Android requires exactly this key name
    public static final int INDEX_ROWID = 0;
    public static final String KEY_TITLE = "title";
    public static final int INDEX_TITLE = 1;
    public static final String KEY_BODY = "body";
    public static final int INDEX_BODY = 2;
    public static final String KEY_STATE = "state";
    public static final int INDEX_STATE = 3;

    public static final String[] KEYS_ALL =
            { HWDB.KEY_ROWID, HWDB.KEY_TITLE, HWDB.KEY_BODY, HWDB.KEY_STATE };


    private Context mContext;
    private SQLiteDatabase mDatabase;
    private HWDBHelper mHelper;

    /** Construct DB for this activity context. */
    public HWDB(Context context) {
        mContext = context;
    }

    /** Opens up a connection to the database. Do this before any operations. */
    public void open() throws SQLException {
        mHelper = new HWDBHelper(mContext);
        mDatabase = mHelper.getWritableDatabase();
    }

    /** Closes the database connection. Operations are not valid after this. */
    public void close() {
        mHelper.close();
        mHelper = null;
        mDatabase = null;
    }

    /**
     Creates and inserts a new row using the given values.
     Returns the rowid of the new row, or -1 on error.
     todo: values should not include a rowid I assume.
     */


    public long createRow(ContentValues values) {
        return mDatabase.insert(DATABASE_TABLE, null, values);
    }

    /**
     Updates the given rowid with the given values.
     Returns true if there was a change (i.e. the rowid was valid).
     */
    public boolean updateRow(long rowId, ContentValues values) {
        return mDatabase.update(DATABASE_TABLE, values,
                HWDB.KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     Deletes the given rowid.
     Returns true if any rows were deleted (i.e. the id was valid).
     */
    public boolean deleteRow(long rowId) {
        return mDatabase.delete(DATABASE_TABLE,
                HWDB.KEY_ROWID + "=" + rowId, null) > 0;
    }


    /** Returns a cursor for all the rows. Caller should close or manage the cursor. */
    public Cursor queryAll() {
        return mDatabase.query(DATABASE_TABLE,
                KEYS_ALL,  // i.e. return all 4 columns
                null, null, null, null,
                HWDB.KEY_TITLE + " ASC"  // order-by, "DESC" for descending
        );

        // Could pass for third arg to filter in effect:
        // TodoDatabaseHelper.KEY_STATE + "=0"

        // query() is general purpose, here we show the most common usage.
    }

    /** Returns a cursor for the given row id. Caller should close or manage the cursor. */
    public Cursor query(long rowId) throws SQLException {
        Cursor cursor = mDatabase.query(true, DATABASE_TABLE,
                KEYS_ALL,
                KEY_ROWID + "=" + rowId,  // select the one row we care about
                null, null, null, null, null);

        // cursor starts before first -- move it to the row itself.
        cursor.moveToFirst();
        return cursor;
    }

    /** Creates a ContentValues hash for our data. Pass in to create/update. */
    public ContentValues createContentValues(String title, String body, int state) {
        ContentValues values = new ContentValues();
        values.put(HWDB.KEY_TITLE, title);
        values.put(HWDB.KEY_BODY, body);
        values.put(HWDB.KEY_STATE, state);
        return values;
    }

    // Helper for database open, create, upgrade.
    // Here written as a private inner class to TodoDB.
    private static class HWDBHelper extends SQLiteOpenHelper {
        // SQL text to create table (basically just string or integer)
        private static final String DATABASE_CREATE =
                "create table " + DATABASE_TABLE + " (" +
                        HWDB.KEY_ROWID + " integer primary key autoincrement, " +
                        HWDB.KEY_TITLE + " text not null, " +
                        HWDB.KEY_BODY + " text not null," +
                        HWDB.KEY_STATE + " integer " +
                        ");";

        // SQLITE does not have a complex type system, so although "done" is a boolean
        // to the app, here we store it as an integer with (0 = false)


        public HWDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /** Creates the initial (empty) database. */
        @Override
        public void onCreate(SQLiteDatabase database) {
            database.execSQL(DATABASE_CREATE);
        }


        /** Called at version upgrade time, in case we want to change/migrate
         the database structure. Here we just do nothing. */
        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
            // we do nothing for this case
        }
    }
}
