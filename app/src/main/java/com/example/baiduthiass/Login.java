package com.example.baiduthiass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.baiduthiass.Helper.DatabaseHelper2;
import com.example.baiduthiass.DAO.UserDao;

public class Login extends AppCompatActivity {
    private Button btnLogin, btnRegister;
    private EditText etUsername, etPassword;
    private CheckBox checkBoxRemember;
    private UserDao userDao;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER = "remember";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initView();
        // Khởi tạo DatabaseHelper để lấy SQLiteDatabase
        DatabaseHelper2 databaseHelper = new DatabaseHelper2(this);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        userDao = new UserDao(db);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Đặt giá trị của username và password nếu đã lưu trước đó
        if (sharedPreferences.getBoolean(KEY_REMEMBER, false)) {
            etUsername.setText(sharedPreferences.getString(KEY_USERNAME, ""));
            etPassword.setText(sharedPreferences.getString(KEY_PASSWORD, ""));
            checkBoxRemember.setChecked(true);
        }

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            String usernameOrEmail = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (validateInputs(usernameOrEmail, password)) {
                boolean validCredentials = userDao.checkUsernameOrEmailAndPassword(usernameOrEmail, password);

                if (validCredentials) {
                    String username;

                    // Kiểm tra xem người dùng có đăng nhập bằng email không
                    if (userDao.isEmail(usernameOrEmail)) {
                        username = userDao.getUsername(usernameOrEmail);
                    } else {
                        username = usernameOrEmail;
                    }

                    if (username != null) {
                        // Lưu thông tin đăng nhập nếu người dùng đã chọn Remember
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        if (checkBoxRemember.isChecked()) {
                            editor.putString(KEY_USERNAME, username);
                            editor.putString(KEY_PASSWORD, password);
                            editor.putBoolean(KEY_REMEMBER, true);
                        } else {
                            editor.remove(KEY_USERNAME);
                            editor.remove(KEY_PASSWORD);
                            editor.remove(KEY_REMEMBER);
                        }
                        editor.apply();

                        Toast.makeText(Login.this, "Chào mừng " + username, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login.this, Functions.class);
                        intent.putExtra("username", username);  // Truyền tên người dùng qua Intent
                        startActivity(intent);
                        finish(); // Đóng Activity hiện tại sau khi đăng nhập thành công
                    } else {
                        showAlertDialog("Tên người dùng không tìm thấy.");
                    }
                } else {
                    showAlertDialog("Tên người dùng hoặc email và mật khẩu không hợp lệ");
                }
            }
        });
    }

    private boolean validateInputs(String usernameOrEmail, String password) {
        boolean hasError = false;

        if (TextUtils.isEmpty(usernameOrEmail)) {
            etUsername.setError("Tên người dùng hoặc email là bắt buộc");
            hasError = true;
        } else {
            etUsername.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Mật khẩu là bắt buộc");
            hasError = true;
        } else {
            etPassword.setError(null);
        }

        return !hasError;
    }

    private void showAlertDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Đăng nhập thất bại")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", null)
                .show();
    }

    private void initView() {
        btnLogin = findViewById(R.id.btn_login_login);
        btnRegister = findViewById(R.id.btnTextView_register);
        etUsername = findViewById(R.id.EditText_username);
        etPassword = findViewById(R.id.EditText_password);
        checkBoxRemember = findViewById(R.id.checkbox_remember);
    }
}
