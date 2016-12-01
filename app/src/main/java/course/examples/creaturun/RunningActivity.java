package course.examples.creaturun;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.TrackerSettings;

public class RunningActivity extends AppCompatActivity implements SensorEventListener {


    public static String TAG = "LocationTracker";

    private LocationTracker tracker;
    private TextView latitude;
    private TextView longitude;
    private TextView speed;
    private TextView timestamp;
    private TextView steps;

    private SensorManager _sensorManager;
    private Sensor _stepCounterSensor;
    private Sensor _stepDetectorSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);

        _sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        _stepCounterSensor = _sensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        _stepDetectorSensor = _sensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        latitude = (TextView)findViewById(R.id.latitude);
        longitude = (TextView)findViewById(R.id.longitude);
        speed = (TextView)findViewById(R.id.speed);
        timestamp = (TextView) findViewById(R.id.timestamp);
        steps = (TextView) findViewById(R.id.step_count);

        // Initialize the location tracker
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // You need to ask the user to enable the permissions
        } else {
            tracker = new LocationTracker(this, new TrackerSettings()
                    .setUseGPS(true)
                    .setUseNetwork(true)
                    .setUsePassive(true)
                    .setTimeBetweenUpdates(1)) {
                @Override
                public void onLocationFound(Location location) {
                    Log.i(TAG, "Latitude: " + location.getLatitude());
                    latitude.setText(String.valueOf(location.getLatitude()));
                    longitude.setText(String.valueOf(location.getLongitude()));
                    speed.setText(String.valueOf(location.getSpeed()));
                    timestamp.setText(String.valueOf(location.getTime()));
                }
                @Override
                public void onTimeout(){

                }
            };
            tracker.startListening();
        }
    }

    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        float[] values = event.values;
        int value = -1;

        if (values.length > 0) {
            value = (int) values[0];
        }

        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            steps.setText("Step Counter Detected : " + value);
        } else if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            // For test only. Only allowed value is 1.0 i.e. for step taken
            steps.setText("Step Detector Detected : " + value);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //Blah blah blah, need to implement this
    }



    protected void onResume() {

        super.onResume();

        _sensorManager.registerListener(this, _stepCounterSensor,

                SensorManager.SENSOR_DELAY_FASTEST);
       _sensorManager.registerListener(this, _stepDetectorSensor,

                SensorManager.SENSOR_DELAY_FASTEST);

    }

    protected void onStop() {
        super.onStop();
        _sensorManager.unregisterListener(this, _stepCounterSensor);
        _sensorManager.unregisterListener(this, _stepDetectorSensor);
    }

}
