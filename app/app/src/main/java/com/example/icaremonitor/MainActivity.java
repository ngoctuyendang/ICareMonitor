package com.example.icaremonitor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.view.View.OnClickListener;


public class MainActivity extends AppCompatActivity {

    private Button btnStart1;
    private static final int REQUEST_CODE = 1;
    private static final String TAG = "ICare Monitor";
    Animation ami;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart1 = (Button) findViewById(R.id.btnStart);
        btnStart1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPermission();
            }
        });
    }

    public void verifyPermission() {
        Log.d(TAG, "VerrifyPermission: asking user for permission");
        String[] permission = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permission[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permission[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permission[2])== PackageManager.PERMISSION_GRANTED){
            Intent intent1 = new Intent(MainActivity.this, Main2Activity.class);
            startActivity(intent1);
            overridePendingTransition(R.anim.animotion,R.anim.animotion2);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, permission, REQUEST_CODE);
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermission();
    }
}
