package com.example.baiduthiass.DAO;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.example.baiduthiass.Model.UserDetails;

public class UserDetailsDao {
    private SQLiteDatabase db;

    public UserDetailsDao(SQLiteDatabase db) {
        this.db = db;
    }

    public boolean insertUserDetails(UserDetails userDetails) {
        ContentValues values = new ContentValues();
        values.put("username", userDetails.getUsername());
        values.put("full_name", userDetails.getFullName());
        values.put("birth_date", userDetails.getBirthDate());
        values.put("height", userDetails.getHeight());
        values.put("weight", userDetails.getWeight());
        values.put("bmi", userDetails.getBmi());
        long result = db.insert("user_details", null, values);
        return result != -1;
    }

    public boolean updateUserDetails(UserDetails userDetails) {
        ContentValues values = new ContentValues();
        values.put("full_name", userDetails.getFullName());
        values.put("birth_date", userDetails.getBirthDate());
        values.put("height", userDetails.getHeight());
        values.put("weight", userDetails.getWeight());
        values.put("bmi", userDetails.getBmi());
        int result = db.update("user_details", values, "username = ?", new String[]{userDetails.getUsername()});
        return result > 0;
    }

    public UserDetails getUserDetails(String usernameOrEmail) {
        Cursor cursor = db.rawQuery("SELECT ud.* FROM user_details ud JOIN users u ON ud.username = u.username WHERE u.username = ? OR u.email = ?", new String[]{usernameOrEmail, usernameOrEmail});
        if (cursor != null && cursor.moveToFirst()) {
            String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            String fullName = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
            String birthDate = cursor.getString(cursor.getColumnIndexOrThrow("birth_date"));
            float height = cursor.getFloat(cursor.getColumnIndexOrThrow("height"));
            float weight = cursor.getFloat(cursor.getColumnIndexOrThrow("weight"));
            Float bmi = cursor.isNull(cursor.getColumnIndexOrThrow("bmi")) ? null : cursor.getFloat(cursor.getColumnIndexOrThrow("bmi"));
            cursor.close();
            return new UserDetails(username, fullName, birthDate, height, weight, bmi);
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public Cursor getUserDetailsFood(String username) {
        return db.rawQuery(
                "SELECT full_name, height, weight FROM user_details WHERE username = ?",
                new String[]{username}
        );
    }

    public boolean checkUserDetail(String usernameOrEmail) {
        Cursor cursor = db.rawQuery("SELECT full_name, birth_date, height, weight FROM user_details WHERE username = (SELECT username FROM users WHERE username = ? OR email = ?)", new String[]{usernameOrEmail, usernameOrEmail});
        boolean complete = false;
        if (cursor != null && cursor.moveToFirst()) {
            String fullName = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
            String birthDate = cursor.getString(cursor.getColumnIndexOrThrow("birth_date"));
            float height = cursor.getFloat(cursor.getColumnIndexOrThrow("height"));
            float weight = cursor.getFloat(cursor.getColumnIndexOrThrow("weight"));
            complete = !TextUtils.isEmpty(fullName) && !TextUtils.isEmpty(birthDate) && height > 0 && weight > 0;
            cursor.close();
        }
        return complete;
    }

    public boolean userHasDetails(String usernameOrEmail) {
        Cursor cursor = db.rawQuery("SELECT * FROM user_details WHERE username = (SELECT username FROM users WHERE username = ? OR email = ?)", new String[]{usernameOrEmail, usernameOrEmail});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean isUserDetailComplete(String username) {
        return userHasDetails(username) && checkUserDetail(username);
    }

    public String getFullName(String usernameOrEmail) {
        String fullName = null;
        Cursor cursor = db.rawQuery(
                "SELECT ud.full_name FROM user_details ud " +
                        "JOIN users u ON ud.username = u.username " +
                        "WHERE u.username = ? OR u.email = ?",
                new String[]{usernameOrEmail, usernameOrEmail}
        );
        if (cursor != null && cursor.moveToFirst()) {
            fullName = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
            cursor.close();
        }
        if (cursor != null) {
            cursor.close();
        }
        return fullName;
    }
}
