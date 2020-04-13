package com.example.mymediaplayer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;

public class WattingActivity extends AppCompatActivity {

    final private int PREMISSION_CODE = 1;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waitting);
        intent = new Intent(WattingActivity.this,MainActivity.class);

        //要求權限
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PREMISSION_CODE);
        }
        else
        {
            startActivity(intent);
            finish();
        }
    }


    //權限選擇CallBack
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PREMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this,"已取得權限",Toast.LENGTH_SHORT).show();
                startActivity(intent);
                finish();
            }
            else
            {
                // requestPermissions 預設為不再提醒
                // 如果之前請求過此權限但用戶拒絕了請求，此方法將返回 true
                // 若第二次請求勾選不再詢問，以後此方法將返回 false
                if (ActivityCompat.shouldShowRequestPermissionRationale(WattingActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE))
                {
                    new AlertDialog.Builder(WattingActivity.this)
                            .setMessage("如果您不允許權限，您將無法使用此應用程式，要結束應用程式嗎？")
                            .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(WattingActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},                                        PREMISSION_CODE);
                                }
                            })
                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).create().show();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
