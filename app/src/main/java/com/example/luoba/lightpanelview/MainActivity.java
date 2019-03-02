package com.example.luoba.lightpanelview;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    /**
     * 光线检测
     */
    private LightPanelView lightPanelView;
    /**
     * 光线值
     */
    private float lightValue;
    /**
     * 光线最大值
     */
    private static final int lightMax = 2000;
    /**
     * 光线传感器监听
     */
    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
                lightValue = sensorEvent.values[0];
                if (lightValue > lightMax) {
                    lightValue = lightMax;
                }
                lightPanelView.setRadio(lightValue / lightMax);
                Log.d("光线变化值",String.valueOf(lightValue));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        SensorLightUtils.getInstance().startSensorService(MainActivity.this,mSensorEventListener);
    }

    private void initView() {
        lightPanelView = findViewById(R.id.lpv);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SensorLightUtils.getInstance().stopSensorServic(MainActivity.this,mSensorEventListener);
    }
}
