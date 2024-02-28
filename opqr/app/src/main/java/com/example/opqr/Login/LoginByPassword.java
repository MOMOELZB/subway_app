package com.example.opqr.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.opqr.R;
import com.example.opqr.overall_situation.MainViewLayout;
import com.example.opqr.overall_situation.OkHttpUtils;
//import com.example.opqr.overall_situation.UseDao;
import com.example.opqr.overall_situation.App_Var;
import com.example.opqr.Boarding.BoardingCode;
import com.mob.MobSDK;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class LoginByPassword extends AppCompatActivity implements View.OnClickListener,View.OnFocusChangeListener {

    private Button forgetpassowrd;
    private Button login_up;
    private Button login_in;
    private Button login_way;
    private Button ve_btn;
    private EditText phone_num;
    private EditText password;
    private String user_phone;
    private String user_password;
    String appKey = "3861318999f26";
    String appSecret = "88517c1179f830d3ee7df5f53c2589af";
    int count_second=30;//计时器
   // private static final String TAG = "mysql-testapp-UserDao";
    private SharedPreferences remember;
    public App_Var temp= App_Var.getInstance();

    private OkHttpUtils net_connect=new OkHttpUtils();
    private boolean login_way_flag=true;

    private EventHandler eventHandler= new EventHandler() {
        public void afterEvent(int event, int result, Object data) {
            if (result == SMSSDK.RESULT_COMPLETE) {
                // TODO 处理验证成功的结果
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                    Log.i("EventHandler", "提交验证码成功");
                    temp.now_user=net_connect.LoginCheckByVerification(user_phone);
                    if(!temp.now_user.UserIsempty())
                    {
                        remember = getSharedPreferences("config", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=remember.edit();
                        editor.putString("phone",temp.now_user.getUserPhoneNum());
                        editor.putString("password",temp.now_user.getUserPassword());
                        editor.commit();
                        Intent travelintent=new Intent(LoginByPassword.this, MainViewLayout.class);
                        travelintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(travelintent);
                    }
                    else {
                        Log.i("EventHandler", "账号可能不存在");
                    }
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //获取验证码成功
                    Log.i("EventHandler", "获取验证码成功");
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    //返回支持发送验证码的国家列表
                    Log.i("EventHandler", "返回支持发送验证码的国家列表");
                }
            } else {
                // TODO 处理错误的结果
                Log.i("EventHandler", "提交验证码失败");
                ((Throwable) data).printStackTrace();
            }
        }
    };
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == -9) {
                ve_btn.setText("重新发送(" + count_second + ")");
            } else if (msg.what == -8) {
                ve_btn.setText("获取验证码");
                ve_btn.setClickable(true);
                count_second = 30;
            }
        }
    };
    @Override
    public void onClick(View v)
    {
        if(v.getId()==R.id.login_in)
        {
            //登录时
            if(login_way_flag){
                loginbypassword();
            }
            else {
                SMSSDK.submitVerificationCode("86", user_phone,password.getText().toString());
            }
        }
        else if (v.getId()==R.id.login_way) {
            password.setText("");
            if(login_way_flag)
            {
                ve_btn.setVisibility(View.VISIBLE);
                password.setHint("请输入验证码");
            }
            else {
                ve_btn.setVisibility(View.INVISIBLE);
                password.setHint("请输入密码");
            }
             login_way_flag=!login_way_flag;
        }
        else if(v.getId()==R.id.login_up)
        {
            //注册时
            Intent intent_login_up=new Intent(this, Registration.class);
            intent_login_up.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent_login_up);
        }
        else if (v.getId()==R.id.forgetpassword) {
            startActivity(new Intent(this, PasswordReset.class));
        }
        else if(v.getId()==R.id.ve_btn)
        {   user_phone=phone_num.getText().toString();
            if(user_phone.length()!=11)
            {
                Toast.makeText(getApplicationContext(), "号码不足十一位", Toast.LENGTH_LONG).show();
                return;
            }
            else
            {
                SMSSDK.getVerificationCode("86", user_phone);
                ve_btn.setClickable(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (; count_second > 0; count_second--) {
                            handler.sendEmptyMessage(-9);
                            if (count_second <= 0) {
                                break;
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        handler.sendEmptyMessage(-8);
                    }
                }).start();
            }
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_by_password);
        //检查权限是否开启
        checkPermission();
        init();
        MobSDK.submitPolicyGrantResult(true);
        MobSDK.init(this,appKey, appSecret);
        SMSSDK.registerEventHandler(eventHandler);
        remember = getSharedPreferences("config", Context.MODE_PRIVATE);
    }
    protected void onStart(){
        super.onStart ();
        Log.i ( "","调用onStart" );
      if (!temp.now_user.UserIsempty()) {
            phone_num.setText(temp.now_user.getUserPhoneNum());
            password.setText(temp.now_user.getUserPassword());
        }
    }
    @Override
    protected void onResume(){
        super.onResume ();
        Log.i ( "","调用onResume" );
        if (!temp.now_user.UserIsempty()) {
            phone_num.setText(temp.now_user.getUserPhoneNum());
            password.setText(temp.now_user.getUserPassword());
        }
    }
    private  void  init()
    {
        ve_btn=findViewById(R.id.ve_btn);
        login_way = findViewById(R.id.login_way);
        login_in=findViewById(R.id.login_in);
        login_up = findViewById(R.id.login_up);
        phone_num = findViewById(R.id.phone_num);
        password = findViewById(R.id.password);
        forgetpassowrd=findViewById(R.id.forgetpassword);

        forgetpassowrd.setOnClickListener(this);
        login_in.setOnClickListener(this);
        login_up.setOnClickListener(this);
        password.setOnFocusChangeListener(this);
        login_way.setOnClickListener(this);
        ve_btn.setOnClickListener(this);
    }
    private void reload()
    {
        String phone=remember.getString("phone",null);
        String password_temp=remember.getString("password",null);
        if(phone!=null)
            phone_num.setText(phone);
        if(password_temp!=null)
            password.setText(password_temp);
    }
    void loginbypassword()
    {
         user_phone= phone_num.getText().toString().trim();
         user_password= password.getText().toString().trim();
        if(user_phone.length()>0&&user_password.length()>0)
        { //Log.e("login", "login: "+phone+password_temp );
            Thread net = new Thread(new Runnable(){
                @Override
                public void run() {
                temp.now_user=net_connect.LoginCheck(user_phone,user_password);}});
            net.start();
            try {
                net.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if(!temp.now_user.UserIsempty())
            {
                SharedPreferences.Editor editor=remember.edit();
                editor.putString("phone",user_phone);
                editor.putString("password",user_password);

                editor.commit();
                Intent travelintent=new Intent(LoginByPassword.this,MainViewLayout.class);
                travelintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(travelintent);
                Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_LONG).show();
            }
        }
    }
    void loginbyverification()
    {

    }
    @Override
    public void onFocusChange(View view, boolean b) {
        if(b)
        {
            String phone= phone_num.getText().toString();
            if(phone.isEmpty()||phone.length()<11)
            {
                phone_num.requestFocus();
                Toast.makeText(this,"号码不足11位",Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void checkPermission()
    {
        int SdkVersion = 0;
        //将需要的权限添加到数组中
        String[] Permission={
                Manifest.permission.READ_CONTACTS,Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.INTERNET,
                Manifest.permission.RECEIVE_SMS,Manifest.permission.READ_SMS,
                Manifest.permission.GET_TASKS,Manifest.permission.ACCESS_FINE_LOCATION
        };
        try
        {
            final PackageInfo packageInfo= this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            SdkVersion = packageInfo.applicationInfo.targetSdkVersion;//获取应用的Target版本
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //如果系统版本>=6.0
            if (SdkVersion >= Build.VERSION_CODES.M) {
                //第 1 步: 检查是否有相应的权限
                boolean allGranted = checkPermissionAllGranted(Permission);
                if (allGranted) {
                    //所有权限已经授权！
                    //Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG).show();
                    return;
                }
                // 请求Permission数组中的所有权限，如果已经有了则会忽略。
                ActivityCompat.requestPermissions(this,
                        Permission, 1);
            }
        }
    }
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                Toast.makeText(getApplicationContext(), "权限不足，部分服务可能无法使用", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }
    /**
     * com.mob.MobSDK.class
     * 回传用户隐私授权结果
     * @param isGranted     用户是否同意隐私协议
     */
    public static void submitPolicyGrantResult(boolean isGranted)
    {
        isGranted=true;
    }
}
