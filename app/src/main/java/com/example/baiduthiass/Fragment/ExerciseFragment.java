package com.example.baiduthiass.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.baiduthiass.DAO.StepRecordDAO;
import com.example.baiduthiass.Model.StepRecord;
import com.example.baiduthiass.R;

public class ExerciseFragment extends Fragment implements SensorEventListener {

    private String username; // Thêm biến username
    private TextView textViewSoBuocChan, textViewSoGio, textViewSoKm;
    private Button btnStop, btnStart, btnRestart, btnFakeStep;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private boolean isSensorPresent = false;
    private long steps = 0;
    private long startTime = 0;
    private boolean isRunning = false;
    private boolean isFakeRunning = false;
    private Handler handler = new Handler();
    private Handler fakeStepHandler = new Handler();
    private Runnable fakeStepRunnable;

    private StepRecordDAO stepRecordDAO;

    // Thêm phương thức setter cho username
    public void setUsername(String username) {
        this.username = username;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_exercise, container, false);

        textViewSoBuocChan = view.findViewById(R.id.textView_soBuocChan);
        textViewSoGio = view.findViewById(R.id.textView_soGio);
        textViewSoKm = view.findViewById(R.id.textView_soKm);
        btnStop = view.findViewById(R.id.btn_stop);
        btnStart = view.findViewById(R.id.btn_start);
        btnRestart = view.findViewById(R.id.btn_restart);
        btnFakeStep = view.findViewById(R.id.btn_fake_step);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null && sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            isSensorPresent = true;
        }

        stepRecordDAO = new StepRecordDAO(getActivity());

        btnStart.setOnClickListener(v -> startExercise());
        btnStop.setOnClickListener(v -> stopExercise());
        btnRestart.setOnClickListener(v -> restartExercise());
        btnFakeStep.setOnClickListener(v -> startFakeSteps());

        fakeStepRunnable = new Runnable() {
            @Override
            public void run() {
                if (isFakeRunning) {
                    fakeStep();
                    fakeStepHandler.postDelayed(this, 1000);
                }
            }
        };

        return view;
    }

    private void startExercise() {
        if (!isRunning) {
            steps = 0;
            startTime = SystemClock.elapsedRealtime();
            if (isSensorPresent && sensorManager != null) {
                sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
            }
            handler.post(updateTimeRunnable);
            isRunning = true;
        }
    }

    private void startFakeSteps() {
        if (!isFakeRunning) {
            steps = 0;
            startTime = SystemClock.elapsedRealtime();
            fakeStepHandler.post(fakeStepRunnable);
            handler.post(updateTimeRunnable);
            isFakeRunning = true;
        }
    }

    private void stopExercise() {
        if (isRunning) {
            if (isSensorPresent && sensorManager != null) {
                sensorManager.unregisterListener(this);
            }
            handler.removeCallbacks(updateTimeRunnable);
            saveStepsToDatabase(); // Sử dụng username ở đây
            isRunning = false;
        }
        if (isFakeRunning) {
            fakeStepHandler.removeCallbacks(fakeStepRunnable);
            isFakeRunning = false;
        }
    }

    private void restartExercise() {
        stopExercise();
        textViewSoBuocChan.setText("0");
        textViewSoGio.setText("00:00");
        textViewSoKm.setText("0.00 km");
        steps = 0;
    }

    private void fakeStep() {
        if (isFakeRunning) {
            steps++;
            textViewSoBuocChan.setText(String.valueOf(steps));
            textViewSoKm.setText(String.format("%.2f km", steps * 0.0008));
            checkDistanceMilestones(steps * 0.0008);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isRunning) {
            steps++;
            textViewSoBuocChan.setText(String.valueOf(steps));
            textViewSoKm.setText(String.format("%.2f km", steps * 0.0008));
            checkDistanceMilestones(steps * 0.0008);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void checkDistanceMilestones(double distance) {
        if (distance == 1.0 || distance == 5.0 || distance == 10.0 || distance == 20.0 || distance == 30.0 || distance == 40.0 || distance == 50.0) {
            String message = "";
            if (distance == 50.0) {
                message = "Bạn đã hoàn thành 50 km! Bạn nên dừng lại và nghỉ ngơi.";
            } else {
                message = String.format("Chúc mừng! Bạn đã hoàn thành %.0f km! Tiếp tục cố gắng nhé!", distance);
            }
            showDialog(message);
        }
    }

    private void showDialog(String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Thông báo")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void saveStepsToDatabase() {
        long elapsedTime = SystemClock.elapsedRealtime() - startTime;
        double distance = steps * 0.0008; // Convert steps to kilometers

        if (username == null) {
            username = "unknown"; // Hoặc xử lý trường hợp username không có giá trị
        }

        StepRecord stepRecord = new StepRecord(0, username, steps, distance, elapsedTime / 1000, System.currentTimeMillis());
        stepRecordDAO.addStepRecord(stepRecord);
    }

    private final Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            long elapsedTime = (SystemClock.elapsedRealtime() - startTime) / 1000;
            long hours = elapsedTime / 3600;
            long minutes = (elapsedTime % 3600) / 60;
            long seconds = elapsedTime % 60;

            textViewSoGio.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if (isRunning && isSensorPresent && sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        handler.removeCallbacks(updateTimeRunnable);
        if (isFakeRunning) {
            fakeStepHandler.removeCallbacks(fakeStepRunnable);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isRunning && isSensorPresent && sensorManager != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
            handler.post(updateTimeRunnable);
        }
        if (isFakeRunning) {
            fakeStepHandler.post(fakeStepRunnable);
        }
    }
}
