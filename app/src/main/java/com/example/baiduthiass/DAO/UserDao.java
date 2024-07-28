package com.example.baiduthiass.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.baiduthiass.Helper.DatabaseHelper2;
import com.example.baiduthiass.Model.User;

public class UserDao {
    private SQLiteDatabase db;

    public UserDao(SQLiteDatabase db) {
        this.db = db;
    }

    // Chèn người dùng mới
    public boolean insertUser(User user) {
        ContentValues values = new ContentValues();
        values.put("username", user.getUsername());
        values.put("password", user.getPassword());
        values.put("email", user.getEmail());
        long result = db.insert("users", null, values);
        return result != -1;
    }

    // Lấy tên người dùng theo email
    public String getUsername(String email) {
        Cursor cursor = db.rawQuery("SELECT username FROM users WHERE email = ?", new String[]{email});
        if (cursor != null && cursor.moveToFirst()) {
            String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            cursor.close();
            return username;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    // Lấy email theo tên người dùng
    public String getEmail(String username) {
        Cursor cursor = db.rawQuery("SELECT email FROM users WHERE username = ?", new String[]{username});
        if (cursor != null && cursor.moveToFirst()) {
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            cursor.close();
            return email;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    // Lấy mật khẩu theo tên người dùng hoặc email
    public String getPassword(String usernameOrEmail) {
        Cursor cursor = db.rawQuery("SELECT password FROM users WHERE username = ? OR email = ?", new String[]{usernameOrEmail, usernameOrEmail});
        if (cursor != null && cursor.moveToFirst()) {
            String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            cursor.close();
            return password;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    // Kiểm tra xem có phải email không
    public boolean isEmail(String usernameOrEmail) {
        Cursor cursor = db.rawQuery("SELECT 1 FROM users WHERE email = ?", new String[]{usernameOrEmail});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Kiểm tra xem tên người dùng đã tồn tại chưa
    public boolean checkUserName(String username) {
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Kiểm tra xem email đã tồn tại chưa
    public boolean checkEmail(String email) {
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Kiểm tra mật khẩu theo tên người dùng
    public boolean checkPassword(String username, String password) {
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ? AND password = ?", new String[]{username, password});
        boolean valid = cursor.getCount() > 0;
        cursor.close();
        return valid;
    }

    // Xác thực người dùng bằng tên người dùng hoặc email và mật khẩu
    public boolean checkUsernameOrEmailAndPassword(String usernameOrEmail, String password) {
        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE (username = ? OR email = ?) AND password = ?",
                new String[]{usernameOrEmail, usernameOrEmail, password}
        );
        boolean valid = cursor.getCount() > 0;
        cursor.close();
        return valid;
    }

    // Cập nhật mật khẩu
    public boolean updatePassword(String username, String newPassword) {
        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        int result = db.update("users", values, "username = ?", new String[]{username});
        return result > 0;
    }
}
