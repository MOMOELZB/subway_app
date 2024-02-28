package com.example.opqr.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.opqr.Boarding.BoardingCode;
import com.example.opqr.R;
import com.example.opqr.overall_situation.OkHttpUtils;
//import com.example.opqr.overall_situation.UseDao;
import com.example.opqr.overall_situation.App_Var;
import com.mob.MobSDK;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class LoginByVerification extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    private RadioGroup login_way;
    private Button ve_btn;
    private EditText phone;
    private EditText ve_code;
    int count_second=30;//计时器
    private App_Var temp= App_Var.getInstance();
    private Button login_up;
    private OkHttpUtils net_connect=new OkHttpUtils();
    String appKey = "3861318999f26";
    String appSecret = "88517c1179f830d3ee7df5f53c2589af";
    String temp_phone;
    private SharedPreferences remember ;
    //注册 eventHandler
    private EventHandler eventHandler= new EventHandler() {
        public void afterEvent(int event, int result, Object data) {
            if (result == SMSSDK.RESULT_COMPLETE) {
                // TODO 处理验证成功的结果
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                    Log.i("EventHandler", "提交验证码成功");
                      temp.now_user=net_connect.LoginCheckByVerification(temp_phone);
                      if(!temp.now_user.UserIsempty())
                      {
                          remember = getSharedPreferences("config", Context.MODE_PRIVATE);
                          SharedPreferences.Editor editor=remember.edit();
                          editor.putString("phone",temp.now_user.getUserPhoneNum());
                          editor.putString("password",temp.now_user.getUserPassword());
                          editor.commit();
                          Intent travelintent=new Intent(LoginByVerification.this, BoardingCode.class);
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
            } else {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 短信注册成功后，返回MainActivity,然后提示
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                        Toast.makeText(getApplicationContext(), "提交验证码成功",
                                Toast.LENGTH_SHORT).show();
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        Toast.makeText(getApplicationContext(), "正在获取验证码",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        ((Throwable) data).printStackTrace();
                    }
                }
            }
        }
    };
    protected void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     setContentView(R.layout.login_by_verification);
        init();
        //设置监听器
        ve_btn.setOnClickListener(this);
        login_up.setOnClickListener(this);
        login_way.setOnCheckedChangeListener(this);
        MobSDK.submitPolicyGrantResult(true);
        //短信服务开启
        MobSDK.init(this,appKey, appSecret);
        SMSSDK.registerEventHandler(eventHandler);
    }
    private void init()
    {
        login_way = findViewById(R.id.login_way);
        phone = findViewById(R.id.phone_num_phone_num);
        ve_btn = findViewById(R.id.ve_btn);
        ve_code = findViewById(R.id.person_phone_num_ve);
        login_up = findViewById(R.id.login_up_ve);
        ve_code= findViewById(R.id.person_phone_num_ve);
    }
    @Override
    public void onClick(View view) {
      temp_phone=phone.getText().toString().trim();
        if(view.getId()==R.id.ve_btn)
        {
            if(temp_phone.length()!=11)
            {
                Toast.makeText(getApplicationContext(), "号码不足十一位", Toast.LENGTH_LONG).show();
                return;
            }
            else
            {
                SMSSDK.getVerificationCode("86", temp_phone);
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
        if(view.getId()== R.id.login_up_ve){
            SMSSDK.submitVerificationCode("86", temp_phone, ve_code.getText().toString());
        }
    }
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if(i== R.id.login_by_passward)
        {
            Intent intent=new Intent(this, LoginByPassword.class);
          //  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
    @Override
    protected void onDestroy() {
        SMSSDK.unregisterAllEventHandler();
        super.onDestroy();
    }
}
