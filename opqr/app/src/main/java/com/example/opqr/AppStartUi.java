package com.example.opqr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.opqr.Login.LoginByPassword;
import com.example.opqr.Boarding.BoardingCode;
import com.example.opqr.overall_situation.App_Var;
import com.example.opqr.overall_situation.MainViewLayout;
import com.example.opqr.overall_situation.OkHttpUtils;

public class AppStartUi extends AppCompatActivity {
    private SharedPreferences remember;
    private OkHttpUtils net_connect=new OkHttpUtils();
    public App_Var temp= App_Var.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appstartui);
        remember = getSharedPreferences("config", Context.MODE_PRIVATE);
            reload();
    }
    private void reload()
    {
        String phone=remember.getString("phone",null);
        String password=remember.getString("password",null);
        if(phone!=null&&password!=null)
        {
            temp.now_user=net_connect.LoginCheck(phone,password);
            if(!temp.now_user.UserIsempty())
            {
                AutoLogin();
            }
            else {
                TravelLogin();
            }
        }
        else {
            TravelLogin();
        }
    }
private  void TravelLogin()
{
    Intent travelintent=new Intent(AppStartUi.this,LoginByPassword.class);
    travelintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(travelintent);
}
    private  void AutoLogin()
    {
        Intent travelintent=new Intent(AppStartUi.this, MainViewLayout.class);
        travelintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(travelintent);
    }
}
