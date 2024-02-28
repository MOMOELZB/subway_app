package com.example.opqr.overall_situation;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class MyPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> list;
    private List<String> listTitle;

//    public MyPagerAdapter(@NonNull FragmentManager fm, int behavior,List<Fragment> list) {
//        super(fm, behavior);
//        this.list=list;
//    }

    public MyPagerAdapter(@NonNull FragmentManager fm, List<Fragment> list,List<String> listTitle) {
        super(fm);
        this.list=list;
        this.listTitle=listTitle;
    }

    @NonNull
    @Override
    //获取当前活动到的Fragment对象
    public Fragment getItem(int position) {
        return list.get(position);
    }

    //获取Fragment的个数
    // @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    //获取当前Fragment对应的标签
    public CharSequence getPageTitle(int position) {
        return listTitle.get(position);
    }
}

