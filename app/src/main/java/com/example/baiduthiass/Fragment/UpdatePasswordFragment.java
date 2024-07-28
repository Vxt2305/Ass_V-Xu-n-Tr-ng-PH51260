package com.example.baiduthiass.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.baiduthiass.Helper.DatabaseHelper2;
import com.example.baiduthiass.DAO.UserDao;
import com.example.baiduthiass.R;

public class UpdatePasswordFragment extends Fragment {

    private EditText oldPasswordEditText, newPasswordEditText;
    private Button saveButton;
    private UserDao userDao;
    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.update_password, container, false);

        oldPasswordEditText = view.findViewById(R.id.EditText_oldPassword);
        newPasswordEditText = view.findViewById(R.id.EditText_newPassword);
        saveButton = view.findViewById(R.id.button_saveLayoutUpdate);

        // Khởi tạo DatabaseHelper2 và UserDao
        DatabaseHelper2 databaseHelper = new DatabaseHelper2(getContext());
        userDao = new UserDao(databaseHelper.getWritableDatabase());

        saveButton.setOnClickListener(v -> updatePassword());

        return view;
    }

    private void updatePassword() {
        String oldPassword = oldPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword)) {
            showAlert("Lỗi", "Cả hai trường đều phải được điền.");
        } else if (!userDao.checkPassword(username, oldPassword)) {
            showAlert("Lỗi", "Mật khẩu cũ không đúng.");
        } else if (oldPassword.equals(newPassword)) {
            showAlert("Lỗi", "Mật khẩu mới không thể giống mật khẩu cũ.");
        } else {
            if (userDao.updatePassword(username, newPassword)) {
                showAlert("Thành công", "Mật khẩu đã được cập nhật thành công.");
            } else {
                showAlert("Lỗi", "Không thể cập nhật mật khẩu.");
            }
        }
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onDestroyView() {
        // Không cần phải đóng database ở đây nếu bạn đã dùng UserDao, nó sẽ được quản lý bởi DatabaseHelper2.
        super.onDestroyView();
    }
}
