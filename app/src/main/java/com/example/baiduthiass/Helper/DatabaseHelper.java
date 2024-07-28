package com.example.baiduthiass.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "assAndroid2.db";
    public static final String TABLE_USERS = "users";
    public static final String TABLE_USER_DETAILS = "user_details";
    public static final String TABLE_BMI = "bmi_records";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createUsersTable(db);
        createUserDetailsTable(db);
        createBMIRecordsTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTables(db);
        onCreate(db);
    }

    private void createUsersTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE, " +
                "password TEXT, " +
                "email TEXT)");
    }

    private void createUserDetailsTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USER_DETAILS + " (" +
                "username TEXT PRIMARY KEY, " +
                "full_name TEXT NOT NULL, " +
                "birth_date TEXT NOT NULL, " +
                "height REAL NOT NULL, " +
                "weight REAL NOT NULL, " +
                "bmi REAL, " +
                "FOREIGN KEY(username) REFERENCES " + TABLE_USERS + "(username))");
    }

    private void createBMIRecordsTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_BMI + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL, " +
                "bmi REAL NOT NULL, " +
                "record_date TEXT NOT NULL, " +
                "FOREIGN KEY(username) REFERENCES " + TABLE_USERS + "(username))");
    }

    private void dropTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BMI);
    }

    // User related methods
    public Boolean insertUser(String username, String password, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("email", email);
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public String getUsername(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT username FROM " + TABLE_USERS + " WHERE email = ?", new String[]{email});
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow("username"));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return null;
    }

    public String getEmail(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT email FROM " + TABLE_USERS + " WHERE username = ?", new String[]{username});
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow("email"));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return null;
    }

    public String getPassword(String usernameOrEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT password FROM " + TABLE_USERS + " WHERE username = ? OR email = ?", new String[]{usernameOrEmail, usernameOrEmail});
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow("password"));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return null;
    }

    public String getUsernameFromEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT username FROM " + TABLE_USERS + " WHERE email = ?", new String[]{email});
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow("username"));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return null;
    }

    public boolean isEmail(String usernameOrEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT 1 FROM " + TABLE_USERS + " WHERE email = ?", new String[]{usernameOrEmail});
            return cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    public boolean checkUserName(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE username = ?", new String[]{username});
            return cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    public boolean checkEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE email = ?", new String[]{email});
            return cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    public boolean checkPassword(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM users WHERE username = ? AND password = ?", new String[]{username, password});
            return cursor.getCount() > 0;
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }


    public Boolean checkUsernameOrEmailAndPassword(String usernameOrEmail, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "SELECT * FROM " + TABLE_USERS + " WHERE (username = ? OR email = ?) AND password = ?",
                    new String[]{usernameOrEmail, usernameOrEmail, password}
            );
            return cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    public boolean updatePassword(String username, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        int result = db.update(TABLE_USERS, values, "username = ?", new String[]{username});
        db.close();
        return result > 0;
    }

    public Boolean insertUserDetails(String username, String fullName, String birthDate, float height, float weight, Float bmi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("full_name", fullName);
        values.put("birth_date", birthDate);
        values.put("height", height);
        values.put("weight", weight);
        values.put("bmi", bmi);
        long result = db.insert(TABLE_USER_DETAILS, null, values);
        db.close();
        return result != -1;
    }

    public boolean updateUserDetails(String username, String fullName, String birthDate, float height, float weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("full_name", fullName);
        values.put("birth_date", birthDate);
        values.put("height", height);
        values.put("weight", weight);
        int result = db.update(TABLE_USER_DETAILS, values, "username = ?", new String[]{username});
        db.close();
        return result > 0;
    }

    public Cursor getUserDetails(String usernameOrEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT ud.* FROM " + TABLE_USER_DETAILS + " ud " +
                "JOIN " + TABLE_USERS + " u ON ud.username = u.username " +
                "WHERE u.username = ? OR u.email = ?";
        return db.rawQuery(query, new String[]{usernameOrEmail, usernameOrEmail});
    }

    public Cursor getUserDetailsFood(String usernameOrEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT ud.* FROM " + TABLE_USER_DETAILS + " ud " +
                "JOIN " + TABLE_USERS + " u ON ud.username = u.username " +
                "WHERE u.username = ? OR u.email = ?";
        return db.rawQuery(query, new String[]{usernameOrEmail, usernameOrEmail});
    }


    public boolean checkUserDetail(String usernameOrEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT full_name, birth_date, height, weight FROM " + TABLE_USER_DETAILS +
                            " WHERE username = (SELECT username FROM " + TABLE_USERS + " WHERE username = ? OR email = ?)",
                    new String[]{usernameOrEmail, usernameOrEmail});
            if (cursor != null && cursor.moveToFirst()) {
                String fullName = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
                String birthDate = cursor.getString(cursor.getColumnIndexOrThrow("birth_date"));
                float height = cursor.getFloat(cursor.getColumnIndexOrThrow("height"));
                float weight = cursor.getFloat(cursor.getColumnIndexOrThrow("weight"));
                return !TextUtils.isEmpty(fullName) && !TextUtils.isEmpty(birthDate) && height > 0 && weight > 0;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return false;
    }

    public boolean userHasDetails(String usernameOrEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_USER_DETAILS +
                            " WHERE username = (SELECT username FROM " + TABLE_USERS +
                            " WHERE username = ? OR email = ?)",
                    new String[]{usernameOrEmail, usernameOrEmail});
            return cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    // BMI records related methods
    public void insertBMIRecord(String username, float bmi, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("bmi", bmi);
        values.put("record_date", date);
        db.insert(TABLE_BMI, null, values);
        db.close(); // Đảm bảo đóng cơ sở dữ liệu sau khi sử dụng
    }


    public Cursor getBMIRecords(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_BMI + " WHERE username = ? ORDER BY record_date DESC", new String[]{username});
    }
}
