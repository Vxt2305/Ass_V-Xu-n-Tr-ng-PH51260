package com.example.baiduthiass.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.baiduthiass.DAO.UserDao;
import com.example.baiduthiass.DAO.UserDetailsDao;
import com.example.baiduthiass.Helper.DatabaseHelper2;
import com.example.baiduthiass.Model.UserDetails;
import com.example.baiduthiass.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class UserDetailFragment extends Fragment {

    private UserDetailsDao userDetailsDao;
    private String username;
    private UserDao userDao;

    public UserDetailFragment() {
        super(R.layout.user_detail);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_information, container, false);

        ImageView imgProfile = view.findViewById(R.id.img_username3);
        TextView tvFullName = view.findViewById(R.id.textView_full_name);
        TextView tvBirthday = view.findViewById(R.id.textView_birthday);
        TextView tvHeight = view.findViewById(R.id.textView_height);
        TextView tvWeight = view.findViewById(R.id.textView_weight);
        TextView tvEmail = view.findViewById(R.id.textView_email);
        Button btnEditProfile = view.findViewById(R.id.button_suaHoSo);

        DatabaseHelper2 dbHelper = new DatabaseHelper2(getActivity());
        userDetailsDao = new UserDetailsDao(dbHelper.getWritableDatabase());
        userDao = new UserDao(dbHelper.getWritableDatabase());
        loadUserDetails(tvFullName, tvBirthday, tvHeight, tvWeight, tvEmail);

        btnEditProfile.setOnClickListener(v -> showEditDialog(tvFullName, tvBirthday, tvHeight, tvWeight));

        return view;
    }

    private void loadUserDetails(TextView tvFullName, TextView tvBirthday, TextView tvHeight, TextView tvWeight, TextView tvEmail) {
        UserDetails userDetails = userDetailsDao.getUserDetails(username);
        if (userDetails != null) {
            tvFullName.setText(userDetails.getFullName());
            tvBirthday.setText(userDetails.getBirthDate());
            tvHeight.setText(formatHeight(userDetails.getHeight()));
            tvWeight.setText(formatWeight(userDetails.getWeight()));
        }
        String email = userDao.getEmail(username);
        tvEmail.setText(email);
    }

    private String formatHeight(float height) {
        return String.format(Locale.getDefault(), "%.1f cm", height);
    }

    private String formatWeight(float weight) {
        return String.format(Locale.getDefault(), "%.1f kg", weight);
    }

    private String extractValue(String valueWithUnit, String unit) {
        return valueWithUnit.replace(unit, "").trim();
    }

    private void showEditDialog(TextView tvFullName, TextView tvBirthday, TextView tvHeight, TextView tvWeight) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.user_detail, null);
        builder.setView(dialogView);

        EditText etFullName = dialogView.findViewById(R.id.editText_full_name);
        EditText etBirthDate = dialogView.findViewById(R.id.editText_birth_date);
        EditText etHeight = dialogView.findViewById(R.id.editText_height);
        EditText etWeight = dialogView.findViewById(R.id.editText_weight);
        Button btnSave = dialogView.findViewById(R.id.button_save);
        Button btnOutSave = dialogView.findViewById(R.id.button_outSave);

        etFullName.setText(tvFullName.getText());
        etBirthDate.setText(tvBirthday.getText());
        etHeight.setText(extractValue(tvHeight.getText().toString(), " cm"));
        etWeight.setText(extractValue(tvWeight.getText().toString(), " kg"));

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String birthDate = etBirthDate.getText().toString().trim();
            String height = etHeight.getText().toString().trim();
            String weight = etWeight.getText().toString().trim();

            if (validateInputs(etFullName, etBirthDate, etHeight, etWeight)) {
                tvFullName.setText(fullName);
                tvBirthday.setText(birthDate);
                tvHeight.setText(formatHeight(Float.parseFloat(height)));
                tvWeight.setText(formatWeight(Float.parseFloat(weight)));

                UserDetails userDetails = new UserDetails(username, fullName, birthDate, Float.parseFloat(height), Float.parseFloat(weight), null);

                if (userDetailsDao.userHasDetails(username)) {
                    userDetailsDao.updateUserDetails(userDetails);
                } else {
                    userDetailsDao.insertUserDetails(userDetails);
                }

                dialog.dismiss();
            }
        });

        btnOutSave.setOnClickListener(v -> dialog.dismiss());
    }

    private boolean validateInputs(EditText etFullName, EditText etBirthDate, EditText etHeight, EditText etWeight) {
        boolean isValid = true;

        if (TextUtils.isEmpty(etFullName.getText().toString().trim())) {
            etFullName.setError("Vui lòng nhập họ tên");
            isValid = false;
        }

        if (TextUtils.isEmpty(etBirthDate.getText().toString().trim())) {
            etBirthDate.setError("Vui lòng nhập ngày sinh");
            isValid = false;
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                dateFormat.parse(etBirthDate.getText().toString().trim());
            } catch (ParseException e) {
                etBirthDate.setError("Ngày sinh không hợp lệ, vui lòng nhập lại (yyyy-MM-dd)");
                isValid = false;
            }
        }

        if (TextUtils.isEmpty(etHeight.getText().toString().trim())) {
            etHeight.setError("Vui lòng nhập chiều cao");
            isValid = false;
        } else {
            try {
                Float.parseFloat(etHeight.getText().toString().trim());
            } catch (NumberFormatException e) {
                etHeight.setError("Chiều cao không hợp lệ, vui lòng nhập lại");
                isValid = false;
            }
        }

        if (TextUtils.isEmpty(etWeight.getText().toString().trim())) {
            etWeight.setError("Vui lòng nhập cân nặng");
            isValid = false;
        } else {
            try {
                Float.parseFloat(etWeight.getText().toString().trim());
            } catch (NumberFormatException e) {
                etWeight.setError("Cân nặng không hợp lệ, vui lòng nhập lại");
                isValid = false;
            }
        }

        return isValid;
    }
}
