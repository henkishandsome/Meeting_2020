package com.example.meeting2020.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.meeting2020.Meeting.MeetingListActivity;
import com.example.meeting2020.R;


public class LeaderThirdFragment extends Fragment {
    private GridView gridView;
    private int[] iconarray = {R.drawable.meetingmanage,  R.drawable.draw};
    private String[] name = {"会议管理",  "抽奖设置"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_third_leader, container, false);
        gridView = view.findViewById(R.id.grid);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Gridadapter gridadapter = new Gridadapter(getActivity(), name, iconarray);
        gridView.setAdapter(gridadapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(getActivity(), MeetingListActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(getActivity(),null));
                        break;
                    default:
                }
            }
        });
    }

    private class Gridadapter extends BaseAdapter {

        private LayoutInflater inflater;
        private String[] name;
        private int[] iconarray;

        public Gridadapter(Context context, String[] name, int[] iconarray) {
            this.inflater = LayoutInflater.from(context);
            this.name = name;
            this.iconarray = iconarray;
        }

        @Override
        public int getCount() {
            return name.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.function_grid_item, null, false);
                holder.iv = convertView.findViewById(R.id.img);
                holder.iv.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
                holder.iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.tv = convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.iv.setImageResource(iconarray[position]);
            holder.tv.setText(name[position]);
            return convertView;
        }

        private class ViewHolder {
            ImageView iv;
            TextView tv;
        }
    }
}
