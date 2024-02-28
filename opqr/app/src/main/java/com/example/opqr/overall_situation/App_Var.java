package com.example.opqr.overall_situation;

import android.app.Application;

import java.util.HashMap;

public class App_Var extends Application {
    private static App_Var now_app;
    public int Uid;
    public User now_user=new User();
    public boolean login_up_way=true;
    public boolean faceway=true;
    public static App_Var getInstance()
    {
        return  now_app;
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
        now_app=this;
    }

}
