package com.example.luoba.lightpanelview;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.List;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Administrator on 2018/8/21.
 */

public class SensorLightUtils {
    public static final String LIGHT_SNOWBLINDNESS = "造成雪盲";
    public static final String LIGHT_DAZZLE = "产生眩光";
    public static final String LIGHT_COMFORTABLE = "光线舒适";
    public static final String LIGHT_DARK = "光线阴暗";
    private static SensorManager manager;
    private static final int lightMax = 2000;
    private final String TAG = getClass().getName() + hashCode();
    private static SensorLightUtils sensorLightUtils;


    public static SensorLightUtils getInstance(){
        if(sensorLightUtils == null){
            sensorLightUtils = new SensorLightUtils();
        }
        return sensorLightUtils;
    }

    public void startSensorService(Context context,SensorEventListener mySensorEventListener){
        manager = (SensorManager)context.getSystemService(SENSOR_SERVICE);
        /**
         * 查看所有传感器
         */
        List<Sensor> sensorList = manager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor s:sensorList){
            System.out.println(s.getName());
        }
        Sensor defaultSensor = manager.getDefaultSensor(Sensor.TYPE_LIGHT);
        //注册传感器 参数1：监听 2.要坚挺的传感器对象 3.决定采用的敏感度  正常
        if (mySensorEventListener != null) {
            manager.registerListener(mySensorEventListener,defaultSensor,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }


    public String getLightMode(float value) {

        if (value >= 0 && value < 300) {
            return LIGHT_DARK;
        } else if (value >= 300 && value < 1300) {
            return LIGHT_COMFORTABLE;
        } else if (value >= 1300 && value < 2000) {
            return LIGHT_DAZZLE;
        }else {
            return LIGHT_SNOWBLINDNESS;
        }
    }

    public void stopSensorServic(Context context,SensorEventListener mySensorEventListener){
        if (manager == null) {
            manager = (SensorManager)context.getSystemService(SENSOR_SERVICE);
        }
        //释放资源
        if (mySensorEventListener != null) {
            manager.unregisterListener(mySensorEventListener);
        }
    }
}
