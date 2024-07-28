package com.example.baiduthiass.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.baiduthiass.R;
import com.example.baiduthiass.Model.WritePost;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WritePostAdapter extends BaseAdapter {
    private Context context;
    private List<WritePost> writePosts;

    public WritePostAdapter(Context context, List<WritePost> writePosts) {
        this.context = context;
        this.writePosts = writePosts;
    }

    @Override
    public int getCount() {
        return writePosts.size();
    }

    @Override
    public Object getItem(int position) {
        return writePosts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.layout_listview_post, parent, false);
        }

//        TextView textViewSTT = convertView.findViewById(R.id.textView_stt);
        TextView textViewLoiBietOn = convertView.findViewById(R.id.textView_loiBietOn);
        TextView textViewNgay = convertView.findViewById(R.id.textView_ngay);

        WritePost writePost = writePosts.get(position);

//        textViewSTT.setText("Lời biết ơn số " + String.valueOf(position + 1 + ": "));
        textViewLoiBietOn.setText(writePost.getPosts());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String formattedDate = sdf.format(new Date(Long.parseLong(writePost.getRecordDate())));
        textViewNgay.setHint(formattedDate);

        return convertView;
    }
}
