package com.example.opqr.Login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.opqr.R;
import com.example.opqr.overall_situation.OkHttpUtils;
//import com.example.opqr.overall_situation.UseDao;
import com.example.opqr.overall_situation.App_Var;
import com.mob.MobSDK;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class PasswordReset extends AppCompatActivity implements View.OnClickListener {

    private String password_num;
    private String password_re;
    private String phone_num;
    private EditText phone;
    private EditText password_set;
    private EditText password_reset;
    private OkHttpUtils net_connect=new OkHttpUtils();
    int count_second=30;//计时器
    private App_Var temp= App_Var.getInstance();
    private EditText ve_code;
    private Button ve_btn;
    private Button confirm;
    String appKey = "3861318999f26";
    String appSecret = "88517c1179f830d3ee7df5f53c2589af";
    private EventHandler eventHandler= new EventHandler() {
        public void afterEvent(int event, int result, Object data) {
            if (result == SMSSDK.RESULT_COMPLETE) {
                // TODO 处理验证成功的结果
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
               //  temp.now_user= net_connect.LoginPasswordReset(phone_num,password_re);
                if(!net_connect.LoginPasswordReset(phone_num,password_re).UserIsempty())
                {
                    Toast.makeText(getApplicationContext(),"重置成功",
                            Toast.LENGTH_SHORT).show();
                    temp.login_up_way=false;
                    temp.now_user.clear();
                    Intent travelintent=new Intent(PasswordReset.this, LoginByPassword.class);
                    travelintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(travelintent);
                }
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //获取验证码成功
                    Log.i("EventHandler", "获取验证码成功");
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    //返回支持发送验证码的国家列表
                    Log.i("EventHandler", "返回支持发送验证码的国家列表");
                }
                else {
                    Toast.makeText(getApplicationContext(),"验证码错误" ,
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(),"验证码错误" ,
                        Toast.LENGTH_SHORT).show();
                // TODO 处理错误的结果
                Log.i("EventHandler", "提交验证码失败");
                ((Throwable) data).printStackTrace();
            }
        }
    };
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.what==1)
            {

            }
            else if (msg.what==-1) {
                Toast.makeText(getApplicationContext(),"网络错误",
                        Toast.LENGTH_SHORT).show();
            }
            else if(msg.what==-2)
            {
                Toast.makeText(getApplicationContext(),"该用户未注册或手机号错误",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };
    private Handler counthandler = new Handler() {
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
        setContentView(R.layout.secret_reset);
        phone = findViewById(R.id.phone_num_phone_num);
        password_set = findViewById(R.id.password_set);
        password_reset = findViewById(R.id.password_reset);
        confirm = findViewById(R.id.passwordreset);
        ve_code = findViewById(R.id.person_phone_num_ve);
        ve_btn = findViewById(R.id.ve_btn);
        ve_btn.setOnClickListener(this);
        confirm.setOnClickListener(this);
        MobSDK.submitPolicyGrantResult(true);
        //短信服务开启
        MobSDK.init(this,appKey, appSecret);
        SMSSDK.registerEventHandler(eventHandler);
    }
    @Override
    public void onClick(View v) {
        phone_num = phone.getText().toString();
        password_num = password_set.getText().toString();
        password_re=password_reset.getText().toString();
        if(v.getId()==R.id.ve_btn)
        {
            if(phone_num.length()!=11)
        {
            Toast.makeText(getApplicationContext(), "号码不足十一位", Toast.LENGTH_LONG).show();
            return;
        }
          else  if(!password_re.equals(password_num))
            {
                    Toast.makeText(getApplicationContext(),"两次密码不一致"+password_num+password_re,
                            Toast.LENGTH_SHORT).show();
                    return;
            }
            else {
                SMSSDK.getVerificationCode("86", phone_num);
                ve_btn.setClickable(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (; count_second > 0; count_second--) {
                            counthandler.sendEmptyMessage(-9);
                            if (count_second <= 0) {
                                break;
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        counthandler.sendEmptyMessage(-8);
                    }
                }).start();
            }
        }
        if(v.getId()==R.id.passwordreset)
        { String code=ve_code.getText().toString();
            if(code!=null)
            {
                SMSSDK.submitVerificationCode("86", phone_num,code );
            }
            else
            {
                    Toast.makeText(getApplicationContext(),"验证码不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
            }
        }
    }
}
