package course.examples.creaturun;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.TrackerSettings;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "LocationTracker";

    private LocationTracker tracker;
    private TextView latitude;
    private TextView longitude;
    private TextView speed;
    private TextView timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitude = (TextView)findViewById(R.id.latitude);
        longitude = (TextView)findViewById(R.id.longitude);
        speed = (TextView)findViewById(R.id.speed);
        timestamp = (TextView) findViewById(R.id.timestamp);

        // Initialize the location tracker
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // You need to ask the user to enable the permissions
        } else {
            tracker = new LocationTracker(this, new TrackerSettings()
                    .setUseGPS(true)
                    .setUseNetwork(true)
                    .setUsePassive(true)
                    .setTimeBetweenUpdates(500)) {
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

    //This is just temporary, you can change the layout or anything, just make sure there's still a way to get to GetCreatures
    /*public void onGetCreatures(View view) {
        Intent intent = new Intent(this, GetCreatures.class);
        startActivity(intent);
    }*/

    // method for accessing the creature view screen
    public void onViewCreatures(View view) {
        Intent intent = new Intent(this, CreatureView.class);
        startActivity(intent);
    }
}
