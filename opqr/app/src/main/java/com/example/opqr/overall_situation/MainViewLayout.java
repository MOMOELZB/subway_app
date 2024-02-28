package com.example.opqr.overall_situation;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.Toast;

import com.example.opqr.Boarding.BoardingCode;
import com.example.opqr.Mine.Mine;
import com.example.opqr.R;
import com.example.opqr.service.Service;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainViewLayout extends AppCompatActivity {
    ViewPager viewPager;
    TabLayout tableLayout;
    int [] Riocn= new int[]{R.drawable.sefalse, R.drawable.qrtrue, R.drawable.minefalse};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_viewpager);
        viewPager = findViewById(R.id.viewpager);
        //准备Fragment对象
        List<Fragment> list = new ArrayList<>();
        list.add(new Service());
        list.add(new BoardingCode());
        list.add(new Mine());
        List<String> listTitle = new ArrayList<>();
        listTitle.add("服务");
        listTitle.add("乘车码");
        listTitle.add("我的");
        //创建Adapter对象
         MyPagerAdapter myPagerAdapter =
                new MyPagerAdapter(getSupportFragmentManager(),list,listTitle);
        //设置Adapter
        viewPager.setAdapter(myPagerAdapter);
        //关联Tablayout与viewpager
        tableLayout = findViewById(R.id.tablauout);
        tableLayout.setupWithViewPager(viewPager);
        for(int i=0;i<3;i++)
        {tableLayout.getTabAt(i).setIcon(Riocn[i]);}

        //设置监听器
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //页面互动时调用
            }

            @Override
            public void onPageSelected(int position) {
                //当滑动到某个页面时调用，position为滑动到的页面位置
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //页面滑动状态改变时调用
            }
        });
        tableLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }
}
