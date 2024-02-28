package com.example.opqr.Boarding;

import android.content.Intent;
import android.graphics.Bitmap;

//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.opqr.Boarding.EncodingUtils;
import com.example.opqr.R;
import com.example.opqr.overall_situation.App_Var;
import com.example.opqr.Mine.Mine;
import com.example.opqr.service.Service;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.opqr.R;

public class BoardingCode extends Fragment {
    private ImageView enCodeImage1,enCodeImage2;//展示生成的二维码
    private EditText editText;//输入要生成二维码的内容
    private App_Var usertemp;
    private RadioGroup login_tool;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,  Bundle savedInstanceState) {

        /*
         * resourse:第一个参数是页面布局文件
         * root：第二个参数，父类
         * attachToRoot：第三个参数，是否以父类容器的形式添加(是否保留当前布局文件的当前属性)
         */
        View view = inflater.inflate(R.layout.boardingcode,
                container,false);//当前的页面布局，当前的布局文件（在写自己的适配器的时候使用过）
        //false没有保留父类容器的布局样式
        //初始化控件
        enCodeImage1 = (ImageView)view.findViewById(R.id.code_image1);
        //  enCodeImage2 = (ImageView) findViewById(R.id.code_image2);
        usertemp = App_Var.getInstance();
        String user_id= usertemp.now_user.getIdentification();
        if(user_id!=null)
        {
            Bitmap codeBitmap = EncodingUtils.createQRCode(user_id,500,500,null);
            enCodeImage1.setImageBitmap(codeBitmap);
        }
        return view;//返回当前的布局文件
    }
}