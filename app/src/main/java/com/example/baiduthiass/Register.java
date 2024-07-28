package com.example.baiduthiass;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.baiduthiass.Helper.DatabaseHelper2;
import com.example.baiduthiass.DAO.UserDao;
import com.example.baiduthiass.Model.User;

public class Register extends AppCompatActivity {
    private Button btnRegister, btnLogin;
    private EditText etUsername, etPassword, etPasswordAgain, etEmail;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        initView();
        // Khởi tạo DatabaseHelper để lấy SQLiteDatabase
        DatabaseHelper2 databaseHelper = new DatabaseHelper2(this);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        userDao = new UserDao(db);

        btnLogin.setOnClickListener(v -> {
            Intent i = new Intent(Register.this, Login.class);
            startActivity(i);
        });

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String passwordAgain = etPasswordAgain.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            if (validateInputs(username, password, passwordAgain, email)) {
                if (userDao.checkUserName(username) || userDao.checkEmail(email)) {
                    Toast.makeText(Register.this, "Tên người dùng hoặc email đã tồn tại", Toast.LENGTH_SHORT).show();
                } else {
                    boolean insert = userDao.insertUser(new User(username, password, email));
                    if (insert) {
                        showSuccessDialog();
                    } else {
                        Toast.makeText(Register.this, "Đăng ký không thành công", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean validateInputs(String username, String password, String passwordAgain, String email) {
        boolean hasError = false;

        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Tên người dùng là bắt buộc");
            hasError = true;
        } else {
            etUsername.setError(null);
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email là bắt buộc");
            hasError = true;
        } else if (!isEmailValid(email)) {
            etEmail.setError("Email không hợp lệ");
            hasError = true;
        } else {
            etEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Mật khẩu là bắt buộc");
            hasError = true;
        } else if (!isPasswordValid(password)) {
            etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            hasError = true;
        } else {
            etPassword.setError(null);
        }

        if (TextUtils.isEmpty(passwordAgain)) {
            etPasswordAgain.setError("Vui lòng xác nhận mật khẩu");
            hasError = true;
        } else if (!password.equals(passwordAgain)) {
            etPasswordAgain.setError("Mật khẩu không khớp");
            hasError = true;
        } else {
            etPasswordAgain.setError(null);
        }

        return !hasError;
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Thông báo")
                .setMessage("Đăng ký thành công")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent i = new Intent(Register.this, Login.class);
                    startActivity(i);
                })
                .show();
    }

    private void initView() {
        btnLogin = findViewById(R.id.btnTextView_login);
        btnRegister = findViewById(R.id.btn_register_register);
        etUsername = findViewById(R.id.EditText_username);
        etPassword = findViewById(R.id.EditText_password);
        etPasswordAgain = findViewById(R.id.EditText_confirmPassword);
        etEmail = findViewById(R.id.EditText_email);
    }
}
