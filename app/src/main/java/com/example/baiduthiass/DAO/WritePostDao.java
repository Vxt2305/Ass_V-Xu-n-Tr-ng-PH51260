package com.example.baiduthiass.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.baiduthiass.Helper.DatabaseHelper2;
import com.example.baiduthiass.Model.WritePost;

import java.util.ArrayList;
import java.util.List;

public class WritePostDao {
    private SQLiteDatabase db;
    private DatabaseHelper2 dbHelper;
    private String[] allColumns = {"id", "username", "posts", "record_date"};

    public WritePostDao(Context context) {
        dbHelper = new DatabaseHelper2(context);
    }
    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public WritePost createWritePost(String username, String posts, String recordDate) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("posts", posts);
        values.put("record_date", recordDate);

        long insertId = db.insert("write_posts", null, values);

        Cursor cursor = db.query("write_posts", allColumns, "id = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        WritePost newWritePost = cursorToWritePost(cursor);
        cursor.close();
        return newWritePost;
    }

    public List<WritePost> getAllWritePosts() {
        List<WritePost> writePosts = new ArrayList<>();

        Cursor cursor = db.query("write_posts", allColumns, null, null, null, null, "record_date DESC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            WritePost writePost = cursorToWritePost(cursor);
            writePosts.add(writePost);
            cursor.moveToNext();
        }
        cursor.close();
        return writePosts;
    }

    public WritePost getWritePostById(int id) {
        Cursor cursor = db.query("write_posts", allColumns, "id = " + id, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        WritePost writePost = cursorToWritePost(cursor);
        cursor.close();
        return writePost;
    }

    public void deleteWritePost(WritePost writePost) {
        int id = writePost.getId();
        db.delete("write_posts", "id = " + id, null);
    }

    private WritePost cursorToWritePost(Cursor cursor) {
        WritePost writePost = new WritePost();
        writePost.setId(cursor.getInt(0));
        writePost.setUsername(cursor.getString(1));
        writePost.setPosts(cursor.getString(2));
        writePost.setRecordDate(cursor.getString(3));
        return writePost;
    }

    public List<WritePost> getWritePostsByUsername(String username) {
        List<WritePost> writePosts = new ArrayList<>();

        String selection = "username = ?";
        String[] selectionArgs = { username };
        Cursor cursor = db.query("write_posts", allColumns, selection, selectionArgs, null, null, "record_date DESC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            WritePost writePost = cursorToWritePost(cursor);
            writePosts.add(writePost);
            cursor.moveToNext();
        }
        cursor.close();
        return writePosts;
    }

}
