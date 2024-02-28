package com.example.opqr.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.opqr.R;
//import com.example.opqr.overall_situation.UseDao;
import com.example.opqr.Boarding.BoardingCode;
import com.example.opqr.overall_situation.App_Var;
import com.example.opqr.overall_situation.OkHttpUtils;
import com.example.opqr.overall_situation.User;
import com.mob.MobSDK;

import androidx.appcompat.app.AppCompatActivity;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class Registration extends AppCompatActivity implements  View.OnClickListener,View.OnFocusChangeListener {
    private EditText phone_num;
    User temp_user;
    private EditText phone_password;
    private EditText person_id;
    private EditText person_name;
    private String temp_phone;
    int count_second =30;
    private App_Var temp= App_Var.getInstance();
    private Button login_btn;
    private Button ve_btn;
    private EditText ve_code;
    private OkHttpUtils net_connect=new OkHttpUtils();
    private SharedPreferences remember;
    String appKey ="3861318999f26";
    String appSecret ="88517c1179f830d3ee7df5f53c2589af";
    private EventHandler eventHandler = new EventHandler() {
        public void afterEvent(int event, int result, Object data) {
            if (result == SMSSDK.RESULT_COMPLETE) {
                // TODO 处理验证成功的结果
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功


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
    };;
    Handler handler = new Handler() {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
       MobSDK.submitPolicyGrantResult(true);
        setContentView(R.layout.login_up);
        phone_num = findViewById(R.id.phone_num);
        phone_password = findViewById(R.id.phone_password);
        person_id = findViewById(R.id.person_num);
        person_name=findViewById(R.id.person_name);
        login_btn = findViewById(R.id.login_btn);
        ve_btn = findViewById(R.id.ve_btn);
        ve_code = findViewById(R.id.person_phone_num_ve);
        ve_btn.setOnClickListener(this);
        phone_password.setOnFocusChangeListener(this);
        login_btn.setOnClickListener(this);
        //注册 eventHandler
        remember = getSharedPreferences("config", Context.MODE_PRIVATE);
        SMSSDK.registerEventHandler(eventHandler);
    }


   private void init()
   {

   }

    public void onClick(View v) {
        setTouchListener(v);
        temp_phone =phone_num.getText().toString().trim();
        if (v.getId() == R.id.login_btn) {
            Log.i("EventHandler", "提交验证码成功");
            Log.i("EventHandler", person_id.getText().toString());
            User temp_user=new User(person_id.getText().toString(),phone_num.getText().toString(),phone_password.getText().toString()
                    ,person_name.getText().toString());
            temp.now_user.setuser(net_connect.UserRegister(temp_user));
            if(!temp.now_user.UserIsempty())
            {
                        SharedPreferences.Editor editor=remember.edit();
                        editor.putString("phone",phone_num.getText().toString());
                        editor.putString("password",phone_password.getText().toString());
                        editor.commit();
                Intent travelintent=new Intent(Registration.this, BoardingCode.class);
                travelintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(travelintent);
                Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_LONG).show();
            }
            //SMSSDK.submitVerificationCode("86", temp_phone, ve_code.getText().toString());
        }
        if(v.getId()==R.id.ve_btn)
        {
            if(temp_phone.length()!=11)
            {
                Toast.makeText(getApplicationContext(), "号码不足十一位", Toast.LENGTH_LONG).show();
                return;
            }
            else
            {
                //获取验证码
                SMSSDK.getVerificationCode("86", temp_phone);
                //按钮不可用，并且开启计时器
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

    /**
     * 检查号码输入
     * @param view The view whose state has changed.
     * @param b The new focus state of v.
     */
    public void onFocusChange(View view, boolean b) {
        if (b) {
            String phone = phone_num.getText().toString();
            if (phone.isEmpty() || phone.length() < 11) {
                phone_num.requestFocus();
                Toast.makeText(this, "号码不足11位", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 点击EditText之外区域隐藏软键盘
     * 实现方法：为除EditText之外的其他控件设置TouchListener
     *
     * @param view
     */
    protected void setTouchListener(final View view) {
        if (view == null) {
            return;
        }
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftInput(view.getWindowToken());
                    return false;
                }
            });
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setTouchListener(innerView);
            }
        }
    }
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
