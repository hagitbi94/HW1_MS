package com.example.hw1_ms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.sip.SipSession;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView helloTxt;
    private TextView textName;
    private TextView textPassword;
    private EditText nameEdit;
    private EditText passwordEdit;
    private Button submit;
    public static float swRoll;
    public static float swPitch;
    public static float swAzimuth;
    public static double azimuth;
    public static double pitch;
    private String name = "hagit";
    private String pass = "qaz12";

    public static SensorManager mSensorManager;
    public static Sensor accelerometer;
    public static Sensor magnetometer;

    public static float[] mAccelerometer = null;
    public static float[] mGeomagnetic = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        findViews();
        initViews();

}


    private void findViews() {
        helloTxt = findViewById(R.id.helloTxt);
        nameEdit = findViewById(R.id.NameEdit);
        passwordEdit = findViewById(R.id.PasswordEdit);
        textName = findViewById(R.id.NameTextV);
        textPassword = findViewById(R.id.PasswordTextV);
        submit = findViewById(R.id.submit);

    }


    private void initViews() {

        submit.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                float brightness = Settings.System.getInt(getApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, -1);
                //only when silent mode is on, and the phone battery is less then 60% , and the position of the phone is flipped and the brightness level is less then 150
                if(detectVibrate(getApplicationContext()) && (getBatteryPercentage(getApplicationContext()) < 60) && (pitch > 0) && (brightness < 150 )) {

                    Toast.makeText(getApplicationContext(), "Great! " + "Battery value " +String.valueOf(getBatteryPercentage(getApplicationContext()))
                            + " oriantation number " + String.valueOf(pitch) + "brightness level " + String.valueOf(brightness), Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(getApplicationContext(), "NO! You need to be more specific!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public static boolean detectVibrate(Context context){
        boolean status = false;
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        if(am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE){
            status = true;
        } else if (1 == Settings.System.getInt(context.getContentResolver(), "vibrate_when_ringing", 0)) //vibrate on
            status = true;
        return status;
    }



    public static int getBatteryPercentage(Context context) {
            BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
            return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }



    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // onSensorChanged gets called for each sensor so we have to remember the values
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mAccelerometer = event.values;
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values;
        }

        if (mAccelerometer != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mAccelerometer, mGeomagnetic);

            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                // at this point, orientation contains the azimuth(direction), pitch and roll values.
                azimuth = 180 * orientation[0] / Math.PI;
                pitch = 180 * orientation[1] / Math.PI;
                double roll = 180 * orientation[2] / Math.PI;
//                Log.d("direction", String.valueOf(azimuth));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, accelerometer);
        mSensorManager.unregisterListener(this, magnetometer);
    }



}