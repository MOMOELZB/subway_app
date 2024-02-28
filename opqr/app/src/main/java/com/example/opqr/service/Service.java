package com.example.opqr.service;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.opqr.R;

public class Service extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,  Bundle savedInstanceState) {
        /*
         * resourse:第一个参数是页面布局文件
         * root：第二个参数，父类
         * attachToRoot：第三个参数，是否以父类容器的形式添加(是否保留当前布局文件的当前属性)
         */
        View view = inflater.inflate(R.layout.service,
                container,false);//当前的页面布局，当前的布局文件（在写自己的适配器的时候使用过）
        //false没有保留父类容器的布局样式

        return view;//返回当前的布局文件
    }
}
