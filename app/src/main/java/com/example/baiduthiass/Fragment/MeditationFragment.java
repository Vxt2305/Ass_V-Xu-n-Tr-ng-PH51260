package com.example.baiduthiass.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.baiduthiass.R;

import java.util.ArrayList;

public class MeditationFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_meditation, container, false);

        ListView listViewMeditation = view.findViewById(R.id.listView_meditation);

        // Tạo danh sách các bài thiền định
        ArrayList<String> meditationList = new ArrayList<>();
        meditationList.add("Cách thiền định");
        meditationList.add("Thiền định buổi sáng");
        meditationList.add("Thiền định buổi trưa");
        meditationList.add("Thiền định buổi tối");

        // Tạo CustomAdapter cho ListView
        CustomAdapter adapter = new CustomAdapter(meditationList);
        listViewMeditation.setAdapter(adapter);

        return view;
    }

    private class CustomAdapter extends BaseAdapter {

        private ArrayList<String> items;

        public CustomAdapter(ArrayList<String> items) {
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            if (convertView == null) {
                switch (position) {
                    case 0:
                        convertView = inflater.inflate(R.layout.item_meditation_method, parent, false);
                        break;
                    case 1:
                        convertView = inflater.inflate(R.layout.item_morning_meditation, parent, false);
                        break;
                    case 2:
                        convertView = inflater.inflate(R.layout.item_noon_meditation, parent, false);
                        break;
                    case 3:
                        convertView = inflater.inflate(R.layout.item_evening_meditation, parent, false);
                        break;
                }
            }

            return convertView;
        }
    }
}
