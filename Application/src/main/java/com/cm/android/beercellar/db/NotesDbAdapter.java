package com.cm.android.beercellar.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//import com.vvw.config.AppConfig;
//import com.vvw.util.Util;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 * <p/>
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class NotesDbAdapter {
    static String TAG = "Wine Cellar::NotesDbAdapter";

    public static final String KEY_ROWID = "_id";
    public static final String KEY_BEER = "beer";
    public static final String KEY_RATING = "rating";
    public static final String KEY_TEXT_EXTRACT = "textextract";
    public static final String KEY_NOTES = "notes";
    public static final String KEY_PICTURE = "picture";
    public static final String KEY_SHARE = "share";
    public static final String KEY_URI = "uri";
    public static final String KEY_CREATED = "created";
    public static final String KEY_UPDATED = "updated";


    public static final String DATABASE_NAME = "beer_cellar_db.sqlite";
    public static final int DATABASE_VERSION = 1; //10 FIELDS
    public static final String DATABASE_TABLE = "wine_list_" + DATABASE_VERSION;

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;


    /**
     * Database creation sql statement
     */
    //8 FIELDS
    private static final String DATABASE_CREATE = "create table if not exists "
            + DATABASE_TABLE + " (" + KEY_ROWID + "  integer primary key,"
            + KEY_BEER + " text default null,"
            + KEY_RATING + " text default null,"
            + KEY_TEXT_EXTRACT + " text default null,"
            + KEY_NOTES + " text default null,"
            + KEY_PICTURE + " text default null,"
            + KEY_SHARE + " text default null,"
            + KEY_URI + " text default null,"
            + KEY_CREATED + " text default null,"
            + KEY_UPDATED + " text default null);";


    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

            Log.i(TAG, "DatabaseHelper");

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG, "DatabaseHelper::onCreate");
            db.execSQL(DATABASE_CREATE);
            Log.i(TAG, "DatabaseHelper::onCreate: Database " + DATABASE_TABLE
                    + " created");
            Log.i(TAG, "DatabaseHelper::onCreate: Seed Data Inserted");
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(TAG, "DatabaseHelper::onUpgrade");
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion);
            db.execSQL(DATABASE_CREATE);
            Log.i(TAG, "DatabaseHelper::onUpgrade: Database " + DATABASE_TABLE
                    + " created");
            if (oldVersion == 1) {
                //Util.onUpgradeToV7FromV6(db);
            }
            Log.i(TAG, "DatabaseHelper::onUpgrade: Data imported");
        }

    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public NotesDbAdapter(Context ctx) {
        Log.i(TAG, "NotesDbAdapter");
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     * initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public NotesDbAdapter open() throws SQLException {

        Log.i(TAG, "open");

        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    /**
     * @return SQLiteDatabase
     */
    public SQLiteDatabase getDatabase() {
        this.open();
        return this.mDb;
    }

    public void close() {

        Log.i(TAG, "close");

        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     *
     * @return rowId or -1 if failed
     */
    public long createNote(Note note) {

        Log.i(TAG, "createNote");
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ROWID, note.id);
        initialValues.put(KEY_BEER, note.beer);
        initialValues.put(KEY_RATING, note.rating.trim());
        initialValues.put(KEY_TEXT_EXTRACT, note.textExtract.trim());
        initialValues.put(KEY_NOTES, note.notes.trim());
        initialValues.put(KEY_PICTURE, note.picture.trim());
        initialValues.put(KEY_SHARE, note.share.trim());
        initialValues.put(KEY_URI, note.uri.trim());
        initialValues.put(KEY_CREATED,
                String.valueOf(System.currentTimeMillis()));
        initialValues.put(KEY_UPDATED,
                String.valueOf(System.currentTimeMillis()));

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     *
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteNote(long rowId) {

        Log.i(TAG, "deleteNote");

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }


    /**
     * Return a Cursor positioned at the note that matches the given rowId
     *
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Note fetchNote(long rowId) throws SQLException {

        Log.i(TAG, "fetchNote:id=" + rowId);
        Cursor cursor = null;
        Note note = null;
        try {
            cursor =

                    mDb.query(false, DATABASE_TABLE, new String[]
                                    {KEY_ROWID, KEY_BEER, KEY_RATING, KEY_TEXT_EXTRACT, KEY_NOTES, KEY_PICTURE, KEY_SHARE, KEY_URI, KEY_CREATED, KEY_UPDATED},
                            KEY_ROWID + "=" + rowId, null, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
            while (!cursor.isAfterLast()) {
                note = cursorToNote(cursor);
                cursor.moveToNext();
            }
            return note;
        } finally {
            if (cursor != null)
                // make sure to close the cursor
                cursor.close();
        }

    }


    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     *
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateNote(Note note) {

        Log.i(TAG, "updateNote");

        ContentValues args = new ContentValues();

        if ((note.rating != null) && ((!note.rating.equals("0.0")))) {
            args.put(KEY_RATING, note.rating.trim());
        }
        if ((note.beer != null) && ((!note.beer.equals("")))) {
            args.put(KEY_BEER, note.beer.trim());
        }
        if ((note.textExtract != null) && ((!note.textExtract.equals("")))) {
            args.put(KEY_TEXT_EXTRACT, note.textExtract.trim());
        }
        if ((note.notes != null) && ((!note.notes.equals("")))) {
            args.put(KEY_NOTES, note.notes.trim());
        }
        if ((note.picture != null) && ((!note.picture.equals("")))) {
            args.put(KEY_PICTURE, note.picture.trim());
        }
        if ((note.share != null) && ((!note.share.equals("")))) {
            args.put(KEY_SHARE, note.share.trim());
        }

        if ((note.uri != null) && ((!note.uri.equals("")))) {
            args.put(KEY_URI, note.uri.trim());
        }

        args.put(KEY_UPDATED, String.valueOf(System.currentTimeMillis()));
        return mDb
                .update(DATABASE_TABLE, args, KEY_ROWID + "=" + note.id, null) > 0;
    }


    private Note cursorToNote(Cursor cursor) {

        Note note = new Note();
        note.id = cursor.getLong(cursor
                .getColumnIndexOrThrow(NotesDbAdapter.KEY_ROWID));
        note.beer = cursor.getString(cursor
                .getColumnIndexOrThrow(NotesDbAdapter.KEY_BEER));
        note.rating = cursor.getString(cursor
                .getColumnIndexOrThrow(NotesDbAdapter.KEY_RATING));
        note.textExtract = cursor.getString(cursor
                .getColumnIndexOrThrow(NotesDbAdapter.KEY_TEXT_EXTRACT));
        note.notes = cursor.getString(cursor
                .getColumnIndexOrThrow(NotesDbAdapter.KEY_NOTES));
        note.share = cursor.getString(cursor
                .getColumnIndexOrThrow(NotesDbAdapter.KEY_SHARE));
        note.picture = cursor.getString(cursor
                .getColumnIndexOrThrow(NotesDbAdapter.KEY_PICTURE));
        note.uri = cursor.getString(cursor
                .getColumnIndex(NotesDbAdapter.KEY_URI));
        note.created = cursor.getLong(cursor
                .getColumnIndex(NotesDbAdapter.KEY_CREATED));
        note.updated = cursor.getLong(cursor
                .getColumnIndex(NotesDbAdapter.KEY_UPDATED));

        return note;

    }

}
