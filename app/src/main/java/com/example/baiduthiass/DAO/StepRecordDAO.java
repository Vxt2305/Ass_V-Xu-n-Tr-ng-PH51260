package com.example.baiduthiass.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.baiduthiass.Helper.DatabaseHelper2;
import com.example.baiduthiass.Model.StepRecord;

import java.util.ArrayList;
import java.util.List;

public class StepRecordDAO {
    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase database;

    public StepRecordDAO(Context context) {
        dbHelper = new DatabaseHelper2(context);
    }

    private void open() {
        database = dbHelper.getWritableDatabase();
    }

    private void close() {
        dbHelper.close();
    }

    public void addStepRecord(StepRecord stepRecord) {
        open();
        ContentValues values = new ContentValues();
        values.put("username", stepRecord.getUsername());
        values.put("steps", stepRecord.getSteps());
        values.put("distance", stepRecord.getDistance());
        values.put("duration", stepRecord.getDuration());
        values.put("record_date", stepRecord.getRecordDate());

        database.insert("step_records", null, values);
        close();
    }

    public List<StepRecord> getAllStepRecords() {
        List<StepRecord> stepRecords = new ArrayList<>();
        open();

        Cursor cursor = database.query("step_records",
                new String[]{"id", "username", "steps", "distance", "duration", "record_date"},
                null, null, null, null, "record_date DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                StepRecord stepRecord = new StepRecord(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("username")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("steps")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("distance")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("duration")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("record_date"))
                );
                stepRecords.add(stepRecord);
            } while (cursor.moveToNext());
            cursor.close();
        }

        close();
        return stepRecords;
    }

    public StepRecord getStepRecordById(int id) {
        open();
        StepRecord stepRecord = null;

        Cursor cursor = database.query("step_records",
                new String[]{"id", "username", "steps", "distance", "duration", "record_date"},
                "id=?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            stepRecord = new StepRecord(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("username")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("steps")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("distance")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("duration")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("record_date"))
            );
            cursor.close();
        }

        close();
        return stepRecord;
    }

    public void updateStepRecord(StepRecord stepRecord) {
        open();
        ContentValues values = new ContentValues();
        values.put("username", stepRecord.getUsername());
        values.put("steps", stepRecord.getSteps());
        values.put("distance", stepRecord.getDistance());
        values.put("duration", stepRecord.getDuration());
        values.put("record_date", stepRecord.getRecordDate());

        database.update("step_records", values, "id=?", new String[]{String.valueOf(stepRecord.getId())});
        close();
    }

    public void deleteStepRecord(int id) {
        open();
        database.delete("step_records", "id=?", new String[]{String.valueOf(id)});
        close();
    }
}
