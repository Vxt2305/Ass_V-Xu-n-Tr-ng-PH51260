package com.example.baiduthiass.Helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper2 extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "AssVXT.db";

    public DatabaseHelper2(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createUsersTable(db);
        createUserDetailsTable(db);
        createBMIRecordsTable(db);
        createStepRecordsTable(db);
        createWritePostsTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTables(db);
        onCreate(db);
    }

    private void createUsersTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE, " +
                "password TEXT, " +
                "email TEXT)");
    }

    private void createUserDetailsTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE user_details (" +
                "username TEXT PRIMARY KEY, " +
                "full_name TEXT NOT NULL, " +
                "birth_date TEXT NOT NULL, " +
                "height REAL NOT NULL, " +
                "weight REAL NOT NULL, " +
                "bmi REAL, " +
                "FOREIGN KEY(username) REFERENCES users(username))");
    }

    private void createBMIRecordsTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE bmi_records (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL, " +
                "bmi REAL NOT NULL, " +
                "record_date TEXT NOT NULL, " +
                "FOREIGN KEY(username) REFERENCES users(username))");
    }

    private void createStepRecordsTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE step_records (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL, " +
                "steps LONG NOT NULL, " +
                "distance REAL NOT NULL, " +
                "duration LONG NOT NULL, " +
                "record_date LONG NOT NULL, " +
                "FOREIGN KEY(username) REFERENCES users(username))");
    }

    private void createWritePostsTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE write_posts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL, " +
                "posts TEXT NOT NULL, " +
                "record_date TEXT NOT NULL, " +
                "FOREIGN KEY(username) REFERENCES users(username))");
    }

    private void dropTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS user_details");
        db.execSQL("DROP TABLE IF EXISTS bmi_records");
        db.execSQL("DROP TABLE IF EXISTS step_records");
        db.execSQL("DROP TABLE IF EXISTS write_posts");
    }
}
