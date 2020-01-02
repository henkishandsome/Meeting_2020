package com.example.meeting2020.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.meeting2020.LoginActivity;
import com.example.meeting2020.R;
import com.example.meeting2020.SelfInfoActivity;

public class ForthFragment extends Fragment {
    private ListView orderListView;
    private String[] orders = {"个人信息", "小视频", "待开发", "待开发", "退出"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_info, container, false);
        orderListView = (ListView) view.findViewById(R.id.lv_order);

        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        OrderBaseAdapter orderAdapter=new OrderBaseAdapter();
        orderListView.setAdapter(orderAdapter);
        orderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:   //显示个人信息
                       Intent intent=new Intent(getActivity(), SelfInfoActivity.class);
                       startActivity(intent);
                       break;
//                    case 1:
//                        Intent intent1=new Intent(getActivity(),VideoPlayActivity.class);
//                        startActivity(intent1);
//                        break;

                    case 4:   //退出登录
                        Intent intent4=new Intent(getActivity(), LoginActivity.class);

                        getActivity().finish();
                        startActivity(intent4);
                        break;
                }
            }
        });
    }
    class ViewHolder{
        TextView order;
    }
    class OrderBaseAdapter extends BaseAdapter{
        @Override
        public int getCount(){
            return orders.length;
        }
        @Override
        public Object getItem(int position){
            return orders[position];
        }
        @Override
        public long getItemId(int position){
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            ViewHolder holder=null;
            if (convertView==null){
                convertView=View.inflate(getActivity(),R.layout.info_item,null);
                holder=new ViewHolder();
                holder.order=(TextView) convertView.findViewById(R.id.order);
                convertView.setTag(holder);
            }else {
                holder=(ViewHolder) convertView.getTag();
            }
            holder.order.setText(orders[position]);
            return convertView;
        }
    }





}
