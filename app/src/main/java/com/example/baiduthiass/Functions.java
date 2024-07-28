package com.example.baiduthiass;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.baiduthiass.DAO.BmiRecordsDao;
import com.example.baiduthiass.DAO.UserDao;
import com.example.baiduthiass.DAO.UserDetailsDao;
import com.example.baiduthiass.Fragment.CounselingFragment;
import com.example.baiduthiass.Fragment.ExerciseFragment;
import com.example.baiduthiass.Fragment.FoodFragment;
import com.example.baiduthiass.Fragment.HomeFragment;
import com.example.baiduthiass.Fragment.MeditationFragment;
import com.example.baiduthiass.Fragment.SleepFragment;
import com.example.baiduthiass.Fragment.UpdatePasswordFragment;
import com.example.baiduthiass.Fragment.UserDetailFragment;
import com.example.baiduthiass.Fragment.WritingFragment;
import com.example.baiduthiass.Helper.DatabaseHelper2;
import com.google.android.material.navigation.NavigationView;

public class Functions extends AppCompatActivity {

    private AlertDialog moodDialog;
    private String username;
    private Fragment pendingFragment;
    private NavigationView navigationView;
    private DatabaseHelper2 dbHelper;
    private UserDao userDao;
    private UserDetailsDao userDetailsDao;
    private BmiRecordsDao bmiRecordsDao;
    private TextView toolbarTitle;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.functions);

        initializeComponents();
        setupToolbar();
        setupNavigationView();

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        username = sharedPreferences.getString(KEY_USERNAME, null);

        if (username == null) {
            navigateToLogin();
            return;
        }

        setupNavigationHeader();
        checkUserDetail();
    }

    private void initializeComponents() {
        dbHelper = new DatabaseHelper2(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        userDao = new UserDao(db);
        userDetailsDao = new UserDetailsDao(db);
        bmiRecordsDao = new BmiRecordsDao(db);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbarTitle = findViewById(R.id.toolbar_title);

        ImageView actionMenu = findViewById(R.id.action_toolbar);
        actionMenu.setOnClickListener(v -> {
            DrawerLayout drawer = findViewById(R.id.functionsLayout);
            drawer.openDrawer(GravityCompat.START);
        });
    }

    private void setupNavigationView() {
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    private void setupNavigationHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView textViewUsername = headerView.findViewById(R.id.textView_username);
        TextView textViewEmail = headerView.findViewById(R.id.textView_email);

        if (username != null) {
            textViewUsername.setText(username);
            String email = userDao.getEmail(username);
            textViewEmail.setText(email);
        }
    }


    private void checkUserDetail() {
        if (!userDetailsDao.isUserDetailComplete(username)) {
            showAlert("Thông tin chưa đầy đủ", "Vui lòng hoàn thiện hồ sơ trước khi tiếp tục.", this::navigateToUserDetailFragment);
        } else {
            showMoodDialog();
        }
    }

    private void showAlert(String title, String message, Runnable onPositiveButtonClick) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    onPositiveButtonClick.run();
                    dialog.dismiss();
                })
                .show();
    }

    private void navigateToUserDetailFragment() {
        UserDetailFragment userDetailFragment = new UserDetailFragment();
        userDetailFragment.setUsername(username);
        loadFragment(userDetailFragment, "Thông tin cá nhân");
        updateNavigationViewSelection(userDetailFragment);
    }

    private void navigateToHome() {
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setUsername(username);
        loadFragment(homeFragment, "Trang chủ");
        updateNavigationViewSelection(homeFragment);
    }

    private void showMoodDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bạn cảm thấy thế nào hôm nay?");
        builder.setCancelable(false);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_mood, null);
        builder.setView(dialogView);

        Button btnHappy = dialogView.findViewById(R.id.btn_happy);
        Button btnSad = dialogView.findViewById(R.id.btn_sad);
        Button btnAngry = dialogView.findViewById(R.id.btn_angry);
        Button btnHungry = dialogView.findViewById(R.id.btn_hungry);

        btnHappy.setOnClickListener(v -> handleMoodSelection(new ExerciseFragment(), "Chạy bộ", "Bạn đang rất vui! Hãy thử một vài bài tập để giữ cho tinh thần phấn chấn này."));
        btnSad.setOnClickListener(v -> handleMoodSelection(new CounselingFragment(), "Tư vấn tâm lý", "Có vẻ bạn đang buồn. Hãy tìm kiếm sự hỗ trợ từ một nhà tư vấn để cảm thấy tốt hơn."));
        btnAngry.setOnClickListener(v -> handleMoodSelection(new MeditationFragment(), "Luyện tập thiền định", "Có vẻ bạn đang cảm thấy tức giận. Hãy thư giãn với một buổi thiền hoặc yoga."));
        btnHungry.setOnClickListener(v -> handleMoodSelection(new FoodFragment(username), "Quản lý BMI", "Có vẻ bạn đang cảm thấy đói. Hãy kiểm tra các gợi ý ăn uống của chúng tôi."));

        builder.setNegativeButton("Hủy", (dialog, which) -> {
            dialog.dismiss();
            navigateToHome();
        });

        moodDialog = builder.create();
        moodDialog.show();
    }

    private void handleMoodSelection(Fragment fragment, String title, String message) {
        pendingFragment = fragment;
        moodDialog.dismiss();
        new AlertDialog.Builder(this)
                .setTitle("Thông báo")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    loadFragment(fragment, title);
                    updateNavigationViewSelection(fragment); // Thêm dòng này để cập nhật trạng thái chọn
                    dialog.dismiss();
                })
                .show();
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.functionsLayout);
        drawer.closeDrawer(GravityCompat.START);

        Fragment fragment = null;
        String title = "";

        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            fragment = new HomeFragment();
            ((HomeFragment) fragment).setUsername(username);
            title = "Trang chủ";
        } else if (itemId == R.id.nav_manage_exercise) {
            fragment = new ExerciseFragment();
            ((ExerciseFragment) fragment).setUsername(username);
            title = "Chạy bộ";
        } else if (itemId == R.id.nav_manage_writing) {
            fragment = new WritingFragment();
            ((WritingFragment) fragment).setUsername(username);
            title = "Quản lý bài viết";
        } else if (itemId == R.id.nav_manage_food) {
            fragment = new FoodFragment(username);
            title = "Quản lý BMI";
        } else if (itemId == R.id.nav_manage_sleep) {
            fragment = new SleepFragment();
            title = "Quản lý giấc ngủ";
        } else if (itemId == R.id.nav_manage_meditation) {
            fragment = new MeditationFragment();
            title = "Luyện tập thiền định";
        } else if (itemId == R.id.nav_manage_counseling) {
            fragment = new CounselingFragment();
            title = "Tư vấn tâm lý";
        } else if (itemId == R.id.nav_information) {
            fragment = new UserDetailFragment();
            ((UserDetailFragment) fragment).setUsername(username);
            title = "Thông tin cá nhân";
        } else if (itemId == R.id.nav_updatePassword) {
            fragment = new UpdatePasswordFragment();
            ((UpdatePasswordFragment) fragment).setUsername(username);
            title = "Cập nhật mật khẩu";
        } else if (itemId == R.id.nav_logout) {
            Intent intent = new Intent(Functions.this, Login.class);
            startActivity(intent);
            finish();
            return true;
        } else {
            return false;
        }

        if (fragment != null) {
            loadFragment(fragment, title);
            updateNavigationViewSelection(fragment);
        }

        return true;
    }

    private void updateNavigationViewSelection(Fragment fragment) {
        MenuItem item = null;
        if (fragment instanceof HomeFragment) {
            item = navigationView.getMenu().findItem(R.id.nav_home);
        } else if (fragment instanceof ExerciseFragment) {
            item = navigationView.getMenu().findItem(R.id.nav_manage_exercise);
        } else if (fragment instanceof WritingFragment) {
            item = navigationView.getMenu().findItem(R.id.nav_manage_writing);
        } else if (fragment instanceof FoodFragment) {
            item = navigationView.getMenu().findItem(R.id.nav_manage_food);
        } else if (fragment instanceof SleepFragment) {
            item = navigationView.getMenu().findItem(R.id.nav_manage_sleep);
        } else if (fragment instanceof MeditationFragment) {
            item = navigationView.getMenu().findItem(R.id.nav_manage_meditation);
        } else if (fragment instanceof CounselingFragment) {
            item = navigationView.getMenu().findItem(R.id.nav_manage_counseling);
        } else if (fragment instanceof UserDetailFragment) {
            item = navigationView.getMenu().findItem(R.id.nav_information);
        } else if (fragment instanceof UpdatePasswordFragment) {
            item = navigationView.getMenu().findItem(R.id.nav_updatePassword);
        }

        if (item != null) {
            item.setChecked(true);
        }
    }

    private void loadFragment(Fragment fragment, String title) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_functions);
        if (currentFragment != null && currentFragment.getClass().equals(fragment.getClass())) {
            return; // Không thay thế nếu fragment hiện tại giống với fragment mới
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_functions, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        toolbarTitle.setText(title);
    }


    private void navigateToLogin() {
        Intent intent = new Intent(Functions.this, Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (moodDialog != null && moodDialog.isShowing()) {
            moodDialog.dismiss();
        }
        super.onDestroy();
    }
}
