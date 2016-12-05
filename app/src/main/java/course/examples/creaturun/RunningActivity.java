package course.examples.creaturun;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.TrackerSettings;

import static course.examples.creaturun.R.id.speed;

public class RunningActivity extends AppCompatActivity {


    public static String TAG = "LocationTracker";

    private LocationTracker tracker;

    private Location startingLocation = null;
    private Location prevLocation = null;
    private Long prevTime = null;
    private Long currTime = null;
    private double distance = 0.0;

    private TextView latitude;
    private TextView longitude;
    private TextView speedView;
    private TextView timestamp;
    private TextView distanceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);

        latitude = (TextView)findViewById(R.id.latitude);
        longitude = (TextView)findViewById(R.id.longitude);
        speedView = (TextView)findViewById(speed);
        timestamp = (TextView) findViewById(R.id.timestamp);
        distanceView = (TextView) findViewById(R.id.distance);

        // Initialize the location tracker
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // You need to ask the user to enable the permissions
        } else {
            tracker = new LocationTracker(this, new TrackerSettings()
                    .setUseGPS(true)
                    .setUseNetwork(true)
                    .setUsePassive(true)
                    .setTimeBetweenUpdates(1000)) {
                @Override
                public void onLocationFound(Location location) {
                    Log.i(TAG, "Latitude: " + location.getLatitude());
                    if (startingLocation == null) {
                        startingLocation = location;
                        prevLocation = startingLocation;
                        prevTime = location.getTime();
                    }
                    double distanceToLast = location.distanceTo(prevLocation);
                    double speed = 0.0f;
                    currTime = location.getTime();
                    if (distanceToLast < 10.00) {
                        Log.i("DISTANCE", "Values too close, so not used");
                    } else {
                        distance += distanceToLast;
                        prevLocation = location;
                        speed = distanceToLast/((currTime-prevTime)*(Math.pow(2.77778, -7)));
                    }
                    prevTime = currTime;
                    distanceView.setText(distance*0.000621371 + "miles");
                    latitude.setText(String.valueOf(location.getLatitude()));
                    longitude.setText(String.valueOf(location.getLongitude()));
                    speedView.setText(String.valueOf(speed) + "miles/hour");
                    timestamp.setText(String.valueOf(location.getTime()));
                }
                @Override
                public void onTimeout(){

                }
            };
            tracker.startListening();
        }
    }
}
