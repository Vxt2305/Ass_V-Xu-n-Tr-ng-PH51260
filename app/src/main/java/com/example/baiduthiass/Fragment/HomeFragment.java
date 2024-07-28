package com.example.baiduthiass.Fragment;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.baiduthiass.DAO.UserDetailsDao;
import com.example.baiduthiass.Helper.DatabaseHelper2;
import com.example.baiduthiass.R;

public class HomeFragment extends Fragment {

    private String username;
    private DatabaseHelper2 dbHelper2;
    private UserDetailsDao userDetailsDao;

    public void setUsername(String username) {
        this.username = username;
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_home, container, false);

        TextView textViewName = view.findViewById(R.id.textView_username_home);

        dbHelper2 = new DatabaseHelper2(getActivity());
        SQLiteDatabase db = dbHelper2.getWritableDatabase();
        userDetailsDao = new UserDetailsDao(db);
        String fullName = userDetailsDao.getFullName(username);

        if (fullName != null && !fullName.isEmpty()) {
            textViewName.setText("Hi, " + fullName);
        } else {
            textViewName.setText("Hi, User");
        }

        return view;
    }
}
