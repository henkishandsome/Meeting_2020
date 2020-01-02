package com.example.meeting2020;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.meeting2020.Fragment.FirstFragment;
import com.example.meeting2020.Fragment.ForthFragment;
import com.example.meeting2020.Fragment.LeaderThirdFragment;
import com.example.meeting2020.Fragment.SecondFragment;
import com.example.meeting2020.Fragment.ThirdFragment;
import com.example.meeting2020.JavaClass.Data;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private ImageView titleLeftImv;
    private TextView titleTv;
    private FirstFragment fg1;
    private SecondFragment fg2;
    private Fragment fg3;
    private ForthFragment fg4;
    private FrameLayout frameLayout;
    private RelativeLayout firstLayout;
    private RelativeLayout secondLayout;
    private RelativeLayout thirdLayout;
    private RelativeLayout forthLayout;
    private ImageView firstImage;
    private ImageView secondImage;
    private ImageView thirdImage;
    private ImageView forthImage;
    private TextView firstText;
    private TextView secondText;
    private TextView thirdText;
    private TextView forthText;
    private FragmentManager fragmentManager;
    private int white = 0xFFFFFFFF;
    private int gray = 0xFF7597B3;
    private int dark = 0xff000000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order);
        fragmentManager = getSupportFragmentManager();
        initView(); //初始化界面控件
        setChioceItem(0);  //初始化页面加载时显示第一个选项卡
    }

    //初始化页面
    private void initView() {
        //初始化页面标题栏
        titleLeftImv = (ImageView) findViewById(R.id.title_imv);
        titleLeftImv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.finish();
                startActivity(intent);
            }
        });
//        titleLeftImv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(MainActivity.this, LoginActivity.class));
//            }
//        });
        titleTv = (TextView) findViewById(R.id.title_text_tv);
        titleTv.setText("首页");
        //初始化底部导航栏的控件
        firstImage = (ImageView) findViewById(R.id.first_image);
        secondImage = (ImageView) findViewById(R.id.second_image);
        thirdImage = (ImageView) findViewById(R.id.third_image);
        forthImage = (ImageView) findViewById(R.id.forth_image);
        firstText = (TextView) findViewById(R.id.first_text);
        secondText = (TextView) findViewById(R.id.second_text);
        thirdText = (TextView) findViewById(R.id.third_text);
        forthText = (TextView) findViewById(R.id.forth_text);
        firstLayout = (RelativeLayout) findViewById(R.id.first_layout);
        secondLayout = (RelativeLayout) findViewById(R.id.second_layout);
        thirdLayout = (RelativeLayout) findViewById(R.id.third_layout);
        forthLayout = (RelativeLayout) findViewById(R.id.forth_layout);
        firstLayout.setOnClickListener(MainActivity.this);
        secondLayout.setOnClickListener(MainActivity.this);
        thirdLayout.setOnClickListener(MainActivity.this);
        forthLayout.setOnClickListener(MainActivity.this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.first_layout:
                setChioceItem(0);
                break;
            case R.id.second_layout:
                setChioceItem(1);
                break;
            case R.id.third_layout:
                setChioceItem(2);
                break;
            case R.id.forth_layout:
                setChioceItem(3);
                break;
            default:
                break;
        }
    }

    //设置点击选项卡的事件处理
    private void setChioceItem(int index) {
        final Data app = (Data) getApplication();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        clearChioce();  //清空，重置选项，隐藏所有Fragment
        hideFragments(fragmentTransaction);
        switch (index) {
            case 0:
                // firstImage.setImageResource(R.drawable....);
                firstText.setTextColor(dark);
                firstLayout.setBackgroundColor(gray);
                //如果fg1为空，则创建一个并添加到界面上
                if (fg1 == null) {
                    fg1 = new FirstFragment();
                    fragmentTransaction.add(R.id.content, fg1);
                } else {
                    //如果不为空，则直接将它显示出来
                    fragmentTransaction.show(fg1);

                }
                break;
            case 1:
                // firstImage.setImageResource(R.drawable....);
                secondText.setTextColor(dark);
                secondLayout.setBackgroundColor(gray);
                //如果fg1为空，则创建一个并添加到界面上
                if (fg2 == null) {
                    fg2 = new SecondFragment();
                    fragmentTransaction.add(R.id.content, fg2);
                } else {
                    //如果不为空，则直接将它显示出来
                    fragmentTransaction.show(fg2);
                }
                break;
            case 2:
                // firstImage.setImageResource(R.drawable....);
                thirdText.setTextColor(dark);
                thirdLayout.setBackgroundColor(gray);
                //如果fg1为空，则创建一个并添加到界面上
                if (fg3 == null) {
                    if (app.getStatus() == 0) {
                        fg3 = new ThirdFragment();
                    }else{
                        fg3 = new LeaderThirdFragment();
                    }
                    fragmentTransaction.add(R.id.content, fg3);
                } else {
                    //如果不为空，则直接将它显示出来
                    fragmentTransaction.show(fg3);
                }
                break;
            case 3:
                // firstImage.setImageResource(R.drawable....);
                forthText.setTextColor(dark);
                forthLayout.setBackgroundColor(gray);
                //如果fg1为空，则创建一个并添加到界面上
                if (fg4 == null) {
                    fg4 = new ForthFragment();
                    fragmentTransaction.add(R.id.content, fg4);
                } else {
                    //如果不为空，则直接将它显示出来
                    fragmentTransaction.show(fg4);

                }
                break;
        }
        fragmentTransaction.commit();  //提交

    }

    //当选中其中一个选项卡时，其它选项卡重置为默认
    private void clearChioce() {
        // firstImage.setImageResource(R.drawable....);
        firstText.setTextColor(gray);
        firstLayout.setBackgroundColor(white);
        // firstImage.setImageResource(R.drawable....);
        secondText.setTextColor(gray);
        secondLayout.setBackgroundColor(white);
        // firstImage.setImageResource(R.drawable....);
        thirdText.setTextColor(gray);
        thirdLayout.setBackgroundColor(white);
        // firstImage.setImageResource(R.drawable....);
        forthText.setTextColor(gray);
        forthLayout.setBackgroundColor(white);
    }

    //隐藏Fragment
    private void hideFragments(FragmentTransaction fragmentTransaction) {
        if (fg1 != null) {
            fragmentTransaction.hide(fg1);
        }
        if (fg2 != null) {
            fragmentTransaction.hide(fg2);
        }
        if (fg3 != null) {
            fragmentTransaction.hide(fg3);
        }
        if (fg4 != null) {
            fragmentTransaction.hide(fg4);
        }
    }

    protected long exitTime;//记录第一次点击时的时间

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                MainActivity.this.finish();

                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
