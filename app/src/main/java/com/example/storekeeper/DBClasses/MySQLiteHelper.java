package com.example.storekeeper.DBClasses;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "my_database.db";
    private static final int DATABASE_VERSION = 1;

    private Context mContext;

    public MySQLiteHelper(SyncService context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Δημιουργία του πίνακα users στη βάση δεδομένων SQLite
        //db.execSQL(User.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Αναβάθμιση της βάσης δεδομένων SQLite σε περίπτωση που αλλάξει η έκδοση της βάσης
        //db.execSQL("DROP TABLE IF EXISTS " + User.TABLE_NAME);
        //onCreate(db);
    }

    public boolean hasChanges() {
        // Έλεγχος για αλλαγές στη βάση δεδομένων SQLite
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + User.TABLE_NAME
//                + " WHERE " + User.COLUMN_IS_SYNCED + " = 0", null);
//        cursor.moveToFirst();
//        int count = cursor.getInt(0);
//        cursor.close();
//        return count > 0;
        return true;
    }

    public void syncWithMySQL(MySQLHelper mySQLHelper) {
        // Συγχρονισμός της βάσης δεδομένων SQLite με τη βάση δεδομένων MySQL
//        SQLiteDatabase db = getWritableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM " + User.TABLE_NAME
//                + " WHERE " + User.COLUMN_IS_SYNCED + " = 0", null);
//        while (cursor.moveToNext()) {
//            User user = new User(
//                    cursor.getInt(cursor.getColumnIndex(User.COLUMN_ID)),
//                    cursor.getString(cursor.getColumnIndex(User.COLUMN_NAME)),
//                    cursor.getString(cursor.getColumnIndex(User.COLUMN_EMAIL)),
//                    cursor.getInt(cursor.getColumnIndex(User.COLUMN_IS_SYNCED))
//            );
//            boolean success = mySQLHelper.syncUser(user);
//            if (success) {
//                // Σημειώνουμε την εγγραφή ως συγχρονισμένη
//                ContentValues values = new ContentValues();
//                values.put(User.COLUMN_IS_SYNCED, 1);
//                db.update(User.TABLE_NAME, values, User.COLUMN_ID + " = ?",
//                        new String[]{String.valueOf(user.getId())});
//            }
//        }
//        cursor.close();
    }
}

