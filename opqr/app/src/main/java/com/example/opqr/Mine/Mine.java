package com.example.opqr.Mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.opqr.Login.LoginByPassword;
import com.example.opqr.Mine.FaceRecognition;
import com.example.opqr.R;
import com.example.opqr.overall_situation.App_Var;

public class Mine extends Fragment implements  View.OnClickListener{
    private RadioGroup login_tool;
    private Button exitbtn;
    private TextView account_name;
    private App_Var temp= App_Var.getInstance();
    private Button Payment_method;
    private View view;
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.Payment_method)
        {
            Intent intent=new Intent(getActivity(), FaceRecognition.class);
            //  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (view.getId()==R.id.exit) {
            temp.login_up_way=false;
            temp.now_user.clear();
            Intent intent=new Intent(getActivity(), LoginByPassword.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,  Bundle savedInstanceState) {

        /*
         * resourse:第一个参数是页面布局文件
         * root：第二个参数，父类
         * attachToRoot：第三个参数，是否以父类容器的形式添加(是否保留当前布局文件的当前属性)
         */
        view = inflater.inflate(R.layout.mine,
                container,false);//当前的页面布局，当前的布局文件（在写自己的适配器的时候使用过）
        //false没有保留父类容器的布局样式
        Payment_method = view.findViewById(R.id.Payment_method);
    //    login_tool=view.findViewById(R.id.toolway);
        exitbtn = view.findViewById(R.id.exit);
        exitbtn.setOnClickListener(this);
        Payment_method.setOnClickListener(this);
        account_name=view.findViewById(R.id.account_name);
        account_name.setText(temp.now_user.getUserName());
        return view;//返回当前的布局文件
    }
}

