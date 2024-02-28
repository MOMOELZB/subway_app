package com.example.opqr.overall_situation;

import static android.os.SystemClock.sleep;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.firebase.crashlytics.buildtools.api.WebApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class OkHttpUtils {

    String id =null;
    String name=null;
    String user_password=null;
    String user_phone=null;
    String adress_ip="http://43.138.110.69:8080";
    boolean respflag=false;
    String responseresult;
    App_Var net=App_Var.getInstance();
    @SuppressLint("SuspiciousIndentation")
    public String okhttpPostFile_forBytes(String mUrl, byte[]mfileData)
    {
        try {
            //补全请求地址
            MultipartBody.Builder builder = new MultipartBody.Builder();
            //设置类型
            builder.setType(MultipartBody.FORM);
            //追加参数
            File file=null;
         //  MediaType mediaType=MediaType.parse("application/json;charset=utf-8");
            builder.addFormDataPart("UID",String.valueOf(net.Uid));
            builder.addFormDataPart("file", "file.map", createProgressRequestBody( okhttp3.MediaType.parse("application/octet-stream"), file,mfileData));
            //创建RequestBody
            RequestBody body = builder.build();
            //创建Request
            final Request request = new Request.Builder().url(mUrl).post(body).build();
            OkHttpClient mOkHttpClient = new OkHttpClient();
            final Call call = mOkHttpClient.newBuilder().writeTimeout(50, TimeUnit.SECONDS).readTimeout(50, TimeUnit.SECONDS).build().newCall(request);
            Response response  = call.execute();

            try
            {

                if (response.isSuccessful()) {
                    if(response.code()==200)
                    responseresult="YES";
                    Log.e("gu", "okhttpPostFile_forBytes: "+responseresult );
                    return responseresult;
                }
                else
                {
                   // Log.e("rest", "okhttp-post-err:" + response.code());
                    responseresult="no";
                }
                Log.e("rest", "okhttp-post-err:" + response.code());
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        while(responseresult==null)
        continue;
        return responseresult;
    }
    public  RequestBody createProgressRequestBody(final  okhttp3.MediaType contentType, final File file,byte[]mfileData) {
        return new RequestBody() {
            @Override
            public okhttp3.MediaType contentType() {
                return contentType;
            }
            @Override
            public long contentLength() {
                if (mfileData!=null)
                    return mfileData.length;
                return file.length();
            }
            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source;
                try {
                    if (mfileData!=null)
                    {
                        source =Okio.source(new ByteArrayInputStream(mfileData));
                        okio.Buffer buf =new okio.Buffer();
                        long remaining = contentLength();
                        long current = 0;
                        for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                            sink.write(buf, readCount);
                            current += readCount;
                            //callback  进度通知
                        }
                    }
                    else
                    {
                        source = Okio.source(file);
                        okio.Buffer buf = new  okio.Buffer();
                        long remaining = contentLength();
                        long current = 0;
                        for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                            sink.write(buf, readCount);
                            current += readCount;
                            //callback 进度通知
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
    public  User LoginCheck(String phone,String password)  {
        Log.d("message","start net test");
        Thread Logincheck = new Thread(new Runnable() {
            @Override
            public void run() {
                respflag=false;
                // @Headers({"Content-Type:application/json","Accept: application/json"})//需要添加头
                MediaType JSON = MediaType.parse("application/json;charset=utf-8");
                JSONObject json = new JSONObject();
                String url = adress_ip+"/user/password_login";
                Log.d("JSONObject解析", "id is "+phone);
//                                  Log.d("JSONObject解析", "name is "+name);
                try {
                    json.put("phone", phone);
                    json.put("user_password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //申明给服务端传递一个json串
                //创建一个OkHttpClient对象
                OkHttpClient okHttpClient = new OkHttpClient();
                //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
                //json为String类型的json数据
                RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
                //创建一个请求对象
//                        String format = String.format(KeyPath.Path.head + KeyPath.Path.waybillinfosensor, username, key, current_timestamp);
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //DialogUtils.showPopMsgInHandleThread(Release_Fragment.this.getContext(), mHandler, "数据获取失败，请重新尝试！");
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                       // Log.d("JSONObject解析", "id is "+response.body().string());
                            try {
                                String jsonstr=response.body().string();
                                Log.e("jsoncall", "onResponse: "+jsonstr);
                                JSONObject jsonObject = new JSONObject(jsonstr);
                                    id = jsonObject.getString("id_card");
                                    name = jsonObject.getString("user_name");
                                    user_password = jsonObject.getString("user_password");
                                    user_phone = jsonObject.getString("phone");
                                    respflag=true;
                                    net.Uid=jsonObject.getInt("uid");
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                           }
                    }
                });
            }
        });
        Logincheck.start();
        while(!respflag)
       {
           continue;
       }
        return new User(id,user_phone,user_password,name);
    }
    public  User UserRegister(User user)  {
        Log.d("message","start net test");
        Thread Logincheck = new Thread(new Runnable(){
            @Override
            public void run() {
                respflag=false;
                // @Headers({"Content-Type:application/json","Accept: application/json"})//需要添加头
                MediaType JSON = MediaType.parse("application/json;charset=utf-8");
                JSONObject json = new JSONObject();
                String url = adress_ip+"/user/register";
//                         Log.d("JSONObject解析", "name is "+name);
                try {
                    json.put("phone", user.getUserPhoneNum());
                    json.put("user_password", user.getUserPassword());
                    json.put("id_card",user.getIdentification());
                    json.put("user_name",user.getUserName());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //申明给服务端传递一个json串
                //创建一个OkHttpClient对象
                OkHttpClient okHttpClient = new OkHttpClient();
                //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
                //json为String类型的json数据
                RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
                //创建一个请求对象
//                 String format = String.format(KeyPath.Path.head + KeyPath.Path.waybillinfosensor, username, key, current_timestamp);
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //DialogUtils.showPopMsgInHandleThread(Release_Fragment.this.getContext(), mHandler, "数据获取失败，请重新尝试！");
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String jsonstr=response.body().string();
                            Log.e("jsoncall", "onResponse: "+jsonstr);
                            JSONObject jsonObject = new JSONObject(jsonstr);
                            name = jsonObject.getString("user_name");
                            user_phone = jsonObject.getString("phone");
                            id = jsonObject.getString("id_card");
                            user_password = jsonObject.getString("user_password");
                            net.Uid=jsonObject.getInt("uid");
                            respflag=true;
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
        Logincheck.start();
        while(!respflag)
        {
            continue;
        }
        return new User(id,user_phone,user_password,name);
    }
    public  User LoginCheckByVerification(String phone)
    {
        Log.d("message","start net test"+phone);
        Thread Logincheck = new Thread(new Runnable() {
            @Override
            public void run() {
                respflag=false;
                // @Headers({"Content-Type:application/json","Accept: application/json"})//需要添加头
                MediaType JSON = MediaType.parse("application/json;charset=utf-8");
                JSONObject json = new JSONObject();
                String url = adress_ip+"/user/verification_code_login";
                try {
                    json.put("phone", phone);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //申明给服务端传递一个json串
                //创建一个OkHttpClient对象
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)//设置连接超时时间
                        .readTimeout(20, TimeUnit.SECONDS)//设置读取超时时间
                        .build();
                //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
                //json为String类型的json数据
                RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
                //创建一个请求对象
//                        String format = String.format(KeyPath.Path.head + KeyPath.Path.waybillinfosensor, username, key, current_timestamp);
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
//                        if(e.getCause().equals(SocketTimeoutException.class) && serversLoadTimes<maxLoadTimes)//如果超时并未超过指定次数，则重新连接
//                        {
//                            serversLoadTimes++;
//                            okHttpClient.newCall(call.request()).enqueue(this);
//                        }else {
//                            e.printStackTrace();
//                            //WebApi.this.serversListEvent.getServers(null);
//                        }

                        //DialogUtils.showPopMsgInHandleThread(Release_Fragment.this.getContext(), mHandler, "数据获取失败，请重新尝试！");
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            id = jsonObject.getString("id_card");
                            name = jsonObject.getString("user_name");
                            user_password = jsonObject.getString("user_password");
                            user_phone = jsonObject.getString("phone");
                            net.Uid=jsonObject.getInt("uid");
                            Log.e("dasj", "onResponse:+"+id );
                            respflag=true;
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
        Logincheck.start();
        while(!respflag)
        {
            continue;
        }
        return new User(id,user_phone,user_password,name);
    }
    public  User LoginPasswordReset(String phone,String password)
    {
        Log.d("message","start net test");
        Thread Logincheck = new Thread(new Runnable() {
            @Override
            public void run() {
                respflag=false;
                // @Headers({"Content-Type:application/json","Accept: application/json"})//需要添加头
                MediaType JSON = MediaType.parse("application/json;charset=utf-8");
                JSONObject json = new JSONObject();
                String url = adress_ip+"/user/change_password";
                try {
                    json.put("phone", phone);
                    json.put("user_password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //申明给服务端传递一个json串
                //创建一个OkHttpClient对象
                OkHttpClient okHttpClient = new OkHttpClient();
                //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
                //json为String类型的json数据
                RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
                //创建一个请求对象
//                        String format = String.format(KeyPath.Path.head + KeyPath.Path.waybillinfosensor, username, key, current_timestamp);
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //DialogUtils.showPopMsgInHandleThread(Release_Fragment.this.getContext(), mHandler, "数据获取失败，请重新尝试！");
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String jsonstr=response.body().string();
                            Log.e("jsoncall", "onResponse: "+jsonstr);
                            JSONObject jsonObject = new JSONObject(jsonstr);
                            id = jsonObject.getString("id_card");
                            name = jsonObject.getString("user_name");
                            user_password = jsonObject.getString("user_password");
                            user_phone = jsonObject.getString("phone");
                            respflag=true;
                            net.Uid=jsonObject.getInt("uid");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
        Logincheck.start();
        while(!respflag)
        {
            continue;
        }
        return new User(id,user_phone,user_password,name);
    }
}
