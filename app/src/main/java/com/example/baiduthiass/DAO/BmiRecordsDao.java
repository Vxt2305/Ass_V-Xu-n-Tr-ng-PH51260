package com.example.baiduthiass.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.baiduthiass.Helper.DatabaseHelper2;
import com.example.baiduthiass.Model.BmiRecord;

public class BmiRecordsDao {
    private SQLiteDatabase db;

    public BmiRecordsDao(SQLiteDatabase db) {
        this.db = db;
    }

    public void insertBMIRecord(BmiRecord bmiRecord) {
        ContentValues values = new ContentValues();
        values.put("username", bmiRecord.getUsername());
        values.put("bmi", bmiRecord.getBmi());
        values.put("record_date", bmiRecord.getRecordDate());
        db.insert("bmi_records", null, values);
    }

    public String getLatestBMIRecord(String username) {
        Cursor cursor = db.rawQuery("SELECT bmi FROM bmi_records WHERE username = ? ORDER BY record_date DESC LIMIT 1", new String[]{username});
        if (cursor != null && cursor.moveToFirst()) {
            String bmi = cursor.getString(cursor.getColumnIndexOrThrow("bmi"));
            cursor.close();
            return bmi;
        }
        return null;
    }
}
