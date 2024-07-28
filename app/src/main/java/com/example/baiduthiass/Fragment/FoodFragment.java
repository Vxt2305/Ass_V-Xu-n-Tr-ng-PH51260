package com.example.baiduthiass.Fragment;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.baiduthiass.DAO.UserDetailsDao;
import com.example.baiduthiass.Helper.DatabaseHelper2;
import com.example.baiduthiass.DAO.BmiRecordsDao;
import com.example.baiduthiass.Model.BmiRecord;
import com.example.baiduthiass.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FoodFragment extends Fragment {
    private TextView TvNameBMI;
    private EditText EdtHeightBMI, EdtWeightBMI;
    private Button btnShowBMI;
    private DatabaseHelper2 dbHelper;
    private BmiRecordsDao bmiRecordsDao;
    private UserDetailsDao userDetailsDao;
    private String username;

    public FoodFragment(String username) {
        this.username = username;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_food, container, false);

        TvNameBMI = view.findViewById(R.id.Name);
        EdtHeightBMI = view.findViewById(R.id.editText_heightBMI);
        EdtWeightBMI = view.findViewById(R.id.editText_weightBMI);
        btnShowBMI = view.findViewById(R.id.button_showBMI);

        dbHelper = new DatabaseHelper2(getContext());
        bmiRecordsDao = new BmiRecordsDao(dbHelper.getWritableDatabase());
        userDetailsDao = new UserDetailsDao(dbHelper.getWritableDatabase());

        loadUserDetails(username);

        btnShowBMI.setOnClickListener(v -> calculateAndShowBMI());

        return view;
    }

    private void loadUserDetails(String username) {
        Cursor cursor = userDetailsDao.getUserDetailsFood(username);
        if (cursor != null && cursor.moveToFirst()) {
            String fullName = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
            TvNameBMI.setText(fullName);

            float height = cursor.getFloat(cursor.getColumnIndexOrThrow("height"));
            float weight = cursor.getFloat(cursor.getColumnIndexOrThrow("weight"));

            if (height > 0) {
                EdtHeightBMI.setText(String.valueOf(height));
            }

            if (weight > 0) {
                EdtWeightBMI.setText(String.valueOf(weight));
            }

            cursor.close();
        }
    }

    private void calculateAndShowBMI() {
        String heightStr = EdtHeightBMI.getText().toString().trim();
        String weightStr = EdtWeightBMI.getText().toString().trim();

        if (TextUtils.isEmpty(heightStr) || TextUtils.isEmpty(weightStr)) {
            showErrorDialog("Thiếu thông tin", "Vui lòng nhập chiều cao và cân nặng.");
            return;
        }

        try {
            float height = Float.parseFloat(heightStr) / 100; // Convert cm to meters
            float weight = Float.parseFloat(weightStr);

            if (height <= 0 || weight <= 0) {
                showErrorDialog("Thông tin không hợp lệ", "Chiều cao và cân nặng phải lớn hơn không.");
                return;
            }

            float bmi = weight / (height * height);
            saveBMIRecord(bmi);
            showBMIDialog(bmi);
        } catch (NumberFormatException e) {
            showErrorDialog("Thông tin không hợp lệ", "Vui lòng nhập số hợp lệ.");
        }
    }

    private void saveBMIRecord(float bmi) {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        BmiRecord bmiRecord = new BmiRecord(0, username, bmi, currentDate);
        bmiRecordsDao.insertBMIRecord(bmiRecord);
    }

    private void showBMIDialog(float bmi) {
        String bmiLevel = getBMILevel(bmi);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.show_bmi_food, null);
        builder.setView(dialogView);
        builder.setCancelable(false);

        TextView textViewBMI = dialogView.findViewById(R.id.textView_BMI);
        TextView textViewBMIResults = dialogView.findViewById(R.id.BMI_results);
        textViewBMI.setText(String.format(Locale.getDefault(), "%.2f", bmi));
        textViewBMIResults.setText(bmiLevel);

        AlertDialog dialog = builder.create();
        dialog.show();

        Button buttonOK = dialogView.findViewById(R.id.button_OK);
        Button buttonWatch = dialogView.findViewById(R.id.button_watchBMI);

        buttonOK.setOnClickListener(v -> dialog.dismiss());
        buttonWatch.setOnClickListener(v -> {
            dialog.dismiss();
            showNutritionAdviceDialog(bmiLevel);
        });
    }

    private void showNutritionAdviceDialog(String bmiLevel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Lời khuyên dinh dưỡng");
        builder.setMessage(getNutritionAdvice(bmiLevel));
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String getNutritionAdvice(String bmiLevel) {
        switch (bmiLevel) {
            case "Gầy độ III":
                return "Hãy tăng cân một cách khoa học. Thay vì ăn thật nhiều vào 2-3 bữa/ngày, bạn nên chia nhỏ bữa ăn thành 5-6 lần/ngày. Ăn các loại thực phẩm giàu dinh dưỡng và nhiều calo như pho-mát, bơ, bánh mì nướng nguyên hạt. Đừng quên kết hợp với chế độ tập luyện để tăng cường khối cơ.";
            case "Gầy độ II":
                return "Hãy tăng cân một cách khoa học. Thay vì ăn thật nhiều vào 2-3 bữa/ngày, bạn nên chia nhỏ bữa ăn thành 5-6 lần/ngày. Ăn các loại thực phẩm giàu dinh dưỡng và nhiều calo như pho-mát, bơ, bánh mì nướng nguyên hạt. Đừng quên kết hợp với chế độ tập luyện để tăng cường khối cơ.";
            case "Gầy độ I":
                return "Hãy tăng cân một cách khoa học. Thay vì ăn thật nhiều vào 2-3 bữa/ngày, bạn nên chia nhỏ bữa ăn thành 5-6 lần/ngày. Ăn các loại thực phẩm giàu dinh dưỡng và nhiều calo như pho-mát, bơ, bánh mì nướng nguyên hạt. Đừng quên kết hợp với chế độ tập luyện để tăng cường khối cơ.";
            case "Bình thường":
                return "Để duy trì cân nặng và chỉ số BMI lý tưởng này, hãy tiếp tục duy trì chế độ ăn uống và tập luyện hàng ngày. Ăn uống đầy đủ dưỡng chất và duy trì vận động thường xuyên.";
            case "Thừa cân":
                return "Hãy cắt giảm khẩu phần ăn hàng ngày, tăng cường ăn các loại rau xanh, củ quả, trái cây và chất xơ. Hạn chế ăn thực phẩm nhiều chất béo, đường và tinh bột. Uống nhiều nước và chia nhỏ bữa ăn trong ngày. Kết hợp với tập thể dục đều đặn như chạy bộ, bơi lội, hoặc đạp xe.";
            case "Béo phì độ I":
                return "Cắt giảm khẩu phần ăn hàng ngày, ăn nhiều rau xanh, củ quả, trái cây và chất xơ. Thay thế thực phẩm chứa nhiều chất béo và đường bằng thực phẩm lành mạnh. Uống nhiều nước và chia nhỏ bữa ăn. Kết hợp với tập thể dục thường xuyên như cardio, bơi lội, hoặc đạp xe.";
            case "Béo phì độ II":
                return "Cắt giảm khẩu phần ăn hàng ngày, tăng cường rau xanh, củ quả, trái cây và chất xơ. Giảm lượng thực phẩm chứa nhiều chất béo và đường. Uống nhiều nước và chia nhỏ bữa ăn. Tập thể dục đều đặn như cardio, bơi lội, hoặc đạp xe. Nên tham khảo ý kiến bác sĩ để có chế độ dinh dưỡng và tập luyện phù hợp.";
            case "Béo phì độ III":
                return "Bạn cần sự can thiệp và theo dõi của bác sĩ và chuyên gia dinh dưỡng. Chế độ ăn uống cần được theo dõi kỹ lưỡng, và bạn cần tuân thủ theo kế hoạch dinh dưỡng từ bác sĩ. Tập thể dục đều đặn với cường độ phù hợp và có thể cần đến một số loại thuốc hoặc phương pháp điều trị đặc biệt.";
            default:
                return "Không có lời khuyên dinh dưỡng.";
        }
    }

    private void showErrorDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String getBMILevel(float bmi) {
        if (bmi < 16) return "Gầy độ III";
        else if (bmi < 17) return "Gầy độ II";
        else if (bmi < 18.5) return "Gầy độ I";
        else if (bmi < 25) return "Bình thường";
        else if (bmi < 30) return "Thừa cân";
        else if (bmi < 35) return "Béo phì độ I";
        else if (bmi < 40) return "Béo phì độ II";
        else return "Béo phì độ III";
    }

    @Override
    public void onDestroyView() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroyView();
    }
}
