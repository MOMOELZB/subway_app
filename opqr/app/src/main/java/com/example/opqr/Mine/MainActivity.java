package com.example.opqr.Mine;

import android.Manifest;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.opqr.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String FILE_NAME = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + File.separator + "test.pcm";

    private AudioRecord audioRecord = null;
    private int recordBufsize = 0;
    private boolean isRecording = false;

    private Button startRecordBtn;
    private Button stopRecordBtn;
    private Thread recordingThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        startRecordBtn = findViewById(R.id.start_record_btn);
        stopRecordBtn = findViewById(R.id.stop_record_btn);
        createAudioRecord();
        startRecordBtn.setOnClickListener(this);
        stopRecordBtn.setOnClickListener(this);
    }

    private void createAudioRecord() {
        recordBufsize = AudioRecord
                .getMinBufferSize(44100,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);
        Log.i("audioRecordTest", "size->" + recordBufsize);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                44100,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                recordBufsize);
    }

    private void startRecord() {
        if (isRecording) {
            return;
        }
        isRecording = true;
        audioRecord.startRecording();
        Log.i("audioRecordTest", "开始录音");
        recordingThread = new Thread(() -> {
            byte data[] = new byte[recordBufsize];

        });
        recordingThread.start();
    }

    private void stopRecord() {
        isRecording = false;
        if (audioRecord != null) {
            audioRecord.stop();
            Log.i("audioRecordTest", "停止录音");
            audioRecord.release();
            audioRecord = null;
            recordingThread = null;
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
                Manifest.permission.GET_TASKS,Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.RECORD_AUDIO
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

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.start_record_btn)
        {
            startRecord();
        }
        if(view.getId()==R.id.stop_record_btn)
        {
            stopRecord();
        }
    }
}
