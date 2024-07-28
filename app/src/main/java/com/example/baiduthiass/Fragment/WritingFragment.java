package com.example.baiduthiass.Fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.baiduthiass.Adapter.WritePostAdapter;
import com.example.baiduthiass.Helper.DatabaseHelper2;
import com.example.baiduthiass.Model.WritePost;
import com.example.baiduthiass.R;
import com.example.baiduthiass.DAO.WritePostDao;

import java.util.List;

public class WritingFragment extends Fragment {
    private ListView listView;
    private WritePostAdapter adapter;
    private WritePostDao writePostDao;
    private SQLiteDatabase db;
    private EditText editTextPost;
    private Button buttonSavePost;
    private String username;

    public void setUsername(String username) {
        this.username = username; // Lấy username từ bên function sau khi nhận thông tin người dùng khi đăng nhập
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_writing, container, false);
        listView = view.findViewById(R.id.listView_posts);
        editTextPost = view.findViewById(R.id.editTextText_post);
        buttonSavePost = view.findViewById(R.id.button_savePost);

        // Khởi tạo DatabaseHelper2 và WritePostDao
        DatabaseHelper2 dbHelper = new DatabaseHelper2(getActivity());
        writePostDao = new WritePostDao(getActivity());
        writePostDao.open();

        // Lấy bài viết của người dùng hiện tại
        List<WritePost> writePostList = writePostDao.getWritePostsByUsername(username);
        adapter = new WritePostAdapter(getActivity(), writePostList);
        listView.setAdapter(adapter);

        buttonSavePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postContent = editTextPost.getText().toString();
                if (!postContent.isEmpty()) {
                    savePost(postContent);
                    updatePostList();
                    editTextPost.setText(""); // Clear the input field after saving
                }
            }
        });

        writePostDao.close();

        return view;
    }

    private void savePost(String postContent) {
        writePostDao.open();
        writePostDao.createWritePost(username, postContent, String.valueOf(System.currentTimeMillis()));
        writePostDao.close();
    }

    private void updatePostList() {
        writePostDao.open();
        List<WritePost> updatedWritePostList = writePostDao.getWritePostsByUsername(username);
        adapter = new WritePostAdapter(getActivity(), updatedWritePostList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        writePostDao.close();
    }
}