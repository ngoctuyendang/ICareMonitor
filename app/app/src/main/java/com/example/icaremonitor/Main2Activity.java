package com.example.icaremonitor;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;

import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;

import android.os.PowerManager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main2Activity extends AppCompatActivity {

    private Main2Activity instance = null;


    private static final String TAG = "HeartRateMonitor";
    private static final AtomicBoolean processing = new AtomicBoolean(false);

    private static SurfaceView preview = null;
    private static SurfaceHolder previewHolder = null;
    private static Camera camera = null;
    private static View image = null;
    private static TextView text = null;

    private static PowerManager.WakeLock wakeLock = null;

    private static int averageIndex = 0;
    private static final int averageArraySize = 4;
    private static final int[] averageArray = new int[averageArraySize];





    public static enum TYPE {
        GREEN, RED
    };

    private static TYPE currentType = TYPE.GREEN;

    public static TYPE getCurrent() {
        return currentType;
    }

    private static int beatsIndex = 0;
    private static final int beatsArraySize = 3;
    private static final int[] beatsArray = new int[beatsArraySize];
    private static double beats = 0;
    private static long startTime = 0;

    private Button btninfo;
    private Button btnmeasure;
    private Button btnhistory;
    private Button btnsetting;

    //database
    Database mDatabase;
    //dialog
    public  Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mDatabase= new Database(this);

        btninfo = (Button) findViewById(R.id.info);
        btninfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Main2Activity.this,Info_Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.animotion,R.anim.animotion2);

            }
        });

        btnmeasure = (Button) findViewById(R.id.measure);
        btnmeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 =new Intent(Main2Activity.this,Main2Activity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.animotion,R.anim.animotion2);

            }
        });

        btnhistory = (Button) findViewById(R.id.history);
        btnhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 =new Intent(Main2Activity.this,History_Activity.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.animotion,R.anim.animotion2);

            }
        });
        preview = (SurfaceView) findViewById(R.id.preview);
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        image = findViewById(R.id.image);
        text = (TextView) findViewById(R.id.text);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,"AppName:ICareMonitor");


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();

        wakeLock.acquire();

        camera = Camera.open();

        startTime = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();

        wakeLock.release();

        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
        instance=this;
    }





    private  Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {
            if (data == null) throw new NullPointerException();
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null) throw new NullPointerException();

            if (!processing.compareAndSet(false, true)) return;

            int width = size.width;
            int height = size.height;

            int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(), height, width);
            if (imgAvg == 0 || imgAvg == 255) {
                processing.set(false);
                return;
            }

            int averageArrayAvg = 0;
            int averageArrayCnt = 0;
            for (int i = 0; i < averageArray.length; i++) {
                if (averageArray[i] > 0) {
                    averageArrayAvg += averageArray[i];
                    averageArrayCnt++;
                }
            }

            int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt) : 0;
            TYPE newType = currentType;
            if (imgAvg < rollingAverage) {
                newType = TYPE.RED;
                if (newType != currentType) {
                    beats++;
                }
            } else if (imgAvg > rollingAverage) {
                newType = TYPE.GREEN;
            }

            if (averageIndex == averageArraySize) averageIndex = 0;
            averageArray[averageIndex] = imgAvg;
            averageIndex++;

            // Transitioned from one state to another to the same
            if (newType != currentType) {
                currentType = newType;
                image.postInvalidate();
            }

            long endTime = System.currentTimeMillis();
            double totalTimeInSecs = (endTime - startTime) / 1000d;
            if (totalTimeInSecs >= 10) {
                double bps = (beats / totalTimeInSecs);
                int dpm = (int) (bps * 60d);
                if (dpm < 30 || dpm > 180) {
                    startTime = System.currentTimeMillis();
                    beats = 0;
                    processing.set(false);
                    return;
                }

                if (beatsIndex == beatsArraySize) beatsIndex = 0;
                beatsArray[beatsIndex] = dpm;
                beatsIndex++;

                int beatsArrayAvg = 0;
                int beatsArrayCnt = 0;
                for (int i = 0; i < beatsArray.length; i++) {
                    if (beatsArray[i] > 0) {
                        beatsArrayAvg += beatsArray[i];
                        beatsArrayCnt++;
                    }
                }
                int beatsAvg = (beatsArrayAvg / beatsArrayCnt);
                text.setText(String.valueOf(beatsAvg));


                startTime = System.currentTimeMillis();
                beats = 0;


               onPause();
               onResume();
               showDialog(instance,beatsAvg);

            }

            processing.set(false);
        }

    };

    private  SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        @SuppressLint("LongLogTag")
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(previewHolder);
                camera.setPreviewCallback(previewCallback);
            } catch (Throwable t) {
                Log.e("PreviewDemo-surfaceCallback", "Exception in setPreviewDisplay()", t);
            }
        }
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {



            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);




            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                Log.d(TAG, "Using width=" + size.width + " height=" + size.height);
            }
            camera.setParameters(parameters);
            camera.startPreview();

            camera.setDisplayOrientation(90);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }


    };

    private  Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea < resultArea) result = size;
                }
            }
        }

        return result;
    }

     public void showDialog(Context context, int a){

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_dialog);
        final TextView tvValue= (TextView)dialog.findViewById(R.id.tvValue);
        tvValue.setText(String.valueOf(a));


        final EditText tvNote =(EditText)dialog.findViewById(R.id.note);

        Button saveButton = (Button) dialog.findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Main2Activity.this,"Đã lưu",Toast.LENGTH_LONG).show();

                // insert value to database
                String newEntry = text.getText().toString();

                //insert time to database
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                //yyyy-MM-dd HH:mm:ss
                String strDate = sdf.format(date);

                // insert not to datase
                String note= (String)tvNote.getText().toString();

                if (text.length()!=0) {
                    add_Data(newEntry, strDate,note);
                    text.setText("");
                }else{
                    Toast.makeText(Main2Activity.this,"You need something in text field",Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
                continueActivity();

            }
        });

        Button nosaveButton = (Button) dialog.findViewById(R.id.nosave);
        nosaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Main2Activity.this,"Không lưu",Toast.LENGTH_LONG).show();
                dialog.dismiss();
                backToMain();
            }
        });

        dialog.show();

    }
    //method of the continue button
    public void continueActivity(){
        Intent con = new Intent(Main2Activity.this, History_Activity.class);
        Main2Activity.this.startActivity(con);
        overridePendingTransition(R.anim.animotion,R.anim.animotion2);

    }
    //method of the back to main button
    public void backToMain(){
        Intent backtomain = new Intent(Main2Activity.this, Main2Activity.class);
        Main2Activity.this.startActivity(backtomain);
        overridePendingTransition(R.anim.animotion,R.anim.animotion2);
    }
    public void add_Data( String newEntry, String date, String note ){
        boolean insertData = mDatabase.INSERT_Data(newEntry, date, note);
        if(insertData){
            Toast.makeText(Main2Activity.this,"Thành công ",Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(Main2Activity.this,"Không thành công",Toast.LENGTH_LONG).show();
        }
    }


}
